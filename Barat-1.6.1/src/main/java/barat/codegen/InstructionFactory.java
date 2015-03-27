package barat.codegen;

import org.apache.bcel.generic.*;
import org.apache.bcel.classfile.*;
import barat.reflect.*;
import barat.collections.*;
import barat.parser.*;
import java.util.Stack;

/**
 * Create instructions.
 *
 * @version $Id: InstructionFactory.java,v 1.3 2002/01/28 10:48:35 dahm Exp $
 * @author  <A HREF="http://www.inf.fu-berlin.de/~dahm">M. Dahm</A>
 */
public class InstructionFactory implements org.apache.bcel.Constants {
  private ClassGenerator  cg;
  private ConstantPoolGen cp;
  private static Factory  factory = Factory.getInstance(); // Parser factory

  public InstructionFactory(ClassGenerator cg) {
    this.cg = cg;
    cp      = cg.getConstantPool();
  }

  public InvokeInstruction createInvoke(String class_name, String name, Type ret_type,
					Type[] arg_types, short kind) {
    int    index;
    int    nargs      = 0;
    int    stack_size; // May be negative
    String signature  = Type.getMethodSignature(ret_type, arg_types);

    for(int i=0; i < arg_types.length; i++) // Count size of arguments
      nargs += arg_types[i].getSize();

    stack_size = ret_type.getSize() - nargs;

    if(kind == INVOKEINTERFACE)
      index = cp.addInterfaceMethodref(class_name, name, signature);
    else
      index = cp.addMethodref(class_name, name, signature);

    if(kind != INVOKESTATIC)
      stack_size--; // pops object reference

    cg.increaseStack(stack_size);

    switch(kind) {
    case INVOKESPECIAL:   return new INVOKESPECIAL(index);
    case INVOKEVIRTUAL:   return new INVOKEVIRTUAL(index);
    case INVOKESTATIC:    return new INVOKESTATIC(index);
    case INVOKEINTERFACE: return new INVOKEINTERFACE(index, nargs + 1);
    default:
      throw new RuntimeException("Oops: Unknown invoke kind:" + kind);
    }
  }

  public InvokeInstruction createInvoke(MethodObject m, short kind) {
    return createInvoke(m.class_name, m.name, m.result_type, m.arg_types, kind);
  }

  public CompoundInstruction createLiteral(Literal literal) {
    Object value = literal.constantValue();

    if((value instanceof Double) || (value instanceof Long))
      cg.increaseStack(2);
    else
      cg.increaseStack(1);

    if(value instanceof String)
      return new PUSH(cp, (String)value);
    else if(value instanceof Character)
      return new PUSH(cp, ((Character)value).charValue());
    else if(value instanceof Boolean)
      return new PUSH(cp, ((Boolean)value).booleanValue());
    else if(value instanceof Integer)
      return new PUSH(cp, ((Integer)value).intValue());
    else if(value instanceof Long)
      return new PUSH(cp, ((Long)value).longValue());
    else if(value instanceof Double)
      return new PUSH(cp, ((Double)value).doubleValue());
    else if(value instanceof Float)
      return new PUSH(cp, ((Float)value).floatValue());
    else if(value instanceof Short)
      return new PUSH(cp, ((Short)value).shortValue());
    else if(value instanceof Byte)
      return new PUSH(cp, ((Byte)value).byteValue());
    else if(value == null)
      return new PUSH(cp, (String)null);
    else {
      throw new RuntimeException("Oops: What literal is this? " + value + ":" +
				 value.getClass());
    }
  }

  private static MethodObject[] append_mos = {
    new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER,
		     new Type[] { Type.STRING }, ACC_PUBLIC),
    new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER,
		     new Type[] { Type.OBJECT }, ACC_PUBLIC),
    null, null, // indices 2, 3
    new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER,
		     new Type[] { Type.BOOLEAN }, ACC_PUBLIC),
    new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER,
		     new Type[] { Type.CHAR }, ACC_PUBLIC),
    new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER,
		     new Type[] { Type.FLOAT }, ACC_PUBLIC),
    new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER,
		     new Type[] { Type.DOUBLE }, ACC_PUBLIC),
    new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER,
		     new Type[] { Type.INT }, ACC_PUBLIC),
    new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER, // No append(byte)
		     new Type[] { Type.INT }, ACC_PUBLIC),
    new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER, // No append(short)
		     new Type[] { Type.INT }, ACC_PUBLIC),
    new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER,
		     new Type[] { Type.LONG }, ACC_PUBLIC)    
  };

  public Instruction createAppend(Type type) {
    byte t = type.getType();

    if(ClassGenerator.isString(type))
      return createInvoke(append_mos[0], INVOKEVIRTUAL);

    switch(t) {
    case T_BOOLEAN:
    case T_CHAR: 
    case T_FLOAT:
    case T_DOUBLE:
    case T_BYTE:
    case T_SHORT:
    case T_INT:
    case T_LONG:    return createInvoke(append_mos[t], INVOKEVIRTUAL);
    case T_ARRAY:
    case T_OBJECT:  return createInvoke(append_mos[1], INVOKEVIRTUAL);
    default:
      throw new RuntimeException("Oops: No append for this type? " + type);
    }
  }
					
  public Instruction createThis() {
    cg.increaseStack();
    return InstructionConstants.THIS;
  }

  public ReturnInstruction createReturn(Type type) {
    cg.decreaseStack(type.getSize());

    switch(type.getType()) {
    case T_ARRAY:
    case T_OBJECT:  return InstructionConstants.ARETURN;
    case T_INT:
    case T_SHORT:
    case T_BOOLEAN:
    case T_CHAR: 
    case T_BYTE:    return InstructionConstants.IRETURN;
    case T_FLOAT:   return InstructionConstants.FRETURN;
    case T_DOUBLE:  return InstructionConstants.DRETURN;
    case T_LONG:    return InstructionConstants.LRETURN;
    case T_VOID:    return InstructionConstants.RETURN;

    default:
      throw new RuntimeException("Invalid type: " + type);
    }
  }

  private Stack orig_il = new Stack();
  private final void save(InstructionList il) {
    orig_il.push(cg.getInstructionList()); // Save old instruction list
    cg.setInstructionList(il);
  }
  private final void restore() {
    cg.setInstructionList((InstructionList)orig_il.pop());  // restore
  }

  /**
   * Generate code for an expression.  Basically does an
   * expr.accept(cg) and returns the resulting InstructionList
   */
  public InstructionList codeFor(AExpression expr, InstructionList il) {
    save(il);
    expr.accept(cg); // Use call back to fill instruction list
    restore();

    return il;
  }

  /**
   * Generate code for a statement block.  Basically does an
   * block.accept(cg) and returns the resulting InstructionList
   */
  public InstructionList codeFor(Block block, InstructionList il) {
    save(il);
    block.accept(cg); // Use call back to fill instruction list
    restore();

    return il;
  }

  /**
   * Generate code for a list of expressions.  Basically does an
   * list.accept(cg) and returns the resulting InstructionList
   */
  public InstructionList codeFor(AExpressionList list, InstructionList il) {
    save(il);

    for(AExpressionIterator i=list.iterator(); i.hasNext();)
      i.next().accept(cg);
    
    restore();
    return il;
  }

  /**
   * Generate code for a list of statements.  Basically does an
   * list.accept(cg) and returns the resulting InstructionList
   */
  public InstructionList codeFor(AStatementList list, InstructionList il) {
    save(il);

    for(AStatementIterator i=list.iterator(); i.hasNext();)
      i.next().accept(cg);
    
    restore();
    return il;
  }

  /**
   * Generate code for a statement.  Basically does an
   * statement.accept(cg) and returns the resulting InstructionList
   */
  public InstructionList codeFor(AStatement statement, InstructionList il) {
    save(il);
    statement.accept(cg);
    restore();
    return il;
  }

  public final InstructionList codeFor(AExpression expr) {
    return codeFor(expr, new InstructionList());
  }

  public final InstructionList codeFor(Block block) {
    return codeFor(block, new InstructionList());
  }

  public final InstructionList codeFor(AExpressionList list) {
    return codeFor(list, new InstructionList());
  }

  public final InstructionList codeFor(AStatementList list) {
    return codeFor(list, new InstructionList());
  }

  public final InstructionList codeFor(AStatement statement) {
    return codeFor(statement, new InstructionList());
  }

  /**
   * Simplify expression to literal expression, if possible.
   *
   * Examples:
   *  (float)2.7182818. -> Float() (Remove cast)
   *  -(42) -> -42   (Remove Unop.)
   */
  static Literal simplifyExpr(AExpression expr) {
    if(expr instanceof Literal)
      return (Literal)expr;

    /* Perform simple casts, like (float)2.7182818
     */
    if((expr instanceof Cast) && (((Cast)expr).getOperand() instanceof Literal)) {
      Cast    cast        = (Cast)expr;
      Literal l           = (Literal)cast.getOperand();
      byte    actual_type = Conversion.getType(l.type()).getType();
      byte    cast_type   = Conversion.getType(cast.getCastType()).getType();

      if(actual_type == cast_type) // Exact match
	return l;

      if((actual_type == T_DOUBLE) && (cast_type == T_FLOAT)) {
        double val = ((Double)l.constantValue()).doubleValue();

	if((val > Float.MAX_VALUE) || (val < Float.MIN_VALUE))
	  System.err.println("Warning: constant too large for float: " + val);

	return factory.createLiteral(new Float((float)val));
      } else if(actual_type == T_INT) {
	int val = ((Integer)l.constantValue()).intValue();

	switch(cast_type) {
	case T_SHORT:
	  if((val > Short.MAX_VALUE) || (val < Short.MIN_VALUE))
	    System.err.println("Warning: constant too large for short: " + val);
	  return factory.createLiteral(new Short((short)val));
	case T_BYTE:
	  if((val > Byte.MAX_VALUE) || (val < Byte.MIN_VALUE))
	    System.err.println("Warning: constant too large for byte: " + val);
	  return factory.createLiteral(new Byte((byte)val));
	case T_CHAR:
	  if((val > Character.MAX_VALUE) || (val < Character.MIN_VALUE))
	    System.err.println("Warning: constant too large for char: " + val);
	  return factory.createLiteral(new Character((char)val));
	case T_LONG:
	  return factory.createLiteral(new Long((long)val));
	}
      }

      return null;
    } else if(expr instanceof UnaryOperation) {
      UnaryOperation unop = (UnaryOperation)expr;
      if((unop.getOperand() instanceof LiteralImpl) && (unop.operator().equals("-"))) {
        LiteralImpl l     = ((LiteralImpl)unop.getOperand()).copy();
        Object      value = l.constantValue();
      
	if(value instanceof Integer)
	  l.constantValue(new Integer(-((Integer)value).intValue()));
	else if(value instanceof Long)
	  l.constantValue(new Long(-((Long)value).longValue()));
	else if(value instanceof Double)
	  l.constantValue(new Double(-((Double)value).doubleValue()));
	else if(value instanceof Float)
	  l.constantValue(new Float(-((Float)value).floatValue()));
	else if(value instanceof Short)
	  l.constantValue(new Short((short)-((Short)value).shortValue()));
	else if(value instanceof Byte)
	  l.constantValue(new Byte((byte)-((Byte)value).byteValue()));
	else
	  l = null;
	
	return l;
      }
    }

    return null;
  }

  private static final Instruction createBinaryIntOp(char first, String op) {
    switch(first) {
    case '-' : return InstructionConstants.ISUB;
    case '+' : return InstructionConstants.IADD;
    case '%' : return InstructionConstants.IREM;
    case '*' : return InstructionConstants.IMUL;
    case '/' : return InstructionConstants.IDIV;
    case '&' : return InstructionConstants.IAND;
    case '|' : return InstructionConstants.IOR;
    case '^' : return InstructionConstants.IXOR;
    case '<' : return InstructionConstants.ISHL;
    case '>' : return op.equals(">>>")? (Instruction)InstructionConstants.IUSHR :
      (Instruction)InstructionConstants.ISHR;
    default: throw new RuntimeException("Invalid operand " + op);
    }
  }

  private static final Instruction createBinaryLongOp(char first, String op) {
    switch(first) {
    case '-' : return InstructionConstants.LSUB;
    case '+' : return InstructionConstants.LADD;
    case '%' : return InstructionConstants.LREM;
    case '*' : return InstructionConstants.LMUL;
    case '/' : return InstructionConstants.LDIV;
    case '&' : return InstructionConstants.LAND;
    case '|' : return InstructionConstants.LOR;
    case '^' : return InstructionConstants.LXOR;
    case '<' : return InstructionConstants.LSHL;
    case '>' : return op.equals(">>>")? (Instruction)InstructionConstants.LUSHR :
      (Instruction)InstructionConstants.LSHR;
    default: throw new RuntimeException("Invalid operand " + op);
    }
  }

  private static final Instruction createBinaryFloatOp(char op) {
    switch(op) {
    case '-' : return InstructionConstants.FSUB;
    case '+' : return InstructionConstants.FADD;
    case '*' : return InstructionConstants.FMUL;
    case '/' : return InstructionConstants.FDIV;
    default: throw new RuntimeException("Invalid operand " + op);
    }
  }

  private static final Instruction createBinaryDoubleOp(char op) {
    switch(op) {
    case '-' : return InstructionConstants.DSUB;
    case '+' : return InstructionConstants.DADD;
    case '*' : return InstructionConstants.DMUL;
    case '/' : return InstructionConstants.DDIV;
    default: throw new RuntimeException("Invalid operand " + op);
    }
  }
  
  /**
   * @return InstructionList ending with a appropiate BranchInstruction
   */
  private final InstructionList createCmp(String op, Type type) {
    InstructionList il = new InstructionList();
    byte            t  = type.getType();

    switch(t) {
    case T_FLOAT:  il.append(InstructionConstants.FCMPG); break;
    case T_DOUBLE: il.append(InstructionConstants.DCMPG); cg.decreaseStack(1); break;
    case T_LONG:   il.append(InstructionConstants.LCMP);  cg.decreaseStack(1); break;
    }
  
    if(op.equals("<")) {
      switch(t) {
      case T_BYTE: case T_SHORT: case T_CHAR: case T_INT:   
	il.append(new IF_ICMPLT(null));	break;
      case T_FLOAT: case T_DOUBLE: case T_LONG:
	il.append(new IFLT(null)); break;
      default:
	throw new RuntimeException("< " + type);
      }
    } else if(op.equals(">")) {
      switch(t) {
      case T_BYTE: case T_SHORT: case T_CHAR: case T_INT:   
	il.append(new IF_ICMPGT(null)); break;
      case T_FLOAT: case T_DOUBLE: case T_LONG:
	il.append(new IFGT(null)); break;
      default:
	throw new RuntimeException("> " + type);
      }
    } else if(op.equals("<=")) {
      switch(t) {
      case T_BYTE: case T_SHORT: case T_CHAR: case T_INT:   
	il.append(new IF_ICMPLE(null)); break;
      case T_FLOAT: case T_DOUBLE: case T_LONG:
	il.append(new IFLE(null)); break;
      default:
	throw new RuntimeException("<= " + type);
      }
    } else if(op.equals(">=")) {
      switch(t) {
      case T_BYTE: case T_SHORT: case T_CHAR: case T_INT:   
	il.append(new IF_ICMPGE(null)); break;
      case T_FLOAT: case T_DOUBLE: case T_LONG:
	il.append(new IFGE(null)); break;
      default:
	throw new RuntimeException(">= " + type);
      }
    } else if(op.equals("==")) {
      switch(t) {
      case T_BYTE: case T_SHORT: case T_CHAR: case T_INT: case T_BOOLEAN:
	il.append(new IF_ICMPEQ(null)); break;
      case T_FLOAT: case T_DOUBLE: case T_LONG:
	il.append(new IFEQ(null)); break;
      case T_OBJECT: case T_ARRAY:
	il.append(new IF_ACMPEQ(null)); break;
      default:       throw new RuntimeException("== " + type);
      }
    } else if(op.equals("!=")) {
      switch(t) {
      case T_BYTE: case T_SHORT: case T_CHAR: case T_INT: case T_BOOLEAN:
	il.append(new IF_ICMPNE(null)); break;
      case T_FLOAT: case T_DOUBLE: case T_LONG:
	il.append(new IFNE(null)); break;
      case T_OBJECT: case T_ARRAY:
	il.append(new IF_ACMPNE(null)); break;
      default:       throw new RuntimeException("!= " + type);
      }
    } else
      throw new RuntimeException("Unknown operator " + op);

    // stack size computation
    switch(t) {
      case T_BYTE: case T_SHORT: case T_CHAR: case T_INT: case T_OBJECT: case T_ARRAY:
	cg.decreaseStack(2); break;
      case T_FLOAT: case T_DOUBLE: case T_LONG:
	cg.decreaseStack(1); break;
    }

    BranchHandle branch = (BranchHandle)il.getEnd();
    il.append(InstructionConstants.ICONST_0);
    BranchHandle g = il.append(new GOTO(null));
    branch.setTarget(il.append(InstructionConstants.ICONST_1));
    g.setTarget(il.append(InstructionConstants.NOP));

    //if((cg.optimize & 1) == 0) // TODO
    cg.increaseStack(1);

    return il;
  }

  public final InstructionList createBinaryBooleanOperation(BinaryOperation op) {
    AExpression     expr1    = op.getLeftOperand();
    Type            type1    = Conversion.getType(expr1.type());
    AExpression     expr2    = op.getRightOperand();
    Type            type2    = Conversion.getType(expr2.type());
    String          operator = op.operator();
    InstructionList il;

    if(operator.equals("&&")) { // stack-neutral
      il = codeFor(expr1);
      BranchHandle if1 = il.append(new IFFALSE(null));
      il.append(codeFor(expr2));
      BranchHandle if2 = il.append(new IFTRUE(null));
      if1.setTarget(il.append(InstructionConstants.ICONST_0));
      BranchHandle g = il.append(new GOTO(null));
      if2.setTarget(il.append(InstructionConstants.ICONST_1));
      g.setTarget(il.append(InstructionConstants.NOP));
    } else if(operator.equals("||")) {
      il = codeFor(expr1);
      BranchHandle if1 = il.append(new IFTRUE(null));
      il.append(codeFor(expr2));
      BranchHandle if2 = il.append(new IFTRUE(null));
      il.append(InstructionConstants.ICONST_0);
      BranchHandle g = il.append(new GOTO(null));
      InstructionHandle ih = il.append(InstructionConstants.ICONST_1);
      if1.setTarget(ih);
      if2.setTarget(ih);
      g.setTarget(il.append(InstructionConstants.NOP));
    } else if(operator.equals("&") || operator.equals("|")) {
      il = codeFor(expr1);
      il.append(codeFor(expr2));
      il.append(operator.equals("&")? (Instruction)InstructionConstants.IAND :
		(Instruction)InstructionConstants.IOR); 
      BranchHandle if1 = il.append(new IFTRUE(null));
      il.append(InstructionConstants.ICONST_0);
      BranchHandle g = il.append(new GOTO(null));
      if1.setTarget(il.append(InstructionConstants.ICONST_1));
      g.setTarget(il.append(InstructionConstants.NOP));
    } else {
      il = codeFor(expr1);
      il.append(codeFor(expr2));

      cg.checkForCast(type2, type1, il);
      il.append(createCmp(operator, type1));
    }

    return il;
  }

  public final InstructionList createUnaryBooleanOperation(UnaryOperation op) {
    AExpression     expr     = op.getOperand();
    String          operator = op.operator();
    InstructionList il;

    if(operator.equals("!")) { // stack-neutral
      il = codeFor(expr);
      BranchHandle if1 = il.append(new IFFALSE(null));
      if1.setTarget(il.append(InstructionConstants.ICONST_0));
      BranchHandle g = il.append(new GOTO(null));
      if1.setTarget(il.append(InstructionConstants.ICONST_1));
      g.setTarget(il.append(InstructionConstants.NOP));
    } else
      throw new RuntimeException("Unknown boolean operator " + operator);

    return il;
  }

  /**
   * Special cases boolean and string are handled in CodeGenerator.visitBinaryOperation
   */
  public final Instruction createBinaryOperation(BinaryOperation op) {
    return createBinaryOperation(op.operator(), Conversion.getType(op.type()));
  }
  
  /**
   * Create BinOp for simple basic types, such as int and float
   */
  public final Instruction createBinaryOperation(String op, Type type) {
    cg.decreaseStack(type.getSize()); // two args popped, one pushed

    char first = op.toCharArray()[0];

    switch(type.getType()) {
    case T_BYTE:
    case T_SHORT:
    case T_INT:
    case T_CHAR:    return createBinaryIntOp(first, op);
    case T_LONG:    return createBinaryLongOp(first, op);
    case T_FLOAT:   return createBinaryFloatOp(first);
    case T_DOUBLE:  return createBinaryDoubleOp(first);
    default:        throw new RuntimeException("Invalid type " + type);
    }
  }

  /**
   * Only produce a value on stack if the container of this expression is not
   * an ExpressionStatement.
   */
  private static final boolean produceValue(ALValue lvalue) {
    /* The success of that cast is assured from the calling context
     */
    Object c = ((UnaryOperation)lvalue.container()).container();

    // TODO: change sloppy check
    return !((c instanceof ExpressionStatement) || (c instanceof For));
  }

  /**
   * Create code for i++ or --a
   */
  public final InstructionList createInc(ALValue lvalue, int inc, boolean postfix) {
    InstructionList il            = new InstructionList();
    Type            type          = Conversion.getType(lvalue.type());
    byte            t             = type.getType();
    boolean         produce_value = produceValue(lvalue);

    // Special case for IINC operation
    if((lvalue instanceof VariableAccess) && (t == T_INT)) {
      LocalVariableGen lg  = cg.local_vars.get(((VariableAccess)lvalue).getVariable());

      if(produce_value && postfix) // either ...
	il.append(createLoad(Type.INT, lg.getIndex()));

      il.append(new IINC(lg.getIndex(), inc)); // stack not changed

      if(produce_value && !postfix) // or ...
	il.append(createLoad(Type.INT, lg.getIndex()));
    } else {
      /* Push reference (and index for arrays) on stack.
       */
      if(lvalue instanceof ArrayAccess) {
	ArrayAccess a = (ArrayAccess)lvalue;
	
	il.append(codeFor(a.getArray()));
	il.append(codeFor(a.getIndex()));
	il.append(createDup(2)); // array ref and index needed two times on stack
      } else if(lvalue instanceof InstanceFieldAccess) {
	il.append(codeFor(((InstanceFieldAccess)lvalue).getInstance()));
	il.append(createDup(1)); // object ref needed two times on stack
      }

      il.append(createLoadAccess(lvalue)); // Consumes first reference if array/field

      if(produce_value && postfix) // Need old value on stack
	il.append(createDup(lvalue));

      /* Produce code for increment operation
       */
      switch(t) {
      case T_INT:
      case T_BYTE:
      case T_SHORT:
      case T_CHAR:
	il.append(new PUSH(cp, (int)1));
	cg.increaseStack(2); // var + 1 on stack
	il.append((inc > 0)? (Instruction)InstructionConstants.IADD : (Instruction)InstructionConstants.ISUB);
	cg.decreaseStack(1); // two args popped, one pushed
	
	// Check for possible overflow.
	switch(t) {
	case T_BYTE:  il.append(InstructionConstants.I2B); break;
	case T_SHORT: il.append(InstructionConstants.I2S); break;
	case T_CHAR:  il.append(InstructionConstants.I2C); break;
	}
	break;

      case T_FLOAT:
	il.append(new PUSH(cp, (float)1));
	cg.increaseStack(2); // var + 1 on stack
	il.append((inc > 0)? (Instruction)InstructionConstants.FADD :
		  (Instruction)InstructionConstants.FSUB);
	cg.decreaseStack(1); // two args popped, one pushed
	break;
	
      case T_DOUBLE:
	il.append(new PUSH(cp, (double)1));
	cg.increaseStack(4); // var + 1 on stack
	il.append((inc > 0)? (Instruction)InstructionConstants.DADD :
		  (Instruction)InstructionConstants.DSUB);
	cg.decreaseStack(2); // two args popped, one pushed
	break;
      
      case T_LONG:
	il.append(new PUSH(cp, (long)1));
	cg.increaseStack(4); // var + 1 on stack
	il.append((inc > 0)? (Instruction)InstructionConstants.LADD :
		  (Instruction)InstructionConstants.LSUB);
	cg.decreaseStack(2); // two args popped, one pushed
	break;
      
      default: throw new RuntimeException("Invalid operation");
      }

      if(produce_value && !postfix) // Need new value on stack
	il.append(createDup(lvalue));

      il.append(createStoreAccess(lvalue));
    }

    return il;
  }
  
  // Auxiliary function
  private final Instruction createDup(ALValue lvalue) {
    int size = Conversion.getType(lvalue.type()).getSize();

    if(lvalue instanceof ArrayAccess) // Put value down by two
      return createDup_2(size);
    else if(lvalue instanceof InstanceFieldAccess) // Put value down by one
      return createDup_1(size);
    else // just duplicate old value
      return createDup(size);
  }

  // Stack neutral operation
  public final Instruction createUnaryOperation(UnaryOperation o) {
    Type   type = Conversion.getType(o.type());
    char   op   = 'x';
    
    try {
      op = o.operator().charAt(0);
    } catch(StringIndexOutOfBoundsException e) {
      System.err.println(e);
      return null;
    }
	
    switch(type.getType()) {
    case T_BYTE:
    case T_SHORT:
    case T_INT:
    case T_CHAR:    return (op == '-')? (Instruction)InstructionConstants.INEG :
      (Instruction)InstructionConstants.NOP;
    case T_FLOAT:   return (op == '-')? (Instruction)InstructionConstants.FNEG :
      (Instruction)InstructionConstants.NOP;
    case T_DOUBLE:  return (op == '-')? (Instruction)InstructionConstants.DNEG :
      (Instruction)InstructionConstants.NOP;
    case T_LONG:    return (op == '-')? (Instruction)InstructionConstants.LNEG :
      (Instruction)InstructionConstants.NOP;
    case T_BOOLEAN: // handled in create createUnaryBooleanOperation
    default:        throw new RuntimeException("Invalid type " + type);
    }
  }

  public final Instruction createPop(int size) {
    cg.increaseStack(size);
    return (size == 2)? (Instruction)InstructionConstants.POP2 :
      (Instruction)InstructionConstants.POP;
  }

  public final Instruction createDup(int size) {
    cg.increaseStack(size);
    return (size == 2)? (Instruction)InstructionConstants.DUP2 :
      (Instruction)InstructionConstants.DUP;
  }

  public final Instruction createDup_2(int size) {
    cg.increaseStack(size);
    return (size == 2)? (Instruction)InstructionConstants.DUP2_X2 :
      (Instruction)InstructionConstants.DUP_X2;
  }

  public final Instruction createDup_1(int size) {
    cg.increaseStack(size);
    return (size == 2)? (Instruction)InstructionConstants.DUP2_X1 :
      (Instruction)InstructionConstants.DUP_X1;
  }

  public final Instruction createLoadAccess(ALValue lvalue) {
    Type            type = Conversion.getType(lvalue.type());

    if(lvalue instanceof VariableAccess) {
      LocalVariableGen lg = cg.local_vars.get(((VariableAccess)lvalue).getVariable());
      return createLoad(type, lg.getIndex());
    }
    else if(lvalue instanceof ArrayAccess) {
      return createArrayLoad(type);
    }
    else if(lvalue instanceof InstanceFieldAccess) {
      cg.increaseStack(type.getSize());
      return new GETFIELD(getFieldIndex(((InstanceFieldAccess)lvalue).getField()));
    }
    else if(lvalue instanceof StaticFieldAccess) {
      cg.increaseStack(type.getSize());
      return new GETSTATIC(getFieldIndex(((StaticFieldAccess)lvalue).getField()));
    }
    else
      throw new RuntimeException("Unknown ALValue: " + lvalue);
  }

  public final Instruction createStoreAccess(ALValue lvalue) {
    Type type = Conversion.getType(lvalue.type());

    if(lvalue instanceof VariableAccess) {
      LocalVariableGen lg = cg.local_vars.get(((VariableAccess)lvalue).getVariable());
      return createStore(type, lg.getIndex());
    }
    else if(lvalue instanceof ArrayAccess)
      return createArrayStore(type);
    else if(lvalue instanceof InstanceFieldAccess) {
      cg.decreaseStack(type.getSize());
      return new PUTFIELD(getFieldIndex(((InstanceFieldAccess)lvalue).getField()));
    }
    else if(lvalue instanceof StaticFieldAccess) {
      cg.decreaseStack(type.getSize());
      return new PUTSTATIC(getFieldIndex(((StaticFieldAccess)lvalue).getField()));
    }
    else
      throw new RuntimeException("Unknown ALValue: " + lvalue);
  }

  public final Instruction createStore(Type type, int index) {
    cg.decreaseStack(type.getSize());

    switch(type.getType()) {
    case T_BOOLEAN:
    case T_CHAR:
    case T_BYTE:
    case T_SHORT:
    case T_INT:    return new ISTORE(index);
    case T_FLOAT:  return new FSTORE(index);
    case T_DOUBLE: return new DSTORE(index);
    case T_LONG:   return new LSTORE(index);
    case T_ARRAY:
    case T_OBJECT: return new ASTORE(index);
    default:       throw new RuntimeException("Invalid type " + type);
    }
  }

  public final Instruction createLoad(Type type, int index) {
    cg.increaseStack(type.getSize());

    switch(type.getType()) {
    case T_BOOLEAN:
    case T_CHAR:
    case T_BYTE:
    case T_SHORT:
    case T_INT:    return new ILOAD(index);
    case T_FLOAT:  return new FLOAD(index);
    case T_DOUBLE: return new DLOAD(index);
    case T_LONG:   return new LLOAD(index);
    case T_ARRAY:
    case T_OBJECT: return new ALOAD(index);
    default:       throw new RuntimeException("Invalid type " + type);
    }
  }

  /**
   * @param type type of elements of array, i.e. array.getElementType()
   */
  public final Instruction createArrayLoad(Type type) {
    cg.increaseStack(type.getSize() - 2); // array ref and index popped

    switch(type.getType()) {
    case T_BOOLEAN:
    case T_BYTE:   return InstructionConstants.BALOAD;
    case T_CHAR:   return InstructionConstants.CALOAD;
    case T_SHORT:  return InstructionConstants.SALOAD;
    case T_INT:    return InstructionConstants.IALOAD;
    case T_FLOAT:  return InstructionConstants.FALOAD;
    case T_DOUBLE: return InstructionConstants.DALOAD;
    case T_LONG:   return InstructionConstants.LALOAD;
    case T_ARRAY:
    case T_OBJECT: return InstructionConstants.AALOAD;
    default:       throw new RuntimeException("Invalid type " + type);
    }
  }

  /**
   * @param type type of elements of array, i.e. array.getElementType()
   */
  public final Instruction createArrayStore(Type type) {
    cg.decreaseStack(type.getSize() + 2); // array ref and index popped

    switch(type.getType()) {
    case T_BOOLEAN:
    case T_BYTE:   return InstructionConstants.BASTORE;
    case T_CHAR:   return InstructionConstants.CASTORE;
    case T_SHORT:  return InstructionConstants.SASTORE;
    case T_INT:    return InstructionConstants.IASTORE;
    case T_FLOAT:  return InstructionConstants.FASTORE;
    case T_DOUBLE: return InstructionConstants.DASTORE;
    case T_LONG:   return InstructionConstants.LASTORE;
    case T_ARRAY:
    case T_OBJECT: return InstructionConstants.AASTORE;
    default:       throw new RuntimeException("Invalid type " + type);
    }
  }

  public final Instruction createCast(Type src_type, Type dest_type) {
    if((src_type instanceof BasicType) && (dest_type instanceof BasicType)) {
      byte dest = dest_type.getType();
      byte src  = src_type.getType();

      if(dest == T_LONG && (src == T_CHAR || src == T_BYTE || src == T_SHORT))
	src = T_INT;

      String[] short_names = { "C", "F", "D", "B", "S", "I", "L" };

      String name = "org.apache.bcel.generic." + short_names[src - T_CHAR] +
	"2" + short_names[dest - T_CHAR];
      
      Instruction i = null;
      try {
	i = (Instruction)java.lang.Class.forName(name).newInstance();
      } catch(Exception e) {
	throw new RuntimeException("Could not find instruction: " + name);
      }

      cg.increaseStack(dest_type.getSize() - src_type.getSize());
      return i;
    } else if((src_type instanceof ReferenceType) && (dest_type instanceof ReferenceType)) {
      if(dest_type instanceof ArrayType)
	return new CHECKCAST(cp.addArrayClass((ArrayType)dest_type));
      else
	return new CHECKCAST(cp.addClass((ObjectType)dest_type));
    }
    else
      throw new RuntimeException("Can not cast " + src_type + " to " + dest_type);
  }
  
  /** Create synthetic access$0() method for reading an otherwise private field
   */
  Method create_getMethod(barat.reflect.Field field, String method_name) {
    Type            type      = Conversion.getType(field.getType());
    boolean         is_static = field.isStatic();
    InstructionList il        = new InstructionList();
    int             index     = getFieldIndex(field);
    MethodGen       mg        = new MethodGen(is_static? ACC_STATIC : 0, 
					      type, Type.NO_ARGS, null, method_name,
					      classNameOf(field), il, cp);
    if(is_static) {
      il.append(new GETSTATIC(index));
      mg.setMaxLocals(1);
    } else {
      il.append(InstructionConstants.THIS);
      il.append(new GETFIELD(index));
    }

    il.append(createReturn(type));

    mg.setMaxStack(1 + type.getSize());
    mg.addAttribute(new Synthetic(cp.addUtf8("Synthetic"), 0, (byte[])null,
				  cp.getConstantPool()));
    return mg.getMethod();
  }

  /** Create synthetic access$0() method for writing to an otherwise private field
   */
  Method create_setMethod(barat.reflect.Field field, String method_name) {
    Type            type      = Conversion.getType(field.getType());
    boolean         is_static = field.isStatic();
    InstructionList il        = new InstructionList();
    int             index     = getFieldIndex(field);
    MethodGen       mg        = new MethodGen(is_static? ACC_STATIC : 0, 
					      Type.VOID, new Type[] { type }, null,
					      method_name,
					      classNameOf(field), il, cp);
    if(is_static) {
      il.append(InstructionConstants.THIS);
      il.append(new PUTSTATIC(index));
    } else {
      il.append(InstructionConstants.THIS);
      il.append(new ALOAD(1));
      il.append(new PUTFIELD(index));
    }

    il.append(InstructionConstants.RETURN);

    mg.setMaxStack(1 + type.getSize());
    mg.addAttribute(new Synthetic(cp.addUtf8("Synthetic"), 0, (byte[])null,
				  cp.getConstantPool()));
    return mg.getMethod();
  }
  
  /** Create synthetic access$0() method for calling an otherwise private method
   */
  Method create_accessMethod(AMethod method, String class_name, String method_name) {
    InstructionList il = new InstructionList();
    MethodObject    m  = new MethodObject(null, method);
    MethodGen       mg = new MethodGen(m.access ^ ACC_PRIVATE, m.result_type,
				       m.arg_types, m.arg_names,
				       method_name, class_name, il, cp);

    // Implicit this parameter is always in slot 0
    int j    = 0;
    int size = m.result_type.getSize();

    if(!method.isStatic()) {
      il.append(InstructionConstants.THIS);
      j = 1;
      size++;
    }

    for(int i=0; i < m.arg_types.length; i++) {
      il.append(createLoad(m.arg_types[i], i + j));
      size += m.arg_types[i].getSize();
    }

    mg.setMaxStack(size);
    mg.addAttribute(new Synthetic(cp.addUtf8("Synthetic"), 0, (byte[])null,
				  cp.getConstantPool()));

    il.append(createInvoke(m, method.isStatic()? INVOKESTATIC : INVOKEVIRTUAL));
    il.append(createReturn(m.result_type));

    return mg.getMethod();
  }

  public final int getFieldIndex(barat.reflect.Field field) {
    Type type = Conversion.getType(field.getType());

    return cp.addFieldref(classNameOf(field), field.getName(),
			  type.getSignature());
  }
  
  public final int addClassMethod(String class_name) {
    InstructionList il = new InstructionList();
    MethodGen       mg = new MethodGen(ACC_PRIVATE | ACC_STATIC | ACC_FINAL,
				       new ObjectType("java.lang.Class"),
				       new Type[] { Type.STRING },
				       new String[] { "class_name" },
				       "class$", class_name, il, cp);
    mg.setMaxStack(3);
    mg.setMaxLocals(2);

    InstructionHandle start = il.append(InstructionConstants.THIS);

    int index = cp.addMethodref("java.lang.Class", "forName",
				"(Ljava/lang/String;)Ljava/lang/Class;");
    il.append(new INVOKESTATIC(index));

    il.append(InstructionConstants.ARETURN);

    InstructionHandle handler = il.append(new ASTORE(1));

    index = cp.addClass("java.lang.NoClassDefFoundError");
    il.append(new NEW(index));
    il.append(InstructionConstants.DUP);
    il.append(new ALOAD(1));
    index = cp.addMethodref("java.lang.Throwable", "getMessage", "()Ljava/lang/String;");
    il.append(new INVOKEVIRTUAL(index));
    index = cp.addMethodref("java.lang.NoClassDefFoundError", "<init>",
			    "(Ljava/lang/String;)V");
    il.append(new INVOKESPECIAL(index));
    il.append(InstructionConstants.ATHROW);
    
    mg.addExceptionHandler(start, handler, handler, new ObjectType("java.lang.ClassNotFoundException"));
    mg.addAttribute(new Synthetic(cp.addUtf8("Synthetic"), 0, (byte[])null,
				  cp.getConstantPool()));

    cg.clazz.addMethod(mg.getMethod());

    return cp.addMethodref(class_name, "class$", "(Ljava/lang/String;)Ljava/lang/Class;");
  }

  private static final void DEBUG(String s) { System.out.println(s); }

  public static boolean isInnerClass(AUserType type) {
    return type.containing(AUserType.class) != null;
  }
  
  public static boolean isAnonymousClass(barat.reflect.Class clazz) {
    return clazz.container() instanceof AnonymousAllocation;
  }

  public String classNameOf(barat.reflect.Field field) {
    String qname = cg.implementationNameOf(field);
    return qname.substring(0, qname.lastIndexOf('.'));
  }
}
