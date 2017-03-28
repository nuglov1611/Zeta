package views.grid.model.cross;

import core.rml.RmlConstants;
import views.grid.GridSwing;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Inner class used to define column fields content
 *
 * @author: vagapova.m
 * @since: 25.08.2010
 */
public class CrossColumnModel extends AbstractTableModel {

    private ArrayList<Object[]> columnFields;

    private GridSwing parent;

    public CrossColumnModel(GridSwing parent) {
        this.parent = parent;
        columnFields = new ArrayList<Object[]>();
    }

    public int getColumnCount() {
        return columnFields.size();
    }

    public ArrayList<Object[]> getColumnFields() {
        return columnFields;
    }

    public void setColumnFields(ArrayList<Object[]> columnFields) {
        this.columnFields = columnFields;
    }

    public int getRowCount() {
        boolean descriptionRequired = parent.getBooleanProperty(RmlConstants.DESCRIPTION_REQUIRED);
        int rowCount = parent.getParametersAccessor().getColsSize();
        if (descriptionRequired) {
            rowCount++;
        }
        return rowCount;
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        return (columnFields.get(columnIndex))[rowIndex];
    }
}
