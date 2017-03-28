package views.grid.model;

import org.apache.log4j.Logger;
import views.ColumnTemplate;
import views.grid.GridColumn;
import views.grid.GridColumnSet;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.util.*;

/**
 * @author vagapova.m
 * @since 17.10.2010
 */
public class GridMetadataModel {

    private static final Logger log = Logger.getLogger(GridMetadataModel.class);

    /**
     * Columns data
     */
    private Map<Integer, GridColumn> columnsData;

    /**
     * Map column targets to column indexes to allow get column easy by target
     */
    private Map<String, Integer> columnsTargets;


    /**
     * содержит номера ВИДИМЫХ столбцов в массиве columns
     */
    private Vector<Integer> columnsVisible;

    private Vector<ColumnTemplate> columnTemplates;

    /**
     * содержит группы столбцов
     */
    private Vector<GridColumnSet> columnSets;

    /**
     * Constructor
     */
    public GridMetadataModel() {
        init();
    }

    public GridMetadataModel(GridMetadataModel metadataModel) {
        init();
        if (metadataModel != null && metadataModel.getColumnTemplates() != null) {
            metadataModel.setColumnTemplates(metadataModel.getColumnTemplates());
        }
    }

    private void init() {
        columnsVisible = new Vector<Integer>();
        columnsData = new HashMap<Integer, GridColumn>();
        columnsTargets = new HashMap<String, Integer>();
        //0-Number;1-String;2-Data
        columnTemplates = new Vector<ColumnTemplate>(3);
        columnSets = new Vector<GridColumnSet>();
    }

    public int getAllColumnCount() {
        if (columnsData != null) {
            return columnsData.size();
        }
        return 0;
    }

    public int getVColumnCount() {
        if (columnsVisible != null) {
            return columnsVisible.size();
        }
        return 0;
    }

    public boolean isColumnsVisible(Integer columnIndex) {
        boolean isVisible = false;
        if (columnsVisible.contains(columnIndex)) {
            isVisible = true;
        }
        return isVisible;
    }

    public void setColumnTemplate(int index, ColumnTemplate columnTemplate) {
        columnTemplates.setElementAt(columnTemplate, index);
    }

    public ColumnTemplate getColumnTemplate(int index) {
        if (index >= 0 && index < columnTemplates.size()) {
            return columnTemplates.get(index);
        }
        return null;
    }

    public void addColumn(Integer columnIndex, GridColumn newColumn) {
        if (columnsData.containsKey(columnIndex)) {
            Map<Integer, GridColumn> tmpData = new HashMap<Integer, GridColumn>();
            List<Integer> indexes = Arrays.asList(columnsData.keySet().toArray(new Integer[columnsData.size()]));
            Collections.sort(indexes, new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    if (o1 > o2) {
                        return -1;
                    } else if (o1 < o2) {
                        return 1;
                    }
                    return 0;
                }
            });
            for (int currentColumnIndex : indexes) {
                if (currentColumnIndex >= columnIndex) {
                    GridColumn tmpColumn = columnsData.remove(currentColumnIndex);
                    Integer tmpIndex = currentColumnIndex + 1;
                    if (tmpColumn.getTarget() != null) {
                        columnsTargets.put(tmpColumn.getTarget(), tmpIndex);
                    }
                    if (tmpColumn.isVisible() && columnsVisible.indexOf(currentColumnIndex) != -1) {
                        columnsVisible.setElementAt(tmpIndex, columnsVisible.indexOf(currentColumnIndex));
                    }
                    tmpData.put(tmpIndex, tmpColumn);
                }
            }
            columnsData.putAll(tmpData);
        }
        columnsData.put(columnIndex, newColumn);
        if (newColumn.getTarget() != null) {
            columnsTargets.put(newColumn.getTarget(), columnIndex);
        }
        if (newColumn.isVisible()) {
            if (columnIndex >= 0 && columnIndex < columnsVisible.size()) {
                columnsVisible.insertElementAt(columnIndex, columnIndex);
            } else {
                columnsVisible.add(columnIndex);
            }
        }
    }

    public void addColumn(GridColumn newColumn) {
        Integer nextIndex = columnsData.size();
        addColumn(nextIndex, newColumn);
    }

    public void deleteColumn(GridColumn column) {
        Integer columnModelIndex = -1;
        for (Map.Entry<Integer, GridColumn> columnEntry : columnsData.entrySet()) {
            if (columnEntry.getValue().equals(column)) {
                columnModelIndex = columnEntry.getKey();
                break;
            }
        }
        if (columnModelIndex != -1) {
            columnsData.remove(columnModelIndex);
            if (columnsVisible.contains(columnModelIndex)) {
                columnsVisible.remove(columnModelIndex);
            }
            if (columnsTargets.containsKey(column.getTarget())) {
                columnsTargets.remove(column.getTarget());
            }
            Map<Integer, GridColumn> newColumnsData = new HashMap<Integer, GridColumn>(columnsData.size());
            Map<String, Integer> newColumnsTargets = new HashMap<String, Integer>(columnsTargets.size());
            Vector<Integer> newColumnsVisible = new Vector<Integer>(columnsVisible.size());
            List<Integer> oldColumnIndexes = new ArrayList<Integer>(columnsData.keySet());
            Collections.sort(oldColumnIndexes);
            for (Integer oldColumnIndex : oldColumnIndexes) {
                if (oldColumnIndex < columnModelIndex) {
                    GridColumn gridColumn = columnsData.get(oldColumnIndex);
                    newColumnsData.put(oldColumnIndex, gridColumn);
                    if (gridColumn.isVisible()) {
                        newColumnsVisible.add(oldColumnIndex);
                    }
                    if (gridColumn.getTarget() != null) {
                        newColumnsTargets.put(gridColumn.getTarget(), oldColumnIndex);
                    }
                } else {
                    Integer newColumnIndex = oldColumnIndex - 1;
                    GridColumn gridColumn = columnsData.get(oldColumnIndex);
                    newColumnsData.put(newColumnIndex, gridColumn);
                    if (gridColumn.isVisible()) {
                        newColumnsVisible.add(newColumnIndex);
                    }
                    if (gridColumn.getTarget() != null) {
                        newColumnsTargets.put(gridColumn.getTarget(), newColumnIndex);
                    }
                }
            }
            columnsData.clear();
            columnsData.putAll(newColumnsData);
            columnsTargets.clear();
            columnsTargets.putAll(newColumnsTargets);
            columnsVisible.clear();
            columnsVisible.addAll(newColumnsVisible);
        }
    }

    public GridColumn getTColumn(Integer columnIndex) {
        return columnsData.get(columnIndex);
    }

    public GridColumn getTColumn(String target) {
        Integer columnIndex = getColumnIndexByTarget(target);
        if (columnIndex != null) {
            return columnsData.get(columnIndex);
        }
        return null;
    }

    public Integer getColumnIndexByTarget(String target) {
        if (columnsTargets.containsKey(target)) {
            return columnsTargets.get(target);
        } else if (columnsTargets.containsKey(target.toUpperCase())) {
            return columnsTargets.get(target.toUpperCase());
        }
        return null;
    }

    public GridColumn getVColumn(Integer columnIndex) {
        try {
            if (columnIndex >= 0 && columnIndex < columnsVisible.size()) {
                return getTColumn(columnsVisible.get(columnIndex));
            }
        } catch (Exception e) {
            log.error("Shit happens", e);
        }
        return null;
    }

    public Vector<ColumnTemplate> getColumnTemplates() {
        return columnTemplates;
    }

    public void setColumnTemplates(Vector<ColumnTemplate> columnTemplates) {
        this.columnTemplates = columnTemplates;
    }

    public int setColumnVisible(GridColumn column, boolean isVisible) {
        int movedIndex = -1;
        Integer columnIndex = getColumnIndexByTarget(column.getTarget());
        //getColumnModelIndex(column);
        if (columnIndex != null) {
            if (isVisible && !columnsVisible.contains(columnIndex)) {
                int closestColumnIndex = findNearColumnIndex(columnIndex);
                if (closestColumnIndex != -1) {
                    columnsVisible.insertElementAt(columnIndex, closestColumnIndex);
                } else {
                    columnsVisible.add(columnIndex);
                }
                movedIndex = columnsVisible.indexOf(columnIndex);
            } else if (!isVisible && columnsVisible.contains(columnIndex)) {
                movedIndex = columnsVisible.indexOf(columnIndex);
                columnsVisible.remove(columnIndex);
            }
        }
        column.setVisible(isVisible);
        return movedIndex;
    }

    private int findNearColumnIndex(Integer columnIndex) {
        Integer nearestColumnIndex = -1;
        boolean afterColumnIndex = false;
        Set<Integer> columnIndexesSet = columnsData.keySet();
        Integer[] columnIndexes = columnIndexesSet.toArray(new Integer[columnIndexesSet.size()]);
        Arrays.sort(columnIndexes);
        for (Integer curColumnIndex : columnIndexes) {
            if (columnsVisible.contains(curColumnIndex)) {
                nearestColumnIndex = curColumnIndex;
                if (afterColumnIndex) {
                    break;
                }
            }
            if (columnIndex.equals(curColumnIndex)) {
                if (nearestColumnIndex != -1) {
                    break;
                } else {
                    afterColumnIndex = true;
                }
            }
        }
        if (nearestColumnIndex != -1) {
            int columnVisibleIndex = columnsVisible.indexOf(nearestColumnIndex);
            if (afterColumnIndex && columnVisibleIndex > 0) {
                columnVisibleIndex--;
            }
            return columnVisibleIndex;
        } else {
            return -1;
        }
    }

    public void addColumnSet(GridColumnSet gridColumnSet) {
        columnSets.add(gridColumnSet);
        for (GridColumnSet columnSet : gridColumnSet.getColumnSets()) {
            addColumnSet(columnSet);
        }
        for (GridColumn column : gridColumnSet.getColumns()) {
            addColumn(column);
        }
    }


    public Enumeration<GridColumnSet> getColumnSets(TableColumn col) {
        if (columnSets == null) {
            return null;
        }
        int columnModelIndex = col.getModelIndex();
        if (columnModelIndex >= 0 && columnModelIndex < columnsVisible.size()) {
//        if (columnsVisible.contains(columnModelIndex)) {
            GridColumn gridColumn = columnsData.get(columnsVisible.get(columnModelIndex));
            if (gridColumn != null) {
                Enumeration<GridColumnSet> enumer = columnSets.elements();
                while (enumer.hasMoreElements()) {
                    GridColumnSet columnSet = enumer.nextElement();
                    Vector<GridColumnSet> v_ret = columnSet.getColumnSets(gridColumn, new Vector<GridColumnSet>());
                    if (v_ret != null) {
                        return v_ret.elements();
                    }
                }
            }
        }
        return null;
    }

    public void setColumnMargin(TableColumnModel columnModel) {
        if (columnSets == null) {
            return;
        }
        int columnMargin = columnModel.getColumnMargin();
        Enumeration<GridColumnSet> enumer = columnSets.elements();
        while (enumer.hasMoreElements()) {
            GridColumnSet columnSet = enumer.nextElement();
            columnSet.setColumnMargin(columnMargin);
        }
    }

    public int getColumnModelIndex(GridColumn column) {
        for (Map.Entry<Integer, GridColumn> columnEntry : columnsData.entrySet()) {
            if (columnEntry.getValue().equals(column)) {
                Integer columnModelIndex = columnEntry.getKey();

                if (columnsVisible.contains(columnModelIndex)) {
                    return columnsVisible.indexOf(columnModelIndex);
                }
            }
        }
        return -1;
    }
}
