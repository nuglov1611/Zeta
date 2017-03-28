package core.browser.listener;

import core.browser.AboutDialog;
import core.browser.Workspace;
import core.connection.BadPasswordException;
import core.connection.ConnectException;
import core.document.exception.LoadDocumentException;
import loader.ZetaProperties;
import loader.ZetaUtility;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NavigatorActionListener implements ActionListener {

    private static final Logger log = Logger.getLogger(NavigatorActionListener.class);

    private Workspace parent;

    public NavigatorActionListener(Workspace parent) {
        this.parent = parent;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(Workspace.CREATE_WORKSPACE)) {
            try {
                parent.getNavigatorController().newWorkspace();
            } catch (ConnectException e1) {
                log.error("Shit happens", e1);
                ZetaUtility.message(ZetaUtility.pr(ZetaProperties.MSG_CANTCONNECTTODBS));
            } catch (LoadDocumentException e1) {
                log.error("Shit happens", e1);
                ZetaUtility.message(ZetaUtility.pr(ZetaProperties.MSG_CANTLOADDOCUMENT));
            } catch (BadPasswordException e1) {
                log.error("Shit happens", e1);
                ZetaUtility.message(ZetaUtility.pr(ZetaProperties.MSG_BADUSERORPASSWORD));
            }
        } else if (e.getActionCommand().equals(Workspace.DELETE_WORKSPACE)) {
            parent.getNavigatorController().removeWorkspace(parent);
        } else if (e.getActionCommand().equals(Workspace.SYSTEM_EXIT)) {
            parent.getNavigatorController().exit();
        } else if (e.getActionCommand().equals(Workspace.SYSTEM_LOGOUT)) {
            try {
                parent.getNavigatorController().logout();
            } catch (ConnectException e1) {
                log.error("Shit happens", e1);
                ZetaUtility.message(ZetaUtility.pr(ZetaProperties.MSG_CANTCONNECTTODBS));
            } catch (LoadDocumentException e1) {
                log.error("Shit happens", e1);
                ZetaUtility.message(ZetaUtility.pr(ZetaProperties.MSG_CANTLOADDOCUMENT));
            } catch (BadPasswordException e1) {
                log.error("Shit happens", e1);
                ZetaUtility.message(ZetaUtility.pr(ZetaProperties.MSG_BADUSERORPASSWORD));
            }
        } else if (e.getActionCommand().equals(Workspace.CONTENT_HELP)) {
            String browser = ZetaUtility.pr(ZetaProperties.HELP_BROWSER, "");
            String page = ZetaUtility.pr(ZetaProperties.HELP_START_PAGE, "");

            if (!"".equals(browser) && !"".equals(page)) {
                try {
                    Runtime.getRuntime().exec(new String[]{browser, page});
                } catch (Exception ex) {
                    log.error("Shit happens", ex);
                }
            }
        } else if (e.getActionCommand().equals(Workspace.DOCUMENT_HELP)) {
            String browser = ZetaUtility.pr(ZetaProperties.HELP_BROWSER, "");
            String page = parent.getCurDoc().getPage();
            if (browser.equals("") || page == null || page.equals("")) {
                return;
            }
            try {
                Runtime.getRuntime().exec(new String[]{browser, page});
            } catch (Exception ex) {
                log.error("Shit happens", ex);
            }
        } else if (e.getActionCommand().equals(Workspace.ABOUT_PROGRAM)) {
            final AboutDialog ad = new AboutDialog("О программе", parent.getFrame());
            if (SwingUtilities.isEventDispatchThread())
                ad.setVisible(true);
            else {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        ad.setVisible(true);
                    }
                });
            }
        }
    }
}
