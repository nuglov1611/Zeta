package views.grid.model.cross.functions;

/**
 * @author mmylnikova
 * @since 12/6/12
 */
public class DisplayFunction extends GenericFunction {
    private String displayValue = "";


    public DisplayFunction() {
    }

    public void processValue(Object value) {

        super.processValue(value);
        if (count >= 1 && value != null) {
            displayValue = value.toString();
        }
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
