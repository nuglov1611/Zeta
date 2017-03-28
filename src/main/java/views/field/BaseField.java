package views.field;

import java.awt.*;

public interface BaseField {

    void requestFocus();
//	protected JPanel fieldPanel = new JPanel();
//	
//    protected JTextComponent editField;
//    private FocusPosition                    fp               = new FocusPosition();
//
//    
//    @Override
//    public Object getValueByName(String name) throws Exception {
//        return null;
//    }
//
//    public void setValueByName(String name, Object obj) throws Exception {
//    }
//
//    @Override
//    public void focusThis() {
//        editField.requestFocus();
//    }
//
//    @Override
//    public int getFocusPosition() {
//        return fp.getFocusPosition();
//    }
//
//    @Override
//    public void setFocusPosition(int position) {
//        fp.setFocusPosition(position);
//    }
//
//    @Override
//    public void keyTyped(KeyEvent e) {
//    }
//
//    public abstract void setValue(Object obj);
//
//    public boolean isEditable() {
//        return false;
//    }
//
//    public abstract Object getValue();
//
//    @Override
//    public void keyReleased(KeyEvent e) {
//    }
//
//    @Override
//    public void mouseClicked(MouseEvent e) {
//    }
//
//    @Override
//    public void mouseEntered(MouseEvent e) {
//    }
//
//    @Override
//    public void mouseExited(MouseEvent e) {
//    }
//
//    @Override
//    public void mouseReleased(MouseEvent e) {
//    }
//    @Override
//    public void mousePressed(MouseEvent e) {
//    }
//
//    public JComponent getVisualComponent(){
//    	return fieldPanel;
//    }

    Object getValue();

    Rectangle getBounds();

    Point getLocationOnScreen();

    boolean isEditable();

    void setValue(Object val);

    void finishTheEditing();

    void setTextOnly(Object time);
}
