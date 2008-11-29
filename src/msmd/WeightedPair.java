package msmd;

import org.w3c.dom.Node;

public class WeightedPair {
    public float weight;
    public Node a;
    public Node b;

    public WeightedPair(Node a, Node b, float weight) {
        this.a=a;
        this.b=b;
        this.weight=weight;
    }

    public String toString() {
        return (a.getNodeName() + "," + b.getNodeName()+","+weight+")");
    }
}
