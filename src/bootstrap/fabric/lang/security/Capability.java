package fabric.lang.security;

public interface Capability extends fabric.lang.Object {
    
    public fabric.lang.security.Principal get$jif$jif_lang_Capability_P();
    
    public fabric.lang.security.Label get$jif$jif_lang_Capability_L();
    
    public fabric.lang.security.Closure get$closure();
    
    public fabric.lang.security.Closure getClosure();
    
    public java.lang.Object invoke();
    
    public static class _Proxy extends fabric.lang.Object._Proxy
      implements fabric.lang.security.Capability
    {
        
        native public fabric.lang.security.Principal
          get$jif$jif_lang_Capability_P();
        
        native public fabric.lang.security.Label get$jif$jif_lang_Capability_L(
          );
        
        native public fabric.lang.security.Closure get$closure();
        
        native public fabric.lang.security.Closure getClosure();
        
        native public java.lang.Object invoke();
        
        native public static boolean jif$Instanceof(
          fabric.lang.security.Principal arg1, fabric.lang.security.Label arg2,
          fabric.lang.Object arg3);
        
        native public static fabric.lang.security.Capability
          jif$cast$jif_lang_Capability(fabric.lang.security.Principal arg1,
                                       fabric.lang.security.Label arg2,
                                       fabric.lang.Object arg3);
        
        public _Proxy(Capability._Impl impl) { super(impl); }
        
        public _Proxy(fabric.worker.Store store, long onum) {
            super(store, onum);
        }
    }
    
    final public static class _Impl extends fabric.lang.Object._Impl
      implements fabric.lang.security.Capability
    {
        
        native public fabric.lang.security.Principal
          get$jif$jif_lang_Capability_P();
        
        native public fabric.lang.security.Label get$jif$jif_lang_Capability_L(
          );
        
        native public fabric.lang.security.Closure get$closure();
        
        native public fabric.lang.security.Closure getClosure();
        
        native public java.lang.Object invoke();
        
        _Impl(fabric.worker.Store $location, fabric.lang.security.Label $label,
              final fabric.lang.security.Principal jif$P,
              final fabric.lang.security.Label jif$L,
              final fabric.lang.security.Closure closure) {
            super($location, $label);
        }
        
        native public static boolean jif$Instanceof(
          final fabric.lang.security.Principal jif$P,
          final fabric.lang.security.Label jif$L, final fabric.lang.Object o);
        
        native public static fabric.lang.security.Capability
          jif$cast$jif_lang_Capability(
          final fabric.lang.security.Principal jif$P,
          final fabric.lang.security.Label jif$L, final fabric.lang.Object o);
        
        native protected fabric.lang.Object._Proxy $makeProxy();
        
        native public void $serialize(java.io.ObjectOutput out,
                                      java.util.List refTypes,
                                      java.util.List intraStoreRefs,
                                      java.util.List interStoreRefs)
              throws java.io.IOException;
        
        public _Impl(fabric.worker.Store store, long onum, int version,
                     long expiry, long label, java.io.ObjectInput in,
                     java.util.Iterator refTypes,
                     java.util.Iterator intraStoreRefs)
              throws java.io.IOException,
            java.lang.ClassNotFoundException {
            super(store, onum, version, expiry, label, in, refTypes,
                  intraStoreRefs);
        }
        
        native public void $copyAppStateFrom(fabric.lang.Object._Impl other);
    }
    
    interface _Static extends fabric.lang.Object, Cloneable {
        
        public fabric.worker.Store get$localStore();
        
        final class _Proxy extends fabric.lang.Object._Proxy
          implements fabric.lang.security.Capability._Static
        {
            
            native public fabric.worker.Store get$localStore();
            
            public _Proxy(fabric.lang.security.Capability._Static._Impl impl) {
                super(impl);
            }
            
            public _Proxy(fabric.worker.Store store, long onum) {
                super(store, onum);
            }
        }
        
        class _Impl extends fabric.lang.Object._Impl
          implements fabric.lang.security.Capability._Static
        {
            
            native public fabric.worker.Store get$localStore();
            
            public _Impl(fabric.worker.Store store,
                         fabric.lang.security.Label label)
                  throws fabric.net.UnreachableNodeException {
                super(store, label);
            }
            
            native protected fabric.lang.Object._Proxy $makeProxy();
            
            native private void $init();
        }
        
    }
    
}
