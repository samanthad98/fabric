package fabric.extension;

import fabric.ast.FabricMethodDecl;
import fabric.types.FabricContext;
import fabric.types.FabricMethodInstance;
import fabric.types.FabricPathMap;
import fabric.types.FabricTypeSystem;

import jif.ast.JifMethodDecl;
import jif.extension.JifMethodDeclExt;
import jif.translate.ToJavaExt;
import jif.types.ConstraintMessage;
import jif.types.JifMethodInstance;
import jif.types.JifProcedureInstance;
import jif.types.LabelConstraint;
import jif.types.NamedLabel;
import jif.types.PathMap;
import jif.types.label.Label;
import jif.visit.LabelChecker;

import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;

public class MethodDeclJifExt extends JifMethodDeclExt {

  protected boolean is_remote;

  public MethodDeclJifExt(ToJavaExt toJava) {
    super(toJava);
    this.is_remote = false;
  }

  public void setRemote() {
    is_remote = true;
  }

  public boolean isRemote() {
    return is_remote;
  }

  /**
   * Add additional context initialization for conflict labels
   */
  @Override
  protected void initContextForBody(LabelChecker lc, JifMethodInstance mi) {
    super.initContextForBody(lc, mi);
    FabricMethodInstance fmi = (FabricMethodInstance) mi;
    FabricContext A = (FabricContext) lc.context();
    A.setBeginConflictBound(fmi.beginConflictLabel());
    A.setConflictLabel(fmi.beginConflictLabel());
    A.setEndConflictBound(fmi.endConflictLabel());
  }

  /**
   * Add additional ending constraint that we're respecting the end conflict
   * label of the method.
   */
  @Override
  protected void addReturnConstraints(Label Li, PathMap X,
      JifProcedureInstance mi, LabelChecker lc, final Type returnType) throws
  SemanticException {
    super.addReturnConstraints(Li, X, mi, lc, returnType);

    FabricMethodInstance fmi = (FabricMethodInstance) mi;
    FabricTypeSystem ts = (FabricTypeSystem) lc.typeSystem();
    FabricContext A = (FabricContext) lc.context();
    FabricPathMap Xf = (FabricPathMap) X;

    // Don't bother if we didn't make accesses.
    if (Xf.CL().equals(ts.noAccesses()))
      return;
    
    final String name = ((JifMethodDecl) node()).name();

    NamedLabel endConflictBoundLabel = new NamedLabel("end conflict label of " + name,
        "lower bound on accesses that can be made up to the end of the body of "
        + name, 
        fmi.endConflictLabel());

    NamedLabel endCLN = new NamedLabel("prev conflict label",
        "the meet of the conflict labels of accesses up to the end of the method",
        Xf.CL());

    // Check that the end conflict label is respected by the method body.
    lc.constrain(endConflictBoundLabel,
        LabelConstraint.LEQ,
        endCLN.join(lc, "{⊥→;⊥←}", ts.noComponentsLabel()),
        A.labelEnv(), mi.position(), new ConstraintMessage() {
      @Override
      public String msg() {
        return "The body of " + name + " makes less restricted accesses than "
          + "the ending conflict label allows.";
      }
    });
  }

  @Override
  protected Label checkEnforceSignature(JifProcedureInstance mi,
          LabelChecker lc) throws SemanticException {
    Label l = super.checkEnforceSignature(mi, lc);
    FabricContext A = (FabricContext) lc.context();
    FabricTypeSystem ts = (FabricTypeSystem) lc.typeSystem();
    FabricMethodDecl md = (FabricMethodDecl) node();

    // Check that we can actually stage from the start to the end.
    FabricMethodInstance fmi = (FabricMethodInstance) md.methodInstance();
    final Position declPos = node().position();
    if (!fmi.beginConflictLabel().equals(ts.noAccesses())) {
      lc.constrain(new NamedLabel("end conflict label", fmi.endConflictLabel()),
          LabelConstraint.LEQ,
          new NamedLabel("begin conflict label", fmi.beginConflictLabel()).join(lc, "{⊥→;⊥←}", ts.noComponentsLabel()),
          A.labelEnv(), declPos,
          new ConstraintMessage() {
            @Override
            public String msg() {
              return "End conflict label must have lower confidentiality than"
                     + " begin conflict label for method at " + declPos;
            }
      });
    }

    return l;
  }
}
