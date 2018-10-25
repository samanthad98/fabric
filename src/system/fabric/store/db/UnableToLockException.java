package fabric.store.db;
import fabric.util.BackoffWrapper;

/**
 * An exception indicating that an attempt to lock an object has failed.
 */
class UnableToLockException extends Exception {
  public BackoffCase backoffc;
  
  public UnableToLockException() {
    UnableToLockException(BackoffCase.BO);
  }
  
  public UnableToLockException(BackoffCase b) {
    backoffc = b;
  }
}
