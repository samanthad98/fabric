package fabric.common.exceptions;

public class FabricRuntimeException extends RuntimeException {
  public BackoffCase b;
  
  public FabricRuntimeException() {
    super();
    b = BO;
  }

  public FabricRuntimeException(String message) {
    super(message);
    b = BO;
  }
  
  public FabricRuntimeException(String message, BackoffCase b) {
    super(message);
    self.b = b;
  }

  public FabricRuntimeException(Throwable cause) {
    super(cause);
    b = BO;
  }
  
  public FabricRuntimeException(Throwable cause, BackoffCase b) {
    super(cause);
    self.b = b;
  }

  public FabricRuntimeException(String message, Throwable cause) {
    super(message, cause);
    b = BO;
  }
  
  public FabricRuntimeException(String message, Throwable cause, BackoffCase b) {
    super(message, cause);
    self.b = b;
  }
  
  enum BackoffCase {
    Palse, BOnon, BO;
  }
}
