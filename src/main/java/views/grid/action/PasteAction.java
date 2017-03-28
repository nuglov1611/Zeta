package views.grid.action;

import views.grid.GridSwing;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class PasteAction extends AbstractAction {

    private GridSwing grid;

    public PasteAction(GridSwing parentGrid) {
        this.grid = parentGrid;
    }

    public void actionPerformed(ActionEvent e) {
        grid.getActionManager().pasteSelected();
    }
}