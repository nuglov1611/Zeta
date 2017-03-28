package views.grid.model.cross.aggregator;

import java.io.Serializable;
import java.util.Calendar;

/**
 * @author: vagapova.m
 * @since: 01.10.2010
 */
public class QuarterAggregator extends GenericAggregator implements Serializable {

    private Calendar cal = Calendar.getInstance();


    public QuarterAggregator() {
    }


    public Object decodeValue(Object value) {
        if (value != null && value instanceof java.util.Date) {
            cal.setTimeInMillis(((java.util.Date) value).getTime());
            int month = cal.get(Calendar.MONTH);
            if (month <= 2)
                return new Double(1);
            else if (month <= 5)
                return new Double(2);
            else if (month <= 8)
                return new Double(3);
            else
                return new Double(4);
        }
        return value;
    }


    public final boolean equals(Object obj) {
        return obj.getClass() == QuarterAggregator.class;
    }


    public final int hashCode() {
        return QuarterAggregator.class.hashCode();
    }


}
