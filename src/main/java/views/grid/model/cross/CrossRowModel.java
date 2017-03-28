package views.grid.model.cross;

import views.grid.GridSwing;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Inner class used to define row fields content.
 *
 * @author: vagapova.m
 * @since: 27.09.2010
 */
public class CrossRowModel extends AbstractTableModel {

    private ArrayList<Object[]> rowFields;

    private GridSwing parent;

    public CrossRowModel(GridSwing parent) {
        this.parent = parent;
        rowFields = new ArrayList<Object[]>();
    }

    public void setRowFields(ArrayList<Object[]> rowFields) {
        this.rowFields = rowFields;
    }

    public int getColumnCount() {
        return parent.getParametersAccessor().getRowsSize();
    }

    public ArrayList<Object[]> getRowFields() {
        return rowFields;
    }

    public int getRowCount() {
        return rowFields.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return (rowFields.get(rowIndex))[columnIndex];
    }
}
