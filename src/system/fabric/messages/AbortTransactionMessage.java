/**
 * Copyright (C) 2010 Fabric project group, Cornell University
 *
 * This file is part of Fabric.
 *
 * Fabric is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 2 of the License, or (at your option) any later
 * version.
 * 
 * Fabric is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 */
package fabric.messages;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import fabric.worker.debug.Timing;
import fabric.common.*;
import fabric.common.exceptions.AccessException;
import fabric.common.exceptions.FabricException;
import fabric.common.exceptions.InternalError;
import fabric.common.exceptions.ProtocolError;
import fabric.net.RemoteNode;

public class AbortTransactionMessage extends
    Message<RemoteNode, AbortTransactionMessage.Response> {

  public static class Response implements Message.Response {
    private Response() {
    }

    /**
     * Deserialization constructor, used by the worker.
     * 
     * @param node
     *          The node from which the response is being read.
     * @param in
     *          the input stream from which to read the response.
     */
    Response(RemoteNode node, DataInput in) {
    }

    /*
     * (non-Javadoc)
     * @see fabric.messages.Message.Response#write(java.io.ObjectOutputStream)
     */
    public void write(DataOutput out) {
    }
  }

  /**
   * The tid for the transaction that is aborting.
   */
  public final TransactionID tid;

  public AbortTransactionMessage(TransactionID tid) {
    super(MessageType.ABORT_TRANSACTION);
    this.tid = tid;
  }

  /**
   * Deserialization constructor.
   */
  protected AbortTransactionMessage(DataInput in) throws IOException {
    this(new TransactionID(in));
  }

  @Override
  public Response dispatch(fabric.store.MessageHandlerThread w) throws AccessException,
      ProtocolError {
    w.handle(this);
    return new Response();
  }

  @Override
  public Response dispatch(fabric.worker.remote.MessageHandlerThread handler)
      throws ProtocolError {
    handler.handle(this);
    return new Response();
  }

  public Response send(RemoteNode node) {
    try {
      Timing.STORE.begin();
      return send(node, true);
    } catch (FabricException e) {
      throw new InternalError(e);
    } finally {
      Timing.STORE.end();
    }
  }

  @Override
  public Response response(RemoteNode node, DataInput in) {
    return new Response(node, in);
  }

  @Override
  public void write(DataOutput out) throws IOException {
    tid.write(out);
  }

}
