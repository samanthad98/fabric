package fabric.metrics.contracts;

import fabric.lang.*;
import fabric.lang.security.*;
import fabric.worker.*;
import fabric.worker.remote.*;
import java.lang.*;
import fabric.util.Collections;
import fabric.util.Set;
import fabric.metrics.DerivedMetric;
import fabric.metrics.Metric;
import fabric.metrics.SampledMetric;
import fabric.metrics.contracts.enforcement.EnforcementPolicy;
import fabric.metrics.contracts.enforcement.WitnessPolicy;
import fabric.metrics.util.Observer;
import fabric.metrics.util.Subject;
import fabric.common.TransactionID;
import fabric.worker.Store;
import fabric.worker.transaction.TransactionManager;
import java.util.logging.Level;
import fabric.common.Logging;

/**
 * A contract asserting that a {@link Metric}'s value is above or below a
 * vectorized boundary expression <code>r\u20d7 * (t - startTime) + b\u20d7</code> until
 * the associated expriation time.
 * <p>
 * This class follows the subject-observer pattern. An instance is an observer
 * of either a {@link Metric} or a set of {@link MetricEqualityContract}s and can be
 * observed by other {@link Contract}s.
 */
public interface MetricEqualityContract
  extends fabric.metrics.contracts.Contract {
    public fabric.metrics.Metric get$metric();
    
    public fabric.metrics.Metric set$metric(fabric.metrics.Metric val);
    
    public fabric.lang.arrays.ObjectArray get$leafMetrics();
    
    public fabric.lang.arrays.ObjectArray set$leafMetrics(
      fabric.lang.arrays.ObjectArray val);
    
    public double get$value();
    
    public double set$value(double val);
    
    public double postInc$value();
    
    public double postDec$value();
    
    /**
   * @param metric
   *        the {@link Metric} this contract asserts a bound on
   * @param value
   *        the value of the equality
   * @param base
   *        the base of the bound this {@link MetricEqualityContract} asserts on
   *        metric.
   */
    public fabric.metrics.contracts.MetricEqualityContract
      fabric$metrics$contracts$MetricEqualityContract$(
      fabric.metrics.Metric metric, double value);
    
    /** @return the {@link Metric} that this contract observes. */
    public fabric.metrics.Metric getMetric();
    
    public void activate();
    
    /**
   * Update this contract's expiration time to stay valid in response to a
   * change in the value of the {@link Subject}s used for enforcing this
   * {@link Contract}. Revokes, extends, and updates the enforcement strategy
   * as needed.
   */
    public boolean refresh(boolean asyncExtension);
    
    public boolean implies(fabric.metrics.Metric otherMetric, double otherRate,
                           double otherBase);
    
    public java.lang.String toString();
    
    public fabric.lang.arrays.ObjectArray getLeafSubjects();
    
    public static class _Proxy extends fabric.metrics.contracts.Contract._Proxy
      implements fabric.metrics.contracts.MetricEqualityContract {
        public fabric.metrics.Metric get$metric() {
            return ((fabric.metrics.contracts.MetricEqualityContract._Impl)
                      fetch()).get$metric();
        }
        
        public fabric.metrics.Metric set$metric(fabric.metrics.Metric val) {
            return ((fabric.metrics.contracts.MetricEqualityContract._Impl)
                      fetch()).set$metric(val);
        }
        
        public fabric.lang.arrays.ObjectArray get$leafMetrics() {
            return ((fabric.metrics.contracts.MetricEqualityContract._Impl)
                      fetch()).get$leafMetrics();
        }
        
        public fabric.lang.arrays.ObjectArray set$leafMetrics(
          fabric.lang.arrays.ObjectArray val) {
            return ((fabric.metrics.contracts.MetricEqualityContract._Impl)
                      fetch()).set$leafMetrics(val);
        }
        
        public double get$value() {
            return ((fabric.metrics.contracts.MetricEqualityContract._Impl)
                      fetch()).get$value();
        }
        
        public double set$value(double val) {
            return ((fabric.metrics.contracts.MetricEqualityContract._Impl)
                      fetch()).set$value(val);
        }
        
        public double postInc$value() {
            return ((fabric.metrics.contracts.MetricEqualityContract._Impl)
                      fetch()).postInc$value();
        }
        
        public double postDec$value() {
            return ((fabric.metrics.contracts.MetricEqualityContract._Impl)
                      fetch()).postDec$value();
        }
        
        public fabric.metrics.contracts.MetricEqualityContract
          fabric$metrics$contracts$MetricEqualityContract$(
          fabric.metrics.Metric arg1, double arg2) {
            return ((fabric.metrics.contracts.MetricEqualityContract) fetch()).
              fabric$metrics$contracts$MetricEqualityContract$(arg1, arg2);
        }
        
        public fabric.metrics.Metric getMetric() {
            return ((fabric.metrics.contracts.MetricEqualityContract) fetch()).
              getMetric();
        }
        
        public fabric.lang.arrays.ObjectArray getLeafSubjects() {
            return ((fabric.metrics.contracts.MetricEqualityContract) fetch()).
              getLeafSubjects();
        }
        
        public _Proxy(MetricEqualityContract._Impl impl) { super(impl); }
        
        public _Proxy(fabric.worker.Store store, long onum) {
            super(store, onum);
        }
    }
    
    public static class _Impl extends fabric.metrics.contracts.Contract._Impl
      implements fabric.metrics.contracts.MetricEqualityContract {
        public fabric.metrics.Metric get$metric() { return this.metric; }
        
        public fabric.metrics.Metric set$metric(fabric.metrics.Metric val) {
            fabric.worker.transaction.TransactionManager tm =
              fabric.worker.transaction.TransactionManager.getInstance();
            boolean transactionCreated = tm.registerWrite(this);
            this.metric = val;
            if (transactionCreated) tm.commitTransaction();
            return val;
        }
        
        protected fabric.metrics.Metric metric;
        
        public fabric.lang.arrays.ObjectArray get$leafMetrics() {
            return this.leafMetrics;
        }
        
        public fabric.lang.arrays.ObjectArray set$leafMetrics(
          fabric.lang.arrays.ObjectArray val) {
            fabric.worker.transaction.TransactionManager tm =
              fabric.worker.transaction.TransactionManager.getInstance();
            boolean transactionCreated = tm.registerWrite(this);
            this.leafMetrics = val;
            if (transactionCreated) tm.commitTransaction();
            return val;
        }
        
        private fabric.lang.arrays.ObjectArray leafMetrics;
        
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
        
        /**
   * @param metric
   *        the {@link Metric} this contract asserts a bound on
   * @param value
   *        the value of the equality
   * @param base
   *        the base of the bound this {@link MetricEqualityContract} asserts on
   *        metric.
   */
        public fabric.metrics.contracts.MetricEqualityContract
          fabric$metrics$contracts$MetricEqualityContract$(
          fabric.metrics.Metric metric, double value) {
            this.set$metric(metric);
            this.set$value((double) value);
            fabric.lang.security.Label lbl =
              fabric.lang.security.LabelUtil._Impl.noComponents();
            if (fabric.lang.Object._Proxy.
                  $getProxy(
                    (java.lang.Object)
                      fabric.lang.WrappedJavaInlineable.
                      $unwrap(
                        metric)) instanceof fabric.metrics.SampledMetric) {
                this.
                  set$leafMetrics(
                    (fabric.lang.arrays.ObjectArray)
                      new fabric.lang.arrays.ObjectArray._Impl(
                        this.$getStore()).
                      fabric$lang$arrays$ObjectArray$(
                        lbl, lbl.confPolicy(),
                        fabric.metrics.SampledMetric._Proxy.class, 1).$getProxy(
                                                                        ));
                this.get$leafMetrics().set(
                                         0,
                                         (fabric.metrics.SampledMetric)
                                           fabric.lang.Object._Proxy.$getProxy(
                                                                       metric));
            }
            else if (fabric.lang.Object._Proxy.
                       $getProxy(
                         (java.lang.Object)
                           fabric.lang.WrappedJavaInlineable.
                           $unwrap(
                             metric)) instanceof fabric.metrics.DerivedMetric) {
                this.set$leafMetrics(
                       ((fabric.metrics.DerivedMetric)
                          fabric.lang.Object._Proxy.$getProxy(
                                                      metric)).getLeafSubjects(
                                                                 ));
            }
            else {
                throw new java.lang.IllegalStateException(
                        "All metrics should be either sampled or derived!");
            }
            fabric$metrics$contracts$Contract$();
            return (fabric.metrics.contracts.MetricEqualityContract)
                     this.$getProxy();
        }
        
        /** @return the {@link Metric} that this contract observes. */
        public fabric.metrics.Metric getMetric() { return this.get$metric(); }
        
        public void activate() {
            fabric.metrics.contracts.MetricEqualityContract._Impl.
              static_activate((fabric.metrics.contracts.MetricEqualityContract)
                                this.$getProxy());
        }
        
        private static void static_activate(
          fabric.metrics.contracts.MetricEqualityContract tmp) {
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                if (tmp.isActivated()) return;
            }
            else {
                {
                    fabric.worker.transaction.TransactionManager $tm349 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled352 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff350 = 1;
                    boolean $doBackoff351 = true;
                    boolean $retry346 = true;
                    $label344: for (boolean $commit345 = false; !$commit345; ) {
                        if ($backoffEnabled352) {
                            if ($doBackoff351) {
                                if ($backoff350 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.sleep($backoff350);
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e347) {
                                            
                                        }
                                    }
                                }
                                if ($backoff350 < 5000) $backoff350 *= 2;
                            }
                            $doBackoff351 = $backoff350 <= 32 || !$doBackoff351;
                        }
                        $commit345 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try { if (tmp.isActivated()) return; }
                        catch (final fabric.worker.RetryException $e347) {
                            $commit345 = false;
                            continue $label344;
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e347) {
                            $commit345 = false;
                            fabric.common.TransactionID $currentTid348 =
                              $tm349.getCurrentTid();
                            if ($e347.tid.isDescendantOf($currentTid348))
                                continue $label344;
                            if ($currentTid348.parent != null) {
                                $retry346 = false;
                                throw $e347;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e347) {
                            $commit345 = false;
                            if ($tm349.checkForStaleObjects()) continue;
                            fabric.common.TransactionID $currentTid348 =
                              $tm349.getCurrentTid();
                            if ($e347.tid.isDescendantOf($currentTid348)) {
                                $retry346 = true;
                            }
                            else if ($currentTid348.parent != null) {
                                $retry346 = false;
                                throw $e347;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e347) {
                            $commit345 = false;
                            if ($tm349.checkForStaleObjects())
                                continue $label344;
                            $retry346 = false;
                            throw new fabric.worker.AbortException($e347);
                        }
                        finally {
                            if ($commit345) {
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e347) {
                                    $commit345 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e347) {
                                    $commit345 = false;
                                    fabric.common.TransactionID $currentTid348 =
                                      $tm349.getCurrentTid();
                                    if ($currentTid348 != null) {
                                        if ($e347.tid.equals($currentTid348) ||
                                              !$e347.tid.isDescendantOf(
                                                           $currentTid348)) {
                                            throw $e347;
                                        }
                                    }
                                }
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit345 && $retry346) {
                                {  }
                                continue $label344;
                            }
                        }
                    }
                }
            }
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                tmp.set$currentPolicy(
                      tmp.get$metric().equalityPolicy(tmp.get$value(), false,
                                                      tmp.$getStore()));
            }
            else {
                {
                    fabric.worker.transaction.TransactionManager $tm358 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled361 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff359 = 1;
                    boolean $doBackoff360 = true;
                    boolean $retry355 = true;
                    $label353: for (boolean $commit354 = false; !$commit354; ) {
                        if ($backoffEnabled361) {
                            if ($doBackoff360) {
                                if ($backoff359 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.sleep($backoff359);
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e356) {
                                            
                                        }
                                    }
                                }
                                if ($backoff359 < 5000) $backoff359 *= 2;
                            }
                            $doBackoff360 = $backoff359 <= 32 || !$doBackoff360;
                        }
                        $commit354 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            tmp.set$currentPolicy(
                                  tmp.get$metric().equalityPolicy(
                                                     tmp.get$value(), false,
                                                     tmp.$getStore()));
                        }
                        catch (final fabric.worker.RetryException $e356) {
                            $commit354 = false;
                            continue $label353;
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e356) {
                            $commit354 = false;
                            fabric.common.TransactionID $currentTid357 =
                              $tm358.getCurrentTid();
                            if ($e356.tid.isDescendantOf($currentTid357))
                                continue $label353;
                            if ($currentTid357.parent != null) {
                                $retry355 = false;
                                throw $e356;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e356) {
                            $commit354 = false;
                            if ($tm358.checkForStaleObjects()) continue;
                            fabric.common.TransactionID $currentTid357 =
                              $tm358.getCurrentTid();
                            if ($e356.tid.isDescendantOf($currentTid357)) {
                                $retry355 = true;
                            }
                            else if ($currentTid357.parent != null) {
                                $retry355 = false;
                                throw $e356;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e356) {
                            $commit354 = false;
                            if ($tm358.checkForStaleObjects())
                                continue $label353;
                            $retry355 = false;
                            throw new fabric.worker.AbortException($e356);
                        }
                        finally {
                            if ($commit354) {
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e356) {
                                    $commit354 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e356) {
                                    $commit354 = false;
                                    fabric.common.TransactionID $currentTid357 =
                                      $tm358.getCurrentTid();
                                    if ($currentTid357 != null) {
                                        if ($e356.tid.equals($currentTid357) ||
                                              !$e356.tid.isDescendantOf(
                                                           $currentTid357)) {
                                            throw $e356;
                                        }
                                    }
                                }
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit354 && $retry355) {
                                {  }
                                continue $label353;
                            }
                        }
                    }
                }
            }
            tmp.get$currentPolicy().activate();
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                fabric.metrics.contracts.Contract._Impl.static_activate(tmp);
                tmp.getMetric().addContract(tmp);
            }
            else {
                {
                    fabric.worker.transaction.TransactionManager $tm367 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled370 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff368 = 1;
                    boolean $doBackoff369 = true;
                    boolean $retry364 = true;
                    $label362: for (boolean $commit363 = false; !$commit363; ) {
                        if ($backoffEnabled370) {
                            if ($doBackoff369) {
                                if ($backoff368 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.sleep($backoff368);
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e365) {
                                            
                                        }
                                    }
                                }
                                if ($backoff368 < 5000) $backoff368 *= 2;
                            }
                            $doBackoff369 = $backoff368 <= 32 || !$doBackoff369;
                        }
                        $commit363 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            fabric.metrics.contracts.Contract._Impl.
                              static_activate(tmp);
                            tmp.getMetric().addContract(tmp);
                        }
                        catch (final fabric.worker.RetryException $e365) {
                            $commit363 = false;
                            continue $label362;
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e365) {
                            $commit363 = false;
                            fabric.common.TransactionID $currentTid366 =
                              $tm367.getCurrentTid();
                            if ($e365.tid.isDescendantOf($currentTid366))
                                continue $label362;
                            if ($currentTid366.parent != null) {
                                $retry364 = false;
                                throw $e365;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e365) {
                            $commit363 = false;
                            if ($tm367.checkForStaleObjects()) continue;
                            fabric.common.TransactionID $currentTid366 =
                              $tm367.getCurrentTid();
                            if ($e365.tid.isDescendantOf($currentTid366)) {
                                $retry364 = true;
                            }
                            else if ($currentTid366.parent != null) {
                                $retry364 = false;
                                throw $e365;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e365) {
                            $commit363 = false;
                            if ($tm367.checkForStaleObjects())
                                continue $label362;
                            $retry364 = false;
                            throw new fabric.worker.AbortException($e365);
                        }
                        finally {
                            if ($commit363) {
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e365) {
                                    $commit363 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e365) {
                                    $commit363 = false;
                                    fabric.common.TransactionID $currentTid366 =
                                      $tm367.getCurrentTid();
                                    if ($currentTid366 != null) {
                                        if ($e365.tid.equals($currentTid366) ||
                                              !$e365.tid.isDescendantOf(
                                                           $currentTid366)) {
                                            throw $e365;
                                        }
                                    }
                                }
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit363 && $retry364) {
                                {  }
                                continue $label362;
                            }
                        }
                    }
                }
            }
        }
        
        /**
   * Update this contract's expiration time to stay valid in response to a
   * change in the value of the {@link Subject}s used for enforcing this
   * {@link Contract}. Revokes, extends, and updates the enforcement strategy
   * as needed.
   */
        public boolean refresh(boolean asyncExtension) {
            if (!isActivated()) { return false; }
            long currentTime = java.lang.System.currentTimeMillis();
            if (!fabric.lang.Object._Proxy.idEquals(this.get$currentPolicy(),
                                                    null)) {
                long curExpiry = this.get$currentPolicy().expiry();
                if (curExpiry >= currentTime) {
                    return update(curExpiry, asyncExtension);
                }
            }
            if (asyncExtension) return false;
            fabric.worker.transaction.TransactionManager tm =
              fabric.worker.transaction.TransactionManager.getInstance();
            acquireReconfigLocks();
            tm.markCoordination();
            fabric.metrics.contracts.enforcement.EnforcementPolicy oldPolicy =
              this.get$currentPolicy();
            fabric.metrics.contracts.enforcement.EnforcementPolicy newPolicy =
              this.get$metric().equalityPolicy(this.get$value(), $getStore());
            if (!fabric.lang.Object._Proxy.idEquals(oldPolicy, null) &&
                  !oldPolicy.equals(newPolicy))
                oldPolicy.unapply(
                            (fabric.metrics.contracts.MetricEqualityContract)
                              this.$getProxy());
            newPolicy.activate();
            long newExpiry = newPolicy.expiry();
            boolean result = update(newExpiry, asyncExtension);
            if (newExpiry >= currentTime) {
                this.set$currentPolicy(newPolicy);
                this.get$currentPolicy().
                  apply((fabric.metrics.contracts.MetricEqualityContract)
                          this.$getProxy());
            } else {
                this.set$currentPolicy(null);
            }
            return result;
        }
        
        public boolean implies(fabric.metrics.Metric otherMetric,
                               double otherRate, double otherBase) {
            if (!getMetric().equals(otherMetric) ||
                  !fabric.metrics.contracts.Bound._Impl.
                  test(otherRate, otherBase, this.get$value(),
                       java.lang.System.currentTimeMillis()))
                return false;
            return valid();
        }
        
        public java.lang.String toString() {
            return ((java.lang.Comparable)
                      fabric.lang.WrappedJavaInlineable.$unwrap(getMetric())).
              toString() + " == " + this.get$value() + " until " + getExpiry();
        }
        
        public fabric.lang.arrays.ObjectArray getLeafSubjects() {
            return this.get$leafMetrics();
        }
        
        public _Impl(fabric.worker.Store $location) { super($location); }
        
        protected fabric.lang.Object._Proxy $makeProxy() {
            return new fabric.metrics.contracts.MetricEqualityContract._Proxy(
                     this);
        }
        
        public void $serialize(java.io.ObjectOutput out,
                               java.util.List refTypes,
                               java.util.List intraStoreRefs,
                               java.util.List interStoreRefs)
              throws java.io.IOException {
            super.$serialize(out, refTypes, intraStoreRefs, interStoreRefs);
            $writeRef($getStore(), this.metric, refTypes, out, intraStoreRefs,
                      interStoreRefs);
            $writeRef($getStore(), this.leafMetrics, refTypes, out,
                      intraStoreRefs, interStoreRefs);
            out.writeDouble(this.value);
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
            this.leafMetrics =
              (fabric.lang.arrays.ObjectArray)
                $readRef(fabric.lang.arrays.ObjectArray._Proxy.class,
                         (fabric.common.RefTypeEnum) refTypes.next(), in, store,
                         intraStoreRefs, interStoreRefs);
            this.value = in.readDouble();
        }
        
        public void $copyAppStateFrom(fabric.lang.Object._Impl other) {
            super.$copyAppStateFrom(other);
            fabric.metrics.contracts.MetricEqualityContract._Impl src =
              (fabric.metrics.contracts.MetricEqualityContract._Impl) other;
            this.metric = src.metric;
            this.leafMetrics = src.leafMetrics;
            this.value = src.value;
        }
    }
    
    interface _Static extends fabric.lang.Object, Cloneable {
        final class _Proxy extends fabric.lang.Object._Proxy
          implements fabric.metrics.contracts.MetricEqualityContract._Static {
            public _Proxy(fabric.metrics.contracts.MetricEqualityContract.
                            _Static._Impl impl) { super(impl); }
            
            public _Proxy(fabric.worker.Store store, long onum) {
                super(store, onum);
            }
            
            public static final fabric.metrics.contracts.MetricEqualityContract.
              _Static $instance;
            
            static {
                fabric.
                  metrics.
                  contracts.
                  MetricEqualityContract.
                  _Static.
                  _Impl
                  impl =
                  (fabric.
                    metrics.
                    contracts.
                    MetricEqualityContract.
                    _Static.
                    _Impl)
                    fabric.lang.Object._Static._Proxy.
                    $makeStaticInstance(
                      fabric.metrics.contracts.MetricEqualityContract._Static.
                        _Impl.class);
                $instance =
                  (fabric.metrics.contracts.MetricEqualityContract._Static)
                    impl.$getProxy();
                impl.$init();
            }
        }
        
        class _Impl extends fabric.lang.Object._Impl
          implements fabric.metrics.contracts.MetricEqualityContract._Static {
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
                return new fabric.metrics.contracts.MetricEqualityContract.
                         _Static._Proxy(this);
            }
            
            private void $init() {  }
        }
        
    }
    
    public static final byte[] $classHash = new byte[] { -128, -31, 11, -8, 93,
    -15, 34, -29, -57, -6, 60, 29, -122, 96, 13, 88, 46, -103, 104, 7, -8, 87,
    77, 14, 36, -48, 32, 41, -53, -108, -110, 58 };
    public static final java.lang.String jlc$CompilerVersion$fabil = "0.3.0";
    public static final long jlc$SourceLastModified$fabil = 1524080235000L;
    public static final java.lang.String jlc$ClassType$fabil =
      "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";
}
