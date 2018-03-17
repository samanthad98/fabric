package fabric.metrics.contracts.enforcement;

import fabric.lang.*;
import fabric.lang.security.*;
import fabric.worker.*;
import fabric.worker.remote.*;
import java.lang.*;
import fabric.metrics.Metric;
import fabric.metrics.contracts.Bound;
import fabric.metrics.contracts.Contract;
import fabric.worker.transaction.TransactionManager;

/**
 * An {@link EnforcementPolicy} for enforcing a {@link Contract}s by
 * checking every update to the associated {@link Metric}, using equality to a
 * given value.
 */
public interface DirectEqualityPolicy
  extends fabric.metrics.contracts.enforcement.EnforcementPolicy,
          fabric.lang.Object
{
    public fabric.metrics.Metric get$metric();
    
    public fabric.metrics.Metric set$metric(fabric.metrics.Metric val);
    
    public double get$value();
    
    public double set$value(double val);
    
    public double postInc$value();
    
    public double postDec$value();
    
    public long get$expiry();
    
    public long set$expiry(long val);
    
    public long postInc$expiry();
    
    public long postDec$expiry();
    
    public boolean get$activated();
    
    public boolean set$activated(boolean val);
    
    /**
   * @param metric
   *            the Metric the associated contract is bounding.
   * @param rate
   *            the rate of the bound enforced by the associated contract.
   * @param base
   *            the base of the bound enforced by the associated contract.
   */
    public fabric.metrics.contracts.enforcement.DirectEqualityPolicy
      fabric$metrics$contracts$enforcement$DirectEqualityPolicy$(
      fabric.metrics.Metric metric, double value);
    
    public void activate();
    
    public void refresh();
    
    public long expiry();
    
    public void apply(fabric.metrics.contracts.Contract mc);
    
    public void unapply(fabric.metrics.contracts.Contract mc);
    
    public java.lang.String toString();
    
    public boolean equals(fabric.lang.Object other);
    
    public void acquireReconfigLocks();
    
    public static class _Proxy extends fabric.lang.Object._Proxy
      implements fabric.metrics.contracts.enforcement.DirectEqualityPolicy {
        public fabric.metrics.Metric get$metric() {
            return ((fabric.metrics.contracts.enforcement.DirectEqualityPolicy.
                      _Impl) fetch()).get$metric();
        }
        
        public fabric.metrics.Metric set$metric(fabric.metrics.Metric val) {
            return ((fabric.metrics.contracts.enforcement.DirectEqualityPolicy.
                      _Impl) fetch()).set$metric(val);
        }
        
        public double get$value() {
            return ((fabric.metrics.contracts.enforcement.DirectEqualityPolicy.
                      _Impl) fetch()).get$value();
        }
        
        public double set$value(double val) {
            return ((fabric.metrics.contracts.enforcement.DirectEqualityPolicy.
                      _Impl) fetch()).set$value(val);
        }
        
        public double postInc$value() {
            return ((fabric.metrics.contracts.enforcement.DirectEqualityPolicy.
                      _Impl) fetch()).postInc$value();
        }
        
        public double postDec$value() {
            return ((fabric.metrics.contracts.enforcement.DirectEqualityPolicy.
                      _Impl) fetch()).postDec$value();
        }
        
        public long get$expiry() {
            return ((fabric.metrics.contracts.enforcement.DirectEqualityPolicy.
                      _Impl) fetch()).get$expiry();
        }
        
        public long set$expiry(long val) {
            return ((fabric.metrics.contracts.enforcement.DirectEqualityPolicy.
                      _Impl) fetch()).set$expiry(val);
        }
        
        public long postInc$expiry() {
            return ((fabric.metrics.contracts.enforcement.DirectEqualityPolicy.
                      _Impl) fetch()).postInc$expiry();
        }
        
        public long postDec$expiry() {
            return ((fabric.metrics.contracts.enforcement.DirectEqualityPolicy.
                      _Impl) fetch()).postDec$expiry();
        }
        
        public boolean get$activated() {
            return ((fabric.metrics.contracts.enforcement.DirectEqualityPolicy.
                      _Impl) fetch()).get$activated();
        }
        
        public boolean set$activated(boolean val) {
            return ((fabric.metrics.contracts.enforcement.DirectEqualityPolicy.
                      _Impl) fetch()).set$activated(val);
        }
        
        public fabric.metrics.contracts.enforcement.DirectEqualityPolicy
          fabric$metrics$contracts$enforcement$DirectEqualityPolicy$(
          fabric.metrics.Metric arg1, double arg2) {
            return ((fabric.metrics.contracts.enforcement.DirectEqualityPolicy)
                      fetch()).
              fabric$metrics$contracts$enforcement$DirectEqualityPolicy$(arg1,
                                                                         arg2);
        }
        
        public void activate() {
            ((fabric.metrics.contracts.enforcement.DirectEqualityPolicy)
               fetch()).activate();
        }
        
        public void refresh() {
            ((fabric.metrics.contracts.enforcement.DirectEqualityPolicy)
               fetch()).refresh();
        }
        
        public long expiry() {
            return ((fabric.metrics.contracts.enforcement.DirectEqualityPolicy)
                      fetch()).expiry();
        }
        
        public void apply(fabric.metrics.contracts.Contract arg1) {
            ((fabric.metrics.contracts.enforcement.DirectEqualityPolicy)
               fetch()).apply(arg1);
        }
        
        public void unapply(fabric.metrics.contracts.Contract arg1) {
            ((fabric.metrics.contracts.enforcement.DirectEqualityPolicy)
               fetch()).unapply(arg1);
        }
        
        public void acquireReconfigLocks() {
            ((fabric.metrics.contracts.enforcement.DirectEqualityPolicy)
               fetch()).acquireReconfigLocks();
        }
        
        public _Proxy(DirectEqualityPolicy._Impl impl) { super(impl); }
        
        public _Proxy(fabric.worker.Store store, long onum) {
            super(store, onum);
        }
    }
    
    public static class _Impl extends fabric.lang.Object._Impl
      implements fabric.metrics.contracts.enforcement.DirectEqualityPolicy {
        public fabric.metrics.Metric get$metric() { return this.metric; }
        
        public fabric.metrics.Metric set$metric(fabric.metrics.Metric val) {
            fabric.worker.transaction.TransactionManager tm =
              fabric.worker.transaction.TransactionManager.getInstance();
            boolean transactionCreated = tm.registerWrite(this);
            this.metric = val;
            if (transactionCreated) tm.commitTransaction();
            return val;
        }
        
        public fabric.metrics.Metric metric;
        
        public double get$value() { return this.value; }
        
        public double set$value(double val) {
            fabric.worker.transaction.TransactionManager tm =
              fabric.worker.transaction.TransactionManager.getInstance();
            boolean transactionCreated = tm.registerWrite(this);
            this.value = val;
            if (transactionCreated) tm.commitTransaction();
            return val;
        }
        
        public double postInc$value() {
            double tmp = this.get$value();
            this.set$value((double) (tmp + 1));
            return tmp;
        }
        
        public double postDec$value() {
            double tmp = this.get$value();
            this.set$value((double) (tmp - 1));
            return tmp;
        }
        
        public double value;
        
        public long get$expiry() {
            fabric.worker.transaction.TransactionManager.getInstance().
              registerRead(this);
            return this.expiry;
        }
        
        public long set$expiry(long val) {
            fabric.worker.transaction.TransactionManager tm =
              fabric.worker.transaction.TransactionManager.getInstance();
            boolean transactionCreated = tm.registerWrite(this);
            this.expiry = val;
            if (transactionCreated) tm.commitTransaction();
            return val;
        }
        
        public long postInc$expiry() {
            long tmp = this.get$expiry();
            this.set$expiry((long) (tmp + 1));
            return tmp;
        }
        
        public long postDec$expiry() {
            long tmp = this.get$expiry();
            this.set$expiry((long) (tmp - 1));
            return tmp;
        }
        
        /** The currently calculated expiration time to use for the contract. */
        private long expiry;
        
        public boolean get$activated() {
            fabric.worker.transaction.TransactionManager.getInstance().
              registerRead(this);
            return this.activated;
        }
        
        public boolean set$activated(boolean val) {
            fabric.worker.transaction.TransactionManager tm =
              fabric.worker.transaction.TransactionManager.getInstance();
            boolean transactionCreated = tm.registerWrite(this);
            this.activated = val;
            if (transactionCreated) tm.commitTransaction();
            return val;
        }
        
        /** Is this policy active and being enforced? */
        private boolean activated;
        
        /**
   * @param metric
   *            the Metric the associated contract is bounding.
   * @param rate
   *            the rate of the bound enforced by the associated contract.
   * @param base
   *            the base of the bound enforced by the associated contract.
   */
        public fabric.metrics.contracts.enforcement.DirectEqualityPolicy
          fabric$metrics$contracts$enforcement$DirectEqualityPolicy$(
          fabric.metrics.Metric metric, double value) {
            this.set$metric(metric);
            this.set$value((double) value);
            fabric$lang$Object$();
            this.set$expiry((long) -1);
            this.set$activated(false);
            return (fabric.metrics.contracts.enforcement.DirectEqualityPolicy)
                     this.$getProxy();
        }
        
        public void activate() {
            fabric.metrics.contracts.enforcement.DirectEqualityPolicy._Impl.
              static_activate(
                (fabric.metrics.contracts.enforcement.DirectEqualityPolicy)
                  this.$getProxy());
        }
        
        private static void static_activate(
          fabric.metrics.contracts.enforcement.DirectEqualityPolicy tmp) {
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                if (tmp.get$activated()) return;
                tmp.refresh();
            }
            else {
                {
                    fabric.worker.transaction.TransactionManager $tm540 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled543 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff541 = 1;
                    boolean $doBackoff542 = true;
                    boolean $retry537 = true;
                    $label535: for (boolean $commit536 = false; !$commit536; ) {
                        if ($backoffEnabled543) {
                            if ($doBackoff542) {
                                if ($backoff541 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.sleep($backoff541);
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e538) {
                                            
                                        }
                                    }
                                }
                                if ($backoff541 < 5000) $backoff541 *= 2;
                            }
                            $doBackoff542 = $backoff541 <= 32 || !$doBackoff542;
                        }
                        $commit536 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            if (tmp.get$activated()) return;
                            tmp.refresh();
                        }
                        catch (final fabric.worker.RetryException $e538) {
                            $commit536 = false;
                            continue $label535;
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e538) {
                            $commit536 = false;
                            fabric.common.TransactionID $currentTid539 =
                              $tm540.getCurrentTid();
                            if ($e538.tid.isDescendantOf($currentTid539))
                                continue $label535;
                            if ($currentTid539.parent != null) {
                                $retry537 = false;
                                throw $e538;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e538) {
                            $commit536 = false;
                            if ($tm540.checkForStaleObjects()) continue;
                            fabric.common.TransactionID $currentTid539 =
                              $tm540.getCurrentTid();
                            if ($e538.tid.isDescendantOf($currentTid539)) {
                                $retry537 = true;
                            }
                            else if ($currentTid539.parent != null) {
                                $retry537 = false;
                                throw $e538;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e538) {
                            $commit536 = false;
                            if ($tm540.checkForStaleObjects())
                                continue $label535;
                            $retry537 = false;
                            throw new fabric.worker.AbortException($e538);
                        }
                        finally {
                            if ($commit536) {
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e538) {
                                    $commit536 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e538) {
                                    $commit536 = false;
                                    fabric.common.TransactionID $currentTid539 =
                                      $tm540.getCurrentTid();
                                    if ($currentTid539 != null) {
                                        if ($e538.tid.equals($currentTid539) ||
                                              !$e538.tid.isDescendantOf(
                                                           $currentTid539)) {
                                            throw $e538;
                                        }
                                    }
                                }
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit536 && $retry537) {
                                {  }
                                continue $label535;
                            }
                        }
                    }
                }
            }
        }
        
        public void refresh() {
            fabric.metrics.contracts.enforcement.DirectEqualityPolicy._Impl.
              static_refresh(
                (fabric.metrics.contracts.enforcement.DirectEqualityPolicy)
                  this.$getProxy());
        }
        
        private static void static_refresh(
          fabric.metrics.contracts.enforcement.DirectEqualityPolicy tmp) {
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                if (tmp.get$value() != tmp.get$metric().value()) {
                    tmp.set$expiry((long) 0);
                } else {
                    tmp.set$expiry((long) java.lang.Long.MAX_VALUE);
                }
                tmp.set$activated(true);
            }
            else {
                {
                    fabric.worker.transaction.TransactionManager $tm549 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled552 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff550 = 1;
                    boolean $doBackoff551 = true;
                    boolean $retry546 = true;
                    $label544: for (boolean $commit545 = false; !$commit545; ) {
                        if ($backoffEnabled552) {
                            if ($doBackoff551) {
                                if ($backoff550 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.sleep($backoff550);
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e547) {
                                            
                                        }
                                    }
                                }
                                if ($backoff550 < 5000) $backoff550 *= 2;
                            }
                            $doBackoff551 = $backoff550 <= 32 || !$doBackoff551;
                        }
                        $commit545 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            if (tmp.get$value() != tmp.get$metric().value()) {
                                tmp.set$expiry((long) 0);
                            } else {
                                tmp.set$expiry((long) java.lang.Long.MAX_VALUE);
                            }
                            tmp.set$activated(true);
                        }
                        catch (final fabric.worker.RetryException $e547) {
                            $commit545 = false;
                            continue $label544;
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e547) {
                            $commit545 = false;
                            fabric.common.TransactionID $currentTid548 =
                              $tm549.getCurrentTid();
                            if ($e547.tid.isDescendantOf($currentTid548))
                                continue $label544;
                            if ($currentTid548.parent != null) {
                                $retry546 = false;
                                throw $e547;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e547) {
                            $commit545 = false;
                            if ($tm549.checkForStaleObjects()) continue;
                            fabric.common.TransactionID $currentTid548 =
                              $tm549.getCurrentTid();
                            if ($e547.tid.isDescendantOf($currentTid548)) {
                                $retry546 = true;
                            }
                            else if ($currentTid548.parent != null) {
                                $retry546 = false;
                                throw $e547;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e547) {
                            $commit545 = false;
                            if ($tm549.checkForStaleObjects())
                                continue $label544;
                            $retry546 = false;
                            throw new fabric.worker.AbortException($e547);
                        }
                        finally {
                            if ($commit545) {
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e547) {
                                    $commit545 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e547) {
                                    $commit545 = false;
                                    fabric.common.TransactionID $currentTid548 =
                                      $tm549.getCurrentTid();
                                    if ($currentTid548 != null) {
                                        if ($e547.tid.equals($currentTid548) ||
                                              !$e547.tid.isDescendantOf(
                                                           $currentTid548)) {
                                            throw $e547;
                                        }
                                    }
                                }
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit545 && $retry546) {
                                {  }
                                continue $label544;
                            }
                        }
                    }
                }
            }
        }
        
        public long expiry() {
            refresh();
            return this.get$expiry();
        }
        
        public void apply(fabric.metrics.contracts.Contract mc) {
            fabric.metrics.contracts.enforcement.DirectEqualityPolicy._Impl.
              static_apply(
                (fabric.metrics.contracts.enforcement.DirectEqualityPolicy)
                  this.$getProxy(), mc);
        }
        
        private static void static_apply(
          fabric.metrics.contracts.enforcement.DirectEqualityPolicy tmp,
          fabric.metrics.contracts.Contract mc) {
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                if (!tmp.get$activated()) tmp.activate();
                tmp.get$metric().addObserver(mc);
            }
            else {
                {
                    fabric.worker.transaction.TransactionManager $tm558 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled561 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff559 = 1;
                    boolean $doBackoff560 = true;
                    boolean $retry555 = true;
                    $label553: for (boolean $commit554 = false; !$commit554; ) {
                        if ($backoffEnabled561) {
                            if ($doBackoff560) {
                                if ($backoff559 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.sleep($backoff559);
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e556) {
                                            
                                        }
                                    }
                                }
                                if ($backoff559 < 5000) $backoff559 *= 2;
                            }
                            $doBackoff560 = $backoff559 <= 32 || !$doBackoff560;
                        }
                        $commit554 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            if (!tmp.get$activated()) tmp.activate();
                            tmp.get$metric().addObserver(mc);
                        }
                        catch (final fabric.worker.RetryException $e556) {
                            $commit554 = false;
                            continue $label553;
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e556) {
                            $commit554 = false;
                            fabric.common.TransactionID $currentTid557 =
                              $tm558.getCurrentTid();
                            if ($e556.tid.isDescendantOf($currentTid557))
                                continue $label553;
                            if ($currentTid557.parent != null) {
                                $retry555 = false;
                                throw $e556;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e556) {
                            $commit554 = false;
                            if ($tm558.checkForStaleObjects()) continue;
                            fabric.common.TransactionID $currentTid557 =
                              $tm558.getCurrentTid();
                            if ($e556.tid.isDescendantOf($currentTid557)) {
                                $retry555 = true;
                            }
                            else if ($currentTid557.parent != null) {
                                $retry555 = false;
                                throw $e556;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e556) {
                            $commit554 = false;
                            if ($tm558.checkForStaleObjects())
                                continue $label553;
                            $retry555 = false;
                            throw new fabric.worker.AbortException($e556);
                        }
                        finally {
                            if ($commit554) {
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e556) {
                                    $commit554 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e556) {
                                    $commit554 = false;
                                    fabric.common.TransactionID $currentTid557 =
                                      $tm558.getCurrentTid();
                                    if ($currentTid557 != null) {
                                        if ($e556.tid.equals($currentTid557) ||
                                              !$e556.tid.isDescendantOf(
                                                           $currentTid557)) {
                                            throw $e556;
                                        }
                                    }
                                }
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit554 && $retry555) {
                                {  }
                                continue $label553;
                            }
                        }
                    }
                }
            }
        }
        
        public void unapply(fabric.metrics.contracts.Contract mc) {
            this.get$metric().removeObserver(mc);
        }
        
        public java.lang.String toString() {
            return "Directly watching " +
            java.lang.String.
              valueOf(
                fabric.lang.WrappedJavaInlineable.$unwrap(this.get$metric())) +
            " == " +
            this.get$value();
        }
        
        public boolean equals(fabric.lang.Object other) {
            if (!(fabric.lang.Object._Proxy.
                    $getProxy((java.lang.Object)
                                fabric.lang.WrappedJavaInlineable.$unwrap(other)) instanceof fabric.metrics.contracts.enforcement.DirectEqualityPolicy))
                return false;
            fabric.metrics.contracts.enforcement.DirectEqualityPolicy that =
              (fabric.metrics.contracts.enforcement.DirectEqualityPolicy)
                fabric.lang.Object._Proxy.$getProxy(other);
            return this.get$metric().equals(that.get$metric()) &&
              this.get$value() == that.get$value();
        }
        
        public void acquireReconfigLocks() {  }
        
        public _Impl(fabric.worker.Store $location) { super($location); }
        
        protected fabric.lang.Object._Proxy $makeProxy() {
            return new fabric.metrics.contracts.enforcement.
                     DirectEqualityPolicy._Proxy(this);
        }
        
        public void $serialize(java.io.ObjectOutput out,
                               java.util.List refTypes,
                               java.util.List intraStoreRefs,
                               java.util.List interStoreRefs)
              throws java.io.IOException {
            super.$serialize(out, refTypes, intraStoreRefs, interStoreRefs);
            $writeRef($getStore(), this.metric, refTypes, out, intraStoreRefs,
                      interStoreRefs);
            out.writeDouble(this.value);
            out.writeLong(this.expiry);
            out.writeBoolean(this.activated);
        }
        
        public _Impl(fabric.worker.Store store, long onum, int version,
                     long expiry, fabric.worker.Store labelStore,
                     long labelOnum, fabric.worker.Store accessPolicyStore,
                     long accessPolicyOnum, java.io.ObjectInput in,
                     java.util.Iterator refTypes,
                     java.util.Iterator intraStoreRefs,
                     java.util.Iterator interStoreRefs)
              throws java.io.IOException,
            java.lang.ClassNotFoundException {
            super(store, onum, version, expiry, labelStore, labelOnum,
                  accessPolicyStore, accessPolicyOnum, in, refTypes,
                  intraStoreRefs, interStoreRefs);
            this.metric = (fabric.metrics.Metric)
                            $readRef(fabric.metrics.Metric._Proxy.class,
                                     (fabric.common.RefTypeEnum)
                                       refTypes.next(), in, store,
                                     intraStoreRefs, interStoreRefs);
            this.value = in.readDouble();
            this.expiry = in.readLong();
            this.activated = in.readBoolean();
        }
        
        public void $copyAppStateFrom(fabric.lang.Object._Impl other) {
            super.$copyAppStateFrom(other);
            fabric.metrics.contracts.enforcement.DirectEqualityPolicy._Impl
              src =
              (fabric.metrics.contracts.enforcement.DirectEqualityPolicy._Impl)
                other;
            this.metric = src.metric;
            this.value = src.value;
            this.expiry = src.expiry;
            this.activated = src.activated;
        }
    }
    
    interface _Static extends fabric.lang.Object, Cloneable {
        final class _Proxy
        extends fabric.
          lang.
          Object.
          _Proxy
          implements fabric.metrics.contracts.enforcement.DirectEqualityPolicy.
                       _Static
        {
            public _Proxy(fabric.metrics.contracts.enforcement.
                            DirectEqualityPolicy._Static._Impl impl) {
                super(impl);
            }
            
            public _Proxy(fabric.worker.Store store, long onum) {
                super(store, onum);
            }
            
            public static final fabric.metrics.contracts.enforcement.
              DirectEqualityPolicy._Static $instance;
            
            static {
                fabric.
                  metrics.
                  contracts.
                  enforcement.
                  DirectEqualityPolicy.
                  _Static.
                  _Impl
                  impl =
                  (fabric.
                    metrics.
                    contracts.
                    enforcement.
                    DirectEqualityPolicy.
                    _Static.
                    _Impl)
                    fabric.lang.Object._Static._Proxy.
                    $makeStaticInstance(
                      fabric.metrics.contracts.enforcement.DirectEqualityPolicy.
                        _Static._Impl.class);
                $instance =
                  (fabric.metrics.contracts.enforcement.DirectEqualityPolicy.
                    _Static) impl.$getProxy();
                impl.$init();
            }
        }
        
        class _Impl
        extends fabric.
          lang.
          Object.
          _Impl
          implements fabric.metrics.contracts.enforcement.DirectEqualityPolicy.
                       _Static
        {
            public void $serialize(java.io.ObjectOutput out,
                                   java.util.List refTypes,
                                   java.util.List intraStoreRefs,
                                   java.util.List interStoreRefs)
                  throws java.io.IOException {
                super.$serialize(out, refTypes, intraStoreRefs, interStoreRefs);
            }
            
            public _Impl(fabric.worker.Store store, long onum, int version,
                         long expiry, fabric.worker.Store labelStore,
                         long labelOnum, fabric.worker.Store accessPolicyStore,
                         long accessPolicyOnum, java.io.ObjectInput in,
                         java.util.Iterator refTypes,
                         java.util.Iterator intraStoreRefs,
                         java.util.Iterator interStoreRefs)
                  throws java.io.IOException,
                java.lang.ClassNotFoundException {
                super(store, onum, version, expiry, labelStore, labelOnum,
                      accessPolicyStore, accessPolicyOnum, in, refTypes,
                      intraStoreRefs, interStoreRefs);
            }
            
            public _Impl(fabric.worker.Store store) { super(store); }
            
            protected fabric.lang.Object._Proxy $makeProxy() {
                return new fabric.metrics.contracts.enforcement.
                         DirectEqualityPolicy._Static._Proxy(this);
            }
            
            private void $init() {  }
        }
        
    }
    
    public static final byte[] $classHash = new byte[] { -29, 69, 71, -113, -4,
    45, -77, 127, 99, -85, 27, -74, -103, -87, -61, 92, 58, -11, -121, 120, 58,
    56, -127, 60, 52, 109, -1, -63, -92, 59, 60, 2 };
    public static final java.lang.String jlc$CompilerVersion$fabil = "0.3.0";
    public static final long jlc$SourceLastModified$fabil = 1520977993000L;
    public static final java.lang.String jlc$ClassType$fabil =
      "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";
}