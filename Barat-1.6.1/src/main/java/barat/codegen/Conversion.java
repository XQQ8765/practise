package barat.codegen;

import barat.reflect.*;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import barat.collections.*;

/**
 * Contains adapter methods to convert Barat to JavaClass API types.
 *
 * @version $Id: Conversion.java,v 1.2 2002/01/28 10:48:35 dahm Exp $
 * @author  <A HREF="http://www.inf.fu-berlin.de/~dahm">M. Dahm</A>
 */
public abstract class Conversion implements org.apache.bcel.Constants {

  /** Convert PrimitiveType to byte tag.
   */
  public static final byte getPrimitiveType(PrimitiveType t) {
    if(t.isBoolean()) return T_BOOLEAN;
    if(t.isByte())    return T_BYTE;
    if(t.isChar())    return T_CHAR;
    if(t.isDouble())  return T_DOUBLE;
    if(t.isFloat())   return T_FLOAT;
    if(t.isInt())     return T_INT;
    if(t.isLong())    return T_LONG;
    if(t.isShort())   return T_SHORT;
    
    throw new RuntimeException("Oops: " + t);
  }

  /** Convert AType to Type.
   */
  public static final Type getType(AType t) {
    if(t == null)
      return Type.VOID;
    
    if(t instanceof NullType)
      return Type.NULL;

    if((t instanceof barat.reflect.Class) || (t instanceof barat.reflect.Interface))
      return new ObjectType(CodeGenerator.implementationNameOf(((ANamed)t)));

    if(t instanceof Array) {
      int   dimensions = 1;
      AType type;

      for(type = ((Array)t).getElementType(); type instanceof Array;
	  type = ((Array)type).getElementType()) {
	dimensions++;
      }

      return new ArrayType(getType(type), dimensions);
    }

    if(t instanceof PrimitiveType)
      return BasicType.getType(getPrimitiveType((PrimitiveType)t));

    throw new RuntimeException("Can't convert type: " + t.getClass());
  }

  /** Convert list of parameters.
   */
  public static final Type[] getParameters(ParameterList params) {
    Type[] types = new Type[params.size()];
    int    i     = 0;

    for(ParameterIterator pit=params.iterator(); pit.hasNext(); i++)
      types[i] = getType(pit.next().getType());

    return types;
  }

  /** Convert modifier to bit flags.
   */
  public static final short getAccessFlags(AHasModifier m) {
    short flags = m.isAbstract()? ACC_ABSTRACT : 0;
    flags |= m.isFinal()? ACC_FINAL : 0;
    flags |= m.isNative()?  ACC_NATIVE : 0;
    flags |= m.isPrivate()?  ACC_PRIVATE : 0;
    flags |= m.isProtected()?  ACC_PROTECTED : 0;
    flags |= m.isPublic()?  ACC_PUBLIC : 0;
    flags |= m.isStatic()?  ACC_STATIC : 0;
    flags |= m.isSynchronized()?  ACC_SYNCHRONIZED : 0;
    flags |= m.isTransient()?  ACC_TRANSIENT : 0;
    flags |= m.isVolatile()?  ACC_VOLATILE : 0;

    return flags;
  }

  public static final String getMethodSignature(AType result_type, ParameterList params) {
    Type[] args = getParameters(params);
    Type   ret  = getType(result_type);
    
    return Type.getMethodSignature(ret, args);
  }
}
