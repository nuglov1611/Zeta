package views.grid.editor;

import java.awt.event.ActionEvent;

import javax.swing.BoxLayout;

import views.grid.GridColumn;

public class TextField extends CommonField {


    public TextField(Object parent, GridColumn column, Object value, boolean isEditable) {

        super(parent, column, value, isEditable);

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(editField);

        setValue(value);
    }

    public void actionPerformed(ActionEvent e) {

    }
}