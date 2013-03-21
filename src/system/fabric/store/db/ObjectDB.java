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
import fabric.common.SerializedObject;
import fabric.common.VersionWarranty;
import fabric.common.exceptions.AccessException;
import fabric.common.util.ConcurrentLongKeyHashMap;
import fabric.common.util.ConcurrentLongKeyMap;
import fabric.common.util.LongHashSet;
import fabric.common.util.LongIterator;
import fabric.common.util.LongKeyMap;
import fabric.common.util.LongSet;
import fabric.common.util.OidKeyHashMap;
import fabric.common.util.Pair;
import fabric.lang.security.NodePrincipal;
import fabric.lang.security.Principal;
import fabric.store.SubscriptionManager;
import fabric.store.TransactionManager;
import fabric.worker.Store;
import fabric.worker.TransactionPrepareFailedException;
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
 * name of the store and its private key, and opens the appropriate back-end
 * database if it exists, or creates it if it doesn't exist.
 * </p>
 */
public abstract class ObjectDB {
  private static final int INITIAL_OBJECT_VERSION_NUMBER = 1;

  private static final int MAX_WARRANTY_LENGTH = 1000;

  private final WarrantyIssuer warrantyIssuer;

  /**
   * The store's name.
   */
  protected final String name;

  /**
   * The store's object grouper.
   */
  private final ObjectGrouper objectGrouper;

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
      if (in.readBoolean()) {
        for (int i = 0; i < size; i++) {
          SerializedObject obj = new SerializedObject(in);
          UpdateType updateType =
              in.readBoolean() ? UpdateType.CREATE : UpdateType.WRITE;
          modData.add(new Pair<SerializedObject, UpdateType>(obj, updateType));
        }
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
      writeCommon(out);

      // Indicate that contents of modData will follow.
      out.writeBoolean(true);

      for (Pair<SerializedObject, UpdateType> obj : modData) {
        obj.first.write(out);
        out.writeBoolean(obj.second == UpdateType.CREATE);
      }
    }

    /**
     * Serializes this PendingTransaction out to the given output stream, whilst
     * omitting data about the objects that have been modified or created.
     */
    void writeNoModData(DataOutput out) throws IOException {
      writeCommon(out);

      // Indicate that contents of modData will not follow.
      out.writeBoolean(false);
    }

    /**
     * Writes everything but contents of modData.
     */
    private void writeCommon(DataOutput out) throws IOException {
      out.writeLong(tid);

      out.writeBoolean(owner != null);
      if (owner != null) {
        out.writeUTF(owner.$getStore().name());
        out.writeLong(owner.$getOnum());
      }

      out.writeInt(modData.size());
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
  protected final ConcurrentLongKeyMap<OidKeyHashMap<PendingTransaction>> pendingByTid;

  /**
   * <p>
   * A ref cell containing a warranty that will expire after all warranties
   * issued thus far. The ref cell is used as a mutex for accessing its value.
   * </p>
   * <p>
   * This should be saved to stable storage and restored when starting up.
   * </p>
   */
  protected final VersionWarranty[] longestWarranty;

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
  protected final ConcurrentLongKeyMap<Long> writeLocks;

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
  protected final ConcurrentLongKeyMap<OidKeyHashMap<LongSet>> writtenOnumsByTid;

  protected ObjectDB(String name, PrivateKey privateKey) {
    this.name = name;
    this.pendingByTid =
        new ConcurrentLongKeyHashMap<OidKeyHashMap<PendingTransaction>>();
    this.writeLocks = new ConcurrentLongKeyHashMap<Long>();
    this.writtenOnumsByTid =
        new ConcurrentLongKeyHashMap<OidKeyHashMap<LongSet>>();
    this.objectGrouper = new ObjectGrouper(this, privateKey);
    this.longestWarranty = new VersionWarranty[] { new VersionWarranty(0) };
    this.versionWarrantyTable = new VersionWarrantyTable();
    this.warrantyIssuer = new WarrantyIssuer(250, MAX_WARRANTY_LENGTH, 250);
  }

  /**
   * Opens a transaction so it can be write-prepared.
   * 
   * @param worker
   *          the worker under whose authority the transaction is running.
   */
  public final void beginPrepareWrites(long tid, Principal worker) {
    // Ensure pendingByTid has a submap for the given TID.
    OidKeyHashMap<PendingTransaction> submap =
        new OidKeyHashMap<PendingTransaction>();
    OidKeyHashMap<PendingTransaction> existingSubmap =
        pendingByTid.putIfAbsent(tid, submap);
    if (existingSubmap != null) submap = existingSubmap;

    synchronized (submap) {
      submap.put(worker, new PendingTransaction(tid, worker));
    }
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
   *          the identifier for the transaction preparing the create/write.
   * @param worker
   *          the worker preparing the create/write.
   * @param obj
   *          the modified object.
   * @param versionConflicts
   *          a map containing the transaction's version-conflict information.
   *          If the object modified was out of date, then a new entry will be
   *          added to this map, binding the object's onum to its current
   *          version.
   * @param create
   *          whether the object was newly created by the transaction.
   */
  public final VersionWarranty registerUpdate(long tid, Principal worker,
      SerializedObject obj,
      LongKeyMap<Pair<SerializedObject, VersionWarranty>> versionConflicts,
      UpdateType updateType) throws TransactionPrepareFailedException {
    long onum = obj.getOnum();

    // First, lock the object.
    try {
      lockForWrite(onum, tid);
    } catch (UnableToLockException e) {
      throw new TransactionPrepareFailedException(versionConflicts, "Object "
          + onum + " has been locked by an uncommitted transaction.");
    }

    // Record the updated object. Doing so will also register that the
    // transaction has updated the object.
    OidKeyHashMap<PendingTransaction> submap = pendingByTid.get(tid);

    synchronized (submap) {
      submap.get(worker).modData.add(new Pair<SerializedObject, UpdateType>(
          obj, updateType));
    }

    switch (updateType) {
    case CREATE:
      // Make sure the onum doesn't already exist in the database.
      if (exists(onum)) {
        throw new TransactionPrepareFailedException(versionConflicts, "Object "
            + onum + " already exists.");
      }

      // Set the object's initial version number.
      obj.setVersion(INITIAL_OBJECT_VERSION_NUMBER);
      return VersionWarranty.EXPIRED_WARRANTY;

    case WRITE:
      // Register the update.
      addWrittenOnumByTid(tid, worker, onum);

      // Read the old copy from the database.
      SerializedObject storeCopy = read(onum);

      // Check version numbers.
      int storeVersion = storeCopy.getVersion();
      int workerVersion = obj.getVersion();
      VersionWarranty warranty;
      if (storeVersion != workerVersion) {
        warranty = refreshWarranty(onum);
        versionConflicts.put(onum, new Pair<SerializedObject, VersionWarranty>(
            storeCopy, warranty));
        return VersionWarranty.EXPIRED_WARRANTY;
      }

      // Obtain existing warranty.
      warranty = getWarranty(onum);

      // Update the version number on the prepared copy of the object.
      obj.setVersion(storeVersion + 1);

      // Notify the warranty issuer.
      warrantyIssuer.notifyWritePrepare(onum);

      return warranty;
    }

    throw new InternalError("Unknown update type: " + updateType);
  }

  protected void addWrittenOnumByTid(long tid, Principal worker, long onum) {
    // Get the submap corresponding to the given TID, creating the submap if
    // necessary.
    OidKeyHashMap<LongSet> submap = new OidKeyHashMap<LongSet>();
    OidKeyHashMap<LongSet> existingSubmap =
        writtenOnumsByTid.putIfAbsent(tid, submap);
    if (existingSubmap != null) submap = existingSubmap;

    LongSet set;
    synchronized (submap) {
      set = submap.get(worker);
      if (set == null) {
        set = new LongHashSet();
        submap.put(worker, set);
      }
    }

    synchronized (set) {
      set.add(onum);
    }
  }

  private LongSet removeWrittenOnumsByTid(long tid, Principal worker) {
    OidKeyHashMap<LongSet> writtenOnumsSubmap = writtenOnumsByTid.get(tid);
    if (writtenOnumsSubmap == null) return null;

    LongSet result;
    synchronized (writtenOnumsSubmap) {
      result = writtenOnumsSubmap.remove(worker);
      if (writtenOnumsSubmap.isEmpty()) {
        writtenOnumsByTid.remove(tid, writtenOnumsSubmap);
      }
    }

    return result;
  }

  /**
   * Registers a write lock for the given TID.
   * 
   * @throws UnableToLockException
   *          when a conflicting lock is held.
   */
  private void lockForWrite(long onum, long tid) throws UnableToLockException {
    if (writeLocks.putIfAbsent(onum, tid) != null) {
      throw new UnableToLockException();
    }
  }

  /**
   * Rolls back a partially prepared transaction. (i.e., one for which
   * finishPrepareWrites() has yet to be called.)
   */
  public final void abortPrepareWrites(long tid, Principal worker) {
    OidKeyHashMap<PendingTransaction> submap = pendingByTid.get(tid);

    synchronized (submap) {
      unpin(submap.remove(worker));
      if (submap.isEmpty()) pendingByTid.remove(tid, submap);
    }

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
   * Returns a GroupContainer for the object stored at a particular onum.
   */
  public final GroupContainer readGroup(long onum) {
    return objectGrouper.getGroup(onum);
  }

  /**
   * Returns the version warranty for the object stored at the given onum.
   * 
   * @return the version warranty. If no warranty has been issued, a really old
   *       warranty will be returned.
   */
  public final VersionWarranty getWarranty(long onum) {
    return versionWarrantyTable.get(onum);
  }

  /**
   * Stores a version warranty for the object stored at the given onum.
   */
  protected final void putWarranty(long onum, VersionWarranty warranty) {
    versionWarrantyTable.put(onum, warranty);
    updateLongestWarranty(warranty);
  }

  private void updateLongestWarranty(VersionWarranty warranty) {
    synchronized (longestWarranty) {
      if (warranty.expiresAfter(longestWarranty[0])) {
        // Fudge longestWarranty so we don't continually touch disk when we create
        // a sequence of warranties whose expiries increase with real time.
        longestWarranty[0] = new VersionWarranty(warranty.expiry() + 30 * 1000);
        saveLongestWarranty();
      }
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
    while (true) {
      // Get the object's current warranty and determine whether it needs to be
      // extended.
      VersionWarranty curWarranty = versionWarrantyTable.get(onum);
      switch (mode) {
      case STRICT:
      case FORCE:
        if (curWarranty.expiresAfterStrict(expiry)) return curWarranty;
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
      if (expiry > System.currentTimeMillis()) {
        if (!versionWarrantyTable.extend(onum, curWarranty, newWarranty))
          continue;

        updateLongestWarranty(newWarranty);
      }

      return newWarranty;
    }
  }

  /**
   * Notifies the warranty issuer of a read prepare.
   */
  public void notifyReadPrepare(long onum) {
    warrantyIssuer.notifyReadPrepare(onum);
  }

  /**
   * Returns any existing version warranty on the object stored at the given
   * onum, if the warranty is still valid. Otherwise, attempts to create and
   * return a new version warranty for the object. If the object is
   * write-locked, then a new warranty cannot be created, and the existing one
   * is returned.
   */
  public VersionWarranty refreshWarranty(long onum) {
    Long newExpiry = warrantyIssuer.suggestWarranty(onum);
    if (newExpiry == null) {
      return versionWarrantyTable.get(onum);
    }

    return extendWarranty(onum, newExpiry, ExtendWarrantyMode.NON_STRICT);
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
    LongSet groupOnums = objectGrouper.removeGroup(onum);

    // Notify the subscription manager that the group has been updated.
//    sm.notifyUpdate(onum, worker);
    if (groupOnums != null) {
      for (LongIterator onumIt = groupOnums.iterator(); onumIt.hasNext();) {
        long relatedOnum = onumIt.next();
        if (relatedOnum == onum) continue;

//        sm.notifyUpdate(relatedOnum, worker);
      }
    }
    // Notify the warranty issuer.
    warrantyIssuer.notifyWriteCommit(onum);
  }

  /**
   * Determines whether an onum has outstanding uncommitted changes.
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
      writeLocks.remove(onum, tx.tid);
    }
  }

  /**
   * <p>
   * Returns a set of onums that aren't currently occupied. The ObjectDB may
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
   * Gracefully shuts down the object database.
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
