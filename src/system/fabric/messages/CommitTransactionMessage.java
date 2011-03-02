package fabric.messages;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import fabric.lang.security.Principal;
import fabric.worker.TransactionCommitFailedException;

public class CommitTransactionMessage
    extends
    Message<CommitTransactionMessage.Response, TransactionCommitFailedException> {
  //////////////////////////////////////////////////////////////////////////////
  // message  contents                                                        //
  //////////////////////////////////////////////////////////////////////////////

  public final long transactionID;

  public CommitTransactionMessage(long transactionID) {
    super(MessageType.COMMIT_TRANSACTION, TransactionCommitFailedException.class);
    this.transactionID = transactionID;
  }


  //////////////////////////////////////////////////////////////////////////////
  // response contents                                                        //
  //////////////////////////////////////////////////////////////////////////////

  public static class Response implements Message.Response {
  }

  //////////////////////////////////////////////////////////////////////////////
  // visitor methods                                                          //
  //////////////////////////////////////////////////////////////////////////////

  @Override
  public Response dispatch(Principal p, MessageHandler h)
      throws TransactionCommitFailedException {
    return h.handle(p, this);
  }

  //////////////////////////////////////////////////////////////////////////////
  // serialization cruft                                                      //
  //////////////////////////////////////////////////////////////////////////////

  @Override
  protected void writeMessage(DataOutput out) throws IOException {
    out.writeLong(transactionID);
  }

  /* readMessage */
  protected CommitTransactionMessage(DataInput in) throws IOException {
    this(in.readLong());
  }

  @Override
  protected void writeResponse(DataOutput out, Response r) {
    // do nothing
  }

  @Override
  protected Response readResponse(DataInput in) {
    return new Response();
  }

}
