package views.grid.action;

import views.grid.GridSwing;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class InsertAction extends AbstractAction {

    private GridSwing grid;

    public InsertAction(GridSwing parentGrid) {
        this.grid = parentGrid;
    }

    public void actionPerformed(ActionEvent e) {
        grid.getActionManager().insertNewRow();
    }
}