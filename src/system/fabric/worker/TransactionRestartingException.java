package fabric.worker;

import java.util.ArrayList;
import java.util.List;

import fabric.common.TransactionID;
import fabric.common.exceptions.FabricRuntimeException;
import fabric.common.util.BackoffWrapper.BackoffCase;
import fabric.common.util.CaseCode;

/**
 * Indicates that the current transaction should be restarted.
 */
public final class TransactionRestartingException
    extends FabricRuntimeException {
  /**
   * Identifies the transaction that is to be restarted.
   */
  public final TransactionID tid;
  public List<CaseCode> casecode;
  public BackoffCase backoffc;

  /**
   * @param tid
   *          identifies the transaction that is to be restarted.
   */
  public TransactionRestartingException(TransactionID tid, CaseCode c) {
    super("restarting " + tid);
    this.tid = tid;
    this.casecode = new ArrayList<>();
    this.casecode.add(c);
  }

  public TransactionRestartingException(TransactionID tid) {
    super("restarting " + tid);
    this.tid = tid;
    this.casecode = new ArrayList<>();
  }

  public TransactionRestartingException(TransactionID tid, CaseCode c,
      BackoffCase b) {
    super("restarting " + tid);
    this.tid = tid;
    this.casecode = new ArrayList<>();
    this.casecode.add(c);
    this.backoffc = b;
  }

  /**
   * @param tid
   *          identifies the transaction that is to be restarted.
   * @param cause
   *          A throwable cause for the restarting exception.
   */
  public TransactionRestartingException(TransactionID tid, Throwable cause,
      List<CaseCode> c) {
    super("restarting " + tid, cause);
    this.tid = tid;
    this.casecode = c;
  }

  public TransactionRestartingException(TransactionID tid, Throwable cause,
      List<CaseCode> c, BackoffCase b) {
    super("restarting " + tid, cause);
    this.casecode = c;
    this.tid = tid;
    this.backoffc = b;
  }
}
