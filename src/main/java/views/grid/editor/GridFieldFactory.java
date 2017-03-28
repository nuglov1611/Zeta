package views.grid.editor;

import views.grid.GridColumn;
import views.grid.GridSwing;

public class GridFieldFactory {

    private static GridFieldFactory instance;

    private GridFieldFactory() {

    }

    public static GridFieldFactory getInstance() {
        if (instance == null) {
            instance = new GridFieldFactory();
        }
        return instance;
    }

    private boolean isFieldEditable(boolean isTableEditable, String columnEditable) {
        boolean isEditable = true;
        if (isTableEditable && "NO".equals(columnEditable)) {
            isEditable = false;
        } else if (!isTableEditable) {
            isEditable = false;
        }
        return isEditable;
    }

    private Object getEditableValue(GridColumn column, Object value) {
        Object editableValue;
        int type = GridSwing.getJType(column.getType());
        switch (type) {
            case 0:
//                String strVal = value.toString();
                String strVal;
                try {
                    strVal = column.valueToString(value);
                } catch (Exception e) {
                    strVal = value.toString();
                }
                if (strVal == null) {
                    strVal = "";
                } else {
                    strVal = strVal.trim();
                    strVal = strVal.replace(" ", "");
                }
                editableValue = strVal;
                break;
            default:
                editableValue = value;
        }
        return editableValue;
    }

    public CommonField createField(Object parent, GridColumn column, Object value, boolean isTableEditable) {

        boolean isPassword = column.isPassword();

        String columnEditable = column.getEditable();
        int type = GridSwing.getJType(column.getType());

        CommonField field;

        boolean isEditable = isFieldEditable(isTableEditable, columnEditable);

        value = getEditableValue(column, value);

        if ("HANDBOOK".equals(columnEditable)) {
            field = new HandbookField(parent, column, value, isEditable);
        } else {
            //0-Number;1-String;2-Data
            switch (type) {
                case 0:
                    field = new TextField(parent, column, value, isEditable);
                    break;
                case 1:
                    if (isPassword) {
                        field = new PasswordField(parent, column, value, isEditable);
                    } else {
                        field = new TextField(parent, column, value, isEditable);
                    }
                    break;
                case 2:
                    field = new DateField(parent, column, value, isEditable);
                    break;
                default:
                    field = null;
            }
        }
        return field;
    }
}
