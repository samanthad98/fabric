/**
 * Copyright (C) 2010-2012 Fabric project group, Cornell University
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
package sif.servlet;

import sif.html.Node;

public class ActionNodePair {
    public final Action a;
    public final Node n;
    public ActionNodePair(Action a, Node n) {
        this.a = a;
        this.n = n;
    }
    public boolean equals(Object o) {
        if (o instanceof ActionNodePair) {
            ActionNodePair that = (ActionNodePair)o;
            return that.a == a && that.n == n;
        }
        return false;
    }
    public int hashCode() {
        return (a == null?0:a.hashCode()) ^ (n == null?0:n.hashCode()); 
    }
}