package fabric.store.smartbuffer;

import fabric.common.util.LongKeyMap;
import fabric.common.SerializedObject;

import java.util.concurrent.Future;

public interface SmartBuffer {
    /**
     * Add a transaction with a set of dependencies to the buffer. This method
     * will return a {@code Future} that resolves with {@code true} if the
     * transaction prepares successfully, and {@code false} with {@code versionconflict}
     * if there is something that prevents the transaction from being prepared
     * such as a version conflict. A transaction is viewed as resolved if all
     * of its dependencies are resolved.
     *
     * @param tid The ID of the transaction.
     * @param reads The set of objects that the txn reads.
     * @return A {@code Future} that resolves in accord with the transaction
     *             dependency status.
     */
    Future<BufferRes> add(long tid, LongKeyMap<Integer> reads);

    /**
     * Remove a committed object from the dependencies of any transactions that
     * rely on it. Any transactions that have no unresolved dependencies after
     * this will have their corresponding futures resolved with {@code true}. Any
     * transactions that depend on an older version of the object will have their
     * corresponding futures resolved with {@code false}.
     *
     * @param object The object being committed.
     */
    void remove(SerializedObject object);

    /**
     * Remove a transaction from the buffer. Note that this will make the
     * future that was returned from adding the transaction resolve with
     * {@code false}.
     *
     * @param tid The ID of the transaction.
     */
    void delete(long tid);

    int numLink();
}
