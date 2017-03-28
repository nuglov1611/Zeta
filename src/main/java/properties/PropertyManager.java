package properties;

import loader.ZetaProperties;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

/*
 * PropertyManager for managing zeta xml properties
 * 
 * @author Marina Vagapova
 * 
 * @since Nov 26, 2008
 */
public class PropertyManager {

    private static final Logger log = Logger
            .getLogger(PropertyManager.class);

    private String currentFileName;

    private static PropertyManager instance;

    private Document propsDocument;

    private static final String DEFAULT_PROP_FILE = "ZetaPropers.xml";

    private PropertyManager() {
        this(DEFAULT_PROP_FILE);
    }

    private PropertyManager(final String fileName) {
        loadProperties(fileName);
    }

    public static PropertyManager getIntance(final String fileName) {
        if (instance == null) {
            instance = new PropertyManager(fileName);
        }
        return instance;
    }

    public static PropertyManager getIntance() {
        if (instance == null) {
            instance = new PropertyManager();
        }
        return instance;
    }

    private Element convertToXml(Session session) {
        Element domSession = propsDocument
                .createElement(PropertyConstants.SESSION);
        domSession.setAttribute(PropertyConstants.ATTR_ID, session.getId());
        for (String propName : session.getPropertyNames()) {
            Element property = propsDocument.createElement(propName);
            property.setTextContent(session.getProperty(propName));
            domSession.appendChild(property);
        }
        return domSession;
    }

    private void createDefaultXml() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setIgnoringElementContentWhitespace(true);
            dbf.setValidating(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            propsDocument = db.newDocument();
            Element root = propsDocument.createElement(PropertyConstants.ROOT);
            Session defaultSession = new Session();
            defaultSession.setProperty(PropertyConstants.NAME,
                    PropertyConstants.DEFAULT_NAME);
            Element domSession = convertToXml(defaultSession);
            root.appendChild(domSession);
            Element loginAuto = propsDocument
                    .createElement(PropertyConstants.LOGIN_AUTO);
            loginAuto.setTextContent(PropertyConstants.DEFAULT_OFF);
            root.appendChild(loginAuto);
            Element loginAfterExit = propsDocument
                    .createElement(PropertyConstants.LOGIN_AFTER_EXIT);
            loginAfterExit.setTextContent(PropertyConstants.DEFAULT_OFF);
            root.appendChild(loginAfterExit);
            Element reportZoom = propsDocument
                    .createElement(PropertyConstants.REPORT_ZOOM);
            reportZoom.setTextContent(PropertyConstants.DEFAULT_ZOOM);
            root.appendChild(reportZoom);
            Element useProxy = propsDocument
                    .createElement(PropertyConstants.USE_PROXY);
            useProxy.setTextContent(PropertyConstants.DEFAULT_OFF);
            root.appendChild(useProxy);
            Element proxyType = propsDocument
                    .createElement(PropertyConstants.PROXY_TYPE);
            proxyType.setTextContent(PropertyConstants.PROXY_HTTP);
            root.appendChild(proxyType);
            Element proxyServer = propsDocument
                    .createElement(PropertyConstants.PROXY_SERVER);
            proxyServer.setTextContent(PropertyConstants.DEFAULT_TEXT);
            root.appendChild(proxyServer);
            Element proxyPort = propsDocument
                    .createElement(PropertyConstants.PROXY_PORT);
            proxyPort.setTextContent(PropertyConstants.DEFAULT_TEXT);
            root.appendChild(proxyPort);
            Element proxyAuth = propsDocument
                    .createElement(PropertyConstants.PROXY_AUTH);
            proxyAuth.setTextContent(PropertyConstants.DEFAULT_OFF);
            root.appendChild(proxyAuth);
            Element proxyLogin = propsDocument
                    .createElement(PropertyConstants.PROXY_LOGIN);
            proxyLogin.setTextContent(PropertyConstants.DEFAULT_TEXT);
            root.appendChild(proxyLogin);
            Element proxyPassword = propsDocument
                    .createElement(PropertyConstants.PROXY_PASSWORD);
            proxyPassword.setTextContent(PropertyConstants.DEFAULT_TEXT);
            root.appendChild(proxyPassword);
            Element proxyNtlm = propsDocument
                    .createElement(PropertyConstants.PROXY_NTLM);
            proxyNtlm.setTextContent(PropertyConstants.DEFAULT_TEXT);
            root.appendChild(proxyNtlm);
            Element useCache = propsDocument
                    .createElement(PropertyConstants.USE_CACHE);
            useCache.setTextContent(PropertyConstants.DEFAULT_OFF);
            root.appendChild(useCache);
            Element cachePath = propsDocument
                    .createElement(PropertyConstants.CACHE_PATH);
            cachePath.setTextContent(PropertyConstants.DEFAULT_TEXT);
            root.appendChild(cachePath);
            propsDocument.appendChild(root);
        } catch (ParserConfigurationException e) {
            log.error(e);
        }
    }

    public void loadProperties(final String fileName) {
        InputStream is = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory
                    .newInstance();
            dbf.setValidating(true);
            dbf.setIgnoringElementContentWhitespace(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            if (ZetaProperties.DEMO) {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                is = cl.getResourceAsStream(ZetaProperties.DEMO_PROPS);
                propsDocument = db.parse(is);
            } else if (fileName != null) {
                File settingsFile = null;
                if (ZetaProperties.ONLOAD) {
                    File uriFile = null;
                    if (fileName.startsWith("http")) {
                        URL fileUrl = new URL(fileName);
                        URLConnection urlConnection = fileUrl.openConnection();
                        urlConnection.connect();
                        is = urlConnection.getInputStream();
                        propsDocument = db.parse(is);
                    } else {
                        String validPath = fileName.replaceAll("\\\\", "/");
                        if (!validPath.startsWith("file")) {
                            validPath = "file:///" + validPath;
                        }
                        URI fileUri = new URI(validPath);
                        uriFile = new File(fileUri);
                        if (uriFile.exists()) {
                            settingsFile = uriFile;
                        }
                    }
                } else {
                    currentFileName = ZetaProperties.HOME_PATH + fileName;
                    //fileName = ZetaProperties.HOME_PATH + fileName;
                    settingsFile = new File(currentFileName);
                }
                if (propsDocument == null) {
                    if (settingsFile != null && settingsFile.exists()) {
                        propsDocument = db.parse(settingsFile);
                    } else {
                        createDefaultXml();
                        saveFileToXml(fileName, propsDocument);
                    }
                }
//                currentFileName = fileName;
            }
            loadSessions();
        } catch (SAXException e) {
            log.error(e);
        } catch (URISyntaxException e) {
            log.error(e);
        } catch (MalformedURLException e) {
            log.error(e);
        } catch (IOException e) {
            log.error(e);
        } catch (ParserConfigurationException e) {
            log.error(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error(e);
                }
            }
        }
    }

    private void loadSessions() {
        Map<String, Session> sessions = new HashMap<String, Session>();
        NodeList domSessions = propsDocument
                .getElementsByTagName(PropertyConstants.SESSION);
        Element domSession;
        String firstId = null;
        String currentSessionId = null;
        boolean needToUpdate = false;
        for (int i = 0; i < domSessions.getLength(); i++) {
            domSession = (Element) domSessions.item(i);
            domSession.setIdAttribute(PropertyConstants.ATTR_ID, true);
            String sessionId = domSession
                    .getAttribute(PropertyConstants.ATTR_ID);
            if (i == 0) {
                firstId = sessionId;
            }
            Session session = new Session(sessionId);

            NodeList sessionProps = domSession.getChildNodes();
            Node property;
            for (int p = 0; p < sessionProps.getLength(); p++) {
                property = sessionProps.item(p);
                if (property.getNodeType() == Node.ELEMENT_NODE) {
                    session.setProperty(((Element) property).getTagName(),
                            property.getTextContent());
                }
            }
            if (!sessions.containsKey(sessionId)) {
                sessions.put(sessionId, session);
            } else {
                Node parentNode = domSession.getParentNode();
                parentNode.removeChild(domSession);
                needToUpdate = true;
            }
        }
        SessionManager.getIntance().setSessions(sessions);
        if (SessionManager.getIntance().getCurrentSession() == null
                && !sessions.isEmpty()) {
            currentSessionId = getProperty(PropertyConstants.CURRENT_SESSION);
            if (currentSessionId != null && !"".equals(currentSessionId)
                    && sessions.containsKey(currentSessionId)) {
                SessionManager.getIntance().setCurrentSession(
                        sessions.get(currentSessionId));
            } else if (firstId != null) {
                currentSessionId = firstId;
                Session firstSession = sessions.get(firstId);
                saveProperty(PropertyConstants.CURRENT_SESSION, currentSessionId, false);
                SessionManager.getIntance().setCurrentSession(firstSession);
                needToUpdate = true;
            }
        }
        if (needToUpdate) {
            saveFileToXml(currentFileName, propsDocument);
        }
    }

    private void saveFileToXml(String fileName, Document propsDocument) {
        if (fileName != null) {
            File docFile = new File(fileName);
            saveFileToXml(docFile, propsDocument);
        }
    }

    private void saveFileToXml(File propertiesFile, Document propsDocument) {
        if (propertiesFile != null) {
            try {
                if (!propertiesFile.exists()) {
                    propertiesFile.createNewFile();
                }
                Transformer xformer = TransformerFactory.newInstance()
                        .newTransformer();
                xformer.setOutputProperty(OutputKeys.METHOD, "xml");
                xformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
                xformer.setOutputProperty(OutputKeys.INDENT, "yes");
                xformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                xformer.setOutputProperty(OutputKeys.MEDIA_TYPE, "UTF-8");

                Result result = new StreamResult(propertiesFile);
                Source source = new DOMSource(propsDocument);
                xformer.transform(source, result);
            } catch (TransformerConfigurationException ex) {
                log.error(ex);
            } catch (TransformerException ex) {
                log.error(ex);
            } catch (IOException ex) {
                log.error(ex);
            }
        }
    }

    public void saveToNewFile(final File newFile) {
        if (!ZetaProperties.DEMO && !ZetaProperties.ONLOAD) {
            currentFileName = newFile.getName();
            saveFileToXml(currentFileName, propsDocument);
        }
    }

    public String getProperty(String propertyName) {
        String property = "";
        NodeList props = propsDocument.getElementsByTagName(propertyName);
        if (props != null && props.getLength() > 0) {
            property = props.item(0).getTextContent();
        }
        return property;
    }

    public void saveProperty(Session session) {
        if (!ZetaProperties.DEMO && !ZetaProperties.ONLOAD) {
            Element existedSession = propsDocument.getElementById(session.getId());
            if (existedSession == null) {
                Element newSession = convertToXml(session);
                propsDocument.getDocumentElement().appendChild(newSession);
            } else {
                NodeList currentProps = null;
                for (String propName : session.getPropertyNames()) {
                    currentProps = existedSession.getElementsByTagName(propName);
                    if (currentProps.getLength() > 0) {
                        currentProps.item(0).setTextContent(
                                session.getProperty(propName));
                    } else {
                        Element property = propsDocument.createElement(propName);
                        property.setTextContent(session.getProperty(propName));
                        existedSession.appendChild(property);
                    }
                }
            }
            saveFileToXml(currentFileName, propsDocument);
        }
    }

    private void saveProperty(String propName, String propValue, boolean saveToFile) {
        NodeList props = propsDocument.getElementsByTagName(propName);
        if (props != null && props.getLength() > 0) {
            props.item(0).setTextContent(propValue);
        } else {
            Element newProperty = propsDocument.createElement(propName);
            newProperty.setTextContent(propValue);
            propsDocument.getDocumentElement().appendChild(newProperty);
        }
        if (saveToFile) {
            saveFileToXml(currentFileName, propsDocument);
        }
    }

    public void saveProperty(String propName, String propValue) {
        saveProperty(propName, propValue, !ZetaProperties.DEMO && !ZetaProperties.ONLOAD);
    }

    public void removeProperty(String propName) {
        if (!ZetaProperties.DEMO && !ZetaProperties.ONLOAD) {
            NodeList props = propsDocument.getElementsByTagName(propName);
            if (props != null && props.getLength() > 0) {
                propsDocument.removeChild(props.item(0));
                saveFileToXml(currentFileName, propsDocument);
            }
        }
    }

    public void removeProperty(Session session) {
        if (!ZetaProperties.DEMO && !ZetaProperties.ONLOAD) {
            Element existedSession = propsDocument.getElementById(session.getId());
            if (existedSession != null) {
                Node parentNode = existedSession.getParentNode();
                parentNode.removeChild(existedSession);
                saveFileToXml(currentFileName, propsDocument);
                SessionManager.getIntance().remove(session);
            }
        }
    }

    public static String getXmlBooleanProperty(boolean isSelected) {
        String property = PropertyConstants.DEFAULT_OFF;
        if (isSelected) {
            property = PropertyConstants.DEFAULT_ON;
        }
        return property;
    }

    public static boolean getBooleanProperty(String strProp) {
        boolean boolProp;
        if ("".equals(strProp)) {
            boolProp = false;
        } else if (PropertyConstants.DEFAULT_ON.equalsIgnoreCase(strProp)) {
            boolProp = true;
        } else if (PropertyConstants.DEFAULT_OFF.equalsIgnoreCase(strProp)) {
            boolProp = false;
        } else {
            boolProp = Boolean.valueOf(strProp);
        }
        return boolProp;
    }

    public static int getIntegerProperty(String strProp) {
        Integer intProp = new Integer(PropertyConstants.DEFAULT_ZOOM);
        try {
            intProp = Integer.parseInt(strProp);
        } catch (NumberFormatException ex) {
            log.error(ex);
        }
        return intProp;
    }
}
