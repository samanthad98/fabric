package fabric.worker.transaction;

import java.util.ArrayList;
import java.util.List;

import fabric.common.exceptions.FabricRuntimeException;
import fabric.common.util.BackoffWrapper.BackoffCase;
import fabric.common.util.CaseCode;

/**
 * An exception to be used to pass stack and message for a signalled retry.
 */
public class RetrySignalException extends FabricRuntimeException {
  public List<CaseCode> casecode;
  public BackoffCase backoffc;

  public RetrySignalException(String message, CaseCode c) {
    super("Retrying due to " + message);
    this.casecode = new ArrayList<>();
    this.casecode.add(c);
    this.backoffc = BackoffCase.BO;
  }

  public RetrySignalException(String message, CaseCode c, BackoffCase b) {
    super("Retrying due to " + message);
    this.casecode = new ArrayList<>();
    this.casecode.add(c);
    this.backoffc = b;
  }

  public RetrySignalException(String message, Throwable cause,
      List<CaseCode> c) {
    super("Retrying due to " + message, cause);
    this.casecode = c;
    this.backoffc = BackoffCase.BO;
  }

  public RetrySignalException(String message, Throwable cause, List<CaseCode> c,
      BackoffCase b) {
    super("Retrying due to " + message, cause);
    this.casecode = c;
    this.backoffc = b;
  }

  public RetrySignalException(String message, List<CaseCode> c) {
    super("Retrying due to " + message);
    this.casecode = c;
    this.backoffc = BackoffCase.BO;
  }

  public RetrySignalException(String message, List<CaseCode> c, BackoffCase b) {
    super("Retrying due to " + message);
    this.casecode = c;
    this.backoffc = b;
  }

}
