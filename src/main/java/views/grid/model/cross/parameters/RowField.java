package views.grid.model.cross.parameters;

import views.grid.model.cross.aggregator.GenericAggregator;

/**
 * Row field descriptor
 *
 * @author: vagapova.m
 * @since: 18.07.2010
 */
public class RowField extends CrossField {

    /**
     * field aggregator, that groups values; default value: GenericAggregator
     */
    private GenericAggregator aggregator = new GenericAggregator();

    public RowField(String columnName, int width, String description) {
        super(columnName, width, description);
    }

    public RowField(String columnName) {
        super(columnName);
    }

    public GenericAggregator getAggregator() {
        return aggregator;
    }

    public final boolean equals(Object obj) {
        if (obj == null || !(obj instanceof RowField)) {
            return false;
        }
        return
                ((RowField) obj).getColumnName().equals(getColumnName()) &&
                        ((RowField) obj).getAggregator().equals(getAggregator());
    }
}
