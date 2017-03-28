package views.grid.model.cross.parameters;

/**
 * @author: vagapova.m
 * @since: 24.10.2010
 */
public class CrossField {

    /**
     * column name in original TableModel, that identify this field
     */
    protected String columnName;

    /**
     * column description
     */
    protected String description;

    /**
     * column width, expressed in pixels
     */
    protected int width;

    public CrossField(String columnName) {
        this(columnName, columnName);
    }

    public CrossField(String columnName, String description) {
        this(columnName, 0, description);
    }

    public CrossField(String columnName, int width) {
        this(columnName, width, columnName);
    }

    public CrossField(String columnName, int width, String description) {
        this.columnName = columnName.toUpperCase();
        this.description = description;
        this.width = width;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
