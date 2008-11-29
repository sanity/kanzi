package msmd.gui;

import msmd.*;
import msmd.diff.*;
import msmd.sysdep.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import msmd.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.w3c.tidy.*;
import org.w3c.dom.*;

/**
 *  Description of the Class
 *
 * @author     ian
 * @created    October 16, 2002
 */
public class Review extends WizardPage implements ActionListener {
    Tidy t = new Tidy();

    JProgressBar docprogbar = new JProgressBar(),
            diffprogbar = new JProgressBar();
    Wizard w;
    JButton review, undo, finished;

    final static String message1 =
            "<html><p>Once complete, you should use the button below " +
            "to review the changes made to your documents.</p></html>",
            message2 =
            "<html><p>If you are satisfied, you may complete the process " +
            "by clicking Finished.  If not, you <b><i>must</i></b> click " +
            "Undo Changes to restore the original documents.</p></html>";


    public String getTitle() {
        return "Step 4:  Apply and Review the Changes";
    }


    public Review(Wizard w) {
        this.w = w;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel pan = new JPanel();
        pan.setLayout(new BoxLayout(pan, BoxLayout.Y_AXIS));
        pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray), "Progress"));
        pan.add(new JLabel("Total Progress:"));
        pan.add(docprogbar);
        pan.add(new JLabel("Document Progress:"));
        pan.add(diffprogbar);
        add(pan);
        add(Box.createVerticalGlue());

        JLabel m1 = new JLabel(message1);
        JLabel m2 = new JLabel(message2);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(Box.createVerticalGlue());
        pan = new JPanel();
        pan.setLayout(new BoxLayout(pan, BoxLayout.X_AXIS));
        pan.add(Box.createHorizontalStrut(50));
        pan.add(m1);
        pan.add(Box.createHorizontalStrut(50));
        add(pan);
        pan = new JPanel();
        pan.setLayout(new BoxLayout(pan, BoxLayout.X_AXIS));
        pan.add(Box.createHorizontalGlue());
        pan.add(review = new JButton("Review Your Documents"));
        pan.add(Box.createHorizontalStrut(50));
        add(pan);
        add(Box.createVerticalGlue());
        pan = new JPanel();
        pan.add(Box.createHorizontalStrut(50));
        pan.setLayout(new BoxLayout(pan, BoxLayout.X_AXIS));
        pan.add(Box.createHorizontalStrut(50));
        pan.add(m2);
        add(pan);

        pan = new JPanel();
        pan.setLayout(new BoxLayout(pan, BoxLayout.X_AXIS));
        pan.add(Box.createHorizontalGlue());
        pan.add(undo = new JButton("Undo Changes"));
        pan.add(Box.createHorizontalStrut(20));
        pan.add(finished = new JButton("Finished"));
        finished.setEnabled(false);
        pan.add(Box.createHorizontalStrut(50));
        add(pan);
        add(Box.createVerticalGlue());

        undo.setActionCommand("undo");
        finished.setActionCommand("finished");
        review.setActionCommand("review");

        undo.addActionListener(this);
        finished.addActionListener(this);
        review.addActionListener(this);

        review.setEnabled(false);
        undo.setEnabled(false);
        finished.setEnabled(false);
    }


    public void entered() {
        System.err.println("Here we go...");
            new Thread() {
                public void run() {
                    try {
                        go((DraftFile) w.volatileProperties.get("orig-df"),
                                (LinkedList) w.volatileProperties.get("targets"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
    }


    public void go(DraftFile example, LinkedList targets) throws IOException {
        File oldF = example.getDraft();
        File newF = example.getFinal();
        Document a = HTMLReader.readPage(oldF);
        Document b = HTMLReader.readPage(newF);
        DOMDiff dd = new DOMDiff();
        LinkedList differences = dd.getDiff(a, b);
        for (Iterator i = differences.iterator(); i.hasNext(); ) {
            System.out.println("Difference: " + i.next());
        }
        int x = 0;
        for (Iterator i = targets.iterator(); i.hasNext(); ) {
            updateDocProgressBar(targets.size(), x);
            applyDifferences(a, differences, (DraftFile) i.next());
            x++;
        }
        docprogbar.setEnabled(false);
        diffprogbar.setEnabled(false);
        review.setEnabled(true);
        undo.setEnabled(true);
        finished.setEnabled(true);
    }


    protected int docmax = -1, diffmax = -1;


    public void updateDiffProgressBar(int max, int current) {
        System.err.println("Diff[" + current + "/" + max + "]");
        if (this.diffmax == -1) {
            this.diffmax = max;
            diffprogbar.setMaximum(max);
            diffprogbar.setMinimum(0);
        }
        diffprogbar.setValue(current + 1);
    }


    public void updateDocProgressBar(int max, int current) {
        System.err.println("Doc[" + current + "/" + max + "]");
        if (this.docmax == -1) {
            this.docmax = max;
            docprogbar.setMaximum(max);
            docprogbar.setMinimum(0);
        }
        docprogbar.setValue(current + 1);
    }



    public void applyDifferences(Document samp, LinkedList d, DraftFile f)
             throws IOException {
        Document mod = HTMLReader.readPage(f.getFinal());
        Remapper rm = new Remapper(samp, mod);
        LinkedList newDiffs = new LinkedList();
        int progress = 0;
        int progressMax = d.size() * 2 + 1;
        //    System.err.println("Checking for loop before diffs:");
        //    loopCheck(mod, new HashSet());
        for (Iterator i = d.iterator(); i.hasNext(); ) {
            Difference dif = (Difference) i.next();
            System.err.println("Remapping: " + dif);
            updateDiffProgressBar(progressMax, progress);
            Difference nd = dif.remap(rm);
            System.err.println("Remapped: " + nd);
            newDiffs.add(nd);
            progress++;
        }
        for (Iterator i = newDiffs.iterator(); i.hasNext(); ) {
            updateDiffProgressBar(progressMax, progress);
            Difference dif = (Difference) i.next();
            dif.apply();
            //       System.err.println("Checking for loop after: "+dif);
            //       loopCheck(mod, new HashSet());
            progress++;
        }

        // Mangle
        if (!w.registered) {
            ProcessTxt.process(mod);
        }

        OutputStream out = new BufferedOutputStream(f.getFinalOut());
        t.pprint(mod, out);
        updateDiffProgressBar(progressMax, progress++);
        w.refresh();
    }


    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("review")) {
            try {
                LaunchBrowser.launch(((DraftFile) w.volatileProperties.get("orig-df")).getFinal().toURL());
            } catch (MalformedURLException m) {
                m.printStackTrace();
            } catch (IOException ie) {
                ie.printStackTrace();
            }
        } else if (e.getActionCommand().equals("undo")) {
            try {
                ((DraftFile) w.volatileProperties.get("orig-df")).revert();
                LinkedList ls = (LinkedList) w.volatileProperties.get("targets");
                for (Iterator i = ls.iterator(); i.hasNext(); ) {
                    ((DraftFile) i.next()).revert();
                }
                undo.setEnabled(false);
            } catch (IOException ie) {
                ie.printStackTrace();
            }
        } else if (e.getActionCommand().equals("finished")) {
            try {
                undo.setEnabled(false);
                ((DraftFile) w.volatileProperties.get("orig-df")).commit();
                LinkedList ls = (LinkedList) w.volatileProperties.get("targets");
                for (Iterator i = ls.iterator(); i.hasNext(); ) {
                    ((DraftFile) i.next()).commit();
                }
                DraftFile.revertAll();
                System.exit(0);
            } catch (IOException ie) {
                ie.printStackTrace();
            }
        }
    }


    public boolean nextAllowed() {
        return false;
    }


    public boolean lastAllowed() {
        return false;
    }


    public void loopCheck(org.w3c.dom.Node b, Set s) {
        if (s.contains(b)) {
            System.err.println("Barf: Loop detected: " + b.getNodeName());
        } else {
            s.add(b);
            int i = 0;
            for (NodeList l = b.getChildNodes(); i < l.getLength(); i++) {
                org.w3c.dom.Node next = l.item(i);
                loopCheck(next, s);
            }
        }
    }
}

