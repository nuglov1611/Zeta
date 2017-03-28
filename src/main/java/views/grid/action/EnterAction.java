package views.grid.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import views.grid.GridSwing;

public class EnterAction extends AbstractAction {

    private GridSwing grid;

    public EnterAction(GridSwing parentGrid) {
        this.grid = parentGrid;
    }

    public void actionPerformed(ActionEvent e) {
        grid.getActionManager().processEnterAction();
    }
}