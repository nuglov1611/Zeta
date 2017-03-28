package views.grid.model.cross.converter;

/**
 * @author: vagapova.m
 * @since: 01.10.2010
 */
public class StringToDoubleConverter extends DataConverter {

  /**
   * @param value data to convert
   * @return converted value
   */
  public final Object decodeValue(Object value) throws Exception {
    if (value==null)
      return null;
    try {
      return new Double(value.toString());
    }
    catch (NumberFormatException ex) {
      throw new Exception(ex);
    }
  }


}
