package views.grid.model.cross.parameters;

import views.grid.model.cross.aggregator.GenericAggregator;

/**
 * Column field descriptor
 *
 * @author: vagapova.m
 * @since: 18.07.2010
 */
public class ColumnField extends CrossField {
    /**
     * field aggregator, that groups values; default value: GenericAggregator
     */
    private GenericAggregator aggregator = new GenericAggregator();

    public ColumnField(String columnName, GenericAggregator aggregator) {
        this(columnName, columnName, aggregator);
    }

    public ColumnField(String columnName, String description, GenericAggregator aggregator) {
        super(columnName, description);
        if (aggregator != null) {
        this.aggregator = aggregator;
    }
    }

    public ColumnField(String columnName) {
        super(columnName);
    }

    public GenericAggregator getAggregator() {
        return aggregator;
    }

    public final boolean equals(Object obj) {
        return !(obj == null || !(obj instanceof ColumnField)) && ((ColumnField) obj).getColumnName().equals(getColumnName()) &&
                ((ColumnField) obj).getAggregator().equals(getAggregator());
    }
}
