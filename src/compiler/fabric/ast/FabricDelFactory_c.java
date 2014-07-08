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
package fabric.ast;

import fabric.extension.FabricCallDel;
import fabric.extension.FabricNewDel;
import fabric.extension.FabricNewLabelDel;
import jif.ast.JifDelFactory_c;
import polyglot.ast.JL;

/** Factory class for creating delegates for fabric types. */
public class FabricDelFactory_c extends JifDelFactory_c implements
    FabricDelFactory {

  //////////////////////////////////////////////////////////////////////////////
  // new ast methods                                                          //
  //////////////////////////////////////////////////////////////////////////////
  
  public final JL delAbortStmt() {
    JL e = delAbortStmtImpl();
    
    if (nextDelFactory() != null && nextDelFactory() instanceof FabricDelFactory) {
      JL e2 = ((FabricDelFactory) nextDelFactory()).delAbortStmt();
      e = composeDels(e,e2);
    }
    
    return postDelAbortStmt(e);
  }
  
  protected JL delAbortStmtImpl() {
    return delBranchImpl();
  }
  
  protected JL postDelAbortStmt(JL e) {
    return postDelBranch(e);
  }
  
  public final JL delAtomic() {
    JL e = delAtomicImpl();

    if (nextDelFactory() != null && nextDelFactory() instanceof FabricDelFactory) {
      JL e2 = ((FabricDelFactory)nextDelFactory()).delAtomic();
      e = composeDels(e, e2);
    }
    return postDelAtomic(e);
  }

  protected JL delAtomicImpl() {
    return delBlockImpl();
  }
  
  protected JL postDelAtomic(JL e) {
    return postDelBlock(e);
  }
  
  public final JL delAmbNewFabricArray() {
    JL e = delAmbNewFabricArrayImpl();
    
    if (nextDelFactory() != null && nextDelFactory() instanceof FabricDelFactory) {
      JL e2 = ((FabricDelFactory)nextDelFactory()).delAmbNewFabricArray();
      e = composeDels(e,e2);
    }
    
    return postDelAmbNewFabricArray(e);
  }

  
  protected JL delAmbNewFabricArrayImpl() {
    return delAmbNewArrayImpl();
  }
  
  protected JL postDelAmbNewFabricArray(JL e) {
    return postDelAmbNewArray(e);
  }
  
  public final JL delWorker() {
    JL e = delWorkerImpl();

    if (nextDelFactory() != null && nextDelFactory() instanceof FabricDelFactory) {
        JL e2 = ((FabricDelFactory)nextDelFactory()).delWorker();
        e = composeDels(e, e2);
    }
    return postDelWorker(e);
  }

  protected JL delWorkerImpl() {
    return delExprImpl();
  }

  protected JL postDelWorker(JL e) {
    return postDelExpr(e);
  }

  public final JL delFabricArrayInit() {
    JL e = delFabricArrayInitImpl();

    if (nextDelFactory() != null && nextDelFactory() instanceof FabricDelFactory) {
        JL e2 = ((FabricDelFactory)nextDelFactory()).delFabricArrayInit();
        e = composeDels(e, e2);
    }
    return postDelFabricArrayInit(e);
  }

  protected JL delFabricArrayInitImpl() {
    return delArrayInitImpl();
  }

  protected JL postDelFabricArrayInit(JL e) {
    return postDelArrayInit(e);
  }

  public final JL delFabricArrayTypeNode() {
    JL e = delFabricArrayTypeNodeImpl();

    if (nextDelFactory() != null && nextDelFactory() instanceof FabricDelFactory) {
        JL e2 = ((FabricDelFactory)nextDelFactory()).delFabricArrayTypeNode();
        e = composeDels(e, e2);
    }
    return postDelFabricArrayTypeNode(e);
  }

  protected JL delFabricArrayTypeNodeImpl() {
    return delArrayTypeNodeImpl();
  }

  protected JL postDelFabricArrayTypeNode(JL e) {
    return postDelArrayTypeNode(e);
  }

  public final JL delNewFabricArray() {
    JL e = delNewFabricArrayImpl();

    if (nextDelFactory() != null && nextDelFactory() instanceof FabricDelFactory) {
        JL e2 = ((FabricDelFactory)nextDelFactory()).delNewFabricArray();
        e = composeDels(e, e2);
    }
    return postDelNewFabricArray(e);
  }

  protected JL delNewFabricArrayImpl() {
    return delNewArrayImpl();
  }

  protected JL postDelNewFabricArray(JL e) {
    return postDelNewArray(e);
  }

  public final JL delRemoteWorkerGetter() {
    JL e = delRemoteWorkerGetterImpl();

    if (nextDelFactory() != null && nextDelFactory() instanceof FabricDelFactory) {
        JL e2 = ((FabricDelFactory)nextDelFactory()).delRemoteWorkerGetter();
        e = composeDels(e, e2);
    }
    return postDelRemoteWorkerGetter(e);
  }
  
  protected JL delRemoteWorkerGetterImpl() {
    return delExprImpl();
  }

  protected JL postDelRemoteWorkerGetter(JL e) {
    return postDelExpr(e);
  }
  
  public final JL delRetryStmt() {
    JL e = delRetryStmtImpl();
    
    if (nextDelFactory() != null && nextDelFactory() instanceof FabricDelFactory) {
      JL e2 = ((FabricDelFactory) nextDelFactory()).delRetryStmt();
      e = composeDels(e, e2);
    }
    
    return postDelRetryStmt(e);
  }
  
  protected JL delRetryStmtImpl() {
    return delBranchImpl();
  }
  
  protected JL postDelRetryStmt(JL e) {
    return postDelBranch(e);
  }

  //////////////////////////////////////////////////////////////////////////////
  // overridden factory methods                                               //
  //////////////////////////////////////////////////////////////////////////////
  
  @Override
  protected JL delNewImpl() {
    return new FabricNewDel();
  }
  
  @Override
  protected JL delNewLabelImpl() {
    return new FabricNewLabelDel();
  }
  
  @Override
  protected JL delCallImpl() {
    return new FabricCallDel();
  }
}
