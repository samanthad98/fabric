/**
 * Copyright (C) 2010 Fabric project group, Cornell University
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
package fabil.extension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.ast.*;
import polyglot.qq.QQ;
import polyglot.types.Flags;
import fabil.visit.ProxyRewriter;

public class FieldDeclExt_c extends ClassMemberExt_c {

  /*
   * (non-Javadoc)
   * 
   * @see fabil.extension.ClassMemberExt#implMember(fabil.visit.ProxyRewriter,
   *      polyglot.ast.ClassDecl)
   */
  @Override
  public List<ClassMember> implMember(ProxyRewriter pr, ClassDecl parent) {
    return implMember(pr, parent, false);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fabil.extension.ClassMemberExt_c#staticImplMember(fabil.visit.ProxyRewriter,
   *      polyglot.ast.ClassDecl)
   */
  @Override
  public List<ClassMember> staticImplMember(ProxyRewriter pr, ClassDecl parent) {
    return implMember(pr, parent, true);
  }

  private List<ClassMember> implMember(ProxyRewriter pr, ClassDecl parent,
      boolean doStatic) {
    FieldDecl fieldDecl = node();
    String fieldName = fieldDecl.name();
    Flags flags = fieldDecl.flags();

    // Only handle fields that have the static flag appropriately set.
    if (doStatic != flags.isStatic())
      return super.implMember(pr, parent);

    // Make the field non-static and non-final.
    flags = flags.clearStatic().clearFinal();
    
    // Clear any initializers on static fields. They get moved into the $init()
    // method.
    if (doStatic) fieldDecl = fieldDecl.init(null);

    List<ClassMember> result = new ArrayList<ClassMember>();
    for (ClassMember m : accessors(pr))
      result.addAll(ext(m).implMember(pr, parent));
    result.add(fieldDecl.flags(flags).name(fieldName));
    return result;
  }

  /*
   * (non-Javadoc)
   *
   * @see fabil.extension.ClassMemberExt_c#staticImplInitMember(fabil.visit.ProxyRewriter)
   */
  @Override
  public List<Stmt> staticImplInitMember(ProxyRewriter pr) {
    FieldDecl fieldDecl = node();
    String fieldName = fieldDecl.name();
    Expr init = fieldDecl.init();
    Flags flags = fieldDecl.flags();
    
    if (init == null || !flags.isStatic() || flags.isFinal())
      return super.staticImplInitMember(pr);
    
    QQ qq = pr.qq();
    return Collections.singletonList(qq.parseStmt(fieldName + " = %E ;", init));
  }

  /*
   * (non-Javadoc)
   * 
   * @see fabil.extension.ClassMemberExt#interfaceMember(fabil.visit.ProxyRewriter,
   *      polyglot.ast.ClassDecl)
   */
  @Override
  public List<ClassMember> interfaceMember(ProxyRewriter pr, ClassDecl parent) {
    // Omit static fields. These will be put in the _Static type.
    if (node().flags().isStatic()) return super.interfaceMember(pr, parent);

    List<ClassMember> result = new ArrayList<ClassMember>();
    for (ClassMember m : accessors(pr))
      result.addAll(ext(m).interfaceMember(pr, parent));
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fabil.extension.ClassMemberExt#proxyMember(fabil.visit.ProxyRewriter,
   *      polyglot.ast.ClassDecl)
   */
  @Override
  public List<ClassMember> proxyMember(ProxyRewriter pr, ClassDecl parent) {
    return proxyMember(pr, parent, false);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fabil.extension.ClassMemberExt_c#staticProxyMember(fabil.visit.ProxyRewriter,
   *      polyglot.ast.ClassDecl)
   */
  @Override
  public List<ClassMember> staticProxyMember(ProxyRewriter pr, ClassDecl parent) {
    return proxyMember(pr, parent, true);
  }

  private List<ClassMember> proxyMember(ProxyRewriter pr, ClassDecl parent,
      boolean doStatic) {
    FieldDecl fieldDecl = node();
    Flags flags = fieldDecl.flags();
    TypeNode type = fieldDecl.type();
    String name = fieldDecl.name();

    // Only handle fields that have the static flag properly set.
    if (doStatic != flags.isStatic())
      return super.implMember(pr, parent);

    // Make the method public, non-final, non-static and non-transient.
    flags =
        ProxyRewriter.toPublic(flags).clearTransient().clearFinal()
            .clearStatic();

    // Figure out the call target for the delegates.
    String target =
        "((" + parent.type().fullName() + (doStatic ? "._Static" : "")
            + "._Impl) fetch())";

    QQ qq = pr.qq();
    List<ClassMember> result = new ArrayList<ClassMember>(4);
    result.add(qq.parseMember(flags + " %T get$" + name + "() {" + "return "
        + target + ".get$" + name + "(); }", type));

    if (!fieldDecl.flags().isFinal()) {
      result.add(qq.parseMember(flags + " %T set$" + name + "(%T val) {"
          + "return " + target + ".set$" + name + "(val); }", type, type));

      // Add post-incrementer and post-decrementer proxies if type is numeric.
      if (type.type().isNumeric()) {
        result.add(qq.parseMember(flags + " %T postInc$" + name + "() {"
            + "return " + target + ".postInc$" + name + "(); }", type));
        result.add(qq.parseMember(flags + " %T postDec$" + name + "() {"
            + "return " + target + ".postDec$" + name + "(); }", type));
      }
    }

    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fabil.extension.ClassMemberExt_c#staticInterfaceMember(fabil.visit.ProxyRewriter,
   *      polyglot.ast.ClassDecl)
   */
  @Override
  public List<ClassMember> staticInterfaceMember(ProxyRewriter pr,
      ClassDecl parent) {
    FieldDecl fieldDecl = node();
    Flags flags = fieldDecl.flags();

    // Ignore non-static fields.
    if (!flags.isStatic()) return super.interfaceMember(pr, parent);
    
    // Ignore Polyglot type information generated by fabc.
    if (flags.isPublic() && flags.isFinal()
        && fieldDecl.name().startsWith("jlc$"))
      return super.interfaceMember(pr, parent);

    List<ClassMember> result = new ArrayList<ClassMember>();
    for (ClassMember m : accessors(pr))
      result.addAll(ext(m).interfaceMember(pr, parent));
    return result;
  }

  /**
   * Produces the getter, setter, and incrementer methods for the field.
   */
  protected List<ClassMember> accessors(ProxyRewriter pr) {
    FieldDecl fieldDecl = node();
    Flags flags = fieldDecl.flags();
    QQ qq = pr.qq();

    TypeNode typeNode = fieldDecl.type();
    String name = fieldDecl.name();
    boolean finalField = flags.isFinal();

    flags = flags.clearTransient().clearFinal().clearStatic().clearPrivate();
    List<ClassMember> members = new ArrayList<ClassMember>(4);
    String regRead =
        finalField ? ""
            : "fabric.worker.transaction.TransactionManager.getInstance().registerRead(this);";
    members.add(qq.parseMember(flags + " %T get$" + name + "() {" + regRead
        + "return this." + name + "; }", typeNode));

    if (!finalField) {
      members.add(qq.parseMember(
          flags + " %T set$" + name + "(%T val) {"
              + "fabric.worker.transaction.TransactionManager tm = "
              + "fabric.worker.transaction.TransactionManager.getInstance();"
              + "boolean transactionCreated = tm.registerWrite(this);"
              + "this." + name + " = val;"
              + "if (transactionCreated) tm.commitTransaction();"
              + "return val; }", typeNode, typeNode));

      // Add post-incrementer and post-decrementer if type is numeric.
      if (typeNode.type().isNumeric()) {
        members.add(qq.parseMember(flags + " %T postInc$" + name + "() {"
            + "%T tmp = this.get$" + name + "();" + "this.set$" + name
            + "((%T) (tmp + 1));" + "return tmp; }", typeNode, typeNode,
            typeNode, typeNode));
        members.add(qq.parseMember(flags + " %T postDec$" + name + "() {"
            + "%T tmp = this.get$" + name + "();" + "this.set$" + name
            + "((%T) (tmp - 1));" + "return tmp; }", typeNode, typeNode,
            typeNode, typeNode));
      }
    }

    return members;
  }

  /*
   * (non-Javadoc)
   * 
   * @see polyglot.ast.Ext_c#node()
   */
  @Override
  public FieldDecl node() {
    return (FieldDecl) super.node();
  }

  private ClassMemberExt ext(ClassMember m) {
    return (ClassMemberExt) m.ext();
  }
}
