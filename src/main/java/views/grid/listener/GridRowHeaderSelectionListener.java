package views.grid.listener;

import views.grid.manager.GridTableManager;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GridRowHeaderSelectionListener extends MouseAdapter {

    private GridTableManager tableManager;

    public GridRowHeaderSelectionListener(GridTableManager tableManager) {
        this.tableManager = tableManager;
    }

    public void mouseClicked(MouseEvent e) {
        tableManager.requestFocusThis();
        if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1) {
            tableManager.setRowSelected(true);
            tableManager.setCurrentColumn(GridTableManager.DEFAULT_COLUMN);
            tableManager.setCurrentRowByHeader(e.getX(), e.getY());
        }
    }
}
