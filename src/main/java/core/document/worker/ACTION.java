/*
 * File: ACTION.java
 * 
 * Created: Thu Apr 8 16:59:07 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package core.document.worker;

import java.text.DateFormat;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import publicapi.RetrieveableAPI;
import action.api.ScriptApi;
import core.document.Document;
import core.document.NotifyInterface;
import core.rml.dbi.Datastore;
import core.rml.dbi.ErrorReader;

public class ACTION {
    private static final Logger       log         = Logger
                                                          .getLogger(ACTION.class);

    private Document                  actionOwner = null;

    private String                    Action;

    private Hashtable<String, Object> Aliases;

    private NotifyInterface           notifyI;

    class Action implements Actioner {
        Command                   cmd = null;

        Hashtable<String, Object> aliases;

        NotifyInterface           ni;

        public Action(String s, Hashtable<String, Object> aliases,
                NotifyInterface ni) throws Exception {
            this.ni = ni;
            Command end = null;
            cmd = null;
            this.aliases = aliases;
            StringTokenizer st = new StringTokenizer(s, ";");
            while (st.hasMoreTokens()) {
                String str = st.nextToken();
                Command cd = new Command(str, aliases, actionOwner);
                if (cmd == null) {
                    end = cmd = cd;
                }
                else {
                    end.next = cd;
                    end = cd;
                }
            }
        }

        public void doAction() throws Exception {
            if (cmd == null) {
                if (ni != null) {
                    ni.notifyIt();
                }
                return;
            }
            Command cd = cmd;
            cmd = cmd.next;
            cd.doCmd(aliases, this);
        }

        public void notifyActioner() {
            try {
                doAction();
            }
            catch (Exception e) {
                log.error("Shit happens!!!", e);
            }
        }
    }

    class Command {
        int              cmd    = -1;

        boolean          newf   = false;

        boolean          overf  = false;

        String           target = "";

        Object[]         arg    = null;

        String           args   = "";

        public Command   next   = null;

        private Document doc    = null;

        public Command(String s, Hashtable<String, Object> aliases, Document d)
                throws Exception {
            doc = d;
            StringTokenizer st = new StringTokenizer(s);
            if (st.countTokens() == 0) {
                return;
            }
            String str = st.nextToken().trim().toUpperCase();
            if (str.compareTo("RETRIEVE") == 0) {
                cmd = RETRIEVE;
            }
            else if (str.compareTo("OPEN") == 0) {
                cmd = OPEN;
            }
            else if (str.compareTo("OPENNEW") == 0) {
                cmd = OPEN;
                newf = true;
            }
            else if (str.compareTo("OPENOVER") == 0) {
                cmd = OPEN;
                overf = true;
                /*
                 * }else if ( str.compareTo("OPENNEWOVER") == 0 ){ cdm = OPEN;
                 * newf = true; overf = true;
                 */
            }
            else if (str.compareTo("CREATE") == 0) {
                cmd = CREATE;
            }
            else if (str.compareTo("CREATENEW") == 0) {
                cmd = CREATE;
                newf = true;
            }
            else if (str.compareTo("CREATEOVER") == 0) {
                cmd = CREATE;
                overf = true;
            }
            else if (str.compareTo("EXECUTE") == 0) {
                cmd = EXECUTE;
            }
            else if (str.compareTo("EXECEXPR") == 0) {
                cmd = EXECEXPR;
            }
            else {
                throw new Exception("~core.document.ACTION$Command::<init>\n\t"
                        + str + " unknown command");
            }
            target = st.nextToken().trim();
            if (st.hasMoreTokens()) // if arguments are present
            {
                args = st.nextToken("");
                StringTokenizer st1 = new StringTokenizer(args, ",");
                int n = st1.countTokens();
                arg = new Object[n];
                try {
                    for (int i = 0; st1.hasMoreTokens(); ++i) {
                        String a = st1.nextToken().trim();
                        log.debug("--------\n" + a);
                        if (a.length() == 0) {
                            arg[i] = a;
                        }
                        else if (a.charAt(0) == '&') {
                            arg[i] = aliases.get(a.substring(1));
                            log.debug("add object " + arg[i] + " in ARGUMENTS."
                                    + i);
                        }
                        else {
                            try {
                                a.toUpperCase();
                                arg[i] = Double.valueOf(a);
                            }
                            catch (Exception e) {
                                // log.error("Shit happens!!!", e);
                                try {
                                    arg[i] = DateFormat.getDateInstance()
                                            .parse(a);
                                }
                                catch (Exception e1) {
                                    // log.error("Shit happens!!!", e1);
                                    arg[i] = a;
                                }
                            }
                        }
                    }
                }
                catch (Exception e) {
                    log.error("Shit happens", e);
                }
            }
        }

        public void doCmd(Hashtable<String, Object> aliases, Actioner actor)
                throws Exception {
            if (cmd == RETRIEVE) {
                Object tg = aliases.get(target.toUpperCase());
                if (tg instanceof RetrieveableAPI) {
                    ((RetrieveableAPI) tg).retrieve();
                }
                else if (tg instanceof Datastore) {
                    ((Datastore) tg).retrieve();
                }
                actor.notifyActioner();
            }
            else if (cmd == EXECUTE) {
                new core.rml.dbi.EXECUTOR(actionOwner).execute(arg, aliases);
                actor.notifyActioner();
            }
            else if ((cmd == OPEN) || (cmd == CREATE)) {
                if (cmd == CREATE) {
                    Document.resetIt = true;
                }
                if (newf) {
                    actionOwner.callDocumentNewWindow(target, arg, actor);
                }
                else {
                    actionOwner.callDocumentSomeWindow(target, arg, actor);
                }
            }
            else if (cmd == EXECEXPR) {
                String expr = null;
                try {
                    expr = (String) arg[0];
                    doc.executeScript(expr, false);
//                    Calc c = new Calc(expr);
//                    c.eval(aliases);
                }
                catch (Exception e) {
                    log.error("Exception in ACTION.doCmd():", e);
                    return;
                }
                actor.notifyActioner();
            }
            else if (cmd == -1) {
                return;
            }
            else {
                throw new Exception("~core.document.ACTION$Command::doCmd " + cmd);
            }
        }
    }

    static final int RETRIEVE = 0;

    static final int CREATE   = 1;

    static final int OPEN     = 2;

    static final int EXECUTE  = 3;

    static final int EXECEXPR = 4;

    public ACTION(Document action_owner) {
        actionOwner = action_owner;
    }

    public void prepareAction(String action, Hashtable<String, Object> aliases,
            NotifyInterface ni) {
        Action = action;
        Aliases = aliases;
        notifyI = ni;
    }

    private void action(String action, Hashtable<String, Object> aliases,
            NotifyInterface ni) throws Exception {
        try {
            
        	action = ScriptApi.macro(action, aliases);
            
            Action a = new Action(action, aliases, ni);
            a.doAction();
        }
        catch (Exception e) {
            log.error("Shit happens", e);
            if (e instanceof java.sql.SQLException) {
                ErrorReader.getInstance().addMessage(e.getMessage());
            }
            throw e;
        }
    }

    public void runAction() throws Exception {
        action(Action, Aliases, notifyI);
    }
    
}
