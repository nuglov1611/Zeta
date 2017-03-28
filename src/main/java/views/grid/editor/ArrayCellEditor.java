package views.grid.editor;

import views.grid.GridColumn;
import views.grid.GridSwing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ArrayCellEditor extends DefaultCellEditor implements KeyListener, FocusListener, ItemListener {

    private GridSwing parentGrid;
    private GridColumn currColumn;
    private JComboBox currField;


    public ArrayCellEditor(GridSwing parentGrid) {
        super(new JComboBox());
        this.parentGrid = parentGrid;
        this.setClickCountToStart(1);
    }


    public Object getCellEditorValue() {
        if (currField == null)
            return super.getCellEditorValue();

        currColumn.calcHandbookDep();

        currColumn.setDSRow(currField.getSelectedIndex());
        return currField.getSelectedItem();
    }

    public boolean stopCellEditing() {
        boolean res = super.stopCellEditing();
        currField = null;
        return res;
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int rowIndex, int columnIndex) {
        columnIndex = parentGrid.convertColumnIndexToModel(columnIndex);
        currColumn = parentGrid.getVColumn(columnIndex);

        currField = new JComboBox(currColumn.getItems());

        currField.setSelectedItem(value);
        currColumn.setDSRow(currField.getSelectedIndex());

        currField.addKeyListener(this);
        currField.addFocusListener(this);
        currField.addItemListener(this);

        parentGrid.toDSSaved = false;
        return currField;
    }


    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            parentGrid.getActionManager().processEnterAction();
        }
//        else if(currField != null){
//            if(e.getKeyCode() == KeyEvent.VK_UP && currField.getSelectedIndex()>0){
//                currField.setSelectedIndex(currField.getSelectedIndex()-1);
//            }else if(e.getKeyCode() == KeyEvent.VK_DOWN && currField.getSelectedIndex()<currField.getItemCount()){
//                currField.setSelectedIndex(currField.getSelectedIndex()+1);
//            }
//        }
    }


    @Override
    public void keyReleased(KeyEvent e) {
    }


    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void focusGained(FocusEvent e) {
        ((JComboBox) e.getSource()).showPopup();
    }

    @Override
    public void focusLost(FocusEvent e) {
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            stopCellEditing();
        }
    }
}