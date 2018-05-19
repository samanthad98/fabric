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
import fabric.worker.metrics.ImmutableMetricsVector;
import fabric.worker.metrics.ImmutableSet;
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
    
    public fabric.worker.metrics.ImmutableMetricsVector get$leafMetrics();
    
    public fabric.worker.metrics.ImmutableMetricsVector set$leafMetrics(
      fabric.worker.metrics.ImmutableMetricsVector val);
    
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
    
    public fabric.worker.metrics.ImmutableMetricsVector getLeafSubjects();
    
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
        
        public fabric.worker.metrics.ImmutableMetricsVector get$leafMetrics() {
            return ((fabric.metrics.contracts.MetricEqualityContract._Impl)
                      fetch()).get$leafMetrics();
        }
        
        public fabric.worker.metrics.ImmutableMetricsVector set$leafMetrics(
          fabric.worker.metrics.ImmutableMetricsVector val) {
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
        
        public fabric.worker.metrics.ImmutableMetricsVector getLeafSubjects() {
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
        
        public fabric.worker.metrics.ImmutableMetricsVector get$leafMetrics() {
            return this.leafMetrics;
        }
        
        public fabric.worker.metrics.ImmutableMetricsVector set$leafMetrics(
          fabric.worker.metrics.ImmutableMetricsVector val) {
            fabric.worker.transaction.TransactionManager tm =
              fabric.worker.transaction.TransactionManager.getInstance();
            boolean transactionCreated = tm.registerWrite(this);
            this.leafMetrics = val;
            if (transactionCreated) tm.commitTransaction();
            return val;
        }
        
        private fabric.worker.metrics.ImmutableMetricsVector leafMetrics;
        
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
            if (fabric.lang.Object._Proxy.
                  $getProxy(
                    (java.lang.Object)
                      fabric.lang.WrappedJavaInlineable.
                      $unwrap(
                        metric)) instanceof fabric.metrics.SampledMetric) {
                this.
                  set$leafMetrics(
                    fabric.worker.metrics.ImmutableMetricsVector.
                        createVector(
                          new fabric.metrics.SampledMetric[] { (fabric.metrics.SampledMetric)
                                                                 fabric.lang.Object._Proxy.
                                                                 $getProxy(
                                                                   metric) }));
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
        
        /**
   * Update this contract's expiration time to stay valid in response to a
   * change in the value of the {@link Subject}s used for enforcing this
   * {@link Contract}. Revokes, extends, and updates the enforcement strategy
   * as needed.
   */
        public boolean refresh(boolean asyncExtension) {
            long currentTime = java.lang.System.currentTimeMillis();
            if (!fabric.lang.Object._Proxy.idEquals(this.get$currentPolicy(),
                                                    null)) {
                long curExpiry = this.get$currentPolicy().expiry();
                if (curExpiry >= currentTime) {
                    this.get$currentPolicy().
                      apply((fabric.metrics.contracts.MetricEqualityContract)
                              this.$getProxy());
                    return update(curExpiry, asyncExtension);
                }
            }
            if (asyncExtension) return false;
            fabric.worker.transaction.TransactionManager tm =
              fabric.worker.transaction.TransactionManager.getInstance();
            tm.markCoordination();
            fabric.metrics.contracts.enforcement.EnforcementPolicy oldPolicy =
              this.get$currentPolicy();
            fabric.metrics.contracts.enforcement.EnforcementPolicy newPolicy =
              this.get$metric().equalityPolicy(this.get$value(), $getStore());
            newPolicy.activate();
            long newExpiry = newPolicy.expiry();
            if (!fabric.lang.Object._Proxy.idEquals(oldPolicy, null))
                oldPolicy.unapply(
                            (fabric.metrics.contracts.MetricEqualityContract)
                              this.$getProxy());
            boolean result = update(newExpiry, asyncExtension);
            if (newExpiry >= currentTime) {
                this.set$currentPolicy(newPolicy);
                this.
                  set$$associated(
                    fabric.worker.metrics.ImmutableSet.emptySet().
                        add(this.get$currentPolicy()));
                this.get$currentPolicy().
                  apply((fabric.metrics.contracts.MetricEqualityContract)
                          this.$getProxy());
            } else {
                this.set$currentPolicy(null);
                this.set$$associated(null);
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
        
        public fabric.worker.metrics.ImmutableMetricsVector getLeafSubjects() {
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
            $writeInline(out, this.leafMetrics);
            out.writeDouble(this.value);
        }
        
        public _Impl(fabric.worker.Store store, long onum, int version,
                     long expiry,
                     fabric.worker.metrics.ImmutableObserverSet observers,
                     fabric.worker.Store labelStore, long labelOnum,
                     fabric.worker.Store accessPolicyStore,
                     long accessPolicyOnum, java.io.ObjectInput in,
                     java.util.Iterator refTypes,
                     java.util.Iterator intraStoreRefs,
                     java.util.Iterator interStoreRefs)
              throws java.io.IOException,
            java.lang.ClassNotFoundException {
            super(store, onum, version, expiry, observers, labelStore,
                  labelOnum, accessPolicyStore, accessPolicyOnum, in, refTypes,
                  intraStoreRefs, interStoreRefs);
            this.metric = (fabric.metrics.Metric)
                            $readRef(fabric.metrics.Metric._Proxy.class,
                                     (fabric.common.RefTypeEnum)
                                       refTypes.next(), in, store,
                                     intraStoreRefs, interStoreRefs);
            this.leafMetrics = (fabric.worker.metrics.ImmutableMetricsVector)
                                 in.readObject();
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
                         long expiry,
                         fabric.worker.metrics.ImmutableObserverSet observers,
                         fabric.worker.Store labelStore, long labelOnum,
                         fabric.worker.Store accessPolicyStore,
                         long accessPolicyOnum, java.io.ObjectInput in,
                         java.util.Iterator refTypes,
                         java.util.Iterator intraStoreRefs,
                         java.util.Iterator interStoreRefs)
                  throws java.io.IOException,
                java.lang.ClassNotFoundException {
                super(store, onum, version, expiry, observers, labelStore,
                      labelOnum, accessPolicyStore, accessPolicyOnum, in,
                      refTypes, intraStoreRefs, interStoreRefs);
            }
            
            public _Impl(fabric.worker.Store store) { super(store); }
            
            protected fabric.lang.Object._Proxy $makeProxy() {
                return new fabric.metrics.contracts.MetricEqualityContract.
                         _Static._Proxy(this);
            }
            
            private void $init() {  }
        }
        
    }
    
    public static final byte[] $classHash = new byte[] { -100, 85, -71, 83,
    -113, 15, -29, -4, -72, 97, 25, -98, 19, -92, 4, 87, -34, 72, -16, 29, -100,
    105, -18, -31, 122, 86, -43, -120, -5, 99, 17, 86 };
    public static final java.lang.String jlc$CompilerVersion$fabil = "0.3.0";
    public static final long jlc$SourceLastModified$fabil = 1526752515000L;
    public static final java.lang.String jlc$ClassType$fabil =
      "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";
}
