package msmd.tests;

import msmd.*;
import org.w3c.dom.*;
import org.w3c.tidy.Tidy;

/**
 *  Description of the Class
 *
 * @author     ian
 * @created    October 27, 2002
 */
public class TestIndex {

    public static void mod(Node a, TreeIndex indx) {
        int idx = indx.getIndexVal(a);
        if (a instanceof Element) {
            ((Element) a).setAttribute("index", Integer.toString(idx));
        }
        int i = 0;
        for (NodeList l = a.getChildNodes(); i < l.getLength(); i++) {
            mod(l.item(i), indx);
        }
    }


    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("usage: TestIndex file");
        }

        Document a = HTMLReader.readPage(args[0]);
        TreeIndex idx = new TreeIndex(a);
        mod(a, idx);
        Tidy t = new Tidy();
        t.setIndentContent(true);
        t.setIndentAttributes(true);
        t.pprint(a, System.out);
    }
}

