package views.grid.model.cross.functions;

/**
 * @author: vagapova.m
 * @since: 18.10.2010
 */
public class AverageFunction extends GenericFunction {

  private Double sum;


  public AverageFunction() {
  }

  public void processValue(Object value) {
    if (value!=null && value instanceof Number) {
      super.processValue(value);
      if (sum==null)
        sum = ((Number) value).doubleValue();
      else
        sum = sum + ((Number) value).doubleValue();
    }
  }


  public Double getValue() {
    if (sum!=null && super.getValue()!=null)
      return sum / super.getValue();
    return null;
  }


}
