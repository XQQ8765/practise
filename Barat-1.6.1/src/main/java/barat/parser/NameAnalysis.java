/*
 * File: NameAnalysis.java
 *
 * $Id: NameAnalysis.java,v 1.77 2003/07/24 13:10:27 bokowski Exp $
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

import barat.collections.StringIterator;
import barat.collections.AStatementIterator;
import barat.collections.ConstructorIterator;
import barat.collections.ParameterIterator;
import barat.collections.ClassIterator;
import barat.collections.InterfaceIterator;
import barat.collections.FieldIterator;
import barat.collections.InterfaceList;
import barat.reflect.*;
import barat.reflect.Class;
import barat.*;
import java.util.*;
//import java.util.*;
import java.io.IOException;
import java.io.InputStream;

/**
 */

public class NameAnalysis
{
  static public String qualifiedNameOf (ANamed o) {

    if(o instanceof Array)
	{
		Array array = (Array)o;
		return qualifiedNameOf(array.getElementType())+"[]";
	}
	  
    barat.reflect.AUserType c = o.containingUserType();

    if (o.container() instanceof barat.reflect.AnonymousAllocation)
      // The proper name of an anonymous class already contains
      // the name of its enclosing class.  Skip one level higher.
      c = c.containingUserType();

    if (c != null)
      return qualifiedNameOf (c) + "." + o.getName();
    else
    {
      barat.reflect.Package p = 
        (barat.reflect.Package)o.containing(barat.reflect.Package.class);
      if(p==null)
        return o.getName();
      else
      {
        String pname = p.getQualifiedName().toString();
        if(pname.length()!=0) pname = pname + ".";
        return pname + o.getName();
      }
    }

  }

  private static List<SourceParsedObserver> sourceParsedObserver =
            new ArrayList<SourceParsedObserver>();

  public static void addSourceParsedObserver(SourceParsedObserver o)
  {
    sourceParsedObserver.add(o);
  }

  public static void removeSourceParsedObserver(SourceParsedObserver o)
  {
    sourceParsedObserver.remove(o);
  }

  private static Factory factory() {
    return Factory.getInstance();
  }

  public static ClassPath classPath = null;
  
  static BaratParser theParser = null;

  public static AUserTypeImpl lookupUserType(Scope scope, String name)
  {
    AUserTypeImpl result = _lookupUserType(scope, name);
      do_assert(result!=null, scope, "UserType " + name + " not found.");
    return result;
  }

  static AUserTypeImpl _lookupUserType(Scope scope, String name)
  {
    if(scope==null) throw new RuntimeException("in _lookupUserType: scope is null");
    Function2<String,Boolean,AUserType> finder = scope.userTypeFinder();
    if(finder==null) throw new RuntimeException("in _lookupUserType: userTypeFinder for scope " + scope + " not found while looking for id=" + name);
    return (AUserTypeImpl)finder.apply(name, new Boolean(false));
  }

  public static AUserTypeImpl lookupUserType(QualifiedName name)
  {
    AUserTypeImpl result = _lookupUserType(name);
      do_assert(result!=null, "(toplevel)", "UserType " + name + " not found.");
    return result;
  }

  static AUserTypeImpl _lookupUserType(QualifiedName name)
  {
    StringIterator i = name.iterator();
    if(!i.hasNext()) return null;
    String ident = i.next();
    // look for a package...
    Scope scope = factory().getPackage(ident);
    loop: while((scope==null || (scope instanceof barat.reflect.Package)) && i.hasNext())
    {
      String id = i.next();
      if(scope!=null)
      {
        scope = _lookupUserType(scope, id);
        if(scope!=null) break loop;
      }
      ident = ident + "." + id;
      scope = factory().getPackage(ident);
    }
    // we now have a package, look for UserType or Package
    while(i.hasNext() && scope!=null)
    {
      scope = _lookupUserType(scope, i.next());
    }
    if(scope instanceof AUserTypeImpl)
    {
      return (AUserTypeImpl)scope;
    }
    else
    {
      return null;
    }
  }

  public static AUserTypeImpl lookupUserType(Scope scope, QualifiedName name)
  {
    AUserTypeImpl result = _lookupUserType(scope, name);
      do_assert(result!=null, scope, "UserType " + name + " not found.");
    return result;
  }

  static AUserTypeImpl _lookupUserType(Scope scope, QualifiedName name)
  {
    if(scope==null) return _lookupUserType(name);
    StringIterator i = name.iterator();
    if(!i.hasNext()) return null;
    String ident = i.next();
    scope = _lookupUserType(scope, ident);
    if(scope==null)
    {
      // must be top-level class
      return _lookupUserType(name);
    }
    while(i.hasNext() && scope!=null)
    {
      scope = _lookupUserType(scope, i.next());
    }
    if(scope instanceof AUserTypeImpl)
    {
      return (AUserTypeImpl)scope;
    }
    else
    {
      return null;
    }
  }

  public static Class getObjectClass()
  {
    PackageImpl java_lang = factory().getPackage("java.lang");
    return (Class)lookupUserType(java_lang, "Object");
  }
  public static Class getClassClass()
  {
    PackageImpl java_lang = factory().getPackage("java.lang");
    return (Class)lookupUserType(java_lang, "Class");
  }
  public static Class getStringClass()
  {
    PackageImpl java_lang = factory().getPackage("java.lang");
    return (Class)lookupUserType(java_lang, "String");
  }

  public static Interface getThrowableInterface()
  {
    PackageImpl java_lang = factory().getPackage("java.lang");
    return (Interface)lookupUserType(java_lang, "Throwable");
  }
  
  public static Interface getCloneableInterface()
  {
    PackageImpl java_lang = factory().getPackage("java.lang");
    return (Interface)lookupUserType(java_lang, "Cloneable");
  }
  
  static Attribute<Class> createClassAttribute(final QualifiedName qname, final Scope scope)
  {
    return new Attribute<Class>()
    {
      protected Class calculate()
      {
        return (Class)lookupUserType(scope, qname);
      }
    };
  }

  static Attribute<Interface> createInterfaceAttribute(final QualifiedName qname, final Scope scope)
  {
    return new Attribute<Interface>()
    {
      protected Interface calculate()
      {
        return (Interface)lookupUserType(scope, qname);
      }
    };
  }

  static Attribute<AType> createUserTypeAttribute(final QualifiedName qname, final Scope scope)
  {
    return new TypeAttribute<AType>(qname.toString())
    {
      protected AType calculate()
      {
        return lookupUserType(scope, qname);
      }
    };
  }

  static Attribute<AUserType> createUserTypeAttributeAsUserType(final QualifiedName qname, final Scope scope)
  {
    return new Attribute<AUserType>()
    {
      protected AUserType calculate()
      {
        return lookupUserType(scope, qname);
      }
    };
  }

  static Attribute<AType> asAttribute(AType t)
  {
    return new Constant<AType>(t);
  }

  public static ATyped lookupATyped(Scope scope, String name, boolean inSupertype)
  {
    if(scope==null) throw new RuntimeException("error in lookupATyped: scope is null (name=" + name + ")");
    Function2<String, Boolean, ATyped> finder = scope.typedFinder();
    if(finder==null) throw new RuntimeException("error in lookupATyped: finder is null (scope=" + scope + " , name=" + name + ")");
    return finder.apply(name, new Boolean(inSupertype));
  }

  public static Attribute<AExpression> createAccessExpressionAttribute(final Attribute<AExpression> prefix, final QualifiedName qname, final Scope scope)
  {
    return new Attribute<AExpression>()
    {
      protected AExpression calculate()
      {
        if(prefix!=null)
        {
          return getAccessOrCall_have_expr(prefix.value(), qname.iterator(), scope, null, null);
        }
        else
        {
//System.out.println("get access or call: " + qname);
          AExpression result = getAccessOrCall_have_nothing(qname.iterator(), scope, null, null);
//System.out.println("result: " + result);
          return result;
        }
      }
    };
  }

  static Attribute<Constructor> createConstructorAttribute (final QualifiedName qname, final Scope scope, final List<Attribute<AExpression>> arguments, final ObjectAllocationImpl alloc) 
  {
    return new Attribute<Constructor>() 
      {
        protected Constructor calculate() 
        {
          if(alloc.prefix==null)
          { // standard case
            return lookupConstructor (scope, 
                                      (Class)lookupUserType (scope, qname),
                                      arguments);
          }
          else
          { // there is a prefix
            AExpression prefix = alloc.prefix.value();
            AUserTypeImpl ut = (AUserTypeImpl)prefix.type();
            return lookupConstructor (scope, 
                                      (Class)lookupUserType (ut, qname),
                                      arguments);
          }
        }
      };
  }

  static ConstructorCallImpl createConstructorCall (final Scope scope, Attribute<AExpression> prefix, boolean isSuper, final List<Attribute<AExpression>> arguments) 
  {
    // prefix expression is optional enclosing instance for superclass
    if (isSuper) 
      return new ConstructorCallImpl 
        (arguments, new Attribute<Constructor>() {
          protected Constructor calculate() {
            return lookupConstructor 
                     (scope,
                      ((Node)scope).containingClass().getSuperclass(),
                      arguments);
          }
        }, prefix);
    else
      return new ConstructorCallImpl 
        (arguments, new Attribute<Constructor>() {
          protected Constructor calculate() {
            return lookupConstructor (scope,
                                      ((Node)scope).containingClass(),
                                      arguments);
          }
        }, prefix);
  }

  private static class FakeNode extends NodeImpl {
    public FakeNode() {} // GJC make the constructor private otherwise !
    public void accept(Visitor v) { throw new RuntimeException("Fake Node"); }
    public void accept(ImplementationVisitor v) { throw new RuntimeException("Fake Node"); }
  }

  public static Attribute<AExpression> createMethodCallExpressionAttribute(final Attribute<AExpression> prefix, final QualifiedName qname, final Scope scope, final List<Attribute<AExpression>> arguments)
  {
    final FakeNode fakeNode = new FakeNode();
    BaratParserTokenManager.setTagsFor(fakeNode,-1,scope);
    return new Attribute<AExpression>()
    {
      protected AExpression calculate()
      {
        AExpression result = null;
	StringIterator nameIterator 
          = qname.isQualified() ? qname.getQualifier().iterator()
                                : new StringIterator() {
                                    public boolean hasNext() { return false; }
                                    public String next() { return null; }
                                    public void remove() { }
                                  };
        if(prefix!=null)
        {
          result =  getAccessOrCall_have_expr(prefix.value(), 
                                           nameIterator,
                                           scope, 
                                           qname.getBaseName(), 
                                           arguments);
        }
        else
        {
          result = getAccessOrCall_have_nothing(nameIterator, 
                                              scope, 
                                              qname.getBaseName(), 
                                              arguments);
        }

	barat.collections.StringList tags = fakeNode.getTags();
	if(tags != null)
	  for(StringIterator i = tags.iterator(); i.hasNext();)
	    ((NodeImpl)result).addTag(i.next(), scope);

        //result.getTags().addAll(fakeNode.getTags());
        return result;
      }
    };
  }

  static boolean contains(Node container, Node n) {
    Node n_container = n.container();
    if(n_container==container)
      return true;
    if(n_container==null)
      return false;
    return contains(container,n_container);
  }

  // given a string expr (a qualified name) and a statement, return an
  // access expression (field access or variable access), resolved
  // according to statement's context
  public static AExpression getAccessExpression(String expr, AStatement statement) {
    Scope scope = null;
    
    if(statement instanceof Scope)
      scope = (Scope)statement;
    else {
      scope = (Scope)statement.containing(Scope.class);
      if(scope instanceof Block) {
	Block b = (Block)scope;
	for(AStatementIterator i=b.getStatements().iterator(); i.hasNext();) {
	  AStatement s = i.next();
	  if(s==statement || contains(s, statement))
	    break;
	  if(s instanceof Scope)
	    scope = (Scope)s;	
	}
      }
    }
    QualifiedName qn = QualifiedName.from(expr);
    return getAccessOrCall_have_nothing(qn.iterator(), scope, null, null);
  }

  public static boolean inStaticContext(Node o) {
	if (o instanceof AMethod) // in a method
		return ((AMethod)o).isStatic();
	else if (o.containingMethod() != null)
		return o.containingMethod().isStatic();
	else if (o.containing(Field.class) != null) // field initializer
		return ((Field)o.containing(Field.class)).isStatic();
	else if (o instanceof Block) // in a class or instance initializer
		return ((Block)o).isStaticInitializer();
	else if (o.containing(Block.class) != null)
		return ((Block)o.containing(Block.class)).isStaticInitializer();
	else {
		return false;
	}
  }

  private static AExpression getAccessOrCall_have_nothing(final StringIterator i, final Scope scope, final String methodName, final List<Attribute<AExpression>> arguments)
  {
    if(!i.hasNext())
    {
      Class thisClass = (scope instanceof Class)?(Class)scope:scope.containingClass();
      AMethod caller = (scope instanceof AMethod)?(AMethod)scope:scope.containingMethod();
	  boolean isStatic = inStaticContext(scope);
      AMethod m = lookupMethod(isStatic, thisClass, scope, methodName, arguments);
      if(m.isStatic())
      {
        return factory().createStaticMethodCall(m, arguments);
      }
      else
      {
        ThisImpl t = factory().createThis();

	Class this_ = m.containingClass();
	
	if(this_ == null) // Interface in fact
	  this_ = thisClass;

        t.setThisClass(new Constant<AUserType>(this_));
        return factory().createInstanceMethodCall(new Constant<AExpression>(t), m, arguments);
      }
    }
    String id = i.next();
    ATyped variable_or_field = lookupATyped(scope, id, false);
    if(variable_or_field!=null)
    {
      AExpression prefix;
      if(variable_or_field instanceof AVariable)
      {
        prefix = factory().createVariableAccess((AVariable)variable_or_field);
      }
      else
      {
        Field f = (Field)variable_or_field;
        if (f.isStatic())
        {
          prefix = factory().createStaticFieldAccess(f);
        }
        else
        {
          ThisImpl t = factory().createThis();
          t.setThisClass(new Constant<AUserType>((barat.reflect.Class)f.containingClass()));
          prefix = factory().createInstanceFieldAccess(new Constant<AExpression>(t), f);
        }
      }
      return getAccessOrCall_have_expr(prefix, i, scope, methodName, arguments);
    }
    Scope s = _lookupUserType(scope, id);
    if(s!=null)
    {
      return getAccessOrCall_have_type((AUserTypeImpl)s, i, scope, methodName, arguments);
    }
    else
    {
      // look for a package...
      s = factory().getPackage(id);
      while(s==null && i.hasNext())
      {
        id = id + "." + i.next();
        s = factory().getPackage(id);
      }
      return getAccessOrCall_have_package((PackageImpl)s, id, i, scope, methodName, arguments);
    }
  }

  private static AExpression getAccessOrCall_have_package(PackageImpl pkg, String pkgname, StringIterator i, final Scope scope, String methodName, List<Attribute<AExpression>> arguments)
  {
    if(!i.hasNext()) throw new RuntimeException("name analysis failure in getAccessOrCall_have_package for name " + pkgname);
    String id = i.next();
    AUserTypeImpl t = (AUserTypeImpl)_lookupUserType(pkg, id);
    if(t!=null)
    {
      return getAccessOrCall_have_type(t, i, scope, methodName, arguments);
    }
    else
    {
      pkgname = pkgname + "." + id;
      pkg = factory().getPackage(pkgname);
      return getAccessOrCall_have_package(pkg, pkgname, i, scope, methodName, arguments);
    }
  }

  private static AExpression getAccessOrCall_have_type(AUserTypeImpl type, StringIterator i, final Scope scope, String methodName, List<Attribute<AExpression>> arguments)
  {
    if(!i.hasNext())
    {
      AMethod m = lookupMethod(true, type, scope, methodName, arguments);
      //assert(m.isStatic(), scope, "non-static method " + methodName + " called like a static method");
      return factory().createStaticMethodCall(m, arguments);
    }
    String id = i.next();
    ATyped variable_or_field = lookupATyped(type, id, false);
    if(variable_or_field!=null)
    {
      AExpression prefix;
      if(variable_or_field instanceof Field)
      {
        Field f = (Field)variable_or_field;
        if (f.isStatic())
        {
          prefix = factory().createStaticFieldAccess(f);
        }
        else
        {
          ThisImpl t = factory().createThis();
          t.setThisClass(new Constant<AUserType>((barat.reflect.Class)f.containingClass()));
          prefix = factory().createInstanceFieldAccess(new Constant<AExpression>(t), f);
        }
      }
      else
      {
        prefix = factory().createVariableAccess((AVariable)variable_or_field);
      }
      return getAccessOrCall_have_expr(prefix, i, scope, methodName, arguments);
    }
    else
    {
      return getAccessOrCall_have_type(lookupUserType(type, id), i, scope, methodName, arguments);
    }
  }


  private static AExpression getAccessOrCall_have_expr(AExpression prefix, StringIterator i, Scope scope, String methodName, List<Attribute<AExpression>> arguments)
  {
    if(!i.hasNext())
    {
      if(methodName==null) return prefix;
      AMethod m = lookupMethod(false, (AReferenceType)prefix.type(), scope, methodName, arguments);
      if(m.isStatic())
      {
        return factory().createStaticMethodCall(m, arguments);
      }
      else
      {
        return factory().createInstanceMethodCall(new Constant<AExpression>(prefix), m, arguments);
      }
    }
    AReferenceType rt = (AReferenceType)prefix.type();
    if(rt instanceof Array)
    {
      String alength = i.next();
      do_assert(alength.equals("length"), scope, "field access on array that is not 'length'");
      ArrayLengthAccessImpl ala = factory().createArrayLengthAccess(new Constant<AExpression>(prefix));
      return getAccessOrCall_have_expr(ala, i, scope, methodName, arguments);
    }
    String id = i.next();
    ATyped variable_or_field = lookupATyped((AUserTypeImpl)rt, id, false);
    if(variable_or_field==null)
    {
      return getAccessOrCall_have_type(lookupUserType((Scope) rt, id), i, scope, methodName, arguments);
    }
    if(variable_or_field instanceof Field)
    {
      Field f = (Field)variable_or_field;
      if (f.isStatic())
      {
        prefix = factory().createStaticFieldAccess(f);
      }
      else
      {
        prefix = factory().createInstanceFieldAccess(new Constant<AExpression>(prefix), f);
      }
    }
    else
    {
      prefix = factory().createVariableAccess((AVariable)variable_or_field);
    }
    return getAccessOrCall_have_expr(prefix, i, scope, methodName, arguments);
  }

  public static ConstructorImpl lookupConstructor_ (
                                  Scope callingScope,                 
                                  Class callee,
                                  AType[] argTypes) 
  {
    List<AMethod> candidates = new ArrayList<AMethod>();
    for (ConstructorIterator i = callee.getConstructors().iterator();
         i.hasNext();) {
      Constructor x = i.next();
      if (isApplicable (x, argTypes) &&
          (callingScope==null || isAccessible (false, x, callingScope)))
        candidates.add (x);
    }
    do_assert (candidates.size() > 0, callee.getName(), "no constructor found that is applicable and accessible");
    candidates = mostSpecific (candidates);
    do_assert (candidates.size() == 1, callee.getName(), "constructor call is ambiguous");
    return (ConstructorImpl)candidates.get(0);
  }

  private static ConstructorImpl lookupConstructor (
                                  Scope callingScope,                 
                                  Class callee,
                                  List<Attribute<AExpression>> arguments) 
  {
    AType[] argTypes = new AType[arguments.size()];
    for(int i=0; i<argTypes.length; i++)
    {
      argTypes[i] = arguments.get(i).value().type(); // this causes two attributes to be calculated
    }
    return lookupConstructor_(callingScope, callee, argTypes);
  }
  
  public static AMethod lookupMethod_(boolean isStatic, AReferenceType calledType_, Scope caller, String methodName, AType[] argTypes)
  {
    if(calledType_ instanceof Array)
    {
      return lookupMethod_(isStatic, getObjectClass(), caller, methodName, argTypes);
    }
    AUserTypeImpl calledType = (AUserTypeImpl)calledType_;
    List<AMethod> candidates = new ArrayList<AMethod>();
    boolean noApplicableMethod = true;
    for(Iterator<AMethod> i= calledType.getMethodList().iterator() ; i.hasNext();)
    {
      AMethod m = i.next();
      if(!methodName.equals(m.getName())) continue;
      if(isApplicable(m, argTypes))
      {
        noApplicableMethod = false;
        // caller == null iff lookupMethod is called from AReferenceTypeImpl
        if((caller==null && isStatic==m.isStatic()) || isAccessible(isStatic, m, caller))
        {
          candidates.add(m);
        }
      }
    }
      do_assert(candidates.size()>0, methodName, "no " + (isStatic?"static ":"") + "method found that is " + (noApplicableMethod?"applicable and ":"") + "accessible in list " + calledType.getMethodList());
    candidates = mostSpecific (candidates);
	candidates = mostApplicable (candidates, calledType);
      do_assert(candidates.size()==1, methodName, "method call is ambiguous");
    return candidates.get(0);
  }

  private static AMethod lookupMethod(boolean isStatic, AReferenceType calledType_, Scope caller, String methodName, List<Attribute<AExpression>> arguments)
  {
    AType[] argTypes = new AType[arguments.size()];
    for(int i=0; i<argTypes.length; i++)
    {
      argTypes[i] = 
        arguments.
        get(i).
        value().
        type(); // this causes two attributes to be calculated
    }
    return lookupMethod_(isStatic, calledType_, caller, methodName, argTypes);
  }

  private static boolean isApplicable(AMethod m, AType[] argTypes)
  {
    if (m.getParameters().size() != argTypes.length) return false;
    ParameterIterator f = m.getParameters().iterator();
    for(int i=0; f.hasNext(); i++)
    {
      AType ft = f.next().getType();
      if(!TypeAnalysis.isPassableTo(argTypes[i], ft)) return false;
    }
    return true;
  }
  
  private static AUserType getOutmostUserType(AUserType ut)
  {
    AUserType outer = (AUserType)ut.containing(AUserType.class);
    if(outer==null) return ut;
    else return getOutmostUserType(outer);
  }

  private static boolean hasPrivateAccess(AUserType caller, AUserType callee)
  {
    return getOutmostUserType(caller)==getOutmostUserType(callee);
  }

  private static boolean isAccessible(boolean isStatic, AMethod m, Scope scope)
  {
    if(isStatic && (!m.isStatic())) return false;
    AUserType callerType = (scope instanceof AUserType)?(AUserType)scope:scope.containingUserType();
    if(m.isPrivate())
    {
      return hasPrivateAccess(callerType,m.containingClass());
    }
    if(m.isProtected())
    {
      return (callerType.containing(barat.reflect.Package.class)==m.containing(barat.reflect.Package.class)) ||
             TypeAnalysis.isSubtypeOf(callerType, m.containingUserType());
    }
    if(!m.isPublic())
    {
      return callerType.containing(barat.reflect.Package.class)==m.containing(barat.reflect.Package.class);
    }
    return true;
  }

  private static List<AMethod> mostSpecific(List<AMethod> candidates)
  {
    List<AMethod> max = new ArrayList<AMethod>();
    if(candidates.size()==0) return max;
    Iterator<AMethod> i = candidates.iterator();
    max.add(i.next());
    while(i.hasNext())
    {
      AMethod m = i.next();
      boolean doAdd = true;
      for(Iterator<AMethod> j=max.iterator(); j.hasNext(); )
      {
        AMethod n = j.next();
        if(isMoreSpecific(m,n))
        {
          j.remove(); // removes n from max
        }
        else if(isMoreSpecific(n,m))
        {
          doAdd = false;
        }
      }
      if(doAdd)
      {
        max.add(m);
      }
    }
    return max;
  }

  private static boolean isMoreSpecific(AMethod t, AMethod u)
  {
    AUserType t_type = (AUserType)t.containing(AUserType.class);
    AUserType u_type = (AUserType)u.containing(AUserType.class);
    if(!TypeAnalysis.isPassableTo(t_type,u_type)) return false;
    ParameterIterator t_i = t.getParameters().iterator();
    ParameterIterator u_i = u.getParameters().iterator();
    while(t_i.hasNext())
    {
      if(!TypeAnalysis.isPassableTo(t_i.next().getType(), u_i.next().getType())) return false;
    }
      do_assert(!u_i.hasNext(), null, "Huh?");
    return true;
  }

  private static List<AMethod> mostApplicable(List<AMethod> candidates,
											  AUserType calledType)
  {
	if(candidates.size()<2) return candidates;
	List<AMethod> max = new ArrayList<AMethod>();
	Iterator<AMethod> i = candidates.iterator();
	while(i.hasNext())
	{
	  AMethod m = i.next();
	  if (Typing.isSubtypeOf(calledType,m.containingUserType())
		  || calledType == m.containingUserType())
		max.add(m);
	}

	if (max.size() == 0 && calledType.containingUserType() != null)
	  return mostApplicable(candidates, calledType.containingUserType());
	else
	  return max;
  }

  static Attribute<AExpression> asAttribute(AExpression e)
  {
    return new Constant<AExpression>(e);
  }

  public static void addNameAnalysis(final AMethodImpl s)
  {
    s.userTypeFinder(new Function2<String, Boolean, AUserType>() {
      public AUserType apply(String name, Boolean inSupertype)
      {
          return s.getEnclosingScope().userTypeFinder().apply(name, inSupertype);
      }
    });
    s.typedFinder(new Function2<String, Boolean, ATyped>() {
      public ATyped apply(String name, Boolean inSupertype)
      {
          for(ParameterIterator i=s.getParameters().iterator(); i.hasNext();)
          {
            Parameter p = i.next();
            if(name.equals(p.getName())) return p;
          }
          return s.getEnclosingScope().typedFinder().apply(name, inSupertype);
      }
    });
  }

  public static void addNameAnalysis(final AUserTypeImpl s)
  {
    s.userTypeFinder(new Function2<String, Boolean, AUserType>() {
      public AUserType apply(String name, Boolean inSupertype)
      {
          for(ClassIterator i=s.getNestedClasses().iterator(); i.hasNext();)
          {
            Class c = i.next();
            if(name.equals(c.getName())) return c;
          }
          for(InterfaceIterator i=s.getNestedInterfaces().iterator(); i.hasNext();)
          {
            Interface c = i.next();
            if(name.equals(c.getName())) return c;
          }
          List<AUserType> candidates = new ArrayList<AUserType>();
          InterfaceList superInterfaces = null;
          if(s instanceof ClassImpl)
          {
            Class c = (Class)s;
            superInterfaces = c.getImplementedInterfaces();
            ClassImpl sc = (ClassImpl)c.getSuperclass();
            if(c!=sc && sc!=null)
            {
              AUserType candidate = sc.userTypeFinder().apply(name, new Boolean(true));
              if(candidate!=null && !candidates.contains(candidate)) candidates.add(candidate);
            }
          }
          else
          {
            Interface i = (Interface)s;
            superInterfaces = i.getExtendedInterfaces();
          }
          for(InterfaceIterator i = superInterfaces.iterator(); i.hasNext();)
          {
            AUserType candidate = ((InterfaceImpl)i.next()).userTypeFinder().apply(name, new Boolean(true));
            if(candidate!=null && !candidates.contains(candidate)) candidates.add(candidate);
          }
          AUserType candidate = s.getEnclosingScope().userTypeFinder().apply(name, inSupertype);
	    if(candidate!=null && !candidates.contains(candidate)) candidates.add(candidate);
            
// BB 2000-12-12: I am not sure whether or not the next line prevents Barf cases (see below)
          if(candidate!=null) return candidate;
          
          if(candidates.size()==0) return null;
          if(candidates.size()==1) return candidates.toArray(new AUserType[1])[0];
          throw new RuntimeException("Barf: finding more than one UserType for " + name + " in " + s.getName());
      }
    });
    s.typedFinder(new Function2<String, Boolean, ATyped>() {
      public ATyped apply(String name, Boolean inSupertype)
      {
          for(FieldIterator i=s.getFields().iterator(); i.hasNext();)
          {
            Field f = i.next();
            if(name.equals(f.getName())) return f;
          }
          InterfaceList superInterfaces = null;
          if(s instanceof ClassImpl)
          {
            Class c = (Class)s;
            superInterfaces = c.getImplementedInterfaces();
            ClassImpl sc = (ClassImpl)c.getSuperclass();
            if(c!=sc && sc!=null)
            {
              ATyped t = sc.typedFinder().apply(name, new Boolean(true));
              if(t!=null) return t;
            }
          }
          else
          {
            Interface i = (Interface)s;
            superInterfaces = i.getExtendedInterfaces();
          }
          for(InterfaceIterator i = superInterfaces.iterator(); i.hasNext();)
          {
            ATyped t = ((InterfaceImpl)i.next()).typedFinder().apply(name, new Boolean(true));
            if(t!=null) return t;
          }
          if(!inSupertype.booleanValue())
            return s.getEnclosingScope().typedFinder().apply(name, new Boolean(false));
          else
            return null;
      }
    });
  }

  public static void addNameAnalysis(final BlockImpl s)
  {
    s.userTypeFinder(new Function2<String, Boolean, AUserType>() {
      public AUserType apply(String name, Boolean inSupertype)
      {
          return s.getEnclosingScope().userTypeFinder().apply(name, inSupertype);
      }
    });
    s.typedFinder(new Function2<String, Boolean, ATyped>() {
      public ATyped apply(String name, Boolean inSupertype)
      {
      	  if(s.addedLocalVariables!=null)
          for(Iterator<LocalVariable> i=s.addedLocalVariables.iterator(); i.hasNext();)
          {
            LocalVariable v = i.next();
            if(name.equals(v.getName())) return v;
          }
          return s.getEnclosingScope().typedFinder().apply(name, inSupertype);
      }
    });
  }

  public static void addNameAnalysis(final CatchImpl s)
  {
    s.userTypeFinder(new Function2<String, Boolean, AUserType>() {
      public AUserType apply(String name, Boolean inSupertype)
      {
          return s.getEnclosingScope().userTypeFinder().apply(name, inSupertype);
      }
    });
    s.typedFinder(new Function2<String, Boolean, ATyped>() {
      public ATyped apply(String name, Boolean inSupertype)
      {
          if(name.equals(s.getParameter().getName())) return s.getParameter();
          return s.getEnclosingScope().typedFinder().apply(name, inSupertype);
      }
    });
  }

  public static void addNameAnalysis(final CompilationUnitImpl s, final List<QualifiedName> classImports, final List<barat.reflect.Package> packageImports)
  {
    s.userTypeFinder(new Function2<String, Boolean, AUserType>() {
      public AUserType apply(String name, Boolean inSupertype)
      {
          if(inSupertype.booleanValue()) return null;
          for(ClassIterator i=s.getClasses().iterator(); i.hasNext();)
          {
            Class c = i.next();
            if(name.equals(c.getName())) return c;
          }
          for(InterfaceIterator i=s.getInterfaces().iterator(); i.hasNext();)
          {
            Interface c = i.next();
            if(name.equals(c.getName())) return c;
          }
          for(Iterator<QualifiedName> i = classImports.iterator(); i.hasNext();)
          {
            QualifiedName qn = i.next();
            if(qn.getBaseName().equals(name))
            {
              return lookupUserType(null, qn);
            }
          }
          for(Iterator<barat.reflect.Package> i = packageImports.iterator(); i.hasNext();)
          {
            barat.reflect.Package p = i.next();
            AUserType c = ((PackageImpl)p).userTypeFinder().apply(name, inSupertype);
            if(c!=null) return c;
          }
          //return s.getEnclosingScope().userTypeFinder().apply(name);
          return null;
      }
    });
    s.typedFinder(new Function2<String, Boolean, ATyped>() {
      public ATyped apply(String name, Boolean inSupertype)
      {
        return null;
      }
    });
  }

    static boolean parserInUse = false;
    
    public static void addNameAnalysis(final PackageImpl s)
	{
	    s.userTypeFinder(new Function2<String, Boolean, AUserType>() {
		// not needed? Map<String,AUserType> loadedUserTypes = new HashMap<String,AUserType>();
		
		private String[] allFiles;
		
		private void loadInnerClasses(String name, AUserTypeImpl enclosingScope)
		    throws IOException {
		    if (allFiles == null)
			allFiles = classPath().allFiles(s.getQualifiedName().toString());
		    AUserType t = get(name);
		    loop: for (int i = 0; i < allFiles.length; ++i) {
			String fileName = allFiles[i];
			for (int j = 0; j < i; ++j)
			    if (allFiles[j].equals(fileName))
				continue loop;
			if (fileName.endsWith(".class") && fileName.indexOf(name) != -1
			    && fileName.indexOf('$') != -1) {
			    String className = s.getQualifiedName().toString() + "." + fileName.substring(0, fileName.length() - 6);
			    String shortClassName = className.substring(className.indexOf('$')+1);
			    if (enclosingScope.getNestedUserType(shortClassName) == null) {
				//System.out.println("Parsing the class file " + fileName + " to load " + className);
				// the class in bytecode file fileName is an inner class of t
				InputStream in = classPath().getClassFile(className);
				if (in != null) {
				    ClassFileParser.parseClassFile (in, fileName);
				    // side-effect: adds parsed inner class/interface to enclosing class
				    in.close();
				}
			    }
			}
		    }
		}
		
		private AUserType get(String name)
		    {
			for(ClassIterator i=s.getClasses(false).iterator(); i.hasNext();)
			{
			    Class c = i.next();
			    if(name.equals(c.getName())) return c;
			}
			for(InterfaceIterator i=s.getInterfaces(false).iterator(); i.hasNext();)
			{
			    Interface c = i.next();
			    if(name.equals(c.getName())) return c;
			}
			return null;
		    }
		public AUserType apply(String name, Boolean inSupertype)
		    {
			if(inSupertype.booleanValue()) return null;
//System.out.println("apply at " + s.getQualifiedName() + " , name=" + name);
			AUserType result = get(name);
			if (result != null) return result;
			
			String fullName = s.getQualifiedName().toString() + "." + name;
			ClassPath.AFile in_java_file;
			ClassPath.AFile in_class_file;
			
			try {
			    
			    
			    in_java_file = classPath().getFile (fullName, true);
			    in_class_file = classPath().getFile (fullName, false);
			    boolean use_java;
			    
			    if(in_java_file == null) {
				if(in_class_file == null)
				    return null;
				else
				    use_java = false;
			    } else {
				if(in_class_file == null)
				    use_java = true;
				else 
				    use_java = !((in_java_file.lastModified() <= in_class_file.lastModified()) &&
						 Barat.preferByteCode);
			    }
			    
			    // declare here for SourceParsedObserver (see below)
			    CompilationUnit compilationUnit = null;
			    
//try { throw new RuntimeException(); } catch(RuntimeException ex) { System.out.print("[" + fullName + "] "); ex.printStackTrace(System.out); }
			    
			    if(use_java) {
				
				long startTime = System.currentTimeMillis();
				if(parserInUse) throw new RuntimeException("Parser in use! (an attribute seems to be evaluated while the parser still runs)");
				parserInUse = true;
				try {
				    
				    java.io.InputStream in = in_java_file.getInputStream();
				    if (theParser == null)
					theParser = new BaratParser (in);
				    else
					BaratParser.ReInit (in);
				    
				    if(Barat.debugLoading)
					
					System.out.println ("!Parsing source " + fullName);
				    compilationUnit =
					theParser.CompilationUnit(in_java_file.getName()); // side-effect: adds parsed classes 
				// and interfaces to package
				    
				    in.close();
				} finally {
				    parserInUse = false;
				}

				long stopTime = System.currentTimeMillis();
				if(Barat.debugLoading) System.out.println(" [" + (stopTime-startTime) + " ms]");
				
				//System.gc();
			    } else {
				// reading from a class file
				java.io.InputStream in = in_class_file.getInputStream();
				if(Barat.debugLoading)
				    System.out.print ("!Parsing class " + fullName);
				long startTime = System.currentTimeMillis();
				AUserTypeImpl t = ClassFileParser.parseClassFile (in, in_class_file.getName()); 
				// side-effect: adds parsed class/interface to package
				in.close();
				loadInnerClasses(name, t);
				long stopTime = System.currentTimeMillis();
				if(Barat.debugLoading) System.out.println(" [" + (stopTime-startTime) + " ms]");
			    }
			    
			    if(fullName.equals("java.lang.Object"))
			    {
				// special treatment: set superclass to null
				ClassImpl object = (ClassImpl)getObjectClass();
				object.setSuperclass(new Constant<Class>(null));
			    }
			    
			    if(use_java)
			    {
				for(Iterator<SourceParsedObserver> i = sourceParsedObserver.iterator(); i.hasNext();)
				{
				    i.next().sourceParsed(compilationUnit);
				}
			    }            
			}
			catch (java.io.IOException ex) {
			    ex.printStackTrace();
			}
			catch (Exception ex) {
			    System.err.println("While parsing " + fullName);
			    ex.printStackTrace();
			}
			return get (name);
		    }
	    });
	    s.typedFinder(new Function2<String, Boolean, ATyped>() {
		public ATyped apply(String name, Boolean inSupertype)
		    {
			return null;
		    }
	    });
	}
    
  public static ClassPath classPath()
  {
    if(classPath==null)
    {
      try
      {
        classPath = new ClassPath();
      }
      catch(java.io.IOException ex)
      {
        ex.printStackTrace();
        throw new RuntimeException("io error");
      }
    }
    return classPath;
  }
  
  // cause loading of all classes of a package
  static void loadPackageFiles(PackageImpl p)
  {
    String[] files = classPath().allFiles(p.getQualifiedName().toString());
    for(int i=0; i<files.length; i++)
    {
      String name = files[i].substring(0,files[i].lastIndexOf('.'));
//System.out.println("" + i + ": loading " + p.getQualifiedName() + "." + files[i]);
      lookupUserType(p, name);
    }
  }

  public static void addNameAnalysis(final UserTypeDeclarationImpl s)
  {
    s.userTypeFinder(new Function2<String, Boolean, AUserType>() {
      public AUserType apply(String name, Boolean inSupertype)
      {
          if(name.equals(s.getUserType().getName())) return s.getUserType();
          return s.getEnclosingScope().userTypeFinder().apply(name, inSupertype);
      }
    });
    s.typedFinder(new Function2<String, Boolean, ATyped>() {
      public ATyped apply(String name, Boolean inSupertype)
      {
          return s.getEnclosingScope().typedFinder().apply(name, inSupertype);
      }
    });
  }

  public static void addNameAnalysis(final VariableDeclarationImpl s)
  {
    s.userTypeFinder(new Function2<String, Boolean, AUserType>() {
      public AUserType apply(String name, Boolean inSupertype)
      {
          return s.getEnclosingScope().userTypeFinder().apply(name, inSupertype);
      }
    });
    s.typedFinder(new Function2<String, Boolean, ATyped>() {
      public ATyped apply(String name, Boolean inSupertype)
      {
          if(name.equals(s.getVariable().getName())) return s.getVariable();
          return s.getEnclosingScope().typedFinder().apply(name, inSupertype);
      }
    });
  }
  
  private static boolean different(String s1, String s2)
  {
    if(s1==null)
    {
      return s2!=null;
    }
    else
    {
      return s2==null || !s1.equals(s2);
    }
  }

  public static Attribute<ATargetStatement> createBreakTargetAttribute(final BreakImpl s, final String label)
  {
    return new Attribute<ATargetStatement>() {
      protected ATargetStatement calculate()
      {
        ATargetStatement ts = (ATargetStatement)s.containing(ATargetStatement.class);
        while(ts!=null && (different(ts.label(), label) || (label==null && !((ts instanceof Switch) || (ts instanceof ALoopingStatement)))))
        {
          ts = (ATargetStatement)ts.containing(ATargetStatement.class);
        }
        return ts;
      }
    };
  }

  public static Attribute<ALoopingStatement> createContinueTargetAttribute(final ContinueImpl s, final String label)
  {
    return new Attribute<ALoopingStatement>() {
      protected ALoopingStatement calculate()
      {
        ALoopingStatement ts = (ALoopingStatement)s.containing(ALoopingStatement.class);
        while(ts!=null && different(ts.label(), label))
        {
          ts = (ALoopingStatement)ts.containing(ALoopingStatement.class);
        }
        return ts;
      }
    };
  }

  private static void do_assert(boolean condition, Object obj, String str)
  {
    if(!condition)
    {
      String obj_description = "<null>";

      try {
	obj_description = "object of " + obj.getClass().toString();
        obj_description = obj.toString();
      }
      catch(Exception ex)
      {
      }
      throw new RuntimeException("Assertion failed for " + obj_description + " : " + str);
    }
  }
  
  public static Attribute<AType> createArrayAttribute(final Attribute<AType> elementType)
  {
    String typeName = null;
    if(elementType instanceof TypeAttribute)
    {
      typeName = ((TypeAttribute<AType>)elementType).typeName;
    }
    return new TypeAttribute<AType>("[" + typeName) {
      protected AType calculate() {
	return elementType.value().getCorrespondingArray();
      }
    };
  }

  public static Attribute<AUserType> createThisClassAttribute (final Scope scope, final boolean isSuper) {
    return new Attribute<AUserType>() {
      protected AUserType calculate() {
	Class result = (scope instanceof Class)?(Class)scope:scope.containingClass();
	// obsolete: if(isSuper) result = result.getSuperclass();
        return result;
      }
    };
  }

  public static Attribute<Class> createAnonymousClassAttribute (final ClassImpl c, final QualifiedName superName, final Scope scope) {
    return new Attribute<Class>() {
      protected Class calculate() {
        AUserType superType = lookupUserType(scope, superName);
        if(superType instanceof ClassImpl)
        {
          c.setSuperclass(new Constant<Class>((Class)superType));
        }
        else if(superType instanceof InterfaceImpl)
        {
          c.setSuperclass(new Constant<Class>(getObjectClass()));
          c.addImplementedInterface(new Constant<Interface>((Interface)superType));
        }
        return c;
      }
    };
  }

}
