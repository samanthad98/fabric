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
package fabric.ast;

import java.util.Collections;
import java.util.List;

import polyglot.ast.Branch_c;
import polyglot.ast.Term;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;

public class RetryStmt_c extends Branch_c implements RetryStmt {
  public RetryStmt_c(Position pos) {
    // XXX assume abort statements do not have labels for now
    super(pos, FabricBranch.RETRY, null);
  }

  @Override
  public <T> List<T> acceptCFG(CFGBuilder<?> v, List<T> succs) {
    return Collections.EMPTY_LIST;
  }

  @Override
  public Term firstChild() {
    return null;
  }
}
