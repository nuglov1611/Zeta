package core.reflection.rml;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicBorders;

import org.apache.log4j.Logger;

import publicapi.ComboBoxAPI;
import publicapi.RetrieveableAPI;
import publicapi.RmlContainerAPI;
import views.focuser.FocusPosition;
import views.focuser.Focusable;
import action.calc.Nil;
import core.connection.BadPasswordException;
import core.document.Document;
import core.parser.Proper;
import core.rml.Container;
import core.rml.RmlObject;
import core.rml.VisualRmlObject;
import core.rml.dbi.Handler;
import core.rml.ui.impl.ZComboBoxImpl;
import core.rml.ui.interfaces.ZComboBox;
import core.rml.ui.interfaces.ZComponent;

/**
 * Графический компонент "выпадающий список"
 * @author nick
 *
 */
public class COMBOBOX extends VisualRmlObject implements ComboBoxAPI, Focusable, Handler, RetrieveableAPI, RmlContainerAPI {
    private class AL implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
        	if(updating)
        		return;
        	
            ((LISTITEM)combo.getItemAt(combo.getSelectedIndex())).doAction();
            doAction();
        }
        
    }
    
    private static final Logger       log          = Logger
                                                           .getLogger(COMBOBOX.class);
    
    private Container container = new Container(this);

    private ZComboBox combo = ZComboBoxImpl.create();
    
    private FocusPosition             fp           = new FocusPosition();

    private String                    targetColumn = null;

    private core.rml.dbi.Datastore             ds           = null;

    private String                    action       = null;
    
    private boolean 				  updating     = true;

    public void init(Proper prop, Document doc) {
    	super.init(prop, doc);
        combo.addActionListener(new AL());

        targetColumn = (String) prop.get("TARGET");
        action = (String) prop.get("ACTION");

        updating = false;
    }

    public void focusThis() {
        combo.requestFocus();
    }

    public void addChild(RmlObject child) {
    	boolean oldUpd = updating;
    	updating = true;
        if (child instanceof core.rml.dbi.Datastore) {
            ds = (core.rml.dbi.Datastore) child;
            ds.addHandler(this);
        }
        else if (child instanceof LISTITEM) {
        	combo.addItem(child);
        }
    	updating = oldUpd;
    }

    public void fromDS() {
    }

    public int retrieve() throws Exception {
        if (ds != null)
        	return ds.retrieve();
        return 0;
    }

    public void toDS() {
    }

    public void update() throws BadPasswordException {
        if (ds != null)
            try {
                ds.update();
            }
            catch (SQLException e) {
                log.error("!", e);
            }
    }

    private void clearAll() {
        for (int i = 0; i < combo.getItemCount(); i++) {
            LISTITEM itm = (LISTITEM) combo.getItemAt(i);
            itm.removeFromDoc();
        }

        combo.removeAllItems();
    }

    public void notifyHandler(Object o) {
    	updating = true;
        if (targetColumn == null)
            return;

        int size = ds.getRowCount();
        clearAll();
        for (int i = 0; i < size; i++) {
            final Proper p = new Proper();
            p.put("LABEL", ds.getValue(i, targetColumn));
            LISTITEM itm = new LISTITEM();
            try {
                itm.init(p, document);
                addChild(itm);
            }
            catch (Exception e) {
                log.error("!!!", e);
            }
        }
        updating = false;
    }

    private void doAction() {
        if (action != null && !action.trim().equals("")) {
            try {
               document.doAction(action, null);
            }
            catch (Exception e) {
                log.error("", e);
            }
        }
    }

    public Object method(String method, Object arg) throws Exception {
        if (method.equals("ADDITEM")) {
            String i_alias = null;
            String i_label = null;
            String i_action = null;
            if(arg instanceof Vector){
                final Vector<String> v = (Vector<String>) arg;
                i_alias = v.elementAt(0);
                i_label = v.elementAt(1);
                i_action = v.elementAt(2);
            }else{
                i_alias = (String) arg;
            }
                   
            addItem(i_alias, i_label, i_action);
        }
        else if (method.equals("GETSELECTEDINDEX")) {
            return new Double(getSelectedIndex());
        }
        else if (method.equals("GETSELECTEDITEM")) {
            return getSelectedItem();
        }
        else if (method.equals("SETSELECTEDITEM")) {
        	if(arg instanceof Double){
        		setSelectedIndex(((Double)arg).intValue());
        	}else{
        		setSelectedItem(arg);
        		
        	}
        }
        else if (method.equals("GETITEM")) {
            return getItemAt(((Double) arg).intValue());
        }
        else if (method.equalsIgnoreCase("getItemCount")) {
            return new Double(getItemCount());
        }
        else if (method.equalsIgnoreCase("setValue")) {
        	String val = arg.toString();
        	setSelectedItem(val);
        }else{
            return super.method(method, arg);

        }
        
        return new Nil();
    }

    @Override
    public void setSelectedItem(String item_label) {
        LISTITEM cur_itm = null;
        for(int i=0; i<getItemCount(); i++){
        	final LISTITEM tmp_itm = (LISTITEM) combo.getModel().getElementAt(i);  
        	if(tmp_itm.toString().equals(item_label)){
        		cur_itm = tmp_itm;
        		break;
        	}
        }
        if(cur_itm != null){
        	combo.setSelectedItem(cur_itm);
        }
    }

    @Override
    public int getItemCount() {
        return combo.getModel().getSize();
    }

    @Override
    public Object getItemAt(int index) {
        return combo.getItemAt(index);
    }

    @Override
    public void setSelectedIndex(int index) {
        combo.setSelectedIndex(index);
        ((LISTITEM) getSelectedItem()).doAction();
        doAction();
    }

    @Override
    public void setSelectedItem(Object arg) {
        combo.setSelectedItem(arg);
        ((LISTITEM) getSelectedItem()).doAction();
        doAction();
    }

    
    @Override
    public Object getSelectedItem() {
        return combo.getItemAt(getSelectedIndex());
    }

    @Override
    public int getSelectedIndex() {
        return combo.getSelectedIndex();
    }

    @Override
    public void addItem(String item_alias, String item_label, String item_action) {
        Proper prop = new Proper();
        prop.put("ALIAS", item_alias);
        prop.put("LABEL", item_label);
        prop.put("ACTION", item_action);

        LISTITEM itm = new LISTITEM();
        itm.init(prop, document);
        addChild(itm);
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
	public void setFocusable(boolean focusable) {
		combo.setFocusable(focusable);
	}

	@Override
	public ZComponent getVisualComponent() {
		return combo;
	}

	@Override
	public RmlObject[] getChildren() {
		return container.getChildren();
	}

	@Override
	public void initChildren() {
	}

	@Override
	public Container getContainer() {
		return container;
	}

	@Override
	public boolean addChildrenAutomaticly() {
		return true;
	}

	@Override
	protected Border getDefaultBorder() {
		return BasicBorders.getTextFieldBorder();
	}

}
