package fabric.worker.transaction;

import fabric.common.exceptions.FabricRuntimeException;
import fabric.common.util.BackoffWrapper.BackoffCase;

/**
 * An exception to be used to pass stack and message for a signalled retry.
 */
public class RetrySignalException extends FabricRuntimeException {

  public RetrySignalException(String message) {
    super(message.split(" ")[0] + " Retrying due to " + message);
  }

  public RetrySignalException(String message, BackoffCase b) {
    super(message.split(" ")[0] + " Retrying due to " + message, b);
  }

  public RetrySignalException(String message, Throwable cause) {
    super(message.split(" ")[0] + " Retrying due to " + message, cause);
  }

  public RetrySignalException(String message, Throwable cause, BackoffCase b) {
    super(message.split(" ")[0] + " Retrying due to " + message, cause, b);
  }
}
