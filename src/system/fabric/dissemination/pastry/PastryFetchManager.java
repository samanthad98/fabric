package fabric.dissemination.pastry;

import java.io.IOException;

import fabric.client.RemoteCore;
import fabric.client.UnreachableCoreException;
import fabric.common.FetchException;
import fabric.common.InternalError;
import fabric.dissemination.FetchManager;
import fabric.dissemination.Glob;

/**
 * A PastryFetchManager performs object fetching by consulting a pastry
 * dissemination network to see if the object is available there. When an
 * instance of PastryFetchManager is created, it starts a pastry node. That
 * node will attempt to join a pastry network by contacting a bootstrap node.
 * This is set in the pastry configuration file (by default etc/pastry.params).
 */
public class PastryFetchManager implements FetchManager {
  
  private Node node;
  
  public PastryFetchManager() {
    try {
      node = new Node();  // start a new pastry node
    } catch (IOException e) {
      throw new InternalError(e);
    }
  }

  public Glob fetch(RemoteCore c, long onum) throws FetchException {
    try {
      return node.disseminator().fetch(c, onum);
    } catch (UnreachableCoreException e) {
      return c.readObjectFromCore(onum);
    }
  }
  
  public void destroy() {
    node.destroy();
  }

}
