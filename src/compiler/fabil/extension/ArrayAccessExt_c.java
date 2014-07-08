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
package fabil.extension;

import polyglot.ast.ArrayAccess;
import polyglot.ast.Expr;
import polyglot.types.Type;
import fabil.types.FabILTypeSystem;
import fabil.visit.ProxyRewriter;

public class ArrayAccessExt_c extends ExprExt_c {

  @Override
  public Expr rewriteProxiesImpl(ProxyRewriter pr) {
    ArrayAccess aa = node();

    // Only rewrite Fabric arrays.
    FabILTypeSystem ts = pr.typeSystem();
    Expr array = aa.array();
    if (!ts.isFabricType(array.type())) return aa;

    Expr result = pr.qq().parseExpr("%E.get(%E)", array, aa.index());

    // Insert a cast if we have a pure Fabric base type.
    Type base = array.type().toArray().ultimateBase();
    if (ts.isFabricType(base) && !ts.isJavaInlineable(base)) {
      Type castType = aa.type();
      if (castType.isArray())
        castType = ts.fabricRuntimeArrayOf(castType.toArray().base());
      result = pr.qq().parseExpr("(%T) %E", castType, result);
    }

    return result;
  }

  @Override
  public ArrayAccess node() {
    return (ArrayAccess) super.node();
  }

}
