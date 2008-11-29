package msmd.gui;

import javax.swing.JPanel;

public abstract class WizardPage extends JPanel {

    public abstract boolean nextAllowed();
    public boolean lastAllowed() {
        return true;
    }

    public void  reset() {}
    public void entered() {}
    public void left() {}
    
    public abstract String getTitle();
}
