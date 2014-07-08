/**
 * Copyright (C) 2010-2012 Fabric project group, Cornell University
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
package fabric.worker;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

import fabric.lang.Object._Impl;
import fabric.worker.transaction.ReadMapEntry;

public class FabricSoftRef extends SoftReference<_Impl> {

  private static final ReferenceQueue<_Impl> queue =
      new ReferenceQueue<_Impl>();

  static {
    new RefCollector().start();
  }

  public Store store;
  public long onum;
  public ReadMapEntry readMapEntry;

  public FabricSoftRef(Store store, long onum, _Impl impl) {
    super(impl, queue);
    this.store = store;
    this.onum = onum;
  }

  /**
   * Evicts the _Impl associated with this soft reference from the worker's
   * cache.
   * 
   * @return true iff the _Impl was found in cache.
   */
  public boolean evict() {
    return store.evict(onum);
  }

  /**
   * Decrements the associated ReadMapEntry's pin count and does garbage
   * collection if possible.
   */
  public void depin() {
    if (readMapEntry != null && readMapEntry.depin()) readMapEntry = null;
  }

  public void readMapEntry(ReadMapEntry readMapEntry) {
    this.readMapEntry = readMapEntry;
  }

  private static class RefCollector extends Thread {
    RefCollector() {
      super("Reference collector");
      setDaemon(true);
    }

    @Override
    public void run() {
      while (true) {
        try {
          FabricSoftRef ref = (FabricSoftRef) queue.remove();
          ref.depin();
        } catch (InterruptedException e) {
        }
      }
    }
  }

}
