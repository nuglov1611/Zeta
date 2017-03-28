package views.grid.editor;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import views.grid.GridColumn;
import views.grid.GridSwing;
import core.rml.RmlConstants;

public class BooleanCellEditor extends DefaultCellEditor implements KeyListener  {

    private GridSwing parentGrid;
    private GridColumn currColumn;
    private JCheckBox currField;
    
    
    public BooleanCellEditor(GridSwing parentGrid) {
        super(new JCheckBox());
        this.parentGrid = parentGrid;
        this.setClickCountToStart(1);
    }

    
    
    public Object getCellEditorValue() {
        if(currField == null)
            return super.getCellEditorValue();

        return currField.isSelected();
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int rowIndex, int columnIndex) {
        columnIndex = parentGrid.convertColumnIndexToModel(columnIndex);
        currColumn = parentGrid.getVColumn(columnIndex);
        if(value == null){
            value = true;
        }

        currField = new JCheckBox("", (Boolean) value);
        String halignment = currColumn.getStringProperty(RmlConstants.HALIGNMENT);
        if ("LEFT".equals(halignment.toUpperCase())) {
            currField.setHorizontalAlignment(SwingConstants.LEFT);
        } else if ("CENTER".equals(halignment.toUpperCase())) {
            currField.setHorizontalAlignment(SwingConstants.CENTER);
        } else if ("RIGHT".equals(halignment.toUpperCase())) {
            currField.setHorizontalAlignment(SwingConstants.RIGHT);
        }

        currField.addKeyListener(this);
        parentGrid.toDSSaved = false;
        return (Component) currField;
    }



    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            parentGrid.getActionManager().processEnterAction();
        }
    }



    @Override
    public void keyReleased(KeyEvent e) {
    }



    @Override
    public void keyTyped(KeyEvent e) {
    }
}