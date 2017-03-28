package views.grid.model.cross.parameters;

import java.text.NumberFormat;

import views.grid.model.cross.functions.GenericFunction;

/**
 * Data field descriptor
 *
 * @author vagapova.m
 * @since 20.07.2010
 */
public class DataField extends CrossField{

    /**
     * numeric formatter
     */
    private NumberFormat formatter = null;

    private GenericFunction function;

    public DataField(String columnName, int width, String description, GenericFunction function) {
        super(columnName, width, description);
        this.function = function;
    }

    public DataField(String columnName) {
        super(columnName);
    }

    public final boolean equals(Object obj) {
        if (obj == null || !(obj instanceof DataField)) {
            return false;
        }
        return
                ((DataField) obj).getColumnName().equals(getColumnName()) &&
                        ((DataField) obj).getFunction().equals(getFunction());
    }

    public final int hashCode() {
        return getColumnName().hashCode() * getFunction().hashCode();
    }

    public GenericFunction getFunction() {
        return function;
    }

    /**
     * @return numeric formatter
     */
    public final NumberFormat getFormatter() {
        return formatter;
    }

    /**
     * Set the numeric formatter.
     *
     * @param formatter numeric formatter
     */
    public final void setFormatter(NumberFormat formatter) {
        this.formatter = formatter;
    }

    public void setFunction(GenericFunction function) {
        this.function = function;
    }
}
