package msmd.gui;

import org.w3c.dom.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import msmd.*;

/**
 *  Description of the Class
 *
 * @author     ian
 * @created    October 10, 2002
 */
public class SourceChooser extends WizardPage implements ActionListener {
    protected JTextField fname;
    protected JButton browse;
    protected Wizard w;
    protected JPanel editPanel;
    protected JLabel editLabel;
    protected JButton launchEditor;
    protected String[] pageInfoS = {"Page Title", "Page Size", "HTML Nodes"};
    protected JLabel[] pageInfoL = new JLabel[pageInfoS.length];


    SourceChooser(Wizard w) {
        this.w = w;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        browse = new JButton("Browse...");
        fname = new JTextField(40);
        add(Box.createVerticalGlue());
        JPanel pan = new JPanel();
        pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray), "Select Example Page"));
        pan.add(fname);
        pan.add(browse);
        add(pan);

        add(Box.createVerticalGlue());
        /*
         *  JPanel infoPanel = new JPanel();
         *  infoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray), "Page Information"));
         *  infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
         *  infoPanel.add(Box.createHorizontalGlue());
         *  JPanel pa1 = new JPanel();
         *  JPanel pa2 = new JPanel();
         *  pa1.setLayout(new BoxLayout(pa1, BoxLayout.Y_AXIS));
         *  pa2.setLayout(new BoxLayout(pa2, BoxLayout.Y_AXIS));
         *  for (int x = 0; x < pageInfoS.length; x++) {
         *  pa1.add(new JLabel(pageInfoS[x]));
         *  JLabel nl = new JLabel("<html><i>no page selected</i></html>");
         *  pa2.add(nl);
         *  pageInfoL[x] = nl;
         *  }
         *  infoPanel.add(pa1);
         *  infoPanel.add(Box.createHorizontalStrut(10));
         *  infoPanel.add(pa2);
         *  infoPanel.add(Box.createHorizontalGlue());
         */
        editPanel = new JPanel();
        editPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray), "Status"));
        editLabel = new JLabel("<html><p>Please select the file you wish to modify as an example to Kanzi</p></html>");
        editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.X_AXIS));
        editPanel.add(editLabel);
//        add(infoPanel);
//        add(Box.createVerticalGlue());
        add(editPanel);
        add(Box.createVerticalGlue());
        browse.addActionListener(this);
        browse.setActionCommand("original");
    }


    public String getTitle() {
        return "Step 1:  Select and Modify an Example Page";
    }


    boolean forceNext = false;


    public boolean nextAllowed() {
        return forceNext || ((w.volatileProperties.get("orig-dom") != null) &&
                ((DraftFile) w.volatileProperties.get("orig-df")).finalWasModified());
    }


    public void actionPerformed(ActionEvent e) {
        JFileChooser chooserNF;
        try {
            chooserNF = new JFileChooser(new File((String) w.properties.getProperty("targetFile")));
        } catch (Exception ex) {
            chooserNF = new JFileChooser();
        }
        final JFileChooser chooser = chooserNF;
        // Note: source for ExampleFileFilter can be found in FileChooserDemo,
        // under the demo/jfc directory in the Java 2 SDK, Standard Edition.
        ExampleFileFilter filter = new ExampleFileFilter();
        filter.addExtension("html");
        filter.addExtension("htm");
        filter.setDescription("HTML Documents");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                fname.setText(chooser.getSelectedFile().getCanonicalPath());
                w.refresh();

                    //Load the DOM in the background
                    new Thread() {
                        public void run() {
                            try {
                                System.err.println("Loading: " + chooser.getSelectedFile());

                                w.volatileProperties.put("orig-dom", HTMLReader.readPage(chooser.getSelectedFile()));
                                w.volatileProperties.put("orig-df", DraftFile.draftOf(chooser.getSelectedFile()));
                                w.properties.setProperty("targetFile", chooser.getSelectedFile().getCanonicalPath());
                                synchronized (w.volatileProperties) {
                                    w.volatileProperties.notifyAll();
                                }

                                // Enable editing stuff
                                editLabel.setText("<html><p>You may now modify the example page you have selected.  " +
                                        "you will not be able to proceed until Kanzi detects that the file has been modified</p></html>");

                                /*
                                 *  // Get information
                                 *  pageInfoL[0].setText(findTITLE((Node) w.volatileProperties.get("orig-dom")));
                                 *  pageInfoL[1].setText("" + ((DraftFile) w.volatileProperties.get("orig-df")).getDraft().length() + " bytes");
                                 *  TreeIndex ti = new TreeIndex((Node) w.volatileProperties.get("orig-dom"));
                                 *  pageInfoL[2].setText("" + ti.getChildCount((Node) w.volatileProperties.get("orig-dom")));
                                 */
                                // Start a timer to monitor for changes to draft file
                                class CheckDFMod extends TimerTask {
                                    java.util.Timer t;
                                    long start = System.currentTimeMillis();


                                    public CheckDFMod(java.util.Timer t) {
                                        this.t = t;
                                    }


                                    public void run() {
                                        if (((DraftFile) w.volatileProperties.get("orig-df")).finalWasModified()) {
                                            w.refresh();
                                            t.cancel();
                                            editLabel.setText("<html><p>You may now proceed to the next step</p></html>");
                                        }
                                        /*
                                         *  else {
                                         *  String os = System.getProperties().getProperty("os.name").toLowerCase();
                                         *  (System.currentTimeMillis() - start) > 5000) {
                                         *  forceNext = true;
                                         *  w.refresh();
                                         *  t.cancel();
                                         *  }
                                         *  }
                                         */
                                    }
                                }
                                java.util.Timer t = new java.util.Timer();
                                t.schedule(new CheckDFMod(t), 1000, 1000);
                            } catch (IOException ie) {
                                ie.printStackTrace();
                            }
                        }
                    }.start();
            } catch (IOException ie) {
                ie.printStackTrace();
            }
        }
    }


    static String findTITLE(Node d) {
        int i = 0;
        for (NodeList l = d.getChildNodes(); i < l.getLength(); i++) {
            if (l.item(i).getNodeName().equalsIgnoreCase("HTML")) {
                return findTITLE(l.item(i));
            }
            if (l.item(i).getNodeName().equalsIgnoreCase("HEAD")) {
                return findTITLE(l.item(i));
            }
            if (l.item(i).getNodeName().equalsIgnoreCase("TITLE")) {
                return findTITLE(l.item(i));
            }
            if ((l.item(i).getNodeValue() != null) && (l.item(i).getNodeValue().length() > 5)) {
                return l.item(i).getNodeValue();
            }
        }
        return "<Unknown>";
    }
}

