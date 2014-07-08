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
import java.util.List;

import polyglot.ast.*;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.util.Position;
import fabil.ast.Atomic;
import fabil.ast.FabILNodeFactory;
import fabil.types.FabILTypeSystem;
import fabil.visit.StaticInitializerCollector;

public class ClassBodyExt_c extends FabILExt_c {

  @SuppressWarnings("unchecked")
  @Override
  public Node collectStaticInitializers(StaticInitializerCollector sc) {
    ClassBody body = node();
    FabILNodeFactory nf = sc.nodeFactory();
    FabILTypeSystem ts = sc.typeSystem();

    // This will hold the statements in the body of the resulting static
    // initializer.
    List<Stmt> initStmts = new ArrayList<Stmt>();

    ClassType classType = null;

    List<ClassMember> members = new ArrayList<ClassMember>();
    for (Object obj : body.members()) {
      ClassMember member = (ClassMember) obj;

      if (member instanceof Initializer) {
        Initializer init = (Initializer) member;
        if (!init.flags().isStatic()) {
          members.add(init);
          continue;
        }

        // Static initializer. Add its body to the initializer we're building.
        initStmts.addAll(init.body().statements());
        continue;
      }

      if (member instanceof FieldDecl) {
        FieldDecl fieldDecl = (FieldDecl) member;
        if (!fieldDecl.flags().isStatic() || fieldDecl.init() == null) {
          members.add(fieldDecl);
          continue;
        }
        
        // Don't touch fields with Polyglot type info.
        if (fieldDecl.name().startsWith("jlc$")) {
          members.add(fieldDecl);
          continue;
        }

        // Static field. Move its initializer into the static initializer.
        Position pos = Position.compilerGenerated();
        String name = fieldDecl.name();
        if (classType == null)
          classType = (ClassType) fieldDecl.fieldInstance().container();

        Receiver fieldReceiver = nf.CanonicalTypeNode(pos, classType);
        Field field = nf.Field(pos, fieldReceiver, nf.Id(pos, name));
        field = (Field) field.type(fieldDecl.type().type());
        field = field.fieldInstance(fieldDecl.fieldInstance());
        FieldAssign fieldInit =
            nf.FieldAssign(pos, field, Assign.ASSIGN, fieldDecl.init());
        fieldInit = (FieldAssign) fieldInit.type(fieldDecl.type().type());
        initStmts.add(nf.Eval(pos, fieldInit));

        members.add(fieldDecl.init(null));
        continue;
      }

      members.add(member);
    }
    
    if (classType != null) {
      // Only translate Fabric classes.
      // XXX This is not quite right. We should be translating Java classes too,
      // but there's some wackiness going on with final fields.
      if (!ts.isFabricClass(classType)) return node();
      
      // Don't translate interfaces.
      if (classType.flags().isInterface()) return node();
    }

    if (!initStmts.isEmpty()) {
      // Create the new static initializer and add it to the class body.
      Position pos = Position.compilerGenerated();
      Atomic atomic = nf.Atomic(pos, initStmts);
      Initializer init =
          nf.Initializer(pos, Flags.STATIC, nf.Block(pos, atomic));
      init =
          init.initializerInstance(ts.initializerInstance(pos, classType,
              Flags.STATIC));
      members.add(init);
    }

    return body.members(members);
  }

  @Override
  public ClassBody node() {
    return (ClassBody) super.node();
  }

}
