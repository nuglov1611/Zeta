package views.grid.listener;

import views.grid.manager.GridTableManager;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class GridComponentListener extends ComponentAdapter {

    private Dimension currentSize;

    private GridTableManager tableManager;

    public GridComponentListener(GridTableManager tableManager) {
        this.tableManager = tableManager;
    }

    public void componentResized(ComponentEvent e) {
        if (tableManager.needScrollLater()) {
            tableManager.scrollToRow(tableManager.getCurrentRow(), false);
            tableManager.refreshView();
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
}
