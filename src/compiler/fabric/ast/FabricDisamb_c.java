package fabric.ast;

import jif.ast.JifDisamb_c;
import polyglot.ast.Ambiguous;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.PackageNode;
import polyglot.ast.Prefix;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import fabil.ast.CodebaseDisamb;
import fabil.frontend.CodebaseSource;
import fabil.types.CodebaseContext;
import fabil.types.CodebasePackage;
import fabric.lang.Codebase;

public class FabricDisamb_c extends JifDisamb_c implements CodebaseDisamb {

  @Override
  public Node disambiguate(Ambiguous amb, ContextVisitor v, Position pos,
      Prefix prefix, Id name) throws SemanticException {

    Node n = super.disambiguate(amb, v, pos, prefix, name);
    //Only qualify packages in remote source by the codebase
    if(n instanceof PackageNode) {
      CodebaseContext ctx = (CodebaseContext) c;
      Codebase cb = ctx.currentCodebase();
      CodebaseSource cbs = ctx.currentSource();
      
      //Set the codebase and source of the package
      PackageNode pn = (PackageNode) n;
      CodebasePackage cbp = (CodebasePackage) pn.package_();
      cbp = cbp.source(cbs);
      cbp = cbp.codebase(cb);
      
      return pn.package_(cbp);
    }
    return n;
  }

}
