package barat.codegen;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import barat.reflect.*;
import barat.collections.*;
import java.util.*;

/**
 * Generate code for inner classes.
 *
 * @version $Id: InnerClassGenerator.java,v 1.2 2002/01/28 10:48:35 dahm Exp $
 * @author  <A HREF="http://www.inf.fu-berlin.de/~dahm">M. Dahm</A>
 */
public class InnerClassGenerator extends ClassGenerator {
  public InnerClassGenerator(CodeGenerator cg) {
    super(cg);
  }

  /** Inner classes may refer to final variables of the surrounding method. These
   *  accesses need to ba transformed to field accesses, where the fields belongs
   * to the inner class and is initialized in its constructor.
   */
  public void visitVariableAccess(VariableAccess v) {
    // Access to variable from inner class method/field?
    AVariable           lv    = v.getVariable(); 
    barat.reflect.Class clazz = v.containingClass();

    // Can only be read access !
    if(lv.containingClass() != clazz) { // Then we have to transform it to a field access
      applyPreHook(v); // same as in super class, at last
      Type type  = Conversion.getType(lv.getType());
      int  index = cp.addFieldref(implementationNameOf(clazz),
				  InnerClassVisitor.VAR_PREFIX + lv.getName(),
				  type.getSignature());
      int size   = type.getSize();

      il.append(new ALOAD(0)); // Push this
      il.append(new GETFIELD(index));
      increaseStack(size); // size pushed, obj reference popped

      applyPostHook(v); // same as in super class, at last
    }
    else
      super.visitVariableAccess(v);
  }
}
