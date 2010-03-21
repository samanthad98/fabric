package jif.lang;

import fabric.worker.Store;
import fabric.lang.SecretKeyObject;
import fabric.lang.Object;
import fabric.util.Set;

public interface Label extends Object {
  ConfPolicy confPolicy();

  IntegPolicy integPolicy();
  
  SecretKeyObject keyObject();
  
  public class _Proxy extends Object._Proxy implements Label {
    public _Proxy(Store store, long onum) {
      super(store, onum);
    }

    public native boolean relabelsTo(Label l, Set s);

    public native Label join(Label l);

    public native Label meet(Label l);

    public native ConfPolicy confPolicy();

    public native IntegPolicy integPolicy();
    
    public native SecretKeyObject keyObject();
  }
}
