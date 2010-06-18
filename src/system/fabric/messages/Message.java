package fabric.messages;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import fabric.common.exceptions.FabricException;
import fabric.common.exceptions.InternalError;
import fabric.common.net.SubSocket;
import fabric.lang.Object._Proxy;
import fabric.worker.Store;
import fabric.worker.Worker;

/**
 * Messages provide an interface for serializing requests and responses.  The
 * <code>Message</code> class itself provides facilities for serialization and
 * deserialization, while the concrete subclasses give the structure of each
 * type of message.</p>
 * 
 * <p>Messages are intended to be used in a synchronous, call-return style.  To
 * support this, each Message type is bound to a specific Response type.  On the
 * sender side, this allows type safety in the <code>send</code> method, for
 * example:<br>
 * <pre>
 * ReadMessage.Response r = new ReadMessage(...).send(...);
 * </pre>
 * while on the receiver side, type safety is enforced by only accepting
 * <code>R</code> in the <code>respond(...)</code> method.</p>
 * 
 * <p>Messages use two instances of the visitor pattern, one for Messages bound
 * for the store ({@link MessageToStore}) and one for Messages bound for the
 * worker ({@link MessageToWorker}).  These interfaces would be subclasses of
 * <code>Message</code>, except that some message types (e.g.
 * <code>CommitTransactionMessage</code>) go to both, and Java doesn't support
 * multiple inheritance.</p>
 * 
 * @param <R> The response type
 * @author mdgeorge
 */
public abstract class Message<R extends Message.Response, E extends Exception> {

  //////////////////////////////////////////////////////////////////////////////
  // public API                                                               //
  //////////////////////////////////////////////////////////////////////////////
  
  /** Marker interface for Message responses. */
  public static interface Response {
  }

  /**
   * Sends this message to the given node and awaits a response.
   * 
   * @param message
   *            the message to send.
   * @return
   *            the reply from the node.
   * @throws FabricException
   *            if an error occurs at the remote node while handling the message.
   * @throws IOException
   *            in the event of a communications failure. 
   */
  public final R send(SubSocket s) throws IOException, E {
    DataInputStream  in  = new DataInputStream(s.getInputStream());
    DataOutputStream out = new DataOutputStream(s.getOutputStream());
    
    // Write this message out.
    out.writeByte(messageType.ordinal());
    writeMessage(out);
    out.flush();

    // Read in the reply. Determine if an error occurred.
    if (in.readBoolean()) {
      // We have an error.
      E exc = readObject(in, this.exceptionClass);
      exc.fillInStackTrace();
      throw exc;
    }

    // Read the response.
    return readResponse(in);
  }

  /**
   * Read a Message from the given <code>DataInput</code>
   * 
   * @throws IOException
   *           If a malformed message is sent, or in the case of a failure in
   *           the <code>DataInput</code> provided.
   */
  public static Message<?,?> receive(DataInput in) throws IOException {
    try {
      MessageType messageType = MessageType.values()[in.readByte()];
      
      return messageType.parse(in);
    } catch (final ArrayIndexOutOfBoundsException e) {
      throw new IOException("Unrecognized message");
    }
  }
  
  /**
   * Send a successful response this message.
   *
   * @param out
   *            the channel on which to send the response
   * @param r
   *            the response to send.
   * @throws IOException
   *            if the provided <code>DataOutput</code> fails.
   */
  public void respond(DataOutput out, R r) throws IOException {
    // Signal that no error occurred.
    out.writeBoolean(false);

    // Write out the response.
    writeResponse(out, r);
  }
  
  /**
   * Send a response to this message that indicates an exception.
   * 
   * @param out
   *            the channel on which to send the response
   * @param e
   *            the exception to send
   * @throws IOException
   *            if the provided <code>DataOutput</code> fails.
   */
  public void respond(DataOutput out, Exception e) throws IOException {
    // Clear out the stack trace before sending an exception out.
    e.setStackTrace(new StackTraceElement[0]);
    
    // Signal that an error occurred and write out the exception.
    out.writeBoolean(true);
    
    // write out the exception
    writeObject(out, e);
  }

  //////////////////////////////////////////////////////////////////////////////
  // API for concrete message implementations                                 //
  //////////////////////////////////////////////////////////////////////////////
  
  /**
   * This enum gives a mapping between message types and ordinals. This is used
   * for efficient encoding and decoding of the type of a message.
   */
  @SuppressWarnings("all")
  protected static enum MessageType {
    ALLOCATE_ONUMS      {Message parse(DataInput in) throws IOException { return new AllocateMessage           (in); }},
    READ_ONUM           {Message parse(DataInput in) throws IOException { return new ReadMessage               (in); }},
    PREPARE_TRANSACTION {Message parse(DataInput in) throws IOException { return new PrepareTransactionMessage (in); }},
    COMMIT_TRANSACTION  {Message parse(DataInput in) throws IOException { return new CommitTransactionMessage  (in); }},
    ABORT_TRANSACTION   {Message parse(DataInput in) throws IOException { return new AbortTransactionMessage   (in); }},
    DISSEM_READ_ONUM    {Message parse(DataInput in) throws IOException { return new DissemReadMessage         (in); }},
    REMOTE_CALL         {Message parse(DataInput in) throws IOException { return new RemoteCallMessage         (in); }},
    DIRTY_READ          {Message parse(DataInput in) throws IOException { return new DirtyReadMessage          (in); }},
    TAKE_OWNERSHIP      {Message parse(DataInput in) throws IOException { return new TakeOwnershipMessage      (in); }},
    GET_PRINCIPAL       {Message parse(DataInput in) throws IOException { return new GetPrincipalMessage       (in); }},
    OBJECT_UPDATE       {Message parse(DataInput in) throws IOException { return new ObjectUpdateMessage       (in); }},
    GET_CERT_CHAIN      {Message parse(DataInput in) throws IOException { return new GetCertChainMessage       (in); }},
    ;

    /** Read a message of the appropriate type from the given DataInput. */
    abstract Message<?,?> parse(DataInput in) throws IOException;
  }

  /** The <code>MessageType</code> corresponding to this class. */
  protected final MessageType messageType;
  
  /** The class of Exceptions that may be thrown in response to this Message */
  protected final Class<E> exceptionClass; 

  /** Constructs a message of the given <code>MessageType</code> 
   * @param exceptionClass TODO
   * @param exceptionClass TODO*/
  protected Message(MessageType messageType, Class<E> exceptionClass) {
    this.messageType    = messageType;
    this.exceptionClass = exceptionClass;
  }

  /**
   * Serializes a fabric object reference.
   */
  protected void writeRef(_Proxy ref, DataOutput out) throws IOException {
    out.writeUTF(ref.$getStore().name());
    out.writeLong(ref.$getOnum());
  }

  /**
   * Deserializes a fabric object reference.
   * 
   * @param type
   *          The type of the reference being read. This must be the interface
   *          corresponding to the Fabric type, and not the _Proxy or _Impl
   *          classes.
   */
  @SuppressWarnings("unchecked")
  protected static _Proxy readRef(Class<?> type, DataInput in) throws IOException {
    Store store = Worker.getWorker().getStore(in.readUTF());
    Class<? extends _Proxy> proxyType = null;
    for (Class<?> c : type.getClasses()) {
      if (c.getSimpleName().equals("_Proxy")) {
        proxyType = (Class<? extends _Proxy>) c;
        break;
      }
    }
  
    if (proxyType == null)
      throw new InternalError("Unable to find proxy class for " + type);
  
    try {
      Constructor<? extends _Proxy> constructor =
          proxyType.getConstructor(Store.class, long.class);
  
      return constructor.newInstance(store, in.readLong());
    } catch (SecurityException e) {
      throw new InternalError(e);
    } catch (NoSuchMethodException e) {
      throw new InternalError(e);
    } catch (IllegalArgumentException e) {
      throw new InternalError(e);
    } catch (InstantiationException e) {
      throw new InternalError(e);
    } catch (IllegalAccessException e) {
      throw new InternalError(e);
    } catch (InvocationTargetException e) {
      throw new InternalError(e);
    }
  }

  /**
   * Serialize a java object to a DataOutput
   */
  protected void writeObject(DataOutput out, Object o) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream    oos  = new ObjectOutputStream(baos);
    oos.writeObject(o);
    oos.flush();
    baos.flush();

    byte[] buf = baos.toByteArray();
    out.write(buf);
  }

  /**
   * Deserialize a java object from a DataOutput
   */
  protected <T> T readObject(DataInput in, Class<T> type) throws IOException {
    byte[] buf = new byte[in.readInt()];
    in.readFully(buf);

    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(buf));
    
    try {
      Object o = ois.readObject();
      return type.cast(o);
    } catch (ClassCastException e) {
      throw new IOException("Unable to deserialize java object -- wrong type");
    } catch (ClassNotFoundException e) {
      throw new IOException("Unable to deserialize java object -- no such class");
    }
  }
  
  //////////////////////////////////////////////////////////////////////////////
  // abstract serialization methods                                           //
  //////////////////////////////////////////////////////////////////////////////
  
  /**
   * Writes this message out on the given output stream.
   * @throws IOException
   *           if the <code>DataOutput</code> fails.
   */
  protected abstract void writeMessage(DataOutput out) throws IOException;

  /**
   * Each subclass should have a constructor of the form:
   * 
   * protected Message(DataInput in) throws IOException
   * 
   * that constructs a message of the given type, reading the data from the
   * provided <code>DataInput</code>.
   * 
   * @throws IOException
   *            if the message is malformed, or if the <code>DataInput</code>
   *            fails.
   */
  /* readMessage */
  // protected Message(DataInput in) throws IOException
  
  /**
   * Creates a Response message of the appropriate type using the provided
   * <code>DataOutput</code>
   * 
   * @throws IOException
   *            if the response is malformed, or if the <code>DataInput</code>
   *            fails.
   */
  protected abstract R readResponse(DataInput in) throws IOException;

  /**
   * Writes a Response message of the appropriate type using the provided
   * <code>DataOutput</code>.
   * 
   * @throws IOException
   *            if the <code>DataOutput</code> fails.
   */
  protected abstract void writeResponse(DataOutput out, R response) throws IOException;
}
