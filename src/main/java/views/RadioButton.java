package views;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicBorders;

import publicapi.RadioButtonAPI;
import views.focuser.FocusPosition;
import views.focuser.Focusable;
import action.api.RTException;
import core.document.Document;
import core.document.Shortcutter;
import core.parser.Proper;
import core.rml.RmlConstants;
import core.rml.VisualRmlObject;
import core.rml.ui.impl.ZRadioButtonImpl;
import core.rml.ui.interfaces.ZCheckBox;
import core.rml.ui.interfaces.ZComponent;

/**
 * 
 */
public class RadioButton extends VisualRmlObject implements Focusable, Shortcutter, RadioButtonAPI {

    class KL extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (document.executeShortcut(e)) {
                e.consume();
            }
        }
    }


	private ZCheckBox button = ZRadioButtonImpl.create(); 
    
    public final static int   DEFAULT_FONT_SIZE = 12;

    private FocusPosition     focusPosition     = new FocusPosition();

    private String            value             = null;

    private boolean           isChecked         = false;


    public RadioButton() {
        button.addKeyListener(new KL());
    }

    public void focusThis() {
    	button.requestFocus();
    }

    @Override
    public Object getValue() {
        return value;
    }

    public int getFontSize() {
        return button.getFont().getSize();
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void init(Proper prop, Document doc) {
    	super.init(prop, doc);
        value = (String) prop.get(RmlConstants.VALUE);

        String sp = (String) prop.get(RmlConstants.CHECK);
        if (sp != null && sp.equals(RmlConstants.YES)) {
            isChecked = true;
        }
        else {
            isChecked = false;
        }

        sp = (String) prop.get(RmlConstants.LABEL);
        if (sp != null) {
            button.setText(sp);
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
                e.printStackTrace();
                System.out.println(e);
            }
        }

        sp = (String) prop.get(RmlConstants.FIRSTFOCUS);
        if (sp != null && sp.toUpperCase().equals(RmlConstants.YES)) {
            button.requestFocusInWindow();
        }
    }

    // javadoc inherited
    public void processShortcut() {
        button.requestFocus();
        if (!button.isSelected()) {
        	setSelected(true);
        }
    }

    // javadoc inherited
    public int getFocusPosition() {
        return focusPosition.getFocusPosition();
    }

    // javadoc inherited
    public void setFocusPosition(int position) {
        focusPosition.setFocusPosition(position);
    }

    public Object method(String method, Object arg) throws Exception {        
        if (method.equals(RmlConstants.SET_VALUE)) {
            String checkValue = String.valueOf(arg);
            if (RmlConstants.ON_VALUE.equals(checkValue.toUpperCase())) {
            	setSelected(true);
                return 1;
            } else if (RmlConstants.OFF_VALUE.equals(checkValue.toUpperCase())) {
            	setSelected(false);
                return 0;
            } else {
                throw new RTException("", "Unknown radiobutton value");
            }
        }else
        	return super.method(method, arg);
    }

	@Override
	public void setFocusable(boolean focusable) {
		button.setFocusable(focusable);
	}

	@Override
	public ZComponent getVisualComponent() {
		return button;
	}

	@Override
	protected Border getDefaultBorder() {
		return BasicBorders.getRadioButtonBorder();
	}

	/**
	 * Возвращает состояние кнопки (включена/выключена)
	 * @return true - включена, false - выключена 
	 */
	public boolean isSelected() {
		return button.isSelected();
	}

	/**
	 * Задает состояние кнопки (включена/выключена)
	 * @param selected если true - включена, false - выключена 
	 */
	public void setSelected(boolean selected) {
		button.setSelected(selected);
	}

}
