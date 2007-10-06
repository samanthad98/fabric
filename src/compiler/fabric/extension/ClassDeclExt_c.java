package fabric.extension;

import java.util.*;

import polyglot.ast.*;
import polyglot.qq.QQ;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.MethodInstance;
import polyglot.types.Type;
import polyglot.util.Position;
import fabric.types.FabricTypeSystem;
import fabric.visit.ProxyRewriter;

public class ClassDeclExt_c extends ClassMemberExt_c {

  /**
   * Returns the interface translation of the class declaration.
   * 
   * @see fabric.extension.FabricExt_c#rewriteProxies(fabric.visit.ProxyRewriter)
   */
  @SuppressWarnings("unchecked")
  @Override
  public Node rewriteProxies(ProxyRewriter pr) {
    ClassDecl classDecl = node();

    // Only translate if we're processing a Fabric class.
    if (!pr.typeSystem().isFabric(classDecl.type())) return classDecl;

    // If already an interface, leave it.
    if (classDecl.flags().isInterface()) return classDecl;

    NodeFactory nf = pr.nodeFactory();
    FabricTypeSystem ts = pr.typeSystem();
    TypeNode superClass = classDecl.superClass();

    // Make the super class explicit.
    // TODO This is probably the wrong way to tell.
    if (superClass == null || "java.lang.Object".equals(superClass.toString())) {
      superClass =
          nf.CanonicalTypeNode(Position.compilerGenerated(), ts.FObject());
    }

    // The super class will be turned into an interface that we will extend.
    List<TypeNode> interfaces = new ArrayList<TypeNode>(classDecl.interfaces());
    interfaces.add(superClass);
    ClassDecl result = classDecl.interfaces(interfaces);
    result = result.superClass(null);

    // We're generating an interface.
    Flags flags = ProxyRewriter.toInterface(classDecl.flags());
    result = result.flags(flags);

    // Rewrite the members.
    ClassBody body = classDecl.body();
    List<ClassMember> oldMembers = body.members();
    List<ClassMember> members = new ArrayList<ClassMember>(oldMembers.size());
    for (ClassMember m : oldMembers) {
      members.addAll(ext(m).interfaceMember(pr, classDecl));
    }

    // Add the $Proxy, $Impl, and $Static classes, as well as the $static field.
    ClassType superType = (ClassType) superClass.type();
    members.add(makeProxy(pr, superType));
    members.add(makeImpl(pr, superType));
    members.addAll(makeStatic(pr, superType));

    // Set the class body.
    return result.body(body.members(members));
  }

  /**
   * Returns the Proxy translation of the class declaration.
   */
  @SuppressWarnings("unchecked")
  private ClassDecl makeProxy(ProxyRewriter pr, ClassType superClass) {
    ClassDecl classDecl = node();

    // Rewrite the members.
    ClassBody body = classDecl.body();
    List<ClassMember> oldMembers = body.members();
    List<ClassMember> members = new ArrayList<ClassMember>(oldMembers.size());
    for (ClassMember m : oldMembers)
      members.addAll(ext(m).proxyMember(pr, classDecl));

    // Create proxy methods based on the class instances of this class and all
    // the interfaces it implements.
    members.addAll(makeProxyMethods(pr, classDecl.type()));

    // Add constructors.
    QQ qq = pr.qq();
    ClassMember implConstr =
        qq.parseMember("public $Proxy(" + classDecl.id() + ".$Impl impl) {"
            + "super(impl); }");
    ClassMember oidConstr =
        qq.parseMember("public $Proxy(fabric.client.Core core, long onum) {"
            + "super(core, onum); }");
    members.add(implConstr);
    members.add(oidConstr);

    // Create the class declaration.
    ClassDecl result =
        qq.parseDecl("public static class $Proxy extends "
            + superClass.fullName() + ".$Proxy implements %T {%LM}", classDecl
            .type(), members);
    return result.type(classDecl.type());
  }

  /**
   * Generates proxy methods for methods contained in the given class type and
   * all the interfaces it implements.
   */
  @SuppressWarnings("unchecked")
  private List<ClassMember> makeProxyMethods(ProxyRewriter pr, ClassType ct) {
    List<ClassMember> result = new ArrayList<ClassMember>();

    Queue<ClassType> toVisit = new LinkedList<ClassType>();
    Set<ClassType> visitedTypes = new HashSet<ClassType>();
    visitedTypes.add(pr.typeSystem().Object());
    visitedTypes.add(pr.typeSystem().FObject());

    // Maps method names to sets of formal argument types. This prevents us
    // from generating duplicate methods.
    Map<String, Set<List<Type>>> translatedInstances =
        new HashMap<String, Set<List<Type>>>();

    // First populate the above data structures with the super class and the
    // type hierarchy above that. The proxy's super class will already have
    // methods for those types.
    toVisit.add(ct.superType().toClass());
    while (!toVisit.isEmpty()) {
      ClassType type = toVisit.remove();
      if (visitedTypes.contains(type)) continue;
      visitedTypes.add(type);

      List<MethodInstance> methods = type.methods();
      for (MethodInstance mi : methods) {
        // We actually want to generate proxies for static methods even if they
        // exist already in a super type. This way, static method dispatching
        // will work correctly.
        if (mi.flags().isStatic()) continue;

        String name = mi.name();
        Set<List<Type>> formalTypes = translatedInstances.get(name);
        if (formalTypes == null) {
          formalTypes = new HashSet<List<Type>>();
          translatedInstances.put(name, formalTypes);
        }
        formalTypes.add(mi.formalTypes());
      }
    }

    // Now go through the class itself and the interfaces it implements.
    toVisit.add(ct);
    while (!toVisit.isEmpty()) {
      ClassType type = toVisit.remove();
      if (visitedTypes.contains(type)) continue;
      visitedTypes.add(type);

      List<MethodInstance> methods = type.methods();
      for (MethodInstance mi : methods) {
        String name = mi.name();
        List<Type> types = mi.formalTypes();

        // Ensure this isn't a duplicate method.
        Set<List<Type>> formalTypes = translatedInstances.get(name);
        if (formalTypes == null) {
          formalTypes = new HashSet<List<Type>>();
          translatedInstances.put(name, formalTypes);
        }

        if (formalTypes.contains(types)) continue;
        formalTypes.add(types);

        result.add(makeProxyMethod(pr, mi));
      }

      toVisit.addAll(type.interfaces());
    }

    return result;
  }

  /**
   * Generates a proxy method for the given method instance.
   */
  @SuppressWarnings("unchecked")
  private ClassMember makeProxyMethod(ProxyRewriter pr, MethodInstance mi) {
    FabricTypeSystem ts = pr.typeSystem();

    // The end result will be quasiquoted. Construct the quasiquoted string and
    // list of substitution arguments in tandem.
    StringBuffer methodDecl = new StringBuffer();
    List<Object> subst = new ArrayList<Object>();

    // Since the method will be implementing part of an interface, make the
    // method public and non-abstract.
    Flags flags = ProxyRewriter.toPublic(mi.flags()).clearAbstract();
    methodDecl.append(flags + " ");

    Type returnType = mi.returnType();
    if (returnType.isArray()) returnType = ts.toFArray(returnType.toArray());

    String name = mi.name();
    methodDecl.append("%T " + name + "(");
    subst.add(returnType);

    // Generate the formals list. While we're doing this, may as well generate
    // the args list too.
    StringBuffer args = new StringBuffer();
    List<Type> formalTypes = mi.formalTypes();
    int argCount = 1;
    for (Type t : formalTypes) {
      methodDecl.append((argCount == 1 ? "" : ", ") + "%T arg" + argCount);
      args.append((argCount == 1 ? "" : ", ") + "arg" + argCount);

      if (t.isArray()) t = ts.toFArray(t.toArray());
      subst.add(t);
      argCount++;
    }

    methodDecl.append(") { " + (returnType.isVoid() ? "" : "return "));

    // Figure out the call target.
    String implType = node().type().fullName() + ".$Impl";
    if (flags.isStatic())
      methodDecl.append(implType);
    else methodDecl.append("((" + implType + ") fetch())");

    // Call the delegate.
    methodDecl.append("." + name + "(" + args + "); }");

    QQ qq = pr.qq();
    return qq.parseMember(methodDecl.toString(), subst);
  }

  /**
   * Returns the Impl translation of the class declaration.
   */
  @SuppressWarnings("unchecked")
  private ClassDecl makeImpl(ProxyRewriter pr, ClassType superClass) {
    ClassDecl classDecl = node();
    ClassType classType = classDecl.type();

    // The Impl class will be public and static.
    Flags flags = ProxyRewriter.toPublic(classDecl.flags()).Static();

    // Rewrite the members.
    ClassBody body = classDecl.body();
    List<ClassMember> oldMembers = body.members();
    List<ClassMember> members = new ArrayList<ClassMember>(oldMembers.size());
    for (ClassMember m : oldMembers) {
      members.addAll(ext(m).implMember(pr, classDecl));
    }

    // Create and the $makeProxy method.
    QQ qq = pr.qq();
    ClassMember makeProxyDecl =
        qq.parseMember("protected fabric.lang.Object.$Proxy $makeProxy() {"
            + "return new " + classType.fullName() + ".$Proxy(this); }");
    members.add(makeProxyDecl);

    // Create the class declaration.
    ClassDecl result =
        qq.parseDecl(flags + " class $Impl extends " + superClass.fullName()
            + ".$Impl implements %T {%LM}", classType, members);
    return result.type(classDecl.type());
  }

  /**
   * Produces the $Static interface declaration for the static non-final fields.
   */
  @SuppressWarnings("unchecked")
  private List<ClassMember> makeStatic(ProxyRewriter pr, ClassType superClass) {
    ClassDecl classDecl = node();
    ClassType classType = classDecl.type();

    // Rewrite the members.
    ClassBody body = classDecl.body();
    List<ClassMember> oldMembers = body.members();
    List<ClassMember> interfaceMembers =
        new ArrayList<ClassMember>(oldMembers.size());
    List<ClassMember> proxyMembers =
        new ArrayList<ClassMember>(oldMembers.size());
    List<ClassMember> implMembers =
        new ArrayList<ClassMember>(oldMembers.size());

    for (ClassMember m : oldMembers) {
      interfaceMembers.addAll(ext(m).staticInterfaceMember(pr, classDecl));
      proxyMembers.addAll(ext(m).staticProxyMember(pr, classDecl));
      implMembers.addAll(ext(m).staticImplMember(pr, classDecl));
    }

    // Create the proxy constructor declarations and add them to the list of
    // static proxy members.
    QQ qq = pr.qq();
    ClassMember proxyConstructorDecl =
        qq.parseMember("public $Proxy(" + classType.fullName()
            + ".$Static.$Impl impl) { super(impl); }");
    proxyMembers.add(proxyConstructorDecl);
    proxyConstructorDecl =
        qq.parseMember("public $Proxy(fabric.client.Core core, long onum) {"
            + "super(core, onum); }");
    proxyMembers.add(proxyConstructorDecl);

    // Create the proxy declaration and add it to the list of interface members.
    ClassDecl proxyDecl =
        qq.parseDecl("class $Proxy extends " + superClass.fullName()
            + ".$Static.$Proxy implements " + classType.fullName()
            + ".$Static {%LM}", (Object) proxyMembers);
    interfaceMembers.add(proxyDecl);

    // Create the impl constructor declarations and add them to the list of
    // static impl members.
    ClassMember implConstructorDecl =
        qq.parseMember("public $Impl(fabric.client.Core core) "
            + "throws fabric.client.UnreachableCoreException {"
            + "super(core); }");
    implMembers.add(implConstructorDecl);
    implConstructorDecl =
        qq.parseMember("public $Impl(fabric.client.Core core, "
            + "fabric.common.Policy policy) "
            + "throws fabric.client.UnreachableCoreException {"
            + "super(core, policy); }");
    implMembers.add(implConstructorDecl);

    // Create the $makeProxy method declaration and add it to the list of static
    // impl members.
    ClassMember makeProxyDecl =
        qq.parseMember("protected fabric.lang.Object.$Proxy "
            + "$makeProxy() { return new " + classType.fullName()
            + ".$Static.$Proxy(this); }");
    implMembers.add(makeProxyDecl);

    // Create the impl declaration and add it to the list of interface members.
    ClassDecl implDecl =
        qq.parseDecl("class $Impl extends " + superClass.fullName()
            + ".$Static.$Impl implements " + classType.fullName()
            + ".$Static {%LM}", (Object) implMembers);
    interfaceMembers.add(implDecl);

    // Create the interface declaration.
    ClassDecl interfaceDecl =
        qq.parseDecl("interface $Static extends " + superClass.fullName()
            + ".$Static {%LM}", (Object) interfaceMembers);

    // Create the field declaration for $static.
    // TODO Where should the static object be located when it's created?
    FieldDecl fieldDecl =
        (FieldDecl) qq.parseMember(classType.fullName()
            + ".$Static $static = new " + classType.fullName()
            + ".$Static.$Impl(fabric.client.Client.getClient().getCore(0));");

    List<ClassMember> result = new ArrayList<ClassMember>(2);
    result.add(interfaceDecl);
    result.add(fieldDecl);
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fabric.extension.ClassMemberExt#interfaceMember(fabric.visit.ProxyRewriter,
   *      polyglot.ast.ClassDecl)
   */
  @Override
  public List<ClassMember> interfaceMember(ProxyRewriter pr, ClassDecl parent) {
    return Collections.singletonList((ClassMember) node());
  }

  /*
   * (non-Javadoc)
   * 
   * @see polyglot.ast.Ext_c#node()
   */
  @Override
  public ClassDecl node() {
    return (ClassDecl) super.node();
  }

  private ClassMemberExt ext(ClassMember m) {
    return (ClassMemberExt) m.ext();
  }
}
