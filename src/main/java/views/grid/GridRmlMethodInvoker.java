package views.grid;

import action.api.RTException;
import action.calc.Nil;
import core.rml.RmlConstants;
import core.rml.dbi.Datastore;
import org.apache.log4j.Logger;
import views.grid.listener.GridPopupActionListener;

import javax.swing.*;
import java.util.Vector;

/**
 * @author mmylnikova
 * @since 12/7/12
 */
public class GridRmlMethodInvoker {

    private static final Logger log = Logger.getLogger(GridRmlMethodInvoker.class);

    private GridSwing parentGrid;

    public GridRmlMethodInvoker(GridSwing parentGrid) {
        this.parentGrid = parentGrid;
    }

    public Object method(String method, Object arg) throws Exception {
        if (method.toUpperCase().contains("COLUMN")) {
            return columnMethod(method, arg);
        } else if (method.toUpperCase().contains("ROW")) {
            return rowMethod(method, arg);
        } else {
            //пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅ CurrentValue
            if (method.equalsIgnoreCase("CURRENTVALUE") && (arg instanceof String)) {
                return parentGrid.getCurrentValue((String) arg);
            } else if (method.equalsIgnoreCase("GETVALUE")) {
                return getValue(arg);
            } else if (method.equalsIgnoreCase("SELECTIONVALUES")) {
                //обработка вызова метода CurrentValue
                if ((arg instanceof String) && parentGrid.getTableManager().getSelectionSize() > 0 /*by pavel: && selection.width()>0*/) {
                    Object[] ret = null;
                    GridColumn column = parentGrid.getTableManager().getColumnByAlias(((String) arg).toUpperCase());
                    if (column == null) {
                        return ret;
                    }
                    ret = new Object[parentGrid.getTableManager().getSelectionSize()];
                    log.debug("inside processing SELECTIONVALUES");
                    for (int i = 0; i < parentGrid.getTableManager().getSelectionSize(); i++) {
                        ret[i] = parentGrid.getSourceValueByTarget(parentGrid.getTableManager().getSelectedRow(i), column.getTarget());
                    }
                    return ret;
                }
            } else if (method.equalsIgnoreCase("SELECTIONINDEXES")) {
                //обработка вызова метода CurrentValue
                if ((arg instanceof String) && parentGrid.getTableManager().getSelectionSize() > 0 /*by pavel: && selection.width()>0*/) {
                    GridColumn column = parentGrid.getTableManager().getColumnByAlias(((String) arg).toUpperCase());
                    if (column == null) {
                        return null;
                    }
                    Object[] ret = new Object[parentGrid.getTableManager().getSelectionSize()];
                    log.debug("inside processing SELECTIONINDEXES");
                    for (int i = 0; i < parentGrid.getTableManager().getSelectionSize(); i++) {
                        ret[i] = parentGrid.getTableManager().getSelectedRow(i);
                    }
                    return ret;
                }
            } else if (method.equalsIgnoreCase("RETRIEVE")) {
                return retrieve(arg);
            } else if (method.equalsIgnoreCase("EDITIT")) {
                parentGrid.getTableManager().startEditAtCell(parentGrid.getTableManager().getSelectedRow(), parentGrid.getTableManager().getSelectedColumn());
                return null;
            } else if (method.equalsIgnoreCase("SETDATASTORE")) {
                if (arg instanceof core.rml.dbi.Datastore) {
                    parentGrid.getDatastore().removeHandler();
                    parentGrid.getParentDatastore().removeHandler();

                    parentGrid.setParentDatastore((core.rml.dbi.Datastore) arg);
                    parentGrid.setDatastore((core.rml.dbi.Datastore) arg);
                    parentGrid.retrieve();
                } else {
                    throw new RTException("CASTEXCEPTION", "(grid@SETDATASTORE String)");
                }
            } else if (method.equalsIgnoreCase("GETDATASTORE")) {
                return parentGrid.getDatastore();
            } else if (method.equalsIgnoreCase("GETALLDATASTORE")) {
                return parentGrid.getParentDatastore();
            } else if (method.equalsIgnoreCase("SUM")) {
                if (parentGrid.getDatastore() == null || parentGrid.getDatastore().getRowCount() == 0) {
                    return (double) 0;
                }
                GridColumn column = parentGrid.getTableManager().getColumnByAlias(((String) arg).toUpperCase());
                if (column == null) {
                    return (double) 0;
                }
                if (!(column.getValue() instanceof Double)) {
                    return (double) 0;
                }
                Double sum = 0.0;
                for (int i = 0; i < parentGrid.getTableManager().getSelectionSize(); i++) {
                    sum += (Double) parentGrid.getSourceValueByTarget(parentGrid.getTableManager().getSelectedRow(i), column.getTarget());
                }
                return sum;
            } else if (method.equalsIgnoreCase("DUMPTOFILE")) {
                dumpToFile();
            } else if (method.equalsIgnoreCase("GETMENU")) {
                return parentGrid.getMenu();
            } else if (method.equalsIgnoreCase("SETMENU")) {
                return setMenu(arg);
            } else if (method.equalsIgnoreCase("REPAINT")) {
                parentGrid.getTableManager().repaintAll();
                return new Nil();
            } else if (method.equalsIgnoreCase("INVERTSELECTION")) {
                Vector<Integer> newSelection = new Vector<Integer>();
                for (int i = 0; i < parentGrid.getSourceRows(); i++) {
                    if (!parentGrid.getTableManager().containsSelectedRow(i)) {
                        newSelection.addElement(i);
                    }
                }
                parentGrid.getTableManager().setSelection(newSelection);

            } else if (method.equalsIgnoreCase("FASTSETSELECTION")) {
                if (arg instanceof Double) {
                    Double d = (Double) arg;
                    Integer i = d.intValue();
                    if (!parentGrid.getTableManager().containsSelectedRow(i)) {
                        parentGrid.getTableManager().addSelecteRow(i);
                    }
                } else {
                    throw new RTException("CASTEXCEPTION", "grid@fastsetselection have one number parameter");
                }
            } else if (method.equalsIgnoreCase("SETSELECTION")) {
                if (arg instanceof Double) {
                    Double d = (Double) arg;
                    Integer i = d.intValue();
                    parentGrid.getTableManager().setCurrentRow(i, true, true);
                } else {
                    throw new RTException("CASTEXCEPTION", "grid@setselection have one number parameter");
                }
            } else if (method.equalsIgnoreCase("SELECTALL")) {
                Vector<Integer> newSelection = new Vector<Integer>();
                for (int i = 0; i < parentGrid.getSourceRows(); i++) {
                    newSelection.addElement(i);
                }
                parentGrid.getTableManager().setSelection(newSelection);
            } else if (method.equalsIgnoreCase("SIZE")) {
                return (double) parentGrid.getTableManager().getRowCount();
            } else if (method.equalsIgnoreCase("ALLIGN")) {
                parentGrid.getTableManager().allign();
            } else if (method.equalsIgnoreCase("SETCELLBGCOLOR")) {
                if (arg instanceof Vector) {
                    Integer rowIndex = ((Double) ((Vector) arg).elementAt(0)).intValue();
                    Integer columnIndex = ((Double) ((Vector) arg).elementAt(1)).intValue();
                    String color = (String) ((Vector) arg).elementAt(2);
                    parentGrid.getTableManager().getUIManager().setCellBgColor(rowIndex, columnIndex, color);
                } else {
                    throw new RTException("CASTEXCEPTION", "Bad arguments in Grid@setCellBgColor");
                }
            } else if (method.equalsIgnoreCase("SETCELLFGCOLOR")) {
                if (arg instanceof Vector) {
                    Integer rowIndex = ((Double) ((Vector) arg).elementAt(0)).intValue();
                    Integer columnIndex = ((Double) ((Vector) arg).elementAt(1)).intValue();
                    String color = (String) ((Vector) arg).elementAt(2);
                    parentGrid.getTableManager().getUIManager().setCellFgColor(rowIndex, columnIndex, color);
                } else {
                    throw new RTException("CASTEXCEPTION", "Bad arguments in Grid@setCellFgColor");
                }
            } else if (method.equalsIgnoreCase("SETCELLFONT")) {
                if (arg instanceof Vector) {
                    Integer rowIndex = ((Double) ((Vector) arg).elementAt(0)).intValue();
                    Integer columnIndex = ((Double) ((Vector) arg).elementAt(1)).intValue();
                    String font = (String) ((Vector) arg).elementAt(2);
                    parentGrid.getTableManager().getUIManager().setCellFont(rowIndex, columnIndex, font);
                } else {
                    throw new RTException("CASTEXCEPTION", "Bad arguments in Grid@setCellFont");
                }
            } else if (method.equalsIgnoreCase("SHOWSEARCHDIALOG")) {
                parentGrid.getActionManager().showSearchDialog();
            } else if (method.equalsIgnoreCase("SETVALUE") && (arg instanceof Vector)) {
                setValue(arg);
            } else if (method.equalsIgnoreCase("NOTIFY")) {
                parentGrid.notifyHandler(null);
            } else if (method.equalsIgnoreCase("GETVALUE")) {
                return getValue2(arg);

            } else if (method.equalsIgnoreCase("GETEDITABLE")) {
                return parentGrid.getEditable();
            } else if (method.equalsIgnoreCase("SETEDITABLE")) {
                setEditable(arg);
            } else {
                return parentGrid.invokeSuperMethod(method, arg);
            }
        }
        return new Nil();
    }

    private Object rowMethod(String method, Object arg) throws RTException {
        if (method.equalsIgnoreCase("SETCURRENTROW") && (arg instanceof Double)) {
            return setCurrentRow(arg);
        } else if (method.equalsIgnoreCase("ADDROW")) {
            parentGrid.doAction(parentGrid.getRmlPropertyContainer().getStringProperty(RmlConstants.ADD));
        } else if (method.equalsIgnoreCase("DELROW")) {
            parentGrid.doAction(parentGrid.getRmlPropertyContainer().getStringProperty(RmlConstants.DEL));
        } else if (method.equalsIgnoreCase("SETROWBGCOLOR")) {
            if (arg instanceof Vector) {
                Integer rowIndex = ((Double) ((Vector) arg).elementAt(0)).intValue();
                String color = (String) ((Vector) arg).elementAt(1);
                parentGrid.getTableManager().getUIManager().setRowBgColor(rowIndex, color);
            } else {
                throw new RTException("CASTEXCEPTION", "Bad arguments in Grid@setRowBgColor");
            }
        } else if (method.equalsIgnoreCase("SETROWFGCOLOR")) {
            if (arg instanceof Vector) {
                Integer rowIndex = ((Double) ((Vector) arg).elementAt(0)).intValue();
                String color = (String) ((Vector) arg).elementAt(1);
                parentGrid.getTableManager().getUIManager().setRowFgColor(rowIndex, color);
            } else {
                throw new RTException("CASTEXCEPTION", "Bad arguments in Grid@setRowFgColor");
            }
        } else if (method.equalsIgnoreCase("SETROWTITLEBGCOLOR")) {
            if (arg instanceof Vector) {
                Integer rowIndex = ((Double) ((Vector) arg).elementAt(0)).intValue();
                String color = (String) ((Vector) arg).elementAt(1);
                parentGrid.getTableManager().getUIManager().setRowTitleBgColor(rowIndex, color);
            } else {
                throw new RTException("CASTEXCEPTION", "Bad arguments in Grid@setRowBgColor");
            }
        } else if (method.equalsIgnoreCase("SETROWTITLEFGCOLOR")) {
            if (arg instanceof Vector) {
                Integer rowIndex = ((Double) ((Vector) arg).elementAt(0)).intValue();
                String color = (String) ((Vector) arg).elementAt(1);
                parentGrid.getTableManager().getUIManager().setRowTitleFgColor(rowIndex, color);
            } else {
                throw new RTException("CASTEXCEPTION", "Bad arguments in Grid@setRowFgColor");
            }
        } else if (method.equalsIgnoreCase("SETROWFONT")) {
            if (arg instanceof Vector) {
                Integer rowIndex = ((Double) ((Vector) arg).elementAt(0)).intValue();
                String font = (String) ((Vector) arg).elementAt(1);
                parentGrid.getTableManager().getUIManager().setRowFont(rowIndex, font);
            } else {
                throw new RTException("CASTEXCEPTION", "Bad arguments in Grid@setRowFont");
            }
        } else if (method.equalsIgnoreCase("SETROWTITLE")) {
            setRowTitle(arg);
        } else if (method.equalsIgnoreCase("DELETEROWTITLE")) {
            deleteRowTitle(arg);
        } else if (method.equalsIgnoreCase("GETROWCOUNT")) {
            return getRowCount();
        } else if (method.equalsIgnoreCase("GETCURRENTROW")) {
            return parentGrid.getCurrentRowIndex();
        }

        return new Nil();
    }

    private Object columnMethod(String method, Object arg) throws RTException {
        if (method.equalsIgnoreCase("RETRIEVECOLUMN")) {
            retrieveColumn(arg);
        } else if (method.equalsIgnoreCase("SETCOLUMNBGCOLOR")) {
            setColumnBgColor(arg);
        } else if (method.equalsIgnoreCase("SETCOLUMNFGCOLOR")) {
            setColumnFgColor(arg);
        } else if (method.equalsIgnoreCase("SETCOLUMNTITLEBGCOLOR")) {
            setColumnTitleBgColor(arg);
        } else if (method.equalsIgnoreCase("SETCOLUMNTITLEFGCOLOR")) {
            setColumnTitleFgColor(arg);
        } else if (method.equalsIgnoreCase("SETCOLUMNFONT")) {
            setColumnFont(arg);
        } else if (method.equalsIgnoreCase("SETCOLUMNTITLE")) {
            setColumnTitle(arg);
        } else if (method.equalsIgnoreCase("SETCOLUMNVISIBLE")) {
            setColumnVisible(arg);
        } else if (method.equalsIgnoreCase("ISCOLUMNVISIBLE")) {
            return isColumnVisible(arg);
        } else if (method.equalsIgnoreCase("ADDTYPECOLUMN")) {
            addTypeColumn(arg);
        } else if (method.equalsIgnoreCase("ADDTARGETCOLUMN")) {
            addTargetColumn(arg);
        } else if (method.equalsIgnoreCase("ADDCOMBOCOLUMN")) {
            addComboColumn(arg);
        } else if (method.equalsIgnoreCase("ADDCOMBOTYPECOLUMN")) {
            addComboTypeColumn(arg);
        } else if (method.equalsIgnoreCase("GETCOLUMNTITLE")) {
            return getColumnTitle(arg);
        } else if (method.equalsIgnoreCase("GETCURCOLUMNALIAS")) {
            return getCurrentColumnAlias();
        } else if (method.equalsIgnoreCase("GETCURCOLUMNINDEX")) {
            return getCurrentColumnIndex();
        } else if (method.equalsIgnoreCase("SETCURRENTCOLUMN") && (arg instanceof Double)) {
            setCurrentColumn((Double) arg);
        } else if (method.equalsIgnoreCase("DELETECOLUMN")) {
            deleteColumn(arg);
        } else if (method.equalsIgnoreCase("GETVISIBLECOLUMNCOUNT")) {
            return getVisibleColumnCount();
        } else if (method.equalsIgnoreCase("GETALLCOLUMNCOUNT")) {
            return getAllColumnCount();
        }
        return new Nil();
    }

    private void setColumnTitleFgColor(Object arg) throws RTException {
        if (arg instanceof Vector) {
            Integer columnIndex;
            if (((Vector) arg).elementAt(0) instanceof Double) {
                columnIndex = ((Double) ((Vector) arg).elementAt(0)).intValue();
            } else {
                String columnAlias = ((Vector) arg).elementAt(0).toString();
                columnIndex = parentGrid.getTableManager().getColumnIndexByAlias(columnAlias);
            }
            String color = (String) ((Vector) arg).elementAt(1);
            parentGrid.getTableManager().getUIManager().setColumnTitleFgColor(columnIndex, color);
        } else {
            throw new RTException("CASTEXCEPTION", "Bad arguments in Grid@setColumnTitleFgColor");
        }
    }

    private void setColumnTitleBgColor(Object arg) throws RTException {
        if (arg instanceof Vector) {
            Integer columnIndex;
            if (((Vector) arg).elementAt(0) instanceof Double) {
                columnIndex = ((Double) ((Vector) arg).elementAt(0)).intValue();
            } else {
                String columnAlias = ((Vector) arg).elementAt(0).toString();
                columnIndex = parentGrid.getTableManager().getColumnIndexByAlias(columnAlias);
            }
            String color = (String) ((Vector) arg).elementAt(1);
            parentGrid.getTableManager().getUIManager().setColumnTitleBgColor(columnIndex, color);
        } else {
            throw new RTException("CASTEXCEPTION", "Bad arguments in Grid@setColumnTitleBgColor");
        }
    }

    private void setCurrentColumn(Double arg) {
        int viewColumn = parentGrid.getTableManager().convertColumnIndexToView(arg.intValue());
        parentGrid.getTableManager().setCurrentColumn(viewColumn, true);
    }

    private Object getCurrentColumnIndex() {
        return new Double(parentGrid.getTableManager().getCurrentColumn());
    }


    private Object getCurrentColumnAlias() throws RTException {
        try {
            Object aliasToReturn = new Nil();
            GridColumn column = parentGrid.getTableManager().getColumn(parentGrid.getTableManager().getCurrentColumn());
            if (column != null) {
                if (column.getAlias() != null) {
                    aliasToReturn = column.getAlias();
                } else {
                    aliasToReturn = column.getTarget();
                }
            }
            return aliasToReturn;
        } catch (Exception e) {
            log.error("Bad arguments", e);
            throw new RTException("", "Bad arguments in Grid.getValue");
        }
    }

    public int getRowCount() {
        return parentGrid.getTableManager().getRowCount();
    }

    public int getVisibleColumnCount() {
        return parentGrid.getTableManager().getVColumnCount();
    }

    public int getAllColumnCount() {
        return parentGrid.getTableManager().getAllColumnCount();
    }

    private Object isColumnVisible(Object arg) throws RTException {
        GridColumn column = null;
        if (arg instanceof String) {
            String columnAlias = (String) arg;
            column = parentGrid.getTableManager().getColumnByAlias(columnAlias);
        } else if (arg instanceof Double) {
            Integer columnIndex = ((Double) arg).intValue();
            column = parentGrid.getTableManager().getColumn(columnIndex);
        } else {
            throw new RTException("CASTEXCEPTION", "Bad arguments in Grid@isColumnVisible");
        }
        return parentGrid.getTableManager().isColumnVisible(column);

    }

    private void setColumnVisible(Object arg) throws RTException {
        if (arg instanceof Vector) {
            GridColumn column;
            if (((Vector) arg).elementAt(0) instanceof Double) {
                Integer columnIndex = ((Double) ((Vector) arg).elementAt(0)).intValue();
                column = parentGrid.getTableManager().getMetadataModel().getVColumn(columnIndex);
            } else {
                String columnAlias = ((Vector) arg).elementAt(0).toString();
                column = parentGrid.getTableManager().getColumnByAlias(columnAlias);
                if (column == null) {
                    //it is target, not alias
                    column = parentGrid.getTableManager().getMetadataModel().getTColumn(columnAlias);
                }
                if (column == null) {
                    throw new RTException("COLUMNNOTFOUND", "Can't find column with given alias/target " + columnAlias + " in scope of specified grid");
                }
            }
            boolean isVisible = false;
            if (((Vector) arg).elementAt(1) instanceof String) {
                String visible = ((String) ((Vector) arg).elementAt(1));
                if (visible.equalsIgnoreCase("YES")) {
                    isVisible = true;
                }
            } else if (((Vector) arg).elementAt(1) instanceof Double) {
                int visible = ((Double) ((Vector) arg).elementAt(1)).intValue();
                if (visible == 1) {
                    isVisible = true;
                }
            }
            parentGrid.getTableManager().setColumnVisible(column, isVisible);
        } else {
            throw new RTException("CASTEXCEPTION", "Bad arguments in Grid@setColumnVisible");
        }
    }

    private void setColumnTitle(Object arg) throws RTException {
        if (arg instanceof Vector) {
            GridColumn column;
            if (((Vector) arg).elementAt(0) instanceof Double) {
                Integer columnIndex = ((Double) ((Vector) arg).elementAt(0)).intValue();
                column = parentGrid.getTableManager().getMetadataModel().getVColumn(columnIndex);
            } else {
                String columnAlias = ((Vector) arg).elementAt(0).toString();
                column = parentGrid.getTableManager().getColumnByAlias(columnAlias);
            }
            String title = (String) ((Vector) arg).elementAt(1);
            if (column != null) {
                parentGrid.getTableManager().setColumnTitle(column, title);
            } else {
                log.error("Can't find column with index/alias/target " + ((Vector) arg).elementAt(0));
                throw new RTException("CASTEXCEPTION", "Bad arguments in Grid@setColumnTitle");
            }
        } else {
            throw new RTException("CASTEXCEPTION", "Bad arguments in Grid@setColumnTitle");
        }
    }

    private void setColumnFont(Object arg) throws RTException {
        if (arg instanceof Vector) {
            Integer columnIndex;
            if (((Vector) arg).elementAt(0) instanceof Double) {
                columnIndex = ((Double) ((Vector) arg).elementAt(0)).intValue();
            } else {
                String columnAlias = ((Vector) arg).elementAt(0).toString();
                columnIndex = parentGrid.getTableManager().getColumnIndexByAlias(columnAlias);
            }
            String font = (String) ((Vector) arg).elementAt(1);
            parentGrid.getTableManager().getUIManager().setColumnFont(columnIndex, font);
        } else {
            throw new RTException("CASTEXCEPTION", "Bad arguments in Grid@setColumnFont");
        }
    }

    private void setColumnFgColor(Object arg) throws RTException {
        if (arg instanceof Vector) {
            Integer columnIndex;
            if (((Vector) arg).elementAt(0) instanceof Double) {
                columnIndex = ((Double) ((Vector) arg).elementAt(0)).intValue();
            } else {
                String columnAlias = ((Vector) arg).elementAt(0).toString();
                columnIndex = parentGrid.getTableManager().getColumnIndexByAlias(columnAlias);
            }
            String color = (String) ((Vector) arg).elementAt(1);
            parentGrid.getTableManager().getUIManager().setColumnFgColor(columnIndex, color);
        } else {
            throw new RTException("CASTEXCEPTION", "Bad arguments in Grid@setColumnFgColor");
        }
    }

    private void setColumnBgColor(Object arg) throws RTException {
        if (arg instanceof Vector) {
            Integer columnIndex;
            if (((Vector) arg).elementAt(0) instanceof Double) {
                columnIndex = ((Double) ((Vector) arg).elementAt(0)).intValue();
            } else {
                String columnAlias = ((Vector) arg).elementAt(0).toString();
                columnIndex = parentGrid.getTableManager().getColumnIndexByAlias(columnAlias);
            }
            String color = (String) ((Vector) arg).elementAt(1);
            parentGrid.getTableManager().getUIManager().setColumnBgColor(columnIndex, color);
        } else {
            throw new RTException("CASTEXCEPTION", "Bad arguments in Grid@setColumnBgColor");
        }
    }

    private void deleteRowTitle(Object arg) throws RTException {
        try {
            int rowIndex = ((Double) arg).intValue();
            parentGrid.getTableManager().deleteRowTitle(rowIndex);
        } catch (Exception e) {
            throw new RTException("CASTEXCEPTION", "Bad arguments in Grid@deleteRowTitle");
        }
    }

    private void setRowTitle(Object arg) throws RTException {
        if (arg instanceof Vector) {
            try {
                int rowIndex = ((Double) ((Vector) arg).elementAt(0)).intValue();
                String rowTitle = (String) ((Vector) arg).elementAt(1);
                parentGrid.getTableManager().setRowTitle(rowIndex, rowTitle);
            } catch (Exception e) {
                throw new RTException("CASTEXCEPTION", "Bad arguments in Grid@setRowTitle");
            }
        } else {
            throw new RTException("CASTEXCEPTION", "Bad arguments in Grid@setRowTitle");
        }
    }

    private void setEditable(Object arg) throws RTException {
        try {
            String editable = arg.toString();
            if (editable.toUpperCase().equals(RmlConstants.NO)) {
                parentGrid.getRmlPropertyContainer().put(RmlConstants.EDITABLE, RmlConstants.NO);
                parentGrid.notifyHandler(null);
            } else if (editable.toUpperCase().equals(RmlConstants.YES)) {
                parentGrid.getRmlPropertyContainer().put(RmlConstants.EDITABLE, RmlConstants.YES);
                parentGrid.notifyHandler(null);
            } else {
                log.error("Can't determine editable mode " + editable);
                throw new RTException("", "Bad arguments in Grid.setEditable");
            }
        } catch (Exception e) {
            log.error("Bad arguments", e);
            throw new RTException("", "Bad arguments in Grid.setEditable");
        }
    }

    private Object getColumnTitle(Object arg) throws RTException {
        try {
            String colal = arg.toString();
            GridColumn column = parentGrid.getTableManager().getColumnByAlias(colal);
            if (column != null) {
                return column.getTitle();
            } else {
                log.error("Can't find column with alias " + colal);
                throw new RTException("", "Bad arguments in Grid.getValue");
            }
        } catch (Exception e) {
            log.error("Bad arguments", e);
            throw new RTException("", "Bad arguments in Grid.getValue");
        }
    }

    private void deleteColumn(Object arg) throws RTException {
        try {
            GridColumn column;
            if (arg instanceof Double) {
                int colIndex = ((Double) arg).intValue();
                column = parentGrid.getTableManager().getColumn(colIndex);
            } else {
                String colal = arg.toString();
                column = parentGrid.getTableManager().getColumnByAlias(colal);
            }
            if (column != null) {
                parentGrid.getTableManager().deleteColumn(column);
            } else {
                log.error("Can't find column with alias/index " + arg);
                throw new RTException("", "Bad arguments in Grid.getValue");
            }
        } catch (Exception e) {
            log.error("Bad arguments", e);
            throw new RTException("", "Bad arguments in Grid.getValue");
        }
    }


    private void retrieveColumn(Object arg) throws RTException {
        try {
            String colal = arg.toString();
            GridColumn column = parentGrid.getTableManager().getColumnByAlias(colal);
            if (column != null) {
                column.retrieve();
            } else {
                log.error("Can't find column with alias " + colal);
                throw new RTException("", "Bad arguments in Grid.getValue");
            }
        } catch (Exception e) {
            log.error("Bad arguments", e);
            throw new RTException("", "Bad arguments in Grid.getValue");
        }
    }

    private void addComboTypeColumn(Object arg) throws RTException {
        if (arg instanceof Vector) {
            String columnAlias = (String) ((Vector) arg).elementAt(0);
            String columnType = (String) ((Vector) arg).elementAt(1);
            String columnTitle = (String) ((Vector) arg).elementAt(2);
            Integer columnWidth = ((Double) ((Vector) arg).elementAt(3)).intValue();
            String values = ((String) ((Vector) arg).elementAt(4));
            Datastore ds = null;
            Integer columnPosition = -1;
            String editStyle = "";
            if (((Vector) arg).size() > 5) {
                if (((Vector) arg).elementAt(5) instanceof Datastore) {
                    ds = ((Datastore) ((Vector) arg).elementAt(5));
                } else if (((Vector) arg).elementAt(5) instanceof Double) {
                    columnPosition = ((Double) ((Vector) arg).elementAt(5)).intValue();
                } else if (((Vector) arg).elementAt(5) instanceof String) {
                    editStyle = ((String) ((Vector) arg).elementAt(5));
                }
            }
            if (((Vector) arg).size() > 6) {
                if (((Vector) arg).elementAt(6) instanceof Datastore) {
                    ds = ((Datastore) ((Vector) arg).elementAt(6));
                } else if (((Vector) arg).elementAt(6) instanceof Double) {
                    columnPosition = ((Double) ((Vector) arg).elementAt(6)).intValue();
                }
            }
            if (((Vector) arg).size() > 7) {
                if (((Vector) arg).elementAt(7) instanceof Double) {
                    columnPosition = ((Double) ((Vector) arg).elementAt(7)).intValue();
                }
            }
            parentGrid.getTableManager().addComboTypeColumn(columnAlias, columnType, columnTitle, columnWidth, columnPosition, editStyle, values, ds);
        } else {
            throw new RTException("CASTEXCEPTION", "Bad arguments in Grid@addTargetColumn");
        }
    }

    private void addComboColumn(Object arg) throws RTException {
        if (arg instanceof Vector) {
            String columnAlias = (String) ((Vector) arg).elementAt(0);
            String columnTarget = (String) ((Vector) arg).elementAt(1);
            String columnTitle = (String) ((Vector) arg).elementAt(2);
            Integer columnWidth = ((Double) ((Vector) arg).elementAt(3)).intValue();
            String values = ((String) ((Vector) arg).elementAt(4));
            Datastore ds = null;
            Integer columnPosition = -1;
            String editStyle = "";
            if (((Vector) arg).size() > 5) {
                if (((Vector) arg).elementAt(5) instanceof Datastore) {
                    ds = ((Datastore) ((Vector) arg).elementAt(5));
                } else if (((Vector) arg).elementAt(5) instanceof Double) {
                    columnPosition = ((Double) ((Vector) arg).elementAt(5)).intValue();
                } else if (((Vector) arg).elementAt(5) instanceof String) {
                    editStyle = ((String) ((Vector) arg).elementAt(5));
                }
            }
            if (((Vector) arg).size() > 6) {
                if (((Vector) arg).elementAt(6) instanceof Datastore) {
                    ds = ((Datastore) ((Vector) arg).elementAt(6));
                } else if (((Vector) arg).elementAt(6) instanceof Double) {
                    columnPosition = ((Double) ((Vector) arg).elementAt(6)).intValue();
                }
            }
            if (((Vector) arg).size() > 7) {
                if (((Vector) arg).elementAt(7) instanceof Double) {
                    columnPosition = ((Double) ((Vector) arg).elementAt(7)).intValue();
                }
            }
            if (parentGrid.getDatastore().getModel().getColumnIndex(columnTarget) != -1) {
                parentGrid.getTableManager().addComboColumn(columnAlias, columnTarget, columnTitle, columnWidth, columnPosition, editStyle, values, ds);
            } else {
                throw new RTException("NoTargetException", "There is no " + columnTarget + " target in datastore, issues with Grid@addComboColumn");
            }
        } else {
            throw new RTException("CASTEXCEPTION", "Bad arguments in Grid@addTargetColumn");
        }
    }

    private Object getValue2(Object arg) throws RTException {
        try {
            int rowNum = parentGrid.getTableManager().getCurrentRow();
            String colal = null;
            if ((arg instanceof Vector)) {
                if (((Vector) arg).elementAt(0) instanceof Double) {
                    rowNum = ((Double) ((Vector) arg).elementAt(0)).intValue();
                } else {
                    colal = (String) ((Vector) arg).elementAt(0);
                }
                if (((Vector) arg).size() > 1) {
                    colal = (String) ((Vector) arg).elementAt(1);
                }
            } else if (arg instanceof String) {
                colal = arg.toString();
            }

            GridColumn column = parentGrid.getTableManager().getColumnByAlias(colal);
            if (column != null) {
                if (column.isVisible()) {
                    int colIndex = parentGrid.getTableManager().getTableColumnModelIndex(column);
                    if (colIndex != -1) {
                        return parentGrid.getTableManager().getValueAt(rowNum, colIndex);
                    } else {
                        log.error("Can't find visible column index with alias " + colal + " in the table");
                        throw new RTException("", "Bad arguments in Grid.getValue");
                    }
                } else {
                    log.error("Can't set value for invisible colummn " + colal);
                    throw new RTException("", "Bad arguments in Grid.getValue");
                }
            } else {
                log.error("Can't find column with alias " + colal);
                throw new RTException("", "Bad arguments in Grid.getValue");
            }
        } catch (Exception e) {
            log.error("Bad arguments", e);
            throw new RTException("", "Bad arguments in Grid.getValue");
        }
    }

    private void setValue(Object arg) throws RTException {
        try {
            int rowNum = parentGrid.getTableManager().getCurrentRow();
            String colal = null;
            Object value;
            if (((Vector) arg).elementAt(0) instanceof Double) {
                rowNum = ((Double) ((Vector) arg).elementAt(0)).intValue();
            } else {
                colal = (String) ((Vector) arg).elementAt(0);
            }
            if (((Vector) arg).size() > 2) {
                colal = (String) ((Vector) arg).elementAt(1);
                value = ((Vector) arg).elementAt(2);
            } else {
                value = ((Vector) arg).elementAt(1);
            }

            GridColumn column = parentGrid.getTableManager().getColumnByAlias(colal);
            if (column != null) {
                if (column.isVisible()) {
                    int colIndex = parentGrid.getTableManager().getTableColumnModelIndex(column);
                    if (colIndex != -1) {
                        parentGrid.getTableManager().insertValueAt(rowNum, colIndex, value);
                    } else {
                        log.error("Can't find visible column index with alias " + colal + " in the table");
                        throw new RTException("", "Bad arguments in Grid.setValue");
                    }
                } else {
                    parentGrid.getDatastore().setValue(rowNum, column.getTarget(), value);
//                    log.error("Can't set value for invisible colummn " + colal);
//                    throw new RTException("", "Bad arguments in Grid.setValue");
                }
            } else {
                log.error("Can't find column with alias " + colal);
                throw new RTException("", "Bad arguments in Grid.setValue");
            }
        } catch (Exception e) {
            log.error("Bad arguments", e);
            throw new RTException("", "Bad arguments in Grid.setValue");
        }
    }

    private void addTargetColumn(Object arg) throws RTException {
        if (arg instanceof Vector) {
            String columnAlias = (String) ((Vector) arg).elementAt(0);
            String columnTarget = (String) ((Vector) arg).elementAt(1);
            String columnTitle = (String) ((Vector) arg).elementAt(2);
            Integer columnWidth = ((Double) ((Vector) arg).elementAt(3)).intValue();
            Integer columnPosition = -1;
            String editStyle = "";
            if (((Vector) arg).size() > 4) {
                if (((Vector) arg).elementAt(4) instanceof Double) {
                    columnPosition = ((Double) ((Vector) arg).elementAt(4)).intValue();
                } else if (((Vector) arg).elementAt(4) instanceof String) {
                    editStyle = ((String) ((Vector) arg).elementAt(4));
                }
            }
            if (((Vector) arg).size() > 5) {
                editStyle = ((String) ((Vector) arg).elementAt(5));
            }
            if (parentGrid.getDatastore().getColumn(columnTarget) != -1) {
                parentGrid.getTableManager().addTargetColumn(columnAlias, columnTarget, columnTitle, columnWidth, columnPosition, editStyle);
            } else {
                throw new RTException("NoTargetException", "There is no " + columnTarget + " target in datastore, issues with Grid@addTargetColumn");
            }
        } else {
            throw new RTException("CASTEXCEPTION", "Bad arguments in Grid@addTargetColumn");
        }
    }

    private void addTypeColumn(Object arg) throws RTException {
        if (arg instanceof Vector) {
            String columnAlias = (String) ((Vector) arg).elementAt(0);
            String columnType = (String) ((Vector) arg).elementAt(1);
            String columnTitle = (String) ((Vector) arg).elementAt(2);
            Integer columnWidth = ((Double) ((Vector) arg).elementAt(3)).intValue();
            Integer columnPosition = -1;
            String editStyle = "";
            if (((Vector) arg).size() > 4) {
                if (((Vector) arg).elementAt(4) instanceof Double) {
                    columnPosition = ((Double) ((Vector) arg).elementAt(4)).intValue();
                } else if (((Vector) arg).elementAt(4) instanceof String) {
                    editStyle = ((String) ((Vector) arg).elementAt(4));
                }
            }
            if (((Vector) arg).size() > 5) {
                editStyle = ((String) ((Vector) arg).elementAt(5));
            }
            parentGrid.getTableManager().addTypeColumn(columnAlias, columnType, columnTitle, columnWidth, columnPosition, editStyle);
        } else {
            throw new RTException("CASTEXCEPTION", "Bad arguments in Grid@addTypeColumn");
        }
    }

    private Object setMenu(Object arg) throws RTException {
        try {
            if (parentGrid.getPopupActionListener() == null) {
                parentGrid.setPopupActionListener(new GridPopupActionListener(parentGrid));
            }
            parentGrid.setMenu((views.Menu) arg);
            parentGrid.getMenu().addActionListenerRecursiv(parentGrid.getPopupActionListener());
        } catch (Exception e) {
            log.error("Shit happens", e);
            throw new RTException("RunTime", " Exception " + e.getMessage() + " in method setmenu (svr_grid )");
        }
        return (double) 0;
    }

    private void dumpToFile() {
        JFileChooser d = new JFileChooser();
        d.setDialogTitle("Сохранить в файле");
        d.setApproveButtonText("Сохранить");
        d.setDialogType(JFileChooser.SAVE_DIALOG);
        d.setFileSelectionMode(JFileChooser.FILES_ONLY);
        d.setMultiSelectionEnabled(false);
        int returnVal = d.showSaveDialog(parentGrid.getTablePanel().getJComponent());

        try {
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                parentGrid.dumpToFile(d.getSelectedFile());
            }
        } catch (Exception e) {
            log.error("Shit happens", e);
        }
    }

    private Object setCurrentRow(Object arg) {
        int viewRow = parentGrid.getTableManager().convertRowIndexToView(((Double) arg).intValue());
        parentGrid.getTableManager().setCurrentRow(viewRow, true, true);
        return new Nil();
    }

    private Object retrieve(Object arg) {
        boolean keepFilters = false;
        if (arg != null && arg.toString().equalsIgnoreCase("YES")) {
            keepFilters = true;
        }
        parentGrid.retrieve(keepFilters);
        return new Nil();
    }

    private Object getValue(Object arg) throws RTException {
        try {
            GridColumn column = null;
            String colal = "UNKNOWN ALIAS!";
            int col_number = -1;
            if (((Vector) arg).elementAt(1) instanceof String) {
                colal = (String) ((Vector) arg).elementAt(1);
                column = parentGrid.getTableManager().getColumnByAlias(colal);
            } else if (((Vector) arg).elementAt(1) instanceof Double) {
                col_number = ((Double) ((Vector) arg).elementAt(0)).intValue();
                column = parentGrid.getTableManager().getColumn(col_number);
            }
            int rownum = ((Double) ((Vector) arg).elementAt(0)).intValue();
            if (column != null) {
                String target = column.getTarget();
                return parentGrid.getDatastore().getValue(rownum, target);
            } else {
                if (col_number == -1)
                    log.error("Can't find column with alias " + colal);
                else
                    log.error("Can't find column with alias " + colal + " and column number " + col_number);

                throw new RTException("", "Bad arguments in Grid.setValue");
            }
        } catch (Exception e) {
            log.error("Bad arguments", e);
            throw new RTException("", "Bad arguments in Grid.getValue");
        }
    }
}
