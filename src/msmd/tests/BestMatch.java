package msmd.tests;

import msmd.*;
import org.w3c.dom.*;
import org.w3c.dom.Node;
import java.util.*;
import org.w3c.tidy.*;

/**
 *  Description of the Class
 *
 * @author     ian
 * @created    September 29, 2002
 */
public class BestMatch {

  /**
   *  Description of the Method
   *
   * @param  d  Description of the Parameter
   * @return    Description of the Return Value
   */
  public static Node findHTML(Document d) {
    int i = 0;
    for (NodeList l = d.getChildNodes(); i < l.getLength(); i++) {
      if (l.item(i).getNodeName().equalsIgnoreCase("HTML")) {
        return l.item(i);
      }
    }
    return null;
  }


  /**
   *  Description of the Method
   *
   * @param  d       Description of the Parameter
   * @param  target  Description of the Parameter
   * @return         Description of the Return Value
   */
  static Node findNode(Node d, String target) {
    //        System.err.println("!"+d.getNodeName()+":"+(d instanceof Element ? ((Element)d).getAttribute("id") : ""));
    if (d instanceof Element &&
        d != null && ((Element) d).getAttribute("id") != null &&
        ((Element) d).getAttribute("id").equals(target)) {
      return d;
    } else {
      int i = 0;
      for (NodeList l = d.getChildNodes(); i < l.getLength(); i++) {
        Node next = l.item(i);
        Node res = findNode(next, target);
        if (res != null) {
          return res;
        }
      }
    }
    return null;
  }


  static TreeMap hm = new TreeMap();


  /**
   *  Description of the Method
   *
   * @param  mc     Description of the Parameter
   * @param  depth  Description of the Parameter
   * @param  n      Description of the Parameter
   * @param  m      Description of the Parameter
   */
  static void matchAll(Matcher mc, int depth, Node n, Node m) {
    float val = mc.compare(n, m, depth);
    hm.put(new Float(val), m);
    int i = 0;
    for (NodeList l = m.getChildNodes(); i < l.getLength(); i++) {
      Node next = l.item(i);
      matchAll(mc, depth, n, next);
    }
  }


  /**
   *  The main program for the BestMatch class
   *
   * @param  args           The command line arguments
   * @exception  Exception  Description of the Exception
   */
  public static void main(String[] args) throws Exception {
    if (args.length < 4) {
      System.err.println("usage: BestMatch original-file target-file original-target-id depth");
      System.exit(1);
    }

    Document a = HTMLReader.readPage(args[0]);
    Document b = HTMLReader.readPage(args[1]);

    Node target_one = findNode(findHTML(a), args[2]);

    Matcher m = new Matcher();
    m.doMatch(null, null);
    matchAll(m, Integer.parseInt(args[3]), target_one, b);
    for (Iterator i = hm.keySet().iterator(); i.hasNext(); ) {
      Float v = (Float) i.next();
      Node n = (Node) hm.get(v);
      if (n instanceof Element) {
        ((Element) n).setAttribute("score", v.toString());
      }
    }

    Float v = (Float) hm.lastKey();
    Node n = (Node) hm.get(v);
    System.err.println("best[" + v + "] " + n.getNodeName() + " ; " + n.getNodeValue());
    if (n instanceof Element) {
      ((Element) n).setAttribute("best", v.toString());
    }
    Tidy t = new Tidy();
    t.setIndentContent(true);
    //t.setIndentAttributes(true);
    t.pprint(b, System.out);
  }
}

