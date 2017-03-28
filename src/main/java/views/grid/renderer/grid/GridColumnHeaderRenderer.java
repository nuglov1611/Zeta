package views.grid.renderer.grid;

import core.rml.RmlConstants;
import views.grid.GridColumn;
import views.grid.manager.GridTableManager;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class GridColumnHeaderRenderer implements TableCellRenderer {

    private TableCellRenderer defaultRenderer;

    private GridTableManager tableManager;

    public GridColumnHeaderRenderer(TableCellRenderer defaultTableRenderer,
                                    GridTableManager tableManager) {
        defaultRenderer = defaultTableRenderer;
        this.tableManager = tableManager;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {

        Component component =
                defaultRenderer.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

        if (column != -1) {
            column = tableManager.convertColumnIndexToModel(column);
            GridColumn currColumn = tableManager.getVColumn(column);
            Font titleFont = currColumn.getFontProperty(RmlConstants.TITLE_FONT);
            if (titleFont != null) {
                component.setFont(titleFont);
            }
            Color titleFontColor = currColumn.getColorProperty(RmlConstants.TITLE_FONT_COLOR);
            if (titleFontColor != null) {
                component.setForeground(titleFontColor);
            }
            Color titleBgColor = currColumn.getColorProperty(RmlConstants.TITLEBAR_BG_COLOR);
            if (titleBgColor != null) {
                component.setBackground(titleBgColor);
            }
            if (component instanceof JLabel) {
                Icon icon = null;
                SortOrder order = tableManager.getSortOrder(column);
                boolean filteringMode = tableManager.isFilterColumn(column);
                if (order == SortOrder.ASCENDING) {
                    if (filteringMode) {
                        icon = tableManager.getUIManager().getFilterSortUpIcon();
                    } else {
                        icon = tableManager.getUIManager().getSortUpIcon();
                    }
                } else if (order == SortOrder.DESCENDING) {
                    if (filteringMode) {
                        icon = tableManager.getUIManager().getFilterSortDownIcon();
                    } else {
                        icon = tableManager.getUIManager().getSortDownIcon();
                    }
                } else if (order == SortOrder.UNSORTED) {
                    if (filteringMode) {
                        icon = tableManager.getUIManager().getFilterIcon();
                    } else {
                        icon = UIManager.getIcon("Table.naturalSortIcon");
                    }
                }
                ((JLabel) component).setIcon(icon);
            }
        }
        return component;
    }
}
