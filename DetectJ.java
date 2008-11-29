import java.awt.*;
import java.applet.*;
import java.net.URL;
import java.awt.event.*;

public class DetectJ extends Applet implements ActionListener {
    
    public void actionPerformed(ActionEvent e) {
        URL url=null;
        try {
            if (new Double(System.getProperty("java.version").substring(0, 3)).doubleValue()>=1.2)
                url=new URL(getDocumentBase(), "/php/download.php?url=/kanzi/kanzi-1.0-installer.exe");
            else url=new URL(getDocumentBase(), "/php/download.php?url=/kanzi/kanzi-1.0-java-installer.exe");
            
        } catch (Exception e2) {
            e2.printStackTrace();
            try {
                url=new URL(getDocumentBase(), "/php/download.php?url=/kanzi/kanzi-1.0-java-installer.exe");
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        }
        getAppletContext().showDocument(url);
    }


    public void init() {
        setBackground(Color.white);
        Button b=new Button("Download Now");
        b.addActionListener(this);
        add(b);
    }
}

            
