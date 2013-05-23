package fabric.dissemination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fabric.common.Logging;
import fabric.common.exceptions.AccessException;
import fabric.common.util.OidKeyHashMap;
import fabric.common.util.Pair;
import fabric.worker.RemoteStore;
import fabric.worker.Worker;

/**
 * The cache object used by the disseminator to store globs. Essentially a
 * hashtable specialized for globs; it also fetches globs directly from stores
 * when needed.
 */
public class Cache {

  /**
   * Cache of globs, indexed by the oid of the glob's head object.
   */
  private final fabric.common.util.Cache<Pair<RemoteStore, Long>, Glob> map;

  /**
   * The set of fetch locks. Used to prevent threads from concurrently
   * attempting to fetch the same object.
   */
  private final OidKeyHashMap<FetchLock> fetchLocks;

  private class FetchLock {
    private boolean ready = false;
    private Glob glob = null;
  }

  public Cache() {
    this.map = new fabric.common.util.Cache<Pair<RemoteStore, Long>, Glob>();
    this.fetchLocks = new OidKeyHashMap<Cache.FetchLock>();
  }

  /**
   * Retrieves a glob from the cache, without trying to fetch it from the store.
   * 
   * @param store
   *          the store of the object to retrieve.
   * @param onum
   *          the onum of the object.
   * @return the glob, if it is in the cache; null otherwise.
   */
  public Glob get(RemoteStore store, long onum) {
    return get(store, onum, false);
  }

  /**
   * Retrieves a glob from the cache or fetches it from the store.
   * 
   * @param store
   *          the store of the object to retrieve.
   * @param onum
   *          the onum of the object.
   * @param fetch
   *          whether the glob should be should fetched from store in the event
   *          of a cache miss.
   * @return the glob, or null if fetch is false and glob does not exists in
   *         cache.
   */
  public Glob get(RemoteStore store, long onum, boolean fetch) {
    Pair<RemoteStore, Long> key = new Pair<RemoteStore, Long>(store, onum);

    Glob g = map.get(key);
    if (g != null || !fetch) return g;

    // Need to fetch. Check the object table in case some other thread fetched
    // the object while we weren't looking. Use fetchLocks as a mutex to
    // atomically check the cache and create a mutex for the object cache in
    // the event of a cache miss.
    FetchLock fetchLock;
    boolean needToFetch = false;
    synchronized (fetchLocks) {
      Glob result = map.get(key);
      if (result != null) return result;

      // Object not in cache. Get/create a mutex for fetching the object.
      fetchLock = fetchLocks.get(store, onum);
      if (fetchLock == null) {
        needToFetch = true;
        fetchLock = new FetchLock();
        fetchLocks.put(store, onum, fetchLock);
      }
    }

    synchronized (fetchLock) {
      if (needToFetch) {
        // We are responsible for fetching the object.
        fetchLock.glob = fetch(key);
        fetchLock.ready = true;

        // Object now cached. Remove our mutex from fetchLocks.
        synchronized (fetchLocks) {
          fetchLocks.remove(store, onum);
        }

        // Signal any other threads that might be waiting for our fetch.
        fetchLock.notifyAll();
      } else {
        // Wait for another thread to fetch the object for us.
        while (!fetchLock.ready) {
          try {
            fetchLock.wait();
          } catch (InterruptedException e) {
            Logging.logIgnoredInterruptedException(e);
          }
        }
      }

      return fetchLock.glob;
    }
  }

  /**
   * Fetches a glob from the store and caches it.
   */
  private Glob fetch(Pair<RemoteStore, Long> oid) {
    Glob g = null;
    RemoteStore store = oid.first;
    long onum = oid.second;

    try {
      g = store.readEncryptedObjectFromStore(onum);
    } catch (AccessException e) {
    }

    if (g != null) {
      while (true) {
        Glob old = get(store, onum);

        if (old == null || old.isOlderThan(g)) {
          if (!map.replace(oid, old, g)) {
            // Value got replaced while we weren't looking. Try again.
            continue;
          }
        } else {
          g = old;
        }

        break;
      }
    }

    return g;
  }

  /**
   * Put given glob into the cache.
   * 
   * @param store
   *          the store of the object.
   * @param onum
   *          the onum of the object.
   * @param g
   *          the glob.
   */
  public void put(RemoteStore store, long onum, Glob g) {
    Pair<RemoteStore, Long> key = new Pair<RemoteStore, Long>(store, onum);

    while (true) {
      Glob old = get(store, onum);

      if (old == null || old.isOlderThan(g)) {
        if (map.replace(key, old, g)) break;

        // Value got replaced while we weren't looking. Try again.
        continue;
      }

      break;
    }
  }

  /**
   * Updates the dissemination and worker cache with the given glob. If the
   * caches do not have entries for the given oid, then nothing is changed.
   * 
   * @return true iff there was a cache entry for the given oid.
   */
  public boolean updateEntry(RemoteStore store, long onum, Glob g) {
    // Update the local worker's cache.
    // XXX What happens if the worker isn't trusted to decrypt the glob?
    boolean result = Worker.getWorker().updateCache(store, g.decrypt());

    Pair<RemoteStore, Long> key = new Pair<RemoteStore, Long>(store, onum);

    while (true) {
      Glob old = get(store, onum);
      if (old == null) return result;

      if (old.isOlderThan(g)) {
        old.copyDissemStateTo(g);
        if (map.replace(key, old, g)) break;

        // Value got replaced while we weren't looking. Try again.
        continue;
      }

      break;
    }

    return true;
  }

  /**
   * Returns a snapshot of the timestamp for each OID currently in the cache.
   * This set is NOT backed by the underlying map. If new keys are inserted or
   * removed from the cache, they will not be reflected by the set returned.
   * However, no synchronization is needed for working with the set.
   */
  public Set<Pair<Pair<RemoteStore, Long>, Long>> timestamps() {
    Set<Pair<Pair<RemoteStore, Long>, Long>> result =
        new HashSet<Pair<Pair<RemoteStore, Long>, Long>>();

    for (Pair<RemoteStore, Long> key : map.keys()) {
      Glob glob = map.get(key);
      if (glob != null)
        result.add(new Pair<Pair<RemoteStore, Long>, Long>(key, glob
            .getTimestamp()));
    }

    return result;
  }

  /**
   * Returns a snapshot set of the timestamp for each OID currently in the
   * cache. The set is sorted in descending order by the popularity of the
   * corresponding objects. Like {@code timestamps()}, the returned set is not
   * backed by the underlying table.
   */
  public List<Pair<Pair<RemoteStore, Long>, Long>> sortedTimestamps() {
    List<Pair<Pair<RemoteStore, Long>, Long>> k =
        new ArrayList<Pair<Pair<RemoteStore, Long>, Long>>(timestamps());

    Collections.sort(k, TIMESTAMP_COMPARATOR);

    return k;
  }

  private final Comparator<Pair<Pair<RemoteStore, Long>, Long>> TIMESTAMP_COMPARATOR =
      new Comparator<Pair<Pair<RemoteStore, Long>, Long>>() {
        @Override
        public int compare(Pair<Pair<RemoteStore, Long>, Long> o1,
            Pair<Pair<RemoteStore, Long>, Long> o2) {
          Glob g1 = map.get(o1.first);
          Glob g2 = map.get(o2.first);

          if (g1 == g2) {
            return 0;
          }

          if (g1 == null) {
            return 1;
          }

          if (g2 == null) {
            return -1;
          }

          return g2.frequency() - g1.frequency();
        }
      };

}
