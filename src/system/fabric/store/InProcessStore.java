package fabric.store;

import static fabric.common.Logging.STORE_TRANSACTION_LOGGER;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import fabric.common.ObjectGroup;
import fabric.common.SerializedObject;
import fabric.common.Threading;
import fabric.common.TransactionID;
import fabric.common.exceptions.AccessException;
import fabric.common.exceptions.InternalError;
import fabric.common.net.RemoteIdentity;
import fabric.common.util.LongIterator;
import fabric.common.util.LongKeyHashMap;
import fabric.common.util.LongKeyMap;
import fabric.common.util.LongSet;
import fabric.common.util.Oid;
import fabric.common.util.OidKeyHashMap;
import fabric.common.util.Pair;
import fabric.dissemination.ObjectGlob;
import fabric.lang.Object._Impl;
import fabric.worker.RemoteStore;
import fabric.worker.TransactionCommitFailedException;
import fabric.worker.TransactionPrepareFailedException;
import fabric.worker.Worker;
import fabric.worker.metrics.ExpiryExtension;
import fabric.worker.metrics.treaties.TreatySet;
import fabric.worker.remote.RemoteWorker;

/**
 * In-process implementation of the Store interface for use when a worker is
 * running in-process with a Store. The operations work directly on the Store's
 * TransactionManager object.
 *
 * @author mdgeorge
 */
public class InProcessStore extends RemoteStore {

  protected final TransactionManager tm;
  protected final SurrogateManager sm;
  protected RemoteIdentity<RemoteWorker> localWorkerIdentity;

  public InProcessStore(String name, Store c) {
    super(name, c.publicKey);
    tm = c.tm;
    sm = c.sm;

    // This will be lazily populated.
    localWorkerIdentity = null;
  }

  private RemoteIdentity<RemoteWorker> getLocalWorkerIdentity() {
    if (localWorkerIdentity == null) {
      Worker worker = Worker.getWorker();
      localWorkerIdentity =
          new RemoteIdentity<>(worker.getLocalWorker(), worker.getPrincipal());
    }

    return localWorkerIdentity;
  }

  @Override
  public void abortTransaction(final TransactionID tid) {
    Threading.getPool().submit(new Runnable() {
      @Override
      public void run() {
        tm.abortTransaction(Worker.getWorker().getPrincipal(), tid.topTid);
      }
    });
  }

  @Override
  public void commitTransaction(final long transactionID) {
    Threading.getPool().submit(new Runnable() {
      @Override
      public void run() {
        try {
          tm.commitTransaction(getLocalWorkerIdentity(), transactionID);
          Worker.getWorker().getLocalWorker()
              .notifyStoreCommitted(transactionID);
        } catch (TransactionCommitFailedException e) {
          STORE_TRANSACTION_LOGGER.log(Level.FINE,
              "Commit of transaction {0} failed.",
              Long.toHexString(transactionID));
        }
      }
    });
  }

  @Override
  public long createOnum() {
    try {
      return tm.newOnums(Worker.getWorker().getPrincipal(), 1)[0];
    } catch (AccessException e) {
      throw new InternalError(e);
    }
  }

  @Override
  public void prepareTransaction(final long tid, final boolean singleStore,
      final boolean readOnly, final long expiryToCheck,
      final Collection<_Impl> toCreate,
      final LongKeyMap<Pair<Integer, TreatySet>> reads,
      final Collection<_Impl> writes,
      final Collection<ExpiryExtension> extensions,
      final LongKeyMap<Set<Oid>> extensionsTriggered,
      final LongSet delayedExtensions) {
    Threading.getPool().submit(new Runnable() {
      @Override
      public void run() {
        Collection<SerializedObject> serializedCreates =
            new ArrayList<>(toCreate.size());
        Collection<SerializedObject> serializedWrites =
            new ArrayList<>(writes.size());

        for (_Impl o : toCreate) {
          @SuppressWarnings("deprecation")
          SerializedObject serialized = new SerializedObject(o);
          serializedCreates.add(serialized);
        }

        for (_Impl o : writes) {
          @SuppressWarnings("deprecation")
          SerializedObject serialized = new SerializedObject(o);
          serializedWrites.add(serialized);
        }

        PrepareRequest req =
            new PrepareRequest(tid, serializedCreates, serializedWrites, reads,
                extensions, extensionsTriggered, delayedExtensions);

        // Swizzle remote pointers.
        sm.createSurrogates(req);

        try {
          OidKeyHashMap<TreatySet> longerContracts =
              tm.prepare(Worker.getWorker().getPrincipal(), req);

          long prepareTime = System.currentTimeMillis();

          if (singleStore || readOnly) {
            tm.commitTransaction(getLocalWorkerIdentity(), tid);
          }
          Worker.getWorker().inProcessRemoteWorker
              .notifyStorePrepareSuccess(tid, prepareTime, longerContracts);
        } catch (TransactionPrepareFailedException e) {
          Worker.getWorker().inProcessRemoteWorker.notifyStorePrepareFailed(tid,
              e);
        } catch (TransactionCommitFailedException e) {
          throw new InternalError(e);
        }
      }
    });
  }

  @Override
  public Collection<ObjectGroup> readObjectFromStore(long onum)
      throws AccessException {
    LongKeyMap<SerializedObject> map = new LongKeyHashMap<>();
    SerializedObject obj = tm.read(onum);
    if (obj == null) throw new AccessException(this, onum);
    map.put(onum, obj);
    for (LongIterator iter = tm.getAssociatedOnums(onum).iterator(); iter
        .hasNext();) {
      long relatedOnum = iter.next();
      SerializedObject related = tm.read(relatedOnum);
      if (related == null) throw new AccessException(this, relatedOnum);
      map.put(relatedOnum, related);
    }
    return Collections.singletonList(new ObjectGroup(map));
  }

  @Override
  public LongKeyMap<ObjectGlob> readEncryptedObjectFromStore(long onum)
      throws AccessException {
    LongKeyMap<ObjectGlob> result = new LongKeyHashMap<>();
    result.put(onum, tm.getGlob(onum, getLocalWorkerIdentity().node));
    for (LongIterator iter = tm.getAssociatedOnums(onum).iterator(); iter
        .hasNext();) {
      long relatedOnum = iter.next();
      result.put(relatedOnum,
          tm.getGlob(relatedOnum, getLocalWorkerIdentity().node));
    }
    return result;
  }

  @Override
  protected List<SerializedObject> getStaleObjects(
      LongKeyMap<Pair<Integer, TreatySet>> reads) {
    try {
      return tm.checkForStaleObjects(getPrincipal(), reads);
    } catch (AccessException e) {
      throw new InternalError(e);
    }
  }

  private Object writeReplace() {
    return new SerializationProxy(name);
  }

  @Override
  public void sendExtensions(LongSet extensions,
      Map<RemoteStore, Collection<SerializedObject>> updates) {
    tm.queueExtensions(extensions);
  }

  @Override
  public void unsubscribe(LongSet onums) {
    // Running this in the current thread because it should only be called in
    // the dedicated thread for InProcessRemoteWorker's handling of updates.
    tm.unsubscribe(Worker.getWorker().getLocalWorker(), onums);
  }
}
