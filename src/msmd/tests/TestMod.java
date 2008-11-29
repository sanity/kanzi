package msmd.tests;

import msmd.*;
import msmd.diff.*;
import org.w3c.dom.*;
import java.util.*;
import java.io.*;
import org.w3c.tidy.*;

/**
 *  Description of the Class
 *
 * @author     ian
 * @created    October 3, 2002
 */
public class TestMod {

  public static void main(String[] args) throws Exception {
    if (args.length < 3) {
      System.err.println("usage: TestDiff original-file modified-original target output-file");
      System.exit(1);
    }

    Document a = HTMLReader.readPage(args[0]);
    Document b = HTMLReader.readPage(args[1]);
    Document c = HTMLReader.readPage(args[2]);
    Tidy t = new Tidy();

    DOMDiff dd = new DOMDiff();
    LinkedList ll = dd.getDiff(a, b);
    System.out.println("Original: ");
    System.out.println("Result:");
    //  t.pprint(a, System.out);
    System.out.println("Confirming that there is now no difference");
    LinkedList ll2 = dd.getDiff(a, b);
    System.out.println("Differences: ");
    for (Iterator i = ll2.iterator(); i.hasNext(); ) {
      Difference d = (Difference) i.next();
      System.out.println(d);
    }

    Remapper rm = new Remapper(a, c);
    LinkedList rll = new LinkedList();
    System.out.println("Number of differences: " + ll.size());
    int ctr = 0;
    for (Iterator i = ll.iterator(); i.hasNext(); ) {
      Difference d = (Difference) i.next();
      System.out.print(" " + ctr++);
      rll.add(d.remap(rm));
    }
    System.out.println("");
    for (Iterator i = rll.iterator(); i.hasNext(); ) {
      Difference d = (Difference) i.next();
      d.apply();
    }
    if (args.length == 4) {
      t.pprint(c, new FileOutputStream(new File(args[3])));
    } else {
      t.pprint(c, System.out);
    }
  }
}

