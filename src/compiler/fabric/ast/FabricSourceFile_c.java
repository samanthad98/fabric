package fabric.ast;

import java.util.List;

import polyglot.ast.Import;
import polyglot.ast.PackageNode;
import polyglot.ast.SourceFile_c;
import polyglot.ast.TopLevelDecl;
import polyglot.types.Context;
import polyglot.util.Position;
import fabric.frontend.RemoteSource;
import fabric.lang.Codebase;
import fabric.types.FabricContext;

/**
 * An extension of JifSourceFiles that supports codebases.
 * @author owen
 */
public class FabricSourceFile_c extends SourceFile_c implements FabricSourceFile {
  protected Codebase codebase;
  
  public FabricSourceFile_c(Position pos, PackageNode package1,
      List<Import> imports, List<TopLevelDecl> decls) {
    super(pos, package1, imports, decls);
  }
  
  @Override
  public Context enterScope(Context c) {
    
    FabricContext A =  (FabricContext) c;
    //System.out.println("ENTERING REMOTE SOURCE:" + source + " : codebase " + codebase);

    //push the codebase onto the context stack
    if(codebase != null) {
      A = (FabricContext) A.pushCodebase(codebase);
    }    
    return super.enterScope(A);
  }
  
  /** Set the codebase of the source file. */
  public FabricSourceFile codebase(Codebase cb) {
      FabricSourceFile_c n = (FabricSourceFile_c) copy();
      n.codebase = cb;
      return n;
  }
  
  /** Set the codebase of the source file. */
  public Codebase codebase() {
      return codebase;
  }

  public boolean isRemote() {
    return source instanceof RemoteSource;
  }  
}
