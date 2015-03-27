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
 * Generate code for a class or an interface.
 *
 * @version $Id: ClassGenerator.java,v 1.6 2002/01/28 10:48:35 dahm Exp $
 * @author  <A HREF="http://www.inf.fu-berlin.de/~dahm">M. Dahm</A>
 */
public class ClassGenerator extends barat.DescendingVisitor implements Constants {
  private static final int mask = 0xFFFF ^ ACC_PRIVATE ^ ACC_PROTECTED ^ ACC_STATIC;
  public static final barat.reflect.Class     OBJECT       = Barat.getObjectClass();

  protected ArrayList     init_hook    = new ArrayList();
  protected ArrayList     clinit_hook  = new ArrayList();
  protected StringIntHashTable added_fields = new StringIntHashTable(); // for .class expression
  protected InstructionList    il;
  protected MethodGen          mg;
  protected FieldGen           fg;
  protected int                max_stack;
  protected CodeGenerator      cg;
  protected ClassGen           clazz;
  protected ConstantPoolGen    cp;
  protected String             class_name;
  protected InstructionFactory factory;
  protected AUserType          cl;

  public ClassGenerator(CodeGenerator cg) {
    this.cg = cg;
  }

  /** Hooks have the possibility to change this flag. This way they
   * one can tell the generator to completely ignore nodes when
   * generating code.
   */
  public boolean generate_code = true;

  protected final boolean applyPreHook(barat.Node node) {
    CodeHook hook = CodeHook.preHookOf(node);

    generate_code = true;
    if(hook != null) {
      hook.execute(this, node);
      //System.out.println("pre:" + node + ":" + hook);
    }
    return generate_code;
  }

  protected final boolean applyPostHook(barat.Node node) {
    CodeHook hook = CodeHook.postHookOf(node);

    generate_code = true;
    if(hook != null) {
      hook.execute(this, node);
      //System.out.println("post:" + node + ":" + hook);
    }

    return generate_code;
  }

  /**
   * Generate class header for interface.
   */
  public void visitInterface(barat.reflect.Interface inter) {
    if(applyPreHook(inter)) {
      if(CodeGenerator.debug)
	System.out.println("***** Compiling interface " + inter.qualifiedName());
      
      StringArrayList list  = new StringArrayList();
      short           flags = (short)(Conversion.getAccessFlags(inter) | ACC_INTERFACE);
      flags      = (short)(flags & mask); // Clear any flags that may be set by inner classes
      class_name = implementationNameOf(inter);
      
      for(InterfaceIterator i=inter.getExtendedInterfaces().iterator(); i.hasNext();)
	list.add(implementationNameOf(i.next()));
      
      clazz   = new ClassGen(class_name, "java.lang.Object", cg.source_file,
			     flags, list.toArray());
      cp      = clazz.getConstantPool();
      cl      = inter;
      factory = new InstructionFactory(this); // Every class needs its own factory
      
      cg.class_map.put(class_name, this);
      
      for(FieldIterator i=inter.getFields().iterator(); i.hasNext();)
	i.next().accept(this);
      
      for(InterfaceIterator i=inter.getNestedInterfaces().iterator(); i.hasNext();)
	i.next().accept(cg.createInnerClassGenerator());
      
      for(ClassIterator i=inter.getNestedClasses().iterator(); i.hasNext();)
	i.next().accept(cg.createInnerClassGenerator());
      
      for(AbstractMethodIterator i=inter.getAbstractMethods().iterator(); i.hasNext();)
	i.next().accept(this);
    }

    applyPostHook(inter);
  }

  private void checkClass(barat.reflect.Class clazz) {
    if((clazz.getAbstractMethods().size() > 0) && !clazz.isAbstract())
      CodeGenerator.error("Class " + clazz + " contains abstract methods " +
			  clazz.getAbstractMethods() + ", but is not declared abstract.");

    if(!clazz.isAbstract()) {
      AbstractMethodList abs_methods = Repository.getAllAbstractMethods(clazz);

      HashSet abs_set = new HashSet(); // contains methods in canonical form

      for(AbstractMethodIterator i = abs_methods.iterator(); i.hasNext(); ) {
	AbstractMethod m = i.next();
	if(!m.isNative())
	  abs_set.add(canonForm(m));
      }

      ConcreteMethodList conc_methods = Repository.getAllConcreteMethods(clazz);

      HashSet conc_set = new HashSet(); // contains methods in canonical form

      for(ConcreteMethodIterator i = conc_methods.iterator(); i.hasNext(); )
	conc_set.add(canonForm(i.next()));

      abs_set.removeAll(conc_set); // Should be empty after that

      if(!abs_set.isEmpty()) {
	for(Iterator i = abs_set.iterator(); i.hasNext(); )
	  System.err.println(i.next() + " has not been implemented");

	CodeGenerator.error("Unimplemented methods in non-abstract class " + clazz);
      }
    }
  }

  private static String canonForm(AMethod method) {
    Type[] args = Conversion.getParameters(method.getParameters());
    Type   ret  = Conversion.getType(method.getResultType());

    StringBuffer buf = new StringBuffer(ret + " " + method.getName() + "(");

    for(int i=0; i < args.length; i++) {
      buf.append(args[i]);

      if(i < args.length - 1)
	buf.append(", ");
    }

    buf.append(")");

    return buf.toString();
  }

  /**
   * Generate class header for class.
   */
  public void visitClass(barat.reflect.Class cl) {
   if(applyPreHook(cl)) {
     checkClass(cl);

     if(CodeGenerator.debug)
       System.out.println("***** Compiling class " + cl.qualifiedName());
    
      StringArrayList list  = new StringArrayList();
      short           flags = (short)(Conversion.getAccessFlags(cl) | ACC_SUPER);
      
      flags      = (short)(flags & mask); // Clear any flags that may be set by inner classes
      class_name = implementationNameOf(cl);
     
      for(InterfaceIterator i=cl.getImplementedInterfaces().iterator(); i.hasNext();)
	list.add(implementationNameOf(i.next()));
      
      clazz   = new ClassGen(class_name,
			     implementationNameOf(cl.getSuperclass()),
			     cg.source_file, flags, list.toArray());
      cp      = clazz.getConstantPool();
      this.cl = cl;
      factory = new InstructionFactory(this);
      
      cg.class_map.put(class_name, this);

      for(FieldIterator i=cl.getFields().iterator(); i.hasNext();)
	i.next().accept(this);

      for(InterfaceIterator i=cl.getNestedInterfaces().iterator(); i.hasNext();)
	i.next().accept(cg.createInnerClassGenerator());
      
      for(ClassIterator i=cl.getNestedClasses().iterator(); i.hasNext();)
	i.next().accept(cg.createInnerClassGenerator());

      for(AbstractMethodIterator i=cl.getAbstractMethods().iterator(); i.hasNext();)
	i.next().accept(this);

      for(ConcreteMethodIterator i=cl.getConcreteMethods().iterator(); i.hasNext();)
	i.next().accept(this);

      for(ConstructorIterator i=cl.getConstructors().iterator(); i.hasNext();)
	i.next().accept(this);
    
      // Add hook for static { ... } blocks
      final BlockIterator bi = cl.getStaticInitializers().iterator();
      if(bi.hasNext()) {
	clinit_hook.add(new Suspension() {
	  public InstructionList getInstructionList() {
	    InstructionList il = new InstructionList();
	    
	    while(bi.hasNext())
	      factory.codeFor(bi.next(), il);
	    
	    return il;
	  }
	});
      }
    }

    applyPostHook(cl);
  }

  public ArrayList getStaticInitializers() {
    return clinit_hook;
  }

  public ArrayList getInitializers() {
    return init_hook;
  }

  public void visitUserTypeDeclaration(UserTypeDeclaration d) {
    if(applyPreHook(d))
      d.getUserType().accept(cg.createInnerClassGenerator());

    applyPostHook(d);
  }

  /**
   * Generate field entry and initialize it if possible, otherwise initialization has
   * will be performed in the &lt;init&gt; and &lt;clinit&gt; method, respectively.
   *
   * The method registers Suspension objects to be executed when creating the
   * methods mentioned above.
   *
   * @see Suspension
   */
  public void visitField(final barat.reflect.Field field) {
    if(applyPreHook(field)) {
      final Type   type  = Conversion.getType(field.getType());
      final String name  = field.getName();
      int          flags = Conversion.getAccessFlags(field);
    
      if(isInterface()) {
	if(field.isProtected() || field.isPrivate())
	  CodeGenerator.error("Illegal modifier for " + name + " in interface class.");
	
	flags = ACC_PUBLIC | ACC_FINAL | ACC_STATIC; // Default flags
      }
      
      fg = new FieldGen(flags, type, name, cp);

      final AExpression expr = field.getInitializer();
      
      if(expr == null) {
	if(isInterface())
	  CodeGenerator.error("Field has no initializer in interface class: " + name);
      } else {
	final Literal l = factory.simplifyExpr(expr);
	
	if((flags & ACC_STATIC) != 0) {
	  Object value;
	  if(((flags & ACC_FINAL) != 0) &&
	     (l != null) && ((value=l.constantValue()) != null)) {
	    if(value instanceof String)
	      fg.setInitValue((String)value);
	    else if(value instanceof Character)
	      fg.setInitValue(((Character)value).charValue());
	    else if(value instanceof Boolean)
	      fg.setInitValue(((Boolean)value).booleanValue());
	    else if(value instanceof Integer)
	      fg.setInitValue(((Integer)value).intValue());
	    else if(value instanceof Long)
	      fg.setInitValue(((Long)value).longValue());
	    else if(value instanceof Double)
	      fg.setInitValue(((Double)value).doubleValue());
	    else if(value instanceof Float)
	      fg.setInitValue(((Float)value).floatValue());
	    else if(value instanceof Short)
	      fg.setInitValue(((Short)value).shortValue());
	    else if(value instanceof Byte)
	      fg.setInitValue(((Byte)value).byteValue());
	    else
	      throw new RuntimeException("Oops: What literal is this? " + value);
	  } else { // Generate code in <clinit>
	    clinit_hook.add(new Suspension() {
	      public InstructionList getInstructionList() {
		int             i  = factory.getFieldIndex(field);
		InstructionList il = factory.codeFor(expr);
		
		checkForCast(expr.type(), field.getType(), il);
		il.append(new PUTSTATIC(i));	      
		decreaseStack(type.getSize());
		
		return il;
	      }
	    });
	  }
	} else { // instance field, generate code in <init>
	  final AExpression expr2 = (l != null)? l : expr;
	  
	  init_hook.add(new Suspension() {
	    public InstructionList getInstructionList() {
	      int             i  = factory.getFieldIndex(field);
	      InstructionList il = new InstructionList();
	      
	      il.append(factory.createThis()); // referenced object is always `this'
	      il.append(factory.codeFor(expr2));
	      checkForCast(expr2.type(), field.getType(), il);
	      il.append(new PUTFIELD(i));
	      decreaseStack(type.getSize() + 1); // size + `this' popped
	      
	      return il;
	    }
	  });
	}
      }
    }

    applyPostHook(field);

    if(fg != null)
      clazz.addField(fg.getField());

    fg = null; // Just in case
  }

  //
  //*************** Array and object allocation ***************
  //

  /**
   * Generate code for new int[12], or new String[12][], e.g.
   */
  public void visitArrayAllocation(ArrayAllocation a) {
    if(applyPreHook(a)) {
      ArrayType array     = (ArrayType)Conversion.getType(a.type());    
      int       dim       = array.getDimensions();
      Type      type      = Conversion.getType(((Array)a.type()).getElementType());
      int       free_dims = a.freeDimensions();

      // Push size arguments on stack, if any
      AExpressionList list = a.getArguments();
      short           args = (short)list.size(); // Arg may be < dim, e.g. new int[1][]
      il.append(factory.codeFor(list));

      /* If there is an initializer, we compute the size of it and push it
       * onto the stack, too
       */
      ArrayInitializer initializer = a.getInitializer();
      if((free_dims > 0) && (initializer != null)) { // has { ... } initializer
	il.append(new PUSH(cp, initializer.getArguments().size()));
	increaseStack();
	
	// If so the array must be completely initialized
	CodeGenerator.do_assert(args == 0, "args == 0");
	
	args = 1; // Size of initializer pushed on stack
	free_dims--;
      }
    
      int index;
      
      if(free_dims + args == 1) {
	switch(type.getType()) {
	case T_ARRAY: // Could be optimized to MULTIANEWARRAY, if type is an array, too
	  index = cp.addArrayClass((ArrayType)type);
	  il.append(new ANEWARRAY(index));
	  break;
	  
	case T_REFERENCE:
	  index = cp.addClass((ObjectType)type);
	  il.append(new ANEWARRAY(index));
	  break;
	  
	default: // Basic type such as int
	  il.append(new NEWARRAY(type.getType()));
	  break;
	}
      } else { // multi-dimensional array, no initializer, but new[][] ...
	CodeGenerator.do_assert(args > 0, "args > 0");
	index = cp.addArrayClass(array);
	il.append(new MULTIANEWARRAY(index, args));
      }
      
      decreaseStack(args - 1); // args popped, new reference pushed

      acceptIfPresent(initializer);
    }

    applyPostHook(a);
  }

  /**
   * int[] array = { 1, 2, 3 };
   */
  public void visitArrayInitializer(ArrayInitializer a) {
    if(applyPreHook(a)) {
      AExpressionIterator iterator = a.getArguments().iterator();
      Type                dest_type=((ArrayType)Conversion.getType(a.type())).getElementType();
      
      for(int i=0; iterator.hasNext(); i++) {
	il.append(InstructionConstants.DUP);       // Dup array reference on stack
	il.append(new PUSH(cp, i)); // Push index to be written to
	increaseStack(2);
	
	AExpression expr     = iterator.next();
	Type        src_type = Conversion.getType(expr.type());
	
	expr.accept(this); // Evaluate expression
	checkForCast(src_type, dest_type, il);
	
	il.append(factory.createArrayStore(dest_type)); // store in a[i]
      }
    }

    applyPostHook(a);
  }

  // Hack, because getCalledConstructor() points to wrong class in case of anonymous allocation
  public static final Constructor getCalledConstructor(ObjectAllocation o) {
    if(o instanceof AnonymousAllocation) {
      return ((AnonymousAllocation)o).getAnonymousClass().getConstructors().get(0);
    } else
      return o.getCalledConstructor();
  }

  /**
   * Generate code for new Integer(12), e.g.
   */
  public void visitObjectAllocation(ObjectAllocation o) {
    if(applyPreHook(o)) {
      AMethod      m  = getCalledConstructor(o);
      MethodObject mo = new MethodObject(this, m);

      int index = cp.addClass(mo.class_name);
      
      il.append(new NEW(index));
      il.append(InstructionConstants.DUP); // reference needed two times on stack
      increaseStack(2);
      
      // Push arguments for constructor, if any
      checkMethodParameters(o.getArguments(), m.getParameters());
      
      il.append(factory.createInvoke(mo.class_name, "<init>", Type.VOID, mo.arg_types,
				     INVOKESPECIAL));

    }

    applyPostHook(o);
  }

  public void visitAnonymousAllocation(AnonymousAllocation a) {
    //applyPreHook(a);
    a.getAnonymousClass().accept(cg.createInnerClassGenerator());
    visitObjectAllocation(a);
    //applyPostHook(a);
  }

  //
  //*************** Access to fields/variables/arrays ***************
  //

  private SuspensionStack assignment_stack = new SuspensionStack();
  private SuspensionStack dup_stack        = new SuspensionStack();

  /**
   * public static int x;
   *
   * x = 4711; // write access
   *
   * y = x; // read access
   */
  public void visitStaticFieldAccess(StaticFieldAccess a) {
    if(applyPreHook(a)) {
      final barat.reflect.Field field = a.getField();
      final int                 size  = Conversion.getType(field.getType()).getSize();
      
      /* Variable being written, i.e. called via getLValue.accept(), s.b. visitAssignment
       */
      if(isLHS(a)) {
	// in nested assignments we need code to duplicate the rhs
	dup_stack.push(new Suspension() {
	  public InstructionList getInstructionList() {
	    return new InstructionList(factory.createDup(size));
	  }
	});
	
	// store instruction
	assignment_stack.push(new Suspension() {
	  public InstructionList getInstructionList() {
	    decreaseStack(size);
	    return new InstructionList(new PUTSTATIC(factory.getFieldIndex(field)));
	  }
	});
      } else { // variable just being read
	AExpression expr      = field.getInitializer();
	boolean     propagate = (expr != null) &&
	  (field.isFinal() || field.containingClass() == null) && // interface fields are always final
	  ConstantAnalysis.isConstant(expr);

	if(propagate) {	
	  Object obj = null;

	  // Workaround, because Barat can not propagate all values yet
	  try {
	    obj = ConstantAnalysis.evaluate(expr);
	  } catch(RuntimeException e) {
	    System.err.println("Warning: " + e.getMessage());
	  }
	  
	  if(obj instanceof Number)
	    il.append(new PUSH(cp, (Number)obj));
	  else if(obj instanceof Character)
	    il.append(new PUSH(cp, (Character)obj));
	  else if(obj instanceof Boolean)
	    il.append(new PUSH(cp, (Boolean)obj));
	  else if(obj instanceof String)
	    il.append(new PUSH(cp, (String)obj));
	  else
	    propagate = false;
	}
	
	if(!propagate)
	  il.append(new GETSTATIC(factory.getFieldIndex(field)));

	increaseStack(size);
      }
    }

    applyPostHook(a);
  }

  /**
   * private double eps=0.00081;
   *
   * like visitStaticFieldAccess
   */
  public void visitInstanceFieldAccess(InstanceFieldAccess a) {
    if(applyPreHook(a)) {
      barat.reflect.Field field = a.getField();
      final int index           = factory.getFieldIndex(field);
      final int size            = Conversion.getType(field.getType()).getSize();

      a.getInstance().accept(this); // push object reference
      
      /* Variable being written, i.e. called via getLValue.accept(), s.b. visitAssignment
       */
      if(isLHS(a)) {
	// in nested assignments we need code to duplicate the rhs
	dup_stack.push(new Suspension() {
	  public InstructionList getInstructionList() {
	    /* Object reference is on top of stack, thus put it down by two
	     */
	    return new InstructionList(factory.createDup_1(size));
	  }
	});

	// store instruction
	assignment_stack.push(new Suspension() {
	  public InstructionList getInstructionList() {
	    decreaseStack(size + 1); // size + obj reference popped
	    return new InstructionList(new PUTFIELD(index));
	  }
	});
      } else {
	il.append(new GETFIELD(index));
	increaseStack(size - 1); // size pushed, obj reference popped
      }
    }

    applyPostHook(a);
  }

  /* Map AVariable objects to LocalVariableGen objects
   */
  protected VariableEnvironment local_vars;

  /**
   * { int i = 0; ... }
   */
  public void visitLocalVariable(barat.reflect.LocalVariable lv) {
    if(applyPreHook(lv)) {
      LocalVariableGen lg = mg.addLocalVariable(lv.getName(),
						Conversion.getType(lv.getType()),
						null,
						null);
      local_vars.put(lv, lg, il.append(InstructionConstants.NOP));
      
      AExpression expr = lv.getInitializer();
      
      if(expr != null) {
	expr.accept(this);
	checkForCast(expr.type(), lv.getType(), il);
	il.append(factory.createStore(lg.getType(), lg.getIndex()));
      }
    }

    applyPostHook(lv);
  }
  
  /**
   * Like visitInstanceFieldAccess, for local variables.
   */
  public void visitVariableAccess(VariableAccess v) {
    if(applyPreHook(v)) {
      final LocalVariableGen lg = local_vars.get(v.getVariable());

      CodeGenerator.do_assert(lg != null, "Variable not declared: " + v);
      
      /* Variable being written, i.e. called via getLValue.accept(), s.b. visitAssignment
       */
      if(isLHS(v)) {
	// in nested assignments we need code to duplicate the rhs
	dup_stack.push(new Suspension() {
	  public InstructionList getInstructionList() {
	    return new InstructionList(factory.createDup(lg.getType().getSize()));
	  }
	});
	
	// store instruction
	assignment_stack.push(new Suspension() {
	  public InstructionList getInstructionList() {
	    return new InstructionList(factory.createStore(lg.getType(), lg.getIndex()));
	  }
	});
      } else // variable just being read
	il.append(factory.createLoad(lg.getType(), lg.getIndex()));
    }

    applyPostHook(v);
  }

  /**
   * a[i] = b[i];
   */
  public void visitArrayAccess(ArrayAccess a)  {
    if(applyPreHook(a)) {
      AExpression expr = a.getArray();
      final Type  type = ((ArrayType)Conversion.getType(expr.type())).getElementType();
      
      expr.accept(this);
      a.getIndex().accept(this);
      
      /* Variable being written, i.e. called via getLValue.accept(), s.b. visitAssignment
       */
      if(isLHS(a)) {
	// in nested assignments we need code to duplicate the rhs
	dup_stack.push(new Suspension() {
	  public InstructionList getInstructionList() {
	    return new InstructionList(factory.createDup_2(type.getSize()));
	  }
	});
	
	// store instruction
	assignment_stack.push(new Suspension() {
	  public InstructionList getInstructionList() {
	    return new InstructionList(factory.createArrayStore(type));
	  }
	});
      } else // variable just being read
	il.append(factory.createArrayLoad(type));
    }

    applyPostHook(a);
  }

  /**
   * LHS code has to be produced like this, e.g. for instance fields:
   * Visit(lhs) -> Push object reference on stack, visit(rhs), putfield.
   * Since saving to given location must be split in two parts, the putfield
   * part of code is put into a Suspension object which is executed after the rhs
   * code has been created (see visitXXXAccess() methods).
   *
   * For nested assignments like a = b = 12, b is written AND read, so before writing 12
   * to b, the topmost stack element is duplicated and stored in a, too. Code for
   * duplicating the rhs is produced in the corresponding visitXXXAccess methods.
   */
  public void visitAssignment(Assignment a) {
    if(applyPreHook(a)) {
      String      op     = a.operator();
      ALValue     lvalue = a.getLvalue();
      AExpression expr   = a.getOperand();
      Type        type   = Conversion.getType(lvalue.type());

      /* Check for *=, +=, ...
       * Transform a *= b to a = (a * b);
       */
      op = op.equals("=")? null : op.substring(0, 1);
      
      // special case: += for strings
      boolean is_str = isString(type);
      
      lvalue.accept(this);
      
      if(op != null) { // +=
	boolean var_access = false;

	if(lvalue instanceof ArrayAccess) // copy reference + index
	  il.append(factory.createDup(2));
	else if(lvalue instanceof InstanceFieldAccess) // copy reference
	  il.append(factory.createDup(1));
	else
	  var_access = true;

	if(is_str) {
	  if(!var_access)
	    il.append(factory.createLoadAccess(lvalue));

	  int index = cp.addClass("java.lang.StringBuffer");
	  
	  il.append(new NEW(index));

	  if(!var_access) {
	    il.append(InstructionConstants.DUP_X1); // behind instance field value
	    il.append(InstructionConstants.SWAP); // instance field value on top
	  } else
	    il.append(InstructionConstants.DUP);
	  increaseStack(2);
	}

	if(var_access || !is_str)
	  il.append(factory.createLoadAccess(lvalue));
	
	if(is_str) { // store string into buffer
	  il.append(factory.createInvoke("java.lang.StringBuffer", "<init>", Type.VOID,
					 new Type[] { Type.STRING }, INVOKESPECIAL));
	  decreaseStack(2);
	}
      }
      
      expr.accept(this);
      
      if(op != null) { // +=
	if(is_str) {
	  il.append(factory.createAppend(Conversion.getType(expr.type())));
	  il.append(factory.createInvoke("java.lang.StringBuffer", "toString",
					 Type.STRING, Type.NO_ARGS, INVOKEVIRTUAL));
	} else {
	  checkForCast(expr.type(), lvalue.type(), il);
	  il.append(factory.createBinaryOperation(op, type));
	}
      }
      
      // Value is garantueed to be produced, so pop() is safe
      Suspension dup = dup_stack.pop();
      
      if(op == null)
	checkForCast(expr.type(), lvalue.type(), il);

      if(withinNestedAssignment(a))
	il.append(dup);       // duplicate expression value on stack before write access
      
      il.append(assignment_stack.pop()); // append putfield, e.g.
    }

    applyPostHook(a);
  }

  private static final boolean withinNestedAssignment(Assignment a) {
    Object o = a.container();

    return !((o instanceof ExpressionStatement) || (o instanceof AForInit) ||
	     (o instanceof For));
  }

  //
  //*************** Methods ***************
  //

  private boolean call_field_init;

  private final void initMethod(String name) {
    if(CodeGenerator.debug)
      System.out.print("@Compiling method " + name + " ... ");

    il         = new InstructionList();
    local_vars = new VariableEnvironment();
    local_vars.increaseLevel();
    br_cont_map = new LabelHashtable();
    max_stack  = 0;
  }

  private final void initMethod(AMethod method, int param_index) {
    initMethod(method.getName());

    MethodObject m = new MethodObject(this, method);

    mg = new MethodGen(m.access, m.result_type, m.arg_types, m.arg_names,
		       method.getName(), class_name, il, cp);
    
    if(!method.isAbstract() && !method.isNative()) {
      LocalVariableGen[] lv = mg.getLocalVariables();

      for(ParameterIterator i=method.getParameters().iterator(); i.hasNext(); param_index++)
	local_vars.put(i.next(), lv[param_index], null);
    }

    for(ClassIterator classes = method.getExceptions().iterator(); classes.hasNext();)
      mg.addException(implementationNameOf(classes.next()));
  }

  public static void optimize(MethodGen mg) {
    if((CodeGenerator.optimize != null) && !(mg.isAbstract() || mg.isNative())) {
      int old_size = mg.getInstructionList().getLength();

      if(CodeGenerator.debug)
	System.out.print("Optimizing method " + mg.getName() + " ");

      new PeepHole(mg, CodeGenerator.debug).optimize(CodeGenerator.optimize);

      if(CodeGenerator.debug) {
	int new_size = mg.getInstructionList().getLength();
	int shrink = 100 - ((new_size * 100) / old_size);
	System.out.println(" (" + shrink + "%)");
      }
    }
  }

  private final void finishMethod() {
    mg.stripAttributes(true);

    optimize(mg);
    //System.out.println(il);

    clazz.addMethod(mg.getMethod());
    local_vars  = null;
    br_cont_map = null;

    if(!(mg.isAbstract() || mg.isNative()))
      il.dispose(); // Save memory by reusing instruction handles

    il          = null;
    mg          = null; // Just in case ...
  }

  /**
   * Constructor method.
   */
  public void visitConstructor(Constructor c) {
    initMethod(c, 1);

    if(applyPreHook(c)) {
      c.getConstructorCall().accept(this); // See below, this sets call_field_init
    
      if(call_field_init/* && init_hook.size() > 0*/) {
	for(Iterator i=init_hook.iterator(); i.hasNext();)
	  il.append((Suspension)i.next());
	//il.append(factory.createThis());
	//il.append(factory.createInvoke(class_name, "$fieldinit",
	//			       Type.VOID, Type.NO_ARGS, INVOKESPECIAL));
      }
      call_field_init = false;
      
      il.append(factory.codeFor(c.getBody()));
      il.append(InstructionConstants.RETURN);
    }

    if(applyPostHook(c))
      finishMethod();
  }

  /**
   * int foo(String bla) { ... }
   */
  public void visitConcreteMethod(ConcreteMethod method) {
    initMethod(method, method.isStatic()? 0 : 1);

    if(applyPreHook(method)) {
      method.getBody().accept(this);

      if(method.getResultType() == null) // void
	il.append(InstructionConstants.RETURN);
    }

    if(applyPostHook(method))
      finishMethod();
  }

  /**
   * Generate empty method body.
   */
  public void visitAbstractMethod(AbstractMethod method) {
    initMethod(method, method.isStatic()? 0 : 1); // static native is allowed

    if(applyPreHook(method)) {
      mg.setInstructionList(null);
      mg.isAbstract(!method.isNative());
    }

    if(applyPostHook(method))
      finishMethod();
  }

  /**
   * Check method arguments and insert type conversions where needed. Paramaters for method call
   * are on stack then.
   */
  public void checkMethodParameters(AExpressionList exprs, ParameterList params) {
    AExpressionIterator i = exprs.iterator();
    ParameterIterator   j = params.iterator();

    while(i.hasNext()) { // i and j must have same length
      AExpression expr  = i.next();
      Parameter   param = j.next();
      expr.accept(this);
      checkForCast(expr.type(), param.getType(), il);
    }
  }

  //
  //*************** Method calls ***************
  //

  /**
   * Also every constructor implicitely or explicitely calls its super constructor, i.e.
   * super() or another constructor of this class with this().
   */
  public void visitConstructorCall(ConstructorCall c) {
    if(applyPreHook(c)) {
      AMethod      m  = c.getCalledConstructor();
      MethodObject mo = new MethodObject(this, m);

      /* Name equals to class name -> this() or super() call
       * if super() call also field initializer, otherwise this has already been
       * done by the called this() constructor.
       */
      call_field_init = !mo.class_name.equals(class_name);

      il.append(factory.createThis()); // Implicit first argument is always `this'
      checkMethodParameters(c.getArguments(), m.getParameters());
      il.append(factory.createInvoke(mo, INVOKESPECIAL)); // stack neutral
    }

    applyPostHook(c);
  }

  public void visitStaticMethodCall(StaticMethodCall call) {
    if(applyPreHook(call)) {
      AMethod m = call.getCalledMethod();

      checkMethodParameters(call.getArguments(), m.getParameters());
      il.append(factory.createInvoke(new MethodObject(this, m), INVOKESTATIC));
    }

    applyPostHook(call);
  }

  /**
   * this.f() or super.f() ?
   */
  private static final boolean isSuper(AExpression obj) {
    return (obj instanceof This) && ((This)obj).isSuper();
  }

  public void visitInstanceof(Instanceof i) {
    if(applyPreHook(i)) {
      i.getOperand().accept(this);

      ObjectType type  = (ObjectType)Conversion.getType(i.getReferenceType());
      int        index = cp.addClass(type.getClassName());
      
      il.append(new INSTANCEOF(index));
    }

    applyPostHook(i);
  }

  public void visitInstanceMethodCall(InstanceMethodCall call)  {
    if(applyPreHook(call)) {
      AMethod     m   = call.getCalledMethod();
      AExpression obj = call.getInstance();

      obj.accept(this); // push object reference
      checkMethodParameters(call.getArguments(), m.getParameters());

      short kind;

      if((obj.type() instanceof Interface) && (m.container() != OBJECT))
	kind = INVOKEINTERFACE;
      else if(m.isPrivate()) // calling local private method
	kind = INVOKESPECIAL;
      else
	kind = isSuper(obj)? INVOKESPECIAL : INVOKEVIRTUAL;

      il.append(factory.createInvoke(implementationNameOf((AUserType)obj.type()),
				     m.getName(), Conversion.getType(m.getResultType()),
				     Conversion.getParameters(m.getParameters()),
				     kind));
    }

    applyPostHook(call);
  }

  //
  //*************** Expressions ***************
  //

  //private int bin_level = 0;
  /**
   * z = x + y;
   *
   * System.out.println(x + ":" + y);
   */
  public void visitBinaryOperation(BinaryOperation op) {
    if(applyPreHook(op)) {
      Type type = Conversion.getType(op.type());
    
      if(type.getType() == T_BOOLEAN) { // Very special case
	InstructionList bool = factory.createBinaryBooleanOperation(op);
	il.append(bool);

	applyPostHook(op);
	return;
      }

      AExpression expr1  = op.getLeftOperand();
      Type        type1  = Conversion.getType(expr1.type());
      AExpression expr2  = op.getRightOperand();
      Type        type2  = Conversion.getType(expr2.type());
      boolean     is_str = isString(type);
      boolean     nested = op.container() instanceof BinaryOperation;

      if(is_str && !nested) { // top-level string concatenation expression
	int index = cp.addClass("java.lang.StringBuffer");

	il.append(new NEW(index));
	il.append(InstructionConstants.DUP);
	increaseStack(2);
	
	il.append(factory.createInvoke("java.lang.StringBuffer", "<init>", Type.VOID,
				       Type.NO_ARGS, INVOKESPECIAL));
      } // else append() will leave reference on stack for us

      expr1.accept(this);
      
      if(is_str) {
	// expr1 created by previous BinaryOperation ? -> do nothing (already appended)
	if(!(isString(type1) && (expr1 instanceof BinaryOperation)))
	  il.append(factory.createAppend(type1));
      } else
	checkForCast(type1, type, il);
		   
      expr2.accept(this);

      if(is_str) // + is left-associative, so we don't need the above binlevel check here
	il.append(factory.createAppend(type2));
      else {
	/* Check for stupid special case: second arg for long << must be int, not long
	 */
	if((op.operator().startsWith("<<") || op.operator().startsWith(">>")) &&
	   (type.getType() == T_LONG))
	  type = Type.INT;

	checkForCast(type2, type, il);
      }
      
      if(is_str) {
	if(!nested) // top-level reached again
	  il.append(factory.createInvoke("java.lang.StringBuffer", "toString",
					 Type.STRING, Type.NO_ARGS, INVOKEVIRTUAL));
      } else // "Normal" binary operation
	il.append(factory.createBinaryOperation(op));
    }

    applyPostHook(op);
  }

  /**
   * z = -1 + ++i;
   */
  public void visitUnaryOperation(UnaryOperation op) {
    if(applyPreHook(op)) {
      Literal l = factory.simplifyExpr(op); // Try to convert -(1) to -1, e.g.

      if(l != null) {
	il.append(factory.createLiteral(l)); // Factory handles stack size computation
	checkForCast(l.type(), op.type(), il);
      } else if(Conversion.getType(op.type()).getType() == T_BOOLEAN) { // Very special case
	InstructionList bool = factory.createUnaryBooleanOperation(op);
	il.append(bool);
      } else {
	AExpression expr = op.getOperand();
	
	if(op.operator().length() == 2) // Sloppy check for ++ or --
	  il.append(factory.createInc((ALValue)expr, op.operator().startsWith("+")? 1 : -1,
				      op.isPostfix()));
	else { // -, +, !, ~, ...
	  expr.accept(this);
	  il.append(factory.createUnaryOperation(op));
	}
	
	checkForCast(expr.type(), op.type(), il);
      }
    }

    applyPostHook(op);
  }

  /** float x = (float)4711.0815;
   */
  public void visitCast(Cast c) {
    if(applyPreHook(c)) {
      Literal l = factory.simplifyExpr(c);

      if(l == null) {
	c.getOperand().accept(this);
	checkForCast(c.getOperand().type(), c.getCastType(), il, true);
      } else
	il.append(factory.createLiteral(l)); // No cast needed
    }

    applyPostHook(c);
  }

  /**
   * a.length
   */
  public void visitArrayLengthAccess(ArrayLengthAccess a)  {
    if(applyPreHook(a)) {
      a.getOperand().accept(this);
      il.append(InstructionConstants.ARRAYLENGTH);
      increaseStack();
    }

    applyPostHook(a);
  }

  /**
   * Explicit or implicit reference to `this'
   */
  public void visitThis(This t) {
    if(applyPreHook(t))
      il.append(factory.createThis());

    applyPostHook(t);
  }

  //
  //*************** Statements ***************
  //

  public void visitThrow(Throw t) {
    if(applyPreHook(t)) {
      t.getExpression().accept(this);
      il.append(InstructionConstants.ATHROW);
    }

    applyPostHook(t);
  }

  private static final boolean popValue(AExpression expr, Type type) {
    return (type.getType() != T_VOID) && !(expr instanceof Assignment) &&
       !((expr instanceof UnaryOperation) && // special case, e.g. i++;
	 (((UnaryOperation)expr).operator().length() == 2));
  }

  public void visitBlock(Block o) {
    if(applyPreHook(o)) {
      InstructionList br = new InstructionList(InstructionConstants.NOP); // target for break
      br_cont_map.put(o, br.getStart());

      local_vars.increaseLevel();
      for(AStatementIterator i=o.getStatements().iterator(); i.hasNext();)
	i.next().accept(this);
      local_vars.decreaseLevel(il.getEnd());

      il.append(br);
    }

    applyPostHook(o);
  }

  /**
   * Single statement ended with ; like y = f(x);
   */
  public void visitExpressionStatement(ExpressionStatement e) {
    if(applyPreHook(e)) {
      AExpression expr = e.getExpression();
      Type        type = Conversion.getType(expr.type());

      expr.accept(this);

      if(popValue(expr, type))
	il.append(factory.createPop(type.getSize()));
    }

    applyPostHook(e);
  }

  /**
   * return 4711;
   */
  public void visitReturn(Return r) {
    if(applyPreHook(r)) {
      AExpression expr = r.getExpression();

      if(expr == null) { // return;
	if(mg.getReturnType().getType() == T_VOID)
	  il.append(InstructionConstants.RETURN);
	else { // return null;
	  il.append(InstructionConstants.ACONST_NULL);
	  il.append(InstructionConstants.ARETURN);
	  increaseStack();
	}
      } else {
	expr.accept(this);

	AType result_type = r.containingMethod().getResultType();
	checkForCast(expr.type(), result_type, il);

	il.append(factory.createReturn(Conversion.getType(result_type)));
      }
    }

    applyPostHook(r);
  }

  /**
   * Generate code for literal like 4711
   */
  public void visitLiteral(Literal l) {
    if(applyPreHook(l))
      il.append(factory.createLiteral(l));

    applyPostHook(l);
  }
  
  private class StringIntHashTable {
    private class Index {
      int i;
      Index(int j) { i = j; }
    }

    private HashMap table  = new HashMap();
    private int     method = -1;

    int lookup(final String field_name, final String _class) {
      if(method == -1) // Not yet added
	method = factory.addClassMethod(class_name);

      Index i = (Index)table.get(field_name);

      if(i == null) {
	final int j = cp.addFieldref(class_name, field_name, "Ljava/lang/Class;");

	table.put(field_name, i = new Index(j));

	clinit_hook.add(new Suspension() {
	  public InstructionList getInstructionList() {
	    clazz.addField(new org.apache.bcel.classfile.Field(ACC_PRIVATE |
							       ACC_STATIC | ACC_FINAL,
							       cp.addUtf8(field_name),
							       cp.addUtf8("Ljava/lang/Class;"),
							       null, cp.getConstantPool()));
	    InstructionList il = new InstructionList();
	    il.append(new PUSH(cp, _class));
	    il.append(new INVOKESTATIC(method));
	    increaseStack(2);
	    il.append(new PUTSTATIC(j));
	    decreaseStack();
	    return il;
	  }
	});
      }

      return i.i;
    }
  }

  /**
   * Generate code for Object.class, e.g.
   */
  public void visitClassExpression(ClassExpression c) {
    if(applyPreHook(c)) {
      String _class     = c.getType().qualifiedName();
      String field_name = "class$" + _class.replace('.', '$');

      increaseStack();
      il.append(new GETSTATIC(added_fields.lookup(field_name, _class)));
    }

    applyPostHook(c);
  }

  //
  //*************** Control flow ***************
  //

  public void visitConditional(Conditional c) {
    if(applyPreHook(c)) {
      IFFALSE if1;
      GOTO    g;

      c.getCondition().accept(this);
      il.append(if1 = new IFFALSE(null));
      c.getIfTrue().accept(this);
      checkForCast(c.getIfTrue().type(), c.type(), il);

      il.append(g=new GOTO(null));
      InstructionList else1 = factory.codeFor(c.getIfFalse());
      if1.setTarget(else1.getStart());
      il.append(else1);
      checkForCast(c.getIfFalse().type(), c.type(), il);
      g.setTarget(il.append(InstructionConstants.NOP));
    }

    applyPostHook(c);
  }

  private LabelHashtable br_cont_map;

  public void visitIf(If if1) {
    if(applyPreHook(if1)) {
      InstructionList br = new InstructionList(InstructionConstants.NOP); // target for break
      br_cont_map.put(if1, br.getStart());

      if1.getExpression().accept(this);
      BranchInstruction i, g=null;
      il.append(i = new IFFALSE(null));
      if1.getThenBranch().accept(this);

      boolean is_branch = isBranch(il.getEnd()); // check for unconditional branch/return
      if(!is_branch)
	il.append(g = new GOTO(null));

      AStatement        expr   = if1.getElseBranch();
      InstructionHandle target;
    
      if(expr != null) {
	InstructionList e = factory.codeFor(expr);

	if(e.size() > 0) {
	  target = e.getStart();
	  il.append(e);
	} else
	  target = il.append(InstructionConstants.NOP);
      } else
	target = il.append(InstructionConstants.NOP); // Safe: Not ending with unconditional branch anyway
      
      i.setTarget(target); // target for IFFALSE

      if(!is_branch) {
	target = il.append(InstructionConstants.NOP);
	g.setTarget(target);
      }
    
      il.append(br);
    }

    applyPostHook(if1);
  }

  private static final int getConstantValue(Object obj) {
    if(obj instanceof Number)
      return ((Number)obj).intValue();
    else if(obj instanceof Character)
      return (int)((Character)obj).charValue();
    else
      throw new RuntimeException("What's this? " + obj);
  }
  
  private static final boolean hasDefault(Switch s) {
    for(ASwitchBranchIterator iter = s.getBranches().iterator(); iter.hasNext();)
      if(iter.next() instanceof DefaultBranch)
	return true;

    return false;
  }

  public void visitSwitch(Switch s) {
    if(applyPreHook(s)) {
      InstructionList br = new InstructionList(InstructionConstants.NOP); // target for break
      br_cont_map.put(s, br.getStart());
      s.getExpression().accept(this);

      ASwitchBranchIterator iter        = s.getBranches().iterator();
      int                   size        = s.getBranches().size();
      boolean               has_default = hasDefault(s);

      if(has_default) // default branch handled separately
	size--;

      int[]             keys    = new int[size];
      InstructionList[] targets = new InstructionList[size];
      InstructionList   def     = new InstructionList(InstructionConstants.NOP);
      int               j       = 0;
      
      while(iter.hasNext()) {
	ASwitchBranch   b = iter.next();
	InstructionList l = new InstructionList(InstructionConstants.NOP); // may be empty statement
	
	l.append(factory.codeFor(b.getStatements()));
	
	if(b instanceof CaseBranch) {
	  AExpression expr = ((CaseBranch)b).getConstantExpression();

	  CodeGenerator.do_assert(ConstantAnalysis.isConstant(expr), expr + " not constant");

	  keys[j]      = getConstantValue(ConstantAnalysis.evaluate(expr));
	  targets[j++] = l;
	}
	else // instanceof DefaultBranch
	  def = l;
      }

      if(j == 0) { // Stupid special case: only default branch
	il.append(InstructionConstants.POP);
	il.append(def);
	il.append(br);

	applyPostHook(s);
	return;
      }
    
      InstructionHandle[] target_ihs = new InstructionHandle[size];
      InstructionHandle   def_ih     = def.getStart();
      for(int i=0; i < size; i++)
	target_ihs[i] = targets[i].getStart();

      // keys and target_ihs will be sorted!
      il.append(new SWITCH(keys, target_ihs, def_ih, 20));
      
      iter = s.getBranches().iterator();
      for(int i=0; iter.hasNext();) {
	if(iter.next() instanceof CaseBranch)
	  il.append(targets[i++]);
	else
	  il.append(def);
      }

      if(!has_default) // make sure some default code is appended
	il.append(def);

      il.append(br);
    }

    applyPostHook(s);
  }

  public void visitDo(Do d) {
    if(applyPreHook(d)) {
      InstructionList br   = new InstructionList(InstructionConstants.NOP); // Targets for break and continue
      InstructionList cont = new InstructionList(InstructionConstants.NOP); // Will be moved to correct position
      br_cont_map.put(d, br.getStart(), cont.getStart());
      
      InstructionHandle start_b = il.append(InstructionConstants.NOP); // body may be empty
      d.getBody().accept(this);
      
      il.append(cont); // Jump here to continue
      InstructionList expr = factory.codeFor(d.getExpression());
      il.append(expr);
      il.append(new IFTRUE(start_b));
      
      il.append(br); // Jump here to break loop
    }

    applyPostHook(d);
  }
    
  public void visitWhile(While w) {
    if(applyPreHook(w)) {
      InstructionList br   = new InstructionList(InstructionConstants.NOP); // Targets for break and continue
      InstructionList cont = new InstructionList(InstructionConstants.NOP); // Will be moved to correct position
      br_cont_map.put(w, br.getStart(), cont.getStart());

      GOTO g;
      il.append(g = new GOTO(null)); // Initial jump to condition
      
      InstructionHandle start_b = il.append(InstructionConstants.NOP); // body may be empty
      w.getBody().accept(this);
      
      il.append(cont); // targets for continue
      InstructionList expr = factory.codeFor(w.getExpression());
      g.setTarget(expr.getStart());
      
      il.append(expr);
      il.append(new IFTRUE(start_b));

      il.append(br); // target for break
    }

    applyPostHook(w);
  }

  public void visitFor(For f) {
    if(applyPreHook(f)) {
      InstructionList br   = new InstructionList(InstructionConstants.NOP); // targets for break and continue
      InstructionList cont = new InstructionList(InstructionConstants.NOP);
      br_cont_map.put(f, br.getStart(), cont.getStart());
      
      local_vars.increaseLevel();
      acceptIfPresent(f.getForInit());

      GOTO g;
      il.append(g = new GOTO(null)); // Initial jump to condition

      InstructionHandle start_b = il.append(InstructionConstants.NOP); // body may be empty
      
      // code for body and increment exprs
      f.getBody().accept(this);
      il.append(cont);
      for(AExpressionIterator i=f.getUpdateExpressions().iterator(); i.hasNext();)
	i.next().accept(this);
      
      AExpression cond = f.getExpression();

      InstructionList expr = (cond != null)? factory.codeFor(cond) : new InstructionList();
      expr.append((cond == null)? (BranchInstruction)new GOTO(start_b) :
		  (BranchInstruction)new IFTRUE(start_b));

      g.setTarget(expr.getStart());
      il.append(expr);
      local_vars.decreaseLevel(il.getEnd());

      il.append(br);
    }

    applyPostHook(f);
  }

  public void visitContinue(Continue c) {
    if(applyPreHook(c))
      il.append(new GOTO(br_cont_map.get(c.getTarget())[1]));

    applyPostHook(c);
  }

  public void visitBreak(Break b) {
    if(applyPreHook(b)) {
      InstructionHandle target = br_cont_map.get(b.getTarget())[0];
      il.append(new GOTO(target));
    }

    applyPostHook(b);
  }

  //
  //*************** try-catch-finally ***************
  //

  /* INV: il.endsWith(UnconditionalBranch || Return) -> appended code will never be
   * reached by normal control flow. This enables some optimizations for otherwise
   * dead code.
   *
   * INV: Generated jump targets will always be NOP instruction so that will always be
   * adjusted correctly in the optimization phase.
   */
  private static final boolean isBranch(InstructionHandle ih) {
    Instruction i = ih.getInstruction();
    return (i instanceof UnconditionalBranch) || (i instanceof ReturnInstruction);
  }

  /**
   * Search for GOTOs and ReturnInstructions and insert a jsr.
   * 
   * @param il instruction list to search for returns
   * @param jsrs List of jsrs (altered)
   * @param index of local variable where to store return value temporarily
   * @param type return type of containing method

   */
  public final void patchBranches(InstructionList il, /*VAR*/java.util.List jsrs,
				   int index, Type type) {
    boolean is_void = type.getSize() == 0;

    for(InstructionHandle ih = il.getStart(); ih != null; ih = ih.getNext()) {
      Instruction i = ih.getInstruction();

      if(i instanceof ReturnInstruction) {
	if(!is_void)
	  il.insert(ih, factory.createStore(type, index));

	jsrs.add(il.insert(ih, new JSR(null)));

	if(!is_void)
	  il.insert(ih, factory.createLoad(type, index));
      }

      // Check for break statement
      if(i instanceof GOTO && !il.contains(((GOTO)i).getTarget())) // Works for null, too
	jsrs.add(il.insert(ih, new JSR(null)));
    }

//      InstructionHandle ih = il.getEnd();
//      Instruction       i  = ih.getInstruction();
  }

  /**
   * With "inlined" visitCatch() and visitFinally().
   */
  public void visitTry(Try t) {
    if(applyPreHook(t)) {
      java.util.List  gotos = new java.util.ArrayList(); // branches to "normal" control flow
      java.util.List  jsrs  = new java.util.ArrayList(); // jumps to finally code
      Finally f             = t.getFinallyClause();
      boolean has_finally   = f != null;

      // try { ... }
      InstructionList   block     = new InstructionList(InstructionConstants.NOP);
      InstructionHandle try_start = block.getStart();
      block.append(factory.codeFor(t.getBlock()));
    
      // unnamed local var for store/load operation
      Type return_type = mg.getReturnType();
      int  store_index = -1;

      if(return_type.getSize() > 0) { // Not void
	store_index = mg.getMaxLocals();
	mg.setMaxLocals(store_index + return_type.getSize());
      }

      if(has_finally)
	patchBranches(block, jsrs, store_index, return_type);
      il.append(block);

      InstructionHandle try_end = il.append(InstructionConstants.NOP);

      if(has_finally)
	jsrs.add(il.append(new JSR(null))); // jump to finally code
      gotos.add(il.append(new GOTO(null))); // continue normal execution

      // Create local variable to hold thrown exceptions
      int exc_index = mg.getMaxLocals();
      mg.setMaxLocals(exc_index + 1);
      
      // catch(Exeption1 e) { ... }
      for(CatchIterator i=t.getCatchClauses().iterator(); i.hasNext();) {
	Catch      c    = i.next();
	Parameter  p    = c.getParameter();
	ObjectType type = (ObjectType)Conversion.getType(p.getType());
	String     name = p.getName();
	
	// Store thrown exception in local variable
	InstructionHandle start = il.append(new ASTORE(exc_index));
	LocalVariableGen  lg    = mg.addLocalVariable(name, type, exc_index, start, null);
	
	local_vars.increaseLevel();
	local_vars.put(p, lg, start);
	
	block = factory.codeFor(c.getBlock());
	if(has_finally)
	  patchBranches(block, jsrs, store_index, return_type);

	local_vars.decreaseLevel(block.getEnd());

	mg.addExceptionHandler(try_start, try_end, start, type);

	il.append(block);

	if(has_finally)
	  jsrs.add(il.append(new JSR(null))); // jump to finally code
	gotos.add(il.append(new GOTO(null))); // continue normal execution
      }

      /* Add handler that handles any exception thrown within a catch block,
       * i.e. make sure that the finally block gets called always even when
       * an uncaught exception occurs.
       */
      if(has_finally) {
	InstructionHandle handlers_end = il.getEnd();
	InstructionHandle handler      = il.append(new ASTORE(exc_index));
	
	jsrs.add(il.append(new JSR(null)));
	il.append(new ALOAD(exc_index)); // reload and rethrow exception
	il.append(InstructionConstants.ATHROW);
      
	mg.addExceptionHandler(try_start, handlers_end, handler, (ObjectType)null);

	/* Generate code for finally block.
	 */
	int finally_index = mg.getMaxLocals();
	mg.setMaxLocals(finally_index + 1);
	
	InstructionHandle finally_target = il.append(new ASTORE(finally_index));
	il.append(factory.codeFor(f.getBlock()));
	il.append(new RET(finally_index));
	
	/* Update targets of JSR instructions
	 */
	for(java.util.Iterator i = jsrs.iterator(); i.hasNext();)
	  ((BranchHandle)i.next()).setTarget(finally_target);
      }
      
      InstructionHandle target = il.append(InstructionConstants.NOP); // target for normal execution flow
      
      /* Update targets for normal execution flow.
       */
      for(java.util.Iterator i = gotos.iterator(); i.hasNext();)
	((BranchHandle)i.next()).setTarget(target);
    }

    applyPostHook(t);
  }

  public void visitSynchronized(Synchronized s) {
    if(applyPreHook(s)) {
      s.getExpression().accept(this);
      il.append(InstructionConstants.DUP);
      increaseStack();
      
      // create local variable to hold synchronized object
      int synch_index  = mg.getMaxLocals();
      mg.setMaxLocals(synch_index + 1);

      il.append(new ASTORE(synch_index));
      InstructionHandle start = il.append(InstructionConstants.MONITORENTER);
      decreaseStack(2);

      s.getBlock().accept(this);
      il.append(new ALOAD(synch_index));
      increaseStack();
      il.append(InstructionConstants.MONITOREXIT);
      decreaseStack();

      BranchHandle goto_ = il.append(new GOTO(null)); // continue normal execution

      /* Add handler that handles any exception thrown within a synch_index block,
       * i.e. make sure that the monitorexit gets called always even when
       * an uncaught exception occurs.
       */
      InstructionHandle handler = il.append(new ALOAD(synch_index));
      il.append(InstructionConstants.MONITOREXIT);

      il.append(InstructionConstants.ATHROW);
      
      mg.addExceptionHandler(start, goto_, handler, (ObjectType)null);
      goto_.setTarget(il.append(InstructionConstants.NOP));
    }

    applyPostHook(s);
  }

  /**
   * Check private constraints before class can be dumped.
   */
  void check() {
    // Add access methods
    //access_list.createMethods();

    // Field initializer necessary?
    /*if((init_hook != null) && (init_hook.size() > 0)) {
      initMethod("$fieldinit");
      mg = new MethodGen(ACC_PRIVATE | ACC_FINAL, Type.VOID,
			 null, null, "$fieldinit", class_name, il, cp);

      for(SuspensionIterator i=init_hook.iterator(); i.hasNext();)
	il.append(i.next());

      il.append(InstructionConstants.RETURN);
      init_hook = null;
      finishMethod();
      }*/

    // Static initializer missing?
    if((clinit_hook != null) && (clinit_hook.size() > 0)) {
      initMethod("<clinit>");
      mg = new MethodGen(ACC_STATIC | ACC_FINAL, Type.VOID, null, null, "<clinit>",
			 class_name, il, cp);

      for(Iterator i=clinit_hook.iterator(); i.hasNext();)
	il.append((Suspension)i.next());

      il.append(InstructionConstants.RETURN);
      clinit_hook = null;

      finishMethod();
    }
  }
  
  //
  //*************** Auxiliary classes and methods ***************
  //

  private static class SuspensionStack {
    private ArrayList stack = new ArrayList();
    
    final void       push(Suspension s) { stack.add(s); }
    final Suspension pop()              { return (Suspension)stack.remove(stack.size() - 1); }
  }

  private static class LabelHashtable {
    private HashMap table = new HashMap();

    private final void put(ATargetStatement target, InstructionHandle[] ihs) {
      table.put(target, ihs);
    }

    final void put(ATargetStatement target, InstructionHandle br, InstructionHandle cont) {
      table.put(target, new InstructionHandle[] { br, cont });
    }

    final void put(ATargetStatement target, InstructionHandle br) {
      put(target, br, null);
    }

    final InstructionHandle[] get(ATargetStatement target) {
      return (InstructionHandle[])table.get(target);
    }
  }

  /**
   * Insert type conversion operator if necessary.
   */
  final void checkForCast(Type src_type, Type dest_type, InstructionList il) {
    checkForCast(src_type, dest_type, il, false);
  }

  final void checkForCast(AType src, AType dest, InstructionList il) {
    checkForCast(Conversion.getType(src), Conversion.getType(dest), il, false);
  }

  final void checkForCast(AType src, AType dest, InstructionList il, boolean explicit_cast) {
    checkForCast(Conversion.getType(src), Conversion.getType(dest), il, explicit_cast);
  }

  final void checkForCast(Type src_type, Type dest_type, InstructionList il,
			  boolean explicit_cast) {
    /* Must have been checked at compile time, so no cast is needed.
     */
    if((src_type instanceof ReferenceType) && (dest_type instanceof ReferenceType)) {
      if(explicit_cast)
	il.append(factory.createCast(src_type, dest_type));
      return;
    }

    if(!src_type.equals(dest_type) && !isSubtype(src_type, dest_type))
      il.append(factory.createCast(src_type, dest_type));  // Need to insert a cast
  }

  public static final boolean isString(Type type) {
    return ((type instanceof ObjectType) && 
            ((ObjectType)type).getClassName().equals("java.lang.String"));
  }

  public static final boolean isSubtype(Type src_type, Type dest_type) {
    byte dest = dest_type.getType();
    byte src  = src_type.getType();

    return ((dest == T_INT) && ((src == T_CHAR) || (src == T_BYTE) || (src == T_SHORT))) ||
      ((dest == T_CHAR) &&  ((src == T_BYTE) || (src == T_SHORT))) ||
      ((dest == T_BYTE) &&  ((src == T_CHAR) || (src == T_SHORT))) ||
      ((dest == T_SHORT) && ((src == T_BYTE) || (src == T_CHAR)));
  }

  private final boolean isInterface() {
    return (clazz.getAccessFlags() & ACC_INTERFACE) != 0;
  }

  private final boolean isClass() {
    return (clazz.getAccessFlags() & ACC_INTERFACE) == 0;
  }

  public String implementationNameOf(ANamed n) {
    if(n instanceof AUserType) {
      AUserType a = (AUserType)n.containing(AUserType.class);

      if(a != null) { // Fix for BB, what about inner inner classes!?
	String s     = implementationNameOf(a);
	String name  = n.getName();  // For anonymous classes name is already A$1
	int    index = name.lastIndexOf('$');

	return s + "$" + name.substring(index + 1);
      }
    }
    
    ANamed c = (ANamed)n.containing(barat.reflect.ANamed.class);
    if(c != null)
      return implementationNameOf(c) + "." + n.getName();

    barat.reflect.Package p = (barat.reflect.Package)n.containing(barat.reflect.Package.class);
    if(p == null)
      return n.getName();

    String pname = p.getQualifiedName().toString();
    if(pname.length() != 0)
      pname = pname + ".";

    return pname + n.getName();
  }

  // write access to variable/array ?
  public static final boolean isLHS(ALValue lv) {
    return (lv.container() instanceof Assignment) && lv.aspect().equals("lvalue");
  }

  private static final void DEBUG(String s) { System.out.println(s); }

  //
  //*************** Computation of stack size ***************
  //

  /**
   * Increase max stack size.
   */
  public final void increaseStack(int n) {
    max_stack += n;
    if(max_stack > mg.getMaxStack())
      mg.setMaxStack(max_stack);
  }

  public final void increaseStack()      { increaseStack(1); }
  public final void decreaseStack(int n) { max_stack -= n; }
  public final void decreaseStack()      { max_stack--; }

  //
  // ******************** Interface to Hooks ***************
  //

  public InstructionList    getInstructionList()                   { return il; }
  public void               setInstructionList(InstructionList il) { this.il = il; }
  public MethodGen          getMethod()                            { return mg; }
  public void               setMethod(MethodGen mg)                { this.mg=mg; }
  public FieldGen           getField()                             { return fg; }
  public void               setField(FieldGen mg)                  { this.fg=fg; }
  public InstructionFactory getFactory()                           { return factory; }
  public void               setFactory(InstructionFactory f)       { factory = f; }
  public String             getClassName()                         { return class_name; }
  public void               setClassName(String c)                 { class_name = c; }
  public CodeGenerator      getCodeGenerator()                     { return cg; }
  public void               setClassGenerator(ClassGen c)          { clazz = c; }
  public ClassGen           getClassGenerator()                    { return clazz; }
  public ConstantPoolGen    getConstantPool()                      { return cp; }
  public void               setConstantPool(ConstantPoolGen c)     { cp = c; }
  public AUserType          getUserType()                          { return cl; }
  public void               setUserType(AUserType a)               { cl = a; }
  public ArrayList     getInitHook()                          { return init_hook; }
  public ArrayList     getClinitHook()                        { return clinit_hook; }
  public void                setLocalVars(VariableEnvironment l)   { local_vars = l; }
  public VariableEnvironment getLocalVars()                        { return local_vars; }
}
