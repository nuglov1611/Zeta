package views.grid.manager;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JList;

import org.apache.log4j.Logger;

import views.FilterDialog;
import views.FilterStruct;
import views.FindDialog;
import views.MaskFilter;
import views.StringBundle;
import views.grid.GridColumn;
import views.grid.GridSwing;
import views.grid.filter.GridRowFilter;
import views.grid.filter.model.HeaderFilterListModel;
import views.grid.filter.model.HeaderListItem;
import core.rml.RmlConstants;

public class GridActionManager {

    private static final Logger log = Logger.getLogger(GridActionManager.class);

    private GridSwing grid;

    /**
     * Dialogs
     */
    private FilterDialog fd = null;

    private FindDialog findd = null;

    private FilterStruct[] filterData;

    /**
     * For copy-paste operations
     */
    private Clipboard clipboard;

    public GridActionManager(GridSwing parent) {
        this.grid = parent;
        clipboard = parent.getVisualComponent().getToolkit().getSystemClipboard();
    }

    public boolean processEnterAction() {
        if (grid.getTableManager().isEditing()) {
            grid.getTableManager().stopEditing();
            return false;
        } else {
            if (grid.getSourceRows() != 0 && grid.isEditable()) {
                int selRow = grid.getTableManager().getCurrentRow();
                int selCol = grid.getTableManager().getCurrentColumn();
                if (selRow != GridTableManager.DEFAULT_ROW && selCol != GridTableManager.DEFAULT_COLUMN) {
                    if (grid.getTableManager().startEditAtCell(grid.getTableManager().convertRowIndexToView(selRow), grid.getTableManager().convertColumnIndexToView(selCol))) {
                        grid.getTableManager().startEditing();
                    }
                }
            } else {
                if (grid.getStringProperty(RmlConstants.EDIT) != null) {
                    grid.doAction(grid.getStringProperty(RmlConstants.EDIT));
                }
            }
            return true;
        }
    }

    public boolean processPageUp() {
        Dimension delta = grid.getTableManager().getTableSize();
        Rectangle cellRectangle = grid.getTableManager().getCurrentCellRectangle();
        cellRectangle.y -= delta.height;
        int prevViewRow = grid.getTableManager().convertRowIndexToView(grid.getTableManager().getCurrentRow());
        int newViewRow = grid.getTableManager().getRowAtPoint(cellRectangle.getLocation());
        if (newViewRow == -1 && grid.getTableManager().getRowCount() > 0) {
            newViewRow = 0;
        }
        if (grid.getTableManager().containsSelectedRow(prevViewRow)) {
            grid.getTableManager().removeSelectedRow(prevViewRow);
        }
        grid.getTableManager().setCurrentRow(newViewRow, false, false);
        return true;
    }

    public boolean processPageDown() {
        Dimension delta = grid.getTableManager().getTableSize();
        Rectangle cellRectangle = grid.getTableManager().getCurrentCellRectangle();
        cellRectangle.y += delta.height;
        int prevViewRow = grid.getTableManager().convertRowIndexToView(grid.getTableManager().getCurrentRow());
        int newViewRow = grid.getTableManager().getRowAtPoint(cellRectangle.getLocation());
        if (newViewRow == -1) {
            newViewRow = grid.getTableManager().getRowCount() - 1;
        }
        if (grid.getTableManager().containsSelectedRow(prevViewRow)) {
            grid.getTableManager().removeSelectedRow(prevViewRow);
        }
        grid.getTableManager().setCurrentRow(newViewRow, false, false);
        return true;
    }

    public boolean selectAll() {
        return grid.getTableManager().selectAll();
    }

    public boolean showSearchDialog() {//вызвать окно поиска
        int selCol = grid.getTableManager().getSelectedColumn();
        if (selCol != GridTableManager.DEFAULT_COLUMN) {
            String title = StringBundle.FindDialog_Caption + " [" + grid.getTableManager().getVColumn(selCol).getTitle() + "]";
            if (findd == null) {
                findd = new FindDialog(grid, null, 400, 180, true);
                findd.setTitle(title);
                findd.setVisible(true);
            } else {
                findd.setTitle(title);
                findd.setVisible(true);
                findd.validate();
            }
            if (findd.find_pressed) {
                findRow(findd.text, findd.go_down, findd.caze);
                return showSearchDialog();
            }
        }
        return true;
    }

    public boolean copySelected() {//копировать содержимое ячейки l
        if (!grid.getTableManager().isEditing()) {
            if (clipboard != null) {
                // Выбрано все
               if (grid.getTableManager().isAllSelected()) {
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < grid.getTableManager().getRowCount(); i++) {
                        for (int j = 0; j < grid.getTableManager().getVColumnCount(); j++) {
                            sb.append(grid.getTableManager().getValueAt(i, j) != null ? grid.getTableManager().getValueAt(i, j) : "");
                            if ((j + 1) < grid.getTableManager().getVColumnCount()) {
                                sb.append('\t');
                            }
                        }
                        if ((i + 1) < grid.getTableManager().getRowCount()) {
                            sb.append('\n');
                        }
                    }
                    String str = sb.toString();
                    clipboard.setContents(new StringSelection(str), null);
                } //Выбрана ячейка
                else if (grid.getTableManager().getSelectionSize() == 1) {
                    String str = grid.getSourceText(grid.getTableManager().getCurrentRow(), grid.getTableManager().getCurrentColumn());
                    clipboard.setContents(new StringSelection(str), null);
                }
                //Выбрана одна или несколько строк (в режиме multiselection),
                // строки берутся из массива selection
                else if (grid.getTableManager().getSelectionSize() > 1 && !grid.getTableManager().isAllSelected()) {
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < grid.getTableManager().getSelectionSize(); i++) {
                        for (int j = 0; j < grid.getTableManager().getVColumnCount(); j++) {
                            int modelRowIndex = grid.getTableManager().getSelectedRow(i);
                            sb.append(grid.getSourceText(modelRowIndex, j));
                            if ((j + 1) < grid.getTableManager().getVColumnCount()) {
                                sb.append('\t');
                            }
                        }
                        if ((i + 1) < grid.getTableManager().getRowCount()) {
                            sb.append('\n');
                        }
                    }
                    String str = sb.toString();
                    clipboard.setContents(new StringSelection(str), null);
                }
            }
        }
        // keep sorting and restore focus
        grid.notifyHandler(null, true, true);
        return true;
    }

    public boolean pasteSelected() {//вставить содержимое ячеек
        if (!grid.isEditable()) {
            return true;
        }
        if (!grid.getTableManager().isEditing()) {
            if (clipboard != null) {
                try {
                    Transferable tf = clipboard.getContents(null);
                    DataFlavor[] df = tf.getTransferDataFlavors();
                    DataFlavor plainFlavor = null;
                    for (int i = 0; i < df.length && plainFlavor == null; i++) {
                        if ("text/plain"
                                .equals(df[i].getHumanPresentableName()) && df[i].getRepresentationClass()
                                .equals(String.class)) {
                            plainFlavor = df[i];
                        }
                    }
                    String s;
                    if (plainFlavor == null) {
                        s = (String) tf.getTransferData(df[0]);
                    } else {
                        s = (String) tf.getTransferData(plainFlavor);
                    }
                    StringTokenizer st = new StringTokenizer(s, "\n");
                    int len = st.countTokens();
                    int currentRow = grid.getTableManager().getCurrentRow();
                    int currentModelColumn = grid.getTableManager().getCurrentColumn();
                    if (currentModelColumn == GridTableManager.DEFAULT_COLUMN) {
                        for (int i = 0; i < len; i++) {
                            StringTokenizer st1 = new StringTokenizer(st
                                    .nextToken(), "\t", true);
                            int len1 = st1.countTokens();
                            int rowCount = grid.getTableManager().getRowCount();
                            if ((currentRow + i) >= rowCount) {
                                if (grid.getStringProperty(RmlConstants.ADD) != null) {
                                    grid.doAction(grid.getStringProperty(RmlConstants.ADD));
                                    //return true;
                                }
                                //if (!this.editable.equals("YES")) return true;
                                //                            process_CTRL_INS();
                            }

                            int inc = 0;
                            for (int j = 0; j < len1; j++) {
                                String text = st1.nextToken();
                                if (text.equals("\t")) {
                                    inc++;
                                    continue;
                                }
                                GridColumn vColumn = grid.getTableManager().getVColumn(grid.getTableManager().convertColumnIndexToView(inc));
                                Object value = vColumn.valueToObject(text);
                                grid.setDSValue(currentRow + i, vColumn.getTarget(), value);
                            }
                        }
                    } else {
                        for (int i = 0; i < len; i++) {
                            GridColumn vColumn = grid.getTableManager().getVColumn(grid.getTableManager().convertColumnIndexToView(currentModelColumn));
                                    Object value = vColumn.valueToObject(s);
                            grid.setDSValue(currentRow + i, vColumn.getTarget(), value);
                        }
                    }
                    grid.notifyHandler(null);
                    //restore focus
                    grid.requestFocusThis();
                } catch (Exception e) {
                    log.error("Shit happens", e);
                    return true;
                }
            }
        }
        return true;
    }

    public void switchToPrevRow(boolean extend) {
        int row = grid.getTableManager().convertRowIndexToView(grid.getTableManager().getCurrentRow());
        if (row - 1 > GridTableManager.DEFAULT_ROW) {
            grid.getTableManager().setCurrentRow(row - 1, false, false, false, extend);
        } else if (row == GridTableManager.DEFAULT_ROW) {
            grid.getTableManager().setCurrentRow(GridTableManager.DEFAULT_ROW + 1, false, false);
//            table.scrollRectToVisible(new Rectangle(0, 0, 0, 0));
        }
//        table.tableChanged(new TableModelEvent(tableModel));
//        repaint();
        //restore selection
//        restoreSelection();
        if (grid.getTableManager().getCurrentColumn() == GridTableManager.DEFAULT_COLUMN) {
            grid.getTableManager().setCurrentColumn(GridTableManager.DEFAULT_COLUMN + 1);
        }
    }

    public void switchToNextRow(boolean extend) {
        int row = grid.getTableManager().convertRowIndexToView(grid.getTableManager().getCurrentRow());
        if (row + 1 < grid.getSourceRows() && row != GridTableManager.DEFAULT_ROW) {
            grid.getTableManager().setCurrentRow(row + 1, false, false, false, extend);
        } else if (row == GridTableManager.DEFAULT_ROW) {
            grid.getTableManager().setCurrentRow(GridTableManager.DEFAULT_ROW + 1, false, false);
        } else if (row + 1 == grid.getSourceRows() && grid.getBooleanProperty(RmlConstants.AUTO_NEW_ROW)) {
            if (grid.getStringProperty(RmlConstants.ADD) != null) {
                grid.doAction(grid.getStringProperty(RmlConstants.ADD));
            } else if (grid.isEditable()) {
                grid.getActionManager().insertNewRow();
            }
        }
        if (grid.getTableManager().getCurrentColumn() == GridTableManager.DEFAULT_COLUMN) {
            grid.getTableManager().setCurrentColumn(GridTableManager.DEFAULT_COLUMN + 1);
        }
    }

    public void switchToHome(boolean isCtrlModifier) {
        if (isCtrlModifier) {
            int rowCount = grid.getSourceRows();
            if (rowCount > 0) {
                grid.getTableManager().setCurrentRow(0, false, false);
            } else {
                grid.getTableManager().setCurrentRow(GridTableManager.DEFAULT_ROW, true, true);
            }
        } else {
            int columnCount = grid.getSourceColumns();
            if (columnCount > 0) {
                grid.getTableManager().setCurrentColumn(0);
            } else {
                grid.getTableManager().setCurrentColumn(GridTableManager.DEFAULT_COLUMN);
            }
        }
    }

    public void switchToEnd(boolean isCtrlModifier) {
        if (isCtrlModifier) {
            int rowCount = grid.getSourceRows();
            if (rowCount > 0) {
                grid.getTableManager().setCurrentRow(rowCount - 1, false, false);
            } else {
                grid.getTableManager().setCurrentRow(GridTableManager.DEFAULT_ROW, true, true);
            }
        } else {
            int columnCount = grid.getSourceColumns();
            if (columnCount > 0) {
                grid.getTableManager().setCurrentColumn(columnCount - 1);
            } else {
                grid.getTableManager().setCurrentColumn(GridTableManager.DEFAULT_COLUMN);
            }
        }
    }

    public int insertNewRow() {
        int index = GridTableManager.DEFAULT_ROW;
        try {
            if (!grid.isDsEmpty()) {
                index = grid.createDsRow();
                grid.notifyHandler(null);
                grid.getTableManager().requestFocusThis();
            }
        } catch (Exception e) {
            log.error("Shit happens", e);
        }
        return index;
    }

    public void deleteCurrentRow() {
        int rowCount = grid.getSourceRows() - 1;
        int viewRow = grid.getTableManager().convertRowIndexToView(grid.getTableManager().getCurrentRow());
        int viewColumn = grid.getTableManager().convertColumnIndexToView(grid.getTableManager().getCurrentColumn());
        if (grid.getStringProperty(RmlConstants.DEL) != null) {
            grid.doAction(grid.getStringProperty(RmlConstants.DEL));
        } else {
            if (!grid.isDsEmpty()) {
                if (grid.getSourceRows() == 0) {
                    return;
                }
                int dsCurRow = grid.getDsCurRow();
                if (dsCurRow != grid.getTableManager().getCurrentRow() || dsCurRow == GridTableManager.DEFAULT_ROW) {
                    return;
                }
                grid.deleteDsCurRow(dsCurRow);
                grid.getTableManager().clearSelection(0);
//            dsCurRow = ds.getCurRow();
//            if (dsCurRow > getSourceRows()) {
//                setCurrentRow(getSourceRows(), true);
//            }
//            else {
//            	setCurrentRow(dsCurRow, false);
//            }
                dsCurRow = grid.getDsCurRow();
                if (dsCurRow != grid.getTableManager().getCurrentRow() || dsCurRow == GridTableManager.DEFAULT_ROW) {
                    grid.getTableManager().setCurrentRow(GridTableManager.DEFAULT_ROW, true, false);
                } else if (dsCurRow > grid.getSourceRows()) {
                    grid.getTableManager().setCurrentRow(grid.getSourceRows(), true, true);
                } else if (dsCurRow == grid.getSourceRows()) {
                    grid.getTableManager().setCurrentRow(grid.getSourceRows() - 1, true, true);
                } else {
                    grid.getTableManager().setCurrentRow(dsCurRow, true, true);
                }

                grid.notifyHandler(null);
                //doc.moveTo(this);
            }
            if (viewRow >= rowCount) {
                if (viewRow - 1 >= 0) {
                    grid.getTableManager().setCurrentRow(viewRow - 1, true, true);
                    grid.getTableManager().setCurrentColumn(viewColumn, true);
                } else {
                    grid.getTableManager().setCurrentRow(GridTableManager.DEFAULT_ROW, false, false);
                    grid.getTableManager().setCurrentColumn(GridTableManager.DEFAULT_COLUMN);
                }
            } else {
                grid.getTableManager().setCurrentRow(viewRow, true, true);
                grid.getTableManager().setCurrentColumn(viewColumn, true);
            }
        }
    }

    public void findRow(String mask, boolean down, boolean caze) {
        //если caze=true, значит поиск производим с учетом регистра
        int count = grid.getSourceRows();
        if (count <= 1) {
            return;
        }
        if (!caze) {
            mask = mask.toUpperCase();
        }

        int startViewRow = grid.getTableManager().convertRowIndexToView(grid.getTableManager().getCurrentRow());
        String curTarget = null;
        GridColumn curColumn = null;
        try {
            curColumn = grid.getTableManager().getVColumn(grid.getTableManager().getCurrentColumn());
            curTarget = curColumn.getTarget();
        } catch (Exception e) {
            log.error("Shit happens", e);
            if (curColumn == null) {
                return;
            }
        }

        MaskFilter mf = new MaskFilter(mask);
        for (int i = 0; i < count; i++) {
            int viewRow;
            if (down) {
                viewRow = (startViewRow + i) % count;
            } else {
                viewRow = (count + startViewRow - i - 2) % count;
            }
            Object value = grid.getSourceValueByTarget(grid.getTableManager().convertRowIndexToModel(viewRow), curTarget);
            String svalue = null;
            try {
                svalue = curColumn.valueToString(value);
            } catch (Exception e) {
                log.error("Shit happens", e);
            }
            if (svalue != null && !caze) {
                svalue = svalue.toUpperCase();
            }
            if (mf.accept(svalue) && viewRow != startViewRow) {
                grid.getTableManager().setCurrentRow(viewRow, true, true);
                return;
            }
        }
    }

    public void processLeftAction() {
        int column = grid.getTableManager().convertColumnIndexToView(grid.getTableManager().getCurrentColumn());
        if (column - 1 > GridTableManager.DEFAULT_COLUMN) {
            if (column == grid.getSourceColumns()) {
                grid.getTableManager().setCurrentColumn(column - 1, true);
            } else {
                grid.getTableManager().setCurrentColumn(column - 1);
            }
        }
    }

    public void processRightAction() {
        int column = grid.getTableManager().convertColumnIndexToView(grid.getTableManager().getCurrentColumn());
        if (column + 1 < grid.getSourceColumns()) {
            if (column == GridTableManager.DEFAULT_COLUMN) {
                grid.getTableManager().setCurrentColumn(column + 1, true);
            } else {
                grid.getTableManager().setCurrentColumn(column + 1);
            }
        }
    }

    public void filterSelectAll() {
        grid.getTableManager().setRowSorter(grid.getTableManager().getDataTable().getModel(), null);
        grid.getTableManager().repaintAll();
        grid.getTableManager().restoreSelection();
    }

    public void showFilterDialog() {

    }

    public void filterRow(Point popupMenuPoint, JList list) {
        int rowItemIndex = list.locationToIndex(popupMenuPoint);
        if (rowItemIndex != -1) {
            Integer columnIndex = Integer.valueOf(list.getName());
            List<HeaderListItem> filters = ((HeaderFilterListModel) list.getModel()).getListItemsAt(rowItemIndex);
            GridRowFilter rowFilter = new GridRowFilter();
            for (HeaderListItem filterItem : filters) {
                rowFilter.addEqualsFilter(columnIndex, GridRowFilter.OR, filterItem.getValue());
            }
            grid.getTableManager().setRowSorter(grid.getTableManager().getDataTable().getModel(), rowFilter);
            grid.getTableManager().repaintAll();
            grid.showFilterMenu(popupMenuPoint, false);
            grid.requestFocusThis();
        }
    }

    public void processLeftClick(int x, int y) {
        grid.getTableManager().setRowSelected(false);
        grid.getTableManager().setCurrentColumn(x, y);
        grid.getTableManager().setCurrentRow(x, y);
        int columnIndex = grid.getTableManager().getCurrentColumn();
        GridColumn curColumn = grid.getTableManager().getVColumn(columnIndex);
        if (curColumn != null && curColumn.isArray() &&
                curColumn.isVisible() && curColumn.isEditable()) {
            int rowIndex = grid.getTableManager().getCurrentRow();
            grid.getTableManager().startEditAtCell(rowIndex, columnIndex);
        }
    }
}
