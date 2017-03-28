package core.browser;

import java.util.Vector;

import loader.Loader;
import loader.ZetaProperties;
import loader.ZetaUtility;

import org.apache.log4j.Logger;

import properties.PropertyConstants;
import properties.PropertyManager;
import boot.Boot;
import core.connection.BadPasswordException;
import core.connection.ConnectException;
import core.connection.DBMSConnection;
import core.document.exception.LoadDocumentException;
import core.rml.dbi.ErrorReader;

public class WorkspaceManager {
    private static final Logger log          = Logger
                                                     .getLogger(WorkspaceManager.class);

    protected static Vector<Workspace> workspaces   = new Vector<Workspace>();

    private static Workspace    curWorkspace = null;
    
    private static WorkspaceManager instance = null;

    private WorkspaceManager() throws ConnectException,
            LoadDocumentException, BadPasswordException {
        newWorkspace();
    }

    public synchronized static WorkspaceManager getInstance() throws ConnectException, LoadDocumentException, BadPasswordException{
    	if(instance == null){
    		instance = new WorkspaceManager();
    	}
    	return instance;
    }
    
    public static synchronized Workspace getCurWorkspace() {
        return curWorkspace;
    }

    public static synchronized void setCurWorkspace(Workspace n) {
        curWorkspace = n;
    }

    public void newWorkspace() throws ConnectException, LoadDocumentException,
            BadPasswordException {
        Workspace nw = new Workspace(this);
        workspaces.addElement(nw);
    }

    public void removeWorkspace(Workspace ws) {
        if (ws == null) {
            return;
        }
        if (workspaces.size() == 1) {
            exit();
        }
        else {
            workspaces.removeElement(ws);
            ws.getFrame().dispose();
            System.gc();
        }
    }

    public void logout() throws ConnectException, LoadDocumentException,
            BadPasswordException {
        ErrorReader.getInstance().closeErrorReader();
        DBMSConnection.closeAll();
        Loader.Reset();
        for (int i = 0; i < workspaces.size(); i++) {
            Workspace f = workspaces.elementAt(i);
            f.getFrame().dispose();
        }
        workspaces.removeAllElements();
        curWorkspace = null;
        Boot.getInstance().showStartup(false);
    }

    public void exit() {
        if (ZetaUtility.sure(ZetaUtility.pr(ZetaProperties.MSG_RUSUREEXIT,
                "Exit ?!, Are You Sure?"), ZetaProperties.MESSAGE_SURE)) {
            ErrorReader.getInstance().closeErrorReader();
            DBMSConnection.closeAll();
            Loader.Reset();
            if (PropertyManager.getIntance().getProperty(
                    PropertyConstants.LOGIN_AFTER_EXIT).toUpperCase().equals(
                    "ON")) {
                for (int i = 0; i < workspaces.size(); i++) {
                    Workspace f = workspaces.elementAt(i);
                    f.getFrame().dispose();
                }
                curWorkspace = null;
                instance = null;
                Boot.getInstance().showStartup(false);
            }
            else {
                System.exit(0);
            }
        }
    }
}
