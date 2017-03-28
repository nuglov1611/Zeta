package views.grid.listener;

import views.grid.GridSwing;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author: vagapova.m
 * @since: 27.10.2010
 */
public class GridHeaderPopupMouseListener extends MouseAdapter {

    private GridSwing grid;

    public GridHeaderPopupMouseListener(GridSwing grid) {
        this.grid = grid;
    }

    public void mouseMoved(MouseEvent me) {
        if (me.getSource() instanceof JList) {
            JList list = (JList) me.getSource();
            list.setSelectedIndex(list.locationToIndex(me.getPoint()));
        }
    }

    @Override
    public void mouseExited(MouseEvent me) {
        if (me.getSource() instanceof JList) {
            JList list = (JList) me.getSource();
            list.clearSelection();
        }
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        if (me.getButton() == MouseEvent.BUTTON1 && me.getSource() instanceof JList) {
            grid.getActionManager().filterRow(me.getPoint(), (JList) me.getSource());
        }
    }
}
