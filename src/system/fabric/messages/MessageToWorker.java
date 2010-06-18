package fabric.messages;

import fabric.common.exceptions.FabricException;
import fabric.lang.security.NodePrincipal;

/** Message destined for a Worker. */
public interface MessageToWorker {
  /** Visitor method 
   * @param p TODO*/
  public Message.Response dispatch(MessageToWorkerHandler handler, NodePrincipal p) throws FabricException;
}
