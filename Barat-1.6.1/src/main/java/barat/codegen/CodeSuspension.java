package barat.codegen;

import barat.Node;

/**
 * Perform additional actions when generating code. execute() is
 * called after or before the usual actions have been performed in visitXXX in
 * the class generator. <BR> Code suspensions may be given a precedence valeu
 * which is by default 0 to specify in which order to execute them. So if you
 * want a suspesnion to be called first give it a precedence value < 0.
 *
 * @version $Id: CodeSuspension.java,v 1.1 2001/02/12 10:19:06 dahm Exp $
 * @author <A HREF="http://www.inf.fu-berlin.de/~dahm">M. Dahm</A>
 */
public abstract class CodeSuspension {
  int precedence = 0;

  public CodeSuspension() {}

  public CodeSuspension(int prec) {
    precedence = prec;
  }

  /**
   * Perform actions on this node after the usual code generating
   * process for this node has finished.
   *
   * @param clazz the class generator object for this class
   * @param node the node currently visited 
   */
  public abstract void execute(ClassGenerator clazz, Node node);
}
