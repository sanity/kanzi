package msmd.diff;
import org.w3c.dom.*;

/**
 *  Description of the Class
 *
 * @author     ian
 * @created    September 29, 2002
 */
public class SetAttribute implements Difference {
  Element chnode;
  String newElName;
  String newElValue;


  /**
   *Constructor for the AddElement object
   *
   * @param  chnode  Description of the Parameter
   * @param  name    Description of the Parameter
   * @param  value   Description of the Parameter
   */
  public SetAttribute(Element chnode, String name, String value) {
    this.chnode = chnode;
    this.newElName = name;
    this.newElValue = value;
  }


  /**
   *  Description of the Method
   *
   * @param  m  Description of the Parameter
   * @return    Description of the Return Value
   */
  public Difference remap(Remapper m) {
    return new SetAttribute((Element) m.getCounterpart(chnode, new IsAnElementConstraint()), newElName, newElValue);
  }


  public void markup() {
    Util.setMod(chnode, "Set attr " + newElName + " to " + newElValue);
  }


  /**  Description of the Method */
  public void apply() {
    chnode.setAttribute(newElName, newElValue);
  }


  public String toString() {
    return "Set Attribute : " + chnode.getNodeName() + " : " + newElName + "=" + newElValue;
  }
}

