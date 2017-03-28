package core.rml.dbi;

import core.document.Document;
import loader.ZetaProperties;
import org.apache.log4j.Logger;

import java.sql.*;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: vagapova.m
 * @since: 03.10.2010
 */
public class SqlManager {

    private static final Logger log = Logger.getLogger(Datastore.class);

    /**
     * хэш, содержит первичные ключи таблиц
     */
    Hashtable<String, String[]> pkColumns;

    /**
     * журнал операций 0-ничего 1-UPDATE 2-DELETE 3-INSERT (new row) координаты
     * абсолютные, т.е это ключи а не индексы
     */
    Map<Integer, DatastoreTransaction> transactionPool;

    Map<Integer, DatastoreTransaction> backupTransactionPool;

    /**
     * по умолчанию не ограничиваем размер выборки
     */
    int maxFetchRow = -1;


    /**
     * исходный, полученный запрос
     */
    String sql = null;

    /**
     * преобразованный запрос, возможно со вставленными аргумента
     */
    String rsql = null;

    public SqlManager() {
        pkColumns = new Hashtable<String, String[]>();
        transactionPool = new HashMap<Integer, DatastoreTransaction>();
        backupTransactionPool = new HashMap<Integer, DatastoreTransaction>();
    }

    public int getMaxFetchRow() {
        return maxFetchRow;
    }

    public void setMaxFetchRow(int maxFetchRow) {
        this.maxFetchRow = maxFetchRow;
    }

    /**
     * По имени таблицы, возвращает ранее установленную уникальную комбинацию
     */
    public Hashtable<String, String[]> getPkColumns() {
        return pkColumns;
    }


    /**
     * Устанавливает поля(столбцы) таблицы, считающиеся уникальной коибинацие
     * DEPRECATED!
     */
    public void setPkColumns(String[] columns, String table) {
        pkColumns.put(table, columns);
    }

    /**
     * По имени таблицы, возвращает ранее установленную уникальную комбинацию
     * DEPRECATED!
     */
    public String[] getPkColumns(String table) {
        return pkColumns.get(table);
    }

    /**
     * По имени таблицы, возвращает ранее установленную уникальную комбинацию
     * DEPRECATED!
     */
    public int[] getIPkColumns(DatastoreModel model, String table) {
        String[] pkColumnNames = pkColumns.get(table);
        int[] iret = new int[model.getColumnCount()];
        for (String pkColumnName : pkColumnNames) {
            for (int j = 0; j < model.getColumnCount(); j++) {
                if (pkColumnName.compareTo(model.getColumnName(j)) == 0) {
                    iret[j] = 1;
                }
            }
        }
        return iret;
    }

    /**
     * Возвращает список таблиц для которых установлена уникальная комбинация
     * DEPRECATED!
     */
    public String[] getUpTables() {
        String[] ret = new String[pkColumns.size()];
        int i = 0;
        for (Enumeration<String> e = pkColumns.keys(); e.hasMoreElements(); ) {
            ret[i] = e.nextElement();
            i++;
        }
        return ret;
    }

    /**
     * Устанавливает поля(столбцы) таблицы, считающиеся уникальной коибинацие
     * DEPRECATED!
     */
    public void setPkColumns(DatastoreModel model, int[] columns, String table) {
        Vector<String> v = new Vector<String>();
        for (int i = 0; i < columns.length; i++) {
            if (columns[i] == 1) {
                v.addElement(model.getColumnName(i));
            }
        }
        String[] ret = new String[v.size()];
        v.copyInto(ret);
        pkColumns.put(table, ret);
    }

    public void addTransaction(int row, int transactionType) {
        addTransaction(row, null, transactionType);
    }

    /**
     * @param rowIndex - ключ строки, а не видимый/отфильтрованный индекс
     * @return
     */
    public void addTransaction(int rowIndex, String columnName, int transactionType) {
        if (!transactionPool.containsKey(rowIndex)) {
            DatastoreTransaction newTransaction = new DatastoreTransaction(transactionType);
            if (columnName != null) {
                newTransaction.addColumnName(columnName);
            }
            transactionPool.put(rowIndex, newTransaction);
        } else {
            DatastoreTransaction currentTransaction = transactionPool.get(rowIndex);
            if ((transactionType == DatastoreTransaction.TRANSACTION_DELETE &&
                    currentTransaction.getState() == DatastoreTransaction.TRANSACTION_INSERT) ||
                    (transactionType == DatastoreTransaction.TRANSACTION_INSERT &&
                            currentTransaction.getState() == DatastoreTransaction.TRANSACTION_DELETE)) {
                transactionPool.remove(rowIndex);
            } else if (transactionType == DatastoreTransaction.TRANSACTION_INSERT) {
                currentTransaction.setState(transactionType);
                currentTransaction.addColumnName(columnName);
                transactionPool.put(rowIndex, currentTransaction);
            } else if (transactionType == DatastoreTransaction.TRANSACTION_DELETE) {
                currentTransaction.setState(transactionType);
                transactionPool.put(rowIndex, currentTransaction);
            } else if (transactionType == DatastoreTransaction.TRANSACTION_UPDATE) {
                currentTransaction.addColumnName(columnName);
                transactionPool.put(rowIndex, currentTransaction);
            }
        }
    }

    public String buildDeleteQuery(DatastoreModel model, String table, int rowIndex) {
        String deleteQuery = null;
        String[] pks = pkColumns.get(table);
        if (pks != null) {
            deleteQuery = "delete from " + table + " where ";
            for (int i = 0; i < pks.length; i++) {
                deleteQuery = deleteQuery + pks[i] + " = ";
                int typeCol = model.getColumnType(pks[i]);
                // Мы удалили строку, поэтому используем индекс напрямую чтоб добраться до ключевых столбцов и данных
                Object o = model.getDataValueAt(rowIndex, pks[i]);
                if (o != null) {
                    deleteQuery += formatSqlQueryValue(o, typeCol, DatastoreTransaction.TRANSACTION_DELETE);
                } else {
                    log.error("Can't build the query, object at " + rowIndex + " row for column " + pks[i] + " is NULL");
                    return null;
                }
                if (i != pks.length - 1) {
                    deleteQuery = deleteQuery + " and ";
                } // else {sql=sql+";";}
            }
        }
        return deleteQuery;
    }

    public String buildInsertQuery(DatastoreModel model, Hashtable<String, Vector<String>> tables, String table, DatastoreTransaction transaction, int rowIndex) {
        String insertQuery = "insert into " + table + " ( ";
        String valuesPostfix = ") values (";
        Vector<String> columnNamesList = tables.get(table);
        boolean vs = false;
        for (int i = 0; i < columnNamesList.size(); i++) {
            String columnName = columnNamesList.elementAt(i);
            if (transaction.containColumnName(columnName)) {
                int typeCol = model.getColumnType(columnName);
                Object myobj = model.getDataValueAt(rowIndex, columnName);
                // Не последний столбец
                if (i != columnNamesList.size() - 1) {
                    String value = formatSqlQueryValue(myobj, typeCol, DatastoreTransaction.TRANSACTION_INSERT);
                    if (value != null) {
                        insertQuery = insertQuery + columnNamesList.elementAt(i) + " , ";
                        valuesPostfix = valuesPostfix + value + " , ";
                    }
                } else {
                    // Последний столбец
                    String value = formatSqlQueryValue(myobj, typeCol, DatastoreTransaction.TRANSACTION_INSERT);
                    if (value != null) {
                        insertQuery = insertQuery + columnNamesList.elementAt(i);
                        valuesPostfix = valuesPostfix + value + ")";
                        vs = true;
                    }
                }
            }
        }
        if (!vs) {
            insertQuery = insertQuery.substring(0, insertQuery.length() - 2);
            valuesPostfix = valuesPostfix.substring(0, valuesPostfix.length() - 2);
            valuesPostfix = valuesPostfix + ")";
        }
        insertQuery += valuesPostfix;
        return insertQuery;
    }

    public String buildUpdateQuery(DatastoreModel model, Hashtable<String, Vector<String>> tables, String table, DatastoreTransaction transaction, int rowIndex) {
        String updateQuery = null;
        String[] pks = pkColumns.get(table);
        if (pks != null) {
            updateQuery = "update  " + table + " set ";
            Vector<String> columnNamesList = tables.get(table);
            boolean vs = false;
            boolean needUpdate = false;
            for (int i = 0; i < columnNamesList.size(); i++) {
                String columnName = columnNamesList.elementAt(i);
                int currentColumnIndex = model.getColumnIndex(columnName);
                if (transaction.containColumnName(columnName)) {
                    vs = false;
                    needUpdate = true;
                    int typeCol = model.getColumnType(currentColumnIndex);
                    updateQuery = updateQuery + columnNamesList.elementAt(i) + " = ";
                    Object o = model.getDataValueAt(rowIndex, currentColumnIndex);
                    if (o != null) {
                        updateQuery += formatSqlQueryValue(o, typeCol, DatastoreTransaction.TRANSACTION_UPDATE);
                        if (i != columnNamesList.size() - 1) {
                            updateQuery = updateQuery + ",";
                            vs = true;
                        }
                    } else {
                        updateQuery += "NULL";
                        if (i != columnNamesList.size() - 1) {
                            updateQuery = updateQuery + ",";
                            vs = true;
                        }
                    }
                }
            }
            if (vs) {
                updateQuery = updateQuery.substring(0, updateQuery.length() - 1);
            }
            if (needUpdate) {
                updateQuery = updateQuery + " where ";
                for (int i = 0; i < pks.length; i++) {
                    int typeCol = model.getColumnType(pks[i]);
                    updateQuery = updateQuery + pks[i] + " = ";
                    Object o = model.getValueAt(rowIndex, pks[i]);
                    if (o != null) {
                        updateQuery += formatSqlQueryValue(o, typeCol, DatastoreTransaction.TRANSACTION_UPDATE);
                        if (i != pks.length - 1) {
                            updateQuery = updateQuery + " and ";
                        }
                    } else {
                        updateQuery += "NULL";
                        if (i != pks.length - 1) {
                            updateQuery = updateQuery + " and ";
                        }
                    }
                }
            } else {
                updateQuery = null;
            }
        }
        return updateQuery;
    }

    public String formatSqlQueryValue(Object value, int typeCol, int operationType) {
        String sqlAppendix = "";
        //Если строчка пустая или нулл, а это инсерт или апдейт, то заменяем на нулл - не используем имя столбца и его значение при инсерте
        if ((operationType == DatastoreTransaction.TRANSACTION_INSERT || operationType == DatastoreTransaction.TRANSACTION_UPDATE) && ((value == null)
                || ((value instanceof String) && (value.equals(""))))) {
//            sqlAppendix += "NULL";
            return null;
        } else {
            boolean insertQuote = (typeCol == Types.TIMESTAMP)
                    || (typeCol == Types.VARCHAR)
                    || (typeCol == Types.CHAR)
                    || (typeCol == Types.DATE) || typeCol == -8;
            if (insertQuote) {
                sqlAppendix += "'";
            }
            if (typeCol == Types.DATE
                    || typeCol == Types.TIMESTAMP) {
                // Инициализация классов преобразования даты
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                FieldPosition fp = new FieldPosition(0);
                StringBuffer datBuff = new StringBuffer();
                sdf.format((java.util.Date) value,
                        datBuff, fp);
                sqlAppendix += new String(datBuff);
            } else if (typeCol == Types.CHAR
                    || typeCol == Types.VARCHAR) {
                String s1 = "'";
                String s2 = "''";
                StringBuilder s = new StringBuilder();
                String beg = value.toString();
                int pos = 0;
                int next;
                while ((next = beg.indexOf(s1,
                        pos)) != -1) {
                    s.append(
                            beg.substring(pos,
                                    next)).append(s2);
                    pos = next + s1.length();
                }
                if (pos < beg.length()) {
                    s.append(beg.substring(pos));
                }
                sqlAppendix += s.toString();
            } else {
                sqlAppendix += value;
            }
            sqlAppendix = sqlAppendix.trim();
            if (insertQuote) {
                sqlAppendix += "'";
            }
        }
        return sqlAppendix;
    }

    public void update(Connection conn, DatastoreModel model, Hashtable<String, Vector<String>> tables) throws UpdateException {
        String table;
        DatastoreTransaction transaction;
        String query;
        for (Integer rowIndex : transactionPool.keySet()) {
            transaction = transactionPool.get(rowIndex);
            if (transaction != null) {
                if (transaction.getState() == DatastoreTransaction.TRANSACTION_INSERT) {
                    // insert
                    if (ZetaProperties.dstore_debug > 1) {
                        log.debug("INSERT executing");
                    }
                    for (Enumeration<String> tbl = tables.keys(); tbl.hasMoreElements(); ) {
                        table = tbl.nextElement();
                        if (pkColumns.get(table) == null) {
                            continue;
                        }
                        query = buildInsertQuery(model, tables, table, transaction, rowIndex);
                        executeQuery(conn, query, DatastoreTransaction.TRANSACTION_INSERT);
                    }
                } else if (transaction.getState() == DatastoreTransaction.TRANSACTION_DELETE) {
                    // delete
                    Stack<String> stk = new Stack<String>();
                    if (ZetaProperties.dstore_debug > 1) {
                        log.debug("update executing");
                        log.debug("DELETE executing");
                    }
                    for (Enumeration<String> tbl = tables.keys(); tbl.hasMoreElements(); ) {
                        stk.push(tbl.nextElement());
                    }
                    while (!stk.empty()) {
                        table = stk.pop();
                        query = buildDeleteQuery(model, table, rowIndex);
                        if (query != null) {
                            executeQuery(conn, query, DatastoreTransaction.TRANSACTION_DELETE);
                        }
                    }
                } else if (transaction.getState() == DatastoreTransaction.TRANSACTION_UPDATE) {
                    // update
                    if (ZetaProperties.dstore_debug > 1) {
                        log.debug("UPDATE executing");
                    }
                    for (Enumeration<String> tbl = tables.keys(); tbl.hasMoreElements(); ) {
                        table = tbl.nextElement();
                        query = buildUpdateQuery(model, tables, table, transaction, rowIndex);
                        if (query != null) {
                            executeQuery(conn, query, DatastoreTransaction.TRANSACTION_UPDATE);
                        }
                    }
                } else {
                    if (ZetaProperties.dstore_debug > 1) {
                        log.debug(" UNKNOWN OPERATION!!!");
                    }
                }
            }
        }

        if (ZetaProperties.dstore_debug > 0) {
            log.debug("core.rml.dbi.Datastore.update:before clear operatiuon");
        }
        if (transactionPool.size() != 0) {
            backupTransactionPool = new HashMap<Integer, DatastoreTransaction>(transactionPool);
        }

        transactionPool.clear();
    }

    public ResultSet executeQuery(Connection conn, String rsql) throws SQLException {
        Statement stmt = conn.createStatement();
        try {
            stmt.execute(rsql);
        } finally {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.error("!", e);
            }
        }
        return null;
    }

    public void executeQuery(Connection conn, String query, int transactionType) throws UpdateException {
        try {
            if (ZetaProperties.dstore_debug > 1) {
                String queryType = transactionType == DatastoreTransaction.TRANSACTION_INSERT ? "INSERT"
                        : transactionType == DatastoreTransaction.TRANSACTION_DELETE ? "DELETE"
                        : transactionType == DatastoreTransaction.TRANSACTION_UPDATE ? "UPDATE" : "NOTHING";
                log.debug("DATASTORE.update: " + queryType + " executing. Query="
                        + query);
            }
            Statement stmt = conn.createStatement();
            try {
                stmt.execute(query);
            } finally {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    log.error("!", e);
                }
            }
        } catch (SQLException ex) {
            if (ZetaProperties.dstore_debug > 1) {
                log.error(ex);
            }
            throw new UpdateException(ex, 0, 0);
        }
    }


    /**
     * разбирает SQL и формирует полные названия столбцов выборк
     */
    public Hashtable<String, Vector<String>> extractTableAndColumnNames(DatastoreModel model) {
        String retrievedSqlQuery = sql.toUpperCase();

        Hashtable<String, Vector<String>> tables = new Hashtable<String, Vector<String>>();

        int k = retrievedSqlQuery.indexOf("SELECT");
        String columnSqlPart = retrievedSqlQuery.substring(k + 6, retrievedSqlQuery.indexOf("FROM"));
        if (ZetaProperties.dstore_debug > 1) {
            log.warn("core.rml.dbi.DATASTORE.tables query= " + columnSqlPart);
        }

        StringTokenizer columnNamesSql = new StringTokenizer(columnSqlPart, ",");
        String table = "";
        String columnName = "";
        boolean is_column = true;
        boolean have_no_table = false;
        String foo = "";
        int columnIndex = -1;

        if (model.getColumnNames() == null || model.getColumnCount() == 0) {
            log.warn("COLNAME is null!!! ATTENSION!!! ");
        }
        for (; columnNamesSql.hasMoreTokens(); ) {
            if (is_column) {
                have_no_table = false;
                columnIndex++;
                foo = columnNamesSql.nextToken();
            } else {
                foo += "," + columnNamesSql.nextToken();
                have_no_table = true;
            }
            if (!isBracketRight(foo)) {
                is_column = false;
                continue;
            } else {
                is_column = true;
            }
            if (have_no_table || !foo.contains(".")
                    || foo.contains("(")) {
                if (model.getColumnLabel(columnIndex) != null) {
                    columnName = model.getColumnLabel(columnIndex).toUpperCase();
                } else {
                    columnName = foo.trim();
                }
            } else {
                StringTokenizer st = new StringTokenizer(foo, ".");
                table = st.nextToken().trim();

                Vector<String> str = tables.get(table);
                if (str == null) {
                    str = new Vector<String>();
                }
                columnName = table + "." + st.nextToken().trim();
                str.addElement(columnName);
                tables.put(table, str);
            }
            if (ZetaProperties.dstore_debug > 2) {
                log.debug("core.rml.dbi.DATASTORE.tables colnam= " + columnName);
            }
            model.addColumnName(columnIndex, columnName);
        }
        return tables;
    }

    public boolean isSqlWord(String word) {
        String[] sqlKeys = {"+", "-", "*", "/", "(", "||"};
        for (String sqlKey : sqlKeys) {
            if (word.contains(sqlKey)) {
                return true;
            }
        }
        return false;
    }

    private boolean isBracketRight(String str) {
        int bracket_count = 0;
        int start_pos = str.indexOf("(");
        if (start_pos > -1) {
            bracket_count++;
            for (int i = start_pos + 1; i < str.length() && bracket_count > 0; i++) {
                if (str.charAt(i) == '(') {
                    bracket_count++;
                } else if (str.charAt(i) == ')') {
                    bracket_count--;
                }
            }
        }
        return (bracket_count == 0);
    }

    public void clear() {
        transactionPool.clear();
    }

    public void rollback() {
        if (backupTransactionPool.size() != 0) {
            if (ZetaProperties.dstore_debug > 0) {
                log.debug("db..DATASTORE:: rollbacking...");
            }
            transactionPool = backupTransactionPool;
        }
    }

    public DatastoreModel retrieveModel(Document doc, Connection conn,
                                        List<Compute> computeColumn,
                                        String alias) throws SQLException {

        DatastoreModel model = new DatastoreModel();
        if (ZetaProperties.dstore_debug > 1) {
            log.debug("core.rml.dbi.DATASTORE.retrieve rsql=" + rsql);
        }
        try {
            rsql = doc.calculateMacro(sql);
        } catch (Exception e) {
            log.error("ERROR IN SQL EXPRESSION: ", e);
            if (ZetaProperties.dstore_debug > 1) {
                log.error(" ERROR IN SQL EXPRESSION:", e);
            }
        }
        if (ZetaProperties.dstore_debug > 1) {
            log.debug("core.rml.dbi.DATASTORE.retrieve rsql=" + rsql);
        }

        ResultSet rset = null;
        ResultSetMetaData rmd = null;
        Statement stmt = null;
        if (ZetaProperties.dstore_debug > 0) {
            log.debug("core.rml.dbi.DATASTORE::retrieve rsql=" + rsql);
        }
        int row = 0;
        try {
            stmt = conn.createStatement();
            rset = stmt.executeQuery(rsql);
            if (rset != null) {
                rmd = rset.getMetaData();

                int columnCount = rmd.getColumnCount();
                if (columnCount == 0) {
                    log.debug("Column count = 0 !!!!! in DS: " + alias
                            + "\n   Query is: " + rsql);
                }
                int compSize = 0;
                if (computeColumn != null) {
                    compSize = computeColumn.size();
                }
                // Сначала заполняем значениями из базы...
                int modelColumnCount = model.getColumnCount();
                for (int i = modelColumnCount; i < modelColumnCount + columnCount; i++) {
                    int columnType = rmd.getColumnType(i + 1);
                    model.addColumnType(i, columnType);
                    String columnLabel = rmd.getColumnLabel(i + 1);
                    model.addColumnLabel(i, columnLabel);
                    if (columnLabel == null || columnLabel.trim().equals("")) {
                        log.debug("Column label is empty = 0 !!!!! in DS: "
                                + alias
                                + "\n   Query is: "
                                + rsql);
                    }

                }
                // А теперь заполняем COMPUTED FIELDS...
                modelColumnCount = model.getColumnCount();
                for (int l = 0; l < compSize; l++) {
                    Compute cm = computeColumn.get(l);
                    model.addColumnType(l + modelColumnCount, cm.getType());
                    model.addColumnName(l + modelColumnCount, cm.getName());
                }

                while (rset.next()) {
                    row = model.addRow();
                    for (int i = 0; i < columnCount; i++) {
                        Object value = null;
                        if (model.getColumnType(i) == Types.NUMERIC && rset.getObject(i + 1) != null) {
                            value = rset.getDouble(i + 1);
                        } else {
                            value = rset.getObject(i + 1);
                        }
                        model.addValue(row, i, value);
                    }
                    if (maxFetchRow != -1 && maxFetchRow == row) {
                        break;
                    }
                    if (row % 100 == 0) {
                        doc.showInfo("Загружено "
                                + (new Integer(row)).toString()
                                + " строк.");
                    }
                }
                doc.clearInfo();
            }
        } catch (SQLException e) {
            log.error("Shit happens " + "\n SqlState:"
                    + e.getSQLState(), e);
            ErrorReader.getInstance().addMessage(e.getMessage());
            doc.clearInfo();
        } finally {
            if (rset != null) {
                try {
                    rset.close();
                } catch (SQLException e) {
                    log.error("!", e);
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    log.error("!", e);
                }
            }
        }
        return model;
    }

    public void setSql(String sql) {
        this.sql = sql;
        this.rsql = sql;
    }

    public String getSql() {
        return sql;
    }
}
