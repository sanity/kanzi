package msmd.diff;
import msmd.*;
import java.util.*;
import org.w3c.dom.*;

/**
 *  Description of the Class
 *
 * @author     ian
 * @created    September 25, 2002
 */
public class DOMDiff {
    TreeIndex ta;
    TreeIndex tb;


    /**
     *  Gets the diff attribute of the DOMDiff object
     *
     * @param  a  Description of the Parameter
     * @param  b  Description of the Parameter
     * @return    The diff value
     */
    public synchronized LinkedList getDiff(Document a, Document b) {
        LinkedList differences = new LinkedList();
        ta = new TreeIndex(a);
        tb = new TreeIndex(b);

        return getDifference(a, b);
    }


    /**
     *  Gets the difference attribute of the DOMDiff object
     *
     * @param  a  Description of the Parameter
     * @param  b  Description of the Parameter
     * @return    The difference value
     */
    protected LinkedList getDifference(Node a, Node b) {
        LinkedList ret = new LinkedList();
        if (ta.getIndexVal(a) == tb.getIndexVal(b)) {
            return ret;
        }

        // Ok, the hashes don't match - figure out why and return one or more
        // difference objects which transform a into b

        /// Check to see if the node's value has changed
        if (!(a.getNodeValue().equals(b.getNodeValue()))) {
            ret.add(new ChangeValue(a, b.getNodeValue()));
        }

        /// Ok, lets look at the attributes
        // FIXME: Here we should really cast the Nodes to be Elements and use their
        //        useful methods for accessing the attributes
        NamedNodeMap nnma = a.getAttributes();
        NamedNodeMap nnmb = b.getAttributes();
        //// Check for element addition and change
        for (int x = 0; x < nnmb.getLength(); x++) {
            Node elb = nnmb.item(x);
            Node ela = nnma.getNamedItem(elb.getNodeName());
            if ((ela == null) || (!(ela.getNodeValue().equals(elb.getNodeValue())))) {
                // This element has been added or changed
                ret.add(new SetAttribute((Element) a, elb.getNodeName(), elb.getNodeValue()));
            }
        }
        //// Check for element deletion
        for (int x = 0; x < nnma.getLength(); x++) {
            Node ela = nnma.item(x);
            if (nnmb.getNamedItem(ela.getNodeName()) == null) {
                ret.add(new DeleteElement(a, ela.getNodeName()));
            }
        }

        /// Now (lastly) lets look at the children (oh please, think of
        /// the children!)
        //// First quickly see if any of the children are actually different
        NodeList aNL = a.getChildNodes();
        NodeList bNL = b.getChildNodes();
        boolean same = false;
        out :
        if (aNL.getLength() == bNL.getLength()) {
            same = true;
            for (int x = 0; x < aNL.getLength(); x++) {
                if (ta.getIndexVal(aNL.item(x)) != tb.getIndexVal(bNL.item(x))) {
                    same = false;
                    break out;
                }
            }
        }
        if (same) {
            // They are the same - terminate
            return ret;
        }
        //// Ok, one or more of the kids are different - time to take a closer look
        ///// Firstly determine any rearrangement of the children
        LinkedList matchedPairs = pairChildren(a, b);

        Hashtable rear = new Hashtable();
        for (ListIterator x = matchedPairs.listIterator(); x.hasNext(); ) {
            Pair p = (Pair) x.next();
            if (p.b != null) {
                rear.put(p.b, p.a);
                x.remove();
            }
        }
        // Now create the difference
        RearrangeChildren rc = new RearrangeChildren(a);
        for (int x = 0; x < b.getChildNodes().getLength(); x++) {
            Node aa = (Node) rear.get(b.getChildNodes().item(x));
            if (aa != null) {
                rc.setNextChild(aa);
                ret.addAll(getDifference(aa, b.getChildNodes().item(x)));
            }
        }
        // But has anything actually been changed?  Don't append the difference if it
        // hasn't
        if (rc.doAnything()) {
            ret.add(rc);
        }

        // The remaining nodes in matchedPairs are to be added
        for (ListIterator x = matchedPairs.listIterator(); x.hasNext(); ) {
            // n in the new node
            Node n = ((Pair) x.next()).a;
            // Is the child added or copied?
            boolean childadded = true;
            //   if (tb.getChildCount(n) > 2) {
            Node copiedNode = ta.getNode(tb.getIndexVal(n));
            if (copiedNode != null) {
                childadded = false;
                if (n.getNextSibling() != null) {
                    ret.add(new CopyChildNode(a, copiedNode, (Node) rear.get(n.getNextSibling())));
                } else {
                    ret.add(new CopyChildNode(a, copiedNode));
                }
            }
            //   }
            if (childadded) {
                if (n.getNextSibling() != null) {
                    ret.add(new AddChildNode(a, n, (Node) rear.get(n.getNextSibling())));
                } else {
                    ret.add(new AddChildNode(a, n));
                }
            }
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
    protected LinkedList pairChildren(Node a, Node b) {
        LinkedList ret = new LinkedList();
        // Extract the children into linked lists and Hashtables
        LinkedList all = new LinkedList();
        // FIXME - what if two children of one of the nodes have the same index?!
        Hashtable aht = new Hashtable();
        LinkedList bll = new LinkedList();
        NodeList nla = a.getChildNodes();
        NodeList nlb = b.getChildNodes();
        for (int x = 0; x < nla.getLength(); x++) {
            all.add(nla.item(x));
        }
        for (int x = 0; x < nlb.getLength(); x++) {
            bll.add(nlb.item(x));
        }

        // Get exact matches
        // Ugly and inefficient - worst case O(N*M),
        for (ListIterator x = all.listIterator(); x.hasNext(); ) {
            Node xn = (Node) x.next();
            for (ListIterator y = bll.listIterator(); y.hasNext(); ) {
                Node yn = (Node) y.next();
                if (ta.getIndexVal(xn) == tb.getIndexVal(yn)) {
                    x.remove();
                    y.remove();
                    ret.add(new Pair(xn, yn, true));
                    break;
                }
            }
        }

        // Now, for each node in list a, find the closest matching node
        // in list b, and if it is close enough - assume that it is a match

        /*
         *  This algorithm will fail in the event that two children with the same
         *  nodeName are modified and then rearranged.
         */
        for (ListIterator x = all.listIterator(); x.hasNext(); ) {
            Node xn = (Node) x.next();
            for (ListIterator y = bll.listIterator(); y.hasNext(); ) {
                Node yn = (Node) y.next();
                if ((xn.getNodeName().equals(yn.getNodeName())) &&
                // Only if there isn't an exact match for yn somewhere
                // else in the document, we will want to copy that
                // instead
                        (ta.getNode(tb.getIndexVal(yn)) == null)) {
                    ret.add(new Pair(xn, yn, false));
                    x.remove();
                    y.remove();
                    break;
//          }
                }
            }
        }

        // Whats left in bll was added
        for (ListIterator x = bll.listIterator(); x.hasNext(); ) {
            ret.add(new Pair((Node) x.next()));
        }
        return ret;
    }
}

