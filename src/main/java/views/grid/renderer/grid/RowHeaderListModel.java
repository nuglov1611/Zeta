package views.grid.renderer.grid;

import javax.swing.AbstractListModel;

import views.grid.GridSwing;

public class RowHeaderListModel extends AbstractListModel {

    private GridSwing grid;

    public RowHeaderListModel(GridSwing gridSwing) {
        grid = gridSwing;
    }

    public int getSize() {
        return grid.getTableManager().getRowCount();
    }

    public Object getElementAt(int index) {
        if (grid.getTableManager().containsRowTitle(grid.getTableManager().convertRowIndexToModel(index) + 1)) {
            return grid.getTableManager().getRowTitle(grid.getTableManager().convertRowIndexToModel(index) + 1);
        } else if (grid.getTableManager().inFilteringMode()) {
            return grid.getTableManager().getRowHeaderViewIndexByModelIndex(
                    grid.getTableManager().convertRowIndexToModel(index)) + 1;
        } else {
            return index + 1;
        }
    }
}