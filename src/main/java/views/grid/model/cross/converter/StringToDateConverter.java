package views.grid.model.cross.converter;

import java.text.SimpleDateFormat;

/**
 * @author: vagapova.m
 * @since: 01.10.2010
 */
public class StringToDateConverter extends DataConverter {

  /** SimpleDateFormat converter */
  private SimpleDateFormat sdf = null;


  public StringToDateConverter(SimpleDateFormat sdf) {
    this.sdf = sdf;
  }


  /**
   * @param value data to convert
   * @return converted value
   */
  public final Object decodeValue(Object value) throws Exception {
    if (value==null)
      return null;
    try {
      return sdf.parse(value.toString());
    }
    catch (Exception ex) {
      throw new Exception(ex);
    }
  }


}

