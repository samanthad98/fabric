package fabric.dissemination.pastry.messages;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;

import rice.p2p.commonapi.Endpoint;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.commonapi.rawserialization.InputBuffer;
import rice.p2p.commonapi.rawserialization.OutputBuffer;
import rice.p2p.commonapi.rawserialization.RawMessage;
import fabric.common.util.Pair;
import fabric.dissemination.ObjectGlob;
import fabric.dissemination.WarrantyRefreshGlob;
import fabric.worker.Worker;

/**
 * A Fetch message represents a request to fetch a particular object.
 */
public class Fetch implements RawMessage {

  private transient final NodeHandle sender;
  private final Id id;
  private final String store;
  private final long onum;
  private boolean refresh;

  private transient Reply reply;

  public Fetch(NodeHandle sender, Id id, String store, long onum) {
    this.sender = sender;
    this.id = id;
    this.store = store;
    this.onum = onum;
  }

  /** The sender of this message. */
  public NodeHandle sender() {
    return sender;
  }

  /** The random id of this message. */
  public Id id() {
    return id;
  }

  /** The store where the requested object resides. */
  public String store() {
    return store;
  }

  /** The object number of the requested object. */
  public long onum() {
    return onum;
  }

  /** The reply message. */
  public Reply reply() {
    return reply;
  }

  /** Sets the reply message. */
  public void reply(Reply reply) {
    this.reply = reply;
  }

  @Override
  public int getPriority() {
    return MEDIUM_PRIORITY;
  }

  @Override
  public String toString() {
    return store + "/" + onum;
  }

  @Override
  public short getType() {
    return MessageType.FETCH;
  }

  @Override
  public void serialize(OutputBuffer buf) throws IOException {
    buf.writeShort(id.getType());
    id.serialize(buf);
    buf.writeUTF(store);
    buf.writeLong(onum);
    buf.writeBoolean(refresh);
  }

  /**
   * Deserialization constructor.
   */
  public Fetch(InputBuffer buf, Endpoint endpoint, NodeHandle sender)
      throws IOException {
    this.sender = sender;
    id = endpoint.readId(buf, buf.readShort());
    store = buf.readUTF();
    onum = buf.readLong();
    refresh = buf.readBoolean();
  }

  /**
   * A reply to a Fetch message. Should carry the object requested by the
   * original fetch message.
   */
  public static class Reply implements RawMessage {

    private final Id id;
    private final String store;
    private final long onum;
    private final Pair<ObjectGlob, WarrantyRefreshGlob> globs;

    public Reply(Fetch parent, Pair<ObjectGlob, WarrantyRefreshGlob> globs) {
      id = parent.id();
      store = parent.store();
      onum = parent.onum();
      this.globs = globs;
    }

    /** The glob returned. */
    public Pair<ObjectGlob, WarrantyRefreshGlob> globs() {
      return globs;
    }

    /** The id of this message. */
    public Id id() {
      return id;
    }

    /** The store where the requested object resides. */
    public String store() {
      return store;
    }

    /** The object number of the requested object. */
    public long onum() {
      return onum;
    }

    @Override
    public int getPriority() {
      return MEDIUM_PRIORITY;
    }

    @Override
    public short getType() {
      return MessageType.FETCH_REPLY;
    }

    @Override
    public void serialize(OutputBuffer buf) throws IOException {
      DataOutputBuffer out = new DataOutputBuffer(buf);
      out.writeShort(id.getType());
      id.serialize(out);
      out.writeUTF(store);
      out.writeLong(onum);
      globs.first.write(out);
      if (globs.second == null) {
        out.writeBoolean(false);
      } else {
        out.writeBoolean(true);
        globs.second.write(out);
      }
    }

    /**
     * Deserialization constructor.
     */
    public Reply(InputBuffer buf, Endpoint endpoint) throws IOException {
      DataInputBuffer in = new DataInputBuffer(buf);
      id = endpoint.readId(in, in.readShort());
      store = in.readUTF();
      onum = in.readLong();

      PublicKey storePubKey = Worker.getWorker().getStore(store).getPublicKey();

      Pair<ObjectGlob, WarrantyRefreshGlob> globs;

      try {
        // Read in entire entry.
        ObjectGlob objectGlob = new ObjectGlob(in);
        WarrantyRefreshGlob warrantyRefreshGlob =
            in.readBoolean() ? new WarrantyRefreshGlob(in) : null;

        // Verify signatures.
        objectGlob.verifySignature(storePubKey);

        if (warrantyRefreshGlob != null) {
          try {
            warrantyRefreshGlob.verifySignature(storePubKey);
          } catch (GeneralSecurityException e) {
            // Warranty-refresh glob was corrupted, so ignore it.
            warrantyRefreshGlob = null;
          }
        }

        globs = new Pair<>(objectGlob, warrantyRefreshGlob);
      } catch (GeneralSecurityException e) {
        // Object glob was corrupted, so just use null for the whole result.
        globs = null;
      }

      this.globs = globs;
    }
  }

}
