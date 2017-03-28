package views.grid.action;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class FocusPrevAction extends AbstractAction {

    public FocusPrevAction() {
    }

    public void actionPerformed(ActionEvent e) {
        FocusManager.getCurrentManager().focusPreviousComponent();
    }
}