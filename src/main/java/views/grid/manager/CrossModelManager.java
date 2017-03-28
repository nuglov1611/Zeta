package views.grid.manager;

import java.awt.Cursor;
import java.awt.Toolkit;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.SwingUtilities;

import views.ColumnTemplate;
import views.grid.GridColumn;
import views.grid.GridSwing;
import views.grid.model.GridMetadataModel;
import views.grid.model.cross.CrossColumnModel;
import views.grid.model.cross.CrossDataModel;
import views.grid.model.cross.CrossRowModel;
import views.grid.model.cross.functions.AverageFunction;
import views.grid.model.cross.functions.DisplayFunction;
import views.grid.model.cross.functions.GenericFunction;
import views.grid.model.cross.functions.MaxFunction;
import views.grid.model.cross.functions.MinFunction;
import views.grid.model.cross.functions.SumFunction;
import views.grid.model.cross.node.ColGenericNode;
import views.grid.model.cross.node.GenericNodeKey;
import views.grid.model.cross.node.GlobalColGenericNode;
import views.grid.model.cross.node.RowGenericNode;
import views.grid.model.cross.parameters.ColumnField;
import views.grid.model.cross.parameters.CrossField;
import views.grid.model.cross.parameters.CrossParameters;
import views.grid.model.cross.parameters.CrossParametersAccessor;
import views.grid.model.cross.parameters.DataField;
import views.grid.model.cross.parameters.RowField;
import views.grid.renderer.cross.CellSpanModel;
import core.parser.Proper;
import core.rml.RmlConstants;
import core.rml.dbi.DatastoreModel;

/**
 * @author: vagapova.m
 * @since: 27.09.2010
 */
public class CrossModelManager {

    private static final String GROUP_SEPARATOR = ";";

    private static final String COLUMN_SEPARATOR = ",";

    private static final String GROUP_START = "{";

    private static final String GROUP_END = "}";

    /**
     * model of Cross Table
     */
    private CrossDataModel dataModel = null;

    private CrossColumnModel columnModel = null;

    private CrossRowModel rowModel = null;

    /**
     * root node, related to row fields
     */
    private RowGenericNode hroot = null;

    /**
     * root node, related to column fields
     */
    private GlobalColGenericNode vroot = null;

    private GridSwing parent;

    private int dataColIndex;

    /**
     * collection of pairs row index,RowGenericNode objects related to current expandable/collapsable row field>
     */
    private HashMap<Integer, RowGenericNode> currentExpandableRowFields = new HashMap<Integer, RowGenericNode>();

    /**
     * collection of pairs column index,ColGenericNode objects related to current expandable/collapsable row field>
     */
    private HashMap<Integer, GlobalColGenericNode> currentExpandableColFields = new HashMap<Integer, GlobalColGenericNode>();

    /**
     * date formatter
     */
    private SimpleDateFormat sdf = new SimpleDateFormat();

    private CellSpanModel colsCellSpanModel;

    private CellSpanModel rowsCellSpanModel;

    public CrossModelManager(GridSwing parent) {
        this.parent = parent;
    }

    public int getHChildrenCount() {
        return hroot.getChildrenCount();
    }

    public RowGenericNode getHChild(int i) {
        return hroot.getChildren(i);
    }

    public int getVChildrenCount() {
        return vroot.getChildrenCount();
    }

    public GlobalColGenericNode getVChild(int i) {
        return vroot.getChildren(i);
    }


    /**
     * @return root node, related to row fields
     */
    public final RowGenericNode getHRoot() {
        return hroot;
    }

    /**
     * @return root node, related to column fields
     */
    public final GlobalColGenericNode getVRoot() {
        return vroot;
    }

    /**
     * Method invoked to re-analyze data model and show data in Cross Table.
     */
    public final void compileData() throws Exception {
        generateCrossTableTree();
        buildCrossTableModel();
        parent.getParametersAccessor().setGroupTableChanged(false);
    }

    /**
     * Method invoked when all rows, columns and data fiels are already created
     */
    private void buildCrossTableModel() {
        clearAll();
        rowModel = new CrossRowModel(parent);
        columnModel = new CrossColumnModel(parent);
        dataModel = new CrossDataModel();

        ArrayList<Object[]> rowFields = new ArrayList<Object[]>();
        rowsCellSpanModel = new CellSpanModel(0, parent.getParametersAccessor().getRowsSize());
        processRowNode(rowFields, rowsCellSpanModel, hroot, 0);
        rowModel.setRowFields(rowFields);
        ArrayList<Object[]> columnFields = new ArrayList<Object[]>();
        colsCellSpanModel = new CellSpanModel(parent.getParametersAccessor().getColsSize() + 1, 0);
        processColumnNode(columnFields, colsCellSpanModel, vroot, 0);
        columnModel.setColumnFields(columnFields);
        ArrayList<Object[]> dataFields = new ArrayList<Object[]>();
        processDataHNode(dataFields, hroot, 0);
        dataModel.setDataFields(dataFields);
    }

    /**
     * Method invoked to re-analyze data model and show data in Cross Table.
     * Data analysis is perfomed in a separated thread, in order to avoid to block application usage.
     */
    public final void compileDataInThread() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    parent.getVisualComponent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    Toolkit.getDefaultToolkit().sync();
                    compileData();
                } catch (Throwable ignored) {
                    ignored.printStackTrace();
                } finally {
                    parent.getVisualComponent().setCursor(Cursor.getDefaultCursor());
                    Toolkit.getDefaultToolkit().sync();
                }
            }
        });
    }

    /**
     * @return GroupRootTableModel generated starting from CrossParameters
     */
    public void generateCrossTableTree() throws Exception {
        CrossParameters pars = parent.getParametersAccessor().getCrossTableParameters();
        GridMetadataModel metadataModel = parent.getTableManager().getMetadataModel();
        DatastoreModel dsModel = parent.getDatastore().getModel();
        // index metadata model column names...
        HashMap<String, Integer> metadataIndexes = new HashMap<String, Integer>();
        for (int j = 0; j < metadataModel.getAllColumnCount(); j++) {
            String columnTarget = metadataModel.getTColumn(j).getTarget();
            if (columnTarget != null) {
                Integer columnIndex = dsModel.getColumnIndex(columnTarget);
                metadataIndexes.put(columnTarget, columnIndex);
            }
        }

        HashMap<String, Integer> dsIndexes = new HashMap<String, Integer>();
        for (String columnName : dsModel.getColumnNames()) {
            Integer columnIndex = dsModel.getColumnIndex(columnName);
            dsIndexes.put(columnName, columnIndex);
        }

        // check whether some row/column/data fields are unknown...
        for (int i = 0; i < pars.getRowFields().size(); i++)
            if (!dsIndexes.containsKey(pars.getRowFields().get(i).getColumnName())) {
                throw new Exception(pars.getRowFields().get(i).getColumnName() + "' row field is not defined in data rootModel: Cross Table cannot be created.");
            }
        for (int i = 0; i < pars.getColumnFields().size(); i++)
            if (!dsIndexes.containsKey(pars.getColumnFields().get(i).getColumnName())) {
                throw new Exception(pars.getColumnFields().get(i).getColumnName() + "' column field is not defined in data rootModel: Cross Table cannot be created.");
            }
        for (int i = 0; i < pars.getDataFields().size(); i++)
            if (!dsIndexes.containsKey(pars.getDataFields().get(i).getColumnName())) {
                throw new Exception(pars.getDataFields().get(i).getColumnName() + "' data field is not defined in data rootModel: Cross Table cannot be created.");
            }
        if (pars.getRowFields().size() == 0) {
            throw new Exception("At least one field must be defined as row field: Cross Table cannot be created.");
        }
        if (pars.getColumnFields().size() == 0) {
            throw new Exception("At least one field must be defined as column field: Cross Table cannot be created.");
        }
        if (pars.getDataFields().size() == 0) {
            throw new Exception("At least one field must be defined as data field: Cross Table cannot be created.");
        }

        // create Cross TableModel...
        hroot = new RowGenericNode();
        vroot = new GlobalColGenericNode();

//    StringBuffer hpath = new StringBuffer();
        GenericNodeKey hpath = null;
        RowGenericNode hnode = null;
        ColGenericNode vnode = null;
        GlobalColGenericNode globalvnode = null;
        RowGenericNode hparentNode = null;
        ColGenericNode vparentNode = null;
        GlobalColGenericNode globalvparentNode = null;
        RowField rowField = null;
        ColumnField colField = null;
        Object hvalue = null;
        Object vvalue = null;
        HashMap<GenericNodeKey, RowGenericNode> htreeNodes = new HashMap<GenericNodeKey, RowGenericNode>();
        HashMap<GenericNodeKey, ColGenericNode> vtreeNodes = null;
        HashMap<GenericNodeKey, GlobalColGenericNode> globalvtreeNodes = new HashMap<GenericNodeKey, GlobalColGenericNode>();
        GenericNodeKey vpath = null;
        GenericFunction[] gf = null;
        int dataSize = pars.getDataFields().size();
        for (Integer rowIndex : dsModel.getRowIndexes()) {
            // read row...
            hparentNode = hroot;
            hpath = new GenericNodeKey();

            // for each row: parse row data, along row fields...
            for (int j = 0; j < pars.getRowFields().size(); j++) {
                rowField = pars.getRowFields().get(j);
                hvalue = rowField.getAggregator().decodeValue(dsModel.getValueAt(rowIndex, dsIndexes.get(rowField.getColumnName())));
                if (hvalue == null) {
                    hvalue = "";
                }

                hpath = hpath.appendKey(hvalue);
                hnode = htreeNodes.get(hpath);
                if (hnode == null) {
                    hnode = new RowGenericNode(hpath);
                    htreeNodes.put(hpath, hnode);
                    hparentNode.add(hnode);
                }
                vtreeNodes = hnode.getVtreeNodes();

                // for each row field node, parse row data, along column fields and calculate data field values...
                vparentNode = null;
                globalvparentNode = vroot;
                vpath = new GenericNodeKey();
                for (int y = 0; y < pars.getColumnFields().size(); y++) {
                    colField = pars.getColumnFields().get(y);
                    vvalue = colField.getAggregator().decodeValue(dsModel.getValueAt(rowIndex, dsIndexes.get(colField.getColumnName())));
                    if (vvalue == null) {
                        vvalue = "";
                    }

                    vpath = vpath.appendKey(vvalue);
                    vnode = vtreeNodes.get(vpath);
                    if (vnode == null) {
                        gf = new GenericFunction[dataSize];
                        for (int u = 0; u < dataSize; u++)
                            try {
                                GenericFunction dfGF = pars.getDataFields().get(u).getFunction();
                                if (dfGF != null) {
                                    gf[u] = pars.getDataFields().get(u).getFunction().getClass().newInstance();
                                } else {
                                    gf[u] = new GenericFunction();
                                }
                            } catch (Throwable ex) {
                                throw new Exception("Error while analyzing data.");
                            }

                        vnode = new ColGenericNode(vpath, gf);
                        vtreeNodes.put(vpath, vnode);
                        if (vparentNode != null) {
                            vparentNode.add(vnode);
                        } else {
                            hnode.setColsParentNode(vnode);
                        }
                    } else {
                        gf = vnode.getGenericFunctions();
                    }

                    // Calculate and aggregate actual values from model
                    for (int dataIndex = 0; dataIndex < dataSize; dataIndex++) {
                        gf[dataIndex].processValue(dsModel.getValueAt(rowIndex, dsIndexes.get(pars.getDataFields().get(dataIndex).getColumnName())));
                    }
                    vparentNode = vnode;

                    // moreover, create a global column fields hierarchy, without associating data field values...
                    globalvnode = globalvtreeNodes.get(vpath);
                    if (globalvnode == null) {
                        globalvnode = new GlobalColGenericNode(vpath);
                        globalvtreeNodes.put(vpath, globalvnode);
                        globalvparentNode.add(globalvnode);
                    }
                    globalvparentNode = globalvnode;

                } // end for on colFields

                hparentNode = hnode;
            } // end for on rowFields
        } // end while
    }

    private List<Integer> buildSpanList(int startPos, int spanSize) {
        List<Integer> spanList = new Vector<Integer>();
        if (spanSize > 0) {
            for (int j = 0; j < spanSize; j++) {
                spanList.add(startPos + j);
            }
        } else {
            spanList.add(startPos);
        }
        return spanList;
    }

    private void processDataHNode(ArrayList<Object[]> rows, RowGenericNode parentNode, int pos) {
        Object[] row;
        RowGenericNode n;
        for (int i = 0; i < parentNode.getChildrenCount(); i++) {
            n = parentNode.getChildren(i);
            row = new Object[columnModel.getColumnCount()];
            dataColIndex = 0;
            processDataVNode(row, n.getVtreeNodes(), vroot, new GenericNodeKey());

            rows.add(row);
            if (n.isNodeExpanded()) {
                n.setNodeExpanded(true);
                processDataHNode(rows, n, pos + 1);
            }
        }
    }

    private void processDataVNode(Object[] row, HashMap dataValues, GlobalColGenericNode n, GenericNodeKey key) {
        GlobalColGenericNode vn;
        ColGenericNode obj;
        Number num;
        GenericNodeKey currentKey;
        Object obj1, obj2;
        for (int i = 0; i < n.getChildrenCount(); i++) {
            vn = n.getChildren(i);
            currentKey = key.appendKey(vn.getValue());
            obj = (ColGenericNode) dataValues.get(currentKey);
            dataColIndex = 0;
            for (int x = 0; x < currentKey.getPath().length; x++)
                for (int y = dataColIndex; y < columnModel.getColumnCount(); y++) {
                    obj1 = currentKey.getPath()[x];
                    obj2 = columnModel.getValueAt(x, y);
                    if (obj1 != null && obj2 != null) {
                        if (obj1 instanceof Number && obj2 instanceof Number &&
                                ((Number) obj1).doubleValue() == ((Number) obj2).doubleValue() ||
                                obj1.equals(obj2)) {
                            dataColIndex = y;
                            break;
                        }
                    }
                }

            if (obj != null) {
                for (int k = 0; k < parent.getParametersAccessor().getDataSize(); k++) {
                    GenericFunction gf = obj.getGenericFunctions()[k];
                    num = gf.getValue();
                    if (gf instanceof DisplayFunction) {
                        if (num == null) {
                            row[dataColIndex++] = null;
                        } else {
                            row[dataColIndex++] = ((DisplayFunction)gf).getDisplayValue();
                        }
                    } else {
                        if (num == null) {
                            row[dataColIndex++] = num;
                        } else if (num.longValue() == num.doubleValue()) {
                            row[dataColIndex++] = new Long(num.longValue());
                        } else {
                            row[dataColIndex++] = num;
                        }
                    }
                }
            }
            if (vn.isNodeExpanded()) {
                vn.setNodeExpanded(true);
                processDataVNode(row, dataValues, vn, key.appendKey(vn.getValue()));
            }
        }
    }

    public void processColumnNode(ArrayList<Object[]> cols, CellSpanModel colsCellSpanModel, GlobalColGenericNode parentNode, int pos) {
        Object[] col;
        GlobalColGenericNode n;
        int oldIndex;
        List<Integer> rowCellsSpan;
        List<Integer> colCellsSpan;
        for (int i = 0; i < parentNode.getChildrenCount(); i++) {
            boolean descriptionRequired = parent.getBooleanProperty(RmlConstants.DESCRIPTION_REQUIRED);
            int colSize = parent.getParametersAccessor().getColsSize();
            if (descriptionRequired) {
                colSize++;
            }
            col = new Object[colSize];
            n = parentNode.getChildren(i);
            if (descriptionRequired) {
                col[0] = parent.getParametersAccessor().getDataField(0).getDescription();
                col[pos+1] = formatValue(n.getValue());
            } else {
            col[pos] = formatValue(n.getValue());
            }
            cols.add(col);
            oldIndex = cols.size() - 1;
            colsCellSpanModel.addColumn();
            addColumn(oldIndex, n);

//            for (int k = 1; k < parent.getParametersAccessor().getDataSize(); k++) {
//                col = new Object[parent.getParametersAccessor().getColsSize() + 1];
//                col[parent.getParametersAccessor().getColsSize()] = parent.getParametersAccessor().getDataField(k).getDescription();
//                cols.add(col);
//                colsCellSpanModel.addColumn();
//            }

            if (n.isNodeExpanded()) {
                n.setNodeExpanded(true);
                processColumnNode(cols, colsCellSpanModel, n, pos + 1);

                rowCellsSpan = buildSpanList(pos + 1, parent.getParametersAccessor().getColsSize() - pos - 1);
                colCellsSpan = buildSpanList(oldIndex, parent.getParametersAccessor().getDataSize());

                colsCellSpanModel.combine(rowCellsSpan, colCellsSpan);

                rowCellsSpan = buildSpanList(pos, 0);
            } else {
                rowCellsSpan = buildSpanList(pos, parent.getParametersAccessor().getColsSize() - pos);
            }
            colCellsSpan = buildSpanList(oldIndex, cols.size() - oldIndex);
            colsCellSpanModel.combine(rowCellsSpan, colCellsSpan);
        }
    }

    private void processRowNode(ArrayList<Object[]> rows, CellSpanModel rowsCellSpanModel, RowGenericNode parentNode, int pos) {
        Object[] row;
        RowGenericNode n;
        int oldIndex;
        List<Integer> rowCellsSpan;
        List<Integer> colCellsSpan;
        for (int i = 0; i < parentNode.getChildrenCount(); i++) {
            row = new Object[parent.getParametersAccessor().getRowsSize()];
            n = parentNode.getChildren(i);
            row[pos] = formatValue(n.getValue());
            rows.add(row);
            oldIndex = rows.size() - 1;
            rowsCellSpanModel.addRow();
            addRow(oldIndex, n);

            if (n.isNodeExpanded()) {
                n.setNodeExpanded(true);

                processRowNode(rows, rowsCellSpanModel, n, pos + 1);
                rowCellsSpan = buildSpanList(oldIndex, 0);
                colCellsSpan = buildSpanList(pos + 1, parent.getParametersAccessor().getRowsSize() - pos - 1);
                rowsCellSpanModel.combine(rowCellsSpan, colCellsSpan);

                colCellsSpan = buildSpanList(pos, 0);
            } else {
                colCellsSpan = buildSpanList(pos, parent.getParametersAccessor().getRowsSize() - pos);
            }

            rowCellsSpan = buildSpanList(oldIndex, rows.size() - oldIndex);
            rowsCellSpanModel.combine(rowCellsSpan, colCellsSpan);
        }
    }


    /**
     * Collapse all row nodes.
     */
    public boolean setExpansionStateNode(RowGenericNode node, boolean expanded, int level) {
        node.setNodeExpanded(expanded);
        boolean containsChildren = false;
        if (node.getLevel() <= level || level == -1) {
            for (int i = 0; i < node.getChildrenCount(); i++)
                containsChildren = containsChildren || setExpansionStateNode(node.getChildren(i), expanded, level);
        }
        return containsChildren;
    }


    /**
     * Collapse column nodes.
     */
    public boolean setExpansionStateNode(GlobalColGenericNode node, boolean expanded, int level) {
        node.setNodeExpanded(expanded);
        boolean containsChildren = false;
        if (node.getLevel() <= level || level == -1) {
            for (int i = 0; i < node.getChildrenCount(); i++)
                containsChildren = containsChildren || setExpansionStateNode(node.getChildren(i), expanded, level);
        }
        return containsChildren;
    }

    /**
     * Expand all row/column fields.
     */
    public void expandAll() {
        int level = 1;
        boolean containsChildren = true;
        while (containsChildren) {
            containsChildren = false;
            for (int i = 0; i < getHChildrenCount(); i++)
                containsChildren = containsChildren || setExpansionStateNode(getHChild(i), true, level);
            level++;
            buildCrossTableModel();
        }
        level = 0;
        containsChildren = true;
        while (containsChildren) {
            containsChildren = false;
            for (int i = 0; i < getVChildrenCount(); i++) {
                containsChildren = containsChildren || setExpansionStateNode(getVChild(i), true, level);
                buildCrossTableModel();
            }
            level++;
        }
    }

    /**
     * Collapse all row/column fields.
     */
    public void collapseAll() {
        currentExpandableColFields.clear();
        currentExpandableRowFields.clear();

        for (int i = 0; i < getHChildrenCount(); i++)
            setExpansionStateNode(getHChild(i), false, -1);
        for (int i = 0; i < getVChildrenCount(); i++)
            setExpansionStateNode(getVChild(i), false, -1);
        buildCrossTableModel();
    }

    private final void coordinateRow(int row, boolean expanded) {
        int r = 0;
        //TODO put it back
//        if (row > 0) {
//            int oldr = 0;
//            for (int i = 0; i < row; i++)
//                if (parent.getRowsTable().rowAtPoint(new Point(5, i * parent.getDataTable().getRowHeight())) != oldr) {
//                    oldr = parent.getRowsTable().rowAtPoint(new Point(5, i * parent.getDataTable().getRowHeight()));
//                    r++;
//                }
//        }
        setExpansionStateNode(getHChild(r), expanded, r);
        buildCrossTableModel();
    }

    /**
     * Expand all rows fields, starting from the specified row index.
     *
     * @param row row index to expand
     */
    public final void expandRow(int row) {
        coordinateRow(row, true);
    }

    /**
     * Collapse all rows fields, starting from the specified row index.
     *
     * @param row row index to collapse
     */
    public final void collapseRow(int row) {
        coordinateRow(row, false);
    }

    private final void coordinateColumn(int column, boolean expanded) {
        int c = 0;
        //TODO put it back
//        if (column > 0) {
//            int oldc = 0;
//            int w = 0;
//            for (int i = 0; i < column; i++) {
//                if (table.getColsTable().columnAtPoint(new Point(w, 5)) != oldc) {
//                    oldc = table.getColsTable().columnAtPoint(new Point(w, 5));
//                    c++;
//                }
//                w += table.getDataTable().getColumnModel().getColumn(i).getWidth();
//            }
//            if (c >= getVChildrenCount()) {
//                c = getVChildrenCount() - 1;
//            }
//        }
        setExpansionStateNode(getVChild(c), expanded, c);
        buildCrossTableModel();
    }

    /**
     * Expand all column fields, starting from the specified column index.
     *
     * @param column column index to expand
     */
    public final void expandColumn(int column) {
        coordinateColumn(column, true);
    }

    /**
     * Collapse all column fields, starting from the specified column index.
     *
     * @param column column index to collapse
     */
    public final void collapseColumn(int column) {
        coordinateColumn(column, false);
    }

    public void clearAll() {
        currentExpandableRowFields.clear();
        currentExpandableColFields.clear();
    }

    public boolean containsRow(int row) {
        return currentExpandableRowFields.get(new Integer(row)) != null;
    }

    public RowGenericNode removeRow(int row) {
        return currentExpandableRowFields.remove(new Integer(row));
    }

    public boolean containsColumn(int col) {
        return currentExpandableColFields.get(new Integer(col)) != null;
    }

    public GlobalColGenericNode removeColumn(int col) {
        return currentExpandableColFields.remove(new Integer(col));
    }

    public void addColumn(int col, GlobalColGenericNode colGenericNode) {
        currentExpandableColFields.put(col, colGenericNode);
    }

    public void addRow(int row, RowGenericNode rowGenericNode) {
        currentExpandableRowFields.put(row, rowGenericNode);
    }

    public GlobalColGenericNode getColumn(int col) {
        return currentExpandableColFields.get(new Integer(col));
    }

    public RowGenericNode getRow(int row) {
        return currentExpandableRowFields.get(new Integer(row));
    }

    public Object formatValue(Object obj) {
        if (obj != null) {
            if (obj instanceof java.util.Date) {
                return sdf.format((java.util.Date) obj);
            } else if (obj instanceof Number) {
                Number num = (Number) obj;
                if (num.longValue() == num.doubleValue()) {
                    return (num.longValue());
                } else {
                    return num;
                }
            } else {
                return obj;
            }
        } else {
            return obj;
        }
    }

    public RowGenericNode getHroot() {
        return hroot;
    }

    public GlobalColGenericNode getVroot() {
        return vroot;
    }

    public CrossDataModel getDataModel() {
        return dataModel;
    }

    public CrossColumnModel getColumnModel() {
        return columnModel;
    }

    public CrossRowModel getRowModel() {
        return rowModel;
    }

    public CellSpanModel getColsCellSpanModel() {
        return colsCellSpanModel;
    }

    public CellSpanModel getRowsCellSpanModel() {
        return rowsCellSpanModel;
    }

    public void generateCrossParams(CrossParametersAccessor parametersAccessor, GridMetadataModel metadataModel) {
        Proper defp = ColumnTemplate.getDefaultProperties();
        parametersAccessor.clearAll();
        if (parent.getStringProperty(RmlConstants.COLUMNFIELDS) != null) {
            String columnFields = parent.getStringProperty(RmlConstants.COLUMNFIELDS);
            String[] colGroups = columnFields.split(GROUP_SEPARATOR);
            for (String group : colGroups) {
                if (group.startsWith(GROUP_START) && group.endsWith(GROUP_END)) {
                    String crossMembers = group.substring(1, group.length() - 1);
                    String[] crossColumns = crossMembers.split(COLUMN_SEPARATOR);
                    for (String crossColumn : crossColumns) {
                        crossColumn = crossColumn.trim();
                        ColumnField columnField = new ColumnField(crossColumn);
                        processCrossField(metadataModel, crossColumn, columnField);
                        parametersAccessor.addColumnField(columnField);
                    }
                }
            }
        }
        if (parent.getStringProperty(RmlConstants.ROWFIELDS) != null) {
            String rowFields = parent.getStringProperty(RmlConstants.ROWFIELDS);
            String[] rowGroups = rowFields.split(GROUP_SEPARATOR);
            for (String group : rowGroups) {
                if (group.startsWith(GROUP_START) && group.endsWith(GROUP_END)) {
                    String crossMembers = group.substring(1, group.length() - 1);
                    String[] crossRows = crossMembers.split(COLUMN_SEPARATOR);
                    for (String crossRow : crossRows) {
                        crossRow = crossRow.trim();
                        RowField rowField = new RowField(crossRow);
                        processCrossField(metadataModel, crossRow, rowField);
                        parametersAccessor.addRowField(rowField);
                    }
                }
            }
        }
        if (parent.getStringProperty(RmlConstants.DATAFIELDS) != null) {
            String dataFields = parent.getStringProperty(RmlConstants.DATAFIELDS);
            String[] dataGroups = dataFields.split(GROUP_SEPARATOR);
            for (String group : dataGroups) {
                if (group.startsWith(GROUP_START) && group.endsWith(GROUP_END)) {
                    String crossMembers = group.substring(1, group.length() - 1);
                    String[] crossDatas = crossMembers.split(COLUMN_SEPARATOR);
                    for (String crossData : crossDatas) {
                        crossData = crossData.trim();
                        DataField dataField = new DataField(crossData);
                        processCrossField(metadataModel, crossData, dataField);
                        parametersAccessor.addDataField(dataField);
                    }
                }
            }
        }
    }

    private void processCrossField(GridMetadataModel metadataModel, String columnName, CrossField field) {
        GridColumn gridColumn = metadataModel.getTColumn(columnName);
        if (gridColumn != null) {
            field.setDescription(gridColumn.getTitle());
            field.setWidth(gridColumn.getColumn().getWidth());
            if (field instanceof DataField) {

                String functionName = gridColumn.getStringProperty(RmlConstants.FUNCTION);
                if (functionName == null) {
                    functionName = parent.getStringProperty(RmlConstants.FUNCTION);
                }
                if (functionName != null) {
                    if (functionName.equalsIgnoreCase(RmlConstants.SUM_FUNCTION)) {
                        ((DataField) field).setFunction(new SumFunction());
                    } else if (functionName.equalsIgnoreCase(RmlConstants.MAX_FUNCTION)) {
                        ((DataField) field).setFunction(new MaxFunction());
                    } else if (functionName.equalsIgnoreCase(RmlConstants.MIN_FUNCTION)) {
                        ((DataField) field).setFunction(new MinFunction());
                    } else if (functionName.equalsIgnoreCase(RmlConstants.AVG_FUNCTION)) {
                        ((DataField) field).setFunction(new AverageFunction());
                    } else if (functionName.equalsIgnoreCase(RmlConstants.DISPLAY_FUNCTION)) {
                        ((DataField) field).setFunction(new DisplayFunction());
                    }
                }
            }
        }
    }
}
