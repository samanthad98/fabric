package fabric.store.smartbuffer;

import fabric.common.net.RemoteIdentity;
import fabric.common.util.LongKeyMap;
import fabric.common.util.OidKeyHashMap;
import fabric.common.SerializedObject;
import fabric.store.PrepareRequest;
import fabric.store.PrepareRequest.ItemPrepare;
import fabric.store.db.ObjectDB;
import fabric.worker.TransactionPrepareFailedException;
import fabric.worker.remote.RemoteWorker;

import java.util.Set;
import java.util.concurrent.Future;

public interface SmartBuffer {
    /**
     * Add a transaction with a set of dependencies to the buffer. This method
     * will return a {@code Future} that resolves with {@code true} if the
     * transaction prepares successfully, and {@code false} if there is
     * something that prevents the transaction from being prepared such as a
     * version conflict. A transaction is viewed as resolved if all of its
     * dependencies are resolved.
     *
     * Preconditions:
     *  - the store must be set via {@link SmartBuffer#setDatabase(ObjectDB)} before
     *    calling this method.
     *  - the transaction ID must exist as a pending transaction in the store
     *
     *
     * @param tid The ID of the transaction.
     * @param reads The PrepareRequest
     * @return A {@code Future} that resolves in accord with the transaction
     *             dependency status.
     */
    Future<TransactionPrepareFailedException> add(long tid, LongKeyMap<Integer> reads);

    /**
     * Remove a dependency from the dependencies of any transactions that rely
     * on it. Any transactions that have no unresolved dependencies after this
     * will have their corresponding futures resolved with {@code true} if any
     * required locks can be successfully grabbed, and {@code false} otherwise.
     *
     * Note that the store must be set via {@link SmartBuffer#setStore(Store)}
     * before calling this method.
     *
     * @param object The dependency.
     */
    void remove(SerializedObject object);

    /**
     * Remove a transaction from the buffer. Note that this will make the
     * future that was returned from adding the transaction resolve with
     * {@code false}.
     *
     * Note that the store must be set via {@link SmartBuffer#setStore(Store)}
     * before calling this method.
     *
     * @param tid The ID of the transaction.
     */
    void delete(long tid);

    /**
     * Set the corresponding store for this buffer. Note that this method
     * <b>must</b> be called before {@link SmartBuffer#add(long, Set)},
     * {@link SmartBuffer#remove(ObjectVN)}, {@link SmartBuffer#eject(ObjectVN)}
     * and {@link SmartBuffer#delete(long)}.
     *
     * @param store The store to assign.
     */
    void setDatabase(ObjectDB database);

    int numLink();
}
