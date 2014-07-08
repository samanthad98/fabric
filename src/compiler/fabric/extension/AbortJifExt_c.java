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
package fabric.extension;

import fabric.ast.AbortStmt;
import fabric.ast.FabricBranch;
import fabric.types.FabricTypeSystem;
import polyglot.ast.Node;
import polyglot.types.SemanticException;
import polyglot.util.InternalCompilerError;
import jif.extension.JifBranchExt;
import jif.translate.ToJavaExt;
import jif.types.*;
import jif.types.label.Label;
import jif.visit.LabelChecker;

public class AbortJifExt_c extends JifBranchExt {
  public AbortJifExt_c(ToJavaExt toJava) {
    super(toJava);
  }
  
  @Override
  public Node labelCheckStmt(LabelChecker lc) throws SemanticException {
    AbortStmt abort = (AbortStmt)node();
    
    FabricTypeSystem ts = (FabricTypeSystem)lc.typeSystem();
    JifContext A = lc.jifContext();
    A = (JifContext)abort.del().enterScope(A);
    
    Label pc = A.pc();
    Label gotoLabel = A.gotoLabel(FabricBranch.ABORT, null);
    
    if (gotoLabel == null) {
      throw new InternalCompilerError("Can't find target for abort.", abort.position());
    }
    
    lc.constrain(new NamedLabel("abort_pc", 
                                "the information that may be revealed " +
                                "by control reaching abort",
                                pc),
                 LabelConstraint.LEQ,
                 new NamedLabel("atomic_pc",
                                "upper bound on information that should be revealed " +
                                "by control reaching inside the atomic block",
                                gotoLabel), 
                 A.labelEnv(), abort.position(),
                 new ConstraintMessage() {
      @Override
      public String msg() {
        return "The information revealed by aborting an atomic block " +
               "may be more restrictive than the information that " +
               "should be revealed by reaching the entry of the atomic block.";
      }

      @Override
      public String detailMsg() {
        return "The program counter label at entry of atomic block " +
               "is at least as restrictive as the program counter label at " +
               "the abort statement.";
      }
      
      @Override
      public String technicalMsg() {
        return "_pc_(abort) <= _pc_(S) in atomic { S }";
      }
    });
    
    PathMap X = ts.pathMap();
    // prevent the single path rule from being used.
    X = X.set(ts.gotoPath(abort.kind(), abort.label()), ts.topLabel());

    return updatePathMap(abort, X);
  }
}
