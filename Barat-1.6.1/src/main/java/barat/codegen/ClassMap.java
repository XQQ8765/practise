package barat.codegen;

import java.util.*;

/**
 * Map class names or Class objects to ClassGenerator objects within a compilation unit.
 *
 * @version $Id: ClassMap.java,v 1.1 2001/02/12 10:19:06 dahm Exp $
 * @author  <A HREF="http://www.inf.fu-berlin.de/~dahm">M. Dahm</A>
 */
class ClassMap {
  private HashMap table = new HashMap();
  private Stack   stack = new Stack();

  final void put(String class_name, ClassGenerator cg) {
    table.put(class_name, cg);
    stack.push(cg);
  }
  
  final void put(barat.reflect.Class cl, ClassGenerator cg) {
    put(CodeGenerator.implementationNameOf(cl), cg);
  }
  
  final ClassGenerator get(String class_name) {
    return (ClassGenerator)table.get(class_name);
  }
  
  final ClassGenerator get(barat.reflect.Class cl) {
    return (ClassGenerator)table.get(CodeGenerator.implementationNameOf(cl));
  }

  final Stack elements() { return stack; }
}
