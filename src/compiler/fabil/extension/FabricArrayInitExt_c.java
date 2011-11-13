package fabil.extension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.qq.QQ;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.MethodInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import fabil.ast.Annotated;
import fabil.ast.FabILNodeFactory;
import fabil.ast.FabricArrayInit;
import fabil.types.FabILTypeSystem;
import fabil.visit.LabelAssigner;
import fabil.visit.ProxyRewriter;

public class FabricArrayInitExt_c extends AnnotatedExt_c {

  @Override
  public Expr rewriteProxiesOverrideImpl(ProxyRewriter rewriter) {
    NodeFactory nf = rewriter.nodeFactory();
    QQ qq = rewriter.qq();
    FabILTypeSystem ts = rewriter.typeSystem();

    FabricArrayInit arrayInit = node();
    Expr location = arrayInit.location();
    Expr label = arrayInit.updateLabel();

    List<Expr> newElements = new ArrayList<Expr>(arrayInit.elements().size());
    for (Object e : arrayInit.elements()) {
      if (e instanceof FabricArrayInit) {
        FabricArrayInit ai = (FabricArrayInit) e;
        newElements.add(ai.location(location));
      } else {
        newElements.add((Expr) e);
      }
    }

    arrayInit = arrayInit.elements(newElements);
    arrayInit = (FabricArrayInit) arrayInit.visitChildren(rewriter);
    
    location = arrayInit.location();
    label = arrayInit.updateLabel();
    Expr accessLabel = arrayInit.accessLabel();

    Type oldBase = arrayInit.type().toArray().base();
    Type newBase = oldBase.isPrimitive() ? oldBase : ts.FObject();
    Expr init =
        nf.NewArray(Position.compilerGenerated(), nf.CanonicalTypeNode(Position
            .compilerGenerated(), newBase), 1, arrayInit);
    
    return qq.parseExpr(
        "fabric.lang.arrays.internal.Compat.convert(%E, %E, %E, %E)", location,
        label, accessLabel, init);
  }

  @Override
  public FabricArrayInit node() {
    return (FabricArrayInit) super.node();
  }

  @Override
  public Annotated assignLabels(LabelAssigner la) throws SemanticException {
    FabricArrayInit expr = node();
    expr = assignUpdateLabel(la, expr);
    expr = assignAccessLabel(la, expr);
    return expr;
  }

  private FabricArrayInit assignUpdateLabel(LabelAssigner la, FabricArrayInit expr)
      throws SemanticException {
        if (expr.updateLabel() != null) return expr;
      
        FabILNodeFactory nf = la.nodeFactory();
        FabILTypeSystem ts = la.typeSystem();
        QQ qq = la.qq();
      
        if (!ts.isFabricReference(expr.type())) return expr;
      
        // Need a label. Use null by default for principal objects. The Principal
        // constructor will fill in the appropriate label.
        if (ts.isPrincipalClass(expr.type())) {
          return expr.updateLabel(qq.parseExpr("null").type(ts.Null()));
        }
      
        // Need a label. By default, we use the same label as the context.
        Context context = la.context();
        ClassType currentClass = context.currentClass();
        if (!ts.isFabricReference(currentClass)) {
          throw new SemanticException("Missing label", expr.position());
        }
      
        Receiver receiver;
        if (context.inStaticContext()) {
          receiver =
              nf.CanonicalTypeNode(Position.compilerGenerated(), currentClass);
        } else {
          receiver = qq.parseExpr("this").type(currentClass);
        }
      
        Position pos = Position.compilerGenerated();
        Call defaultLabel = nf.Call(pos, receiver, nf.Id(pos, "get$label"));
      
        Flags flags = Flags.NONE;
        if (context.inStaticContext()) flags = Flags.STATIC;
      
        MethodInstance lmi =
            ts.methodInstance(pos, currentClass, flags, ts.Label(), "get$label",
                Collections.emptyList(), Collections.emptyList());
        defaultLabel = (Call) defaultLabel.type(ts.Label());
        defaultLabel = defaultLabel.methodInstance(lmi);
        return expr.updateLabel(defaultLabel);
      }

  private FabricArrayInit assignAccessLabel(LabelAssigner la, FabricArrayInit expr) {
    if (expr.accessLabel() != null) return expr;
  
    FabILTypeSystem ts = la.typeSystem();
    QQ qq = la.qq();
  
    if (!ts.isFabricReference(expr.type())) return expr;
  
    // Need an access label. Use null by default for principal objects. The
    // Principal constructor will fill in the appropriate access label.
    if (ts.isPrincipalClass(expr.type())) {
      return expr.accessLabel(qq.parseExpr("null").type(ts.Null()));
    }
  
    // Need a label. Use the object label by default.
    Expr label = expr.updateLabel();
    return expr.accessLabel(label);
  }

}
