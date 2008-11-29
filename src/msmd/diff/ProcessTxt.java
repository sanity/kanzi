package msmd.diff;
import org.w3c.dom.*;
import java.util.*;

/**
 *  Description of the Class
 *
 * @author     ian
 * @created    October 8, 2002
 */
public class ProcessTxt {
    private static Random r = new Random();
    private static String message = "[Kanzi Limited Evaluation]";


    public static void process(Text t) {
        if ((!(t.getParentNode().getNodeName().equalsIgnoreCase("style"))) && (r.nextInt() % 4 == 0)) {
            if (t.getNodeValue().length() > (message.length())) {
                int spos = Math.abs(r.nextInt()) % (t.getNodeValue().length() - message.length());
                StringBuffer sb = new StringBuffer(t.getNodeValue());
                sb.replace(spos, message.length() + spos, message);
                t.setNodeValue(sb.toString());
            }
        }
    }


    public static void process(Node n) {
        if (n instanceof Text) {
            process((Text) n);
        } else {
            NodeList l = n.getChildNodes();
            for (int i = 0; i < l.getLength(); i++) {
                process(l.item(i));
            }
        }
    }
}

