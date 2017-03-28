package core.reflection.rml;

import java.sql.SQLException;
import java.util.Vector;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import publicapi.ListBoxAPI;
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
import core.rml.ui.impl.ZListImpl;
import core.rml.ui.impl.ZScrollPaneImpl;
import core.rml.ui.interfaces.ZComponent;
import core.rml.ui.interfaces.ZList;
import core.rml.ui.interfaces.ZScrollPane;

public class LISTBOX  extends VisualRmlObject implements ListBoxAPI, Focusable, Handler, RetrieveableAPI, RmlContainerAPI {
    private class SL implements ListSelectionListener{

        @Override
        public void valueChanged(ListSelectionEvent e) {
            ((LISTITEM)list.getModel().getElementAt(list.getSelectedIndex())).doAction();
            doAction();
        }
        
    }
    
    private Container container = new Container(this); 
    
    private ZScrollPane panel = ZScrollPaneImpl.create();
    
    private ZList list = ZListImpl.create();
    private Vector<Object> listData = new Vector<Object>();
    
    private static final Logger       log          = Logger
                                                           .getLogger(COMBOBOX.class);

    private FocusPosition             fp           = new FocusPosition();

    private String                    targetColumn = null;

    private core.rml.dbi.Datastore             ds           = null;

    private String                    action       = null;

    public void init(Proper prop, Document doc) {

    	super.init(prop, doc);
    	
    	panel.getViewport().setView(list.getJComponent());
        list.addListSelectionListener(new SL());
        document = doc;

        targetColumn = (String) prop.get("TARGET");
        action = (String) prop.get("ACTION");

        list.setListData(listData);
    }

    public void focusThis() {
        panel.requestFocus();
    }

    public void addChild(RmlObject child) {
        if (child instanceof core.rml.dbi.Datastore) {
            ds = (core.rml.dbi.Datastore) child;
            ds.addHandler(this);
        }
        else if (child instanceof LISTITEM) {
            listData.add(child);
        }
    }

    public void fromDS() {
    }

    public int retrieve() {
        if (ds != null)
            try {
                ds.retrieve();
            }
            catch (Exception e) {
                log.error("!", e);
            }
            
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
        listData.clear();
        list.setListData(listData);
    }

    public void notifyHandler(Object o) {
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
        list.setListData(listData);
    }

    private void doAction() {
        if (action != null && !action.trim().equals("")) {
            try {
                document.executeScript(action, false);
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
            return getSelectedValue();
        }
        else if (method.equals("SETSELECTEDITEM")) {
            setSelectedIndex(((Double) arg).intValue());
        }
        else if (method.equals("GETITEM")) {
            return getItemAt(((Double)arg).intValue());
        }
        else if (method.equalsIgnoreCase("getItemCount")) {
            return new Double(getItemCount());
        }else{
        	return super.method(method, arg);
        }
        return new Nil();
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
		panel.setFocusable(focusable);
	}

	@Override
	public ZComponent getVisualComponent() {
		return panel;
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
		return new EmptyBorder(0,0,0,0);
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

    @Override
    public int getSelectedIndex() {
        return list.getSelectedIndex();
    }

    @Override
    public Object getSelectedValue() {
        return list.getSelectedValue();
    }

    @Override
    public void setSelectedItem(String item) {
        list.setSelectedValue(item);
    }

    @Override
    public int getItemCount() {
        return list.getModel().getSize();
    }

    @Override
    public Object getItemAt(int index) {
        return list.getModel().getElementAt(index);
    }

    @Override
    public void setSelectedIndex(int index) {
        list.setSelectedIndex(index);
        ((LISTITEM) list.getModel().getElementAt(list.getSelectedIndex())).doAction();
        doAction();

    }

    @Override
    public void setSelectedItem(Object item) {
        list.setSelectedValue(item);
    }

}
