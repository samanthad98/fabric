package fabric.store.smartbuffer;

import fabric.common.SerializedObject;
import fabric.common.exceptions.AccessException;
import fabric.common.net.RemoteIdentity;
import fabric.common.util.ConcurrentLongKeyHashMap;
import fabric.common.util.LongKeyMap;
import fabric.common.util.LongKeyHashMap;
import fabric.common.util.OidKeyHashMap;
import fabric.store.PrepareRequest;
import fabric.store.db.ObjectDB;
import fabric.worker.TransactionPrepareFailedException;
import fabric.worker.Worker;
import fabric.worker.remote.RemoteWorker;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OptimizedNumLinkBuffer implements SmartBuffer {
    /*
     * A map from the object to transaction IDs that depend on the object.
     */
    private ConcurrentHashMap<LongKeyMap.Entry<Integer>, HashSet<Long>> depsMap;

    /*
     * A map from the object to transaction IDS that depend on the object and that the dependency is not resolved.
     */
    private ConcurrentHashMap<LongKeyMap.Entry<Integer>, HashSet<Long>> unresolveddepsMap;

    /*
     * A map from a transaction ID to the number of unresolved dependencies.
     * The keys are synchronized with {@code futures}.
     */
    private LongKeyHashMap<Integer> numLink;

    /*
     * A map from oid to the associated lock.
     */
    private ConcurrentLongKeyHashMap<Lock> objlocktable;

    /*
     * A map from tid to the associated lock
     */
    private ConcurrentLongKeyHashMap<Lock> txnlocktable;

    /*
     * A map from transactions in the buffer to associated client.
     */
    private ConcurrentLongKeyHashMap<RemoteIdentity<RemoteWorker>> clientMap;

    /*
     * A map from tid to pending
     */
    private ConcurrentLongKeyHashMap<PrepareRequest> PendingTxn;

    /*
     *
     */
    private ConcurrentLongKeyHashMap<CompletableFuture<TransactionPrepareFailedException>> futures;

    /*
     * A pointer to the store that the buffer is associated with.
     */
    public ObjectDB database;

    private int num_abort_lock;
    private int num_abort_vc;
    private int num_resolve;


    public OptimizedNumLinkBuffer() {
        depsMap = new ConcurrentHashMap<>();
        unresolveddepsMap = new ConcurrentHashMap<>();
        numLink = new LongKeyHashMap<>();
        objlocktable = new ConcurrentLongKeyHashMap<>();
        txnlocktable = new ConcurrentLongKeyHashMap<>();
        clientMap = new ConcurrentLongKeyHashMap<>();
        PendingTxn = new ConcurrentLongKeyHashMap<>();

        num_abort_lock = 0;
        num_abort_vc = 0;
    }

    private Lock getObjLock(Long onum) {
        Lock lock = new ReentrantLock();
        Lock existing = objlocktable.putIfAbsent(onum, lock);
        return existing == null? lock : existing;
    }

    private Lock getTxnLock(Long tid) {
        Lock lock = new ReentrantLock();
        Lock existing = txnlocktable.putIfAbsent(tid, lock);
        return existing == null? lock : existing;
    }

    @Override
    public Future<TransactionPrepareFailedException> add(long tid, LongKeyMap<Integer> reads) {
        CompletableFuture<TransactionPrepareFailedException> future = new CompletableFuture<>();
        synchronized (getTxnLock(tid)) {
            numLink.put(tid, 0);
            futures.put(tid, future);
        }

        OidKeyHashMap<SerializedObject> versionConflicts = new OidKeyHashMap<>();

        for (LongKeyMap.Entry<Integer> object : reads.entrySet()) {
            long onum = object.getKey();
            int version = object.getValue();

            synchronized (getObjLock(onum)) {
                try {
                    if (database.getVersion(onum) > version) {
                        //version conflict
                        synchronized (getTxnLock(tid)) {
                            numLink.remove(tid);
                            versionConflicts.put(Worker.getWorker().getStore(database.getName()), onum, database.read(onum));
                        }
                    } else if (versionConflicts.isEmpty() && database.getVersion(onum) < version) {
                        addtoSetMap(unresolveddepsMap, object, tid);
                        addtoSetMap(depsMap, object, tid);

                        synchronized (getTxnLock(tid)) {
                            if (numLink.containsKey(tid)) {
                                numLink.put(tid, numLink.get(tid) + 1);
                            } else {
                                break;
                            }
                        }
                    } else {
                        addtoSetMap(depsMap, object, tid);
                    }
                } catch (AccessException e) {
                    // Should never happen
                    e.printStackTrace();
                }

            }
        }

        if (!versionConflicts.isEmpty()) {
            synchronized (getTxnLock(tid)) {
                numLink.remove(tid);
                futures.remove(tid);
            }
            future.complete(new TransactionPrepareFailedException(versionConflicts));
            return future;
        }

        synchronized (getTxnLock(tid)) {
            if (numLink.containsKey(tid)) {
                if (numLink.get(tid) == 0) {
                    numLink.remove(tid);
                    futures.remove(tid);
                    future.complete(new TransactionPrepareFailedException(true));
                }
            }
        }
        return future;
    }

    @Override
    public void remove(SerializedObject object) {
        eject(object);
        long onum = object.getOnum();
        int version = object.getVersion();
        synchronized (getObjLock(onum)) {
            LongKeyMap.Entry<Integer> to_remove = new LongKeyHashMap.SimpleEntry<>(onum, version);
            //If the current version of object is not ejected from the buffer
            if (unresolveddepsMap.containsKey(to_remove)) {
                for (long tid : depsMap.get(to_remove)) {
                    synchronized (getTxnLock(tid)) {
                        if (numLink.containsKey(tid)) {
                            numLink.put(tid, numLink.get(tid) - 1);
                            if (numLink.get(tid) == 0) {
                                numLink.remove(tid);
                                futures.get(tid).complete(new TransactionPrepareFailedException(true));
                                futures.remove(tid);
                            }
                        }
                    }
                }
            }
            unresolveddepsMap.remove(object);
            depsMap.remove(object);
        }
    }

    public void eject(SerializedObject object) {
        long onum = object.getOnum();
        int version = object.getVersion();
        synchronized (getObjLock(onum)) {
            LongKeyMap.Entry<Integer> to_remove = new LongKeyHashMap.SimpleEntry<>(onum, version);
            if (depsMap.containsKey(to_remove)){
                for (long tid : depsMap.get(to_remove)){
                    synchronized (getTxnLock(tid)) {
                        if (numLink.containsKey(tid)){
                            OidKeyHashMap<SerializedObject> versionConflicts = new OidKeyHashMap<>();
                            versionConflicts.put(Worker.getWorker().getStore(database.getName()), onum, database.read(onum));
                            TransactionPrepareFailedException e = new TransactionPrepareFailedException(versionConflicts);

                            CompletableFuture<TransactionPrepareFailedException> future = futures.get(tid);
                            future.complete(e);

                            numLink.remove(tid);
                            futures.remove(tid);
                        }
                    }
                }
                depsMap.remove(to_remove);
            }
        }
    }

    @Override
    public void delete(long tid) {
        synchronized (getTxnLock(tid)) {
            numLink.remove(tid);
            futures.remove(tid);
        }
    }

    @Override
    public void setDatabase(ObjectDB database) {
        this.database = database;
    }

    @Override
    public int numLink() {
        return numLink.size();
    }

    @Override
    public String toString() {
        return String.format(
                "Buffer resolved %d txns, aborted %d txns because of a lock conflict, %d txns because of a version conflict",
                num_resolve, num_abort_lock, num_abort_vc
        );
    }

    public void addtoSetMap(ConcurrentHashMap<LongKeyMap.Entry<Integer>,
            HashSet<Long>> map, LongKeyMap.Entry<Integer> object, long tid) {
        if (!map.containsKey(object)) {
            map.put(object, new HashSet<>());
        }
        map.get(object).add(tid);
    }
}
