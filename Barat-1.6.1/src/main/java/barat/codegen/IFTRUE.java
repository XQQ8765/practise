package barat.codegen;

/** 
 * IFNE - Branch on true value, i.e. there is a 1 on the stack. This is merely
 * a convenience class.
 *
 * Stack: ..., value -> ...
 *
 * @version $Id: IFTRUE.java,v 1.2 2002/01/28 10:48:35 dahm Exp $
 * @author  <A HREF="http://www.inf.fu-berlin.de/~dahm">M. Dahm</A>
 */
public final class IFTRUE extends org.apache.bcel.generic.IFNE {
  public IFTRUE(org.apache.bcel.generic.InstructionHandle target) {
    super(target);
  }
}
