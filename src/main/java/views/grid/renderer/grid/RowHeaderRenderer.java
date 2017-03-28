package views.grid.renderer.grid;

import core.rml.RmlConstants;
import views.grid.GridSwing;
import views.grid.renderer.GridRowHeader;

import javax.swing.*;
import java.awt.*;

public class RowHeaderRenderer extends JButton implements ListCellRenderer {

    private GridSwing owner;

    public RowHeaderRenderer(GridSwing newOwner) {
        owner = newOwner;
    }

    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index, boolean isSelected,
                                                  boolean cellHasFocus) {
        setFont(owner.getFontProperty(RmlConstants.BUTTON_BAR_FONT));
        setBackground(owner.getTableManager().getUIManager().getRowTitleBgColor(index + 1));
        setForeground(owner.getTableManager().getUIManager().getRowTitleFgColor(index + 1));
        setMargin(new Insets(1, 1, 1, 1));
        setText(value != null ? String.valueOf(value) : "...");
        if (list instanceof GridRowHeader && ((GridRowHeader) list).getRowSize() != null) {
            int rowHeight = ((GridRowHeader) list).getRowSize().get(index);
            if (rowHeight > 0) {
                Dimension bSize = getPreferredSize();
                bSize.setSize(bSize.getWidth(), rowHeight);
                setPreferredSize(bSize);
            }
        }
        return this;
    }
}