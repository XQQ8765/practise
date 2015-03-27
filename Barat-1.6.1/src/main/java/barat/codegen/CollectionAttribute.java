package barat.codegen;
import java.util.*;
import barat.AbstractAttribute;
import barat.Node;

/**
 * Gather data related to a node, references are stored only once.
 *
 * @version $Id: CollectionAttribute.java,v 1.1 2001/02/12 10:19:06 dahm Exp $
 * @author  <A HREF="http://www.inf.fu-berlin.de/~dahm">M. Dahm</A>
 */
class CollectionAttribute extends HashSet implements AbstractAttribute {
  public Object objectValue() {
    return this;
  }

  private static final CollectionAttribute create(Node n) {
    CollectionAttribute ca = (CollectionAttribute)n.
      attributeValue(CollectionAttribute.class);

    if(ca == null)
      n.addAttribute(CollectionAttribute.class, ca = new CollectionAttribute());

    return ca;
  }

  /** Gather some data objects specific to a node.
   */
  public static void add(Node n, Object obj) {
    create(n).add(obj);
  }

  /** Add all attributes from another CollectionAttribute
   */
  public static void addAll(Node n, CollectionAttribute ca) {
    create(n).addAll(ca);
  }

  public static final CollectionAttribute collectionOf(Node node) {
    return (CollectionAttribute)node.attributeValue(CollectionAttribute.class);
  }
}
