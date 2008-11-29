package msmd;
import java.util.*;
import java.io.*;
import org.w3c.dom.*;
import org.w3c.dom.Node;
import javax.xml.parsers.*;

/**
 *  Description of the Class
 *
 * @author     ian
 * @created    September 18, 2002
 */
public class Matcher {

    /**  Description of the Field */
    public final static boolean DEBUG = false;

    /**  Description of the Field */
    public float nodeSiblingPosWeight = 0.2F;
    /**  Description of the Field */
    public float nodeNameWeight = 0.2F;
    /**  Description of the Field */
    public float nodeValueWeight = 0.2F;
    /**  Description of the Field */
    public float attributeNumberWeight = 0.2F;
    /**  Description of the Field */
    public float childNumberWeight = 0.2F;

    /**  Description of the Field */
    public float childrenWeight = 0.7F;
    /**  Description of the Field */
    public float attributeWeight = 0.6F;
    /**  Description of the Field */
    public float parentWeight = 0.7F;
    /**  Description of the Field */
    public float localWeight = 0.2F;

    /**  Description of the Field */
    public Hashtable cache;


    /**
     * Construct a new matcher object, oldDoc is the original document
     * from which fragments are taken
     */
    public Matcher() { }


    /**
     *  Identify candidate positions for a fragment in this new document
     *
     * @param  newDoc    The new document to be searched
     * @param  fragRoot  The fragment (from oldDoc) to be sought
     */
    public void doMatch(Document newDoc, Node fragRoot) {
        // Clear the cache
        cache = new Hashtable();
    }


    /**
     *  Gets the next closest match, must be called after a doMatch()
     *
     * @return    The next closest match
     */
    public Match getNextMatch() {
        return null;
    }


    /**
     *  Description of the Method
     *
     * @param  a  Description of the Parameter
     * @param  b  Description of the Parameter
     * @return    Description of the Return Value
     */
    protected HashSet seenBefore;

    int maxDepth = 0;


    /**
     *  Description of the Method
     *
     * @param  a      Description of the Parameter
     * @param  b      Description of the Parameter
     * @param  depth  Description of the Parameter
     * @param  wt     Description of the Parameter
     * @return        Description of the Return Value
     */

    public float compare(Node a, Node b, int depth, WeightTracker wt) {
        maxDepth = depth;
        seenBefore = new HashSet();
        return recursiveComparison(a, b, depth, wt);
    }


    /**
     *  Description of the Method
     *
     * @param  a      Description of the Parameter
     * @param  b      Description of the Parameter
     * @param  depth  Description of the Parameter
     * @return        Description of the Return Value
     */
    public float compare(Node a, Node b, int depth) {
        maxDepth = depth;
        seenBefore = new HashSet();
        return recursiveComparison(a, b, depth, null);
    }


    /**
     *  Description of the Method
     *
     * @param  a      Description of the Parameter
     * @param  b      Description of the Parameter
     * @param  depth  Description of the Parameter
     * @param  wt     Description of the Parameter
     * @return        Description of the Return Value
     */
    public float recursiveComparison(Node a, Node b, int depth, WeightTracker wt) {
        if (DEBUG) {
            for (int x = 0; x < (maxDepth - depth) * 4; x++) {
                System.err.print(" ");
            }
            System.err.println("recursiveComparison(" + a.getNodeName() +
                    ", " + b.getNodeName() + ", " + depth + ")");
        }
        // Prevent loops
        if (seenBefore.contains(a)) {
            if (DEBUG) {
                for (int x = 0; x < (maxDepth - depth) * 4; x++) {
                    System.err.print(" ");
                }
                System.err.println("-> (seen before) -1");
            }
            return -1;
        }
        seenBefore.add(a);
        float localCloseness = compareNodes(a, b, wt);

        // Out of depth
        if (depth < 1) {
            if (DEBUG) {
                for (int x = 0; x < (maxDepth - depth) * 4; x++) {
                    System.err.print(" ");
                }
                System.err.println("-> (out of depth) " + localCloseness);
            }
            if (wt != null) {
                wt.local += localCloseness;
            }
            return localCloseness;
        }
        depth--;

        // Parent recurse
        float parentCloseness = -1;
        if ((a.getParentNode() != null) && (b.getParentNode() != null)) {
            parentCloseness = recursiveComparison(a.getParentNode(), b.getParentNode(), depth, wt);
        }

        // Attribute recurse
        float attrCloseness = 0;
        NamedNodeMap nnma = a.getAttributes();
        NamedNodeMap nnmb = b.getAttributes();
        int act = 0;
        if ((nnma != null) && (nnmb != null)) {
            for (int x = 0; x < Math.min(nnma.getLength(), nnmb.getLength()); x++) {
                float c = recursiveComparison(nnma.item(x), nnmb.item(x), depth, wt);
                if (c != -1) {
                    act++;
                    attrCloseness += c;
                }
            }
        }
        if (act == 0) {
            attrCloseness = -1;
        } else {
            attrCloseness = (attrCloseness / act);
        }

        // Children recurse
        float childCloseness = 0;
        NodeList nla = a.getChildNodes();
        NodeList nlb = b.getChildNodes();
        act = 0;
        if ((nla != null) && (nlb != null)) {
            if (Math.max(nla.getLength(), nlb.getLength()) != 0) {
                for (int x = 0; x < Math.min(nla.getLength(), nlb.getLength()); x++) {
                    float c = recursiveComparison(nla.item(x), nlb.item(x), depth, wt);
                    if (c != -1) {
                        act++;
                        childCloseness += c;
                    }
                }
            }
        }
        if (act == 0) {
            childCloseness = -1;
        } else {
            childCloseness = (childCloseness / act);
        }

        float ret = 0;
        float tret = 0;
        if (localCloseness != -1) {
            ret += localCloseness * localWeight;
            if (wt != null) {
                wt.local += localCloseness * localWeight;
            }
            tret += localWeight;
        }
        if (attrCloseness != -1) {
            ret += attrCloseness * attributeWeight;
            if (wt != null) {
                wt.attribute += attrCloseness * attributeWeight;
            }
            tret += attributeWeight;
        }
        if (childCloseness != -1) {
            ret += childCloseness * childrenWeight;
            if (wt != null) {
                wt.children += childCloseness * childrenWeight;
            }
            tret += childrenWeight;
        }
        if (parentCloseness != -1) {
            ret += parentCloseness * parentWeight;
            if (wt != null) {
                wt.parent += parentCloseness * parentWeight;
            }
            tret += parentWeight;
        }
        if (tret != 0) {
            if (DEBUG) {
                for (int x = 0; x < (maxDepth - depth - 1) * 4; x++) {
                    System.err.print(" ");
                }
                System.err.println("-> (" + localCloseness + ", " + attrCloseness + ", " +
                        childCloseness + ", " + parentCloseness + " : " + tret + ") " + ret / tret);
            }
            return ret / tret;
        } else {
            if (DEBUG) {
                for (int x = 0; x < (maxDepth - depth - 1) * 4; x++) {
                    System.err.print(" ");
                }
                System.err.println("-> -1 (tret == 0)");
            }
            return -1;
        }
    }


    /**
     *  Compares two nodes and determines their degree of similarity
     *
     * @param  a   The first node to be compared
     * @param  b   The second node to be compared
     * @param  wt  Description of the Parameter
     * @return     The degree of similarity, 0=totally dissimilar 1=virtually identical
     */
    public float compareNodes(Node a, Node b, WeightTracker wt) {

        float ret = 0;
        /*
         *  Comment out as this would screw up WeightTracker
         *  / If closeness has been cached, then return immediately
         *  Float f = (Float) cache.get(new Integer(a.hashCode() ^ b.hashCode()));
         *  if (f != null) {
         *  return f.floatValue();
         *  }
         */
        float nodePosScore = 0;
        // Determine relative positions of each node within parent tree
        if ((a.getParentNode() != null) && (b.getParentNode() != null)) {
            int apos = 0;
            int bpos = 0;
            Node pp = a;
            while (pp != null) {
                pp = pp.getPreviousSibling();
                apos++;
            }
            pp = b;
            while (pp != null) {
                pp = pp.getPreviousSibling();
                bpos++;
            }
            if (apos != bpos) {
                nodePosScore = 1F / ((float) (Math.abs(apos - bpos)));
            } else {
                nodePosScore = 1F;
            }
        } else if ((a.getParentNode() == null) && (b.getParentNode() == null)) {
            nodePosScore = 1F;
        }
        ret += nodeSiblingPosWeight * nodePosScore;
        if (wt != null) {
            wt.nodeSiblingPos += nodeSiblingPosWeight * nodePosScore;
        }
        if ((a.getNodeName() != null) && (b.getNodeName() != null) &&
                (a.getNodeName().toLowerCase().equals(b.getNodeName().toLowerCase()))) {
            ret += nodeNameWeight;
            if (wt != null) {
                wt.nodeName += nodeNameWeight;
            }
        }
        ret += attributeNumberWeight * compAttributeNo(a, b);
        if (wt != null) {
            wt.attributeNumber += attributeNumberWeight * compAttributeNo(a, b);
        }
        ret += childNumberWeight * compChildNo(a, b);
        if (wt != null) {
            wt.childNumber += childNumberWeight * compChildNo(a, b);
        }
        float sd = StringDist.dist(a.getNodeValue(), b.getNodeValue());
        ret += nodeValueWeight * sd;
        if (wt != null) {
            wt.nodeValue += nodeValueWeight * sd;
        }
        // Cache this
        cache.put(new Integer(a.hashCode() ^ b.hashCode()), new Float(ret));

        if (DEBUG) {
            System.err.println(":::::::::::::::::: compareNodes(" + a.getNodeName() + ", " + b.getNodeName() +
                    ") -> " + ret);
        }
        return ret;
    }


    /**
     *  Description of the Method
     *
     * @param  a  Description of the Parameter
     * @param  b  Description of the Parameter
     * @return    Description of the Return Value
     */
    protected float compAttributeNo(Node a, Node b) {
        // Attribute number comparison
        int la;
        int lb;
        NamedNodeMap nnma = a.getAttributes();
        NamedNodeMap nnmb = b.getAttributes();
        if (nnma != null) {
            la = nnma.getLength();
        } else {
            la = 0;
        }
        if (nnmb != null) {
            lb = nnmb.getLength();
        } else {
            lb = 0;
        }

        return (float) (1 / Math.pow(2, (Math.abs(la - lb))));
    }


    /**
     *  Description of the Method
     *
     * @param  a  Description of the Parameter
     * @param  b  Description of the Parameter
     * @return    Description of the Return Value
     */
    protected float compChildNo(Node a, Node b) {
        // Attribute number comparison
        int la;
        int lb;
        NodeList nnma = a.getChildNodes();
        NodeList nnmb = b.getChildNodes();
        if (nnma != null) {
            la = nnma.getLength();
        } else {
            la = 0;
        }
        if (nnmb != null) {
            lb = nnmb.getLength();
        } else {
            lb = 0;
        }
        return (float) (1 / Math.pow(2, (Math.abs(la - lb))));
    }


    /**
     *  Description of the Class
     *
     * @author     ian
     * @created    September 18, 2002
     */
    public class Match {
        /**  Description of the Field */
        public Node n;
        /**  Description of the Field */
        public float closeness;
    }


    /**
     *  Description of the Class
     *
     * @author     ian
     * @created    September 21, 2002
     */
    public class WeightTracker {
        /**  Description of the Field */
        public float nodeName = 0, nodeValue = 0, attributeNumber = 0, childNumber = 0,
                children = 0, attribute = 0, parent = 0, local = 0, nodeSiblingPos = 0;


        /**Constructor for the WeightTracker object */
        public WeightTracker() { }


        /**
         *  Description of the Method
         *
         * @return    Description of the Return Value
         */
        public String toString() {
            return "nodeName:" + nodeName + ", nodeValue: " + nodeValue + ", attrNo: " + attributeNumber +
                    ", childNo: " + childNumber + ", chldrn: " + children + ", attr: " + attribute +
                    ", parent: " + parent + ", local: " + local + ", nodeSiblingPos: " + nodeSiblingPos;
        }


        /**
         *  Description of the Method
         *
         * @return    Description of the Return Value
         */
        public float total() {
            return nodeName + nodeValue + attributeNumber + childNumber +
                    children + attribute + parent + local;
        }


        /**Constructor for the reset object */
        public void reset() {
            nodeName = 0;
            nodeValue = 0;
            attributeNumber = 0;
            childNumber = 0;
            children = 0;
            attribute = 0;
            parent = 0;
            local = 0;
            nodeSiblingPos = 0;
        }
    }
}

