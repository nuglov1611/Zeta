package views.grid.renderer.grid;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import views.grid.GridSwing;

/**
 * @author: vagapova.m
 * @since: 19.12.2010
 */
public class GridColumnHeader extends JTableHeader {

    public GridColumnHeader(GridSwing grid, TableColumnModel model) {
        super(model);
        setTable(grid.getTableManager().getDataTable());
        setUI(new GridColumnHeaderUI(grid));
        setReorderingAllowed(false);
    }

    public void setReorderingAllowed(boolean b) {
        reorderingAllowed = false;
    }

    @Override
    public void setTable(JTable table) {
        if (table != null) {
            super.setTable(table);
        }
    }
}

