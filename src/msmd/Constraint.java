package msmd;

import org.w3c.dom.*;

/**
 * A constraint is a boolean producing operator applied to 
 * a Node
 */
public interface Constraint {

    /**
     * Returns true if the given node satisfies this constraint
     */
    public boolean satisfied(Node n);

}
