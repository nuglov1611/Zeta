/*
 * File: Boot.java
 * 
 * Created: Mon Mar 22 10:43:36 1999
 * 
 * Copyright(c) by Alexey Chen
 */

package boot;

import java.awt.Cursor;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.TimeZone;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import loader.NullOutputStream;
import loader.ZetaProperties;
import loader.ZetaUtility;
import loader.log.ZetaAppender;

import org.apache.log4j.Logger;

import properties.PropertyConstants;
import properties.PropertyManager;
import properties.Session;
import properties.SessionManager;
import properties.SettingsDialog;
import core.browser.WorkspaceManager;
import core.rml.dbi.ErrorReader;

/*
 * Startup class
 */

public class Boot extends MainWindow implements ActionListener, KeyListener,
        CaretListener, FocusListener {

    /**
     *
     */
    private static final Logger log     = Logger.getLogger(Boot.class);

    public static boolean       nologon = false;

    static {
        try {
            // Устанавливаем системный стиль полностью
            String systemLAFName = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(systemLAFName);
        }
        catch (ClassNotFoundException e) {
            setWindowsLookAndFeel();
        }
        catch (InstantiationException e) {
            setWindowsLookAndFeel();
        }
        catch (IllegalAccessException e) {
            setWindowsLookAndFeel();
        }
        catch (UnsupportedLookAndFeelException e) {
            log.error("Shit happens", e);
        }
    }

    private static void setWindowsLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (UnsupportedLookAndFeelException e) {
            log.error("!", e);
        } catch (ClassNotFoundException e) {
            log.error("!", e);
		} catch (InstantiationException e) {
            log.error("!", e);
		} catch (IllegalAccessException e) {
            log.error("!", e);
		}
    }

    public static Boot instance;

    public static Boot getInstance() {
        if (instance == null) {
            instance = new Boot();
        }
        return instance;
    }

    /**
     * Показывает стартовое окно
     *
     * @param login - заходим мы в систему, или это событие выхода из нее
     */
    public void showStartup(boolean login) {
        boolean autoLogin = PropertyManager.getIntance().getProperty(
                PropertyConstants.LOGIN_AUTO).equalsIgnoreCase("on");
        if (login && autoLogin && !nologon) {
            String curSessionId = PropertyManager.getIntance().getProperty(
                    PropertyConstants.DEFAULT_LOGIN_SESSION);
            Session newCurrentSession = SessionManager.getIntance()
                    .getSessionById(curSessionId);
            SessionManager.getIntance().setCurrentSession(newCurrentSession);
            showNavigator();
        } else {
            updateSessions(login);
            setVisible(true);
        }
    }

    private Boot() {
        String s = ZetaUtility.pr(ZetaProperties.SYSTEM_OUT, "NULL").trim()
                .toUpperCase();
        if (s.equals("NULL")) {
            PrintStream ps = new PrintStream(new NullOutputStream());
            System.setErr(ps);
            System.setOut(ps);
        } else if (s.equals("OUT")) {
            System.setErr(System.out);
        }
        setLocationRelativeTo(null);
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

        if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            this.setUndecorated(true);
            this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
        }

        txtName.addKeyListener(this);
        txtName.addCaretListener(this);
        txtName.addFocusListener(this);
        txtPassword.addKeyListener(this);
        txtPassword.addCaretListener(this);
        txtPassword.addFocusListener(this);
        bEnter.addActionListener(this);
        if(ZetaProperties.DEMO || ZetaProperties.ONLOAD) {
            bProperties.setEnabled(false);
        } else {
            bProperties.addActionListener(this);
        }
        cbSessions.addActionListener(this);

        pack();
    }

    protected void showNavigator() {
        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            new Thread(ErrorReader.getInstance()).start();
            WorkspaceManager manager = WorkspaceManager.getInstance();
            if(manager.getCurWorkspace() == null){
            	manager.newWorkspace();
            }
            setVisible(false);
        }
        catch (SQLException e) {
 //           ErrorReader.getInstance().closeErrorReader();
            txtPassword.setText("");
            log.error("Shit happens", e);
            ZetaUtility.oracleMessage(e, this);
            //if something going wrong
            setVisible(true);
        }
        catch (Exception e) {
            log.error("Shit happens", e);
//            ErrorReader.getInstance().closeErrorReader();
            ZetaUtility.message(ZetaUtility.pr(ZetaProperties.MSG_UNKNOWNERROR),
                    ZetaProperties.MESSAGE_ERROR, this);
            //if something going wrong
            setVisible(true);
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    private void validInputData() {
        if (txtName.getText().length() > 0
                && txtPassword.getPassword().length > 0
                && cbSessions.getSelectedItem() != null) {
            bEnter.setEnabled(true);
        } else {
            bEnter.setEnabled(false);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == bEnter) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Session session = SessionManager.getIntance().getCurrentSession();
            if (PropertyConstants.DEFAULT_TEXT.equals(session
                    .getProperty(PropertyConstants.DB_USERNAME))) {
                session.setProperty(PropertyConstants.DB_USERNAME, txtName
                        .getText());
                SessionManager.getIntance().save(session);
            } else {
                session.setProperty(PropertyConstants.DB_USERNAME, txtName
                        .getText());
            }
            session.setProperty(PropertyConstants.DB_PASSWORD, String
                    .valueOf(txtPassword.getPassword()));
            showNavigator();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } else if (e.getSource() == bProperties) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            SettingsDialog.getInstance().show(this);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } else if (e.getSource() == cbSessions) {
            Session newCurrentSession = (Session) cbSessions.getSelectedItem();
            SessionManager.getIntance().setCurrentSession(newCurrentSession);
            txtName.setText(newCurrentSession
                    .getProperty(PropertyConstants.DB_USERNAME));
            txtPassword.setText(newCurrentSession
                    .getProperty(PropertyConstants.DB_PASSWORD));
            validInputData();
        }
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public void caretUpdate(CaretEvent e) {
        validInputData();
    }

    public void focusGained(FocusEvent e) {
        if (e.getSource() == txtName) {
            txtName.selectAll();
        } else if (e.getSource() == txtPassword) {
            txtPassword.selectAll();
        }
    }

    public void focusLost(FocusEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == Event.ENTER) {
            if (e.getSource() == txtName) {
                txtPassword.requestFocus();
            } else if (e.getSource() == txtPassword) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                Session session = SessionManager.getIntance()
                        .getCurrentSession();
                if (PropertyConstants.DEFAULT_TEXT.equals(session
                        .getProperty(PropertyConstants.DB_USERNAME))) {
                    session.setProperty(PropertyConstants.DB_USERNAME, txtName
                            .getText());
                    SessionManager.getIntance().save(session);
                } else {
                    session.setProperty(PropertyConstants.DB_USERNAME, txtName
                            .getText());
                }
                session.setProperty(PropertyConstants.DB_PASSWORD, String
                        .valueOf(txtPassword.getPassword()));
                showNavigator();
            }
        } else if (e.getKeyCode() == Event.ESCAPE) {
            setVisible(false);
            System.exit(0);
        }
    }

    public void updateSessions(boolean login) {
        Collection<Session> sessions = SessionManager.getIntance()
                .getSessions();
        cbSessions.setModel(new DefaultComboBoxModel(sessions.toArray()));
        if (sessions.size() > 0) {
            Session currentSession = SessionManager.getIntance()
                    .getCurrentSession();
            if (currentSession == null && sessions.iterator().hasNext()) {
                currentSession = sessions.iterator().next();
                SessionManager.getIntance().setCurrentSession(currentSession);
            }
            cbSessions.setSelectedItem(currentSession);
            txtName.setText(currentSession
                    .getProperty(PropertyConstants.DB_USERNAME));
            if (login) {
                txtPassword.setText(currentSession
                        .getProperty(PropertyConstants.DB_PASSWORD));
            } else {
                txtPassword.setText(PropertyConstants.DEFAULT_TEXT);
            }
        } else {
            txtName.setText(PropertyConstants.DEFAULT_TEXT);
            txtPassword.setText(PropertyConstants.DEFAULT_TEXT);
        }
        validInputData();
    }

    public static void main(String args[]) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-nologon"))
                Boot.nologon = true;
            else if (args[i].equalsIgnoreCase("-ws")) {
                File f = new File(System.getProperty("user.home")
                        + System.getProperty("file.separator") + "Zeta");
                f.mkdir();
                ZetaProperties.HOME_PATH = f.getAbsolutePath()
                        + System.getProperty("file.separator");
                Logger.getRootLogger().removeAllAppenders();
                Logger.getRootLogger().addAppender(new ZetaAppender());
            } else if (args[i].equalsIgnoreCase("-demo")) {
                ZetaProperties.DEMO = true;
            } else if (args[i].toLowerCase().contains("-prop")) {
                if (i + 1 < args.length) {
                ZetaProperties.ONLOAD = true;
                String file = args[++i];
                PropertyManager.getIntance(file);
                } else if (args[i].length() > "-prop".length()) {
                    String file = args[i].substring("-prop".length(), args[i].length()).trim();
                    PropertyManager.getIntance(file);
                } else {
                    log.error("Can't find link for -prop argument");
                }
            } else {
                log.warn("Unknown argument " + args[i]);
            }
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Boot.getInstance().showStartup(true);
            }
        });
    }
}
