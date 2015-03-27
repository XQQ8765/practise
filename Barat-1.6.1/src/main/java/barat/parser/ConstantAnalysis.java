/*
 * File: ConstantAnalysis.java
 *
 * $Id: ConstantAnalysis.java,v 1.7 2000/11/20 01:51:04 bokowski Exp $
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
import barat.*;

public class ConstantAnalysis extends AnalysisVisitor
{
  static Object isConstantTag = new Object();
  static Object constantValueTag = new Object();

  public static boolean isConstant(AExpression expr) {
    Boolean b = Attribute.getValue (isConstantTag, expr);
    return b.booleanValue();
  }

  public static Object evaluate(AExpression expr) {
    return Attribute.getValue(constantValueTag, expr);
  }

  public void visitLiteralImpl(final LiteralImpl o)
  {
    o.addAttribute(isConstantTag, new Constant<Boolean>(Boolean.TRUE));
    o.addAttribute(constantValueTag, new Attribute<Object>()
    {
      protected Object calculate()
      {
        return o.constantValue();
      }
    });
  }
  public void visitThisImpl(final ThisImpl o)
  {
    o.addAttribute(isConstantTag, new Constant<Boolean>(Boolean.FALSE));
  }
  public void visitObjectAllocationImpl(final ObjectAllocationImpl o)
  {
    o.addAttribute(isConstantTag, new Constant<Boolean>(Boolean.FALSE));
  }
  public void visitArrayAccessImpl(final ArrayAccessImpl o)
  {
    o.addAttribute(isConstantTag, new Constant<Boolean>(Boolean.FALSE));
  }
  public void visitArrayAllocationImpl(final ArrayAllocationImpl o)
  {
    o.addAttribute(isConstantTag, new Constant<Boolean>(Boolean.FALSE));
  }
  public void visitArrayInitializerImpl(final ArrayInitializerImpl o)
  {
    o.addAttribute(isConstantTag, new Constant<Boolean>(Boolean.FALSE));
  }
  public void visitAssignmentImpl(final AssignmentImpl o)
  {
    o.addAttribute(isConstantTag, new Constant<Boolean>(Boolean.FALSE));
  }
  public void visitBinaryOperationImpl(final BinaryOperationImpl o)
  {
    o.addAttribute(isConstantTag, new Attribute<Boolean>()
    {
      protected Boolean calculate()
      {
        Boolean left = Attribute.getValue(isConstantTag, o.getLeftOperand());
        Boolean right = Attribute.getValue(isConstantTag, o.getRightOperand());
        return new Boolean(left.booleanValue() && right.booleanValue());
      }
    });
    o.addAttribute(constantValueTag, new Attribute<Object>()
    {
      protected Object calculate()
      {
        Object left = Attribute.getValue(constantValueTag, o.getLeftOperand());
        Object right = Attribute.getValue(constantValueTag, o.getRightOperand());
        String op = o.operator();
        if(o.type() == NameAnalysis.getStringClass())
        {
          return "" + left + right;
        }
        if((o.type() instanceof PrimitiveType) && ((PrimitiveType)o.type()).isBoolean())
        {
          switch(op.charAt(0))
          {
            case '&':
              return new Boolean(((Boolean)left).booleanValue() & ((Boolean)right).booleanValue());
            case '|':
              return new Boolean(((Boolean)left).booleanValue() | ((Boolean)right).booleanValue());
            default:
              throw new RuntimeException("cannot evaluate boolean expr: " + o);
          }
        }
        switch(op.charAt(0))
        {
          case '+':
            break;
        }
        throw new RuntimeException("constant value: not yet implemented 1");
      }
    });
  }
  public void visitCastImpl(final CastImpl o)
  {
    o.addAttribute(isConstantTag, new Constant<Boolean>(Boolean.FALSE));
  }
  public void visitConditionalImpl(final ConditionalImpl o)
  {
    o.addAttribute(isConstantTag, new Constant<Boolean>(Boolean.FALSE));
  }
  public void visitInstanceFieldAccessImpl(final InstanceFieldAccessImpl o)
  {
    o.addAttribute(isConstantTag, new Constant<Boolean>(Boolean.FALSE));
  }
  public void visitInstanceMethodCallImpl(final InstanceMethodCallImpl o)
  {
    o.addAttribute(isConstantTag, new Constant<Boolean>(Boolean.FALSE));
  }
  public void visitArrayLengthAccessImpl(final ArrayLengthAccessImpl o)
  {
    o.addAttribute(isConstantTag, new Constant<Boolean>(Boolean.FALSE));
  }
  public void visitParenExpressionImpl(final ParenExpressionImpl o)
  {
    o.addAttribute(isConstantTag, new Attribute<Boolean>() {
      protected Boolean calculate() {
	return Attribute.getValue(isConstantTag, o.getOperand());
      }
    });

    o.addAttribute(constantValueTag, new Attribute<Object>()
    {
      protected Object calculate()
      {
        return Attribute.getValue(constantValueTag, o.getOperand());
      }
    });
  }
  public void visitStaticFieldAccessImpl(final StaticFieldAccessImpl o)
  {
    o.addAttribute(isConstantTag, new Attribute<Boolean>() {
      protected Boolean calculate() {
	return new Boolean(o.getField().isFinal());
      }
    });

    o.addAttribute(constantValueTag, new Attribute<Object>()
    {
      protected Object calculate()
      {
        return Attribute.getValue(constantValueTag, o.getField().getInitializer());
      }
    });
  }
  public void visitInstanceofImpl(final InstanceofImpl o)
  {
    o.addAttribute(isConstantTag, new Constant<Boolean>(Boolean.FALSE));
  }
  public void visitStaticMethodCallImpl(final StaticMethodCallImpl o)
  {
    o.addAttribute(isConstantTag, new Constant<Boolean>(Boolean.FALSE));
  }
  public void visitUnaryOperationImpl(final UnaryOperationImpl o)
  {
    o.addAttribute(isConstantTag, new Attribute<Boolean>()
    {
      protected Boolean calculate()
      {
        Boolean operand = Attribute.getValue(isConstantTag, o.getOperand());
        return new Boolean(operand.booleanValue());
      }
    });

    o.addAttribute(constantValueTag, new Attribute<Object>()
    {
      protected Object calculate()
      {
	AExpression operand = o.getOperand();
	String      operator = o.operator();

	if(operand instanceof Literal) {
	  Object obj = ((Literal)operand).constantValue();

	  if(operator.equals("-")) {
	    if(obj instanceof Integer)
	      return new Integer(-((Integer)obj).intValue());
	    else if(obj instanceof Double)
	      return new Double(-((Double)obj).doubleValue());
	    else if(obj instanceof Float)
	      return new Float(-((Float)obj).floatValue());
	    else
	      throw new RuntimeException("constant value: not a number?: " + obj);
	  } else
	    return obj;
	}
	  
        throw new RuntimeException("constant value: not yet implemented 2");
      }
    });
}
  public void visitVariableAccessImpl(final VariableAccessImpl o)
  {
    o.addAttribute(isConstantTag, new Constant<Boolean>(Boolean.FALSE));
  }
  public void visitAnonymousAllocationImpl(final AnonymousAllocationImpl o)
  {
    o.addAttribute(isConstantTag, new Constant<Boolean>(Boolean.FALSE));
  }
}
