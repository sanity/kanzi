package msmd;

import org.w3c.tidy.*;
import org.w3c.dom.*;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 *  Description of the Class
 *
 * @author     ian
 * @created    October 8, 2002
 */
public abstract class HTMLReader {
  static WeakHashMap domCache = new WeakHashMap();
  static boolean cache = true;
  static Tidy t = new Tidy();

  static {
      t.setQuiet(true);
  }


  public static Document readPage(InputStream in) throws IOException, DOMException {
    Document d = t.parseDOM(in, null);
    d.normalize();
    return d;
  }


  public static Document readPage(URL u) throws IOException, DOMException {
      InputStream in=new BufferedInputStream(u.openStream());
      Document d=readPage(in);
      in.close();
      return d;
  }


  public static Document readPage(String filename) throws IOException, DOMException {
    return readPage(new File(filename));
  }


  public static Document readPage(File f) throws IOException, DOMException {
    Object[] dc = (Object[]) domCache.get(f);
    if (cache && (dc != null)) {
      if (((Long) dc[0]).longValue() == f.lastModified()) {
        return (Document) dc[1];
      }
    }

    InputStream is=new BufferedInputStream(new FileInputStream(f));
    Document d = readPage(is);
    is.close();
    if (cache) {
      dc = new Object[2];
      dc[0] = new Long(f.lastModified());
      dc[1] = d;
      domCache.put(f, dc);
    }
    return d;
  }


  public static void main(String[] args) throws Exception {
    System.err.println(readPage(args[0]));
  }
}

