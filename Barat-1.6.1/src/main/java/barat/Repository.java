/*
 * File: Repository.java
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

package barat;

import barat.reflect.*;
import barat.parser.*;
import barat.collections.*;
import barat.collections.InterfaceIterator;
import barat.collections.ConcreteMethodIterator;
import java.util.*;

/**
 * The repository to maintain information about classes loaded by
 * Barat. The constructor takes an initial set of classes names, and a
 * list of packages that ought to be ignored.  E.g., you may not want
 * the classes in packages like "java.lang" in the repository.  <p>
 * When a class is loaded by Barat, the repository memorizes top-down
 * inheritance hierarchy by storing a special attribute in the class
 * nodes. It also memorizes overridden methods. I.e. you can retrieve
 * all classes that subclass a given class, or all methods overriding
 * a given method. This information is computed after you close the list by
 * calling the close() method.
 *
 * @version $Id: Repository.java,v 1.4 2002/01/28 12:06:12 dahm Exp $
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 * @see TraversingVisitor
 */
public class Repository implements SourceParsedObserver {
  private ArrayList list     = new ArrayList();
  private HashSet   set      = new HashSet();
  private HashSet   rejected = new HashSet(Arrays.asList(new Object[] { OBJECT }));

  static {
    barat.parser.Factory.addAnalysis(new barat.parser.ConstantAnalysis());
  }

  private boolean add_binary;

  public boolean visitBinaryClasses()          { return add_binary; }

  private HashSet omitted_packages = new HashSet(Arrays.asList(new String[] {
    "org.apache.bcel", "org.apache.bcel.classfile",
    "org.apache.bcel.generic", "org.apache.bcel.util",
    "barat", "barat.parser", "barat.reflect", "barat.collections",
    "barat.codegen"
  }));

  public final boolean omittedPackage(AUserType clazz) {
    String pack = clazz.containing(barat.reflect.Package.class).toString();
    return omitted_packages.contains(pack);
  }

  /** By default add binary classes to the repository and allow all
   * packages except the Barat packages themselves.
   *
   * @param names initial list of class names
   */
  public Repository(String[] names) {
    this(names, null, true);
  }

  /** Allow all packages except the Barat packages themselves.
   * @param names initial list of class names
   * @param add_binary add binary classes to the list, too?
   */
  public Repository(String[] names, boolean add_binary) {
    this(names, null, add_binary);
  }

  /**
   * @param names initial list of class names
   * @param omitted_packages don't add these classes to the repository
   * @param add_binary add binary classes to the list, too?
   */
  public Repository(String[] names, String[] omitted_packages, boolean add_binary) {
    if(omitted_packages != null)
      this.omitted_packages.addAll(Arrays.asList(omitted_packages));
    this.add_binary       = add_binary;

    for(int i=0; i < names.length; i++) {
      AUserType clazz = barat.Barat.getUserType(names[i]);
      add(clazz);
    }

    NameAnalysis.addSourceParsedObserver(this);
  }

  /** Override this to get some messages about the progress, does nothing by default
   */
  protected void print(String s) {
  }

  public void sourceParsed(CompilationUnit cu) {
    if(set == null) // List closed
      return;


    for(ClassIterator i=cu.getClasses().iterator(); i.hasNext();) {
      barat.reflect.Class c = i.next();

      print("Parsed " + c.getName());

      add(c);
    }

    for(InterfaceIterator i=cu.getInterfaces().iterator(); i.hasNext();) {
      Interface c = i.next();

      print("Parsed " + c.getName());

      add(c);
    }
  }

  /** Take no more new entries, i.e. additional calls to add() will be ignored.
   * Also computes dependencies, such as overridden methods and subclasses.
   */
  public void close() {
    rejected = null;
    set      = null; // may be abandoned now
    NameAnalysis.removeSourceParsedObserver(this);

    for(Iterator l = list.iterator(); l.hasNext(); ) {
      AUserType clazz = (AUserType)l.next();

      if(clazz instanceof Interface) {
	Interface inter = (Interface)clazz;
	inheritanceFor(inter, OBJECT); // always extends Object

	for(InterfaceIterator j=inter.getExtendedInterfaces().iterator(); j.hasNext();)
	  inheritanceFor(inter, j.next());
      } else { // Class
	barat.reflect.Class cl = (barat.reflect.Class)clazz;
	inheritanceFor(cl, cl.getSuperclass());
	
	for(ConcreteMethodIterator i=cl.getConcreteMethods().iterator(); i.hasNext();) {
	  AMethod method = i.next();

	  if(!method.isStatic() && !method.isAbstract()) {
	    AMethod overridden = method.getOverriddenMethod();

	    if(overridden != null)
	      overriddenBy(method, overridden);
	  }
	}

	for(InterfaceIterator j=cl.getImplementedInterfaces().iterator(); j.hasNext();)
	  inheritanceFor(cl, j.next());
      }
    }
  }

  private static final void inheritanceFor(AUserType clazz, AUserType super_clazz) {
    Inheritance.descendantsOf(super_clazz).add(clazz);
  }

  /** @return all known subclasses of a class (transitively)
   */
  public static Set getAllSubclasses(barat.reflect.Class clazz) {
    return Inheritance.allDescendantsOf(clazz);
  }

  /** @return all known direct subtypes of a class
   */
  public static Set getSubclasses(barat.reflect.Class clazz) {
    return Inheritance.descendantsOf(clazz);
  }

  /** @return all known implementors/subinterfaces of an interface (transitively)
   */
  public static Set getAllImplementors(Interface clazz) {
    return Inheritance.allDescendantsOf(clazz);
  }

  /** @return all known direct implementors/subinterfaces of an interface
   */
  public static Set getImplementors(Interface clazz) {
    return Inheritance.descendantsOf(clazz);
  }

  private static final void overriddenBy(AMethod method, AMethod super_method) {
    Overridden.overridersOf(super_method).add(method);
  }

  /** @return all known overriding methods of method (transitive)
   */
  public static Set getAllOverriders(AMethod method) {
    return Overridden.allOverridersOf(method);
  }

  /** @return known direct overriding methods of method
   */
  public static Set getOverriders(AMethod method) {
    return Overridden.overridersOf(method);
  }

  /** Add a class to the list if it is not from an omitted package, and
   * the list is not closed yet. 
   */
  public final void add(AUserType clazz) {
    if((set == null) || rejected.contains(clazz))
      return;

    if((!omittedPackage(clazz)) &&
       (!isBinaryClass(clazz) || add_binary) &&
        set.add(clazz)) {
      list.add(clazz);
    } else
      rejected.add(clazz);
  }

  public Iterator iterator() {
    return new Iterator() {
      private int index = 0;

      public boolean hasNext() { return index < list.size(); }
      public Object next()     { return list.get(index++); }
      public void remove()     { throw new UnsupportedOperationException(); }
    };
  }

  public boolean contains(Object o) { return list.contains(o); }
  public String  toString()         { return list.toString(); }
  public Collection getClasses()    { return new ArrayList(list); }

  private abstract static class SetAttribute extends HashSet implements AbstractAttribute {
    public Object objectValue() { return this; }

    /**
     * @param key must be class object for subclass of SetAttribute
     */
    protected static HashSet getSet(Node node, java.lang.Class key) {
      SetAttribute s = (SetAttribute)node.attributeValue(key);

      try {
	if(s == null)
	  node.addAttribute(key, s = (SetAttribute)key.newInstance());
      } catch(Exception e) {
	System.err.println(e);
      }

      return s;
    }
  }

  private static class Overridden extends SetAttribute {
    /** @return overriders of method
     */
    static final Set overridersOf(AMethod method) { return getSet(method, Overridden.class); }

    /** @return all overriders of method (transitive)
     */
    static final Set allOverridersOf(AMethod method) {
      HashSet overriders = getSet(method, Overridden.class);
      HashSet set        = (HashSet)overriders.clone();
      
      for(Iterator i = overriders.iterator(); i.hasNext(); )
	set.addAll(allOverridersOf((AMethod)i.next()));
      
      return set;
    }
  }

  private static class Inheritance extends SetAttribute {
    /** @return descendants of class
     */
    static final Set descendantsOf(AUserType clazz) {
      return getSet(clazz, Inheritance.class);
    }

    /** @return all descendants of class (transitive)
     */
    static final Set allDescendantsOf(AUserType clazz) {
      HashSet descendants = getSet(clazz, Inheritance.class);
      HashSet set         = (HashSet)descendants.clone();
      
      for(Iterator i = descendants.iterator(); i.hasNext(); )
	set.addAll(allDescendantsOf((AUserType)i.next()));
      
      return set;
    }
  }

  /************************ Static repository methods ******************************/

  public static final barat.reflect.Class     OBJECT       = Barat.getObjectClass();
  public static final barat.reflect.Interface SERIALIZABLE = Barat.getInterface("java.io.Serializable");

  private static Map classes    = new HashMap(); // class -> list<class>
  private static Map interfaces = new HashMap(); // interface/class -> list<interfaces>
  private static Map fields     = new HashMap(); // class -> list<field>
  private static Map types      = new HashMap(); // type -> set<type>

  // Save memory by re-using empty lists which may occur quite often
  private static final ClassList     EMPTY_CLASSES    = new ClassArrayList();
  private static final InterfaceList EMPTY_INTERFACES = new InterfaceArrayList();
  private static final FieldList     EMPTY_FIELDS     = new FieldArrayList();

  /** @return all super classes of this class (excluding Object)
   */
  public static ClassList getSuperClasses(barat.reflect.Class clazz) {
    ClassList list = (ClassList)classes.get(clazz);

    if(list == null) {
      list = new ClassArrayList();

      for(barat.reflect.Class cl = clazz.getSuperclass(); cl != OBJECT;
	  cl = cl.getSuperclass())
	list.add(cl);

      classes.put(clazz, list.isEmpty()? EMPTY_CLASSES : list);
    }

    return list;
  }

  /** @return all interfaces extended by this interface (transitively)
   */
  public static InterfaceList getExtendedInterfaces(Interface inter) {
    InterfaceList list = (InterfaceList)interfaces.get(inter);

    if(list == null) {
      list = new InterfaceArrayList();

      for(InterfaceIterator i = inter.getExtendedInterfaces().iterator(); i.hasNext();) {
	Interface in1 = i.next();

	if(!list.contains(in1)) {
	  list.add(in1);

	  for(InterfaceIterator j = getExtendedInterfaces(in1).iterator(); j.hasNext();) {
	    Interface in2 = j.next();

	    if(!list.contains(in2))
	      list.add(in2);
	  }
	}
      }

      interfaces.put(inter, list.isEmpty()? EMPTY_INTERFACES : list);
    }

    return list;
  }

  /** @return all interfaces implemented by this class (transitively)
   */
  public static InterfaceList getImplementedInterfaces(final barat.reflect.Class clazz) {
    InterfaceList list = (InterfaceList)interfaces.get(clazz);

    if(list == null) {
      list = new InterfaceArrayList();

      for(barat.reflect.Class cl = clazz; cl != OBJECT; cl = cl.getSuperclass()) {
	for(InterfaceIterator i = cl.getImplementedInterfaces().iterator(); i.hasNext();) {
	  Interface inter = i.next();

	  list.addAll(getExtendedInterfaces(inter)); // checked for uniqueness already

	  if(!list.contains(inter))
	    list.add(inter);
	}
      }

      interfaces.put(clazz, list.isEmpty()? EMPTY_INTERFACES : list);
    }

    return list;
  }

  /** @return all instance fields the class (including those of super classes)
   */
  public static FieldList getAllInstanceFields(barat.reflect.Class clazz) {
    FieldList list = (FieldList)fields.get(clazz);

    if(list == null) {
      list = new FieldArrayList();

      for(FieldIterator j = clazz.getFields().iterator(); j.hasNext();) {
	Field f = j.next();

	if(!f.isStatic())
	  list.add(f);
      }

      for(ClassIterator i = getSuperClasses(clazz).iterator(); i.hasNext();) {
	for(FieldIterator j = i.next().getFields().iterator(); j.hasNext();) {
	  Field f = j.next();

	  if(!f.isStatic())
	    list.add(f);
	}
      }

      fields.put(clazz, list.isEmpty()? EMPTY_FIELDS : list);
    }

    return list;
  }

  /** @return all abstract methods the class (including those of super classes)
   */
  public static AbstractMethodList getAllAbstractMethods(barat.reflect.Class clazz) {
    AbstractMethodList list = new AbstractMethodArrayList();

    list.addAll(clazz.getAbstractMethods());

    for(ClassIterator i = getSuperClasses(clazz).iterator(); i.hasNext();)
      list.addAll(i.next().getAbstractMethods());

    return list;
  }

  /** @return all concrete methods of the class (including those of super classes,
   * if they are not overridden, except for those in java.lang.Object)
   */
  public static ConcreteMethodList getAllConcreteMethods(barat.reflect.Class clazz) {
    ConcreteMethodList list       = new ConcreteMethodArrayList();
    HashSet            overridden = new HashSet();

    list.addAll(clazz.getConcreteMethods());

    for(ConcreteMethodIterator i = clazz.getConcreteMethods().iterator(); i.hasNext();) {
      AMethod super_method = i.next().getOverriddenMethod();

      if((super_method != null) &&
	 (super_method.containing(AUserType.class) instanceof barat.reflect.Class))
	overridden.add(super_method);
    }

    for(ClassIterator i = getSuperClasses(clazz).iterator(); i.hasNext();) {
      for(ConcreteMethodIterator j = i.next().getConcreteMethods().iterator(); j.hasNext();) {
	ConcreteMethod method       = j.next();
	AMethod        super_method = method.getOverriddenMethod();

	if((super_method != null) &&
	   (super_method.containing(AUserType.class) instanceof barat.reflect.Class))
	  overridden.add(super_method);
	
	if(!overridden.contains(method))
	  list.add(method);
      }
    }

    return list;
  }

  /**
   * @param type Array, or AUserType
   * @return all possible types for this type, i.e. all super classes and
   * implemented interfaces (transitively).
   */
  public static Set getTypes(AType o_type) {
    Set set = (Set)types.get(o_type);

    if(set == null) {
      AType type = Repository.realType(o_type); // Get super types in case of arrays, too
      set = new HashSet();

      set.add(type);

      if(type instanceof barat.reflect.Class) {
	set.add(OBJECT); // Always in the set
	barat.reflect.Class cl = (barat.reflect.Class)type;
	set.addAll(Arrays.asList(Repository.getSuperClasses(cl).toArray()));
	set.addAll(Arrays.asList(Repository.getImplementedInterfaces(cl).toArray()));
      } else {
	set.add(OBJECT); // Always in the set
	Interface in = (Interface)type;
	set.addAll(Arrays.asList(Repository.getExtendedInterfaces(in).toArray()));
      }

      set = Collections.unmodifiableSet(set);
      types.put(o_type, set);
    }

    return set;
  }  

  /** @return element type of type if it is an array or the type itself otherwise
   */
  public static final AType realType(AType type) {
    while(type instanceof Array)
      type = ((Array)type).getElementType();

    return type;
  }

  /** @return true, if this lvalue is really the left hand side of an assignment
   */
  public static final boolean isLHS(ALValue lv) {
    return (lv.container() instanceof Assignment) && lv.aspect().equals("lvalue");
  }

  /** @return true, if this is a method inherited from java.lang.Object
   */
  public static final boolean isObjectMethod(AMethod method) {
    AMethod m = method;

    while((m.getOverriddenMethod() != null) && (m.containingClass() != OBJECT))
      m = m.getOverriddenMethod();

    return m.containingClass() == OBJECT;
  }

  /** @return true, if this class is serializable
   */
  public static final boolean isSerializable(AUserType clazz) {
    if(clazz instanceof Interface)
      return ((Interface)clazz).isSubinterfaceOf(SERIALIZABLE);
    else
      return ((barat.reflect.Class)clazz).isImplementationOf(SERIALIZABLE);
  }

  public static final boolean isBinaryClass(Node node) {
    return !((CompilationUnit)node.containing(CompilationUnit.class)).hasSource();
  }

  /** Method or field accessible from some given class?
   */
  public static boolean isAccessible(AHasModifier field, barat.reflect.Class clazz) {
    if(field.isPublic())
      return true;

    barat.reflect.Class fclass = field.containingClass();

    if(clazz.containingClass() == fclass) // inner class, check for nested classes missing, TODO
      return true;

    if(field.isPrivate())
      return fclass == clazz;

    barat.reflect.Package fpack = (barat.reflect.Package)field.containing(barat.reflect.Package.class);
    barat.reflect.Package cpack = (barat.reflect.Package)clazz.containing(barat.reflect.Package.class);

    if(field.isProtected())
      return (fpack == cpack) || clazz.isSubclassOf(fclass);

    // Default access
    return (fpack == cpack);
  }

}
