package fabric.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fabric.common.SerializedObject;
import fabric.common.exceptions.FabricException;
import fabric.common.util.LongKeyMap;
import fabric.common.util.LongKeyHashMap;
import fabric.common.util.OidKeyHashMap;
import fabric.net.RemoteNode;

public class TransactionPrepareFailedException extends FabricException {
  /**
   * A set of objects used by the transaction and were out of date.
   */
  public final OidKeyHashMap<SerializedObject> versionConflicts;

  /**
   * A set of objects used by the transaction and were not seen by the store.
   * Unempty if and only if the txn prepare do not have out of date objects and no other failures.
   */
  public final LongKeyMap<Integer> unseenObjects;

  public final List<String> messages;

  public TransactionPrepareFailedException(
      TransactionRestartingException cause) {
    this.messages = new ArrayList<>();
    this.versionConflicts = new OidKeyHashMap<>();
    this.unseenObjects = new LongKeyHashMap<>();
  }

  public TransactionPrepareFailedException(
      OidKeyHashMap<SerializedObject> versionConflicts) {
    this.versionConflicts = versionConflicts;
    this.messages = new ArrayList<>();
    this.unseenObjects = new LongKeyHashMap<>();
  }

  public TransactionPrepareFailedException(
          LongKeyMap<Integer> unseenObjects) {
    this.versionConflicts = new OidKeyHashMap<>();
    this.messages = new ArrayList<>();
    this.unseenObjects = unseenObjects;
  }

  public TransactionPrepareFailedException(
      OidKeyHashMap<SerializedObject> versionConflicts, List<String> messages) {
    this.versionConflicts = versionConflicts;
    this.messages = messages;
    this.unseenObjects = new LongKeyHashMap<>();
  }

  public TransactionPrepareFailedException(
          LongKeyHashMap<Integer> unseenObjects,
          List<String> messages) {
    this.versionConflicts = new OidKeyHashMap<>();
    this.messages = messages;
    this.unseenObjects = unseenObjects;
  }


  public TransactionPrepareFailedException(
      Map<RemoteNode<?>, TransactionPrepareFailedException> failures) {
    this.versionConflicts = new OidKeyHashMap<>();
    this.unseenObjects = new LongKeyHashMap<>();

    messages = new ArrayList<>();
    for (Map.Entry<RemoteNode<?>, TransactionPrepareFailedException> entry : failures
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
    this.versionConflicts = new OidKeyHashMap<>();
    this.unseenObjects = new LongKeyHashMap<>();

    messages = new ArrayList<>();
    for (TransactionPrepareFailedException exc : causes) {
      if (exc.versionConflicts != null)
        versionConflicts.putAll(exc.versionConflicts);

      if (exc.messages != null) messages.addAll(exc.messages);
    }
  }

  public TransactionPrepareFailedException(
      OidKeyHashMap<SerializedObject> versionConflicts, String message) {
    this.versionConflicts = versionConflicts;
    messages = java.util.Collections.singletonList(message);
    this.unseenObjects = new LongKeyHashMap<>();
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
