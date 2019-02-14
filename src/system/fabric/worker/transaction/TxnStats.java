package fabric.worker.transaction;

import java.util.ArrayList;
import java.util.List;

import fabric.worker.Worker;

/**
 * Class for easily tracking stats about app level transactions (as opposed to
 * individual attempts).
 */
public class TxnStats {
  private int txnAttempts = 0;
  private long tid = 0;
  private boolean coordinated = false;
  private int fetches = 0;
  private int fetchWaits = 0;
  private long backofftime = 0;
  private long backoffcount = 0;
  private long backoffincrease = 0;
  private List<String> msgs = new ArrayList<>();
  private List<String> fetched = new ArrayList<>();
  private List<String> versionConflicts = new ArrayList<>();
  private List casecode = new ArrayList();

  public TxnStats() {
  }

  public void reset() {
    txnAttempts = 0;
    tid = 0;
    coordinated = false;
    fetches = 0;
    fetchWaits = 0;
    backofftime = 0;
    backoffcount = 0;
    backoffincrease = 0;
    msgs.clear();
    fetched.clear();
    versionConflicts.clear();
    casecode = new ArrayList();
  }

  /**
   * @return the txnAttempts count
   */
  public int getTxnAttempts() {
    return txnAttempts;
  }

  /**
   * @return the last tid
   */
  public long getTid() {
    return tid;
  }

  /**
   * @return the coordinated flag
   */
  public boolean isCoordinated() {
    return coordinated;
  }

  /**
   * @return the fetches
   */
  public int getFetches() {
    return fetches;
  }

  /**
   * @return the fetch waits (possibly being performed by another transaction)
   */
  public int getFetchWaits() {
    return fetchWaits;
  }

  /**
   * Mark the final attempt as a coordination.
   */
  public void markCoordination() {
    coordinated = true;
  }

  /**
   * Count a transaction attempt.
   */
  public void markTxnAttempt() {
    txnAttempts++;
  }

  /**
   * Record the final tid.
   */
  public void recordTid(long tid) {
    this.tid = tid;
  }

  /**
   * Record a fetch.
   */
  public void markFetch() {
    fetches++;
  }

  /**
   * Record a fetch wait (possibly waiting for another doing the fetching).
   */
  public void markFetchWait() {
    fetchWaits++;
  }

  /**
   * Record the object being waited for by a txn.
   */
  public void markFetched(fabric.lang.Object._Proxy p) {
    if (p != null && Worker.getWorker().config.recordFetched)
      fetched.add("" + p.getClass() + "#" + p.$getOnum() + "@" + p.$getStore());
  }

  /**
   * Add a custom message.
   */
  public void addMsg(String msg) {
    this.msgs.add(msg);
  }

  /**
   * Reset custom messages.
   */
  public void clearMsgs() {
    this.msgs.clear();
  }

  /**
   * Mark the version conflicts that occurred.
   */
  public void addConflicts(String conflicts) {
    if (Worker.getWorker().config.recordConflicts)
      versionConflicts.add(conflicts);
  }

  /**
   * Add backofftime.
   */
  public void addBackoffTime(long t) {
    backofftime = backofftime + t;
  }

  /**
   * Add backoffcount.
   */
  public void addBackoffCount() {
    backoffcount = backoffcount + 1;
  }

  /**
   * Add backoffincrease.
   */
  public void addBackoffIncrease() {
    backoffincrease = backoffincrease + 1;
  }

  /**
   * Add casecode.
   */
  public void addRetrycase(List c) {
    this.casecode.addAll(c);
  }

  public String casestring() {
    String casestring = "Retryed because ";
    for (Object o : casecode) {
      casestring += o.toString() + ",";
    }
    return casestring;
  }

  @Override
  public String toString() {
    return "[COORDINATED: " + coordinated + " WITH " + txnAttempts
        + " TXN ATTEMPTS" + " USING " + fetches + " FETCHES " + fetchWaits
        + " WAITS FOR FETCHES" + " MSGS: " + msgs + " FETCHED: " + fetched
        + " CONFLICTS: " + versionConflicts + " BACKOFFTIME: " + backofftime
        + " BACKOFFCOUNT: " + backoffcount + " BACKOFFINCREASE: "
        + backoffincrease + " IN " + Long.toHexString(tid) + "]";
  }
}
