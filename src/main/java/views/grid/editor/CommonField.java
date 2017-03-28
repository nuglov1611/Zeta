package views.grid.editor;

import core.document.NotifyInterface;
import core.document.ObjectNotifyInterface;
import core.document.Shortcutter;
import core.reflection.objects.VALIDATOR;
import core.rml.RmlConstants;
import loader.ZetaProperties;
import loader.ZetaUtility;
import org.apache.log4j.Logger;
import views.field.BaseField;
import views.focuser.FocusPosition;
import views.focuser.Focusable;
import views.grid.GridColumn;
import views.grid.GridSwing;

import javax.swing.FocusManager;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;

abstract public class CommonField extends JPanel implements BaseField, ActionListener, NotifyInterface, Shortcutter, Focusable, KeyListener, FocusListener {

    protected static final Logger log = Logger.getLogger(CommonField.class);
    protected boolean isComputed = false;

    protected boolean isPassword = false;

    //������ ���� ����� � ������������, �������� �� ������������� ����
    protected GridColumn editColumn;

    protected Object lastValidValue;

    //Grid - �������� ������� ����
    protected Object parent;

    private boolean isEditing = false;

    protected JTextComponent editField;
    private FocusPosition fp = new FocusPosition();


    protected CommonField(Object parent, GridColumn column, Object value, boolean isEditable) {

        this.parent = parent;

        editColumn = column;

        editField = new JTextField();
        isEditing = false;

        editField.setEditable(isEditable);

        if ("LEFT".equals(editColumn.getStringProperty(RmlConstants.HALIGNMENT).toUpperCase())) {
            ((JTextField) editField).setHorizontalAlignment(JTextField.LEFT);
        } else if ("CENTER".equals(editColumn.getStringProperty(RmlConstants.HALIGNMENT).toUpperCase())) {
            ((JTextField) editField).setHorizontalAlignment(JTextField.CENTER);
        } else if ("RIGHT".equals(editColumn.getStringProperty(RmlConstants.HALIGNMENT).toUpperCase())) {
            ((JTextField) editField).setHorizontalAlignment(JTextField.RIGHT);
        }

        Font font = editColumn.getFontProperty(RmlConstants.FONT);
        if (font != null) {
            setFont(font);
        }


        editField.addKeyListener(this);


        editField.addFocusListener(this);

        addFocusListener(this);

//        Color fontColor = editColumn.getFont_color();
//        if (fontColor != null) {
//            setForeground(fontColor);
//        }
//        Color bgColor = editColumn.getBg_color();
//        if (bgColor != null) {
//            setBackground(bgColor);
//        }
    }

    public int getType() {
        return editColumn.getType();
    }

    public String getText() {
        return editField.getText();
    }

    public void setText(String text) {
        editField.setText(text);
    }

    public boolean isEditable() {
        return editField.isEditable();
    }

    protected boolean selectUndo() {
        Toolkit.getDefaultToolkit().beep();
        editField.selectAll();
        Object[] options = {ZetaUtility.pr(ZetaProperties.MSG_BADEDITBUTTONEDIT),
                ZetaUtility.pr(ZetaProperties.MSG_BADEDITBUTTONUNDO)};
        String mustBeType;
        switch (GridSwing.getJType(editColumn.getType())) {
            case 0:
                mustBeType = "Numeric";
                break;
            case 1:
                mustBeType = "String";
                break;
            case 2:
                mustBeType = "Data";
                break;
            default:
                mustBeType = "Unknown";
        }
        int answer = JOptionPane.showOptionDialog(
                SwingUtilities.getWindowAncestor(editField),
                ZetaUtility.pr(ZetaProperties.MSG_BADEDITVALUEPREFIX) + " " + mustBeType + " " +
                        ZetaUtility.pr(ZetaProperties.MSG_BADEDITVALUEPOSTFIX) + "\n" +
                        ZetaUtility.pr(ZetaProperties.MSG_BADEDITVALUE),
                ZetaUtility.pr(ZetaProperties.MSG_BADEDITVALUEHEADER),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null,
                options,
                options[1]);

        return answer == 1;
    }

    public boolean isValid(Object value) {
        boolean isValueValid;
        try {
            editColumn.valueToString(value);
            isValueValid = true;
        } catch (Exception e) {
            isValueValid = false;
        }
        return isValueValid;
    }

    public void processShortcut() {
        if (parent instanceof GridSwing && ((GridSwing) parent).getDoc() != null) {
            focusThis();
        }
        if ("HANDBOOK".equals(editColumn.getEditable())) {
            actionPerformed(new ActionEvent(editField, 911, "HANDBOOK"));
        }
    }

    public void focusThis() {
        editField.requestFocus();
    }

    public Object getValue() {
        Object value = null;
        try {
            if (lastValidValue != null && !"".equals(lastValidValue)) {
                if (lastValidValue instanceof String) {
                    value = editColumn.valueToObject((String) lastValidValue);
                } else {
                    value = lastValidValue;
                }
            }
//            //�������� �� ����������, �������� ��� �� ��������� ������� ����
//            else {
//               switch (GridSwing.getJType(editColumn.getType())) {
//                   case 0:
//                       value = 0.0;
//                       break;
//                   case 1:
//                       value = "";
//                       break;
//                   case 2:
//                       value = new Date();
//                       break;
//                   default:
//                       value = null;
//               }
//            }
        } catch (Exception e) {
            log.error("Shit happens", e);
        }
        return value;
    }

    public void setValue(Object obj) {
        try {
            String strValue;
            if (obj instanceof String) {
                strValue = (String) obj;
            } else {
                strValue = editColumn.valueToString(obj);
            }
            lastValidValue = strValue;
            editField.setText(strValue);
        } catch (Exception e) {
            log.error("Shit happens", e);
        }
    }

    public void notifyIt() {
        // ���� ���������������� ���� editExp � ����� ������, ����� �������������
        // ��������� �� � ������ �������, � �� �����.
//        DATASTORE ds2 = (DATASTORE) editColumn.getAliases().get(AliasesKeys.STORE);
//        log.debug("Notify it in DateField called");
//        log.debug("ds2=" + ds2);
//        if (ds2 != null) {
//            if (editExp != null) {
//                calcHandbookExp();
//                if (depends != null) {
//                    calcHandbookDep();
//                    calcDep();
//                }
//            }
//        }

        if (parent instanceof ObjectNotifyInterface) {
            ((ObjectNotifyInterface) parent).notifyIt(this);
        } else if (parent instanceof NotifyInterface) {
            ((NotifyInterface) parent).notifyIt();
        }
    }

    public void keyPressed(KeyEvent e) {
        if (parent instanceof GridSwing) {
            GridSwing grid = (GridSwing) parent;
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                grid.getActionManager().processEnterAction();
            }
        } else {
            super.processKeyEvent(e);
        }

    }

    public void keyReleased(KeyEvent e) {
        if (VALIDATOR.NUMERIC_TYPE == editColumn.getValidator().getType() &&
                e.getKeyChar() == ',') {
            String text = editField.getText();
            editField.setText(text.replace(',', '.'));
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }


    synchronized public void startEditing() {
//        if (isEditing) 
//            return;

        isEditing = true;
        if (!FocusManager.getCurrentManager().getFocusOwner().equals(editField))
            editField.requestFocus();
        editField.setCaretPosition(editField.getText().length());
        editField.selectAll();
    }

    synchronized public void startEditing(KeyEvent e) {
        isEditing = true;
        if (!e.isActionKey() &&
                e.getKeyCode() != KeyEvent.VK_DELETE &&
                e.getKeyCode() != KeyEvent.VK_INSERT)
            editField.setText("" + e.getKeyChar());
        else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            editField.setText("");
        }

        if (!FocusManager.getCurrentManager().getFocusOwner().equals(editField))
            editField.requestFocus();
//        e.setSource(editField);
//        KeyListener[] kl = editField.getKeyListeners();
//        for(int i=0; i<kl.length; i++){
//        	kl[i].keyPressed(e);
//        }
    }


    public void focusGained(FocusEvent e) {
        if (e.getComponent().equals(this)) {
            editField.requestFocus();
        }
        if (editField.isEditable()) {
            if (!isEditing)
                startEditing();
        }
    }

    public void focusLost(FocusEvent e) {
//        Component comp = e.getOppositeComponent();
//        if(comp != null && !comp.equals(editField) && !comp.equals(this)){
        if (willLostFocus(e)) {
            if (((GridSwing) parent).isEditing()) {
                ((GridSwing) parent).stopEditing();
            }
            editField.select(0, 0);
        }
    }

    public boolean willLostFocus(FocusEvent e) {
        Component oComp = e.getOppositeComponent();
        return !(oComp != null && (oComp.equals(editField) || oComp.equals(this)));
    }

    public void stopEditing() {
        isEditing = false;
    }

    @Override
    public int getFocusPosition() {
        return fp.getFocusPosition();
    }

    @Override
    public void setFocusPosition(int position) {
        fp.setFocusPosition(position);
    }

    public void finishTheEditing() {
        if (((GridSwing) parent).isEditing()) {
            ((GridSwing) parent).stopEditing();
        }
        editField.select(0, 0);
    }

    public void setTextOnly(Object o) {
        try {
            editField.setText(editColumn.valueToString(o));
        } catch (Exception e) {
            log.error("!", e);
        }
    }
}