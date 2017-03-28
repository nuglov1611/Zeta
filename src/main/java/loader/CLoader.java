/*
 * File: CLoader.java
 * 
 * Created: Mon Mar 22 13:24:01 1999
 * 
 * Copyright(c) by Alexey Chen
 */

package loader;

import org.apache.log4j.Logger;

import java.util.Hashtable;

public class CLoader extends ClassLoader {

    private static final Logger log = Logger.getLogger(CLoader.class);

    public static String CName2FName(String s) {
        return s.replace('.', '/') + ".class";
    }

    private static CLoader instance = null;

    Hashtable<String, Class> cache = new Hashtable<String, Class>();

    Hashtable<String, String> nafing = new Hashtable<String, String>();

    private CLoader() {
    }

    public static synchronized CLoader getInstance() {
        if (instance == null) {
            instance = new CLoader();
        }

        return instance;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve)
            throws ClassNotFoundException {
        try {
            if (ZetaProperties.loader_debug > 1)
                log.debug("~loader.CLoader::loadclass loading class " + name);
            Class<?> c = cache.get(name);
            if (c == null) {
                if (nafing.containsKey(name))
                    throw new Exception();
                try {
                    //c = findSystemClass(name);
                    c = getClass().getClassLoader().loadClass(name);
                } catch (ClassNotFoundException e) {
                    log.error("Shit happens", e);
                    byte data[];
                    try {
                        data = loadClassData(name);
                    } catch (Exception ecc) {
                        log.error("Shit happens", ecc);
                        nafing.put(name, "");
                        throw ecc;
                    }
                    c = defineClass(name, data, 0, data.length);
                    if (Compiler.compileClass(c)) {
                        if (ZetaProperties.loader_debug > 1)
                            log
                                    .debug("~loader.CLoader::loadclass success compiling class "
                                            + name);
                    } else if (ZetaProperties.loader_debug > 1)
                        log
                                .debug("~loader.CLoader::loadclass error compiling class "
                                        + name);
                    cache.put(name, c);
                }

            }
            if (resolve)
                resolveClass(c);
            return c;
        } catch (Exception ec) {
            log.error("Shit happens", ec);
            throw new ClassNotFoundException("class " + name);
        } catch (Error err) {
            log.error("Error with loading class: \n\t" + name, err);
            throw err;
        }

    }

    byte[] loadClassData(String name) throws Exception {
        return Loader.getInstanceClass().loadByName_bytes(CName2FName(name));
    }
}
