package fabric.client;

/**
 * An <code>AbortException</code> is thrown to indicate that a transaction was
 * aborted because of an escaped exception.
 */
public class AbortException extends RuntimeException {
  public AbortException(Throwable cause) {
    super(cause);
  }
}
