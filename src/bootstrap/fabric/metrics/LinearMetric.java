package fabric.metrics;

import fabric.lang.*;
import fabric.lang.security.*;
import fabric.worker.*;
import fabric.worker.remote.*;
import java.lang.*;
import fabric.util.Arrays;
import fabric.util.LinkedHashMap;
import fabric.util.Map;
import fabric.util.Set;
import fabric.util.TreeSet;
import fabric.util.ArrayList;
import fabric.util.HashMap;
import fabric.util.Iterator;
import fabric.util.List;
import fabric.metrics.contracts.Bound;
import fabric.metrics.contracts.Contract;
import fabric.metrics.contracts.enforcement.DirectPolicy;
import fabric.metrics.contracts.enforcement.EnforcementPolicy;
import fabric.metrics.contracts.enforcement.WitnessPolicy;
import fabric.metrics.util.Matrix;
import fabric.metrics.util.Observer;
import fabric.metrics.util.Subject;
import fabric.worker.Store;

/**
 * A {@link Metric} for the entries of Cm\u20d7 where m\u20d7 is a {@link Metric} and C is
 * a {@link Matrix} of coefficients.
 *
 * Currently deprecated since it's not as easy to generalize for more and more
 * operations.
 */
public interface LinearMetric extends fabric.metrics.DerivedMetric {
    public fabric.metrics.util.Matrix get$matrix();
    
    public fabric.metrics.util.Matrix set$matrix(
      fabric.metrics.util.Matrix val);
    
    public fabric.metrics.util.Matrix get$varMatrix();
    
    public fabric.metrics.util.Matrix set$varMatrix(fabric.metrics.util.Matrix val);
    
    /**
   * @param store
   *        the Store that holds this {@link Metric}
   * @param matrix
   *        The coefficients matrix as a double[][]
   * @param terms
   *        The {@link Metric}s this applies to
   */
    public fabric.metrics.LinearMetric fabric$metrics$LinearMetric$(
      fabric.metrics.util.Matrix matrix, fabric.metrics.Metric[] terms);
    
    public double computePresetR();
    
    public double computePresetB();
    
    public double computePresetV();
    
    public double computePresetN();
    
    public double computeValue(boolean useWeakCache);
    
    /**
   * @param i
   *            an index in the value vector
   * @return the freshly computed ith entry in the value vector
   */
    public double computeValue(int i);
    
    public double computeVelocity(boolean useWeakCache);
    
    public double computeNoise(boolean useWeakCache);
    
    public java.lang.String toString();
    
    public fabric.metrics.DerivedMetric times(double scalar);
    
    /**
   * {@inheritDoc}
   * <p>
   * {@link LinearMetric}s try to consolidate local computations so that there
   * isn't unnecessary nodes in the {@link Subject}-{@link Observer} tree for
   * {@link #handleUpdates()}.
   */
    public fabric.metrics.DerivedMetric plus(fabric.metrics.Metric other);
    
    /**
   * {@inheritDoc}
   * <p>
   * {@link LinearMetric}s try to consolidate local computations so that there
   * isn't unnecessary nodes in the {@link Subject}-{@link Observer} tree for
   * {@link #handleUpdates()}.
   */
    public fabric.metrics.Metric min(fabric.metrics.Metric other);
    
    public fabric.metrics.DerivedMetric copyOn(final fabric.worker.Store s);
    
    /**
   * XXX/TODO: This is not appropriately "low contention" and still uses
   * strong operations for model parameters.
   */
    public fabric.metrics.contracts.enforcement.EnforcementPolicy
      thresholdPolicy(double rate, double base, boolean useWeakCache,
                      final fabric.worker.Store str);
    
    public int hashCode();
    
    public boolean equals(fabric.lang.Object other);
    
    public static class _Proxy extends fabric.metrics.DerivedMetric._Proxy
      implements fabric.metrics.LinearMetric {
        public fabric.metrics.util.Matrix get$matrix() {
            return ((fabric.metrics.LinearMetric._Impl) fetch()).get$matrix();
        }
        
        public fabric.metrics.util.Matrix set$matrix(
          fabric.metrics.util.Matrix val) {
            return ((fabric.metrics.LinearMetric._Impl) fetch()).set$matrix(
                                                                   val);
        }
        
        public fabric.metrics.util.Matrix get$varMatrix() {
            return ((fabric.metrics.LinearMetric._Impl) fetch()).get$varMatrix(
                                                                   );
        }
        
        public fabric.metrics.util.Matrix set$varMatrix(
          fabric.metrics.util.Matrix val) {
            return ((fabric.metrics.LinearMetric._Impl) fetch()).set$varMatrix(
                                                                   val);
        }
        
        public fabric.metrics.LinearMetric fabric$metrics$LinearMetric$(
          fabric.metrics.util.Matrix arg1, fabric.metrics.Metric[] arg2) {
            return ((fabric.metrics.LinearMetric) fetch()).
              fabric$metrics$LinearMetric$(arg1, arg2);
        }
        
        public double computeValue(int arg1) {
            return ((fabric.metrics.LinearMetric) fetch()).computeValue(arg1);
        }
        
        public fabric.metrics.DerivedMetric copyOn(fabric.worker.Store arg1) {
            return ((fabric.metrics.LinearMetric) fetch()).copyOn(arg1);
        }
        
        public _Proxy(LinearMetric._Impl impl) { super(impl); }
        
        public _Proxy(fabric.worker.Store store, long onum) {
            super(store, onum);
        }
    }
    
    public static class _Impl extends fabric.metrics.DerivedMetric._Impl
      implements fabric.metrics.LinearMetric {
        public fabric.metrics.util.Matrix get$matrix() {
            fabric.worker.transaction.TransactionManager.getInstance().
              registerRead(this);
            return this.matrix;
        }
        
        public fabric.metrics.util.Matrix set$matrix(
          fabric.metrics.util.Matrix val) {
            fabric.worker.transaction.TransactionManager tm =
              fabric.worker.transaction.TransactionManager.getInstance();
            boolean transactionCreated = tm.registerWrite(this);
            this.matrix = val;
            if (transactionCreated) tm.commitTransaction();
            return val;
        }
        
        /**
   * The {@link Matrix} that defines the transformation applied to this
   * {@link LinearMetric}s terms.
   */
        protected fabric.metrics.util.Matrix matrix;
        
        public fabric.metrics.util.Matrix get$varMatrix() {
            fabric.worker.transaction.TransactionManager.getInstance().
              registerRead(this);
            return this.varMatrix;
        }
        
        public fabric.metrics.util.Matrix set$varMatrix(
          fabric.metrics.util.Matrix val) {
            fabric.worker.transaction.TransactionManager tm =
              fabric.worker.transaction.TransactionManager.getInstance();
            boolean transactionCreated = tm.registerWrite(this);
            this.varMatrix = val;
            if (transactionCreated) tm.commitTransaction();
            return val;
        }
        
        /**
   * The {@link Matrix} that defines the transformation applied to this
   * {@link LinearMetric}s terms's noise estimates.
   */
        protected fabric.metrics.util.Matrix varMatrix;
        
        /**
   * @param store
   *        the Store that holds this {@link Metric}
   * @param matrix
   *        The coefficients matrix as a double[][]
   * @param terms
   *        The {@link Metric}s this applies to
   */
        public fabric.metrics.LinearMetric fabric$metrics$LinearMetric$(
          fabric.metrics.util.Matrix matrix, fabric.metrics.Metric[] terms) {
            fabric$metrics$DerivedMetric$(terms);
            if (matrix.rows() == 0)
                throw new java.lang.IllegalArgumentException(
                        "LinearMetric needs at least 1 coefficient row!");
            if (matrix.columns() != terms.length)
                throw new java.lang.IllegalArgumentException(
                        "LinearMetric coefficient columns size as the dimension of term: " +
                          matrix.toString());
            this.set$matrix(
                   ((fabric.metrics.LinearMetric._Impl)
                      this.fetch()).normalize(matrix).copy(getStore()));
            this.set$varMatrix(
                   this.get$matrix().elementMultiply(
                                       this.get$matrix()).copy(getStore()));
            initialize();
            return (fabric.metrics.LinearMetric) this.$getProxy();
        }
        
        public double computePresetR() { return 0.0; }
        
        public double computePresetB() { return 0.0; }
        
        public double computePresetV() { return 0.0; }
        
        public double computePresetN() { return 0.0; }
        
        /**
   * Utility to normalize the {@link Metric} terms and {@link Matrix}
   * associated with this {@link LinearMetric}.
   *
   * @param matrix
   *        the original matrix
   * @return the matrix rearranged for sorted terms, sorted rows, and
   *       deduplication of rows.
   */
        private fabric.metrics.util.Matrix normalize(
          fabric.metrics.util.Matrix matrix) {
            fabric.lang.arrays.ObjectArray normTerms =
              (fabric.lang.arrays.ObjectArray)
                new fabric.lang.arrays.ObjectArray._Impl(
                  this.$getStore()).fabric$lang$arrays$ObjectArray$(
                                      this.get$$updateLabel(),
                                      this.get$$updateLabel().confPolicy(),
                                      fabric.metrics.Metric._Proxy.class,
                                      this.get$terms().get$length()).$getProxy(
                                                                       );
            fabric.util.Arrays._Impl.arraycopy(this.get$terms(), 0, normTerms,
                                               0,
                                               this.get$terms().get$length());
            fabric.util.Arrays._Impl.sort(normTerms);
            fabric.metrics.util.Matrix colNormed =
              ((fabric.metrics.util.Matrix)
                 new fabric.metrics.util.Matrix._Impl(
                   this.$getStore()).$getProxy()).fabric$metrics$util$Matrix$(
                                                    matrix.rows(),
                                                    this.get$terms().get$length(
                                                                       ));
            for (int i = 0; i < this.get$terms().get$length(); i++) {
                int oldIdx =
                  fabric.util.Arrays._Impl.asList(this.get$terms()).
                  indexOf((fabric.metrics.Metric) normTerms.get(i));
                colNormed.setColumn(i, matrix.getColumn(oldIdx));
            }
            this.set$terms(normTerms);
            return fabric.metrics.LinearMetric._Impl.normalizeRows(
                                                       colNormed, 0,
                                                       colNormed.rows());
        }
        
        /**
   * Utility for normalizing a coefficients {@link Matrix} by sorting rows and
   * eliminating duplicates.
   *
   * @param m
   *        the {@link Matrix} being normalized
   * @return the normalized {@link Matrix}
   */
        private static fabric.metrics.util.Matrix normalizeRows(
          fabric.metrics.util.Matrix m, int start, int end) {
            if (end - start > 1) {
                int mid = start + (end - start) / 2;
                fabric.metrics.util.Matrix sub1 =
                  fabric.metrics.LinearMetric._Impl.normalizeRows(m, start,
                                                                  mid);
                fabric.metrics.util.Matrix sub2 =
                  fabric.metrics.LinearMetric._Impl.normalizeRows(m, mid, end);
                fabric.metrics.util.Matrix
                  merged =
                  ((fabric.metrics.util.Matrix)
                     new fabric.
                       metrics.
                       util.
                       Matrix.
                       _Impl(
                       fabric.metrics.LinearMetric._Static._Proxy.$instance.
                           $getStore()).
                     $getProxy()).fabric$metrics$util$Matrix$(end - start,
                                                              m.columns());
                int idx1 = 0;
                int idx2 = 0;
                for (int i = 0; i < merged.rows(); i++) {
                    int comp =
                      fabric.metrics.LinearMetric._Impl.rowCompare(
                                                          sub1.getRow(idx1),
                                                          sub2.getRow(idx2));
                    if (comp < 0) {
                        merged.setRow(i, sub1.getRow(idx1++));
                    } else if (comp > 0) {
                        merged.setRow(i, sub2.getRow(idx2++));
                    } else {
                        idx2++;
                        merged.removeRow(i--);
                    }
                }
                return merged;
            }
            else if (start == end) {
                return ((fabric.metrics.util.Matrix)
                          new fabric.
                            metrics.
                            util.
                            Matrix.
                            _Impl(
                            fabric.metrics.LinearMetric._Static._Proxy.
                              $instance.
                                $getStore()).
                          $getProxy()).fabric$metrics$util$Matrix$(0, 0);
            }
            return fabric.metrics.util.Matrix._Impl.singleRow(m.getRow(start));
        }
        
        /**
   * Utility for comparing two rows of a {@link Matrix} during
   * {@link #normalizeRows(Matrix)}.
   *
   * @param r1
   *        the first row as a double[]
   * @param r2
   *        the second row as a double[]
   * @return 0 if r1 == r2, 1 if r1 > r2 (lexicographically), and -1
   *       otherwise.
   */
        private static int rowCompare(fabric.lang.arrays.doubleArray r1,
                                      fabric.lang.arrays.doubleArray r2) {
            for (int i = 0; i < r1.get$length(); i++) {
                if ((double) r1.get(i) < (double) r2.get(i)) return -1;
                if ((double) r1.get(i) > (double) r2.get(i)) return 1;
            }
            return 0;
        }
        
        public double computeValue(boolean useWeakCache) {
            fabric.lang.arrays.doubleArray values =
              (fabric.lang.arrays.doubleArray)
                new fabric.lang.arrays.doubleArray._Impl(
                  this.$getStore()).fabric$lang$arrays$doubleArray$(
                                      this.get$$updateLabel(),
                                      this.get$$updateLabel().confPolicy(),
                                      this.get$terms().get$length()).$getProxy(
                                                                       );
            for (int i = 0; i < this.get$terms().get$length(); i++) {
                values.set(i,
                           ((fabric.metrics.Metric)
                              this.get$terms().get(i)).value(useWeakCache));
            }
            fabric.lang.arrays.doubleArray results =
              this.get$matrix().multiply(values);
            double result = java.lang.Double.MAX_VALUE;
            for (int i = 0; i < results.get$length(); i++) {
                result = java.lang.Math.min(result, (double) results.get(i));
            }
            return result;
        }
        
        /**
   * @param i
   *            an index in the value vector
   * @return the freshly computed ith entry in the value vector
   */
        public double computeValue(int i) {
            fabric.lang.arrays.doubleArray values =
              (fabric.lang.arrays.doubleArray)
                new fabric.lang.arrays.doubleArray._Impl(
                  this.$getStore()).fabric$lang$arrays$doubleArray$(
                                      this.get$$updateLabel(),
                                      this.get$$updateLabel().confPolicy(),
                                      this.get$terms().get$length()).$getProxy(
                                                                       );
            for (int j = 0; j < this.get$terms().get$length(); j++) {
                values.set(j,
                           ((fabric.metrics.Metric)
                              this.get$terms().get(j)).value());
            }
            fabric.lang.arrays.doubleArray results =
              this.get$matrix().multiply(values);
            return (double) results.get(i);
        }
        
        public double computeVelocity(boolean useWeakCache) {
            fabric.lang.arrays.doubleArray values =
              (fabric.lang.arrays.doubleArray)
                new fabric.lang.arrays.doubleArray._Impl(
                  this.$getStore()).fabric$lang$arrays$doubleArray$(
                                      this.get$$updateLabel(),
                                      this.get$$updateLabel().confPolicy(),
                                      this.get$terms().get$length()).$getProxy(
                                                                       );
            for (int i = 0; i < this.get$terms().get$length(); i++) {
                values.set(i,
                           ((fabric.metrics.Metric)
                              this.get$terms().get(i)).velocity(useWeakCache));
            }
            fabric.lang.arrays.doubleArray results =
              this.get$matrix().multiply(values);
            double result = java.lang.Double.MAX_VALUE;
            for (int i = 0; i < results.get$length(); i++) {
                result = java.lang.Math.min(result, (double) results.get(i));
            }
            return result;
        }
        
        /**
   * @param i
   *            an index in the value vector
   * @return the freshly estimated ith entry in the velocity vector
   */
        private double computeVelocity(int i) {
            fabric.lang.arrays.doubleArray values =
              (fabric.lang.arrays.doubleArray)
                new fabric.lang.arrays.doubleArray._Impl(
                  this.$getStore()).fabric$lang$arrays$doubleArray$(
                                      this.get$$updateLabel(),
                                      this.get$$updateLabel().confPolicy(),
                                      this.get$terms().get$length()).$getProxy(
                                                                       );
            for (int j = 0; j < this.get$terms().get$length(); j++) {
                values.set(j,
                           ((fabric.metrics.Metric)
                              this.get$terms().get(j)).velocity());
            }
            fabric.lang.arrays.doubleArray results =
              this.get$matrix().multiply(values);
            return (double) results.get(i);
        }
        
        public double computeNoise(boolean useWeakCache) {
            fabric.lang.arrays.doubleArray noises =
              (fabric.lang.arrays.doubleArray)
                new fabric.lang.arrays.doubleArray._Impl(
                  this.$getStore()).fabric$lang$arrays$doubleArray$(
                                      this.get$$updateLabel(),
                                      this.get$$updateLabel().confPolicy(),
                                      this.get$terms().get$length()).$getProxy(
                                                                       );
            for (int i = 0; i < this.get$terms().get$length(); i++) {
                noises.set(i,
                           ((fabric.metrics.Metric)
                              this.get$terms().get(i)).noise(useWeakCache));
            }
            fabric.lang.arrays.doubleArray resultNoises =
              this.get$varMatrix().multiply(noises);
            double noise = 0;
            for (int i = 0; i < resultNoises.get$length(); i++) {
                noise = java.lang.Math.max(noise, (double) resultNoises.get(i));
            }
            return noise;
        }
        
        /**
   * @param i
   *            an index in the value vector
   * @return the freshly estimated ith entry in the noise vector
   */
        private double computeNoise(int i) {
            fabric.lang.arrays.doubleArray noises =
              (fabric.lang.arrays.doubleArray)
                new fabric.lang.arrays.doubleArray._Impl(
                  this.$getStore()).fabric$lang$arrays$doubleArray$(
                                      this.get$$updateLabel(),
                                      this.get$$updateLabel().confPolicy(),
                                      this.get$terms().get$length()).$getProxy(
                                                                       );
            for (int j = 0; j < this.get$terms().get$length(); j++) {
                noises.set(j,
                           ((fabric.metrics.Metric)
                              this.get$terms().get(j)).noise());
            }
            fabric.lang.arrays.doubleArray resultNoises =
              this.get$varMatrix().multiply(noises);
            return (double) resultNoises.get(i);
        }
        
        public java.lang.String toString() {
            java.lang.String str = "min(";
            boolean nonEmpty = false;
            for (int i = 0; i < this.get$matrix().rows(); i++) {
                boolean nonEmptyRow = false;
                for (int j = 0; j < this.get$terms().get$length(); j++) {
                    if (this.get$matrix().get(i, j) == 0) continue;
                    if (nonEmptyRow) {
                        str += " + ";
                    } else if (nonEmpty) {
                        str += ", ";
                    }
                    nonEmptyRow = true;
                    str +=
                      "(" +
                      java.lang.String.
                        valueOf(
                          fabric.lang.WrappedJavaInlineable.
                              $unwrap((fabric.metrics.Metric)
                                        this.get$terms().get(j))) +
                      " * " +
                      this.get$matrix().get(i, j) +
                      ")";
                }
                if (nonEmptyRow) nonEmpty = true;
            }
            return str + ")@" + getStore().name();
        }
        
        public fabric.metrics.DerivedMetric times(double scalar) {
            fabric.metrics.Metric[] termsCopy =
              new fabric.metrics.Metric[this.get$terms().get$length()];
            for (int i = 0; i < this.get$terms().get$length(); i++)
                termsCopy[i] = (fabric.metrics.Metric) this.get$terms().get(i);
            final fabric.worker.Store s = getStore();
            return fabric.metrics.Metric._Impl.
              findDerivedMetric(
                s,
                ((fabric.metrics.LinearMetric)
                   new fabric.metrics.LinearMetric._Impl(s).$getProxy()).
                    fabric$metrics$LinearMetric$(
                      this.get$matrix().multiply(scalar), termsCopy));
        }
        
        /**
   * {@inheritDoc}
   * <p>
   * {@link LinearMetric}s try to consolidate local computations so that there
   * isn't unnecessary nodes in the {@link Subject}-{@link Observer} tree for
   * {@link #handleUpdates()}.
   */
        public fabric.metrics.DerivedMetric plus(fabric.metrics.Metric other) {
            final fabric.worker.Store s = getStore();
            if (fabric.lang.Object._Proxy.
                  $getProxy(
                    (java.lang.Object)
                      fabric.lang.WrappedJavaInlineable.
                      $unwrap(other)) instanceof fabric.metrics.LinearMetric &&
                  other.$getStore().equals(s)) {
                fabric.metrics.LinearMetric that =
                  (fabric.metrics.LinearMetric)
                    fabric.lang.Object._Proxy.$getProxy(other);
                fabric.util.Set termsBag =
                  ((fabric.util.TreeSet)
                     new fabric.util.TreeSet._Impl(this.$getStore()).$getProxy(
                                                                       )).
                  fabric$util$TreeSet$();
                termsBag.addAll(
                           fabric.util.Arrays._Impl.asList(this.get$terms()));
                termsBag.addAll(
                           fabric.util.Arrays._Impl.asList(that.get$terms()));
                fabric.metrics.util.Matrix thisExpanded =
                  ((fabric.metrics.util.Matrix)
                     new fabric.metrics.util.Matrix._Impl(
                       this.$getStore()).$getProxy()).
                  fabric$metrics$util$Matrix$(this.get$matrix().rows(),
                                              termsBag.size());
                fabric.metrics.util.Matrix thatExpanded =
                  ((fabric.metrics.util.Matrix)
                     new fabric.metrics.util.Matrix._Impl(
                       this.$getStore()).$getProxy()).
                  fabric$metrics$util$Matrix$(that.get$matrix().rows(),
                                              termsBag.size());
                fabric.metrics.Metric[] newTerms =
                  new fabric.metrics.Metric[termsBag.size()];
                int aggIdx = 0;
                int thisIdx = 0;
                int thatIdx = 0;
                fabric.util.Iterator mIter = termsBag.iterator();
                while (mIter.hasNext()) {
                    fabric.metrics.Metric m =
                      (fabric.metrics.Metric)
                        fabric.lang.Object._Proxy.$getProxy(mIter.next());
                    if (((fabric.metrics.Metric) this.get$terms().get(thisIdx)).
                          equals(m)) {
                        thisExpanded.setColumn(
                                       aggIdx,
                                       this.get$matrix().getColumn(thisIdx++));
                    }
                    if (((fabric.metrics.Metric) that.get$terms().get(thatIdx)).
                          equals(m)) {
                        thatExpanded.setColumn(
                                       aggIdx,
                                       that.get$matrix().getColumn(thatIdx++));
                    }
                    newTerms[aggIdx++] = m;
                }
                fabric.metrics.util.Matrix combined =
                  ((fabric.metrics.util.Matrix)
                     new fabric.metrics.util.Matrix._Impl(
                       this.$getStore()).$getProxy()).
                  fabric$metrics$util$Matrix$(thisExpanded.rows() *
                                                  thatExpanded.rows(),
                                              newTerms.length);
                for (int i = 0; i < thisExpanded.rows(); i++) {
                    for (int j = 0; j < thatExpanded.rows(); j++) {
                        int row = i * thatExpanded.rows() + j;
                        for (int k = 0; k < newTerms.length; k++) {
                            combined.set(row,
                                         k,
                                         thisExpanded.get(i, k) +
                                             thatExpanded.get(j, k));
                        }
                    }
                }
                return fabric.metrics.Metric._Impl.
                  findDerivedMetric(
                    s,
                    ((fabric.metrics.LinearMetric)
                       new fabric.metrics.LinearMetric._Impl(s).$getProxy()).
                        fabric$metrics$LinearMetric$(combined, newTerms));
            }
            else if (fabric.util.Arrays._Impl.asList(this.get$terms()).
                       indexOf(other) >= 0) {
                int idx =
                  fabric.util.Arrays._Impl.asList(this.get$terms()).indexOf(
                                                                      other);
                fabric.metrics.util.Matrix newCs = this.get$matrix().copy();
                for (int i = 0; i < newCs.rows(); i++) {
                    newCs.set(i, idx, newCs.get(i, idx) + 1);
                }
                fabric.metrics.Metric[] termsCopy =
                  new fabric.metrics.Metric[this.get$terms().get$length()];
                for (int i = 0; i < this.get$terms().get$length(); i++)
                    termsCopy[i] = (fabric.metrics.Metric)
                                     this.get$terms().get(i);
                return fabric.metrics.Metric._Impl.
                  findDerivedMetric(
                    s,
                    ((fabric.metrics.LinearMetric)
                       new fabric.metrics.LinearMetric._Impl(s).$getProxy()).
                        fabric$metrics$LinearMetric$(newCs, termsCopy));
            }
            else if (isSingleStore() && !other.$getStore().equals(s)) {
                return fabric.metrics.Metric._Impl.
                  findDerivedMetric(
                    s,
                    ((fabric.metrics.LinearMetric)
                       new fabric.metrics.LinearMetric._Impl(s).$getProxy()).
                        fabric$metrics$LinearMetric$(
                          fabric.metrics.util.Matrix._Impl.constant(1, 2, 1),
                          new fabric.metrics.Metric[] { (fabric.metrics.LinearMetric)
                                                          this.$getProxy(),
                            other }));
            }
            fabric.metrics.Metric[] newTerms =
              new fabric.metrics.Metric[this.get$terms().get$length() + 1];
            for (int i = 0; i < this.get$terms().get$length(); i++)
                newTerms[i] = (fabric.metrics.Metric) this.get$terms().get(i);
            newTerms[this.get$terms().get$length()] = other;
            java.util.Arrays.sort(newTerms, 0, newTerms.length);
            int idx =
              java.util.Arrays.asList(newTerms).
              indexOf((java.lang.Object)
                        fabric.lang.WrappedJavaInlineable.$unwrap(other));
            fabric.lang.arrays.doubleArray newCol =
              (fabric.lang.arrays.doubleArray)
                new fabric.lang.arrays.doubleArray._Impl(
                  this.$getStore()).fabric$lang$arrays$doubleArray$(
                                      this.get$$updateLabel(),
                                      this.get$$updateLabel().confPolicy(),
                                      this.get$matrix().rows()).$getProxy();
            for (int i = 0; i < newCol.get$length(); i++) { newCol.set(i, 1); }
            fabric.metrics.util.Matrix newCs = this.get$matrix().copy();
            newCs.insertColumn(idx, newCol);
            return fabric.metrics.Metric._Impl.
              findDerivedMetric(
                s,
                ((fabric.metrics.LinearMetric)
                   new fabric.metrics.LinearMetric._Impl(s).$getProxy()).
                    fabric$metrics$LinearMetric$(newCs, newTerms));
        }
        
        /**
   * {@inheritDoc}
   * <p>
   * {@link LinearMetric}s try to consolidate local computations so that there
   * isn't unnecessary nodes in the {@link Subject}-{@link Observer} tree for
   * {@link #handleUpdates()}.
   */
        public fabric.metrics.Metric min(fabric.metrics.Metric other) {
            final fabric.worker.Store s = $getStore();
            if (fabric.lang.Object._Proxy.
                  $getProxy(
                    (java.lang.Object)
                      fabric.lang.WrappedJavaInlineable.
                      $unwrap(other)) instanceof fabric.metrics.LinearMetric &&
                  other.$getStore().equals(s)) {
                fabric.metrics.LinearMetric that =
                  (fabric.metrics.LinearMetric)
                    fabric.lang.Object._Proxy.$getProxy(other);
                fabric.util.Set termsBag =
                  ((fabric.util.TreeSet)
                     new fabric.util.TreeSet._Impl(this.$getStore()).$getProxy(
                                                                       )).
                  fabric$util$TreeSet$();
                termsBag.addAll(
                           fabric.util.Arrays._Impl.asList(this.get$terms()));
                termsBag.addAll(
                           fabric.util.Arrays._Impl.asList(that.get$terms()));
                fabric.metrics.util.Matrix thisExpanded =
                  ((fabric.metrics.util.Matrix)
                     new fabric.metrics.util.Matrix._Impl(
                       this.$getStore()).$getProxy()).
                  fabric$metrics$util$Matrix$(this.get$matrix().rows(),
                                              termsBag.size());
                fabric.metrics.util.Matrix thatExpanded =
                  ((fabric.metrics.util.Matrix)
                     new fabric.metrics.util.Matrix._Impl(
                       this.$getStore()).$getProxy()).
                  fabric$metrics$util$Matrix$(that.get$matrix().rows(),
                                              termsBag.size());
                fabric.metrics.Metric[] newTerms =
                  new fabric.metrics.Metric[termsBag.size()];
                int aggIdx = 0;
                int thisIdx = 0;
                int thatIdx = 0;
                fabric.util.Iterator mIter = termsBag.iterator();
                while (mIter.hasNext()) {
                    fabric.metrics.Metric m =
                      (fabric.metrics.Metric)
                        fabric.lang.Object._Proxy.$getProxy(mIter.next());
                    if (((fabric.metrics.Metric) this.get$terms().get(thisIdx)).
                          equals(m)) {
                        thisExpanded.setColumn(
                                       aggIdx,
                                       this.get$matrix().getColumn(thisIdx++));
                    }
                    if (((fabric.metrics.Metric) that.get$terms().get(thatIdx)).
                          equals(m)) {
                        thatExpanded.setColumn(
                                       aggIdx,
                                       that.get$matrix().getColumn(thatIdx++));
                    }
                    newTerms[aggIdx++] = m;
                }
                fabric.metrics.util.Matrix combined =
                  ((fabric.metrics.util.Matrix)
                     new fabric.metrics.util.Matrix._Impl(
                       this.$getStore()).$getProxy()).
                  fabric$metrics$util$Matrix$(thisExpanded.rows() +
                                                  thatExpanded.rows(),
                                              newTerms.length);
                for (int i = 0; i < thisExpanded.rows(); i++) {
                    for (int j = 0; j < newTerms.length; j++) {
                        combined.set(i, j, thisExpanded.get(i, j));
                        combined.set(i + thisExpanded.rows(), j,
                                     thatExpanded.get(i, j));
                    }
                }
                return fabric.metrics.Metric._Impl.
                  findDerivedMetric(
                    s,
                    ((fabric.metrics.LinearMetric)
                       new fabric.metrics.LinearMetric._Impl(s).$getProxy()).
                        fabric$metrics$LinearMetric$(combined, newTerms));
            }
            else if (fabric.util.Arrays._Impl.asList(this.get$terms()).
                       indexOf(other) >= 0) {
                int idx =
                  fabric.util.Arrays._Impl.asList(this.get$terms()).indexOf(
                                                                      other);
                fabric.metrics.util.Matrix newCs = this.get$matrix().copy();
                fabric.lang.arrays.doubleArray newRow =
                  (fabric.lang.arrays.doubleArray)
                    new fabric.lang.arrays.doubleArray._Impl(
                      this.$getStore()).fabric$lang$arrays$doubleArray$(
                                          this.get$$updateLabel(),
                                          this.get$$updateLabel().confPolicy(),
                                          this.get$terms().get$length(
                                                             )).$getProxy();
                newRow.set(idx, 1);
                newCs.addRow(newRow);
                fabric.metrics.Metric[] termsCopy =
                  new fabric.metrics.Metric[this.get$terms().get$length()];
                for (int i = 0; i < this.get$terms().get$length(); i++)
                    termsCopy[i] = (fabric.metrics.Metric)
                                     this.get$terms().get(i);
                return fabric.metrics.Metric._Impl.
                  findDerivedMetric(
                    s,
                    ((fabric.metrics.LinearMetric)
                       new fabric.metrics.LinearMetric._Impl(s).$getProxy()).
                        fabric$metrics$LinearMetric$(newCs, termsCopy));
            }
            else if (isSingleStore() && !other.$getStore().equals(s)) {
                return fabric.metrics.Metric._Impl.
                  findDerivedMetric(
                    s,
                    ((fabric.metrics.LinearMetric)
                       new fabric.metrics.LinearMetric._Impl(s).$getProxy()).
                        fabric$metrics$LinearMetric$(
                          fabric.metrics.util.Matrix._Impl.identity(2),
                          new fabric.metrics.Metric[] { (fabric.metrics.LinearMetric)
                                                          this.$getProxy(),
                            other }));
            }
            fabric.metrics.Metric[] newTerms =
              new fabric.metrics.Metric[this.get$terms().get$length() + 1];
            for (int i = 0; i < this.get$terms().get$length(); i++)
                newTerms[i] = (fabric.metrics.Metric) this.get$terms().get(i);
            newTerms[this.get$terms().get$length()] = other;
            java.util.Arrays.sort(newTerms, 0, newTerms.length);
            int idx =
              java.util.Arrays.asList(newTerms).
              indexOf((java.lang.Object)
                        fabric.lang.WrappedJavaInlineable.$unwrap(other));
            fabric.metrics.util.Matrix newCs = this.get$matrix().copy();
            newCs.addColumn(
                    (fabric.lang.arrays.doubleArray)
                      new fabric.lang.arrays.doubleArray._Impl(
                        this.$getStore()).fabric$lang$arrays$doubleArray$(
                                            this.get$$updateLabel(),
                                            this.get$$updateLabel().confPolicy(
                                                                      ),
                                            this.get$matrix().rows()).$getProxy(
                                                                        ));
            fabric.lang.arrays.doubleArray newRow =
              (fabric.lang.arrays.doubleArray)
                new fabric.lang.arrays.doubleArray._Impl(
                  this.$getStore()).fabric$lang$arrays$doubleArray$(
                                      this.get$$updateLabel(),
                                      this.get$$updateLabel().confPolicy(),
                                      this.get$terms().get$length() +
                                          1).$getProxy();
            newRow.set(idx, 1);
            newCs.addRow(newRow);
            return fabric.metrics.Metric._Impl.
              findDerivedMetric(
                s,
                ((fabric.metrics.LinearMetric)
                   new fabric.metrics.LinearMetric._Impl(s).$getProxy()).
                    fabric$metrics$LinearMetric$(newCs, newTerms));
        }
        
        public fabric.metrics.DerivedMetric copyOn(
          final fabric.worker.Store s) {
            fabric.metrics.Metric[] termsCopy =
              new fabric.metrics.Metric[this.get$terms().get$length()];
            for (int i = 0; i < this.get$terms().get$length(); i++)
                termsCopy[i] = (fabric.metrics.Metric) this.get$terms().get(i);
            return fabric.metrics.Metric._Impl.
              findDerivedMetric(
                s,
                ((fabric.metrics.LinearMetric)
                   new fabric.metrics.LinearMetric._Impl(s).$getProxy()).
                    fabric$metrics$LinearMetric$(this.get$matrix(), termsCopy));
        }
        
        /**
   * XXX/TODO: This is not appropriately "low contention" and still uses
   * strong operations for model parameters.
   */
        public fabric.metrics.contracts.enforcement.EnforcementPolicy
          thresholdPolicy(double rate, double base, boolean useWeakCache,
                          final fabric.worker.Store str) {
            if (isSingleStore())
                return ((fabric.metrics.contracts.enforcement.DirectPolicy)
                          new fabric.metrics.contracts.enforcement.DirectPolicy.
                            _Impl(str).
                          $getProxy()).
                  fabric$metrics$contracts$enforcement$DirectPolicy$(
                    (fabric.metrics.LinearMetric) this.$getProxy(), rate, base);
            fabric.util.Map witnesses =
              ((fabric.util.LinkedHashMap)
                 new fabric.util.LinkedHashMap._Impl(
                   this.$getStore()).$getProxy()).fabric$util$LinkedHashMap$();
            long currentTime = java.lang.System.currentTimeMillis();
            double baseNow =
              fabric.metrics.contracts.Bound._Impl.value(rate, base,
                                                         currentTime);
            for (int i = 0; i < this.get$matrix().rows(); i++) {
                double totalValue = computeValue(i);
                double totalVelocity = ((fabric.metrics.LinearMetric._Impl)
                                          this.fetch()).computeVelocity(i);
                double totalNoise = ((fabric.metrics.LinearMetric._Impl)
                                       this.fetch()).computeNoise(i);
                double numTerms = this.get$terms().get$length();
                for (int j = 0; j < numTerms; j++) {
                    double c = this.get$matrix().get(i, j);
                    fabric.metrics.Metric m = term(j);
                    double scaledX = m.value() * c;
                    double scaledV = m.velocity() * c;
                    double scaledN = m.noise() * c * c;
                    double r = scaledV - (totalVelocity - rate) / numTerms;
                    r = r / c;
                    double b = scaledX - scaledN / totalNoise *
                      (totalValue - baseNow);
                    b = b / c;
                    if (c < 0) {
                        m = m.times(-1);
                        b = -b;
                        r = -r;
                    }
                    double[] normalized =
                      fabric.metrics.contracts.Bound._Impl.createBound(
                                                             r, b, currentTime);
                    if (!witnesses.containsKey(m) ||
                          !((fabric.metrics.contracts.Contract)
                              fabric.lang.Object._Proxy.$getProxy(
                                                          witnesses.get(m))).
                          implies(m, normalized[0], normalized[1])) {
                        witnesses.put(m,
                                      m.getThresholdContract(r, b,
                                                             currentTime));
                    }
                }
            }
            fabric.metrics.contracts.Contract[] finalWitnesses =
              new fabric.metrics.contracts.Contract[witnesses.size()];
            int i = 0;
            for (fabric.util.Iterator iter = witnesses.values().iterator();
                 iter.hasNext(); ) {
                finalWitnesses[i++] =
                  (fabric.metrics.contracts.Contract)
                    fabric.lang.Object._Proxy.$getProxy(iter.next());
            }
            return ((fabric.metrics.contracts.enforcement.WitnessPolicy)
                      new fabric.metrics.contracts.enforcement.WitnessPolicy.
                        _Impl(str).
                      $getProxy()).
              fabric$metrics$contracts$enforcement$WitnessPolicy$(
                finalWitnesses);
        }
        
        public int hashCode() {
            int hash = fabric.util.Arrays._Impl.hashCode(this.get$terms()) *
              32 + this.get$matrix().hashCode();
            return hash * 32 + $getStore().hashCode();
        }
        
        public boolean equals(fabric.lang.Object other) {
            if (fabric.lang.Object._Proxy.
                  $getProxy(
                    (java.lang.Object)
                      fabric.lang.WrappedJavaInlineable.
                      $unwrap(other)) instanceof fabric.metrics.LinearMetric) {
                fabric.metrics.LinearMetric that =
                  (fabric.metrics.LinearMetric)
                    fabric.lang.Object._Proxy.$getProxy(other);
                return this.get$matrix().equals(that.get$matrix()) &&
                  fabric.util.Arrays._Impl.deepEquals(this.get$terms(),
                                                      that.get$terms()) &&
                  this.$getStore().equals(that.$getStore());
            }
            return false;
        }
        
        public _Impl(fabric.worker.Store $location) { super($location); }
        
        protected fabric.lang.Object._Proxy $makeProxy() {
            return new fabric.metrics.LinearMetric._Proxy(this);
        }
        
        public void $serialize(java.io.ObjectOutput out,
                               java.util.List refTypes,
                               java.util.List intraStoreRefs,
                               java.util.List interStoreRefs)
              throws java.io.IOException {
            super.$serialize(out, refTypes, intraStoreRefs, interStoreRefs);
            $writeRef($getStore(), this.matrix, refTypes, out, intraStoreRefs,
                      interStoreRefs);
            $writeRef($getStore(), this.varMatrix, refTypes, out,
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
            this.matrix = (fabric.metrics.util.Matrix)
                            $readRef(fabric.metrics.util.Matrix._Proxy.class,
                                     (fabric.common.RefTypeEnum)
                                       refTypes.next(), in, store,
                                     intraStoreRefs, interStoreRefs);
            this.varMatrix = (fabric.metrics.util.Matrix)
                               $readRef(fabric.metrics.util.Matrix._Proxy.class,
                                        (fabric.common.RefTypeEnum)
                                          refTypes.next(), in, store,
                                        intraStoreRefs, interStoreRefs);
        }
        
        public void $copyAppStateFrom(fabric.lang.Object._Impl other) {
            super.$copyAppStateFrom(other);
            fabric.metrics.LinearMetric._Impl src =
              (fabric.metrics.LinearMetric._Impl) other;
            this.matrix = src.matrix;
            this.varMatrix = src.varMatrix;
        }
    }
    
    interface _Static extends fabric.lang.Object, Cloneable {
        final class _Proxy extends fabric.lang.Object._Proxy
          implements fabric.metrics.LinearMetric._Static {
            public _Proxy(fabric.metrics.LinearMetric._Static._Impl impl) {
                super(impl);
            }
            
            public _Proxy(fabric.worker.Store store, long onum) {
                super(store, onum);
            }
            
            public static final fabric.metrics.LinearMetric._Static $instance;
            
            static {
                fabric.
                  metrics.
                  LinearMetric.
                  _Static.
                  _Impl
                  impl =
                  (fabric.metrics.LinearMetric._Static._Impl)
                    fabric.lang.Object._Static._Proxy.
                    $makeStaticInstance(
                      fabric.metrics.LinearMetric._Static._Impl.class);
                $instance = (fabric.metrics.LinearMetric._Static)
                              impl.$getProxy();
                impl.$init();
            }
        }
        
        class _Impl extends fabric.lang.Object._Impl
          implements fabric.metrics.LinearMetric._Static {
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
                return new fabric.metrics.LinearMetric._Static._Proxy(this);
            }
            
            private void $init() {  }
        }
        
    }
    
    public static final byte[] $classHash = new byte[] { 95, -53, -100, 80, 17,
    -37, -20, -52, -13, 99, -40, -8, -47, -81, 80, -121, -69, 6, 45, 115, -34,
    12, -87, 41, 56, -95, -15, 114, -45, -66, -128, -68 };
    public static final java.lang.String jlc$CompilerVersion$fabil = "0.3.0";
    public static final long jlc$SourceLastModified$fabil = 1523829666000L;
    public static final java.lang.String jlc$ClassType$fabil =
      "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";
}
