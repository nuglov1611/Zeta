/*
 * File: Nafigator.java
 * 
 * Created: Wed Mar 31 15:20:31 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * Author: Alexey Chen
 */

package core.browser;

import core.browser.listener.NavigatorActionListener;
import core.connection.BadPasswordException;
import core.connection.ConnectException;
import core.connection.DBMSConnection;
import core.document.Document;
import core.document.exception.LoadDocumentException;
import core.parser.Proper;
import loader.ZetaProperties;
import loader.ZetaUtility;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXFrame;
import properties.PropertyConstants;
import properties.Session;
import properties.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

/*
 * class Nafigator for Nafigation on Document server
 */
public class Workspace extends DocumentContainer implements Runnable {

    private static final Logger log = Logger
            .getLogger(Workspace.class);

    private WorkspaceManager nc = null;

    private NavigatorActionListener navigatorActionListener;

    private String startDocument = null;

    private JXFrame window = null;

    private JXBusyLabel bl = null;

    public static final String CREATE_WORKSPACE = "1";

    public static final String DELETE_WORKSPACE = "2";

    public static final String SYSTEM_EXIT = "3";

    public static final String SYSTEM_LOGOUT = "4";

    public static final String DOCUMENT_HELP = "6";

    public static final String ABOUT_PROGRAM = "7";

    public static final String CONTENT_HELP = "5";


    @Override
    public void closeDocumentWindow() {
        DBMSConnection.closeConnection(this);
        nc.removeWorkspace(this);
    }

    private Workspace() throws LoadDocumentException, ConnectException, BadPasswordException {
        super();
        navigatorActionListener = new NavigatorActionListener(this);

        window = new JXFrame(ZetaUtility.pr(ZetaProperties.TITLE_NAFIGATOR, "Nafigator"));

        setWorkSpace(this);

        DBMSConnection.getConnection(this);

        WorkspaceManager.setCurWorkspace(this);

        if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            window.setUndecorated(true);
            window.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
        }
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        // window.setJMenuBar(createMenuBar());

        window.addWindowListener(new WL());
        window.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(ZetaProperties.IMAGE_ICON)));
        int width = 600;
        int height = 400;
        int x = -1;
        int y = -1;
        try {
            width = Integer.valueOf(ZetaUtility.pr(ZetaProperties.NAFIGATOR_WIDTH, "400"));
        } catch (Exception e) {
            log.error("Shit happens", e);
        }
        try {
            height = Integer.valueOf(ZetaUtility.pr(ZetaProperties.NAFIGATOR_HEIGHT, "400"));
        } catch (Exception e) {
            log.error("Shit happens", e);
        }

        try {
            String _x = ZetaUtility.pr(ZetaProperties.NAFIGATOR_X, "@");
            if (!_x.equals("@")) {
                x = Integer.valueOf(_x);
            }
        } catch (Exception e) {
            log.error("Shit happens", e);
        }
        try {
            String _y = ZetaUtility.pr(ZetaProperties.NAFIGATOR_Y, "@");
            if (!_y.equals("@")) {
                y = Integer.valueOf(_y);
            }
        } catch (Exception e) {
            log.error("Shit happens", e);
        }
        startDocument = SessionManager.getIntance().getCurrentSession().
                getProperty(PropertyConstants.RML_START_DOC);

        if (ZetaUtility.pr(ZetaProperties.NAFIGATOR_MAXIMIZE, "YES").toUpperCase()
                .equals("YES")) {
            window.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        window.setSize(width, height);

        window.getContentPane().setLayout(new GridLayout(1, 1));
        window.getContentPane().add(this);

        Dimension dm = getToolkit().getScreenSize();
        if ((x == -1) && (y == -1)) {
            window.setLocation((dm.width - width) / 2, (dm.height - height) / 2);
        } else {
            window.setLocation(x, y);
        }
    }

    public Workspace(WorkspaceManager nc) throws ConnectException, LoadDocumentException, BadPasswordException {
        this();
        this.nc = nc;
        loadDocument(startDocument, null, false);
    }

    public JMenuBar createMenuBar(ArrayList<JMenu> documentMenu) {
        JMenuBar ret = new JMenuBar();
        String title = "Работа";
        String s1 = "Создать РабочееПространство";
        String s2 = "Удалить РабочееПространство";
        String s3 = "Выйти из Системы";
        String s4 = "Войти в Систему под другим именем";

        JMenuItem i1, i2, i3, i4;
        i1 = new JMenuItem(s1);
        i2 = new JMenuItem(s2);
        i3 = new JMenuItem(s3);
        i4 = new JMenuItem(s4);
        i1.setActionCommand(CREATE_WORKSPACE);
        i1.addActionListener(navigatorActionListener);
        i2.setActionCommand(DELETE_WORKSPACE);
        i2.addActionListener(navigatorActionListener);
        i3.setActionCommand(SYSTEM_EXIT);
        i3.addActionListener(navigatorActionListener);
        i4.setActionCommand(SYSTEM_LOGOUT);
        i4.addActionListener(navigatorActionListener);
        JMenu m = new JMenu(title);

        m.add(i1);
        m.add(i2);
        m.add(i4);
        m.addSeparator();
        m.add(i3);
        ret.add(m);

        if (documentMenu != null) {
            for (JMenu menu : documentMenu)
                ret.add(menu);
        }

        title = "Помощь";
        m = new JMenu(title);
        s1 = "Содержание";
        s2 = "О документе";
        s3 = "О программе";
        i1 = new JMenuItem(s1);
        i2 = new JMenuItem(s2);
        i3 = new JMenuItem(s3);
        i1.setActionCommand(CONTENT_HELP);
        i1.addActionListener(navigatorActionListener);
        i2.setActionCommand(DOCUMENT_HELP);
        i2.addActionListener(navigatorActionListener);
        i3.setActionCommand(ABOUT_PROGRAM);
        i3.addActionListener(navigatorActionListener);
        m.add(i1);
        m.add(i2);
        m.addSeparator();
        m.add(i3);
        ret.add(m);
        return ret;
    }

    class WL extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent e) {
            closeDocument();
        }

        @Override
        public void windowActivated(WindowEvent e) {
            if (nc != null) {
                WorkspaceManager.setCurWorkspace(Workspace.this);
            }
            if (getCurDoc() != null) {
                getCurDoc().activatedWindowRequestors(e);
            }
        }
    }

    class FL extends FocusAdapter {

        @Override
        public void focusGained(FocusEvent e) {
            if (nc != null) {
                WorkspaceManager.setCurWorkspace(Workspace.this);
            }
        }
    }

    @Override
    protected void setTitle(String title) {
        final Session ses = SessionManager.getIntance().getCurrentSession();
        final String user = ses.getProperty(PropertyConstants.DB_USERNAME);
        final String ses_name = ses.getProperty(PropertyConstants.NAME);
        window.setTitle(title + "                    " + user + "@" + ses_name);
    }


    @Override
    protected void showDocumentInNewWindow(String doc_name, Object[] args) throws LoadDocumentException {
        ModalDocumentDialog dg = new ModalDocumentDialog(this, window);

        Document dc = Document.getDocumentFromHash(doc_name, getCurDoc(), args, dg);
        if (dc == null) {
            dc = new Document(doc_name, args, getCurDoc(), dg);
        }


        int width, height;
        try {
            width = ((Integer) ((Proper) dc.getAliases().get("###propers###"))
                    .get("DOCUMENT_WIDTH")).intValue();
        } catch (Exception e) {
            width = 600;
            log.error("Shit happens", e);
        }
        try {
            height = ((Integer) ((Proper) dc.getAliases().get("###propers###"))
                    .get("DOCUMENT_HEIGHT")).intValue();
        } catch (Exception e) {
            height = 400;
            log.error("Shit happens", e);
        }
        dg.setWindowSize(width, height);

        dg.loadDocument(dc);
    }

    public JXFrame getFrame() {
        return window;
    }

    public WorkspaceManager getNavigatorController() {
        return nc;
    }

    @Override
    protected void showDocumentWindow() {

        if (SwingUtilities.isEventDispatchThread()) {
            if (!window.isVisible()) {
                window.setVisible(true);
            }
        } else {
            try {
                SwingUtilities.invokeAndWait(this);
            } catch (Exception e) {
                log.error("!", e);
                SwingUtilities.invokeLater(this);
            }
        }
    }

    @Override
    public void run() {
        if (window != null && !window.isVisible()) {
            window.setVisible(true);
        }
    }

    public synchronized void lockFrame() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                showInfo("Данная операция может занять значительное время");
                //lockPanel.setSize(window.getSize());

                //window.setGlassPane(lockPanel);
                window.setWaitCursorVisible(true);
                window.getContentPane().setEnabled(false);
//		    	window.setWaiting(true);
//		    	window.setEnabled(false);
            }
        });
        //window.setIdle(true);

        //busyDlg.setVisible(true);

        //window.setWaitPaneVisible(true);
        //bl.setBusy(true);

    }

    public synchronized void unlockFrame() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                clearInfo();
                //setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                window.setWaitCursorVisible(false);
                window.getContentPane().setEnabled(false);
//		    	window.setWaiting(false);
//		    	window.setEnabled(true);
            }
        });
    }

    @Override
    public void setMenuBar(JMenuBar menu) {
        window.setJMenuBar(menu);
        window.getJMenuBar().repaint();
    }

}
