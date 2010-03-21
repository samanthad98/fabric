package jif.lang;

import fabric.worker.Store;
import fabric.lang.DelegatingPrincipal;
import fabric.lang.Principal;

public interface AbstractPrincipal extends DelegatingPrincipal,
    fabric.lang.Object {
  public static class _Proxy extends DelegatingPrincipal._Proxy implements
      AbstractPrincipal {

    public _Proxy(AbstractPrincipal._Impl impl) {
      super(impl);
    }

    public _Proxy(Store store, long onum) {
      super(store, onum);
    }

    @Override
    public native void addDelegatesTo(Principal p);

    @Override
    public native void removeDelegatesTo(Principal p);
  }

  public abstract static class _Impl extends DelegatingPrincipal._Impl
      implements AbstractPrincipal {

    public _Impl(Store store, Label label, String name) {
      super(store, label);
    }

    public native void addDelegatesTo(Principal p);

    public native void removeDelegatesTo(Principal p);
  }
}
