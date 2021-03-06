package msmd;

/**
 *  Description of the Class
 *
 * @author     ian
 * @created    September 18, 2002
 */
public class StringDist {

  //****************************
  // Get minimum of three values
  //****************************

  /**
   *  Description of the Method
   *
   * @param  a  Description of the Parameter
   * @param  b  Description of the Parameter
   * @param  c  Description of the Parameter
   * @return    Description of the Return Value
   */
  private static int Minimum(int a, int b, int c) {
    int mi;
    mi = a;
    if (b < mi) {
      mi = b;
    }
    if (c < mi) {
      mi = c;
    }
    return mi;
  }


  //*****************************
  // Compute Levenshtein distance
  //*****************************

  /**
   *  Description of the Method
   *
   * @param  ss  Description of the Parameter
   * @param  tt  Description of the Parameter
   * @return     Description of the Return Value
   */
  public static float dist(String ss, String tt) {
    int lenDiff = -1;
    // Limit string length - this algorithm scales *very* poorly
    if (ss.length() > 100) {
      lenDiff = Math.abs(ss.length() - tt.length());
      ss = ss.substring(0, 100);
    }
    if (tt.length() > 100) {
      lenDiff = Math.abs(ss.length() - tt.length());
      tt = tt.substring(0, 100);
    }

    float ret;
    if ((ss == null) && (tt == null)) {
      ret = 1;
    } else
        if ((ss == null) || (tt == null)) {
      ret = 0;
    } else {
      String s = ss.toLowerCase();
      String t = tt.toLowerCase();

      int d[][];
      // matrix
      int n;
      // length of s
      int m;
      // length of t
      int i;
      // iterates through s
      int j;
      // iterates through t
      char s_i;
      // ith character of s
      char t_j;
      // jth character of t
      int cost;
      // cost
      // Step 1
      n = s.length();
      m = t.length();
      if ((n == 0) && (m == 0)) {
        return 1;
      }
      if (n == 0) {
        return 0;
      }
      if (m == 0) {
        return 0;
      }
      if ((ss.length() > 1000) && (tt.length() > 1000)) {
      }
      d = new int[n + 1][m + 1];
      // Step 2
      for (i = 0; i <= n; i++) {
        d[i][0] = i;
      }
      for (j = 0; j <= m; j++) {
        d[0][j] = j;
      }
      // Step 3
      for (i = 1; i <= n; i++) {
        s_i = s.charAt(i - 1);
        // Step 4
        for (j = 1; j <= m; j++) {
          t_j = t.charAt(j - 1);
          // Step 5
          if (s_i == t_j) {
            cost = 0;
          } else {
            cost = 1;
          }
          // Step 6
          d[i][j] = Minimum(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + cost);
        }
      }
      // Step 7
      int cdist = d[n][m];
      if (cdist > 0) {
        ret = (((float) Math.max(s.length(), t.length())) - cdist)
             / (float) Math.max(s.length(), t.length());
      } else {
        ret = 1;
      }
    }
    if (lenDiff > 0) {
      ret = ((float) (ret * 0.5)) + ((float) (0.5 / (float) lenDiff));
    }
    return ret;
  }
}

