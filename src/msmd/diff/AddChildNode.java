package msmd.diff;
import org.w3c.dom.*;

/**
 *  Description of the Class
 *
 * @author     ian
 * @created    September 29, 2002
 */
public class AddChildNode implements Difference {
  Node chnode;
  Node newchild, refchild;
  int pos;


  /**
   *Constructor for the AddChildNode object
   *
   * @param  chnode    Description of the Parameter
   * @param  newchild  Description of the Parameter
   */
  public AddChildNode(Node chnode, Node newchild) {
    this(chnode, newchild, null);
  }


  /**
   *Constructor for the AddChildNode object
   *
   * @param  chnode    Description of the Parameter
   * @param  newchild  Description of the Parameter
   * @param  refchild  Description of the Parameter
   */
  public AddChildNode(Node chnode, Node newchild, Node refchild) {
    this.chnode = chnode;
    this.newchild = newchild.cloneNode(true);
    this.pos = pos;
  }


  /**
   *  Description of the Method
   *
   * @param  m  Description of the Parameter
   * @return    Description of the Return Value
   */
  public Difference remap(Remapper m) {
    if (refchild != null) {
      return new AddChildNode(m.getCounterpart(chnode, new CanHaveChildrenConstraint()), newchild, m.getCounterpart(refchild));
    } else {
      return new AddChildNode(m.getCounterpart(chnode, new CanHaveChildrenConstraint()), newchild);
    }
  }


  public void markup() {
    if (refchild != null) {
      Util.setMod(chnode, "Add new child node " + newchild.getNodeName() + " before " + Util.getId(refchild));
    } else {
      Util.setMod(chnode, "Add new child node " + newchild.getNodeName() + " at end");
    }
  }


  /**  Description of the Method */
  public void apply() {
    if (refchild != null) {
      chnode.insertBefore(newchild, refchild);
    } else {
      chnode.appendChild(newchild);
    }
  }


  public String toString() {
    if (refchild != null) {
      return "AddChildNode : " + chnode.getNodeName() + " : " + newchild.getNodeName() + " before " + refchild.getNodeName();
    } else {
      return "AddChildNode : " + chnode.getNodeName() + " : " + newchild.getNodeName() + " at end";
    }
  }
}

