package barat.codegen;

/** 
 * IFFALSE - Branch on false value, i.e. there is a 0 on the stack. This is merely
 * a convenience class.
 *
 * Stack: ..., 1 or 0 -> ...
 *
 * @version $Id: IFFALSE.java,v 1.2 2002/01/28 10:48:35 dahm Exp $
 * @author  <A HREF="http://www.inf.fu-berlin.de/~dahm">M. Dahm</A>
 */
public final class IFFALSE extends org.apache.bcel.generic.IFEQ {
  public IFFALSE(org.apache.bcel.generic.InstructionHandle target) {
    super(target);
  }
}
