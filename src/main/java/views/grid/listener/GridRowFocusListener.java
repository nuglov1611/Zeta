package views.grid.listener;

import views.grid.manager.GridTableManager;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class GridRowFocusListener implements FocusListener {

    private GridTableManager tableManager;

    public GridRowFocusListener(GridTableManager tableManager) {
        this.tableManager = tableManager;
    }

    public void focusGained(FocusEvent e) {
        tableManager.requestFocusThis();
    }

    public void focusLost(FocusEvent e) {
    }
}