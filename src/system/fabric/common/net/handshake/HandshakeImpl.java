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
package fabric.common.net.handshake;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import fabric.common.net.NotImplementedException;
import fabric.common.net.naming.SocketAddress;

/**
 * This class implements authenticated, anonymous, and unauthenticated connections.
 */
public class HandshakeImpl implements HandshakeProtocol {

  private enum ConnType {
    AUTHENTICATED,
    ANONYMOUS,
    UNAUTHENTICATED,
    ;
  }
  
  private ConnType type;
  
  public ShakenSocket initiate(String name, SocketAddress addr) throws IOException {
    Socket s = new Socket(addr.getAddress(), addr.getPort());
    fixSocket(s);
    
    DataOutputStream out = new DataOutputStream(s.getOutputStream());
    out.writeUTF(name);
    out.write(type.ordinal());
    out.flush();
    
    return new ShakenSocket(name, null, s);
  }

  public ShakenSocket receive(Socket s) throws IOException {
    fixSocket(s);
    
    DataInputStream in = new DataInputStream(s.getInputStream());
    String   name = in.readUTF();
    ConnType type = ConnType.values()[in.read()];
    
    System.out.println(name + " [" + name.length() + "]");
    return new ShakenSocket(name, null, s);
  }

  private void fixSocket(Socket s) throws IOException {
    s.setSoLinger(false, 0);
    s.setTcpNoDelay(true);
  }
}
