package views.grid.dnd;

import org.apache.log4j.Logger;
import views.grid.GridSwing;
import views.grid.manager.GridTableManager;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

/**
 * @author Alex
 */
public class GridTransferHandler extends TransferHandler {

    private static final Logger log = Logger.getLogger(GridTransferHandler.class);

    private GridSwing grid;

    public GridTransferHandler(GridSwing grid) {
        this.grid = grid;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    public
    @Override
    Transferable createTransferable(JComponent c) {
        return new StringSelection((String) grid.getTableManager().getValueAt(grid.getTableManager().getCurrentRow(), grid.getTableManager().getCurrentColumn()));
    }

    @Override
    public void exportDone(JComponent c, Transferable t, int action) {
        /* if (action == MOVE) {
            c.removeSelection();
        }*/
    }

    @Override
    public boolean canImport(TransferSupport supp) {
        // Check for String flavor
        return supp.isDataFlavorSupported(DataFlavor.stringFlavor);
    }

    @Override
    public boolean importData(TransferSupport supp) {
        if (!canImport(supp)) {
            return false;
        }

        // Fetch the Transferable and its data
        Transferable t = supp.getTransferable();
        try {
            String data = (String) t.getTransferData(DataFlavor.stringFlavor);
            // Fetch the drop location
            JTable.DropLocation loc = grid.getTableManager().getDataTable().getDropLocation();

            int viewRowIndex = loc.getRow();
            int viewColIndex = loc.getColumn();
            if (viewRowIndex != GridTableManager.DEFAULT_ROW && viewColIndex != GridTableManager.DEFAULT_COLUMN) {
                grid.getTableManager().setCurrentColumn(viewColIndex, true);
                grid.getTableManager().setCurrentRow(viewRowIndex, true, false);
                grid.setToDSSaved(false);
                // Insert the data at this location
                grid.getTableManager().getDataTable().getModel().setValueAt(data, viewRowIndex, viewColIndex);
                grid.notifyHandler(null);
                grid.getTableManager().getVColumn(viewColIndex).runDropHook();
            }

            return true;
        } catch (Exception e) {
            log.error("Shit happens: " + e.getMessage());
        }

        return false;
    }
}
