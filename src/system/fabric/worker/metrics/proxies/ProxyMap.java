package fabric.worker.metrics.proxies;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import fabric.common.FastSerializable;
import fabric.metrics.Metric;
import fabric.worker.Store;
import fabric.worker.Worker;

/**
 * Utility for an inlined mapping from stores to proxies for metrics.
 */
@SuppressWarnings("serial")
public class ProxyMap implements FastSerializable, Serializable {

  private Map<Store, Metric> map;

  private static final ProxyMap EMPTY = new ProxyMap();

  /**
   * Create an empty map for the given metric.
   */
  public static ProxyMap emptyMap() {
    return EMPTY;
  }

  private ProxyMap() {
    this.map = new HashMap<>();
  }

  private ProxyMap(ProxyMap original) {
    this.map = new HashMap<>(original.map);
  }

  /**
   * Clear the map.
   * @return the cleared out map.
   */
  public ProxyMap clear() {
    ProxyMap updated = new ProxyMap(this);
    updated.map.clear();
    return updated;
  }

  /**
   * @return true iff the given store is a key in this map.
   */
  public boolean containsKey(Store s) {
    return map.containsKey(s);
  }

  /**
   * @return true iff the given value is a value in this map.
   */
  public boolean containsValue(Metric value) {
    return map.containsValue(value);
  }

  /**
   * @return a Set of entries for this ProxyMap.
   */
  public Set<Entry<Store, Metric>> entrySet() {
    return map.entrySet();
  }

  /**
   * @return the Metric mapped to by the given store key.
   */
  public Metric get(Store key) {
    return map.get(key);
  }

  /** @return true iff this map is empty. */
  public boolean isEmpty() {
    return map.isEmpty();
  }

  /** @return a set of the keys in this map. */
  public Set<Store> keySet() {
    return map.keySet();
  }

  /**
   * Put the given store-metric mapping.
   * @return the updated map.
   */
  public ProxyMap put(Store key, Metric value) {
    ProxyMap updated = new ProxyMap(this);
    updated.map.put(key, (Metric) value.$getProxy());
    return updated;
  }

  /**
   * Put all of the given store-metric mappings.
   * @return the updated map.
   */
  public ProxyMap putAll(ProxyMap m) {
    ProxyMap updated = new ProxyMap(this);
    updated.map.putAll(m.map);
    return updated;
  }

  /**
   * Remove the mapping for the given store.
   * @return the updated map.
   */
  public ProxyMap remove(Store key) {
    ProxyMap updated = new ProxyMap(this);
    updated.map.remove(key);
    return updated;
  }

  /** @return the size of this map. */
  public int size() {
    return map.size();
  }

  /** @return A set of the metrics in this map */
  public Collection<Metric> values() {
    return map.values();
  }

  /* Serializable definitions, need to special case fabric references. */

  private void writeObject(ObjectOutputStream out) throws IOException {
    write(out);
  }

  public ProxyMap(DataInput in) throws IOException {
    int size = in.readInt();
    this.map = new HashMap<>(size);
    for (int i = 0; i < size; i++) {
      Store s = Worker.getWorker().getStore(in.readUTF());
      this.map.put(s, (Metric) new Metric._Proxy(s, in.readLong()).$getProxy());
    }
  }

  /**
   * @throws ClassNotFoundException
   */
  private void readObject(ObjectInputStream in)
      throws IOException, ClassNotFoundException {
    int size = in.readInt();
    this.map = new HashMap<>(size);
    for (int i = 0; i < size; i++) {
      Store s = Worker.getWorker().getStore(in.readUTF());
      this.map.put(s, (Metric) new Metric._Proxy(s, in.readLong()).$getProxy());
    }
  }

  /**
   * @throws ObjectStreamException
   */
  @SuppressWarnings("unused")
  private void readObjectNoData() throws ObjectStreamException {
    map = new HashMap<>();
  }

  @Override
  public void write(DataOutput out) throws IOException {
    out.writeInt(map.size());
    for (Map.Entry<Store, Metric> e : map.entrySet()) {
      out.writeUTF(e.getKey().name());
      out.writeLong(e.getValue().$getOnum());
    }
  }
}
