package views.grid.model.cross.aggregator;

import java.io.Serializable;
import java.util.Calendar;

/**
 * @author: vagapova.m
 * @since: 01.10.2010
 */
public class HalfYearAggregator extends GenericAggregator implements Serializable {

    private Calendar cal = Calendar.getInstance();


    public HalfYearAggregator() {
    }


    public Object decodeValue(Object value) {
        if (value != null && value instanceof java.util.Date) {
            cal.setTimeInMillis(((java.util.Date) value).getTime());
            int month = cal.get(Calendar.MONTH);
            if (month <= 6)
                return new Double(1);
            else
                return new Double(2);
        }
        return value;
    }


    public final boolean equals(Object obj) {
        return obj.getClass() == HalfYearAggregator.class;
    }


    public final int hashCode() {
        return HalfYearAggregator.class.hashCode();
    }
}