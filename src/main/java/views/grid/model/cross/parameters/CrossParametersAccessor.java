package views.grid.model.cross.parameters;

/**
 * @author: vagapova.m
 * @since: 27.09.2010
 */
public class CrossParametersAccessor {

    /**
     * flag used to indicate that Cross Table is not yet updated with current row/column fields
     */
    private boolean groupTableChanged = false;

    /**
     * Cross Table parameters, used to create CrossTable content
     */
    private CrossParameters pars;

    public CrossParametersAccessor() {
        this.pars = new CrossParameters();
    }

    public CrossParametersAccessor(CrossParameters pars) {
        if (pars != null) {
            this.pars = pars;
        }
        else {
            this.pars = new CrossParameters();
        }
    }

    /**
     * Add a column name as row field in Cross Table.
     *
     * @param rowField row field
     */
    public final void addRowField(RowField rowField) {
        pars.getRowFields().add(rowField);
        setGroupTableChanged(true);
    }


    /**
     * Add a column name as row field in Cross Table.
     *
     * @param index    column index to use to insert row field
     * @param rowField row field
     */
    public final void addRowField(int index, RowField rowField) {
        pars.getRowFields().add(index, rowField);
        setGroupTableChanged(true);
    }


    /**
     * Remove a column name as row field in Cross Table.
     *
     * @param rowField row field
     */
    public final void removeRowField(RowField rowField) {
        int index = pars.getRowFields().indexOf(rowField);
        pars.getRowFields().remove(index);
        if (index != -1) {
            setGroupTableChanged(true);
        }
    }


    /**
     * Add a column name as column field in Cross Table.
     *
     * @param columnField column field
     */
    public final void addColumnField(ColumnField columnField) {
        pars.getColumnFields().add(columnField);
        setGroupTableChanged(true);
    }


    /**
     * Add a column name as column field in Cross Table.
     *
     * @param index       column index to use to insert column field
     * @param columnField column field
     */
    public final void addColumnField(int index, ColumnField columnField) {
        pars.getColumnFields().add(index, columnField);
        setGroupTableChanged(true);
    }


    /**
     * Remove a column name as column field in Cross Table.
     *
     * @param columnField column field
     */
    public final void removeColumnField(ColumnField columnField) {
        pars.getColumnFields().remove(columnField);
        setGroupTableChanged(true);
    }


    /**
     * Add a column name as data field in Cross Table.
     *
     * @param dataField data field
     */
    public final void addDataField(DataField dataField) {
        pars.getDataFields().add(dataField);
        setGroupTableChanged(true);
    }


    /**
     * Add a column name as data field in Cross Table.
     *
     * @param index     column index to use to insert data field
     * @param dataField data field
     */
    public final void addDataField(int index, DataField dataField) {
        pars.getDataFields().add(index, dataField);
        setGroupTableChanged(true);
    }


    /**
     * Remove a column name as data field in Cross Table.
     *
     * @param dataField data field
     */
    public final void removeDataField(DataField dataField) {
        int index = pars.getDataFields().indexOf(dataField);
        pars.getDataFields().remove(index);
        if (index != -1) {
            setGroupTableChanged(true);
        }
    }

    /**
     * @return Cross Table parameters, used to create CrossTable content
     */
    public final CrossParameters getCrossTableParameters() {
        return pars;
    }

    /**
     * @pars Cross Table parameters, used to create CrossTable content
     */
    public final void setCrossTableParameters(CrossParameters pars) {
        this.pars = pars;
    }

    public boolean isGroupTableChanged() {
        return groupTableChanged;
    }

    /**
     * Set current state of groupTableChanged flag
     */
    public void setGroupTableChanged(boolean CrossTableChanged) {
        this.groupTableChanged = CrossTableChanged;
    }

    public int getColsSize() {
        return pars.getColumnFields().size();
    }

    public RowField getRowField(int index) {
        return pars.getRowFields().get(index);
    }

    public int getRowsSize() {
        return pars.getRowFields().size();
    }

    public DataField getDataField(int index) {
        return pars.getDataFields().get(index);
    }

    public int getDataSize() {
        return pars.getDataFields().size();
    }

    public void clearAll() {
        pars.clearAll();
    }
}
