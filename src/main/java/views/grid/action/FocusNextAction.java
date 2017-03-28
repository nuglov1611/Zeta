package views.grid.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.FocusManager;

public class FocusNextAction extends AbstractAction {

    public FocusNextAction() {
    }

    public void actionPerformed(ActionEvent e) {
        FocusManager.getCurrentManager().focusNextComponent();
    }
}