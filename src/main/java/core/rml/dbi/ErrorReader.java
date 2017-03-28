package core.rml.dbi;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import loader.ZetaProperties;

import org.apache.log4j.Logger;

import views.MessageFactory;
import core.connection.BadPasswordException;
import core.connection.ConnectException;
import core.connection.DBMSConnection;

public class ErrorReader extends Thread {
private static final Logger log          = Logger
                                                     .getLogger(ErrorReader.class);

    private static ErrorReader  instance     = null;

    private Connection          conn;

//    private Statement           stmt;
//
    private CallableStatement   cs           = null;

    private MsgDebug            fr           = new MsgDebug("Сообщения");

    private String              pipe         = "";

    private boolean             success      = false;

    private boolean             fullreset    = false;

    private volatile boolean    circle       = true;

    private volatile boolean    initializing = false;

    protected ErrorReader() throws BadPasswordException, ConnectException {
        circle = true;

        conn = DBMSConnection.getConnection(this);

        fr.setSize(300, 300);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        fr.setLocation((d.width - 300) / 2, (d.height - 300) / 2);
    }

    public static ErrorReader getInstance() {
        if (instance == null) {
            try {
                instance = new ErrorReader();
            }
            catch (SQLException e) {
                log.error("Shit happens!", e);
            }
        }
        return instance;
    }

    private void initPipe() throws SQLException {
        try {
            initializing = true;
            if (fullreset) {
                // addMessage("doing full reset....");
                cs.close();
                conn = DBMSConnection.reconnect(this);
            }
            if (conn != null) {
            try {
                cs = conn
                        .prepareCall("begin select count(*) into :1 from v$db_pipes where name='ZETA$'; end;");
                cs.registerOutParameter(1, java.sql.Types.INTEGER);
                cs.execute();
                int n = cs.getInt(1);
                if (n > 0) {
                    Thread.sleep(15000);
                }

                executeQuery("begin deb_lib.init('zeta$'); end;");
                executeQuery("begin deb_lib.write('zeta$presence', user, 5); end;");
                try {
                    cs = conn
                            .prepareCall("begin :1 := deb_lib.read('zeta$', 5); end;");
                    cs.registerOutParameter(1, java.sql.Types.VARCHAR);
                    cs.execute();
                    String s = cs.getString(1).trim();
                    if (s != null
                            && s.equals("yes")
                            && !MessageFactory.getInstance().showMessage(
                                    ZetaProperties.MSG_USERALREADYEXIST,
                                    MessageFactory.Type.CONFIRMATION)) {
                        executeQuery("begin deb_lib.close('zeta$'); end;");
                        cs.close();
                        System.exit(0);
                    }
                }
                catch (Exception e) {
                    //log.error("Shit happens", e);
                }
                executeQuery("begin deb_lib.close('zeta$'); end;");

            }
            catch (Exception e) {
                //log.error("Shit happens", e);
            }

            executeQuery("begin deb_lib.close(); end;");
            if (ZetaProperties.dstore_debug > 0) {
                log.debug("pipe removed...");
            }
            cs = conn.prepareCall("begin :1 := deb_lib.init(); end;");
            cs.registerOutParameter(1, java.sql.Types.VARCHAR);
            cs.executeUpdate();
            pipe = cs.getString(1);
            if (ZetaProperties.dstore_debug > 0) {
                log.debug("Pipe initialized PipeName=" + pipe);
            }
            cs = conn.prepareCall("begin :1 := deb_lib.read('"+pipe+"', "+1+"); end;");
            cs.registerOutParameter(1, java.sql.Types.VARCHAR);
            success = true;
        }
        }
        catch (SQLException e) {
            log.error("Error initializing pipe:", e); // не смогли
            // инициализировать
            // канал для отладки
            
            success = false;
            if(e.getMessage().toUpperCase().indexOf("ORA-06550")>-1)
            	throw e;
        }
        finally {
            initializing = false;
        }

    }

    /**
     * интерфейс к базе данных для выполнения SQL запроса
     */
    private void executeQuery(String query) throws SQLException {
        Statement stmt = null;
        try{
            stmt = conn.createStatement();
            stmt.execute(query);
        }finally{
            if (stmt != null) {
            stmt.close();
        }
    }
    }

    public void addMessage(String msg) {
        fr.addMessage(msg);
        fr.setVisible(true);
    }

    /**
     * интерфейс к базе данных для выполнения SQL запроса
     */
    @Override
    public void run() {
        try {
        initPipe();
		} catch (SQLException e2) {
			return;
		}
        circle = true;
        while (circle) {
            try {
                cs.execute();
                String message = cs.getString(1);
                if (message.trim().startsWith("zeta$")) {
                    String command = message.trim().substring(5);
                    if (command.equals("presence")) {
                        executeQuery("begin deb_lib.write('yes', 'zeta$', 5); end;");
                    }
                    continue;
                }
                if (ZetaProperties.dstore_debug > 1) {
                    log.debug("Recieved message:" + message);
                }
                fr.addMessage(message + "\n");
                fr.setVisible(true);
            }
            catch (SQLException e) {
                try {
                    sleep(1000);
                }
                catch (Exception e1) {
                    log.error("Shit happens", e1);
                }

                if (e.getErrorCode() != 20117) {
                    log.debug("Error recive message" + e);
                    success = false;
                    while (!success && circle) {
                        fullreset = true;
                        try {
                        initPipe();
						} catch (SQLException e1) {
							return;
						}
                    }
                    // ошибка связи
                }
            }
            catch (Exception extra) {
                log.error("Shit happens", extra);
                return;
            }
        }
    }

    public void closeErrorReader() {
        try {
            circle = false;
            fr.clear();
            fr.setVisible(false);
            while (initializing) {
            }
            executeQuery("begin deb_lib.write('zeta$empty'); end;");
            this.interrupt();
            cs.close();
            executeQuery("begin deb_lib.close('"+pipe+"'); end;");
            DBMSConnection.closeConnection(this);
        }
        catch (Exception e) {
            log.error("Shit happens", e);
        }
    }

}
