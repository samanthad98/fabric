package fabric.worker;

import fabric.common.TransactionID;
import fabric.common.exceptions.FabricRuntimeException;
import fabric.common.util.BackoffWrapper.BackoffCase;

/**
 * Indicates that the current transaction should be restarted.
 */
public final class TransactionRestartingException
    extends FabricRuntimeException {
  /**
   * Identifies the transaction that is to be restarted.
   */
  public final TransactionID tid;

  /**
   * @param tid
   *          identifies the transaction that is to be restarted.
   */
  public TransactionRestartingException(TransactionID tid) {
    super("restarting " + tid);
    this.tid = tid;
  }

  public TransactionRestartingException(TransactionID tid, BackoffCase b) {
    super("restarting " + tid, b);
    this.tid = tid;
  }

  /**
   * @param tid
   *          identifies the transaction that is to be restarted.
   * @param cause
   *          A throwable cause for the restarting exception.
   */
  public TransactionRestartingException(TransactionID tid, Throwable cause) {
    super("restarting " + tid, cause);
    this.tid = tid;
  }

  public TransactionRestartingException(TransactionID tid, Throwable cause,
      BackoffCase b) {
    super("restarting " + tid, cause, b);
    this.tid = tid;
  }
}
