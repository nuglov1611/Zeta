package views.grid.model.cross.functions;

/**
 * Reports the count of processed non null object values
 */
public class GenericFunction{

  protected Double count;

  public GenericFunction() {
  }

  public void processValue(Object value) {
    if (value!=null) {
      if (count==null)
        count = new Double(1);
      else
        count = new Double(count.doubleValue()+1);
    }
  }

  public Double getValue() {
    return count;
  }

  public boolean equals(Object obj) {
    return obj.getClass().getName().equals(this.getClass().getName());
  }

  public int hashCode() {
    return this.getClass().getName().hashCode();
  }


}
