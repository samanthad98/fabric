package fabric.dissemination.pastry;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import rice.environment.Environment;
import rice.pastry.NodeIdFactory;
import rice.pastry.PastryNode;
import rice.pastry.socket.SocketPastryNodeFactory;
import rice.pastry.standard.RandomNodeIdFactory;

/**
 * Represents a pastry node with a dissemination process. Creating an instance
 * of this class will set up a pastry node on the local machine, attempt to
 * join a pastry ring, and start a disseminator application associated with the
 * pastry node created.
 */
public class Node {

  protected Environment env;
  protected SocketPastryNodeFactory pnf;
  protected PastryNode node;
  protected Disseminator disseminator;

  public Node() throws IOException {
    env = new Environment("etc/pastry");
    String b = env.getParameters().getString("bootstrap");
    String[] parts = b.split(":");
    String h = parts.length > 0 ? parts[0] : null;
    h = h.equals("localhost") ? InetAddress.getLocalHost().getHostName() : h;
    int p = parts.length == 2 ? Integer.parseInt(parts[1]) : 3373;
    InetSocketAddress boot = new InetSocketAddress(h, p);
    
    int port = findFreePort(3373);
    NodeIdFactory idf = new RandomNodeIdFactory(env);
    pnf = new SocketPastryNodeFactory(idf, port, env);
    
    node = pnf.newNode(pnf.getNodeHandle(boot));
    waitForReady();

    disseminator = new Disseminator(node);
  }
  
  /** Returns the disseminator application of this node. */
  public Disseminator disseminator() {
    return disseminator;
  }

  private void waitForReady() throws IOException {
    synchronized (node) {
      while (!node.isReady() && !node.joinFailed()) {
        try {
          node.wait(500);
        } catch (InterruptedException e) {}
      }

      if (node.joinFailed()) {
        throw new IOException("Network error: "
            + "Could not join Pastry network.");
      }
    }
  }

  private int findFreePort(int port) {
    while (true) {
      try {
        ServerSocket sock = new ServerSocket();
        sock.bind(new InetSocketAddress(InetAddress.getLocalHost(), port));
        sock.close();
        break;
      } catch (IOException e) {
        port++;
      }
    }
    
    return port;
  }
  
  /**
   * Shuts down and destroys this node.
   */
  public void destroy() {
    env.destroy();
  }

  // just for testing
  public static void main(String[] args) throws IOException {
    new Node().destroy();
  }

}
