package views;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBox;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicBorders;

import org.apache.log4j.Logger;

import publicapi.CheckBoxAPI;
import views.focuser.FocusPosition;
import views.focuser.Focusable;
import action.api.RTException;
import core.document.Document;
import core.document.Shortcutter;
import core.parser.Proper;
import core.rml.RmlConstants;
import core.rml.VisualRmlObject;
import core.rml.ui.impl.ZCheckBoxImpl;
import core.rml.ui.interfaces.ZCheckBox;
import core.rml.ui.interfaces.ZComponent;

/**
 * Графический элемент CheckBox
 */
public class CheckBox extends VisualRmlObject implements Focusable, Shortcutter, CheckBoxAPI {

    public static final int DEFAULT_FONT_SIZE = 12;

    class AL implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            doAction();
        }
        
    }

    
    class KL extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            if (document.executeShortcut(e)) {
                e.consume();
            }
        }
    }

    /**
     * 
     */

    private static final Logger log              = Logger
                                                         .getLogger(CheckBox.class);

    private ZCheckBox chBox = ZCheckBoxImpl.create();
    
    private FocusPosition       focusPosition    = new FocusPosition();

    private String              onValue          = null;

    private String              offValue         = null;

    private Color               bgColor          = null;

    private String action = null;
    
    public CheckBox() {
        super();
        chBox.getJComponent().addKeyListener(new KL());
        ((JCheckBox)chBox.getJComponent()).addActionListener(new AL());
    }

    public Color getBgColor() {
        return bgColor;
    }

    public void focusThis() {
    	chBox.requestFocus();
    }

    public Object getValue() {
        return this;
    }

    public Object getValueByName(String name) {
        return null;
    }

    public void init(Proper prop, Document doc) {
    	super.init(prop, doc);
        String sp;
        onValue = (String) prop.get(RmlConstants.ON_VALUE);
        offValue = (String) prop.get(RmlConstants.OFF_VALUE);

        sp = (String) prop.get(RmlConstants.CHECK);
        if (sp != null) {
        	chBox.setSelected(sp.equals(RmlConstants.YES));
        }

        sp = (String) prop.get(RmlConstants.LABEL);
        if (sp != null) {
        	chBox.setText(sp);
        }

        sp = (String) prop.get(RmlConstants.SHORTCUT);
        if (sp != null) {
            try {
                String[] ar = UTIL.parseDep(sp);
                for (String element : ar) {
                    doc.addShortcut(element, this);
                }
            }
            catch (Exception e) {
                log.error("Shit happens!", e);
            }
        }

//        sp = (String) prop.get(RmlConstants.FIRST_FOCUS);
//        if (sp != null && sp.toUpperCase().equals(RmlConstants.YES)) {
//        	chBox.requestFocusInWindow();
//        }
//        
        action = (String) prop.get("ACTION");
    }

    public Object method(String method, Object arg) throws Exception {
        if (method.equals(RmlConstants.GET_VALUE)) {
            log.debug("state=" + isSelected());
            log.debug("onvalue=" + onValue);
            log.debug("offvalue=" + offValue);
            return getStateValue();
        }
        else if (method.equalsIgnoreCase(RmlConstants.IS_CHECKED)){
            return isSelected() ? 1 : 0;
        }
        else if (method.equals(RmlConstants.SET_VALUE)) {
            setState(arg);
            return null;
        }
        else {
            return super.method(method, arg);
        }
    }

    /**
     * Установить состояние компонента соответсвующее ассоциированному значению
     * @param value значение (onValue или offValue)
     * @throws RTException если value не является ни onValue ни offValue 
     */
    public void setState(Object value) throws RTException {
        if (value.equals(onValue)) {
        	chBox.setSelected(true);
        }
        else if (value.equals(offValue)) {
        	chBox.setSelected(false);
        }
        else {
            throw new RTException("", "Unknown checkbox value");
        }
    }

    /**
     * Устонавливает состояние CheckBox
     * @param selected - сосояние true - включен, false - выключен
     */
    public void setSelected(boolean selected) {
        chBox.setSelected(selected);
    }
    
    
    /**
     * @return сосояние CheckBox true - включен, false - выключен
     */
    public boolean isSelected() {
        return chBox.isSelected();
    }

    /**
     * @return ассоциированное с состоянием значение (onValue - если включен, щааМфдгу - если выключен )
     */
    public String getStateValue() {
        if (isSelected()) {
            return onValue;
        }
        else {
            return offValue;
        }
    }

    public void processShortcut() {
    	chBox.requestFocus();
    	chBox.setSelected(!isSelected());
    }

    public void setValue(Object value) {
    }

    public void setValueByName(String name, Object value) {
    }

    public String type() {
        return "CheckBox";
    }

    public boolean unfocusThis() {
        return true;
    }

    // javadoc inherited
    public int getFocusPosition() {
        return focusPosition.getFocusPosition();
    }

    // javadoc inherited
    public void setFocusPosition(int position) {
        focusPosition.setFocusPosition(position);
    }
    
    private void doAction(){
        if(action != null && !action.trim().equals("")){
            try {
            	document.doAction(action, null);
            }
            catch (Exception e) {
                log.error("!", e);
            }
        }
    }

	@Override
	public void setFocusable(boolean focusable) {
		chBox.setFocusable(focusable);
	}

	@Override
	public ZComponent getVisualComponent() {
		return chBox;
	}

	@Override
	protected Border getDefaultBorder() {
		return BasicBorders.getRadioButtonBorder();
	}

}
