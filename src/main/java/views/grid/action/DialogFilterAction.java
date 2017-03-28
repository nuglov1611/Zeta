package views.grid.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import views.grid.GridSwing;

/**
 * @author: vagapova.m
 * @since: 27.10.2010
 */
public class DialogFilterAction extends AbstractAction {
    private GridSwing grid;

    public DialogFilterAction(GridSwing grid, String label) {
        super(label);
        this.grid = grid;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        grid.getActionManager().showFilterDialog();
    }
}
