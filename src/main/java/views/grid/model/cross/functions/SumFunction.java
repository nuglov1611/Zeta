package views.grid.model.cross.functions;

/**
 * Reports the summary of all values that match the row/column fields criteria.
 */
public class SumFunction extends GenericFunction {

    private Double sum;

    public SumFunction() {
    }

    public void processValue(Object value) {
        if (value != null && value instanceof Number) {
            if (sum == null)
                sum = new Double(((Number) value).doubleValue());
            else
                sum = new Double(sum.doubleValue() + ((Number) value).doubleValue());
        }
    }

    public Double getValue() {
        return sum;
    }


}
