package views.grid.model.cross.parameters;

import java.util.ArrayList;

/**
 * Group table model
 *
 * @author: vagapova.m
 * @since: 18.07.2010
 */
public class CrossParameters {

    /**
     * list of RowField objects, related to row fields in Group Table
     */
    private ArrayList<RowField> rowFields;

    /**
     * list of ColumnField objects, related to column fields in Group Table
     */
    private ArrayList<ColumnField> columnFields;

    /**
     * list of DataField objects, related to data fields in Group Table
     */
    private ArrayList<DataField> dataFields;

    public CrossParameters() {
        rowFields = new ArrayList<RowField>();
        columnFields = new ArrayList<ColumnField>();
        dataFields = new ArrayList<DataField>();
    }

    /**
     * @return list of ColumnField objects, related to column fields in Group Table
     */
    public final ArrayList<ColumnField> getColumnFields() {
        return columnFields;
    }

    /**
     * @return list of DataField objects, related to data fields in Group Table
     */
    public final ArrayList<DataField> getDataFields() {
        return dataFields;
    }

    /**
     * @return list of RowField objects, related to row fields in Group Table
     */
    public final ArrayList<RowField> getRowFields() {
        return rowFields;
    }

    public void clearAll() {
        rowFields.clear();
        columnFields.clear();
        dataFields.clear();
    }
}
