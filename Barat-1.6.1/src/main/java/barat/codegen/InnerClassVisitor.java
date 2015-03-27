package barat.codegen;

import barat.*;
import barat.collections.*;
import barat.reflect.*;
import barat.parser.*;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import org.apache.bcel.Constants;
import java.io.*;
import java.util.*;

/**
 * Before the actual class generator is executed we need to gather informations
 * about any inner class and how it fields are accessed.
 *
 * @version $Id: InnerClassVisitor.java,v 1.3 2002/01/28 10:48:35 dahm Exp $
 * @author  <A HREF="http://www.inf.fu-berlin.de/~dahm">M. Dahm</A>
 */
public class InnerClassVisitor extends barat.DescendingVisitor implements Constants {
  public static final String OUTER_THIS  = "this$0";
  public static final String VAR_PREFIX  = "val$";
  public static final String ACC_PREFIX  = "access$";
  public static final String CALL_PREFIX = "call$";

  /** The outer class contains inner class, so that they both have to define
   * an InnerClasses attribute. But since they may be multiple inner classes we have
   * to gather this information before actually creating the attribute.
   *
   * This is then evaluated in the add_inner_attribute code chunk.
   */
  private static void addInnerAttribute(AUserType outer, AUserType inner, boolean anon) {
    String   outer_class_name = CodeGenerator.implementationNameOf(outer); 
    String   inner_class_name = CodeGenerator.implementationNameOf(inner);
    int      index            = inner_class_name.lastIndexOf('$');
    String   name             = inner_class_name.substring(index + 1);

    // Converted to String, will be reconverted below
    String   flags            = Integer.toString(Conversion.getAccessFlags(inner));
    String[] attr             = { inner_class_name,
				  anon? null : outer_class_name,
				  anon? null : name, flags };

    CollectionAttribute.add(outer, attr);
    CollectionAttribute.add(inner, attr);
  }

  /////// Define some commonly used Code suspensions /////////

  /** When the class has been traversed add an InnerClasses attribute to it if necessary.
   */
  private static CodeSuspension add_inner_attribute = new CodeSuspension() {
    public void execute(ClassGenerator clazz, barat.Node node) {
      CollectionAttribute ca = CollectionAttribute.collectionOf(node);
      
      if(ca != null) { // Any InnerClass attributes present?
	ClassGen        cg            = clazz.getClassGenerator();
	ConstantPoolGen cp            = cg.getConstantPool();
	Object[]        objs          = ca.toArray(); // String[] in fact
	int             size          = objs.length;
	String[][]      inner_classes = new String[size][];
	InnerClass[]    classes       = new InnerClass[size];

	System.arraycopy(objs, 0, inner_classes, 0, size);

	int attr_name = cp.addUtf8("InnerClasses");

	for(int i=0; i < size; i++) {
	  String inner_class_name = inner_classes[i][0];
	  String outer_class_name = inner_classes[i][1];
	  String name             = inner_classes[i][2];
	  int    flags            = 0;

	  try {
	    flags = Integer.parseInt(inner_classes[i][3]); // Reconvert, s.a.
	  } catch(NumberFormatException e) {}

	  classes[i] = new InnerClass(cp.addClass(inner_class_name),
				      outer_class_name == null? 0 :
				      cp.addClass(outer_class_name),
				      name == null? 0 : cp.addUtf8(name),
				      flags);
	}

	cg.addAttribute(new InnerClasses(attr_name, 2 + 8 * size,
					 classes, cp.getConstantPool()));
      }
    }
  };

  /** Non-static inner classes need reference to outer class to access its fields.
   */
  private static CodeSuspension add_this_field = new CodeSuspension() {
    public void execute(ClassGenerator clazz, barat.Node node) {
      ClassGen        cg         = clazz.getClassGenerator();
      AUserType       outer      = (AUserType)node.containing(AUserType.class);
      String          outer_name = CodeGenerator.implementationNameOf(outer);
      ConstantPoolGen cp         = cg.getConstantPool();

      // Field is not private, because nested inner classes may want to access it
      FieldGen        fg         = new FieldGen(0, new ObjectType(outer_name),
						OUTER_THIS, cp);
      cg.addField(fg.getField());
    }
  };

  /** Add parameters to constructor of inner member class, i.e. reference to outer this
   * and (final) local variables. Store them in appropriate instance fields.
   */
  private static CodeSuspension add_constructor_args = new CodeSuspension() {
    public void execute(ClassGenerator clazz, barat.Node node) { 
      CollectionAttribute ca      = CollectionAttribute.collectionOf(node);
      
      if(ca == null)
        return;

      ClassGen            cg      = clazz.getClassGenerator();
      MethodGen           mg      = clazz.getMethod();
      ConstantPoolGen     cp      = cg.getConstantPool();
      InstructionList     il      = clazz.getInstructionList();
      Object[]            objs    = ca.toArray();
      int                 size    = objs.length;
      InstructionFactory  factory = clazz.getFactory();

      // Get space for extra args
      Type[] old_args  = mg.getArgumentTypes();
      int    args_size = old_args.length;
      Type[] args      = new Type[args_size + size];
      System.arraycopy(old_args, 0, args, 0, args_size);
      
      for(int i=0; i < size; i++) {
	Type   type;
	String name;
	
	// Hack warning: Distinguish between outer this (String) and local variables
	if(objs[i] instanceof String) { // this$0 arg
	  name = OUTER_THIS;
	  type = new ObjectType((String)objs[i]);
	} else { // local variable
	  AVariable lv = (AVariable)objs[i];
	  
	  name = VAR_PREFIX + lv.getName();
	  type = Conversion.getType(lv.getType());
	}

	int slot = mg.addLocalVariable(name, type, null, null).getIndex();
	args[args_size++] = type;

	// Add code to execute
	InstructionList code = new InstructionList();
	code.append(new ALOAD(0));
	code.append(factory.createLoad(type, slot));
	
	int index = cp.addFieldref(CodeGenerator.
				   implementationNameOf(node.containingClass()),
				   name, type.getSignature());
	
	code.append(new PUTFIELD(index));

	clazz.increaseStack(type.getSize()); // Adjust stack size

	InstructionHandle ih = il.getStart();
	while(!(ih.getInstruction() instanceof INVOKESPECIAL))
  	  ih = ih.getNext();
	il.append(ih, code); // Put code right after super() or this()
      
	mg.setArgumentTypes(args);
      }     
    }
  };

  /** Add arguments to constructor call of inner class, i.e. add reference to outer class
   * and (final) local variables.
   */
  private static CodeSuspension add_allocation_args = new CodeSuspension() {
    public void execute(ClassGenerator clazz, barat.Node node) { 
      Constructor         c  = ClassGenerator.getCalledConstructor((ObjectAllocation)node);
      CollectionAttribute ca = CollectionAttribute.collectionOf(node);

      if(ca == null)
	return;

      ClassGen            cg      = clazz.getClassGenerator();
      ConstantPoolGen     cp      = cg.getConstantPool();
      InstructionList     il      = clazz.getInstructionList();
      InstructionHandle   ih      = il.getEnd(); // Last instruction is invokespecial
      Object[]            objs    = ca.toArray();
      int                 size    = objs.length;
      InstructionFactory  factory = clazz.getFactory();
      MethodObject        mo      = new MethodObject(null, c);

      // Get space for extra args
      Type[] old_args  = mo.arg_types;
      int    args_size = old_args.length;
      Type[] args      = new Type[args_size + size];
      System.arraycopy(old_args, 0, args, 0, args_size);

      for(int i=0; i < size; i++) {
	Type type;

	// Same hack, s.a.
	if(objs[i] instanceof String) { // this$0 arg
	  type = new ObjectType((String)objs[i]);
	  il.insert(ih, new ALOAD(0));
	} else { // local variable
	  LocalVariableGen lg = clazz.local_vars.get((AVariable)objs[i]);
	  type = lg.getType();
	  il.insert(ih, factory.createLoad(type, lg.getIndex()));
	}

	clazz.increaseStack(type.getSize() + 1); // Adjust stack size

	args[args_size++] = type;
      }

      ih.setInstruction(factory.createInvoke(mo.class_name, "<init>", Type.VOID,
					     args, INVOKESPECIAL));
    }
  };

  private static final boolean hasStaticContext(barat.Node n) {
    AMethod method = n.containingMethod();

    if(method == null) { // static block or field initializer
      barat.reflect.Field f = (barat.reflect.Field)n.containing(barat.reflect.Field.class);

      return (f == null)? true : f.isStatic();
    }
    else
      return method.isStatic();
  }


  public void visitObjectAllocation(ObjectAllocation o) {
    CollectionAttribute ca = CollectionAttribute.collectionOf(ClassGenerator.getCalledConstructor(o));

    if(ca != null) {
      CollectionAttribute.addAll(o, ca); // copy all values
      CodeHook.addToPostHook(o, add_allocation_args);
    }
  }

  public void visitAnonymousAllocation(AnonymousAllocation a) {
    AUserType           outer      = (AUserType)a.containing(AUserType.class);
    String              outer_name = CodeGenerator.implementationNameOf(outer);
    barat.reflect.Class inner      = a.getAnonymousClass();

    addInnerAttribute(outer, inner, true);

    Constructor c = inner.getConstructors().get(0);
    CodeHook.addToPostHook(c, add_constructor_args); // Added only once, of course

    if(!hasStaticContext(a)) { // Needs reference to outer class, e.g. to access fields
      CodeHook.addToPostHook(inner, add_this_field);
      CollectionAttribute.add(c, outer_name);
    }

    inner.accept(this);
    visitObjectAllocation(a);
  }

  public void visitUserTypeDeclaration(UserTypeDeclaration d) {
    AUserType outer      = d.containingClass();
    String    outer_name = CodeGenerator.implementationNameOf(outer);
    AUserType inner      = d.getUserType();

    addInnerAttribute(outer, inner, false);

    if(inner instanceof barat.reflect.Class) {
      barat.reflect.Class cl = (barat.reflect.Class)inner;

      if(!hasStaticContext(d)) // Needs reference to outer class, e.g. to access fields
	CodeHook.addToPostHook(cl, add_this_field);

      for(ConstructorIterator j=cl.getConstructors().iterator(); j.hasNext();) {
	Constructor c = j.next();

	CodeHook.addToPostHook(c, add_constructor_args); // Added only once, of course

	if(!hasStaticContext(d))
	  CollectionAttribute.add(c, outer_name);
      }
    }

    inner.accept(this);
  }

  public void visitInterface(barat.reflect.Interface inter) {
    visitAUserType(inter);
    super.visitInterface(inter);
  }

  public void visitClass(barat.reflect.Class clazz) {
    visitAUserType(clazz);
    super.visitClass(clazz);
  }

  private void visitAUserType(AUserType outer) {
    String outer_name = CodeGenerator.implementationNameOf(outer);

    for(InterfaceIterator i=outer.getNestedInterfaces().iterator(); i.hasNext();) {
      Interface inner = i.next();

      addInnerAttribute(outer, inner, false);
      //inner.accept(this);
    }

    for(ClassIterator i=outer.getNestedClasses().iterator(); i.hasNext();) {
      barat.reflect.Class inner = i.next();

      addInnerAttribute(outer, inner, false);
      //inner.accept(this);

      if(!inner.isStatic()) { // Needs reference to outer class, e.g. to access fields
	CodeHook.addToPostHook(inner, add_this_field);

	// Add new parameter to constructors!
	for(ConstructorIterator j=inner.getConstructors().iterator(); j.hasNext();) {
	  Constructor c = j.next();
	  CollectionAttribute.add(c, outer_name);
	  CodeHook.addToPostHook(c, add_constructor_args); // Added only once, of course
	}
      }
    }

    if(CollectionAttribute.collectionOf(outer) != null) // Any InnerClass attributes present?
      CodeHook.addToPostHook(outer, add_inner_attribute);
  }

  /**
   * Inner classes may refer to final variables of the surrounding method. These
   * accesses need to be transformed to field accesses, where the fields belongs
   * to the inner class and is initialized in its constructor.
   *
   * Code transformation itself occurs in visitVariableAccess, overridden in
   * InnerClassGenerator.
   */
  public void visitVariableAccess(VariableAccess v) {
    // Access to variable from inner class method/field?
    final AVariable     lv = v.getVariable(); 
    final barat.reflect.Class cl = v.containingClass();

    if(lv.containingClass() != cl) { // Access to outer class variable
      // Add new parameter to constructors!
      for(ConstructorIterator j=cl.getConstructors().iterator(); j.hasNext();) {
	Constructor c = j.next();
	CollectionAttribute.add(c, lv);
	CodeHook.addToPostHook(c, add_constructor_args);
      }

      // Add new field to clazz to hold value of local variable
      CodeHook.addToPostHook(cl, new CodeSuspension() {
	public void execute(ClassGenerator clazz, barat.Node node) {
	  String   name = VAR_PREFIX + lv.getName();
	  ClassGen cg   = clazz.getClassGenerator();

	  if(cg.containsField(name) == null) { // Already added?
            Type     type = Conversion.getType(lv.getType());
            FieldGen fg   = new FieldGen(ACC_PRIVATE,
					 type, name, cg.getConstantPool());
	    cg.addField(fg.getField());
	  }
	}
      });
    }
  }

  InstructionHandle last_ih; // Used above by the two code suspensions

  private static CodeSuspension add_read_method = new CodeSuspension() {
    public void execute(ClassGenerator clazz, barat.Node node) {
      barat.reflect.Field field = (barat.reflect.Field)node;
      clazz.getClassGenerator().addMethod(clazz.getFactory().
					  create_getMethod(field,
							   ACC_PREFIX + field.getName()));
    }
  };

  private static CodeSuspension add_write_method = new CodeSuspension() {
    public void execute(ClassGenerator clazz, barat.Node node) {
      barat.reflect.Field field = (barat.reflect.Field)node;
      clazz.getClassGenerator().addMethod(clazz.getFactory().
					  create_setMethod(field,
							   ACC_PREFIX + field.getName()));
    }
  };

  private static CodeSuspension redirect_read = new CodeSuspension() {
    public void execute(ClassGenerator clazz, barat.Node node) {
      InstructionHandle ih   = clazz.getInstructionList().getEnd();
      Instruction       inst = ih.getInstruction();

      if((inst instanceof GETFIELD) || (inst instanceof GETSTATIC) ) {
	barat.reflect.Field field      = ((AFieldAccess)node).getField();
	String              class_name = CodeGenerator.
	implementationNameOf(field.containingClass());
	InstructionFactory  factory    = clazz.getFactory();
	Type                type       = Conversion.getType(field.getType());

	ih.setInstruction(factory.createInvoke(class_name, ACC_PREFIX + field.getName(),
					       type, Type.NO_ARGS,
					       field.isStatic()? INVOKESTATIC :
					       INVOKEVIRTUAL));
      }
    }
  };

  private static CodeSuspension redirect_write = new CodeSuspension() {
    public void execute(ClassGenerator clazz, barat.Node node) {
      InstructionHandle ih   = clazz.getInstructionList().getEnd();
      Instruction       inst = ih.getInstruction();

      if((inst instanceof PUTFIELD) || (inst instanceof PUTSTATIC)) {
	barat.reflect.Field field      =
	((AFieldAccess)((Assignment)node).getLvalue()).getField();
	String              class_name = CodeGenerator.
	implementationNameOf(field.containingClass());
	InstructionFactory  factory    = clazz.getFactory();
	Type                type       = Conversion.getType(field.getType());

	ih.setInstruction(factory.createInvoke(class_name, ACC_PREFIX + field.getName(),
					       Type.VOID, new Type[] { type },
					       field.isStatic()? INVOKESTATIC :
					       INVOKEVIRTUAL));
      }
    }
  };

  /**
   * Similarly inner classes may access private fields and methods of the
   * outer class!
   */
  private void visitAFieldAccess(AFieldAccess a) {
    barat.reflect.Field field = a.getField();

    // Otherwise illegal access -> must be from inner class
    if(field.isPrivate() && (a.containingClass() != field.containingClass())) {
      if(ClassGenerator.isLHS(a)) {
	CodeHook.addToPostHook(field, add_write_method);
	CodeHook.addToPostHook(a.container() /*Assignment*/, redirect_write);
      } else {
	CodeHook.addToPostHook(field, add_read_method);
	CodeHook.addToPostHook(a, redirect_read);
      }
    }
  }

  public void visitInstanceFieldAccess(InstanceFieldAccess a) {
    visitAFieldAccess(a);
    super.visitInstanceFieldAccess(a);
  }

  public void visitStaticFieldAccess(StaticFieldAccess a) {
    visitAFieldAccess(a);
    super.visitStaticFieldAccess(a);
  }

  private static CodeSuspension add_access_method = new CodeSuspension() {
    public void execute(ClassGenerator clazz, barat.Node node) {
      AMethod method = (AMethod)node;

      clazz.getClassGenerator().addMethod(clazz.getFactory().
					  create_accessMethod(method,
							      clazz.getClassName(),
							      CALL_PREFIX +
							      method.getName()));
    }
  };

  private static CodeSuspension redirect_call = new CodeSuspension() {
    public void execute(ClassGenerator clazz, barat.Node node) {
      AMethod            method      = ((AMethodCall)node).getCalledMethod();
      MethodObject       m           = new MethodObject(null, method);
      InstructionHandle  ih          = clazz.getInstructionList().getEnd();
      InvokeInstruction  inst        = (InvokeInstruction)ih.getInstruction();
      InstructionFactory factory     = clazz.getFactory();

      m.name = CALL_PREFIX + method.getName();
      ih.setInstruction(factory.createInvoke(m, inst.getOpcode()));
    }
  };

  public void visitAMethodCall(AMethodCall a) {
    AMethod method = a.getCalledMethod();

    // Otherwise illegal access -> must be call from inner class
    if(method.isPrivate() && (a.containingClass() != method.containingClass())) {
      CodeHook.addToPostHook(method, add_access_method);
      CodeHook.addToPostHook(a, redirect_call);
    }
  }

  public void visitInstanceMethodCall(InstanceMethodCall call) {
    visitAMethodCall(call);
    super.visitInstanceMethodCall(call);
  }

  public void visitStaticMethodCall(StaticMethodCall call) {
    visitAMethodCall(call);
    super.visitStaticMethodCall(call);
  }

  public void visitConstructorCall(ConstructorCall call) {
    AMethod method = call.getCalledConstructor();

    if(method.isPrivate() & (call.containingClass() != method.containingClass()))
      throw new RuntimeException("Access to outer private constructor not implemented yet");

    super.visitConstructorCall(call);
  }

  /**
   * Explicit or implicit reference to `this'
   */
  public void visitThis(final This t) {
    final AUserType this_               = t.getThisClass();
    final barat.reflect.Class container = t.containingClass();

    if((this_ == null) || (this_ == container) || t.isSuper() ||
       ((this_ instanceof barat.reflect.Class) && 
	container.isSubclassOf((barat.reflect.Class)this_)))
      return; // Not reference to outer class

    // aload_0 already on stack
    CodeHook.addToPostHook(t, new CodeSuspension() {
      public void execute(ClassGenerator clazz, barat.Node node) {
	InstructionList     il = clazz.getInstructionList();
	barat.reflect.Class cl = container;
	ClassGen            cg = clazz.getClassGenerator();
      	ConstantPoolGen     cp = cg.getConstantPool();

	while(cl != this_) { // Loop until correct "this$0" is reached
	  barat.reflect.Class outer = cl.containingClass();
	  String inner_name = CodeGenerator.implementationNameOf(cl);
	  String outer_name = CodeGenerator.implementationNameOf(outer);
	  int    index      = cp.addFieldref(inner_name, InnerClassVisitor.OUTER_THIS,
					     new ObjectType(outer_name).getSignature());
	  
	  il.append(new GETFIELD(index));
	  cl = outer;
	}
      }
    });
  }
}
