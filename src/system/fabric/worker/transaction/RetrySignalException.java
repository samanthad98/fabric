package fabric.worker.transaction;

import java.util.ArrayList;
import java.util.List;

import fabric.common.exceptions.FabricRuntimeException;
import fabric.common.util.CaseCode;

/**
 * An exception to be used to pass stack and message for a signalled retry.
 */
public class RetrySignalException extends FabricRuntimeException {
  public List<CaseCode> casecode;

  public RetrySignalException(String message, CaseCode c) {
    super("Retrying due to " + message);
    this.casecode = new ArrayList<>();
    this.casecode.add(c);
  }

  public RetrySignalException(String message, Throwable cause,
      List<CaseCode> c) {
    super("Retrying due to " + message, cause);
    this.casecode = c;
  }

  public RetrySignalException(String message, List<CaseCode> c) {
    super("Retrying due to " + message);
    this.casecode = c;
  }
}
