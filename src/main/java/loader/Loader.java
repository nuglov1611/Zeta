/*
 * File: Loader.java
 * 
 * Created: Mon Mar 22 08:41:51 1999
 * 
 * Copyright(c) by Alexey Chen
 */

package loader;

import loader.protocols.FILE;
import loader.protocols.HTTP;
import org.apache.log4j.Logger;
import properties.PropertyConstants;
import properties.Session;
import properties.SessionManager;

public class Loader {

    private static final Logger log = Logger
            .getLogger(Loader.class);

    private static final int RML_LOADER_TYPE = 0;

    private static final int CLASS_LOADER_TYPE = 1;

    private static Loader LoaderInstance = null;

    private static Loader ClassLoaderInstance = null;

    Protocol protocol;

    private Loader(int type) throws Exception {
        String link = "";
        String p = "";
        final Session currentSession = SessionManager.getIntance().getCurrentSession();
        if (type == RML_LOADER_TYPE) {
            p = currentSession.getProperty(PropertyConstants.RML_SERVER_TYPE);
            link = currentSession.getProperty(PropertyConstants.RML_SERVER);
        } else if (type == CLASS_LOADER_TYPE) {
            p = currentSession.getProperty(PropertyConstants.RML_SERVER_TYPE);
            link = currentSession.getProperty(PropertyConstants.CLASS_SERVER);
        }

        if (p.equalsIgnoreCase(PropertyConstants.FILE_PROTO)) {
            protocol = new FILE(link);
        } else if (p.equalsIgnoreCase(PropertyConstants.HTTP_PROTO)) {
            String login = "";
            String pwd = "";
            if (type == RML_LOADER_TYPE) {
                login = currentSession.getProperty(PropertyConstants.RML_USERNAME);
                pwd = currentSession.getProperty(PropertyConstants.RML_PASSWORD);
            } else if (type == CLASS_LOADER_TYPE) {
                login = currentSession.getProperty(PropertyConstants.CLASS_USERNAME);
                pwd = currentSession.getProperty(PropertyConstants.CLASS_PASSWOR);
            }
            protocol = new HTTP(link, login, pwd);
        }
    }

    public static synchronized Loader getInstanceRml() throws Exception {
        if (LoaderInstance == null) {
            LoaderInstance = new Loader(RML_LOADER_TYPE);
        }
        return LoaderInstance;
    }

    public static synchronized Loader getInstanceClass() throws Exception {
        if (ClassLoaderInstance == null) {
            ClassLoaderInstance = new Loader(CLASS_LOADER_TYPE);
        }
        return ClassLoaderInstance;
    }

    public synchronized byte[] loadByName_bytes(String name) throws Exception {
        try {
            return protocol.getByName_bytes(name);
        } catch (Exception e) {
            if (ZetaProperties.loader_exception) {
                log.error("Shit happens", e);
            }
            throw new Exception("~loader.Loader::loadByName_bytes loading");
        }
    }

    public synchronized char[] loadByName_chars(String name, boolean enc) throws Exception {
        try {
            return protocol.getByName_chars(name, enc);
        } catch (Exception e) {
            if (ZetaProperties.loader_exception) {
                log.error("Shit happens", e);
            }
            throw new Exception("~loader.Loader::loadByName_chars loading");
        }
    }

    public static void Reset() {
        ClassLoaderInstance = null;
        LoaderInstance = null;
    }
}
