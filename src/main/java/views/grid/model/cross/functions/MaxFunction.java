package views.grid.model.cross.functions;

/**
 * @author: vagapova.m
 * @since: 18.10.2010
 */
public class MaxFunction extends GenericFunction {

  private Double maxValue;


  public MaxFunction() {
  }


  public void processValue(Object value) {
    if (maxValue==null) {
      if (value!=null && value instanceof Number)
      maxValue = new Double( ((Number)value).doubleValue());
    }
    else if (value!=null &&
             value instanceof Number &&
             ((Number)value).doubleValue()>maxValue.doubleValue())
      maxValue = new Double( ((Number)value).doubleValue() );
  }


  public Double getValue() {
    return maxValue;
  }


}
