package fabric.util;

public interface LinkedHashSet extends fabric.util.Set, fabric.util.HashSet {
    
    public fabric.util.LinkedHashSet fabric$util$LinkedHashSet$();
    
    public fabric.util.LinkedHashSet fabric$util$LinkedHashSet$(
      int initialCapacity);
    
    public fabric.util.LinkedHashSet fabric$util$LinkedHashSet$(
      int initialCapacity, float loadFactor);
    
    public fabric.util.LinkedHashSet fabric$util$LinkedHashSet$(
      fabric.util.Collection c);
    
    public fabric.util.HashMap init(int capacity, float load);
    
    public fabric.lang.Object $initLabels();
    
    public static class _Proxy extends fabric.util.HashSet._Proxy
      implements fabric.util.LinkedHashSet
    {
        
        native public fabric.util.LinkedHashSet fabric$util$LinkedHashSet$();
        
        native public fabric.util.LinkedHashSet fabric$util$LinkedHashSet$(
          int arg1);
        
        native public fabric.util.LinkedHashSet fabric$util$LinkedHashSet$(
          int arg1, float arg2);
        
        native public fabric.util.LinkedHashSet fabric$util$LinkedHashSet$(
          fabric.util.Collection arg1);
        
        native public boolean addAll(fabric.util.Collection arg1);
        
        native public boolean containsAll(fabric.util.Collection arg1);
        
        native public boolean equals(fabric.lang.Object arg1);
        
        native public int hashCode();
        
        native public fabric.util.Iterator iterator();
        
        native public boolean removeAll(fabric.util.Collection arg1);
        
        native public boolean retainAll(fabric.util.Collection arg1);
        
        native public fabric.lang.arrays.ObjectArray toArray();
        
        native public fabric.lang.arrays.ObjectArray toArray(
          fabric.lang.arrays.ObjectArray arg1);
        
        public _Proxy(LinkedHashSet._Impl impl) { super(impl); }
        
        public _Proxy(fabric.worker.Store store, long onum) {
            super(store, onum);
        }
    }
    
    public static class _Impl extends fabric.util.HashSet._Impl
      implements fabric.util.LinkedHashSet
    {
        
        native public fabric.util.LinkedHashSet fabric$util$LinkedHashSet$();
        
        native public fabric.util.LinkedHashSet fabric$util$LinkedHashSet$(
          int initialCapacity);
        
        native public fabric.util.LinkedHashSet fabric$util$LinkedHashSet$(
          int initialCapacity, float loadFactor);
        
        native public fabric.util.LinkedHashSet fabric$util$LinkedHashSet$(
          fabric.util.Collection c);
        
        native public fabric.util.HashMap init(int capacity, float load);
        
        native public fabric.lang.Object $initLabels();
        
        public _Impl(fabric.worker.Store $location) { super($location); }
        
        native protected fabric.lang.Object._Proxy $makeProxy();
        
        native public void $serialize(java.io.ObjectOutput out,
                                      java.util.List refTypes,
                                      java.util.List intraStoreRefs,
                                      java.util.List interStoreRefs)
              throws java.io.IOException;
        
        public _Impl(fabric.worker.Store store, long onum, int version,
                     long expiry, long label, long accessLabel,
                     java.io.ObjectInput in, java.util.Iterator refTypes,
                     java.util.Iterator intraStoreRefs)
              throws java.io.IOException,
            java.lang.ClassNotFoundException {
            super(store, onum, version, expiry, label, accessLabel, in,
                  refTypes, intraStoreRefs);
        }
    }
    
    interface _Static extends fabric.lang.Object, Cloneable {
        
        public long get$serialVersionUID();
        
        final class _Proxy extends fabric.lang.Object._Proxy
          implements fabric.util.LinkedHashSet._Static
        {
            
            native public long get$serialVersionUID();
            
            public _Proxy(fabric.util.LinkedHashSet._Static._Impl impl) {
                super(impl);
            }
            
            public _Proxy(fabric.worker.Store store, long onum) {
                super(store, onum);
            }
        }
        
        class _Impl extends fabric.lang.Object._Impl
          implements fabric.util.LinkedHashSet._Static
        {
            
            native public long get$serialVersionUID();
            
            public _Impl(fabric.worker.Store store)
                  throws fabric.net.UnreachableNodeException {
                super(store);
            }
            
            native protected fabric.lang.Object._Proxy $makeProxy();
            
            native private void $init();
        }
        
    }
    
}
