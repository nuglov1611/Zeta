package views.grid.action;

import views.grid.GridSwing;

import javax.swing.*;
import java.awt.event.ActionEvent;

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
