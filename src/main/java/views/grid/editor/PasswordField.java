package views.grid.editor;

import views.grid.GridColumn;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class PasswordField extends CommonField {

    public PasswordField(Object parent, GridColumn column, Object value, boolean isEditable) {

        super(parent, column, value, isEditable);

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(editField);

        setValue(value);
    }

    public void actionPerformed(ActionEvent e) {

    }
}
