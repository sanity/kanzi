package msmd;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.lang.reflect.*;

/**
 *  Description of the Class
 *
 * @author     ian
 * @created    October 8, 2002
 */
public class DraftFile {

    static HashSet unclosedDF = new HashSet();

    static {
        try {
            Runtime r=Runtime.getRuntime();
            Method m=r.getClass().getMethod("addShutdownHook", new Class[] {java.lang.Thread.class});
            if (m!=null) {
                m.invoke(r, new Object[] {
                    new Thread() {
                        public void run() {
                            revertAll();
                        }
                    }
                });
            }
        } catch (Exception e) {}
    }

    public static synchronized void revertAll() {
        for (Iterator i = unclosedDF.iterator(); i.hasNext(); ) {
            System.err.println("Forcibly reverting.");
            DraftFile d = (DraftFile) i.next();
            try {
                d.revert();
                d.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected File draftCopy, finalCopy;
    protected long lmd, lmf, lszd, lszf;
    protected boolean closed = false;


    protected static File temp(File base) throws IOException {
        File p = base.getParentFile();
        String n = base.getName();
        File f = File.createTempFile(n, ".mtmp");
        return f;
    }


    protected static void copy(InputStream in, OutputStream out) throws IOException {
        int i = 0;
        byte[] buffer = new byte[65536];
        while ((i = in.read(buffer)) > -1) {
            out.write(buffer, 0, i);
        }
    }


    public static DraftFile draftOf(File f) throws IOException {
        File n = temp(f);
        BufferedOutputStream o = new BufferedOutputStream(new FileOutputStream(n));
        BufferedInputStream i = new BufferedInputStream(new FileInputStream(f));

        copy(i, o);
        o.flush();
        o.close();
        i.close();
        DraftFile df = new DraftFile(n, f);
        return df;
    }


    public static DraftFile draftTo(File f) throws IOException {
        return new DraftFile(temp(f), f);
    }


    DraftFile(File draft, File finalc) {
        draftCopy = draft;
        finalCopy = finalc;
        lmd = draft.lastModified();
        lmf = finalc.lastModified();
        lszd = draft.length();
        lszf = draft.length();
        unclosedDF.add(this);
        closed = true;
    }


    public boolean draftWasModified() {
        return draftCopy.lastModified() != lmd ||
                draftCopy.length() != lszd;
    }


    public boolean finalWasModified() {
        return finalCopy.lastModified() != lmf ||
                finalCopy.length() != lszf;
    }


    public File getDraft() {
        return draftCopy;
    }


    public File getFinal() {
        return finalCopy;
    }


    public OutputStream getDraftOut() throws IOException {
        return new FileOutputStream(draftCopy);
    }


    public InputStream getFinalIn() throws IOException {
        return new FileInputStream(finalCopy);
    }


    public OutputStream getFinalOut() throws IOException {
        return new FileOutputStream(finalCopy);
    }


    public InputStream getDraftIn() throws IOException {
        return new FileInputStream(draftCopy);
    }


    public void commit() throws IOException {
        draftCopy.delete();
        unclosedDF.remove(this);
        closed = true;
    }


    public void revert() throws IOException {
        BufferedOutputStream o = new BufferedOutputStream(getFinalOut());
        BufferedInputStream i = new BufferedInputStream(getDraftIn());
        copy(i, o);
        o.flush();
        o.close();
        i.close();
        commit();
    }


    public void delete() {
        draftCopy.delete();
        unclosedDF.remove(this);
        closed = true;
    }


    protected void finalize() throws Throwable {
        if (!closed) {
            try {
                revert();
            } catch (IOException e) {
                e.printStackTrace();
            }
            unclosedDF.remove(this);
        }
    }


    public static void main(String[] s) throws Exception {
        DraftFile df = DraftFile.draftOf(new File(s[0]));
        ((DraftFile) null).getDraft();
    }
}

