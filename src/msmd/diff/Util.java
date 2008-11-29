package msmd.diff;
import msmd.*;
import org.w3c.dom.*;

/**
 *  Description of the Class
 *
 * @author     ian
 * @created    October 13, 2002
 */
public class Util {
  // Not thread safe
  static int id = 0;


  public static String getId(Node n) {
    if (n instanceof Element) {
      return ((Element) n).getAttribute("id");
    } else {
      return n.getNodeName() + ":" + n.getNodeValue();
    }
  }


  public static void setMod(Node n, String s) {
    if (n instanceof Element) {
      String t = ((Element) n).getAttribute("mod");
      if (t != null) {
        ((Element) n).setAttribute("mod", t + " and " + s);
      } else {
        ((Element) n).setAttribute("mod", s);
      }
    }
  }


  static TreeIndex t;


  public static void assignIDs(Node n) {
    t = new TreeIndex(n);
    assignIDR(n);
  }


  public static void assignIDR(Node n) {

    if (n instanceof Element) {
      ((Element) n).setAttribute("id", "" + id + " (" + t.getIndexVal(n) + ")");
      id++;
    } else {
      n.setNodeValue(n.getNodeValue() + " - " + id + " (" + t.getIndexVal(n) + ")");
    }

    int i = 0;
    for (NodeList l = n.getChildNodes(); i < l.getLength(); i++) {
      assignIDR((Node) l.item(i));
    }
  }
}

