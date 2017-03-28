/*
 * File: Document.java
 * 
 * Created: Thu Mar 18 16:15:09 1999
 * 
 * Copyright(c) by Alexey Chen Patched by Igor Kumagin 23.06.2001
 */

package core.document;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.FocusManager;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import loader.Loader;
import loader.ZetaProperties;
import loader.ZetaUtility;

import org.apache.log4j.Logger;

import properties.PropertyConstants;
import properties.SessionManager;
import publicapi.DocumentAPI;
import publicapi.RetrieveableAPI;
import views.focuser.Focusable;
import views.focuser.Focuser;
import action.api.ARGV;
import action.api.GlobalValuesObject;
import action.api.HaveMethod;
import action.api.RTException;
import action.api.ScriptApi;
import action.calc.Nil;
import action.calc.objects.class_type;
import core.browser.DocumentContainer;
import core.connection.BadPasswordException;
import core.connection.ConnectException;
import core.document.exception.LoadDocumentException;
import core.document.worker.ACTION;
import core.document.worker.Actioner;
import core.document.worker.ScriptExecutor;
import core.parser.Parser;
import core.parser.Proper;
import core.rml.Container;
import core.rml.LayoutMng;
import core.rml.RmlObject;
import core.rml.VisualRmlObject;
import core.rml.dbi.DSCollection;
import core.rml.dbi.Datastore;
import core.rml.dbi.ErrorReader;
import core.rml.dbi.exception.UpdateException;
import core.rml.ui.impl.ZPanelImpl;
import core.rml.ui.interfaces.ZPanel;
//import devices.DBF_JDBC1.FileControllerLoad;
//import devices.DBF_JDBC1.FileControllerSave;

public class Document implements DocumentAPI, GlobalValuesObject, HaveMethod, class_type, PropertyChangeListener {
	
    protected static final Logger log = Logger.getLogger(Document.class);
    
    public final static String PROGERSS = "progress";
    
    private final ScriptExecutor worker = new ScriptExecutor(this);
    
    private final Container children = new Container(this);
    
    private final JFileChooser chooser = new JFileChooser();
    
    private final ArrayList<JMenu> docMenu = new ArrayList<JMenu>();
    
    private final MenuListener mListener = new MenuListener();
    
    private JMenuBar mainMenu = null;

    class MenuListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            try {
                doAction(command, null);
            }
            catch (Exception ex) {
                log.error("Shit happens", ex);
            }
//            if (e.getSource() instanceof views.Item) {
//                try {
//                    executeScript(((views.Item) e.getSource()).getExp(), false);
//                } catch (Exception ex) {
//                    log.debug("!", ex);
//                }
//            }
        }
    }
    
    
    class FL extends FocusAdapter {

        @Override
        public void focusGained(FocusEvent e) {
            FocusManager.getCurrentManager().downFocusCycle();
        }
    }

    /** */
    class HandlerList {
        public HandlerList next;

        public Closeable   handler;

        public HandlerList(Closeable c, HandlerList h) {
            handler = c;
            next = h;
        }
    }
    
    /** */
    final public static int                         ACT_CANCEL = 0;

    /** */
    final public static int                         ACT_DOK    = 1;

    /** */
    final public static int                         ACT_HOK    = 2;

    /** */
    final public static int                         ACT_NEW    = 3;

    /** */
    final public static int                         ACT_SAVE   = 4;
    

    
    public static boolean                           resetIt    = false;

    public static final Hashtable<String, Document> hashable   = new Hashtable<String, Document>();

    private Parser                                  parser     = null;
    
    private boolean creatingDocument = true;

    public Hashtable<String, Object> getAliases() {
        return aliases;
    }

    /**
     * загрузить и открыть документ в новом окне
     */
    public synchronized void callDocumentNewWindow(String doc_name,
            Object[] args, Actioner actor) throws Exception {

        container.loadDocument(doc_name, args, true);

        actor.notifyActioner();
    }

    /**
     * загрузить и открыть документ в том же окне в котором открыт текущий и
     * зарегистрировать Actor.
     */
    public synchronized void callDocumentSomeWindow(String doc_name,
            Object[] args, Actioner actor) throws Exception {
        container.loadDocument(doc_name, args, false);

        actor.notifyActioner();
    }

    public static Document getDocumentFromHash(String docName, Document parent, Object[] args, DocumentContainer cnt) {
        Document doc = hashable.get(docName); 
        if(doc != null){
            doc.setParent(parent);
            doc.setArguments(args);
            doc.setContainer(cnt);
        }
        return doc;
    }

    public Document getDocumentFromLHash(String docName) {
        return lhashable.get(docName);
    }
    
    
    /**
     * Конструктор
     * 
     * @param DocName
     * @param args
     * @param parent
     * 
     * @throws LoadDocumentException
     */
    public Document(String DocName, Object[] args, Document parent,
            DocumentContainer cnt) throws LoadDocumentException {

        container = cnt;

        parentDocument = parent;

        parser = new Parser(this);
        

        try {
            DocName = DocName.trim();
            Proper prop;
            if (DocName.charAt(0) == '&') {
                try {
                    prop = (Proper) parent.aliases.get(DocName.substring(1));
                    myname = (String) prop.hash.get("###file###");
                    mypath = (String) prop.hash.get("###path###");
                }
                catch (Exception e) {
                    log.error("Shit happens", e);
                    throw new RTException("CastException", "&ALIAS <"
                            + DocName.substring(1)
                            + "> must containt Proper!!!!");
                }
            }
            else {
                int foo = DocName.lastIndexOf('/');
                if (foo != -1) {
                    myname = DocName.substring(foo + 1);
                    mypath = DocName.substring(0, foo);
                }
                else
                    myname = DocName;
                char[] text = Loader.getInstanceRml().loadByName_chars(DocName,
                        true);
                prop = parser.createProper(text, null);
            }

            //панель документа
            LayoutMng.setLayout(panel, prop, new GridLayout(1,1));

            aliases = new Hashtable<String, Object>();
            aliases.put(AliasesKeys.DOCUMENT, this);
            aliases.put(AliasesKeys.DOCUMENT_ARGUMENTS, new ARG(args));
            aliases.put(AliasesKeys.SELF, this);
            aliases.put(AliasesKeys.GLOBAL, new ARGV());

            final Focuser focuser = new Focuser();
            focuser.init(null, this);
            aliases.put(AliasesKeys.FOCUSER, focuser);

            version = (String) prop.get("VERSION");
            if (version == null)
                version = "...";

            aliases.put(AliasesKeys.DOCUMENT_VERSION, version);
            try{
            	executeScript((String) prop.get("PRELOADSCRIPT"), true);
            }catch(Exception e){
            	log.error("Preload Script FAILED!", e);
            }
            
            children.addChildren(prop, this);
            
            //очищаем описание дерева объектов за ненадобностью, и сохраняем проперти на всякий случай
            prop.content = null;
            prop.next = null;
            System.gc();
            
            aliases.put(AliasesKeys.DOCUMENT_PROPERTIES, prop);
            
        	try{
        		executeScript((String) prop.get("POSTLOADSCRIPT"), true);
            }catch(Exception e){
            	log.error("Postload Script FAILED!", e);
            }
        		

            final String script = (String) prop.get("CLOSESCRIPT");
            if (script != null) {
                closescript = ScriptApi.getAPI(script);
            }
            String h = (String) prop.get("HASHABLE");

            if (DocName.charAt(0) == '&') {
                if ((h != null)
                        && (h.trim().toUpperCase().compareTo("YES") == 0)) {
                    lhashable.put(DocName, this);
                }
            }
            else if ((h != null)
                    && (h.trim().toUpperCase().compareTo("YES") == 0)) {
                hashable.put(DocName, this);
            }
        }
        catch (Exception e) {
            log.error("Can not load the core.document! BadDocument!", e);
            ZetaUtility.message("Произошла внутренняя ошибка при загрузке документа, обратитесь к программисту.");
            // notifyHandlers(hl);
            throw new LoadDocumentException(mypath + "/" + myname);
        }
        
        creatingDocument = false;
    }

    
    public Object                                   actionMutex      = new Object();

    private ScriptApi                                    closescript      = null;

    public String                                   myname           = "";

    public String                                   mypath           = "";

    public String                                   version          = new String(
                                                                             "...");

    /** */
    private Document                                parentDocument   = null;

    /** */
    private Hashtable<String, Object>               aliases          = null;

    /** */
    public final Hashtable<String, Document> lhashable        = new Hashtable<String, Document>();

    /** */
    private DocumentContainer                       container        = null;

    /** */
    private Actioner                                actor            = null;

    /** */
    private ZPanel                                  panel = ZPanelImpl.create();

    /** */
    private core.rml.dbi.DSCollection                        collection       = null;

    /* обработка shortcut'ов и пересылки кнопок в определенный компонент*/
    private Hashtable<ShortcutInfo, Shortcutter>    shorts           = new Hashtable<ShortcutInfo, Shortcutter>();

    private Hashtable<Integer, Object>              catchers         = new Hashtable<Integer, Object>();

    private int                                     keyNum           = 0;

    private KeyCatcher                              nowCatcher       = null;

    private Hashtable<Integer, Object>              nowList          = catchers;

    private Vector<Integer>                         keyBuf           = new Vector<Integer>();

    private KeyCatcherInfo                          nowCheck         = null;

    private HandlerList                             hl               = null;

    /**
     * Requestors which needs to be notified when window events occurs,
     * i.e. windowsOpened and other
     */
    private Vector<WindowListener>                  windowRequestors = new Vector<WindowListener>();

    public void addWindowRequestor(WindowListener newRequestor) {
        if (!windowRequestors.contains(newRequestor)) {
            windowRequestors.add(newRequestor);
        }
    }

    public void activatedWindowRequestors(WindowEvent e) {
        if (!windowRequestors.isEmpty()) {
            for (WindowListener windowRequestor : windowRequestors) {
                windowRequestor.windowActivated(e);
            }
        }
    }

    public void addHandler(Closeable handler) {
        hl = new HandlerList(handler, hl);
    }

    public void addKeyCatcher(KeyCatcher com, int[] keys) {
        if (com == null || keys == null) {
            return;
        }

        KeyCatcherInfo inf = new KeyCatcherInfo();
        inf.keys = keys;
        inf.com = com;
        int num = 0;
        Hashtable<Integer, Object> hash = catchers;
        do {
            Integer key = new Integer(keys[num]);
            Object obj = hash.get(key);
            if (obj == null || num == keys.length - 1) {
                hash.put(key, inf);
                log.debug("keyCatcher = " + inf + " on " + key);
                break;
            }
            else if (obj instanceof KeyCatcherInfo) {
                KeyCatcherInfo inf2 = (KeyCatcherInfo) obj;
                if (num != inf2.keys.length - 1) {
                    Hashtable<Integer, KeyCatcherInfo> hash2 = new Hashtable<Integer, KeyCatcherInfo>();
                    hash2.put(new Integer(inf2.keys[++num]), inf2);
                    hash2.put(new Integer(keys[num]), inf);
                    hash.put(key, hash2);
                    log.debug("keyCatcher = " + inf + " on " + keys[num]);
                }
                break;
            }
            else {
                hash = (Hashtable<Integer, Object>) obj;
                num++;
            }
        } while (true);
    }

    public void addKeyCatcher(KeyCatcher com, String s)
            throws IllegalArgumentException {
        addKeyCatcher(com, parseKeys(s));
    }

    public void addShortcut(String name, Shortcutter com) {
        if (com == null) {
            return;
        }
        try {
            ShortcutInfo inf = new ShortcutInfo();
            name = name.toUpperCase();
            int ind = -1;
            if ((ind = name.indexOf("ALT+")) != -1) {
                name = name.substring(0, ind) + name.substring(ind + 4);
                inf.modifiers |= KeyEvent.ALT_MASK;
            }
            if ((ind = name.indexOf("CTRL+")) != -1) {
                name = name.substring(0, ind) + name.substring(ind + 5);
                inf.modifiers |= KeyEvent.CTRL_MASK;
            }
            if ((ind = name.indexOf("SHIFT+")) != -1) {
                name = name.substring(0, ind) + name.substring(ind + 6);
                inf.modifiers |= KeyEvent.SHIFT_MASK;
            }
            try {
                inf.key = parseKey(name);
                shorts.put(inf, com);
            }
            catch (Exception e) {
                log.error("Shit happens", e);
            }
        }
        catch (Exception e) {
            log.error("Shit happens", e);
        }
    }

    /** */
    public boolean close() {
        showInfo("Закрытие документа");
        try {
            if (closescript != null) {
                Object close_ret = closescript.eval(aliases);
                if(close_ret != null && !close_ret.toString().trim().equals("")){
                    return false;
                }
            }
        }
        catch (Exception e) {
            log.error("Shit happens", e);
        }
        showProgress(30);
        if (collection != null) {
            collection.removeLocks();
        }
        showProgress(60);

        if (actor != null) {
            actor.notifyActioner();
        }
        showProgress(100);

        clearInfo();
        showProgress(0);
        return true;
    }

    public String getTitle() {
        String title;
        try {
            title = (String) ((Proper) aliases
                    .get(AliasesKeys.DOCUMENT_PROPERTIES))
                    .get("DOCUMENT_TITLE");
        }
        catch (Exception e) {
            log.error("Shit happens", e);
            title = ZetaUtility.pr(ZetaProperties.TITLE_MAINWINDOW, "MainWindow");
        }
        if (title == null) {
            title = ZetaUtility.pr(ZetaProperties.TITLE_MAINWINDOW, "MainWindow");
        }
        return title;
    }

    public boolean executeShortcut(KeyEvent e) {
        if (keyCatched(e)) {
            return true;
        }
        if (e.getKeyCode() == KeyEvent.VK_N
                && e.getModifiers() == KeyEvent.CTRL_MASK + KeyEvent.ALT_MASK) {
            log.debug("Document named " + mypath + "/" + myname);
            e.consume();
            return true;
        }
        ShortcutInfo inf = new ShortcutInfo(e.getKeyCode(), e.getModifiers());
        Shortcutter com = shorts.get(inf);
        if (com != null) {
            com.processShortcut();
            e.consume();
            return true;
        }
        return false;
    }

    public String getPage() {
        Proper p = (Proper) aliases.get(AliasesKeys.DOCUMENT_PROPERTIES);
        if (p != null)
            return (String) p.get("HTML_PAGE");
        else
            return null;
    }

    /** */
    public ZPanel getPanel() {
        return panel;
    }

    public Document getParentDocument() {
        return parentDocument;
    }

    //    public DocumentContainer getContainer() {
    //        return container;
    //    }

    public Object getValue() throws Exception {
        return this;
    }

    // end pavel patch

    public Object getValueByName(String name) throws Exception {
        return null;
    }

    private boolean keyCatched(KeyEvent e) {
        if (nowCatcher != null) {
            if (!nowCatcher.catchKey(e)) {
                nowCatcher = null;
            }
            return true;
        }

        keyBuf.addElement(new Integer(e.getKeyCode()));
        if (nowCheck != null) {
            if (nowCheck.keys[keyNum] != e.getKeyCode()) {
                scrollKeys();
            }
        }
        else {
            Object obj = nowList.get(new Integer(e.getKeyCode()));
            if (obj == null) {
                scrollKeys();
            }
            else if (obj instanceof KeyCatcherInfo) {
                nowCheck = (KeyCatcherInfo) obj;
                nowList = catchers;
            }
            else {
                nowList = (Hashtable<Integer, Object>) obj;
            }
        }

        if (nowList == catchers && nowCheck == null) {
            keyNum = 0;
            keyBuf.removeAllElements();
        }
        else {
            keyNum++;
        }
        if (nowCheck != null && nowCheck.keys.length == keyNum) {
            keyNum = 0;
            nowCatcher = nowCheck.com;
            nowCheck = null;
            keyBuf.removeAllElements();
        }
        return false;
    }

    public Object method(String method, Object arg) throws Exception {
        if (method.equals("PATH")) {
            return mypath;
        }
        else if (method.equals("NAME"))
        /*
         * if ( arg!=null ) throw new RTException("CastException",
         * "DOCUMENT@NAME must called with out arguments");
        */{
            return myname;
        }
        else if (method.equals("ACTSAVE")) {
            return new Double(actSave());
        }
        else if (method.equals("ACTCANCEL")) {
            return new Double(actCancel());
        }
        else if (method.equals("ACTDOK")) {
            return new Double(actDoc());
        }
        else if (method.equals("ACTHOK")) {
            return new Double(actHok());
        }
        else if (method.equals("ACTNEW")) {
            return new Double(actNew());
        }
        else if (method.equals("CALCULATE")) {
            if (!(arg instanceof String)) {
                throw new RTException("CastException",
                        "DOCUMENT@DOACTION must called with one String argument");
            }
            try {
            	return calculate((String) arg);
            }
            catch (Exception e) {
                log.error("Shit happens", e);
                throw new RTException("EXCEPTION", e.toString());
            }
            
        }
        else if (method.equalsIgnoreCase("runInBackground")) {
            if (!(arg instanceof String)) {
                throw new RTException("CastException",
                        "DOCUMENT@DOACTION must called with one String argument");
            }
            try {
                runInBackground((String) arg);
                return null;
            }
            catch (Exception e) {
                log.error("Shit happens", e);
                throw new RTException("EXCEPTION", e.toString());
            }
            
        }
        else if (method.equals("DOACTION")) {
            if (!(arg instanceof String)) {
                throw new RTException("CastException",
                        "DOCUMENT@DOACTION must called with one String argument");
            }
            try {
                doAction((String) arg, null);
            }
            catch (Exception e) {
                log.error("Shit happens", e);
                throw new RTException("EXCEPTION", e.toString());
            }
            return new Nil();
        }
        else if (method.equals("SERVERPATH"))
            return getServerParth();
        else if (method.equals("GETALIASES"))
            return aliases;
        else if (method.equals("GETOBJECT"))
        	return getObject((String) arg);
        else if (method.equals("LOAD")) {
            if (!(arg instanceof String)) {
                throw new RTException("CastException",
                        "DOCUMENT@LOAD must called with one String argument");
            }
            try {
                return loadRml((String) arg);
            }
            catch (Exception e) {
                log.error("Shit happens", e);
                throw new RTException("LoadException", "Error load core.document");
            }
        }
        else if (method.equals("PARSE")) {
            if (!(arg instanceof String))
                throw new RTException("CastException",
                        "DOCUMENT@PARSE must called with one String argument");
            try {
                return parse((String) arg);
            }
            catch (Exception e) {
                log.error("Shit happens", e);
                throw new RTException("LoadException", "" + e);
            }
        }
        else if (method.equals("OPENFILE")){
            return openFileDlg();
        }
        else if (method.equals("READFILE"))
            try {
                return readFile((String)arg);
            }
            catch (Exception e) {
                log.error("Shit happens", e);
                throw new RTException("RunTime", e.getMessage());
            }
        else if (method.equals("SAVEFILE"))
            try {
                Vector<Object> v = (Vector<Object>) arg;
                writeToFile((String) v.elementAt(0), (byte[]) v.elementAt(1));
                return new Double(0);
            }
            catch (Exception e) {
                log.error("Shit happens", e);
                throw new RTException("RunTime", e.getMessage());
            }
        else if (method.equals("SHELL"))
            try {
                shell((String) arg);
                return new Double(0);
            }
            catch (Exception e) {
                log.error("Shit happens", e);
                throw new RTException("RunTime", e.getMessage());
            }
        else if (method.equals("SHELLWAIT"))
            try {
                return shellWait((String)arg);
            }
            catch (Exception e) {
                log.error("Shit happens", e);
                throw new RTException("RunTime", e.getMessage());
            }
        else if (method.equals("FILEDIALOG"))
            try {
                int returnVal = -1;
                if (((String) arg).equalsIgnoreCase("load"))
                	returnVal = chooser.showOpenDialog(container);
                else
                	returnVal = chooser.showSaveDialog(container);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                       //return chooser.getSelectedFile().getAbsoluteFile();
                       File f = chooser.getSelectedFile();
                       Object s[] = {f.getAbsolutePath(), f.getName(), f.getAbsoluteFile()};
                       return s;
                }
                return null;
            }
            catch (Exception e) {
                log.error("Shit happens", e);
                throw new RTException("RunTime", e.getMessage());
            }
        else if (method.equals("RUSER"))
            try {
                return new Double(0);
            }
            catch (Exception e) {
                log.error("Shit happens", e);
                throw new RTException("RunTime", e.getMessage());
            }
        else if (method.equals("EXTSURE")){
            return ZetaUtility.extSure((String)arg);
        }
        else if (method.equals("SURE")) {
            Vector<Object> v = (Vector<Object>) arg;
            String msg = (String) v.elementAt(0);
            Double val = (Double) v.elementAt(1);
            String font = null;
            if (v.size() > 2) {
                font = (String) ((Vector) arg).elementAt(2);
            }
            boolean b;
            //just message
            if (val.intValue() == ZetaProperties.MESSAGE_SURE) {
                b = ZetaUtility.sure(msg, ZetaProperties.MESSAGE_SURE, font);
            }
            //an error
            else if (val.intValue() == ZetaProperties.MESSAGE_ERROR) {
                b = true;
                ZetaUtility.message(msg, ZetaProperties.MESSAGE_ERROR, font);
            //warning
            } else {
                b = true;
                ZetaUtility.message(msg, ZetaProperties.MESSAGE_WARN, font);
            }
            return new Double(b ? 1 : 0);
        }
//        else if (method.equals("SAVEDBF")) {
//            Vector<Object> v = (Vector<Object>) arg;
//            String hdr = (String) v.elementAt(0);
//            core.rml.dbi.Datastore ds = (core.rml.dbi.Datastore) v.elementAt(1);
//            FileControllerSave fl = new FileControllerSave(hdr);
//            fl.saveNew(ds, "");
//            return new Double(0);
//        }
        else if (method.equals("FOCUS")) {
            if (arg instanceof Vector) {
                arg = ((Vector<Object>) arg).elementAt(0);
            }
            if (arg instanceof Focusable) {
                ((Focusable) arg).focusThis();
            }
            return new Double(0);
        }
        else if (method.equals("ERROR")) {
            if (!(arg instanceof String)) {
                throw new RTException("WrongParams",
                        "Wrong parameters of error <string>");
            }
            ZetaUtility.message((String) arg, ZetaProperties.MESSAGE_ERROR);
            return null;
        }
//        else if (method.equalsIgnoreCase("LOADDBF")) {
//            Vector<Object> v = (Vector<Object>) arg;
//            String s = (String) v.elementAt(0);
//            core.rml.dbi.Datastore datastore1 = (core.rml.dbi.Datastore) v.elementAt(1);
//            FileControllerLoad filecontrollerload = null;
//            filecontrollerload = new FileControllerLoad(s);
//            if (!filecontrollerload.canRead("")) {
//                throw new RTException("RunTime", "File not found");
//            }
//            else {
//                filecontrollerload.load(datastore1, "");
//                return new Double(0.0D);
//            }
//        } 
        else if (method.equals("INPUT")) {
            if (arg instanceof String) {
                return ZetaUtility.input((String)arg);
            }
            else if (arg instanceof Vector) {
            Vector v = (Vector) arg;
            String msg = (String) v.elementAt(0);
                String val = String.valueOf(v.elementAt(1));
                return ZetaUtility.input(msg, val);
            } else {
                throw new RTException("HasMethodException",
                        "object DOCUMENT has not method " + method);
        }
        } else {
            throw new RTException("HasMethodException",
                    "object DOCUMENT has not method " + method);
        }
    }

    void notifyHandlers(HandlerList hl) {
        if (hl != null) {
            if (hl.next != null) {
                notifyHandlers(hl.next);
            }
            try {
                hl.handler.closeNotify();
            }
            catch (Exception e) {
                log.error("Shit happens", e);
            }
        }
    }

    private int parseKey(String s) throws Exception {
        try {
            Field f = KeyEvent.class.getField("VK_" + s);
            return f.getInt(null);
        }
        catch (Exception e) {
            return Integer.parseInt(s);
        }
    }

    private void setParent(Document parent){
        parentDocument = parent;
    }

    private void setArguments(Object[] args){
        aliases.put(AliasesKeys.DOCUMENT_ARGUMENTS, new ARG(args));
    }

    private void setContainer(DocumentContainer cnt){
        container = cnt;
    }
    
    
    public int[] parseKeys(String s) throws IllegalArgumentException {
        if (s == null) {
            return null;
        }

        String[] ar = null;
        int i = 0;
        try {
            ar = views.UTIL.parseDep(s);
            int[] ret = new int[ar.length];
            for (; i < ar.length; i++) {
                ar[i] = ar[i].trim().toUpperCase();
                ret[i] = parseKey(ar[i]);
            }

            return ret;

        }
        catch (Exception e) {
            if (ar != null && i < ar.length) {
                throw new IllegalArgumentException("Wrong key " + ar[i]);
            }
            else {
                throw new IllegalArgumentException("Wrong keys: " + e);
            }
        }
    }

    /** */
    public int processAction(int action) {
        log.debug("process Action " + action);
        int success = 1;
        switch (action) {
        case ACT_CANCEL:
            // известить всех кто того желает , о том что документ
            // закрывается
            notifyHandlers(hl);
            container.closeDocument();
            break;
        case ACT_DOK: /* */
            try {
                //((RetrieveableAPI) maincomponent).toDS();
            	final RmlObject[] objs = children.getChildren();
            	for(RmlObject child : objs){
            		if(child instanceof VisualRmlObject && child instanceof RetrieveableAPI){
            			((RetrieveableAPI) child).toDS();
            		}
            	}
            	
                if (collection != null) {
                    collection.update();
                }
                else {
                    updateAllStore();
                }
                // известить всех кто того желает , о том что документ закрывается
                notifyHandlers(hl);
                container.closeDocument();
            }
            catch (Exception e) {
                log.error("Shit happens", e);
                ErrorReader.getInstance().addMessage(e.getMessage());
                success = 0;
            }
            break;
        case ACT_HOK: /* */
            try {
                try {
                    // известить всех кто того желает , о том что документ закрывается
                    notifyHandlers(hl);
                    if (parentDocument != null && aliases != null && aliases.get(AliasesKeys.RETURNSTORE) != null && parentDocument.getAliases() != null) {
                        parentDocument.getAliases().put(AliasesKeys.STORE,
                                aliases.get(AliasesKeys.RETURNSTORE));
                    }
                }
                catch (NullPointerException e) {
                    log.error("Shit happens", e);
                    parentDocument.getAliases().remove(AliasesKeys.STORE);
                }
                container.closeDocument();
            }
            catch (Exception e) {
                log.error("Shit happens", e);
                ErrorReader.getInstance().addMessage(e.getMessage());
                success = 0;
            }
            break;
        case ACT_SAVE: /* */
            try {
                //((RetrieveableAPI) maincomponent).toDS();
            	final RmlObject[] objs = children.getChildren();
            	for(RmlObject child : objs){
            		if(child instanceof VisualRmlObject && child instanceof RetrieveableAPI){
            			((RetrieveableAPI) child).toDS();
            		}
            	}
            	            	
                if (collection != null) {
                    collection.update();
                }
                else {
                    updateAllStore();
                }
            }
            catch (Exception e) {
                log.error("Shit happens", e);
                ErrorReader.getInstance().addMessage(e.getMessage());
                success = 0;
            }
            break;
        case ACT_NEW: /* */
            try {
                reset();
            }
            catch (Exception e) {
                log.error("Shit happens", e);
                ErrorReader.getInstance().addMessage(e.getMessage());
                success = 0;
            }
            break;
        default:
            break;
        }
        return success;
    }

    /** */
    public void reset() throws Exception {
        if (collection != null) {
            collection.reset();
//            if (maincomponent == null) {
//                log.debug("maincomponent==null");
//            }
//            else {
//                ((RetrieveableAPI) maincomponent).fromDS();
//            }
        	final RmlObject[] objs = children.getChildren();
        	for(RmlObject child : objs){
        		if(child instanceof VisualRmlObject && child instanceof RetrieveableAPI){
        			((RetrieveableAPI) child).fromDS();
        		}
        	}
            
        }
        else {
            throw new Exception();
        }
    }

    private void scrollKeys() {
        for (int num = 1; num <= keyNum; num++) {
            Integer key = keyBuf.elementAt(num);
            Object obj = catchers.get(key);

            if (obj != null) {
                int num2 = num + 1;
                for (; num2 <= keyNum; num2++) {
                    key = keyBuf.elementAt(num2);
                    if (obj instanceof Hashtable) {
                        obj = ((Hashtable) obj).get(key);
                        if (obj == null) {
                            break;
                        }
                    }
                    else if (((KeyCatcherInfo) obj).keys[num2 - num] != key
                            .intValue()) {
                        break;
                    }
                }
                if (num2 > keyNum) {
                    for (int i = 0; i < num; i++) {
                        keyBuf.removeElementAt(i);
                    }
                    keyNum = num2 - num - 1;
                    if (obj instanceof KeyCatcherInfo) {
                        nowCheck = (KeyCatcherInfo) obj;
                        nowList = catchers;
                    }
                    else {
                        nowCheck = null;
                        nowList = (Hashtable<Integer, Object>) obj;
                    }
                    return;
                }

            } // if (obj != null)

        } // for (int num = 1; num <= keyNum; num++)

        nowCheck = null;
        nowList = catchers;

    }

    public void setValue(Object obj) throws Exception {
    }

    public void setValueByName(String name, Object obj) throws Exception {
    }

    public String toString() {
        return "DOCUMENT " + mypath + "/" + myname;
    }


    public String type() throws Exception {
        return "DOCUMENT";
    }

    /**
     * @throws SQLException 
     * @throws BadPasswordException 
     * @throws UpdateException  */
    void updateAllStore() throws UpdateException, BadPasswordException, SQLException {
    	Enumeration<Object> e = aliases.elements();
    	while(e.hasMoreElements()){
    		final Object tmp = e.nextElement();
    		if(tmp instanceof Datastore){
    			((Datastore) tmp).update();
    		}
    	}
    }

    public void setFirstFocus(Component comp) {
        ((Focuser) aliases.get(AliasesKeys.FOCUSER)).setFirsFocus(comp);
    }

    public String getName(){
        return myname;
    }
    
//    public Object executeJavaScript(String script) throws Exception{
//        ScriptEngineManager m = new ScriptEngineManager();
//        ScriptEngine engine = m.getEngineByName("js");
//    	if(script.startsWith("js_file:")){
//    		script = new String(Loader.getInstanceRml().loadByName_chars(script.substring(8), true));
//    	}
//        
//        Bindings bindings = engine.createBindings();
//        bindings.putAll(aliases);
//        
//        
//        try {
//            Object ret = engine.eval(script, bindings); 
//            return ret;
//        } catch (ScriptException e) {
//            log.error("", e);
//            return null; 
//        }
//    }    
    
    public void doAction(String action, NotifyInterface ni) throws Exception {
    	if(creatingDocument){
    		final ACTION a = new ACTION(this);
    		a.prepareAction(action, aliases, ni);
    		a.runAction();
    	}else{
    		worker.doAction(action, ni);
    	}
    }

    public void doBgAction(String action, NotifyInterface ni) throws Exception {
        if ((action == null) || (action.trim().length() == 0)) {
            return;
        }
        final ACTION act = new ACTION(this);
        act.prepareAction(action, aliases, ni);
        final Thread bgThread = new Thread() {

            public void run() {
                try {
                	act.runAction();
                } catch (Exception ex) {
                    log.error("!", ex);
                }
            }
        };
        bgThread.start();
    }

    public void executeScript(String script, boolean block_mode) throws Exception {
    	if(script == null || script.trim().length() == 0)
    		return;
    	
    	if(creatingDocument || block_mode){
    	    final ScriptApi c = ScriptApi.getAPI(script);
    	   
    		c.eval(aliases);
    	}else{
    		worker.executeScript(script);
    	}
    }

    @Override
    public void runInBackground(String script) throws Exception {
    	if(script == null || script.trim().length() == 0)
    		return;
    	
    	final ScriptApi c = ScriptApi.getAPI(script);
        final Thread bgThread = new Thread() {

            public void run() {
                try {
                    c.eval(aliases);
                } catch (Exception ex) {
                	log.error("", ex);
                }
            }
        };
        bgThread.start();
    }

    @Override
    public Object calculate(String script) throws Exception {
        if(script == null || script.trim().length() == 0)
            return null;
        
        final ScriptApi c = ScriptApi.getAPI(script);
        return c.eval(aliases);
    }
    
    public String calculateMacro(String script) throws Exception {
        return ScriptApi.macro(script, aliases);
    }
    
    

    public void showInfo(String info) {
        container.showInfo(info);
    }

    public void clearInfo() {
        container.clearInfo();
    }

    public void showProgress(int progress) {
        container.showProgress(progress);
    }

    public Connection getConnection() throws ConnectException,
            BadPasswordException {
        return container.getConnection();
    }
    
    public DocumentContainer getDocContainer(){
        return container;
    }

	public RmlObject findObject(String alias) {
		return (RmlObject) aliases.get(alias);
	}

	public void registrate(RmlObject rmlObject) {
		if(rmlObject.getAlias() != null)
			aliases.put(rmlObject.getAlias().toUpperCase(), rmlObject);
	}

	
    public void addMenuActionListener(JMenuItem item) {
        if(item instanceof JMenu){
            JMenu menu = (JMenu) item;
            int item_count = menu.getItemCount(); // getItemCount();
            JMenuItem cur_itm = null;
            for (int i = 0; i < item_count; i++) {
                cur_itm = menu.getItem(i);
                if (cur_itm != null) {
                    if (cur_itm instanceof JMenu) {
                        addMenuActionListener(cur_itm);
                    }else if (cur_itm instanceof JMenuItem) {
                        ((JMenuItem) cur_itm).addActionListener(mListener);
                    }
                 }
            }
        }
        else if (item instanceof JMenuItem) {
            ((JMenuItem) item).addActionListener(mListener);
        }
    }
	
    public void setDocumentMenu(){
    	if (mainMenu == null) {
    		initDocumentMenu();
    	}
    	else{
    		paintMenu();
    	}
    }

    private void paintMenu(){
	    if (SwingUtilities.isEventDispatchThread()) {
		    container.setMenuBar(mainMenu);
	    } else {
	        Runnable shell = new Runnable() {
	            @Override
	            public void run() {
	                try {
	            	    container.setMenuBar(mainMenu);
	                } catch (Exception ex) {
	                    throw new RuntimeException(ex);
	                }
	            }
	        };
	        try {
				SwingUtilities.invokeAndWait(shell);
			} catch (InvocationTargetException e) {
			} catch (InterruptedException e) {
			}
	    }
	    mainMenu.repaint();
    }
    

    
    
	public void initDocumentMenu(){
		ArrayList<JMenu> documentMenu = null;
	    if(docMenu.size() > 0){
		    documentMenu = new ArrayList<JMenu>();
	        for(JMenu menu_item : docMenu){
	            documentMenu.add(menu_item);
	        }
	    }

	    mainMenu = container.createMenuBar(documentMenu);
	    
    	paintMenu();
	}
	
	public void addMenu(JMenu item){
	    addMenuActionListener(item);
	    docMenu.add(item);
	}
	
	@Override
	public void addChild(RmlObject child) {
        children.addChildToCollection(child);
        if (child instanceof DSCollection) {
            collection = (DSCollection) child;
        }
        if (child instanceof VisualRmlObject) {
            //panel.add(((VisualRmlObject)child).getVisualComponent());
        	LayoutMng.add(panel, (VisualRmlObject) child);
        }
        else if (child instanceof Focuser) {
            aliases.put(AliasesKeys.FOCUSER, child);
        }
	}

	@Override
	public RmlObject[] getChildren() {
		return children.getChildren();
	}

	@Override
	public Container getContainer() {
		return children;
	}

	@Override
	public void initChildren() throws Exception {

		showInfo("Инициализация документа.");
        
        try {
            ((Focuser) aliases.get(AliasesKeys.FOCUSER)).activateFocusers();

            // если в документе нет коллекц
            if (collection == null)
                collection = new DSCollection(this);
            
            aliases.put(AliasesKeys.DSCOLLECTION, collection);

            try {
                Vector<Object> v = new Vector<Object>();
                for (Enumeration<Object> e = aliases.elements(); e
                        .hasMoreElements();) {
                    Object o = e.nextElement();
                    if ((o instanceof core.rml.dbi.Datastore) && (o != collection)) {
                        v.addElement(o);
                    }
                }
                core.rml.dbi.Datastore[] dss = new core.rml.dbi.Datastore[v.size()];
                v.copyInto(dss);
                collection.setDatastores(dss);
            }
            catch (Exception ex) {
                log.error("Shit happens", ex);
            }

            if (resetIt) {
                resetIt = false;
                reset();
            }
            else {
            	final RmlObject[] o = children.getChildren();
            	for(RmlObject child : o){
            		if(child instanceof VisualRmlObject && child instanceof RetrieveableAPI){
            			((RetrieveableAPI) child).retrieve();
            		}
            	}
            }
        }
        finally {
            clearInfo();
        }
	}

	public void setProgress(int progress) {
		worker.setProgress(progress);
	}

	@Override
	public boolean addChildrenAutomaticly() {
		return false;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(PROGERSS)){
			container.showProgress((Integer) evt.getNewValue());
		}
	}

    @Override
    public String getPath() {
        return mypath;
    }

    @Override
    public int actSave() {
        return processAction(ACT_SAVE);
    }

    @Override
    public int actCancel() {
        return processAction(ACT_CANCEL);
    }

    @Override
    public int actDoc() {
        return processAction(ACT_DOK);
    }

    @Override
    public int actHok() {
        return processAction(ACT_HOK);
    }

    @Override
    public int actNew() {
        return processAction(ACT_NEW);
    }

    @Override
    public void doAction(String action) throws Exception {
        doAction(action, null);
    }

    @Override
    public String getServerParth() {
        return SessionManager.getIntance().getCurrentSession().getProperty(
                PropertyConstants.RML_SERVER);
    }

    @Override
    public Object getObject(String alias) {
        return aliases.get(alias.toUpperCase());
    }

    @Override
    public Proper loadRml(String file) throws Exception {
        char[] text = Loader.getInstanceRml().loadByName_chars(file, true);
        return parser.createProper(text, null);
    }

    @Override
    public Proper parse(String rml) throws Exception {
        return parser.createProper(rml.toCharArray(), null);
    }

    @Override
    public File openFileDlg() {
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(container);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
               return chooser.getSelectedFile().getAbsoluteFile();
        }
        return null;
    }

    @Override
    public String readFile(String file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] b = new byte[fis.available()];
        fis.read(b);
        fis.close();
        return new String(b);
    }

    @Override
    public void writeToFile(String file, byte[] value) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(value);
        fos.close();
    }

    @Override
    public void shell(String cmd) throws IOException {
        Runtime rt = Runtime.getRuntime();
        rt.exec(cmd);
    }

    @Override
    public int shellWait(String cmd) throws IOException, InterruptedException {
        Runtime rt = Runtime.getRuntime();
        Process p = rt.exec(cmd);
        p.waitFor();
        return p.exitValue();
    }
}
