package views.grid.listener;

import org.apache.log4j.Logger;
import views.grid.manager.GridTableManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * User: marina.vagapova
 * Date: 2/23/13
 */
public class GridColumnSelectionListener implements ListSelectionListener {
    private static final Logger log = Logger.getLogger(GridColumnSelectionListener.class);

    private GridTableManager gridTableManager;

    public GridColumnSelectionListener(GridTableManager gridTableManager) {
        this.gridTableManager = gridTableManager;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        //Ignore extra messages.
        if (e.getValueIsAdjusting()) return;

        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        if (!lsm.isSelectionEmpty()) {
            int selectedColumn = lsm.getLeadSelectionIndex();
            if (selectedColumn != GridTableManager.DEFAULT_COLUMN) {
                int modelColumn = gridTableManager.convertColumnIndexToModel(selectedColumn);
                if (modelColumn != GridTableManager.DEFAULT_COLUMN && modelColumn != gridTableManager.getCurrentColumn()) {
                    gridTableManager.setCurrentColumn(selectedColumn, true);
                }
            }
        }
    }
}
