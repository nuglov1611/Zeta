package views.grid.model.cross.functions;

/**
 * @author: vagapova.m
 * @since: 18.10.2010
 */
public class MinFunction extends GenericFunction {

    private Double minValue;


    public MinFunction() {
    }


    public void processValue(Object value) {
        if (minValue == null) {
            if (value != null && value instanceof Number)
                minValue = new Double(((Number) value).doubleValue());
        } else if (value != null &&
                value instanceof Number &&
                ((Number) value).doubleValue() < minValue.doubleValue())
            minValue = new Double(((Number) value).doubleValue());
    }


    public Double getValue() {
        return minValue;
    }


}