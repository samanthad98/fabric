package fabric.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fabric.common.SerializedObject;
import fabric.common.VersionWarranty;
import fabric.common.exceptions.FabricException;
import fabric.common.util.LongKeyHashMap;
import fabric.common.util.LongKeyMap;
import fabric.common.util.Pair;
import fabric.net.RemoteNode;

public class TransactionPrepareFailedException extends FabricException {
  /**
   * A set of objects used by the transaction and were out of date.
   */
  public final LongKeyMap<Pair<SerializedObject, VersionWarranty>> versionConflicts;

  public final List<String> messages;

  public TransactionPrepareFailedException(TransactionRestartingException cause) {
    this.messages = null;
    this.versionConflicts = null;
  }

  public TransactionPrepareFailedException(
      LongKeyMap<Pair<SerializedObject, VersionWarranty>> versionConflicts) {
    this.versionConflicts = versionConflicts;
    this.messages = null;
  }

  public TransactionPrepareFailedException(
      Map<RemoteNode, TransactionPrepareFailedException> failures) {
    this.versionConflicts = null;

    messages = new ArrayList<String>();
    for (Map.Entry<RemoteNode, TransactionPrepareFailedException> entry : failures
        .entrySet()) {
      TransactionPrepareFailedException exn = entry.getValue();

      if (exn.messages != null) {
        for (String s : exn.messages)
          messages.add(entry.getKey() + ": " + s);
      }
    }
  }

  public TransactionPrepareFailedException(
      List<TransactionPrepareFailedException> causes) {
    this.versionConflicts =
        new LongKeyHashMap<Pair<SerializedObject, VersionWarranty>>();

    messages = new ArrayList<String>();
    for (TransactionPrepareFailedException exc : causes) {
      if (exc.versionConflicts != null)
        versionConflicts.putAll(exc.versionConflicts);

      if (exc.messages != null) messages.addAll(exc.messages);
    }
  }

  public TransactionPrepareFailedException(
      LongKeyMap<Pair<SerializedObject, VersionWarranty>> versionConflicts,
      String message) {
    this.versionConflicts = versionConflicts;
    messages = java.util.Collections.singletonList(message);
  }

  public TransactionPrepareFailedException(String message) {
    this(null, message);
  }

  @Override
  public String getMessage() {
    String result = "Transaction failed to prepare.";

    if (messages != null) {
      for (String m : messages) {
        result += System.getProperty("line.separator") + "    " + m;
      }
    }

    return result;
  }

}
