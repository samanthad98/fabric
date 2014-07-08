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
import sif.servlet.Request;
import fabric.lang.security.Label;
import fabric.lang.security.Principal;

public final class CheckBox extends InputNode {
    private final boolean checked;
    public CheckBox(Principal servletP, Label L, Label E, Input input, boolean checked_) {
        super(servletP, L, E, input);
        checked = checked_;
    }

    /* (non-Javadoc)
     * @see servlet.Node#write(servlet.HTMLWriter)
     */
    void writeImpl(HTMLWriter p) {
	p.print("<input type=checkbox");
	p.print(" name=");
	p.printq(input.getName());
	p.print(" value=yes");
	if (checked) p.print(" checked");
	p.print(" />");
    }
    
    /** 
     * @param r
     * @return Whether this checkbox is checked in client request r.
     */
    public boolean isChecked(Request r) {
	if (r.getParam(input) == null) return false;
	return r.getParam(input).equals("yes");
    }

    public static boolean jif$Instanceof(Principal P, Label l, Label e, Object o) {
        return ((o instanceof CheckBox) && InputNode.jif$Instanceof(P, l, e, o));
    }

    public static CheckBox jif$cast$sif_html_CheckBox(Principal P, Label l, Label e, Object o) {
        if (o == null) return null; 
        if (jif$Instanceof(P, l, e, o))
            return (CheckBox)o;
        throw new ClassCastException();
    }    
}
