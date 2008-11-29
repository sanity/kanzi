import java.lang.reflect.*;
import org.w3c.tidy.*;

public class Test {

    public static void main(String[] args) throws Exception {
        Tidy d=new Tidy();
        Class c=d.getClass();
        
        Method[] m=c.getMethods();
        for (int i=0; i<m.length; i++) {
            if (m[i].getName().startsWith("get"))
                System.err.println(m[i].getName()+"\t"+
                                   m[i].invoke(d, new Object[0]));
        }
    }
}
