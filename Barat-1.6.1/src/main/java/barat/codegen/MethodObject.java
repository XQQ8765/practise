package barat.codegen;

import barat.reflect.AMethod;
import barat.reflect.Parameter;
import barat.collections.ParameterIterator;
import barat.collections.ParameterList;
import org.apache.bcel.generic.Type;
import org.apache.bcel.classfile.Utility;

/**
 * Auxiliary class.
 *
 * "Record" method parameters needed to create an invocation of this method,
 * namely type, name, and arguments. Bridge class between barat and JavaClass API.
 *
 * @version $Id: MethodObject.java,v 1.2 2002/01/28 10:48:35 dahm Exp $
 * @author  <A HREF="http://www.inf.fu-berlin.de/~dahm">M. Dahm</A>
 */
public final class MethodObject {
  public Type[]   arg_types;
  public Type     result_type;
  public String[] arg_names;
  public String   class_name;
  public String   name;
  public int      access;

  public MethodObject(String c, String n, Type r, Type[] a, int acc) {
    class_name  = c;
    name        = n;
    result_type = r;
    arg_types   = a;
    access      = acc;
  }

  public MethodObject(ClassGenerator cg, AMethod method) {
    ParameterList params = method.getParameters();

    arg_types = new Type[params.size()];
    arg_names = new String[params.size()];

    int i = 0;

    for(ParameterIterator pit=params.iterator(); pit.hasNext(); i++) {
      Parameter p  = pit.next();
      arg_types[i] = Conversion.getType(p.getType());
      arg_names[i] = p.getName();
    }

    result_type = Conversion.getType(method.getResultType());

    String qname = (cg == null)? CodeGenerator.implementationNameOf(method) :
      cg.implementationNameOf(method);
    int    index = qname.lastIndexOf('.');

    class_name = qname.substring(0, index);
    name       = method.getName();
    access     = Conversion.getAccessFlags(method);
  }

  public String toString() {
    StringBuffer buf = new StringBuffer(result_type + " " + class_name + "." +
					name + "(");
    for(int i=0; i < arg_names.length; i++) {
      buf.append(arg_types[i] + " " + arg_names[i]);
      if(i < arg_names.length - 1)
	buf.append(", ");
    }

    buf.append(")");
    return buf.toString();
  }
}
