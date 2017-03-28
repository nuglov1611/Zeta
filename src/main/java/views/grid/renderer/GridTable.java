package views.grid.renderer;

import core.rml.RmlConstants;
import core.rml.ui.interfaces.ZComponent;
import core.rml.ui.interfaces.ZScrollPane;
import org.apache.log4j.Logger;
import views.grid.GridSwing;
import views.grid.editor.CommonField;
import views.grid.manager.GridTableManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.plaf.basic.BasicListUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

/**
 * @author: vagapova.m
 * @since: 01.11.2010
 */
public class GridTable extends JTable {

    private static final Logger log = Logger.getLogger(GridTable.class);

    private GridSwing grid;

    public GridTable(GridSwing parent) {
        this.grid = parent;
        if (grid.getStringProperty(RmlConstants.EDIT_STYLE)
                .equalsIgnoreCase(RmlConstants.EDIT_STYLE_FAST)) {
            setFastEdit(true);
        } else {
            setFastEdit(false);
        }
    }

    private void updateRowHeader(List<Integer> size) {
        ZComponent tableContainer = grid.getTableManager().getTableContainer();
        if (tableContainer instanceof ZScrollPane) {
            Component rowHeader = ((ZScrollPane) tableContainer).getRowHeader().getView();
            if (rowHeader instanceof GridRowHeader) {
                ((GridRowHeader) rowHeader).setRowSize(size);
                computeListSize((GridRowHeader) rowHeader);
            }
        }
    }

    private void computeListSize(final JList list) {
        if (list.getUI() instanceof BasicListUI) {
            final BasicListUI ui = (BasicListUI) list.getUI();

            try {
                final Method method = BasicListUI.class.getDeclaredMethod("updateLayoutState");
                method.setAccessible(true);
                method.invoke(ui);
                list.revalidate();
                list.repaint();
            } catch (final SecurityException e) {
                e.printStackTrace();
            } catch (final NoSuchMethodException e) {
                e.printStackTrace();
            } catch (final IllegalArgumentException e) {
                e.printStackTrace();
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
            } catch (final InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void doLayout() {
        if (grid.getBooleanProperty(RmlConstants.MULTILINE)) {
            List<Integer> size = new ArrayList<Integer>();
            for (int row = 0; row < getRowCount(); row++) {
                int maxRowHeight = 0;
                for (int column = 0; column < getColumnModel().getColumnCount(); column++) {
                    TableCellRenderer renderer = getCellRenderer(row, column);
                    if (renderer != null) {
                        Component c = prepareRenderer(renderer, row, column);
                        if (c instanceof JTextArea) {
                            JTextComponent a = (JTextComponent) c;
                            int h = getPreferredHeight(a) +
                                    getIntercellSpacing().height;
                            if (h > maxRowHeight) {
                                maxRowHeight = h;
                            }
                        }
                    }
                }

                int currentHeight = getRowHeight(row);
                if (maxRowHeight > 0 && currentHeight != maxRowHeight) {
                    setRowHeight(row, maxRowHeight);
                    size.add(maxRowHeight);
                } else {
                    size.add(currentHeight);
                }
            }
            updateRowHeader(size);
        }
        super.doLayout();
    }

    private int getPreferredHeight(JTextComponent c) {
        Insets insets = c.getInsets();
        View view = c.getUI().getRootView(c).getView(0);
        int preferredHeight = (int) view.getPreferredSpan(View.Y_AXIS);
        return preferredHeight + insets.top + insets.bottom;
    }


    public boolean isFastEdit() {
        return fastEdit;
    }

    public void setFastEdit(boolean fastEdit) {
        this.fastEdit = fastEdit;
        if (!fastEdit) {
            putClientProperty("JTable.autoStartsEdit", Boolean.FALSE);
        }
    }

    private boolean fastEdit = false;

    public void editingStopped(ChangeEvent e) {
        super.editingStopped(e);
        //log.debug("editingStopped");
    }

    public void editingCanceled(ChangeEvent e) {
        super.editingCanceled(e);
        //log.debug("editingCanceled");
    }


    public void changeSelection(int row, int column, boolean toggle,
                                boolean extend) {
        super.changeSelection(row, column, toggle, extend);

//        if (fastEdit) {
//        if(grid.getTableManager().getCurrentRow() != row) {
//            grid.getTableManager().setCurrentRow(row, false, false, toggle, extend);
//        } else if (!grid.isDsEmpty() && grid.getDatastore().getCurRow() != row) {
//            // Запоминаем новую текущую строку в датасторе, возможна рассинхронизация ввиду скриптовых вызовов setCurRow для датастори
//            grid.saveDsCurRow(row);
//        }
//        if(grid.getTableManager().getCurrentColumn() != column) {
//            grid.getTableManager().setCurrentColumn(column, false);
//        }
//        if (column != GridTableManager.DEFAULT_COLUMN && grid.getTableManager().getVColumn(column).isArray()) {
//            grid.getTableManager().startEditAtCell(row, column);
//        }
//        }
    }

    @Override
    public boolean editCellAt(int row, int column, EventObject e) {

        boolean res = false;
        if (fastEdit) {
            if (e instanceof KeyEvent) {
                int keyCode = ((KeyEvent) e).getKeyCode();
                if (keyCode == KeyEvent.VK_CAPS_LOCK ||
                        keyCode == KeyEvent.VK_SCROLL_LOCK ||
                        keyCode == KeyEvent.VK_WINDOWS ||
                        keyCode == KeyEvent.VK_CONTEXT_MENU ||
                        keyCode == KeyEvent.VK_PAUSE ||
                        keyCode == KeyEvent.VK_NUM_LOCK ||
                        ((KeyEvent) e).isActionKey() ||
                        (((KeyEvent) e).getModifiers() == KeyEvent.CTRL_MASK &&
                                (keyCode == KeyEvent.VK_C || keyCode == KeyEvent.VK_V || keyCode == KeyEvent.VK_X || keyCode == KeyEvent.VK_DELETE))
                        )
                    return false;
            }

            if (grid.getSourceRows() != 0 && grid.isEditable()) {
                int selRow = grid.getTableManager().getCurrentRow();
                int selCol = grid.getTableManager()
                        .getCurrentColumn();
                if (selRow != GridTableManager.DEFAULT_ROW
                        && selCol != GridTableManager.DEFAULT_COLUMN) {
                    res = super.editCellAt(row, column, e);
                    if (res) {
                        Component editorComponent = getEditorComponent();
                        if (editorComponent != null) {
                            if (editorComponent instanceof CommonField && ((CommonField) editorComponent).isEditable()) {
                                if (e instanceof KeyEvent) {
                                    ((CommonField) editorComponent).startEditing((KeyEvent) e);
                                } else {
                                    ((CommonField) editorComponent).startEditing();
                                }
                            }
                            editorComponent.requestFocus();
                        }
                        //log.debug("started");
                    }
                }
            }
        } else
            res = super.editCellAt(row, column, e);
        return res;
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        super.setPreferredSize(preferredSize);
        correctHeader(preferredSize);
    }

    @Override
    public void setSize(Dimension d) {
        super.setSize(d);
        correctHeader(d);
    }

    private void correctHeader(Dimension newSize) {
        if (this.getTableHeader().getWidth() <= newSize.getWidth()) {
            Dimension headerSize = new Dimension(newSize.width + 100, getTableHeader().getPreferredSize().height);
            this.getTableHeader().setPreferredSize(headerSize);
        }
    }
}
