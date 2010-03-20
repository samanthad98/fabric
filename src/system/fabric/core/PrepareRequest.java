package fabric.core;

import java.util.Collection;

import fabric.common.SerializedObject;
import fabric.common.util.LongKeyMap;

/**
 * A convenience class for grouping together the created, modified, and read
 * object sets of a prepare request.
 * 
 * @author mdgeorge
 */
public final class PrepareRequest {
  public final long tid;

  /** The set of created objects */
  public final Collection<SerializedObject> creates;

  /** The collection of modified objects */
  public final Collection<SerializedObject> writes;

  /** The object numbers and version numbers of the read objects */
  public final LongKeyMap<Integer> reads;

  /** The commit time of the transaction, as proposed by the worker */
  public final long commitTime;

  /** Create a PrepareRequest with the provided fields */
  public PrepareRequest(long tid, long commitTime,
                        Collection<SerializedObject> creates,
                        Collection<SerializedObject> writes,
                        LongKeyMap<Integer> reads) {
    this.tid     = tid;
    this.commitTime    = commitTime;
    this.creates = creates;
    this.writes  = writes;
    this.reads   = reads;
  }

}

/*
 * * vim: ts=2 sw=2 et cindent cino=\:0
 */
