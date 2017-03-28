package views.grid.listener;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import views.grid.GridSwing;
import core.rml.RmlConstants;

public class GridKeyListener extends KeyAdapter {

    private GridSwing grid;

    public GridKeyListener(GridSwing parent) {
        this.grid = parent;
    }

    public void keyPressed(KeyEvent e) {
        if (grid.getDoc().executeShortcut(e)) {
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            grid.getActionManager().processEnterAction();
        } else if (e.getKeyCode() == KeyEvent.VK_INSERT
                && (e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
            if (grid.getStringProperty(RmlConstants.ADD) != null) {
                grid.doAction(grid.getStringProperty(RmlConstants.ADD));
            } else if (grid.isEditable()) {
                grid.getActionManager().insertNewRow();
            }
        } else if (e.getKeyCode() == KeyEvent.VK_DELETE
                && (e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
            grid.getActionManager().deleteCurrentRow();
        } else if (e.getKeyCode() == KeyEvent.VK_S
                && (e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
            //вызываем окно поиска
            grid.getActionManager().showSearchDialog();
        } else if (e.getKeyCode() == KeyEvent.VK_F
                && (e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
            //вызываем окно фильтра
            grid.getActionManager().showFilterDialog();
        } else if (e.getKeyCode() == KeyEvent.VK_CONTEXT_MENU) {
            grid.showPopup(grid.getVisualComponent().getJComponent(), 0, 0);
        } else if (e.getKeyCode() == KeyEvent.VK_C
                && (e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
            grid.getActionManager().copySelected();
        } else if (e.getKeyCode() == KeyEvent.VK_V
                && (e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
            grid.getActionManager().pasteSelected();
        } else if (e.getKeyCode() == KeyEvent.VK_A
                && (e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
            grid.getActionManager().selectAll();
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            grid.getActionManager().processLeftAction();
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            grid.getActionManager().processRightAction();
        } else if (e.getKeyCode() == KeyEvent.VK_UP) {
            boolean extend = false;
            if ((e.getModifiers() & KeyEvent.SHIFT_MASK) != 0)  {
                extend = true;
            }
            grid.getActionManager().switchToPrevRow(extend);
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            boolean extend = false;
            if ((e.getModifiers() & KeyEvent.SHIFT_MASK) != 0)  {
                extend = true;
            }
            grid.getActionManager().switchToNextRow(extend);
        } else if (e.getKeyCode() == KeyEvent.VK_HOME) {
            grid.getActionManager().switchToHome((e.getModifiers() & KeyEvent.CTRL_MASK) != 0);
        } else if (e.getKeyCode() == KeyEvent.VK_END) {
            grid.getActionManager().switchToEnd((e.getModifiers() & KeyEvent.CTRL_MASK) != 0);
        } else if (e.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            grid.getActionManager().processPageUp();
        } else if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            grid.getActionManager().processPageDown();
        } else {
            super.keyPressed(e);
        }
    }
}