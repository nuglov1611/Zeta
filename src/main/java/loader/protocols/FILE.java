/*
 * File: FILE.java
 * 
 * Created: Mon Mar 22 08:50:57 1999
 * 
 * Copyright(c) by Alexey Chen
 */

package loader.protocols;

import loader.Protocol;
import loader.ZetaProperties;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FILE implements Protocol {
    private static final Logger log = Logger.getLogger(FILE.class);

    String root = "/";

    public FILE(String link) {
        root = link;
    }

    public byte[] getByName_bytes(String path) throws Exception {
        try {
            if (ZetaProperties.protocol_debug > 0)
                log.debug("FILE " + root + path);
            File f = new File(root + path);
            FileInputStream fs = new FileInputStream(f);
            long flen = f.length();
            byte[] text = new byte[(int) flen];
            if (fs.read(text) != text.length)
                throw new Exception("NotCorrectly readed file \n\t" + root
                        + path);
            fs.close();
            return text;
        } catch (Exception e) {
            if (ZetaProperties.loader_exception) {
                log.error("~loader.FILE::getByName Exception", e);
            }
            throw new Exception("~loader.FILE::getByName get file!");
        }
    }

    public char[] getByName_chars(String path) throws Exception {
        return getByName_chars(path, false);
    }

    public char[] getByName_chars(String path, boolean enc) throws Exception {
        try {
            byte[] b = getByName_bytes(path);
            char[] text = new char[b.length];
            if (enc) {
                int foo = 0;
                for (; (foo < b.length) && (b[foo] != '\n') && (b[foo] != '\r'); ++foo) {
                }

                String encoding = new String(b, 0, foo);

                if (ZetaProperties.protocol_debug > 0)
                    log.debug("~loader.FILE::getByName_chars \n\t"
                            + "Document encoding " + encoding);

                text = new String(b, foo + 1, b.length - foo - 1, encoding)
                        .toCharArray();
            } else
                text = new String(b).toCharArray();
            return text;

        } catch (Exception e) {
            if (ZetaProperties.loader_exception)
                log.error("~loader.FILE::getByName_chars exception", e);

            throw new Exception();
        }
    }

    public void write(String file, String encoding, String text)
            throws Exception {
        try {
            if (encoding == null)
                encoding = "KOI8_R";
            byte[] data = text.getBytes(encoding);
            File f = new File(root + file);
            FileOutputStream fs = new FileOutputStream(f);
            fs.write(encoding.getBytes());
            fs.write('\n');
            fs.write(data);
            fs.close();
        } catch (Exception e) {
            if (ZetaProperties.loader_exception) {
                log.error("~loader.FILE::write exception", e);
            }

            throw new Exception();
        }

    }
}
