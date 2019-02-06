package fabric.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fabric.common.SerializedObject;
import fabric.common.exceptions.FabricException;
import fabric.common.util.CaseCode;
import fabric.common.util.OidKeyHashMap;
import fabric.net.RemoteNode;

public class TransactionPrepareFailedException extends FabricException {
  /**
   * A set of objects used by the transaction and were out of date.
   */
  public final OidKeyHashMap<SerializedObject> versionConflicts;

  public final List<String> messages;

  public List<CaseCode> casecode;

  public TransactionPrepareFailedException(
      TransactionRestartingException cause) {
    this.messages = new ArrayList<>();
    String str = cause.getCause().getMessage();
    this.messages.add(str);
    this.versionConflicts = new OidKeyHashMap<>();
    this.casecode = cause.casecode;
  }

  public TransactionPrepareFailedException(
      OidKeyHashMap<SerializedObject> versionConflicts, CaseCode c) {
    this.versionConflicts = versionConflicts;
    this.messages = new ArrayList<>();
    this.casecode = new ArrayList<>();
    this.casecode.add(c);
  }

  public TransactionPrepareFailedException(
      OidKeyHashMap<SerializedObject> versionConflicts) {
    this.versionConflicts = versionConflicts;
    this.messages = new ArrayList<>();
    this.casecode = new ArrayList<>();
  }

  public TransactionPrepareFailedException(
      OidKeyHashMap<SerializedObject> versionConflicts, List<String> messages,
      CaseCode c) {
    this.versionConflicts = versionConflicts;
    this.messages = messages;
    this.casecode = new ArrayList<>();
    this.casecode.add(c);
  }

  public TransactionPrepareFailedException(
      OidKeyHashMap<SerializedObject> versionConflicts, List<String> messages) {
    this.versionConflicts = versionConflicts;
    this.messages = messages;
    this.casecode = new ArrayList<>();
  }

  public TransactionPrepareFailedException(
      Map<RemoteNode<?>, TransactionPrepareFailedException> failures) {
    this.casecode = new ArrayList<>();
    this.versionConflicts = new OidKeyHashMap<>();

    messages = new ArrayList<>();
    for (Map.Entry<RemoteNode<?>, TransactionPrepareFailedException> entry : failures
        .entrySet()) {
      TransactionPrepareFailedException exn = entry.getValue();

      if (exn.messages != null) {
        for (String s : exn.messages)
          messages.add(entry.getKey() + ": " + s);
      }

      this.casecode.addAll(exn.casecode);
    }
  }

  public TransactionPrepareFailedException(
      List<TransactionPrepareFailedException> causes) {
    this.versionConflicts = new OidKeyHashMap<>();
    this.casecode = new ArrayList<>();

    messages = new ArrayList<>();
    for (TransactionPrepareFailedException exc : causes) {
      if (exc.versionConflicts != null)
        versionConflicts.putAll(exc.versionConflicts);

      if (exc.messages != null) messages.addAll(exc.messages);

      this.casecode.addAll(exc.casecode);
    }
  }

  public TransactionPrepareFailedException(
      OidKeyHashMap<SerializedObject> versionConflicts, String message,
      CaseCode c) {
    this.casecode = new ArrayList<>();
    this.casecode.add(c);
    this.versionConflicts = versionConflicts;
    messages = java.util.Collections.singletonList(message);
  }

  public TransactionPrepareFailedException(
      OidKeyHashMap<SerializedObject> versionConflicts, String message) {
    this.casecode = new ArrayList<>();
    this.versionConflicts = versionConflicts;
    messages = java.util.Collections.singletonList(message);
  }

  public TransactionPrepareFailedException(String message, CaseCode c) {
    this(new OidKeyHashMap<SerializedObject>(), message, c);
  }

  public TransactionPrepareFailedException(String message) {
    this(new OidKeyHashMap<SerializedObject>(), message);
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
