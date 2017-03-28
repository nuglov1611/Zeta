package views.grid.editor;

import views.grid.GridColumn;
import views.grid.GridSwing;

import javax.swing.*;
import java.awt.*;

public class TextFieldCellEditor extends DefaultCellEditor {

    private GridSwing parentGrid;
    private CommonField currField;

    public TextFieldCellEditor(GridSwing parentGrid) {
        super(new JTextField());
        this.parentGrid = parentGrid;
    }

    public Object getCellEditorValue() {
        if (currField == null)
            return super.stopCellEditing();
        return currField.getValue();
    }

    public boolean stopCellEditing() {
        if (currField == null)
            return super.stopCellEditing();
        String newText = currField.getText();
        if (!currField.isValid(newText)) {
            boolean undo = currField.selectUndo();
            if (!undo) {
                return false;
            } else {
                currField.setValue(currField.getValue());
            }
        } else {
            currField.setValue(newText);
        }
        return super.stopCellEditing();
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int rowIndex, int columnIndex) {
        columnIndex = parentGrid.convertColumnIndexToModel(columnIndex);

        GridColumn currColumn = parentGrid.getVColumn(columnIndex);

        currField = GridFieldFactory.getInstance().
                createField(parentGrid, currColumn, value, parentGrid.isEditable());

        parentGrid.toDSSaved = false;
        return currField;
    }
}