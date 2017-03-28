package views.grid.action;

import views.grid.GridSwing;

import javax.swing.*;
import java.awt.event.ActionEvent;

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
