package fabric.metrics.contracts;

import fabric.lang.*;
import fabric.lang.security.*;
import fabric.worker.*;
import fabric.worker.remote.*;
import java.lang.*;
import fabric.util.Collections;
import fabric.util.Set;
import fabric.metrics.contracts.enforcement.DirectPolicy;
import fabric.metrics.contracts.enforcement.EnforcementPolicy;
import fabric.metrics.DerivedMetric;
import fabric.metrics.Metric;
import fabric.metrics.SampledMetric;
import fabric.metrics.util.Subject;

/**
 * A contract asserting that a {@link Metric}'s value is above or below a
 * vectorized boundary expression <code>r\u20d7 * (t - startTime) + b\u20d7</code> until
 * the associated expriation time.
 * <p>
 * This class follows the subject-observer pattern. An instance is an observer
 * of either a {@link Metric} or a set of {@link MetricContract}s and can be
 * observed by other {@link Contract}s.
 */
public interface MetricContract extends fabric.metrics.contracts.Contract {
    public fabric.metrics.Metric get$metric();
    
    public fabric.metrics.Metric set$metric(fabric.metrics.Metric val);
    
    public fabric.metrics.contracts.Bound get$bound();
    
    public fabric.metrics.contracts.Bound set$bound(
      fabric.metrics.contracts.Bound val);
    
    public fabric.metrics.contracts.enforcement.EnforcementPolicy
      get$currentPolicy();
    
    public fabric.metrics.contracts.enforcement.EnforcementPolicy
      set$currentPolicy(fabric.metrics.contracts.enforcement.EnforcementPolicy val);
    
    /**
   * @param metric
   *        the {@link Metric} this contract asserts a bound on
   * @param bound
   *        the {@link Bound} this {@link MetricContract} asserts on
   *        metric.
   */
    public fabric.metrics.contracts.MetricContract
      fabric$metrics$contracts$MetricContract$(
      fabric.metrics.Metric metric, fabric.metrics.contracts.Bound bound);
    
    /** @return the {@link Metric} that this contract observes. */
    public fabric.metrics.Metric getMetric();
    
    /** @return the {@link Bound} that this contract observes. */
    public fabric.metrics.contracts.Bound getBound();
    
    /**
   * @return the expected lifetime of this {@link MetricContract} given the
   *       associated {@link Metric}s current velocity.
   */
    public long getExpectedLifetime();
    
    public void activate();
    
    public fabric.util.Set getLeafSubjects();
    
    public void deactivate();
    
    /**
   * Update this contract's expiration time to stay valid in response to a
   * change in the value of the {@link Subject}s used for enforcing this
   * {@link Contract}. Revokes, extends, and updates the enforcement strategy
   * as needed.
   */
    public void refresh();
    
    /**
   * Check if this implies another {@link MetricContract} being considered.
   *
   * @param otherMetric
   *        the {@link Metric} the other {@link MetricContract} would
   *        assert a bound on
   * @param otherBound
   *        the {@link Bound} that would be used by the other
   *        {@link MetricContract}
   * @return true iff this would imply (and therefore) can enforce another
   *       {@link MetricContract} with the given metric and bound.
   */
    public boolean implies(
      fabric.metrics.Metric otherMetric, fabric.metrics.contracts.Bound otherBound);
    
    /**
   * Check if this implies the other {@link MetricContract}.
   *
   * @param other
   *        the other {@link MetricContract} this is being compared with
   * @return true iff this would imply (and therefore) can enforce other for
   *       the entire duration of other.
   */
    public boolean implies(fabric.metrics.contracts.MetricContract other);
    
    /**
   * Check if this enforces the <strong>same</strong> bound as another
   * {@link MetricContract} being considered.
   *
   * @param otherMetric
   *        the {@link Metric} the other {@link MetricContract} would
   *        assert a bound on
   * @param otherBound
   *        the {@link Bound} that would be used by the other
   *        {@link MetricContract}
   * @return true iff this enforces another {@link MetricContract} with the
   *       given parameters.
   */
    public boolean enforces(
      fabric.metrics.Metric otherMetric, fabric.metrics.contracts.Bound otherBound);
    
    /**
   * Check if this enforces the <strong>same</strong> bound as another
   * {@link MetricContract}.
   *
   * @param other
   *        the other {@link MetricContract} this is being compared with
   * @return true iff this enforces the same bound as other.
   */
    public boolean enforces(fabric.metrics.contracts.MetricContract other);
    
    public java.lang.String toString();
    
    /**
   * Given the current transaction context's view of the system state, return
   * a new {@link EnforcementPolicy} to enforce this {@link MetricContract}.
   *
   * @return The {@link EnforcementPolicy} to use for this
   *       {@link MetricContract} after the call completes.
   */
    public abstract fabric.metrics.contracts.enforcement.EnforcementPolicy
      enforcementStrategy();
    
    public fabric.metrics.contracts.enforcement.EnforcementPolicy
      directStrategy();
    
    public static class _Proxy extends fabric.metrics.contracts.Contract._Proxy
      implements fabric.metrics.contracts.MetricContract {
        public fabric.metrics.Metric get$metric() {
            return ((fabric.metrics.contracts.MetricContract._Impl) fetch()).
              get$metric();
        }
        
        public fabric.metrics.Metric set$metric(fabric.metrics.Metric val) {
            return ((fabric.metrics.contracts.MetricContract._Impl) fetch()).
              set$metric(val);
        }
        
        public fabric.metrics.contracts.Bound get$bound() {
            return ((fabric.metrics.contracts.MetricContract._Impl) fetch()).
              get$bound();
        }
        
        public fabric.metrics.contracts.Bound set$bound(
          fabric.metrics.contracts.Bound val) {
            return ((fabric.metrics.contracts.MetricContract._Impl) fetch()).
              set$bound(val);
        }
        
        public fabric.metrics.contracts.enforcement.EnforcementPolicy
          get$currentPolicy() {
            return ((fabric.metrics.contracts.MetricContract._Impl) fetch()).
              get$currentPolicy();
        }
        
        public fabric.metrics.contracts.enforcement.EnforcementPolicy
          set$currentPolicy(
          fabric.metrics.contracts.enforcement.EnforcementPolicy val) {
            return ((fabric.metrics.contracts.MetricContract._Impl) fetch()).
              set$currentPolicy(val);
        }
        
        public fabric.metrics.contracts.MetricContract
          fabric$metrics$contracts$MetricContract$(
          fabric.metrics.Metric arg1, fabric.metrics.contracts.Bound arg2) {
            return ((fabric.metrics.contracts.MetricContract) fetch()).
              fabric$metrics$contracts$MetricContract$(arg1, arg2);
        }
        
        public fabric.metrics.Metric getMetric() {
            return ((fabric.metrics.contracts.MetricContract) fetch()).
              getMetric();
        }
        
        public fabric.metrics.contracts.Bound getBound() {
            return ((fabric.metrics.contracts.MetricContract) fetch()).getBound(
                                                                         );
        }
        
        public long getExpectedLifetime() {
            return ((fabric.metrics.contracts.MetricContract) fetch()).
              getExpectedLifetime();
        }
        
        public fabric.util.Set getLeafSubjects() {
            return ((fabric.metrics.contracts.MetricContract) fetch()).
              getLeafSubjects();
        }
        
        public boolean implies(fabric.metrics.Metric arg1,
                               fabric.metrics.contracts.Bound arg2) {
            return ((fabric.metrics.contracts.MetricContract) fetch()).implies(
                                                                         arg1,
                                                                         arg2);
        }
        
        public boolean implies(fabric.metrics.contracts.MetricContract arg1) {
            return ((fabric.metrics.contracts.MetricContract) fetch()).implies(
                                                                         arg1);
        }
        
        public boolean enforces(fabric.metrics.Metric arg1,
                                fabric.metrics.contracts.Bound arg2) {
            return ((fabric.metrics.contracts.MetricContract) fetch()).enforces(
                                                                         arg1,
                                                                         arg2);
        }
        
        public boolean enforces(fabric.metrics.contracts.MetricContract arg1) {
            return ((fabric.metrics.contracts.MetricContract) fetch()).enforces(
                                                                         arg1);
        }
        
        public fabric.metrics.contracts.enforcement.EnforcementPolicy
          enforcementStrategy() {
            return ((fabric.metrics.contracts.MetricContract) fetch()).
              enforcementStrategy();
        }
        
        public fabric.metrics.contracts.enforcement.EnforcementPolicy
          directStrategy() {
            return ((fabric.metrics.contracts.MetricContract) fetch()).
              directStrategy();
        }
        
        public _Proxy(MetricContract._Impl impl) { super(impl); }
        
        public _Proxy(fabric.worker.Store store, long onum) {
            super(store, onum);
        }
    }
    
    public abstract static class _Impl
    extends fabric.metrics.contracts.Contract._Impl
      implements fabric.metrics.contracts.MetricContract {
        public fabric.metrics.Metric get$metric() {
            fabric.worker.transaction.TransactionManager.getInstance().
              registerRead(this);
            return this.metric;
        }
        
        public fabric.metrics.Metric set$metric(fabric.metrics.Metric val) {
            fabric.worker.transaction.TransactionManager tm =
              fabric.worker.transaction.TransactionManager.getInstance();
            boolean transactionCreated = tm.registerWrite(this);
            this.metric = val;
            if (transactionCreated) tm.commitTransaction();
            return val;
        }
        
        protected fabric.metrics.Metric metric;
        
        public fabric.metrics.contracts.Bound get$bound() {
            fabric.worker.transaction.TransactionManager.getInstance().
              registerRead(this);
            return this.bound;
        }
        
        public fabric.metrics.contracts.Bound set$bound(
          fabric.metrics.contracts.Bound val) {
            fabric.worker.transaction.TransactionManager tm =
              fabric.worker.transaction.TransactionManager.getInstance();
            boolean transactionCreated = tm.registerWrite(this);
            this.bound = val;
            if (transactionCreated) tm.commitTransaction();
            return val;
        }
        
        protected fabric.metrics.contracts.Bound bound;
        
        public fabric.metrics.contracts.enforcement.EnforcementPolicy
          get$currentPolicy() {
            fabric.worker.transaction.TransactionManager.getInstance().
              registerRead(this);
            return this.currentPolicy;
        }
        
        public fabric.metrics.contracts.enforcement.EnforcementPolicy
          set$currentPolicy(
          fabric.metrics.contracts.enforcement.EnforcementPolicy val) {
            fabric.worker.transaction.TransactionManager tm =
              fabric.worker.transaction.TransactionManager.getInstance();
            boolean transactionCreated = tm.registerWrite(this);
            this.currentPolicy = val;
            if (transactionCreated) tm.commitTransaction();
            return val;
        }
        
        protected fabric.metrics.contracts.enforcement.EnforcementPolicy currentPolicy;
        
        /**
   * @param metric
   *        the {@link Metric} this contract asserts a bound on
   * @param bound
   *        the {@link Bound} this {@link MetricContract} asserts on
   *        metric.
   */
        public fabric.metrics.contracts.MetricContract
          fabric$metrics$contracts$MetricContract$(
          fabric.metrics.Metric metric, fabric.metrics.contracts.Bound bound) {
            this.set$metric(metric);
            this.set$bound(bound);
            fabric$metrics$contracts$Contract$();
            return (fabric.metrics.contracts.MetricContract) this.$getProxy();
        }
        
        /** @return the {@link Metric} that this contract observes. */
        public fabric.metrics.Metric getMetric() { return this.get$metric(); }
        
        /** @return the {@link Bound} that this contract observes. */
        public fabric.metrics.contracts.Bound getBound() {
            return this.get$bound();
        }
        
        /**
   * @return the expected lifetime of this {@link MetricContract} given the
   *       associated {@link Metric}s current velocity.
   */
        public long getExpectedLifetime() {
            return getMetric().expectedTimeToHit(
                                 getBound(),
                                 java.lang.System.currentTimeMillis());
        }
        
        public void activate() {
            super.activate();
            getMetric().addContract((fabric.metrics.contracts.MetricContract)
                                      this.$getProxy());
        }
        
        public fabric.util.Set getLeafSubjects() {
            fabric.metrics.Metric m = getMetric();
            if (fabric.lang.Object._Proxy.
                  $getProxy(
                    (java.lang.Object)
                      fabric.lang.WrappedJavaInlineable.
                      $unwrap(m)) instanceof fabric.metrics.SampledMetric) {
                return fabric.util.Collections._Impl.
                  singleton((fabric.metrics.SampledMetric)
                              fabric.lang.Object._Proxy.$getProxy(m));
            }
            else if (fabric.lang.Object._Proxy.
                       $getProxy(
                         (java.lang.Object)
                           fabric.lang.WrappedJavaInlineable.
                           $unwrap(
                             m)) instanceof fabric.metrics.DerivedMetric) {
                return ((fabric.metrics.DerivedMetric)
                          fabric.lang.Object._Proxy.$getProxy(m)).
                  getLeafSubjects();
            }
            else {
                throw new java.lang.IllegalStateException(
                        "All metrics should be either sampled or derived!");
            }
        }
        
        public void deactivate() {
            if (!isObserved()) {
                if (!fabric.lang.Object._Proxy.idEquals(
                                                 this.get$currentPolicy(),
                                                 null))
                    this.get$currentPolicy().
                      unapply((fabric.metrics.contracts.MetricContract)
                                this.$getProxy());
            }
            super.deactivate();
        }
        
        /**
   * Update this contract's expiration time to stay valid in response to a
   * change in the value of the {@link Subject}s used for enforcing this
   * {@link Contract}. Revokes, extends, and updates the enforcement strategy
   * as needed.
   */
        public void refresh() {
            long currentTime = java.lang.System.currentTimeMillis();
            fabric.metrics.contracts.enforcement.EnforcementPolicy oldPolicy =
              this.get$currentPolicy();
            if (!fabric.lang.Object._Proxy.idEquals(oldPolicy, null))
                oldPolicy.unapply((fabric.metrics.contracts.MetricContract)
                                    this.$getProxy());
            if (this.get$bound().test(this.get$metric(), currentTime)) {
                fabric.metrics.contracts.enforcement.EnforcementPolicy
                  newPolicy;
                if (this.get$metric().isSingleStore()) {
                    newPolicy = directStrategy();
                } else {
                    newPolicy = enforcementStrategy();
                }
                if (newPolicy.expiry() > currentTime) {
                    this.set$currentPolicy(newPolicy);
                    this.get$currentPolicy().
                      apply((fabric.metrics.contracts.MetricContract)
                              this.$getProxy());
                } else {
                    this.set$currentPolicy(null);
                }
                update(
                  java.lang.Math.
                      min(
                        newPolicy.expiry(),
                        currentTime +
                            fabric.metrics.contracts.MetricContract._Static._Proxy.$instance.
                            get$EXTENSION_LIMIT()));
            } else {
                update(0);
            }
        }
        
        /**
   * Check if this implies another {@link MetricContract} being considered.
   *
   * @param otherMetric
   *        the {@link Metric} the other {@link MetricContract} would
   *        assert a bound on
   * @param otherBound
   *        the {@link Bound} that would be used by the other
   *        {@link MetricContract}
   * @return true iff this would imply (and therefore) can enforce another
   *       {@link MetricContract} with the given metric and bound.
   */
        public boolean implies(fabric.metrics.Metric otherMetric,
                               fabric.metrics.contracts.Bound otherBound) {
            return getMetric().equals(otherMetric) &&
              valid(otherBound.get$startTime()) &&
              this.get$bound().implies(otherBound);
        }
        
        /**
   * Check if this implies the other {@link MetricContract}.
   *
   * @param other
   *        the other {@link MetricContract} this is being compared with
   * @return true iff this would imply (and therefore) can enforce other for
   *       the entire duration of other.
   */
        public boolean implies(fabric.metrics.contracts.MetricContract other) {
            return valid(other.getExpiry()) &&
              implies(other.getMetric(), other.getBound());
        }
        
        /**
   * Check if this enforces the <strong>same</strong> bound as another
   * {@link MetricContract} being considered.
   *
   * @param otherMetric
   *        the {@link Metric} the other {@link MetricContract} would
   *        assert a bound on
   * @param otherBound
   *        the {@link Bound} that would be used by the other
   *        {@link MetricContract}
   * @return true iff this enforces another {@link MetricContract} with the
   *       given parameters.
   */
        public boolean enforces(fabric.metrics.Metric otherMetric,
                                fabric.metrics.contracts.Bound otherBound) {
            return getMetric().equals(otherMetric) &&
              valid(otherBound.get$startTime()) &&
              this.get$bound().equals(otherBound);
        }
        
        /**
   * Check if this enforces the <strong>same</strong> bound as another
   * {@link MetricContract}.
   *
   * @param other
   *        the other {@link MetricContract} this is being compared with
   * @return true iff this enforces the same bound as other.
   */
        public boolean enforces(fabric.metrics.contracts.MetricContract other) {
            return valid(other.getExpiry()) &&
              enforces(other.getMetric(), other.getBound());
        }
        
        public java.lang.String toString() {
            return ((java.lang.Object)
                      fabric.lang.WrappedJavaInlineable.$unwrap(getMetric())).
              toString() +
            " " +
            java.lang.String.
              valueOf(
                fabric.lang.WrappedJavaInlineable.$unwrap(this.get$bound())) +
            " until " +
            getExpiry();
        }
        
        /**
   * Given the current transaction context's view of the system state, return
   * a new {@link EnforcementPolicy} to enforce this {@link MetricContract}.
   *
   * @return The {@link EnforcementPolicy} to use for this
   *       {@link MetricContract} after the call completes.
   */
        public abstract fabric.metrics.contracts.enforcement.EnforcementPolicy
          enforcementStrategy();
        
        public fabric.metrics.contracts.enforcement.EnforcementPolicy
          directStrategy() {
            long currentTime = java.lang.System.currentTimeMillis();
            long trueTime = getBound().trueExpiry(getMetric(), currentTime);
            long hedgedTime = ((fabric.metrics.contracts.MetricContract._Impl)
                                 this.fetch()).hedged(currentTime);
            if (getExpiry() > trueTime || hedgedTime > getExpiry() +
                  fabric.metrics.contracts.MetricContract._Static._Proxy.$instance.get$EXTENSION_BUFFER()) {
                return ((fabric.metrics.contracts.enforcement.DirectPolicy)
                          new fabric.metrics.contracts.enforcement.DirectPolicy.
                            _Impl(this.$getStore()).
                          $getProxy()).
                  fabric$metrics$contracts$enforcement$DirectPolicy$(
                    hedgedTime);
            }
            return ((fabric.metrics.contracts.enforcement.DirectPolicy)
                      new fabric.metrics.contracts.enforcement.DirectPolicy.
                        _Impl(this.$getStore()).
                      $getProxy()).
              fabric$metrics$contracts$enforcement$DirectPolicy$(getExpiry());
        }
        
        /**
   * Using the associated {@link Metric's} statistics and some tuning
   * constants, figure out a time to advertise given the current time.
   *
   * @param time
   *        the time we're calculating this hedged expiration at
   * @return an appropriately conservative time to advertise to other nodes in
   *       the system for this contract.
   */
        private long hedged(long time) {
            fabric.metrics.Metric m = getMetric();
            long hedgedResult = getBound().trueExpiry(m, time);
            long startTime = getBound().get$startTime();
            double r = getBound().get$rate();
            double b = getBound().get$base() + r * (time - startTime);
            double x = m.value();
            double v = m.velocity();
            double n = m.noise();
            double bm = b - x;
            double rv = r - v;
            double minYs = x - (v > 0 ? n : -n) /
              (4.0 * (v * java.lang.Math.sqrt(v * v + 1)));
            long min = getBound().trueExpiry(minYs, time);
            if (minYs < x && getBound().test(minYs, time)) {
                hedgedResult = java.lang.Math.min(min, hedgedResult);
            }
            else if (bm * rv > 0) {
                double rotatedY1 =
                  1.0 - java.lang.Math.sqrt(4.0 * rv * bm + 1.0) / (2.0 * rv);
                double rotatedX1 =
                  (rotatedY1 + bm) / rv;
                double rotatedY2 =
                  1.0 + java.lang.Math.sqrt(4.0 * rv * bm + 1.0) / (2.0 * rv);
                double rotatedX2 =
                  (rotatedY2 + bm) / rv;
                double xxFact =
                  1 / java.lang.Math.sqrt(v * v + 1);
                double xyFact =
                  v / java.lang.Math.sqrt(v * v + 1);
                double intersectX1 = rotatedX1 *
                  xxFact +
                  rotatedY1 * xyFact;
                double intersectX2 = rotatedX2 *
                  xxFact +
                  rotatedY2 * xyFact;
                double soonestX1 =
                  intersectX1 >
                  0
                  ? time +
                  intersectX1
                  : java.lang.Long.MAX_VALUE;
                double soonestX2 =
                  intersectX2 >
                  0
                  ? time +
                  intersectX2
                  : java.lang.Long.MAX_VALUE;
                hedgedResult =
                  java.lang.Math.min((long)
                                       java.lang.Math.min(soonestX1, soonestX2),
                                     hedgedResult);
            }
            return hedgedResult;
        }
        
        public _Impl(fabric.worker.Store $location) { super($location); }
        
        protected fabric.lang.Object._Proxy $makeProxy() {
            return new fabric.metrics.contracts.MetricContract._Proxy(this);
        }
        
        public void $serialize(java.io.ObjectOutput out,
                               java.util.List refTypes,
                               java.util.List intraStoreRefs,
                               java.util.List interStoreRefs)
              throws java.io.IOException {
            super.$serialize(out, refTypes, intraStoreRefs, interStoreRefs);
            $writeRef($getStore(), this.metric, refTypes, out, intraStoreRefs,
                      interStoreRefs);
            $writeRef($getStore(), this.bound, refTypes, out, intraStoreRefs,
                      interStoreRefs);
            $writeRef($getStore(), this.currentPolicy, refTypes, out,
                      intraStoreRefs, interStoreRefs);
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
            this.bound = (fabric.metrics.contracts.Bound)
                           $readRef(fabric.metrics.contracts.Bound._Proxy.class,
                                    (fabric.common.RefTypeEnum) refTypes.next(),
                                    in, store, intraStoreRefs, interStoreRefs);
            this.currentPolicy =
              (fabric.
                metrics.
                contracts.
                enforcement.
                EnforcementPolicy)
                $readRef(
                  fabric.metrics.contracts.enforcement.EnforcementPolicy.
                    _Proxy.class, (fabric.common.RefTypeEnum) refTypes.next(),
                  in, store, intraStoreRefs, interStoreRefs);
        }
        
        public void $copyAppStateFrom(fabric.lang.Object._Impl other) {
            super.$copyAppStateFrom(other);
            fabric.metrics.contracts.MetricContract._Impl src =
              (fabric.metrics.contracts.MetricContract._Impl) other;
            this.metric = src.metric;
            this.bound = src.bound;
            this.currentPolicy = src.currentPolicy;
        }
    }
    
    interface _Static extends fabric.lang.Object, Cloneable {
        public double get$HEDGE_FACTOR();
        
        public double set$HEDGE_FACTOR(double val);
        
        public double postInc$HEDGE_FACTOR();
        
        public double postDec$HEDGE_FACTOR();
        
        public long get$EXTENSION_BUFFER();
        
        public long set$EXTENSION_BUFFER(long val);
        
        public long postInc$EXTENSION_BUFFER();
        
        public long postDec$EXTENSION_BUFFER();
        
        public long get$EXTENSION_LIMIT();
        
        public long set$EXTENSION_LIMIT(long val);
        
        public long postInc$EXTENSION_LIMIT();
        
        public long postDec$EXTENSION_LIMIT();
        
        final class _Proxy extends fabric.lang.Object._Proxy
          implements fabric.metrics.contracts.MetricContract._Static {
            public double get$HEDGE_FACTOR() {
                return ((fabric.metrics.contracts.MetricContract._Static._Impl)
                          fetch()).get$HEDGE_FACTOR();
            }
            
            public double set$HEDGE_FACTOR(double val) {
                return ((fabric.metrics.contracts.MetricContract._Static._Impl)
                          fetch()).set$HEDGE_FACTOR(val);
            }
            
            public double postInc$HEDGE_FACTOR() {
                return ((fabric.metrics.contracts.MetricContract._Static._Impl)
                          fetch()).postInc$HEDGE_FACTOR();
            }
            
            public double postDec$HEDGE_FACTOR() {
                return ((fabric.metrics.contracts.MetricContract._Static._Impl)
                          fetch()).postDec$HEDGE_FACTOR();
            }
            
            public long get$EXTENSION_BUFFER() {
                return ((fabric.metrics.contracts.MetricContract._Static._Impl)
                          fetch()).get$EXTENSION_BUFFER();
            }
            
            public long set$EXTENSION_BUFFER(long val) {
                return ((fabric.metrics.contracts.MetricContract._Static._Impl)
                          fetch()).set$EXTENSION_BUFFER(val);
            }
            
            public long postInc$EXTENSION_BUFFER() {
                return ((fabric.metrics.contracts.MetricContract._Static._Impl)
                          fetch()).postInc$EXTENSION_BUFFER();
            }
            
            public long postDec$EXTENSION_BUFFER() {
                return ((fabric.metrics.contracts.MetricContract._Static._Impl)
                          fetch()).postDec$EXTENSION_BUFFER();
            }
            
            public long get$EXTENSION_LIMIT() {
                return ((fabric.metrics.contracts.MetricContract._Static._Impl)
                          fetch()).get$EXTENSION_LIMIT();
            }
            
            public long set$EXTENSION_LIMIT(long val) {
                return ((fabric.metrics.contracts.MetricContract._Static._Impl)
                          fetch()).set$EXTENSION_LIMIT(val);
            }
            
            public long postInc$EXTENSION_LIMIT() {
                return ((fabric.metrics.contracts.MetricContract._Static._Impl)
                          fetch()).postInc$EXTENSION_LIMIT();
            }
            
            public long postDec$EXTENSION_LIMIT() {
                return ((fabric.metrics.contracts.MetricContract._Static._Impl)
                          fetch()).postDec$EXTENSION_LIMIT();
            }
            
            public _Proxy(fabric.metrics.contracts.MetricContract._Static.
                            _Impl impl) { super(impl); }
            
            public _Proxy(fabric.worker.Store store, long onum) {
                super(store, onum);
            }
            
            public static final fabric.metrics.contracts.MetricContract._Static
              $instance;
            
            static {
                fabric.
                  metrics.
                  contracts.
                  MetricContract.
                  _Static.
                  _Impl
                  impl =
                  (fabric.
                    metrics.
                    contracts.
                    MetricContract.
                    _Static.
                    _Impl)
                    fabric.lang.Object._Static._Proxy.
                    $makeStaticInstance(
                      fabric.metrics.contracts.MetricContract._Static.
                        _Impl.class);
                $instance = (fabric.metrics.contracts.MetricContract._Static)
                              impl.$getProxy();
                impl.$init();
            }
        }
        
        class _Impl extends fabric.lang.Object._Impl
          implements fabric.metrics.contracts.MetricContract._Static {
            public double get$HEDGE_FACTOR() { return this.HEDGE_FACTOR; }
            
            public double set$HEDGE_FACTOR(double val) {
                fabric.worker.transaction.TransactionManager tm =
                  fabric.worker.transaction.TransactionManager.getInstance();
                boolean transactionCreated = tm.registerWrite(this);
                this.HEDGE_FACTOR = val;
                if (transactionCreated) tm.commitTransaction();
                return val;
            }
            
            public double postInc$HEDGE_FACTOR() {
                double tmp = this.get$HEDGE_FACTOR();
                this.set$HEDGE_FACTOR((double) (tmp + 1));
                return tmp;
            }
            
            public double postDec$HEDGE_FACTOR() {
                double tmp = this.get$HEDGE_FACTOR();
                this.set$HEDGE_FACTOR((double) (tmp - 1));
                return tmp;
            }
            
            private double HEDGE_FACTOR;
            
            public long get$EXTENSION_BUFFER() { return this.EXTENSION_BUFFER; }
            
            public long set$EXTENSION_BUFFER(long val) {
                fabric.worker.transaction.TransactionManager tm =
                  fabric.worker.transaction.TransactionManager.getInstance();
                boolean transactionCreated = tm.registerWrite(this);
                this.EXTENSION_BUFFER = val;
                if (transactionCreated) tm.commitTransaction();
                return val;
            }
            
            public long postInc$EXTENSION_BUFFER() {
                long tmp = this.get$EXTENSION_BUFFER();
                this.set$EXTENSION_BUFFER((long) (tmp + 1));
                return tmp;
            }
            
            public long postDec$EXTENSION_BUFFER() {
                long tmp = this.get$EXTENSION_BUFFER();
                this.set$EXTENSION_BUFFER((long) (tmp - 1));
                return tmp;
            }
            
            private long EXTENSION_BUFFER;
            
            public long get$EXTENSION_LIMIT() { return this.EXTENSION_LIMIT; }
            
            public long set$EXTENSION_LIMIT(long val) {
                fabric.worker.transaction.TransactionManager tm =
                  fabric.worker.transaction.TransactionManager.getInstance();
                boolean transactionCreated = tm.registerWrite(this);
                this.EXTENSION_LIMIT = val;
                if (transactionCreated) tm.commitTransaction();
                return val;
            }
            
            public long postInc$EXTENSION_LIMIT() {
                long tmp = this.get$EXTENSION_LIMIT();
                this.set$EXTENSION_LIMIT((long) (tmp + 1));
                return tmp;
            }
            
            public long postDec$EXTENSION_LIMIT() {
                long tmp = this.get$EXTENSION_LIMIT();
                this.set$EXTENSION_LIMIT((long) (tmp - 1));
                return tmp;
            }
            
            private long EXTENSION_LIMIT;
            
            public void $serialize(java.io.ObjectOutput out,
                                   java.util.List refTypes,
                                   java.util.List intraStoreRefs,
                                   java.util.List interStoreRefs)
                  throws java.io.IOException {
                super.$serialize(out, refTypes, intraStoreRefs, interStoreRefs);
                out.writeDouble(this.HEDGE_FACTOR);
                out.writeLong(this.EXTENSION_BUFFER);
                out.writeLong(this.EXTENSION_LIMIT);
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
                this.HEDGE_FACTOR = in.readDouble();
                this.EXTENSION_BUFFER = in.readLong();
                this.EXTENSION_LIMIT = in.readLong();
            }
            
            public _Impl(fabric.worker.Store store) { super(store); }
            
            protected fabric.lang.Object._Proxy $makeProxy() {
                return new fabric.metrics.contracts.MetricContract._Static.
                         _Proxy(this);
            }
            
            private void $init() {
                {
                    {
                        fabric.worker.transaction.TransactionManager $tm23 =
                          fabric.worker.transaction.TransactionManager.
                          getInstance();
                        int $backoff24 = 1;
                        $label19: for (boolean $commit20 = false; !$commit20;
                                       ) {
                            if ($backoff24 > 32) {
                                while (true) {
                                    try {
                                        java.lang.Thread.sleep($backoff24);
                                        break;
                                    }
                                    catch (java.lang.
                                             InterruptedException $e21) {
                                        
                                    }
                                }
                            }
                            if ($backoff24 < 5000) $backoff24 *= 2;
                            $commit20 = true;
                            fabric.worker.transaction.TransactionManager.
                              getInstance().startTransaction();
                            try {
                                fabric.metrics.contracts.MetricContract._Static.
                                  _Proxy.
                                  $instance.
                                  set$HEDGE_FACTOR((double) 2);
                                fabric.metrics.contracts.MetricContract._Static.
                                  _Proxy.
                                  $instance.
                                  set$EXTENSION_BUFFER((long) 20);
                                fabric.metrics.contracts.MetricContract._Static.
                                  _Proxy.
                                  $instance.
                                  set$EXTENSION_LIMIT((long) 500L);
                            }
                            catch (final fabric.worker.RetryException $e21) {
                                $commit20 = false;
                                continue $label19;
                            }
                            catch (final fabric.worker.
                                     TransactionRestartingException $e21) {
                                $commit20 = false;
                                fabric.common.TransactionID $currentTid22 =
                                  $tm23.getCurrentTid();
                                if ($e21.tid.isDescendantOf($currentTid22))
                                    continue $label19;
                                if ($currentTid22.parent != null) throw $e21;
                                throw new InternalError(
                                        "Something is broken with transaction management. Got a signal to restart a different transaction than the one being managed.");
                            }
                            catch (final Throwable $e21) {
                                $commit20 = false;
                                if ($tm23.checkForStaleObjects())
                                    continue $label19;
                                throw new fabric.worker.AbortException($e21);
                            }
                            finally {
                                if ($commit20) {
                                    try {
                                        fabric.worker.transaction.TransactionManager.
                                          getInstance().commitTransaction();
                                    }
                                    catch (final fabric.worker.
                                             AbortException $e21) {
                                        $commit20 = false;
                                    }
                                    catch (final fabric.worker.
                                             TransactionRestartingException $e21) {
                                        $commit20 = false;
                                        fabric.common.TransactionID
                                          $currentTid22 = $tm23.getCurrentTid();
                                        if ($currentTid22 ==
                                              null ||
                                              $e21.tid.isDescendantOf(
                                                         $currentTid22) &&
                                              !$currentTid22.equals($e21.tid))
                                            continue $label19;
                                        throw $e21;
                                    }
                                } else {
                                    fabric.worker.transaction.TransactionManager.getInstance().abortTransaction();
                                }
                                if (!$commit20) {  }
                            }
                        }
                    }
                }
            }
        }
        
    }
    
    public static final byte[] $classHash = new byte[] { 23, -120, 3, 44, -79,
    -5, 5, -104, 44, 12, 73, 18, 110, -84, 17, 46, 69, 48, -75, -95, 93, -56,
    51, -98, -38, -56, -104, 6, 70, 5, -62, -37 };
    public static final java.lang.String jlc$CompilerVersion$fabil = "0.3.0";
    public static final long jlc$SourceLastModified$fabil = 1491929446000L;
    public static final java.lang.String jlc$ClassType$fabil =
      "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";
}
