package views;

import java.sql.SQLException;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import publicapi.TabbedPanelAPI;
import action.api.RTException;
import action.calc.Nil;
import core.connection.BadPasswordException;
import core.document.Document;
import core.parser.Proper;
import core.rml.Container;
import core.rml.RmlObject;
import core.rml.VisualRmlObject;
import core.rml.dbi.Datastore;
import core.rml.dbi.exception.UpdateException;
import core.rml.ui.impl.ZTabbedPaneImpl;
import core.rml.ui.interfaces.ZComponent;
import core.rml.ui.interfaces.ZTabbedPane;

/**
 * ѕанель с закладками 
 *
 */
public class TabbedPanel  extends VisualRmlObject implements  TabbedPanelAPI {

	private Container container = new Container(this);
	
    public ZComponent getVisualComponent() {
        return tabset;
    }

    class TSL implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            tabset.requestFocus();
            int tab = tabset.getSelectedIndex();
            ((Tab) tabs.elementAt(tab)).Select();
//            if (tab != -1 && reports.get(tab) != null) {
//                (reports.get(tab)).setUpdateFlag(true);
//            }

        }
    }
    private ZTabbedPane tabset = ZTabbedPaneImpl.create();
    private String tabpls = "TOP";
    private Vector<Tab> tabs = new Vector<Tab>();
    private Vector<Datastore> ds = new Vector<Datastore>();

    public void init(Proper prop, Document doc) {
        super.init(prop, doc);

        tabpls = (String) prop.get("TABPLACE", "TOP");

        tabset.addChangeListener(new TSL());
        if (tabpls.equalsIgnoreCase("TOP")) {
            tabset.setTabPlacement(JTabbedPane.TOP);
        } else if (tabpls.equalsIgnoreCase("BOTTOM")) {
            tabset.setTabPlacement(JTabbedPane.BOTTOM);
        } else if (tabpls.equalsIgnoreCase("LEFT")) {
            tabset.setTabPlacement(JTabbedPane.LEFT);
        } else if (tabpls.equalsIgnoreCase("RIGHT")) {
            tabset.setTabPlacement(JTabbedPane.RIGHT);
        }

        if (prop.get("FIRSTFOCUS", "NO").equals("YES")) {
            tabset.requestFocus();
        }

    }

    public Object method(String method, Object arg) throws Exception {
        if (method.equals("SETCURRENTTAB")) {
            if (!(arg instanceof Double)) {
                return new Nil();
            }
            int cur = ((Double) arg).intValue();
            setCurrentTab(cur);
        } else if (method.equals("GETCURRENTTAB")) {
            return new Double(getCurrentTab());
        } else {
            try {
                return super.method(method, arg);
            } catch (RTException e) {
                throw new RTException("HasNotMethod", "method " + method + " not defined in class views.TabSet!");
            }
        }
        return new Nil();
    }

    /**
     * ¬озвращает номер закладки, активной в данный момент времени.
     * @return
     */
    public int getCurrentTab() {
        return tabset.getSelectedIndex();
    }

    /**
     * ќткрыть (сделать текущей) закладку 
     * @param tabNumber - номер закладки (нумераци€ с 0)
     */
    public void setCurrentTab(int tabNumber) {
        tabset.setSelectedIndex(tabNumber);
    }

    public void focusThis() {
    }

    public void addChild(RmlObject child) {
        container.addChildToCollection(child);
        
        if (child instanceof Tab) {
            tabs.addElement((Tab)child);
            ImageIcon icon = null;
        	icon = createImageIcon(((Tab) child).getIcon(), ((Tab) child).getLabel());
        	if(icon == null)
        		tabset.addTab(((Tab) child).getLabel(), ((Tab) child).getVisualComponent().getJComponent());
        	else
        		tabset.addTab(((Tab) child).getLabel(), icon, ((Tab) child).getVisualComponent().getJComponent());
        } else if (child instanceof Datastore) {
            ds.addElement((Datastore) child);
        }

    }

    public RmlObject[] getChildren(){
//        RmlObject[] objs = new RmlObject[tabs.size()];
//        tabs.copyInto(objs);
//        return objs;
    	return container.getChildren();
    }

	@Override
	protected Border getDefaultBorder() {
		return new EmptyBorder(0,0,0,0);
	}

	@Override
	public boolean addChildrenAutomaticly() {
		return true;
	}

	@Override
	public Container getContainer() {
		return container;
	}

	@Override
	public void initChildren() throws Exception {
		// TODO Auto-generated method stub
	}
	@Override
	public void fromDS() {
		container.fromDSAll();
	}

	@Override
	public int retrieve() throws Exception {
    	container.retrieveAll();
        return 0;
	}

	@Override
	public void toDS() {
		container.toDSAll();
	}

	@Override
	public void update() throws UpdateException, BadPasswordException,
			SQLException {
		container.updateAll();
	}
}