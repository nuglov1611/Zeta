package views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicBorders;

import publicapi.ButtonAPI;
import views.focuser.FocusPosition;
import views.focuser.Focusable;
import action.api.RTException;
import core.document.Document;
import core.document.Shortcutter;
import core.parser.Proper;
import core.rml.RmlConstants;
import core.rml.VisualRmlObject;
import core.rml.ui.impl.ZButtonImpl;
import core.rml.ui.interfaces.ZButton;
import core.rml.ui.interfaces.ZComponent;


/**
 * Графический компонент "кнопка"
 * @author nick
 *
 */
public class Button extends VisualRmlObject implements Focusable, Shortcutter, ButtonAPI {
    class AL implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            doAction();
        }
    }

    class KL extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            if (document.executeShortcut(e)) {
                return;
            }

        }

    }

    private FocusPosition     fp               = new FocusPosition();

    private String                    aAction = null;

    private String                    action             = null;

    private String icon = null;
    

    ZButton button = ZButtonImpl.create();

	private boolean iconScaled = false;

    public void focusThis() {
    	button.requestFocus();
    }

    public Object getValue() {
        return this;
    }

    public Object getValueByName(String name) {
        return null;
    }

    public void init(Proper prop, Document doc) {
    	super.init(prop, doc);
        String s = (String) prop.get("LABEL");
        if (s != null) {
        	button.setText(s);
        }
        action = (String) prop.get("ACTION");
        aAction = (String) prop.get("AACTION");

        s = (String) prop.get("SHORTCUT");
        if (s != null) {
            try {
                String[] ar = UTIL.parseDep(s);
                for (String element : ar) {
                    document.addShortcut(element, this);
                }
            }
            catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }

        ((JButton)button.getJComponent()).addActionListener(new AL());
        button.getJComponent().addKeyListener(new KL());
        
        icon = (String) prop.get(RmlConstants.ICON);
        
        iconScaled = ((String) prop.get(RmlConstants.ICONSCALED, RmlConstants.NO)).equalsIgnoreCase(RmlConstants.YES);
        
        setIcon((String) prop.get("LABEL"));
        
    }

	private void setIcon(final String description) {
		if(icon != null){
//        	button.setAlignmentX(JComponent.CENTER_ALIGNMENT);
//        	button.setAlignmentY(JComponent.CENTER_ALIGNMENT);
        	
//        	ImageIcon im = createImageIcon(icon, description); 
//        	if(iconScaled)
//        		button.setIcon(new ImageIcon(getScaledImage(im.getImage(), width, height)));
//        	else
//        		button.setIcon(im);
        	
        	new SwingWorker<Void, Void>(){
        		ImageIcon im = null;
				@Override
				protected Void doInBackground() throws Exception {
					im = createImageIcon(icon, description);
		        	if(iconScaled)
		        		im = new ImageIcon(getScaledImage(im.getImage(), width, height));
					return null;
				}
				
				protected void done(){
					if(im != null){
						button.setIcon(im);
					}
				}
        	}.execute();
        }
	}

    public Object method(String method, Object arg) throws Exception {
        if (method.equals("SETCAPTION")) {
            if (arg instanceof String) {
                setCaption((String) arg);
                return null;
            }
            else {
                throw new RTException("ClassCastException",
                        "Wrong paramter of setEnabled number");
            }
        }
        else if (method.equals("DOACTION")) {
            doAction();
            return null;
        }
        else {
            return super.method(method, arg);
        }
    }

    /**
     * Задать текст надписи на кнопке
     * @param caption текст надписи
     */
    public void setCaption(String caption) {
        button.setText((String) caption);
    }

    /**
     * Выполнить действие ассоциированное с кнопкой
     */
    public void doAction(){
        if (action != null) {
            try {
                    document.executeScript(action, false);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (aAction != null) {
            try {
                document.doAction(aAction, null);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void processShortcut() {
        if (button.isEnabled()) {
            doAction();
        }
    }

    public void setValue(Object o) {
    }

    public void setValueByName(String name, Object o) {
    }

    public boolean unfocusThis() {
        return true;
    }

    @Override
    public int getFocusPosition() {
        return fp.getFocusPosition();
    }

    @Override
    public void setFocusPosition(int position) {
        fp.setFocusPosition(position);
    }

	@Override
	public ZComponent getVisualComponent() {
		return button;
	}

	@Override
	public void setFocusable(boolean focusable) {
		button.setFocusable(focusable);
	}

	@Override
	protected Border getDefaultBorder() {
		return BasicBorders.getButtonBorder();
	}
}