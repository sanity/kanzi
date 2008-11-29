package msmd.gui;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.File;
import java.io.FileFilter;
import java.util.*;
import msmd.*;
import org.w3c.dom.*;

/**
 *  Description of the Class
 *
 * @author     ian
 * @created    October 14, 2002
 */
public class MultiFileChooser extends WizardPage implements ActionListener,
        ListSelectionListener,
        ChangeListener {

    protected ExampleFileFilter efilter = new ExampleFileFilter();

    protected JList files;
    protected JButton addFile, addDir, remove;
    protected Wizard w;
    protected DefaultListModel lm;
    protected JCheckBox filter;
    protected JSlider slider;
    protected JLabel msim, lsim;


    MultiFileChooser(Wizard w) {
        this.w = w;
        lm = new DefaultListModel();
        files = new JList(lm);
        JScrollPane sp = new JScrollPane(files,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        setLayout(new BorderLayout());
        JPanel mpan = new JPanel();
        mpan.setLayout(new BorderLayout());
        mpan.add(sp, BorderLayout.CENTER);
        JPanel pan = new JPanel();
        pan.setLayout(new BoxLayout(pan, BoxLayout.Y_AXIS));
        pan.setAlignmentX(pan.RIGHT_ALIGNMENT);
        pan.add(addFile = new JButton("Add File(s)"));
        pan.add(addDir = new JButton("Add a Directory"));
        pan.add(remove = new JButton("Remove"));
        pan.add(filter = new JCheckBox("Similarity Filter"));
        pan.add(msim = new JLabel("More Strict"));
        pan.add(slider = new JSlider(JSlider.VERTICAL, 0, 100, 50));
        pan.add(lsim = new JLabel("Less Strict"));
        filter.setSelected(false);
        slider.setEnabled(false);
        slider.setInverted(false);
        mpan.add(pan, BorderLayout.EAST);
        filter.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    slider.setEnabled(filter.isSelected());
                    lsim.setEnabled(filter.isSelected());
                    msim.setEnabled(filter.isSelected());
                    if (filter.isSelected()) {
                        try {
                            filterUpdate(slider.getValue() / 100.0f);
                        } catch (IOException ie) {
                            ie.printStackTrace();
                        }
                    }
                }
            });
        slider.addChangeListener(this);
        addFile.addActionListener(this);
        addDir.addActionListener(this);
        remove.addActionListener(this);
        addFile.setActionCommand("add-file");
        addDir.setActionCommand("add-dir");
        remove.setActionCommand("remove");
        remove.setEnabled(false);
        mpan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Select pages to modify"));

        files.addListSelectionListener(this);
        add(mpan, BorderLayout.CENTER);

        efilter.addExtension("html");
        efilter.addExtension("htm");
        efilter.setDescription("HTML Documents");
    }


    public String getTitle() {
        return "Step 2:  Select Pages for Modification";
    }


    public boolean nextAllowed() {
        return !files.isSelectionEmpty();
    }


    public void stateChanged(ChangeEvent e) {
        int thresh = (int) slider.getValue();
        try {
            filterUpdate(thresh / 100.0f);
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }


    public void valueChanged(ListSelectionEvent e) {
        remove.setEnabled(!files.isSelectionEmpty());
        w.refresh();
    }


    public Dimension getPreferredSize() {
        return new Dimension(300, 150);
    }


    boolean firstEntered = true;


    public void entered() {
        if (firstEntered) {
            firstEntered = false;

            while (w.volatileProperties.get("orig-dom") == null) {
                synchronized (w.volatileProperties) {
                    try {
                        w.volatileProperties.wait(200);
                    } catch (InterruptedException ie) {}
                }
            }
            File example = ((DraftFile) w.volatileProperties.get("orig-df")).getFinal();
            File dir = example.getParentFile();
            addAllHtmls(dir, example);
        }
    }


    void addAllHtmls(File dir, File exclude) {
        File[] fl = dir.listFiles(efilter);
        Vector indices = new Vector();
        for (int i = 0; i < fl.length; i++) {
            if (!fl[i].isDirectory() && (exclude == null ||
                    !fl[i].equals(exclude))) {
                lm.addElement(fl[i]);
                indices.addElement(new Integer(lm.indexOf(fl[i])));
            }
        }
        int[] oidcs = files.getSelectedIndices();
        int[] idcs = new int[indices.size() + oidcs.length];
        System.arraycopy(oidcs, 0, idcs, indices.size(), oidcs.length);
        for (int i = 0; i < idcs.length; i++) {
            idcs[i] = ((Integer) indices.elementAt(i)).intValue();
        }
        files.setSelectedIndices(idcs);
    }


    public void left() {
        LinkedList ll = new LinkedList();
        int[] idcs = files.getSelectedIndices();
        for (int i = 0; i < idcs.length; i++) {
            try {
                ll.add(DraftFile.draftOf((File) lm.getElementAt(idcs[i])));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        w.volatileProperties.put("targets", ll);
    }


    public void actionPerformed(ActionEvent e) {
        while (w.volatileProperties.get("orig-dom") == null) {
            synchronized (w.volatileProperties) {
                try {
                    w.volatileProperties.wait(200);
                } catch (InterruptedException ie) {}
            }
        }
        if (e.getActionCommand().equals("add-file")) {
            JFileChooser chooser = new JFileChooser(((DraftFile) w.volatileProperties.get("orig-df")).getDraft().getParentFile());
            // Note: source for ExampleFileFilter can be found in FileChooserDemo,
            // under the demo/jfc directory in the Java 2 SDK, Standard Edition.
            chooser.setFileFilter(efilter);
            chooser.setMultiSelectionEnabled(true);
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File[] sfiles = chooser.getSelectedFiles();
                Vector v = new Vector();
                for (int i = 0; i < sfiles.length; i++) {
                    lm.addElement(sfiles[i]);
                    v.add(new Integer(lm.indexOf(sfiles[i])));
                }
                int[] idcs = new int[v.size()];
                int x = 0;
                for (Iterator i = v.iterator(); i.hasNext(); ) {
                    idcs[x++] = ((Integer) i.next()).intValue();
                }
                files.setSelectedIndices(idcs);
            }
            if (filter.isSelected()) {
                try {
                    filterUpdate(slider.getValue() / 100.0f);
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
            }
        } else if (e.getActionCommand().equals("add-dir")) {
            JFileChooser chooser = new JFileChooser(((DraftFile) w.volatileProperties.get("orig-df")).getDraft().getParentFile());
            chooser.setMultiSelectionEnabled(true);
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File[] sfiles = chooser.getSelectedFiles();
                for (int i = 0; i < sfiles.length; i++) {
                    addAllHtmls(sfiles[i], null);
                }
            }
        } else if (e.getActionCommand().equals("remove")) {
            int[] ixs = files.getSelectedIndices();
            Vector v = new Vector();
            for (int i = 0; i < ixs.length; i++) {
                pageScores.remove(lm.getElementAt(ixs[i]));
                v.add(lm.getElementAt(ixs[i]));
            }
            for (Iterator i = v.iterator(); i.hasNext(); ) {
                lm.remove(lm.indexOf(i.next()));
            }
            files.clearSelection();
        }
    }


    //Code for automatic filtering
    protected HashMap pageScores = new HashMap();
    protected Matcher m = new Matcher();


    static Node findBODY(Node d) {
        int i = 0;
        for (NodeList l = d.getChildNodes(); i < l.getLength(); i++) {
            if (l.item(i).getNodeName().equalsIgnoreCase("HTML")) {
                return findBODY(l.item(i));
            }
            if (l.item(i).getNodeName().equalsIgnoreCase("BODY")) {
                return l.item(i);
            }
        }
        return null;
    }


    float calcScore(File f, Document d) throws IOException {
        Document t = HTMLReader.readPage(f);
        Node n1 = findBODY(t);
        Node n2 = findBODY(d);
        if (n1 == null) {
            n1 = t;
        }
        if (n2 == null) {
            n2 = d;
        }
        m.parentWeight = 0.0f;
        m.childrenWeight = 0.5f;
        m.doMatch(t, n2);
        return m.compare(n1, n2, 10);
    }


    void filterUpdate(float thresh) throws IOException {
        System.err.println("Updating threshold: " + thresh);
        w.status.setText("Please wait...");
        Vector selection = new Vector();
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;
        for (int i = 0; i < lm.getSize(); i++) {
            Object s = lm.getElementAt(i);
            Float score = (Float) pageScores.get(s);
            if (score == null) {
                System.err.println("Fetching score for " + s + "...");
                while (w.volatileProperties.get("orig-dom") == null) {
                    synchronized (w.volatileProperties) {
                        try {
                            w.volatileProperties.wait(200);
                        } catch (InterruptedException ie) {}
                    }
                }
                pageScores.put(s, score = new Float(calcScore((File) s, (Document) w.volatileProperties.get("orig-dom"))));
                ;
                System.err.println("Score is " + score + ".");

            }
            if (score.floatValue() < min) {
                min = score.floatValue();
            }
            if (score.floatValue() > max) {
                max = score.floatValue();
            }
        }

        thresh = (thresh * ((max - min) + 0.005f)) + min - 0.001f;

        for (int i = 0; i < lm.getSize(); i++) {
            Object s = lm.getElementAt(i);
            Float score = (Float) pageScores.get(s);
            if (score.floatValue() > thresh) {
                selection.add(new Integer(i));
            }
        }
        files.clearSelection();
        int idcs[] = new int[selection.size()];
        for (int i = 0; i < idcs.length; i++) {
            idcs[i] = ((Integer) selection.elementAt(i)).intValue();
        }
        files.setSelectedIndices(idcs);
        w.status.setText("");
    }
}

