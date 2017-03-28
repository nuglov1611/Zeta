package views.grid.manager;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.log4j.Logger;

import views.ColumnTemplate;
import views.grid.GridColumn;
import views.grid.GridSwing;
import views.grid.editor.ArrayCellEditor;
import views.grid.editor.BooleanCellEditor;
import views.grid.editor.CommonField;
import views.grid.editor.TextFieldCellEditor;
import views.grid.filter.GridComparator;
import views.grid.filter.GridRowFilter;
import views.grid.listener.GridColumnSelectionListener;
import views.grid.listener.GridComponentListener;
import views.grid.listener.GridFocusListener;
import views.grid.listener.GridHeaderFocusListener;
import views.grid.listener.GridHeaderMouseListener;
import views.grid.listener.GridKeyListener;
import views.grid.listener.GridMouseListener;
import views.grid.listener.GridRowFocusListener;
import views.grid.listener.GridRowHeaderSelectionListener;
import views.grid.listener.GridRowSelectionListener;
import views.grid.model.GridMetadataModel;
import views.grid.model.GridTableFactory;
import views.grid.renderer.GridHeaderFactory;
import views.grid.renderer.cross.CrossTablePanel;
import core.parser.Proper;
import core.rml.RmlConstants;
import core.rml.dbi.Datastore;
import core.rml.ui.interfaces.ZComponent;
import core.rml.ui.interfaces.ZScrollPane;

public class GridTableManager {

    private static final Logger log = Logger.getLogger(GridTableManager.class);

    private GridSwing parent;


    private GridMetadataModel metadataModel;

    /**
     * Components for table view
     */
    private JTable dataTable;

    /**
     * Cell editor components
     */
    private TextFieldCellEditor tableCellEditor;

    private ArrayCellEditor arrayCellEditor;

    private BooleanCellEditor booleanCellEditor;

    /**
     * Table listeners section
     */
    private GridMouseListener tableMouseListener;

    private GridKeyListener tableKeyListener;

    private GridFocusListener tableFocusListener;

    private GridComponentListener gridComponentListener;

    private GridColumnSelectionListener gridColumnSelectionListener;

    private GridRowSelectionListener gridRowSelectionListener;

    /**
     * Header Listeners section
     */
    private GridRowHeaderSelectionListener rowHeaderSelectionListener;

    private GridHeaderMouseListener headerMouseListener;

    private GridHeaderFocusListener headerFocusListener;

    private GridRowFocusListener rowFocusListener;

    /**
     * Sorter components
     */
    private TableRowSorter<TableModel> rowSorter;

    private SortOrder sortMode = SortOrder.UNSORTED;

    private ArrayList<RowSorter.SortKey> sortKeys;

    private GridComparator gridComparator;

    public final static int DEFAULT_ROW = -1;

    public final static int DEFAULT_COLUMN = -1;

    public static final String NEXT_FOCUS_ACTION = "nextFocusAction";

    public static final String PREV_FOCUS_ACTION = "prevFocusAction";

    //Хранит индексы выбранных строк, представленных в model, а не view

    private Vector<Integer> selection = new Vector<Integer>();

    private boolean isCtrlA = false;

    //Строки(row) нумеруются с 0, если ничего не выбрано, то по умолчанию -1

    private int currentModelRow = GridTableManager.DEFAULT_ROW;

    //Столбцы(column) нумеруются с 0, если ничего не выбрано, то по умолчанию -1

    private int currentModelColumn = GridTableManager.DEFAULT_COLUMN;

    // Флаг, показывающий текущее состояний выборки (строка, или ячейка).

    private boolean isRowSelected = false;

    //Надо ли скроллить к текущей строке как только откроется окно

    private boolean needToBeScrolling = false;

    // Надо ли вписывать таблицу в текущие рамки как только откроется окно

    private boolean needToBeAlligned = false;

    //Компонент, отвечающий за представление номеров строк в таблице

    public Component rowHeader;

    private GridUIManager gridUiManager;

    //If ALLIGN method was called

    private boolean needAllign;

    private ZComponent tableContainer;

    private boolean firstTime = true;

    private boolean filteringMode = false;

    private Set<Integer> columnFilterIndexes = new HashSet<Integer>();

    private HashMap<Integer, Integer> rowHeaderIndexes = new HashMap<Integer, Integer>();

    private Map<Integer, String> rowTitles = new HashMap<Integer, String>();


    public GridTableManager(GridSwing parent) {
        this.parent = parent;
        gridUiManager = new GridUIManager(this);
    }

    public ZComponent createTable() {
        tableMouseListener = new GridMouseListener(parent);
        tableKeyListener = new GridKeyListener(parent);
        tableFocusListener = new GridFocusListener(this);
        gridComponentListener = new GridComponentListener(this);
        gridColumnSelectionListener = new GridColumnSelectionListener(this);
        gridRowSelectionListener = new GridRowSelectionListener(this);
        List<EventListener> listeners = new Vector<EventListener>();
        listeners.add(tableMouseListener);
        listeners.add(tableKeyListener);
        listeners.add(tableFocusListener);
        listeners.add(gridComponentListener);
        listeners.add(gridRowSelectionListener);
        listeners.add(gridColumnSelectionListener);
        tableCellEditor = new TextFieldCellEditor(parent);
        arrayCellEditor = new ArrayCellEditor(parent);
        booleanCellEditor = new BooleanCellEditor(parent);
        List<TableCellEditor> editors = new Vector<TableCellEditor>();
        editors.add(tableCellEditor);
        editors.add(arrayCellEditor);
        editors.add(booleanCellEditor);
        tableContainer = GridTableFactory.getInstance().getTableComponent(parent, this, listeners, editors);
        if (tableContainer instanceof ZScrollPane) {
            ZScrollPane scrollPane = ((ZScrollPane) tableContainer);
            Component view = scrollPane.getViewport().getView();
            if (view instanceof JTable) {
                dataTable = ((JTable) view);
            }
        } else if (tableContainer instanceof CrossTablePanel) {
            dataTable = ((CrossTablePanel) tableContainer).getDataTable();
        }
        return tableContainer;
    }

    public void initTableModel() {
        GridTableFactory.getInstance().initTableModel(parent, this, tableContainer, dataTable);
    }

    /**
     * Устанавливаем свойства из rml для dataTable и rowHeader
     */
    public void setTableProperties() {
        GridTableFactory.getInstance().initTableProps(parent, tableContainer, dataTable);
    }

    public void initColumnHeader() {
        //Для смены типов сортировки (возрастающая, убывающая, не отсортировано)
        headerMouseListener = new GridHeaderMouseListener(this);
        headerFocusListener = new GridHeaderFocusListener(this);

        List<EventListener> listeners = new Vector<EventListener>();
        listeners.add(headerMouseListener);
        listeners.add(headerFocusListener);
        listeners.add(tableKeyListener);

        GridHeaderFactory.getInstance().initColumnHeader(parent, tableContainer, dataTable, listeners);
    }

    public void initRowHeader() {
        rowHeaderSelectionListener = new GridRowHeaderSelectionListener(this);
        rowFocusListener = new GridRowFocusListener(this);
        List<EventListener> listeners = new Vector<EventListener>();
        listeners.add(rowHeaderSelectionListener);
        listeners.add(rowFocusListener);
        listeners.add(tableKeyListener);
        rowHeader = GridHeaderFactory.getInstance().initRowHeader(parent, tableContainer, listeners);
    }

    public GridSwing getParent() {
        return parent;
    }

    public int getColumnIndexByAlias(String al) {
        if (metadataModel == null) {
            return DEFAULT_COLUMN;
        }
        for (int i = 0; i < metadataModel.getAllColumnCount(); i++) {
            String alias = metadataModel.getTColumn(i).getAlias();
            if (alias == null) {
                alias = metadataModel.getTColumn(i).getTarget();
            }
            if (alias != null && alias.equals(al)) {
                return i;
            }
        }
        return DEFAULT_COLUMN;
    }

    public GridColumn getColumnByAlias(String al) {
        if (metadataModel == null) {
            return null;
        }
        for (int i = 0; i < metadataModel.getAllColumnCount(); i++) {
            final String alias = metadataModel.getTColumn(i).getAlias();
            final String target = metadataModel.getTColumn(i).getTarget();
            if ((alias != null && alias.equalsIgnoreCase(al)) ||
                    (target != null && target.equalsIgnoreCase(al))) {
                return metadataModel.getTColumn(i);
            }
        }
        return null;
    }

    public int getVColumnCount() {
        return dataTable.getColumnCount();
    }

    public int getAllColumnCount() {
        return metadataModel.getAllColumnCount();
    }

    /**
     * Получает столбец из модели данных столбец может быть как видимый,
     * так и невидимый.
     *
     * @param colIndex - индекс столбца в абсолютной модели данных
     * @return
     */
    public GridColumn getColumn(int colIndex) {
        return metadataModel.getTColumn(colIndex);
    }

    /**
     * Получает только видимый столбец из модели данных.
     *
     * @param colIndex - индекс столбца в видимой модели данных
     * @return
     */
    public GridColumn getVColumn(int colIndex) {
        return metadataModel.getVColumn(colIndex);
    }

    public void setRowSorter(TableModel model, RowFilter filter) {
        rowSorter = new TableRowSorter<TableModel>(model);
        if (model != null && metadataModel != null) {
        	if(parent.getSourceColumns() != 0)
        		rowSorter.setMaxSortKeys(parent.getSourceColumns());
            rowSorter.setSortsOnUpdates(true);
            dataTable.setRowSorter(rowSorter);
            for (int i = 0; i < getVColumnCount(); i++) {
                gridComparator = new GridComparator(getVColumn(i));
                rowSorter.setComparator(i, gridComparator);
            }
            if (sortKeys != null) {
                rowSorter.setSortKeys(sortKeys);
                rowSorter.sort();
            }
            if (filter != null && filter instanceof GridRowFilter) {
                for (int viewRow = 0; viewRow < getRowCount(); viewRow++) {
                    int modelRow = convertRowIndexToModel(viewRow);
                    rowHeaderIndexes.put(modelRow, viewRow);
                }
                rowSorter.setRowFilter(filter);
                columnFilterIndexes = ((GridRowFilter)filter).getColumnIndexes();
                filteringMode = true;
            } else {
                rowHeaderIndexes.clear();
                columnFilterIndexes.clear();
                filteringMode = false;
            }
        }
    }

    public SortOrder getSortOrder(int columnIndex) {
        SortOrder order = SortOrder.UNSORTED;
        if (sortKeys != null) {
            RowSorter.SortKey curKey;
            boolean isNotFound = true;

            for (int i = 0; i < sortKeys.size() && isNotFound; i++) {
                curKey = sortKeys.get(i);
                if (curKey.getColumn() == columnIndex) {
                    isNotFound = false;
                    order = curKey.getSortOrder();
                }
            }
        }
        return order;
    }

    public void toggleNextSortMode(int x) {
        if (rowSorter != null) {
            int columnIndex = dataTable.getColumnModel().getColumnIndexAtX(x);
            columnIndex = convertColumnIndexToModel(columnIndex);
//			setCurrentColumn(columnIndex);

            if (columnIndex != GridTableManager.DEFAULT_COLUMN) {

                if (sortKeys == null) {
                    sortKeys = new ArrayList<RowSorter.SortKey>();
                }

                RowSorter.SortKey curKey = null;
                boolean isNotFound = true;

                for (int i = 0; i < sortKeys.size() && isNotFound; i++) {
                    curKey = sortKeys.get(i);
                    if (curKey.getColumn() == columnIndex) {
                        isNotFound = false;
                    }
                }

                if (isNotFound) {
                    sortMode = SortOrder.UNSORTED;
                } else {
                    sortMode = curKey.getSortOrder();
                }

                if (sortMode == SortOrder.UNSORTED) {
                    sortMode = SortOrder.ASCENDING;
                } else if (sortMode == SortOrder.ASCENDING) {
                    sortMode = SortOrder.DESCENDING;
                } else if (sortMode == SortOrder.DESCENDING) {
                    sortMode = SortOrder.UNSORTED;
                }

                //Чистим текущий ключ сортировки, чтобы положить туда новый
                //с другим типом сортировки или если режим у столбца - без
                //сортировки, то просто чистим ключ
                if (!isNotFound) {
                    sortKeys.remove(curKey);
                }
                if (sortMode != SortOrder.UNSORTED) {
                    RowSorter.SortKey newKey = new RowSorter.SortKey(columnIndex, sortMode);
                    sortKeys.add(newKey);
                }
                rowSorter.setSortKeys(sortKeys);
                rowSorter.sort();
            }

            // Прокручиваем до текущей строки, если ее не видно
            if (currentModelRow != GridTableManager.DEFAULT_ROW) {
                int viewRow = convertRowIndexToView(currentModelRow);
                if (needScroll(viewRow)) {
                    scrollToRow(viewRow, false);
                }
            }
        }
    }


    public boolean isTableModelEmpty() {
        return dataTable.getModel() == null;
    }

    public TableModel getTableModel() {
        return dataTable.getModel();
    }

    public ColumnTemplate getColumnTemplate(int type) {
        return metadataModel.getColumnTemplate(type);
    }

    public boolean needScrollLater() {
        return needToBeScrolling;
    }

    public void resetNeeedScrollLater() {
        needToBeScrolling = false;
    }

    public Integer convertRowIndexToView(int modelRowIndex) {
        if (modelRowIndex > GridTableManager.DEFAULT_ROW &&
                ((dataTable.getRowSorter() != null &&
                        modelRowIndex < dataTable.getRowSorter().getModelRowCount())) ||
                modelRowIndex < dataTable.getRowCount()) {
            return dataTable.convertRowIndexToView(modelRowIndex);
        }
        return GridTableManager.DEFAULT_ROW;
    }

    public Integer convertRowIndexToModel(int viewRowIndex) {
        if (viewRowIndex > GridTableManager.DEFAULT_ROW && viewRowIndex < dataTable.getRowCount()) {
            return dataTable.convertRowIndexToModel(viewRowIndex);
        }
        return GridTableManager.DEFAULT_ROW;
    }

    public Integer convertColumnIndexToView(int modelColumnIndex) {
        if (modelColumnIndex > GridTableManager.DEFAULT_COLUMN && modelColumnIndex < dataTable.getColumnCount()) {
            return dataTable.convertColumnIndexToView(modelColumnIndex);
        }
        return GridTableManager.DEFAULT_COLUMN;
    }

    public Integer convertColumnIndexToModel(int viewColumnIndex) {
        if (viewColumnIndex > GridTableManager.DEFAULT_COLUMN && viewColumnIndex < dataTable.getColumnCount()) {
            return dataTable.convertColumnIndexToModel(viewColumnIndex);
        }
        return GridTableManager.DEFAULT_COLUMN;
    }

    public int getSelectionSize() {
        return selection.size();
    }

    public Vector<Integer> getSelection() {
        return selection;
    }

    public void clearSelection() {
        selection.removeAllElements();
    }

    public Integer getRowHeaderViewIndexByModelIndex(Integer modelIndex) {
        return rowHeaderIndexes.get(modelIndex);
    }

    public int getSelectedRow(int selRowIndex) {
        if (selRowIndex >= 0 && selRowIndex < selection.size()) {
            return selection.get(selRowIndex);
        }
        return DEFAULT_ROW;
    }

    public boolean containsSelectedRow(int selRowIndex) {
        return selection.contains(selRowIndex);
    }

    public int getColumnsVisibleSize() {
        if (metadataModel != null) {
            return metadataModel.getVColumnCount();
        } else {
            return 0;
        }
    }

    public int getCurrentRow() {
        return currentModelRow;
    }

    public int getCurrentColumn() {
        return currentModelColumn;
    }

    public void setCurrentRowByHeader(int x, int y) {
        int nextRow = dataTable.rowAtPoint(new Point(x + parent.getIntProperty(RmlConstants.BUTTONBAR_SIZE), y));
        if (nextRow != DEFAULT_ROW) {
            int nextModelRow = convertRowIndexToModel(nextRow);
            if (nextModelRow != DEFAULT_ROW && getCurrentRow() != nextModelRow) {
        setCurrentRow(nextRow, true, true);
    }
        }
    }

    public void setCurrentRow(int x, int y) {
        Integer row = dataTable.rowAtPoint(new Point(x, y));
        if (row != DEFAULT_ROW) {
            int nextModelRow = convertRowIndexToModel(row);
            if (nextModelRow != DEFAULT_ROW && getCurrentRow() != nextModelRow) {
        setCurrentRow(row, true, false);
    }
        }
    }

    public void setCurrentRow(Integer nextRow, boolean drawSelection, boolean needScrolling) {
        setCurrentRow(nextRow, drawSelection, needScrolling, false, false);
    }

    /**
     * Необходим для обработки выбранной строки в датасторе
     *
     * @param nextRow       - view index строки, которая будет считаться текущей
     * @param drawSelection - true если необходимо программно отрисовать строки
     *                      выбранными (отсутствует последействие),
     *                      false если после установки строки произойдет
     *                      действие, благодаря которому текущая строка сама
     *                      отрисуется (событие KeyEvent.DOWN и т.п.)
     * @param needScrolling - Надо ли прокручивать до выбранной строки,
     *                      например, при сортировке или заново загруженной
     *                      таблице. В некоторых случаях, когда перелистывается
     *                      страница прокручивать нет необходимости, это
     *                      осущетвляется программно (событие KeyEvent.DOWN и т.п.)
     * @param toggle - if ctrl pressed
     * @param extend - if shift pressed
     */
    public void setCurrentRow(Integer nextRow, boolean drawSelection, boolean needScrolling, boolean toggle, boolean extend) {

        // Проверяем на событие выбора всего и вся, если после него выбирается
        // какая либо из строк, то мы флаг выбора снимаем и восстанавливаем
        // механизм выборки строки - мультиселект или единичный выбор
        if (isCtrlA) {
            isCtrlA = false;
//            if (!parent.isMultiSelection()) {
//                dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//            }
        }

        // Заканчиваем редактирование ячейки, если таковое было
        if (dataTable.isEditing()) {
            stopEditing();
        }

        // Преобразуем индекс видимой строки к индексу строки в модели данных
        if (nextRow != DEFAULT_ROW && nextRow <= dataTable.getRowCount()) {
            nextRow = convertRowIndexToModel(nextRow);
        }

        // Выставляем выделение строки с заносом в массив selection
        // Индекс строки корректный
        Integer viewRow = DEFAULT_ROW;
        if (nextRow != DEFAULT_ROW) {
            viewRow = convertRowIndexToView(nextRow);
        }
        if (viewRow > DEFAULT_ROW && viewRow < dataTable.getRowCount()) {
            if (parent.isMultiSelection() || toggle || extend) {
                //Обработка таблицы с мультиселектом
                if (isRowSelected || toggle || extend) {
                    // Выбрана строка, а не ячейка
                    if (selection.contains(nextRow)) {  // && isPrevRowSelected
                        // Она уже была выбрана - снимаем с нее выделение
                        selection.remove(nextRow);
                        if (drawSelection) {
                            dataTable.removeRowSelectionInterval(viewRow, viewRow);
                        }
                        if (selection.size() > 0) {
                            nextRow = selection.lastElement();
                        } else {
                            nextRow = DEFAULT_ROW;
                        }
                    } else {
                        // Она не выбрана - добавляем к уже выделенным
                        if (extend) {
                            int startRow = convertRowIndexToView(currentModelRow) + 1;
                            for (int i = startRow; i < viewRow; i++) {
                                int modelRow = convertRowIndexToModel(i);
                                selection.add(modelRow);
                            }
                        }
                        selection.add(nextRow);
                        if (drawSelection) {
                            dataTable.addRowSelectionInterval(viewRow, viewRow);
                        }
                    }
                } else {
                    // Выбрана ячейка - снимаем все выделение и выделяем только
                    // эту строку
                    selection.removeAllElements();
                    selection.add(nextRow);
                    if (drawSelection) {
                        dataTable.setRowSelectionInterval(viewRow, viewRow);
                    }
                }
            } else {
                // Таблица с единичным выбором строк
                if (isRowSelected) {
                    // Выбрана строка, а не ячейка
                    if (selection.contains(nextRow)) { //isPrevRowSelected
                        // Она уже была выбрана - снимаем с нее выделение
                        selection.removeAllElements();
                        if (drawSelection) {
                            dataTable.removeRowSelectionInterval(viewRow, viewRow);
                        }
                    } else {
                        // Она не выбрана - выделяем
                        selection.removeAllElements();
                        selection.add(nextRow);
                        if (drawSelection) {
                            dataTable.setRowSelectionInterval(viewRow, viewRow);
                        }
                    }
                } else {
                    // Выбрана ячейка - снимаем все выделение и выделяем только
                    // эту строку
                    selection.removeAllElements();
                    selection.add(nextRow);
                    if (drawSelection) {
                        dataTable.setRowSelectionInterval(viewRow, viewRow);
                    }
                }
            }
        } else {
            // Индекс строки не вписывается в таблицу, возможно, перед нами
            // новая таблица или просто хотят снять выделение
            // Чистимся, снимаем отовсюду выделение
            selection.removeAllElements();
            if (drawSelection) {
                dataTable.clearSelection();
            }
            nextRow = GridTableManager.DEFAULT_ROW;
        }

        // Запоминаем новую текущую строку в таблице
        currentModelRow = nextRow;

        // Прокручиваем до текущей строки, если ее не видно
        if (needScrolling && currentModelRow != GridTableManager.DEFAULT_ROW) {
            viewRow = convertRowIndexToView(currentModelRow);
            if (needScroll(viewRow)) {
                scrollToRow(viewRow, false);
            }
        }
        if (!parent.isDsEmpty()) {
            // Запоминаем новую текущую строку в датасторе
            parent.saveDsCurRow(nextRow);
        }
    }

    /**
     * Возвращает состояние выборки в таблице
     *
     * @return true - если выбрана какая либо строка (клик мышкой по кнопке с
     *         индексом строки) false - если выбрана какая-либо ячейка (клик
     *         мышкой по ячейке таблицы)
     */
    public boolean isRowSelected() {
        return isRowSelected;
    }

    /**
     * Флаг, отмечающий, выбрана строка или ячейка Если выбрана строка, то true
     * Если выбрана ячейка, то false
     *
     * @param rowSelected - флаг
     */
    public void setRowSelected(boolean rowSelected) {
        if (rowSelected != isRowSelected) {
        isRowSelected = rowSelected;
            refreshView();
        }
    }


    /**
     * Устанавливает текущий столбец
     *
     * @param column - индекс текущего столбца во view model
     * @param draw   - надо ли выделять столбец
     */
    public void setCurrentColumn(int column, boolean draw) {
        if (column != GridTableManager.DEFAULT_COLUMN) {
            column = convertColumnIndexToModel(column);
        }
        currentModelColumn = column;
        if (draw && column >= 0 && column < dataTable.getColumnCount()) {
            dataTable.setColumnSelectionInterval(column, column);
        }
    }

    /**
     * Устанавливает текущий столбец
     *
     * @param column - индекс текущего столбца во view model
     */
    public void setCurrentColumn(int column) {
        setCurrentColumn(column, false);
    }

    /**
     * Устанавливает текущий столбец
     *
     * @param x - X координата текущего столбца во view model
     * @param y - Y координата текущего столбца во view model
     */
    public void setCurrentColumn(int x, int y) {
        int column = dataTable.columnAtPoint(new Point(x, y));
        setCurrentColumn(column);
    }

    /**
     * Устанавливает текущий столбец
     *
     * @param x - X координата текущего столбца во view model
     * @param y - Y координата текущего столбца во view model
     */
    public void setCurrentColumn(int x, int y, boolean draw) {
        int column = dataTable.columnAtPoint(new Point(x, y));
        setCurrentColumn(column, draw);
    }

    public void setSelection(Vector<Integer> newSelection) {
        if (newSelection == null || newSelection.size() == 0) {
            dataTable.clearSelection();
            selection = newSelection;
        } else if (isCtrlA) {
            for (Integer selectedIndex : selection) {
                dataTable.addRowSelectionInterval(selectedIndex, selectedIndex);
            }
        } else if (!parent.isMultiSelection() && newSelection.size() > 1) {
            selection.removeAllElements();
            selection.add(newSelection.get(0));
            dataTable.setRowSelectionInterval(selection.get(0), selection.get(0));
        } else {
            selection = newSelection;
            for (Integer selectedIndex : selection) {
                dataTable.addRowSelectionInterval(selectedIndex, selectedIndex);
            }
        }
    }

    public void restoreSelection() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
        if (isCtrlA) {
            dataTable.selectAll();
        } else if (selection != null && selection.size() != 0) {
            for (Integer selectedIndex : selection) {
                int viewRow = convertRowIndexToView(selectedIndex);
                if (viewRow >= 0 && (dataTable.getRowSorter() == null ||
                        (dataTable.getRowSorter() != null && viewRow < dataTable.getRowSorter().getViewRowCount()))) {
                    dataTable.addRowSelectionInterval(viewRow, viewRow);
                }
            }
        }
        if (currentModelColumn != DEFAULT_COLUMN) {
            int viewColumn = convertColumnIndexToView(currentModelColumn);
            dataTable.setColumnSelectionInterval(viewColumn, viewColumn);
        }
    }
        });
    }

    public boolean isEditing() {
        return dataTable.isEditing();
    }

    public boolean startEditAtCell(int row, int column) {
        return dataTable.editCellAt(row, column);
    }

    public void startEditing() {
        Component editorComponent = dataTable.getEditorComponent();
        if (editorComponent != null && editorComponent instanceof CommonField && ((CommonField) editorComponent).isEditable()) {
            ((CommonField) editorComponent).startEditing();
        }
        if (editorComponent != null) {
            editorComponent.requestFocus();
        }
    }

    public void stopEditing() {
        tableCellEditor.stopCellEditing();
        arrayCellEditor.stopCellEditing();
//        dataTable.editingStopped(new ChangeEvent(this));
    }

    public void repaintColumns() {
        if (dataTable.getModel() != null) {
            ((AbstractTableModel) dataTable.getModel()).fireTableStructureChanged();
            for (int i = 0; i < getVColumnCount(); i++) {
                int size = getVColumn(i).getColumn().getWidth();//tableManager.getVColumn(i).getWidth();
                if (size > 0) {
                    dataTable.getColumnModel().getColumn(i).setPreferredWidth(size);//tableManager.getTColumn(i).setPreferredWidth(size);
                }
            }
        }
    }

    public void repaintAll() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

        if (rowSorter != null) {
            rowSorter.modelStructureChanged();
        }
        if (dataTable.getModel() != null) {
            dataTable.tableChanged(new TableModelEvent(dataTable.getModel()));
        }
        parent.getVisualComponent().repaint();
        initRowHeader();
        tableContainer.revalidate();
        tableContainer.repaint();

        //TODO scrollPane was used there
//        scrollPane.getRowHeader().repaint();
    }
        });
    }

    public void requestFocusThis() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
        dataTable.requestFocusInWindow();
    }
        });
    }

    public int getSelectedRow() {
        return dataTable.getSelectedRow();
    }

    public int getSelectedColumn() {
        return dataTable.getSelectedColumn();
    }

    public void addSelecteRow(Integer selRowIndex) {
        selection.addElement(selRowIndex);
    }

    public void setValueAt(Object value, int row, int column) {
        dataTable.getModel().setValueAt(value, row, column);
    }

    public Dimension getTableSize() {
        return dataTable.getParent().getSize();
    }

    public Rectangle getCurrentCellRectangle() {
        return dataTable.getCellRect(convertRowIndexToView(getCurrentRow()), 0, true);
    }

    public int getColumnAtPoint(Point columnPoint) {
        return dataTable.columnAtPoint(columnPoint);
    }

    public int getRowAtPoint(Point rowLocation) {
        return dataTable.rowAtPoint(rowLocation);
    }

    public int getRowCount() {
        return dataTable.getRowCount();
    }

    public void removeSelectedRow(Integer selRowIndex) {
        selection.remove(selRowIndex);
    }

    public boolean selectAll() {
        if (parent.isMultiSelection()) {
            dataTable.selectAll();
        } else {
            dataTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            dataTable.selectAll();
        }
        isCtrlA = true;
        return isCtrlA;
    }

    public boolean isAllSelected() {
        return isCtrlA;
    }

    public Object getValueAt(int row, int col) {
        return dataTable.getValueAt(row, col);
    }

    public void setSelectedBackground(JTable source) {
        source.getTableHeader().setBackground(parent.getColorProperty(RmlConstants.SELTITLE_BG_COLOR));
    }

    public void setUnselectedBackground(JTable source) {
        source.getTableHeader().setBackground(parent.getColorProperty(RmlConstants.TITLEBAR_BG_COLOR));
    }

    public void clearSelection(int limit) {
        Enumeration<Integer> elements = selection.elements();
        while (elements.hasMoreElements()) {
            Integer i = elements.nextElement();
            if (i > limit) {
                selection.removeElement(i);
            }
        }
    }


    /**
     * Проверяет, попадает ли строка в видимую область таблицы
     *
     * @param rowIndex - индекс строки, которую надо проверить
     * @return needToScroll - видно ли строку
     */
    public boolean needScroll(int rowIndex) {
        boolean needScroll = false;
        Rectangle visibleRect = dataTable.getVisibleRect();
        if (rowIndex > GridTableManager.DEFAULT_ROW && rowIndex <= getRowCount()) {
            if (visibleRect != null && visibleRect.width > 0 && visibleRect.height > 0) {
                Rectangle cellRectangle = dataTable.getCellRect(rowIndex, 0, true);
                if ((cellRectangle.y <= visibleRect.y && cellRectangle.y != 0)
                        || cellRectangle.y >= (visibleRect.y + visibleRect.height)) {
                    needScroll = true;
                }
            } else if (!dataTable.isShowing()) {
                needToBeScrolling = true;
            }
        }
        return needScroll;
    }

    /**
     * Прокручивает таблицу к строке, указанной в параметрах
     *
     * @param rowIndex      - индекс строки, к которой надо прокрутить
     * @param needSelection - надо ли при этом выбирать строку
     */
    public void scrollToRow(final int rowIndex, final boolean needSelection) {
        Rectangle visibleRect = dataTable.getVisibleRect();
        if (rowIndex > GridTableManager.DEFAULT_ROW && rowIndex <= getRowCount()) {
            if (visibleRect != null && visibleRect.width > 0 && visibleRect.height > 0) {
                if (needSelection) {
                    dataTable.getSelectionModel().setSelectionInterval(rowIndex, rowIndex);
                }
                Rectangle cellRectangle = dataTable.getCellRect(rowIndex, 0, true);
                if (cellRectangle.y >= 0 && cellRectangle.y <= dataTable.getHeight()) {
                    visibleRect.y = cellRectangle.y;
                    visibleRect.height = cellRectangle.height;
                    dataTable.scrollRectToVisible(visibleRect);
                }
                needToBeScrolling = false;
            } else if (!dataTable.isShowing()) {
                needToBeScrolling = true;
            }
        }
    }

    public JTable getDataTable() {
        return dataTable;
    }

    public boolean needAllignLater() {
        return needToBeAlligned;
    }

    public void resetNeedAllignLater() {
        needToBeAlligned = false;
    }

    public void allign() {
        needAllign = true;
        if (parent.getVisualComponent().getJComponent().isShowing()) {
            Integer totalColumnWidth = dataTable.getColumnModel().getTotalColumnWidth();
            int verticalBarWidth = 0;
            if (tableContainer instanceof ZScrollPane) {
                ZScrollPane scrollPane = ((ZScrollPane) tableContainer);
                verticalBarWidth = scrollPane.getVerticalScrollBar().getWidth();
            } else if (tableContainer instanceof CrossTablePanel) {
                ZScrollPane scrollPane = ((CrossTablePanel) tableContainer).getMainScrollPane();
                verticalBarWidth = scrollPane.getVerticalScrollBar().getWidth();
            }
            Double necessaryWidth = Integer.valueOf(parent.getVisualComponent().getWidth() - verticalBarWidth).doubleValue();
            // - dataTable.getColumnModel().getColumnMargin() * dataTable.getColumnCount();
            if (rowHeader != null) {
                if (rowHeader.getSize().getWidth() > 0) {
                    necessaryWidth -= rowHeader.getSize().getWidth();
                } else if (rowHeader.getPreferredSize().getWidth() > 0) {
                    necessaryWidth -= rowHeader.getPreferredSize().getWidth();
                } else if (rowHeader instanceof JList && ((JList) rowHeader).getFixedCellWidth() > 0) {
                    necessaryWidth -= ((JList) rowHeader).getFixedCellWidth();
                }
            }
            Integer curColumnWidth;
            Double curColumnWidthInPercent;
            Double newColumnWidth;
            if (necessaryWidth > 0) {
                for (int i = 0; i < dataTable.getColumnCount(); i++) {
                    curColumnWidth = dataTable.getColumnModel().getColumn(i).getWidth();
                    curColumnWidthInPercent = (curColumnWidth.doubleValue() * 10000 / totalColumnWidth.doubleValue()) / 10000;
                    newColumnWidth = necessaryWidth * curColumnWidthInPercent;
                    dataTable.getColumnModel().getColumn(i).setPreferredWidth(newColumnWidth.intValue());
                }
                repaintAll();
                restoreSelection();
            }
            needToBeAlligned = false;
        } else {
            needToBeAlligned = true;
        }
    }

    public GridUIManager getUIManager() {
        return gridUiManager;
    }

    public boolean needAllign() {
        return needAllign;
    }

    public GridMetadataModel getMetadataModel() {
        return metadataModel;
    }

    public void setMetadataModel(GridMetadataModel metadataModel) {
        this.metadataModel = metadataModel;
    }

    public void refreshView() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
        dataTable.revalidate();
        dataTable.repaint();
    }
        });
    }

    public void setFirstTime(boolean firstTime) {
        this.firstTime = firstTime;
    }

    public boolean isFirstTime() {
        return firstTime;
    }

    public ZComponent getTableContainer() {
        return tableContainer;
    }

    public void activateTable() {
        if (parent.getBooleanProperty(RmlConstants.MULTILINE)) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
            dataTable.doLayout();
        }
            });
        }
    }

    public boolean inFilteringMode() {
        return filteringMode;
    }

    public boolean isFilterColumn(Integer columnIndex) {
        return columnFilterIndexes.contains(columnIndex);
    }

    public void setColumnTitle(GridColumn column, String title) {
            String oldTitle = column.getTitle();
            JTableHeader th = dataTable.getTableHeader();
            TableColumnModel tcm = th.getColumnModel();
            int columnIndex = -1;
            try {
                columnIndex = tcm.getColumnIndex(oldTitle);
            } catch (IllegalArgumentException e) {
                //no such columns with titles
            }
            if (columnIndex != -1) {
                column.setTitle(title);
                TableColumn tc = tcm.getColumn(columnIndex);
                tc.setHeaderValue(title);
                th.repaint();
            }
        }

    public void setColumnVisible(GridColumn column, boolean visible) {
        column.setVisible(visible);
        int movedData = metadataModel.setColumnVisible(column, visible);
        if (movedData != -1) {
            repaintColumns();
            if (!visible && movedData == getCurrentColumn()) {
                setCurrentColumn(0);
            }
        }
    }

    public Object isColumnVisible(GridColumn column) {
        //Integer columnIndex = metadataModel.getColumnIndexByTarget(columnTarget);
    	Integer columnIndex =  metadataModel.getColumnIndexByTarget(column.getTarget()); 
        return metadataModel.isColumnsVisible(columnIndex);
    }

    public void addDynamicColumn(String columnName, GridMetadataModel metadataModel) {
        int type = parent.getDatastore().getModel().getColumnType(columnName);
        addColumn(null, columnName, type, columnName, -1, -1, "",
                metadataModel, false);
    }

    public void addTypeColumn(String columnAlias, String columnType, String columnTitle, Integer columnWidth, Integer columnPosition, String editStyle) {
        int type = GridColumn.generateType(columnType);
        String columnTarget = parent.getDatastore().addColumn(type, true);
        addColumn(columnAlias, columnTarget, type, columnTitle, columnWidth,
                columnPosition, editStyle, metadataModel, true);
    }

    public void addTargetColumn(String columnAlias, String columnTarget, String columnTitle, Integer columnWidth, Integer columnPosition, String editStyle) {
        int type = parent.getDatastore().getModel().getColumnType(columnTarget);
        addColumn(columnAlias, columnTarget, type, columnTitle, columnWidth,
                columnPosition, editStyle, metadataModel, true);
    }

    public void addComboColumn(String columnAlias, String columnTarget,
                               String columnTitle, Integer columnWidth,
                               Integer columnPosition, String editStyle,
                               String values, Datastore ds) {
        int type = -1;
        if (ds != null) {
            type = ds.getModel().getColumnType(columnTarget);
        } else {
            type = parent.getDatastore().getModel().getColumnType(columnTarget);
        }
        if (type == -1) {
            type = parent.getDatastore().getModel().getColumnType(columnTarget);
        }
        addColumn(columnAlias, columnTarget, type, columnTitle, columnWidth,
                columnPosition, values, ds, editStyle, metadataModel, true);
    }

    public void addComboTypeColumn(String columnAlias, String columnType,
                               String columnTitle, Integer columnWidth,
                               Integer columnPosition, String editStyle,
                               String values, Datastore ds) {
        int type = GridColumn.generateType(columnType);
        String columnTarget = parent.getDatastore().addColumn(type, true);
        addColumn(columnAlias, columnTarget, type, columnTitle, columnWidth,
                columnPosition, values, ds, editStyle, metadataModel, true);
    }

    public void addColumn(String columnAlias, String columnTarget, int columnType,
                          String columnTitle, Integer columnWidth,
                          Integer columnPosition, String editStyle,
                          GridMetadataModel metadataModel, boolean needRepaint) {
        addColumn(columnAlias, columnTarget, columnType, columnTitle, columnWidth,
                columnPosition, null, null, editStyle, metadataModel, needRepaint);
    }

    public void addColumn(String columnAlias, String columnTarget,
                          int columnType, String columnTitle,
                          Integer columnWidth, Integer columnPosition,
                          String values, Datastore ds, String editStyle,
                          GridMetadataModel metadataModel, boolean needRepaint) {
        Proper prop;
        Proper defp = ColumnTemplate.getDefaultProperties();
        ColumnTemplate ct = null;
        if (columnType != -1) {
            int type = GridSwing.getJType(columnType);
            if (type >= 0) {
                ct = metadataModel.getColumnTemplate(type);
            }
        }
        if (ct != null) {
            prop = ct.getProperties();
        } else {
            prop = defp;
        }

        GridColumn newColumn = new GridColumn();
        newColumn.setParent(parent);
        newColumn.init(prop, parent.getDoc());
        newColumn.setTitle(columnTitle);
        if (columnAlias != null) {
            newColumn.setAlias(columnAlias);
            parent.getDoc().registrate(newColumn);
        }
        newColumn.setTarget(columnTarget);
        if (columnWidth >= 0) {
            newColumn.getColumn().setWidth(columnWidth);
        }
        if (editStyle != null && !editStyle.isEmpty()) {
            newColumn.setEditStyle(editStyle);
        }
        if (values != null && !values.isEmpty()) {
            newColumn.setIsArray(true);
            newColumn.setValues(values);
            newColumn.setDatastore(ds);
        }
        newColumn.setType(columnType);
        if (columnPosition >= 0 && columnPosition < metadataModel.getVColumnCount()) {
            metadataModel.addColumn(columnPosition, newColumn);
        } else {
            metadataModel.addColumn(newColumn);
        }
        if (needRepaint) {
            repaintColumns();
        }
    }

    public void deleteColumn(GridColumn column) {
        int deletedColumnIndex = parent.getDatastore().getModel().deleteColumn(column.getTarget());
        if (deletedColumnIndex == -1) {
            log.warn("Can't find column " + column.getTarget() + " in datastore");
        }
        metadataModel.deleteColumn(column);
        dataTable.getColumnModel().removeColumn(column.getColumn());
        repaintColumns();
    }

    public int getTableColumnModelIndex(GridColumn column) {
    	//int columnModelIndex = metadataModel.getColumnIndexByTarget(column.getTarget());
        int columnModelIndex = metadataModel.getColumnModelIndex(column);
        
        if (columnModelIndex != -1) {
            try {
                dataTable.getColumnModel().getColumn(columnModelIndex);
                return dataTable.convertColumnIndexToView(columnModelIndex);
            } catch (ArrayIndexOutOfBoundsException e) {
                return -1;
            }
        }
        return -1;
    }

    public void insertValueAt(int rownum, int colIndex, Object value) {
        int oldCurColumn = getCurrentColumn();
        int oldCurRow = getCurrentRow();
        setCurrentRow(rownum, false, false);
        setCurrentColumn(colIndex);
        parent.setToDSSaved(false);
        setValueAt(value, rownum, colIndex);
        setCurrentColumn(oldCurColumn);
        setCurrentRow(oldCurRow, true, false);
    }

    public TableRowSorter<TableModel> getRowSorter() {
        return rowSorter;
    }

    public ArrayList<RowSorter.SortKey> getSortKeys() {
        return sortKeys;
    }

    public String getRowTitle(int rowIndex) {
        return rowTitles.get(rowIndex);
    }

    public boolean containsRowTitle(int rowIndex) {
        return rowTitles.containsKey(rowIndex);
    }

    public void setRowTitle(int rowIndex, String title) {
        rowTitles.put(rowIndex, title);
    }

    public void deleteRowTitle(int rowIndex) {
        rowTitles.remove(rowIndex);
    }
}