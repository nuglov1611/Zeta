package views.grid.model;

import java.awt.Dimension;
import java.awt.event.ComponentListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.Date;
import java.util.EventListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.GroupLayout;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;

import org.apache.log4j.Logger;

import views.grid.GridSwing;
import views.grid.action.CopyAction;
import views.grid.action.DeleteAction;
import views.grid.action.EnterAction;
import views.grid.action.FocusNextAction;
import views.grid.action.FocusPrevAction;
import views.grid.action.InsertAction;
import views.grid.action.PasteAction;
import views.grid.dnd.GridTransferHandler;
import views.grid.editor.ArrayCellEditor;
import views.grid.editor.BooleanCellEditor;
import views.grid.listener.GridColumnSelectionListener;
import views.grid.listener.GridRowSelectionListener;
import views.grid.manager.GridTableManager;
import views.grid.model.cross.CrossColumnModel;
import views.grid.model.cross.CrossRowModel;
import views.grid.renderer.GridTable;
import views.grid.renderer.cell.ArrayCellRenderer;
import views.grid.renderer.cell.BooleanCellRenderer;
import views.grid.renderer.cell.DateCellRenderer;
import views.grid.renderer.cell.NumericCellRenderer;
import views.grid.renderer.cell.StringCellRenderer;
import views.grid.renderer.cross.CellSpanModel;
import views.grid.renderer.cross.CellSpanTable;
import views.grid.renderer.cross.CrossTablePanel;
import core.rml.RmlConstants;
import core.rml.ui.impl.ZScrollPaneImpl;
import core.rml.ui.interfaces.ZComponent;
import core.rml.ui.interfaces.ZScrollPane;

/**
 * @author vagapova.m
 * @since 18.10.2010
 */
public class GridTableFactory {
    private static final Logger log = Logger.getLogger(GridTableFactory.class);

    private static GridTableFactory instance;

    public static GridTableFactory getInstance() {
        if (instance == null) {
            instance = new GridTableFactory();
        }
        return instance;
    }

    private void initKeySequences(GridSwing parent, JComponent dataTable) {
        CopyAction copyAction = new CopyAction(parent);
        PasteAction pasteAction = new PasteAction(parent);
        EnterAction enterAction = new EnterAction(parent);
        InsertAction insertAction = new InsertAction(parent);
        DeleteAction deleteAction = new DeleteAction(parent);
        InputMap im = dataTable.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        KeyStroke ctrlC = KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK, false);
        im.put(ctrlC, copyAction);
        KeyStroke ctrlV = KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK, false);
        im.put(ctrlV, pasteAction);
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
        im.put(enter, enterAction);
        KeyStroke ctrlIns = KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, KeyEvent.CTRL_DOWN_MASK, false);
        im.put(ctrlIns, insertAction);
        KeyStroke ctrlDel = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.CTRL_DOWN_MASK, false);
        im.put(ctrlDel, deleteAction);
        dataTable.setInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, im);

        // Настраиваем Focus traversal
        FocusNextAction nextFocusAction = new FocusNextAction();
        FocusPrevAction prevFocusAction = new FocusPrevAction();
        KeyStroke tab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0, false);
        im.getParent().remove(tab);
        KeyStroke shiftTab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.SHIFT_DOWN_MASK, false);
        im.getParent().remove(shiftTab);
        dataTable.getInputMap(JComponent.WHEN_FOCUSED).put(tab, GridTableManager.NEXT_FOCUS_ACTION);
        dataTable.getActionMap().put(GridTableManager.NEXT_FOCUS_ACTION, nextFocusAction);
        dataTable.getInputMap(JComponent.WHEN_FOCUSED).put(shiftTab, GridTableManager.PREV_FOCUS_ACTION);
        dataTable.getActionMap().put(GridTableManager.PREV_FOCUS_ACTION, prevFocusAction);
    }

    public ZComponent getTableComponent(GridSwing parent, GridTableManager tableManager,
                                        List<EventListener> listeners, List<TableCellEditor> editors) {
        if (parent.isCross()) {
            CrossTablePanel mainPanel = CrossTablePanel.create();
            final ZScrollPane dataScrollPane = ZScrollPaneImpl.create();
            dataScrollPane.setBorder(BorderFactory.createEmptyBorder());
            dataScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            dataScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            final CellSpanTable rowsTable = new CellSpanTable();
            rowsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

            dataScrollPane.setRowHeaderView(rowsTable);

            final CellSpanTable colsTable = new CellSpanTable();
            colsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            dataScrollPane.setColumnHeaderView(colsTable);

            JTable dataTable = new JTable() {
                //http://www.rsdn.ru/forum/java/797273.flat.aspx
                protected void configureEnclosingScrollPane() {
                    super.configureEnclosingScrollPane();
                    dataScrollPane.setColumnHeaderView(colsTable); //СѓСЃС‚Р°РЅРѕРІРёРј ColumnHeader РїРѕСЃР»Рµ С‚РѕРіРѕ РєР°Рє Сѓ РЅР°СЃ С‚Р°Рј С‡С‚Рѕ-С‚Рѕ РїРѕРјРµРЅСЏРµС‚СЃСЏ
                    dataScrollPane.setRowHeaderView(rowsTable); //СѓСЃС‚Р°РЅРѕРІРёРј RowHeader РїРѕСЃР»Рµ С‚РѕРіРѕ РєР°Рє Сѓ РЅР°СЃ С‚Р°Рј С‡С‚Рѕ-С‚Рѕ РїРѕРјРµРЅСЏРµС‚СЃСЏ
                }
            };
            dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            dataTable.setDragEnabled(true);
            dataTable.setShowGrid(true);
            dataTable.setDropMode(DropMode.ON);
            dataTable.setTransferHandler(new GridTransferHandler(parent));
            dataScrollPane.setViewportView(dataTable);

            GroupLayout mainPanelLayout = new GroupLayout(mainPanel.getJComponent());
            mainPanel.setLayout(mainPanelLayout);

            mainPanelLayout.setHorizontalGroup(
                mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(dataScrollPane.getJComponent(), GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
            );
            mainPanelLayout.setVerticalGroup(
                mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(dataScrollPane.getJComponent(), GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
            );

            mainPanel.setMainScrollPane(dataScrollPane);
            mainPanel.setColsTable(colsTable);
            mainPanel.setRowsTable(rowsTable);
            mainPanel.setDataTable(dataTable);
            return mainPanel;
        } else {
            JTable dataTable = new GridTable(parent);
            ZScrollPane scrollPane = ZScrollPaneImpl.create();

            dataTable.setDragEnabled(true);
            dataTable.setDropMode(DropMode.ON);
            dataTable.setTransferHandler(new GridTransferHandler(parent));
//            dataTable.putClientProperty("JTable.autoStartsEdit", Boolean.FALSE);

            for (EventListener listener : listeners) {
                if (listener instanceof MouseListener) {
                    dataTable.addMouseListener((MouseListener) listener);
                    scrollPane.addMouseListener((MouseListener) listener);
                } else if (listener instanceof FocusListener) {
                    dataTable.addFocusListener((FocusListener) listener);
                    parent.getVisualComponent().addFocusListener((FocusListener) listener);
                } else if (listener instanceof KeyListener) {
                    dataTable.addKeyListener((KeyListener) listener);
                } else if (listener instanceof ComponentListener) {
                    scrollPane.addComponentListener((ComponentListener) listener);
                } else if (listener instanceof GridColumnSelectionListener) {
                    ListSelectionModel colSM =
                            dataTable.getColumnModel().getSelectionModel();
                    colSM.addListSelectionListener((ListSelectionListener) listener);
                } else if (listener instanceof GridRowSelectionListener) {
                    ListSelectionModel rowSM = dataTable.getSelectionModel();
                    rowSM.addListSelectionListener((ListSelectionListener) listener);
                }
            }

            initKeySequences(parent, dataTable);

            //Для выставления ширины столбца в зависимости от его свойства width
            dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            //C сортировкой сохраняется выделение
            dataTable.setUpdateSelectionOnSort(true);

            //Для отображения ячеек по типу объекта, содержащегося в них
            dataTable.setDefaultRenderer(Date.class, new DateCellRenderer(tableManager));
            dataTable.setDefaultRenderer(Double.class, new NumericCellRenderer(tableManager));
            dataTable.setDefaultRenderer(String.class, new StringCellRenderer(tableManager));
            dataTable.setDefaultRenderer(Object[].class, new ArrayCellRenderer(tableManager));
            dataTable.setDefaultRenderer(Boolean.class, new BooleanCellRenderer(tableManager));

            for (TableCellEditor editor : editors) {
                if (editor instanceof ArrayCellEditor) {
                    dataTable.setDefaultEditor(Object[].class, editor);
                } else if (editor instanceof BooleanCellEditor) {
                    dataTable.setDefaultEditor(Boolean.class, editor);
                } else {
                    dataTable.setDefaultEditor(Date.class, editor);
                    dataTable.setDefaultEditor(Double.class, editor);
                    dataTable.setDefaultEditor(String.class, editor);
                }
            }

            scrollPane.setViewportView(dataTable);
            scrollPane.getViewport().putClientProperty("EnableWindowBlit", true);
            //        scrollPane.getViewport().setBackground(dataTable.getBackground());
            return scrollPane;
        }
    }

    public AbstractTableModel initTableModel(GridSwing parent, GridTableManager tableManager, ZComponent tableContainer, JTable dataTable) {
        AbstractTableModel dataModel = null;
        if (parent.isCross() &&
                tableContainer instanceof CrossTablePanel) {
            CellSpanTable colsTable = ((CrossTablePanel) tableContainer).getColsTable();
            CellSpanTable rowsTable = ((CrossTablePanel) tableContainer).getRowsTable();

            //Building table
            try {
                parent.getCrossModelManager().compileData();

            // define TableModel for all tables...
                CrossColumnModel columnModel = parent.getCrossModelManager().getColumnModel();
                CellSpanModel colsSpanModel = parent.getCrossModelManager().getColsCellSpanModel();
            colsTable.setModel(columnModel);
                colsTable.setCellSpanModel(colsSpanModel);
                CrossRowModel rowModel = parent.getCrossModelManager().getRowModel();
                CellSpanModel rowSpanModel = parent.getCrossModelManager().getRowsCellSpanModel();
            rowsTable.setModel(rowModel);
            rowsTable.setCellSpanModel(rowSpanModel);
            dataModel = parent.getCrossModelManager().getDataModel();
            dataTable.setModel(dataModel);

                JTableHeader th = dataTable.getTableHeader();
            th.setPreferredSize(new Dimension(th.getPreferredSize().width, 0));
            th.setReorderingAllowed(false);
            th.setResizingAllowed(false);

                tableManager.initRowHeader();
                tableManager.initColumnHeader();
            }catch (Exception e) {
                log.warn("Issue during building model", e);
            }

        } else {
            tableManager.initRowHeader();
            tableManager.initColumnHeader();
            dataModel = new GridModel(parent);
            dataTable.setModel(dataModel);
            tableManager.setRowSorter(dataModel, null);

            for (int i = 0; i < tableManager.getVColumnCount(); i++) {
                int size = tableManager.getVColumn(i).getColumn().getWidth();//tableManager.getVColumn(i).getWidth();
                if (size > 0) {
                    dataTable.getColumnModel().getColumn(i).setPreferredWidth(size);//tableManager.getTColumn(i).setPreferredWidth(size);
                }
            }
        }
        return dataModel;
    }

    public void initTableProps(GridSwing parent, ZComponent tableContainer, JTable dataTable) {
        if (parent.isCross()) {
            dataTable.setRowHeight(parent.getIntProperty(RmlConstants.ROWSIZE));
            dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            dataTable.setSurrendersFocusOnKeystroke(true);
//      dataTable.setBackground(GRID_CELL_BACKGROUND);
//      dataTable.setForeground(GRID_CELL_FOREGROUND);
//      dataTable.setSelectionBackground(ClientSettings.GRID_SELECTION_BACKGROUND);
//      dataTable.setSelectionForeground(ClientSettings.GRID_SELECTION_FOREGROUND);
            dataTable.setShowGrid(true);
            dataTable.setRowSelectionAllowed(false);
            dataTable.setColumnSelectionAllowed(false);
        } else {
//            dataTable.getTableHeader().setSize(parent.getIntProperty(RmlConstants.TITLEBAR_SIZE), parent.getIntProperty(RmlConstants.BUTTONBAR_SIZE));
//        if (table.isFocusOwner()) {
//            table.getTableHeader().setBackground(tbbg_color);
//        } else {
//            table.getTableHeader().setBackground(tbsbg_color);
//        }
            dataTable.setBackground(parent.getColorProperty(RmlConstants.BG_COLOR));
            dataTable.setRowHeight(parent.getIntProperty(RmlConstants.ROWSIZE));
            dataTable.setSelectionForeground(parent.getColorProperty(RmlConstants.CURROW_COLOR));
            dataTable.setSelectionBackground(parent.getColorProperty(RmlConstants.CURROW_BG_COLOR));

            ((ZScrollPane) tableContainer).getVerticalScrollBar().setSize(parent.getIntProperty(RmlConstants.VSCROLLSIZE), parent.getVisualComponent().getHeight());
            ((ZScrollPane) tableContainer).getHorizontalScrollBar().setSize(parent.getVisualComponent().getWidth(), parent.getIntProperty(RmlConstants.HSCROLLSIZE));

//            if (!parent.isMultiSelection()) {
//                dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//            } else {
//                dataTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//            }

        }
    }
}
