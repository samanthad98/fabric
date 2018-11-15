package fabric.common.exceptions;

import fabric.common.util.BackoffWrapper.BackoffCase;

public class FabricRuntimeException extends RuntimeException {
  public BackoffCase backoffc;

  public FabricRuntimeException() {
    super();
    backoffc = BackoffCase.BO;
  }

  public FabricRuntimeException(String message) {
    super(message);
    backoffc = BackoffCase.BO;
  }

  public FabricRuntimeException(String message, BackoffCase b) {
    super(message);
    backoffc = b;
  }

  public FabricRuntimeException(Throwable cause) {
    super(cause);
    backoffc = BackoffCase.BO;
  }

  public FabricRuntimeException(Throwable cause, BackoffCase b) {
    super(cause);
    backoffc = b;
  }

  public FabricRuntimeException(String message, Throwable cause) {
    super(message, cause);
    backoffc = BackoffCase.BO;
  }

  public FabricRuntimeException(String message, Throwable cause,
      BackoffCase b) {
    super(message, cause);
    backoffc = b;
  }
}
