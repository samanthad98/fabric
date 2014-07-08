/**
 * Copyright (C) 2010-2012 Fabric project group, Cornell University
 *
 * This file is part of Fabric.
 *
 * Fabric is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 2 of the License, or (at your option) any later
 * version.
 * 
 * Fabric is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 */
package fabric.translate;

import java.util.ArrayList;
import java.util.List;

import jif.translate.ClassDeclToJavaExt_c;
import jif.translate.JifToJavaRewriter;
import jif.translate.ParamToJavaExpr_c;
import jif.types.JifPolyType;
import jif.types.ParamInstance;
import jif.types.label.ConfPolicy;
import jif.types.label.Label;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.SemanticException;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import fabil.ast.FabILNodeFactory;
import fabil.types.FabILTypeSystem;
import fabric.types.FabricClassType;
import fabric.types.FabricParsedClassType_c;
import fabric.types.FabricTypeSystem;
import fabric.visit.FabricToFabilRewriter;

public class ClassDeclToFabilExt_c extends ClassDeclToJavaExt_c {
  public static final String jifConstructorTranslatedName(ClassType ct) {
    return ClassDeclToJavaExt_c.constructorTranslatedName(ct);
  }

  @Override
  public Node toJava(JifToJavaRewriter rw) throws SemanticException {
    ClassDecl cd = (ClassDecl) super.toJava(rw);

    cd = cd.body(addLabelInitializer(cd.body(), rw));

    FabILNodeFactory nf = (FabILNodeFactory) rw.nodeFactory();
    FabILTypeSystem ts = (FabILTypeSystem) rw.java_ts();

    TypeNode worker =
        nf.CanonicalTypeNode(Position.compilerGenerated(), ts.Worker());

    List<ClassMember> members =
        new ArrayList<ClassMember>(cd.body().members().size() + 1);
    members.add(nf.FieldDecl(
        Position.compilerGenerated(),
        Flags.FINAL.Static(),
        worker,
        nf.Id(Position.compilerGenerated(), "worker$"),
        nf.Call(Position.compilerGenerated(), worker,
            nf.Id(Position.compilerGenerated(), "getWorker"))));

    members.addAll(cd.body().members());

    return cd.body(cd.body().members(members));
  }

  @Override
  protected ClassMember produceInstanceOfMethod(JifPolyType jpt,
      JifToJavaRewriter rw, boolean useGetters) throws SemanticException {

    Context A = rw.context();
    FabricToFabilRewriter frw =
        (FabricToFabilRewriter) rw.context(A.pushStatic());
    FabricTypeSystem jifts = (FabricTypeSystem) frw.jif_ts();

    if (jifts.isTransient(jpt)) {
      return super.produceInstanceOfMethod(jpt, frw, useGetters);
    }

    List<Formal> formals = produceFormals(jpt, frw);
    String name = jpt.name();

    // Replace "this" with argument to instanceof.
    Expr thisPrincipal = frw.qq().parseExpr("o");
    frw.setStaticThisExpr(thisPrincipal);

    boolean sigMode = frw.inSignatureMode();

    if (jifts.isSignature(jpt) || sigMode) {
      // just produce a header
      return frw.qq().parseMember("static public native boolean %s(%LF);",
          INSTANCEOF_METHOD_NAME, formals);
    }

    StringBuffer sb = new StringBuffer();
    sb.append("static public boolean %s(%LF) {");

    sb.append("if (o == null) return false;");
    // Add code that checks that the access label of jpt flows to o.store
    // proceed normally if it does, otherwise throw an InternalError
    if (!(jpt instanceof FabricParsedClassType_c))
      throw new InternalCompilerError(
          "Trying to produce an instanceof method for a non-fabric class");

    FabricParsedClassType_c fpct = (FabricParsedClassType_c) jpt;
    ConfPolicy cp = fpct.accessPolicy();
    Label accessLabel =
        jifts.pairLabel(cp.position(), cp, jifts.topIntegPolicy(cp.position()));
    Expr accessLabelExpr = frw.labelToJava(accessLabel);
    Expr objectExpr = frw.qq().parseExpr("o");
    sb.append(frw.runtimeLabelUtil() + ".accessCheck(%E, %E);");

    if (jpt.params().isEmpty()) {
      sb.append("return (o instanceof %s);");
    } else {
      sb.append("if (o instanceof %s) { ");
      sb.append("%s c = (%s)o; ");

      // now test each of the params
      boolean moreThanOneParam = (jpt.params().size() > 1);
      sb.append(moreThanOneParam ? "boolean ok = true;" : "");
      for (ParamInstance pi : jpt.params()) {
        String paramFieldName = ParamToJavaExpr_c.paramFieldName(pi);
        String paramArgName = ParamToJavaExpr_c.paramArgName(pi);
        String comparison = "equivalentTo";
        if (pi.isCovariantLabel()) {
          comparison = "relabelsTo";
        }

        sb.append(moreThanOneParam ? "ok = ok && " : "return ");

        String paramExpr = paramFieldName;
        if (useGetters) {
          paramExpr = ParamToJavaExpr_c.paramFieldNameGetter(pi) + "()";
        }
        if (pi.isPrincipal()) {
          // e.g., PrincipalUtil.equivTo(c.expr, paramArgName)
          sb.append(jifts.PrincipalUtilClassName() + "." + comparison + "(c."
              + paramExpr + "," + paramArgName + ");");
        } else {
          // e.g., LabelUtil.equivTo(paramArgName)
          sb.append(frw.runtimeLabelUtil() + "." + comparison + "(c."
              + paramExpr + "," + paramArgName + ");");
        }
      }
      if (moreThanOneParam) sb.append("return ok;");
      sb.append("}");
      sb.append("return false;");
    }
    sb.append("}");

    frw.clearStaticThisExpr();
    return frw.qq().parseMember(sb.toString(), INSTANCEOF_METHOD_NAME, formals,
        accessLabelExpr, objectExpr, name, name, name);
  }

  @Override
  protected Formal produceObjectFormal(JifPolyType jpt, JifToJavaRewriter rw) {
    FabricTypeSystem ts = (FabricTypeSystem) rw.typeSystem();
    if (ts.isTransient(jpt)) {
      return super.produceObjectFormal(jpt, rw);
    } else {
      TypeNode tn = rw.qq().parseType("fabric.lang.Object");
      return rw.java_nf().Formal(Position.compilerGenerated(), Flags.FINAL, tn,
          rw.java_nf().Id(Position.compilerGenerated(), "o"));
    }
  }

//  @Override
//  protected List<Expr> produceParamArgs(JifPolyType jpt, JifToJavaRewriter rw) {
//    List<Expr> args = super.produceParamArgs(jpt, rw);
//
////     add access policy arg
//    args.add(rw.qq().parseExpr("jif$accessPolicy"));
//
//    return args;
//  }

  @Override
  protected ClassBody addInitializer(ClassBody cb, JifToJavaRewriter rw) {
    List<Stmt> inits = new ArrayList<Stmt>(rw.getInitializations());
    rw.getInitializations().clear();
    return cb.addMember(rw.qq().parseMember("private void %s() { %LS }",
        INITIALIZATIONS_METHOD_NAME, inits));
  }

  /**
   * Create a method for initializing update and access labels
   * 
   * @throws SemanticException
   *           if the field or access label cannot be translated
   */
  protected ClassBody addLabelInitializer(ClassBody cb, JifToJavaRewriter rw)
      throws SemanticException {
    FabricTypeSystem ts = (FabricTypeSystem) rw.jif_ts();
    boolean sigMode = ((FabricToFabilRewriter) rw).inSignatureMode();

    //FIXME: why is rw.currentClass() null?
    ClassDecl n = (ClassDecl) node();
    FabricClassType ct = (FabricClassType) n.type();

    if (!sigMode && ts.isFabricClass(ct)) {

      // translate the labels to FabIL
      Label updateLabel = ct.updateLabel();
      ConfPolicy accessPolicy = ct.accessPolicy();

      if (updateLabel == null || accessPolicy == null) {
        throw new InternalCompilerError("Null update label or access policy");
      }

      // locate labels at the same store as the object
      rw =
          ((FabricToFabilRewriter) rw).pushLocation(rw.qq().parseExpr(
              "this.$getStore()"));

      Expr updateLabelExpr = rw.labelToJava(updateLabel);

      Label accessLabel =
          ts.pairLabel(accessPolicy.position(), accessPolicy,
              ts.topIntegPolicy(accessPolicy.position()));

      Expr accessLabelExpr = rw.labelToJava(accessLabel);

      return cb.addMember(rw.qq().parseMember(
          "public Object %s() { " + "this.$updateLabel = %E;  "
              + "this.$accessPolicy = %E.confPolicy();" + "return this;" + "}",
          FabricToFabilRewriter.LABEL_INITIALIZER_METHOD_NAME, updateLabelExpr,
          accessLabelExpr));
    }

    return cb;
  }
}
