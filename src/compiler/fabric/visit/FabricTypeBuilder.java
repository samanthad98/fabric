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
package fabric.visit;

import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.types.*;
import polyglot.util.Position;
import polyglot.visit.TypeBuilder;

public class FabricTypeBuilder extends TypeBuilder {
  public FabricTypeBuilder(Job job, TypeSystem ts, NodeFactory nf) {
    super(job, ts, nf);
  }

  @Override
  protected ParsedClassType newClass(Position pos, Flags flags, String name) 
  throws SemanticException {
    // see if an appropriately named class already exists in the resolver
    String fullName = name;
    if (currentClass() != null) {
        fullName = currentClass().fullName() + "." + name;
    }
    else if (currentPackage() != null) {
        fullName = currentPackage().fullName() + "." + name;
    }

    ParsedClassType pct = null;
    Named n = ts.systemResolver().check(fullName);
    if (n instanceof ParsedClassType) {
      pct = (ParsedClassType)n;
    }
    
    if (pct != null && job().source().equals(pct.fromSource())) {
      // If a type of the same name and from the same source has been loaded, 
      // there is no need to generate it again.
      // It won't miss real duplicate class declarations, because we only 
      // take this shortcut when the cached class type was from the same source.
      // Moreover, type-checking of SourceFile will take care of duplicate 
      // definitions in the same source.
      return pct;
    }
    
    return super.newClass(pos, flags, name);
  }
}
