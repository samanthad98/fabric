package fabric.extension;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import fabric.visit.ProxyRewriter;

public class ExprExt_c extends FabricExt_c {

  /*
   * (non-Javadoc)
   * 
   * @see fabric.extension.FabricExt_c#rewriteProxies(fabric.visit.ProxyRewriter)
   */
  @Override
  public final Node rewriteProxies(ProxyRewriter pr) {
    Expr expr = rewriteProxiesImpl(pr);
    if (expr != null) expr = expr.type(node().type());
    return expr;
  }

  protected Expr rewriteProxiesImpl(ProxyRewriter pr) {
    return (Expr) super.rewriteProxies(pr);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fabric.extension.FabricExt_c#rewriteProxiesOverride(fabric.visit.ProxyRewriter)
   */
  @Override
  public final Node rewriteProxiesOverride(ProxyRewriter rewriter) {
    Expr expr = rewriteProxiesOverrideImpl(rewriter);
    if (expr != null) expr = expr.type(node().type());
    return expr;
  }

  protected Expr rewriteProxiesOverrideImpl(ProxyRewriter pr) {
    return (Expr) super.rewriteProxiesOverride(pr);
  }

  /*
   * (non-Javadoc)
   * 
   * @see polyglot.ast.Ext_c#node()
   */
  @Override
  public Expr node() {
    return (Expr) super.node();
  }

}
