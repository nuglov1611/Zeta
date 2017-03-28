package views.grid.renderer.cell;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellRenderer;

import views.grid.GridColumn;
import views.grid.manager.GridTableManager;
import core.rml.RmlConstants;

public class ArrayCellRenderer implements TableCellRenderer {
    private GridTableManager tableManager;
    Hashtable<GridColumn, JComboBox> combos = new Hashtable<GridColumn, JComboBox>();
    public ArrayCellRenderer(GridTableManager tableManager) {
        super();
        this.tableManager = tableManager;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {

        column = tableManager.convertColumnIndexToModel(column);
        GridColumn currColumn = tableManager.getVColumn(column);

        if(!combos.containsKey(currColumn)){
            JComboBox combo = new JComboBox(currColumn.getItems());
            Font font = tableManager.getUIManager().getFont(row, column);
            if (font != null) {
                combo.setFont(font);
            }
            combos.put(currColumn, combo);
        }
        
        JComboBox combo = combos.get(currColumn);
        if (isSelected) {
            combo.setForeground(table.getSelectionForeground());
            combo.setBackground(table.getSelectionBackground());
        } else {
            Color fontColor = tableManager.getUIManager().getFgColor(row, column);
            if (fontColor != null) {
                combo.setForeground(fontColor);
            }
            Color bgColor = tableManager.getUIManager().getBgColor(row, column);
            if (bgColor != null) {
                combo.setBackground(bgColor);
            }
        }

        String halignment = currColumn.getStringProperty(RmlConstants.HALIGNMENT);
        if ("LEFT".equals(halignment.toUpperCase())) {
            combo.setAlignmentX(SwingConstants.LEFT);
        } else if ("CENTER".equals(halignment.toUpperCase())) {
            combo.setAlignmentX(SwingConstants.CENTER);
        } else if ("RIGHT".equals(halignment.toUpperCase())) {
            combo.setAlignmentX(SwingConstants.RIGHT);
        }

        if (hasFocus && !tableManager.isRowSelected()) {
            Border border = null;
            if (isSelected) {
//                border = UIManager.getBorder("Table.focusSelectedCellHighlightBorder");

                border = BorderFactory.createCompoundBorder(
                        BorderFactory.createBevelBorder(BevelBorder.RAISED,
                                Color.white, Color.gray, Color.white, Color.lightGray),
                        new LineBorder(new Color(204, 204, 204), 1, true));

//                border = BorderFactory.createLineBorder(parentGrid.getTbsbg_color(), 2);
            }
            if (border == null) {
                border = UIManager.getBorder("Table.focusCellHighlightBorder");
            }
            combo.setBorder(border);
        } else {
            combo.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        }
        combo.setSelectedItem(value);
        return combo;
    }
}
