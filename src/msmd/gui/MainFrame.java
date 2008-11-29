package msmd.gui;

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import msmd.DraftFile;
import msmd.Version;

/**
 *  Description of the Class
 *
 * @author     ian
 * @created    October 8, 2002
 */
public class MainFrame extends JFrame implements Version {
    protected Wizard wiz;


    MainFrame() {
        super("Kanzi v"+VERSION);
        getContentPane().add(wiz = new Wizard());
        pack();
    }


    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(new com.incors.plaf.kunststoff.KunststoffLookAndFeel());
        MainFrame m = new MainFrame();

        m.addWindowListener(
            new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    DraftFile.revertAll();
                    System.exit(1);
                }
            });

        m.wiz.add(new SourceChooser(m.wiz));
        m.wiz.add(new MultiFileChooser(m.wiz));
        m.wiz.add(new Review(m.wiz));
        m.wiz.refresh();
        m.show();
    }
}


