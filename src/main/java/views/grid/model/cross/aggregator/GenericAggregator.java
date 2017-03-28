package views.grid.model.cross.aggregator;

import java.io.Serializable;

/**
 * @author: vagapova.m
 * @since: 01.10.2010
 */
public class GenericAggregator implements Serializable {


    public GenericAggregator() {
    }


    public Object decodeValue(Object value) {
        return value;
    }


    public boolean equals(Object obj) {
        return obj.getClass() == GenericAggregator.class;
    }


    public int hashCode() {
        return GenericAggregator.class.hashCode();
    }
}

