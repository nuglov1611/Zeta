package views.grid.listener;

import org.apache.log4j.Logger;
import views.grid.GridSwing;
import views.menu.ZMenuItem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GridPopupActionListener implements ActionListener {

    private static final Logger log = Logger.getLogger(GridPopupActionListener.class);

    private GridSwing parent;

    public GridPopupActionListener(GridSwing parent) {
        this.parent = parent;
    }

    public void actionPerformed(ActionEvent e) {
        if (parent.isEditing()) {
            parent.stopEditing();
        }
        String command = e.getActionCommand();
        log.debug("Action performed:" + command);
        try {
            parent.getDoc().doAction(command, parent);
        } catch (Exception ex) {
            log.error("Shit happens", ex);
        }
        if (e.getSource() instanceof ZMenuItem) {
            String script = ((ZMenuItem) e.getSource()).getExp();
            try {
                parent.getDoc().executeScript(script, false);
            } catch (Exception ex) {
                log.error("Shit happens", ex);
            }
        }
    }
}
