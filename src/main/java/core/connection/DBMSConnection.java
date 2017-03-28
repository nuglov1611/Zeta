/**
 * 
 */
package core.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.log4j.Logger;

/*
 * @author nuglov
 */
public class DBMSConnection {
    private static final Logger                    log         = Logger
                                                                       .getLogger(DBMSConnection.class);

    protected static String                        host;

    protected static String                        port;

    protected static String                        name;

    protected static String                        user;

    protected static String                        pwd;

    protected static String                        connStr     = "";

    protected static Hashtable<Object, Connection> connections = new Hashtable<Object, Connection>();

    protected static Connection connect() throws ConnectException,
            BadPasswordException {
        return OracleConnection.connect();
    }

    public static synchronized Connection getConnection(Object owner)
            throws ConnectException, BadPasswordException {
        if (owner == null)
            return null;
        Connection connection = connections.get(owner);
        if (connection == null) {
            connection = connect();
        }
        if (connection != null) {
            connections.put(owner, connection);
        }
        return connection;
    }

    public static synchronized void closeConnection(Object owner) {
        if (connections.get(owner) == null)
            return;
        try {
            connections.get(owner).close();
        }
        catch (SQLException e) {
            log.error("Can't close core.connection!", e);
        }
        connections.remove(owner);
    }

    public static synchronized void closeAll() {
        Iterator<Connection> iter = connections.values().iterator();
        for (; iter.hasNext();) {
            try {
                Connection conn = iter.next();
                //conn.rollback();
                conn.close();
            }
            catch (SQLException e) {
                log.error("Can't close core.connection!", e);
            }
        }
        connections.clear();
    }

    public static synchronized Connection reconnect(Object owner) {
        closeConnection(owner);
        try {
            return getConnection(owner);
        }
        catch (ConnectException e) {
            log.error("!", e);
        }
        catch (BadPasswordException e) {
            log.error("!", e);
        }
        return null;
    }
}
