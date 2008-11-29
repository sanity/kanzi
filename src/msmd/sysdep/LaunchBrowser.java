package msmd.sysdep;
import java.net.*;
import java.io.*;

/**
 *  Description of the Class
 *
 * @author     ian
 * @created    October 8, 2002
 */
public class LaunchBrowser {

  /**
   * Display a URL in the operating system's browser
   *
   * @param  url              The url to display
   * return True if a browser was found, false if not (ie. unsupported OS)
   * @return                  Description of the Return Value
   * @exception  IOException  Description of the Exception
   */
  public static boolean launch(URL url)
       throws IOException {
    // FIXME: Mac support?
    String os = System.getProperties().getProperty("os.name").toLowerCase();
    if (os.startsWith("windows")) {
      Runtime.getRuntime().exec("start " + url.toExternalForm());
      return true;
    } else if (os.startsWith("linux")) {
      // FIXME: Assumes they are running mozilla
      Runtime.getRuntime().exec("mozilla " + url);
      return true;
    }

    return false;
  }
}

