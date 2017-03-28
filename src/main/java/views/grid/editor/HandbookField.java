package views.grid.editor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;

import javax.swing.GroupLayout;
import javax.swing.JButton;

import views.grid.GridColumn;
import views.grid.GridSwing;

public class HandbookField extends CommonField {

    //ширина кнопки
    private final int BUTTON_WIDTH = 15;

    private JButton selectButton;

    public HandbookField(Object parent, GridColumn column, Object value, boolean isEditable) {
        //редактируется только по нажатию кнопки - выбором из списка
        super(parent, column, value, false);

        selectButton = new JButton();
        selectButton.setText("...");
        selectButton.setPreferredSize(new Dimension(BUTTON_WIDTH, editField.getHeight()));
        selectButton.addActionListener(this);

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
            if (e.getSource() == selectButton && editColumn.getEdit() != null) {
                ((GridSwing) parent).getDoc()
                        .doAction(editColumn.getEdit(), this);
            }
        } catch (Exception ex) {
            log.error("Shit happens", ex);
        }
    }

    @Override
    public boolean willLostFocus(FocusEvent e) {
        Component oComp = e.getOppositeComponent();
        if(oComp != null && oComp.equals(selectButton))
            return false;
        else
            return super.willLostFocus(e);
    }

}