package views.grid.listener;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import views.grid.manager.GridTableManager;

public class GridWindowListener extends WindowAdapter {

    private Dimension currentSize;
    private GridTableManager tableManager;

    public GridWindowListener(GridTableManager tableManager) {
        this.tableManager = tableManager;
    }

    @Override
    public void windowActivated(WindowEvent e) {
        if (tableManager.isFirstTime()) {
            tableManager.activateTable();
            tableManager.setFirstTime(false);
        }
        if (tableManager.needScrollLater()) {
            tableManager.scrollToRow(tableManager.getCurrentRow(), false);
        }
        if (tableManager.needAllignLater()) {
            tableManager.allign();
        }
        if (tableManager.needAllign()) {
            Dimension newSize = e.getComponent().getSize();
            if ((currentSize != null && currentSize.width != newSize.width) || currentSize == null) {
                tableManager.allign(); 
            }
            currentSize = newSize;
        }
    }

    @Override
    public void windowStateChanged(WindowEvent e) {
        super.windowStateChanged(e);
    }
}
