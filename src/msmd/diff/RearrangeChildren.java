package msmd.diff;
import msmd.*;
import org.w3c.dom.*;
import java.util.*;

/**
 *  Description of the Class
 *
 * @author     ian
 * @created    September 29, 2002
 */
public class RearrangeChildren implements Difference {
  Node chnode;
  LinkedList newOrder = new LinkedList();


  // Pun intended

  /**
   *Constructor for the RearrangeChildren object
   *
   * @param  chnode  Description of the Parameter
   */
  public RearrangeChildren(Node chnode) {
    this.chnode = chnode;
  }


  /**
   *  Sets the nextChild attribute of the RearrangeChildren object
   *
   * @param  nextChild  The new nextChild value
   */
  public void setNextChild(Node nextChild) {
    newOrder.add(nextChild);
  }


  // Will this difference actually do anything?
  public boolean doAnything() {
    NodeList nl = chnode.getChildNodes();
    int x = 0;
    for (ListIterator li = newOrder.listIterator(); (li.hasNext() && (x < nl.getLength())); ) {
      Node no = (Node) li.next();
      if (nl.item(x) != no) {
        return true;
      }
      x++;
    }
    if (x != nl.getLength()) {
      return true;
    } else {
      return false;
    }
  }


  /**
   *  Description of the Method
   *
   * @param  m  Description of the Parameter
   * @return    Description of the Return Value
   */
  public Difference remap(Remapper m) {
    Node cpn = m.getCounterpart(chnode, new CanHaveChildrenConstraint());
    RearrangeChildren rc = new RearrangeChildren(cpn);
    ChildConstraint cc = new ChildConstraint(cpn);
    for (ListIterator li = newOrder.listIterator(); li.hasNext(); ) {
      rc.setNextChild(m.getCounterpart((Node) li.next(), cc));
    }
    return rc;
  }


  /**  Description of the Method */
  public void apply() {
    // Remove all the children
    while (chnode.hasChildNodes()) {
      chnode.removeChild(chnode.getFirstChild());
    }

    // Now put them back in the correct order
    for (ListIterator li = newOrder.listIterator(); li.hasNext(); ) {
      chnode.appendChild((Node) li.next());
    }
  }


  public void markup() {
    StringBuffer ret = new StringBuffer();
    for (ListIterator li = newOrder.listIterator(); li.hasNext(); ) {
      ret.append(Util.getId((Node) li.next()) + " ");
    }
    Util.setMod(chnode, "Rearranged Children to: " + ret);
  }


  public String toString() {
    StringBuffer ret = new StringBuffer("Rearrange Children : ");
    ret.append(chnode.getNodeName() + ": (");
    for (ListIterator li = newOrder.listIterator(); li.hasNext(); ) {
      ret.append(((Node) li.next()).getNodeName() + ", ");
    }
    ret.append(")");
    return ret.toString();
  }
}

