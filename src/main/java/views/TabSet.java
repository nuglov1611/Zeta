package views;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import publicapi.TabSetAPI;
import views.focuser.FocusPosition;
import views.focuser.Focusable;
import action.calc.Nil;
import core.connection.BadPasswordException;
import core.document.Document;
import core.document.Shortcutter;
import core.parser.Proper;
import core.rml.Container;
import core.rml.RmlObject;
import core.rml.VisualRmlObject;
import core.rml.dbi.exception.UpdateException;
import core.rml.ui.impl.ZTabbedPaneImpl;
import core.rml.ui.interfaces.ZComponent;
import core.rml.ui.interfaces.ZTabbedPane;

/**
 * ѕанель с закладками
 * @deprecated рекомендуетс€ использовать TabbedPane
 *
 */
public class TabSet extends VisualRmlObject  implements  Focusable, Shortcutter, TabSetAPI {

    private static final Logger log = Logger.getLogger(TabSet.class);
	
	private ZTabbedPane tabPane = ZTabbedPaneImpl.create();
	private Container container = new Container(this);
	private Hashtable<Integer, Report> reports = new Hashtable<Integer, Report>();
	
    private class KL extends KeyAdapter {

        public void keyPressed(KeyEvent e) {
            if (document.executeShortcut(e)) {
                return;
            }
        }

    }

    private class ChL implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            tabPane.requestFocus();
            int tab = tabPane.getSelectedIndex();
            if (tab != -1 && reports.get(tab) != null) {
                (reports.get(tab)).setUpdateFlag(true);
            }

        }

    }

    private class PCL implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals("CurrentTab")
                    && ((Integer) e.getOldValue()).intValue() != -1
                    && ((Integer) e.getOldValue()).intValue() != ((Integer) e
                            .getNewValue()).intValue() && tabSel != null) {
                try {
                    document.executeScript(tabSel, true);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * 
     */
    private FocusPosition     fp               = new FocusPosition();

    String                    font_face        = "Serif";

    int                       font_family      = 0;

    int                       font_size        = 12;

    public int                bg_color         = 0;

    boolean                   tabs_on_top      = true;

    String[]                  labels;

    String                      tabSel;
    
    public TabSet() {
        tabPane.setMinimumSize(new Dimension(0, 0));
        tabPane.addKeyListener(new KL());
        tabPane.getModel().addChangeListener(new ChL());
    }

    public void initChildren() {
    	final RmlObject[] objs = container.getChildren();
        try {
            for (int i = 0; i < objs.length; i++) {
            	if(objs[i] instanceof VisualRmlObject){
            		VisualRmlObject c = (VisualRmlObject) objs[i];
	                tabPane.addTab(labels[i], c.getVisualComponent().getJComponent());
	                if(c instanceof Report){
	                	reports.put(tabPane.getTabCount()-1, (Report) c);
	                }
            	}

            }
        }
        catch (Exception e) {
            System.out.println("~views.TabSet::addChildren() : " + e);
            e.printStackTrace();
        }
    }

    public void focusThis() {
    	tabPane.requestFocus();
    }

    public void fromDS() {
       container.fromDSAll();
    }

    public void init(Proper prop, Document doc) {
    	super.init(prop, doc);
    	
        String sp;
        if (prop != null) {
            sp = (String) prop.get("TABS_ON_TOP");
            if (sp != null) {
                if (sp.equals("YES")) {
                    tabs_on_top = true;
                }
                if (sp.equals("NO")) {
                    tabs_on_top = false;
                }
            }
            if (!tabs_on_top) {
            	tabPane.setTabPlacement(SwingConstants.BOTTOM);
            }
            sp = (String) prop.get("TABPLACEMENT");
            if (sp != null) {
                if (sp.equals("TOP")) {
                	tabPane.setTabPlacement(JTabbedPane.TOP);
                }else if (sp.equals("BOTTOM")) {
                	tabPane.setTabPlacement(JTabbedPane.BOTTOM);
                }else if (sp.equals("LEFT")) {
                	tabPane.setTabPlacement(JTabbedPane.LEFT);
                }else if (sp.equals("RIGTH")) {
                	tabPane.setTabPlacement(JTabbedPane.RIGHT);
                }
            }
            
            sp = (String) prop.get("TABLAYOUT");
            if (sp != null) {
                if (sp.equals("SCROLL")) {
                	tabPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
                }else if (sp.equals("WRAP")) {
                	tabPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
                }
            }
            
            sp = (String) prop.get("LABELS");
            if (sp != null) {
                labels = parseLabels(sp);
            }

            sp = (String) prop.get("SHORTCUT");
            if (sp != null) {
                try {
                    String[] ar = UTIL.parseDep(sp);
                    for (String element : ar) {
                        doc.addShortcut(element, this);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            sp = (String) prop.get("FIRSTFOCUS");
            if (sp != null && sp.toUpperCase().equals("YES")) {
            	tabPane.requestFocusInWindow();
            }

            tabSel = (String) prop.get("TABSELEXP");
            tabPane.addPropertyChangeListener(new PCL());

        }
        
        try {
			container.addChildren(prop, doc);
		} catch (Exception e) {
			log.error("!", e);
		}
    }

    public Object method(String method, Object arg) throws Exception {
        if (method.equals("SETCURRENTTAB")) {
            if (!(arg instanceof Double)) {
                return new Nil();
            }
            int cur = ((Double) arg).intValue();
            // setCurrentTab(cur);
            tabPane.setSelectedIndex(cur);
            System.out.println("method setCurentTab in views.TabSet called");
        }
        else if (method.equals("GETCURRENTTAB")){
            return new Double(tabPane.getSelectedIndex());
        }
        else {
           return super.method(method, arg);
        }
        return new Nil();
    }

    String[] parseLabels(String s) {
        StringTokenizer st = new StringTokenizer(s, ",");
        int count = st.countTokens();
        String[] work = new String[count];
        for (int i = 0; i < count; i++) {
            work[i] = st.nextToken();
        }
        return work;
    }

    public void processShortcut() {
    	tabPane.requestFocus();
    }

    public int retrieve() throws Exception {
    	container.retrieveAll();
        return 0;
    }

    public void toDS() {
    	container.toDSAll();
    }

    public void update() throws UpdateException, BadPasswordException, SQLException {
    	container.updateAll();
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
	public void addChild(RmlObject child) {
        container.addChildToCollection(child);
	}

	@Override
	public RmlObject[] getChildren() {
		return container.getChildren();
	}

	@Override
	public void setFocusable(boolean focusable) {
		tabPane.setFocusable(focusable);
	}

	@Override
	public ZComponent getVisualComponent() {
		return tabPane;
	}

	@Override
	public Container getContainer() {
		return container;
	}

	@Override
	public boolean addChildrenAutomaticly() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected Border getDefaultBorder() {
		return new EmptyBorder(0,0,0,0);
	}
}
