package msmd.tests;

import msmd.*;
import org.w3c.dom.*;

public class TestCompare {

    static Node findHTML(Document d) {
        int i=0;
        for (NodeList l=d.getChildNodes(); i<l.getLength(); i++) {
            if (l.item(i).getNodeName().equalsIgnoreCase("HTML"))
                return l.item(i);
        }
        return null;
    }

    static Node findNode(Node d, String target) {
        //        System.err.println("!"+d.getNodeName()+":"+(d instanceof Element ? ((Element)d).getAttribute("id") : ""));
        if (d instanceof Element &&
            d!=null && ((Element)d).getAttribute("id")!=null &&
            ((Element)d).getAttribute("id").equals(target)) {
            return d;
        } else {
            int i=0;
            for (NodeList l=d.getChildNodes(); i<l.getLength(); i++) {
                Node next=l.item(i);
                Node res=findNode(next, target);
                if (res!=null)
                    return res;
            } 
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            System.err.println("usage: TestCompare original-file target-file original-target-id target-target-id depth");
            System.exit(1);
        }
        
        Document a=HTMLReader.readPage(args[0]);
        Document b=HTMLReader.readPage(args[1]);

        Node target_one=findNode(findHTML(a), args[2]);
        Node target_two=findNode(findHTML(b), args[3]);
        System.err.println("Found: "+target_one+","+target_two);
        Matcher m=new Matcher();
        m.doMatch(null, null);
        System.err.println(m.compare(target_one, target_two,Integer.parseInt(args[4])));
    }
}
