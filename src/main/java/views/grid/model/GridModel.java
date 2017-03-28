package views.grid.model;

import java.util.Date;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import views.grid.GridColumn;
import views.grid.GridSwing;
import core.reflection.objects.VALIDATOR;

/**
 *  ласс дл€ представлени€ табличной модели, использует в качестве столбцов
 * GridColumn. «начени€ €чеек получаетс€ из
 */
public class GridModel extends AbstractTableModel {

    private static final Logger log = Logger.getLogger(GridModel.class);

    private GridSwing parentGrid = null;

    /**
     * Constructor
     *
     * @param parent - parent grid which contains table
     */
    public GridModel(GridSwing parent) {
        parentGrid = parent;
    }

    // *** TABLE MODEL METHODS ***
    public int getColumnCount() {
        if (parentGrid != null) {
            return parentGrid.getSourceColumns();
        }
        return 0;
    }

    public int getRowCount() {
        if (parentGrid != null) {
            return parentGrid.getSourceRows();
        }
        return 0;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value = "";
        if (rowIndex >= 0 && columnIndex >= 0) {
            try {
                if (parentGrid != null) {
                    value = parentGrid.getSourceValue(rowIndex, columnIndex);
//                    value = parentGrid.getVColumn(columnIndex).valueToString(value);
                }
            }
            catch (Exception e) {
                log.error("Shit happens", e);
            }
        }
        return value;
    }

    // огда заканчиваетс€ редактирование €чейки, вызываетс€ из CellEditor
    @Override
    public void setValueAt(Object newValue, int rowIndex, int columnIndex) {
        try {
            String oldValue = parentGrid.getVColumn(columnIndex).valueToString(getValueAt(rowIndex, columnIndex));
            String newStrValue = parentGrid.getVColumn(columnIndex).valueToString(newValue);
            if ((oldValue == null && newStrValue != null) || (oldValue != null && newStrValue != null && oldValue.compareTo(newStrValue) != 0) && !parentGrid.toDSSaved) {
                parentGrid.setSavedFieldValue(newValue);
                parentGrid.toDS();
            }
        }
        catch (Exception e) {
            log.error("Shit happens", e);
        }
    }

    public String getColumnName(int col) {
        String columnName = "";
        GridColumn column = parentGrid.getVColumn(col);
        if (column != null) {
            columnName = column.getTitle();
        }
        return columnName;
    }

    public boolean isCellEditable(int row, int col) {
        boolean isEditable = true;
        if (row >= 0 && col >= 0) {
            boolean gridEditable = parentGrid.isEditable();
            boolean colEditable = parentGrid.getVColumn(col).isEditable();
            if (gridEditable && !colEditable) {
                isEditable = false;
            } else if (!gridEditable) {
                isEditable = false;
            }
        }
        return isEditable;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        GridColumn visibleColumn = parentGrid.getVColumn(columnIndex);
        if (visibleColumn != null) {
            if(visibleColumn.isArray()){
                return Object[].class;
            }
            int type = GridSwing.getJType(visibleColumn.getType());
            switch (type) {
                case 0:
                    return Double.class;
                case 1:
                    return String.class;
                case 2:
                    return Date.class;                    
                case VALIDATOR.ARRAY_DATE_TYPE:
                case VALIDATOR.ARRAY_STRING_TYPE:
                case VALIDATOR.ARRAY_NUMERIC_TYPE:
                    return Object[].class;
                case VALIDATOR.BOOLEAN_TYPE:
                    return Boolean.class;
                default:
                    return Object.class;
            }
        }
        return Object.class;
    }            
}