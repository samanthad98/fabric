package fabric.core;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.net.ssl.*;


import fabric.common.InternalError;
import fabric.common.Resources;

// TODO implement dissemination nodes
public class Node {
  public Options opts;

  /**
   * A map from core IDs to corresponding TransactionManagers.
   */
  protected Map<Long, TransactionManager> cores;

  /** The number of additional connections the node can handle. */
  private int availConns;

  /** The thread pool. */
  private Stack<Worker> pool;

  public Node(Options opts) {
    this.opts = opts;
    this.cores = new HashMap<Long, TransactionManager>();

    for (long coreid : opts.coreIDs) {
      try {
        ObjectStore core = loadCore(coreid);
        // XXX using an empty memory store for each core
        addCore(coreid, new TransactionManager(core));
      } catch (DuplicateCoreException e) {
        // Should never happen.
      }
    }
  }

  private ObjectStore loadCore(long coreid) {
    // see if a file exists containing the core
    try {
      InputStream in = Resources.readFile("var", Long.toString(coreid));
      ObjectStore os = new MemoryStore(in);
      return os;
    } catch (Exception exc) {
      // continue
      throw new InternalError("could not initialize core", exc);
    }
  }

  /**
   * Adds a new Core to this node.
   * 
   * @param coreID
   *          the 48-bit ID for the core being added.
   * @param tm
   *          a <code>TransactionManager</code> to use for the core being added.
   */
  public void addCore(long coreID, TransactionManager tm)
      throws DuplicateCoreException {
    if (cores.containsKey(coreID)) throw new DuplicateCoreException();
    cores.put(coreID, tm);
  }

  /**
   * Given a 48-bit ID for an object core, returns its corresponding
   * <code>TransactionManager</code>.
   * 
   * @return null if there is no corresponding binding.
   */
  public TransactionManager getTransactionManager(long coreID) {
    return cores.get(coreID);
  }

  /**
   * The main execution body of a core node.
   */
  public void start() throws IOException {
    SSLServerSocket server;
    try {
      SSLContext sslContext = SSLContext.getInstance("TLS");
      KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
      kmf.init(opts.keyStore, opts.passwd);
      TrustManager[] tm = null;
      if (opts.trustStore != null) {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX");
        tmf.init(opts.trustStore);
        tm = tmf.getTrustManagers();
      }
      sslContext.init(kmf.getKeyManagers(), tm, null);
      SSLServerSocketFactory ssf = sslContext.getServerSocketFactory();
      server = (SSLServerSocket) ssf.createServerSocket(opts.port);
      server.setNeedClientAuth(true);
    } catch (KeyManagementException e) {
      throw new InternalError("Unable to initialise key manager factory.", e);
    } catch (UnrecoverableKeyException e1) {
      throw new InternalError("Unable to open key store.", e1);
    } catch (NoSuchAlgorithmException e1) {
      throw new InternalError(e1);
    } catch (KeyStoreException e1) {
      throw new InternalError("Unable to initialise key manager factory.", e1);
    }
    availConns = opts.maxConnect;

    // Set up the thread pool.
    pool = new Stack<Worker>();
    for (int i = 0; i < opts.threadPool; i++) {
      pool.push(new Worker(this));
    }

    // The main server loop.
    while (true) {
      // If we can't handle any more connections, block until an existing
      // connection is done.
      synchronized (this) {
        while (availConns == 0) {
          try {
            this.wait();
          } catch (InterruptedException e) {
            continue;
          }
        }
      }

      // Accept a connection and give it to a worker thread.
      SSLSocket client = (SSLSocket) server.accept();
      Worker worker = getWorker();
      try {
        // XXX not setting timeout
        // client.setSoTimeout(opts.timeout * 1000);
        client.startHandshake();
        worker.handle(client);
      } catch (IOException e) {
        // Something messed up with the client socket. Abort handling the
        // client.
        e.printStackTrace();
        workerDone(worker);
      }
    }
  }

  /**
   * Returns an available <code>Worker</code> object.
   */
  private synchronized Worker getWorker() {
    availConns--;

    if (pool.isEmpty()) return new Worker(this);
    return pool.pop();
  }

  /**
   * This is invoked by a <code>Worker</code> to notify this node that it is
   * finished serving a client.
   */
  protected synchronized void workerDone(Worker worker) {
    availConns++;

    // Clean up the worker and add it to the thread pool if there's room.
    if (pool.size() == opts.threadPool) return;
    worker.cleanup();
    pool.push(worker);
  }
}
