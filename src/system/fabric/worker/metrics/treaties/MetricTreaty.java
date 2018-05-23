package fabric.worker.metrics.treaties;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import fabric.metrics.Metric;
import fabric.worker.metrics.ImmutableObserverSet;
import fabric.worker.metrics.treaties.enforcement.EnforcementPolicy;
import fabric.worker.metrics.treaties.enforcement.NoPolicy;
import fabric.worker.metrics.treaties.statements.ThresholdStatement;
import fabric.worker.metrics.treaties.statements.TreatyStatement;

/**
 * An inlineable representation of a treaty defined as a statement about a
 * metric.
 */
public class MetricTreaty implements Treaty<MetricTreaty> {

  /** The metric this treaty is over. */
  public final OidRef<Metric> metric;

  /**
   * The id of this treaty within the metric.  Used to reference treaties
   * through their associated metrics.
   */
  public final long id;

  /**
   * Has this treaty been activated since creation?
   */
  public final boolean activated;

  /** The asserted statement. */
  public final TreatyStatement statement;

  /** The observers of this treaty. */
  public final ImmutableObserverSet observers;

  /**
   * Maximum time in milliseconds before the currently advertised expiry that an
   * extension should be applied within.
   */
  public static long UPDATE_THRESHOLD = 1000;

  /** The policy being used to enforce this treaty. */
  private final EnforcementPolicy policy;

  /** The expiration time. */
  public final long expiry;

  public MetricTreaty(Metric metric, long id, TreatyStatement statement) {
    this.metric = new OidRef<>(metric);
    this.id = id;
    this.activated = false;
    this.statement = statement;
    this.observers = ImmutableObserverSet.emptySet();
    this.policy = new NoPolicy();
    this.expiry = policy.calculateExpiry(this);
  }

  /**
   * Deserialization constructor.
   *
   * Metric is provided on the side to avoid unnecessary repeated references in
   * serialized TreatySet.
   */
  public MetricTreaty(OidRef<Metric> m, DataInput in) throws IOException {
    this.metric = m;
    this.id = in.readLong();
    this.activated = in.readBoolean();
    this.statement = TreatyStatement.read(in);
    this.observers = new ImmutableObserverSet(in);
    this.policy = EnforcementPolicy.read(in);
    this.expiry = in.readLong();
  }

  @Override
  public void write(DataOutput out) throws IOException {
    // Don't serialize the metric, it'll be provided on deserialization.
    //this.metric.write(out);
    out.writeLong(id);
    out.writeBoolean(activated);
    this.statement.write(out);
    this.observers.write(out);
    this.policy.write(out);
    out.writeLong(this.expiry);
  }

  /**
   * Constructor for the update step, not changing the policy.
   */
  private MetricTreaty(MetricTreaty original) {
    this.metric = original.metric;
    this.id = original.id;
    this.activated = original.activated;
    this.statement = original.statement;
    this.observers = original.observers;
    this.policy = original.policy;
    this.expiry = this.policy.updatedExpiry(original);
  }

  /**
   * Constructor for the update step, not changing the policy.
   */
  private MetricTreaty(MetricTreaty original, EnforcementPolicy policy) {
    this.metric = original.metric;
    this.id = original.id;
    this.activated = original.activated || !(policy instanceof NoPolicy);
    this.statement = original.statement;
    this.observers = original.observers;
    this.policy = original.policy;
    this.expiry = this.policy.updatedExpiry(original);
  }

  @Override
  public MetricTreaty update(boolean asyncExtension) {
    long updatedCurExpiry = this.policy.updatedExpiry(this);
    if (updatedCurExpiry != this.expiry) {
      if (asyncExtension || updatedCurExpiry > System.currentTimeMillis()) {
        // Keep using the same policy if the policy still gives a good expiry or
        // we're only doing an async extension.
        return new MetricTreaty(this);
      } else {
        // Otherwise, try a different policy.
        // TODO
        return this;
      }
    }
    // If nothing changed, don't update.
    return this;
  }

  @Override
  public boolean valid() {
    return (expiry > System.currentTimeMillis());
  }

  @Override
  public long getExpiry() {
    return expiry;
  }

  /**
   * @return the metric
   */
  public Metric getMetric() {
    return metric.get();
  }

  @Override
  public long getId() {
    return id;
  }

  @Override
  public ImmutableObserverSet getObservers() {
    return observers;
  }

  /**
   * @return the policy
   */
  public EnforcementPolicy getPolicy() {
    return policy;
  }

  @Override
  public boolean isActive() {
    return activated;
  }

  @Override
  public boolean implies(MetricTreaty other) {
    return this.statement.implies(other.statement);
  }

  public boolean implies(Metric m, double rate, double base) {
    if (this.statement instanceof ThresholdStatement) {
      return getMetric().equals(m)
          && this.statement.implies(new ThresholdStatement(rate, base));
    }
    return false;
  }
}