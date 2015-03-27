package barat.codegen;

import barat.*;
import barat.collections.*;
import java.util.*;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.Synthetic;
import org.apache.bcel.generic.Type;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.BasicType;
import barat.parser.AttributeWrapper;
import barat.reflect.*;

/**
 * Repository for Barat classes related informations.
 *
 * @version $Id: Repository.java,v 1.2 2002/01/28 10:48:35 dahm Exp $
 * @author <A HREF="http://www.inf.fu-berlin.de/~dahm">M. Dahm</A> 
 */
public abstract class Repository {
  public static final barat.reflect.Class     OBJECT       = Barat.getObjectClass();
  public static final barat.reflect.Class     STRING       = Barat.getStringClass();
  public static final barat.reflect.Class     THROWABLE    = Barat.getClass("java.lang.Throwable");
  public static final barat.reflect.Class     THREAD       = Barat.getClass("java.lang.Thread");
  public static final barat.reflect.Class     CLASS        = Barat.getClass("java.lang.Class");
  public static final barat.reflect.Interface SERIALIZABLE = Barat.getInterface("java.io.Serializable");

  private static Map classes    = new HashMap(); // class -> list(class)
  private static Map interfaces = new HashMap(); // interface/class -> list(interfaces)
  private static Map fields     = new HashMap(); // class -> list(fields)
  private static Map types      = new HashMap(); // type -> set(type)

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

  /** @return all interfaces extended by this interface (transitive)
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

  /** @return all interfaces implemented by this class (transitive)
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

  /** @return all abstract methods the class (including those of super classes and inherited
   * from interfaces)
   */
  public static AbstractMethodList getAllAbstractMethods(barat.reflect.Class clazz) {
    AbstractMethodList list = new AbstractMethodArrayList();

    list.addAll(clazz.getAbstractMethods());

    for(ClassIterator i = getSuperClasses(clazz).iterator(); i.hasNext();)
      list.addAll(i.next().getAbstractMethods());

    for(InterfaceIterator i = getImplementedInterfaces(clazz).iterator(); i.hasNext();)
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
	
	if(!overridden.contains(method) && !isSynthetic(method))
	  list.add(method);
      }
    }

    return list;
  }

  /**
   * @param type Array, or AUserType
   * @return all possible types for this type, i.e. all super classes and
   * implemented interfaces (transitive).
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

  private static final boolean isSynthetic(AMethod method) {
    Attribute[] attrs = (Attribute[])method.attributeValue(AttributeWrapper.class);

    if(attrs != null)
      return attrs[0] instanceof Synthetic;

    return false;
  }

  public static final Constructor getEmptyConstructor(barat.reflect.Class clazz) {
    for(ConstructorIterator i=clazz.getConstructors().iterator(); i.hasNext();) {
      Constructor c = i.next();
      
      if(c.getParameters().size() == 0)
	return c;
    }
    
    return null;
  }

  public static final AType realType(AType type) {
    while(type instanceof Array)
      type = ((Array)type).getElementType();

    return type;
  }

  public static final Type realType(Type type) {
    if(type instanceof ArrayType)
      return ((ArrayType)type).getBasicType();
    else
      return type;
  }

  public static final boolean isLHS(ALValue lv) {
    return (lv.container() instanceof Assignment) && lv.aspect().equals("lvalue");
  }

  /** @return true, if node is not a PrimitiveType, which will cause qualifiers
   * to be ignored
   */
  public static final boolean isPrimitive(AType t) {
    t = realType(t);
    return (t == null) || (t instanceof PrimitiveType);
  }

  public static final boolean isPrimitive(Type t) {
    return (realType(t) instanceof BasicType);
  }

  public static final boolean isMainMethod(ConcreteMethod method) {
    return method.getName().equals("main") && method.isStatic() && method.isPublic();
  }

  public static final boolean isObjectMethod(AMethod method) {
    AMethod m = method;

    while((m.getOverriddenMethod() != null) && (m.containingClass() != OBJECT))
      m = m.getOverriddenMethod();

    return m.containingClass() == OBJECT;
  }

  public static final AUserType containingUserType(Node node) {
    return (AUserType)node.containing(AUserType.class);
  }

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

