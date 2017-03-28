package core.connection;

import loader.ZetaProperties;
import oracle.jdbc.pool.OracleDataSource;
import org.apache.log4j.Logger;
import properties.PropertyConstants;
import properties.Session;
import properties.SessionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.GregorianCalendar;
import java.util.Locale;

public class OracleConnection extends DBMSConnection {
    private static final Logger log = Logger
            .getLogger(OracleConnection.class);

    private static final String protocol = "jdbc:oracle:thin:";

    private static final String initSQL = "alter session set NLS_DATE_FORMAT='dd-mm-yyyy'";

    protected static Connection connect() throws ConnectException,
            BadPasswordException {
        long sec = GregorianCalendar.getInstance().getTimeInMillis();

        log.debug("Start connecting");

        Session cur_session = SessionManager.getIntance().getCurrentSession();
        user = cur_session.getProperty(PropertyConstants.DB_USERNAME);
        pwd = cur_session.getProperty(PropertyConstants.DB_PASSWORD);
        host = cur_session.getProperty(PropertyConstants.DB_SERVER);
        port = cur_session.getProperty(PropertyConstants.DB_PORT);
        name = cur_session.getProperty(PropertyConstants.DB_NAME);

        String connStr = protocol + user + "/" + pwd + "@" + host + ":" + port
                + ":" + name;

        //        connStr = "" + ZetaUtility.pr(ZetaProperties.DBS_PROTO) + "salisr"
        //        + "/" + "517037" + ZetaUtility.pr(ZetaProperties.DBS_HOST);

        Connection conn = null;
        Statement stmt = null;
        Locale origLocale = Locale.getDefault();
        try {
            boolean workaround = false;

            if (origLocale.toString().equals("ru_RU")) {
                Locale.setDefault(new Locale("en", "US"));
                workaround = true;
            }
            OracleDataSource ods = new OracleDataSource();
            ods.setURL(connStr);
            conn = ods.getConnection();
            conn.setAutoCommit(false);

            stmt = conn.createStatement();
            if (workaround) {
                String territory = "CIS";
                ZetaProperties.ORACLE_VERSION = conn.getMetaData()
                        .getDatabaseProductVersion();
                if (conn.getMetaData().getDatabaseMajorVersion() >= 10)
                    territory = "RUSSIA";
                stmt
                        .execute("ALTER SESSION SET NLS_LANGUAGE='RUSSIAN' NLS_TERRITORY='"
                                + territory + "'");

                log.debug("NLS_TERRITORY changed to " + territory);
            }
            stmt.execute(initSQL);
        } catch (SQLException e) {
            log.error("Error during conneting to database", e);
            if (e.getMessage().toUpperCase().contains("ORA-01017")) {
                throw new BadPasswordException(
                        "Неверный пароль или имя пользователя", e);
            } else {
                throw new ConnectException("Невозможно установить соединение", e);
            }
        } finally {
            Locale.setDefault(origLocale);
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) {
                log.error("!", e);
            }
        }

        if (ZetaProperties.dstore_debug > 0) {
            log.debug("Connected!");
        }

        sec = GregorianCalendar.getInstance().getTimeInMillis() - sec;
        log.debug("Connection done during " + sec / 1000 + " seconds");
        return conn;
    }

}
