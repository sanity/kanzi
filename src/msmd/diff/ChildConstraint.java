package msmd.diff;
import org.w3c.dom.*;
import msmd.*;

class ChildConstraint implements Constraint {

  Node p;


  public ChildConstraint(Node p) {
    this.p = p;
  }


  public boolean satisfied(Node n) {
    return (n.getParentNode() == p);
  }
}

