package views.grid.listener;

import views.grid.manager.GridTableManager;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GridHeaderMouseListener extends MouseAdapter {

    private GridTableManager tableManager;

    public GridHeaderMouseListener(GridTableManager tableManager) {
        this.tableManager = tableManager;
    }

    public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            tableManager.getParent().showFilterMenu(e.getPoint(), true);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            tableManager.toggleNextSortMode(e.getX());
            tableManager.requestFocusThis();
        }
    }
}
