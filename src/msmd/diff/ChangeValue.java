package msmd.diff;
import org.w3c.dom.*;

/**
 *  Change the value of a node
 *
 * @author     ian
 * @created    September 25, 2002
 */
public class ChangeValue implements Difference {
    Node chnode;
    public String newVal, replS, replD;


    /**
     *Constructor for the ChangeValue object
     *
     * @param  newVal  Description of the Parameter
     * @param  chnode  Description of the Parameter
     * @param  replS   Description of the Parameter
     * @param  replD   Description of the Parameter
     */

    public ChangeValue(Node chnode, String newVal, String replS, String replD) {
        this.newVal = newVal;
        this.chnode = chnode;
        this.replS = replS;
        this.replD = replD;
    }


    public ChangeValue(Node chnode, String newVal) {
        this.chnode = chnode;
        this.newVal = newVal;
        String val = chnode.getNodeValue();
        int s;
        int e;
        try {
            for (s = 0; val.charAt(s) == newVal.charAt(s); s++) {
            }
            for (e = 1; val.charAt(val.length() - e) == newVal.charAt(newVal.length() - e); e++) {
            }
            replS = val.substring(s, 1 + val.length() - e);
            replD = newVal.substring(s, 1 + newVal.length() - e);
        } catch (StringIndexOutOfBoundsException ex) {}
    }


    /**
     *  Description of the Method
     *
     * @param  m  Description of the Parameter
     * @return    Description of the Return Value
     */
    public Difference remap(Remapper m) {
        return new ChangeValue(m.getCounterpart(chnode), newVal, replS, replD);
    }


    public void markup() {
        if (chnode instanceof Element) {
            Util.setMod(chnode, "Change value to " + newVal);
        } else {
            chnode.setNodeValue("Changed to: " + newVal);
        }
    }


    /**  Description of the Method */
    public void apply() {
        String val = chnode.getNodeValue();
        int i = replS==null || replD==null ? -1 : val.indexOf(replS);
        if ((i != -1) && (i == (val.lastIndexOf(replS))) && (replD != null)) {
            StringBuffer newV = new StringBuffer(val.substring(0, i));
            newV.append(replD);
            newV.append(val.substring(i + replS.length(), val.length()));
            chnode.setNodeValue(newV.toString());
        } else {
            chnode.setNodeValue(newVal);
        }
    }


    public String toString() {
        return "Change Value " + newVal +","+replS+","+replD +" "+chnode.getNodeName()+"{"+chnode.getNodeValue()+"} to " + newVal;
    }
}

