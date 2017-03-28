package views.grid.listener;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTable;

import views.grid.manager.GridTableManager;

public class GridFocusListener implements FocusListener {

    private GridTableManager tableManager;

    public GridFocusListener(GridTableManager tableManager) {
        this.tableManager = tableManager;
    }

    public void focusGained(FocusEvent e) {
        if (e.getSource() == tableManager.getParent()) {
            tableManager.requestFocusThis();
        } else {
            tableManager.setSelectedBackground((JTable) e.getSource());
        }
    }

    public void focusLost(FocusEvent e) {
        if (e.getSource() != tableManager.getParent()) {
            tableManager.setUnselectedBackground((JTable) e.getSource());
        }
    }
}