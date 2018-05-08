package fabric.store;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;

import fabric.common.Logging;
import fabric.common.ObjectGroup;
import fabric.common.Threading;
import fabric.common.exceptions.AccessException;
import fabric.common.util.LongHashSet;
import fabric.common.util.LongIterator;
import fabric.common.util.LongKeyCache;
import fabric.common.util.LongKeyHashMap;
import fabric.common.util.LongKeyMap;
import fabric.common.util.LongSet;
import fabric.dissemination.ObjectGlob;
import fabric.store.db.GroupContainer;
import fabric.worker.Worker;
import fabric.worker.remote.RemoteWorker;

/**
 * Keeps track of who's subscribed to what object. Handles subscriptions for a
 * single store.
 */
public class SubscriptionManager {

  private final ConcurrentMap<RemoteWorker, LongSet> notificationQueues;

  /**
   * The set of nodes subscribed to each onum. The second component of each pair
   * indicates whether the node is subscribed as a dissemination node. (true =
   * dissemination, false = worker)
   */
  private final LongKeyCache<ConcurrentMap<RemoteWorker, Boolean>> subscriptions;

  /**
   * The name of the store for which we are managing subscriptions.
   */
  private final String store;

  /**
   * The transaction manager corresponding to the store for which we are
   * managing subscriptions.
   */
  private final TransactionManager tm;

  /**
   * @param tm
   *          The transaction manager corresponding to the store for which
   *          subscriptions are to be managed.
   */
  public SubscriptionManager(String store, TransactionManager tm) {
    this.store = store;
    this.notificationQueues = new ConcurrentHashMap<>();
    this.tm = tm;
    this.subscriptions = new LongKeyCache<>();
  }

  /**
   * Unsubscribes the given worker to the given onums.
   *
   * @param worker
   * the worker being unsubscribed.
   * @param onums
   * the onums to be unsubscribed from.
   */
  public void unsubscribe(RemoteWorker worker, LongSet unsubscribes) {
    for (LongIterator iter = unsubscribes.iterator(); iter.hasNext();) {
      long onum = iter.next();
      ConcurrentMap<RemoteWorker, Boolean> subscribers =
          subscriptions.get(onum);
      if (subscribers != null) {
        subscribers.remove(worker);
      }
    }
  }

  /**
   * Subscribes the given worker to the given onum.
   *
   * @param dissemSubscribe
   *          If true, then the given subscriber will be subscribed as a
   *          dissemination node; otherwise it will be subscribed as a worker.
   */
  public void subscribe(long onum, RemoteWorker worker,
      boolean dissemSubscribe) {
    ConcurrentMap<RemoteWorker, Boolean> subscribers = subscriptions.get(onum);
    if (subscribers == null) {
      subscribers = new ConcurrentHashMap<>();
      ConcurrentMap<RemoteWorker, Boolean> existing =
          subscriptions.putIfAbsent(onum, subscribers);
      if (existing != null) subscribers = existing;
    }

    subscribers.putIfAbsent(worker, dissemSubscribe);
  }

  private class UpdateNotifier extends Threading.NamedRunnable {

    private final RemoteWorker worker;
    private final LongSet queue;

    public UpdateNotifier(RemoteWorker worker) {
      super("Update notifier for " + worker.name());
      this.worker = worker;
      this.queue = notificationQueues.get(worker);
    }

    @Override
    protected final void runImpl() {
      handle();
    }

    protected void handle() {
      try {
        while (true) {
          LongSet curGroup = new LongHashSet();
          synchronized (queue) {
            while (queue.isEmpty()) {
              try {
                queue.wait();
              } catch (InterruptedException e) {
                Logging.logIgnoredInterruptedException(e);
              }
            }
            curGroup.addAll(queue);
          }
          runGroup(curGroup);
        }
      } catch (Throwable t) {
        Logging.MISC_LOGGER.log(Level.SEVERE,
            "Subscription runner exited with exception", t);
      }
    }

    protected void runGroup(LongSet onums) {
      Map<ObjectGlob, LongSet> globs = new HashMap<>();
      LongSet onumsSent = new LongHashSet();
      Set<ObjectGroup> groups = new HashSet<>();
      LongKeyMap<LongSet> associatedOnums = new LongKeyHashMap<>();
      // Gather the updates.
      for (LongIterator it = onums.iterator(); it.hasNext();) {
        long onum = it.next();

        ConcurrentMap<RemoteWorker, Boolean> subMap = subscriptions.get(onum);
        if (subMap == null) {
          synchronized (queue) {
            queue.remove(onum);
          }
          continue;
        }
        boolean isDissem = subMap.get(worker);

        GroupContainer groupContainer;
        try {
          // Skip if the onum represents a surrogate.
          if (tm.read(onum).isSurrogate()) continue;

          // Now grab it and open it up to more notifications.
          synchronized (queue) {
            queue.remove(onum);
          }
          groupContainer = tm.getGroupContainer(onum);
          LongSet curAssociatedOnums =
              tm.getAssociatedOnumsExcluded(onum, onums);
          if (isDissem) {
            ObjectGlob glob = groupContainer.getGlob();
            if (!globs.containsKey(glob)) {
              globs.put(glob, new LongHashSet());
            }
            globs.get(glob).add(onum);
            LongSet trueOnums = new LongHashSet(curAssociatedOnums);
            for (LongIterator iter = curAssociatedOnums.iterator(); iter
                .hasNext();) {
              long associated = iter.next();
              // Subscribe the worker to the associated, if it wasn't already.
              subscribe(associated, worker, true);
              if (!trueOnums.contains(associated)) continue;
              // Don't bother packaging each onum separately
              GroupContainer associatedContainer =
                  tm.getGroupContainer(associated);
              trueOnums.removeAll(associatedContainer.onums);
              trueOnums.add(associated);
              ObjectGlob associatedGlob = associatedContainer.getGlob();
              if (!globs.containsKey(associatedGlob)) {
                globs.put(associatedGlob, new LongHashSet());
              }
              globs.get(associatedGlob).add(associated);
            }
            curAssociatedOnums = trueOnums;
          } else {
            onumsSent.add(onum);
            groups.add(groupContainer.getGroup(worker.getPrincipal()));
            for (LongIterator iter = curAssociatedOnums.iterator(); iter
                .hasNext();) {
              long associated = iter.next();
              // Subscribe the worker to the associated, if it wasn't already.
              subscribe(associated, worker, false);
              GroupContainer associatedContainer =
                  tm.getGroupContainer(associated);
              groups.add(associatedContainer.getGroup(worker.getPrincipal()));
            }
          }
          associatedOnums.put(onum, curAssociatedOnums);
        } catch (AccessException e) {
          throw new InternalError(e);
        }
      }

      // Now send it.
      worker.notifyObjectUpdates(store, globs, onumsSent, groups,
          associatedOnums);
    }
  }

  private void submit(RemoteWorker worker, LongSet addition) {
    LongSet init = new LongHashSet();
    LongSet current = notificationQueues.putIfAbsent(worker, init);
    if (current == null) {
      current = init;
      Thread runner = new Thread(new UpdateNotifier(worker));
      runner.setDaemon(true);
      runner.start();
    }
    synchronized (current) {
      current.addAll(addition);
      current.notifyAll();
    }
  }

  /**
   * Notifies the subscription manager that a set of objects has been updated by a
   * particular worker.
   */
  public void notifyUpdate(LongSet onums, RemoteWorker worker) {
    if (!Worker.getWorker().config.useSubscriptions) return;

    Set<RemoteWorker> handled = new HashSet<>();
    for (LongIterator iter = onums.iterator(); iter.hasNext();) {
      long onum = iter.next();
      // TODO: Can I concurrently iterate over the entries like this?
      ConcurrentMap<RemoteWorker, Boolean> subMap = subscriptions.get(onum);
      if (subMap != null) {
        for (Map.Entry<RemoteWorker, Boolean> e : subMap.entrySet()) {
          RemoteWorker subbed = e.getKey();
          boolean isDissem = e.getValue();
          if (!isDissem || subbed.equals(worker) || handled.contains(subbed))
            continue;
          submit(subbed, onums);
          handled.add(subbed);
        }
      }
    }
  }
}
