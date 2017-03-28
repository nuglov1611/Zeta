package views.grid.model.cross.aggregator;

import java.io.Serializable;
import java.util.Calendar;

/**
 * @author: vagapova.m
 * @since: 01.10.2010
 */
public class YearAggregator extends GenericAggregator implements Serializable {

    private Calendar cal = Calendar.getInstance();


    public YearAggregator() {
    }


    public Object decodeValue(Object value) {
        if (value != null && value instanceof java.util.Date) {
            cal.setTimeInMillis(((java.util.Date) value).getTime());
            return new Double(cal.get(cal.YEAR));
        }
        return value;
    }


    public final boolean equals(Object obj) {
        return obj.getClass() == YearAggregator.class;
    }


    public final int hashCode() {
        return YearAggregator.class.hashCode();
    }


}

