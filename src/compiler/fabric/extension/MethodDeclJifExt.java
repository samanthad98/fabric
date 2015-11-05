package fabric.extension;

import fabric.types.FabricContext;
import fabric.types.FabricMethodInstance;
import fabric.types.FabricPathMap;
import fabric.types.FabricProcedureInstance;
import fabric.types.FabricTypeSystem;

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

  @Override
  protected void addReturnConstraints(Label Li, PathMap X,
      JifProcedureInstance mi, LabelChecker lc, final Type returnType)
    throws SemanticException {
    super.addReturnConstraints(Li, X, mi, lc, returnType);
    addAccessReturnConstraints(lc, (FabricPathMap) X, (FabricProcedureInstance) mi);
  }

  @Override
  protected void initContextForBody(LabelChecker lc, JifMethodInstance mi) {
    super.initContextForBody(lc, mi);
    FabricMethodInstance fmi = (FabricMethodInstance) mi;
    FabricContext A = (FabricContext) lc.context();
    FabricTypeSystem ts = (FabricTypeSystem) lc.typeSystem();
    A.setAccessedConfBound(ts.pairLabel(fmi.position(), fmi.beginAccessPolicy(), ts.bottomIntegPolicy(fmi.position())));
    A.setEndConfBound(ts.toLabel(fmi.endConfPolicy()));
  }

  //TODO: Move this into general FabricProcedureDeclExt?
  /**
   * Add constraints requiring that all accesses in the method have
   * confidentiality upperbounded by the end confidentiality label.
   */
  protected void addAccessReturnConstraints(LabelChecker lc, FabricPathMap X,
      FabricProcedureInstance mi) throws SemanticException {
    FabricTypeSystem ts = (FabricTypeSystem) lc.typeSystem();
    FabricContext A = (FabricContext) lc.context();

    // Get the join of all accesses in the method
    NamedLabel accessedConfLabel = new NamedLabel("accessed conf label",
        "the join of the confidentiality policies of referenced fields in the method",
        ts.join(A.accessedConf(), X.AC()));

    NamedLabel endConfLabel = new NamedLabel("end conf label",
        "the upper bound on the confidentiality of accessed fields in this method",
        ts.toLabel(mi.endConfPolicy()));

    lc.constrain(accessedConfLabel, LabelConstraint.LEQ, endConfLabel,
        A.labelEnv(), mi.position(), new ConstraintMessage() {
      @Override
      public String msg() {
        return "This method makes more confidential accesses than the ending "
             + "confidentiality label allows.";
      }
    });
  }
}
