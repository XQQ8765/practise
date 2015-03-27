/*
 * File: Typing.java
 *
 * $Id: Typing.java,v 1.6 2000/11/20 01:51:10 bokowski Exp $
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
import barat.parser.Factory;
import barat.collections.*;

public class Typing
{
  protected static Factory factory() {
    return Factory.getInstance();
  }

  static private boolean noReturnTypeConflicts (Interface a,
                                                Interface b) {
    AbstractMethodList methodsA = a.getAbstractMethods(),
                       methodsB = b.getAbstractMethods();
    for (AbstractMethodIterator ia = methodsA.iterator(); ia.hasNext();) {
      for (AbstractMethodIterator ib = methodsB.iterator(); ib.hasNext();) {
        AbstractMethod methodA = ia.next();
        AbstractMethod methodB = ib.next();
        if (   equalSignature (methodA, methodB) 
            && !isEqual (methodA.getResultType(), methodB.getResultType()))
          return false;
      }
    }
    return true;
  }

  /**
   * § 5.1.1
   */
  static private boolean isIdentityConvertible (AType a, AType b) {
    return isEqual (a, b);
  }

  /**
   * § 5.1.2
   */
  static private boolean isWideningPrimitiveConvertible (AType a, AType b) {
    if (!(a instanceof PrimitiveType) || !(b instanceof PrimitiveType))
      return false;
    else {
      PrimitiveType pa = (PrimitiveType)a;
      PrimitiveType pb = (PrimitiveType)b;
      if (pa.isByte())
        return pb.isShort() || pb.isInt() || pb.isLong() || 
               pb.isFloat() || pb.isDouble();
      else if (pa.isShort() || pa.isChar())
        return pb.isInt() || pb.isLong() || pb.isFloat() || pb.isDouble();
      else if (pa.isInt())
        return pb.isLong() || pb.isFloat() || pb.isDouble();
      else if (pa.isLong())
        return pb.isFloat() || pb.isDouble();
      else if (pa.isFloat())
        return pb.isDouble();
      else 
        return false; // never reached
    }
  }

  /**
   * § 5.1.3
   */
  static private boolean isNarrowingPrimitiveConvertible (AType a, AType b) {
    if (!(a instanceof PrimitiveType) || !(b instanceof PrimitiveType))
      return false;
    else {
      PrimitiveType pa = (PrimitiveType)a;
      PrimitiveType pb = (PrimitiveType)b;
      if (pa.isByte())
        return pb.isChar();
      else if (pa.isShort())
        return pb.isByte() || pb.isChar();
      else if (pa.isChar())
        return pb.isByte() || pb.isShort();
      else if (pa.isInt())
        return pb.isByte() || pb.isShort() || pb.isChar();
      else if (pa.isLong())
        return pb.isByte() || pb.isShort() || pb.isChar() || pb.isInt();
      else if (pa.isFloat())
        return pb.isByte() || pb.isShort() || pb.isChar() || 
               pb.isInt() || pb.isLong();
      else if (pa.isDouble())
        return pb.isByte() || pb.isShort() || pb.isChar() || pb.isInt() ||
               pb.isLong() || pb.isFloat();
      else
        return false; // never reached
    }
  }

  /**
   * § 5.1.4
   */
  static private boolean isWideningReferenceConvertible (AType a, AType b) {
    if (!(a instanceof AReferenceType) || !(b instanceof AReferenceType))
      return false;

    else if (a instanceof Class)
      if (b instanceof Class)
        return isSubclassOf ((Class)a, (Class)b);
      else if (b instanceof Interface)
        return isImplementationOf ((Class)a, (Interface)b);
      else
        return false;

    else if (a instanceof NullType)
      return (b instanceof Class) || (b instanceof Interface) 
          || (b instanceof Array);

    else if (a instanceof Interface) 
      if (b instanceof Interface)
        return isSubinterfaceOf ((Interface)a, (Interface)b);
      else if (b instanceof Class)
        return ((Class)b).qualifiedName().equals ("java.lang.Object");
      else 
        return false;
    
    else if (a instanceof Array)
      if (b instanceof Class)
        return ((Class)b).qualifiedName().equals ("java.lang.Object");
      else if (b instanceof Interface)
        return ((Interface)b).qualifiedName().equals("java.lang.Cloneable");
      else if (b instanceof Array)
        return isWideningReferenceConvertible (((Array)a).getElementType(),
                                               ((Array)b).getElementType());
      else
        return false;

    else
      throw new RuntimeException ("unknown type");
  }

  /**
   * § 5.1.5
   */
  static private boolean isNarrowingReferenceConvertible (AType a, AType b) {
    if (!(a instanceof AReferenceType) || !(b instanceof AReferenceType))
      return false;

    else if (a instanceof Class)
      if (b instanceof Class)
        return isSubclassOf ((Class)b, (Class)a);
      else if (b instanceof Interface)
        return    !(((Class)a).isFinal()) 
               && !isImplementationOf ((Class)a, (Interface)b);
        else if (((Class)a).qualifiedName().equals ("java.lang.Object"))
        // second alternative below is already included above ...
        // this seems superfluous in the standard
        return (b instanceof Array) || (b instanceof Interface); 
      else
        return false;
    
    else if (a instanceof Interface)
      if (b instanceof Class)
        return    !((Class)b).isFinal()
               || isImplementationOf ((Class)b, (Interface)a);
      else if (b instanceof Interface)
        return    !isSubinterfaceOf ((Interface)a, (Interface)b)
               && noReturnTypeConflicts ((Interface)a, (Interface)b);
      else
        return false;

    else if (a instanceof Array)
      return (b instanceof Array)
          && isNarrowingReferenceConvertible (((Array)a).getElementType(),
                                              ((Array)b).getElementType());
    else
      throw new RuntimeException ("unknown type");

  }

  // public methods below this line

  /**
   * Returns true if both methods have the same signature.
   * A "signature", according to § 8.4.2, consists of the name of the
   * method and the number and types of formal parameters to the method.
   * It does not include the method's return type.
   */
  static public boolean equalSignature (AMethod a, AMethod b) {
    if (a.getName().equals (b.getName())) {
      ParameterIterator ap = a.getParameters().iterator();
      ParameterIterator bp = b.getParameters().iterator();
      while (ap.hasNext() && bp.hasNext()) {
        Parameter pa = ap.next();
	Parameter pb = bp.next();
	if(pa instanceof ParameterImpl && pb instanceof ParameterImpl)
	{
		// optimization: this does not cause parsing of classes/interfaces
		int r = ((ParameterImpl)pa).quickEqualsType((ParameterImpl)pb);
		// -1: not equal, 0: can´t determine without parsing, 1: equal
		if(r==-1) return false;
		else if(r==1) continue;
		// fall through for r==0...
//System.out.println("quick check failed for " + pa.getType() + " and " + pb.getType());
	}
        if (!isEqual (pa.getType(), pb.getType()))
          return false;
      }
      if (!ap.hasNext() && !bp.hasNext())
        return true;
      else
        return false;
    } else
      return false;
  }

  static public boolean isEqual (AType a, AType b) {
    // this is ensured by always using the factory, and using
    // AType.getCorrespondingArray
    return a == b;
  }

  /**
   * § 8.1.3
   */
  static public boolean isSubclassOf (Class subClass, 
                                       Class superClass) {
    barat.reflect.Class c = subClass.getSuperclass();
    while (c != null) {
      if (isEqual (c, superClass)) 
        return true;
      else
      {
        if(c.qualifiedName().equals("java.lang.Object"))
        {
          c = null;
        }
        else
        {
          c = c.getSuperclass();
        }
      }
    }
    return false;
  }

  /**
   * § 8.1.4
   */
  static public boolean isImplementationOf (Class     subClass,
                                             Interface superInterface) {
    // this is a depth-first search in the superinterface tree
    for (InterfaceIterator ii = subClass.getImplementedInterfaces().iterator();
         ii.hasNext();) {
      Interface i = ii.next();
      if (isEqual (i, superInterface) || isSubinterfaceOf (i, superInterface))
        return true;
    }

    barat.reflect.Class superClass = subClass.getSuperclass();
    if (superClass != null && 
        !superClass.qualifiedName().equals("java.lang.Object"))
      return isImplementationOf (superClass, superInterface);

    return false;
  }

  /**
   * § 9.1.3
   */
  static public boolean isSubinterfaceOf (Interface subInterface,
                                           Interface superInterface) {
    // this is a depth-first search in the superinterface tree
    for (InterfaceIterator ii=subInterface.getExtendedInterfaces().iterator();
         ii.hasNext();) {
      Interface i = ii.next();
      if (isEqual (i, superInterface) || isSubinterfaceOf (i, superInterface))
        return true;
    }
    return false;
  }

  public static boolean isSubtypeOf(AReferenceType subType, 
                                    AReferenceType superType)
  {
    if ((subType instanceof Interface) && (superType instanceof Interface))
      return isSubinterfaceOf ((Interface)subType, (Interface)superType);
    else if (subType instanceof Class) 
      if (superType instanceof Interface)
        return isImplementationOf ((Class)subType, (Interface)superType);
      else if (superType instanceof Class)
        return isSubclassOf ((Class)subType, (Class)superType);
      else
        return false;
    else if ((subType instanceof Array) && (superType instanceof Array)) {
      AType subElementType   = ((Array)subType).getElementType();
      AType superElementType = ((Array)superType).getElementType();
      if ((subElementType instanceof AReferenceType) &&
          (superElementType instanceof AReferenceType))
        return isSubtypeOf ((AReferenceType)subElementType, 
                            (AReferenceType)superElementType);
      else
        return false;
    } else
      return false;
  }

  /**
   * § 5.2
   */
  static public boolean isAssignableTo (AType actual, AType formal) {
    if (isIdentityConvertible (actual, formal) ||
        isWideningPrimitiveConvertible (actual, formal) ||
        isWideningReferenceConvertible (actual, formal))
      return true;
    else // *** special case: primitive narrowing conversion is allowed 
         // *** when assigning compile-time constants of type int.
         // *** don't know what to do here...  ignore it for now
      return false;
  }

  /**
   * § 5.3
   */
  static public boolean isPassableTo (AType actual, AType formal) {
    return isIdentityConvertible (actual, formal)
        || isWideningPrimitiveConvertible (actual, formal)
        || isWideningReferenceConvertible (actual, formal);
  }

  /**
   * § 5.5
   */
  static public boolean isCastableTo (AType actual, AType formal) {
    return isIdentityConvertible (actual, formal)
        || isWideningPrimitiveConvertible (actual, formal)
        || isWideningReferenceConvertible (actual, formal)
        || isNarrowingPrimitiveConvertible (actual, formal)
        || isNarrowingReferenceConvertible (actual, formal);
  }

  public static boolean isNumeric(AType t)
  {
    if(!(t instanceof PrimitiveType)) return false;
    return t!=factory().getBoolean();
  }
  public static boolean isIntegral(AType t)
  {
    if(!(t instanceof PrimitiveType)) return false;
    return t!=factory().getFloat() && t!=factory().getDouble();
  }
  public static boolean isBoolean(AType t)
  {
    return t==factory().getBoolean();
  }
  public static PrimitiveType promoted(PrimitiveType t)
  {
    if(t==factory().getChar() || 
       t==factory().getShort() || 
       t==factory().getByte())
    {
      return factory().getInt();
    }
    else
    {
      return t;
    }
  }
  public static PrimitiveType promoted(PrimitiveType t1, PrimitiveType t2)
  {
    if(t1==factory().getDouble() || t2==factory().getDouble())
    {
      return factory().getDouble();
    }
    if(t1==factory().getFloat() || t2==factory().getFloat())
    {
      return factory().getFloat();
    }
    if(t1==factory().getLong() || t2==factory().getLong())
    {
      return factory().getLong();
    }
    return factory().getInt();
  }
}
