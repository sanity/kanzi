package msmd.diff;

import java.util.TreeMap;
import org.w3c.dom.*;
import msmd.*;
import java.util.*;

/**
 *  An object which will take a node in one document and return its
 * most likely counterpart in a new document
 *
 * @author     ian
 * @created    September 25, 2002
 */
public class Remapper {

    /**  Description of the Field */
    protected Matcher m;
    /**  Description of the Field */
    protected Document newDoc;
    /**  Description of the Field */
    protected TreeMap hm = new TreeMap();


    /**
     *Constructor for the Remapper object
     *
     * @param  a  The source document for nodes
     * @param  b  The target document for nodes
     */
    public Remapper(Document a, Document b) {
        newDoc = b;
        m = new Matcher();

    }


    /**
     *  Given a node in the source document, return the corresponding
     * node in the target document
     *
     * @param  a  A node in the source document
     * @return    A node in the target document
     */
    public Node getCounterpart(Node a) {
        return getCounterpart(a, null);
    }


    // For some reason, does not find a deterministic
    // number of scores given the same document, same constraint,
    // but a different node for which a counterpart is desired.
    public Node getCounterpart(Node a, Constraint c) {
        m.doMatch(newDoc, a);
        hm.clear();
        int cc = matchAll(5, a, newDoc, c);
        System.err.println("Examined " + cc + " nodes.");
        Node n = null;
        try {
            Float v = (Float) hm.lastKey();
            System.err.println("match[" + v + "]");
            n = (Node) hm.get(v);
            hm.remove(v);
        } catch (NoSuchElementException nse) {
            return null;
        }
        return n;
    }


    /**
     *  Description of the Method
     *
     * @param  maxNodes  Description of the Parameter
     * @param  n         Description of the Parameter
     * @param  b         Description of the Parameter
     * @param  c         Description of the Parameter
     * @return           Description of the Return Value
     */
    int matchAll(int maxNodes, Node n, Node b, Constraint c) {
        if (c == null || c.satisfied(b)) {
            float val = m.compare(n, b, maxNodes);
            hm.put(new Float(val), b);
        }
        int i = 0;

        int v = b.getChildNodes().getLength();
        for (NodeList l = b.getChildNodes(); i < l.getLength(); i++) {
            Node next = l.item(i);
            v += matchAll(maxNodes, n, next, c);
        }
        return 1 + v;
    }
}

