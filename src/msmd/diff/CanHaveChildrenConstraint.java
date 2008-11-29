package msmd.diff;
import msmd.*;
import org.w3c.dom.*;

/**
 *  Description of the Class
 *
 * @author     ian
 * @created    October 12, 2002
 */
public class CanHaveChildrenConstraint implements Constraint {
  public CanHaveChildrenConstraint() { }


  public boolean satisfied(Node n) {
    return !((n instanceof Text) ||
        (n instanceof Comment) ||
        (n instanceof Notation) ||
        (n instanceof DocumentType));
  }
}

