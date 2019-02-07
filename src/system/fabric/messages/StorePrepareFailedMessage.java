package fabric.messages;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fabric.common.SerializedObject;
import fabric.common.exceptions.ProtocolError;
import fabric.common.net.RemoteIdentity;
import fabric.common.util.BackoffWrapper.BackoffCase;
import fabric.common.util.CaseCode;
import fabric.common.util.LongKeyMap;
import fabric.common.util.OidKeyHashMap;
import fabric.worker.Store;
import fabric.worker.TransactionPrepareFailedException;
import fabric.worker.Worker;
import fabric.worker.remote.RemoteWorker;

/**
 * A <code>PrepareSuccessMessage</code> represents a failed prepare for the
 * given tid.
 */
public class StorePrepareFailedMessage extends AsyncMessage {
  // ////////////////////////////////////////////////////////////////////////////
  // message contents //
  // ////////////////////////////////////////////////////////////////////////////

  public final long tid;

  public final OidKeyHashMap<SerializedObject> conflicts;

  public final List<String> messages;

  public BackoffCase backoffc;
  public List<CaseCode> casecode;

  /**
   * Used to prepare transactions at remote workers.
   */
  public StorePrepareFailedMessage(long tid,
      TransactionPrepareFailedException e) {
    super(MessageType.STORE_PREPARE_FAILED);
    this.tid = tid;
    this.conflicts = e.versionConflicts;
    this.messages = e.messages;
    this.backoffc = e.backoffc;
    this.casecode = e.casecode;
  }

  // ////////////////////////////////////////////////////////////////////////////
  // visitor methods //
  // ////////////////////////////////////////////////////////////////////////////

  @Override
  public void dispatch(RemoteIdentity<RemoteWorker> client, MessageHandler h)
      throws ProtocolError {
    h.handle(client, this);
  }

  // ////////////////////////////////////////////////////////////////////////////
  // serialization cruft //
  // ////////////////////////////////////////////////////////////////////////////

  @Override
  protected void writeMessage(DataOutput out) throws IOException {
    // Serialize tid.
    out.writeLong(tid);

    out.writeInt(conflicts.storeSet().size());
    for (Store s : conflicts.storeSet()) {
      out.writeUTF(s.name());
      out.writeInt(conflicts.get(s).size());
      for (LongKeyMap.Entry<SerializedObject> entry : conflicts.get(s)
          .entrySet()) {
        out.writeLong(entry.getKey());
        entry.getValue().write(out);
      }
    }

    out.writeInt(messages.size());
    for (String msg : messages) {
      out.writeUTF(msg);
    }

    out.writeUTF(backoffc.name());

    out.writeInt(casecode.size());
    for (CaseCode c : casecode) {
      out.writeUTF(c.name());
    }
  }

  /* readMessage */
  protected StorePrepareFailedMessage(DataInput in) throws IOException {
    super(MessageType.STORE_PREPARE_FAILED);
    this.tid = in.readLong();

    int size = in.readInt();
    this.conflicts = new OidKeyHashMap<>();
    for (int i = 0; i < size; i++) {
      Store s = Worker.getWorker().getStore(in.readUTF());
      int size2 = in.readInt();
      for (int j = 0; j < size2; j++) {
        long onum = in.readLong();
        conflicts.put(s, onum, new SerializedObject(in));
      }
    }

    size = in.readInt();
    this.messages = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      messages.add(in.readUTF());
    }

    backoffc = BackoffCase.valueOf(in.readUTF());

    size = in.readInt();
    this.casecode = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      casecode.add(CaseCode.valueOf(in.readUTF()));
    }
  }

  @Override
  public String toString() {
    return "Failed to prepare at store with messages: " + messages.toString()
        + " and conflicts: " + conflicts.toString();
  }
}
