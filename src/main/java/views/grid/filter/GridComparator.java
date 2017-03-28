package views.grid.filter;

import org.apache.log4j.Logger;
import views.grid.GridColumn;

import java.util.Comparator;
import java.util.Date;

public class GridComparator implements Comparator {

    private static final Logger log = Logger.getLogger(GridComparator.class);

    private GridColumn column;

    public GridComparator(GridColumn column) {
        this.column = column;
    }

    private int compareObjects(Object o1, Object o2) {
        int result = 0;
        if (o1 instanceof String && o2 instanceof String) {
            String s1 = (String) o1;
            String s2 = (String) o2;
            result = s1.compareTo(s2);
        } else if (o1 instanceof Double && o2 instanceof Double) {
            Double d1 = (Double) o1;
            Double d2 = (Double) o2;
            result = d1 < d2 ? -1 : (d1 > d2 ? 1 : 0);
        } else if (o1 instanceof Date && o2 instanceof Date) {
            Date d1 = (Date) o1;
            Date d2 = (Date) o2;
            result = d1.compareTo(d2);
        } else {
            throw new ClassCastException();
        }
        return result;
    }

    public int compare(Object o1, Object o2) {
        int result = 0;
        try {
            if (o1 instanceof String && o2 instanceof String) {
                if ("".equals(o1)) {
                    if ("".equals(o2)) {
                        result = 0;
                    } else {
                        result = -1;
                    }
                } else if ("".equals(o2)) {
                    if ("".equals(o1)) {
                        result = 0;
                    } else {
                        result = 1;
                    }
                } else {
                    o1 = column.valueToObject((String) o1);
                    o2 = column.valueToObject((String) o2);
                    result = compareObjects(o1, o2);
                }
            } else {
                result = compareObjects(o1, o2);
            }
        } catch (Exception e) {
            log.error("Shit happens", e);
        }
        return result;
    }
}