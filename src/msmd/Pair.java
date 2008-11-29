package msmd;

import org.w3c.dom.Node;

public class Pair {
    public boolean exact;
    public Node a;
    public Node b;

    public Pair(Node a, Node b) {
        this.a=a;
        this.b=b;
    }

    public Pair(Node a, Node b, boolean exact) {
      this.a = a;
      this.b = b;
      this.exact = exact;
    }


    public Pair(Node a) {
      this.a = a;
      this.b = null;
    }

    public String toString() {
      if (b != null) {
        if (exact) {
          return a.getNodeName() + " exact " + b.getNodeName();
        } else {
          return a.getNodeName() + " approx " + b.getNodeName();
        }
      } else {
        return a.getNodeName() + " added";
      }
    }
}
