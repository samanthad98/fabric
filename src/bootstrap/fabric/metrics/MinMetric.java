package fabric.metrics;

import fabric.lang.*;
import fabric.lang.security.*;
import fabric.worker.*;
import fabric.worker.remote.*;
import java.lang.*;
import fabric.util.Arrays;
import fabric.util.Set;
import fabric.util.TreeSet;
import fabric.util.Iterator;
import fabric.util.LinkedHashMap;
import fabric.util.Map;
import fabric.metrics.contracts.Bound;
import fabric.metrics.contracts.Contract;
import fabric.metrics.contracts.enforcement.DirectPolicy;
import fabric.metrics.contracts.enforcement.EnforcementPolicy;
import fabric.metrics.contracts.enforcement.WitnessPolicy;
import fabric.metrics.util.Observer;
import fabric.metrics.util.Subject;
import fabric.worker.Store;
import fabric.worker.transaction.TransactionManager;

/**
 * A {@link DerivedMetric} for the minimum of a group of other {@link Metric}s.
 */
public interface MinMetric extends fabric.metrics.DerivedMetric {
    /**
     * @param store
     *            the {@link Store} that holds this {@link Metric}
     * @param terms
     *            The {@link Metric}s this applies to
     */
    public fabric.metrics.MinMetric fabric$metrics$MinMetric$(
      fabric.metrics.Metric[] terms);
    
    public double computePresetR();
    
    public double computePresetB();
    
    public double computePresetV();
    
    public double computePresetN();
    
    public double computeValue(boolean useWeakCache);
    
    public double computeVelocity(boolean useWeakCache);
    
    public double computeNoise(boolean useWeakCache);
    
    public java.lang.String toString();
    
    public fabric.metrics.DerivedMetric times(double scalar);
    
    /**
     * {@inheritDoc}
     * <p>
     * {@link MinMetric}s try to consolidate local computations so that there
     * isn't unnecessary nodes in the {@link Subject}-{@link Observer} tree for
     * {@link #handleUpdates()}.
     */
    public fabric.metrics.DerivedMetric plus(fabric.metrics.Metric other);
    
    /**
     * {@inheritDoc}
     * <p>
     * {@link MinMetric}s try to consolidate local computations so that there
     * isn't unnecessary nodes in the {@link Subject}-{@link Observer} tree for
     * {@link #handleUpdates()}.
     */
    public fabric.metrics.Metric min(fabric.metrics.Metric other);
    
    public fabric.metrics.contracts.enforcement.EnforcementPolicy
      thresholdPolicy(double rate, double base, boolean useWeakCache,
                      final fabric.worker.Store s);
    
    public int hashCode();
    
    public boolean equals(fabric.lang.Object other);
    
    public static class _Proxy extends fabric.metrics.DerivedMetric._Proxy
      implements fabric.metrics.MinMetric {
        public fabric.metrics.MinMetric fabric$metrics$MinMetric$(
          fabric.metrics.Metric[] arg1) {
            return ((fabric.metrics.MinMetric) fetch()).
              fabric$metrics$MinMetric$(arg1);
        }
        
        public _Proxy(MinMetric._Impl impl) { super(impl); }
        
        public _Proxy(fabric.worker.Store store, long onum) {
            super(store, onum);
        }
    }
    
    public static class _Impl extends fabric.metrics.DerivedMetric._Impl
      implements fabric.metrics.MinMetric {
        /**
     * @param store
     *            the {@link Store} that holds this {@link Metric}
     * @param terms
     *            The {@link Metric}s this applies to
     */
        public fabric.metrics.MinMetric fabric$metrics$MinMetric$(
          fabric.metrics.Metric[] terms) {
            fabric$metrics$DerivedMetric$(terms);
            java.util.Set termsBag =
              new java.util.TreeSet(java.util.Arrays.asList(terms));
            this.set$terms(
                   (fabric.lang.arrays.ObjectArray)
                     new fabric.lang.arrays.ObjectArray._Impl(
                       this.$getStore()).fabric$lang$arrays$ObjectArray$(
                                           this.get$$updateLabel(),
                                           this.get$$updateLabel().confPolicy(),
                                           fabric.metrics.Metric._Proxy.class,
                                           termsBag.size()).$getProxy());
            int tIdx = 0;
            for (java.util.Iterator it = termsBag.iterator(); it.hasNext(); ) {
                this.
                  get$terms().
                  set(
                    tIdx++,
                    (fabric.metrics.Metric)
                      fabric.lang.Object._Proxy.
                      $getProxy(
                        fabric.lang.WrappedJavaInlineable.$wrap(it.next())));
            }
            initialize();
            return (fabric.metrics.MinMetric) this.$getProxy();
        }
        
        public double computePresetR() {
            double result = java.lang.Double.MAX_VALUE;
            for (int i = 0; i < this.get$terms().get$length(); i++) {
                result =
                  java.lang.Math.max(result,
                                     ((fabric.metrics.Metric)
                                        this.get$terms().get(i)).getPresetR());
            }
            return result;
        }
        
        public double computePresetB() {
            double result = java.lang.Double.MAX_VALUE;
            for (int i = 0; i < this.get$terms().get$length(); i++) {
                result =
                  java.lang.Math.max(result,
                                     ((fabric.metrics.Metric)
                                        this.get$terms().get(i)).getPresetB());
            }
            return result;
        }
        
        public double computePresetV() {
            double result = java.lang.Double.MAX_VALUE;
            for (int i = 0; i < this.get$terms().get$length(); i++) {
                result =
                  java.lang.Math.min(result,
                                     ((fabric.metrics.Metric)
                                        this.get$terms().get(i)).getPresetV());
            }
            return result;
        }
        
        public double computePresetN() {
            double result = java.lang.Double.MAX_VALUE;
            for (int i = 0; i < this.get$terms().get$length(); i++) {
                result =
                  java.lang.Math.max(result,
                                     ((fabric.metrics.Metric)
                                        this.get$terms().get(i)).getPresetN());
            }
            return result;
        }
        
        public double computeValue(boolean useWeakCache) {
            double result = java.lang.Double.MAX_VALUE;
            for (int i = 0; i < this.get$terms().get$length(); i++) {
                result =
                  java.lang.Math.min(
                                   result,
                                   ((fabric.metrics.Metric)
                                      this.get$terms().get(i)).value(
                                                                 useWeakCache));
            }
            return result;
        }
        
        public double computeVelocity(boolean useWeakCache) {
            double result = java.lang.Double.MAX_VALUE;
            for (int i = 0; i < this.get$terms().get$length(); i++) {
                result =
                  java.lang.Math.min(
                                   result,
                                   ((fabric.metrics.Metric)
                                      this.get$terms().get(i)).velocity(
                                                                 useWeakCache));
            }
            return result;
        }
        
        public double computeNoise(boolean useWeakCache) {
            double noise = 0;
            for (int i = 0; i < this.get$terms().get$length(); i++) {
                noise =
                  java.lang.Math.max(
                                   noise,
                                   ((fabric.metrics.Metric)
                                      this.get$terms().get(i)).noise(
                                                                 useWeakCache));
            }
            return noise;
        }
        
        public java.lang.String toString() {
            java.lang.String str = "min(";
            boolean nonEmpty = false;
            for (int i = 0; i < this.get$terms().get$length(); i++) {
                if (nonEmpty) { str += ", "; }
                nonEmpty = true;
                str += (fabric.metrics.Metric) this.get$terms().get(i);
            }
            return str + ")@" + $getStore();
        }
        
        public fabric.metrics.DerivedMetric times(double scalar) {
            return fabric.metrics.MinMetric._Impl.static_times(
                                                    (fabric.metrics.MinMetric)
                                                      this.$getProxy(), scalar);
        }
        
        private static fabric.metrics.DerivedMetric static_times(
          fabric.metrics.MinMetric tmp, double scalar) {
            fabric.metrics.Metric[] newTerms =
              new fabric.metrics.Metric[tmp.get$terms().get$length()];
            for (int i = 0; i < tmp.get$terms().get$length(); i++) {
                newTerms[i] = ((fabric.metrics.Metric)
                                 tmp.get$terms().get(i)).times(scalar);
            }
            final fabric.worker.Store s = tmp.$getStore();
            fabric.metrics.DerivedMetric val = null;
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                val =
                  ((fabric.metrics.MinMetric)
                     new fabric.metrics.MinMetric._Impl(s).$getProxy()).
                    fabric$metrics$MinMetric$(newTerms);
            }
            else {
                {
                    fabric.metrics.DerivedMetric val$var159 = val;
                    fabric.worker.transaction.TransactionManager $tm165 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled168 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff166 = 1;
                    boolean $doBackoff167 = true;
                    boolean $retry162 = true;
                    $label160: for (boolean $commit161 = false; !$commit161; ) {
                        if ($backoffEnabled168) {
                            if ($doBackoff167) {
                                if ($backoff166 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.sleep($backoff166);
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e163) {
                                            
                                        }
                                    }
                                }
                                if ($backoff166 < 5000) $backoff166 *= 2;
                            }
                            $doBackoff167 = $backoff166 <= 32 || !$doBackoff167;
                        }
                        $commit161 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            val =
                              ((fabric.metrics.MinMetric)
                                 new fabric.metrics.MinMetric._Impl(s).
                                 $getProxy()).fabric$metrics$MinMetric$(
                                                newTerms);
                        }
                        catch (final fabric.worker.RetryException $e163) {
                            $commit161 = false;
                            continue $label160;
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e163) {
                            $commit161 = false;
                            fabric.common.TransactionID $currentTid164 =
                              $tm165.getCurrentTid();
                            if ($e163.tid.isDescendantOf($currentTid164))
                                continue $label160;
                            if ($currentTid164.parent != null) {
                                $retry162 = false;
                                throw $e163;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e163) {
                            $commit161 = false;
                            if ($tm165.checkForStaleObjects()) continue;
                            fabric.common.TransactionID $currentTid164 =
                              $tm165.getCurrentTid();
                            if ($e163.tid.isDescendantOf($currentTid164)) {
                                $retry162 = true;
                            }
                            else if ($currentTid164.parent != null) {
                                $retry162 = false;
                                throw $e163;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e163) {
                            $commit161 = false;
                            if ($tm165.checkForStaleObjects())
                                continue $label160;
                            $retry162 = false;
                            throw new fabric.worker.AbortException($e163);
                        }
                        finally {
                            if ($commit161) {
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e163) {
                                    $commit161 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e163) {
                                    $commit161 = false;
                                    fabric.common.TransactionID $currentTid164 =
                                      $tm165.getCurrentTid();
                                    if ($currentTid164 != null) {
                                        if ($e163.tid.equals($currentTid164) ||
                                              !$e163.tid.isDescendantOf(
                                                           $currentTid164)) {
                                            throw $e163;
                                        }
                                    }
                                }
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit161 && $retry162) {
                                { val = val$var159; }
                                continue $label160;
                            }
                        }
                    }
                }
            }
            return fabric.metrics.Metric._Impl.findDerivedMetric(s, val);
        }
        
        /**
     * {@inheritDoc}
     * <p>
     * {@link MinMetric}s try to consolidate local computations so that there
     * isn't unnecessary nodes in the {@link Subject}-{@link Observer} tree for
     * {@link #handleUpdates()}.
     */
        public fabric.metrics.DerivedMetric plus(fabric.metrics.Metric other) {
            return fabric.metrics.MinMetric._Impl.static_plus(
                                                    (fabric.metrics.MinMetric)
                                                      this.$getProxy(), other);
        }
        
        private static fabric.metrics.DerivedMetric static_plus(
          fabric.metrics.MinMetric tmp, fabric.metrics.Metric other) {
            fabric.metrics.Metric[] newTerms =
              new fabric.metrics.Metric[tmp.get$terms().get$length()];
            for (int i = 0; i < newTerms.length; i++) {
                newTerms[i] = other.plus((fabric.metrics.Metric)
                                           tmp.get$terms().get(i));
            }
            final fabric.worker.Store s = tmp.$getStore();
            fabric.metrics.DerivedMetric val = null;
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                val =
                  ((fabric.metrics.MinMetric)
                     new fabric.metrics.MinMetric._Impl(s).$getProxy()).
                    fabric$metrics$MinMetric$(newTerms);
            }
            else {
                {
                    fabric.metrics.DerivedMetric val$var169 = val;
                    fabric.worker.transaction.TransactionManager $tm175 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled178 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff176 = 1;
                    boolean $doBackoff177 = true;
                    boolean $retry172 = true;
                    $label170: for (boolean $commit171 = false; !$commit171; ) {
                        if ($backoffEnabled178) {
                            if ($doBackoff177) {
                                if ($backoff176 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.sleep($backoff176);
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e173) {
                                            
                                        }
                                    }
                                }
                                if ($backoff176 < 5000) $backoff176 *= 2;
                            }
                            $doBackoff177 = $backoff176 <= 32 || !$doBackoff177;
                        }
                        $commit171 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            val =
                              ((fabric.metrics.MinMetric)
                                 new fabric.metrics.MinMetric._Impl(s).
                                 $getProxy()).fabric$metrics$MinMetric$(
                                                newTerms);
                        }
                        catch (final fabric.worker.RetryException $e173) {
                            $commit171 = false;
                            continue $label170;
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e173) {
                            $commit171 = false;
                            fabric.common.TransactionID $currentTid174 =
                              $tm175.getCurrentTid();
                            if ($e173.tid.isDescendantOf($currentTid174))
                                continue $label170;
                            if ($currentTid174.parent != null) {
                                $retry172 = false;
                                throw $e173;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e173) {
                            $commit171 = false;
                            if ($tm175.checkForStaleObjects()) continue;
                            fabric.common.TransactionID $currentTid174 =
                              $tm175.getCurrentTid();
                            if ($e173.tid.isDescendantOf($currentTid174)) {
                                $retry172 = true;
                            }
                            else if ($currentTid174.parent != null) {
                                $retry172 = false;
                                throw $e173;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e173) {
                            $commit171 = false;
                            if ($tm175.checkForStaleObjects())
                                continue $label170;
                            $retry172 = false;
                            throw new fabric.worker.AbortException($e173);
                        }
                        finally {
                            if ($commit171) {
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e173) {
                                    $commit171 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e173) {
                                    $commit171 = false;
                                    fabric.common.TransactionID $currentTid174 =
                                      $tm175.getCurrentTid();
                                    if ($currentTid174 != null) {
                                        if ($e173.tid.equals($currentTid174) ||
                                              !$e173.tid.isDescendantOf(
                                                           $currentTid174)) {
                                            throw $e173;
                                        }
                                    }
                                }
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit171 && $retry172) {
                                { val = val$var169; }
                                continue $label170;
                            }
                        }
                    }
                }
            }
            return fabric.metrics.Metric._Impl.findDerivedMetric(s, val);
        }
        
        /**
     * {@inheritDoc}
     * <p>
     * {@link MinMetric}s try to consolidate local computations so that there
     * isn't unnecessary nodes in the {@link Subject}-{@link Observer} tree for
     * {@link #handleUpdates()}.
     */
        public fabric.metrics.Metric min(fabric.metrics.Metric other) {
            return fabric.metrics.MinMetric._Impl.static_min(
                                                    (fabric.metrics.MinMetric)
                                                      this.$getProxy(), other);
        }
        
        private static fabric.metrics.Metric static_min(
          fabric.metrics.MinMetric tmp, fabric.metrics.Metric other) {
            final fabric.worker.Store s = tmp.$getStore();
            fabric.metrics.DerivedMetric val = null;
            if (fabric.lang.Object._Proxy.
                  $getProxy(
                    (java.lang.Object)
                      fabric.lang.WrappedJavaInlineable.
                      $unwrap(other)) instanceof fabric.metrics.MinMetric &&
                  other.$getStore().equals(s)) {
                fabric.metrics.MinMetric that =
                  (fabric.metrics.MinMetric)
                    fabric.lang.Object._Proxy.$getProxy(other);
                java.util.Set termsBag = new java.util.TreeSet();
                for (int i = 0; i < tmp.get$terms().get$length(); i++) {
                    termsBag.
                      add(
                        (java.lang.Object)
                          fabric.lang.WrappedJavaInlineable.
                          $unwrap((fabric.metrics.Metric)
                                    tmp.get$terms().get(i)));
                }
                for (int i = 0; i < that.get$terms().get$length(); i++) {
                    termsBag.
                      add(
                        (java.lang.Object)
                          fabric.lang.WrappedJavaInlineable.
                          $unwrap((fabric.metrics.Metric)
                                    that.get$terms().get(i)));
                }
                fabric.metrics.Metric[] newTerms =
                  new fabric.metrics.Metric[termsBag.size()];
                int aggIdx = 0;
                for (java.util.Iterator iter = termsBag.iterator();
                     iter.hasNext(); ) {
                    fabric.metrics.Metric
                      m =
                      (fabric.metrics.Metric)
                        fabric.lang.Object._Proxy.
                        $getProxy(
                          fabric.lang.WrappedJavaInlineable.$wrap(iter.next()));
                    newTerms[aggIdx++] = m;
                }
                if (fabric.worker.transaction.TransactionManager.getInstance().
                      inTxn()) {
                    val =
                      ((fabric.metrics.MinMetric)
                         new fabric.metrics.MinMetric._Impl(s).$getProxy()).
                        fabric$metrics$MinMetric$(newTerms);
                }
                else {
                    {
                        fabric.metrics.DerivedMetric val$var179 = val;
                        int aggIdx$var180 = aggIdx;
                        fabric.worker.transaction.TransactionManager $tm186 =
                          fabric.worker.transaction.TransactionManager.
                          getInstance();
                        boolean $backoffEnabled189 =
                          fabric.worker.Worker.getWorker(
                                                 ).config.txRetryBackoff;
                        int $backoff187 = 1;
                        boolean $doBackoff188 = true;
                        boolean $retry183 = true;
                        $label181: for (boolean $commit182 = false; !$commit182;
                                        ) {
                            if ($backoffEnabled189) {
                                if ($doBackoff188) {
                                    if ($backoff187 > 32) {
                                        while (true) {
                                            try {
                                                java.lang.Thread.sleep(
                                                                   $backoff187);
                                                break;
                                            }
                                            catch (java.lang.
                                                     InterruptedException $e184) {
                                                
                                            }
                                        }
                                    }
                                    if ($backoff187 < 5000) $backoff187 *= 2;
                                }
                                $doBackoff188 = $backoff187 <= 32 ||
                                                  !$doBackoff188;
                            }
                            $commit182 = true;
                            fabric.worker.transaction.TransactionManager.
                              getInstance().startTransaction();
                            try {
                                val =
                                  ((fabric.metrics.MinMetric)
                                     new fabric.metrics.MinMetric._Impl(s).
                                     $getProxy()).fabric$metrics$MinMetric$(
                                                    newTerms);
                            }
                            catch (final fabric.worker.RetryException $e184) {
                                $commit182 = false;
                                continue $label181;
                            }
                            catch (final fabric.worker.
                                     TransactionRestartingException $e184) {
                                $commit182 = false;
                                fabric.common.TransactionID $currentTid185 =
                                  $tm186.getCurrentTid();
                                if ($e184.tid.isDescendantOf($currentTid185))
                                    continue $label181;
                                if ($currentTid185.parent != null) {
                                    $retry183 = false;
                                    throw $e184;
                                }
                                throw new InternalError(
                                        "Something is broken with " +
                                            "transaction management. Got a signal to restart a " +
                                            "different transaction than the one being managed.");
                            }
                            catch (final fabric.worker.metrics.
                                     LockConflictException $e184) {
                                $commit182 = false;
                                if ($tm186.checkForStaleObjects()) continue;
                                fabric.common.TransactionID $currentTid185 =
                                  $tm186.getCurrentTid();
                                if ($e184.tid.isDescendantOf($currentTid185)) {
                                    $retry183 = true;
                                }
                                else if ($currentTid185.parent != null) {
                                    $retry183 = false;
                                    throw $e184;
                                }
                                else {
                                    throw new InternalError(
                                            "Something is broken with transaction " +
                                                "management. Got a signal for a lock conflict in a different " +
                                                "transaction than the one being managed.");
                                }
                            }
                            catch (final Throwable $e184) {
                                $commit182 = false;
                                if ($tm186.checkForStaleObjects())
                                    continue $label181;
                                $retry183 = false;
                                throw new fabric.worker.AbortException($e184);
                            }
                            finally {
                                if ($commit182) {
                                    try {
                                        fabric.worker.transaction.TransactionManager.
                                          getInstance().commitTransaction();
                                    }
                                    catch (final fabric.worker.
                                             AbortException $e184) {
                                        $commit182 = false;
                                    }
                                    catch (final fabric.worker.
                                             TransactionRestartingException $e184) {
                                        $commit182 = false;
                                        fabric.common.TransactionID
                                          $currentTid185 =
                                          $tm186.getCurrentTid();
                                        if ($currentTid185 != null) {
                                            if ($e184.tid.equals(
                                                            $currentTid185) ||
                                                  !$e184.tid.
                                                  isDescendantOf(
                                                    $currentTid185)) {
                                                throw $e184;
                                            }
                                        }
                                    }
                                } else {
                                    fabric.worker.transaction.TransactionManager.getInstance().abortTransaction();
                                }
                                if (!$commit182 && $retry183) {
                                    {
                                        val = val$var179;
                                        aggIdx = aggIdx$var180;
                                    }
                                    continue $label181;
                                }
                            }
                        }
                    }
                }
                return fabric.metrics.Metric._Impl.findDerivedMetric(s, val);
            }
            else if (fabric.util.Arrays._Impl.asList(tmp.get$terms()).
                       indexOf(other) >= 0) {
                return tmp;
            }
            fabric.metrics.Metric[] newTerms =
              new fabric.metrics.Metric[tmp.get$terms().get$length() + 1];
            for (int i = 0; i < tmp.get$terms().get$length(); i++) {
                newTerms[i] = (fabric.metrics.Metric) tmp.get$terms().get(i);
            }
            newTerms[tmp.get$terms().get$length()] = other;
            java.util.Arrays.sort(newTerms, 0, newTerms.length);
            if (fabric.worker.transaction.TransactionManager.getInstance().
                  inTxn()) {
                val =
                  ((fabric.metrics.MinMetric)
                     new fabric.metrics.MinMetric._Impl(s).$getProxy()).
                    fabric$metrics$MinMetric$(newTerms);
            }
            else {
                {
                    fabric.metrics.DerivedMetric val$var190 = val;
                    fabric.worker.transaction.TransactionManager $tm196 =
                      fabric.worker.transaction.TransactionManager.getInstance(
                                                                     );
                    boolean $backoffEnabled199 =
                      fabric.worker.Worker.getWorker().config.txRetryBackoff;
                    int $backoff197 = 1;
                    boolean $doBackoff198 = true;
                    boolean $retry193 = true;
                    $label191: for (boolean $commit192 = false; !$commit192; ) {
                        if ($backoffEnabled199) {
                            if ($doBackoff198) {
                                if ($backoff197 > 32) {
                                    while (true) {
                                        try {
                                            java.lang.Thread.sleep($backoff197);
                                            break;
                                        }
                                        catch (java.lang.
                                                 InterruptedException $e194) {
                                            
                                        }
                                    }
                                }
                                if ($backoff197 < 5000) $backoff197 *= 2;
                            }
                            $doBackoff198 = $backoff197 <= 32 || !$doBackoff198;
                        }
                        $commit192 = true;
                        fabric.worker.transaction.TransactionManager.
                          getInstance().startTransaction();
                        try {
                            val =
                              ((fabric.metrics.MinMetric)
                                 new fabric.metrics.MinMetric._Impl(s).
                                 $getProxy()).fabric$metrics$MinMetric$(
                                                newTerms);
                        }
                        catch (final fabric.worker.RetryException $e194) {
                            $commit192 = false;
                            continue $label191;
                        }
                        catch (final fabric.worker.
                                 TransactionRestartingException $e194) {
                            $commit192 = false;
                            fabric.common.TransactionID $currentTid195 =
                              $tm196.getCurrentTid();
                            if ($e194.tid.isDescendantOf($currentTid195))
                                continue $label191;
                            if ($currentTid195.parent != null) {
                                $retry193 = false;
                                throw $e194;
                            }
                            throw new InternalError(
                                    "Something is broken with " +
                                        "transaction management. Got a signal to restart a " +
                                        "different transaction than the one being managed.");
                        }
                        catch (final fabric.worker.metrics.
                                 LockConflictException $e194) {
                            $commit192 = false;
                            if ($tm196.checkForStaleObjects()) continue;
                            fabric.common.TransactionID $currentTid195 =
                              $tm196.getCurrentTid();
                            if ($e194.tid.isDescendantOf($currentTid195)) {
                                $retry193 = true;
                            }
                            else if ($currentTid195.parent != null) {
                                $retry193 = false;
                                throw $e194;
                            }
                            else {
                                throw new InternalError(
                                        "Something is broken with transaction " +
                                            "management. Got a signal for a lock conflict in a different " +
                                            "transaction than the one being managed.");
                            }
                        }
                        catch (final Throwable $e194) {
                            $commit192 = false;
                            if ($tm196.checkForStaleObjects())
                                continue $label191;
                            $retry193 = false;
                            throw new fabric.worker.AbortException($e194);
                        }
                        finally {
                            if ($commit192) {
                                try {
                                    fabric.worker.transaction.TransactionManager.
                                      getInstance().commitTransaction();
                                }
                                catch (final fabric.worker.
                                         AbortException $e194) {
                                    $commit192 = false;
                                }
                                catch (final fabric.worker.
                                         TransactionRestartingException $e194) {
                                    $commit192 = false;
                                    fabric.common.TransactionID $currentTid195 =
                                      $tm196.getCurrentTid();
                                    if ($currentTid195 != null) {
                                        if ($e194.tid.equals($currentTid195) ||
                                              !$e194.tid.isDescendantOf(
                                                           $currentTid195)) {
                                            throw $e194;
                                        }
                                    }
                                }
                            }
                            else {
                                fabric.worker.transaction.TransactionManager.
                                  getInstance().abortTransaction();
                            }
                            if (!$commit192 && $retry193) {
                                { val = val$var190; }
                                continue $label191;
                            }
                        }
                    }
                }
            }
            return fabric.metrics.Metric._Impl.findDerivedMetric(s, val);
        }
        
        public fabric.metrics.contracts.enforcement.EnforcementPolicy
          thresholdPolicy(double rate, double base, boolean useWeakCache,
                          final fabric.worker.Store s) {
            fabric.metrics.contracts.Contract[] witnesses =
              new fabric.metrics.contracts.Contract[this.get$terms().get$length(
                                                                       )];
            for (int i = 0; i < this.get$terms().get$length(); i++) {
                witnesses[i] = term(i).getThresholdContract(rate, base);
            }
            return ((fabric.metrics.contracts.enforcement.WitnessPolicy)
                      new fabric.metrics.contracts.enforcement.WitnessPolicy.
                        _Impl(s).
                      $getProxy()).
              fabric$metrics$contracts$enforcement$WitnessPolicy$(witnesses);
        }
        
        public int hashCode() {
            return fabric.util.Arrays._Impl.hashCode(this.get$terms()) * 32 +
              $getStore().hashCode();
        }
        
        public boolean equals(fabric.lang.Object other) {
            if (fabric.lang.Object._Proxy.
                  $getProxy(
                    (java.lang.Object)
                      fabric.lang.WrappedJavaInlineable.
                      $unwrap(other)) instanceof fabric.metrics.MinMetric) {
                fabric.metrics.MinMetric that =
                  (fabric.metrics.MinMetric)
                    fabric.lang.Object._Proxy.$getProxy(other);
                return fabric.util.Arrays._Impl.deepEquals(this.get$terms(),
                                                           that.get$terms()) &&
                  this.$getStore().equals(that.$getStore());
            }
            return false;
        }
        
        public _Impl(fabric.worker.Store $location) { super($location); }
        
        protected fabric.lang.Object._Proxy $makeProxy() {
            return new fabric.metrics.MinMetric._Proxy(this);
        }
        
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
    }
    
    interface _Static extends fabric.lang.Object, Cloneable {
        final class _Proxy extends fabric.lang.Object._Proxy
          implements fabric.metrics.MinMetric._Static {
            public _Proxy(fabric.metrics.MinMetric._Static._Impl impl) {
                super(impl);
            }
            
            public _Proxy(fabric.worker.Store store, long onum) {
                super(store, onum);
            }
            
            public static final fabric.metrics.MinMetric._Static $instance;
            
            static {
                fabric.
                  metrics.
                  MinMetric.
                  _Static.
                  _Impl
                  impl =
                  (fabric.metrics.MinMetric._Static._Impl)
                    fabric.lang.Object._Static._Proxy.
                    $makeStaticInstance(
                      fabric.metrics.MinMetric._Static._Impl.class);
                $instance = (fabric.metrics.MinMetric._Static) impl.$getProxy();
                impl.$init();
            }
        }
        
        class _Impl extends fabric.lang.Object._Impl
          implements fabric.metrics.MinMetric._Static {
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
                return new fabric.metrics.MinMetric._Static._Proxy(this);
            }
            
            private void $init() {  }
        }
        
    }
    
    public static final byte[] $classHash = new byte[] { 65, 63, -119, 45, -44,
    47, -115, 89, 18, 16, 113, -28, 66, -124, -84, 97, -48, 60, 97, -101, 115,
    112, -128, 104, 46, -17, 97, 66, -27, -98, 105, 42 };
    public static final java.lang.String jlc$CompilerVersion$fabil = "0.3.0";
    public static final long jlc$SourceLastModified$fabil = 1520977993000L;
    public static final java.lang.String jlc$ClassType$fabil =
      "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";
}
