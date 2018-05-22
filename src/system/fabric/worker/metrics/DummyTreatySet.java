package fabric.worker.metrics;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;

import fabric.metrics.Metric;

/**
 * Dummy stand-in value for treaty set for non-metric objects.
 *
 * In the future, we may support other kinds of treaties for non-metric objects.
 */
public class DummyTreatySet extends TreatySet {

  protected static final DummyTreatySet singleton = new DummyTreatySet();

  private DummyTreatySet() {
    super(TreatySet.Kind.DUMMY);
  }

  /** @return the number of treaties. */
  @Override
  public int size() {
    return 0;
  }

  private static final Iterator<MetricTreaty> dummyIterator =
      new Iterator<MetricTreaty>() {
        @Override
        public boolean hasNext() {
          return false;
        }

        @Override
        public MetricTreaty next() {
          return null;
        }
      };

  @Override
  public Iterator<MetricTreaty> iterator() {
    return dummyIterator;
  }

  /** @return a value to use for an empty vector */
  public static TreatySet emptySet(Metric owner) {
    return new MetricTreatySet(owner);
  }

  @Override
  public boolean equals(Object obj) {
    return (obj instanceof DummyTreatySet) && this == obj;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public void writeData(DataOutput out) throws IOException {
    // Do nothing.
  }

  @Override
  public String toString() {
    return "DummyTreatySet";
  }
}
