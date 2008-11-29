package msmd.gui;

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;
import msmd.Key;
import msmd.Version;
import msmd.sysdep.*;

/**
 *  Description of the Class
 *
 * @author     ian
 * @created    October 14, 2002
 */
public class Wizard extends JPanel implements ActionListener, Version {

    Map volatileProperties = new HashMap();
    Properties properties;
    LinkedList elements = new LinkedList();
    WizardPage currentPane;
    JButton next, last, register, buy;
    JLabel title, status, evalVersion;
    File propFile;
    boolean registered=false;

  public Wizard() {
    // Add shutdown hook
      try {
          Runtime r=Runtime.getRuntime();
          Method m=r.getClass().getMethod("addShutdownHook", new Class[] {java.lang.Thread.class});
          if (m!=null) {
              m.invoke(r, new Object[] {
                  new Thread() {
                      public void run() {
                          try {
                              properties.store(new FileOutputStream(propFile), "Kanzi 1.0 Properties");
                          } catch (Exception ae) {
                              ae.printStackTrace();
                              System.out.println("Error saving properties");
                          }
                      }
                  }
              });
          }
      } catch (Exception e) {}

    setLayout(new BorderLayout());
    next = new JButton("Next");
    last = new JButton("Previous");
    JPanel pan = new JPanel();
    pan.setLayout(new BoxLayout(pan, BoxLayout.X_AXIS));
    pan.add(status = new JLabel(""));
    
    // Load in saved volatileProperties
    String os = System.getProperties().getProperty("os.name").toLowerCase();
    if (os.startsWith("windows")) {
      propFile = new File(System.getProperty("user.home"), "kanzi.ini");
    } else {
      propFile = new File(System.getProperty("user.home"), ".kanzi");
    }
    try {
        properties=new Properties();
        properties.load(new FileInputStream(propFile));
        String key=properties.getProperty("licenseKey");
        try {
            Key k=Key.decode(key);
            registered=k.getValue("product").equals("Kanzi") &&
                k.getValue("version").equals(MAJOR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    } catch (FileNotFoundException e) {} catch (IOException e) {
      System.out.println("Error loading properties file");
      e.printStackTrace();
    }

    if (!registered) {
        register=new JButton("Register Now");
        register.setActionCommand("register");
        register.addActionListener(this);

        pan.add(register);
        JButton kanzihp=new JButton("Buy Online");
        kanzihp.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        LaunchBrowser.launch(new URL("http://cematics.com/site.php/kanzi"));
                    } catch (MalformedURLException m) {
                        m.printStackTrace();
                    } catch (IOException ie) {
                        ie.printStackTrace();
                    }                      
                }
            });
        pan.add(kanzihp);
        pan.add(Box.createHorizontalGlue());
        pan.add(evalVersion=new JLabel("Evaluation Version!"));
    }                
    pan.add(Box.createHorizontalGlue());
    pan.add(last);
    pan.add(next);
    add(pan, BorderLayout.SOUTH, 0);
    add(title = new JLabel(), BorderLayout.NORTH);
    title.setFont(new Font("SansSerif", Font.ITALIC, 20));
    next.addActionListener(this);
    last.addActionListener(this);
    next.setActionCommand("next");
    last.setActionCommand("last");
  }


  public Dimension getPreferredSize() {
    return new Dimension(600, 300);
  }


  public void add(WizardPage p) {
    if (currentPane == null) {
      setActive(p);
    }

    elements.addLast(p);
  }


  void setActive(WizardPage p) {
    System.err.println(p);
    if (currentPane != null) {
      currentPane.left();
      currentPane = null;
      remove(1);
    }
    add(p, BorderLayout.CENTER, 1);
    title.setText(p.getTitle());
    currentPane = p;
    refresh();
    currentPane.entered();
  }


  void refresh() {
    int i = elements.indexOf(currentPane);
    next.setEnabled((i + 1) < elements.size() && currentPane.nextAllowed());
    last.setEnabled(i > 0 && currentPane.lastAllowed());
    revalidate();
    repaint();
  }


  public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equals("next")) {
          next();
      } else if (e.getActionCommand().equals("last")) {
          last();
      } else if (e.getActionCommand().equals("register")) {
          final JFrame jf=new JFrame("Enter License Key");
          JPanel pan=new JPanel();
          JPanel pan2=new JPanel();
          pan.setLayout(new BoxLayout(pan, BoxLayout.Y_AXIS));
          pan2.setLayout(new BoxLayout(pan2, BoxLayout.X_AXIS));
          pan2.add(new JLabel("<html><body><p>If you have already purchased Kanzi, copy the License Key,<br> including the \"----- BEGIN/END -----\" lines below:</p><body></html>"));
          pan2.add(Box.createHorizontalGlue());
          final JTextArea jt=new JTextArea(20, 75);
          jt.setFont(new Font("Monospaced", Font.PLAIN, 12));
          pan.add(pan2);
          pan.add(Box.createVerticalStrut(10));
          pan.add(jt);
          pan2=new JPanel();
          pan2.setLayout(new BoxLayout(pan2, BoxLayout.X_AXIS));
          pan2.add(Box.createHorizontalGlue());
          JButton ok=new JButton("Ok");
          ok.addActionListener(new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                      jf.dispose();
                      String key=jt.getText();
                      properties.setProperty("licenseKey", key);
                      try {
                          Key k=Key.decode(key);
                          registered=true;
                          if (register!=null)
                              register.setEnabled(false);
                          if (evalVersion!=null)
                              evalVersion.setText("");
                      } catch (Exception ae) { 
                          ae.printStackTrace();
                      }
                  }});
          pan2.add(ok);
          pan.add(pan2);
          
          jf.getContentPane().add(pan);
          jf.pack();
          jf.show();
      }
  }


  void next() {
    int i = elements.indexOf(currentPane);
    setActive((WizardPage) elements.get(i + 1));
  }


  void last() {
    int i = elements.indexOf(currentPane);
    setActive((WizardPage) elements.get(i - 1));
  }
}

