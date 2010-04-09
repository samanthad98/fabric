package fabric.lang.security;

import java.io.IOException;
import java.io.ObjectInput;
import java.util.Iterator;

import fabric.worker.Store;
import fabric.net.UnreachableNodeException;
import fabric.common.RefTypeEnum;
import fabric.lang.Object;

public interface PrincipalUtil extends Object {
  public static class _Impl extends Object._Impl implements PrincipalUtil {
    public _Impl(Store store, Label label) throws UnreachableNodeException {
      super(store, label);
    }

    public _Impl(Store store, long onum, int version, long expiry, long label,
        ObjectInput serializedInput, Iterator<RefTypeEnum> refTypes,
        Iterator<Long> intraStoreRefs) throws IOException,
        ClassNotFoundException {
      super(store, onum, version, expiry, label, serializedInput, refTypes,
          intraStoreRefs);
    }

    public static native boolean equals(Principal p, Principal q);
  }

  public interface TopPrincipal extends Principal, Object {
    public static class _Impl extends Principal._Impl implements TopPrincipal {
      public _Impl(Store store, Label label) throws UnreachableNodeException {
        super(store, label);
      }

      @Override
      public native boolean delegatesTo(Principal p);

      @Override
      public native boolean equals(Principal p);

      @Override
      public native ActsForProof findProofDownto(Store store, Principal q,
          java.lang.Object searchState);

      @Override
      public native ActsForProof findProofUpto(Store store, Principal p,
          java.lang.Object searchState);

      @Override
      public native boolean isAuthorized(java.lang.Object authPrf,
          Closure closure, Label lb, boolean executeNow);

      @Override
      public native String name();
    }
  }
}
