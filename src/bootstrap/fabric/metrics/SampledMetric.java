package fabric.metrics;

import fabric.lang.*;
import fabric.lang.security.*;
import fabric.worker.*;
import fabric.worker.remote.*;
import java.lang.*;
import fabric.util.ArrayList;
import fabric.util.List;
import fabric.metrics.util.Observer;
import fabric.worker.metrics.RunningMetricStats;
import fabric.common.ConfigProperties;
import fabric.common.exceptions.InternalError;
import fabric.worker.Worker;
import fabric.worker.Store;
import fabric.worker.transaction.TransactionManager;
import java.util.logging.Level;
import fabric.common.Logging;

/**
 * A {@link Metric} that is directly updated by applications with new sample
 * values to use for its value.
 * <p>
 * In a transaction which has written the value being measured,
 * {@link #takeSample(double,long)} must be called to ensure that
 * {@link Observer}s of this {@link Metric} are aware of the change and the
 * transaction context performs the proper book-keeping.
 */
public interface SampledMetric extends fabric.metrics.Metric {
    public java.lang.String get$name();
    
    public java.lang.String set$name(java.lang.String val);
    
    /**
   * @param store
   *            the {@link Store} that holds this {@link Metric}
   * @param name
   *            the name the {@link Store} associates with this
   *            {@link SampledMetric}, so it can be easily retrieved later
   * @param init
   *        the initial value of this {@link Metric}
   */
    public fabric.metrics.SampledMetric fabric$metrics$SampledMetric$(
      java.lang.String name, double init);
    
    public boolean get$usePreset();
    
    public boolean set$usePreset(boolean val);
    
    public boolean getUsePreset();
    
    public double get$presetR();
    
    public double set$presetR(double val);
    
    public double postInc$presetR();
    
    public double postDec$presetR();
    
    public double getPresetR();
    
    public double get$presetB();
    
    public double set$presetB(double val);
    
    public double postInc$presetB();
    
    public double postDec$presetB();
    
    public double getPresetB();
    
    public double get$presetV();
    
    public double set$presetV(double val);
    
    public double postInc$presetV();
    
    public double postDec$presetV();
    
    public double getPresetV();
    
    public double get$presetN();
    
    public double set$presetN(double val);
    
    public double postInc$presetN();
    
    public double postDec$presetN();
    
    public double getPresetN();
    
    /**
   * Update to a new value of the metric. The sample is assumed to be taken at
   * the time of the call.
   *
   * @param sample
   *        the value the metric is updating to.
   */
    public void takeSample(double sample);
    
    /**
   * Alias for takeSample.
   */
    public void setValue(double value);
    
    /**
   * Update to a new value of the metric.
   *
   * @param sample
   *        the value the metric is updating to.
   * @param time
   *        the time the sample occurred.
   */
    public void takeSample(double sample, long time);
    
    public double computeValue(boolean useWeakCache);
    
    public long computeSamples(boolean useWeakCache);
    
    public long computeLastUpdate(boolean useWeakCache);
    
    public double computeUpdateInterval(boolean useWeakCache);
    
    public double computeVelocity(boolean useWeakCache);
    
    public double computeNoise(boolean useWeakCache);
    
    public java.lang.String toString();
    
    public boolean isSingleStore();
    
    public fabric.worker.metrics.RunningMetricStats get$stats();
    
    public fabric.worker.metrics.RunningMetricStats set$stats(fabric.worker.metrics.RunningMetricStats val);
    
    /**
   * Updates the velocity and interval estimates with the given observation.
   *
   * @param newVal
   *        the new value of the measured quantity
   * @param eventTime
   *        the time, in milliseconds, this update happened
   */
    public void updateEstimates(double newVal, long eventTime);
    
    public double value(boolean useWeakCache);
    
    public long samples(boolean useWeakCache);
    
    public long lastUpdate(boolean useWeakCache);
    
    public double updateInterval(boolean useWeakCache);
    
    public double velocity(boolean useWeakCache);
    
    public double noise(boolean useWeakCache);
    
    public static class _Proxy extends fabric.metrics.Metric._Proxy
      implements fabric.metrics.SampledMetric {
        public java.lang.String get$name() {
            return ((fabric.metrics.SampledMetric._Impl) fetch()).get$name();
        }
        
        public java.lang.String set$name(java.lang.String val) {
            return ((fabric.metrics.SampledMetric._Impl) fetch()).set$name(val);
        }
        
        public boolean get$usePreset() {
            return ((fabric.metrics.SampledMetric._Impl) fetch()).get$usePreset(
                                                                    );
        }
        
        public boolean set$usePreset(boolean val) {
            return ((fabric.metrics.SampledMetric._Impl) fetch()).set$usePreset(
                                                                    val);
        }
        
        public double get$presetR() {
            return ((fabric.metrics.SampledMetric._Impl) fetch()).get$presetR();
        }
        
        public double set$presetR(double val) {
            return ((fabric.metrics.SampledMetric._Impl) fetch()).set$presetR(
                                                                    val);
        }
        
        public double postInc$presetR() {
            return ((fabric.metrics.SampledMetric._Impl) fetch()).
              postInc$presetR();
        }
        
        public double postDec$presetR() {
            return ((fabric.metrics.SampledMetric._Impl) fetch()).
              postDec$presetR();
        }
        
        public double get$presetB() {
            return ((fabric.metrics.SampledMetric._Impl) fetch()).get$presetB();
        }
        
        public double set$presetB(double val) {
            return ((fabric.metrics.SampledMetric._Impl) fetch()).set$presetB(
                                                                    val);
        }
        
        public double postInc$presetB() {
            return ((fabric.metrics.SampledMetric._Impl) fetch()).
              postInc$presetB();
        }
        
        public double postDec$presetB() {
            return ((fabric.metrics.SampledMetric._Impl) fetch()).
              postDec$presetB();
        }
        
        public double get$presetV() {
            return ((fabric.metrics.SampledMetric._Impl) fetch()).get$presetV();
        }
        
        public double set$presetV(double val) {
            return ((fabric.metrics.SampledMetric._Impl) fetch()).set$presetV(
                                                                    val);
        }
        
        public double postInc$presetV() {
            return ((fabric.metrics.SampledMetric._Impl) fetch()).
              postInc$presetV();
        }
        
        public double postDec$presetV() {
            return ((fabric.metrics.SampledMetric._Impl) fetch()).
              postDec$presetV();
        }
        
        public double get$presetN() {
            return ((fabric.metrics.SampledMetric._Impl) fetch()).get$presetN();
        }
        
        public double set$presetN(double val) {
            return ((fabric.metrics.SampledMetric._Impl) fetch()).set$presetN(
                                                                    val);
        }
        
        public double postInc$presetN() {
            return ((fabric.metrics.SampledMetric._Impl) fetch()).
              postInc$presetN();
        }
        
        public double postDec$presetN() {
            return ((fabric.metrics.SampledMetric._Impl) fetch()).
              postDec$presetN();
        }
        
        public fabric.worker.metrics.RunningMetricStats get$stats() {
            return ((fabric.metrics.SampledMetric._Impl) fetch()).get$stats();
        }
        
        public fabric.worker.metrics.RunningMetricStats set$stats(
          fabric.worker.metrics.RunningMetricStats val) {
            return ((fabric.metrics.SampledMetric._Impl) fetch()).set$stats(
                                                                    val);
        }
        
        public fabric.metrics.SampledMetric fabric$metrics$SampledMetric$(
          java.lang.String arg1, double arg2) {
            return ((fabric.metrics.SampledMetric) fetch()).
              fabric$metrics$SampledMetric$(arg1, arg2);
        }
        
        public void takeSample(double arg1) {
            ((fabric.metrics.SampledMetric) fetch()).takeSample(arg1);
        }
        
        public void setValue(double arg1) {
            ((fabric.metrics.SampledMetric) fetch()).setValue(arg1);
        }
        
        public void takeSample(double arg1, long arg2) {
            ((fabric.metrics.SampledMetric) fetch()).takeSample(arg1, arg2);
        }
        
        public void updateEstimates(double arg1, long arg2) {
            ((fabric.metrics.SampledMetric) fetch()).updateEstimates(arg1,
                                                                     arg2);
        }
        
        public _Proxy(SampledMetric._Impl impl) { super(impl); }
        
        public _Proxy(fabric.worker.Store store, long onum) {
            super(store, onum);
        }
    }
    
    public static class _Impl extends fabric.metrics.Metric._Impl
      implements fabric.metrics.SampledMetric {
        public java.lang.String get$name() { return this.name; }
        
        public java.lang.String set$name(java.lang.String val) {
            fabric.worker.transaction.TransactionManager tm =
              fabric.worker.transaction.TransactionManager.getInstance();
            boolean transactionCreated = tm.registerWrite(this);
            this.name = val;
            if (transactionCreated) tm.commitTransaction();
            return val;
        }
        
        private java.lang.String name;
        
        /**
   * @param store
   *            the {@link Store} that holds this {@link Metric}
   * @param name
   *            the name the {@link Store} associates with this
   *            {@link SampledMetric}, so it can be easily retrieved later
   * @param init
   *        the initial value of this {@link Metric}
   */
        public fabric.metrics.SampledMetric fabric$metrics$SampledMetric$(
          java.lang.String name, double init) {
            fabric.lang.security.Label lbl =
              fabric.lang.security.LabelUtil._Impl.noComponents();
            fabric.worker.Store s = $getStore();
            this.set$name(name);
            this.
              set$stats(
                fabric.worker.metrics.RunningMetricStats.
                    createRunningMetricStats(init, 0, 1));
            fabric.common.ConfigProperties config =
              fabric.worker.Worker.getWorker().config;
            this.set$usePreset(config.usePreset);
            if (this.get$usePreset()) {
                java.lang.String key = name + "@" + s.name();
                this.set$presetR((double)
                                   (config.rates.containsKey(key)
                                      ? ((java.lang.Double)
                                           config.rates.get(key)).doubleValue()
                                      : 0.0));
                this.set$presetB((double)
                                   (config.bounds.containsKey(key)
                                      ? ((java.lang.Double)
                                           config.bounds.get(key)).doubleValue()
                                      : 1.0));
                this.set$presetV(
                       (double)
                         (config.velocities.containsKey(key)
                            ? ((java.lang.Double)
                                 config.velocities.get(key)).doubleValue()
                            : 0.0));
                this.set$presetN((double)
                                   (config.noises.containsKey(key)
                                      ? ((java.lang.Double)
                                           config.noises.get(key)).doubleValue()
                                      : 0.0));
            } else {
                this.set$presetR((double) 0.0);
                this.set$presetB((double) 0.0);
                this.set$presetV((double) 0.0);
                this.set$presetN((double) 0.0);
            }
            fabric$metrics$Metric$();
            return (fabric.metrics.SampledMetric) this.$getProxy();
        }
        
        public boolean get$usePreset() { return this.usePreset; }
        
        public boolean set$usePreset(boolean val) {
            fabric.worker.transaction.TransactionManager tm =
              fabric.worker.transaction.TransactionManager.getInstance();
            boolean transactionCreated = tm.registerWrite(this);
            this.usePreset = val;
            if (transactionCreated) tm.commitTransaction();
            return val;
        }
        
        public boolean usePreset;
        
        public boolean getUsePreset() { return this.get$usePreset(); }
        
        public double get$presetR() { return this.presetR; }
        
        public double set$presetR(double val) {
            fabric.worker.transaction.TransactionManager tm =
              fabric.worker.transaction.TransactionManager.getInstance();
            boolean transactionCreated = tm.registerWrite(this);
            this.presetR = val;
            if (transactionCreated) tm.commitTransaction();
            return val;
        }
        
        public double postInc$presetR() {
            double tmp = this.get$presetR();
            this.set$presetR((double) (tmp + 1));
            return tmp;
        }
        
        public double postDec$presetR() {
            double tmp = this.get$presetR();
            this.set$presetR((double) (tmp - 1));
            return tmp;
        }
        
        public double presetR;
        
        public double getPresetR() { return this.get$presetR(); }
        
        public double get$presetB() { return this.presetB; }
        
        public double set$presetB(double val) {
            fabric.worker.transaction.TransactionManager tm =
              fabric.worker.transaction.TransactionManager.getInstance();
            boolean transactionCreated = tm.registerWrite(this);
            this.presetB = val;
            if (transactionCreated) tm.commitTransaction();
            return val;
        }
        
        public double postInc$presetB() {
            double tmp = this.get$presetB();
            this.set$presetB((double) (tmp + 1));
            return tmp;
        }
        
        public double postDec$presetB() {
            double tmp = this.get$presetB();
            this.set$presetB((double) (tmp - 1));
            return tmp;
        }
        
        public double presetB;
        
        public double getPresetB() { return this.get$presetB(); }
        
        public double get$presetV() { return this.presetV; }
        
        public double set$presetV(double val) {
            fabric.worker.transaction.TransactionManager tm =
              fabric.worker.transaction.TransactionManager.getInstance();
            boolean transactionCreated = tm.registerWrite(this);
            this.presetV = val;
            if (transactionCreated) tm.commitTransaction();
            return val;
        }
        
        public double postInc$presetV() {
            double tmp = this.get$presetV();
            this.set$presetV((double) (tmp + 1));
            return tmp;
        }
        
        public double postDec$presetV() {
            double tmp = this.get$presetV();
            this.set$presetV((double) (tmp - 1));
            return tmp;
        }
        
        public double presetV;
        
        public double getPresetV() { return this.get$presetV(); }
        
        public double get$presetN() { return this.presetN; }
        
        public double set$presetN(double val) {
            fabric.worker.transaction.TransactionManager tm =
              fabric.worker.transaction.TransactionManager.getInstance();
            boolean transactionCreated = tm.registerWrite(this);
            this.presetN = val;
            if (transactionCreated) tm.commitTransaction();
            return val;
        }
        
        public double postInc$presetN() {
            double tmp = this.get$presetN();
            this.set$presetN((double) (tmp + 1));
            return tmp;
        }
        
        public double postDec$presetN() {
            double tmp = this.get$presetN();
            this.set$presetN((double) (tmp - 1));
            return tmp;
        }
        
        public double presetN;
        
        public double getPresetN() { return this.get$presetN(); }
        
        /**
   * Update to a new value of the metric. The sample is assumed to be taken at
   * the time of the call.
   *
   * @param sample
   *        the value the metric is updating to.
   */
        public void takeSample(double sample) {
            takeSample(sample, java.lang.System.currentTimeMillis());
        }
        
        /**
   * Alias for takeSample.
   */
        public void setValue(double value) { takeSample(value); }
        
        /**
   * Update to a new value of the metric.
   *
   * @param sample
   *        the value the metric is updating to.
   * @param time
   *        the time the sample occurred.
   */
        public void takeSample(double sample, long time) {
            if (this.get$stats().getValue() != sample) {
                updateEstimates(sample, time);
                fabric.worker.transaction.TransactionManager.getInstance().
                  registerSample((fabric.metrics.SampledMetric)
                                   this.$getProxy());
            }
        }
        
        public double computeValue(boolean useWeakCache) {
            return fabric.metrics.SampledMetric._Impl.
              static_computeValue((fabric.metrics.SampledMetric)
                                    this.$getProxy(), useWeakCache);
        }
        
        private static double static_computeValue(
          fabric.metrics.SampledMetric tmp, boolean useWeakCache) {
            double result = 0;
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                tmp.get$stats().preload(tmp.get$name());
                result = tmp.get$stats().getValue();
            }
            else {
                {
                    double result$var238 = result;
                    fabric.worker.transaction.TransactionManager $tm244 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled247 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff245 = 1;
                    boolean $doBackoff246 = true;
                    boolean $retry241 = true;
                    $label239: for (boolean $commit240 = false; !$commit240; ) {
                        if ($backoffEnabled247) {
                            if ($doBackoff246) {
                                if ($backoff245 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.sleep($backoff245);
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e242) {
                                            
                                        }
                                    }
                                }
                                if ($backoff245 < 5000) $backoff245 *= 2;
                            }
                            $doBackoff246 = $backoff245 <= 32 || !$doBackoff246;
                        }
                        $commit240 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            tmp.get$stats().preload(tmp.get$name());
                            result = tmp.get$stats().getValue();
                        }
                        catch (final fabric.worker.RetryException $e242) {
                            $commit240 = false;
                            continue $label239;
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e242) {
                            $commit240 = false;
                            fabric.common.TransactionID $currentTid243 =
                              $tm244.getCurrentTid();
                            if ($e242.tid.isDescendantOf($currentTid243))
                                continue $label239;
                            if ($currentTid243.parent != null) {
                                $retry241 = false;
                                throw $e242;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e242) {
                            $commit240 = false;
                            if ($tm244.checkForStaleObjects()) continue;
                            fabric.common.TransactionID $currentTid243 =
                              $tm244.getCurrentTid();
                            if ($e242.tid.isDescendantOf($currentTid243)) {
                                $retry241 = true;
                            }
                            else if ($currentTid243.parent != null) {
                                $retry241 = false;
                                throw $e242;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e242) {
                            $commit240 = false;
                            if ($tm244.checkForStaleObjects())
                                continue $label239;
                            $retry241 = false;
                            throw new fabric.worker.AbortException($e242);
                        }
                        finally {
                            if ($commit240) {
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e242) {
                                    $commit240 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e242) {
                                    $commit240 = false;
                                    fabric.common.TransactionID $currentTid243 =
                                      $tm244.getCurrentTid();
                                    if ($currentTid243 != null) {
                                        if ($e242.tid.equals($currentTid243) ||
                                              !$e242.tid.isDescendantOf(
                                                           $currentTid243)) {
                                            throw $e242;
                                        }
                                    }
                                }
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit240 && $retry241) {
                                { result = result$var238; }
                                continue $label239;
                            }
                        }
                    }
                }
            }
            return result;
        }
        
        public long computeSamples(boolean useWeakCache) {
            return fabric.metrics.SampledMetric._Impl.
              static_computeSamples((fabric.metrics.SampledMetric)
                                      this.$getProxy(), useWeakCache);
        }
        
        private static long static_computeSamples(
          fabric.metrics.SampledMetric tmp, boolean useWeakCache) {
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                if (tmp.get$usePreset()) {
                    return 0;
                } else {
                    tmp.get$stats().preload(tmp.get$name());
                    return tmp.get$stats().getSamples();
                }
            }
            else {
                long rtn = 0;
                {
                    long rtn$var248 = rtn;
                    fabric.worker.transaction.TransactionManager $tm254 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled257 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff255 = 1;
                    boolean $doBackoff256 = true;
                    boolean $retry251 = true;
                    $label249: for (boolean $commit250 = false; !$commit250; ) {
                        if ($backoffEnabled257) {
                            if ($doBackoff256) {
                                if ($backoff255 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.sleep($backoff255);
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e252) {
                                            
                                        }
                                    }
                                }
                                if ($backoff255 < 5000) $backoff255 *= 2;
                            }
                            $doBackoff256 = $backoff255 <= 32 || !$doBackoff256;
                        }
                        $commit250 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            if (tmp.get$usePreset()) {
                                rtn = 0;
                            } else {
                                tmp.get$stats().preload(tmp.get$name());
                                rtn = tmp.get$stats().getSamples();
                            }
                        }
                        catch (final fabric.worker.RetryException $e252) {
                            $commit250 = false;
                            continue $label249;
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e252) {
                            $commit250 = false;
                            fabric.common.TransactionID $currentTid253 =
                              $tm254.getCurrentTid();
                            if ($e252.tid.isDescendantOf($currentTid253))
                                continue $label249;
                            if ($currentTid253.parent != null) {
                                $retry251 = false;
                                throw $e252;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e252) {
                            $commit250 = false;
                            if ($tm254.checkForStaleObjects()) continue;
                            fabric.common.TransactionID $currentTid253 =
                              $tm254.getCurrentTid();
                            if ($e252.tid.isDescendantOf($currentTid253)) {
                                $retry251 = true;
                            }
                            else if ($currentTid253.parent != null) {
                                $retry251 = false;
                                throw $e252;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e252) {
                            $commit250 = false;
                            if ($tm254.checkForStaleObjects())
                                continue $label249;
                            $retry251 = false;
                            throw new fabric.worker.AbortException($e252);
                        }
                        finally {
                            if ($commit250) {
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e252) {
                                    $commit250 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e252) {
                                    $commit250 = false;
                                    fabric.common.TransactionID $currentTid253 =
                                      $tm254.getCurrentTid();
                                    if ($currentTid253 != null) {
                                        if ($e252.tid.equals($currentTid253) ||
                                              !$e252.tid.isDescendantOf(
                                                           $currentTid253)) {
                                            throw $e252;
                                        }
                                    }
                                }
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit250 && $retry251) {
                                { rtn = rtn$var248; }
                                continue $label249;
                            }
                        }
                    }
                }
                return rtn;
            }
        }
        
        public long computeLastUpdate(boolean useWeakCache) {
            return fabric.metrics.SampledMetric._Impl.
              static_computeLastUpdate((fabric.metrics.SampledMetric)
                                         this.$getProxy(), useWeakCache);
        }
        
        private static long static_computeLastUpdate(
          fabric.metrics.SampledMetric tmp, boolean useWeakCache) {
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                if (tmp.get$usePreset()) {
                    return 0;
                } else {
                    tmp.get$stats().preload(tmp.get$name());
                    return tmp.get$stats().getLastUpdate();
                }
            }
            else {
                long rtn = 0;
                {
                    long rtn$var258 = rtn;
                    fabric.worker.transaction.TransactionManager $tm264 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled267 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff265 = 1;
                    boolean $doBackoff266 = true;
                    boolean $retry261 = true;
                    $label259: for (boolean $commit260 = false; !$commit260; ) {
                        if ($backoffEnabled267) {
                            if ($doBackoff266) {
                                if ($backoff265 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.sleep($backoff265);
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e262) {
                                            
                                        }
                                    }
                                }
                                if ($backoff265 < 5000) $backoff265 *= 2;
                            }
                            $doBackoff266 = $backoff265 <= 32 || !$doBackoff266;
                        }
                        $commit260 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            if (tmp.get$usePreset()) {
                                rtn = 0;
                            } else {
                                tmp.get$stats().preload(tmp.get$name());
                                rtn = tmp.get$stats().getLastUpdate();
                            }
                        }
                        catch (final fabric.worker.RetryException $e262) {
                            $commit260 = false;
                            continue $label259;
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e262) {
                            $commit260 = false;
                            fabric.common.TransactionID $currentTid263 =
                              $tm264.getCurrentTid();
                            if ($e262.tid.isDescendantOf($currentTid263))
                                continue $label259;
                            if ($currentTid263.parent != null) {
                                $retry261 = false;
                                throw $e262;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e262) {
                            $commit260 = false;
                            if ($tm264.checkForStaleObjects()) continue;
                            fabric.common.TransactionID $currentTid263 =
                              $tm264.getCurrentTid();
                            if ($e262.tid.isDescendantOf($currentTid263)) {
                                $retry261 = true;
                            }
                            else if ($currentTid263.parent != null) {
                                $retry261 = false;
                                throw $e262;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e262) {
                            $commit260 = false;
                            if ($tm264.checkForStaleObjects())
                                continue $label259;
                            $retry261 = false;
                            throw new fabric.worker.AbortException($e262);
                        }
                        finally {
                            if ($commit260) {
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e262) {
                                    $commit260 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e262) {
                                    $commit260 = false;
                                    fabric.common.TransactionID $currentTid263 =
                                      $tm264.getCurrentTid();
                                    if ($currentTid263 != null) {
                                        if ($e262.tid.equals($currentTid263) ||
                                              !$e262.tid.isDescendantOf(
                                                           $currentTid263)) {
                                            throw $e262;
                                        }
                                    }
                                }
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit260 && $retry261) {
                                { rtn = rtn$var258; }
                                continue $label259;
                            }
                        }
                    }
                }
                return rtn;
            }
        }
        
        public double computeUpdateInterval(boolean useWeakCache) {
            return fabric.metrics.SampledMetric._Impl.
              static_computeUpdateInterval((fabric.metrics.SampledMetric)
                                             this.$getProxy(), useWeakCache);
        }
        
        private static double static_computeUpdateInterval(
          fabric.metrics.SampledMetric tmp, boolean useWeakCache) {
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                if (tmp.get$usePreset()) {
                    return 0;
                } else {
                    tmp.get$stats().preload(tmp.get$name());
                    return tmp.get$stats().getIntervalEstimate();
                }
            }
            else {
                double rtn = 0;
                {
                    double rtn$var268 = rtn;
                    fabric.worker.transaction.TransactionManager $tm274 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled277 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff275 = 1;
                    boolean $doBackoff276 = true;
                    boolean $retry271 = true;
                    $label269: for (boolean $commit270 = false; !$commit270; ) {
                        if ($backoffEnabled277) {
                            if ($doBackoff276) {
                                if ($backoff275 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.sleep($backoff275);
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e272) {
                                            
                                        }
                                    }
                                }
                                if ($backoff275 < 5000) $backoff275 *= 2;
                            }
                            $doBackoff276 = $backoff275 <= 32 || !$doBackoff276;
                        }
                        $commit270 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            if (tmp.get$usePreset()) {
                                rtn = 0;
                            } else {
                                tmp.get$stats().preload(tmp.get$name());
                                rtn = tmp.get$stats().getIntervalEstimate();
                            }
                        }
                        catch (final fabric.worker.RetryException $e272) {
                            $commit270 = false;
                            continue $label269;
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e272) {
                            $commit270 = false;
                            fabric.common.TransactionID $currentTid273 =
                              $tm274.getCurrentTid();
                            if ($e272.tid.isDescendantOf($currentTid273))
                                continue $label269;
                            if ($currentTid273.parent != null) {
                                $retry271 = false;
                                throw $e272;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e272) {
                            $commit270 = false;
                            if ($tm274.checkForStaleObjects()) continue;
                            fabric.common.TransactionID $currentTid273 =
                              $tm274.getCurrentTid();
                            if ($e272.tid.isDescendantOf($currentTid273)) {
                                $retry271 = true;
                            }
                            else if ($currentTid273.parent != null) {
                                $retry271 = false;
                                throw $e272;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e272) {
                            $commit270 = false;
                            if ($tm274.checkForStaleObjects())
                                continue $label269;
                            $retry271 = false;
                            throw new fabric.worker.AbortException($e272);
                        }
                        finally {
                            if ($commit270) {
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e272) {
                                    $commit270 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e272) {
                                    $commit270 = false;
                                    fabric.common.TransactionID $currentTid273 =
                                      $tm274.getCurrentTid();
                                    if ($currentTid273 != null) {
                                        if ($e272.tid.equals($currentTid273) ||
                                              !$e272.tid.isDescendantOf(
                                                           $currentTid273)) {
                                            throw $e272;
                                        }
                                    }
                                }
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit270 && $retry271) {
                                { rtn = rtn$var268; }
                                continue $label269;
                            }
                        }
                    }
                }
                return rtn;
            }
        }
        
        public double computeVelocity(boolean useWeakCache) {
            return fabric.metrics.SampledMetric._Impl.
              static_computeVelocity((fabric.metrics.SampledMetric)
                                       this.$getProxy(), useWeakCache);
        }
        
        private static double static_computeVelocity(
          fabric.metrics.SampledMetric tmp, boolean useWeakCache) {
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                if (tmp.get$usePreset()) {
                    return tmp.get$presetV();
                } else {
                    tmp.get$stats().preload(tmp.get$name());
                    return tmp.get$stats().getVelocityEstimate();
                }
            }
            else {
                double rtn = 0;
                {
                    double rtn$var278 = rtn;
                    fabric.worker.transaction.TransactionManager $tm284 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled287 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff285 = 1;
                    boolean $doBackoff286 = true;
                    boolean $retry281 = true;
                    $label279: for (boolean $commit280 = false; !$commit280; ) {
                        if ($backoffEnabled287) {
                            if ($doBackoff286) {
                                if ($backoff285 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.sleep($backoff285);
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e282) {
                                            
                                        }
                                    }
                                }
                                if ($backoff285 < 5000) $backoff285 *= 2;
                            }
                            $doBackoff286 = $backoff285 <= 32 || !$doBackoff286;
                        }
                        $commit280 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            if (tmp.get$usePreset()) {
                                rtn = tmp.get$presetV();
                            } else {
                                tmp.get$stats().preload(tmp.get$name());
                                rtn = tmp.get$stats().getVelocityEstimate();
                            }
                        }
                        catch (final fabric.worker.RetryException $e282) {
                            $commit280 = false;
                            continue $label279;
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e282) {
                            $commit280 = false;
                            fabric.common.TransactionID $currentTid283 =
                              $tm284.getCurrentTid();
                            if ($e282.tid.isDescendantOf($currentTid283))
                                continue $label279;
                            if ($currentTid283.parent != null) {
                                $retry281 = false;
                                throw $e282;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e282) {
                            $commit280 = false;
                            if ($tm284.checkForStaleObjects()) continue;
                            fabric.common.TransactionID $currentTid283 =
                              $tm284.getCurrentTid();
                            if ($e282.tid.isDescendantOf($currentTid283)) {
                                $retry281 = true;
                            }
                            else if ($currentTid283.parent != null) {
                                $retry281 = false;
                                throw $e282;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e282) {
                            $commit280 = false;
                            if ($tm284.checkForStaleObjects())
                                continue $label279;
                            $retry281 = false;
                            throw new fabric.worker.AbortException($e282);
                        }
                        finally {
                            if ($commit280) {
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e282) {
                                    $commit280 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e282) {
                                    $commit280 = false;
                                    fabric.common.TransactionID $currentTid283 =
                                      $tm284.getCurrentTid();
                                    if ($currentTid283 != null) {
                                        if ($e282.tid.equals($currentTid283) ||
                                              !$e282.tid.isDescendantOf(
                                                           $currentTid283)) {
                                            throw $e282;
                                        }
                                    }
                                }
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit280 && $retry281) {
                                { rtn = rtn$var278; }
                                continue $label279;
                            }
                        }
                    }
                }
                return rtn;
            }
        }
        
        public double computeNoise(boolean useWeakCache) {
            return fabric.metrics.SampledMetric._Impl.
              static_computeNoise((fabric.metrics.SampledMetric)
                                    this.$getProxy(), useWeakCache);
        }
        
        private static double static_computeNoise(
          fabric.metrics.SampledMetric tmp, boolean useWeakCache) {
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                if (tmp.get$usePreset()) {
                    return tmp.get$presetN();
                } else {
                    tmp.get$stats().preload(tmp.get$name());
                    return tmp.get$stats().getNoiseEstimate();
                }
            }
            else {
                double rtn = 0;
                {
                    double rtn$var288 = rtn;
                    fabric.worker.transaction.TransactionManager $tm294 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled297 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff295 = 1;
                    boolean $doBackoff296 = true;
                    boolean $retry291 = true;
                    $label289: for (boolean $commit290 = false; !$commit290; ) {
                        if ($backoffEnabled297) {
                            if ($doBackoff296) {
                                if ($backoff295 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.sleep($backoff295);
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e292) {
                                            
                                        }
                                    }
                                }
                                if ($backoff295 < 5000) $backoff295 *= 2;
                            }
                            $doBackoff296 = $backoff295 <= 32 || !$doBackoff296;
                        }
                        $commit290 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            if (tmp.get$usePreset()) {
                                rtn = tmp.get$presetN();
                            } else {
                                tmp.get$stats().preload(tmp.get$name());
                                rtn = tmp.get$stats().getNoiseEstimate();
                            }
                        }
                        catch (final fabric.worker.RetryException $e292) {
                            $commit290 = false;
                            continue $label289;
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e292) {
                            $commit290 = false;
                            fabric.common.TransactionID $currentTid293 =
                              $tm294.getCurrentTid();
                            if ($e292.tid.isDescendantOf($currentTid293))
                                continue $label289;
                            if ($currentTid293.parent != null) {
                                $retry291 = false;
                                throw $e292;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e292) {
                            $commit290 = false;
                            if ($tm294.checkForStaleObjects()) continue;
                            fabric.common.TransactionID $currentTid293 =
                              $tm294.getCurrentTid();
                            if ($e292.tid.isDescendantOf($currentTid293)) {
                                $retry291 = true;
                            }
                            else if ($currentTid293.parent != null) {
                                $retry291 = false;
                                throw $e292;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e292) {
                            $commit290 = false;
                            if ($tm294.checkForStaleObjects())
                                continue $label289;
                            $retry291 = false;
                            throw new fabric.worker.AbortException($e292);
                        }
                        finally {
                            if ($commit290) {
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e292) {
                                    $commit290 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e292) {
                                    $commit290 = false;
                                    fabric.common.TransactionID $currentTid293 =
                                      $tm294.getCurrentTid();
                                    if ($currentTid293 != null) {
                                        if ($e292.tid.equals($currentTid293) ||
                                              !$e292.tid.isDescendantOf(
                                                           $currentTid293)) {
                                            throw $e292;
                                        }
                                    }
                                }
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit290 && $retry291) {
                                { rtn = rtn$var288; }
                                continue $label289;
                            }
                        }
                    }
                }
                return rtn;
            }
        }
        
        public java.lang.String toString() {
            return "SampledMetric(" + this.get$name() + ")@" +
            getStore().name();
        }
        
        public boolean isSingleStore() { return true; }
        
        public fabric.worker.metrics.RunningMetricStats get$stats() {
            fabric.worker.transaction.TransactionManager.getInstance().
              registerRead(this);
            return this.stats;
        }
        
        public fabric.worker.metrics.RunningMetricStats set$stats(
          fabric.worker.metrics.RunningMetricStats val) {
            fabric.worker.transaction.TransactionManager tm =
              fabric.worker.transaction.TransactionManager.getInstance();
            boolean transactionCreated = tm.registerWrite(this);
            this.stats = val;
            if (transactionCreated) tm.commitTransaction();
            return val;
        }
        
        /**
   * The statistics for this metric.
   */
        protected fabric.worker.metrics.RunningMetricStats stats;
        
        /**
   * Updates the velocity and interval estimates with the given observation.
   *
   * @param newVal
   *        the new value of the measured quantity
   * @param eventTime
   *        the time, in milliseconds, this update happened
   */
        public void updateEstimates(double newVal, long eventTime) {
            this.get$stats().preload(this.get$name());
            this.set$stats(this.get$stats().update(newVal));
            if (this.get$stats().getSamples() %
                  fabric.worker.Worker.getWorker(
                                         ).config.metricStatsLogInterval == 0) {
                fabric.common.Logging.METRICS_LOGGER.
                  log(
                    java.util.logging.Level.INFO,
                    "STATS {0} {1}", new java.lang.Object[] { (java.lang.Object) fabric.lang.WrappedJavaInlineable.$unwrap((fabric.metrics.SampledMetric) this.$getProxy()), this.get$stats() });
            }
        }
        
        public double value(boolean useWeakCache) {
            return fabric.metrics.SampledMetric._Impl.
              static_value((fabric.metrics.SampledMetric) this.$getProxy(),
                           useWeakCache);
        }
        
        private static double static_value(fabric.metrics.SampledMetric tmp,
                                           boolean useWeakCache) {
            double result = 0;
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                if (useWeakCache) {
                    result = tmp.get$cachedValue();
                } else {
                    result = tmp.computeValue(false);
                }
            }
            else {
                {
                    double result$var298 = result;
                    fabric.worker.transaction.TransactionManager $tm304 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled307 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff305 = 1;
                    boolean $doBackoff306 = true;
                    boolean $retry301 = true;
                    $label299: for (boolean $commit300 = false; !$commit300; ) {
                        if ($backoffEnabled307) {
                            if ($doBackoff306) {
                                if ($backoff305 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.sleep($backoff305);
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e302) {
                                            
                                        }
                                    }
                                }
                                if ($backoff305 < 5000) $backoff305 *= 2;
                            }
                            $doBackoff306 = $backoff305 <= 32 || !$doBackoff306;
                        }
                        $commit300 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            if (useWeakCache) {
                                result = tmp.get$cachedValue();
                            } else {
                                result = tmp.computeValue(false);
                            }
                        }
                        catch (final fabric.worker.RetryException $e302) {
                            $commit300 = false;
                            continue $label299;
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e302) {
                            $commit300 = false;
                            fabric.common.TransactionID $currentTid303 =
                              $tm304.getCurrentTid();
                            if ($e302.tid.isDescendantOf($currentTid303))
                                continue $label299;
                            if ($currentTid303.parent != null) {
                                $retry301 = false;
                                throw $e302;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e302) {
                            $commit300 = false;
                            if ($tm304.checkForStaleObjects()) continue;
                            fabric.common.TransactionID $currentTid303 =
                              $tm304.getCurrentTid();
                            if ($e302.tid.isDescendantOf($currentTid303)) {
                                $retry301 = true;
                            }
                            else if ($currentTid303.parent != null) {
                                $retry301 = false;
                                throw $e302;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e302) {
                            $commit300 = false;
                            if ($tm304.checkForStaleObjects())
                                continue $label299;
                            $retry301 = false;
                            throw new fabric.worker.AbortException($e302);
                        }
                        finally {
                            if ($commit300) {
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e302) {
                                    $commit300 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e302) {
                                    $commit300 = false;
                                    fabric.common.TransactionID $currentTid303 =
                                      $tm304.getCurrentTid();
                                    if ($currentTid303 != null) {
                                        if ($e302.tid.equals($currentTid303) ||
                                              !$e302.tid.isDescendantOf(
                                                           $currentTid303)) {
                                            throw $e302;
                                        }
                                    }
                                }
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit300 && $retry301) {
                                { result = result$var298; }
                                continue $label299;
                            }
                        }
                    }
                }
            }
            return result;
        }
        
        public long samples(boolean useWeakCache) {
            return fabric.metrics.SampledMetric._Impl.
              static_samples((fabric.metrics.SampledMetric) this.$getProxy(),
                             useWeakCache);
        }
        
        private static long static_samples(fabric.metrics.SampledMetric tmp,
                                           boolean useWeakCache) {
            long result = 0;
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                if (useWeakCache) {
                    result = tmp.get$cachedSamples();
                } else {
                    result = tmp.computeSamples(false);
                }
            }
            else {
                {
                    long result$var308 = result;
                    fabric.worker.transaction.TransactionManager $tm314 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled317 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff315 = 1;
                    boolean $doBackoff316 = true;
                    boolean $retry311 = true;
                    $label309: for (boolean $commit310 = false; !$commit310; ) {
                        if ($backoffEnabled317) {
                            if ($doBackoff316) {
                                if ($backoff315 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.sleep($backoff315);
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e312) {
                                            
                                        }
                                    }
                                }
                                if ($backoff315 < 5000) $backoff315 *= 2;
                            }
                            $doBackoff316 = $backoff315 <= 32 || !$doBackoff316;
                        }
                        $commit310 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            if (useWeakCache) {
                                result = tmp.get$cachedSamples();
                            } else {
                                result = tmp.computeSamples(false);
                            }
                        }
                        catch (final fabric.worker.RetryException $e312) {
                            $commit310 = false;
                            continue $label309;
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e312) {
                            $commit310 = false;
                            fabric.common.TransactionID $currentTid313 =
                              $tm314.getCurrentTid();
                            if ($e312.tid.isDescendantOf($currentTid313))
                                continue $label309;
                            if ($currentTid313.parent != null) {
                                $retry311 = false;
                                throw $e312;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e312) {
                            $commit310 = false;
                            if ($tm314.checkForStaleObjects()) continue;
                            fabric.common.TransactionID $currentTid313 =
                              $tm314.getCurrentTid();
                            if ($e312.tid.isDescendantOf($currentTid313)) {
                                $retry311 = true;
                            }
                            else if ($currentTid313.parent != null) {
                                $retry311 = false;
                                throw $e312;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e312) {
                            $commit310 = false;
                            if ($tm314.checkForStaleObjects())
                                continue $label309;
                            $retry311 = false;
                            throw new fabric.worker.AbortException($e312);
                        }
                        finally {
                            if ($commit310) {
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e312) {
                                    $commit310 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e312) {
                                    $commit310 = false;
                                    fabric.common.TransactionID $currentTid313 =
                                      $tm314.getCurrentTid();
                                    if ($currentTid313 != null) {
                                        if ($e312.tid.equals($currentTid313) ||
                                              !$e312.tid.isDescendantOf(
                                                           $currentTid313)) {
                                            throw $e312;
                                        }
                                    }
                                }
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit310 && $retry311) {
                                { result = result$var308; }
                                continue $label309;
                            }
                        }
                    }
                }
            }
            return result;
        }
        
        public long lastUpdate(boolean useWeakCache) {
            return fabric.metrics.SampledMetric._Impl.
              static_lastUpdate((fabric.metrics.SampledMetric) this.$getProxy(),
                                useWeakCache);
        }
        
        private static long static_lastUpdate(fabric.metrics.SampledMetric tmp,
                                              boolean useWeakCache) {
            long result = 0;
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                if (useWeakCache) {
                    result = tmp.get$cachedLastUpdate();
                } else {
                    result = tmp.computeLastUpdate(false);
                }
            }
            else {
                {
                    long result$var318 = result;
                    fabric.worker.transaction.TransactionManager $tm324 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled327 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff325 = 1;
                    boolean $doBackoff326 = true;
                    boolean $retry321 = true;
                    $label319: for (boolean $commit320 = false; !$commit320; ) {
                        if ($backoffEnabled327) {
                            if ($doBackoff326) {
                                if ($backoff325 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.sleep($backoff325);
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e322) {
                                            
                                        }
                                    }
                                }
                                if ($backoff325 < 5000) $backoff325 *= 2;
                            }
                            $doBackoff326 = $backoff325 <= 32 || !$doBackoff326;
                        }
                        $commit320 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            if (useWeakCache) {
                                result = tmp.get$cachedLastUpdate();
                            } else {
                                result = tmp.computeLastUpdate(false);
                            }
                        }
                        catch (final fabric.worker.RetryException $e322) {
                            $commit320 = false;
                            continue $label319;
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e322) {
                            $commit320 = false;
                            fabric.common.TransactionID $currentTid323 =
                              $tm324.getCurrentTid();
                            if ($e322.tid.isDescendantOf($currentTid323))
                                continue $label319;
                            if ($currentTid323.parent != null) {
                                $retry321 = false;
                                throw $e322;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e322) {
                            $commit320 = false;
                            if ($tm324.checkForStaleObjects()) continue;
                            fabric.common.TransactionID $currentTid323 =
                              $tm324.getCurrentTid();
                            if ($e322.tid.isDescendantOf($currentTid323)) {
                                $retry321 = true;
                            }
                            else if ($currentTid323.parent != null) {
                                $retry321 = false;
                                throw $e322;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e322) {
                            $commit320 = false;
                            if ($tm324.checkForStaleObjects())
                                continue $label319;
                            $retry321 = false;
                            throw new fabric.worker.AbortException($e322);
                        }
                        finally {
                            if ($commit320) {
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e322) {
                                    $commit320 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e322) {
                                    $commit320 = false;
                                    fabric.common.TransactionID $currentTid323 =
                                      $tm324.getCurrentTid();
                                    if ($currentTid323 != null) {
                                        if ($e322.tid.equals($currentTid323) ||
                                              !$e322.tid.isDescendantOf(
                                                           $currentTid323)) {
                                            throw $e322;
                                        }
                                    }
                                }
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit320 && $retry321) {
                                { result = result$var318; }
                                continue $label319;
                            }
                        }
                    }
                }
            }
            return result;
        }
        
        public double updateInterval(boolean useWeakCache) {
            return fabric.metrics.SampledMetric._Impl.
              static_updateInterval((fabric.metrics.SampledMetric)
                                      this.$getProxy(), useWeakCache);
        }
        
        private static double static_updateInterval(
          fabric.metrics.SampledMetric tmp, boolean useWeakCache) {
            double result = 0;
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                if (useWeakCache) {
                    result = tmp.get$cachedUpdateInterval();
                } else {
                    result = tmp.computeUpdateInterval(false);
                }
            }
            else {
                {
                    double result$var328 = result;
                    fabric.worker.transaction.TransactionManager $tm334 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled337 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff335 = 1;
                    boolean $doBackoff336 = true;
                    boolean $retry331 = true;
                    $label329: for (boolean $commit330 = false; !$commit330; ) {
                        if ($backoffEnabled337) {
                            if ($doBackoff336) {
                                if ($backoff335 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.sleep($backoff335);
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e332) {
                                            
                                        }
                                    }
                                }
                                if ($backoff335 < 5000) $backoff335 *= 2;
                            }
                            $doBackoff336 = $backoff335 <= 32 || !$doBackoff336;
                        }
                        $commit330 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            if (useWeakCache) {
                                result = tmp.get$cachedUpdateInterval();
                            } else {
                                result = tmp.computeUpdateInterval(false);
                            }
                        }
                        catch (final fabric.worker.RetryException $e332) {
                            $commit330 = false;
                            continue $label329;
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e332) {
                            $commit330 = false;
                            fabric.common.TransactionID $currentTid333 =
                              $tm334.getCurrentTid();
                            if ($e332.tid.isDescendantOf($currentTid333))
                                continue $label329;
                            if ($currentTid333.parent != null) {
                                $retry331 = false;
                                throw $e332;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e332) {
                            $commit330 = false;
                            if ($tm334.checkForStaleObjects()) continue;
                            fabric.common.TransactionID $currentTid333 =
                              $tm334.getCurrentTid();
                            if ($e332.tid.isDescendantOf($currentTid333)) {
                                $retry331 = true;
                            }
                            else if ($currentTid333.parent != null) {
                                $retry331 = false;
                                throw $e332;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e332) {
                            $commit330 = false;
                            if ($tm334.checkForStaleObjects())
                                continue $label329;
                            $retry331 = false;
                            throw new fabric.worker.AbortException($e332);
                        }
                        finally {
                            if ($commit330) {
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e332) {
                                    $commit330 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e332) {
                                    $commit330 = false;
                                    fabric.common.TransactionID $currentTid333 =
                                      $tm334.getCurrentTid();
                                    if ($currentTid333 != null) {
                                        if ($e332.tid.equals($currentTid333) ||
                                              !$e332.tid.isDescendantOf(
                                                           $currentTid333)) {
                                            throw $e332;
                                        }
                                    }
                                }
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit330 && $retry331) {
                                { result = result$var328; }
                                continue $label329;
                            }
                        }
                    }
                }
            }
            return result;
        }
        
        public double velocity(boolean useWeakCache) {
            return fabric.metrics.SampledMetric._Impl.
              static_velocity((fabric.metrics.SampledMetric) this.$getProxy(),
                              useWeakCache);
        }
        
        private static double static_velocity(fabric.metrics.SampledMetric tmp,
                                              boolean useWeakCache) {
            double result = 0;
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                if (useWeakCache) {
                    result = tmp.get$cachedVelocity();
                } else {
                    result = tmp.computeVelocity(false);
                }
            }
            else {
                {
                    double result$var338 = result;
                    fabric.worker.transaction.TransactionManager $tm344 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled347 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff345 = 1;
                    boolean $doBackoff346 = true;
                    boolean $retry341 = true;
                    $label339: for (boolean $commit340 = false; !$commit340; ) {
                        if ($backoffEnabled347) {
                            if ($doBackoff346) {
                                if ($backoff345 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.sleep($backoff345);
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e342) {
                                            
                                        }
                                    }
                                }
                                if ($backoff345 < 5000) $backoff345 *= 2;
                            }
                            $doBackoff346 = $backoff345 <= 32 || !$doBackoff346;
                        }
                        $commit340 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            if (useWeakCache) {
                                result = tmp.get$cachedVelocity();
                            } else {
                                result = tmp.computeVelocity(false);
                            }
                        }
                        catch (final fabric.worker.RetryException $e342) {
                            $commit340 = false;
                            continue $label339;
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e342) {
                            $commit340 = false;
                            fabric.common.TransactionID $currentTid343 =
                              $tm344.getCurrentTid();
                            if ($e342.tid.isDescendantOf($currentTid343))
                                continue $label339;
                            if ($currentTid343.parent != null) {
                                $retry341 = false;
                                throw $e342;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e342) {
                            $commit340 = false;
                            if ($tm344.checkForStaleObjects()) continue;
                            fabric.common.TransactionID $currentTid343 =
                              $tm344.getCurrentTid();
                            if ($e342.tid.isDescendantOf($currentTid343)) {
                                $retry341 = true;
                            }
                            else if ($currentTid343.parent != null) {
                                $retry341 = false;
                                throw $e342;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e342) {
                            $commit340 = false;
                            if ($tm344.checkForStaleObjects())
                                continue $label339;
                            $retry341 = false;
                            throw new fabric.worker.AbortException($e342);
                        }
                        finally {
                            if ($commit340) {
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e342) {
                                    $commit340 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e342) {
                                    $commit340 = false;
                                    fabric.common.TransactionID $currentTid343 =
                                      $tm344.getCurrentTid();
                                    if ($currentTid343 != null) {
                                        if ($e342.tid.equals($currentTid343) ||
                                              !$e342.tid.isDescendantOf(
                                                           $currentTid343)) {
                                            throw $e342;
                                        }
                                    }
                                }
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit340 && $retry341) {
                                { result = result$var338; }
                                continue $label339;
                            }
                        }
                    }
                }
            }
            return result;
        }
        
        public double noise(boolean useWeakCache) {
            return fabric.metrics.SampledMetric._Impl.
              static_noise((fabric.metrics.SampledMetric) this.$getProxy(),
                           useWeakCache);
        }
        
        private static double static_noise(fabric.metrics.SampledMetric tmp,
                                           boolean useWeakCache) {
            double result = 0;
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                if (useWeakCache) {
                    result = tmp.get$cachedNoise();
                } else {
                    result = tmp.computeNoise(false);
                }
            }
            else {
                {
                    double result$var348 = result;
                    fabric.worker.transaction.TransactionManager $tm354 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled357 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff355 = 1;
                    boolean $doBackoff356 = true;
                    boolean $retry351 = true;
                    $label349: for (boolean $commit350 = false; !$commit350; ) {
                        if ($backoffEnabled357) {
                            if ($doBackoff356) {
                                if ($backoff355 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.sleep($backoff355);
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e352) {
                                            
                                        }
                                    }
                                }
                                if ($backoff355 < 5000) $backoff355 *= 2;
                            }
                            $doBackoff356 = $backoff355 <= 32 || !$doBackoff356;
                        }
                        $commit350 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            if (useWeakCache) {
                                result = tmp.get$cachedNoise();
                            } else {
                                result = tmp.computeNoise(false);
                            }
                        }
                        catch (final fabric.worker.RetryException $e352) {
                            $commit350 = false;
                            continue $label349;
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e352) {
                            $commit350 = false;
                            fabric.common.TransactionID $currentTid353 =
                              $tm354.getCurrentTid();
                            if ($e352.tid.isDescendantOf($currentTid353))
                                continue $label349;
                            if ($currentTid353.parent != null) {
                                $retry351 = false;
                                throw $e352;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e352) {
                            $commit350 = false;
                            if ($tm354.checkForStaleObjects()) continue;
                            fabric.common.TransactionID $currentTid353 =
                              $tm354.getCurrentTid();
                            if ($e352.tid.isDescendantOf($currentTid353)) {
                                $retry351 = true;
                            }
                            else if ($currentTid353.parent != null) {
                                $retry351 = false;
                                throw $e352;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e352) {
                            $commit350 = false;
                            if ($tm354.checkForStaleObjects())
                                continue $label349;
                            $retry351 = false;
                            throw new fabric.worker.AbortException($e352);
                        }
                        finally {
                            if ($commit350) {
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e352) {
                                    $commit350 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e352) {
                                    $commit350 = false;
                                    fabric.common.TransactionID $currentTid353 =
                                      $tm354.getCurrentTid();
                                    if ($currentTid353 != null) {
                                        if ($e352.tid.equals($currentTid353) ||
                                              !$e352.tid.isDescendantOf(
                                                           $currentTid353)) {
                                            throw $e352;
                                        }
                                    }
                                }
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit350 && $retry351) {
                                { result = result$var348; }
                                continue $label349;
                            }
                        }
                    }
                }
            }
            return result;
        }
        
        public _Impl(fabric.worker.Store $location) { super($location); }
        
        protected fabric.lang.Object._Proxy $makeProxy() {
            return new fabric.metrics.SampledMetric._Proxy(this);
        }
        
        public void $serialize(java.io.ObjectOutput out,
                               java.util.List refTypes,
                               java.util.List intraStoreRefs,
                               java.util.List interStoreRefs)
              throws java.io.IOException {
            super.$serialize(out, refTypes, intraStoreRefs, interStoreRefs);
            $writeInline(out, this.name);
            out.writeBoolean(this.usePreset);
            out.writeDouble(this.presetR);
            out.writeDouble(this.presetB);
            out.writeDouble(this.presetV);
            out.writeDouble(this.presetN);
            $writeInline(out, this.stats);
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
            this.name = (java.lang.String) in.readObject();
            this.usePreset = in.readBoolean();
            this.presetR = in.readDouble();
            this.presetB = in.readDouble();
            this.presetV = in.readDouble();
            this.presetN = in.readDouble();
            this.stats = (fabric.worker.metrics.RunningMetricStats)
                           in.readObject();
        }
        
        public void $copyAppStateFrom(fabric.lang.Object._Impl other) {
            super.$copyAppStateFrom(other);
            fabric.metrics.SampledMetric._Impl src =
              (fabric.metrics.SampledMetric._Impl) other;
            this.name = src.name;
            this.usePreset = src.usePreset;
            this.presetR = src.presetR;
            this.presetB = src.presetB;
            this.presetV = src.presetV;
            this.presetN = src.presetN;
            this.stats = src.stats;
        }
    }
    
    interface _Static extends fabric.lang.Object, Cloneable {
        final class _Proxy extends fabric.lang.Object._Proxy
          implements fabric.metrics.SampledMetric._Static {
            public _Proxy(fabric.metrics.SampledMetric._Static._Impl impl) {
                super(impl);
            }
            
            public _Proxy(fabric.worker.Store store, long onum) {
                super(store, onum);
            }
            
            public static final fabric.metrics.SampledMetric._Static $instance;
            
            static {
                fabric.
                  metrics.
                  SampledMetric.
                  _Static.
                  _Impl
                  impl =
                  (fabric.metrics.SampledMetric._Static._Impl)
                    fabric.lang.Object._Static._Proxy.
                    $makeStaticInstance(
                      fabric.metrics.SampledMetric._Static._Impl.class);
                $instance = (fabric.metrics.SampledMetric._Static)
                              impl.$getProxy();
                impl.$init();
            }
        }
        
        class _Impl extends fabric.lang.Object._Impl
          implements fabric.metrics.SampledMetric._Static {
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
                return new fabric.metrics.SampledMetric._Static._Proxy(this);
            }
            
            private void $init() {  }
        }
        
    }
    
    public static final byte[] $classHash = new byte[] { -18, -82, 46, -30, 68,
    -85, 46, 103, 121, 11, 14, 41, 30, -31, -100, 44, -113, -35, -127, 34, -9,
    -73, -33, 52, -119, 94, 27, -33, -87, 85, 20, 104 };
    public static final java.lang.String jlc$CompilerVersion$fabil = "0.3.0";
    public static final long jlc$SourceLastModified$fabil = 1524675608000L;
    public static final java.lang.String jlc$ClassType$fabil =
      "H4sIAAAAAAAAAK1aC3QU1Rm+u+RNyIMQhBBCCAFEcBfwcVQUCxEksgkxgRRjJU5mb5IhszPrzN2wSLGox6K2J74CgkX6EGuRCJb6qpoe2qJA6dFjfdaqWJUjHqutelqpx2r//87dZ2YnmXPCYf5vd+b+//3f997NDHxCsk2D1HRKHYrqYxvD1PQtlzrqA02SYdJgnSqZ5mq42y6PzarffuqhYJWXeAOkUJY0XVNkSW3XTEaKAuulXsmvUeZf01y/6GqSLyPjCsnsZsR79dKoQarDurqxS9WZmGSI/G1z/f33ris5OIYUt5FiRWthElPkOl1jNMraSGGIhjqoYS4JBmmwjZRqlAZbqKFIqnI9DNS1NjLeVLo0iUUMajZTU1d7ceB4MxKmBp8zdhPV10FtIyIz3QD1Syz1I0xR/QHFZIsCJKdToWrQvI7cQLICJLtTlbpg4MRAzAo/l+hfjvdheIECahqdkkxjLFk9ihZkZFo6R9zi2pUwAFhzQ5R16/GpsjQJbpDxlkqqpHX5W5ihaF0wNFuPwCyMVGQUCoPywpLcI3XRdkYmpY9rsh7BqHzuFmRhpDx9GJcEMatIi1lStD5pvLhvk7ZC8xIP6Byksor65wFTVRpTM+2kBtVkajEWnhXYLk0cvNVLCAwuTxtsjXny+599Z17VoaPWmCk2Y1Z1rKcya5f3dBS9VFk358IxqEZeWDcVTIUUy3lUm8STRdEwZPvEuER86Is9PNT8/FVbHqYfe0lBPcmRdTUSgqwqlfVQWFGpcTnVqCExGqwn+VQL1vHn9SQXPgcUjVp3V3V2mpTVkyyV38rR+XdwUSeIQBflwmdF69Rjn8MS6+afo2FCSC5cxAP/TxGy8CH4PIWQMeczstLfrYeov0ON0A2Q3n64qGTI3X6oW0OR/aYh+42IxhQYJG5BFgGY/hYpFFZpsIF/9YEa4dEVF0XtSzZ4PODYabIepB2SCVESGbO0SYWiWKGrQWq0y2rfYD0pG9zJsyYfM92EbOV+8UCkK9N7RDJvf2Tpss/2tx+3Mg55hdsYqbR09AkdfSk6glqFWEs+6E4+6E4Dnqivbnf9Pp4yOSavrbikQpB0UViVWKduhKLE4+FmTeD8PFcg0j3QQaBJFM5pueaKa2+tGQNJGt6QhXGDobXpJZNoNPXwSYI6aJeLt576z4Htm/VE8TBSO6Smh3JiTdak+8jQZRqEnpcQf1a19Hj74OZaL/aTfGh1TIJkhL5RlT5HSm0uivU59EZ2gIxFH0gqPoo1pwLWbegbEnd47IuQjLfSAJ2VpiBvkZe0hO9/44WPzuGLR6ybFie13RbKFiVVMAor5rVamvD9aoNSGPf2jqZ7tn2y9WrueBgxw27CWqR1ULkSlKxu3HL0ur+eeGfPK95EsBjJCUc6VEWOcltKv4V/Hri+wQvLEG8gQjOuEy2gOt4DwjjzrIRu0A1U6Eigulm7RgvpQaVTkTpUipnydfHMBY//o6/ECrcKdyznGWTe8AIS9ycvJVuOr/uyiovxyLgaJfyXGGa1uLKE5CWGIW1EPaI3/mXqziPS/ZD50KBM5XrKew7h/iA8gAu5L87mdEHas3OR1FjequT3c82h7X45rpuJXGzzD+yqqFv8sVXx8VxEGdNtKr5VSiqThQ+H/u2tyXnOS3LbSAlfsiWNtUrQtSAN2mDRNevEzQAZl/I8dQG1VotF8VqrTK+DpGnTqyDRaeAzjsbPBVbiW4kDjihBJ1XCNRXadVQgXxHLwkgnRD2Ef7iIs8zgdBaSOdyRXkZyw4bSC5nFSL4SCkUYxp7PMpdZOnCectis8MiiXT5rY8AfTE7vXVY5Ij0/rmYxqjkdrlZCCtsFNtiouSyDmvjxLCSXxlTLj5i0CZcPZpMLTYYSgnLuFUs/vbX/9m99ff1WHVj7oxlDtijJPNYeic81jk8YhVmmO83COZZ/eGDzM7/avNXaP4xPXe2XaZHQI6/978++He8es1lLcjt0XaWSltF/VXB9D/x2XOCzNv67csT+g7Cj85r50CuEjQgN0J+COjQo6qhJByHjFgqcYaPJVW41WYpfWx3n7Ia5dgq83WbOdW7nbB1+To2QImLhuM9s5pTdztmYcc5CnGcmrBAXQ7EMCLzHZs5u+zlhZckPGzqDlkOD0bhYL4odK8TdLfC2JLEMTmJw5jFjhX6m2NVs0I0easQ3N80RTYOqtzY3LXEG+wYQzaAid0tCN/4vR2w3zxPoT9ItqekTrMGpmU4GvP723NS/O7jqwQVesXI0gkeYHj5bpb1UTRJVi9U85OTZwM9DiWXg3Y+nXljXc7LLquZpaTOnj97bMHDs8lny3V4yJt7vhxzCUpkWpXb5AoPCGVJbndLrq+O+ykcfNMJ1JiHZ+RZmHU5Oj0RS8YpkqSmQJ1j+KHAw3c2J1debyOElPF256Jsd1uhbkNzAyFQrd2pF0tSm7IhrEwpuSjVrGlxroQLuELjRnVnIEhVoZDYrWeEfOzzrQ7IV8rqLsjWxZQbvBex0x+V3HUx8WuBJd7ojywcC3xmZ7tscnt2L5E5GCkB3S/FmHsFMmgehr7ULDLjTHFlWClw2Ms13Ozz7GZKdyZovddR8PUz7vMDfuNMcWQ4KHBiZ5g85PNuL5BfJmrc6ag6b36KzBVa40xxZJgssG5nmjzo8O4hkX7LmjRk1nw3XJlgZ/QJL3WmOLCUCCzJr7kmsE1bX+a2D+s8geQzUZ1IPtVqN3X4mq1dXgnYm1cJ1E+jzhMCfuDMJWe4T2O/CpOccTDqC5BAjeZhFeKLA74N2yuPuqw+OAL8XuMOd8shyr8C7MiuftBS02vpW1bUuPt0LDla9jORYSqDs7OI7INwGbCekdJdA08GuniG7HM5iCFRGFJQAl/qmg/pvIXmV4c+3oXCE0Xhg0guF72lWwwVpMX6ehaUfZTDAds+4mOFvQ/gLdNoOrkRIOyXwzRHFrCRh4EkHAz9E8g4jZdbU7cPZGQ/UXkLKtglUM9iZIVDI0iOww0WgPnWw419IPmKkSBiQ1BNezBSqRwiZMNvCsg9chQrJezZhQknvC3zVbZhOO5j3FZLPGSlPDZODlTxQF8B1mJCJMy0s/6+7QCHLaYH/HHmgPJ7MlnjG4M2vmfUjN5gQkEy2JhyUGM0Yq+/CdRSsGBS4YVRihZJ6BXa7jJVnrIOF+LuBJ5uRSamxcjaUh2sxXCcImXRE4DZ34UKWfoE/chEuhz2EZyKSYkg8YYVlQT3+7alXUjN2wmvggkKYrAicOiohQ0mVAovchqzKwcpqJJMYqUwN2fDGxqvsKzjBbhK4yl3YkKVRoMNuekjYZjsYNAdJDSPFsX5OVV1W2MaMAcMa+wY2qaUWTjk+KgFDSX8S+Gxm0+wDtsDBvnOQzGVkYtqy5WBmbOXylBNSda7Asa5CxVkKBHpchOoiB1MuRnJeYovRqCum4xbDA8eAqsMCbxyNOHFJWwQ6HKPt43SZg3HLkSwesr3IaCPf31aAJvMJqX5e4OMOQRq6v+Usjwncn9mYZDUbHJ5hNXtWwL6c6S3xP90tsdO7GiaFBJt+icBZ7vRGlpkCp41M71aHZ2uRXMnIOMVsAa1V2sJ0gzs9/ccMXhmXwszLCal5X+Cj7ioDWQ4I/OWIMoiflF/kqrY7mCEhaYNOFuHNeJnJlBAgH2t7OpoLWkCV1N4sUHIXBWS5VmCbixLvdrBhPRKZkexex+NDAwhcS8jMbAtrj41KbaOkowKfdlvbhoNV+MOYR4XGJWo7o3HxsIQImfWKwIPuwoIsvxa4z0VYNjkYsBlJLyO55jCHhQYuksxeK7ByVAKDkqYIHOc2MLc42LUVyQ/gLCQC42AeD40PlPghIXPmC3RaFG1CgywFArNchKbPwYQ7kdzGSIE6/PGgCaTeDnM/LXDTqEQHJV0vUM1slX10djiYdh+Su+EAJKLjbCEPEO5WfgoltEegux/JOUtUoMPqPiRAP3ew4gEkuyDHIiM7ELSC5AcImXeBhXO/GJUgoaTPBX6Q2TL7IO1zMO8RJA8mztvDWxmvpKcAYpjvLlDIEkOvi0A95mDJE0gOwPald7gjANbRszDzEwJH5ZjNJfUKdH3M/p2DYYeQPAUbg9jy42BffAV6iZD5dwnU3QUHWTSBDoYMCc4RBxtwhff8ATYGmuOmH9efVwlZUGbh/NdHJTIo6TWBR91G5iUHq15GcjyxMUgYF4XtaMofBvHFoSk2r/CJF0rlusN0z8mV88ozvL43acgrvoJv/+7ivDN2r3mdv5AWf1k0P0DyOiOqmvyCTdLnnLBBOxXu0HzrdZswt+cNaHOp7/sx/g4tfkIHeF6zxv2NkRxrHH57i3uT/5WngrupHNpJ2muDlhP4ED5hRcTAl5cHvjjjdE7e6nf5u2Pg1epP9/veu2yfr2vj2KI5VX/fNe+Ot2+s+fLJE+fetm7Kib1rJnT/H2fsrkdULQAA";
}
