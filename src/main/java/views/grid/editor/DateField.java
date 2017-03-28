package views.grid.editor;

import views.field.DateCalendar;
import views.grid.GridColumn;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.text.ParseException;

public class DateField extends CommonField {

    //ширина кнопки
    private final int BUTTON_WIDTH = 15;

    private JButton selectButton;

    private DateCalendar cld;

    public DateField(Object parent, GridColumn column, Object value, boolean isEditable) {

        super(parent, column, value, isEditable);

        selectButton = new JButton();
        selectButton.setText("...");
        selectButton.setPreferredSize(new Dimension(BUTTON_WIDTH, editField.getHeight()));
        selectButton.addActionListener(this);

        try {
            MaskFormatter mf = new MaskFormatter("##.##.####");
            mf.setPlaceholderCharacter('_');
            mf.setOverwriteMode(true);
            editField = new JFormattedTextField(mf);
            editField.addKeyListener(this);
            editField.addFocusListener(this);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addComponent(editField, 0,
                                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(selectButton, GroupLayout.PREFERRED_SIZE,
                                BUTTON_WIDTH, GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(editField, 0,
                                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(selectButton, 0,
                                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setValue(value);
    }

    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == selectButton) {
                cld = DateCalendar.getInstance(this);
            }
        } catch (Exception ex) {
            log.error("Shit happens", ex);
        }
    }

    @Override
    public boolean willLostFocus(FocusEvent e) {
        Component oComp = e.getOppositeComponent();
        if (oComp != null && (oComp.equals(cld) || oComp.equals(selectButton)))
            return false;
        else
            return super.willLostFocus(e);
    }
}