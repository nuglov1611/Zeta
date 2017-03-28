package views;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;

import publicapi.RadioGroupAPI;
import core.document.Document;
import core.parser.Proper;
import core.rml.Container;
import core.rml.RmlConstants;
import core.rml.RmlObject;
import core.rml.VisualRmlObject;
import core.rml.ui.impl.ZPanelImpl;
import core.rml.ui.interfaces.ZComponent;
import core.rml.ui.interfaces.ZPanel;
/**
 * Объединени RadioButton
 * @author uglov
 *
 */
/**
 * Визуальный компонент - объединение RadioButton. В один момент времени в группе может быть включена только одна кнопка.
 * @author 
 *
 */
public class RadioGroup extends VisualRmlObject implements  RadioGroupAPI {
    private static final Logger log = Logger.getLogger(RadioGroup.class);

	private Container container = new Container(this);
	
	private ZPanel btnPanel = ZPanelImpl.create();

    private static final int  GAP                 = 5;

    public final static int   DEFAULT_FONT_SIZE   = 12;

    private Vector<RadioButton>    buttons             = new Vector<RadioButton>();

    // выравнивать кнопки по горизонтали или по вертикали k
    private boolean           horisontalAlignment = false;
//TODO : Переделать на вызов через прокси!
    private ButtonGroup       buttonGroup;

    private int               fontSize;


    private void setButtonFont(RadioButton button, int fontSize) {
        Font currentFont = button.getFont();
        button.setFont(new Font(currentFont.getName(), currentFont.getStyle(),
                fontSize));
    }

    public void init(Proper prop, Document doc) {
    	super.init(prop, doc);
        String sp;
        Integer ip;

        buttonGroup = new ButtonGroup();

        sp = (String) prop.get(RmlConstants.ALIGNMENT);
        if (sp != null && sp.equals(RmlConstants.HORIZONTAL)) {
            horisontalAlignment = true;
        }
        else {
            horisontalAlignment = false;
        }

        ip = (Integer) prop.get(RmlConstants.FONT_SIZE);
        if (ip != null && ip > 0) {
            fontSize = ip;
        }
        else {
            fontSize = DEFAULT_FONT_SIZE;
        }
        
        try {
			container.addChildren(prop, doc);
		} catch (Exception e) {
			log.error("!", e);
		}
        
    }

    public void initChildren() {

        if (buttons.size() == 0) {
            return;
        }

        // пересчитываем размер пенели в зависимости от размеров и числа
        // компонентов
        int w = 0, h = 0;

        JFrame fix = new JFrame(); // фиктивный фрейм, нужен чтобы
        // getPreferredSize
        // вернул правильное значение
        fix.setLayout(new FlowLayout());
        for (int i = 0; i < buttons.size(); i++) {
            fix.add(((RadioButton) buttons.elementAt(i)).getVisualComponent().getJComponent());
        }
        fix.pack();
        for (int i = 0; i < buttons.size(); i++) {
            RadioButton button = (RadioButton) buttons.elementAt(i);
            int buttonFontSize = button.getFontSize();
            if (buttonFontSize != RadioButton.DEFAULT_FONT_SIZE) {
                setButtonFont(button, buttonFontSize);
            }
            else if (fontSize != DEFAULT_FONT_SIZE) {
                setButtonFont(button, fontSize);
            }
            Dimension d = button.getVisualComponent().getPreferredSize();
            if (horisontalAlignment) {
                w += d.width + GAP;
                if (h < d.height) {
                    h = d.height;
                }
            }
            else {
                if (w < d.width) {
                    w = d.width;
                }
                h += d.height + GAP;
            }
        }
        fix.removeAll();
        fix.dispose();

        btnPanel.setBounds(left, top, w, h);
        if (horisontalAlignment) {
//        	btnPanel.setLayout(new GridLayout(1, buttons.size()));
        	btnPanel.setLayout(new BoxLayout(btnPanel.getJComponent(), BoxLayout.X_AXIS));
        }
        else {
//        	btnPanel.setLayout(new GridLayout(buttons.size(), 1));
        	btnPanel.setLayout(new BoxLayout(btnPanel.getJComponent(), BoxLayout.Y_AXIS));
        }

        for (int i = 0; i < buttons.size(); i++) {
            RadioButton button = (RadioButton) buttons.elementAt(i);
            buttonGroup.add((AbstractButton) button.getVisualComponent().getJComponent());
            btnPanel.add(button.getVisualComponent());
        }
        for (int i = 0; i < buttons.size(); i++) {
            RadioButton button = (RadioButton) buttons.elementAt(i);
            if (button.isChecked()) {
                button.setSelected(true);
            }
            else {
                button.setSelected(false);
            }
        }
    }

    public Object getValue() {
        return this;
    }

    public Object getValueByName(String name) {
        return null;
    }

    public Object method(String method, Object arg) throws Exception {
        if (method.equals(RmlConstants.GET_VALUE)) {
            return getCurrentValue();
        }
        else if (method.equals(RmlConstants.SET_VALUE)) {
            final Vector args = (Vector) arg;
            final String checkValue = String.valueOf(args.get(0));
            final int buttonNumber = ((Double) args.get(1)).intValue();
            final boolean value = RmlConstants.ON_VALUE.equals(checkValue.toUpperCase());
            setSelected(buttonNumber, value);
            return value ? 1 : 0; 
//            RadioButton button = (RadioButton) (buttons.elementAt(buttonNumber.intValue()));
            
//            if (RmlConstants.ON_VALUE.equals(checkValue.toUpperCase())) {
//                button.setSelected(true);
//                return 1;
//            } else if (RmlConstants.OFF_VALUE.equals(checkValue.toUpperCase())) {
//                button.setSelected(false);
//                return 0;
//            } else {
//                throw new RTException("", "Unknown radiogroup value");
//            }
        }else
        	return super.method(method, arg);
    }

    
    /**
     * Включает кнопку 
     * @param buttonNumber номер кнопки в группе
     * @param selected значение true - включить false - выключить
     */
    public void setSelected(int buttonNumber, boolean selected) {
        buttons.elementAt(buttonNumber).setSelected(selected);
        
    }

    public int getSelected() {
        int i = -1;
        for(RadioButton b : buttons){
            if(b.isSelected())
                break;
            i++;
        }
        
        return i;
    }
    
    /**
     * Возвращает значение ассоциированное с текущей (включенной) кнопкой в группе
     * @return значение ассоциированное с текущей выбранной кнопкой
     */
    public Object getCurrentValue() {
        Object ret = null;
        for (int i = 0; i < buttons.size(); i++) {
            RadioButton button = (RadioButton) (buttons.elementAt(i));
            if (button.isSelected()) {
                ret = button.getValue();
            }
        }
        return ret;
    }

    public void setValue(Object value) {
    }

    public void setValueByName(String name, Object value) {
    }

    public String type() {
        return "views.RadioGroup";
    }

	@Override
	public void focusThis() {
		btnPanel.requestFocus();
	}

	@Override
	public ZComponent getVisualComponent() {
		return btnPanel;
	}

	@Override
	public void addChild(RmlObject child) {
        container.addChildToCollection(child);
	    
        if (child instanceof RadioButton) {
            buttons.addElement((RadioButton)child);
        }
	}

	@Override
	public RmlObject[] getChildren() {
		return container.getChildren();
	}

	@Override
	public Container getContainer() {
		return container;
	}

	@Override
	public boolean addChildrenAutomaticly() {
		return false;
	}

	@Override
	protected Border getDefaultBorder() {
		return new EmptyBorder(0,0,0,0);
	}
}
