package msmd;

import org.w3c.dom.*;
import java.util.Stack;
import java.util.HashMap;

/**
 *  Description of the Class
 *
 * @author     ian
 * @created    September 25, 2002
 */
public class TreeIndex {

    protected final static int INDEX = 0,
            CHILDCOUNT = 1, DEPTH = 2;
    protected final static Object DEPTHMARKER = new Object();

    /**  Description of the Field */
    protected HashMap index;
    protected final static int
            A = 0x72fc9453,
            B = 0x512b69ed,
            C = 0xfeedface,
            D = 0xdeadbeef,
            E = 0x8c921f73;


    final static int rotl(int v, int bits) {
        return (v << bits) | (v >>> (32 - bits));
    }


    /**
     * Indexes the tree rooted at the given parameter
     *
     * @param  rootNode  Description of the Parameter
     */
    public TreeIndex(Node rootNode) {
        index = new HashMap();
        index(rootNode, 0);
    }


    /**
     *  Gets the indexVal attribute of the TreeIndex object
     *
     * @param  n  Description of the Parameter
     * @return    The indexVal value
     */
    public int getIndexVal(Node n) {
        int[] i = (int[]) index.get(n);
        if (i == null) {
            return -1;
        } else {
            return i[INDEX];
        }
    }


    /**
     *  Gets the depth attribute of the TreeIndex object (the
     * depth below the given node)
     *
     * @param  n  Description of the Parameter
     * @return    The indexVal value
     */
    public int getDepth(Node n) {
        int[] i = (int[]) index.get(n);
        if (i == null) {
            return -1;
        } else {
            return i[DEPTH];
        }
    }


    /**
     *  Gets the child count attribute of the TreeIndex object (the
     *  numbers of nodes below the g
     *iven node)
     *
     * @param  n  Description of the Parameter
     * @return    The childCount value
     */
    public int getChildCount(Node n) {
        int[] i = (int[]) index.get(n);
        if (i == null) {
            return -1;
        } else {
            return i[CHILDCOUNT];
        }
    }


    /**
     *  Gets the node attribute of the TreeIndex object
     *
     * @param  indexVal  Description of the Parameter
     * @return           The node value
     */
    public Node getNode(int indexVal) {
        return (Node) index.get(new Integer(indexVal));
    }


    /**
     *  Description of the Method
     *
     * @param  n  Description of the Parameter
     * @param  d  Description of the Parameter
     * @return    Description of the Return Value
     */
    int index(Node n, int d) {
        int childCount = 0;
        if (n.hasChildNodes()) {
            NodeList nm = n.getChildNodes();
            for (int i = 0; i < nm.getLength(); i++) {
                childCount += index(nm.item(i), d + 1);
            }
        }

        int iv = rotl(hashNode(n), childCount % 29);
        int[] v = new int[3];
        v[INDEX] = iv;
        v[CHILDCOUNT] = childCount;
        v[DEPTH] = d;
        index.put(n, v);
        index.put(new Integer(iv), n);
        return childCount + 1;
    }


    /**
     *  Description of the Method
     *
     * @param  n  Description of the Parameter
     * @return    Description of the Return Value
     */
    int hashNode(Node n) {
        int v = hashNodeDetails(n);
        if (n.hasChildNodes()) {
            NodeList nm = n.getChildNodes();
            for (int i = 0; i < nm.getLength(); i++) {
                v = rotl(v, 13) ^ getIndexVal(nm.item(i));
            }
        }
        return v;
    }


    /**
     *  Description of the Method
     *
     * @param  n  Description of the Parameter
     * @return    Description of the Return Value
     */
    static int hashNodeDetails(Node n) {
        int v = 0;
        if (n.getNodeName() != null) {
            v = rotl(n.getNodeName().hashCode(), 7);
        }
        if (n.getNodeValue() != null) {
            v ^= rotl(n.getNodeValue().hashCode(), 13);
        }
        v ^= ((int) n.getNodeType() << 24);

        NamedNodeMap nm = n.getAttributes();
        if (nm != null) {
            for (int i = 0; i < nm.getLength(); i++) {
                switch (i % 4) {
                    case 0:
                        v ^= A;
                    case 1:
                        v ^= B;
                    case 2:
                        v ^= C;
                    case 3:
                        v ^= D;
                }
                v ^= hashNodeDetails(nm.item(i));
            }
        }
        return v;
    }


    /**
     *  The main program for the TreeIndex class
     *
     * @param  args           The command line arguments
     * @exception  Exception  Description of the Exception
     */
    public static void main(String[] args) throws Exception {
        Document d = HTMLReader.readPage(args[0]);
        TreeIndex i = new TreeIndex(d);
        Node a = msmd.tests.BestMatch.findHTML(d);
        System.err.println(i.getIndexVal(a));
        d = HTMLReader.readPage(args[0]);
        i = new TreeIndex(d);
        Node b = msmd.tests.BestMatch.findHTML(d);
        System.err.println(i.getIndexVal(b));
    }

}

