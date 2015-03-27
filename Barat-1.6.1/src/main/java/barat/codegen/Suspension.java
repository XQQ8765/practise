package barat.codegen;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.CompoundInstruction;

/**
 * Generate a piece of code which cannot be done at node discovery time. For instance
 * a field variable may be initialized, but the initialization has to be performed
 * in the &lt;init&gt; and &lt;clinit&gt; method, respectively.
 *
 * @version $Id: Suspension.java,v 1.2 2002/01/28 10:48:35 dahm Exp $
 * @author  <A HREF="http://www.inf.fu-berlin.de/~dahm">M. Dahm</A>
 */
public interface Suspension extends CompoundInstruction {
}
