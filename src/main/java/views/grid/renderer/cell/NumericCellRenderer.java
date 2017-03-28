package views.grid.renderer.cell;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellRenderer;

import views.grid.GridColumn;
import views.grid.manager.GridTableManager;
import core.rml.RmlConstants;

public class NumericCellRenderer extends JFormattedTextField implements TableCellRenderer {

    private GridTableManager tableManager;

    public NumericCellRenderer(GridTableManager tableManager) {
        super();
        this.tableManager = tableManager;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {

        column = tableManager.convertColumnIndexToModel(column);
        GridColumn currColumn = tableManager.getVColumn(column);
        Font font = tableManager.getUIManager().getFont(row, column);
        if (font != null) {
            setFont(font);
        }
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        } else {
            Color fontColor = tableManager.getUIManager().getFgColor(row, column);
            if (fontColor != null) {
                setForeground(fontColor);
            }
            Color bgColor = tableManager.getUIManager().getBgColor(row, column);
            if (bgColor != null) {
                setBackground(bgColor);
            }
        }

        String halignment = currColumn.getStringProperty(RmlConstants.HALIGNMENT);
        if ("LEFT".equals(halignment.toUpperCase())) {
            setHorizontalAlignment(JFormattedTextField.LEFT);
        } else if ("CENTER".equals(halignment.toUpperCase())) {
            setHorizontalAlignment(JFormattedTextField.CENTER);
        } else if ("RIGHT".equals(halignment.toUpperCase())) {
            setHorizontalAlignment(JFormattedTextField.RIGHT);
        }

        if (hasFocus && !tableManager.isRowSelected()) {
            Border border = null;
            if (isSelected) {
//                border = UIManager.getBorder("Table.focusSelectedCellHighlightBorder");

                border = BorderFactory.createCompoundBorder(
                        BorderFactory.createBevelBorder(BevelBorder.RAISED,
                                Color.white, Color.gray, Color.white, Color.lightGray),
                        new LineBorder(new Color(204, 204, 204), 3, true));

//                border = BorderFactory.createLineBorder(parentGrid.getTbsbg_color(), 2);
            }
            if (border == null) {
                border = UIManager.getBorder("Table.focusCellHighlightBorder");
            }
            setBorder(border);
        } else {
            setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        }
        try {
            setText(currColumn.valueToString(value));
        }
        catch (Exception e) {
            if (value != null) {
                setText(String.valueOf(value));
            } else {
                setText("");
            }
//            e.printStackTrace();
        }
        return this;
    }
}