package views.grid.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import views.grid.GridSwing;

public class PasteAction extends AbstractAction {

    private GridSwing grid;

    public PasteAction(GridSwing parentGrid) {
        this.grid = parentGrid;
    }

    public void actionPerformed(ActionEvent e) {
        grid.getActionManager().pasteSelected();
    }
}