package views.grid.renderer.cross;

/**
 * @author: vagapova.m
 * @since: 19.10.2010
 */
public class CellSpanPosition {

    private int rowIndex;
    private int columnIndex;

    public CellSpanPosition(int rowIndex, int columnIndex) {
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }
}
