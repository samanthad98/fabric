package fabric.common.util;

/**
 * Represent different reasons of retry.
 */
public enum CaseCode {
  /**
   * To break local deadlock.
   */
  Deadlock,
  /**
   * Retry triggered by parent.
   */
  TBParent,
  /**
   * Some writer wants to write the thing being read, then readers are aborted
   */
  RWConflict,
  /**
   * Version conflicts on the worker's side.
   */
  LocalVC,
  /**
   * No transaction to prepare.
   */
  RNoTrans,
  /**
   * Does not have permission or privilege.
   */
  RNoPerm,
  /**
   * Version conflicts on the store's side.
   */
  RemoteVC,
  /**
   * Unable to grab the lock.
   */
  RNoLock,
  /**
   * Transaction is already in ABORTING state when adding a create/read/write.
   */
  RInAborting,
  /**
   * Object to create already exists.
   */
  RObExist,
  /**
   * Aborted by another thread.
   */
  RAborted,
  /**
   * Coordinator Side issue
   */
  Coord
}
