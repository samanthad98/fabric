package fabil.types;

import fabil.frontend.CodebaseSource;
import fabric.common.SysUtil;
import fabric.lang.Codebase;
import fabric.lang.FClass;
import polyglot.types.ClassType;
import polyglot.types.Named;
import polyglot.types.NoClassException;
import polyglot.types.PackageContextResolver;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.util.StringUtil;

public class CodebasePackageContextResolver extends PackageContextResolver {

  public CodebasePackageContextResolver(TypeSystem ts, CodebasePackage p) {
    super(ts, p);
  }

  /**
   * Find a type object by name.
   */
  @Override
  public Named find(String name, ClassType accessor) throws SemanticException {
    if (!StringUtil.isNameShort(name)) {
      throw new InternalCompilerError("Cannot lookup qualified name " + name);
    }
    String fqName;
    Named n = null;
    CodebaseTypeSystem cbts = (CodebaseTypeSystem) ts;
    if(cbts.isPlatformType(p))
      return super.find(name, accessor);
    
    CodebaseSource cs = ((CodebasePackage) p).source();
    Codebase cb = cs.codebase();
    FClass fcls = cb.resolveClassName(p.fullName() + "." + name);
    if(fcls == null)
      return null;
    
    String prefix = SysUtil.codebasePrefix(fcls.getCodebase());
    fqName = prefix + p.fullName() + "." + name;

    try {
      n = ts.systemResolver().find(fqName);
    } catch (NoClassException e) {
      // Rethrow if some _other_ class or package was not found.
      if (!e.getClassName().equals(fqName)) {
        throw e;
      }
    }
    System.out.println("N: " +n);

    if (n == null) {
      n = ts.createPackage(p, name);
    }

    if (!canAccess(n, accessor)) {
      throw new SemanticException("Cannot access " + n + " from " + accessor
          + ".");
    }

    return n;
  }

}
