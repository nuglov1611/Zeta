package views.grid.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.FocusManager;

public class FocusPrevAction extends AbstractAction {

    public FocusPrevAction() {
    }

    public void actionPerformed(ActionEvent e) {
        FocusManager.getCurrentManager().focusPreviousComponent();
    }
}