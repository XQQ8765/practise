package barat.codegen;

import java.util.*;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.bcel.generic.InstructionHandle;
import barat.reflect.AVariable;

/**
 * Class to manage local variables and their levels/ranges of validity.
 *
 * @version $Id: VariableEnvironment.java,v 1.2 2002/01/28 10:48:35 dahm Exp $
 * @author  <A HREF="http://www.inf.fu-berlin.de/~dahm">M. Dahm</A>
 */
public class VariableEnvironment {
  private Map   map   = new HashMap();
  private Stack stack = new Stack();

  VariableEnvironment() {}

  public void increaseLevel() {
    stack.push(new ArrayList());
  }
  
  /**
   * @param ih until where the local var is valid
   */
  public void decreaseLevel(InstructionHandle ih) {
    List current_vars = (List)stack.pop();

    for(Iterator i = current_vars.iterator(); i.hasNext();) {
      AVariable        var = (AVariable)i.next();
      LocalVariableGen lg  = (LocalVariableGen)map.get(var);

      lg.setEnd(ih);
      map.remove(var);
    }
  }

  public int getLevel() { return stack.size(); }

  /**
   * @param ih from where on the local var is valid
   */
  public void put(AVariable var, LocalVariableGen lg, InstructionHandle ih) {
    //System.out.println("At level " + getLevel() + " put " + var);
    map.put(var, lg);

    List current_vars = (List)stack.peek();
    current_vars.add(var);
    lg.setStart(ih);
  }

  /** @return all local variables valid at current level.
   */
  public List getLocalVars() {
    List list = new ArrayList();

    for(Iterator i = stack.iterator(); i.hasNext(); )
      list.addAll((List)i.next());

    return list;
  }

  public String toString() {
    return "At level " + getLevel() + " " + getLocalVars().toString();
  }

  public LocalVariableGen get(AVariable var) {
    return (LocalVariableGen)map.get(var);
  }
}
