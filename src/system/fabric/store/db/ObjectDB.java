package fabric.store.db;

import java.io.DataOutput;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.security.auth.x500.X500Principal;

import fabric.common.FastSerializable;
import fabric.common.ONumConstants;
import fabric.common.ObjectGroup;
import fabric.common.SerializedObject;
import fabric.common.VersionWarranty;
import fabric.common.exceptions.AccessException;
import fabric.common.util.LongHashSet;
import fabric.common.util.LongIterator;
import fabric.common.util.LongKeyHashMap;
import fabric.common.util.LongKeyMap;
import fabric.common.util.LongKeyMap.Entry;
import fabric.common.util.LongSet;
import fabric.common.util.OidKeyHashMap;
import fabric.common.util.Pair;
import fabric.lang.security.NodePrincipal;
import fabric.lang.security.Principal;
import fabric.store.PartialObjectGroup;
import fabric.store.SubscriptionManager;
import fabric.store.TransactionManager;
import fabric.worker.Store;
import fabric.worker.Worker;

/**
 * <p>
 * An ObjectDB encapsulates the persistent state of the Store. It is responsible
 * for storing and retrieving objects, and also for checking permissions.
 * </p>
 * <p>
 * The ObjectDB interface is designed to support a three-phase commit protocol.
 * Consequently to insert or modify an object, users must first call the
 * beginPrepareWrites() method, registerUpdate() for each inserted or modified
 * object, followed by finishPrepareWrites(). These objects will be stored, but
 * will remain unavailable until after the commit() method is called.
 * </p>
 * <p>
 * In general, implementations of ObjectDB are not thread-safe. Only
 * TransactionManager should be interacting directly with ObjectDB
 * implementations; it is responsible for ensuring safe use of ObjectDB.
 * </p>
 * <p>
 * All ObjectDB implementations should provide a constructor which takes the
 * name of the store and opens the appropriate back-end database if it exists,
 * or creates it if it doesn't exist.
 * </p>
 */
public abstract class ObjectDB {

  protected final String name;
  private long nextGlobID;

  /**
   * Maps object numbers to globIDs. The group container with ID
   * globIDByOnum(onum) holds a copy of object onum. (globIDs really ought to be
   * called group-container IDs, but we're sticking with globID for historical
   * reasons and because it's shorter.)
   */
  private final LongKeyMap<Long> globIDByOnum;

  /**
   * Maps globIDs to entries (either GroupContainers or PartialObjectGroups) and
   * the number of times the entry is referenced in globIDByOnum.
   */
  private final GroupTable globTable;

  public static enum UpdateType {
    CREATE, WRITE
  }

  /**
   * The data stored for a partially prepared transaction.
   */
  protected static final class PendingTransaction implements FastSerializable,
      Iterable<Long> {
    public final long tid;
    public final Principal owner;

    /**
     * Objects that have been modified or created.
     */
    public final Collection<Pair<SerializedObject, UpdateType>> modData;

    PendingTransaction(long tid, Principal owner) {
      this.tid = tid;
      this.owner = owner;
      this.modData = new ArrayList<Pair<SerializedObject, UpdateType>>();
    }

    /**
     * Deserialization constructor.
     */
    public PendingTransaction(ObjectInputStream in) throws IOException {
      this.tid = in.readLong();

      if (in.readBoolean()) {
        Store store = Worker.getWorker().getStore(in.readUTF());
        this.owner = new Principal._Proxy(store, in.readLong());
      } else {
        this.owner = null;
      }

      int size = in.readInt();
      this.modData = new ArrayList<Pair<SerializedObject, UpdateType>>(size);
      for (int i = 0; i < size; i++) {
        SerializedObject obj = new SerializedObject(in);
        UpdateType updateType =
            in.readBoolean() ? UpdateType.CREATE : UpdateType.WRITE;
        modData.add(new Pair<SerializedObject, UpdateType>(obj, updateType));
      }
    }

    /**
     * Returns an iterator of onums involved in this transaction.
     */
    @Override
    public Iterator<Long> iterator() {
      return new Iterator<Long>() {
        private Iterator<Pair<SerializedObject, UpdateType>> modIt = modData
            .iterator();

        @Override
        public boolean hasNext() {
          return modIt.hasNext();
        }

        @Override
        public Long next() {
          return modIt.next().first.getOnum();
        }

        @Override
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
    }

    /**
     * Serializes this object out to the given output stream.
     */
    @Override
    public void write(DataOutput out) throws IOException {
      out.writeLong(tid);

      out.writeBoolean(owner != null);
      if (owner != null) {
        out.writeUTF(owner.$getStore().name());
        out.writeLong(owner.$getOnum());
      }

      out.writeInt(modData.size());
      for (Pair<SerializedObject, UpdateType> obj : modData) {
        obj.first.write(out);
        out.writeBoolean(obj.second == UpdateType.CREATE);
      }
    }
  }

  /**
   * <p>
   * The table of partially prepared transactions. Note that this does not need
   * to be saved to stable storage, since we only need to persist transactions
   * that are fully prepared.
   * </p>
   * <p>
   * Maps tids to principal oids to PendingTransactions.
   * </p>
   */
  protected final LongKeyMap<OidKeyHashMap<PendingTransaction>> pendingByTid;

  /**
   * <p>
   * A warranty that will expire after all warranties issued thus far.
   * </p>
   * <p>
   * This should be saved to stable storage and restored when starting up.
   * </p>
   */
  protected VersionWarranty longestWarranty;

  /**
   * The table containing the version warranties that we've issued.
   */
  protected final VersionWarrantyTable versionWarrantyTable;

  /**
   * <p>
   * Tracks the write locks for each onum. Maps each onum to the tid for the
   * lock holder.
   * </p>
   * <p>
   * This should be recomputed from the set of prepared transactions when
   * restoring from stable storage.
   * </p>
   */
  protected final LongKeyMap<Long> writeLocks;

  /**
   * <p>
   * Tracks the set of onums written by each transaction that has been prepared,
   * but for which a commit message has yet to be received.
   * </p>
   * <p>
   * Maps TIDs to principal oids to sets of onums.
   * </p>
   * <p>
   * This should be recomputed from the set of prepared transactions when
   * restoring from stable storage.
   * </p>
   */
  protected final LongKeyMap<OidKeyHashMap<LongSet>> writtenOnumsByTid;

  protected ObjectDB(String name) {
    this.name = name;
    this.pendingByTid = new LongKeyHashMap<OidKeyHashMap<PendingTransaction>>();
    this.writeLocks = new LongKeyHashMap<Long>();
    this.writtenOnumsByTid = new LongKeyHashMap<OidKeyHashMap<LongSet>>();
    this.globIDByOnum = new LongKeyHashMap<Long>();
    this.globTable = new GroupTable();
    this.nextGlobID = 0;
    this.longestWarranty = new VersionWarranty(0);
    this.versionWarrantyTable = new VersionWarrantyTable();
  }

  /**
   * Opens a transaction so it can be write-prepared.
   * 
   * @param worker
   *          the worker under whose authority the transaction is running.
   */
  public final void beginPrepareWrites(long tid, Principal worker) {
    OidKeyHashMap<PendingTransaction> submap = pendingByTid.get(tid);
    if (submap == null) {
      submap = new OidKeyHashMap<PendingTransaction>();
      pendingByTid.put(tid, submap);
    }

    submap.put(worker, new PendingTransaction(tid, worker));
  }

  public static enum ExtendWarrantyStatus {
    OK, BAD_VERSION, DENIED
  }

  /**
   * Attempts to extend the warranty on a particular version of an object.
   * 
   * @throws AccessException if no object exists at the given onum.
   */
  public final ExtendWarrantyStatus extendWarranty(Principal worker, long onum,
      int version, long commitTime) throws AccessException {
    if (version != getVersion(onum)) return ExtendWarrantyStatus.BAD_VERSION;

    VersionWarranty newWarranty =
        extendWarranty(onum, commitTime, ExtendWarrantyMode.STRICT);
    return newWarranty == null ? ExtendWarrantyStatus.DENIED
        : ExtendWarrantyStatus.OK;
  }

  /**
   * Registers that a transaction has created or written to an object. This
   * update will not become visible in the store until after commit() is called
   * for the transaction.
   * 
   * @param tid
   *          the identifier for the transaction.
   * @param obj
   *          the updated object.
   * @param create
   *          whether the object was newly created by the transaction.
   */
  public final void registerUpdate(long tid, Principal worker,
      SerializedObject obj, UpdateType updateType) {
    addWriteLock(obj.getOnum(), tid);
    pendingByTid.get(tid).get(worker).modData
        .add(new Pair<SerializedObject, UpdateType>(obj, updateType));

    if (updateType == UpdateType.WRITE) {
      // Register the update.
      addWrittenOnumByTid(tid, worker, obj.getOnum());
    }
  }

  protected void addWrittenOnumByTid(long tid, Principal worker, long onum) {
    OidKeyHashMap<LongSet> submap = writtenOnumsByTid.get(tid);
    if (submap == null) {
      submap = new OidKeyHashMap<LongSet>();
      writtenOnumsByTid.put(tid, submap);
    }

    LongSet set = submap.get(worker);
    if (set == null) {
      set = new LongHashSet();
      submap.put(worker, set);
    }
    set.add(onum);
  }

  private LongSet removeWrittenOnumsByTid(long tid, Principal worker) {
    OidKeyHashMap<LongSet> writtenOnumsSubmap = writtenOnumsByTid.get(tid);
    if (writtenOnumsSubmap == null) return null;

    LongSet result = writtenOnumsSubmap.remove(worker);
    if (writtenOnumsSubmap.isEmpty()) writtenOnumsByTid.remove(tid);
    return result;
  }

  /**
   * Acquires a write lock on the given onum for the given transaction.
   */
  private void addWriteLock(long onum, long tid) {
    writeLocks.put(onum, tid);
  }

  /**
   * Rolls back a partially prepared transaction. (i.e., one for which
   * finishPrepareWrites() has yet to be called.)
   */
  public final void abortPrepareWrites(long tid, Principal worker) {
    OidKeyHashMap<PendingTransaction> submap = pendingByTid.get(tid);
    unpin(submap.remove(worker));
    if (submap.isEmpty()) pendingByTid.remove(tid);

    removeWrittenOnumsByTid(tid, worker);
  }

  /**
   * <p>
   * Notifies the database that the given transaction is finished preparing. The
   * transaction is not considered to be prepared until this is called. After
   * calling this method, there should not be any further calls to
   * registerUpdate() for the given transaction. This method
   * MUST be called before calling commit().
   * </p>
   * <p>
   * Upon receiving this call, the object database should save the prepared
   * transaction to stable storage so that it can be recovered in case of
   * failure.
   * </p>
   */
  public abstract void finishPrepareWrites(long tid, Principal worker);

  /**
   * Causes the objects prepared in transaction [tid] to be committed. The
   * changes will be visible to read after the given commit time.
   * 
   * @param tid
   *          the transaction id
   * @param commitTime
   *          the time after which the commit should take effect. 
   * @param workerNode
   *          the remote worker that is performing the commit
   * @param workerPrincipal
   *          the principal requesting the commit
   */
  public final void commit(long tid, long commitTime,
      Principal workerPrincipal, SubscriptionManager sm) {
    // Extend the version warranties for the updated objects.
    LongSet onums = removeWrittenOnumsByTid(tid, workerPrincipal);
    if (onums != null) {
      for (LongIterator it = onums.iterator(); it.hasNext();) {
        long onum = it.next();
        extendWarranty(onum, commitTime, ExtendWarrantyMode.FORCE);
      }
    }

    scheduleCommit(tid, commitTime, workerPrincipal, sm);
  }

  /**
   * Schedules a transaction for commit.
   * 
   * @param tid
   *          the transaction id
   * @param commitTime
   *          the time after which the commit should take effect. 
   * @param workerNode
   *          the remote worker that is performing the commit
   * @param workerPrincipal
   *          the principal requesting the commit
   */
  protected abstract void scheduleCommit(long tid, long commitTime,
      Principal workerPrincipal, SubscriptionManager sm);

  /**
   * Causes the objects prepared in transaction [tid] to be discarded.
   * 
   * @param tid
   *          the transaction id
   * @param worker
   *          the principal requesting the rollback
   * @throws AccessException
   *           if the principal differs from the caller of prepare()
   */
  public final void abort(long tid, Principal worker) throws AccessException {
    rollback(tid, worker);
    removeWrittenOnumsByTid(tid, worker);
  }

  protected abstract void rollback(long tid, Principal worker)
      throws AccessException;

  /**
   * Returns the object stored at a particular onum.
   * 
   * @param onum
   *          the identifier
   * @return the object or null if no object exists at the given onum
   */
  public abstract SerializedObject read(long onum);

  /**
   * Returns the version warranty for the object stored at the given onum.
   * 
   * @return the version warranty. If no warranty has been issued, a really old
   *       warranty will be returned.
   */
  public final VersionWarranty getWarranty(long onum) {
    return versionWarrantyTable.get(onum);
  }

  private static final long FIVE_MINUTES = 5 * 60 * 1000;

  /**
   * Stores a version warranty for the object stored at the given onum.
   */
  protected final void putWarranty(long onum, VersionWarranty warranty) {
    versionWarrantyTable.put(onum, warranty);

    if (warranty.expiresAfter(longestWarranty)) {
      // Fudge longestWarranty so we don't continually touch disk when we create
      // a sequence of warranties whose expiries increase with real time.
      longestWarranty = new VersionWarranty(warranty.expiry() + FIVE_MINUTES);
      saveLongestWarranty();
    }
  }

  /**
   * Saves the warranty in <code>longestWarranty</code> to stable storage. On
   * recovery, this value will be used as the default warranty.
   */
  protected abstract void saveLongestWarranty();

  private static enum ExtendWarrantyMode {
    STRICT, NON_STRICT, FORCE
  }

  /**
   * Extends the version warranty of an object, if necessary and possible. The
   * object's resulting warranty is returned.
   * <p>
   * This method will return null in STRICT mode if the object's warranty
   * expires before the requested expiry time, and the warranty could not be
   * extended to the requested time.
   * </p>
   * <p>
   * In NON_STRICT mode, a new warranty is created only if the existing warranty
   * has expired and the object is not write-locked. The new warranty may expire
   * before the requested expiry time. The object's resulting warranty is
   * returned.
   * </p>
   * <p>
   * In STRICT mode, a new warranty is created only if the existing warranty
   * expires before the requested expiry time, and the object is not
   * write-locked. A null value is returned if the object's warranty cannot be
   * extended to the requested expiry time. Otherwise, the object's resulting
   * warranty is returned.
   * </p>
   * <p>
   * FORCE mode is like STRICT mode, except write locks are ignored.
   * </p>
   */
  private VersionWarranty extendWarranty(long onum, long expiry,
      ExtendWarrantyMode mode) {
    // Get the object's current warranty and determine whether it needs to be
    // extended.
    VersionWarranty curWarranty = versionWarrantyTable.get(onum);
    switch (mode) {
    case STRICT:
    case FORCE:
      if (curWarranty.expiresAfter(expiry, false)) return curWarranty;
      break;

    case NON_STRICT:
      if (!curWarranty.expired(true)) return curWarranty;
      break;
    }

    // Need to extend warranty.
    if (mode != ExtendWarrantyMode.FORCE && isWritten(onum)) {
      // Unable to extend warranty.
      switch (mode) {
      case STRICT:
        return null;

      case NON_STRICT:
        return curWarranty;

      case FORCE:
        throw new InternalError("Shouldn't reach here.");
      }
    }

    // Extend the object's warranty.
    VersionWarranty newWarranty = new VersionWarranty(expiry);
    if (expiry > System.currentTimeMillis()) putWarranty(onum, newWarranty);
    return newWarranty;
  }

  /**
   * Attempts to create and return a new version warranty for the object stored
   * at the given onum. If the object is write-locked, then a new warranty
   * cannot be created, and the existing one is returned.
   */
  public VersionWarranty refreshWarranty(long onum) {
    // TODO Make this smarter. Currently, warranties last for at most a minute.
    return extendWarranty(onum, System.currentTimeMillis() + 60000,
        ExtendWarrantyMode.NON_STRICT);
  }

  /**
   * Returns the version number on the object stored at a particular onum.
   * 
   * @throws AccessException
   *           if no object exists at the given onum.
   */
  public int getVersion(long onum) throws AccessException {
    SerializedObject obj = read(onum);
    if (obj == null) throw new AccessException(name, onum);

    return obj.getVersion();
  }

  /**
   * Given the ID of the group to which the given onum belongs. Null is returned
   * if no such group exists.
   */
  public final Long getCachedGroupID(long onum) {
    return globIDByOnum.get(onum);
  }

  private final GroupTable.Entry getGroupTableEntry(long onum) {
    Long groupID = getCachedGroupID(onum);
    if (groupID == null) return null;
    return globTable.getContainer(groupID);
  }

  /**
   * Gives the cached partial group for the given onum. Null is returned if no
   * such partial group exists.
   */
  public final PartialObjectGroup getCachedPartialGroup(long onum) {
    GroupTable.Entry entry = getGroupTableEntry(onum);
    if (entry instanceof PartialObjectGroup) return (PartialObjectGroup) entry;
    return null;
  }

  /**
   * Returns the cached GroupContainer containing the given onum. Null is
   * returned if no such GroupContainer exists.
   */
  public final GroupContainer getCachedGroupContainer(long onum) {
    GroupTable.Entry entry = getGroupTableEntry(onum);
    if (entry instanceof GroupContainer) return (GroupContainer) entry;
    return null;
  }

  /**
   * Inserts the given partial group into the cache.
   */
  public final void cachePartialGroup(PartialObjectGroup partialGroup) {
    // Get a new ID for the partial group.
    long groupID = nextGlobID++;

    partialGroup.setID(groupID);

    // Establish groupID bindings for all non-surrogate onums we're given.
    for (Entry<SerializedObject> entry : partialGroup.objects.entrySet()) {
      SerializedObject obj = entry.getValue();
      if (obj.isSurrogate()) {
        continue;
      }

      // Establish groupID binding for the non-surrogate object.
      long onum = entry.getKey();
      Long oldGroupID = globIDByOnum.put(onum, groupID);
      if (oldGroupID != null) {
        globTable.unpin(oldGroupID);
      }
    }

    if (partialGroup.size() > 0) {
      // Insert into the group table.
      globTable.put(groupID, partialGroup, partialGroup.size());
    }
  }

  /**
   * Coalesces one partial group into another.
   */
  public void coalescePartialGroups(PartialObjectGroup from,
      PartialObjectGroup to) {
    long fromID = from.groupID();
    long toID = to.groupID();

    globTable.remove(fromID);

    for (LongIterator it = from.objects.keySet().iterator(); it.hasNext();) {
      long onum = it.next();
      Long oldGroupID = globIDByOnum.put(onum, toID);
      if (oldGroupID != null && oldGroupID != fromID)
        globTable.unpin(oldGroupID);
    }

    to.mergeFrom(from);
    if (to.size() > 0) globTable.put(toID, to, to.size());
  }

  public GroupContainer promotePartialGroup(PrivateKey signingKey,
      PartialObjectGroup partialGroup) {
    ObjectGroup group = new ObjectGroup(partialGroup.objects);
    Store store = Worker.getWorker().getStore(getName());
    GroupContainer result = new GroupContainer(store, signingKey, group);
    if (partialGroup.size() > 0) {
      globTable.put(partialGroup.groupID(), result, partialGroup.size());
    }

    return result;
  }

  /**
   * Performs operations in response to a committed object update. Removes from
   * cache the glob associated with the onum and notifies the subscription
   * manager of the update.
   * 
   * @param onum
   *          the onum of the object that was updated.
   * @param worker
   *          the worker that performed the update.
   */
  protected final void notifyCommittedUpdate(SubscriptionManager sm, long onum) {
    // Remove from the glob table the glob associated with the onum.
    Long globID = globIDByOnum.remove(onum);
    GroupContainer group = null;
    if (globID != null) {
      GroupTable.Entry entry = globTable.remove(globID);
      // Clean out entries in globIDByOnum that refer to the entry we just
      // removed.
      if (entry instanceof GroupContainer) {
        group = (GroupContainer) entry;
        for (LongIterator it = group.onums.iterator(); it.hasNext();) {
          globIDByOnum.remove(it.next());
        }
      } else {
        PartialObjectGroup partialGroup = (PartialObjectGroup) entry;
        for (LongIterator it = partialGroup.objects.keySet().iterator(); it
            .hasNext();) {
          globIDByOnum.remove(it.next());
        }
      }
    }

    // Notify the subscription manager that the group has been updated.
    // sm.notifyUpdate(onum, worker);
    if (group != null) {
      for (LongIterator onumIt = group.onums.iterator(); onumIt.hasNext();) {
        long relatedOnum = onumIt.next();
        if (relatedOnum == onum) continue;

        Long relatedGlobId = globIDByOnum.get(relatedOnum);
        if (relatedGlobId != null && relatedGlobId == globID) {
          // sm.notifyUpdate(relatedOnum, worker);
        }
      }
    }

  }

  /**
   * Determine whether an onum has outstanding uncommitted changes.
   * 
   * @param onum
   *          the object number in question
   * @return true if the object has been changed by a transaction that hasn't
   *         been committed or rolled back.
   */
  public final boolean isWritten(long onum) {
    return writeLocks.get(onum) != null;
  }

  /**
   * Adjusts writeLocks to account for the fact that the given transaction is
   * about to be committed or aborted.
   */
  protected final void unpin(PendingTransaction tx) {
    for (Pair<SerializedObject, UpdateType> update : tx.modData) {
      long onum = update.first.getOnum();
      writeLocks.remove(onum);
    }
  }

  /**
   * <p>
   * Return a set of onums that aren't currently occupied. The ObjectDB may
   * return the same onum more than once from this method, althogh doing so
   * would encourage collisions. There is no assumption of unpredictability or
   * randomness about the returned ids.
   * </p>
   * <p>
   * The returned onums should be packed in the lower 48 bits. We assume that
   * the object database is never full, and can always provide new onums
   * </p>
   * 
   * @param num
   *          the number of onums to return
   * @return num fresh onums
   */
  public abstract long[] newOnums(int num);

  /**
   * Checks whether an object with the corresponding onum exists, in either
   * prepared or committed form.
   * 
   * @param onum
   *          the onum of to check
   * @return true if an object exists for onum
   */
  public abstract boolean exists(long onum);

  /**
   * Returns the name of this store.
   */
  public final String getName() {
    return name;
  }

  /**
   * Gracefully shutdown the object database.
   * 
   * @throws IOException
   */
  public abstract void close() throws IOException;

  /**
   * Determines whether the object database has been initialized.
   */
  protected abstract boolean isInitialized();

  /**
   * Sets a flag to indicate that the object database has been initialized.
   */
  protected abstract void setInitialized();

  /**
   * Ensures that the object database has been properly initialized. This
   * creates, for example, the name-service map and the store's principal, if
   * they do not already exist in the database.
   */
  public final void ensureInit(TransactionManager tm) {
    if (isInitialized()) {
      recoverState(tm);
      return;
    }

    final Store store = Worker.getWorker().getStore(name);

    Worker.runInSubTransaction(new Worker.Code<Void>() {
      @SuppressWarnings("deprecation")
      @Override
      public Void run() {
        // No need to initialize global constants here, as those objects will be
        // supplied by the workers' local store.
        String principalName = new X500Principal("CN=" + name).getName();
        NodePrincipal._Impl principal =
            (NodePrincipal._Impl) new NodePrincipal._Impl(store)
                .fabric$lang$security$NodePrincipal$(principalName).fetch();
        principal.$forceRenumber(ONumConstants.STORE_PRINCIPAL);

        // Create the label {store->_; store<-_} for the root map.
        // XXX above not done. HashMap needs to be parameterized on labels.
        fabric.util.HashMap._Impl map =
            (fabric.util.HashMap._Impl) new fabric.util.HashMap._Impl(store)
                .fabric$util$HashMap$().fetch();
        map.$forceRenumber(ONumConstants.ROOT_MAP);

        return null;
      }
    });

    setInitialized();
  }

  /**
   * Recovers the object database's in-memory state from stable storage.
   */
  protected abstract void recoverState(TransactionManager tm);

}
