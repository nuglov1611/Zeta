package views.grid.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import views.grid.GridSwing;

/**
 * @author: vagapova.m
 * @since: 27.10.2010
 */
public class SelectAllFilterAction extends AbstractAction {

    private GridSwing grid;

    public SelectAllFilterAction(GridSwing parentGrid, String label) {
        super(label);
        this.grid = parentGrid;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        grid.getActionManager().filterSelectAll();
    }
}
