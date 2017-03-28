package core.rml.dbi;

import action.api.RTException;
import core.connection.BadPasswordException;
import core.connection.ConnectException;
import core.document.Document;
import core.parser.Proper;
import core.rml.RmlObject;
import loader.ZetaProperties;
import loader.ZetaUtility;
import org.apache.log4j.Logger;
import publicapi.DSCollectionAPI;

import java.sql.*;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

/*
 * обьект - коллекция DATASTORE позволяет совершать их синхронизацию с базой в
 * пределах одной транзакции
 */
public class DSCollection extends Datastore implements DSCollectionAPI {
    private static final Logger log = Logger
            .getLogger(DSCollection.class);

    Datastore[] dss;

    String query, seqQuery;

    String[] aliases;

    String[] names_sq;

    Hashtable<String, Object> fromDBnames;

    Hashtable<String, Object> seqNames = new Hashtable<String, Object>();

    Datastore head = null;

    String initAction = null;

    public DSCollection(Document doc) {
        super(doc);
    }

    public DSCollection() {
        super();
    }

    public void init(Proper prop, Document doc) {
        super.init(prop, doc);
        setAliases((String) prop.get("ALIASES"));
        setInitQuery((String) prop.get("INITQUERY"));
        setSeqQuery((String) prop.get("SEQQUERY"));
        setSeqNames((String) prop.get("SEQNAMES"));
        setInitAction((String) prop.get("INITACTION"));
    }

    public void setDatastores(Datastore[] dss) {
        this.dss = dss;
        for (Datastore ds : dss) {
            if (ds.isHead()) {
                head = ds;
            }
        }
    }

    public void removeDs(String alias) {
        Vector<Datastore> v = new Vector<Datastore>();
        for (Datastore ds1 : dss) {
            if (!ds1.getAlias().equals(alias)) {
                v.addElement(ds1);
            }
        }
        Datastore ds[] = new Datastore[v.size()];
        v.copyInto(ds);
        dss = ds;
    }

    @Override
    public void addChild(RmlObject child) {
        super.addChild(child);

        if (child instanceof Datastore && dss != null) {
            addDs((Datastore) child);
        }
    }

    public void addDs(Datastore ds) {
        try {
            Datastore[] dsa = new Datastore[dss.length + 1];
            System.arraycopy(dss, 0, dsa, 0, dss.length);
            dsa[dss.length] = ds;
            dss = dsa;
        } catch (Exception e) {
            log.error("!", e);
        }
    }

    public void setSeqQuery(String seqQuery) {
        if (seqQuery == null) {
            return;
        }
        this.seqQuery = seqQuery;
    }

    public void setSeqNames(String seqNames) {
        if (seqNames == null) {
            return;
        }
        seqNames = seqNames.toUpperCase();
        StringTokenizer st = new StringTokenizer(seqNames, ",");
        int countToken = st.countTokens();
        names_sq = new String[countToken];
        for (int i = 0; i < countToken; i++) {
            names_sq[i] = st.nextToken().trim();
        }
    }

    public void nextVal() throws ConnectException, BadPasswordException {
        log.debug("core.rml.dbi.DSCollection.nextVal called");
        log.debug("core.rml.dbi.DSCollection.nextVal seqQuery=" + seqQuery
                + " seqNames=" + Arrays.toString(names_sq));
        ResultSet rset = null;
        ResultSetMetaData rmd = null;
        Statement stmt = null;
        try {
            stmt = document.getConnection().createStatement();
            rset = stmt.executeQuery(seqQuery);
            rmd = rset.getMetaData();
            int columnCount = rmd.getColumnCount();
            int types;
            Object dat;
            rset.next();
            for (int i = 1; i <= columnCount; i++) {
                types = rmd.getColumnType(i);
                if (types == Types.NUMERIC) {
                    dat = new Double(rset.getDouble(i));
                } else {
                    dat = rset.getObject(i);
                }
                seqNames.put(names_sq[i - 1], dat);
            }
            rset.close();
        } catch (SQLException e) {
            log.error("Shit happens", e);
            throw new ConnectException("Потеря соединения с сервером");
        } finally {
            if (stmt != null)
                try {
                    stmt.close();
                } catch (SQLException e) {
                    log.error("!", e);
                }
        }
    }

    @Override
    public int retrieve() {
        if (dss == null) {
            return 0;
        }
        for (Datastore ds : dss) {
            try {
                ds.retrieve();
            } catch (Exception e) {
                log.error("Shit happens", e);
            }
        }
        return 0;
    }

    /**
     * метод, синхронизирующий коллекцию DATASTORE с базой
     *
     * @throws BadPasswordException
     * @throws SQLException
     */
    @Override
    public void update() throws SQLException {
        int i = 0;
        if (ZetaProperties.dstore_debug > 0) {
            log.debug("core.rml.dbi.DSCollection.update called");
        }
        if (dss == null) {
            return;
        }
        try { // pavel DATASTORE.conn.setAutoCommit(false);
            try {
                if (head != null) {
                    if (ZetaProperties.dstore_debug > 0) {
                        log.debug("core.rml.dbi.DSCollection.update UPDATING HEAD:::"
                                + head);
                    }
                    head.update();
                }
                for (i = 0; i < dss.length; i++) {
                    dss[i].update();
                    log.debug("core.rml.dbi.DSCOlection.update " + dss[i]);
                }
            } catch (SQLException e) {
                if (ZetaProperties.dstore_debug > 0) {
                    log.error("Shit happens", e);
                }
                for (i = 0; i < dss.length; i++) {
                    dss[i].rollback();
                    log.debug("rollback in " + dss[i]);
                }

                document.getConnection().rollback();
                if (ZetaProperties.dstore_debug > 0) {
                    log.debug("core.rml.dbi.DSCollection: Rollback PERFORMED");
                }
                log.debug("core.rml.dbi.DSCollection.updateException " + e);
                throw e;
            } catch (Exception e1) {
                log.error("Shit happens", e1);
                document.getConnection().rollback();
//                if (e1.getMessage().toUpperCase().indexOf("PROTOCOL") != -1) {
//                    stmt.execute("rollback");
//                }

                log.debug("core.rml.dbi.DSCollection.UnknownException " + e1);
                throw new UpdateException((SQLException) e1, 0, i);
            }

            document.getConnection().commit();
            repeatLocks();
            if (ZetaProperties.dstore_debug > 0) {
                log.debug("core.rml.dbi.DSCollection: COMMIT PERFORMED");
            }
        } catch (SQLException e) {
            if (e instanceof UpdateException) {
                log.error("Shit happens", e);
                throw e;
            } else {
                log
                        .debug("core.rml.dbi.DSCollection.update: ConnectException performed");
                log.error("Shit happens", e);
                throw new ConnectException(e.getMessage());
            }
        }

    }

    public void reset() throws ConnectException, BadPasswordException {
        ResultSet rset = null;
        ResultSetMetaData rmd = null;
        Statement stmt = null;
        try {
            stmt = document.getConnection().createStatement();
            log.debug("core.rml.dbi.DSCollection.reset before execute query=" + query);
            rset = stmt.executeQuery(query);
            if (rset != null) {
                rmd = rset.getMetaData();
                int columnCount = rmd.getColumnCount();
                int types;
                Object dat;
                if (ZetaProperties.dstore_debug > 0) {
                    log.debug("core.rml.dbi.DSCollection.reset rset=" + rset);
                }
                rset.next();
                fromDBnames = new Hashtable<String, Object>(columnCount);
                for (int i = 1; i <= columnCount; i++) {
                    types = rmd.getColumnType(i);
                    if (types == Types.NUMERIC) {
                        dat = new Double(rset.getDouble(i));
                    } else {
                        dat = rset.getObject(i);
                    }

                    fromDBnames.put(aliases[i - 1], dat);
                    if (ZetaProperties.dstore_debug > 0) {
                        log.debug("core.rml.dbi.DSCollection.reset aliases="
                                + aliases[i - 1] + " dat=" + dat);
                    }
                }
                rset.close();
            }
        } catch (SQLException e) {
            log.error("Shit happens", e);
            log.debug("core.rml.dbi.DSCollection.reset " + e);
            throw new ConnectException("Потеря соединения с сервером");
        } finally {
            if (stmt != null)
                try {
                    stmt.close();
                } catch (SQLException e) {
                    log.error("!", e);
                }
        }

        if (ZetaProperties.dstore_debug > 0) {
            log.debug("core.rml.dbi.DSCollection.reset dss.length=" + dss.length);
        }
        try {
            if (head != null) {
                head.retrieve();
                head.clear();
                head.newRow();
            }
            for (Datastore ds : dss) {
                if (!ds.isHead()) {
                    ds.retrieve();
                    ds.clear();
                }
                if (ds.handler != null) {
                    ds.handler.notifyHandler(null);
                }
            }
        } catch (Exception e) {
            log.error("Shit happens", e);
        }
        if (initAction != null) {

            try {
                document.executeScript(initAction, true);
            } catch (Exception e) {
                log.error("Shit happens", e);
            }

        }
    }

    public void setInitQuery(String query) {
        if (query == null) {
            return;
        }
        this.query = query;
    }

    /**
     * alias1,alias2,....,aliasn
     */
    public void setInitAction(String initAction) {
        this.initAction = initAction;
    }

    public void setAliases(String alias) {
        if (alias == null) {
            return;
        }
        try {
            alias = alias.toUpperCase();
            StringTokenizer st = new StringTokenizer(alias, ",");
            int count = st.countTokens();
            aliases = new String[count];
            for (int i = 0; i < count; i++) {
                aliases[i] = st.nextToken().trim();
            }
        } catch (Exception e) {
            log.error("Shit happens", e);
        }
    }

    @Override
    public void setValue(Object obj) {
    }

    @Override
    public Object getValue() {
        return this;
    }

    @Override
    public void setValueByName(String name, Object obj) {
    }

    @Override
    public Object getValueByName(String name) {
        Object o = null;
        if (fromDBnames != null) {
            o = fromDBnames.get(name);
        }

        if (o == null && seqNames != null) {
            try {
                o = seqNames.get(name);
                nextVal();
            } catch (Exception e) {
                log.error("Shit happens", e);
                return null;
            }
            return seqNames.get(name);
        }
        return o;
    }

    @Override
    public Object method(String method, Object arg) throws Exception {
        if (method.equals("DELSTORE")) {
            try {
                removeDs((String) arg);
                return new Double(0);
            } catch (ClassCastException e) {
                log.error("Shit happens", e);
                throw new RTException("CastException",
                        "method DELSTORE must have"
                                + " one parameter compateable with String type");
            }
        } else if (method.equals("ADDSTORE")) {
            try {
                addDs((Datastore) arg);
                return new Double(0);
            } catch (ClassCastException e) {
                log.error("Shit happens", e);
                throw new RTException(
                        "CastException",
                        "method ADDSTORE must have"
                                + "one parameter compateable with DATASTORE type");
            }
        } else if (method.equals("LOCKROW")) {
            try {
                Vector<String> v = (Vector<String>) arg;
                String table = v.elementAt(0);
                String key = v.elementAt(1);
                Object val = v.elementAt(2);
                lockRow(table, key, val);
            } catch (ClassCastException e) {
                log.error("Shit happens", e);
                throw new RTException("CastException",
                        "Wrong arguments of LockRow <Table>, <Key>, <Value>");
            } catch (IndexOutOfBoundsException e) {
                log.error("Shit happens", e);
                throw new RTException("CastException",
                        "Wrong arguments of LockRow <Table>, <Key>, <Value>");
            }
        } else if (method.equals("ISCONNECTED")) {
            Statement stmt = null;
            try {
                stmt = document.getConnection().createStatement();
                stmt.executeQuery("select user from dual");
                return new Double(1);
            } catch (SQLException e) {
                log.error("Shit happens", e);
                return new Double(0);
            } finally {
                if (stmt != null)
                    try {
                        stmt.close();
                    } catch (SQLException e) {
                        log.error("!", e);
                    }
            }
        } else {
            throw new RTException("HasNotMethod", "method " + method
                    + " not defined in class DSCOLLECTION!");
        }
        return new Double(0);

    }

    private static Vector<CallableStatement> locks = new Vector<CallableStatement>();

    private Vector<CallableStatement> thisLocks = new Vector<CallableStatement>();

    public static synchronized void repeatLocks() throws SQLException {
        for (int i = 0; i < locks.size(); i++) {
            locks.elementAt(i).executeUpdate();
        }
    }

    public void removeLocks() {
        synchronized (getClass()) {
            for (int i = 0; i < thisLocks.size(); i++) {
                locks.removeElement(thisLocks.elementAt(i));
            }
            try {
                document.getConnection().rollback();
                repeatLocks();
            } catch (SQLException e) {
                log.error("Shit happens", e);
            }
        }
    }

    public synchronized void lockRow(String table, String key, Object val)
            throws Exception {
        synchronized (getClass()) {
            String sql = "begin select " + key + " into :1 from " + table
                    + " where " + key + "=";
            if (val instanceof String) {
                sql += "'" + val + "'";
            } else if (val instanceof Double) {
                sql += val;
            } else {
                throw new RTException("CastException",
                        "<Value> can be only String or Number");
            }

            sql += "for update nowait; end;";
            log.debug(sql);
            CallableStatement cs = document.getConnection().prepareCall(sql);
            if (val instanceof String) {
                cs.registerOutParameter(1, java.sql.Types.VARCHAR);
            } else if (val instanceof Double) {
                cs.registerOutParameter(1, java.sql.Types.NUMERIC);
            }

            try {
                cs.executeUpdate();
            } catch (SQLException e) {
                log.error("Shit happens", e);
                if (e.getMessage().contains("NOWAIT")) {
                    throw new SQLException(ZetaUtility.pr(ZetaProperties.MSG_BLOCKED,
                            "Объект уже заблокирован другим пользователем"));
                } else {
                    throw e;
                }
            }

            locks.addElement(cs);
            thisLocks.addElement(cs);
        }
    }

    @Override
    public String type() {
        return "DSCOLLECTION";
    }

}
