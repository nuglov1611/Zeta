package views.grid.listener;

import views.grid.manager.GridTableManager;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

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