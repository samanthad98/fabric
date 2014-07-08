/**
 * Copyright (C) 2010 Fabric project group, Cornell University
 *
 * This file is part of Fabric.
 *
 * Fabric is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 2 of the License, or (at your option) any later
 * version.
 * 
 * Fabric is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 */
package fabric.store.db;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

import fabric.common.util.LongKeyHashMap;
import fabric.common.util.LongKeyMap;
import fabric.common.util.MutableInteger;
import fabric.common.util.Pair;

/**
 * Maps globIDs to pairs of GroupContainers and pin counts. This class is
 * thread-safe and only keeps soft references to the group containers.
 */
final class GroupContainerTable {
  private final LongKeyMap<Pair<SoftRef, MutableInteger>> table;
  private final ReferenceQueue<GroupContainer> queue;

  public GroupContainerTable() {
    this.table = new LongKeyHashMap<Pair<SoftRef, MutableInteger>>();
    this.queue = new ReferenceQueue<GroupContainer>();

    new Collector().start();
  }

  public synchronized GroupContainer getContainer(long globID) {
    Pair<SoftRef, MutableInteger> pair = table.get(globID);
    if (pair == null) return null;
    return pair.first.get();
  }

  public synchronized void unpin(long globID) {
    Pair<SoftRef, MutableInteger> pair = table.get(globID);
    if (pair == null) return;

    if (pair.second.value == 1) remove(globID);
    pair.second.value--;
  }

  public synchronized void put(long globID, GroupContainer groupContainer,
      int pinCount) {
    SoftRef ref = new SoftRef(globID, groupContainer, queue);
    Pair<SoftRef, MutableInteger> entry =
        new Pair<SoftRef, MutableInteger>(ref, new MutableInteger(pinCount));
    table.put(globID, entry);
  }

  public synchronized GroupContainer remove(long globID) {
    Pair<SoftRef, MutableInteger> entry = table.remove(globID);
    if (entry == null) return null;
    
    return entry.first.get();
  }

  private static class SoftRef extends SoftReference<GroupContainer> {
    final long globId;

    public SoftRef(long globID, GroupContainer group,
        ReferenceQueue<GroupContainer> queue) {
      super(group, queue);

      this.globId = globID;
    }
  }

  private final class Collector extends Thread {
    public Collector() {
      super("Group container soft-ref collector");
    }
    
    @Override
    public void run() {
      while (true) {
        try {
          SoftRef ref = (SoftRef) queue.remove();
          remove(ref.globId);
        } catch (InterruptedException e) {
        }
      }
    }
  }
}
