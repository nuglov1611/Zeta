package views.grid.renderer.cross;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Enumeration;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public class CellSpanTable extends JTable {

    public static final int ROW = 0;
    public static final int COLUMN = 1;

    private CellSpanModel cellSpanModel;
//    protected int[][][] span;

    public CellSpanTable() {
        setShowGrid(false);
        this.getColumnModel().setColumnMargin(0);
        this.setRowMargin(0);
        cellSpanModel = new CellSpanModel();
//    setUI(new CellSpanTableUI());
//    getTableHeader().setReorderingAllowed(false);
//    setCellSelectionEnabled(true);
//    setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    }


    public void setModel(TableModel model) {
        super.setModel(model);
        setUI(new CellSpanTableUI());
//      setShowGrid(false);
//      setGridSize(new Dimension(model.getColumnCount(),model.getRowCount()));
    }


    public Rectangle getCellRect(int row, int column, boolean includeSpacing) {
        Rectangle rect = super.getCellRect(row, column, includeSpacing);
        if ((row < 0) || (column < 0) ||
                (getRowCount() <= row) || (getColumnCount() <= column)) {
            return rect;
        }
        if (!isGridVisible(row, column)) {
            int temp_row = row;
            int temp_column = column;
            row += cellSpanModel.getRowSpan(temp_row, temp_column);
            column += cellSpanModel.getColumnSpan(temp_row, temp_column);
        }
        CellSpan cellSpan = cellSpanModel.getCellSpan(row, column);

        int index = 0;
        int columnMargin = getColumnModel().getColumnMargin();

        Rectangle cellFrame = new Rectangle();
        int aCellHeight = rowHeight + rowMargin;
        cellFrame.y = row * aCellHeight;
        cellFrame.height = cellSpan.getRowSpan() * aCellHeight;

        Enumeration enumeration = getColumnModel().getColumns();
        while (enumeration.hasMoreElements()) {
            TableColumn aColumn = (TableColumn) enumeration.nextElement();
            cellFrame.width = aColumn.getWidth() + columnMargin;
            if (index == column) break;
            cellFrame.x += cellFrame.width;
            index++;
        }
        try {
            for (int i = 0; i < cellSpan.getColumnSpan() - 1; i++) {
                TableColumn aColumn = (TableColumn) enumeration.nextElement();
                cellFrame.width += aColumn.getWidth() + columnMargin;
            }
        }
        catch (Exception ignored) {
        }
        if (!includeSpacing) {
            Dimension spacing = getIntercellSpacing();
            cellFrame.setBounds(cellFrame.x + spacing.width / 2,
                    cellFrame.y + spacing.height / 2,
                    cellFrame.width - spacing.width,
                    cellFrame.height - spacing.height);
        }
        return cellFrame;
    }

    private CellSpan rowColumnAtPoint(Point point) {
        CellSpan retValue = new CellSpan(-1, -1);
        int row = point.y / (rowHeight + rowMargin);
        if ((row < 0) || (getRowCount() <= row)) return retValue;
        int column = getColumnModel().getColumnIndexAtX(point.x);

        if (isGridVisible(row, column)) {
            retValue.setColumnSpan(column);
            retValue.setRowSpan(row);
            return retValue;
        }
        retValue.setSpan(cellSpanModel.getCellSpan(row, column));
        return retValue;
    }

    public int rowAtPoint(Point point) {
        return rowColumnAtPoint(point).getRowSpan();
    }

    public int columnAtPoint(Point point) {
        return rowColumnAtPoint(point).getColumnSpan();
    }


    public void columnSelectionChanged(ListSelectionEvent e) {
        repaint();
    }

    public void valueChanged(ListSelectionEvent e) {
        int firstIndex = e.getFirstIndex();
        int lastIndex = e.getLastIndex();
        if (firstIndex == -1 && lastIndex == -1) { // Selection cleared.
            repaint();
        }
        Rectangle dirtyRegion = getCellRect(firstIndex, 0, false);
        int numColumns = getColumnCount();
        int index = firstIndex;
        for (int i = 0; i < numColumns; i++) {
            dirtyRegion.add(getCellRect(index, i, false));
        }
        index = lastIndex;
        for (int i = 0; i < numColumns; i++) {
            dirtyRegion.add(getCellRect(index, i, false));
        }
        repaint(dirtyRegion.x, dirtyRegion.y, dirtyRegion.width, dirtyRegion.height);
    }

    public boolean isGridVisible(int row, int column) {
        CellSpan cellSpan = cellSpanModel.getCellSpan(row, column);
        return !((cellSpan.getColumnSpan() < 1)
                || (cellSpan.getRowSpan() < 1));
    }

    public void setGridSize(Dimension size) {
        int columnSize = size.width;
        int rowSize = size.height;
        cellSpanModel.setSize(rowSize, columnSize);
    }

    public void combine(java.util.List<Integer> rowSpans, java.util.List<Integer> columnSpans) {
        cellSpanModel.combine(rowSpans, columnSpans);
    }

    public void split(int row, int column) {cellSpanModel.split(row, column);}

    public void addRow() {cellSpanModel.addRow();}

    public void addColumn() {cellSpanModel.addColumn();}

    public void insertRow(int row) {cellSpanModel.insertRow(row);}

    public int getColumnSpan(int row, int column) {
        return cellSpanModel.getColumnSpan(row, column);
    }

    public int getRowSpan(int row, int column) {
        return cellSpanModel.getRowSpan(row, column);
    }

    public CellSpan getCellSpan(int row, int column) {
        return cellSpanModel.getCellSpan(row, column);
    }

    public void setCellSpanModel(CellSpanModel cellSpanModel) {
        this.cellSpanModel = cellSpanModel;
    }
}
