package fabric.messages;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.security.Principal;
import java.util.List;

import fabric.client.Client;
import fabric.client.RemoteNode;
import fabric.client.UnreachableNodeException;
import fabric.client.remote.messages.GetPrincipalMessage;
import fabric.client.remote.messages.RemoteCallMessage;
import fabric.client.remote.messages.TakeOwnershipMessage;
import fabric.common.*;
import fabric.common.InternalError;
import fabric.core.Worker;

public abstract class Message<N extends RemoteNode, R extends Message.Response> {

  /**
   * The <code>MessageType</code> corresponding to this class.
   */
  protected final MessageType messageType;

  protected Message(MessageType messageType) {
    this.messageType = messageType;
  }

  /**
   * Sends this message to the given node.
   * 
   * @param message
   *          The message to send.
   * @return The reply from the node.
   * @throws FabricException
   *           if an error occurs at the remote node while handling the message.
   * @throws UnreachableNodeException
   *           if unable to connect to the node.
   */
  protected final R send(N node, boolean useSSL) throws FabricException,
      UnreachableNodeException {
    // XXX Won't always send to the same node host. Is this a problem?
    // XXX This is pretty ugly. Can it be cleaned up?
    // XXX Is this code in the right place?

    // FIXME? do we want to lock entire node?
    synchronized (node) {
      boolean needToConnect = !node.isConnected(useSSL);
      Client client = Client.getClient();
      final int retries = client.retries;

      int hostIdx = 0;

      // These will be filled in with real values if needed.
      List<InetSocketAddress> hosts = null;
      Principal nodePrincipal = null;
      int numHosts = 0;
      int startHostIdx = 0;

      for (int retry = 0; retries < 0 || retry < retries;) {
        try {
          if (needToConnect) {
            if (hosts == null) {
              Pair<List<InetSocketAddress>, Principal> entry =
                  client.nameService.lookup(node);
              hosts = entry.first;
              nodePrincipal = entry.second;

              numHosts = hosts.size();
              startHostIdx = Client.RAND.nextInt(numHosts);
            }

            // Attempt to establish a connection.
            int hostNum = (startHostIdx + hostIdx) % numHosts;
            node.connect(useSSL, hosts.get(hostNum), nodePrincipal);
          } else {
            // Set the flag for the next loop iteration in case we fail.
            needToConnect = true;
          }

          // Attempt to send our message and obtain a reply.
          return send(node, node.objectInputStream(useSSL), node
              .objectOutputStream(useSSL));
        } catch (NoSuchNodeError e) {
          // Connected to a system that doesn't host the node we're interested
          // in.
          // Increment loop counter variables.
          hostIdx++;
          if (hostIdx == numHosts) {
            hostIdx = 0;
            if (retries >= 0) retry++;
          }
          continue;
        } catch (IOException e) {
          // Retry.
          if (hosts == null) {
            // Attempt to reuse an existing connection failed. Just restart the
            // loop.
            continue;
          }

          // Increment loop counter variables.
          hostIdx++;
          if (hostIdx == numHosts) {
            hostIdx = 0;
            if (retries >= 0) retry++;
          }
          continue;
        }
      }

      throw new UnreachableNodeException(node);
    }
  }

  /**
   * Sends this message to a remote node. Used only by the client.
   * 
   * @param node
   *          the node to which the object is being sent.
   * @param in
   *          the input stream for sending objects to the node.
   * @param out
   *          the output stream on which to obtain response objects.
   * @return the response from the remote node.
   * @throws FabricException
   *           if an error occurred at the remote node while handling this
   *           request.
   * @throws IOException
   *           if an I/O error occurs during serialization/deserialization.
   */
  private R send(N node, ObjectInputStream in, ObjectOutputStream out)
      throws FabricException, IOException {
    // Write this message out.
    out.writeByte(messageType.ordinal());
    write(out);
    out.flush();

    // Read in the reply. Determine if an error occurred.
    if (in.readBoolean()) {
      try {
        // We have an error.
        FabricException exc = (FabricException) in.readObject();
        exc.fillInStackTrace();
        throw exc;
      } catch (ClassNotFoundException e) {
        throw new InternalError("Unexpected response from remote node", e);
      }
    }

    // Read the response.
    try {
      return response(node, in);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new InternalError(e);
    }
  }

  /**
   * This reads a <code>Message</code> from the provided input stream,
   * dispatches it to the given <code>Worker</code>, and writes the response to
   * the provided OutputStream. Used only by the core.
   * 
   * @param in
   *          The input stream to read the incoming message from.
   * @param out
   *          The output stream to write the result to.
   * @param handler
   *          The worker that will handle the message and generate the response
   * @throws IOException
   *           If a malformed message is sent, or in the case of a failure in
   *           the i/o streams provided.
   * @throws ClassNotFoundException
   */
  public static void receive(ObjectInput in, ObjectOutputStream out,
      MessageHandler handler) throws IOException {

    try {
      MessageType messageType = MessageType.values()[in.readByte()];
      Class<? extends Message<?, ?>> messageClass = messageType.messageClass;
      Message<?, ?> m;

      try {
        m =
            messageClass.getDeclaredConstructor(DataInput.class)
                .newInstance(in);
      } catch (InvocationTargetException e) {
        Throwable cause = e.getCause();
        if (cause instanceof IOException) throw (IOException) cause;
        throw new FabricException(cause);
      } catch (RuntimeException e) {
        throw e;
      } catch (Exception e) {
        throw new FabricException(e);
      }

      Response r = m.dispatch(handler);

      // Signal that no error occurred.
      out.writeBoolean(false);

      // Write out the response.
      r.write(out);
      out.reset();
      out.flush();
    } catch (final FabricException e) {
      // Clear out the stack trace before sending the exception to the client.
      e.setStackTrace(new StackTraceElement[0]);

      // Signal that an error occurred and write out the exception.
      out.writeBoolean(true);
      out.writeUnshared(e);
      out.flush();
    } catch (final FabricRuntimeException e) {
      // TODO: this is copied and pasted from above. We need to figure out what
      // exceptions _not_ to catch and then catch all the others.

      // Clear out the stack trace before sending the exception to the client.
      e.setStackTrace(new StackTraceElement[0]);

      // Signal that an error occurred and write out the exception.
      out.writeBoolean(true);
      out.writeUnshared(e);
      out.flush();
    }
  }

  private final R dispatch(MessageHandler handler) throws FabricException {
    if (handler instanceof fabric.client.remote.Worker) {
      return dispatch((fabric.client.remote.Worker) handler);
    }

    return dispatch((Worker) handler);
  }

  /**
   * Calls the appropriate <code>handle(...)</code> method on the worker.
   * 
   * @param handler
   * @return the result computed by the handler
   * @throws FabricException
   */
  public R dispatch(Worker handler) throws FabricException {
    throw new InternalError(
        "Invalid, unsupported, or unimplemented core message: " + getClass());
  }

  /**
   * Calls the appropriate <code>handle(...)</code> method on the worker.
   * 
   * @param handler
   * @return the result computed by the handler
   * @throws FabricException
   */
  public R dispatch(fabric.client.remote.Worker handler) throws FabricException {
    throw new InternalError(
        "Invalid, unsupported, or unimplemented client message: " + getClass());
  }

  /**
   * Creates a Response message of the appropriate type using the provided input
   * stream.
   * 
   * @param node
   *          the remote node from which the response originated.
   * @param in
   *          Input stream containing the message.
   * @return A Response message with the appropriate type.
   */
  public abstract R response(N node, DataInput in) throws IOException,
      FabricException;

  /**
   * Writes this message out on the given output stream. Only used by the
   * client.
   * 
   * @throws IOException
   *           if the output stream throws an IOException.
   */
  public abstract void write(DataOutput out) throws IOException;

  public static interface Response {
    void write(DataOutput out) throws IOException;
  }

  protected static enum MessageType {
    ALLOCATE_ONUMS(AllocateMessage.class), READ_ONUM(
        fabric.messages.ReadMessage.class), PREPARE_TRANSACTION(
        PrepareTransactionMessage.class), COMMIT_TRANSACTION(
        CommitTransactionMessage.class), ABORT_TRANSACTION(
        AbortTransactionMessage.class), DISSEM_READ_ONUM(
        DissemReadMessage.class), REMOTE_CALL(RemoteCallMessage.class), INTERCLIENT_READ(
        fabric.client.remote.messages.ReadMessage.class), TAKE_OWNERSHIP(
        TakeOwnershipMessage.class), GET_PRINCIPAL(GetPrincipalMessage.class);

    private final Class<? extends Message<?, ?>> messageClass;

    MessageType(Class<? extends Message<?, ?>> messageClass) {
      this.messageClass = messageClass;
    }
  }

}
