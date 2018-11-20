package fabric.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fabric.common.SerializedObject;
import fabric.common.exceptions.FabricException;
import fabric.common.util.BackoffWrapper.BackoffCase;
import fabric.common.util.OidKeyHashMap;
import fabric.net.RemoteNode;

public class TransactionPrepareFailedException extends FabricException {
  /**
   * A set of objects used by the transaction and were out of date.
   */
  public final OidKeyHashMap<SerializedObject> versionConflicts;

  public final List<String> messages;

  public BackoffCase backoffc;

  public TransactionPrepareFailedException(
      TransactionRestartingException cause) {
    this.messages = new ArrayList<>();
    this.versionConflicts = new OidKeyHashMap<>();
    backoffc = cause.backoffc;
  }

  public TransactionPrepareFailedException(
      OidKeyHashMap<SerializedObject> versionConflicts) {
    this.versionConflicts = versionConflicts;
    this.messages = new ArrayList<>();
    backoffc = BackoffCase.BO;
  }

  public TransactionPrepareFailedException(
      OidKeyHashMap<SerializedObject> versionConflicts, BackoffCase b) {
    this.versionConflicts = versionConflicts;
    this.messages = new ArrayList<>();
    backoffc = b;
  }

  public TransactionPrepareFailedException(
      OidKeyHashMap<SerializedObject> versionConflicts, List<String> messages) {
    this.versionConflicts = versionConflicts;
    this.messages = messages;
    backoffc = BackoffCase.BO;
  }

  public TransactionPrepareFailedException(
      OidKeyHashMap<SerializedObject> versionConflicts, List<String> messages,
      BackoffCase b) {
    this.versionConflicts = versionConflicts;
    this.messages = messages;
    backoffc = b;
  }

  public TransactionPrepareFailedException(
      Map<RemoteNode<?>, TransactionPrepareFailedException> failures) {
    this.versionConflicts = new OidKeyHashMap<>();
    this.backoffc = BackoffCase.Pause;

    messages = new ArrayList<>();
    for (Map.Entry<RemoteNode<?>, TransactionPrepareFailedException> entry : failures
        .entrySet()) {
      TransactionPrepareFailedException exn = entry.getValue();

      if (exn.messages != null) {
        for (String s : exn.messages)
          messages.add(entry.getKey() + ": " + s);
      }

      if (this.backoffc.weakerThan(exn.backoffc)) {
        this.backoffc = exn.backoffc;
      }
    }
  }

  public TransactionPrepareFailedException(
      List<TransactionPrepareFailedException> causes) {
    this.versionConflicts = new OidKeyHashMap<>();
    this.backoffc = BackoffCase.Pause;

    messages = new ArrayList<>();
    for (TransactionPrepareFailedException exc : causes) {
      if (exc.versionConflicts != null)
        versionConflicts.putAll(exc.versionConflicts);

      if (exc.messages != null) messages.addAll(exc.messages);

      if (this.backoffc.weakerThan(exc.backoffc)) {
        this.backoffc = exc.backoffc;
      }
    }
  }

  public TransactionPrepareFailedException(
      OidKeyHashMap<SerializedObject> versionConflicts, String message) {
    this.versionConflicts = versionConflicts;
    messages = java.util.Collections.singletonList(message);
    backoffc = BackoffCase.BO;
  }

  public TransactionPrepareFailedException(
      OidKeyHashMap<SerializedObject> versionConflicts, String message,
      BackoffCase b) {
    this.versionConflicts = versionConflicts;
    messages = java.util.Collections.singletonList(message);
    backoffc = b;
  }

  public TransactionPrepareFailedException(String message) {
    this(new OidKeyHashMap<SerializedObject>(), message);
  }

  public TransactionPrepareFailedException(String message, BackoffCase b) {
    this(new OidKeyHashMap<SerializedObject>(), message, b);
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
