package views.grid.renderer.cell;

import core.rml.RmlConstants;
import views.grid.GridColumn;
import views.grid.manager.GridTableManager;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class BooleanCellRenderer implements TableCellRenderer {
    private GridTableManager tableManager;

    public BooleanCellRenderer(GridTableManager tableManager) {
        super();
        this.tableManager = tableManager;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {

        column = tableManager.convertColumnIndexToModel(column);
        GridColumn currColumn = tableManager.getVColumn(column);

        if (value == null) {
            value = true;
        }
        JPanel panel = new JPanel(new BorderLayout());
        JCheckBox check = new JCheckBox("", (Boolean) value);
        panel.add(check, BorderLayout.CENTER);
        if (isSelected) {
            panel.setForeground(table.getSelectionForeground());
            panel.setBackground(table.getSelectionBackground());
        } else {
            Color bgColor = tableManager.getUIManager().getBgColor(row, column);
            if (bgColor != null) {
                panel.setBackground(bgColor);
            }
        }

        String halignment = currColumn.getStringProperty(RmlConstants.HALIGNMENT);
        if ("LEFT".equals(halignment.toUpperCase())) {
            check.setHorizontalAlignment(SwingConstants.LEFT);
        } else if ("CENTER".equals(halignment.toUpperCase())) {
            check.setHorizontalAlignment(SwingConstants.CENTER);
        } else if ("RIGHT".equals(halignment.toUpperCase())) {
            check.setHorizontalAlignment(SwingConstants.RIGHT);
        }

        if (hasFocus && !tableManager.isRowSelected()) {
            Border border = null;
            if (isSelected) {
                border = BorderFactory.createCompoundBorder(
                        BorderFactory.createBevelBorder(BevelBorder.RAISED,
                                Color.white, Color.gray, Color.white, Color.lightGray),
                        new LineBorder(new Color(204, 204, 204), 1, true));
            }
            if (border == null) {
                border = UIManager.getBorder("Table.focusCellHighlightBorder");
            }
            panel.setBorder(border);
        } else {
            panel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        }
        check.setSelected((Boolean) value);
        return panel;
    }
}
