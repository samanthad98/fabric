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
package fabric.store;

import static fabric.common.Logging.STORE_LOGGER;

import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;

import fabric.common.Version;
import fabric.common.exceptions.TerminationException;
import fabric.common.exceptions.UsageError;

// TODO: load resources

public class Main {
  public static void main(String[] args) {
    try {
      start(args);
    } catch (TerminationException te) {
      if (te.getMessage() != null)
        (te.exitCode == 0 ? System.out : System.err).println(te.getMessage());

      System.exit(te.exitCode);
    }
  }

  public static void start(String args[]) {
    STORE_LOGGER.info("Store node");
    STORE_LOGGER.log(Level.CONFIG, "Fabric version {0}", new Version());
    STORE_LOGGER.info("");

    // Parse the command-line options.
    Options opts;
    try {
      opts = new Options(args);
    } catch (UsageError ue) {
      PrintStream out = ue.exitCode == 0 ? System.out : System.err;
      if (ue.getMessage() != null && ue.getMessage().length() > 0) {
        out.println(ue.getMessage());
        out.println();
      }

      Options.usage(out);
      throw new TerminationException(ue.exitCode);
    }
    
    // Start up store-node services.
    try {
      Node node = new Node(opts);
      node.start();
    } catch (IOException e) {
      throw new TerminationException(e.getMessage(), 1);
    }

  }
}
