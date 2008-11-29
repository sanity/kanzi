package msmd.diff;
import org.w3c.dom.*;

// TODO: This should be called DeleteAttribute

/**
 *  Description of the Class
 *
 * @author     ian
 * @created    September 29, 2002
 */
public class DeleteElement implements Difference {
  Node chnode;
  String elName;


  /**
   *Constructor for the AddElement object
   *
   * @param  chnode  Description of the Parameter
   * @param  name    Description of the Parameter
   */
  public DeleteElement(Node chnode, String name) {
    this.chnode = chnode;
    this.elName = name;
  }


  /**
   *  Description of the Method
   *
   * @param  m  Description of the Parameter
   * @return    Description of th
   * Return Value
   */
  public Difference remap(Remapper m) {
    return new DeleteElement(m.getCounterpart(chnode, new IsAnElementConstraint()), elName);
  }


  public void markup() {
    Util.setMod(chnode, "Remove element " + elName);
  }


  /**  Description of the Method */
  public void apply() {
    NamedNodeMap nnm = chnode.getAttributes();
    nnm.removeNamedItem(elName);
  }


  public String toString() {
    return "Delete Element : " + chnode.getNodeName() + " : " + " El: " + elName;
  }
}

