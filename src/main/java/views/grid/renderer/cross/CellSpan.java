package views.grid.renderer.cross;

/**
 * @author: vagapova.m
 * @since: 19.10.2010
 */
public class CellSpan {

    private int rowSpan;

    private int columnSpan;

    public CellSpan(int rowSpan, int columnSpan) {
        this.rowSpan = rowSpan;
        this.columnSpan = columnSpan;
    }

    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }

    public void setColumnSpan(int columnSpan) {
        this.columnSpan = columnSpan;
    }

    public int getRowSpan() {
        return rowSpan;
    }

    public int getColumnSpan() {
        return columnSpan;
    }

    public void setSpan(CellSpan span) {
        this.columnSpan = span.getColumnSpan();
        this.rowSpan = span.getRowSpan();
    }
}
