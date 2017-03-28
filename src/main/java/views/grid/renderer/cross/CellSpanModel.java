package views.grid.renderer.cross;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: vagapova.m
 * @since: 21.10.2010
 */
public class CellSpanModel {

    public static final int ROW = 0;

    public static final int COLUMN = 1;

    protected int rowSize;

    protected int columnSize;

    protected Map<Integer, Map<Integer, CellSpan>> span;

    public CellSpanModel() {
        this(0, 0);
    }

    public CellSpanModel(int rowSize, int columnSize) {
        this.rowSize = rowSize;
        this.columnSize = columnSize;
        span = new HashMap<Integer, Map<Integer, CellSpan>>();
        setSize(rowSize, columnSize);
    }

    private Map<Integer, CellSpan> buildColumnSpan(int columnSize) {
        Map<Integer, CellSpan> columns = new HashMap<Integer, CellSpan>();
        for (int column = 0; column < columnSize; column++) {
            columns.put(0, new CellSpan(1, 1));
        }
        return columns;
    }

    public int getColumnSpan(int row, int column) {
        CellSpan cellSpan = getCellSpan(row, column);
        if (cellSpan != null) {
            return cellSpan.getColumnSpan();
        }
        return 1;
    }

    public int getRowSpan(int row, int column) {
        CellSpan cellSpan = getCellSpan(row, column);
        if (cellSpan != null) {
            return cellSpan.getRowSpan();
        }
        return 1;
    }

    public CellSpan getCellSpan(int row, int column) {
        CellSpan cellSpan = null;
        Map<Integer, CellSpan> columns = span.get(row);
        if (columns != null) {
            cellSpan = columns.get(column);
        }
        if (cellSpan == null) {
            cellSpan = new CellSpan(1, 1);
        }
        return cellSpan;
    }

    public void addColumn() {
        for (Integer row : span.keySet()) {
            Map<Integer, CellSpan> columns = span.get(row);
            columns.put(columnSize, new CellSpan(1, 1));
        }
        columnSize++;
    }

    public void addRow() {
        Map<Integer, CellSpan> columns = buildColumnSpan(columnSize);
        span.put(rowSize, columns);
        rowSize++;
    }

    public void insertRow(int row) {
        Map<Integer, Map<Integer, CellSpan>> tmpSpan = new HashMap<Integer, Map<Integer, CellSpan>>();
        Integer newRowIndex;
        for (Integer rowIndex : span.keySet()) {
            if (rowIndex >= row) {
                newRowIndex = rowIndex + 1;
                tmpSpan.put(newRowIndex, span.get(rowIndex));
            }
        }
        span.putAll(tmpSpan);
        Map<Integer, CellSpan> columns = buildColumnSpan(columnSize);
        span.put(row, columns);
        rowSize++;
    }


    public void split(int row, int column) {
        CellSpan cellSpan = getCellSpan(row, column);
        if (cellSpan != null) {
            int columnSpan = cellSpan.getColumnSpan();
            int rowSpan = cellSpan.getRowSpan();
            for (int i = 0; i < rowSpan; i++) {
                for (int j = 0; j < columnSpan; j++) {
                    CellSpan splitSpan = getCellSpan(row + i, column + j);
                    if (splitSpan != null) {
                        splitSpan.setColumnSpan(1);
                        splitSpan.setRowSpan(1);
                    }
                }
            }
        }
    }

    public void combine(List<Integer> rowSpans, List<Integer> columnSpans) {
        int rowSpanSize = rowSpans.size();
        int columnSpanSize = columnSpans.size();
        int startRow = rowSpanSize == 0 ? 0 : rowSpans.get(0);
        int startColumn = columnSpanSize == 0 ? 0 : columnSpans.get(0);
        for (int i = 0; i < rowSpanSize; i++) {
            for (int j = 0; j < columnSpanSize; j++) {
                if (getColumnSpan(startRow + i, startColumn + j) != 1
                        || getRowSpan(startRow + i, startColumn + j) != 1) {
                    return;
                }
            }
        }
        for (int i = 0, ii = 0; i < rowSpanSize; i++, ii--) {
            for (int j = 0, jj = 0; j < columnSpanSize; j++, jj--) {
                setCellSpan(startRow + i, startColumn + j, new CellSpan(ii, jj));
            }
        }
        setCellSpan(startRow, startColumn, new CellSpan(rowSpanSize, columnSpanSize));
    }

    private void setCellSpan(Integer row, Integer column, CellSpan newSpan) {
        Map<Integer, CellSpan> columns = span.get(row);
        if (columns == null) {
            columns = new HashMap<Integer, CellSpan>();
        }
        columns.put(column, newSpan);
        span.put(row, columns);
    }

    public void setSize(int rowSize, int columnSize) {
        this.rowSize = rowSize;
        this.columnSize = columnSize;
        for (int i = 0; i < rowSize; i++) {
            Map<Integer, CellSpan> columns = buildColumnSpan(columnSize);
            span.put(i, columns);
        }
    }
}
