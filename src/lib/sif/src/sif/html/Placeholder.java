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
package sif.html;

import sif.servlet.HTMLWriter;
import fabric.lang.security.Label;

public class Placeholder extends Node {	
    
    public Placeholder(Label L, Label E) {
        super(L, E);
    }
    
    void writeImpl(HTMLWriter p) {
        if (p instanceof HTMLPrecomputerWriter) {
            ((HTMLPrecomputerWriter)p).addPlaceholderNode(p);
        }
    }
    
    public static boolean jif$Instanceof(Label l, Label e, Object o) {
	return (o instanceof Placeholder) && Node.jif$Instanceof(l, e, o);
    }

    public static Placeholder jif$cast$sif_html_Placeholder(Label l, Label e, Object o) {
        if (o == null) return null; 
	if (jif$Instanceof(l, e, o))
	    return (Placeholder)o;
	throw new ClassCastException();
    }
}
