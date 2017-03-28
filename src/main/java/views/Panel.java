package views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import loader.ZetaProperties;

import org.apache.log4j.Logger;

import publicapi.PanelAPI;
import action.api.RTException;
import core.document.Document;
import core.parser.Proper;
import core.rml.Container;
import core.rml.LayoutMng;
import core.rml.RmlObject;
import core.rml.VisualRmlObject;
import core.rml.dbi.Datastore;
import core.rml.ui.impl.ZPanelImpl;
import core.rml.ui.interfaces.ZComponent;
import core.rml.ui.interfaces.ZPanel;

/**
* Графический компонент "Панель"
* 
*/
public class Panel extends VisualRmlObject implements PanelAPI {
    private static final Logger       log         = Logger
                                                          .getLogger(Panel.class);
    
    private ZPanel panel = ZPanelImpl.create();
    
    private Container container = new Container(this);

    private Vector<Field>             fields      = new Vector<Field>();         // дети-Fields(и только они!)

    private String                    font_face   = "Serif";                     // имя фонта

    private int                       font_family = 0;                           // стиль фонта(Plain,Bold,Italic)

    private int                       font_size   = 12;

    private String                    colontitul;

    private Datastore                 ds;

    private views.Menu                menu;

    private ActionListener            popupAL;

    int                               menuKey     = 93;

    
    
    public Panel() {
//        panel.setLayout(null);
        panel.setFont(new Font(font_face, font_family, font_size));
        panel.addMouseListener(new ML());
        panel.addComponentListener(new CompL());
        panel.setMinimumSize(new Dimension(0, 0));
    }

    
    class CompL extends ComponentAdapter {
        public void componentResized(ComponentEvent e) {
        	panel.revalidate();
        	panel.repaint();
        }
    }

    class ML extends MouseAdapter {

        public void mouseClicked(MouseEvent e) {
            if (e.isPopupTrigger() && menu != null) {
                menu.show(panel.getJComponent(), e.getX(), e.getY());
            }
            e.consume();
        }
    }

    public Datastore getDatastore() {
        return ds;
    }

    public void initChildren() {
    	final RmlObject[] objs = container.getChildren();
    	
        if (ds == null) { // если датасторе не указано, создаем фиктивное
            ds = new core.rml.dbi.Datastore(document);
            ds.setSql("select user from dual");
        }
        
        for (RmlObject o : objs) {
            if (o instanceof Field) {

                // Если target=null, значит это вычисляемое поле,
                // не связанное с базой данных.Но соответствующий
                // столбец в Datastore обязан быть.
                if (((Field) o).gettarget() == null) {
                    int type = ((Field) o).getType();
                    if (type == Integer.MIN_VALUE) {
                        log.debug(" type for computed field not defined!");
                        continue;
                    }
                    ((Field) o).settarget(ds.addColumn(type));
                }
                addField((Field) o);
//            }else if (o instanceof Label) {
//            	LayoutMng.add(panel, (Label) o);
//                panel.add(((views.Label) o).getVisualComponent());
//            }else if (o instanceof Datastore) {
//                setDATASTORE((Datastore) o);
            }else if (o instanceof views.Menu) {
                popupAL = new PopupAL();
                menu = (views.Menu) o;
                if (menu == null) {
                    log.debug("popmenu=null");
                    continue;
                }
                menu.addActionListenerRecursiv(popupAL);
            }else if (o instanceof VisualRmlObject)
            	LayoutMng.add(panel, (VisualRmlObject) o);
//                panel.add(((VisualRmlObject) o).getVisualComponent());

        }
        panel.revalidate();
    }

    public int retrieve() {
    	int ret = 0;
        if (ds == null) {
            log.debug("ds = null");
            return 0;
        }

        try {
            ret = ds.retrieve();
            fromDS();
        }
        catch (Exception e) {
            log.error("Catch exception from DATASTORE.retrieve() : ", e);
        }
        return ret;
    }

    public void update() {
        toDS();
        try {
            ds.update();
        }
        catch (SQLException e) {
            log.error("Shit happens", e);
        }
    }

    public void fromDS() {
        if (ZetaProperties.views_debug > 0) {
            log.debug("fromDS in form called !");
        }

        for (int i = 0; i < fields.size(); i++) {
            views.Field f = fields.elementAt(i);
            if (f == null) {
                continue;
            }
            String col = f.gettarget();
            if (col == null) {
                if (ZetaProperties.views_debug > 0) {
                    log.debug("target not defined for field " + f);
                }
                continue;
            }
            if (col.indexOf(Datastore.compute) == 0) { //значит, target для этого филда начинается c @@COMPUTE
                try {
                    f.setValue(f.textToObject(f.gettext()));
                }
                catch (Exception e) {
                    log.error("Shit happens", e);
                    f.settext(null);
                }
                continue;
            }
            if (ZetaProperties.views_debug > 0) {
                log.debug("Field.retrieve...colName=" + col);
            }

            Object value = null;
            try {
                value = ds.getValue(0, col);
            }
            catch (Exception e) {
                log.error("Shit happens", e);
            }
            int type = 0;
            try {
                type = ds.getType(col);
            }
            catch (Exception e) {
                log.error("Cannot get type!", e);
            }

            if (ZetaProperties.views_debug > 0) {
                log.debug("value=" + value);
            }

            f.setType(type);
            if (ZetaProperties.views_debug > 0) {
                log.debug("Type =" + type);
            }
            f.setValue(value);
        }
        for (int i = 0; i < fields.size(); i++) {
            views.Field f = fields.elementAt(i);
            if (f != null) {
                f.calcDep();
            }
        }
    }

    public void toDS() {
        if (ZetaProperties.views_debug > 0) {
            log.debug("views.Panel::toDS() called");
        }

        for (int i = 0; i < fields.size(); i++) {
            views.Field f = fields.elementAt(i);
            if (f == null) {
                log.debug("child-field is null!");
                continue;
            }
            // if (!f.fromEditField(f.gettext()))
            // {
            // throw new Error("Bad value from field!");
            // }
        }

    }

    public synchronized void setfont_face(String name) {
        Font f = new Font(name, font_family, font_size);
        if (f != null) {
            panel.setFont(f);
            this.font_face = name;
        }

    }

    public String getfont_face() {
        return font_face;
    }

    public synchronized void setfont_family(int fam) {
        Font f = new Font(font_face, fam, font_size);
        if (f != null) {
        	panel.setFont(f);
            font_family = fam;
        }
    }

    public int getfont_family() {
        return font_family;
    }

    public synchronized void setfont_size(int size) {
        Font f = new Font(font_face, font_family, size);
        if (f != null) {
        	panel.setFont(f);
            font_size = size;
        }
    }

    public void setfont_color(String color) {
        try {
            int red = Integer.parseInt(color.substring(1, 3), 16);
            int green = Integer.parseInt(color.substring(3, 5), 16);
            int blue = Integer.parseInt(color.substring(5, 7), 16);
            setfont_color_i((red << 16) + (green << 8) + blue);
        }
        catch (Exception e) {
            log.error("Exception inside Field.setfont_color: ", e);
        }

    }

    public void setfont_color_i(int color) {
    	panel.setForeground(new Color(color));

    }

    public void setbg_color(String color) {
        try {
            int red = Integer.parseInt(color.substring(1, 3), 16);
            int green = Integer.parseInt(color.substring(3, 5), 16);
            int blue = Integer.parseInt(color.substring(5, 7), 16);
            setbg_color_i((red << 16) + (green << 8) + blue);
        }
        catch (Exception e) {
            log.error("Exception inside Field.setbg_color: ", e);
        }

    }

    public void setbg_color_i(int color) {
    	panel.setBackground(new Color(color));
    }

    public void setcolontitul(String colontitul) {
        this.colontitul = colontitul;
    }

    public String getcolontitul() {
        return colontitul;
    }

    public void addField(Field f) {
    	LayoutMng.add(panel, f);
//    	panel.add(f.getVisualComponent());
        fields.addElement(f);
    }

    public void init(Proper prop, Document doc) {
    	super.init(prop, doc);
    	LayoutMng.setLayout(panel, prop, null);

        String sp = (String) prop.get("MENUKEY");
        if (sp != null) {
            try {
                menuKey = Integer.parseInt(sp);
            }
            catch (Exception e) {
                log.error("Shit happens", e);
            }
        }
    }

    public Field getField(String name) {
        for (int i = 0; i < fields.size(); i++) {
            Field f = fields.elementAt(i);
            if (f.getAlias().equals(name)) {
                return f;
            }
        }
        return null;
    }

    // методы интерфейса GlobalValuesObject
    public Object getValue() {
        return this;
    }

    public void setValue(Object o) {
    }

    public void setValueByName(String name, Object o) {
    }

    public Object getValueByName(String name) {
        Field f = getField(name);
        Object ob = null;
        if (f != null) {
            ob = f.getValue();
        }
        return ob;
    }

    public void showMenu(JPanel comp) {
        menu.show(comp, 0, 0);
    }

    class PopupAL implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            try {
                document.doAction(command, null);
            }
            catch (Exception ex) {
                log.error("Shit happens", ex);
            }
            if (e.getSource() instanceof views.Item) {
                try {
					document.executeScript(((views.Item) e.getSource()).getExp(), false);
				} catch (Exception ex) {
					log.debug("!", ex);
				}
            }
        }
    }

    // Методы интерфейса class_method
    public Object method(String method, Object arg) throws Exception {

        if (method.equals("GETMENU")) {
            return getMenu();
        }
        else if (method.equals("SETMENU")) {
            setMenu((views.Menu) arg);

            return new Double(0);

        }else
        	return super.method(method, arg);
        
    }

    /**
     * Получть контекстное меню
     * @return меню MENU 
     */
    public views.Menu getMenu() {
        return menu;
    }

    /**
     * Добавить контекстное меню
     * @param m MENU меню 
     * @throws RTException
     */
    public void setMenu(views.Menu m) throws RTException {
        try {
            if (popupAL == null) {
                popupAL = new PopupAL();
            }
            menu = m;
            menu.addActionListenerRecursiv(popupAL);
        }
        catch (Exception e) {
            log.error("Shit happens", e);
            throw new RTException("RunTime", " Exception " + e.getMessage()
                    + " in method setmenu (svr_grid )");

        }
    }

	@Override
	public void focusThis() {
		panel.requestFocus();
	}

	@Override
	public ZComponent getVisualComponent() {
		return panel;
	}

	@Override
	public void addChild(RmlObject child) {
        container.addChildToCollection(child);

        if(child instanceof Datastore)
			ds = (Datastore) child;
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
		return true;
	}

	@Override
	protected Border getDefaultBorder() {
		return new EmptyBorder(0,0,0,0);
	}
}
