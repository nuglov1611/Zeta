package core.rml.dbi;

/**
* @author: vagapova.m
* @since: 14.10.2010
*/
public class DatastoreColumn {  

/**
 * ����� ����� ����������� �� ���� ������(�������� ��� ������� sql
 * ���������) String
 */
    private String columnName;

    /**
     * ����� ����� ����������� �� ���� ������ String
     */
    private String columnLabel;

/**
 * ���� �������� int
 */
    private Integer columnType;

    private Integer columnIndex;

    DatastoreColumn(Integer columnIndex) {
        this.columnIndex = columnIndex;
    }

    public DatastoreColumn(String columnName, Integer columnIndex, String columnLabel, Integer columnType) {
        this(columnIndex);
        this.columnName = columnName;
        this.columnLabel = columnLabel;
        this.columnType = columnType;
    }

    public Integer getColumnType() {
        return columnType;
    }

    public void setColumnType(Integer columnType) {
        this.columnType = columnType;
    }

    public String getColumnLabel() {
        return columnLabel;
    }

    public void setColumnLabel(String columnLabel) {
        this.columnLabel = columnLabel;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Integer getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(Integer columnIndex) {
        this.columnIndex = columnIndex;
    }
}
