/*
 * File: TypeAnalysis.java
 *
 * $Id: TypeAnalysis.java,v 1.30 2003/07/24 10:48:10 bokowski Exp $
 *
 * This file is part of Barat.
 * Copyright (c) 1998-2000 Boris Bokowski (bokowski@users.sourceforge.net)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 *   Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 *   Neither the name of Boris Bokowski nor the names of his contributors
 *   may be used to endorse or promote products derived from this
 *   software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BORIS BOKOWSKI
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package barat.parser;

import barat.reflect.*;
import barat.reflect.Class;
import barat.*;
import barat.collections.ConcreteMethodIterator;
import barat.collections.AbstractMethodIterator;
import barat.collections.InterfaceIterator;
import java.util.*;
//import java.util.*;

public class TypeAnalysis extends Typing
{
  static Attribute<AType> createTypeAttributeFromSignature(String sig)
  {
    char[] chs = sig.toCharArray();

    /**
     * The field signature represents the value of an argument to a function or 
     * the value of a variable. It is a series of bytes generated by the 
     * following grammar:
     *
     * <field_signature> ::= <field_type>
     * <field_type>      ::= <base_type>|<object_type>|<array_type>
     * <base_type>       ::= B|C|D|F|I|J|S|Z
     * <object_type>     ::= L<fullclassname>;
     * <array_type>      ::= [<field_type>
     * <optional_size>   ::= [0-9]*
     *
     * The meaning of the base types is as follows:
     * B byte signed byte
     * C char character
     * D double double precision IEEE float
     * F float single precision IEEE float
     * I int integer
     * J long long integer
     * L<fullclassname>; ... an object of the given class
     * S short signed short
     * Z boolean true or false
     * [<field sig> ... array
     *
    */
    switch(chs[0]) {
      case 'B' : return new TypeConstant<AType>("byte", factory().getByte());
      case 'C' : return new TypeConstant<AType>("char", factory().getChar());
      case 'D' : return new TypeConstant<AType>("double", factory().getDouble());
      case 'F' : return new TypeConstant<AType>("float", factory().getFloat());
      case 'I' : return new TypeConstant<AType>("int", factory().getInt());
      case 'J' : return new TypeConstant<AType>("long", factory().getLong());

      case 'L' :
      { // Full class name
        int index = sig.indexOf(';'); // Look for closing `;'

        if(index < 0)
        {
          throw new RuntimeException("Invalid signature: " + sig);
        }

        return NameAnalysis.createUserTypeAttribute(
            QualifiedName.from(
              sig.substring(1, index).replace('/','.')),
              null);
      }

      case 'S' : return new TypeConstant<AType>("short", factory().getShort());
      case 'Z' : return new TypeConstant<AType>("boolean", factory().getBoolean());

      case '[' :
      { // Array declaration
        int dimensions = 0;

        // Count opening brackets
        int n=0;
        while(n < sig.length())
        {
          char ch = chs[n];

          if(ch != '[') break;

          n++;
          dimensions++;
        }

        // The rest of the string denotes a `<field_type>'
        Attribute<AType> type = createTypeAttributeFromSignature(sig.substring(n));
        for(int i=0; i<dimensions; i++)
        {
          type = NameAnalysis.createArrayAttribute(type);
        }  
 
        return type;
      }

      case 'V' : return null;

      default  : throw new RuntimeException("huh? sig: " + sig);
    }
  }

  public static void addTypeAnalysis(final ArrayAccessImpl o)
  {
    o.type(new Attribute<AType>()
      {
        protected AType calculate()
        {
          Array at = (Array)o.getArray().type();
          return at.getElementType();
        }
      });
  }

  public static void addTypeAnalysis(final ArrayAllocationImpl o)
  {
    o.type(new Attribute<AType>()
      {
        protected AType calculate()
        {
          AType at = o.getElementType();
          for(int i=0; i<o.getArguments().size()+o.freeDimensions(); i++)
          {
            at = factory().createArray(new Constant<AType>(at));
          }
          return at;
        }
      });
  }

  public static void addTypeAnalysis(final ArrayInitializerImpl o)
  {
    o.type(new Attribute<AType>()
      {
        protected AType calculate()
        {
          // we need information about the initialized array's type...
	  int numberOfParentsUntilArrayAllocation = 1;
	  Node maybeArrayAllocation = o.container();
	  while(!(maybeArrayAllocation instanceof ArrayAllocation)) {
      	    maybeArrayAllocation = maybeArrayAllocation.container();
            numberOfParentsUntilArrayAllocation++;
          }
	  AType typeOfInitializer = ((ArrayAllocation)maybeArrayAllocation).getElementType();
	  int freeDimensions = ((ArrayAllocation)maybeArrayAllocation).freeDimensions();
	  for(int i=1; i<numberOfParentsUntilArrayAllocation+1-freeDimensions; i++) {
	    typeOfInitializer = ((Array)typeOfInitializer).getElementType();
	  }
          AType result = factory().createArray(new Constant<AType>(typeOfInitializer));
	  for(int i=1; i<freeDimensions; i++) {
	      result = factory().createArray(new Constant<AType>(result));
	  }
	  return result;
        }
      });
  }

  public static void addTypeAnalysis(final AssignmentImpl o)
  {
    o.type(new Attribute<AType>()
      {
        protected AType calculate()
        {
          return o.getLvalue().type();
        }
      });
  }

  public static void addTypeAnalysis(final BinaryOperationImpl o)
  {
    o.type(new Attribute<AType>()
      {
        protected AType calculate()
        {
          AType left = o.getLeftOperand().type();
          AType right = o.getRightOperand().type();
          String op = o.operator();
          if(op.equals("*") || op.equals("/") || op.equals("%"))
          {
            do_assert(isNumeric(left), o, "left operand has non-numeric type");
            do_assert(isNumeric(right), o, "right operand has non-numeric type");
            return promoted((PrimitiveType)left, (PrimitiveType)right);
          }
          if(op.equals("+"))
          {
            Class java_lang_string = NameAnalysis.getStringClass();
            if(left==java_lang_string || right==java_lang_string)
            {
              return java_lang_string;
            }
          }
          if(op.equals("+") || op.equals("-"))
          {
            do_assert(isNumeric(left), o, "left operand has non-numeric type");
            do_assert(isNumeric(right), o, "right operand has non-numeric type");
            return promoted((PrimitiveType)left, (PrimitiveType)right);
          }
          if(op.equals("<<") || op.equals(">>") || op.equals(">>>"))
          {
            do_assert(isNumeric(left), o, "left operand has non-numeric type");
            do_assert(isNumeric(right), o, "right operand has non-numeric type");
            return promoted((PrimitiveType)left);
          }
          if(op.equals(">") || op.equals("<") || op.equals(">=") || op.equals("<="))
          {
            do_assert(isNumeric(left), o, "left operand has non-numeric type");
            do_assert(isNumeric(right), o, "right operand has non-numeric type");
            return factory().getBoolean();
          }
          if(op.equals("==") || op.equals("!="))
          {
            do_assert(isNumeric(left)==isNumeric(right), o, "numeric and non-numeric type compared");
            do_assert(isBoolean(left)==isBoolean(right), o, "boolean and non-boolean type compared");
            do_assert((left instanceof AReferenceType)==(right instanceof AReferenceType), o, "reference and non-reference type compared");
            do_assert((left instanceof PrimitiveType) || isCastableTo(left, right) || isCastableTo(right, left), o, "objects would necessarily be unequal");
            return factory().getBoolean();
          }
          if(op.equals("&") || op.equals("|") || op.equals("^"))
          {
            do_assert(isIntegral(left)==isIntegral(right), o, "integral and non-integral type in integer bitwise operation");
            do_assert(isBoolean(left)==isBoolean(right), o, "boolean and non-boolean type in boolean logical operation");
            if(isBoolean(left)) return left;
            return promoted((PrimitiveType)left, (PrimitiveType)right);
          }
          if(op.equals("&&") || op.equals("||"))
          {
            do_assert(isBoolean(left), o, "left operand is of non-boolean type");
            do_assert(isBoolean(right), o, "right operand is of non-boolean type");
            return left;
          }
          throw new RuntimeException("Huh?");
        }
      });
  }

  public static void addTypeAnalysis(final CastImpl o)
  {
    o.type(new Attribute<AType>()
      {
        protected AType calculate()
        {
          return o.getCastType();
        }
      }
    );
  }

  private static AUserType enclosingUserType(Scope s) {
	Scope enclosingScope = s.getEnclosingScope();
	if(enclosingScope==null) return null;
	if(enclosingScope instanceof AUserType) return (AUserType)enclosingScope;
	return enclosingUserType(enclosingScope);
  }

  public static void addTypeAnalysis(final ClassImpl o)
  {
    o.setMethodList(new Attribute<List<AMethod>>()
      {
        void addMethod(AMethod m, List<AMethod> l)
        {
            for(Iterator<AMethod> i=l.iterator(); i.hasNext();)
            {
              AMethod am = i.next();
              if(equalSignature(m, am))
              {
                do_assert(isEqual(m.getResultType(), am.getResultType()), o, "two methods with equal signatures but different return types");
		((AMethodImpl)am).setOverriddenMethod(m);
                return;
              }
            }
            l.add(m);
        }
        protected List<AMethod> calculate()
        {
          List<AMethod> result = new ArrayList<AMethod>();
          for(ConcreteMethodIterator i=o.getConcreteMethods().iterator(); i.hasNext();)
          {
            addMethod(i.next(), result);
          }
          for(AbstractMethodIterator i=o.getAbstractMethods().iterator(); i.hasNext();)
          {
            addMethod(i.next(), result);
          }
          if(o!=NameAnalysis.getObjectClass())
          {
            for(Iterator<AMethod> i=((AUserTypeImpl)o.getSuperclass()).getMethodList().iterator(); i.hasNext();)
            {
              addMethod(i.next(), result);
            }
          }
          for(InterfaceIterator i=o.getImplementedInterfaces().iterator(); i.hasNext();)
          {
            for(Iterator<AMethod> i2=((AUserTypeImpl)i
            .next())
            .getMethodList()
            .iterator(); i2
            .hasNext();)
            {
              addMethod(i2
              .next(), result);
            }
          }
          AUserTypeImpl outer = (AUserTypeImpl)enclosingUserType(o);
          if(outer!=null)
          {
            // current class is inner class
            for(Iterator<AMethod> i2=outer.getMethodList().iterator(); i2.hasNext();)
            {
              addMethod(i2.next(), result);
            }
          }
          return result;
        }
      }
    );
  }

  public static void addTypeAnalysis(final ConditionalImpl o)
  {
    o.type(new Attribute<AType>()
      {
        protected AType calculate()
        {
	  AType iftrue = o.getIfTrue().type();
	  AType iffalse = o.getIfFalse().type();
	  if(isPassableTo(iftrue, iffalse))
	  {
	    return iffalse;
	  }
	  else if(isPassableTo(iffalse, iftrue))
	  {
	    return iftrue;
	  }
	  else
	  {
	    throw new RuntimeException("type error in conditional expression");
	  }
        }
      }
    );
  }

  public static void addTypeAnalysis(final AFieldAccessImpl o)
  {
    o.type(new Attribute<AType>()
      {
        protected AType calculate()
        {
          return o.getField().getType();
        }
      }
    );
  }

  public static void addTypeAnalysis(final AMethodCallImpl o)
  {
    o.type(new Attribute<AType>()
      {
        protected AType calculate()
        {
          return o.getCalledMethod().getResultType();
        }
      }
    );
  }

  public static void addTypeAnalysis(final InstanceofImpl o)
  {
    o.type(new Attribute<AType>()
      {
        protected AType calculate()
        {
          do_assert(o.getOperand().type() instanceof AReferenceType, o,
            "instanceof operator applied to non-reference type");
          return factory().getBoolean();
        }
      }
    );
  }

  public static void addTypeAnalysis(final InterfaceImpl o)
  {
    o.setMethodList(new Attribute<List<AMethod>>()
      {
        void addMethod(AMethod m, List<AMethod> l)
        {
          for(Iterator<AMethod> i=l.iterator(); i.hasNext();)
          {
            AMethod am = i.next();
            if(equalSignature(m, am))
            {
              do_assert(isEqual(m.getResultType(), am.getResultType()), o, "two methods with equal signatures but different return types");
	      ((AMethodImpl)am).setOverriddenMethod(m);
              return;
            }
          }
          l.add(m);
        }
        protected List<AMethod> calculate()
        {
          List<AMethod> result = new ArrayList<AMethod>();
          for(AbstractMethodIterator i=o.getAbstractMethods().iterator(); i.hasNext();)
          {
            addMethod(i.next(), result);
          }
          boolean isToplevel = true;
          for(InterfaceIterator i=o.getExtendedInterfaces().iterator(); i.hasNext();)
          {
            isToplevel = false;
            for(Iterator<AMethod> i2=((AUserTypeImpl)i.next()).getMethodList().iterator(); i2.hasNext();)
            {
              addMethod(i2.next(), result);
            }
          }
          if(isToplevel)
          {
            AUserTypeImpl java_lang_Object = NameAnalysis.lookupUserType(QualifiedName.from("java.lang.Object"));
            for(Iterator<AMethod> i2=java_lang_Object.getMethodList().iterator(); i2.hasNext();)
            {
              addMethod(i2.next(), result);
            }
          }
          AUserTypeImpl outer = (AUserTypeImpl)enclosingUserType(o);
          if(outer!=null)
          {
            // current class is inner class
            for(Iterator<AMethod> i2=outer.getMethodList().iterator(); i2.hasNext();)
            {
              addMethod(i2.next(), result);
            }
          }
          return result;
        }
      }
    );
  }

  public static void addTypeAnalysis(final LiteralImpl o)
  {
    o.type(new Attribute<AType>()
      {
        protected AType calculate()
        {
          Object val = o.constantValue();
          if(val instanceof Boolean) return factory().getBoolean();
          if(val instanceof Byte) return factory().getByte();
          if(val instanceof Character) return factory().getChar();
          if(val instanceof Short) return factory().getShort();
          if(val instanceof Integer) return factory().getInt();
          if(val instanceof Long) return factory().getLong();
          if(val instanceof Float) return factory().getFloat();
          if(val instanceof Double) return factory().getDouble();
          if(val instanceof String) return NameAnalysis.getStringClass();
          return factory().getNullType();
        }
      }
    );
  }

  public static void addTypeAnalysis(final ObjectAllocationImpl o)
  {
    o.type(new Attribute<AType>()
      {
        protected AType calculate()
        {
          return o.getCalledConstructor().containingClass();
        }
      }
    );
  }

  public static void addTypeAnalysis(final AnonymousAllocationImpl o)
  {
    o.type(new Attribute<AType>()
      {
        protected AType calculate()
        {
          return o.getAnonymousClass();
        }
      }
    );
  }

  public static void addTypeAnalysis(final ArrayLengthAccessImpl o)
  {
    o.type(new Attribute<AType>()
      {
        protected AType calculate()
        {
          return factory().getInt();
        }
      }
    );
  }

  public static void addTypeAnalysis(final ParenExpressionImpl o)
  {
    o.type(new Attribute<AType>()
      {
        protected AType calculate()
        {
          return o.getOperand().type();
        }
      }
    );
  }

  public static void addTypeAnalysis(final ThisImpl o)
  {
    o.type(new Attribute<AType>()
      {
        protected AType calculate()
        {
          if(o.isSuper())
          {
            barat.reflect.Class c = (barat.reflect.Class)o.getThisClass();
            return c.getSuperclass();
          }
          else
          {
            return o.getThisClass();
          }
        }
      }
    );
  }

  public static void addTypeAnalysis(final UnaryOperationImpl o)
  {
    o.type(new Attribute<AType>()
      {
        protected AType calculate()
        {
          AType operand = o.getOperand().type();
          String op = o.operator();
          if(op.equals("++") || op.equals("--"))
          {
            do_assert(isNumeric(operand), o, "operand has non-numeric type");
            do_assert(o.getOperand() instanceof ALValue, o, "operand must be a lvalue");
            return operand;
          }
          if(op.equals("+") || op.equals("-") || op.equals("~"))
          {
            do_assert(isNumeric(operand), o, "operand has non-numeric type");
            return promoted((PrimitiveType)operand);
          }
          if(op.equals("!"))
          {
            do_assert(isBoolean(operand), o, "operand has non-boolean type");
            return operand;
          }
          throw new RuntimeException("Huh?");
        }
      }
    );
  }

  public static void addTypeAnalysis(final VariableAccessImpl o)
  {
    o.type(new Attribute<AType>()
      {
        protected AType calculate()
        {
          return o.getVariable().getType();
        }
      }
    );
  }

  public static void addTypeAnalysis(final ClassExpressionImpl o)
  {
    o.type(new Attribute<AType>()
      {
        protected AType calculate()
        {
          return NameAnalysis.getClassClass();
        }
      }
    );
  }

  private static void do_assert(boolean condition, Object o, String msg)
  {
    if(!condition) throw new RuntimeException("Assertion failed for " + o + " : " + msg);
  }
}
