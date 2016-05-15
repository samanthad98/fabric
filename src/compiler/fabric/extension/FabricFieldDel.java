package fabric.extension;

import jif.extension.JifFieldDel;

import polyglot.ast.Expr;

public class FabricFieldDel extends JifFieldDel implements FabricStagingDel {
  /**
   * Squirreled away the staging check expression to be produced when rewriting
   * to FabIL.
   */
  protected Expr stageCheck;

  @Override
  public Expr stageCheck() {
    return stageCheck;
  }

  @Override
  public void setStageCheck(Expr stageCheck) {
    this.stageCheck = stageCheck;
  }
}