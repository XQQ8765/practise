package barat;

import barat.*;
import barat.reflect.*;
import java.util.*;
import barat.collections.*;

/**
 * Find every class definition reachable from the given list of classes. When new
 * classes are discovered, they're added to the list. Thus the visitor traverse
 * the complete transitive hull reachable from the starting class.
 *
 * @version $Id: TraversingVisitor.java,v 1.1 2001/07/11 10:35:21 dahm Exp $
 * @author  <A HREF="http://www.inf.fu-berlin.de/~dahm">M. Dahm</A>
 */
public class TraversingVisitor extends DescendingVisitor {
  protected Repository repository;
  protected boolean    visit_binary;

  /**
   * @param repository initial list of classes, add newly found classes to
   * it. Binary classes will be added to the list, too, if they are
   * super classes.
   * @param visit_binary descend into binary classes, too?
   */
  public TraversingVisitor(Repository repository, boolean visit_binary) {
    this.repository   = repository;
    this.visit_binary = visit_binary;
  }

  /** Start traversal.
   */
  public void start() {
    for(Iterator i = repository.iterator(); i.hasNext();) {
      AUserType clazz = (AUserType)i.next();
      //System.out.println("Visiting: " + clazz.getName());
      clazz.accept(this);
    }

    repository.close(); // Take no more new classes into account
  }

  public void visitClass(barat.reflect.Class clazz) {
    barat.reflect.Class super_class = clazz.getSuperclass();

    repository.add(super_class);

    for(InterfaceIterator i=clazz.getImplementedInterfaces().iterator(); i.hasNext();)
      repository.add(i.next());

    if(!Repository.isBinaryClass(clazz) || visit_binary)
      super.visitClass(clazz);
  }

  public void visitInterface(Interface clazz) {                                
    for(InterfaceIterator i=clazz.getExtendedInterfaces().iterator(); i.hasNext();)
      repository.add(i.next());

    if(!Repository.isBinaryClass(clazz) || visit_binary)
       super.visitInterface(clazz);
  }

  public void visitObjectAllocation(ObjectAllocation a) {
    repository.add(a.getCalledConstructor().containingClass());
  }

  public void visitField(Field field) {
    visitType(field.getType());
  }

  public void visitInstanceFieldAccess(InstanceFieldAccess a) {
    repository.add((AUserType)a.getInstance().type());
    visitType(a.getField().getType());
    super.visitInstanceFieldAccess(a);
  }

  public void visitStaticFieldAccess(StaticFieldAccess a) {
    repository.add((AUserType)a.getField().containing(AUserType.class));
    visitType(a.getField().getType());
    super.visitStaticFieldAccess(a);
  }

  public void visitInstanceMethodCall(InstanceMethodCall call) {
    AType type = call.getInstance().type();

    if(!(type instanceof Array))
      repository.add((AUserType)type);

    super.visitInstanceMethodCall(call);
  }

  public void visitStaticMethodCall(StaticMethodCall call) {
    repository.add(call.getCalledMethod().containingClass());
    super.visitStaticMethodCall(call);
  }

  private static final boolean isPrimitive(AType t) {
    t = Repository.realType(t);
    return (t == null) || (t instanceof PrimitiveType);
  }

  private final void visitType(AType type) {
    if(!isPrimitive(type))
      repository.add(Barat.getUserType(Repository.realType(type).qualifiedName()));
  }

  public void visitMethod(AMethod method) {
    visitType(method.getResultType());

    for(ParameterIterator i = method.getParameters().iterator(); i.hasNext();)
      visitType(i.next().getType());
  }

  public void visitConcreteMethod(ConcreteMethod method) { // Forward to visitMethod
    visitMethod(method);
    method.getBody().accept(this);
  }

  public void visitAbstractMethod(AbstractMethod method) {
    visitMethod(method);
  }

  public void visitConstructor(Constructor c) {
    visitMethod(c);
    c.getBody().accept(this);
  }

  public void visitLocalVariable(LocalVariable v) {
    visitType(v.getType());
    acceptIfPresent(v.getInitializer());
  }
  
  public static void main(String[] args) {
    if(args.length == 0) {
      System.out.println("Usage: java barat.TraversingVisitor <fully qualified class names>");
      System.exit(1);
    }

    Repository  todo_list = new Repository(args); 

    System.out.println("Starting traversal ...");
    new TraversingVisitor(todo_list, false).start(); // Find all reachable classes
    
    System.out.println("Classes found: " + todo_list);
  }
}
