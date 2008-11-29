package msmd.diff;
import msmd.*;
import org.w3c.dom.*;

/**
 *  Description of the Class
 *
 * @author     ian
 * @created    October 12, 2002
 */
public class IsAnElementConstraint implements Constraint {
  public boolean satisfied(Node n) {
    return (n instanceof Element);
  }
}

