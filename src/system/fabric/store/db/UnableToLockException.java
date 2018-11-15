package fabric.store.db;

import fabric.common.util.BackoffWrapper.BackoffCase;

/**
 * An exception indicating that an attempt to lock an object has failed.
 */
class UnableToLockException extends Exception {
  public BackoffCase backoffc;

  public UnableToLockException() {
    backoffc = BackoffCase.BO;
  }

  public UnableToLockException(BackoffCase b) {
    backoffc = b;
  }
}
