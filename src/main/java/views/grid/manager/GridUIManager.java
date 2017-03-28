package views.grid.manager;

import core.rml.RmlConstants;
import loader.ZetaProperties;
import views.grid.GridColumn;
import views.util.ResourceHelper;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GridUIManager {

    //Хранит шрифты для строк, если раскраски нет, выбирается дефолтная
    private Map<Integer, Font> rowFontColors = new HashMap<Integer, Font>();

    //Хранит шрифты для ячеек, ключом служит номер_строки + "_" + номер_столбца
    private Map<String, Font> cellFontColors = new HashMap<String, Font>();

    //Хранит раскраску бекграунда строк для строк, если раскраски нет, выбирается дефолтная
    private Map<Integer, Color> rowBgColors = new HashMap<Integer, Color>();

    //Хранит раскраску бекграунда строк для ячеек, ключом служит номер_строки + "_" + номер_столбца
    private Map<String, Color> cellBgColors = new HashMap<String, Color>();

    //Хранит раскраску цвета текста для строк, если раскраски нет, выбирается дефолтная
    private Map<Integer, Color> rowFgColors = new HashMap<Integer, Color>();

    //Хранит раскраску цвета текста для ячеек, ключом служит номер_строки + "_" + номер_столбца
    private Map<String, Color> cellFgColors = new HashMap<String, Color>();

    //Хранит раскраску цвета текста для заголовков строк, если раскраски нет, выбирается дефолтная
    private Map<Integer, Color> rowTitleFgColors = new HashMap<Integer, Color>();

    //Хранит раскраску бекграунда для заголовков строк, если раскраски нет, выбирается дефолтная
    private Map<Integer, Color> rowTitleBgColors = new HashMap<Integer, Color>();

    private ImageIcon sortUpIcon;
    private ImageIcon sortDownIcon;
    private ImageIcon filterIcon;
    private ImageIcon filterSortUpIcon;
    private ImageIcon filterSortDownIcon;

    private GridTableManager tableManager;

    public GridUIManager(GridTableManager tableManager) {
        this.tableManager = tableManager;
        initIcons();
    }

    private void initIcons() {
        sortUpIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(ZetaProperties.IMAGE_SORT_UP)));
        sortDownIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(ZetaProperties.IMAGE_SORT_DOWN)));
        filterIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(ZetaProperties.IMAGE_FILTER)));
        filterSortUpIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(ZetaProperties.IMAGE_FILTER_SORT_UP)));
        filterSortDownIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(ZetaProperties.IMAGE_FILTER_SORT_DOWN)));
    }

    public void setRowBgColor(int row, String textColor) {
        if (row >= 0 && row <= tableManager.getRowCount()) {
            if (ResourceHelper.DEFAULT_COLOR.equals(textColor)) {
                rowBgColors.remove(row);
            } else {
                Color rowColor = ResourceHelper.getColor(textColor);
                if (rowColor != null) {
                    rowBgColors.put(row, rowColor);
                }
            }
        }
    }

    public void setRowTitleBgColor(int row, String textColor) {
        if (row >= 0 && row <= tableManager.getRowCount()) {
            if (ResourceHelper.DEFAULT_COLOR.equals(textColor)) {
                rowTitleBgColors.remove(row);
            } else {
                Color rowColor = ResourceHelper.getColor(textColor);
                if (rowColor != null) {
                    rowTitleBgColors.put(row, rowColor);
                }
            }
        }
    }

    public void setColumnBgColor(int column, String textColor) {
        if (column >= 0 && column < tableManager.getAllColumnCount()) {
            GridColumn gridColumn = tableManager.getColumn(column);
            Color columnColor = ResourceHelper.getColor(textColor);
            if (gridColumn != null && columnColor != null) {
                gridColumn.setBgColor(columnColor);
            }
        }
    }

    public void setCellBgColor(final int row, final int column, final String textColor) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (row >= 0 && row <= tableManager.getRowCount() &&
                        column >= 0 && column < tableManager.getAllColumnCount()) {
                    String cellKey = row + "_" + column;
                    if (ResourceHelper.DEFAULT_COLOR.equals(textColor)) {
                        cellBgColors.remove(cellKey);
                    } else {
                        Color cellColor = ResourceHelper.getColor(textColor);
                        if (cellColor != null) {
                            cellBgColors.put(cellKey, cellColor);
                        }
                    }
                }
            }
        });
    }

    public void setColumnTitleBgColor(int column, String textColor) {
        if (column >= 0 && column < tableManager.getAllColumnCount()) {
            GridColumn gridColumn = tableManager.getColumn(column);
            Color columnColor = ResourceHelper.getColor(textColor);
            if (gridColumn != null && columnColor != null) {
                gridColumn.setTitleBgColor(columnColor);
            }
        }
    }

    public void setColumnTitleFgColor(int column, String textColor) {
        if (column >= 0 && column < tableManager.getAllColumnCount()) {
            GridColumn gridColumn = tableManager.getColumn(column);
            Color columnColor = ResourceHelper.getColor(textColor);
            if (gridColumn != null && columnColor != null) {
                gridColumn.setTitleFgColor(columnColor);
            }
        }
    }

    public void setRowFgColor(int row, String textColor) {
        if (row >= 0 && row <= tableManager.getRowCount()) {
            if (ResourceHelper.DEFAULT_COLOR.equals(textColor)) {
                rowFgColors.remove(row);
            } else {
                Color rowColor = ResourceHelper.getColor(textColor);
                if (rowColor != null) {
                    rowFgColors.put(row, rowColor);
                }
            }
        }
    }

    public void setRowTitleFgColor(int row, String textColor) {
        if (row >= 0 && row <= tableManager.getRowCount()) {
            if (ResourceHelper.DEFAULT_COLOR.equals(textColor)) {
                rowTitleFgColors.remove(row);
            } else {
                Color rowColor = ResourceHelper.getColor(textColor);
                if (rowColor != null) {
                    rowTitleFgColors.put(row, rowColor);
                }
            }
        }
    }

    public void setColumnFgColor(int column, String textColor) {
        if (column >= 0 && column < tableManager.getAllColumnCount()) {
            GridColumn gridColumn = tableManager.getColumn(column);
            Color columnColor = ResourceHelper.getColor(textColor);
            if (gridColumn != null && columnColor != null) {
                gridColumn.setFontColor(columnColor);
            }
        }
    }

    public void setCellFgColor(int row, int column, String textColor) {
        if (row >= 0 && row <= tableManager.getRowCount() &&
                column >= 0 && column < tableManager.getAllColumnCount()) {
            String cellKey = row + "_" + column;
            if (ResourceHelper.DEFAULT_COLOR.equals(textColor)) {
                cellFgColors.remove(cellKey);
            } else {
                Color cellColor = ResourceHelper.getColor(textColor);
                if (cellColor != null) {
                    cellFgColors.put(cellKey, cellColor);
                }
            }
        }
    }

    public void setRowFont(int row, String textFont) {
        if (row >= 0 && row <= tableManager.getRowCount()) {
            Font rowFont = ResourceHelper.getFont(textFont);
            if (rowFont != null) {
                rowFontColors.put(row, rowFont);
            }
        }
    }

    public void setColumnFont(int column, String textFont) {
        if (column >= 0 && column < tableManager.getAllColumnCount()) {
            GridColumn gridColumn = tableManager.getColumn(column);
            Font columnFont = ResourceHelper.getFont(textFont);
            if (gridColumn != null && columnFont != null) {
                gridColumn.setFont(columnFont);
            }
        }
    }

    public void setCellFont(int row, int column, String textFont) {
        if (row >= 0 && row <= tableManager.getRowCount() &&
                column >= 0 && column < tableManager.getAllColumnCount()) {
            String cellKey = row + "_" + column;
            Font cellFont = ResourceHelper.getFont(textFont);
            if (cellFont != null) {
                cellFontColors.put(cellKey, cellFont);
            }
        }
    }

    public Color getRowBgColor(int row) {
        return rowBgColors.get(row);
    }

    public Color getRowTitleBgColor(int row) {
        Color rowTitleBgColor = rowTitleBgColors.get(row);
        if (rowTitleBgColor == null) {
            rowTitleBgColor = tableManager.getParent().getColorProperty(RmlConstants.BUTTONBAR_BG_COLOR);
        }
        return rowTitleBgColor;
    }

    public Color getColumnBgColor(int column) {
        Color color = null;
        GridColumn gridColumn = tableManager.getColumn(column);
        if (gridColumn != null) {
            color = gridColumn.getColorProperty(RmlConstants.BG_COLOR);
        }
        return color;
    }

    public Color getCellBgColor(int row, int column) {
        return cellBgColors.get(row + "_" + column);
    }

    public Color getBgColor(int row, int column) {
        Color bgColor = getCellBgColor(row, column);
        if (bgColor == null) {
            bgColor = getRowBgColor(row);
        }
        if (bgColor == null) {
            bgColor = getColumnBgColor(column);
        }
        return bgColor;
    }

    public Color getRowFgColor(int row) {
        return rowFgColors.get(row);
    }

    public Color getRowTitleFgColor(int row) {
        Color rowTitleFgColor = rowTitleFgColors.get(row);
        if (rowTitleFgColor == null) {
            rowTitleFgColor = tableManager.getParent().getColorProperty(RmlConstants.BUTTONBAR_FONT_COLOR);
        }
        return rowTitleFgColor;
    }

    public Color getColumnFgColor(int column) {
        Color color = null;
        GridColumn gridColumn = tableManager.getColumn(column);
        if (gridColumn != null) {
            color = gridColumn.getColorProperty(RmlConstants.FONT_COLOR);
        }
        return color;
    }

    public Color getCellFgColor(int row, int column) {
        return cellFgColors.get(row + "_" + column);
    }

    public Color getFgColor(int row, int column) {
        Color bgColor = getCellFgColor(row, column);
        if (bgColor == null) {
            bgColor = getRowFgColor(row);
        }
        if (bgColor == null) {
            bgColor = getColumnFgColor(column);
        }
        return bgColor;
    }

    public Font getRowFont(int row) {
        return rowFontColors.get(row);
    }

    public Font getColumnFont(int column) {
        Font font = null;
        GridColumn gridColumn = tableManager.getColumn(column);
        if (gridColumn != null) {
            font = gridColumn.getFontProperty(RmlConstants.FONT);
        }
        return font;
    }

    public Font getCellFont(int row, int column) {
        return cellFontColors.get(row + "_" + column);
    }

    public Font getFont(int row, int column) {
        Font bgColor = getCellFont(row, column);
        if (bgColor == null) {
            bgColor = getRowFont(row);
        }
        if (bgColor == null) {
            bgColor = getColumnFont(column);
        }
        return bgColor;
    }

    public ImageIcon getSortUpIcon() {
        return sortUpIcon;
    }

    public ImageIcon getSortDownIcon() {
        return sortDownIcon;
    }

    public ImageIcon getFilterIcon() {
        return filterIcon;
    }

    public ImageIcon getFilterSortUpIcon() {
        return filterSortUpIcon;
    }

    public ImageIcon getFilterSortDownIcon() {
        return filterSortDownIcon;
    }
}
