package fabric.metrics;

import fabric.lang.*;
import fabric.lang.security.*;
import fabric.worker.*;
import fabric.worker.remote.*;
import java.lang.*;
import fabric.util.ArrayList;
import fabric.util.HashMap;
import fabric.util.List;
import fabric.util.Set;
import fabric.metrics.util.AbstractSubject;
import fabric.metrics.util.Observer;
import fabric.worker.Store;
import fabric.worker.Worker;
import fabric.worker.remote.RemoteWorker;
import fabric.worker.transaction.TransactionManager;
import fabric.worker.metrics.ImmutableObserverSet;
import fabric.worker.metrics.StatsMap;
import fabric.worker.metrics.RunningMetricStats;
import fabric.worker.metrics.proxies.ProxyMap;
import fabric.worker.metrics.treaties.MetricTreaty;
import fabric.worker.metrics.treaties.TreatySet;
import fabric.worker.metrics.treaties.enforcement.DirectPolicy;
import fabric.worker.metrics.treaties.enforcement.EnforcementPolicy;
import fabric.worker.metrics.treaties.statements.EqualityStatement;
import fabric.worker.metrics.treaties.statements.ThresholdStatement;
import fabric.lang.security.LabelUtil;
import java.util.logging.Level;
import java.util.Iterator;
import fabric.common.Logging;
import fabric.common.util.Pair;
import fabric.common.util.LongSet;

/**
 * Abstract class with base implementation of some {@link Metric} methods.
 */
public interface Metric
  extends java.lang.Comparable, fabric.metrics.util.Observer,
          fabric.metrics.util.AbstractSubject
{
    /** @return the current value of the {@link Metric}. */
    public double value();
    
    /**
   * @param scalar
   *            a double scalar to scale this metric by
   * @return A {@link Metric} that tracks the scaled value of this
   *         {@link Metric}.
   */
    public fabric.metrics.DerivedMetric times(double scalar);
    
    /**
   * @param other
   *            another {@link Metric} to add with this {@link Metric}.
   * @return a {@link Metric} that tracks the value of the sum of other and
   *         this.
   */
    public fabric.metrics.DerivedMetric plus(fabric.metrics.Metric other);
    
    /**
   * @param other
   *            another {@link Metric} to take the minimum of along with this
   *            {@link Metric}.
   * @return a {@link Metric} that tracks the value of the minimum of this and
   *         the other {@link Metric}.
   */
    public fabric.metrics.Metric min(fabric.metrics.Metric other);
    
    /**
   * @param other
   *            another {@link Metric} to take the maximum of along with this
   *            {@link Metric}.
   * @return a {@link Metric} that tracks the value of the maximum of this and
   *         the other {@link Metric}.
   */
    public fabric.metrics.Metric max(fabric.metrics.Metric other);
    
    /**
   * @param rate
   *            the rate parameter for the bound on the resulting
   *            {@link MetricTreaty}
   * @param base
   *            the base parameter for the bound on the resulting
   *            {@link MetricTreaty}
   * @param time
   *            the startTime parameter of the bound on the resulting
   *            {@link MetricTreaty}
   * @return a {@link MetricTreaty} which enforces that the {@link Metric}
   *         satisfies a bound with the given parameters at the given time. If
   *         such a {@link MetricTreaty} already exists, it is returned,
   *         otherwise a new one is created and returned (unactivated).
   */
    public fabric.worker.metrics.treaties.MetricTreaty getEqualityTreaty(double value);
    
    /**
   * @param rate
   *            the rate parameter for the bound on the resulting
   *            {@link MetricTreaty}
   * @param base
   *            the base parameter for the bound on the resulting
   *            {@link MetricTreaty}
   * @param time
   *            the startTime parameter of the bound on the resulting
   *            {@link MetricTreaty}
   * @return a {@link MetricTreaty} which enforces that the {@link Metric}
   *         satisfies a bound with the given parameters at the given
   *         time. If such a {@link MetricTreaty} already exists, it is
   *         returned, otherwise a new one is created and returned
   *         (unactivated).
   *
   */
    public fabric.worker.metrics.treaties.MetricTreaty getThresholdTreaty(
      double rate, double base, long time);
    
    /**
   * @param rate
   *            the rate parameter for the bound on the resulting
   *            {@link MetricTreaty}
   * @param base
   *            the base parameter for the bound on the resulting
   *            {@link MetricTreaty}
   * @return a {@link MetricTreaty} which enforces that the {@link Metric}
   *         satisfies a bound with the given parameters at the
   *         current time. If such a {@link MetricTreaty} already exists, it
   *         is returned, otherwise a new one is created and returned
   *         (unactivated).
   */
    public fabric.worker.metrics.treaties.MetricTreaty getThresholdTreaty(
      double rate, double base);
    
    /**
   * Cache of DerivedMetrics using this Metric as a term (helps to break up
   * interning cache to avoid one big map.
   */
    public abstract boolean getUsePreset();
    
    public abstract double getPresetR();
    
    public abstract double getPresetB();
    
    public abstract double getPresetV();
    
    public abstract double getPresetN();
    
    public fabric.worker.metrics.proxies.ProxyMap get$proxies();
    
    public fabric.worker.metrics.proxies.ProxyMap set$proxies(
      fabric.worker.metrics.proxies.ProxyMap val);
    
    /**
   * @param store
   *            the Store that holds this {@link Metric}
   */
    public fabric.metrics.Metric fabric$metrics$Metric$();
    
    /**
   * Get a proxy metric for this metric (returns this if the store is the same).
   */
    public fabric.metrics.Metric getProxy(fabric.worker.Store s);
    
    /**
   * @param weakStats
   *            StatsMap for mapping metrics to weakly consistent values to use
   *            for metric stats.
   * @return the current value of the {@link Metric}.
   */
    public abstract double value(fabric.worker.metrics.StatsMap weakStats);
    
    /** @return the number of samples of the {@link Metric}. */
    public long samples();
    
    /**
   * @param weakStats
   *            StatsMap for mapping metrics to weakly consistent values to use
   *            for metric stats.
   * @return the number of samples of the {@link Metric}.
   */
    public abstract long samples(fabric.worker.metrics.StatsMap weakStats);
    
    /** @return the time of the last update of the {@link Metric}. */
    public long lastUpdate();
    
    /**
   * @param weakStats
   *            StatsMap for mapping metrics to weakly consistent values to use
   *            for metric stats.
   * @return the time of the last update of the {@link Metric}.
   */
    public abstract long lastUpdate(fabric.worker.metrics.StatsMap weakStats);
    
    /** @return the current updateInterval estimate for updates of {@link Metric}. */
    public double updateInterval();
    
    /**
   * @param weakStats
   *            StatsMap for mapping metrics to weakly consistent values to use
   *            for metric stats.
   * @return the updateInterval estimated for updates of the {@link Metric}.
   */
    public abstract double updateInterval(
      fabric.worker.metrics.StatsMap weakStats);
    
    /** @return the estimated velocity of the {@link Metric}. */
    public double velocity();
    
    /**
   * @param weakStats
   *            StatsMap for mapping metrics to weakly consistent values to use
   *            for metric stats.
   * @return the estimated velocity of the {@link Metric}.
   */
    public abstract double velocity(fabric.worker.metrics.StatsMap weakStats);
    
    /** @return the estimated noise of the {@link Metric}. */
    public double noise();
    
    /**
   * @param weakStats
   *            StatsMap for mapping metrics to weakly consistent values to use
   *            for metric stats.
   * @return the estimated velocity of the {@link Metric}.
   */
    public abstract double noise(fabric.worker.metrics.StatsMap weakStats);
    
    /**
   * Used to construct and enforce {@link MetricTreaty}s bounding this
   * {@link Metric}s value.
   * <p>
   * Implementations of this method should use "weak" estimates of value,
   * velocity, and noise to avoid contention issues. The internal
   * implementation is expected to have refreshed these estimates recently
   * prior to the call.
   *
   * @param value
   *            the value we're asserting equality with.
   * @param weakStats
   *            StatsMap for mapping metrics to weakly consistent values to use
   *            for metric stats.
   * @return a {@link EnforcementPolicy} that enforces this {@link Metric}
   *         being above bound.
   */
    public fabric.worker.metrics.treaties.enforcement.EnforcementPolicy
      equalityPolicy(
      double value, fabric.worker.metrics.StatsMap weakStats, final fabric.worker.Store s);
    
    /**
   * Used to construct and enforce {@link MetricTreaty}s bounding this
   * {@link Metric}s value.
   * <p>
   * Implementations of this method should use "weak" estimates of value,
   * velocity, and noise to avoid contention issues. The internal
   * implementation is expected to have refreshed these estimates recently
   * prior to the call.
   *
   * @param value
   *            the value we're asserting equality with.
   * @return a {@link EnforcementPolicy} that enforces this {@link Metric}
   *         being above bound.
   */
    public fabric.worker.metrics.treaties.enforcement.EnforcementPolicy
      equalityPolicy(double value, final fabric.worker.Store s);
    
    /**
   * Used to construct and enforce {@link MetricTreaty}s bounding this
   * {@link Metric}s value.
   * <p>
   * Implementations of this method should use "weak" estimates of value,
   * velocity, and noise to avoid contention issues. The internal
   * implementation is expected to have refreshed these estimates recently
   * prior to the call.
   *
   * @param bound
   *            a bound that the returned policy enforces.
   * @param weakStats
   *            StatsMap for mapping metrics to weakly consistent values to use
   *            for metric stats.
   * @return a {@link EnforcementPolicy} that enforces this {@link Metric}
   *         being above bound.
   */
    public fabric.worker.metrics.treaties.enforcement.EnforcementPolicy
      thresholdPolicy(
      double rate,
      double base, fabric.worker.metrics.StatsMap weakStats, final fabric.worker.Store s);
    
    /**
   * Used to construct and enforce {@link MetricTreaty}s bounding this
   * {@link Metric}s value.
   * <p>
   * Implementations of this method should use "weak" estimates of value,
   * velocity, and noise to avoid contention issues. The internal
   * implementation is expected to have refreshed these estimates recently
   * prior to the call.
   *
   * @param bound
   *            a bound that the returned policy enforces.
   * @return a {@link EnforcementPolicy} that enforces this {@link Metric}
   *         being above bound.
   */
    public fabric.worker.metrics.treaties.enforcement.EnforcementPolicy
      thresholdPolicy(double rate, double base, final fabric.worker.Store s);
    
    /**
   * Update the current "weak" estimates to be used by {@link #policy(Bound)}
   * @param weakStats
   *            StatsMap for mapping metrics to weakly consistent values to use
   *            for metric stats.
   */
    public fabric.worker.metrics.StatsMap refreshWeakEstimates(
      fabric.worker.metrics.StatsMap weakStats);
    
    public fabric.worker.metrics.StatsMap refreshWeakEstimates_remote(
      fabric.lang.security.Principal caller, fabric.worker.metrics.StatsMap weakStats);
    
    /**
   * Utility to allow for running updates as close to the metric as possible.
   * @param weakStats
   *            StatsMap for mapping metrics to weakly consistent values to use
   *            for metric stats.
   *
   * Ugh, public because we don't allow remote calls for non public methods.
   */
    public fabric.worker.metrics.StatsMap refreshLocally(
      fabric.worker.metrics.StatsMap weakStats);
    
    public fabric.worker.metrics.StatsMap refreshLocally_remote(
      fabric.lang.security.Principal caller, fabric.worker.metrics.StatsMap weakStats);
    
    /**
   * @param weakStats
   *            StatsMap for mapping metrics to weakly consistent values to use
   *            for metric stats.
   * @return a freshly computed weak value for this {@link DerivedMetric}.
   */
    public abstract double computeValue(fabric.worker.metrics.StatsMap weakStats);
    
    /**
   * @param weakStats
   *            StatsMap for mapping metrics to weakly consistent values to use
   *            for metric stats.
   * @return a freshly computed weak value for this {@link DerivedMetric}.
   */
    public abstract long computeSamples(fabric.worker.metrics.StatsMap weakStats);
    
    /**
   * @param weakStats
   *            StatsMap for mapping metrics to weakly consistent values to use
   *            for metric stats.
   * @return a freshly computed weak value for this {@link DerivedMetric}.
   */
    public abstract long computeLastUpdate(fabric.worker.metrics.StatsMap weakStats);
    
    /**
   * @param weakStats
   *            StatsMap for mapping metrics to weakly consistent values to use
   *            for metric stats.
   * @return a freshly computed weak value for this {@link DerivedMetric}.
   */
    public abstract double computeUpdateInterval(fabric.worker.metrics.StatsMap weakStats);
    
    /**
   * @param weakStats
   *            StatsMap for mapping metrics to weakly consistent values to use
   *            for metric stats.
   * @return a freshly computed weak velocity for this {@link DerivedMetric}.
   */
    public abstract double computeVelocity(fabric.worker.metrics.StatsMap weakStats);
    
    /**
   * @param weakStats
   *            StatsMap for mapping metrics to weakly consistent values to use
   *            for metric stats.
   * @return a freshly computed noise for this {@link DerivedMetric}.
   */
    public abstract double computeNoise(fabric.worker.metrics.StatsMap weakStats);
    
    /**
   * @return true iff all the sampling and transformations on this metric are
   *         stored on a single store.
   */
    public abstract boolean isSingleStore();
    
    /**
   * @param bound
   *        the bound that the treaty will enforce on this
   *        {@link Metric}
   * @return a {@link MetricTreaty} asserting this metric satisfies the
   *       given bound.
   */
    public fabric.worker.metrics.treaties.MetricTreaty createThresholdTreaty(
      double rate, double base, long time);
    
    /**
   * @param bound
   *        the bound that the treaty will enforce on this
   *        {@link Metric}
   * @return a {@link MetricTreaty} asserting this metric satisfies the
   *       given bound.
   */
    public fabric.worker.metrics.treaties.MetricTreaty createEqualityTreaty(
      double value);
    
    public int compareTo(java.lang.Object that);
    
    public fabric.worker.metrics.ImmutableObserverSet handleUpdates(
      boolean includesObserver, fabric.common.util.LongSet treaties);
    
    /**
   * Handle an update where the metric itself is observing.
   */
    public abstract fabric.worker.metrics.ImmutableObserverSet
      handleDirectUpdates();
    
    public static class _Proxy
    extends fabric.metrics.util.AbstractSubject._Proxy
      implements fabric.metrics.Metric {
        public fabric.worker.metrics.proxies.ProxyMap get$proxies() {
            return ((fabric.metrics.Metric._Impl) fetch()).get$proxies();
        }
        
        public fabric.worker.metrics.proxies.ProxyMap set$proxies(
          fabric.worker.metrics.proxies.ProxyMap val) {
            return ((fabric.metrics.Metric._Impl) fetch()).set$proxies(val);
        }
        
        public double value() {
            return ((fabric.metrics.Metric) fetch()).value();
        }
        
        public fabric.metrics.DerivedMetric times(double arg1) {
            return ((fabric.metrics.Metric) fetch()).times(arg1);
        }
        
        public fabric.metrics.DerivedMetric plus(fabric.metrics.Metric arg1) {
            return ((fabric.metrics.Metric) fetch()).plus(arg1);
        }
        
        public static fabric.metrics.DerivedMetric static_plus(
          fabric.metrics.Metric arg1, fabric.metrics.Metric arg2) {
            return fabric.metrics.Metric._Impl.static_plus(arg1, arg2);
        }
        
        public fabric.metrics.Metric min(fabric.metrics.Metric arg1) {
            return ((fabric.metrics.Metric) fetch()).min(arg1);
        }
        
        public fabric.metrics.Metric max(fabric.metrics.Metric arg1) {
            return ((fabric.metrics.Metric) fetch()).max(arg1);
        }
        
        public fabric.worker.metrics.treaties.MetricTreaty getEqualityTreaty(
          double arg1) {
            return ((fabric.metrics.Metric) fetch()).getEqualityTreaty(arg1);
        }
        
        public fabric.worker.metrics.treaties.MetricTreaty getThresholdTreaty(
          double arg1, double arg2, long arg3) {
            return ((fabric.metrics.Metric) fetch()).getThresholdTreaty(arg1,
                                                                        arg2,
                                                                        arg3);
        }
        
        public fabric.worker.metrics.treaties.MetricTreaty getThresholdTreaty(
          double arg1, double arg2) {
            return ((fabric.metrics.Metric) fetch()).getThresholdTreaty(arg1,
                                                                        arg2);
        }
        
        public static fabric.metrics.Metric scaleAtStore(
          fabric.worker.Store arg1, double arg2, fabric.metrics.Metric arg3) {
            return fabric.metrics.Metric._Impl.scaleAtStore(arg1, arg2, arg3);
        }
        
        public static fabric.metrics.Metric addAtStore(
          fabric.worker.Store arg1, fabric.metrics.Metric[] arg2) {
            return fabric.metrics.Metric._Impl.addAtStore(arg1, arg2);
        }
        
        public static fabric.metrics.Metric minAtStore(
          fabric.worker.Store arg1, fabric.metrics.Metric[] arg2) {
            return fabric.metrics.Metric._Impl.minAtStore(arg1, arg2);
        }
        
        public static fabric.metrics.Metric maxAtStore(
          fabric.worker.Store arg1, fabric.metrics.Metric[] arg2) {
            return fabric.metrics.Metric._Impl.maxAtStore(arg1, arg2);
        }
        
        public boolean getUsePreset() {
            return ((fabric.metrics.Metric) fetch()).getUsePreset();
        }
        
        public double getPresetR() {
            return ((fabric.metrics.Metric) fetch()).getPresetR();
        }
        
        public double getPresetB() {
            return ((fabric.metrics.Metric) fetch()).getPresetB();
        }
        
        public double getPresetV() {
            return ((fabric.metrics.Metric) fetch()).getPresetV();
        }
        
        public double getPresetN() {
            return ((fabric.metrics.Metric) fetch()).getPresetN();
        }
        
        public fabric.metrics.Metric fabric$metrics$Metric$() {
            return ((fabric.metrics.Metric) fetch()).fabric$metrics$Metric$();
        }
        
        public fabric.metrics.Metric getProxy(fabric.worker.Store arg1) {
            return ((fabric.metrics.Metric) fetch()).getProxy(arg1);
        }
        
        public double value(fabric.worker.metrics.StatsMap arg1) {
            return ((fabric.metrics.Metric) fetch()).value(arg1);
        }
        
        public long samples() {
            return ((fabric.metrics.Metric) fetch()).samples();
        }
        
        public long samples(fabric.worker.metrics.StatsMap arg1) {
            return ((fabric.metrics.Metric) fetch()).samples(arg1);
        }
        
        public long lastUpdate() {
            return ((fabric.metrics.Metric) fetch()).lastUpdate();
        }
        
        public long lastUpdate(fabric.worker.metrics.StatsMap arg1) {
            return ((fabric.metrics.Metric) fetch()).lastUpdate(arg1);
        }
        
        public double updateInterval() {
            return ((fabric.metrics.Metric) fetch()).updateInterval();
        }
        
        public double updateInterval(fabric.worker.metrics.StatsMap arg1) {
            return ((fabric.metrics.Metric) fetch()).updateInterval(arg1);
        }
        
        public double velocity() {
            return ((fabric.metrics.Metric) fetch()).velocity();
        }
        
        public double velocity(fabric.worker.metrics.StatsMap arg1) {
            return ((fabric.metrics.Metric) fetch()).velocity(arg1);
        }
        
        public double noise() {
            return ((fabric.metrics.Metric) fetch()).noise();
        }
        
        public double noise(fabric.worker.metrics.StatsMap arg1) {
            return ((fabric.metrics.Metric) fetch()).noise(arg1);
        }
        
        public fabric.worker.metrics.treaties.enforcement.EnforcementPolicy
          equalityPolicy(double arg1, fabric.worker.metrics.StatsMap arg2,
                         fabric.worker.Store arg3) {
            return ((fabric.metrics.Metric) fetch()).equalityPolicy(arg1, arg2,
                                                                    arg3);
        }
        
        public fabric.worker.metrics.treaties.enforcement.EnforcementPolicy
          equalityPolicy(double arg1, fabric.worker.Store arg2) {
            return ((fabric.metrics.Metric) fetch()).equalityPolicy(arg1, arg2);
        }
        
        public fabric.worker.metrics.treaties.enforcement.EnforcementPolicy
          thresholdPolicy(double arg1, double arg2,
                          fabric.worker.metrics.StatsMap arg3,
                          fabric.worker.Store arg4) {
            return ((fabric.metrics.Metric) fetch()).thresholdPolicy(arg1, arg2,
                                                                     arg3,
                                                                     arg4);
        }
        
        public fabric.worker.metrics.treaties.enforcement.EnforcementPolicy
          thresholdPolicy(double arg1, double arg2, fabric.worker.Store arg3) {
            return ((fabric.metrics.Metric) fetch()).thresholdPolicy(arg1, arg2,
                                                                     arg3);
        }
        
        public fabric.worker.metrics.StatsMap refreshWeakEstimates(
          fabric.worker.metrics.StatsMap arg1) {
            return ((fabric.metrics.Metric) fetch()).refreshWeakEstimates(arg1);
        }
        
        public fabric.worker.metrics.StatsMap refreshWeakEstimates_remote(
          fabric.lang.security.Principal arg1,
          fabric.worker.metrics.StatsMap arg2) {
            return ((fabric.metrics.Metric) fetch()).
              refreshWeakEstimates_remote(arg1, arg2);
        }
        
        public static final java.lang.Class[] $paramTypes0 =
          { fabric.worker.metrics.StatsMap.class };
        
        public fabric.worker.metrics.StatsMap refreshWeakEstimates$remote(
          final fabric.worker.remote.RemoteWorker $remoteWorker,
          fabric.lang.security.Principal arg1,
          fabric.worker.metrics.StatsMap arg2) {
            if ($remoteWorker ==
                  fabric.worker.Worker.getWorker().getLocalWorker())
                return refreshWeakEstimates(arg2);
            else
                try {
                    return (fabric.worker.metrics.StatsMap)
                             fabric.lang.Object._Proxy.
                             $getProxy(
                               $remoteWorker.
                                   issueRemoteCall(
                                     this, "refreshWeakEstimates", $paramTypes0,
                                     new java.lang.Object[] { arg2 }));
                }
                catch (fabric.worker.remote.RemoteCallException $e) {
                    java.lang.Throwable $t = $e.getCause();
                    throw new fabric.common.exceptions.InternalError($e);
                }
        }
        
        public fabric.worker.metrics.StatsMap refreshLocally(
          fabric.worker.metrics.StatsMap arg1) {
            return ((fabric.metrics.Metric) fetch()).refreshLocally(arg1);
        }
        
        public fabric.worker.metrics.StatsMap refreshLocally_remote(
          fabric.lang.security.Principal arg1,
          fabric.worker.metrics.StatsMap arg2) {
            return ((fabric.metrics.Metric) fetch()).refreshLocally_remote(
                                                       arg1, arg2);
        }
        
        public static final java.lang.Class[] $paramTypes1 =
          { fabric.worker.metrics.StatsMap.class };
        
        public fabric.worker.metrics.StatsMap refreshLocally$remote(
          final fabric.worker.remote.RemoteWorker $remoteWorker,
          fabric.lang.security.Principal arg1,
          fabric.worker.metrics.StatsMap arg2) {
            if ($remoteWorker ==
                  fabric.worker.Worker.getWorker().getLocalWorker())
                return refreshLocally(arg2);
            else
                try {
                    return (fabric.worker.metrics.StatsMap)
                             fabric.lang.Object._Proxy.
                             $getProxy(
                               $remoteWorker.
                                   issueRemoteCall(
                                     this, "refreshLocally", $paramTypes1,
                                     new java.lang.Object[] { arg2 }));
                }
                catch (fabric.worker.remote.RemoteCallException $e) {
                    java.lang.Throwable $t = $e.getCause();
                    throw new fabric.common.exceptions.InternalError($e);
                }
        }
        
        public double computeValue(fabric.worker.metrics.StatsMap arg1) {
            return ((fabric.metrics.Metric) fetch()).computeValue(arg1);
        }
        
        public long computeSamples(fabric.worker.metrics.StatsMap arg1) {
            return ((fabric.metrics.Metric) fetch()).computeSamples(arg1);
        }
        
        public long computeLastUpdate(fabric.worker.metrics.StatsMap arg1) {
            return ((fabric.metrics.Metric) fetch()).computeLastUpdate(arg1);
        }
        
        public double computeUpdateInterval(
          fabric.worker.metrics.StatsMap arg1) {
            return ((fabric.metrics.Metric) fetch()).computeUpdateInterval(
                                                       arg1);
        }
        
        public double computeVelocity(fabric.worker.metrics.StatsMap arg1) {
            return ((fabric.metrics.Metric) fetch()).computeVelocity(arg1);
        }
        
        public double computeNoise(fabric.worker.metrics.StatsMap arg1) {
            return ((fabric.metrics.Metric) fetch()).computeNoise(arg1);
        }
        
        public boolean isSingleStore() {
            return ((fabric.metrics.Metric) fetch()).isSingleStore();
        }
        
        public fabric.worker.metrics.treaties.MetricTreaty
          createThresholdTreaty(double arg1, double arg2, long arg3) {
            return ((fabric.metrics.Metric) fetch()).createThresholdTreaty(
                                                       arg1, arg2, arg3);
        }
        
        public fabric.worker.metrics.treaties.MetricTreaty createEqualityTreaty(
          double arg1) {
            return ((fabric.metrics.Metric) fetch()).createEqualityTreaty(arg1);
        }
        
        public int compareTo(java.lang.Object arg1) {
            return ((fabric.metrics.Metric) fetch()).compareTo(arg1);
        }
        
        public fabric.worker.metrics.ImmutableObserverSet handleUpdates(
          boolean arg1, fabric.common.util.LongSet arg2) {
            return ((fabric.metrics.Metric) fetch()).handleUpdates(arg1, arg2);
        }
        
        public fabric.worker.metrics.ImmutableObserverSet handleDirectUpdates(
          ) {
            return ((fabric.metrics.Metric) fetch()).handleDirectUpdates();
        }
        
        public fabric.worker.metrics.ImmutableMetricsVector getLeafSubjects() {
            return ((fabric.metrics.Metric) fetch()).getLeafSubjects();
        }
        
        public _Proxy(Metric._Impl impl) { super(impl); }
        
        public _Proxy(fabric.worker.Store store, long onum) {
            super(store, onum);
        }
    }
    
    public abstract static class _Impl
    extends fabric.metrics.util.AbstractSubject._Impl
      implements fabric.metrics.Metric {
        /** @return the current value of the {@link Metric}. */
        public double value() {
            return value(fabric.worker.metrics.StatsMap.emptyStats());
        }
        
        /**
   * @param scalar
   *            a double scalar to scale this metric by
   * @return A {@link Metric} that tracks the scaled value of this
   *         {@link Metric}.
   */
        public fabric.metrics.DerivedMetric times(double scalar) {
            return fabric.metrics.Metric._Impl.static_times(
                                                 (fabric.metrics.Metric)
                                                   this.$getProxy(), scalar);
        }
        
        private static fabric.metrics.DerivedMetric static_times(
          fabric.metrics.Metric tmp, double scalar) {
            final fabric.worker.Store s = tmp.$getStore();
            fabric.metrics.DerivedMetric val = null;
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                val =
                  ((fabric.metrics.ScaledMetric)
                     new fabric.metrics.ScaledMetric._Impl(s).$getProxy()).
                    fabric$metrics$ScaledMetric$(scalar, tmp);
            }
            else {
                {
                    fabric.metrics.DerivedMetric val$var88 = val;
                    fabric.worker.transaction.TransactionManager $tm94 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled97 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff95 = 1;
                    boolean $doBackoff96 = true;
                    boolean $retry91 = true;
                    $label89: for (boolean $commit90 = false; !$commit90; ) {
                        if ($backoffEnabled97) {
                            if ($doBackoff96) {
                                if ($backoff95 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.sleep($backoff95);
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e92) {
                                            
                                        }
                                    }
                                }
                                if ($backoff95 < 5000) $backoff95 *= 2;
                            }
                            $doBackoff96 = $backoff95 <= 32 || !$doBackoff96;
                        }
                        $commit90 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            val =
                              ((fabric.metrics.ScaledMetric)
                                 new fabric.metrics.ScaledMetric._Impl(s).
                                 $getProxy()).fabric$metrics$ScaledMetric$(
                                                scalar, tmp);
                        }
                        catch (final fabric.worker.RetryException $e92) {
                            $commit90 = false;
                            continue $label89;
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e92) {
                            $commit90 = false;
                            fabric.common.TransactionID $currentTid93 =
                              $tm94.getCurrentTid();
                            if ($e92.tid.isDescendantOf($currentTid93))
                                continue $label89;
                            if ($currentTid93.parent != null) {
                                $retry91 = false;
                                throw $e92;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e92) {
                            $commit90 = false;
                            if ($tm94.checkForStaleObjects()) continue;
                            fabric.common.TransactionID $currentTid93 =
                              $tm94.getCurrentTid();
                            if ($e92.tid.isDescendantOf($currentTid93)) {
                                $retry91 = true;
                            }
                            else if ($currentTid93.parent != null) {
                                $retry91 = false;
                                throw $e92;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e92) {
                            $commit90 = false;
                            if ($tm94.checkForStaleObjects()) continue $label89;
                            $retry91 = false;
                            throw new fabric.worker.AbortException($e92);
                        }
                        finally {
                            if ($commit90) {
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e92) {
                                    $commit90 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e92) {
                                    $commit90 = false;
                                    fabric.common.TransactionID $currentTid93 =
                                      $tm94.getCurrentTid();
                                    if ($currentTid93 != null) {
                                        if ($e92.tid.equals($currentTid93) ||
                                              !$e92.tid.isDescendantOf(
                                                          $currentTid93)) {
                                            throw $e92;
                                        }
                                    }
                                }
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit90 && $retry91) {
                                { val = val$var88; }
                                continue $label89;
                            }
                        }
                    }
                }
            }
            return val;
        }
        
        /**
   * @param other
   *            another {@link Metric} to add with this {@link Metric}.
   * @return a {@link Metric} that tracks the value of the sum of other and
   *         this.
   */
        public fabric.metrics.DerivedMetric plus(fabric.metrics.Metric other) {
            return fabric.metrics.Metric._Impl.static_plus(
                                                 (fabric.metrics.Metric)
                                                   this.$getProxy(), other);
        }
        
        public static fabric.metrics.DerivedMetric static_plus(
          fabric.metrics.Metric tmp, fabric.metrics.Metric other) {
            if (fabric.lang.Object._Proxy.
                  $getProxy(
                    (java.lang.Object)
                      fabric.lang.WrappedJavaInlineable.
                      $unwrap(other)) instanceof fabric.metrics.SumMetric)
                return other.plus(tmp);
            final fabric.worker.Store s = tmp.$getStore();
            fabric.metrics.DerivedMetric val = null;
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                val =
                  ((fabric.metrics.SumMetric)
                     new fabric.metrics.SumMetric._Impl(s).$getProxy()).
                    fabric$metrics$SumMetric$(new fabric.metrics.Metric[] { tmp,
                                                other });
            }
            else {
                {
                    fabric.metrics.DerivedMetric val$var98 = val;
                    fabric.worker.transaction.TransactionManager $tm104 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled107 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff105 = 1;
                    boolean $doBackoff106 = true;
                    boolean $retry101 = true;
                    $label99: for (boolean $commit100 = false; !$commit100; ) {
                        if ($backoffEnabled107) {
                            if ($doBackoff106) {
                                if ($backoff105 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.sleep($backoff105);
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e102) {
                                            
                                        }
                                    }
                                }
                                if ($backoff105 < 5000) $backoff105 *= 2;
                            }
                            $doBackoff106 = $backoff105 <= 32 || !$doBackoff106;
                        }
                        $commit100 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            val =
                              ((fabric.metrics.SumMetric)
                                 new fabric.metrics.SumMetric._Impl(s).
                                 $getProxy()).
                                fabric$metrics$SumMetric$(
                                  new fabric.metrics.Metric[] { tmp, other });
                        }
                        catch (final fabric.worker.RetryException $e102) {
                            $commit100 = false;
                            continue $label99;
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e102) {
                            $commit100 = false;
                            fabric.common.TransactionID $currentTid103 =
                              $tm104.getCurrentTid();
                            if ($e102.tid.isDescendantOf($currentTid103))
                                continue $label99;
                            if ($currentTid103.parent != null) {
                                $retry101 = false;
                                throw $e102;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e102) {
                            $commit100 = false;
                            if ($tm104.checkForStaleObjects()) continue;
                            fabric.common.TransactionID $currentTid103 =
                              $tm104.getCurrentTid();
                            if ($e102.tid.isDescendantOf($currentTid103)) {
                                $retry101 = true;
                            }
                            else if ($currentTid103.parent != null) {
                                $retry101 = false;
                                throw $e102;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e102) {
                            $commit100 = false;
                            if ($tm104.checkForStaleObjects())
                                continue $label99;
                            $retry101 = false;
                            throw new fabric.worker.AbortException($e102);
                        }
                        finally {
                            if ($commit100) {
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e102) {
                                    $commit100 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e102) {
                                    $commit100 = false;
                                    fabric.common.TransactionID $currentTid103 =
                                      $tm104.getCurrentTid();
                                    if ($currentTid103 != null) {
                                        if ($e102.tid.equals($currentTid103) ||
                                              !$e102.tid.isDescendantOf(
                                                           $currentTid103)) {
                                            throw $e102;
                                        }
                                    }
                                }
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit100 && $retry101) {
                                { val = val$var98; }
                                continue $label99;
                            }
                        }
                    }
                }
            }
            return val;
        }
        
        /**
   * @param other
   *            another {@link Metric} to take the minimum of along with this
   *            {@link Metric}.
   * @return a {@link Metric} that tracks the value of the minimum of this and
   *         the other {@link Metric}.
   */
        public fabric.metrics.Metric min(fabric.metrics.Metric other) {
            return fabric.metrics.Metric._Impl.static_min(
                                                 (fabric.metrics.Metric)
                                                   this.$getProxy(), other);
        }
        
        private static fabric.metrics.Metric static_min(
          fabric.metrics.Metric tmp, fabric.metrics.Metric other) {
            if (tmp.equals(other)) return tmp;
            if (fabric.lang.Object._Proxy.
                  $getProxy(
                    (java.lang.Object)
                      fabric.lang.WrappedJavaInlineable.
                      $unwrap(other)) instanceof fabric.metrics.MinMetric)
                return other.min(tmp);
            final fabric.worker.Store s = tmp.$getStore();
            fabric.metrics.DerivedMetric val = null;
            if (tmp.compareTo(
                      (java.lang.Object)
                        fabric.lang.WrappedJavaInlineable.$unwrap(other)) >
                  0) {
                if (fabric.worker.transaction.TransactionManager.getInstance().
                      inTxn()) {
                    val =
                      ((fabric.metrics.MinMetric)
                         new fabric.metrics.MinMetric._Impl(s).$getProxy()).
                        fabric$metrics$MinMetric$(
                          new fabric.metrics.Metric[] { other, tmp });
                }
                else {
                    {
                        fabric.metrics.DerivedMetric val$var108 = val;
                        fabric.worker.transaction.TransactionManager $tm114 =
                          fabric.worker.transaction.TransactionManager.
                          getInstance();
                        boolean $backoffEnabled117 =
                          fabric.worker.Worker.getWorker(
                                                 ).config.txRetryBackoff;
                        int $backoff115 = 1;
                        boolean $doBackoff116 = true;
                        boolean $retry111 = true;
                        $label109: for (boolean $commit110 = false; !$commit110;
                                        ) {
                            if ($backoffEnabled117) {
                                if ($doBackoff116) {
                                    if ($backoff115 > 32) {
                                        while (true) {
                                            try {
                                                java.lang.Thread.sleep(
                                                                   $backoff115);
                                                break;
                                            }
                                            catch (java.lang.
                                                     InterruptedException $e112) {
                                                
                                            }
                                        }
                                    }
                                    if ($backoff115 < 5000) $backoff115 *= 2;
                                }
                                $doBackoff116 = $backoff115 <= 32 ||
                                                  !$doBackoff116;
                            }
                            $commit110 = true;
                            fabric.worker.transaction.TransactionManager.
                              getInstance().startTransaction();
                            try {
                                val =
                                  ((fabric.metrics.MinMetric)
                                     new fabric.metrics.MinMetric._Impl(s).
                                     $getProxy()).
                                    fabric$metrics$MinMetric$(
                                      new fabric.metrics.Metric[] { other,
                                        tmp });
                            }
                            catch (final fabric.worker.RetryException $e112) {
                                $commit110 = false;
                                continue $label109;
                            }
                            catch (final fabric.worker.
                                     TransactionRestartingException $e112) {
                                $commit110 = false;
                                fabric.common.TransactionID $currentTid113 =
                                  $tm114.getCurrentTid();
                                if ($e112.tid.isDescendantOf($currentTid113))
                                    continue $label109;
                                if ($currentTid113.parent != null) {
                                    $retry111 = false;
                                    throw $e112;
                                }
                                throw new InternalError(
                                        "Something is broken with " +
                                            "transaction management. Got a signal to restart a " +
                                            "different transaction than the one being managed.");
                            }
                            catch (final fabric.worker.metrics.
                                     LockConflictException $e112) {
                                $commit110 = false;
                                if ($tm114.checkForStaleObjects()) continue;
                                fabric.common.TransactionID $currentTid113 =
                                  $tm114.getCurrentTid();
                                if ($e112.tid.isDescendantOf($currentTid113)) {
                                    $retry111 = true;
                                }
                                else if ($currentTid113.parent != null) {
                                    $retry111 = false;
                                    throw $e112;
                                }
                                else {
                                    throw new InternalError(
                                            "Something is broken with transaction " +
                                                "management. Got a signal for a lock conflict in a different " +
                                                "transaction than the one being managed.");
                                }
                            }
                            catch (final Throwable $e112) {
                                $commit110 = false;
                                if ($tm114.checkForStaleObjects())
                                    continue $label109;
                                $retry111 = false;
                                throw new fabric.worker.AbortException($e112);
                            }
                            finally {
                                if ($commit110) {
                                    try {
                                        fabric.worker.transaction.TransactionManager.
                                          getInstance().commitTransaction();
                                    }
                                    catch (final fabric.worker.
                                             AbortException $e112) {
                                        $commit110 = false;
                                    }
                                    catch (final fabric.worker.
                                             TransactionRestartingException $e112) {
                                        $commit110 = false;
                                        fabric.common.TransactionID
                                          $currentTid113 =
                                          $tm114.getCurrentTid();
                                        if ($currentTid113 != null) {
                                            if ($e112.tid.equals(
                                                            $currentTid113) ||
                                                  !$e112.tid.
                                                  isDescendantOf(
                                                    $currentTid113)) {
                                                throw $e112;
                                            }
                                        }
                                    }
                                } else {
                                    fabric.worker.transaction.TransactionManager.getInstance().abortTransaction();
                                }
                                if (!$commit110 && $retry111) {
                                    { val = val$var108; }
                                    continue $label109;
                                }
                            }
                        }
                    }
                }
            }
            else {
                if (fabric.worker.transaction.TransactionManager.getInstance().
                      inTxn()) {
                    val =
                      ((fabric.metrics.MinMetric)
                         new fabric.metrics.MinMetric._Impl(s).$getProxy()).
                        fabric$metrics$MinMetric$(
                          new fabric.metrics.Metric[] { tmp, other });
                }
                else {
                    {
                        fabric.metrics.DerivedMetric val$var118 = val;
                        fabric.worker.transaction.TransactionManager $tm124 =
                          fabric.worker.transaction.TransactionManager.
                          getInstance();
                        boolean $backoffEnabled127 =
                          fabric.worker.Worker.getWorker(
                                                 ).config.txRetryBackoff;
                        int $backoff125 = 1;
                        boolean $doBackoff126 = true;
                        boolean $retry121 = true;
                        $label119: for (boolean $commit120 = false; !$commit120;
                                        ) {
                            if ($backoffEnabled127) {
                                if ($doBackoff126) {
                                    if ($backoff125 > 32) {
                                        while (true) {
                                            try {
                                                java.lang.Thread.sleep(
                                                                   $backoff125);
                                                break;
                                            }
                                            catch (java.lang.
                                                     InterruptedException $e122) {
                                                
                                            }
                                        }
                                    }
                                    if ($backoff125 < 5000) $backoff125 *= 2;
                                }
                                $doBackoff126 = $backoff125 <= 32 ||
                                                  !$doBackoff126;
                            }
                            $commit120 = true;
                            fabric.worker.transaction.TransactionManager.
                              getInstance().startTransaction();
                            try {
                                val =
                                  ((fabric.metrics.MinMetric)
                                     new fabric.metrics.MinMetric._Impl(s).
                                     $getProxy()).
                                    fabric$metrics$MinMetric$(
                                      new fabric.metrics.Metric[] { tmp,
                                        other });
                            }
                            catch (final fabric.worker.RetryException $e122) {
                                $commit120 = false;
                                continue $label119;
                            }
                            catch (final fabric.worker.
                                     TransactionRestartingException $e122) {
                                $commit120 = false;
                                fabric.common.TransactionID $currentTid123 =
                                  $tm124.getCurrentTid();
                                if ($e122.tid.isDescendantOf($currentTid123))
                                    continue $label119;
                                if ($currentTid123.parent != null) {
                                    $retry121 = false;
                                    throw $e122;
                                }
                                throw new InternalError(
                                        "Something is broken with " +
                                            "transaction management. Got a signal to restart a " +
                                            "different transaction than the one being managed.");
                            }
                            catch (final fabric.worker.metrics.
                                     LockConflictException $e122) {
                                $commit120 = false;
                                if ($tm124.checkForStaleObjects()) continue;
                                fabric.common.TransactionID $currentTid123 =
                                  $tm124.getCurrentTid();
                                if ($e122.tid.isDescendantOf($currentTid123)) {
                                    $retry121 = true;
                                }
                                else if ($currentTid123.parent != null) {
                                    $retry121 = false;
                                    throw $e122;
                                }
                                else {
                                    throw new InternalError(
                                            "Something is broken with transaction " +
                                                "management. Got a signal for a lock conflict in a different " +
                                                "transaction than the one being managed.");
                                }
                            }
                            catch (final Throwable $e122) {
                                $commit120 = false;
                                if ($tm124.checkForStaleObjects())
                                    continue $label119;
                                $retry121 = false;
                                throw new fabric.worker.AbortException($e122);
                            }
                            finally {
                                if ($commit120) {
                                    try {
                                        fabric.worker.transaction.TransactionManager.
                                          getInstance().commitTransaction();
                                    }
                                    catch (final fabric.worker.
                                             AbortException $e122) {
                                        $commit120 = false;
                                    }
                                    catch (final fabric.worker.
                                             TransactionRestartingException $e122) {
                                        $commit120 = false;
                                        fabric.common.TransactionID
                                          $currentTid123 =
                                          $tm124.getCurrentTid();
                                        if ($currentTid123 != null) {
                                            if ($e122.tid.equals(
                                                            $currentTid123) ||
                                                  !$e122.tid.
                                                  isDescendantOf(
                                                    $currentTid123)) {
                                                throw $e122;
                                            }
                                        }
                                    }
                                } else {
                                    fabric.worker.transaction.TransactionManager.getInstance().abortTransaction();
                                }
                                if (!$commit120 && $retry121) {
                                    { val = val$var118; }
                                    continue $label119;
                                }
                            }
                        }
                    }
                }
            }
            return val;
        }
        
        /**
   * @param other
   *            another {@link Metric} to take the maximum of along with this
   *            {@link Metric}.
   * @return a {@link Metric} that tracks the value of the maximum of this and
   *         the other {@link Metric}.
   */
        public fabric.metrics.Metric max(fabric.metrics.Metric other) {
            return this.times(-1).min(other.times(-1)).times(-1);
        }
        
        /**
   * @param rate
   *            the rate parameter for the bound on the resulting
   *            {@link MetricTreaty}
   * @param base
   *            the base parameter for the bound on the resulting
   *            {@link MetricTreaty}
   * @param time
   *            the startTime parameter of the bound on the resulting
   *            {@link MetricTreaty}
   * @return a {@link MetricTreaty} which enforces that the {@link Metric}
   *         satisfies a bound with the given parameters at the given time. If
   *         such a {@link MetricTreaty} already exists, it is returned,
   *         otherwise a new one is created and returned (unactivated).
   */
        public fabric.worker.metrics.treaties.MetricTreaty getEqualityTreaty(
          double value) {
            return fabric.metrics.Metric._Impl.static_getEqualityTreaty(
                                                 (fabric.metrics.Metric)
                                                   this.$getProxy(), value);
        }
        
        private static fabric.worker.metrics.treaties.MetricTreaty
          static_getEqualityTreaty(fabric.metrics.Metric tmp, double value) {
            fabric.worker.metrics.treaties.MetricTreaty mc = null;
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                mc = tmp.createEqualityTreaty(value);
            }
            else {
                {
                    fabric.worker.metrics.treaties.MetricTreaty mc$var128 = mc;
                    fabric.worker.transaction.TransactionManager $tm134 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled137 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff135 = 1;
                    boolean $doBackoff136 = true;
                    boolean $retry131 = true;
                    $label129: for (boolean $commit130 = false; !$commit130; ) {
                        if ($backoffEnabled137) {
                            if ($doBackoff136) {
                                if ($backoff135 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.sleep($backoff135);
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e132) {
                                            
                                        }
                                    }
                                }
                                if ($backoff135 < 5000) $backoff135 *= 2;
                            }
                            $doBackoff136 = $backoff135 <= 32 || !$doBackoff136;
                        }
                        $commit130 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try { mc = tmp.createEqualityTreaty(value); }
                        catch (final fabric.worker.RetryException $e132) {
                            $commit130 = false;
                            continue $label129;
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e132) {
                            $commit130 = false;
                            fabric.common.TransactionID $currentTid133 =
                              $tm134.getCurrentTid();
                            if ($e132.tid.isDescendantOf($currentTid133))
                                continue $label129;
                            if ($currentTid133.parent != null) {
                                $retry131 = false;
                                throw $e132;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e132) {
                            $commit130 = false;
                            if ($tm134.checkForStaleObjects()) continue;
                            fabric.common.TransactionID $currentTid133 =
                              $tm134.getCurrentTid();
                            if ($e132.tid.isDescendantOf($currentTid133)) {
                                $retry131 = true;
                            }
                            else if ($currentTid133.parent != null) {
                                $retry131 = false;
                                throw $e132;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e132) {
                            $commit130 = false;
                            if ($tm134.checkForStaleObjects())
                                continue $label129;
                            $retry131 = false;
                            throw new fabric.worker.AbortException($e132);
                        }
                        finally {
                            if ($commit130) {
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e132) {
                                    $commit130 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e132) {
                                    $commit130 = false;
                                    fabric.common.TransactionID $currentTid133 =
                                      $tm134.getCurrentTid();
                                    if ($currentTid133 != null) {
                                        if ($e132.tid.equals($currentTid133) ||
                                              !$e132.tid.isDescendantOf(
                                                           $currentTid133)) {
                                            throw $e132;
                                        }
                                    }
                                }
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit130 && $retry131) {
                                { mc = mc$var128; }
                                continue $label129;
                            }
                        }
                    }
                }
            }
            return mc;
        }
        
        /**
   * @param rate
   *            the rate parameter for the bound on the resulting
   *            {@link MetricTreaty}
   * @param base
   *            the base parameter for the bound on the resulting
   *            {@link MetricTreaty}
   * @param time
   *            the startTime parameter of the bound on the resulting
   *            {@link MetricTreaty}
   * @return a {@link MetricTreaty} which enforces that the {@link Metric}
   *         satisfies a bound with the given parameters at the given
   *         time. If such a {@link MetricTreaty} already exists, it is
   *         returned, otherwise a new one is created and returned
   *         (unactivated).
   *
   */
        public fabric.worker.metrics.treaties.MetricTreaty getThresholdTreaty(
          double rate, double base, long time) {
            return fabric.metrics.Metric._Impl.static_getThresholdTreaty(
                                                 (fabric.metrics.Metric)
                                                   this.$getProxy(), rate, base,
                                                 time);
        }
        
        private static fabric.worker.metrics.treaties.MetricTreaty
          static_getThresholdTreaty(fabric.metrics.Metric tmp, double rate,
                                    double base, long time) {
            fabric.worker.metrics.treaties.MetricTreaty mc = null;
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                mc = tmp.createThresholdTreaty(rate, base, time);
            }
            else {
                {
                    fabric.worker.metrics.treaties.MetricTreaty mc$var138 = mc;
                    fabric.worker.transaction.TransactionManager $tm144 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled147 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff145 = 1;
                    boolean $doBackoff146 = true;
                    boolean $retry141 = true;
                    $label139: for (boolean $commit140 = false; !$commit140; ) {
                        if ($backoffEnabled147) {
                            if ($doBackoff146) {
                                if ($backoff145 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.sleep($backoff145);
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e142) {
                                            
                                        }
                                    }
                                }
                                if ($backoff145 < 5000) $backoff145 *= 2;
                            }
                            $doBackoff146 = $backoff145 <= 32 || !$doBackoff146;
                        }
                        $commit140 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            mc = tmp.createThresholdTreaty(rate, base, time);
                        }
                        catch (final fabric.worker.RetryException $e142) {
                            $commit140 = false;
                            continue $label139;
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e142) {
                            $commit140 = false;
                            fabric.common.TransactionID $currentTid143 =
                              $tm144.getCurrentTid();
                            if ($e142.tid.isDescendantOf($currentTid143))
                                continue $label139;
                            if ($currentTid143.parent != null) {
                                $retry141 = false;
                                throw $e142;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e142) {
                            $commit140 = false;
                            if ($tm144.checkForStaleObjects()) continue;
                            fabric.common.TransactionID $currentTid143 =
                              $tm144.getCurrentTid();
                            if ($e142.tid.isDescendantOf($currentTid143)) {
                                $retry141 = true;
                            }
                            else if ($currentTid143.parent != null) {
                                $retry141 = false;
                                throw $e142;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e142) {
                            $commit140 = false;
                            if ($tm144.checkForStaleObjects())
                                continue $label139;
                            $retry141 = false;
                            throw new fabric.worker.AbortException($e142);
                        }
                        finally {
                            if ($commit140) {
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e142) {
                                    $commit140 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e142) {
                                    $commit140 = false;
                                    fabric.common.TransactionID $currentTid143 =
                                      $tm144.getCurrentTid();
                                    if ($currentTid143 != null) {
                                        if ($e142.tid.equals($currentTid143) ||
                                              !$e142.tid.isDescendantOf(
                                                           $currentTid143)) {
                                            throw $e142;
                                        }
                                    }
                                }
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit140 && $retry141) {
                                { mc = mc$var138; }
                                continue $label139;
                            }
                        }
                    }
                }
            }
            return mc;
        }
        
        /**
   * @param rate
   *            the rate parameter for the bound on the resulting
   *            {@link MetricTreaty}
   * @param base
   *            the base parameter for the bound on the resulting
   *            {@link MetricTreaty}
   * @return a {@link MetricTreaty} which enforces that the {@link Metric}
   *         satisfies a bound with the given parameters at the
   *         current time. If such a {@link MetricTreaty} already exists, it
   *         is returned, otherwise a new one is created and returned
   *         (unactivated).
   */
        public fabric.worker.metrics.treaties.MetricTreaty getThresholdTreaty(
          double rate, double base) {
            return getThresholdTreaty(rate, base, 0);
        }
        
        /**
   * Create a locally tracked {@link DerivedMetric} that takes a scaled value
   * of another {@link Metric}.
   *
   * @param s
   *            the {@link Store} the returned {@link DerivedMetric} will be
   *            stored at
   * @param a
   *        the scaling factor
   * @param term
   *        the {@link Metric}
   * @return the locally tracked {@link DerivedMetric} for the scaled value of
   *       the given {@link Metric}.
   */
        public static fabric.metrics.Metric scaleAtStore(
          final fabric.worker.Store s, double a, fabric.metrics.Metric term) {
            if (term.$getStore().equals(s)) return term.times(a);
            fabric.metrics.DerivedMetric val = null;
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                val =
                  ((fabric.metrics.ScaledMetric)
                     new fabric.metrics.ScaledMetric._Impl(s).$getProxy()).
                    fabric$metrics$ScaledMetric$(a, term);
            }
            else {
                {
                    fabric.metrics.DerivedMetric val$var148 = val;
                    fabric.worker.transaction.TransactionManager $tm154 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled157 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff155 = 1;
                    boolean $doBackoff156 = true;
                    boolean $retry151 = true;
                    $label149: for (boolean $commit150 = false; !$commit150; ) {
                        if ($backoffEnabled157) {
                            if ($doBackoff156) {
                                if ($backoff155 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.sleep($backoff155);
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e152) {
                                            
                                        }
                                    }
                                }
                                if ($backoff155 < 5000) $backoff155 *= 2;
                            }
                            $doBackoff156 = $backoff155 <= 32 || !$doBackoff156;
                        }
                        $commit150 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            val =
                              ((fabric.metrics.ScaledMetric)
                                 new fabric.metrics.ScaledMetric._Impl(s).
                                 $getProxy()).fabric$metrics$ScaledMetric$(
                                                a, term);
                        }
                        catch (final fabric.worker.RetryException $e152) {
                            $commit150 = false;
                            continue $label149;
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e152) {
                            $commit150 = false;
                            fabric.common.TransactionID $currentTid153 =
                              $tm154.getCurrentTid();
                            if ($e152.tid.isDescendantOf($currentTid153))
                                continue $label149;
                            if ($currentTid153.parent != null) {
                                $retry151 = false;
                                throw $e152;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e152) {
                            $commit150 = false;
                            if ($tm154.checkForStaleObjects()) continue;
                            fabric.common.TransactionID $currentTid153 =
                              $tm154.getCurrentTid();
                            if ($e152.tid.isDescendantOf($currentTid153)) {
                                $retry151 = true;
                            }
                            else if ($currentTid153.parent != null) {
                                $retry151 = false;
                                throw $e152;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e152) {
                            $commit150 = false;
                            if ($tm154.checkForStaleObjects())
                                continue $label149;
                            $retry151 = false;
                            throw new fabric.worker.AbortException($e152);
                        }
                        finally {
                            if ($commit150) {
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e152) {
                                    $commit150 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e152) {
                                    $commit150 = false;
                                    fabric.common.TransactionID $currentTid153 =
                                      $tm154.getCurrentTid();
                                    if ($currentTid153 != null) {
                                        if ($e152.tid.equals($currentTid153) ||
                                              !$e152.tid.isDescendantOf(
                                                           $currentTid153)) {
                                            throw $e152;
                                        }
                                    }
                                }
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit150 && $retry151) {
                                { val = val$var148; }
                                continue $label149;
                            }
                        }
                    }
                }
            }
            return val;
        }
        
        /**
   * Create a locally tracked {@link DerivedMetric} that takes the sum of
   * other {@link Metric}s.
   *
   * @param s
   *            the {@link Store} the returned {@link DerivedMetric} will be
   *            stored at
   * @param terms
   *            the {@link Metric}s to sum
   * @return the locally tracked {@link DerivedMetric} for the sum of the
   *       terms.
   */
        public static fabric.metrics.Metric addAtStore(
          final fabric.worker.Store s, fabric.metrics.Metric[] terms) {
            if (terms.length == 0)
                throw new java.lang.IllegalArgumentException(
                        "Must have at least 1 term!");
            if (terms.length == 1)
                return fabric.metrics.Metric._Impl.scaleAtStore(s, 1.0,
                                                                terms[0]);
            fabric.metrics.DerivedMetric val = null;
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                val =
                  ((fabric.metrics.SumMetric)
                     new fabric.metrics.SumMetric._Impl(s).$getProxy()).
                    fabric$metrics$SumMetric$(terms);
            }
            else {
                {
                    fabric.metrics.DerivedMetric val$var158 = val;
                    fabric.worker.transaction.TransactionManager $tm164 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled167 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff165 = 1;
                    boolean $doBackoff166 = true;
                    boolean $retry161 = true;
                    $label159: for (boolean $commit160 = false; !$commit160; ) {
                        if ($backoffEnabled167) {
                            if ($doBackoff166) {
                                if ($backoff165 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.sleep($backoff165);
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e162) {
                                            
                                        }
                                    }
                                }
                                if ($backoff165 < 5000) $backoff165 *= 2;
                            }
                            $doBackoff166 = $backoff165 <= 32 || !$doBackoff166;
                        }
                        $commit160 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            val =
                              ((fabric.metrics.SumMetric)
                                 new fabric.metrics.SumMetric._Impl(s).
                                 $getProxy()).fabric$metrics$SumMetric$(terms);
                        }
                        catch (final fabric.worker.RetryException $e162) {
                            $commit160 = false;
                            continue $label159;
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e162) {
                            $commit160 = false;
                            fabric.common.TransactionID $currentTid163 =
                              $tm164.getCurrentTid();
                            if ($e162.tid.isDescendantOf($currentTid163))
                                continue $label159;
                            if ($currentTid163.parent != null) {
                                $retry161 = false;
                                throw $e162;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e162) {
                            $commit160 = false;
                            if ($tm164.checkForStaleObjects()) continue;
                            fabric.common.TransactionID $currentTid163 =
                              $tm164.getCurrentTid();
                            if ($e162.tid.isDescendantOf($currentTid163)) {
                                $retry161 = true;
                            }
                            else if ($currentTid163.parent != null) {
                                $retry161 = false;
                                throw $e162;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e162) {
                            $commit160 = false;
                            if ($tm164.checkForStaleObjects())
                                continue $label159;
                            $retry161 = false;
                            throw new fabric.worker.AbortException($e162);
                        }
                        finally {
                            if ($commit160) {
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e162) {
                                    $commit160 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e162) {
                                    $commit160 = false;
                                    fabric.common.TransactionID $currentTid163 =
                                      $tm164.getCurrentTid();
                                    if ($currentTid163 != null) {
                                        if ($e162.tid.equals($currentTid163) ||
                                              !$e162.tid.isDescendantOf(
                                                           $currentTid163)) {
                                            throw $e162;
                                        }
                                    }
                                }
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit160 && $retry161) {
                                { val = val$var158; }
                                continue $label159;
                            }
                        }
                    }
                }
            }
            return val;
        }
        
        /**
   * Create a locally tracked {@link DerivedMetric} that takes the minimum of
   * other {@link Metric}s.
   *
   * @param s
   *            the {@link Store} the returned {@link Metric} will be stored
   *            at
   * @param terms
   *        the {@link Metric}s to take the min of
   * @return the locally tracked {@link Metric} for the minimum of the terms.
   */
        public static fabric.metrics.Metric minAtStore(
          final fabric.worker.Store s, fabric.metrics.Metric[] terms) {
            if (terms.length == 0)
                throw new java.lang.IllegalArgumentException(
                        "Must have at least 1 term!");
            if (terms.length == 1)
                return fabric.metrics.Metric._Impl.scaleAtStore(s, 1.0,
                                                                terms[0]);
            fabric.metrics.DerivedMetric val = null;
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                val =
                  ((fabric.metrics.MinMetric)
                     new fabric.metrics.MinMetric._Impl(s).$getProxy()).
                    fabric$metrics$MinMetric$(terms);
            }
            else {
                {
                    fabric.metrics.DerivedMetric val$var168 = val;
                    fabric.worker.transaction.TransactionManager $tm174 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled177 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff175 = 1;
                    boolean $doBackoff176 = true;
                    boolean $retry171 = true;
                    $label169: for (boolean $commit170 = false; !$commit170; ) {
                        if ($backoffEnabled177) {
                            if ($doBackoff176) {
                                if ($backoff175 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.sleep($backoff175);
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e172) {
                                            
                                        }
                                    }
                                }
                                if ($backoff175 < 5000) $backoff175 *= 2;
                            }
                            $doBackoff176 = $backoff175 <= 32 || !$doBackoff176;
                        }
                        $commit170 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            val =
                              ((fabric.metrics.MinMetric)
                                 new fabric.metrics.MinMetric._Impl(s).
                                 $getProxy()).fabric$metrics$MinMetric$(terms);
                        }
                        catch (final fabric.worker.RetryException $e172) {
                            $commit170 = false;
                            continue $label169;
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e172) {
                            $commit170 = false;
                            fabric.common.TransactionID $currentTid173 =
                              $tm174.getCurrentTid();
                            if ($e172.tid.isDescendantOf($currentTid173))
                                continue $label169;
                            if ($currentTid173.parent != null) {
                                $retry171 = false;
                                throw $e172;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e172) {
                            $commit170 = false;
                            if ($tm174.checkForStaleObjects()) continue;
                            fabric.common.TransactionID $currentTid173 =
                              $tm174.getCurrentTid();
                            if ($e172.tid.isDescendantOf($currentTid173)) {
                                $retry171 = true;
                            }
                            else if ($currentTid173.parent != null) {
                                $retry171 = false;
                                throw $e172;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e172) {
                            $commit170 = false;
                            if ($tm174.checkForStaleObjects())
                                continue $label169;
                            $retry171 = false;
                            throw new fabric.worker.AbortException($e172);
                        }
                        finally {
                            if ($commit170) {
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e172) {
                                    $commit170 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e172) {
                                    $commit170 = false;
                                    fabric.common.TransactionID $currentTid173 =
                                      $tm174.getCurrentTid();
                                    if ($currentTid173 != null) {
                                        if ($e172.tid.equals($currentTid173) ||
                                              !$e172.tid.isDescendantOf(
                                                           $currentTid173)) {
                                            throw $e172;
                                        }
                                    }
                                }
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit170 && $retry171) {
                                { val = val$var168; }
                                continue $label169;
                            }
                        }
                    }
                }
            }
            return val;
        }
        
        /**
   * Create a locally tracked {@link DerivedMetric} that takes the maximum of
   * other {@link Metric}s.
   *
   * @param s
   *            the {@link Store} the returned {@link Metric} will be stored
   *            at
   * @param terms
   *        the {@link Metric}s to take the max of
   * @return the locally tracked {@link Metric} for the maximum of the terms.
   */
        public static fabric.metrics.Metric maxAtStore(
          final fabric.worker.Store s, fabric.metrics.Metric[] terms) {
            if (terms.length == 0)
                throw new java.lang.IllegalArgumentException(
                        "Must have at least 1 term!");
            if (terms.length == 1)
                return fabric.metrics.Metric._Impl.scaleAtStore(s, 1.0,
                                                                terms[0]);
            for (int i = 0; i < terms.length; i++) {
                terms[i] = terms[i].times(-1);
            }
            return fabric.metrics.Metric._Impl.minAtStore(s, terms).times(-1);
        }
        
        /**
   * Cache of DerivedMetrics using this Metric as a term (helps to break up
   * interning cache to avoid one big map.
   */
        public abstract boolean getUsePreset();
        
        public abstract double getPresetR();
        
        public abstract double getPresetB();
        
        public abstract double getPresetV();
        
        public abstract double getPresetN();
        
        public fabric.worker.metrics.proxies.ProxyMap get$proxies() {
            fabric.worker.transaction.TransactionManager.getInstance().
              registerRead(this);
            return this.proxies;
        }
        
        public fabric.worker.metrics.proxies.ProxyMap set$proxies(
          fabric.worker.metrics.proxies.ProxyMap val) {
            fabric.worker.transaction.TransactionManager tm =
              fabric.worker.transaction.TransactionManager.getInstance();
            boolean transactionCreated = tm.registerWrite(this);
            this.proxies = val;
            if (transactionCreated) tm.commitTransaction();
            return val;
        }
        
        protected fabric.worker.metrics.proxies.ProxyMap proxies;
        
        /**
   * @param store
   *            the Store that holds this {@link Metric}
   */
        public fabric.metrics.Metric fabric$metrics$Metric$() {
            fabric$metrics$util$AbstractSubject$();
            this.set$proxies(fabric.worker.metrics.proxies.ProxyMap.emptyMap());
            return (fabric.metrics.Metric) this.$getProxy();
        }
        
        /**
   * Get a proxy metric for this metric (returns this if the store is the same).
   */
        public fabric.metrics.Metric getProxy(fabric.worker.Store s) {
            if ($getStore().equals(s))
                return (fabric.metrics.Metric) this.$getProxy();
            if (this.get$proxies().containsKey(s))
                return this.get$proxies().get(s);
            fabric.metrics.Metric result =
              ((fabric.metrics.ProxyMetric)
                 new fabric.metrics.ProxyMetric._Impl(s).$getProxy()).
              fabric$metrics$ProxyMetric$((fabric.metrics.Metric)
                                            this.$getProxy());
            this.set$proxies(this.get$proxies().put(s, result));
            return result;
        }
        
        /**
   * @param weakStats
   *            StatsMap for mapping metrics to weakly consistent values to use
   *            for metric stats.
   * @return the current value of the {@link Metric}.
   */
        public abstract double value(fabric.worker.metrics.StatsMap weakStats);
        
        /** @return the number of samples of the {@link Metric}. */
        public long samples() {
            return samples(fabric.worker.metrics.StatsMap.emptyStats());
        }
        
        /**
   * @param weakStats
   *            StatsMap for mapping metrics to weakly consistent values to use
   *            for metric stats.
   * @return the number of samples of the {@link Metric}.
   */
        public abstract long samples(fabric.worker.metrics.StatsMap weakStats);
        
        /** @return the time of the last update of the {@link Metric}. */
        public long lastUpdate() {
            return lastUpdate(fabric.worker.metrics.StatsMap.emptyStats());
        }
        
        /**
   * @param weakStats
   *            StatsMap for mapping metrics to weakly consistent values to use
   *            for metric stats.
   * @return the time of the last update of the {@link Metric}.
   */
        public abstract long lastUpdate(fabric.worker.metrics.StatsMap weakStats);
        
        /** @return the current updateInterval estimate for updates of {@link Metric}. */
        public double updateInterval() {
            return updateInterval(fabric.worker.metrics.StatsMap.emptyStats());
        }
        
        /**
   * @param weakStats
   *            StatsMap for mapping metrics to weakly consistent values to use
   *            for metric stats.
   * @return the updateInterval estimated for updates of the {@link Metric}.
   */
        public abstract double updateInterval(
          fabric.worker.metrics.StatsMap weakStats);
        
        /** @return the estimated velocity of the {@link Metric}. */
        public double velocity() {
            return velocity(fabric.worker.metrics.StatsMap.emptyStats());
        }
        
        /**
   * @param weakStats
   *            StatsMap for mapping metrics to weakly consistent values to use
   *            for metric stats.
   * @return the estimated velocity of the {@link Metric}.
   */
        public abstract double velocity(
          fabric.worker.metrics.StatsMap weakStats);
        
        /** @return the estimated noise of the {@link Metric}. */
        public double noise() {
            return noise(fabric.worker.metrics.StatsMap.emptyStats());
        }
        
        /**
   * @param weakStats
   *            StatsMap for mapping metrics to weakly consistent values to use
   *            for metric stats.
   * @return the estimated velocity of the {@link Metric}.
   */
        public abstract double noise(fabric.worker.metrics.StatsMap weakStats);
        
        /**
   * Used to construct and enforce {@link MetricTreaty}s bounding this
   * {@link Metric}s value.
   * <p>
   * Implementations of this method should use "weak" estimates of value,
   * velocity, and noise to avoid contention issues. The internal
   * implementation is expected to have refreshed these estimates recently
   * prior to the call.
   *
   * @param value
   *            the value we're asserting equality with.
   * @param weakStats
   *            StatsMap for mapping metrics to weakly consistent values to use
   *            for metric stats.
   * @return a {@link EnforcementPolicy} that enforces this {@link Metric}
   *         being above bound.
   */
        public fabric.worker.metrics.treaties.enforcement.EnforcementPolicy
          equalityPolicy(double value, fabric.worker.metrics.StatsMap weakStats,
                         final fabric.worker.Store s) {
            return fabric.worker.metrics.treaties.enforcement.DirectPolicy.
                     singleton;
        }
        
        /**
   * Used to construct and enforce {@link MetricTreaty}s bounding this
   * {@link Metric}s value.
   * <p>
   * Implementations of this method should use "weak" estimates of value,
   * velocity, and noise to avoid contention issues. The internal
   * implementation is expected to have refreshed these estimates recently
   * prior to the call.
   *
   * @param value
   *            the value we're asserting equality with.
   * @return a {@link EnforcementPolicy} that enforces this {@link Metric}
   *         being above bound.
   */
        public fabric.worker.metrics.treaties.enforcement.EnforcementPolicy
          equalityPolicy(double value, final fabric.worker.Store s) {
            return equalityPolicy(value,
                                  fabric.worker.metrics.StatsMap.emptyStats(),
                                  s);
        }
        
        /**
   * Used to construct and enforce {@link MetricTreaty}s bounding this
   * {@link Metric}s value.
   * <p>
   * Implementations of this method should use "weak" estimates of value,
   * velocity, and noise to avoid contention issues. The internal
   * implementation is expected to have refreshed these estimates recently
   * prior to the call.
   *
   * @param bound
   *            a bound that the returned policy enforces.
   * @param weakStats
   *            StatsMap for mapping metrics to weakly consistent values to use
   *            for metric stats.
   * @return a {@link EnforcementPolicy} that enforces this {@link Metric}
   *         being above bound.
   */
        public fabric.worker.metrics.treaties.enforcement.EnforcementPolicy
          thresholdPolicy(double rate, double base,
                          fabric.worker.metrics.StatsMap weakStats,
                          final fabric.worker.Store s) {
            return fabric.worker.metrics.treaties.enforcement.DirectPolicy.
                     singleton;
        }
        
        /**
   * Used to construct and enforce {@link MetricTreaty}s bounding this
   * {@link Metric}s value.
   * <p>
   * Implementations of this method should use "weak" estimates of value,
   * velocity, and noise to avoid contention issues. The internal
   * implementation is expected to have refreshed these estimates recently
   * prior to the call.
   *
   * @param bound
   *            a bound that the returned policy enforces.
   * @return a {@link EnforcementPolicy} that enforces this {@link Metric}
   *         being above bound.
   */
        public fabric.worker.metrics.treaties.enforcement.EnforcementPolicy
          thresholdPolicy(double rate, double base,
                          final fabric.worker.Store s) {
            return thresholdPolicy(rate, base,
                                   fabric.worker.metrics.StatsMap.emptyStats(),
                                   s);
        }
        
        /**
   * Update the current "weak" estimates to be used by {@link #policy(Bound)}
   * @param weakStats
   *            StatsMap for mapping metrics to weakly consistent values to use
   *            for metric stats.
   */
        public fabric.worker.metrics.StatsMap refreshWeakEstimates(
          fabric.worker.metrics.StatsMap weakStats) {
            return refreshLocally(weakStats);
        }
        
        public fabric.worker.metrics.StatsMap refreshWeakEstimates_remote(
          fabric.lang.security.Principal caller,
          fabric.worker.metrics.StatsMap weakStats) {
            return refreshWeakEstimates(weakStats);
        }
        
        /**
   * Utility to allow for running updates as close to the metric as possible.
   * @param weakStats
   *            StatsMap for mapping metrics to weakly consistent values to use
   *            for metric stats.
   *
   * Ugh, public because we don't allow remote calls for non public methods.
   */
        public fabric.worker.metrics.StatsMap refreshLocally(
          fabric.worker.metrics.StatsMap weakStats) {
            return fabric.metrics.Metric._Impl.static_refreshLocally(
                                                 (fabric.metrics.Metric)
                                                   this.$getProxy(), weakStats);
        }
        
        private static fabric.worker.metrics.StatsMap static_refreshLocally(
          fabric.metrics.Metric tmp, fabric.worker.metrics.StatsMap weakStats) {
            fabric.common.Logging.METRICS_LOGGER.
              finest(
                "REFRESHING LOCALLY " +
                  java.lang.String.valueOf(
                                     fabric.lang.WrappedJavaInlineable.$unwrap(
                                                                         tmp)) +
                  " IN " +
                  fabric.worker.transaction.TransactionManager.getInstance().
                    getCurrentLog() +
                  " " +
                  java.lang.Thread.currentThread());
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                return weakStats.put(tmp, tmp.computeValue(weakStats),
                                     tmp.computeSamples(weakStats),
                                     tmp.computeLastUpdate(weakStats),
                                     tmp.computeUpdateInterval(weakStats),
                                     tmp.computeVelocity(weakStats),
                                     tmp.computeNoise(weakStats));
            }
            else {
                fabric.worker.metrics.StatsMap result = null;
                {
                    fabric.worker.metrics.StatsMap result$var178 = result;
                    fabric.worker.transaction.TransactionManager $tm184 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled187 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff185 = 1;
                    boolean $doBackoff186 = true;
                    boolean $retry181 = true;
                    $label179: for (boolean $commit180 = false; !$commit180; ) {
                        if ($backoffEnabled187) {
                            if ($doBackoff186) {
                                if ($backoff185 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.sleep($backoff185);
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e182) {
                                            
                                        }
                                    }
                                }
                                if ($backoff185 < 5000) $backoff185 *= 2;
                            }
                            $doBackoff186 = $backoff185 <= 32 || !$doBackoff186;
                        }
                        $commit180 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            result =
                              weakStats.put(
                                          tmp, tmp.computeValue(weakStats),
                                          tmp.computeSamples(weakStats),
                                          tmp.computeLastUpdate(weakStats),
                                          tmp.computeUpdateInterval(weakStats),
                                          tmp.computeVelocity(weakStats),
                                          tmp.computeNoise(weakStats));
                        }
                        catch (final fabric.worker.RetryException $e182) {
                            $commit180 = false;
                            continue $label179;
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e182) {
                            $commit180 = false;
                            fabric.common.TransactionID $currentTid183 =
                              $tm184.getCurrentTid();
                            if ($e182.tid.isDescendantOf($currentTid183))
                                continue $label179;
                            if ($currentTid183.parent != null) {
                                $retry181 = false;
                                throw $e182;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e182) {
                            $commit180 = false;
                            if ($tm184.checkForStaleObjects()) continue;
                            fabric.common.TransactionID $currentTid183 =
                              $tm184.getCurrentTid();
                            if ($e182.tid.isDescendantOf($currentTid183)) {
                                $retry181 = true;
                            }
                            else if ($currentTid183.parent != null) {
                                $retry181 = false;
                                throw $e182;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e182) {
                            $commit180 = false;
                            if ($tm184.checkForStaleObjects())
                                continue $label179;
                            $retry181 = false;
                            throw new fabric.worker.AbortException($e182);
                        }
                        finally {
                            if ($commit180) {
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e182) {
                                    $commit180 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e182) {
                                    $commit180 = false;
                                    fabric.common.TransactionID $currentTid183 =
                                      $tm184.getCurrentTid();
                                    if ($currentTid183 != null) {
                                        if ($e182.tid.equals($currentTid183) ||
                                              !$e182.tid.isDescendantOf(
                                                           $currentTid183)) {
                                            throw $e182;
                                        }
                                    }
                                }
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit180 && $retry181) {
                                { result = result$var178; }
                                continue $label179;
                            }
                        }
                    }
                }
                return result;
            }
        }
        
        public fabric.worker.metrics.StatsMap refreshLocally_remote(
          fabric.lang.security.Principal caller,
          fabric.worker.metrics.StatsMap weakStats) {
            return refreshLocally(weakStats);
        }
        
        /**
   * @param weakStats
   *            StatsMap for mapping metrics to weakly consistent values to use
   *            for metric stats.
   * @return a freshly computed weak value for this {@link DerivedMetric}.
   */
        public abstract double computeValue(fabric.worker.metrics.StatsMap weakStats);
        
        /**
   * @param weakStats
   *            StatsMap for mapping metrics to weakly consistent values to use
   *            for metric stats.
   * @return a freshly computed weak value for this {@link DerivedMetric}.
   */
        public abstract long computeSamples(fabric.worker.metrics.StatsMap weakStats);
        
        /**
   * @param weakStats
   *            StatsMap for mapping metrics to weakly consistent values to use
   *            for metric stats.
   * @return a freshly computed weak value for this {@link DerivedMetric}.
   */
        public abstract long computeLastUpdate(fabric.worker.metrics.StatsMap weakStats);
        
        /**
   * @param weakStats
   *            StatsMap for mapping metrics to weakly consistent values to use
   *            for metric stats.
   * @return a freshly computed weak value for this {@link DerivedMetric}.
   */
        public abstract double computeUpdateInterval(fabric.worker.metrics.StatsMap weakStats);
        
        /**
   * @param weakStats
   *            StatsMap for mapping metrics to weakly consistent values to use
   *            for metric stats.
   * @return a freshly computed weak velocity for this {@link DerivedMetric}.
   */
        public abstract double computeVelocity(fabric.worker.metrics.StatsMap weakStats);
        
        /**
   * @param weakStats
   *            StatsMap for mapping metrics to weakly consistent values to use
   *            for metric stats.
   * @return a freshly computed noise for this {@link DerivedMetric}.
   */
        public abstract double computeNoise(fabric.worker.metrics.StatsMap weakStats);
        
        /**
   * @return true iff all the sampling and transformations on this metric are
   *         stored on a single store.
   */
        public abstract boolean isSingleStore();
        
        /**
   * @param bound
   *        the bound that the treaty will enforce on this
   *        {@link Metric}
   * @return a {@link MetricTreaty} asserting this metric satisfies the
   *       given bound.
   */
        public fabric.worker.metrics.treaties.MetricTreaty
          createThresholdTreaty(double rate, double base, long time) {
            return this.get$$treaties().
              create(
                new fabric.worker.metrics.treaties.statements.ThresholdStatement(
                  rate, base, time));
        }
        
        /**
   * @param bound
   *        the bound that the treaty will enforce on this
   *        {@link Metric}
   * @return a {@link MetricTreaty} asserting this metric satisfies the
   *       given bound.
   */
        public fabric.worker.metrics.treaties.MetricTreaty createEqualityTreaty(
          double value) {
            return this.get$$treaties().
              create(
                new fabric.worker.metrics.treaties.statements.EqualityStatement(
                  value));
        }
        
        public int compareTo(java.lang.Object that) {
            if (!(fabric.lang.Object._Proxy.
                    $getProxy(that) instanceof fabric.metrics.Metric)) return 0;
            fabric.metrics.Metric
              other =
              (fabric.metrics.Metric)
                fabric.lang.Object._Proxy.
                $getProxy(fabric.lang.WrappedJavaInlineable.$wrap(that));
            int thisHash = hashCode();
            int thatHash =
              ((java.lang.Comparable)
                 fabric.lang.WrappedJavaInlineable.$unwrap(other)).hashCode();
            if (thisHash == thatHash) {
                if (other.equals((fabric.metrics.Metric) this.$getProxy())) {
                    return 0;
                }
                else {
                    return toString().
                      compareTo(
                        ((java.lang.Comparable)
                           fabric.lang.WrappedJavaInlineable.$unwrap(other)).
                            toString());
                }
            }
            return thisHash - thatHash;
        }
        
        public fabric.worker.metrics.ImmutableObserverSet handleUpdates(
          boolean includesObserver, fabric.common.util.LongSet treaties) {
            fabric.worker.metrics.ImmutableObserverSet affected =
              fabric.worker.metrics.ImmutableObserverSet.emptySet();
            if (includesObserver) {
                affected = affected.addAll(handleDirectUpdates());
            }
            for (java.util.Iterator iter = this.get$$treaties().iterator();
                 iter.hasNext(); ) {
                fabric.worker.metrics.treaties.MetricTreaty treaty =
                  (fabric.worker.metrics.treaties.MetricTreaty) iter.next();
                if (treaties.contains(treaty.getId()) ||
                      affected.contains((fabric.metrics.Metric)
                                          this.$getProxy(), treaty.getId())) {
                    fabric.common.util.Pair treatyUpdate =
                      treaty.update(
                               false,
                               fabric.worker.metrics.StatsMap.emptyStats());
                    affected =
                      affected.addAll(
                                 (fabric.worker.metrics.ImmutableObserverSet)
                                   treatyUpdate.second);
                    affected = affected.remove((fabric.metrics.Metric)
                                                 this.$getProxy(),
                                               treaty.getId());
                }
            }
            return affected;
        }
        
        /**
   * Handle an update where the metric itself is observing.
   */
        public abstract fabric.worker.metrics.ImmutableObserverSet
          handleDirectUpdates();
        
        public _Impl(fabric.worker.Store $location) { super($location); }
        
        protected fabric.lang.Object._Proxy $makeProxy() {
            return new fabric.metrics.Metric._Proxy(this);
        }
        
        public void $serialize(java.io.ObjectOutput out,
                               java.util.List refTypes,
                               java.util.List intraStoreRefs,
                               java.util.List interStoreRefs)
              throws java.io.IOException {
            super.$serialize(out, refTypes, intraStoreRefs, interStoreRefs);
            $writeInline(out, this.proxies);
        }
        
        public _Impl(fabric.worker.Store store, long onum, int version,
                     fabric.worker.metrics.ImmutableObserverSet observers,
                     fabric.worker.Store labelStore, long labelOnum,
                     fabric.worker.Store accessPolicyStore,
                     long accessPolicyOnum, java.io.ObjectInput in,
                     java.util.Iterator refTypes,
                     java.util.Iterator intraStoreRefs,
                     java.util.Iterator interStoreRefs)
              throws java.io.IOException,
            java.lang.ClassNotFoundException {
            super(store, onum, version, observers, labelStore, labelOnum,
                  accessPolicyStore, accessPolicyOnum, in, refTypes,
                  intraStoreRefs, interStoreRefs);
            this.proxies = (fabric.worker.metrics.proxies.ProxyMap)
                             in.readObject();
        }
        
        public void $copyAppStateFrom(fabric.lang.Object._Impl other) {
            super.$copyAppStateFrom(other);
            fabric.metrics.Metric._Impl src = (fabric.metrics.Metric._Impl)
                                                other;
            this.proxies = src.proxies;
        }
    }
    
    interface _Static extends fabric.lang.Object, Cloneable {
        final class _Proxy extends fabric.lang.Object._Proxy
          implements fabric.metrics.Metric._Static {
            public _Proxy(fabric.metrics.Metric._Static._Impl impl) {
                super(impl);
            }
            
            public _Proxy(fabric.worker.Store store, long onum) {
                super(store, onum);
            }
            
            public static final fabric.metrics.Metric._Static $instance;
            
            static {
                fabric.
                  metrics.
                  Metric.
                  _Static.
                  _Impl
                  impl =
                  (fabric.metrics.Metric._Static._Impl)
                    fabric.lang.Object._Static._Proxy.
                    $makeStaticInstance(
                      fabric.metrics.Metric._Static._Impl.class);
                $instance = (fabric.metrics.Metric._Static) impl.$getProxy();
                impl.$init();
            }
        }
        
        class _Impl extends fabric.lang.Object._Impl
          implements fabric.metrics.Metric._Static {
            public void $serialize(java.io.ObjectOutput out,
                                   java.util.List refTypes,
                                   java.util.List intraStoreRefs,
                                   java.util.List interStoreRefs)
                  throws java.io.IOException {
                super.$serialize(out, refTypes, intraStoreRefs, interStoreRefs);
            }
            
            public _Impl(fabric.worker.Store store, long onum, int version,
                         fabric.worker.metrics.ImmutableObserverSet observers,
                         fabric.worker.Store labelStore, long labelOnum,
                         fabric.worker.Store accessPolicyStore,
                         long accessPolicyOnum, java.io.ObjectInput in,
                         java.util.Iterator refTypes,
                         java.util.Iterator intraStoreRefs,
                         java.util.Iterator interStoreRefs)
                  throws java.io.IOException,
                java.lang.ClassNotFoundException {
                super(store, onum, version, observers, labelStore, labelOnum,
                      accessPolicyStore, accessPolicyOnum, in, refTypes,
                      intraStoreRefs, interStoreRefs);
            }
            
            public _Impl(fabric.worker.Store store) { super(store); }
            
            protected fabric.lang.Object._Proxy $makeProxy() {
                return new fabric.metrics.Metric._Static._Proxy(this);
            }
            
            private void $init() {  }
        }
        
    }
    
    public static final byte[] $classHash = new byte[] { -91, 91, 18, 110, -78,
    77, 38, -8, -17, -102, 107, -35, -64, -80, -101, -42, 123, -14, 66, -81, -2,
    -52, 58, 57, -68, -124, 112, 42, 65, -20, -58, -108 };
    public static final java.lang.String jlc$CompilerVersion$fabil = "0.3.0";
    public static final long jlc$SourceLastModified$fabil = 1527882698000L;
    public static final java.lang.String jlc$ClassType$fabil =
      "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";
}
