package views.grid.listener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.apache.log4j.Logger;

import views.grid.GridSwing;
import core.rml.RmlConstants;

public class GridMouseListener extends MouseAdapter {

    private static final Logger log = Logger.getLogger(GridMouseListener.class);

    private GridSwing grid;

    public GridMouseListener(GridSwing grid) {
        this.grid = grid;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
    }

    public void mousePressed(MouseEvent e) {
        if (e.getSource() instanceof JTable) {
            if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON3) {
                grid.getTableManager().setCurrentColumn(e.getX(), e.getY(), true);
                grid.getTableManager().setCurrentRow(e.getX(), e.getY());
                grid.getTableManager().setRowSelected(false);
                grid.showPopup((JTable) e.getSource(), e.getX(), e.getY());
            }
        } else if (e.getClickCount() == 1 && e.getSource() instanceof JScrollPane &&
                e.getButton() == MouseEvent.BUTTON3) {
            grid.showPopup((JScrollPane)e.getSource(), e.getX(), e.getY());
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!grid.getVisualComponent().isFocusOwner()) {
            grid.requestFocusThis();
        }
        if (e.getSource() instanceof JTable) {
            if (e.getClickCount() == 2 && !grid.isEditable() &&
                    grid.getStringProperty(RmlConstants.EDIT) != null &&
                    e.getButton() == MouseEvent.BUTTON1) {
                try {
                    grid.doAction(grid.getStringProperty(RmlConstants.EDIT));
                } catch (Exception e1) {
                    log.error("Shit happens", e1);
                }
                //Selection performing in GridRowSelectionListener
            }
        }
    }
}
