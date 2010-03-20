package fabric.core;

import java.util.ArrayList;
import java.util.Collection;

import fabric.worker.Worker;
import fabric.worker.RemoteCore;
import fabric.worker.TransactionCommitFailedException;
import fabric.worker.TransactionPrepareFailedException;
import fabric.common.*;
import fabric.common.exceptions.AccessException;
import fabric.common.exceptions.FetchException;
import fabric.common.exceptions.InternalError;
import fabric.common.util.LongKeyHashMap;
import fabric.common.util.LongKeyMap;
import fabric.core.Node.Core;
import fabric.lang.Object._Impl;

/**
 * In-process implementation of the Core interface for use when a worker is
 * running in-process with a Core. The operations work directly on the Core's
 * TransactionManager object.
 * 
 * @author mdgeorge
 */
public class InProcessCore extends RemoteCore {

  protected TransactionManager tm;

  public InProcessCore(String name, Core c) {
    super(name, c.publicKey);
    tm = c.tm;
  }

  @Override
  public void abortTransaction(boolean useAuthentication, TransactionID tid) {
    try {
      tm.abortTransaction(Worker.getWorker().getPrincipal(), tid.topTid);
    } catch (AccessException e) {
      throw new InternalError(e);
    }
  }

  @Override
  public void commitTransaction(boolean useAuthentication, long transactionID)
      throws TransactionCommitFailedException {
    tm
        .commitTransaction(null, Worker.getWorker().getPrincipal(),
            transactionID);
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
  @SuppressWarnings("deprecation")
  public boolean prepareTransaction(boolean useAuthentication, long tid,
      long commitTime, Collection<_Impl> toCreate, LongKeyMap<Integer> reads,
      Collection<_Impl> writes) throws TransactionPrepareFailedException {
    Collection<SerializedObject> serializedCreates =
        new ArrayList<SerializedObject>(toCreate.size());
    Collection<SerializedObject> serializedWrites =
        new ArrayList<SerializedObject>(writes.size());

    for (_Impl o : toCreate)
      serializedCreates.add(new SerializedObject(o));

    for (_Impl o : writes)
      serializedWrites.add(new SerializedObject(o));

    PrepareRequest req =
        new PrepareRequest(tid, commitTime, serializedCreates,
            serializedWrites, reads);

    return tm.prepare(Worker.getWorker().getPrincipal(), req);
  }

  @Override
  public ObjectGroup readObjectFromCore(long onum) throws FetchException {
    LongKeyMap<SerializedObject> map = new LongKeyHashMap<SerializedObject>();
    SerializedObject obj = tm.read(onum);
    if (obj == null) throw new FetchException(new AccessException(this, onum));
    map.put(onum, obj);
    return new ObjectGroup(map);
  }

}
