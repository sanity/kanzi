package msmd.diff;
import org.w3c.dom.*;

/**
 *  Description of the Class
 *
 * @author     ian
 * @created    September 29, 2002
 */
public class CopyChildNode implements Difference {
    Node chnode;
    Node copiedNode, refchild;


    /**
     *Constructor for the AddChildNode object
     *
     * @param  chnode      Description of the Parameter
     * @param  copiedNode  Description of the Parameter
     */
    public CopyChildNode(Node chnode, Node copiedNode) {
        this(chnode, copiedNode, null);
    }


    /**
     *Constructor for the CopyChildNode object
     *
     * @param  chnode      Description of the Parameter
     * @param  refchild    Description of the Parameter
     * @param  copiedNode  Description of the Parameter
     */
    public CopyChildNode(Node chnode, Node copiedNode, Node refchild) {
        this.chnode = chnode;
        this.copiedNode = copiedNode;
    }


    /**
     *  Description of the Method
     *
     * @param  m  Description of the Parameter
     * @return    Description of the Return Value
     */
    public Difference remap(Remapper m) {
        Node newch = m.getCounterpart(chnode);
        if (refchild != null) {
            return new CopyChildNode(newch,
                    m.getCounterpart(copiedNode, new CanHaveChildrenConstraint()),
                    m.getCounterpart(refchild, new ChildConstraint(newch)));
        } else {
            return new CopyChildNode(m.getCounterpart(chnode, new CanHaveChildrenConstraint()),
                    m.getCounterpart(copiedNode));
        }
    }


    public void markup() {
        if (refchild != null) {
            Util.setMod(chnode, "Copy child node from " + Util.getId(copiedNode) +
                    " to pos before " + Util.getId(refchild));
        } else {
            Util.setMod(chnode, "Copy child node from " + Util.getId(copiedNode) +
                    " to end");
        }
    }


    /**  Description of the Method */
    public void apply() {
        if (refchild != null) {
            chnode.insertBefore(copiedNode.cloneNode(true), refchild);
        } else {
            chnode.appendChild(copiedNode.cloneNode(true));
        }
    }


    public String toString() {
        if (refchild != null) {
            return "CopyChildNode : " + chnode.getNodeName() + " : " + copiedNode.getNodeName() + " before " + refchild.getNodeName();
        } else {
            return "CopyChildNode : " + chnode.getNodeName() + " : " + copiedNode.getNodeName() + " at end";
        }
    }
}

