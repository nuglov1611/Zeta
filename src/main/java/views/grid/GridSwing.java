package views.grid;

import action.api.RTException;
import action.api.ScriptApi;
import action.calc.objects.class_type;
import core.document.*;
import core.document.Closeable;
import core.parser.Proper;
import core.reflection.objects.VALIDATOR;
import core.rml.Container;
import core.rml.RmlConstants;
import core.rml.RmlObject;
import core.rml.VisualRmlObject;
import core.rml.dbi.Datastore;
import core.rml.dbi.Handler;
import core.rml.dbi.Packer;
import core.rml.ui.impl.ZPanelImpl;
import core.rml.ui.interfaces.ZComponent;
import core.rml.ui.interfaces.ZPanel;
import core.rml.ui.interfaces.ZScrollPane;
import loader.ZetaProperties;
import org.apache.log4j.Logger;
import publicapi.GridAPI;
import publicapi.RetrieveableAPI;
import views.ColumnTemplate;
import views.FilterStruct;
import views.Menu;
import views.UTIL;
import views.focuser.FocusPosition;
import views.focuser.Focusable;
import views.grid.editor.CommonField;
import views.grid.filter.FilterMenu;
import views.grid.filter.GridRowFilter;
import views.grid.listener.GridPopupActionListener;
import views.grid.listener.GridWindowListener;
import views.grid.manager.CrossModelManager;
import views.grid.manager.GridActionManager;
import views.grid.manager.GridTableManager;
import views.grid.model.GridMetadataModel;
import views.grid.model.cross.parameters.CrossParametersAccessor;
import views.util.RmlPropertyContainer;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Swing Grid realization class. Author: Marina Mylnikova
 */
public class GridSwing extends VisualRmlObject implements GridAPI,
        ObjectNotifyInterface, RetrieveableAPI, class_type, Packer, Closeable,
        Shortcutter, Focusable, Handler {

    private static final Logger log = Logger.getLogger(GridSwing.class);

    Container container = new Container(this);

    public static final String PROPERTY_NAME = "PropertyName";

    public static final String PROPERTY_VALUE = "PropertyValue";
    public static final String OBJECT_PROPERTY = "object";
    public static final String OBJECT_COLUMN = "Column";

    private FocusPosition focusPosition = new FocusPosition();

    private ZPanel tablePanel = ZPanelImpl.create(new GridLayout(1, 0));

    /**
     * Action managers
     */
    private GridActionManager actionManager;

    private GridTableManager tableManager;

    private GridWindowListener gridWindowListener;

    /**
     * Menu and Popup menu components
     */
    private Menu menu = null;

    private FilterMenu headerFilterMenu;

    private ActionListener popupAL;

    /**
     * Data stores
     */
    // фильтр, наложенный на parentds(ds=parentds, если фильтр не наложен)
    private Datastore datastore = null;

    // исходное Datastore

    private Datastore parentDatastore = null;

    // Если true - то Columns будут добавлены после Retrieve'а

    private boolean dynamic = false;

    // данный массив используется для вычисления значений Computed Column'ов

    private Vector<Integer> calcArray;

    // Сохраненное значение из редактируемой ячейки таблицы

    private Object savedFieldValue = "";

    // Флаг, показывающий все ли данные сохранены. При редактировании false,
    // после сохранения в DS становится true

    public boolean toDSSaved = true;

    /**
     * Initialization properties
     */
    /**
     * Contains information about view
     */

    // Document

    /**
     * Cross managers
     */
    private CrossParametersAccessor parametersAccessor;

    private CrossModelManager crossModelManager;

    private RmlPropertyContainer rmlPropertyContainer;

    private boolean containsSets;

    public static final int DEFAULT_HEIGHT = 20;

    private GridRmlMethodInvoker rmlMethodInvoker;

    /**
     * Cоздает последовательность для вычисления Computed Column'ов
     */
    private void createCalcSequence() {
        if (tableManager.isTableModelEmpty()) {
            return;
        }
        Vector<String> names = new Vector<String>();
        Vector<Vector<String>> Bn = new Vector<Vector<String>>();
        for (int i = 0; i < tableManager.getAllColumnCount(); i++) {
            GridColumn column = tableManager.getColumn(i);
            String alias = column.getAlias();
            if (alias != null
                    && column.getStringProperty(RmlConstants.EXP) != null) {// кладем
                // его
                // в
                // вектор
                // names
                names.addElement(alias);
                Vector<String> bi = new Vector<String>();
                ScriptApi cc = ScriptApi.getAPI(column.getCalc());
                String[] als = null;
                try {
                    if (cc != null) {
                        // TODO: сейчас не работает, т.к. пытается анализировать
                        // альясы еще
                        // до того как создались все объекты соответсвенно куча
                        // альясов просто не находится.
                        // als = cc.getAliases();
                    }
                } catch (Exception e) {
                    log.error("Shit happens", e);
                }
                if (als != null) {
                    for (String al : als) {
                        if (al != null
                                && (!al.equals(column.getAlias()))) {
                            bi.addElement(al);
                        }
                    }
                }
                Bn.addElement(bi);
            }
        }
        Vector ret = null;
        if (names.size() > 0) {
            try {
                ret = UTIL.createSequence(names, Bn);
            } catch (Exception e) {
                log.error("Shit happens", e);
                throw new Error(e.getMessage());
            }
        }
        if (ret != null) {
            if (ret.size() > 0) {
                calcArray = new Vector<Integer>(ret.size());
            } else {
                return;
            }

            for (int i = 0; i < ret.size(); i++) {
                String name = (String) ret.elementAt(i);
                int index = tableManager.getColumnIndexByAlias(name);
                if (index != GridTableManager.DEFAULT_COLUMN) {
                    calcArray.add(index);
                }
            }
        }
    }// end of create sequence

    public GridSwing() {
        tableManager = new GridTableManager(this);

        actionManager = new GridActionManager(this);

        gridWindowListener = new GridWindowListener(tableManager);

        parametersAccessor = new CrossParametersAccessor();

        crossModelManager = new CrossModelManager(this);

        headerFilterMenu = new FilterMenu(this);

        rmlMethodInvoker = new GridRmlMethodInvoker(this);

        rmlPropertyContainer = new RmlPropertyContainer();

        rmlPropertyContainer.put(RmlConstants.BUTTONBAR_SIZE, 35);
        rmlPropertyContainer.put(RmlConstants.TITLEBAR_SIZE, 20);
        rmlPropertyContainer.put(RmlConstants.TITLEBAR_HEIGHT, DEFAULT_HEIGHT);
        rmlPropertyContainer.put(RmlConstants.VSCROLLSIZE, 15);
        rmlPropertyContainer.put(RmlConstants.HSCROLLSIZE, 15);
        rmlPropertyContainer.put(RmlConstants.ROWSIZE, DEFAULT_HEIGHT);
        rmlPropertyContainer.put(RmlConstants.BG_COLOR, Color.white);
        rmlPropertyContainer.put(RmlConstants.CURROW_COLOR, Color.blue);
        rmlPropertyContainer.put(RmlConstants.CURROW_BG_COLOR, Color.lightGray);
        rmlPropertyContainer.put(RmlConstants.BUTTONBAR_BG_COLOR,
                Color.lightGray);
        rmlPropertyContainer.put(RmlConstants.TITLEBAR_BG_COLOR,
                Color.lightGray);
        rmlPropertyContainer.put(RmlConstants.MULTISELECT, RmlConstants.NO);
        rmlPropertyContainer.put(RmlConstants.MULTILINE, RmlConstants.NO);
        rmlPropertyContainer.put(RmlConstants.EDITABLE, RmlConstants.NO);

        tablePanel.validate();
    }

    public void init(Proper prop, Document doc) {
        super.init(prop, doc);

        document.addHandler(this);
        document.addWindowRequestor(gridWindowListener);

        rmlPropertyContainer.initProperty(prop, RmlConstants.ALIAS);
        rmlPropertyContainer.initProperty(prop, RmlConstants.DYNAMIC, "NO");

        rmlPropertyContainer
                .initProperty(prop, RmlConstants.BUTTONBAR_SIZE, 35);
        rmlPropertyContainer.initProperty(prop, RmlConstants.TITLEBAR_SIZE, 20);
        rmlPropertyContainer.initProperty(prop, RmlConstants.TITLEBAR_HEIGHT,
                DEFAULT_HEIGHT);
        rmlPropertyContainer.initProperty(prop, RmlConstants.VSCROLLSIZE, 15);
        rmlPropertyContainer.initProperty(prop, RmlConstants.HSCROLLSIZE, 15);
        rmlPropertyContainer.initProperty(prop, RmlConstants.ROWSIZE,
                DEFAULT_HEIGHT);
        rmlPropertyContainer.initProperty(prop,
                RmlConstants.BUTTONBAR_BG_COLOR, Color.class, Color.lightGray);
        rmlPropertyContainer.initProperty(prop, RmlConstants.TITLEBAR_BG_COLOR,
                Color.class, Color.lightGray);
        rmlPropertyContainer.initProperty(prop, RmlConstants.BG_COLOR,
                Color.class, Color.white);
        rmlPropertyContainer.initProperty(prop,
                RmlConstants.BUTTONBAR_FONT_FACE, "Times");
        rmlPropertyContainer.initProperty(prop,
                RmlConstants.BUTTONBAR_FONT_FAMILY, 0);
        rmlPropertyContainer.initProperty(prop,
                RmlConstants.BUTTONBAR_FONT_SIZE, 10);
        rmlPropertyContainer.initProperty(prop,
                RmlConstants.BUTTONBAR_FONT_COLOR, Color.class, Color.black);

        Font bbFont = new Font(
                rmlPropertyContainer
                        .getStringProperty(RmlConstants.BUTTONBAR_FONT_FACE),
                rmlPropertyContainer
                        .getIntProperty(RmlConstants.BUTTONBAR_FONT_FAMILY),
                rmlPropertyContainer
                        .getIntProperty(RmlConstants.BUTTONBAR_FONT_SIZE));
        rmlPropertyContainer.put(RmlConstants.BUTTON_BAR_FONT, bbFont);

        rmlPropertyContainer.initProperty(prop, RmlConstants.CURROW_COLOR,
                Color.class, Color.blue);
        rmlPropertyContainer.initProperty(prop, RmlConstants.CURROW_BG_COLOR,
                Color.class, Color.lightGray);

        Color defaultColor = new Color(rmlPropertyContainer.getColorProperty(
                RmlConstants.TITLEBAR_BG_COLOR).getRGB() | 0xe0);
        rmlPropertyContainer.initProperty(prop, RmlConstants.SELTITLE_BG_COLOR,
                Color.class, defaultColor);

        rmlPropertyContainer.initProperty(prop, RmlConstants.EDITABLE,
                RmlConstants.NO);
        rmlPropertyContainer.initProperty(prop, RmlConstants.MULTISELECT,
                RmlConstants.NO);

        rmlPropertyContainer.initProperty(prop, RmlConstants.EDIT);
        rmlPropertyContainer.initProperty(prop, RmlConstants.ADD);
        rmlPropertyContainer.initProperty(prop, RmlConstants.DEL);
        rmlPropertyContainer.initProperty(prop, RmlConstants.EDITEXP, true);
        rmlPropertyContainer.initProperty(prop, RmlConstants.ADDEXP, true);
        rmlPropertyContainer.initProperty(prop, RmlConstants.DELEXP, true);
        rmlPropertyContainer.initProperty(prop, RmlConstants.EDIT_STYLE,
                RmlConstants.EDIT_STYLE_FAST);
        rmlPropertyContainer.initProperty(prop, RmlConstants.MULTILINE,
                RmlConstants.NO);
        rmlPropertyContainer.initProperty(prop, RmlConstants.AUTO_NEW_ROW,
                RmlConstants.NO);

        rmlPropertyContainer.initProperty(prop, RmlConstants.RETURN,
                RmlConstants.NO);

        String shortcutList = (String) prop.get(RmlConstants.SHORTCUT);
        if (shortcutList != null) {
            try {
                String[] shortcuts = UTIL.parseDep(shortcutList);
                for (String shortcut : shortcuts) {
                    document.addShortcut(shortcut, this);
                }
            } catch (Exception e) {
                log.error("Shit happens", e);
            }
        }

        rmlPropertyContainer.initProperty(prop, RmlConstants.CROSS,
                RmlConstants.NO);
        if (isCross()) {
            rmlPropertyContainer.initProperty(prop, RmlConstants.COLUMNFIELDS);
            rmlPropertyContainer.initProperty(prop, RmlConstants.ROWFIELDS);
            rmlPropertyContainer.initProperty(prop, RmlConstants.DATAFIELDS);
            rmlPropertyContainer.initProperty(prop, RmlConstants.FUNCTION);
            rmlPropertyContainer.initProperty(prop,
                    RmlConstants.DESCRIPTION_REQUIRED);
        }
        final ZComponent table = tableManager.createTable();
        table.setToolTipText(toolTipText);
        // tablePanel.add(tableManager.createTable());
        tablePanel.add(table);
        tableManager.setTableProperties();

        try {
            container.addChildren(prop, doc);
        } catch (Exception e) {
            log.error("!", e);
        }
    }

    private Map<String, String> parseChildProp(Object propertyString) {
        Map<String, String> propMap = new HashMap<String, String>();
        Pattern propPat = Pattern.compile("(.*)=(.*)");
        Matcher matcher = propPat.matcher(propertyString.toString());
        if (matcher.matches()) {
            String propertyName = matcher.group(0);
            String propertyValue = matcher.group(1);
            propMap.put(PROPERTY_NAME, propertyName);
            propMap.put(PROPERTY_VALUE, propertyValue);
        }
        return propMap;
    }

    public void addChild(String objectType, Vector properties) {
        Proper prop = new Proper();
        for (Object property : properties) {
            Map<String, String> propertyMap = parseChildProp(property);
            prop.put(propertyMap.get(PROPERTY_NAME),
                    propertyMap.get(PROPERTY_VALUE));
        }
        if (objectType.equalsIgnoreCase(OBJECT_COLUMN)) {
            GridColumn column = new GridColumn();
            column.init(prop, document);

            addColumn(column, tableManager.getMetadataModel());
        }
    }

    public void initChildren() {
        RmlObject[] children = container.getChildren();

        boolean areColumnsHere = false;
        try {
            GridMetadataModel metadataModel = tableManager.getMetadataModel();
            if (metadataModel == null) {
                metadataModel = new GridMetadataModel();
            }
            for (Object child : children) {
                if (child == null) {
                    log.error("Object GridSwing cannot be created!");
                    throw new Error("Object GridSwing cannot be created!");
                }
                if (child instanceof Datastore) {
                    datastore = (Datastore) child;
                    parentDatastore = datastore;
                } else if (child instanceof GridColumn) {
                    areColumnsHere = true;
                } else if (child instanceof GridColumnSet) {
                    containsSets = true;
                } else if (child instanceof ColumnTemplate) {
                    ColumnTemplate ct = (ColumnTemplate) child;
                    switch (ct.getType()) {
                        case 0:
                            metadataModel.setColumnTemplate(0, ct);
                            break;
                        case 1:
                            metadataModel.setColumnTemplate(1, ct);
                            break;
                        case 2:
                            metadataModel.setColumnTemplate(2, ct);
                            break;
                        default:
                            break;
                    }
                }
            }
            if (areColumnsHere || containsSets || !rmlPropertyContainer.getBooleanProperty(RmlConstants.DYNAMIC)) {
                metadataModel = new GridMetadataModel(metadataModel);
            } else if (rmlPropertyContainer.getBooleanProperty(RmlConstants.DYNAMIC)) {
                // если число Column'ов=0, значит, они будут добавлены после
                // Retrieve'а
                dynamic = true;
            }

            for (Object child : children) {
                if (child instanceof GridColumn) {
                    addColumn((GridColumn) child, metadataModel);
                } else if (child instanceof GridColumnSet) {
                    addColumnSet((GridColumnSet) child, metadataModel);
                } else if (child instanceof Menu) {
                    menu = (Menu) child;
                    if (menu == null) {
                        log.warn("popup menu = null");
                        return;
                    }
                    if (popupAL == null) {
                        popupAL = new GridPopupActionListener(this);
                    }
                    menu.addActionListenerRecursiv(popupAL);
                }
            }

            tableManager.setMetadataModel(metadataModel);
            // createCalcSequence();

        } catch (Exception e) {
            log.error("Shit happens", e);
        }
    }

    private void addColumnSet(GridColumnSet gridColumnSet,
                              GridMetadataModel metadataModel) {
        gridColumnSet.setParent(this);
        metadataModel.addColumnSet(gridColumnSet);
        setColumnSetTargets(gridColumnSet);
    }

    private void setColumnSetTargets(GridColumnSet gridColumnSet) {
        for (GridColumnSet colSet : gridColumnSet.getColumnSets()) {
            setColumnSetTargets(colSet);
        }

        for (GridColumn newColumn : gridColumnSet.getColumns())
            if (newColumn.getTarget() == null) {
                if (newColumn.getType() == Integer.MIN_VALUE) {
                    log.warn("type for computed column not defined!");
                } else {
                    newColumn
                            .setTarget(datastore.addColumn(newColumn.getType()));
                }
            }
    }

    private void addColumn(GridColumn newColumn, GridMetadataModel metadataModel) {
        newColumn.setParent(this);
        if (newColumn.getTarget() == null) {
            if (newColumn.getType() == Integer.MIN_VALUE) {
                log.warn("type for computed column not defined!");
            } else {
                newColumn.setTarget(datastore.addColumn(newColumn.getType()));
            }
        }
        metadataModel.addColumn(newColumn);
    }

    public void initPostRetrieve() {
        try {
            int numColumns = getSourceColumns();
            int numRows = getSourceRows();
            if (numColumns <= 0 || numRows <= 0) {
                log.warn("Attention, no data was returned");
            }
//            if (numColumns == 0) {
//                return;
//            }
            GridMetadataModel metadataModel = tableManager.getMetadataModel();
            if (dynamic) {
                // Store column templates which could be loaded before
                metadataModel = new GridMetadataModel(metadataModel);
                String[] targets = datastore.getNames();
                for (int i = 0; i < numColumns; i++) {
                    tableManager.addDynamicColumn(targets[i], metadataModel);
                }
            }

            if (isCross()) {
                crossModelManager.generateCrossParams(parametersAccessor,
                        metadataModel);
            }

            boolean is_columns_not_null = false;
            for (int i = 0; ((i < metadataModel.getAllColumnCount())); i++) {
                if (metadataModel.getTColumn(i) != null
                        && metadataModel.getTColumn(i).getTarget() != null) {
                    metadataModel.getTColumn(i).setType(
                            datastore.getType(metadataModel.getTColumn(i)
                                    .getTarget()));
                }
                is_columns_not_null = true;
            }
            tableManager.setMetadataModel(metadataModel);
            tableManager.initTableModel();
            if (is_columns_not_null) {
                if (calcArray == null) {
                    createCalcSequence();
                }
                if (calcArray != null) {
                    for (int j = 0; j < numRows; j++) {
                        datastore.setCurrentRow(j);
                        for (Integer aCalcArray : calcArray) {
                            metadataModel.getTColumn(aCalcArray).calc();
                        }
                    }
                }

                /*---------------------------------------------*/
                if (tableManager.getSelectionSize() > 0) {
                    tableManager.clearSelection();
                }

                // Handler необходим для своевременного извещения об изменении
                // данных со стороны датасторе
                datastore.addHandler(this);
                datastore.addPacker(this);
            }
        } catch (Exception e) {
            log.error("Shit happens", e);
        }
    }

    public int getSourceRows() {
        if (datastore != null) {
            return datastore.getRowCount();
        } else {
            return 0;
        }
    }

    public int getSourceColumns() {
        if (dynamic && datastore != null) {
            return datastore.getCountColumns();
        }
        return tableManager.getColumnsVisibleSize();
    }

    public Object getSourceValueByTarget(int row, String columnTarget) {
        return datastore.getValue(row, columnTarget);
    }

    public Object getSourceValue(int row, int col) {
        GridColumn column = tableManager.getVColumn(col);
        if (column != null) {
            String colTarget = column.getTarget();
            if (datastore.getModel().getColumnIndex(colTarget) != -1) {
                return datastore.getValue(row, colTarget);
            } else if (column.getDatastore() != null) {
                return column.getDatastore().getValue(
                        column.getDatastore().getCurRow(), colTarget);
            }
        }
        return null;
    }

    public String getSourceText(int row, int col) {
        Object value = datastore.getValue(row, tableManager.getVColumn(col)
                .getTarget());
        try {
            if (value != null) {
                return tableManager.getVColumn(col).valueToString(value);
            } else {
                return "";
            }
        } catch (Exception e) {
            log.error("Shit happens", e);
            return "";
        }
    }

    public GridTableManager getTableManager() {
        return tableManager;
    }

    public GridActionManager getActionManager() {
        return actionManager;
    }

    public boolean isMultiSelection() {
        return rmlPropertyContainer
                .getBooleanProperty(RmlConstants.MULTISELECT);
    }

    public String getEditable() {
        return rmlPropertyContainer.getStringProperty(RmlConstants.EDITABLE);
    }

    public boolean isEditable() {
        return rmlPropertyContainer.getBooleanProperty(RmlConstants.EDITABLE);
    }

    public boolean isCross() {
        return rmlPropertyContainer.getBooleanProperty(RmlConstants.CROSS);
    }

    public void setSavedFieldValue(Object aValue) {
        savedFieldValue = aValue;
    }

    public Object getSavedFieldValue() {
        return savedFieldValue;
    }

    public static int getJType(int sqltype) {
        switch (sqltype) {
            case Types.BIGINT:
            case Types.DECIMAL:
            case Types.DOUBLE:
            case Types.FLOAT:
            case Types.INTEGER:
            case Types.NUMERIC:
            case Types.REAL:
            case Types.SMALLINT:
            case Types.TINYINT: {
                return VALIDATOR.NUMERIC_TYPE;
            }
            case Types.CHAR:
            case Types.VARCHAR: {
                return VALIDATOR.STRING_TYPE;
            }
            case Types.TIME:
            case Types.TIMESTAMP:
            case Types.DATE: {
                return VALIDATOR.DATE_TYPE;
            }
            case Types.OTHER: {
                return VALIDATOR.UNKNOWN_TYPE;
            }
            case Types.BOOLEAN: {
                return VALIDATOR.BOOLEAN_TYPE;
            }
            default: {
                log.warn("unknown type<" + sqltype + ">");
                return VALIDATOR.UNKNOWN_TYPE;
            }
        }
    }

    public void processAddExp(core.rml.dbi.Datastore dstore) throws Exception {
        if (rmlPropertyContainer.get(RmlConstants.PARSEADDEXP) == null) {
            return;
        }
        if (dstore == null) {
            return;
        }
        int crows = dstore.getRowCount();
        String[] parseAdd = (String[]) rmlPropertyContainer
                .get(RmlConstants.PARSEADDEXP);
        datastore.removeHandler();

        for (int i = 0; i < crows; i++) {
            dstore.setCurrentRow(i);
            int index = datastore.newRow();
            datastore.setCurrentRow(index);
            for (String aParseAdd : parseAdd) {
                // String col1 = parseAdd[j][0];//имя столбца в DATASTORE грида
                // String exp = parseAdd[j][1];
                // Object val = Calc.macro(exp,aliases);
                try {
                    if (aParseAdd != null) {
                        document.executeScript(aParseAdd, true);
                    }
                } catch (Exception e) {
                    log.error("Shit happens", e);
                }
                // setDSValue(index, col1, val);
            }
        }

        datastore.addHandler(this);
    }

    public void dumpToFile(File file) throws Exception {
        if (!tableManager.isTableModelEmpty()) {
            OutputStream os = new BufferedOutputStream(new FileOutputStream(
                    file), 2048);
            int rows = getSourceRows();
            os.write((int) '-');
            os.write((int) '\n');
            for (int j = 0; j < tableManager.getVColumnCount(); j++) {
                String text = tableManager.getVColumn(j).getTitle();
                os.write((text + "\t").getBytes());
            }
            os.write((int) '\n');
            os.write((int) '-');
            os.write((int) '\n');
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < tableManager.getVColumnCount(); j++) {
                    Object text = getSourceValue(i, j);
                    os.write((text + "\t").getBytes());
                }
                os.write((int) '\n');
            }
            os.close();
        }
    }

    public void doAction(String action) {
        if (action != null) {
            try {
                document.doAction(action, this);
            } catch (Exception ex) {
                log.error("Shit happens", ex);
            }
        }
    }

    public void showPopup(JComponent source, int x, int y) {
        if (menu != null) {
            menu.show(source, x, y);
        }
    }

    public core.rml.dbi.Datastore returnSelection() {
        if (datastore == null || datastore.getRowCount() == 0) {
            return null;
        }
        if (!isMultiSelection()) {
            Vector<Integer> filteredIndexes = new Vector<Integer>();
            filteredIndexes.add(tableManager.getSelectedRow(0));
            return datastore.getFilterManager().createFilter(datastore,
                    filteredIndexes);
        }
        // if (selection == null) {
        // return null;
        // }
        // if (selection.size() == 0) {
        // return null;
        // }
        //
        // int[] keys = new int[selection.size()];
        // for (int i = 0; i < keys.length; i++) {
        // keys[i] = selection.elementAt(i) - 1;
        // }
        if (tableManager.getSelectionSize() == 0) {
            return null;
        }
        Vector<Integer> filteredIndexes = new Vector<Integer>();
        filteredIndexes.addAll(tableManager.getSelection());
        return datastore.getFilterManager().createFilter(datastore,
                filteredIndexes);
    }

    // вызывается для грида-справочника, когда тот закрывается

    public void closeNotify() {
        core.rml.dbi.Datastore ds2 = null;
        if (rmlPropertyContainer.getBooleanProperty(RmlConstants.RETURN)) {
            ds2 = returnSelection();
        }
        tableManager.clearSelection();
        if (ds2 != null) {
            document.getAliases().put(AliasesKeys.RETURNSTORE, ds2);
            if (ZetaProperties.views_debug > 0) {
                log.debug("!row count in returned datastore is "
                        + ds2.getRowCount());
            }
        }
    }

    public void notifyHandler(Object o) {
        notifyHandler(o, false, false);
    }

    public void notifyHandler(Object o, boolean requestFocus,
                              boolean keepSorting) {
        ArrayList<RowSorter.SortKey> sortKeys = null;
        if (keepSorting) {
            sortKeys = new ArrayList<RowSorter.SortKey>();
            ArrayList<RowSorter.SortKey> currKeys = tableManager.getSortKeys();
            sortKeys.addAll(currKeys);
        }
        tableManager.repaintAll();
        int numRows = getSourceRows();
        int numColumns = getSourceColumns();
        // Ставим выделение на последнюю строку, если удалилась последняя
        if (numRows <= tableManager.getCurrentRow()) {
            tableManager.setCurrentRow(numRows - 1, true, true);
        }
        if (numColumns <= tableManager.getCurrentColumn()) {
            tableManager.setCurrentColumn(numColumns);
        }
        if (tableManager.getCurrentRow() != datastore.getCurRow()) {
            tableManager.setCurrentRow(datastore.getCurRow(), true, true);
        }
        tableManager.restoreSelection();
        if (keepSorting && !sortKeys.isEmpty()) {
            tableManager.getRowSorter().setSortKeys(sortKeys);
            tableManager.getRowSorter().sort();
        }
        if (requestFocus) {
            requestFocusThis();
        }
    }

    // Вызывается при завершении ACTION

    public void notifyIt() {
        notifyIt(null);
    }

    public void notifyIt(Object source) {
        // if (ZetaUtility.views_debug>0)
        // если editMode=true, значит notifyIt вызван филдом, в который
        // занесли значение из справочника -> нужно пересчитать значение
        // зависимых столбцов
        if (tableManager.isEditing()
                || (source != null && source instanceof CommonField)) {
            datastore.removeHandler();
            tableManager.stopEditing();

            GridColumn currColumn = tableManager.getVColumn(tableManager
                    .getSelectedColumn());

            // установка значения в столбце из справочника
            if (currColumn != null && currColumn.getEditExp() != null) {
                try {
                    document.executeScript(currColumn.getEditExp(), true);
                    currColumn.calcHandbookDep();
                    // Object val = Calc.macro(currColumn.getEditExp(),
                    // aliases);
                    // setDSValue(ds.getCurRow() - 1, currColumn.getTarget(),
                    // val);
                } catch (Exception e) {
                    log.error("Shit happens", e);
                }
            }
            // пересчет зависимых столбцов
            // String[] deps =
            // tableModel.getVColumn(table.getSelectedColumn()).getDep();
            // if (deps != null) {
            // for (String dep : deps) {
            // int colIndex = getColumnNumByAlias(dep);
            // if (colIndex != DEFAULT_COLUMN) {
            // if (tableModel.getColumn(colIndex).getEditExp() != null) {
            // try {
            // Calc c = new Calc(tableModel.getColumn(colIndex).getEditExp());
            // c.eval(aliases);
            // Object val =
            // Calc.macro(tableModel.getColumn(colIndex).getEditExp(), aliases);
            // setDSValue(currentModelRow,
            // tableModel.getColumn(colIndex).getTarget(), val);
            // } catch (Exception e) {
            // log.error("Shit happens", e);
            // }
            // }
            // }
            // }
            // }
            // Удаляем STORE, т.к. больше не нужно его хранить
            document.getAliases().remove(AliasesKeys.STORE);

            datastore.addHandler(this);
            notifyHandler(null);
        } else if (rmlPropertyContainer.get(RmlConstants.ADDEXP) != null) {
            core.rml.dbi.Datastore ds2 = (core.rml.dbi.Datastore) document
                    .findObject(AliasesKeys.STORE);
            if (ds2 != null) {
                try {
                    processAddExp(ds2);
                    tableManager.setCurrentRow(datastore.getCurRow(), true,
                            true);
                    notifyHandler(null);
                } catch (Exception e) {
                    log.error("Shit happens", e);
                }
                // Удаляем STORE, т.к. больше не нужно его хранит
                document.getAliases().remove(AliasesKeys.STORE);
            }
            requestFocusThis();
        }
    }

    public void requestFocusThis() {
        tableManager.requestFocusThis();
    }

    public boolean isDsEmpty() {
        return datastore == null;
    }

    public void saveDsCurRow(Integer nextRow) {
        datastore.setCurrentRow(nextRow);
    }

    // Методы интерфейса GlobalValuesObject

    public void setValue(Object o) {
    }

    public void setValueByName(String name, Object o) {
    }

    public Object getValue() {
        return this;
    }

    public Object getValueByName(String name) {
        return null;
    }

    // Методы интерфейса class_type

    public String type() {
        return "VIEWS_GRID";
    }

    public Object getCurrentValue(String columnName) throws RTException {
        Object value = null;
        GridColumn col = tableManager.getColumnByAlias(columnName);
        log.debug("inside processing CURRENTVALUE");
        if (col != null) {
            value = col.getValue();
        }
        return value;
    }

    /**
     * RetrieveableAPI implementaion
     */
    public int retrieve() {
        return retrieve(false);
    }

    public int retrieve(boolean keepFilters) {
        RowFilter rowFilter = null;
        int ret = 0;
        if (keepFilters) {
            TableRowSorter rowSorter = tableManager.getRowSorter();
            rowFilter = new GridRowFilter(rowSorter.getRowFilter());
        }
        try {
            if (datastore != null) {
                ret = datastore.retrieve();
                initPostRetrieve();
                tableManager.setCurrentRow(datastore.getCurRow(), true, true);
                if (getSourceColumns() != 0) {
                    tableManager.setCurrentColumn(0, true);
                }
            }
        } catch (Exception e) {
            log.error("Shit happens", e);
        }
        if (tableManager.isTableModelEmpty()) {
            log.warn("After retrieve columns are null!");
            return 0;
        }

        for (int i = 0; i < tableManager.getAllColumnCount(); i++) {
            if (tableManager.getColumn(i) != null) {
                tableManager.getColumn(i).retrieve();
            }
        }
        if (keepFilters) {
            tableManager.setRowSorter(tableManager.getTableModel(), rowFilter);
        }
        return ret;
    }

    public void update() {
        log.debug("update field");
        try {
            if (datastore != null) {
                datastore.update();
            }
        } catch (Exception e) {
            log.error("Shit happens", e);
        }
    }

    public void fromDS() {
        if (datastore != null) {
            initPostRetrieve();
        } else {
            log.error("GridSwing cannot be created.DS = null!");
            throw new Error("Grid cannot be created.DS = null!");

        }
        if (tableManager.isTableModelEmpty()) {
            log.error("After post_ret_init() columns are null!");
            return;
        }

        for (int i = 0; i < tableManager.getAllColumnCount(); i++) {
            if (tableManager.getColumn(i) != null) {
                tableManager.getColumn(i).retrieve();
            }
        }
    }

    public void toDS() {
        if (!toDSSaved
                && tableManager.getCurrentColumn() != GridTableManager.DEFAULT_COLUMN) {
            try {
                boolean needRefresh = false;
                int viewColumnIndex = tableManager
                        .convertColumnIndexToView(tableManager
                                .getCurrentColumn());
                GridColumn column = tableManager.getVColumn(viewColumnIndex);
                datastore.setValue(column.getTarget(), getSavedFieldValue());
                column.setValue(getSavedFieldValue());
                if (column.getEditExp() != null) {
                    column.calcHandbookExp();
                    needRefresh = true;
                }
                if (column.getDep() != null) {
                    column.calcHandbookDep();
                    needRefresh = true;
                }
                toDSSaved = true;
                if (needRefresh) {
                    notifyHandler(null);
                }
            } catch (Exception e) {
                log.error("Shit happens", e);
            }
        }
    }

    public int createDsRow() {
        return datastore.newRow();
    }

    public Integer getDsCurRow() {
        return datastore.getCurRow();
    }

    public void setDSValue(int rowIndex, String columnTarget, Object value) {
        datastore.setValue(rowIndex, columnTarget, value);
    }

    public void setDSValue(int row, int column, Object value) throws Exception {

        int type = datastore.getType(column);
        switch (type) {
            case Types.BIGINT:
            case Types.DECIMAL:
            case Types.DOUBLE:
            case Types.FLOAT:
            case Types.INTEGER:
            case Types.NUMERIC:
            case Types.REAL:
            case Types.SMALLINT:
            case Types.TINYINT: {
                if (ZetaProperties.views_debug > 0) {
                    log.debug("number type");
                }
                Double d;
                if (!(value instanceof Double)) {
                    // проверка, если тип - строка
                    if (value instanceof String) {
                        // пробуем конвертить в Double, если тип не Double - ловим
                        // исключение
                        d = Double.parseDouble(String.valueOf(value));
                        datastore.setValue(row, column, value);
                        tableManager.setValueAt(value, row, column);
                    } else {
                        throw new Exception(
                                "incompatible types in different datastores!");
                    }
                } else {
                    datastore.setValue(row, column, value);
                    tableManager.setValueAt(value, row, column);
                }
                break;
            }
            case Types.CHAR:
            case Types.VARCHAR: {
                if (ZetaProperties.views_debug > 0) {
                    log.debug("string type");
                }
                if (!(value instanceof String)) {
                    throw new Exception(
                            "incompatible types in different datastores!");
                } else {
                    datastore.setValue(row, column, value);
                    tableManager.setValueAt(value, row, column);
                }
                break;
            }
            case Types.TIME:
            case Types.TIMESTAMP:
            case Types.DATE: {
                break;
            }
            case Types.OTHER: {
                log.warn("Unknown type!");
                return;
            }
            default: {
                log.warn("UNKNOWN TYPE!!!");
            }
        }
    }

    public void processShortcut() {
        requestFocusThis();
        if (tableManager.getCurrentRow() != GridTableManager.DEFAULT_ROW
                && tableManager.getCurrentColumn() != GridTableManager.DEFAULT_COLUMN) {
            tableManager.startEditAtCell(tableManager
                            .convertRowIndexToView(tableManager.getCurrentRow()),
                    tableManager.convertColumnIndexToView(tableManager
                            .getCurrentColumn()));
        }
    }

    public InputStream pack(Object data) {
        ByteArrayInputStream bis = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(data);
            oos.close();
            bis = new ByteArrayInputStream(bos.toByteArray());
            log.debug("Object's packed width = " + bos.size());
        } catch (Exception e) {
            log.error("Shit happens", e);
        }
        return bis;
    }

    public Object unpack(InputStream is) {
        FilterStruct[] ret = null;
        if (is == null) {
            return null;
        }
        try {
            ObjectInputStream ois = new ObjectInputStream(is);
            ret = (FilterStruct[]) ois.readObject();
        } catch (Exception e) {
            log.error("Shit happens", e);
        }
        return ret;
    }

    public void focusThis() {
        requestFocusThis();
    }

    // javadoc inherited

    public int getFocusPosition() {
        return focusPosition.getFocusPosition();
    }

    // javadoc inherited

    public void setFocusPosition(int position) {
        focusPosition.setFocusPosition(position);
    }

    public void showFilterMenu(Point popupPoint, boolean showMenu) {
        if (!isCross()) {
            if (showMenu) {
                int columnIndex = tableManager.getColumnAtPoint(popupPoint);
                if (columnIndex != -1) {
                    ZComponent tableContainer = tableManager
                            .getTableContainer();
                    if (tableContainer instanceof ZScrollPane) {
                        int rowWidth = ((ZScrollPane) tableContainer)
                                .getRowHeader().getView().getWidth();
                        if (rowWidth > 0) {
                            popupPoint.setLocation(
                                    popupPoint.getX() + rowWidth,
                                    popupPoint.getY());
                        }
                    }
                    headerFilterMenu.createFilterModel(columnIndex);
                    headerFilterMenu.show(tablePanel.getJComponent(),
                            popupPoint.x, popupPoint.y);
                    MenuElement[] selectedPath = MenuSelectionManager
                            .defaultManager().getSelectedPath();
                    if (selectedPath.length < 2) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                MenuElement[] subElements = headerFilterMenu
                                        .getSubElements();
                                MenuElement[] menuElements;
                                if (subElements.length > 0) {
                                    menuElements = new MenuElement[2];
                                    menuElements[0] = headerFilterMenu;
                                    int selectionIndex = 0;
                                    menuElements[1] = subElements[selectionIndex];
                                } else {
                                    menuElements = new MenuElement[1];
                                    menuElements[0] = headerFilterMenu;
                                }
                                MenuSelectionManager.defaultManager()
                                        .setSelectedPath(menuElements);
                            }
                        });
                    }
                }
            } else {
                headerFilterMenu.setVisible(false);
            }
        }
    }

    public void deleteDsCurRow(Integer rowIndex) {
        datastore.delRow(rowIndex);
    }

    public int convertColumnIndexToModel(int columnIndex) {
        return tableManager.convertColumnIndexToModel(columnIndex);
    }

    public GridColumn getVColumn(int columnIndex) {
        return tableManager.getVColumn(columnIndex);
    }

    public boolean isEditing() {
        return tableManager.isEditing();
    }

    public void stopEditing() {
        tableManager.stopEditing();
    }

    public String getAlias() {
        return rmlPropertyContainer.getStringProperty(RmlConstants.ALIAS);
    }

    public CrossParametersAccessor getParametersAccessor() {
        return parametersAccessor;
    }

    public Datastore getDatastore() {
        return datastore;
    }

    public Datastore getParentDatastore() {
        return parentDatastore;
    }

    public CrossModelManager getCrossModelManager() {
        return crossModelManager;
    }

    /**
     * Методы интерфейса class_method
     */
    public Object method(String method, Object arg) throws Exception {
        return rmlMethodInvoker.method(method, arg);
    }

    public boolean isContainsSets() {
        // return false;
        return containsSets;
    }

    public void setToDSSaved(boolean toDSSaved) {
        this.toDSSaved = toDSSaved;
    }

    public Boolean getBooleanProperty(String propName) {
        return rmlPropertyContainer.getBooleanProperty(propName);
    }

    public String getStringProperty(String propName) {
        return rmlPropertyContainer.getStringProperty(propName);
    }

    public Integer getIntProperty(String propName) {
        return rmlPropertyContainer.getIntProperty(propName);
    }

    public Color getColorProperty(String propName) {
        return rmlPropertyContainer.getColorProperty(propName);
    }

    public Font getFontProperty(String propName) {
        return rmlPropertyContainer.getFontProperty(propName);
    }

    @Override
    public ZComponent getVisualComponent() {
        return tablePanel;
    }

    @Override
    public void setFocusable(boolean focusable) {
        tablePanel.setFocusable(focusable);
    }

    @Override
    public void addChild(RmlObject child) {
        container.addChildToCollection(child);
    }

    @Override
    public RmlObject[] getChildren() {
        return container.getChildren();
    }

    @Override
    public Container getContainer() {
        return container;
    }

    public Document getDoc() {
        return document;
    }

    @Override
    public boolean addChildrenAutomaticly() {
        return false;
    }

    @Override
    protected Border getDefaultBorder() {
        return new EmptyBorder(0, 0, 0, 0);
    }

    public Object invokeSuperMethod(String method, Object arg) throws Exception {
        return super.method(method, arg);
    }

    public void setParentDatastore(Datastore parentDatastore) {
        this.parentDatastore = parentDatastore;
    }

    public void setDatastore(Datastore datastore) {
        this.datastore = datastore;
    }

    public RmlPropertyContainer getRmlPropertyContainer() {
        return rmlPropertyContainer;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public ActionListener getPopupActionListener() {
        return popupAL;
    }

    public void setPopupActionListener(ActionListener popupActionListener) {
        this.popupAL = popupActionListener;
    }

    public ZPanel getTablePanel() {
        return tablePanel;
    }

    @Override
    public void retrieveColumn(String col) throws RTException {
        GridColumn column = getTableManager().getColumnByAlias(col);
        if (column != null) {
            column.retrieve();
        } else {
            log.error("Can't find column with alias " + col);
            throw new RTException("", "Bad arguments in Grid.getValue");
        }
    }

    @Override
    public void setColumnBgColor(int col, String color) {
        getTableManager().getUIManager().setColumnBgColor(col, color);
    }

    @Override
    public void setColumnBgColor(String col, String color) {
        int columnIndex = getTableManager().getColumnIndexByAlias(col);
        setColumnBgColor(columnIndex, color);
    }

    @Override
    public void setColumnFgColor(int col, String color) {
        getTableManager().getUIManager().setColumnFgColor(col, color);
    }

    @Override
    public void setColumnFgColor(String col, String color) {
        int columnIndex = getTableManager().getColumnIndexByAlias(col);
        setColumnFgColor(columnIndex, color);
    }

    @Override
    public void setColumnTitleBgColor(int col, String color) {
        getTableManager().getUIManager().setColumnTitleBgColor(col, color);
    }

    @Override
    public void setColumnTitleBgColor(String col, String color) {
        int columnIndex = getTableManager().getColumnIndexByAlias(col);
        setColumnTitleBgColor(columnIndex, color);
    }

    @Override
    public void setColumnTitleFgColor(int col, String color) {
        getTableManager().getUIManager().setColumnTitleFgColor(col, color);
    }

    @Override
    public void setColumnTitleFgColor(String col, String color) {
        int columnIndex = getTableManager().getColumnIndexByAlias(col);
        setColumnTitleFgColor(columnIndex, color);
    }

    @Override
    public void setColumnFont(int col, String font) {
        getTableManager().getUIManager().setColumnFont(col, font);
    }

    @Override
    public void setColumnFont(String col, String font) {
        int columnIndex = getTableManager().getColumnIndexByAlias(col);
        getTableManager().getUIManager().setColumnFont(columnIndex, font);
    }

    @Override
    public void setColumnTitle(int col, String title) {
        GridColumn column = getTableManager().getColumn(col);
        getTableManager().setColumnTitle(column, title);
    }

    @Override
    public void setColumnTitle(String col, String title) {
        GridColumn column = getTableManager().getColumnByAlias(col);
        getTableManager().setColumnTitle(column, title);
    }

    @Override
    public void setColumnVisible(int col, boolean visible) {
        GridColumn column = getTableManager().getColumn(col);
        getTableManager().setColumnVisible(column, visible);
    }

    @Override
    public void setColumnVisible(String col, boolean visible) {
        GridColumn column = getTableManager().getColumnByAlias(col);
        getTableManager().setColumnVisible(column, visible);
    }

    @Override
    public boolean isColumnVisible(int col) {
        GridColumn column = getTableManager().getColumn(col);
        return (Boolean) getTableManager().isColumnVisible(column);
    }

    @Override
    public boolean isColumnVisible(String col) {
        GridColumn column = getTableManager().getColumnByAlias(col);
        return (Boolean) getTableManager().isColumnVisible(column);
    }

    @Override
    public void addTypeColumn(Map<String, Object> params) {
        final String columnAlias = (String) params.get(RmlConstants.ALIAS);
        final String columnType = (String) params.get(RmlConstants.TYPE);
        final String columnTitle = (String) params.get(RmlConstants.TITLE);
        final Integer columnWidth = (Integer) params.get(RmlConstants.WIDTH);
        final Integer columnPosition = (Integer) params
                .get(RmlConstants.POSITION);
        final String editStyle = (String) params.get(RmlConstants.EDITABLE);
        getTableManager().addTypeColumn(columnAlias, columnType, columnTitle,
                columnWidth, columnPosition, editStyle);
    }

    @Override
    public void addTargetColumn(Map<String, Object> params) {
        final String columnAlias = (String) params.get(RmlConstants.ALIAS);
        final String columnTarget = (String) params.get(RmlConstants.TARGET);
        final String columnTitle = (String) params.get(RmlConstants.TITLE);
        final Integer columnWidth = (Integer) params.get(RmlConstants.WIDTH);
        final Integer columnPosition = (Integer) params
                .get(RmlConstants.POSITION);
        final String editStyle = (String) params.get(RmlConstants.EDITABLE);
        getTableManager().addTargetColumn(columnAlias, columnTarget,
                columnTitle, columnWidth, columnPosition, editStyle);
    }

    @Override
    public void addComboColumn(Map<String, Object> params) {
        final String columnAlias = (String) params.get(RmlConstants.ALIAS);
        final String columnTarget = (String) params.get(RmlConstants.TARGET);
        final String columnTitle = (String) params.get(RmlConstants.TITLE);
        final Integer columnWidth = (Integer) params.get(RmlConstants.WIDTH);
        final Integer columnPosition = (Integer) params
                .get(RmlConstants.POSITION);
        final String editStyle = (String) params.get(RmlConstants.EDITABLE);
        final String values = (String) params.get(RmlConstants.VALUES);
        final Datastore ds = (Datastore) params.get(RmlConstants.DS);
        getTableManager()
                .addComboColumn(columnAlias, columnTarget, columnTitle,
                        columnWidth, columnPosition, editStyle, values, ds);
    }

    @Override
    public void addComboTypeColumn(Map<String, Object> params) {
        final String columnAlias = (String) params.get(RmlConstants.ALIAS);
        final String columnType = (String) params.get(RmlConstants.TYPE);
        final String columnTitle = (String) params.get(RmlConstants.TITLE);
        final Integer columnWidth = (Integer) params.get(RmlConstants.WIDTH);
        final Integer columnPosition = (Integer) params
                .get(RmlConstants.POSITION);
        final String editStyle = (String) params.get(RmlConstants.EDITABLE);
        final String values = (String) params.get(RmlConstants.VALUES);
        final Datastore ds = (Datastore) params.get(RmlConstants.DS);
        getTableManager()
                .addComboTypeColumn(columnAlias, columnType, columnTitle,
                        columnWidth, columnPosition, editStyle, values, ds);
    }

    @Override
    public String getColumnTitle(int col) {
        GridColumn column = getTableManager().getColumn(col);
        if (column != null) {
            return column.getTitle();
        } else {
            return null;
        }
    }

    @Override
    public String getColumnTitle(String col) {
        GridColumn column = getTableManager().getColumnByAlias(col);
        if (column != null) {
            return column.getTitle();
        } else {
            return null;
        }
    }

    @Override
    public String getCurrentColumnAlias() {
        String aliasToReturn = null;
        GridColumn column = tableManager.getColumn(tableManager
                .getCurrentColumn());
        if (column != null) {
            if (column.getAlias() != null) {
                aliasToReturn = column.getAlias();
            } else {
                aliasToReturn = column.getTarget();
            }
        }
        return aliasToReturn;
    }

    @Override
    public int getCurrentColumnIndex() {
        return tableManager.getCurrentColumn();
    }

    @Override
    public void setCurrentColumn(int col) {
        int viewColumn = tableManager.convertColumnIndexToView(col);
        tableManager.setCurrentColumn(viewColumn, true);
    }

    @Override
    public boolean deleteColumn(int col) {
        GridColumn column;
        column = tableManager.getColumn(col);
        if (column != null) {
            tableManager.deleteColumn(column);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean deleteColumn(String col) {
        GridColumn column;
        column = tableManager.getColumnByAlias(col);
        if (column != null) {
            tableManager.deleteColumn(column);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getVisibleColumnCount() {
        return tableManager.getVColumnCount();
    }

    @Override
    public int getColumnCount() {
        return tableManager.getAllColumnCount();
    }

    @Override
    public void setCurrentRow(int row) {
        int viewRow = tableManager.convertRowIndexToView(row);
        tableManager.setCurrentRow(viewRow, true, true);
    }

    @Override
    public void addRow() {
        doAction(rmlPropertyContainer.getStringProperty(RmlConstants.ADD));
    }

    @Override
    public void deleteRow() {
        doAction(rmlPropertyContainer.getStringProperty(RmlConstants.DEL));
    }

    @Override
    public void setRowBGColor(int row, String color) {
        tableManager.getUIManager().setRowBgColor(row, color);
    }

    @Override
    public void setRowFGColor(int row, String color) {
        tableManager.getUIManager().setRowFgColor(row, color);
    }

    @Override
    public void setRowTitleBGColor(int row, String color) {
        tableManager.getUIManager().setRowTitleBgColor(row, color);
    }

    @Override
    public void setRowTitleFGColor(int row, String color) {
        tableManager.getUIManager().setRowTitleFgColor(row, color);
    }

    @Override
    public void setRowFont(int row, String font) {
        tableManager.getUIManager().setRowFont(row, font);
    }

    @Override
    public void setRowTitle(int row, String title) {
        tableManager.setRowTitle(row, title);
    }

    @Override
    public void deleteRowTitle(int row) {
        tableManager.deleteRowTitle(row);
    }

    @Override
    public int getRowCount() {
        return tableManager.getRowCount();
    }

    @Override
    public Object currentValue(String col) throws RTException {
        return getCurrentValue(col);
    }

    @Override
    public Object getValue(int row, int col) {
        GridColumn column = tableManager.getColumn(col);
        if (column != null) {
            String target = column.getTarget();
            return datastore.getValue(row, target);
        } else {
            return null;
        }
    }

    @Override
    public Object getValue(int row, String col) {
        GridColumn column = tableManager.getColumnByAlias(col);
        if (column != null) {
            String target = column.getTarget();
            return datastore.getValue(row, target);
        } else {
            return null;
        }
    }

    @Override
    public Object[] getSelectionValues(String col) {

        //обработка вызова метода CurrentValue
        Object[] ret = null;
        GridColumn column = tableManager.getColumnByAlias(col.toUpperCase());
        if (column == null) {
            return ret;
        }
        ret = new Object[tableManager.getSelectionSize()];
        log.debug("inside processing SELECTIONVALUES");
        for (int i = 0; i < tableManager.getSelectionSize(); i++) {
            ret[i] = getSourceValueByTarget(tableManager.getSelectedRow(i), column.getTarget());
        }
        return ret;
    }

    @Override
    public int[] getSelection() {
        int[] ret = new int[tableManager.getSelectionSize()];
        for (int i = 0; i < tableManager.getSelectionSize(); i++) {
            ret[i] = tableManager.getSelectedRow(i);
        }
        return ret;
    }

    @Override
    public void edit() {
        tableManager.startEditAtCell(tableManager.getSelectedRow(), tableManager.getSelectedColumn());
    }

    @Override
    public Datastore getAllDatastore() {
        return getParentDatastore();
    }

    @Override
    public double sum(String col) throws RTException {
        if (getDatastore() == null || getDatastore().getRowCount() == 0) {
            return (double) 0;
        }
        GridColumn column = getTableManager().getColumnByAlias(col.toUpperCase());
        if (column == null) {
            return (double) 0;
        }
        if (!(column.getValue() instanceof Double)) {
            return (double) 0;
        }
        Double sum = 0.0;
        for (int i = 0; i < tableManager.getSelectionSize(); i++) {
            sum += (Double) getSourceValueByTarget(tableManager.getSelectedRow(i), column.getTarget());
        }
        return sum;
    }

    @Override
    public void dumpToFile() {

        JFileChooser d = new JFileChooser();
        d.setDialogTitle("Сохранить в файле");
        d.setApproveButtonText("Сохранить");
        d.setDialogType(JFileChooser.SAVE_DIALOG);
        d.setFileSelectionMode(JFileChooser.FILES_ONLY);
        d.setMultiSelectionEnabled(false);
        int returnVal = d.showSaveDialog(tablePanel.getJComponent());

        try {
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                dumpToFile(d.getSelectedFile());
            }
        } catch (Exception e) {
            log.error("Shit happens", e);
        }
    }

    @Override
    public void repaint() {
        tableManager.repaintAll();
    }

    @Override
    public void invertSelection() {
        Vector<Integer> newSelection = new Vector<Integer>();
        for (int i = 0; i < getSourceRows(); i++) {
            if (!tableManager.containsSelectedRow(i)) {
                newSelection.addElement(i);
            }
        }
        tableManager.setSelection(newSelection);

    }

    @Override
    public void fastSelection(int row) {
        if (!tableManager.containsSelectedRow(row)) {
            tableManager.addSelecteRow(row);
        }
    }

    @Override
    public void setSelection(int row) {
        tableManager.setCurrentRow(row, true, true);
    }

    @Override
    public void selectAll() {

        Vector<Integer> newSelection = new Vector<Integer>();
        for (int i = 0; i < getSourceRows(); i++) {
            newSelection.addElement(i);
        }
        tableManager.setSelection(newSelection);
    }

    @Override
    public int size() {
        return tableManager.getRowCount();
    }

    @Override
    public void allign() {
        tableManager.allign();
    }

    @Override
    public void setCellBGColor(int row, int col, String color) {
        tableManager.getUIManager().setCellBgColor(row, col, color);
    }

    @Override
    public void setCellFGColor(int row, int col, String color) {
        tableManager.getUIManager().setCellFgColor(row, col, color);
    }

    @Override
    public void setCellFont(int row, int col, String font) {
        tableManager.getUIManager().setCellFont(row, col, font);
    }

    @Override
    public void showSearchDialog() {
        getActionManager().showSearchDialog();
    }

    @Override
    public void setValue(int row, int col, Object value) {
        GridColumn column = tableManager.getColumn(col);
        if (column != null) {
            if (column.isVisible()) {
                int colIndex = tableManager.getTableColumnModelIndex(column);
                if (colIndex != -1) {
                    tableManager.insertValueAt(row, colIndex, value);
                }
            }
        }
    }

    @Override
    public void setValue(int row, String col, Object value) {
        GridColumn column = tableManager.getColumnByAlias(col);
        if (column != null) {
            if (column.isVisible()) {
                int colIndex = tableManager.getTableColumnModelIndex(column);
                if (colIndex != -1) {
                    tableManager.insertValueAt(row, colIndex, value);
                }
            }
        }
    }

    @Override
    public void notifySubscribers() {
        notifyAll();
    }

    @Override
    public void setEditable(boolean editable) {
        if (!editable) {
            rmlPropertyContainer.put(RmlConstants.EDITABLE, RmlConstants.NO);
            notifyHandler(null);
        } else if (editable) {
            rmlPropertyContainer.put(RmlConstants.EDITABLE, RmlConstants.YES);
            notifyHandler(null);
        }
    }

    @Override
    public int getCurrentRowIndex() {
        return tableManager.getCurrentRow();
    }
}