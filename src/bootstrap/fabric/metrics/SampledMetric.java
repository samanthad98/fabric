package fabric.metrics;

import fabric.lang.*;
import fabric.lang.security.*;
import fabric.worker.*;
import fabric.worker.remote.*;
import java.lang.*;
import fabric.util.ArrayList;
import fabric.util.List;
import fabric.metrics.util.Observer;
import fabric.worker.metrics.ImmutableMetricsVector;
import fabric.worker.metrics.ImmutableObserverSet;
import fabric.worker.metrics.RunningMetricStats;
import fabric.worker.metrics.StatsMap;
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
    public long get$key();
    
    public long set$key(long val);
    
    public long postInc$key();
    
    public long postDec$key();
    
    /**
   * @param store
   *            the {@link Store} that holds this {@link Metric}
   * @param key
   *            the key the {@link Store} associates with this
   *            {@link SampledMetric}, so it can be easily retrieved later for
   *            configuration purposes.
   * @param init
   *        the initial value of this {@link Metric}
   */
    public fabric.metrics.SampledMetric fabric$metrics$SampledMetric$(
      long key, double init);
    
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
    
    public double computeValue(fabric.worker.metrics.StatsMap weakStats);
    
    public long computeSamples(fabric.worker.metrics.StatsMap weakStats);
    
    public long computeLastUpdate(fabric.worker.metrics.StatsMap weakStats);
    
    public double computeUpdateInterval(
      fabric.worker.metrics.StatsMap weakStats);
    
    public double computeVelocity(fabric.worker.metrics.StatsMap weakStats);
    
    public double computeNoise(fabric.worker.metrics.StatsMap weakStats);
    
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
    
    public double value(fabric.worker.metrics.StatsMap weakStats);
    
    public long samples(fabric.worker.metrics.StatsMap weakStats);
    
    public long lastUpdate(fabric.worker.metrics.StatsMap weakStats);
    
    public double updateInterval(fabric.worker.metrics.StatsMap weakStats);
    
    public double velocity(fabric.worker.metrics.StatsMap weakStats);
    
    public double noise(fabric.worker.metrics.StatsMap weakStats);
    
    public fabric.worker.metrics.ImmutableObserverSet handleDirectUpdates();
    
    public fabric.worker.metrics.ImmutableMetricsVector getLeafSubjects();
    
    public static class _Proxy extends fabric.metrics.Metric._Proxy
      implements fabric.metrics.SampledMetric {
        public long get$key() {
            return ((fabric.metrics.SampledMetric._Impl) fetch()).get$key();
        }
        
        public long set$key(long val) {
            return ((fabric.metrics.SampledMetric._Impl) fetch()).set$key(val);
        }
        
        public long postInc$key() {
            return ((fabric.metrics.SampledMetric._Impl) fetch()).postInc$key();
        }
        
        public long postDec$key() {
            return ((fabric.metrics.SampledMetric._Impl) fetch()).postDec$key();
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
          long arg1, double arg2) {
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
        
        public fabric.worker.metrics.ImmutableMetricsVector getLeafSubjects() {
            return ((fabric.metrics.SampledMetric) fetch()).getLeafSubjects();
        }
        
        public _Proxy(SampledMetric._Impl impl) { super(impl); }
        
        public _Proxy(fabric.worker.Store store, long onum) {
            super(store, onum);
        }
    }
    
    public static class _Impl extends fabric.metrics.Metric._Impl
      implements fabric.metrics.SampledMetric {
        public long get$key() { return this.key; }
        
        public long set$key(long val) {
            fabric.worker.transaction.TransactionManager tm =
              fabric.worker.transaction.TransactionManager.getInstance();
            boolean transactionCreated = tm.registerWrite(this);
            this.key = val;
            if (transactionCreated) tm.commitTransaction();
            return val;
        }
        
        public long postInc$key() {
            long tmp = this.get$key();
            this.set$key((long) (tmp + 1));
            return tmp;
        }
        
        public long postDec$key() {
            long tmp = this.get$key();
            this.set$key((long) (tmp - 1));
            return tmp;
        }
        
        private long key;
        
        /**
   * @param store
   *            the {@link Store} that holds this {@link Metric}
   * @param key
   *            the key the {@link Store} associates with this
   *            {@link SampledMetric}, so it can be easily retrieved later for
   *            configuration purposes.
   * @param init
   *        the initial value of this {@link Metric}
   */
        public fabric.metrics.SampledMetric fabric$metrics$SampledMetric$(
          long key, double init) {
            fabric.lang.security.Label lbl =
              fabric.lang.security.LabelUtil._Impl.noComponents();
            fabric.worker.Store s = $getStore();
            this.set$key((long) key);
            this.
              set$stats(
                fabric.worker.metrics.RunningMetricStats.
                    createRunningMetricStats(init, 0, 1));
            fabric.common.ConfigProperties config =
              fabric.worker.Worker.getWorker().config;
            if (getUsePreset()) {
                java.lang.String presetKey = "" + key + "@" + s.name();
                this.set$presetR(
                       (double)
                         (config.rates.containsKey(presetKey)
                            ? ((java.lang.Double)
                                 config.rates.get(presetKey)).doubleValue()
                            : 0.0));
                this.set$presetB(
                       (double)
                         (config.bounds.containsKey(presetKey)
                            ? ((java.lang.Double)
                                 config.bounds.get(presetKey)).doubleValue()
                            : 1.0));
                this.set$presetV(
                       (double)
                         (config.velocities.containsKey(presetKey)
                            ? ((java.lang.Double)
                                 config.velocities.get(presetKey)).doubleValue()
                            : 0.0));
                this.set$presetN(
                       (double)
                         (config.noises.containsKey(presetKey)
                            ? ((java.lang.Double)
                                 config.noises.get(presetKey)).doubleValue()
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
        
        public boolean getUsePreset() {
            return fabric.worker.Worker.getWorker().config.usePreset;
        }
        
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
        
        public double computeValue(fabric.worker.metrics.StatsMap weakStats) {
            return fabric.metrics.SampledMetric._Impl.
              static_computeValue((fabric.metrics.SampledMetric)
                                    this.$getProxy(), weakStats);
        }
        
        private static double static_computeValue(
          fabric.metrics.SampledMetric tmp,
          fabric.worker.metrics.StatsMap weakStats) {
            double result = 0;
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                fabric.worker.metrics.RunningMetricStats preloaded =
                  tmp.get$stats().preload(tmp.get$key());
                if (!fabric.lang.Object._Proxy.idEquals(preloaded,
                                                        tmp.get$stats()))
                    tmp.set$stats(preloaded);
                result = tmp.get$stats().getValue();
            }
            else {
                {
                    double result$var285 = result;
                    fabric.worker.transaction.TransactionManager $tm292 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled295 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff293 = 1;
                    boolean $doBackoff294 = true;
                    boolean $retry288 = true;
                    boolean $keepReads289 = false;
                    $label286: for (boolean $commit287 = false; !$commit287; ) {
                        if ($backoffEnabled295) {
                            if ($doBackoff294) {
                                if ($backoff293 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.
                                              sleep(
                                                java.lang.Math.
                                                    round(
                                                      java.lang.Math.random() *
                                                          $backoff293));
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e290) {
                                            
                                        }
                                    }
                                }
                                if ($backoff293 < 5000) $backoff293 *= 2;
                            }
                            $doBackoff294 = $backoff293 <= 32 || !$doBackoff294;
                        }
                        $commit287 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            try {
                                fabric.worker.metrics.RunningMetricStats
                                  preloaded =
                                  tmp.get$stats().preload(tmp.get$key());
                                if (!fabric.lang.Object._Proxy.idEquals(
                                                                 preloaded,
                                                                 tmp.get$stats(
                                                                       )))
                                    tmp.set$stats(preloaded);
                                result = tmp.get$stats().getValue();
                            }
                            catch (final fabric.worker.RetryException $e290) {
                                throw $e290;
                            }
                            catch (final fabric.worker.
                                     TransactionAbortingException $e290) {
                                throw $e290;
                            }
                            catch (final fabric.worker.
                                     TransactionRestartingException $e290) {
                                throw $e290;
                            }
                            catch (final fabric.worker.metrics.
                                     LockConflictException $e290) {
                                throw $e290;
                            }
                            catch (final Throwable $e290) {
                                $tm292.getCurrentLog().checkRetrySignal();
                                throw $e290;
                            }
                        }
                        catch (final fabric.worker.RetryException $e290) {
                            $commit287 = false;
                            continue $label286;
                        }
                        catch (fabric.worker.
                                 TransactionAbortingException $e290) {
                            $commit287 = false;
                            $retry288 = false;
                            $keepReads289 = $e290.keepReads;
                            if ($tm292.checkForStaleObjects()) {
                                $retry288 = true;
                                $keepReads289 = false;
                                continue $label286;
                            }
                            fabric.common.TransactionID $currentTid291 =
                              $tm292.getCurrentTid();
                            if ($e290.tid == null ||
                                  !$e290.tid.isDescendantOf($currentTid291)) {
                                throw $e290;
                            }
                            throw new fabric.worker.UserAbortException();
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e290) {
                            $commit287 = false;
                            fabric.common.TransactionID $currentTid291 =
                              $tm292.getCurrentTid();
                            if ($e290.tid.isDescendantOf($currentTid291))
                                continue $label286;
                            if ($currentTid291.parent != null) {
                                $retry288 = false;
                                throw $e290;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e290) {
                            $commit287 = false;
                            if ($tm292.checkForStaleObjects())
                                continue $label286;
                            fabric.common.TransactionID $currentTid291 =
                              $tm292.getCurrentTid();
                            if ($e290.tid.isDescendantOf($currentTid291)) {
                                $retry288 = true;
                            }
                            else if ($currentTid291.parent != null) {
                                $retry288 = false;
                                throw $e290;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e290) {
                            $commit287 = false;
                            if ($tm292.checkForStaleObjects())
                                continue $label286;
                            $retry288 = false;
                            throw new fabric.worker.AbortException($e290);
                        }
                        finally {
                            if ($commit287) {
                                fabric.common.TransactionID $currentTid291 =
                                  $tm292.getCurrentTid();
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e290) {
                                    $commit287 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionAbortingException $e290) {
                                    $commit287 = false;
                                    $retry288 = false;
                                    $keepReads289 = $e290.keepReads;
                                    if ($tm292.checkForStaleObjects()) {
                                        $retry288 = true;
                                        $keepReads289 = false;
                                        continue $label286;
                                    }
                                    if ($e290.tid ==
                                          null ||
                                          !$e290.tid.isDescendantOf(
                                                       $currentTid291))
                                        throw $e290;
                                    throw new fabric.worker.UserAbortException(
                                            );
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e290) {
                                    $commit287 = false;
                                    $currentTid291 = $tm292.getCurrentTid();
                                    if ($currentTid291 != null) {
                                        if ($e290.tid.equals($currentTid291) ||
                                              !$e290.tid.isDescendantOf(
                                                           $currentTid291)) {
                                            throw $e290;
                                        }
                                    }
                                }
                            }
                            else if ($keepReads289) {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransactionUpdates();
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit287) {
                                { result = result$var285; }
                                if ($retry288) { continue $label286; }
                            }
                        }
                    }
                }
            }
            return result;
        }
        
        public long computeSamples(fabric.worker.metrics.StatsMap weakStats) {
            return fabric.metrics.SampledMetric._Impl.
              static_computeSamples((fabric.metrics.SampledMetric)
                                      this.$getProxy(), weakStats);
        }
        
        private static long static_computeSamples(
          fabric.metrics.SampledMetric tmp,
          fabric.worker.metrics.StatsMap weakStats) {
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                fabric.worker.metrics.RunningMetricStats preloaded =
                  tmp.get$stats().preload(tmp.get$key());
                if (!fabric.lang.Object._Proxy.idEquals(preloaded,
                                                        tmp.get$stats()))
                    tmp.set$stats(preloaded);
                return tmp.get$stats().getSamples();
            }
            else {
                long rtn = 0;
                {
                    long rtn$var296 = rtn;
                    fabric.worker.transaction.TransactionManager $tm303 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled306 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff304 = 1;
                    boolean $doBackoff305 = true;
                    boolean $retry299 = true;
                    boolean $keepReads300 = false;
                    $label297: for (boolean $commit298 = false; !$commit298; ) {
                        if ($backoffEnabled306) {
                            if ($doBackoff305) {
                                if ($backoff304 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.
                                              sleep(
                                                java.lang.Math.
                                                    round(
                                                      java.lang.Math.random() *
                                                          $backoff304));
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e301) {
                                            
                                        }
                                    }
                                }
                                if ($backoff304 < 5000) $backoff304 *= 2;
                            }
                            $doBackoff305 = $backoff304 <= 32 || !$doBackoff305;
                        }
                        $commit298 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            try {
                                fabric.worker.metrics.RunningMetricStats
                                  preloaded =
                                  tmp.get$stats().preload(tmp.get$key());
                                if (!fabric.lang.Object._Proxy.idEquals(
                                                                 preloaded,
                                                                 tmp.get$stats(
                                                                       )))
                                    tmp.set$stats(preloaded);
                                rtn = tmp.get$stats().getSamples();
                            }
                            catch (final fabric.worker.RetryException $e301) {
                                throw $e301;
                            }
                            catch (final fabric.worker.
                                     TransactionAbortingException $e301) {
                                throw $e301;
                            }
                            catch (final fabric.worker.
                                     TransactionRestartingException $e301) {
                                throw $e301;
                            }
                            catch (final fabric.worker.metrics.
                                     LockConflictException $e301) {
                                throw $e301;
                            }
                            catch (final Throwable $e301) {
                                $tm303.getCurrentLog().checkRetrySignal();
                                throw $e301;
                            }
                        }
                        catch (final fabric.worker.RetryException $e301) {
                            $commit298 = false;
                            continue $label297;
                        }
                        catch (fabric.worker.
                                 TransactionAbortingException $e301) {
                            $commit298 = false;
                            $retry299 = false;
                            $keepReads300 = $e301.keepReads;
                            if ($tm303.checkForStaleObjects()) {
                                $retry299 = true;
                                $keepReads300 = false;
                                continue $label297;
                            }
                            fabric.common.TransactionID $currentTid302 =
                              $tm303.getCurrentTid();
                            if ($e301.tid == null ||
                                  !$e301.tid.isDescendantOf($currentTid302)) {
                                throw $e301;
                            }
                            throw new fabric.worker.UserAbortException();
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e301) {
                            $commit298 = false;
                            fabric.common.TransactionID $currentTid302 =
                              $tm303.getCurrentTid();
                            if ($e301.tid.isDescendantOf($currentTid302))
                                continue $label297;
                            if ($currentTid302.parent != null) {
                                $retry299 = false;
                                throw $e301;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e301) {
                            $commit298 = false;
                            if ($tm303.checkForStaleObjects())
                                continue $label297;
                            fabric.common.TransactionID $currentTid302 =
                              $tm303.getCurrentTid();
                            if ($e301.tid.isDescendantOf($currentTid302)) {
                                $retry299 = true;
                            }
                            else if ($currentTid302.parent != null) {
                                $retry299 = false;
                                throw $e301;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e301) {
                            $commit298 = false;
                            if ($tm303.checkForStaleObjects())
                                continue $label297;
                            $retry299 = false;
                            throw new fabric.worker.AbortException($e301);
                        }
                        finally {
                            if ($commit298) {
                                fabric.common.TransactionID $currentTid302 =
                                  $tm303.getCurrentTid();
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e301) {
                                    $commit298 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionAbortingException $e301) {
                                    $commit298 = false;
                                    $retry299 = false;
                                    $keepReads300 = $e301.keepReads;
                                    if ($tm303.checkForStaleObjects()) {
                                        $retry299 = true;
                                        $keepReads300 = false;
                                        continue $label297;
                                    }
                                    if ($e301.tid ==
                                          null ||
                                          !$e301.tid.isDescendantOf(
                                                       $currentTid302))
                                        throw $e301;
                                    throw new fabric.worker.UserAbortException(
                                            );
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e301) {
                                    $commit298 = false;
                                    $currentTid302 = $tm303.getCurrentTid();
                                    if ($currentTid302 != null) {
                                        if ($e301.tid.equals($currentTid302) ||
                                              !$e301.tid.isDescendantOf(
                                                           $currentTid302)) {
                                            throw $e301;
                                        }
                                    }
                                }
                            }
                            else if ($keepReads300) {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransactionUpdates();
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit298) {
                                { rtn = rtn$var296; }
                                if ($retry299) { continue $label297; }
                            }
                        }
                    }
                }
                return rtn;
            }
        }
        
        public long computeLastUpdate(
          fabric.worker.metrics.StatsMap weakStats) {
            return fabric.metrics.SampledMetric._Impl.
              static_computeLastUpdate((fabric.metrics.SampledMetric)
                                         this.$getProxy(), weakStats);
        }
        
        private static long static_computeLastUpdate(
          fabric.metrics.SampledMetric tmp,
          fabric.worker.metrics.StatsMap weakStats) {
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                fabric.worker.metrics.RunningMetricStats preloaded =
                  tmp.get$stats().preload(tmp.get$key());
                if (!fabric.lang.Object._Proxy.idEquals(preloaded,
                                                        tmp.get$stats()))
                    tmp.set$stats(preloaded);
                return tmp.get$stats().getLastUpdate();
            }
            else {
                long rtn = 0;
                {
                    long rtn$var307 = rtn;
                    fabric.worker.transaction.TransactionManager $tm314 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled317 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff315 = 1;
                    boolean $doBackoff316 = true;
                    boolean $retry310 = true;
                    boolean $keepReads311 = false;
                    $label308: for (boolean $commit309 = false; !$commit309; ) {
                        if ($backoffEnabled317) {
                            if ($doBackoff316) {
                                if ($backoff315 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.
                                              sleep(
                                                java.lang.Math.
                                                    round(
                                                      java.lang.Math.random() *
                                                          $backoff315));
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
                        $commit309 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            try {
                                fabric.worker.metrics.RunningMetricStats
                                  preloaded =
                                  tmp.get$stats().preload(tmp.get$key());
                                if (!fabric.lang.Object._Proxy.idEquals(
                                                                 preloaded,
                                                                 tmp.get$stats(
                                                                       )))
                                    tmp.set$stats(preloaded);
                                rtn = tmp.get$stats().getLastUpdate();
                            }
                            catch (final fabric.worker.RetryException $e312) {
                                throw $e312;
                            }
                            catch (final fabric.worker.
                                     TransactionAbortingException $e312) {
                                throw $e312;
                            }
                            catch (final fabric.worker.
                                     TransactionRestartingException $e312) {
                                throw $e312;
                            }
                            catch (final fabric.worker.metrics.
                                     LockConflictException $e312) {
                                throw $e312;
                            }
                            catch (final Throwable $e312) {
                                $tm314.getCurrentLog().checkRetrySignal();
                                throw $e312;
                            }
                        }
                        catch (final fabric.worker.RetryException $e312) {
                            $commit309 = false;
                            continue $label308;
                        }
                        catch (fabric.worker.
                                 TransactionAbortingException $e312) {
                            $commit309 = false;
                            $retry310 = false;
                            $keepReads311 = $e312.keepReads;
                            if ($tm314.checkForStaleObjects()) {
                                $retry310 = true;
                                $keepReads311 = false;
                                continue $label308;
                            }
                            fabric.common.TransactionID $currentTid313 =
                              $tm314.getCurrentTid();
                            if ($e312.tid == null ||
                                  !$e312.tid.isDescendantOf($currentTid313)) {
                                throw $e312;
                            }
                            throw new fabric.worker.UserAbortException();
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e312) {
                            $commit309 = false;
                            fabric.common.TransactionID $currentTid313 =
                              $tm314.getCurrentTid();
                            if ($e312.tid.isDescendantOf($currentTid313))
                                continue $label308;
                            if ($currentTid313.parent != null) {
                                $retry310 = false;
                                throw $e312;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e312) {
                            $commit309 = false;
                            if ($tm314.checkForStaleObjects())
                                continue $label308;
                            fabric.common.TransactionID $currentTid313 =
                              $tm314.getCurrentTid();
                            if ($e312.tid.isDescendantOf($currentTid313)) {
                                $retry310 = true;
                            }
                            else if ($currentTid313.parent != null) {
                                $retry310 = false;
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
                            $commit309 = false;
                            if ($tm314.checkForStaleObjects())
                                continue $label308;
                            $retry310 = false;
                            throw new fabric.worker.AbortException($e312);
                        }
                        finally {
                            if ($commit309) {
                                fabric.common.TransactionID $currentTid313 =
                                  $tm314.getCurrentTid();
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e312) {
                                    $commit309 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionAbortingException $e312) {
                                    $commit309 = false;
                                    $retry310 = false;
                                    $keepReads311 = $e312.keepReads;
                                    if ($tm314.checkForStaleObjects()) {
                                        $retry310 = true;
                                        $keepReads311 = false;
                                        continue $label308;
                                    }
                                    if ($e312.tid ==
                                          null ||
                                          !$e312.tid.isDescendantOf(
                                                       $currentTid313))
                                        throw $e312;
                                    throw new fabric.worker.UserAbortException(
                                            );
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e312) {
                                    $commit309 = false;
                                    $currentTid313 = $tm314.getCurrentTid();
                                    if ($currentTid313 != null) {
                                        if ($e312.tid.equals($currentTid313) ||
                                              !$e312.tid.isDescendantOf(
                                                           $currentTid313)) {
                                            throw $e312;
                                        }
                                    }
                                }
                            }
                            else if ($keepReads311) {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransactionUpdates();
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit309) {
                                { rtn = rtn$var307; }
                                if ($retry310) { continue $label308; }
                            }
                        }
                    }
                }
                return rtn;
            }
        }
        
        public double computeUpdateInterval(
          fabric.worker.metrics.StatsMap weakStats) {
            return fabric.metrics.SampledMetric._Impl.
              static_computeUpdateInterval((fabric.metrics.SampledMetric)
                                             this.$getProxy(), weakStats);
        }
        
        private static double static_computeUpdateInterval(
          fabric.metrics.SampledMetric tmp,
          fabric.worker.metrics.StatsMap weakStats) {
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                fabric.worker.metrics.RunningMetricStats preloaded =
                  tmp.get$stats().preload(tmp.get$key());
                if (!fabric.lang.Object._Proxy.idEquals(preloaded,
                                                        tmp.get$stats()))
                    tmp.set$stats(preloaded);
                return tmp.get$stats().getIntervalEstimate();
            }
            else {
                double rtn = 0;
                {
                    double rtn$var318 = rtn;
                    fabric.worker.transaction.TransactionManager $tm325 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled328 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff326 = 1;
                    boolean $doBackoff327 = true;
                    boolean $retry321 = true;
                    boolean $keepReads322 = false;
                    $label319: for (boolean $commit320 = false; !$commit320; ) {
                        if ($backoffEnabled328) {
                            if ($doBackoff327) {
                                if ($backoff326 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.
                                              sleep(
                                                java.lang.Math.
                                                    round(
                                                      java.lang.Math.random() *
                                                          $backoff326));
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e323) {
                                            
                                        }
                                    }
                                }
                                if ($backoff326 < 5000) $backoff326 *= 2;
                            }
                            $doBackoff327 = $backoff326 <= 32 || !$doBackoff327;
                        }
                        $commit320 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            try {
                                fabric.worker.metrics.RunningMetricStats
                                  preloaded =
                                  tmp.get$stats().preload(tmp.get$key());
                                if (!fabric.lang.Object._Proxy.idEquals(
                                                                 preloaded,
                                                                 tmp.get$stats(
                                                                       )))
                                    tmp.set$stats(preloaded);
                                rtn = tmp.get$stats().getIntervalEstimate();
                            }
                            catch (final fabric.worker.RetryException $e323) {
                                throw $e323;
                            }
                            catch (final fabric.worker.
                                     TransactionAbortingException $e323) {
                                throw $e323;
                            }
                            catch (final fabric.worker.
                                     TransactionRestartingException $e323) {
                                throw $e323;
                            }
                            catch (final fabric.worker.metrics.
                                     LockConflictException $e323) {
                                throw $e323;
                            }
                            catch (final Throwable $e323) {
                                $tm325.getCurrentLog().checkRetrySignal();
                                throw $e323;
                            }
                        }
                        catch (final fabric.worker.RetryException $e323) {
                            $commit320 = false;
                            continue $label319;
                        }
                        catch (fabric.worker.
                                 TransactionAbortingException $e323) {
                            $commit320 = false;
                            $retry321 = false;
                            $keepReads322 = $e323.keepReads;
                            if ($tm325.checkForStaleObjects()) {
                                $retry321 = true;
                                $keepReads322 = false;
                                continue $label319;
                            }
                            fabric.common.TransactionID $currentTid324 =
                              $tm325.getCurrentTid();
                            if ($e323.tid == null ||
                                  !$e323.tid.isDescendantOf($currentTid324)) {
                                throw $e323;
                            }
                            throw new fabric.worker.UserAbortException();
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e323) {
                            $commit320 = false;
                            fabric.common.TransactionID $currentTid324 =
                              $tm325.getCurrentTid();
                            if ($e323.tid.isDescendantOf($currentTid324))
                                continue $label319;
                            if ($currentTid324.parent != null) {
                                $retry321 = false;
                                throw $e323;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e323) {
                            $commit320 = false;
                            if ($tm325.checkForStaleObjects())
                                continue $label319;
                            fabric.common.TransactionID $currentTid324 =
                              $tm325.getCurrentTid();
                            if ($e323.tid.isDescendantOf($currentTid324)) {
                                $retry321 = true;
                            }
                            else if ($currentTid324.parent != null) {
                                $retry321 = false;
                                throw $e323;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e323) {
                            $commit320 = false;
                            if ($tm325.checkForStaleObjects())
                                continue $label319;
                            $retry321 = false;
                            throw new fabric.worker.AbortException($e323);
                        }
                        finally {
                            if ($commit320) {
                                fabric.common.TransactionID $currentTid324 =
                                  $tm325.getCurrentTid();
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e323) {
                                    $commit320 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionAbortingException $e323) {
                                    $commit320 = false;
                                    $retry321 = false;
                                    $keepReads322 = $e323.keepReads;
                                    if ($tm325.checkForStaleObjects()) {
                                        $retry321 = true;
                                        $keepReads322 = false;
                                        continue $label319;
                                    }
                                    if ($e323.tid ==
                                          null ||
                                          !$e323.tid.isDescendantOf(
                                                       $currentTid324))
                                        throw $e323;
                                    throw new fabric.worker.UserAbortException(
                                            );
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e323) {
                                    $commit320 = false;
                                    $currentTid324 = $tm325.getCurrentTid();
                                    if ($currentTid324 != null) {
                                        if ($e323.tid.equals($currentTid324) ||
                                              !$e323.tid.isDescendantOf(
                                                           $currentTid324)) {
                                            throw $e323;
                                        }
                                    }
                                }
                            }
                            else if ($keepReads322) {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransactionUpdates();
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit320) {
                                { rtn = rtn$var318; }
                                if ($retry321) { continue $label319; }
                            }
                        }
                    }
                }
                return rtn;
            }
        }
        
        public double computeVelocity(
          fabric.worker.metrics.StatsMap weakStats) {
            return fabric.metrics.SampledMetric._Impl.
              static_computeVelocity((fabric.metrics.SampledMetric)
                                       this.$getProxy(), weakStats);
        }
        
        private static double static_computeVelocity(
          fabric.metrics.SampledMetric tmp,
          fabric.worker.metrics.StatsMap weakStats) {
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                if (tmp.getUsePreset()) {
                    return tmp.get$presetV();
                }
                else {
                    fabric.worker.metrics.RunningMetricStats preloaded =
                      tmp.get$stats().preload(tmp.get$key());
                    if (!fabric.lang.Object._Proxy.idEquals(preloaded,
                                                            tmp.get$stats()))
                        tmp.set$stats(preloaded);
                    return tmp.get$stats().getVelocityEstimate();
                }
            }
            else {
                double rtn = 0;
                {
                    double rtn$var329 = rtn;
                    fabric.worker.transaction.TransactionManager $tm336 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled339 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff337 = 1;
                    boolean $doBackoff338 = true;
                    boolean $retry332 = true;
                    boolean $keepReads333 = false;
                    $label330: for (boolean $commit331 = false; !$commit331; ) {
                        if ($backoffEnabled339) {
                            if ($doBackoff338) {
                                if ($backoff337 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.
                                              sleep(
                                                java.lang.Math.
                                                    round(
                                                      java.lang.Math.random() *
                                                          $backoff337));
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e334) {
                                            
                                        }
                                    }
                                }
                                if ($backoff337 < 5000) $backoff337 *= 2;
                            }
                            $doBackoff338 = $backoff337 <= 32 || !$doBackoff338;
                        }
                        $commit331 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            try {
                                if (tmp.getUsePreset()) {
                                    rtn = tmp.get$presetV();
                                }
                                else {
                                    fabric.worker.metrics.RunningMetricStats
                                      preloaded =
                                      tmp.get$stats().preload(tmp.get$key());
                                    if (!fabric.lang.Object._Proxy.
                                          idEquals(preloaded, tmp.get$stats()))
                                        tmp.set$stats(preloaded);
                                    rtn = tmp.get$stats().getVelocityEstimate();
                                }
                            }
                            catch (final fabric.worker.RetryException $e334) {
                                throw $e334;
                            }
                            catch (final fabric.worker.
                                     TransactionAbortingException $e334) {
                                throw $e334;
                            }
                            catch (final fabric.worker.
                                     TransactionRestartingException $e334) {
                                throw $e334;
                            }
                            catch (final fabric.worker.metrics.
                                     LockConflictException $e334) {
                                throw $e334;
                            }
                            catch (final Throwable $e334) {
                                $tm336.getCurrentLog().checkRetrySignal();
                                throw $e334;
                            }
                        }
                        catch (final fabric.worker.RetryException $e334) {
                            $commit331 = false;
                            continue $label330;
                        }
                        catch (fabric.worker.
                                 TransactionAbortingException $e334) {
                            $commit331 = false;
                            $retry332 = false;
                            $keepReads333 = $e334.keepReads;
                            if ($tm336.checkForStaleObjects()) {
                                $retry332 = true;
                                $keepReads333 = false;
                                continue $label330;
                            }
                            fabric.common.TransactionID $currentTid335 =
                              $tm336.getCurrentTid();
                            if ($e334.tid == null ||
                                  !$e334.tid.isDescendantOf($currentTid335)) {
                                throw $e334;
                            }
                            throw new fabric.worker.UserAbortException();
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e334) {
                            $commit331 = false;
                            fabric.common.TransactionID $currentTid335 =
                              $tm336.getCurrentTid();
                            if ($e334.tid.isDescendantOf($currentTid335))
                                continue $label330;
                            if ($currentTid335.parent != null) {
                                $retry332 = false;
                                throw $e334;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e334) {
                            $commit331 = false;
                            if ($tm336.checkForStaleObjects())
                                continue $label330;
                            fabric.common.TransactionID $currentTid335 =
                              $tm336.getCurrentTid();
                            if ($e334.tid.isDescendantOf($currentTid335)) {
                                $retry332 = true;
                            }
                            else if ($currentTid335.parent != null) {
                                $retry332 = false;
                                throw $e334;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e334) {
                            $commit331 = false;
                            if ($tm336.checkForStaleObjects())
                                continue $label330;
                            $retry332 = false;
                            throw new fabric.worker.AbortException($e334);
                        }
                        finally {
                            if ($commit331) {
                                fabric.common.TransactionID $currentTid335 =
                                  $tm336.getCurrentTid();
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e334) {
                                    $commit331 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionAbortingException $e334) {
                                    $commit331 = false;
                                    $retry332 = false;
                                    $keepReads333 = $e334.keepReads;
                                    if ($tm336.checkForStaleObjects()) {
                                        $retry332 = true;
                                        $keepReads333 = false;
                                        continue $label330;
                                    }
                                    if ($e334.tid ==
                                          null ||
                                          !$e334.tid.isDescendantOf(
                                                       $currentTid335))
                                        throw $e334;
                                    throw new fabric.worker.UserAbortException(
                                            );
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e334) {
                                    $commit331 = false;
                                    $currentTid335 = $tm336.getCurrentTid();
                                    if ($currentTid335 != null) {
                                        if ($e334.tid.equals($currentTid335) ||
                                              !$e334.tid.isDescendantOf(
                                                           $currentTid335)) {
                                            throw $e334;
                                        }
                                    }
                                }
                            }
                            else if ($keepReads333) {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransactionUpdates();
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit331) {
                                { rtn = rtn$var329; }
                                if ($retry332) { continue $label330; }
                            }
                        }
                    }
                }
                return rtn;
            }
        }
        
        public double computeNoise(fabric.worker.metrics.StatsMap weakStats) {
            return fabric.metrics.SampledMetric._Impl.
              static_computeNoise((fabric.metrics.SampledMetric)
                                    this.$getProxy(), weakStats);
        }
        
        private static double static_computeNoise(
          fabric.metrics.SampledMetric tmp,
          fabric.worker.metrics.StatsMap weakStats) {
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                if (tmp.getUsePreset()) {
                    return tmp.get$presetN();
                }
                else {
                    fabric.worker.metrics.RunningMetricStats preloaded =
                      tmp.get$stats().preload(tmp.get$key());
                    if (!fabric.lang.Object._Proxy.idEquals(preloaded,
                                                            tmp.get$stats()))
                        tmp.set$stats(preloaded);
                    return tmp.get$stats().getNoiseEstimate();
                }
            }
            else {
                double rtn = 0;
                {
                    double rtn$var340 = rtn;
                    fabric.worker.transaction.TransactionManager $tm347 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled350 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff348 = 1;
                    boolean $doBackoff349 = true;
                    boolean $retry343 = true;
                    boolean $keepReads344 = false;
                    $label341: for (boolean $commit342 = false; !$commit342; ) {
                        if ($backoffEnabled350) {
                            if ($doBackoff349) {
                                if ($backoff348 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.
                                              sleep(
                                                java.lang.Math.
                                                    round(
                                                      java.lang.Math.random() *
                                                          $backoff348));
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e345) {
                                            
                                        }
                                    }
                                }
                                if ($backoff348 < 5000) $backoff348 *= 2;
                            }
                            $doBackoff349 = $backoff348 <= 32 || !$doBackoff349;
                        }
                        $commit342 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            try {
                                if (tmp.getUsePreset()) {
                                    rtn = tmp.get$presetN();
                                }
                                else {
                                    fabric.worker.metrics.RunningMetricStats
                                      preloaded =
                                      tmp.get$stats().preload(tmp.get$key());
                                    if (!fabric.lang.Object._Proxy.
                                          idEquals(preloaded, tmp.get$stats()))
                                        tmp.set$stats(preloaded);
                                    rtn = tmp.get$stats().getNoiseEstimate();
                                }
                            }
                            catch (final fabric.worker.RetryException $e345) {
                                throw $e345;
                            }
                            catch (final fabric.worker.
                                     TransactionAbortingException $e345) {
                                throw $e345;
                            }
                            catch (final fabric.worker.
                                     TransactionRestartingException $e345) {
                                throw $e345;
                            }
                            catch (final fabric.worker.metrics.
                                     LockConflictException $e345) {
                                throw $e345;
                            }
                            catch (final Throwable $e345) {
                                $tm347.getCurrentLog().checkRetrySignal();
                                throw $e345;
                            }
                        }
                        catch (final fabric.worker.RetryException $e345) {
                            $commit342 = false;
                            continue $label341;
                        }
                        catch (fabric.worker.
                                 TransactionAbortingException $e345) {
                            $commit342 = false;
                            $retry343 = false;
                            $keepReads344 = $e345.keepReads;
                            if ($tm347.checkForStaleObjects()) {
                                $retry343 = true;
                                $keepReads344 = false;
                                continue $label341;
                            }
                            fabric.common.TransactionID $currentTid346 =
                              $tm347.getCurrentTid();
                            if ($e345.tid == null ||
                                  !$e345.tid.isDescendantOf($currentTid346)) {
                                throw $e345;
                            }
                            throw new fabric.worker.UserAbortException();
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e345) {
                            $commit342 = false;
                            fabric.common.TransactionID $currentTid346 =
                              $tm347.getCurrentTid();
                            if ($e345.tid.isDescendantOf($currentTid346))
                                continue $label341;
                            if ($currentTid346.parent != null) {
                                $retry343 = false;
                                throw $e345;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e345) {
                            $commit342 = false;
                            if ($tm347.checkForStaleObjects())
                                continue $label341;
                            fabric.common.TransactionID $currentTid346 =
                              $tm347.getCurrentTid();
                            if ($e345.tid.isDescendantOf($currentTid346)) {
                                $retry343 = true;
                            }
                            else if ($currentTid346.parent != null) {
                                $retry343 = false;
                                throw $e345;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e345) {
                            $commit342 = false;
                            if ($tm347.checkForStaleObjects())
                                continue $label341;
                            $retry343 = false;
                            throw new fabric.worker.AbortException($e345);
                        }
                        finally {
                            if ($commit342) {
                                fabric.common.TransactionID $currentTid346 =
                                  $tm347.getCurrentTid();
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e345) {
                                    $commit342 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionAbortingException $e345) {
                                    $commit342 = false;
                                    $retry343 = false;
                                    $keepReads344 = $e345.keepReads;
                                    if ($tm347.checkForStaleObjects()) {
                                        $retry343 = true;
                                        $keepReads344 = false;
                                        continue $label341;
                                    }
                                    if ($e345.tid ==
                                          null ||
                                          !$e345.tid.isDescendantOf(
                                                       $currentTid346))
                                        throw $e345;
                                    throw new fabric.worker.UserAbortException(
                                            );
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e345) {
                                    $commit342 = false;
                                    $currentTid346 = $tm347.getCurrentTid();
                                    if ($currentTid346 != null) {
                                        if ($e345.tid.equals($currentTid346) ||
                                              !$e345.tid.isDescendantOf(
                                                           $currentTid346)) {
                                            throw $e345;
                                        }
                                    }
                                }
                            }
                            else if ($keepReads344) {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransactionUpdates();
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit342) {
                                { rtn = rtn$var340; }
                                if ($retry343) { continue $label341; }
                            }
                        }
                    }
                }
                return rtn;
            }
        }
        
        public java.lang.String toString() {
            return "SampledMetric(" + this.get$key() + ")@" + getStore().name();
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
            fabric.worker.metrics.RunningMetricStats preloaded =
              this.get$stats().preload(this.get$key());
            if (!fabric.lang.Object._Proxy.idEquals(preloaded,
                                                    this.get$stats()))
                this.set$stats(preloaded);
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
        
        public double value(fabric.worker.metrics.StatsMap weakStats) {
            return fabric.metrics.SampledMetric._Impl.
              static_value((fabric.metrics.SampledMetric) this.$getProxy(),
                           weakStats);
        }
        
        private static double static_value(
          fabric.metrics.SampledMetric tmp,
          fabric.worker.metrics.StatsMap weakStats) {
            if (weakStats.containsKey(tmp)) return weakStats.getValue(tmp);
            double result = 0;
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                result = tmp.computeValue(weakStats);
            }
            else {
                {
                    double result$var351 = result;
                    fabric.worker.transaction.TransactionManager $tm358 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled361 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff359 = 1;
                    boolean $doBackoff360 = true;
                    boolean $retry354 = true;
                    boolean $keepReads355 = false;
                    $label352: for (boolean $commit353 = false; !$commit353; ) {
                        if ($backoffEnabled361) {
                            if ($doBackoff360) {
                                if ($backoff359 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.
                                              sleep(
                                                java.lang.Math.
                                                    round(
                                                      java.lang.Math.random() *
                                                          $backoff359));
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
                        $commit353 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            try { result = tmp.computeValue(weakStats); }
                            catch (final fabric.worker.RetryException $e356) {
                                throw $e356;
                            }
                            catch (final fabric.worker.
                                     TransactionAbortingException $e356) {
                                throw $e356;
                            }
                            catch (final fabric.worker.
                                     TransactionRestartingException $e356) {
                                throw $e356;
                            }
                            catch (final fabric.worker.metrics.
                                     LockConflictException $e356) {
                                throw $e356;
                            }
                            catch (final Throwable $e356) {
                                $tm358.getCurrentLog().checkRetrySignal();
                                throw $e356;
                            }
                        }
                        catch (final fabric.worker.RetryException $e356) {
                            $commit353 = false;
                            continue $label352;
                        }
                        catch (fabric.worker.
                                 TransactionAbortingException $e356) {
                            $commit353 = false;
                            $retry354 = false;
                            $keepReads355 = $e356.keepReads;
                            if ($tm358.checkForStaleObjects()) {
                                $retry354 = true;
                                $keepReads355 = false;
                                continue $label352;
                            }
                            fabric.common.TransactionID $currentTid357 =
                              $tm358.getCurrentTid();
                            if ($e356.tid == null ||
                                  !$e356.tid.isDescendantOf($currentTid357)) {
                                throw $e356;
                            }
                            throw new fabric.worker.UserAbortException();
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e356) {
                            $commit353 = false;
                            fabric.common.TransactionID $currentTid357 =
                              $tm358.getCurrentTid();
                            if ($e356.tid.isDescendantOf($currentTid357))
                                continue $label352;
                            if ($currentTid357.parent != null) {
                                $retry354 = false;
                                throw $e356;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e356) {
                            $commit353 = false;
                            if ($tm358.checkForStaleObjects())
                                continue $label352;
                            fabric.common.TransactionID $currentTid357 =
                              $tm358.getCurrentTid();
                            if ($e356.tid.isDescendantOf($currentTid357)) {
                                $retry354 = true;
                            }
                            else if ($currentTid357.parent != null) {
                                $retry354 = false;
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
                            $commit353 = false;
                            if ($tm358.checkForStaleObjects())
                                continue $label352;
                            $retry354 = false;
                            throw new fabric.worker.AbortException($e356);
                        }
                        finally {
                            if ($commit353) {
                                fabric.common.TransactionID $currentTid357 =
                                  $tm358.getCurrentTid();
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e356) {
                                    $commit353 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionAbortingException $e356) {
                                    $commit353 = false;
                                    $retry354 = false;
                                    $keepReads355 = $e356.keepReads;
                                    if ($tm358.checkForStaleObjects()) {
                                        $retry354 = true;
                                        $keepReads355 = false;
                                        continue $label352;
                                    }
                                    if ($e356.tid ==
                                          null ||
                                          !$e356.tid.isDescendantOf(
                                                       $currentTid357))
                                        throw $e356;
                                    throw new fabric.worker.UserAbortException(
                                            );
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e356) {
                                    $commit353 = false;
                                    $currentTid357 = $tm358.getCurrentTid();
                                    if ($currentTid357 != null) {
                                        if ($e356.tid.equals($currentTid357) ||
                                              !$e356.tid.isDescendantOf(
                                                           $currentTid357)) {
                                            throw $e356;
                                        }
                                    }
                                }
                            }
                            else if ($keepReads355) {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransactionUpdates();
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit353) {
                                { result = result$var351; }
                                if ($retry354) { continue $label352; }
                            }
                        }
                    }
                }
            }
            return result;
        }
        
        public long samples(fabric.worker.metrics.StatsMap weakStats) {
            return fabric.metrics.SampledMetric._Impl.
              static_samples((fabric.metrics.SampledMetric) this.$getProxy(),
                             weakStats);
        }
        
        private static long static_samples(
          fabric.metrics.SampledMetric tmp,
          fabric.worker.metrics.StatsMap weakStats) {
            if (weakStats.containsKey(tmp)) return weakStats.getSamples(tmp);
            long result = 0;
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                result = tmp.computeSamples(weakStats);
            }
            else {
                {
                    long result$var362 = result;
                    fabric.worker.transaction.TransactionManager $tm369 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled372 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff370 = 1;
                    boolean $doBackoff371 = true;
                    boolean $retry365 = true;
                    boolean $keepReads366 = false;
                    $label363: for (boolean $commit364 = false; !$commit364; ) {
                        if ($backoffEnabled372) {
                            if ($doBackoff371) {
                                if ($backoff370 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.
                                              sleep(
                                                java.lang.Math.
                                                    round(
                                                      java.lang.Math.random() *
                                                          $backoff370));
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e367) {
                                            
                                        }
                                    }
                                }
                                if ($backoff370 < 5000) $backoff370 *= 2;
                            }
                            $doBackoff371 = $backoff370 <= 32 || !$doBackoff371;
                        }
                        $commit364 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            try { result = tmp.computeSamples(weakStats); }
                            catch (final fabric.worker.RetryException $e367) {
                                throw $e367;
                            }
                            catch (final fabric.worker.
                                     TransactionAbortingException $e367) {
                                throw $e367;
                            }
                            catch (final fabric.worker.
                                     TransactionRestartingException $e367) {
                                throw $e367;
                            }
                            catch (final fabric.worker.metrics.
                                     LockConflictException $e367) {
                                throw $e367;
                            }
                            catch (final Throwable $e367) {
                                $tm369.getCurrentLog().checkRetrySignal();
                                throw $e367;
                            }
                        }
                        catch (final fabric.worker.RetryException $e367) {
                            $commit364 = false;
                            continue $label363;
                        }
                        catch (fabric.worker.
                                 TransactionAbortingException $e367) {
                            $commit364 = false;
                            $retry365 = false;
                            $keepReads366 = $e367.keepReads;
                            if ($tm369.checkForStaleObjects()) {
                                $retry365 = true;
                                $keepReads366 = false;
                                continue $label363;
                            }
                            fabric.common.TransactionID $currentTid368 =
                              $tm369.getCurrentTid();
                            if ($e367.tid == null ||
                                  !$e367.tid.isDescendantOf($currentTid368)) {
                                throw $e367;
                            }
                            throw new fabric.worker.UserAbortException();
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e367) {
                            $commit364 = false;
                            fabric.common.TransactionID $currentTid368 =
                              $tm369.getCurrentTid();
                            if ($e367.tid.isDescendantOf($currentTid368))
                                continue $label363;
                            if ($currentTid368.parent != null) {
                                $retry365 = false;
                                throw $e367;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e367) {
                            $commit364 = false;
                            if ($tm369.checkForStaleObjects())
                                continue $label363;
                            fabric.common.TransactionID $currentTid368 =
                              $tm369.getCurrentTid();
                            if ($e367.tid.isDescendantOf($currentTid368)) {
                                $retry365 = true;
                            }
                            else if ($currentTid368.parent != null) {
                                $retry365 = false;
                                throw $e367;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e367) {
                            $commit364 = false;
                            if ($tm369.checkForStaleObjects())
                                continue $label363;
                            $retry365 = false;
                            throw new fabric.worker.AbortException($e367);
                        }
                        finally {
                            if ($commit364) {
                                fabric.common.TransactionID $currentTid368 =
                                  $tm369.getCurrentTid();
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e367) {
                                    $commit364 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionAbortingException $e367) {
                                    $commit364 = false;
                                    $retry365 = false;
                                    $keepReads366 = $e367.keepReads;
                                    if ($tm369.checkForStaleObjects()) {
                                        $retry365 = true;
                                        $keepReads366 = false;
                                        continue $label363;
                                    }
                                    if ($e367.tid ==
                                          null ||
                                          !$e367.tid.isDescendantOf(
                                                       $currentTid368))
                                        throw $e367;
                                    throw new fabric.worker.UserAbortException(
                                            );
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e367) {
                                    $commit364 = false;
                                    $currentTid368 = $tm369.getCurrentTid();
                                    if ($currentTid368 != null) {
                                        if ($e367.tid.equals($currentTid368) ||
                                              !$e367.tid.isDescendantOf(
                                                           $currentTid368)) {
                                            throw $e367;
                                        }
                                    }
                                }
                            }
                            else if ($keepReads366) {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransactionUpdates();
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit364) {
                                { result = result$var362; }
                                if ($retry365) { continue $label363; }
                            }
                        }
                    }
                }
            }
            return result;
        }
        
        public long lastUpdate(fabric.worker.metrics.StatsMap weakStats) {
            return fabric.metrics.SampledMetric._Impl.
              static_lastUpdate((fabric.metrics.SampledMetric) this.$getProxy(),
                                weakStats);
        }
        
        private static long static_lastUpdate(
          fabric.metrics.SampledMetric tmp,
          fabric.worker.metrics.StatsMap weakStats) {
            if (weakStats.containsKey(tmp)) return weakStats.getLastUpdate(tmp);
            long result = 0;
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                result = tmp.computeLastUpdate(weakStats);
            }
            else {
                {
                    long result$var373 = result;
                    fabric.worker.transaction.TransactionManager $tm380 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled383 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff381 = 1;
                    boolean $doBackoff382 = true;
                    boolean $retry376 = true;
                    boolean $keepReads377 = false;
                    $label374: for (boolean $commit375 = false; !$commit375; ) {
                        if ($backoffEnabled383) {
                            if ($doBackoff382) {
                                if ($backoff381 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.
                                              sleep(
                                                java.lang.Math.
                                                    round(
                                                      java.lang.Math.random() *
                                                          $backoff381));
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e378) {
                                            
                                        }
                                    }
                                }
                                if ($backoff381 < 5000) $backoff381 *= 2;
                            }
                            $doBackoff382 = $backoff381 <= 32 || !$doBackoff382;
                        }
                        $commit375 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            try { result = tmp.computeLastUpdate(weakStats); }
                            catch (final fabric.worker.RetryException $e378) {
                                throw $e378;
                            }
                            catch (final fabric.worker.
                                     TransactionAbortingException $e378) {
                                throw $e378;
                            }
                            catch (final fabric.worker.
                                     TransactionRestartingException $e378) {
                                throw $e378;
                            }
                            catch (final fabric.worker.metrics.
                                     LockConflictException $e378) {
                                throw $e378;
                            }
                            catch (final Throwable $e378) {
                                $tm380.getCurrentLog().checkRetrySignal();
                                throw $e378;
                            }
                        }
                        catch (final fabric.worker.RetryException $e378) {
                            $commit375 = false;
                            continue $label374;
                        }
                        catch (fabric.worker.
                                 TransactionAbortingException $e378) {
                            $commit375 = false;
                            $retry376 = false;
                            $keepReads377 = $e378.keepReads;
                            if ($tm380.checkForStaleObjects()) {
                                $retry376 = true;
                                $keepReads377 = false;
                                continue $label374;
                            }
                            fabric.common.TransactionID $currentTid379 =
                              $tm380.getCurrentTid();
                            if ($e378.tid == null ||
                                  !$e378.tid.isDescendantOf($currentTid379)) {
                                throw $e378;
                            }
                            throw new fabric.worker.UserAbortException();
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e378) {
                            $commit375 = false;
                            fabric.common.TransactionID $currentTid379 =
                              $tm380.getCurrentTid();
                            if ($e378.tid.isDescendantOf($currentTid379))
                                continue $label374;
                            if ($currentTid379.parent != null) {
                                $retry376 = false;
                                throw $e378;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e378) {
                            $commit375 = false;
                            if ($tm380.checkForStaleObjects())
                                continue $label374;
                            fabric.common.TransactionID $currentTid379 =
                              $tm380.getCurrentTid();
                            if ($e378.tid.isDescendantOf($currentTid379)) {
                                $retry376 = true;
                            }
                            else if ($currentTid379.parent != null) {
                                $retry376 = false;
                                throw $e378;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e378) {
                            $commit375 = false;
                            if ($tm380.checkForStaleObjects())
                                continue $label374;
                            $retry376 = false;
                            throw new fabric.worker.AbortException($e378);
                        }
                        finally {
                            if ($commit375) {
                                fabric.common.TransactionID $currentTid379 =
                                  $tm380.getCurrentTid();
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e378) {
                                    $commit375 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionAbortingException $e378) {
                                    $commit375 = false;
                                    $retry376 = false;
                                    $keepReads377 = $e378.keepReads;
                                    if ($tm380.checkForStaleObjects()) {
                                        $retry376 = true;
                                        $keepReads377 = false;
                                        continue $label374;
                                    }
                                    if ($e378.tid ==
                                          null ||
                                          !$e378.tid.isDescendantOf(
                                                       $currentTid379))
                                        throw $e378;
                                    throw new fabric.worker.UserAbortException(
                                            );
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e378) {
                                    $commit375 = false;
                                    $currentTid379 = $tm380.getCurrentTid();
                                    if ($currentTid379 != null) {
                                        if ($e378.tid.equals($currentTid379) ||
                                              !$e378.tid.isDescendantOf(
                                                           $currentTid379)) {
                                            throw $e378;
                                        }
                                    }
                                }
                            }
                            else if ($keepReads377) {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransactionUpdates();
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit375) {
                                { result = result$var373; }
                                if ($retry376) { continue $label374; }
                            }
                        }
                    }
                }
            }
            return result;
        }
        
        public double updateInterval(fabric.worker.metrics.StatsMap weakStats) {
            return fabric.metrics.SampledMetric._Impl.
              static_updateInterval((fabric.metrics.SampledMetric)
                                      this.$getProxy(), weakStats);
        }
        
        private static double static_updateInterval(
          fabric.metrics.SampledMetric tmp,
          fabric.worker.metrics.StatsMap weakStats) {
            if (weakStats.containsKey(tmp))
                return weakStats.getUpdateInterval(tmp);
            double result = 0;
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                result = tmp.computeUpdateInterval(weakStats);
            }
            else {
                {
                    double result$var384 = result;
                    fabric.worker.transaction.TransactionManager $tm391 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled394 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff392 = 1;
                    boolean $doBackoff393 = true;
                    boolean $retry387 = true;
                    boolean $keepReads388 = false;
                    $label385: for (boolean $commit386 = false; !$commit386; ) {
                        if ($backoffEnabled394) {
                            if ($doBackoff393) {
                                if ($backoff392 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.
                                              sleep(
                                                java.lang.Math.
                                                    round(
                                                      java.lang.Math.random() *
                                                          $backoff392));
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e389) {
                                            
                                        }
                                    }
                                }
                                if ($backoff392 < 5000) $backoff392 *= 2;
                            }
                            $doBackoff393 = $backoff392 <= 32 || !$doBackoff393;
                        }
                        $commit386 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            try {
                                result = tmp.computeUpdateInterval(weakStats);
                            }
                            catch (final fabric.worker.RetryException $e389) {
                                throw $e389;
                            }
                            catch (final fabric.worker.
                                     TransactionAbortingException $e389) {
                                throw $e389;
                            }
                            catch (final fabric.worker.
                                     TransactionRestartingException $e389) {
                                throw $e389;
                            }
                            catch (final fabric.worker.metrics.
                                     LockConflictException $e389) {
                                throw $e389;
                            }
                            catch (final Throwable $e389) {
                                $tm391.getCurrentLog().checkRetrySignal();
                                throw $e389;
                            }
                        }
                        catch (final fabric.worker.RetryException $e389) {
                            $commit386 = false;
                            continue $label385;
                        }
                        catch (fabric.worker.
                                 TransactionAbortingException $e389) {
                            $commit386 = false;
                            $retry387 = false;
                            $keepReads388 = $e389.keepReads;
                            if ($tm391.checkForStaleObjects()) {
                                $retry387 = true;
                                $keepReads388 = false;
                                continue $label385;
                            }
                            fabric.common.TransactionID $currentTid390 =
                              $tm391.getCurrentTid();
                            if ($e389.tid == null ||
                                  !$e389.tid.isDescendantOf($currentTid390)) {
                                throw $e389;
                            }
                            throw new fabric.worker.UserAbortException();
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e389) {
                            $commit386 = false;
                            fabric.common.TransactionID $currentTid390 =
                              $tm391.getCurrentTid();
                            if ($e389.tid.isDescendantOf($currentTid390))
                                continue $label385;
                            if ($currentTid390.parent != null) {
                                $retry387 = false;
                                throw $e389;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e389) {
                            $commit386 = false;
                            if ($tm391.checkForStaleObjects())
                                continue $label385;
                            fabric.common.TransactionID $currentTid390 =
                              $tm391.getCurrentTid();
                            if ($e389.tid.isDescendantOf($currentTid390)) {
                                $retry387 = true;
                            }
                            else if ($currentTid390.parent != null) {
                                $retry387 = false;
                                throw $e389;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e389) {
                            $commit386 = false;
                            if ($tm391.checkForStaleObjects())
                                continue $label385;
                            $retry387 = false;
                            throw new fabric.worker.AbortException($e389);
                        }
                        finally {
                            if ($commit386) {
                                fabric.common.TransactionID $currentTid390 =
                                  $tm391.getCurrentTid();
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e389) {
                                    $commit386 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionAbortingException $e389) {
                                    $commit386 = false;
                                    $retry387 = false;
                                    $keepReads388 = $e389.keepReads;
                                    if ($tm391.checkForStaleObjects()) {
                                        $retry387 = true;
                                        $keepReads388 = false;
                                        continue $label385;
                                    }
                                    if ($e389.tid ==
                                          null ||
                                          !$e389.tid.isDescendantOf(
                                                       $currentTid390))
                                        throw $e389;
                                    throw new fabric.worker.UserAbortException(
                                            );
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e389) {
                                    $commit386 = false;
                                    $currentTid390 = $tm391.getCurrentTid();
                                    if ($currentTid390 != null) {
                                        if ($e389.tid.equals($currentTid390) ||
                                              !$e389.tid.isDescendantOf(
                                                           $currentTid390)) {
                                            throw $e389;
                                        }
                                    }
                                }
                            }
                            else if ($keepReads388) {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransactionUpdates();
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit386) {
                                { result = result$var384; }
                                if ($retry387) { continue $label385; }
                            }
                        }
                    }
                }
            }
            return result;
        }
        
        public double velocity(fabric.worker.metrics.StatsMap weakStats) {
            return fabric.metrics.SampledMetric._Impl.
              static_velocity((fabric.metrics.SampledMetric) this.$getProxy(),
                              weakStats);
        }
        
        private static double static_velocity(
          fabric.metrics.SampledMetric tmp,
          fabric.worker.metrics.StatsMap weakStats) {
            if (!tmp.getUsePreset() && weakStats.containsKey(tmp))
                return weakStats.getVelocity(tmp);
            double result = 0;
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                result = tmp.computeVelocity(weakStats);
            }
            else {
                {
                    double result$var395 = result;
                    fabric.worker.transaction.TransactionManager $tm402 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled405 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff403 = 1;
                    boolean $doBackoff404 = true;
                    boolean $retry398 = true;
                    boolean $keepReads399 = false;
                    $label396: for (boolean $commit397 = false; !$commit397; ) {
                        if ($backoffEnabled405) {
                            if ($doBackoff404) {
                                if ($backoff403 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.
                                              sleep(
                                                java.lang.Math.
                                                    round(
                                                      java.lang.Math.random() *
                                                          $backoff403));
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e400) {
                                            
                                        }
                                    }
                                }
                                if ($backoff403 < 5000) $backoff403 *= 2;
                            }
                            $doBackoff404 = $backoff403 <= 32 || !$doBackoff404;
                        }
                        $commit397 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            try { result = tmp.computeVelocity(weakStats); }
                            catch (final fabric.worker.RetryException $e400) {
                                throw $e400;
                            }
                            catch (final fabric.worker.
                                     TransactionAbortingException $e400) {
                                throw $e400;
                            }
                            catch (final fabric.worker.
                                     TransactionRestartingException $e400) {
                                throw $e400;
                            }
                            catch (final fabric.worker.metrics.
                                     LockConflictException $e400) {
                                throw $e400;
                            }
                            catch (final Throwable $e400) {
                                $tm402.getCurrentLog().checkRetrySignal();
                                throw $e400;
                            }
                        }
                        catch (final fabric.worker.RetryException $e400) {
                            $commit397 = false;
                            continue $label396;
                        }
                        catch (fabric.worker.
                                 TransactionAbortingException $e400) {
                            $commit397 = false;
                            $retry398 = false;
                            $keepReads399 = $e400.keepReads;
                            if ($tm402.checkForStaleObjects()) {
                                $retry398 = true;
                                $keepReads399 = false;
                                continue $label396;
                            }
                            fabric.common.TransactionID $currentTid401 =
                              $tm402.getCurrentTid();
                            if ($e400.tid == null ||
                                  !$e400.tid.isDescendantOf($currentTid401)) {
                                throw $e400;
                            }
                            throw new fabric.worker.UserAbortException();
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e400) {
                            $commit397 = false;
                            fabric.common.TransactionID $currentTid401 =
                              $tm402.getCurrentTid();
                            if ($e400.tid.isDescendantOf($currentTid401))
                                continue $label396;
                            if ($currentTid401.parent != null) {
                                $retry398 = false;
                                throw $e400;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e400) {
                            $commit397 = false;
                            if ($tm402.checkForStaleObjects())
                                continue $label396;
                            fabric.common.TransactionID $currentTid401 =
                              $tm402.getCurrentTid();
                            if ($e400.tid.isDescendantOf($currentTid401)) {
                                $retry398 = true;
                            }
                            else if ($currentTid401.parent != null) {
                                $retry398 = false;
                                throw $e400;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e400) {
                            $commit397 = false;
                            if ($tm402.checkForStaleObjects())
                                continue $label396;
                            $retry398 = false;
                            throw new fabric.worker.AbortException($e400);
                        }
                        finally {
                            if ($commit397) {
                                fabric.common.TransactionID $currentTid401 =
                                  $tm402.getCurrentTid();
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e400) {
                                    $commit397 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionAbortingException $e400) {
                                    $commit397 = false;
                                    $retry398 = false;
                                    $keepReads399 = $e400.keepReads;
                                    if ($tm402.checkForStaleObjects()) {
                                        $retry398 = true;
                                        $keepReads399 = false;
                                        continue $label396;
                                    }
                                    if ($e400.tid ==
                                          null ||
                                          !$e400.tid.isDescendantOf(
                                                       $currentTid401))
                                        throw $e400;
                                    throw new fabric.worker.UserAbortException(
                                            );
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e400) {
                                    $commit397 = false;
                                    $currentTid401 = $tm402.getCurrentTid();
                                    if ($currentTid401 != null) {
                                        if ($e400.tid.equals($currentTid401) ||
                                              !$e400.tid.isDescendantOf(
                                                           $currentTid401)) {
                                            throw $e400;
                                        }
                                    }
                                }
                            }
                            else if ($keepReads399) {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransactionUpdates();
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit397) {
                                { result = result$var395; }
                                if ($retry398) { continue $label396; }
                            }
                        }
                    }
                }
            }
            return result;
        }
        
        public double noise(fabric.worker.metrics.StatsMap weakStats) {
            return fabric.metrics.SampledMetric._Impl.
              static_noise((fabric.metrics.SampledMetric) this.$getProxy(),
                           weakStats);
        }
        
        private static double static_noise(
          fabric.metrics.SampledMetric tmp,
          fabric.worker.metrics.StatsMap weakStats) {
            if (!tmp.getUsePreset() && weakStats.containsKey(tmp))
                return weakStats.getNoise(tmp);
            double result = 0;
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                result = tmp.computeNoise(weakStats);
            }
            else {
                {
                    double result$var406 = result;
                    fabric.worker.transaction.TransactionManager $tm413 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled416 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff414 = 1;
                    boolean $doBackoff415 = true;
                    boolean $retry409 = true;
                    boolean $keepReads410 = false;
                    $label407: for (boolean $commit408 = false; !$commit408; ) {
                        if ($backoffEnabled416) {
                            if ($doBackoff415) {
                                if ($backoff414 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.
                                              sleep(
                                                java.lang.Math.
                                                    round(
                                                      java.lang.Math.random() *
                                                          $backoff414));
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e411) {
                                            
                                        }
                                    }
                                }
                                if ($backoff414 < 5000) $backoff414 *= 2;
                            }
                            $doBackoff415 = $backoff414 <= 32 || !$doBackoff415;
                        }
                        $commit408 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            try { result = tmp.computeNoise(weakStats); }
                            catch (final fabric.worker.RetryException $e411) {
                                throw $e411;
                            }
                            catch (final fabric.worker.
                                     TransactionAbortingException $e411) {
                                throw $e411;
                            }
                            catch (final fabric.worker.
                                     TransactionRestartingException $e411) {
                                throw $e411;
                            }
                            catch (final fabric.worker.metrics.
                                     LockConflictException $e411) {
                                throw $e411;
                            }
                            catch (final Throwable $e411) {
                                $tm413.getCurrentLog().checkRetrySignal();
                                throw $e411;
                            }
                        }
                        catch (final fabric.worker.RetryException $e411) {
                            $commit408 = false;
                            continue $label407;
                        }
                        catch (fabric.worker.
                                 TransactionAbortingException $e411) {
                            $commit408 = false;
                            $retry409 = false;
                            $keepReads410 = $e411.keepReads;
                            if ($tm413.checkForStaleObjects()) {
                                $retry409 = true;
                                $keepReads410 = false;
                                continue $label407;
                            }
                            fabric.common.TransactionID $currentTid412 =
                              $tm413.getCurrentTid();
                            if ($e411.tid == null ||
                                  !$e411.tid.isDescendantOf($currentTid412)) {
                                throw $e411;
                            }
                            throw new fabric.worker.UserAbortException();
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e411) {
                            $commit408 = false;
                            fabric.common.TransactionID $currentTid412 =
                              $tm413.getCurrentTid();
                            if ($e411.tid.isDescendantOf($currentTid412))
                                continue $label407;
                            if ($currentTid412.parent != null) {
                                $retry409 = false;
                                throw $e411;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e411) {
                            $commit408 = false;
                            if ($tm413.checkForStaleObjects())
                                continue $label407;
                            fabric.common.TransactionID $currentTid412 =
                              $tm413.getCurrentTid();
                            if ($e411.tid.isDescendantOf($currentTid412)) {
                                $retry409 = true;
                            }
                            else if ($currentTid412.parent != null) {
                                $retry409 = false;
                                throw $e411;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e411) {
                            $commit408 = false;
                            if ($tm413.checkForStaleObjects())
                                continue $label407;
                            $retry409 = false;
                            throw new fabric.worker.AbortException($e411);
                        }
                        finally {
                            if ($commit408) {
                                fabric.common.TransactionID $currentTid412 =
                                  $tm413.getCurrentTid();
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e411) {
                                    $commit408 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionAbortingException $e411) {
                                    $commit408 = false;
                                    $retry409 = false;
                                    $keepReads410 = $e411.keepReads;
                                    if ($tm413.checkForStaleObjects()) {
                                        $retry409 = true;
                                        $keepReads410 = false;
                                        continue $label407;
                                    }
                                    if ($e411.tid ==
                                          null ||
                                          !$e411.tid.isDescendantOf(
                                                       $currentTid412))
                                        throw $e411;
                                    throw new fabric.worker.UserAbortException(
                                            );
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e411) {
                                    $commit408 = false;
                                    $currentTid412 = $tm413.getCurrentTid();
                                    if ($currentTid412 != null) {
                                        if ($e411.tid.equals($currentTid412) ||
                                              !$e411.tid.isDescendantOf(
                                                           $currentTid412)) {
                                            throw $e411;
                                        }
                                    }
                                }
                            }
                            else if ($keepReads410) {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransactionUpdates();
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit408) {
                                { result = result$var406; }
                                if ($retry409) { continue $label407; }
                            }
                        }
                    }
                }
            }
            return result;
        }
        
        public fabric.worker.metrics.ImmutableObserverSet handleDirectUpdates(
          ) {
            throw new fabric.common.exceptions.InternalError(
                    "This should never happen, sampled metrics don\'t observe anything.");
        }
        
        public fabric.worker.metrics.ImmutableMetricsVector getLeafSubjects() {
            return fabric.worker.metrics.ImmutableMetricsVector.
              createVector(
                new fabric.metrics.Metric[] { (fabric.metrics.SampledMetric)
                                                this.$getProxy() });
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
            out.writeLong(this.key);
            out.writeDouble(this.presetR);
            out.writeDouble(this.presetB);
            out.writeDouble(this.presetV);
            out.writeDouble(this.presetN);
            $writeInline(out, this.stats);
        }
        
        public _Impl(fabric.worker.Store store, long onum, int version,
                     fabric.worker.metrics.ImmutableObjectSet associates,
                     fabric.worker.metrics.ImmutableObserverSet observers,
                     fabric.worker.metrics.treaties.TreatySet treaties,
                     fabric.worker.Store labelStore, long labelOnum,
                     fabric.worker.Store accessPolicyStore,
                     long accessPolicyOnum, java.io.ObjectInput in,
                     java.util.Iterator refTypes,
                     java.util.Iterator intraStoreRefs,
                     java.util.Iterator interStoreRefs)
              throws java.io.IOException,
            java.lang.ClassNotFoundException {
            super(store, onum, version, associates, observers, treaties,
                  labelStore, labelOnum, accessPolicyStore, accessPolicyOnum,
                  in, refTypes, intraStoreRefs, interStoreRefs);
            this.key = in.readLong();
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
            this.key = src.key;
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
                         fabric.worker.metrics.ImmutableObjectSet associates,
                         fabric.worker.metrics.ImmutableObserverSet observers,
                         fabric.worker.metrics.treaties.TreatySet treaties,
                         fabric.worker.Store labelStore, long labelOnum,
                         fabric.worker.Store accessPolicyStore,
                         long accessPolicyOnum, java.io.ObjectInput in,
                         java.util.Iterator refTypes,
                         java.util.Iterator intraStoreRefs,
                         java.util.Iterator interStoreRefs)
                  throws java.io.IOException,
                java.lang.ClassNotFoundException {
                super(store, onum, version, associates, observers, treaties,
                      labelStore, labelOnum, accessPolicyStore,
                      accessPolicyOnum, in, refTypes, intraStoreRefs,
                      interStoreRefs);
            }
            
            public _Impl(fabric.worker.Store store) { super(store); }
            
            protected fabric.lang.Object._Proxy $makeProxy() {
                return new fabric.metrics.SampledMetric._Static._Proxy(this);
            }
            
            private void $init() {  }
        }
        
    }
    
    public static final byte[] $classHash = new byte[] { -96, 67, -9, -106, -22,
    98, 14, 78, -31, -23, -76, -91, 69, -4, -117, -119, 95, 95, 16, -2, -87,
    120, 10, -116, -92, -111, 113, 98, -63, -67, -96, -64 };
    public static final java.lang.String jlc$CompilerVersion$fabil = "0.3.0";
    public static final long jlc$SourceLastModified$fabil = 1537039040000L;
    public static final java.lang.String jlc$ClassType$fabil =
      "H4sIAAAAAAAAAK1aC3AV1Rk+exPyIpAQDEKAEEJAAyFX0KqIrSVXHoEAMSFYQzXu3Xtusmbv7mX33HBBcezDQm0NYwkoPhiscaqYYn0NUy0V6qP4GKdSW2tbBF8VioxjrdIZEfv/Z8995t5NdiYMe77NnvOf87/Pf/buwGkyyjJJdVD2q1o92ximVv0S2d/Y1CybFg34NNmy1sDTDmV0buPOE78KVHqIp4kUK7Ju6Koiax26xcjYppvkHtmrU+Zta2lcuI4UKki4TLa6GPGsa4iapCpsaBs7NYOJRQbNv2OOt+/uG0qfzCEl7aRE1VuZzFTFZ+iMRlk7KQ7RkJ+a1qJAgAbayTid0kArNVVZUzfBQENvJ2WW2qnLLGJSq4VahtaDA8usSJiafM3YQ2TfALbNiMIME9gvtdmPMFXzNqkWW9hE8oIq1QLWenIryW0io4Ka3AkDJzTFpPDyGb1L8DkML1KBTTMoKzRGktut6gFGpqVTxCWuWQEDgDQ/RFmXEV8qV5fhASmzWdJkvdPbykxV74Sho4wIrMJIRdZJYVBBWFa65U7awcjE9HHNdheMKuRqQRJGytOH8ZnAZhVpNkuy1ulVV/berC/TPUQCngNU0ZD/AiCqTCNqoUFqUl2hNmHx7Kad8oQDWz2EwODytMH2mP23fPbdusqDh+0xkzOMWe2/iSqsQ+n3j31ziq92QQ6yURA2LBVdIUVybtVm0bMwGgZvnxCfETvrY50HW16+7ra99JSHFDWSPMXQIiHwqnGKEQqrGjWXUp2aMqOBRlJI9YCP9zeSfLhvUnVqP10dDFqUNZJcjT/KM/jfoKIgTIEqyod7VQ8asfuwzLr4fTRMCMmHi0jwfz8h8z+C+0pCcl5iZIW3ywhRr1+L0A3g3l64qGwqXV6IW1NVvJapeM2IzlQYJB6BFwFY3lY5FNZoYCX/sx7YCI/sdFHkvnSDJIFipylGgPplC6wkPKahWYOgWGZoAWp2KFrvgUYy/sAu7jWF6OkWeCvXiwSWnpKeI5Jp+yINiz/b1/Ga7XFIK9TGyBSbx3rBY30Kj8BWMcZSPWSneshOA1K03re78THuMnkWj634TMUw0xVhTWZBwwxFiSRxsc7j9NxXwNLdkEEgSRTXtl6//Mat1TngpOENuWg3GFqTHjKJRNMIdzLEQYdSsuXEl4/v3GwkgoeRmkExPZgSY7I6XUemodAA5LzE9LOr5Gc6Dmyu8WA+KYRUx2RwRsgblelrpMTmwlieQ22MaiKjUQeyhl2x5FTEukxjQ+IJt/1YbMpsN0BlpTHIU+S3W8MP/O2NkxfzzSOWTUuS0m4rZQuTIhgnK+GxOi6h+zUmpTDu6D3N23ec3rKOKx5GzMi0YA22PohcGULWMG8/vP6dY+/2v+VJGIuRvHDEr6lKlMsy7hv4J8F1Di8MQ3yACMnYJ1JAVTwHhHHlWQneIBtokJGAdaumTQ8ZATWoyn6NoqecLZk575lPekttc2vwxFaeSeqGniDxfFIDue21G85U8mkkBXejhP4Sw+wUNz4x8yLTlDciH9EfHJm664/yA+D5kKAsdRPlOYdwfRBuwPlcF3N5Oy+t7xJsqm1tTeHP86zB6X4J7psJX2z3Dtxf4fvOKTvi476Ic0zPEPFr5aQwmb839IWnOu8lD8lvJ6V8y5Z1tlaGrAVu0A6bruUTD5vImJT+1A3U3i0WxmNtSnocJC2bHgWJTAP3OBrvi2zHtx0HFFGKSpoEVxUhubk25nyFvePD2J4XlQi/uYKTzODtLGxquSI9jOSHTbUHPIuRQjUUijC0PV9lDiM53XRjBlU3m2oIoqVH7Kx0a98d39T39tluZpcfMwZVAMk0dgnCVxnDl4rCKtOdVuEUSz5+fPNzj2zeYm/PZamb6WI9Evr1X79+vf6e469kSNW5mmGn21Kug0vjKiwh9m5H2ggZfbNALYMKl2VRId7OxuaqmNpApbCvsRY+dJEQEOFqiP2AAcFPHTlZBxy8L/DPGThpdstJA/650nFNmZDiRoFXZFizze2aa4desxPW+o3ABzOs2e52zVVZ1yzGNWdC9q0lpPoRgdsyrNmReU3I2oVh02AQzjQQjU/rwWlHi+l6Bd6eNC2DUw6cJyxOUc7IhaJi2GCY3dSMFw4tEV2HcsAuHFrjBJPSCwMuWjQLi1wtCd74vzxRyr0o8PdJvCUlVIIBODVb1c2Dr/+HfbsDqx+e5xFZeTFohBnhuRrtoVrSVLMwlAed6lbys0YixR4/NXWBr/ujTjuUp6WtnD760ZUDryydpfzCQ3LiuXTQASeVaGFqBi0yKZzP9DUpebQqrqtC1MEKuOoIGfU7gZuS3SPhVDOw6U51gQJBslGgla7mxM7mSfiwj7srn3qTw/53CzYRRqbavlMjnKYmpdqsSTBopoo1Da5rwEtnCixxJxaSjBVYkF2sZIZ/5NDHg+NW8OtOytos2szjNlOizPcbhkZlPZNIU+C6Fvh5ROAOdyIhSZ/A3uGJtM2h7y5s7mCkCESy5Wnhhs3G+fWQjkpsHH3WHedI8pXAL4bH+T0Offdisz2Z8wZHzhXgvEeg4o5zJPEL/P7wOH/Qoe8hbO5P5nytI+cqLHtU4KvuOEeSVwS+MDzO9zr0DWDzcDLnq7JyfgHBUwAZc0zgIXecI8lBgc9m51xKbB92MnrKgf1nsNkH7DO5m9oZKFP05vYYaiCTSDVwQZU1NihwhTuRkGS5wKtdiPS8g0hcq88yUoBehEU8/r0/E/Pz4doCBcx6gcvcMY8kSwUuys580g7Baxkfn/qwgwTco19IMUomGXgRdDFc2+DksFzgDAcZ/IMKHU5SLXDSkAaIlT2VmcseXumslMOZix3OzZsOYr+NzesM36qGwhFG48ZLDyZeDrXAtR24/ovAviyCZyw3v8PwlQ2+GE4r/krFbNsF3p5dJ0l2LcXmCF/1uIOA72PzDiPj7aU7hpIzbuB+Qso+EfiSOwMjyYsCnxtWhNlynHSQ4xQ2HzIyVgiQlDd82UwFe/v4jQIvd2UqbI5mMBPOdJnAWrdm+txBPL4Tn2akPNVMDlJyQ10K128JKe8VGHBnKCRRBLa7MNRZB0nOYXOG2e+eQYQm2WJt4YDMaFZbwXGZHCBkwnQby4+OiK1wpn8KfMOlraS87BJKWMJKsCNOTLWVs6DcXFfC9QYhExsElrkzF5KME+hQR6ebSypxEGYcNkXgeEIKW4JG/EmoR9ayZsJ1cB0BNo4JzFY+uzMZztQn8CduTTbJQcrJ2IxnZEqqyYYWNh5lHxAyucDGinfdmQ1Jjgp8y4XZqh0EwrOaNJWRklg+p5qhqGxjVoNhjH0MMuwS2DYiBsOZ1ghc4tZgdQ7y1WMzi5EJaduWg5ixnUsCqIwKvMaVqThJs8ClLkz1LQdRLsPmokSJscpQLccSQwLaaTk2Vv5hJOzEZzok8Cm3drrKQTisQaUFg8qLrDLyGrgCOJlKyPQXBD7pYKTBNTAneULgY9mFSWZzmUPfcmx8ULszw/61OlZxlvLfIfAtfH1Sx6AaM5OEVcAelLjVCwRWu5MQSaYLnDw8Cdsc+q7FppmRMarVCmJotJUZJjfPlkwxdBWsDHE045jAAXcxhCSPCfzlsHwtcUSRbnAQ40ZsroOcF+Fpe7HF1BAgH5vxrFULXMBWO/NVgXvdWQFJHhX4kItk0Okgg4qNn5FRPY4HjRUw4RJCZi0VOGZEsgDOVGzjzHNus8B6B6nwodQNgSGyQFbh4maBTeOCfQJ7swiXxSxIcqfALS7M4vB+VML3o1KEkXxriGMFGuZ7hFw418YLPh8Rw+BM/xH4oVvD/NhBLiyipFvh1CQM4yAeN00dMBEEC90nMOrONEiyQeB6F6a500EE/IVF2spIkTb0QWIVzHoTIbOrbKz9YESsgzO9L9ChfstsnbsdRNuFzV1wVBLWcZYw9vpIgtpmzgMCmTsDIYklMOTCQHscpMAcL90HPhYZ3tEB4la6BVytzsY5J0fESDjTCYF/d2skh5etEu5+Un/iZD60lPFIuoOQuU8IdJnkkOROgW6SnMN7Vwnfu0r7oNDpGeqwgHHUS0j9JTbOPTMiJsKZvhR4wq2JDjgI9jw2+6EwiG0/DvLFd6D7CfE+J/A+d8ZBknsF9mUXZJBxXnaQ4TA2h6Aw0B2PB7j/7CHkostt9J7Lwrk7y+BMXwv81K1l/uQg1RFsXk0UBlmF42bBwxsUBfMOCtzpzixIskPgtuxSJPP3jkPfP7B5C442XbIe0OjVqkkVkZ7jv8XPzvxSujH2FcxqvwVpgpqt4gfKYR0egBHpaUj1vQKD7tSAJFRgx/DU8C+HPoxU6T0Irk7KmqgcbI3wL5LiKqgbQgXiS8a1lH+wnFEJUTiapPwSjV+BTc7wPab4OljxvUj7P1pRV57lW8yJg77XFnT7dpcUnL+77W3+dWH8y9/CJlIQjGha8tdSSfd5YZMGVa74QvvbqTBXzGew5aV+vMn4B9F4xwviT+1x/2Ukzx6Hf33BjVHBm5gKy9O+AbWVwIfwBSsiJn6JPvD5+f/LK1hznH8ICOap2uM7s/Pf/rGr3jv59MOLz/7spx0dpecejRb9vP+u9f5DB/Yc/D/85Vt+IS8AAA==";
}
