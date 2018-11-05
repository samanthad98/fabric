package fabric.common.util;
/**
 * 
 */
public class BackoffWrapper {
  public enum BackoffCase {
    Palse, BOnon, BO;
    
    public boolean weakerThan(BackoffCase b) {
      switch(this) {
        case BO:
          return false;
        case BOnon:
          return (b == BO);
        case Palse:
          return true;
      }
    }
    
  }
}
