package views.grid.renderer;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.EventListener;
import java.util.List;

import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import views.grid.GridSwing;
import views.grid.renderer.cross.CellSpanTable;
import views.grid.renderer.cross.ColumnHeaderRenderer;
import views.grid.renderer.cross.CrossTablePanel;
import views.grid.renderer.cross.RowsHeaderRenderer;
import views.grid.renderer.grid.GridColumnHeader;
import views.grid.renderer.grid.GridColumnHeaderRenderer;
import views.grid.renderer.grid.RowHeaderListModel;
import views.grid.renderer.grid.RowHeaderRenderer;
import core.rml.RmlConstants;
import core.rml.ui.interfaces.ZComponent;
import core.rml.ui.interfaces.ZScrollPane;

/**
 * @author: vagapova.m
 * @since: 03.10.2010
 */
public class GridHeaderFactory {

    private static GridHeaderFactory instance;

    public static GridHeaderFactory getInstance() {
        if (instance == null) {
            instance = new GridHeaderFactory();
        }
        return instance;
    }

    public void initColumnHeader(GridSwing parent, ZComponent tableContainer, JTable dataTable, List<EventListener> listeners) {
        if (parent.isCross()) {
            if (tableContainer instanceof CrossTablePanel) {
                CellSpanTable colsTable = ((CrossTablePanel) tableContainer).getColsTable();
                // remove headers from all tables...
                JTableHeader th = colsTable.getTableHeader();
                th.setPreferredSize(new Dimension(th.getPreferredSize().width, 0));
                th.setReorderingAllowed(false);
                th.setResizingAllowed(false);
                colsTable.setShowGrid(false);

                colsTable.setRowSelectionAllowed(false);
                colsTable.setColumnSelectionAllowed(false);
                colsTable.setBackground((parent.getColorProperty(RmlConstants.TITLEBAR_BG_COLOR)));
                for (EventListener listener : listeners) {
                    if (listener instanceof MouseListener) {
                        colsTable.addMouseListener((MouseListener) listener);
                    } else if (listener instanceof FocusListener) {
                        colsTable.addFocusListener((FocusListener) listener);
                    } else if (listener instanceof KeyListener) {
                        colsTable.addKeyListener((KeyListener) listener);
                    }
                }
                int rowWidth = parent.getIntProperty(RmlConstants.ROWSIZE);
                int rowHeight = parent.getIntProperty(RmlConstants.TITLEBAR_HEIGHT);
                int totalRowHeight = colsTable.getRowCount() * rowHeight;
                int k = 0;
                colsTable.setRowHeight(rowHeight);
                for (int i = 0; i < colsTable.getColumnModel().getColumnCount(); i++) {
//                colsTable.getColumnModel().getColumn(i).setPreferredWidth(parametersAccessor.getDataField(k).getWidth());
                    colsTable.getColumnModel().getColumn(i).setWidth(rowWidth);
                    colsTable.getColumnModel().getColumn(i).setCellRenderer(new ColumnHeaderRenderer(parent));
                    k++;
//                if (k == parametersAccessor.getDataSize()) {
//                    k = 0;
//                }
                }
                Dimension d = colsTable.getPreferredScrollableViewportSize();
                d.height = totalRowHeight;
                colsTable.setPreferredScrollableViewportSize(d);
//                colsTable.setSize(colsTable.getPreferredSize());
            }
        } else {
            TableCellRenderer defaultHeaderRenderer = dataTable.getTableHeader().getDefaultRenderer();
            if (parent.isContainsSets()) {
                dataTable.setTableHeader(new GridColumnHeader(parent, dataTable.getColumnModel()));
            }  else {
                //С сортировкой перемещается не только отсортированная ячейка, а вся строка целиком
                dataTable.getTableHeader().setReorderingAllowed(true);
            }
            int titleBarHeight = parent.getIntProperty(RmlConstants.TITLEBAR_HEIGHT);
            if (titleBarHeight != GridSwing.DEFAULT_HEIGHT) {
                int totalColWidth = dataTable.getColumnModel().getTotalColumnWidth();
                dataTable.getTableHeader().setPreferredSize(new Dimension(totalColWidth, titleBarHeight));
            }
            //Для отображения стилей заголовков столбцов
            GridColumnHeaderRenderer columnHeaderRenderer = new GridColumnHeaderRenderer(defaultHeaderRenderer, parent.getTableManager());
            dataTable.getTableHeader().setDefaultRenderer(columnHeaderRenderer);
            for (EventListener listener : listeners) {
                if (listener instanceof MouseListener) {
                    dataTable.getTableHeader().addMouseListener((MouseListener) listener);
                } else if (listener instanceof FocusListener) {
                    dataTable.getTableHeader().addFocusListener((FocusListener) listener);
                } else if (listener instanceof KeyListener) {
                    dataTable.getTableHeader().addKeyListener((KeyListener) listener);
                }
            }
        }
    }

    public Component initRowHeader(GridSwing parent, ZComponent tableContainer, List<EventListener> listeners) {
        //Для создания заголовков строк
        if (parent.isCross()) {
            JTable rowsTable = null;
            if (tableContainer instanceof CrossTablePanel) {
                rowsTable = ((CrossTablePanel) tableContainer).getRowsTable();
                // remove headers from all tables...
                JTableHeader th = rowsTable.getTableHeader();
                th.setPreferredSize(new Dimension(th.getPreferredSize().width, 0));
                th.setReorderingAllowed(false);
                th.setResizingAllowed(false);
                rowsTable.setShowGrid(false);

                rowsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                rowsTable.setSurrendersFocusOnKeystroke(true);
                rowsTable.setRowSelectionAllowed(false);
                rowsTable.setColumnSelectionAllowed(false);
                rowsTable.setForeground(parent.getColorProperty(RmlConstants.BUTTONBAR_FONT_COLOR));
                rowsTable.setBackground(parent.getColorProperty(RmlConstants.TITLEBAR_BG_COLOR));
                int rowWidth = parent.getIntProperty(RmlConstants.BUTTONBAR_SIZE);
                rowsTable.setIntercellSpacing(new Dimension(0, 0));
                int totalRowWidth = 0;
//                // define columns width for all tables...
                for (int i = 0; i < rowsTable.getColumnModel().getColumnCount(); i++) {
                    rowsTable.getColumnModel().getColumn(i).setPreferredWidth(rowWidth);
                    rowsTable.getColumnModel().getColumn(i).setCellRenderer(new RowsHeaderRenderer(parent));
                    totalRowWidth += rowWidth;
                }
                rowsTable.setRowHeight(parent.getIntProperty(RmlConstants.ROWSIZE));
                Dimension d = rowsTable.getPreferredScrollableViewportSize();
                d.width = totalRowWidth;
                rowsTable.setPreferredScrollableViewportSize(d);
            }
            return rowsTable;
        } else {
            //Компонент, отвечающий за нумерацию строк-кнопок в таблице
            ListModel rowListModel = new RowHeaderListModel(parent);
            JList rowHeader = new GridRowHeader(rowListModel);
            RowHeaderRenderer rowHeaderRenderer = new RowHeaderRenderer(parent);
            rowHeader.setCellRenderer(rowHeaderRenderer);

            rowHeader.setFixedCellWidth(parent.getIntProperty(RmlConstants.BUTTONBAR_SIZE));
            if (!parent.getBooleanProperty(RmlConstants.MULTILINE)) {
                rowHeader.setFixedCellHeight(parent.getIntProperty(RmlConstants.ROWSIZE));
            }
            rowHeader.setBackground(parent.getColorProperty(RmlConstants.TITLEBAR_BG_COLOR));

            for (EventListener listener : listeners) {
                if (listener instanceof MouseListener) {
                    rowHeader.addMouseListener((MouseListener) listener);
                } else if (listener instanceof FocusListener) {
                    rowHeader.addFocusListener((FocusListener) listener);
                } else if (listener instanceof KeyListener) {
                    rowHeader.addKeyListener((KeyListener) listener);
                }
            }
            if (tableContainer instanceof ZScrollPane) {
                ZScrollPane scrollPane = ((ZScrollPane) tableContainer);
                scrollPane.setRowHeaderView(rowHeader);
            }
            return rowHeader;
        }
    }
}
