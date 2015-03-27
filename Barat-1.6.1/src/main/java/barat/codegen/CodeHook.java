package barat.codegen;
import java.util.*;
import barat.AbstractAttribute;
import barat.Node;

/**
 * Gather pieces of code to be executed within a specific node. I.e. at start or
 * end of the visitXXX() method. Code suspensions are ordered because you can set
 * a precedence value in your code which is by default 0. If you want your suspensions
 * to be called first give them a precedence < 0.
 *
 * @version $Id: CodeHook.java,v 1.1 2001/02/12 10:19:06 dahm Exp $
 * @author  <A HREF="http://www.inf.fu-berlin.de/~dahm">M. Dahm</A>
 */
public class CodeHook extends TreeSet implements AbstractAttribute {
  private static final Object KEY1 = new Object();
  private static final Object KEY2 = new Object();

  public CodeHook() {
    super(new Comparator() {
      public int compare(Object o1, Object o2) {
	int value = ((CodeSuspension)o1).precedence - ((CodeSuspension)o2).precedence;

	if(value == 0) // Same precedence, but we want to have both in the set
	  return 1;
	else
	  return value;
      }
    });
  }

  public Object objectValue() {
    return this;
  }

  /**
   * Perform actions on node after the usual code generating
   * process for this node has finished, i.e. execute all gathered code suspensions.
   *
   * @param clazz the class generator for this class
   * @param node the node currently visited 
   */
  public void execute(ClassGenerator clazz, Node node) {
    for(Iterator i = iterator(); i.hasNext(); )
      ((CodeSuspension)i.next()).execute(clazz, node);
  }

  /** Add code chunk to node to be executed before the node has been visited by the
   * visitXXX() method of the ClassGenerator.
   */
  public static void addToPreHook(Node n, CodeSuspension code) {
    addToHook(n, code, KEY1);
  }

  /** Add code chunk to node to be executed after the node has been visited by the
   * visitXXX() method of the ClassGenerator.
   */
  public static void addToPostHook(Node n, CodeSuspension code) {
    addToHook(n, code, KEY2);
  }

  private static final void addToHook(Node n, CodeSuspension code, Object key) {
    CodeHook c = (CodeHook)n.attributeValue(key);

    if(c == null)
      n.addAttribute(key, c = new CodeHook());

    c.add(code);
  }

  public static CodeHook preHookOf(Node node) {
    return (CodeHook)node.attributeValue(KEY1);
  }

  public static CodeHook postHookOf(Node node) {
    return (CodeHook)node.attributeValue(KEY2);
  }
}
