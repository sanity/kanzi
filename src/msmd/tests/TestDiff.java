package msmd.tests;

import msmd.*;
import msmd.diff.*;
import org.w3c.dom.*;
import java.util.*;
import org.w3c.tidy.*;

/**
 *  Description of the Class
 *
 * @author     ian
 * @created    October 3, 2002
 */
public class TestDiff {

  public static void main(String[] args) throws Exception {
    if (args.length < 2) {
      System.err.println("usage: TestDiff original-file target-file");
      System.exit(1);
    }

    Document a = HTMLReader.readPage(args[0]);
    Document b = HTMLReader.readPage(args[1]);
    Tidy t = new Tidy();
    DOMDiff dd = new DOMDiff();
    LinkedList ll = dd.getDiff(a, b);
    Util.assignIDs(a);
    System.out.println("Original: ");
    t.setIndentContent(true);
    System.out.println("Differences: ");
    for (Iterator i = ll.iterator(); i.hasNext(); ) {
      Difference d = (Difference) i.next();
      d.markup();
    }
    t.pprint(a, System.out);
  }
}

