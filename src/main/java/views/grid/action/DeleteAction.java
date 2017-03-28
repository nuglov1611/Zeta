package views.grid.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import views.grid.GridSwing;

public class DeleteAction extends AbstractAction {

    private GridSwing grid;

    public DeleteAction(GridSwing parentGrid) {
        this.grid = parentGrid;
    }

    public void actionPerformed(ActionEvent e) {
        grid.getActionManager().deleteCurrentRow();
    }
}