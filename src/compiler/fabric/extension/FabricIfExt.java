package fabric.extension;

import fabric.ast.FabricUtil;
import fabric.types.FabricContext;
import fabric.types.FabricPathMap;
import fabric.types.FabricTypeSystem;

import jif.extension.JifIfExt;
import jif.translate.ToJavaExt;
import jif.types.JifContext;
import jif.types.PathMap;
import jif.types.label.Label;
import jif.visit.LabelChecker;

import polyglot.ast.If;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.types.SemanticException;
import polyglot.util.SerialVersionUID;

/** The Fabric version of the Jif extension of the <code>If</code> node.
 * 
 *  @see polyglot.ast.Block_c
 *  @see jif.extension.JifIfExt
 */
public class FabricIfExt extends JifIfExt {
  private static final long serialVersionUID = SerialVersionUID.generate();

  public FabricIfExt(ToJavaExt toJava) {
      super(toJava);
  }

  /**
   * Extended to add any necessary staging at the end of each branch to
   * ensure that we're in a consistent stage after the if statement.
   */
  @Override
  public Node labelCheckStmt(LabelChecker lc) throws SemanticException {
    If iff = (If) super.labelCheckStmt(lc);
    if (iff.alternative() != null) {
      FabricTypeSystem ts = (FabricTypeSystem) lc.jifTypeSystem();
      FabricContext A = (FabricContext) lc.context();

      // Get the conflict label at the end of each alternative
      FabricPathMap Xt = (FabricPathMap) getPathMap(iff.consequent());
      Label tCL = Xt.CL();
      FabricPathMap Xf = (FabricPathMap) getPathMap(iff.alternative());
      Label fCL = Xf.CL();
      boolean tToF = A.labelEnv().leq(tCL, fCL);
      boolean fToT = A.labelEnv().leq(tCL, fCL);

      // Simplify the ending label based on the environment
      FabricPathMap X = (FabricPathMap) getPathMap(iff);
      if (tToF && fToT) {
        Stmt alt = (Stmt) updatePathMap(iff.alternative(), Xf.CL(tCL));
        iff = iff.alternative(alt);
        X = X.CL(tCL);
      } else if (tToF) {
        X = X.CL(tCL);
      } else if (fToT) {
        X = X.CL(fCL);
      } else {
        X = X.CL(ts.meet(tCL, fCL));
      }
      iff = (If) updatePathMap(iff, X);

      // Set the conflict pc to the meet of the two branches.
      A.setConflictLabel(X.CL());

      if (!tToF || !fToT) {
        // Consequent or alternative isn't statically ending at a stage equal to
        // the other, so we need to add a final staging check to one or both
        // branches.
        FabricStagingExt fse = FabricUtil.fabricStagingExt(iff);
        fse.setStageCheck(X.CL());
      }
    }
    return iff;
  }

  /**
   * Modified to reset the conflict pc after first branch.
   */
  @Override
  protected void updateContextForConsequent(LabelChecker lc, JifContext A,
      PathMap Xcond) {
    super.updateContextForConsequent(lc, A, Xcond);
    FabricContext Af = (FabricContext) A;
    FabricPathMap Xfcond = (FabricPathMap) Xcond;
    Af.setConflictLabel(Xfcond.CL());
  }
}