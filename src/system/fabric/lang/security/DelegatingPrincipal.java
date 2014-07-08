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
package fabric.lang.security;

import java.io.IOException;
import java.io.ObjectInput;
import java.util.Iterator;

import fabric.common.RefTypeEnum;
import fabric.worker.Store;
import fabric.worker.Worker;

public interface DelegatingPrincipal extends Principal {
  /**
   * Jif initializer.
   */
  public DelegatingPrincipal fabric$lang$security$DelegatingPrincipal$();

  void addDelegatesTo(Principal p);

  void removeDelegatesTo(Principal p);

  public static class _Proxy extends Principal._Proxy implements
      DelegatingPrincipal {

    public _Proxy(DelegatingPrincipal._Impl impl) {
      super(impl);
    }

    public _Proxy(Store store, long onum) {
      super(store, onum);
    }

    @Override
    public DelegatingPrincipal fabric$lang$security$DelegatingPrincipal$() {
      return ((DelegatingPrincipal) fetch())
          .fabric$lang$security$DelegatingPrincipal$();
    }

    @Override
    public void addDelegatesTo(Principal p) {
      ((DelegatingPrincipal) fetch()).addDelegatesTo(p);
    }

    @Override
    public void removeDelegatesTo(Principal p) {
      ((DelegatingPrincipal) fetch()).removeDelegatesTo(p);
    }

    public static DelegatingPrincipal $addDefaultDelegates(DelegatingPrincipal p) {
      return DelegatingPrincipal._Impl.$addDefaultDelegates(p);
    }

    public static DelegatingPrincipal jif$cast$fabric_lang_security_DelegatingPrincipal(
        Object o) {
      return DelegatingPrincipal._Impl
          .jif$cast$fabric_lang_security_DelegatingPrincipal(o);
    }
  }

  public abstract static class _Impl extends Principal._Impl implements
      DelegatingPrincipal {

    public _Impl(Store store) {
      super(store);
    }

    public _Impl(Store store, long onum, int version, long expiry, long label,
        long accessLabel, ObjectInput in, Iterator<RefTypeEnum> refTypes,
        Iterator<Long> intraStoreRefs) throws IOException,
        ClassNotFoundException {
      super(store, onum, version, expiry, label, accessLabel, in, refTypes,
          intraStoreRefs);
    }

    public static DelegatingPrincipal $addDefaultDelegates(DelegatingPrincipal p) {
      NodePrincipal store = p.$getStore().getPrincipal();
      p.addDelegatesTo(store);

      NodePrincipal worker = Worker.getWorker().getPrincipal();
      p.addDelegatesTo(worker);

      return p;
    }

    @Override
    public DelegatingPrincipal fabric$lang$security$DelegatingPrincipal$() {
      fabric$lang$security$Principal$();
      return (DelegatingPrincipal) this.$getProxy();
    }

    @Override
    public abstract void addDelegatesTo(Principal p);

    @Override
    public abstract void removeDelegatesTo(Principal p);

    public static DelegatingPrincipal jif$cast$fabric_lang_security_DelegatingPrincipal(
        Object o) {
      //XXX: What is the right access label check??
      return (DelegatingPrincipal) fabric.lang.Object._Proxy.$getProxy(o);
    }
  }
}
