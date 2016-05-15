package fabric.translate;

import fabil.ast.FabILNodeFactory;

import fabric.extension.FabricFieldDel;

import jif.translate.FieldToJavaExt_c;
import jif.translate.JifToJavaRewriter;

import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Node;
import polyglot.types.SemanticException;
import polyglot.util.InternalCompilerError;

public class FieldToFabilExt_c extends FieldToJavaExt_c {

  @Override
  public Node toJava(JifToJavaRewriter rw) throws SemanticException {
    Field orig = (Field) node();
    FabricFieldDel ffd = (FabricFieldDel) orig.del();
    Field fd = (Field) super.toJava(rw);
    if (ffd.stageCheck() != null) {
      FabILNodeFactory nf = (FabILNodeFactory) rw.java_nf();
      if (!(fd.target() instanceof Expr)) {
        throw new InternalCompilerError("Staging does not currently support static non-final fields!");
      }
      Expr targetExp = (Expr) fd.target();
      return fd.target(nf.StageCall(fd.position(), targetExp, rw.visitEdge(orig, ffd.stageCheck())));
    }
    return fd;
  }
}