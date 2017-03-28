package views.grid.action;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class FocusNextAction extends AbstractAction {

    public FocusNextAction() {
    }

    public void actionPerformed(ActionEvent e) {
        FocusManager.getCurrentManager().focusNextComponent();
    }
}