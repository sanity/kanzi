package msmd.gui;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

/**
 *  Description of the Class
 *
 * @author     ian
 * @created    October 8, 2002
 */
public abstract class CommitChanges extends WizardPage implements ActionListener, ListSelectionListener, ChangeListener {
  protected JList list;
}

abstract class CellRenderer extends JLabel implements ListCellRenderer {
}

