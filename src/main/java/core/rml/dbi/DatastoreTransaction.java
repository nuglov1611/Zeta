package core.rml.dbi;

import java.util.List;
import java.util.Vector;

/**
 * @author: vagapova.m
 */
public class DatastoreTransaction {

    private List<String> columnNames;

    private int state;

    public static final int TRANSACTION_NOTHING = 0;

    public static final int TRANSACTION_UPDATE = 1;

    public static final int TRANSACTION_DELETE = 2;

    public static final int TRANSACTION_INSERT = 3;

    public DatastoreTransaction(int state) {
        this.state = state;
        columnNames = new Vector<String>();
    }

    public void addColumnName(String columnName) {
        columnNames.add(columnName.toUpperCase());
    }

    public void removeColumnName(String columnName) {
        columnNames.remove(columnName.toUpperCase());
    }

    public boolean containColumnName(String columnName) {
        return columnNames.contains(columnName) || (columnNames.contains(columnName.toUpperCase()));
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
