package fabric.worker.metrics.treaties.enforcement;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

import fabric.common.Logging;
import fabric.common.Threading;
import fabric.metrics.Metric;
import fabric.worker.Worker;
import fabric.worker.metrics.StatsMap;
import fabric.worker.metrics.treaties.MetricTreaty;
import fabric.worker.metrics.treaties.TreatiesBoxRef;
import fabric.worker.metrics.treaties.statements.EqualityStatement;
import fabric.worker.metrics.treaties.statements.ThresholdStatement;
import fabric.worker.metrics.treaties.statements.TreatyStatement;
import fabric.worker.transaction.TransactionManager;

/**
 * Policy enforcing the treaty by directly monitoring the metric value.
 */
public class WitnessPolicy extends EnforcementPolicy {

  private final TreeMultimap<TreatiesBoxRef, TreatyStatement> witnesses;

  public WitnessPolicy(Multimap<Metric, TreatyStatement> witnesses) {
    super(EnforcementPolicy.Kind.WITNESS);
    this.witnesses = TreeMultimap.create(new Comparator<TreatiesBoxRef>() {
      @Override
      public int compare(TreatiesBoxRef a, TreatiesBoxRef b) {
        int storeComp = a.objStoreName.compareTo(b.objStoreName);
        if (storeComp != 0) return storeComp;
        return Long.compare(a.objOnum, b.objOnum);
      }
    }, new Comparator<TreatyStatement>() {
      @Override
      public int compare(TreatyStatement a, TreatyStatement b) {
        if (a instanceof ThresholdStatement) {
          if (b instanceof ThresholdStatement) {
            ThresholdStatement aT = (ThresholdStatement) a;
            ThresholdStatement bT = (ThresholdStatement) b;
            int rateComp = Double.compare(aT.rate, bT.rate);
            if (rateComp != 0) return rateComp;
            return Double.compare(aT.base, bT.base);
          } else {
            return 1;
          }
        } else {
          if (b instanceof ThresholdStatement) {
            EqualityStatement aT = (EqualityStatement) a;
            EqualityStatement bT = (EqualityStatement) b;
            return Double.compare(aT.value, bT.value);
          } else {
            return -1;
          }
        }
      }
    });
    for (Map.Entry<Metric, TreatyStatement> witness : witnesses.entries()) {
      this.witnesses.put(new TreatiesBoxRef(witness.getKey().get$treatiesBox()),
          witness.getValue());
    }
  }

  public WitnessPolicy(DataInput in) throws IOException {
    super(EnforcementPolicy.Kind.WITNESS);
    this.witnesses = TreeMultimap.create(new Comparator<TreatiesBoxRef>() {
      @Override
      public int compare(TreatiesBoxRef a, TreatiesBoxRef b) {
        int storeComp = a.objStoreName.compareTo(b.objStoreName);
        if (storeComp != 0) return storeComp;
        return Long.compare(a.objOnum, b.objOnum);
      }
    }, new Comparator<TreatyStatement>() {
      @Override
      public int compare(TreatyStatement a, TreatyStatement b) {
        if (a instanceof ThresholdStatement) {
          if (b instanceof ThresholdStatement) {
            ThresholdStatement aT = (ThresholdStatement) a;
            ThresholdStatement bT = (ThresholdStatement) b;
            int rateComp = Double.compare(aT.rate, bT.rate);
            if (rateComp != 0) return rateComp;
            return Double.compare(aT.base, bT.base);
          } else {
            return 1;
          }
        } else {
          if (b instanceof ThresholdStatement) {
            EqualityStatement aT = (EqualityStatement) a;
            EqualityStatement bT = (EqualityStatement) b;
            return Double.compare(aT.value, bT.value);
          } else {
            return -1;
          }
        }
      }
    });
    int count = in.readInt();
    for (int i = 0; i < count; i++) {
      this.witnesses.put(new TreatiesBoxRef(in), TreatyStatement.read(in));
    }
  }

  @Override
  public long calculateExpiry(MetricTreaty treaty, StatsMap weakStats) {
    long calculated = Long.MAX_VALUE;
    for (Map.Entry<TreatiesBoxRef, TreatyStatement> witness : witnesses
        .entries()) {
      MetricTreaty witnessTreaty = witness.getKey().get().get$treatiesBox()
          .get$$treaties().get(witness.getValue());
      calculated = Math.min(calculated,
          witnessTreaty == null ? 0 : witnessTreaty.getExpiry());
    }
    return calculated;
  }

  @Override
  public long updatedExpiry(MetricTreaty oldTreaty, StatsMap weakStats) {
    long calculated = Long.MAX_VALUE;
    for (Map.Entry<TreatiesBoxRef, TreatyStatement> witness : witnesses
        .entries()) {
      MetricTreaty witnessTreaty = witness.getKey().get().get$treatiesBox()
          .get$$treaties().get(witness.getValue());
      calculated = Math.min(calculated,
          witnessTreaty == null ? 0 : witnessTreaty.getExpiry());
    }
    return calculated;
  }

  @Override
  protected void writePolicyData(DataOutput out) throws IOException {
    out.writeInt(witnesses.size());
    for (Map.Entry<TreatiesBoxRef, TreatyStatement> witness : witnesses
        .entries()) {
      witness.getKey().write(out);
      witness.getValue().write(out);
    }
  }

  @Override
  public boolean equals(Object obj) {
    return obj == this || (obj instanceof WitnessPolicy
        && witnesses.equals(((WitnessPolicy) obj).witnesses));
  }

  @Override
  public int hashCode() {
    return EnforcementPolicy.Kind.WITNESS.ordinal() ^ witnesses.hashCode();
  }

  @Override
  public String toString() {
    return "enforced by " + witnesses;
  }

  @Override
  public void activate(StatsMap weakStats) {
    if (TransactionManager.getInstance().inTxn()) {
      for (Map.Entry<TreatiesBoxRef, TreatyStatement> witness : witnesses
          .entries()) {
        TreatiesBoxRef witnessMetric = witness.getKey();
        TreatyStatement witnessStatement = witness.getValue();
        if (witnessStatement instanceof ThresholdStatement) {
          witnessMetric.get().refreshThresholdTreaty(false,
              ((ThresholdStatement) witnessStatement).rate,
              ((ThresholdStatement) witnessStatement).base, weakStats);
        } else if (witnessStatement instanceof EqualityStatement) {
          witnessMetric.get().refreshEqualityTreaty(false,
              ((EqualityStatement) witnessStatement).value, weakStats);
        }
        // Small optimization to give up once we know this policy isn't going
        // anywhere.
        MetricTreaty w = witnessMetric.get().get$treatiesBox().get$$treaties()
            .get(witness.getValue());
        if (w == null || !w.valid()) break;
      }
    } else {
      Future<?> futures[] = new Future<?>[witnesses.size()];
      int i = 0;
      for (Map.Entry<TreatiesBoxRef, TreatyStatement> witness : witnesses
          .entries()) {
        final TreatiesBoxRef witnessMetric = witness.getKey();
        final TreatyStatement witnessStatement = witness.getValue();
        futures[i++] = Threading.getPool().submit(new Runnable() {
          @Override
          public void run() {
            if (witnessStatement instanceof ThresholdStatement) {
              ((Metric._Proxy) witnessMetric.get().$getProxy())
                  .refreshThresholdTreaty$remote(
                      Worker.getWorker().getWorker(witnessMetric.objStoreName),
                      null, false, ((ThresholdStatement) witnessStatement).rate,
                      ((ThresholdStatement) witnessStatement).base, weakStats);
            } else if (witnessStatement instanceof EqualityStatement) {
              ((Metric._Proxy) witnessMetric.get().$getProxy())
                  .refreshEqualityTreaty$remote(
                      Worker.getWorker().getWorker(witnessMetric.objStoreName),
                      null, false, ((EqualityStatement) witnessStatement).value,
                      weakStats);
            }
          }
        });
      }
      for (Future<?> f : futures) {
        try {
          f.get();
        } catch (ExecutionException e) {
          throw new InternalError(
              "Execution exception running witness activation!", e);
        } catch (InterruptedException e) {
          Logging.logIgnoredInterruptedException(e);
        }
      }
    }
  }

  @Override
  public void apply(MetricTreaty t) {
    // Observe the witnesses
    for (Map.Entry<TreatiesBoxRef, TreatyStatement> witness : witnesses
        .entries()) {
      Metric m = witness.getKey().get();
      if (m == null) Logging.METRICS_LOGGER.log(Level.SEVERE,
          "A witness metric was null applying to {0}", t);
      MetricTreaty w =
          m.get$treatiesBox().get$$treaties().get(witness.getValue());
      if (w == null) Logging.METRICS_LOGGER.log(Level.SEVERE,
          "A witness treaty was null applying to {0}", t);
      w.addObserver(t.getMetric(), t.getId());
    }
  }

  @Override
  public void unapply(MetricTreaty t) {
    // TODO: make this async where applicable.
    // Stop observing the metric.
    for (Map.Entry<TreatiesBoxRef, TreatyStatement> witness : witnesses
        .entries()) {
      // Don't worry about missing witnesses, it's possible they were cleared
      // out anticipating this.
      Metric m = witness.getKey().get();
      if (m == null) continue;
      MetricTreaty w =
          m.get$treatiesBox().get$$treaties().get(witness.getValue());
      if (w == null) continue;
      w.removeObserver(t.getMetric(), t.getId());
    }
  }

  @Override
  public void shiftPolicies(MetricTreaty t, EnforcementPolicy newPolicy) {
    if (newPolicy instanceof WitnessPolicy) {
      WitnessPolicy nextPol = (WitnessPolicy) newPolicy;
      // Only add and remove nonoverlapping witnesses
      Set<Map.Entry<TreatiesBoxRef, TreatyStatement>> toBeRemoved =
          new HashSet<>(witnesses.entries());
      toBeRemoved.removeAll(nextPol.witnesses.entries());
      for (Map.Entry<TreatiesBoxRef, TreatyStatement> e : toBeRemoved) {
        Metric m = e.getKey().get();
        if (m == null) continue;
        MetricTreaty w = m.get$treatiesBox().get$$treaties().get(e.getValue());
        if (w == null) continue;
        w.removeObserver(t.getMetric(), t.getId());
      }

      Set<Map.Entry<TreatiesBoxRef, TreatyStatement>> toBeAdded =
          new HashSet<>(nextPol.witnesses.entries());
      toBeRemoved.removeAll(witnesses.entries());
      for (Map.Entry<TreatiesBoxRef, TreatyStatement> e : toBeAdded) {
        Metric m = e.getKey().get();
        if (m == null) Logging.METRICS_LOGGER.log(Level.SEVERE,
            "A witness metric was null applying to {0}", t);
        MetricTreaty w = m.get$treatiesBox().get$$treaties().get(e.getValue());
        if (w == null) Logging.METRICS_LOGGER.log(Level.SEVERE,
            "A witness treaty was null applying to {0}", t);
        w.addObserver(t.getMetric(), t.getId());
      }
    } else {
      // Do the normal thing.
      unapply(t);
      newPolicy.apply(t);
    }
  }
}
