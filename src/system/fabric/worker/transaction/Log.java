package fabric.worker.transaction;

import static fabric.common.Logging.HOTOS_LOGGER;
import static fabric.common.Logging.WORKER_DEADLOCK_LOGGER;
import static fabric.common.Logging.WORKER_TRANSACTION_LOGGER;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;

import fabric.common.Logging;
import fabric.common.SerializedObject;
import fabric.common.SysUtil;
import fabric.common.Threading;
import fabric.common.Timing;
import fabric.common.TransactionID;
import fabric.common.util.LongHashSet;
import fabric.common.util.LongKeyHashMap;
import fabric.common.util.LongKeyMap;
import fabric.common.util.LongSet;
import fabric.common.util.Oid;
import fabric.common.util.OidKeyHashMap;
import fabric.common.util.Pair;
import fabric.common.util.WeakReferenceArrayList;
import fabric.lang.Object._Impl;
import fabric.lang.security.LabelCache;
import fabric.lang.security.SecurityCache;
import fabric.metrics.util.AbstractSubject;
import fabric.metrics.util.Observer;
import fabric.metrics.util.Subject;
import fabric.worker.FabricSoftRef;
import fabric.worker.RemoteStore;
import fabric.worker.Store;
import fabric.worker.TransactionRestartingException;
import fabric.worker.Worker;
import fabric.worker.metrics.ExpiryExtension;
import fabric.worker.metrics.treaties.MetricTreaty;
import fabric.worker.metrics.treaties.TreatySet;
import fabric.worker.remote.RemoteWorker;
import fabric.worker.remote.WriterMap;

/**
 * Stores per-transaction information. Records the objects that are created,
 * read, and written during a single nested transaction.
 */
public final class Log {
  public static final Log NO_READER = new Log((Log) null);

  /**
   * The transaction ID for this log.
   */
  TransactionID tid;

  /**
   * The log for the parent transaction, or null if there is none. A null value
   * here does not necessarily mean that this is the top-level transaction. The
   * tid should be checked to determine whether this transaction is top-level.
   */
  final Log parent;

  /**
   * A map indicating where to fetch objects from.
   */
  WriterMap writerMap;

  /**
   * The sub-transaction.
   */
  private Log child;

  /**
   * The thread that is running this transaction.
   */
  Thread thread;

  /**
   * The TxnStats associated with this transaction.
   */
  TxnStats stats;

  /**
   * Prepare object associated with this log.  Allows for remote workers to
   * initiate second phase of commit protocol through log.
   */
  TransactionPrepare prepare;

  /**
   * A flag indicating whether this transaction should abort or be retried. This
   * flag should be checked before each operation. This flag is set when it's
   * non-null and indicates the transaction in the stack that is to be retried;
   * all child transactions are to be aborted.
   */
  volatile TransactionID retrySignal;

  /**
   * An exception with stack trace and reason for retry being signalled.  Null
   * if there's no retry being signalled.
   */
  volatile RetrySignalException retryCause;

  /**
   * Maps OIDs to <code>readMap</code> entries for objects read in this
   * transaction or completed sub-transactions. Reads from running or aborted
   * sub-transactions don't count here.
   */
  // Proxy objects aren't used for keys here because doing so would result in
  // calls to hashcode() and equals() on such objects, resulting in fetching the
  // corresponding Impls from the store.
  protected final OidKeyHashMap<ReadMap.Entry> reads;

  /**
   * Reads on objects that have been read by an ancestor transaction.
   */
  protected final List<ReadMap.Entry> readsReadByParent;

  /**
   * A collection of all objects created in this transaction or completed
   * sub-transactions. Objects created in running or aborted sub-transactions
   * don't count here. To keep them from being pinned, objects on local store
   * are not tracked here.
   */
  protected final OidKeyHashMap<_Impl> creates;

  /**
   * Tracks objects created on local store. See <code>creates</code>.
   */
  protected final WeakReferenceArrayList<_Impl> localStoreCreates;

  /**
   * A collection of all objects modified in this transaction or completed
   * sub-transactions. Objects modified in running or aborted sub-transactions
   * don't count here. To keep them from being pinned, objects on local store
   * are not tracked here.
   */
  protected final OidKeyHashMap<_Impl> writes;

  /**
   * Collection of {@link Subjects} in this transaction that need to be/have
   * been observed by {@link Observer}s before the transaction commits.
   */
  protected final OidKeyHashMap<Subject> unobservedSamples;

  /**
   * Flag indicating if we're in the middle of resolving observations (to avoid
   * infinite loop).
   */
  protected boolean resolving = false;

  /**
   * A collection of {@link TreatySet}s that are extended by this transaction
   */
  protected final OidKeyHashMap<ExpiryExtension> extendedTreaties;

  /**
   * A collection of {@link Oid}s that have treaties that should be extended
   * after this transaction, mapping to the objects that trigger this possible
   * extension.
   * TODO: Maybe index by treaty id as well.
   */
  protected final OidKeyHashMap<Set<Oid>> delayedExtensions;

  /**
   * A collection of Oids that trigger delayedExtensions to run after this
   * transaction. (Acts as the reverse map for delayedExtensions.) Null key used
   * for those extensions which have no trigger.
   * TODO: Maybe specify treaty id in mapping target.
   */
  protected final OidKeyHashMap<Set<Oid>> extensionTriggers;

  /**
   * Tracks objects on local store that have been modified. See
   * <code>writes</code>.
   */
  protected final WeakReferenceArrayList<_Impl> localStoreWrites;

  /**
   * The set of workers called by this transaction and completed
   * sub-transactions.
   */
  public final List<RemoteWorker> workersCalled;

  /**
   * Indicates the state of commit for the top-level transaction.
   */
  public final CommitState commitState;

  public static class CommitState {
    public static enum Values {
      /**
       * Signifies a transaction before it has been prepared or aborted.
       */
      UNPREPARED,
      /**
       * Signifies a transaction that is currently being prepared.
       */
      PREPARING,
      /**
       * Signifies a transaction that has successfully prepared, but has not yet
       * been committed.
       */
      PREPARED,
      /**
       * Signifies a transaction that has failed to prepare, but has not yet
       * been rolled back.
       */
      PREPARE_FAILED,
      /**
       * Signifies a transaction that is currently being committed.
       */
      COMMITTING,
      /**
       * Signifies a transaction that has been committed.
       */
      COMMITTED,
      /**
       * Signifies a transaction that is currently being aborted.
       */
      ABORTING,
      /**
       * Signifies a transaction that has been aborted.
       */
      ABORTED
    }

    public Values value = Values.UNPREPARED;
  }

  public final AbstractSecurityCache securityCache;

  /**
   * The time at which this subtransaction was started.
   */
  public final long startTime;

  /**
   * Earliest expiry used in this transaction.
   */
  private long expiryToCheck = Long.MAX_VALUE;

  /**
   * If a transaction T's log appears in this set, then this transaction is
   * waiting for a lock that is held by transaction T.
   */
  private final Set<Log> waitsFor;

  /**
   * The lock, if any, this transaction is waiting on.
   */
  private Object waitsOn;

  /**
   * Locks that will be acquired if this transaction commits successfully.
   */
  protected final OidKeyHashMap<Boolean> acquires = new OidKeyHashMap<>();

  /**
   * Locks that will be released if this transaction commits successfully.
   */
  protected final OidKeyHashMap<Boolean> pendingReleases =
      new OidKeyHashMap<>();

  /**
   * Locks that were created in this transaction.
   */
  protected final OidKeyHashMap<Boolean> locksCreated = new OidKeyHashMap<>();

  /**
   * Creates a new log with the given parent and the given transaction ID. The
   * TID for the parent and the given TID are assumed to be consistent. If the
   * given TID is null, a random tid is generated for the subtransaction.
   */
  Log(Log parent, TransactionID tid) {
    this.parent = parent;
    if (tid == null) {
      if (parent == null) {
        this.tid = new TransactionID();
      } else {
        this.tid = new TransactionID(parent.tid);
      }
    } else {
      this.tid = tid;
    }

    this.child = null;
    this.thread = Thread.currentThread();
    this.retrySignal = parent == null ? null : parent.retrySignal;
    this.retryCause = parent == null ? null : parent.retryCause;
    this.reads = new OidKeyHashMap<>();
    this.readsReadByParent = new ArrayList<>();
    this.creates = new OidKeyHashMap<>();
    this.localStoreCreates = new WeakReferenceArrayList<>();
    this.writes = new OidKeyHashMap<>();
    this.unobservedSamples = new OidKeyHashMap<>();
    this.extendedTreaties = new OidKeyHashMap<>();
    this.delayedExtensions = new OidKeyHashMap<>();
    this.extensionTriggers = new OidKeyHashMap<>();
    this.localStoreWrites = new WeakReferenceArrayList<>();
    this.workersCalled = new ArrayList<>();
    this.startTime = System.currentTimeMillis();
    this.waitsFor = new HashSet<>();
    this.waitsOn = null;

    if (parent != null) {
      this.unobservedSamples.putAll(parent.unobservedSamples);
      try {
        Timing.SUBTX.begin();
        this.writerMap = new WriterMap(parent.writerMap);
        synchronized (parent) {
          parent.child = this;
        }

        commitState = parent.commitState;
        this.securityCache =
            new SecurityCache((SecurityCache) parent.securityCache);
      } finally {
        Timing.SUBTX.end();
      }
      this.resolving = parent.resolving;
      this.locksCreated.putAll(parent.locksCreated);
      this.acquires.putAll(parent.acquires);
      stats = parent.stats;
    } else {
      this.writerMap = new WriterMap(this.tid.topTid);
      commitState = new CommitState();

      LabelCache labelCache = Worker.getWorker().labelCache;
      this.securityCache = new SecurityCache(labelCache);

      // New top-level frame. Register it in the transaction registry.
      TransactionRegistry.register(this);
      stats = TransactionManager.getInstance().stats;
    }
  }

  /**
   * Creates a nested transaction whose parent is the transaction with the given
   * log. The created transaction log is added to the parent's children.
   *
   * @param parent
   *          the log for the parent transaction or null if creating the log for
   *          a top-level transaction.
   */
  Log(Log parent) {
    this(parent, null);
  }

  /**
   * Creates a log with the given transaction ID.
   */
  public Log(TransactionID tid) {
    this(null, tid);
  }

  /**
   * Returns true iff the given Log is in the ancestry of (or is the same as)
   * this log.
   */
  boolean isDescendantOf(Log log) {
    return log != null && tid.isDescendantOf(log.tid);
  }

  /**
   * @return true if the transaction is not distributed and neither creates nor
   *         modifies objects on remote stores.
   */
  public boolean isReadOnly() {
    return writes.isEmpty() && creates.isEmpty() && workersCalled.isEmpty();
  }

  /**
   * Returns a set of stores affected by this transaction. This is the set of
   * stores to contact when preparing and committing a transaction.
   */
  Set<Store> storesToContact() {
    Set<Store> result = new HashSet<>();

    result.addAll(reads.storeSet());

    for (_Impl obj : writes.values()) {
      if (obj.$isOwned) result.add(obj.$getStore());
    }

    for (_Impl obj : creates.values()) {
      if (obj.$isOwned) result.add(obj.$getStore());
    }

    if (!localStoreWrites.isEmpty() || !localStoreCreates.isEmpty()) {
      result.add(Worker.getWorker().getLocalStore());
    }

    return result;
  }

  /**
   * @return a set of stores to contact when checking for object freshness.
   */
  Set<Store> storesToCheckFreshness() {
    Set<Store> result = new HashSet<>();
    result.addAll(reads.storeSet());
    for (ReadMap.Entry entry : readsReadByParent) {
      result.add(entry.getStore());
    }

    return result;
  }

  /**
   * Returns a map from onums to version numbers of objects read at the given
   * store. Reads on created objects are never included.
   *
   * @param includeModified
   *          whether to include reads on modified objects.
   */
  LongKeyMap<Pair<Integer, TreatySet>> getReadsForStore(Store store,
      boolean includeModified) {
    LongKeyMap<Pair<Integer, TreatySet>> result = new LongKeyHashMap<>();
    LongKeyMap<ReadMap.Entry> submap = reads.get(store);
    if (submap == null) return result;

    for (LongKeyMap.Entry<ReadMap.Entry> entry : submap.entrySet()) {
      result.put(entry.getKey(), new Pair<>(entry.getValue().getVersionNumber(),
          entry.getValue().getTreaties()));
    }

    if (parent != null) {
      for (ReadMap.Entry entry : readsReadByParent) {
        FabricSoftRef entryRef = entry.getRef();
        if (store.equals(entryRef.store)) {
          result.put(entryRef.onum,
              new Pair<>(entry.getVersionNumber(), entry.getTreaties()));
        }
      }
    }

    Log curLog = this;
    while (curLog != null) {
      if (store.isLocalStore()) {
        Iterable<_Impl> writesToExclude =
            includeModified ? Collections.<_Impl> emptyList()
                : curLog.localStoreWrites;
        Iterable<_Impl> chain =
            SysUtil.chain(writesToExclude, curLog.localStoreCreates);
        for (_Impl write : chain)
          result.remove(write.$getOnum());
      } else {
        Iterable<_Impl> writesToExclude =
            includeModified ? Collections.<_Impl> emptyList()
                : curLog.writes.values();
        Iterable<_Impl> chain =
            SysUtil.chain(writesToExclude, curLog.creates.values());
        for (_Impl write : chain)
          if (write.$getStore() == store) result.remove(write.$getOnum());
      }
      curLog = curLog.parent;
    }

    return result;
  }

  /**
   * Returns a collection of objects modified at the given store. Writes on
   * created objects are not included.
   */
  Collection<_Impl> getWritesForStore(Store store) {
    // This should be a Set of _Impl, but we have a map indexed by OID to
    // avoid calling hashCode and equals on the _Impls.
    LongKeyMap<_Impl> result = new LongKeyHashMap<>();

    if (store.isLocalStore()) {
      for (_Impl obj : localStoreWrites) {
        if (!extendedTreaties.containsKey(obj)) {
          result.put(obj.$getOnum(), obj);
        }
      }

      for (_Impl create : localStoreCreates) {
        result.remove(create.$getOnum());
      }
    } else {
      for (_Impl obj : writes.values()) {
        if (obj.$getStore() == store && obj.$isOwned
            && !extendedTreaties.containsKey(obj)) {
          result.put(obj.$getOnum(), obj);
        }
      }

      for (_Impl create : creates.values())
        if (create.$getStore() == store) result.remove(create.$getOnum());
    }

    return result.values();
  }

  Collection<ExpiryExtension> getExtensionsForStore(Store store) {
    if (!extendedTreaties.storeSet().contains(store)) {
      return Collections.emptyList();
    }
    List<ExpiryExtension> extensions =
        new ArrayList<>(extendedTreaties.get(store).size());
    for (ExpiryExtension extension : extendedTreaties.get(store).values()) {
      extensions.add(extension);
    }
    return extensions;
  }

  LongKeyMap<Set<Oid>> getTriggeredExtensionsForStore(Store store) {
    if (!extensionTriggers.storeSet().contains(store)) {
      return new LongKeyHashMap<>();
    }
    return extensionTriggers.get(store);
  }

  LongSet getDelayedExtensionsForStore(Store store) {
    LongSet rtn = new LongHashSet();
    if (extensionTriggers.containsKey((fabric.lang.Object) null)) {
      for (Oid o : extensionTriggers.get((fabric.lang.Object) null)) {
        if (o.store == store) {
          rtn.add(o.onum);
        }
      }
    }
    return rtn;
  }

  /**
   * Returns a collection of objects created at the given store.
   */
  Collection<_Impl> getCreatesForStore(Store store) {
    // This should be a Set of _Impl, but to avoid calling methods on the
    // _Impls, we instead use a map keyed on OID.
    LongKeyMap<_Impl> result = new LongKeyHashMap<>();

    if (store.isLocalStore()) {
      for (_Impl obj : localStoreCreates) {
        result.put(obj.$getOnum(), obj);
      }
    } else {
      for (_Impl obj : creates.values())
        if (obj.$getStore() == store && obj.$isOwned)
          result.put(obj.$getOnum(), obj);
    }

    return result.values();
  }

  /**
   * Check if this transaction has been told to abort and retry.
   * @throws TransactionRestartingException if a retry was flagged.
   */
  public void checkRetrySignal() throws TransactionRestartingException {
    if (this.retrySignal != null) {
      synchronized (this) {
        WORKER_TRANSACTION_LOGGER.log(Level.FINEST, "{0} got retry signal",
            this);

        throw new TransactionRestartingException(this.retrySignal,
            this.retryCause);
      }
    }
  }

  /**
   * Sets the retry flag on this and the logs of all sub-transactions.
   */
  public void flagRetry(String reason) {
    Queue<Log> toFlag = new LinkedList<>();
    toFlag.add(this);
    while (!toFlag.isEmpty()) {
      Log log = toFlag.remove();
      synchronized (log) {
        if (log.child != null) toFlag.add(log.child);
        if (log.retrySignal == null || log.retrySignal.isDescendantOf(tid)) {
          log.retrySignal = tid;
          try {
            throw new RetrySignalException(reason);
          } catch (RetrySignalException e) {
            log.retryCause = e;
          }

          // Grab the currently marked blocking dependency
          final Object curWaitingOn = log.waitsOn;
          if (curWaitingOn != null) {
            // Run this in a different thread, we need to avoid any deadlocks in
            // this step.
            final Log logCopy = log;
            Threading.getPool().submit(new Runnable() {
              @Override
              public void run() {
                synchronized (curWaitingOn) {
                  // If still blocked on the object after synchronizing, wake
                  // the thread that needs to be aborted so it sees retry
                  // signal.
                  if (logCopy.waitsOn == curWaitingOn) {
                    logCopy.waitsOn.notifyAll();
                  }
                }
              }
            });
          }
        }
      }
    }
  }

  /**
   * Updates logs and data structures in <code>_Impl</code>s to abort this
   * transaction. All locks held by this transaction are released.
   */
  void abort() {
    Store localStore = Worker.getWorker().getLocalStore();
    Set<Store> stores = storesToContact();
    // Note what we were trying to do before we aborted.
    Logging.log(HOTOS_LOGGER, Level.FINE,
        "aborted tid {0} ({1} stores, {2} extensions, {3} delayed extensions)",
        tid, stores.size() - (stores.contains(localStore) ? 1 : 0),
        extendedTreaties.size(), delayedExtensions.size());
    // Release read locks.
    for (LongKeyMap<ReadMap.Entry> submap : reads) {
      for (ReadMap.Entry entry : submap.values()) {
        entry.releaseLock(this);
      }
    }

    for (ReadMap.Entry entry : readsReadByParent)
      entry.releaseLock(this);

    // Roll back writes and release write locks.
    Iterable<_Impl> chain = SysUtil.chain(writes.values(), localStoreWrites);
    for (_Impl write : chain) {
      synchronized (write) {
        if (WORKER_DEADLOCK_LOGGER.isLoggable(Level.FINEST)) {
          Logging.log(WORKER_DEADLOCK_LOGGER, Level.FINEST,
              "{0} in {5} aborted and released write lock on {1}/{2} ({3}) ({4})",
              this, write.$getStore(), write.$getOnum(), write.getClass(),
              System.identityHashCode(write), Thread.currentThread());
        }
        write.$copyStateFrom(write.$history);

        // Signal any waiting readers/writers.
        if (write.$numWaiting > 0 && !isDescendantOf(write.$writeLockHolder))
          write.notifyAll();
      }
    }
    // Reset the expiry to check.
    expiryToCheck = Long.MAX_VALUE;

    prepare = null;

    if (parent != null && parent.tid.equals(tid.parent)) {
      // The parent frame represents the parent transaction. Null out its child.
      synchronized (parent) {
        parent.child = null;
      }
    } else {
      // This frame will be reused to represent the parent transaction. Clear
      // out the log data structures.
      reads.clear();
      readsReadByParent.clear();
      creates.clear();
      localStoreCreates.clear();
      writes.clear();
      unobservedSamples.clear();
      extendedTreaties.clear();
      delayedExtensions.clear();
      extensionTriggers.clear();
      localStoreWrites.clear();
      workersCalled.clear();
      securityCache.reset();
      acquires.clear();
      pendingReleases.clear();
      locksCreated.clear();

      if (parent != null) {
        writerMap = new WriterMap(parent.writerMap);
        unobservedSamples.putAll(parent.unobservedSamples);
        resolving = parent.resolving;
        locksCreated.putAll(parent.locksCreated);
        acquires.putAll(parent.acquires);
      } else {
        writerMap = new WriterMap(tid.topTid);
      }

      if (retrySignal != null) {
        synchronized (this) {
          if (retrySignal.equals(tid)) {
            retrySignal = null;
            retryCause = null;
          }
        }
      }
    }
  }

  /**
   * Resolve unobserved subjects, either before attempting to commit at the top
   * level or before using a {@link MetricTreaty}.
   */
  public void resolveObservations() {
    // Skip if there's nothing to handle or if this was called in the middle of
    // an already ongoing resolve.
    if (!resolving && !unobservedSamples.isEmpty()) {
      Logging.METRICS_LOGGER.finer("PROCESSING SAMPLES IN " + tid);
      try {
        resolving = true;
        LinkedList<Subject> q = new LinkedList<>();
        for (Subject s : unobservedSamples.values()) {
          q.add(s);
        }
        AbstractSubject._Impl.processSamples(q);
        unobservedSamples.clear();
      } finally {
        resolving = false;
      }
    }
  }

  /**
   * Update the earliest expiry we have to check against.
   */
  protected synchronized void updateExpiry(long exp) {
    expiryToCheck = Math.min(expiryToCheck, exp);
  }

  /**
   * @return the earliest expiry used by this transaction.
   */
  protected long expiry() {
    return expiryToCheck;
  }

  /**
   * Updates logs and data structures in <code>_Impl</code>s to commit this
   * transaction. Assumes there is a parent transaction. This transaction log is
   * merged into the parent's log and any locks held are transferred to the
   * parent.
   */
  void commitNested() {
    // TODO See if lazy merging of logs helps performance.

    if (parent == null || !parent.tid.equals(tid.parent)) {
      // Reuse this frame for the parent transaction.
      return;
    }

    // Merge reads and transfer read locks.
    for (LongKeyMap<ReadMap.Entry> submap : reads) {
      for (ReadMap.Entry entry : submap.values()) {
        parent.transferReadLock(this, entry);
      }
    }

    for (ReadMap.Entry entry : readsReadByParent) {
      entry.releaseLock(this);
    }

    synchronized (parent.unobservedSamples) {
      parent.unobservedSamples.clear();
      parent.unobservedSamples.putAll(unobservedSamples);
    }

    // Do this before writes and creates!
    for (Store s : extendedTreaties.storeSet()) {
      for (ExpiryExtension obs : extendedTreaties.get(s).values()) {
        synchronized (parent.delayedExtensions) {
          if (parent.markedForDelayedExtension(new Oid(s, obs.onum)))
            parent.cancelDelayedExtension(new Oid(s, obs.onum));
        }
        // Check if the parent already plans to extend the treaties, if so,
        // update the mapping.
        synchronized (parent.extendedTreaties) {
          if (parent.extendedTreaties.containsKey(new Oid(s, obs.onum))) {
            parent.extendedTreaties.put(new Oid(s, obs.onum), obs);
            continue;
          }
        }
        synchronized (parent.writes) {
          if (parent.writes.containsKey(new Oid(s, obs.onum))) continue;
        }
        synchronized (parent.creates) {
          if (parent.creates.containsKey(new Oid(s, obs.onum))) continue;
        }
        synchronized (parent.extendedTreaties) {
          // If not already being written or created, add it to the extensions.
          parent.extendedTreaties.put(new Oid(s, obs.onum), obs);
        }
      }
    }

    for (Store s : delayedExtensions.storeSet()) {
      for (LongKeyMap.Entry<Set<Oid>> e : delayedExtensions.get(s).entrySet()) {
        long onum = e.getKey();
        Oid oid = new Oid(s, onum);
        // Skip if the parent is doing any kind of write to the object.
        synchronized (parent.writes) {
          if (parent.writes.containsKey(s, onum)) continue;
        }
        synchronized (parent.creates) {
          if (parent.creates.containsKey(s, onum)) continue;
        }
        synchronized (parent.extendedTreaties) {
          if (parent.extendedTreaties.containsKey(s, onum)) continue;
        }
        synchronized (parent.delayedExtensions) {
          if (e.getValue().isEmpty()) {
            if (parent.markedForDelayedExtension(oid)) continue;
            parent.addDelayedExtension(oid);
          } else {
            for (Oid trigger : e.getValue()) {
              parent.addDelayedExtension(new Oid(s, onum), trigger);
            }
          }
        }
      }
    }

    // Merge writes and transfer write locks.
    OidKeyHashMap<_Impl> parentWrites = parent.writes;
    for (_Impl obj : writes.values()) {
      synchronized (obj) {
        if (obj.$history.$writeLockHolder == parent) {
          // The parent transaction already wrote to the object. Discard one
          // layer of history. In doing so, we also end up releasing this
          // transaction's write lock.
          obj.$history = obj.$history.$history;
        } else {
          // The parent transaction didn't write to the object. Add write to
          // parent and transfer our write lock.
          synchronized (parentWrites) {
            parentWrites.put(obj, obj);
          }
        }
        obj.$writer = null;
        obj.$writeLockHolder = parent;

        // Signal any readers/writers.
        //if (obj.$numWaiting > 0) obj.notifyAll();
      }
    }

    WeakReferenceArrayList<_Impl> parentLocalStoreWrites =
        parent.localStoreWrites;
    for (_Impl obj : localStoreWrites) {
      synchronized (obj) {
        if (obj.$history.$writeLockHolder == parent) {
          // The parent transaction already wrote to the object. Discard one
          // layer of history. In doing so, we also end up releasing this
          // transaction's write lock.
          obj.$history = obj.$history.$history;
        } else {
          // The parent transaction didn't write to the object. Add write to
          // parent and transfer our write lock.
          synchronized (parentLocalStoreWrites) {
            parentLocalStoreWrites.add(obj);
          }
        }
        obj.$writer = null;
        obj.$writeLockHolder = parent;

        // Signal any readers/writers.
        //if (obj.$numWaiting > 0) obj.notifyAll();
      }
    }

    // Merge creates and transfer write locks.
    OidKeyHashMap<_Impl> parentCreates = parent.creates;
    synchronized (parentCreates) {
      for (_Impl obj : creates.values()) {
        parentCreates.put(obj, obj);
        obj.$writeLockHolder = parent;
      }
    }

    WeakReferenceArrayList<_Impl> parentLocalStoreCreates =
        parent.localStoreCreates;
    synchronized (parentLocalStoreCreates) {
      for (_Impl obj : localStoreCreates) {
        parentLocalStoreCreates.add(obj);
        obj.$writeLockHolder = parent;
      }
    }

    // Merge the set of workers that have been called.
    synchronized (parent.workersCalled) {
      for (RemoteWorker worker : workersCalled) {
        if (!parent.workersCalled.contains(worker))
          parent.workersCalled.add(worker);
      }
    }

    // Replace the parent's security cache with the current cache.
    parent.securityCache.set((SecurityCache) securityCache);

    // Merge the writer map.
    synchronized (parent.writerMap) {
      parent.writerMap.putAll(writerMap);
    }

    // Merge pessimistic lock pending operations.
    // Acquires
    synchronized (parent.acquires) {
      parent.acquires.putAll(acquires);
    }
    // Releases
    synchronized (parent.pendingReleases) {
      parent.pendingReleases.putAll(pendingReleases);
    }
    // Creates
    synchronized (parent.locksCreated) {
      parent.locksCreated.putAll(locksCreated);
    }

    // Update the expiry time and drop this child.
    synchronized (parent) {
      parent.expiryToCheck = Math.min(expiryToCheck, parent.expiryToCheck);
      parent.child = null;
    }
  }

  // Time before expiration runs out to process pending extensions
  public static final long EXTENSION_WINDOW = 1000;

  /**
   * Updates logs and data structures in <code>_Impl</code>s to commit this
   * transaction. Assumes this is a top-level transaction. All locks held by
   * this transaction are released.
   */
  void commitTopLevel() {
    // Grab the stores already contacted to be checked against below.
    Set<Store> alreadyContacted = storesToContact();

    // Release write locks on created objects and set version numbers.
    // XXX TRM 3/9/18: Do this before reads and writes so that nobody gets a
    // reference to the creates before we've released the writer locks on
    // creates.
    Iterable<_Impl> chain2 = SysUtil.chain(creates.values(), localStoreCreates);
    for (_Impl obj : chain2) {
      if (WORKER_DEADLOCK_LOGGER.isLoggable(Level.FINEST)) {
        Logging.log(WORKER_DEADLOCK_LOGGER, Level.FINEST,
            "{0} in {5} committed and released (created) write lock on {1}/{2} ({3}) ({4})",
            this, obj.$getStore(), obj.$getOnum(), obj.getClass(),
            System.identityHashCode(obj), Thread.currentThread());
      }
      if (!obj.$isOwned) {
        // The cached object is out-of-date. Evict it.
        obj.$ref.evict();
        continue;
      }

      // XXX: Should we notify here in case somehow a reference is found in
      // another transaction in another thread still?
      obj.$writer = null;
      obj.$writeLockHolder = null;
      obj.$writeLockStackTrace = null;
      obj.$version = 1;
      obj.$readMapEntry.incrementVersionAndUpdateTreaties(
          extendedTreaties.containsKey(obj) ? extendedTreaties.get(obj).treaties
              : obj.$treaties);
      obj.$isOwned = false;
    }

    // Release read locks.
    for (LongKeyMap<ReadMap.Entry> submap : reads) {
      for (LongKeyMap.Entry<ReadMap.Entry> e : submap.entrySet()) {
        long onum = e.getKey();
        ReadMap.Entry entry = e.getValue();
        // Extend the expiry if we did so in this transaction.
        if (extendedTreaties.containsKey(entry.getStore(), onum))
          entry.extendTreaties(
              extendedTreaties.get(entry.getStore(), onum).treaties);
        entry.releaseLock(this);
      }
    }

    // sanity check
    if (!readsReadByParent.isEmpty())
      throw new InternalError("something was read by a non-existent parent");

    // Release write locks and ownerships; update version numbers.
    Iterable<_Impl> chain = SysUtil.chain(writes.values(), localStoreWrites);
    for (_Impl obj : chain) {
      if (!obj.$isOwned) {
        // The cached object is out-of-date. Evict it.
        obj.$ref.evict();
        continue;
      }

      synchronized (obj) {
        if (WORKER_DEADLOCK_LOGGER.isLoggable(Level.FINEST)) {
          Logging.log(WORKER_DEADLOCK_LOGGER, Level.FINEST,
              "{0} in {5} committed and released write lock on {1}/{2} ({3}) ({4})",
              this, obj.$getStore(), obj.$getOnum(), obj.getClass(),
              System.identityHashCode(obj), Thread.currentThread());
        }
        obj.$writer = null;
        obj.$writeLockHolder = null;
        obj.$writeLockStackTrace = null;
        // Don't increment the version if it's only extending treaties
        if (!extendedTreaties.containsKey(obj)) {
          obj.$version++;
          obj.$readMapEntry.incrementVersionAndUpdateTreaties(obj.$treaties);
        } else {
          obj.$readMapEntry.extendTreaties(extendedTreaties.containsKey(obj)
              ? extendedTreaties.get(obj).treaties
              : obj.$treaties);
        }
        obj.$isOwned = false;

        // Discard one layer of history.
        obj.$history = obj.$history.$history;

        // Signal any waiting readers/writers.
        if (obj.$numWaiting > 0) obj.notifyAll();
      }

      // Note writes that committed so they can be flushed at the caller.
      TransactionManager tm = TransactionManager.getInstance();
      if (tm.committedWrites != null)
        tm.committedWrites.put(new Oid(obj), obj.$version);
    }

    // Queue up extension transactions not triggered by an update and not sent
    // in the commit message already.
    Map<Store, LongSet> extensionsToSend = new HashMap<>();
    Set<Oid> noTriggerExtensions =
        extensionTriggers.get((fabric.lang.Object) null);
    if (noTriggerExtensions != null) {
      for (Oid o : noTriggerExtensions) {
        if (alreadyContacted.contains(o.store)) continue;
        if (!extensionsToSend.containsKey(o.store))
          extensionsToSend.put(o.store, new LongHashSet());
        extensionsToSend.get(o.store).add(o.onum);
      }
    }

    for (Map.Entry<Store, LongSet> entry : extensionsToSend.entrySet()) {
      entry.getKey().sendExtensions(entry.getValue(),
          new HashMap<RemoteStore, Collection<SerializedObject>>());
    }

    // Update this thread's lock state in the TransactionManager.
    TransactionManager tm = TransactionManager.getInstance();
    tm.updateLockState(acquires, pendingReleases);
    prepare = null;

    // Merge the security cache into the top-level label cache.
    securityCache.mergeWithTopLevel();
  }

  /**
   * Transfers a read lock from a child transaction.
   */
  private void transferReadLock(Log child, ReadMap.Entry readMapEntry) {
    // If we already have a read lock, return; otherwise, register a read lock.
    Boolean lockedByAncestor = readMapEntry.transferLockToParent(child);
    if (lockedByAncestor == null) {
      // We already had a lock; nothing to do.
      return;
    }

    // Only record the read in this transaction if none of our ancestors have
    // read this object.
    if (!lockedByAncestor) {
      FabricSoftRef ref = readMapEntry.getRef();
      synchronized (reads) {
        reads.put(ref.store, ref.onum, readMapEntry);
      }
    } else {
      readsReadByParent.add(readMapEntry);
    }

    // Signal any readers/writers and clear the $reader stamp.
    readMapEntry.signalObject();
  }

  /**
   * Grabs a read lock for the given object.
   */
  void acquireReadLock(_Impl obj) {
    // If we already have a read lock, return; otherwise, register a read
    // lock.
    ReadMap.Entry readMapEntry = obj.$readMapEntry;
    boolean lockedByAncestor = false;
    synchronized (readMapEntry) {
      Set<Log> readers = readMapEntry.getReaders();
      if (readers.contains(this)) {
        // We already have a lock; nothing to do.
        return;
      }

      // Check if any of our ancestors already has a read lock.
      Log curAncestor = parent;
      while (curAncestor != null) {
        if (readers.contains(curAncestor)) {
          lockedByAncestor = true;
          break;
        }

        curAncestor = curAncestor.parent;
      }

      readMapEntry.addLock(this);
    }

    if (obj.$writer != this) {
      // Clear the object's write stamp -- the writer's write condition no
      // longer holds.
      obj.$writer = null;
    }

    // Only record the read in this transaction if none of our ancestors have
    // read this object.
    if (!lockedByAncestor) {
      synchronized (reads) {
        reads.put(obj.$ref.store, obj.$ref.onum, readMapEntry);
      }
    } else {
      readsReadByParent.add(readMapEntry);
    }
  }

  /**
   * Blocks until all threads in <code>threads</code> are finished.
   */
  void waitForThreads() {
  }

  public TransactionID getTid() {
    return tid;
  }

  public Log getChild() {
    return child;
  }

  /**
   * @return the log of the outermost ancestor transaction available at this
   *     worker. This will be different from the top-level transaction if the
   *     top-level was started at a different worker.
   */
  public Log getOutermost() {
    Log result = this;
    while (result.parent != null) {
      result = result.parent;
    }
    return result;
  }

  /**
   * Changes the waitsFor set to an empty set.  This is for waiting on objects
   * which are not necessarily held by other worker transactions, such as
   * waiting for a fetch to complete.
   */
  public void setWaitsFor(Object obj) {
    synchronized (this.waitsFor) {
      this.waitsFor.clear();
      this.waitsOn = obj;
    }
  }

  /**
   * Changes the waitsFor set to a singleton set containing the given log.
   */
  public void setWaitsFor(Log waitsFor, Object obj) {
    synchronized (this.waitsFor) {
      this.waitsFor.clear();
      this.waitsFor.add(waitsFor);
      this.waitsOn = obj;
    }
  }

  /**
   * Changes the waitsFor set to contain exactly the elements of the given set.
   */
  public void setWaitsFor(Set<Log> waitsFor, Object obj) {
    synchronized (this.waitsFor) {
      this.waitsFor.clear();
      this.waitsFor.addAll(waitsFor);
      this.waitsOn = obj;
    }
  }

  /**
   * Empties the waitsFor set.
   */
  public void clearWaitsFor() {
    synchronized (this.waitsFor) {
      this.waitsFor.clear();
      this.waitsOn = null;
    }
  }

  /**
   * Returns a copy of the waitsFor set.
   */
  public Set<Log> getWaitsFor() {
    synchronized (this.waitsFor) {
      return new HashSet<>(this.waitsFor);
    }
  }

  /**
   * Goes through this transaction log and performs an onum renumbering. This is
   * used by fabric.worker.TransactionRegistery.renumberObject. Do not call this
   * unless if you really know what you are doing.
   *
   * @deprecated
   */
  @Deprecated
  public void renumberObject(Store store, long onum, long newOnum) {
    ReadMap.Entry entry = reads.remove(store, onum);
    if (entry != null) {
      reads.put(store, newOnum, entry);
    }

    if (child != null) child.renumberObject(store, onum, newOnum);
  }

  @Override
  public String toString() {
    return "[" + tid + "]";
  }

  // Delayed Extension Management

  /**
   * Update data structures for a new delayed extension, given the triggering
   * dependency.
   */
  public void addDelayedExtension(fabric.lang.Object toBeExtended,
      fabric.lang.Object trigger) {
    addDelayedExtension(new Oid(toBeExtended), new Oid(trigger));
  }

  /**
   * Update data structures for a new delayed extension, given the triggering
   * dependency.
   */
  public void addDelayedExtension(Oid toBeExtended,
      fabric.lang.Object trigger) {
    addDelayedExtension(toBeExtended, new Oid(trigger));
  }

  /**
   * Update data structures for a new delayed extension, given the triggering
   * dependency.
   */
  public void addDelayedExtension(fabric.lang.Object toBeExtended,
      Oid trigger) {
    addDelayedExtension(new Oid(toBeExtended), trigger);
  }

  /**
   * Update data structures for a new delayed extension, given the triggering
   * dependency.
   */
  public void addDelayedExtension(Oid toBeExtended, Oid trigger) {
    synchronized (delayedExtensions) {
      // Forward
      if (!delayedExtensions.containsKey(toBeExtended)) {
        delayedExtensions.put(toBeExtended, new HashSet<Oid>());
      }
      delayedExtensions.get(toBeExtended).add(trigger);

      // Backward
      if (!extensionTriggers.containsKey(trigger)) {
        extensionTriggers.put(trigger, new HashSet<Oid>());
      }
      extensionTriggers.get(trigger).add(toBeExtended);

      // Remove null trigger.
      if (extensionTriggers.containsKey((fabric.lang.Object) null)) {
        extensionTriggers.get((fabric.lang.Object) null).remove(toBeExtended);
      }
    }
  }

  /**
   * Update data structures for a new delayed extension, given no trigger.
   */
  public void addDelayedExtension(fabric.lang.Object toBeExtended) {
    addDelayedExtension(new Oid(toBeExtended));
  }

  /**
   * Update data structures for a new delayed extension, given no trigger.
   */
  public void addDelayedExtension(Oid toBeExtended) {
    synchronized (delayedExtensions) {
      // Forward
      if (!delayedExtensions.containsKey(toBeExtended)) {
        delayedExtensions.put(toBeExtended, new HashSet<Oid>());
      }

      // Backward
      if (!extensionTriggers.containsKey((fabric.lang.Object) null)) {
        extensionTriggers.put((fabric.lang.Object) null, new HashSet<Oid>());
      }
      extensionTriggers.get((fabric.lang.Object) null).add(toBeExtended);
    }
  }

  /**
   * @return true iff the transaction is going to trigger a delayed extension
   * after committing.
   */
  public boolean markedForDelayedExtension(fabric.lang.Object obj) {
    synchronized (delayedExtensions) {
      return delayedExtensions.containsKey(obj);
    }
  }

  /**
   * @return true iff the transaction is going to trigger a delayed extension
   * after committing.
   */
  public boolean markedForDelayedExtension(Oid obj) {
    synchronized (delayedExtensions) {
      return delayedExtensions.containsKey(obj);
    }
  }

  /**
   * Cancel a delayed extension.
   */
  public void cancelDelayedExtension(fabric.lang.Object obj) {
    cancelDelayedExtension(new Oid(obj));
  }

  /**
   * Cancel a delayed extension.
   */
  public void cancelDelayedExtension(Oid obj) {
    synchronized (delayedExtensions) {
      Set<Oid> triggers = delayedExtensions.remove(obj);
      if (triggers != null) {
        if (triggers.isEmpty()) {
          // Remove null trigger.
          if (extensionTriggers.containsKey((fabric.lang.Object) null)) {
            extensionTriggers.get((fabric.lang.Object) null).remove(obj);
          }
        } else {
          for (Oid trigger : triggers) {
            extensionTriggers.get(trigger).remove(obj);
          }
        }
      }
    }
  }

  public OidKeyHashMap<TreatySet> getLongerTreaties() {
    if (prepare != null) return prepare.getLongerTreaties();
    return new OidKeyHashMap<>();
  }

  public long getCommitTime() {
    if (prepare != null) return prepare.getCommitTime();
    return 0;
  }
}
