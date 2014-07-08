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
package OO7.traversals;

import OO7.*;

/**
 * Traversal T2: Traversal with updates Repeat Traversal T1, but update objects
 * during the traversal. There are three types of update patterns in this
 * traversal. In each, a single update to an atomic part consists of swapping
 * its (x,y) attributes. The three types of updates are: A Update one atomic
 * part per composite part.
 */
public class T2a extends PrivatePartTraversal {
  private int result = 0;

  public static void main(String[] args) {
    new T2a().mainImpl(args);
  }

  public void visitAtomicPart(AtomicPart current) {
    long x = current.x();
    current.setX(current.y());
    current.setY(x);
  }

  public void visitCompositePart(CompositePart p) {
    p.rootPart().accept(this);
  }
}

/*
 * * vim: ts=2 sw=2 cindent cino=\:0 et syntax=java
 */
