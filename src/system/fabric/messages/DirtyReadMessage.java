package fabric.messages;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import fabric.worker.Worker;
import fabric.worker.Store;
import fabric.worker.remote.RemoteWorker;
import fabric.common.SerializedObject;
import fabric.common.TransactionID;
import fabric.common.exceptions.FabricException;
import fabric.common.exceptions.InternalError;
import fabric.lang.Object._Impl;
import fabric.net.UnreachableNodeException;

/**
 * Represents a request from a worker to read an object owned by another worker.
 */
public class DirtyReadMessage
     extends Message<DirtyReadMessage.Response>
  implements MessageToWorker
{
  //////////////////////////////////////////////////////////////////////////////
  // message  contents                                                        //
  //////////////////////////////////////////////////////////////////////////////

  public final TransactionID tid;
  public final Store store;
  public final long onum;

  public DirtyReadMessage(TransactionID tid, Store store, long onum) {
    super(MessageType.DIRTY_READ);

    this.tid = tid;
    this.store = store;
    this.onum = onum;
  }

  //////////////////////////////////////////////////////////////////////////////
  // response contents                                                        //
  //////////////////////////////////////////////////////////////////////////////

  public static class Response implements Message.Response {

    public final _Impl obj;

    public Response(_Impl obj) {
      this.obj = obj;
    }
  }

  //////////////////////////////////////////////////////////////////////////////
  // visitor methods                                                          //
  //////////////////////////////////////////////////////////////////////////////

  public Response dispatch(MessageToWorkerHandler h) throws FabricException {
    return h.handle(this);
  }

  //////////////////////////////////////////////////////////////////////////////
  // convenience method for sending                                           //
  //////////////////////////////////////////////////////////////////////////////

  public Response send(RemoteWorker remoteWorker)
      throws UnreachableNodeException {
    try {
      return super.send(remoteWorker, true);
    } catch (UnreachableNodeException e) {
      throw e;
    } catch (FabricException e) {
      throw new InternalError("Unexpected response from worker.", e);
    }
  }

  //////////////////////////////////////////////////////////////////////////////
  // serialization cruft                                                      //
  //////////////////////////////////////////////////////////////////////////////

  @Override
  protected void writeMessage(DataOutput out) throws IOException {
    tid.write(out);
    out.writeUTF(store.name());
    out.writeLong(onum);
  }

  /* readMessage */
  protected DirtyReadMessage(DataInput in) throws IOException {
    this(new TransactionID(in), Worker.getWorker().getStore(in.readUTF()), in
        .readLong());
  }

  @Override
  protected void writeResponse(DataOutput out, Response r) throws IOException {
    if (r.obj != null) {
      out.writeBoolean(true);
      out.writeUTF(r.obj.$getStore().name());
      SerializedObject.write(r.obj, out);
    } else {
      out.writeBoolean(false);
    }
  }

  @Override
  protected Response readResponse(DataInput in) throws IOException {
    if (!in.readBoolean()) {
      return new Response(null);
    }

    Store store = Worker.getWorker().getStore(in.readUTF());
    SerializedObject serialized = new SerializedObject(in);
    _Impl deserialized = serialized.deserialize(store);

    return new Response(deserialized);
  }

}
