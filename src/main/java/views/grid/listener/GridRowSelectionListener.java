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
public class GridRowSelectionListener implements ListSelectionListener {

    private static final Logger log = Logger.getLogger(GridRowSelectionListener.class);

    private GridTableManager gridTableManager;

    public GridRowSelectionListener(GridTableManager gridTableManager) {
        this.gridTableManager = gridTableManager;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        //Ignore extra messages.
        if (e.getValueIsAdjusting()) {
            return;
        }

        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        if (!lsm.isSelectionEmpty()) {
            int selectedRow = lsm.getLeadSelectionIndex();
            if (selectedRow != GridTableManager.DEFAULT_ROW) {
                int nextModelRow = gridTableManager.convertRowIndexToModel(selectedRow);
                if (nextModelRow != GridTableManager.DEFAULT_ROW && gridTableManager.getCurrentRow() != nextModelRow) {
                    gridTableManager.setCurrentRow(selectedRow, true, false);
                } else if (!gridTableManager.getParent().isDsEmpty() && gridTableManager.getParent().getDatastore().getCurRow() != selectedRow) {
                    // Запоминаем новую текущую строку в датасторе, возможна рассинхронизация ввиду скриптовых вызовов setCurRow для датастори
                    gridTableManager.getParent().saveDsCurRow(selectedRow);
                }
            }
        }
    }
}
