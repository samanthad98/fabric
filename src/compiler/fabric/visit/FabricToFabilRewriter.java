package fabric.visit;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import jif.translate.JifToJavaRewriter;
import polyglot.ast.ClassDecl;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.SourceCollection;
import polyglot.ast.SourceFile;
import polyglot.ast.TypeNode;
import polyglot.frontend.Job;
import polyglot.frontend.Source;
import polyglot.main.Report;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import codebases.ast.CBSourceFile;
import codebases.frontend.CBJobExt;
import codebases.frontend.CodebaseSource;
import codebases.types.CodebaseClassType;
import fabil.FabILOptions;
import fabil.ast.FabILNodeFactory;
import fabil.types.FabILTypeSystem;
import fabric.ExtensionInfo;
import fabric.ast.FabricNodeFactory;
import fabric.common.NSUtil;
import fabric.lang.Codebase;
import fabric.types.FabricContext;
import fabric.types.FabricSubstType;
import fabric.types.FabricTypeSystem;

public class FabricToFabilRewriter extends JifToJavaRewriter {
  private static final Collection<String> TOPICS = CollectionUtil.list(
      "publish", "mobile");

  protected boolean principalExpected = false;
  
  public FabricToFabilRewriter(Job job, FabricTypeSystem fab_ts,
      FabricNodeFactory fab_nf, fabil.ExtensionInfo fabil_ext) {
    super(job, fab_ts, fab_nf, fabil_ext);
    this.job = job;
  }

  public Source createDerivedSource(CodebaseSource src, String newName) {
    fabric.ExtensionInfo extInfo = (ExtensionInfo) job.extensionInfo();
    FabricTypeSystem fab_ts = (FabricTypeSystem) jif_ts();
    Source derived;
    if(src.shouldPublish() && 
        extInfo.localNamespace().equals(src.namespace())) {
      //If the source we are deriving source is being published, 
      // we should use the published namespace.

      Codebase cb = fab_ts.codebaseFromNS(src.namespace());
      URI published_ns = NSUtil.namespace(cb);
      derived = src.publishedSource(published_ns, newName);
    }
    else {
      //Otherwise, we just create a derived source with a new name
      derived = src.derivedSource(newName);
    }
    if(Report.should_report(TOPICS, 2)) {
      Report.report(2, "Creating derived source " + derived + " from " + src);
    }
    
    return derived;
  }

  public boolean fabIsPublished() {
    return ((CodebaseSource) job.source()).shouldPublish();
  }
  
  public FabricToFabilRewriter pushLocation(Expr location) {
    FabricContext context = (FabricContext) context();
    return (FabricToFabilRewriter) context(context.pushLocation(location));
  }

  public Expr currentLocation() {
    Expr loc = ((FabricContext) context()).location();
    if (loc == null) {
      // XXX: this should only happen for runtime checks that need
      // to create labels. They should *never* flow into persistent
      // objects. How to check this?
      loc = qq().parseExpr("Worker.getWorker().getLocalStore()");
    }
    return loc;
  }

  @Override
  public String runtimeLabelUtil() {
    return jif_ts().LabelUtilClassName();
  }

  @Override
  public TypeNode typeToJava(Type t, Position pos) throws SemanticException {
    FabILNodeFactory fabil_nf = (FabILNodeFactory) java_nf();
    FabILTypeSystem fabil_ts = (FabILTypeSystem) java_ts();
    FabricTypeSystem fabric_ts = (FabricTypeSystem) jif_ts();

    if (fabric_ts.typeEquals(t, fabric_ts.Worker())) {
      return canonical(fabil_nf, fabil_ts.Worker(), pos);
    }

    if (fabric_ts.typeEquals(t, fabric_ts.RemoteWorker())) {
      return canonical(fabil_nf, fabil_ts.RemoteWorker(), pos);
    }

    if (fabric_ts.isFabricArray(t)) {
      return fabil_nf.FabricArrayTypeNode(pos,
          typeToJava(t.toArray().base(), pos));
    }
    
    if (t.isClass() && !fabric_ts.isLabel(t) && !fabric_ts.isPrincipal(t)) {
      CodebaseClassType ct= (CodebaseClassType) t.toClass();
      CBJobExt ext = (CBJobExt) job().ext();
      if(ct instanceof FabricSubstType)
        ct = (CodebaseClassType) ((FabricSubstType) ct).base();
      if (ext.isExternal(ct)) {
        String alias = ext.aliasFor(ct);
        return fabil_nf.TypeNodeFromQualifiedName(pos, alias + "." + t.toClass().fullName());
      }
    }
    return super.typeToJava(t, pos);
  }

  public boolean inSignatureMode() {
    FabILOptions opts =
        (FabILOptions) ((fabil.ExtensionInfo) java_ext).getOptions();
    return opts.signatureMode();
  }

  @SuppressWarnings("unchecked")
  @Override
  public Node leavingSourceFile(SourceFile n) {
    List l = new ArrayList(n.decls().size() + additionalClassDecls.size());
    l.addAll(n.decls());
    for (Iterator iter = this.additionalClassDecls.iterator(); iter.hasNext();) {
      ClassDecl cd = (ClassDecl) iter.next();
      if (cd.flags().isPublic()) {
        // cd is public, we will put it in it's own source file.
        SourceFile sf =
            java_nf().SourceFile(Position.compilerGenerated(), n.package_(),
                Collections.EMPTY_LIST, Collections.singletonList(cd));

        String newName =
            cd.name() + "." + job.extensionInfo().defaultFileExtension();
        
        CBSourceFile cbn = (CBSourceFile) n;
        CodebaseSource source = (CodebaseSource) cbn.source();
        Source derived = createDerivedSource(source, newName);
        this.newSourceFiles.add(sf.source(derived));

      } else {
        // cd is not public; it's ok to put the class decl in the source file.
        l.add(cd);
      }
    }

    this.additionalClassDecls.clear();
    return n.decls(l);
  }

}
