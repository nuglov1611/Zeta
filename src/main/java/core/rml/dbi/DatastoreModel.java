package core.rml.dbi;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * @author mvagapova
 */
public class DatastoreModel {

    private Map<Integer, DatastoreColumn> columns;

    private Map<String, Integer> columnIndexes;

    /**
     * ћассив в котором лежат данные, полученные из базы
     * Vector из Object
     */
    private Map<Integer, Map<Integer, Object>> data;

    /**
     * ћассив дл€ синхронизации между индексами строк следующих по пор€дку
     * от 0 до последней строки и реальным индексами строк в массиве дата,
     * которые им соответствуют. ѕри добавлении или удалении строк, необходимо
     * соблюдать четкую синхронзацию
     */
    private List<Integer> rowIndexes;

    public int DEFAULT_TYPE = -1;

    public String DEFAULT_NAME = "";

    public DatastoreModel() {
        columns = new HashMap<Integer, DatastoreColumn>(1);
        columnIndexes = new HashMap<String, Integer>(1);
        rowIndexes = new Vector<Integer>(1);
        data = new HashMap<Integer, Map<Integer, Object>>(1);
    }

    /**
     * Data как такова€ никогда не мен€етс€. ћен€етс€ лишь количество
     * доступных в ней строк.
     *
     * @return
     */
    public int getRowCount() {
        return rowIndexes.size();
    }

    public int getColumnCount() {
        if (columns != null) {
            return columns.size();
        }
        return 0;
    }

    public String getColumnLabel(int columnIndex) {
        DatastoreColumn column = getDatastoreColumn(columnIndex);
        if (column != null) {
            return column.getColumnLabel();
        }
        return null;
    }

    private DatastoreColumn getDatastoreColumn(int columnIndex) {
        if (columns != null && columns.containsKey(columnIndex)) {
            return columns.get(columnIndex);
        }
        return null;
    }

    public String getColumnName(int columnIndex) {
        DatastoreColumn column = getDatastoreColumn(columnIndex);
        if (column != null) {
            return column.getColumnName();
        }
        return DEFAULT_NAME;
    }

    public int getColumnType(int columnIndex) {
        DatastoreColumn column = getDatastoreColumn(columnIndex);
        if (column != null) {
            return column.getColumnType();
        }
        return DEFAULT_TYPE;
    }

    public void setColumnType(int columnIndex, int columnType) {
        DatastoreColumn column = getDatastoreColumn(columnIndex);
        if (column != null) {
            column.setColumnType(columnType);
        }
    }

    public void setColumnLabel(int columnIndex, String columnLabel) {
        DatastoreColumn column = getDatastoreColumn(columnIndex);
        if (column != null) {
            column.setColumnLabel(columnLabel);
        }
    }

    public void setColumnName(int columnIndex, String columnName) {
        DatastoreColumn column = getDatastoreColumn(columnIndex);
        if (column != null) {
            column.setColumnName(columnName);
        }
    }

    public void clearData() {
        rowIndexes.clear();
        data.clear();
    }

    public void clearAll() {
        clearData();
        columns.clear();
        columnIndexes.clear();
    }

    public int getColumnIndex(String columnName) {
        if (columnName != null) {
            if (columnIndexes != null) {
                if (!columnIndexes.containsKey(columnName)) {
                    columnName = columnName.toUpperCase();

                }
                if (columnIndexes.containsKey(columnName) && columnIndexes.get(columnName) != null) {
                    return columnIndexes.get(columnName);
                }
            }
        }
        return -1;
    }

    public int getColumnType(String columnName) {
        if (columnIndexes != null) {
            if (!columnIndexes.containsKey(columnName)) {
                columnName = columnName.toUpperCase();
            }
            if (columnIndexes.containsKey(columnName) && columnIndexes.get(columnName) != null) {
                Integer columnIndex = columnIndexes.get(columnName);
                if (columns != null && columns.containsKey(columnIndex) &&
                        columns.get(columnIndex) != null && columns.get(columnIndex).getColumnType() != null) {
                    return columns.get(columnIndex).getColumnType();
                }
            }
        }
        return DEFAULT_TYPE;
    }

    private Object getValueAt(int rowIndex, int columnIndex, boolean useDataIndex) {
        Map<Integer, Object> columns;
        if (useDataIndex) {
            columns = getDataRow(rowIndex);
        } else {
            columns = getRow(rowIndex);
        }
        if (columns != null) {
            return columns.get(columnIndex);
        }
        return null;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return getValueAt(rowIndex, columnIndex, false);
    }

    public Object getValueAt(int rowIndex, String columnName) {
        return getValueAt(rowIndex, getColumnIndex(columnName));
    }

    public Object getDataValueAt(int rowIndex, int columnIndex) {
        return getValueAt(rowIndex, columnIndex, true);
    }

    public Object getDataValueAt(int rowIndex, String columnName) {
        return getDataValueAt(rowIndex, getColumnIndex(columnName));
    }

    public void setValueAt(int rowIndex, String columnName, Object value) {
        setValueAt(rowIndex, getColumnIndex(columnName), value);
    }

    public void setValueAt(int rowIndex, int columnIndex, Object value) {
        int convertedRowIndex = getRowIndex(rowIndex);
        Map<Integer, Object> columns = getDataRow(convertedRowIndex);
        if (columns == null) {
            columns = new HashMap<Integer, Object>();
        }
        columns.put(columnIndex, value);
        data.put(convertedRowIndex, columns);
    }

    public int addRow() {
        return addRow(new HashMap<Integer, Object>(1));
    }

    public int addRow(Map<Integer, Object> row) {
        if (data == null) {
            data = new HashMap<Integer, Map<Integer, Object>>(1);
        }
        int rowKey;
        if (getRowCount() == 0) {
            rowKey = 0;
        } else {
            rowKey = data.size();
        }
        data.put(rowKey, row);
        rowIndexes.add(rowKey);
        return rowKey;
    }

    public void addValue(int rowIndex, Object value) {
        addValue(rowIndex, -1, value);
    }

    public void addValue(int rowIndex, int columnIndex, Object value) {
        Map<Integer, Object> columns = getRow(rowIndex);
        if (columns == null) {
            columns = new HashMap<Integer, Object>();
        }
        if (columnIndex < 0) {
            columnIndex = columns.size();
        }
        columns.put(columnIndex, value);
        data.put(rowIndex, columns);
    }

    public Collection<String> getColumnNames() {
        return columnIndexes.keySet();
    }

    public void addColumnType(Integer columnIndex, Integer columnType) {
        DatastoreColumn column;
        if (columns != null && columns.containsKey(columnIndex) && columns.get(columnIndex) != null) {
            column = columns.get(columnIndex);
        } else {
            column = new DatastoreColumn(columnIndex);
        }
        column.setColumnType(columnType);
        columns.put(columnIndex, column);
    }

    public void addColumnLabel(Integer columnIndex, String columnLabel) {
        DatastoreColumn column;
        if (columns != null && columns.containsKey(columnIndex) && columns.get(columnIndex) != null) {
            column = columns.get(columnIndex);
        } else {
            column = new DatastoreColumn(columnIndex);
        }
        column.setColumnLabel(columnLabel);
        columns.put(columnIndex, column);
    }

    public void addColumnName(Integer columnIndex, String columnName) {
        DatastoreColumn column;
        if (columns != null && columns.containsKey(columnIndex) && columns.get(columnIndex) != null) {
            column = columns.get(columnIndex);
            String currentName = column.getColumnName();
            if (currentName != null) {
                columnIndexes.remove(currentName);
            }
        } else {
            column = new DatastoreColumn(columnIndex);
        }
        columnIndexes.put(columnName, columnIndex);
        column.setColumnName(columnName);
        columns.put(columnIndex, column);
    }

    /**
     * ”даление строки как таковой не происходит, просто удал€етс€ возможность
     * выбрать ее извне. “е кто знает индекс столбца, все еще могут добратьс€ до него.
     *
     * @param rowIndex
     */
    public void deleteRow(int rowIndex) {
        int rowDeleteIndex = getRowIndex(rowIndex);
        if (rowIndexes.contains(rowDeleteIndex)) { //rowDeleteIndex >= 0 && rowDeleteIndex < getRowCount()) {
            rowIndexes.remove((Integer)rowDeleteIndex);
        }
    }

    public List<Integer> getRowIndexes() {
        return rowIndexes;
    }

    public Map<Integer, DatastoreColumn> getColumns() {
        return columns;
    }

    public Map<String, Integer> getColumnsIndexes() {
        return columnIndexes;
    }

    /**
     * ѕолучает чистую неотсортированную неотфильтрованную строку.
     * –екомендуетс€ использовать только дл€ доступа до строк, которые
     * не попали в фильтр, сортировку или были удалены.
     *
     * @param dataIndex
     * @return
     */
    public Map<Integer, Object> getDataRow(int dataIndex) {
        return data.get(dataIndex);
    }

    /**
     * ѕровер€ем индекс на наличие в списке доступных строк.
     *
     * @param rowIndex
     * @return
     */
    public Map<Integer, Object> getRow(int rowIndex) {
        return data.get(getRowIndex(rowIndex));
    }

    public int getRowIndex(int dataIndex) {
        int convertedRowIndex = dataIndex;
        if (dataIndex >= 0 && dataIndex < getRowCount()) {
            convertedRowIndex = rowIndexes.get(dataIndex);
        }
        return convertedRowIndex;
    }

    public Map<Integer, Map<Integer, Object>> getRows() {
        return data;
    }

    public void copyMetadata(DatastoreModel model) {
        this.columnIndexes = model.getColumnsIndexes();
        this.columns = model.getColumns();
    }

    public void copy(DatastoreModel model) {
        copyMetadata(model);
        data = model.getRows();
        rowIndexes = model.getRowIndexes();
    }

    public void addColumn(DatastoreColumn column) {
        if (column != null && column.getColumnIndex() > -1) {
            Integer columnIndex = column.getColumnIndex();
            if (column.getColumnName() != null) {
                columns.put(columnIndex, column);
                columnIndexes.put(column.getColumnName(), columnIndex);
            } else if (column.getColumnLabel() != null) {
                columns.put(columnIndex, column);
                columnIndexes.put(column.getColumnLabel(), columnIndex);
            }
        }
    }

    public void setRowIndexes(List<Integer> rowIndexes) {
        this.rowIndexes = rowIndexes;
    }


    public Set<Integer> getDataIndexes() {
        return data.keySet();
    }

    public int deleteColumn(String columnTarget) {
        Integer deletedColumnIndex = -1;
        if (columnIndexes.containsKey(columnTarget)) {
            deletedColumnIndex = columnIndexes.remove(columnTarget);
            columns.remove(deletedColumnIndex);
            for(int i = 0; i<getRowCount(); i++){
            	Map<Integer, Object> columns = getRow(i);
            	columns.remove(deletedColumnIndex);
            }
            
            //data.remove(deletedColumnIndex);
        }
        return deletedColumnIndex;
    }
}
