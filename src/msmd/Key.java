package msmd;

import java.security.*;
import java.security.spec.*;
import java.util.Map;
import java.io.*;
import java.util.zip.InflaterInputStream;

public class Key {
    private static final byte[] kb=
     new byte[] {
         (byte)0x30,(byte)0x81,(byte)0x9f,(byte)0x30,(byte)0xd,(byte)0x6,
         (byte)0x9,
         (byte)0x2a,(byte)0x86,(byte)0x48,(byte)0x86,(byte)0xf7,(byte)0xd,
         (byte)0x1,(byte)0x1,(byte)0x1,(byte)0x5,(byte)0x0,(byte)0x3,
         (byte)0x81,(byte)0x8d,(byte)0x0,(byte)0x30,(byte)0x81,(byte)0x89,
         (byte)0x2,(byte)0x81,(byte)0x81,(byte)0x0,(byte)0xbb,(byte)0x56,
         (byte)0x54,(byte)0x4f,(byte)0xc0,(byte)0x98,(byte)0x57,(byte)0x54,
         (byte)0x54,(byte)0x8b,(byte)0x15,(byte)0x14,(byte)0x87,(byte)0xbb,
         (byte)0xb9,(byte)0xa5,(byte)0x2c,(byte)0xa7,(byte)0xae,(byte)0xfd,
         (byte)0x65,(byte)0xac,(byte)0xd9,(byte)0xdc,(byte)0x2d,(byte)0x3f,
         (byte)0x1e,(byte)0x28,(byte)0xd9,(byte)0xa,(byte)0xc7,(byte)0xa8,
         (byte)0x6f,(byte)0x18,(byte)0x29,(byte)0xe3,(byte)0xa4,(byte)0xb5,
         (byte)0xe6,(byte)0x11,(byte)0x9a,(byte)0xbe,(byte)0xb5,(byte)0x1a,
         (byte)0xb9,(byte)0xab,(byte)0x20,(byte)0x4a,(byte)0x51,(byte)0xf8,
         (byte)0xbb,(byte)0x43,(byte)0x53,(byte)0x21,(byte)0xe0,(byte)0x8a,
         (byte)0x8b,(byte)0xc3,(byte)0x49,(byte)0xa5,(byte)0xb6,(byte)0xfc,
         (byte)0xa2,(byte)0x5f,(byte)0x86,(byte)0x8c,(byte)0x23,(byte)0x4c,
         (byte)0x3c,(byte)0x72,(byte)0x39,(byte)0xe3,(byte)0xfd,(byte)0xb4,
         (byte)0x7a,(byte)0x69,(byte)0xd7,(byte)0x10,(byte)0x7d,(byte)0x3e,
         (byte)0xfc,(byte)0xd0,(byte)0xb0,(byte)0x41,(byte)0x6e,(byte)0x31,
         (byte)0xfa,(byte)0xd0,(byte)0x9d,(byte)0x3e,(byte)0x99,(byte)0xb2,
         (byte)0x4a,(byte)0xb7,(byte)0xfe,(byte)0x1a,(byte)0x5a,(byte)0xd1,
         (byte)0x27,(byte)0x5c,(byte)0xe4,(byte)0x80,(byte)0xd8,(byte)0xcd,
         (byte)0x11,(byte)0x12,(byte)0x81,(byte)0x9d,(byte)0xa4,(byte)0x44,
         (byte)0x32,(byte)0xfc,(byte)0x14,(byte)0x1e,(byte)0x4,(byte)0xfa,
         (byte)0x29,(byte)0xc6,(byte)0x2a,(byte)0x34,(byte)0x81,(byte)0xf4,
         (byte)0xad,(byte)0x2e,(byte)0xdd,(byte)0x0,(byte)0x7b,(byte)0x7,
         (byte)0x2,(byte)0x3,(byte)0x1,(byte)0x0,(byte)0x1         
     };

    private Map data;
    private static final String HEADER=
        "----- BEGIN Cematics License Key -----",
        FOOTER=
        "----- END License Key -----";

    public static byte[] hexToBytes(String s) throws NumberFormatException {
        if ((s.length() % 2) != 0) {
            s="0"+s;
        }
        
        byte[] out = new byte[s.length() / 2];
        byte b;
        for (int i=0; i < s.length(); i++) {
            char c = Character.toLowerCase(s.charAt(i));
            if (!((c >= 'a' && c <= 'f') || (c >= '0' && c <='9')))
                throw new NumberFormatException();
            b = (byte) (c >= 'a' && c <='f' ? c - 'a' + 10 : c - '0');
            if (i%2 == 0) {
                out[i/2] = (byte) (b << 4);
            } else {
                out[(i-1)/2] = (byte) (out[(i-1)/2] | b);
            }
        }
        return out;
    }

    public static Key decode(String key) throws AccessControlException, IOException {
        return decode(new ByteArrayInputStream(key.getBytes("UTF8")));
    }

    static Key decode(InputStream in) throws AccessControlException, IOException {
        BufferedReader rd=new BufferedReader(new InputStreamReader(in));
        String line=null;
        do {
            line=rd.readLine();
        } while (line!=null && !line.equals(HEADER));
        if (line==null) 
            throw new AccessControlException("Invalid key");

        StringBuffer sb=new StringBuffer();
        line=rd.readLine();
        while (line!=null && !line.trim().equals(FOOTER)) {
            sb.append(line.trim().toLowerCase());
            line=rd.readLine();
        }
        String data=sb.toString();
        byte[] b=hexToBytes(data);
        return load(new InflaterInputStream(new ByteArrayInputStream(b)));
    }

    static Key load(InputStream in) throws AccessControlException, IOException {
        try {
            PublicKey ck=KeyFactory.getInstance("rsa").generatePublic(new X509EncodedKeySpec(kb));
            ObjectInputStream oin=new ObjectInputStream(in);
            SignedObject so=(SignedObject)oin.readObject();
            Signature algo=Signature.getInstance("SHA1withRSA");
            
            if (so.verify(ck, algo)) {
                Map data=(Map)so.getObject();
                return new Key(data);
            } else throw new AccessControlException("Invalid certificate");
        } catch (NoSuchAlgorithmException nse) {
            throw new AccessControlException("Cannot verify certificate");
        } catch (InvalidKeySpecException kse) {
            throw new AccessControlException("Invalid certificate");
        } catch (Exception e) {
            e.printStackTrace();
            throw new AccessControlException("Cannot read certificate");
        }
    }

    private Key(Map m) {
        data=m;
    }

    public String getValue(String key) {
        return (String)data.get(key);
    }


    public static void main(String[] args) throws Exception {
        Key k=Key.decode(new FileInputStream(args[0]));
        System.err.println(k);
        System.err.println(k.getValue("name"));
        System.err.println(k.getValue("email"));
    }
}
