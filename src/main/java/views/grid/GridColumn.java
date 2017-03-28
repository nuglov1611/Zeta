package views.grid;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.io.ByteArrayInputStream;
import java.sql.Types;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.table.TableColumn;

import loader.Loader;
import loader.ZetaProperties;

import org.apache.log4j.Logger;

import publicapi.GridColumnAPI;
import views.util.RmlPropertyContainer;
import action.api.RTException;
import action.calc.Nil;
import core.document.Document;
import core.parser.Proper;
import core.reflection.objects.VALIDATOR;
import core.rml.Container;
import core.rml.RmlConstants;
import core.rml.RmlObject;
import core.rml.dbi.Datastore;
import core.rml.dbi.Handler;

/**
 * Визуальный Rml-объект "столбец" используется при описании таблицы.  
 *
 */
public class GridColumn extends RmlObject implements Handler, GridColumnAPI {

    private static final Logger log = Logger.getLogger(GridColumn.class);
    
    private Container container = new Container(this);
    
    private TableColumn column = new TableColumn(); 
    
    public int dw = 2;

    public int dh = 2;

    public FontMetrics fm = null;

    private int type = Integer.MIN_VALUE;//говорит о том, что type не определен

    private String target;

    private String[] depends = null;

    private Datastore ds = null;

    private Object parent = null;

    private VALIDATOR validator = new VALIDATOR();

    private String targetArray = null;

    private Vector<Object> items = new Vector<Object>();

    private boolean isArray = false;
    
    private RmlPropertyContainer rmlPropertyContainer;
    
    public GridColumn() {
        this("");
    }

    public GridColumn(String title) {
    	column.setWidth(50);
        rmlPropertyContainer = new RmlPropertyContainer();
        rmlPropertyContainer.put(RmlConstants.TITLE, title);
    }

    public void calc() {
        try {
        	document.executeScript(rmlPropertyContainer.getStringProperty(RmlConstants.EXP), true);
        } catch (Exception e) {
            log.error("Shit happens", e);
        }

    }

    public void calcDep() {
        if (depends == null) {
            return;
        }
        for (String element : depends) {
            GridColumn c = (GridColumn) document.findObject(element);
            if (c != null) {
                c.calc();
            } else {
                log.error("Shit happens",
                        new Exception("object views.grid.GridColumn was not " +
                                "found for alias " + element));
            }
        }
    }

    public void calcHandbookDep() {
        if (depends == null) {
            return;
        }
        for (String element : depends) {
            GridColumn c = (GridColumn) document.findObject(element);
            if (c != null) {
                c.calcHandbookExp();
            } else {
                log.warn("object views.GridColumn not found for alias " + element);
            }
        }
    }

    public void calcHandbookExp() {
        try {
            document.executeScript(getEditExp(), false);
        } catch (Exception e) {
            log.error("Shit happens", e);
        }
    }

    public String getEditable() {
        return rmlPropertyContainer.getStringProperty(RmlConstants.EDITABLE);
    }

    public Object getParent() {
        return parent;
    }

    public core.rml.dbi.Datastore getParentDS() {
        if (parent instanceof views.ReportGrid) {
            return ((views.ReportGrid) parent).ds;
        } else if (parent instanceof GridSwing) {
            return ((GridSwing) parent).getDatastore();
        }
        return null;
    }

    public String getTitle() {
        return rmlPropertyContainer.getStringProperty(RmlConstants.TITLE);
    }

    public int getType() {
        return type;
    }

    public String[] getDep() {
        return depends;
    }

    public Object getValue() throws RTException {
        Datastore ds;
        Object o = null;
        if ((ds = getParentDS()) != null) {
            o = ds.getValue(target);
        } else {
            o = null;
        }
    	if(type == Types.BOOLEAN){
    		try {
				o = validator.toString(o);
			} catch (Exception e) {
				log.error("!", e);
			} 
    	}
    	return o;
    }

    public Object getValueByName(String name) {
        return null;
    }

    public void setValue(Object o) {
        Datastore ds;
        if ((ds = getParentDS()) != null) {
        	if(target == null){
        		log.error("Столбец "+alias+" имеет target == NULL!!!!");
        	}
        	if(type == Types.BOOLEAN && o != null && o instanceof String){
        		try {
					o = validator.toObject((String) o);
				} catch (Exception e) {
					log.error("!", e);
				} 
        	}
            ds.setValue(target, o);
            if (depends != null) {
                calcDep();
            }
        }
    }

    public void setValueByName(String name, Object o) {
    }

    public void init(Proper prop, Document doc) {
    	super.init(prop, doc);
        if (prop == null) {
            return;
        }
        rmlPropertyContainer.initProperty(prop, RmlConstants.ALIAS);

        rmlPropertyContainer.initProperty(prop, RmlConstants.TITLE_FONT_FACE, "Serif");
        rmlPropertyContainer.initProperty(prop, RmlConstants.TITLE_FONT_SIZE, 12);
        rmlPropertyContainer.initProperty(prop, RmlConstants.TITLE_FONT_FAMILY, 0);
        rmlPropertyContainer.initProperty(prop, RmlConstants.TITLE_FONT_COLOR, Color.class, Color.black);

        Font titleFont = new Font(rmlPropertyContainer.getStringProperty(RmlConstants.TITLE_FONT_FACE),
                rmlPropertyContainer.getIntProperty(RmlConstants.TITLE_FONT_FAMILY),
                rmlPropertyContainer.getIntProperty(RmlConstants.TITLE_FONT_SIZE));
        rmlPropertyContainer.put(RmlConstants.TITLE_FONT, titleFont);

        rmlPropertyContainer.initProperty(prop, RmlConstants.FONT_FACE, "Serif");
        rmlPropertyContainer.initProperty(prop, RmlConstants.FONT_SIZE, 10);
        rmlPropertyContainer.initProperty(prop, RmlConstants.FONT_FAMILY, 0);
        rmlPropertyContainer.initProperty(prop, RmlConstants.FONT_COLOR, Color.class, Color.black);

        rmlPropertyContainer.initProperty(prop, RmlConstants.FONT_FILE);
        Font font = null;
        if (rmlPropertyContainer.getStringProperty(RmlConstants.FONT_FILE) != null) {
            try {
                byte[] font_src = Loader.getInstanceRml().loadByName_bytes(rmlPropertyContainer.getStringProperty(RmlConstants.FONT_FILE));
                ByteArrayInputStream fontStream = new ByteArrayInputStream(font_src);
                font = Font.createFont(Font.TRUETYPE_FONT, fontStream);
                font = font.deriveFont(rmlPropertyContainer.getIntProperty(RmlConstants.FONT_SIZE));
            } catch (Exception e) {
                log.error("!", e);
            }
        } else {
            font = new Font(rmlPropertyContainer.getStringProperty(RmlConstants.FONT_FACE),
                    rmlPropertyContainer.getIntProperty(RmlConstants.FONT_FAMILY),
                    rmlPropertyContainer.getIntProperty(RmlConstants.FONT_SIZE));
        }
        rmlPropertyContainer.put(RmlConstants.FONT, font);

        rmlPropertyContainer.initProperty(prop, RmlConstants.SIZE);
        if (rmlPropertyContainer.getIntProperty(RmlConstants.SIZE) != null) {
            column.setWidth(rmlPropertyContainer.getIntProperty(RmlConstants.SIZE));
        }
        rmlPropertyContainer.initProperty(prop, RmlConstants.VALUES);
        if (rmlPropertyContainer.getStringProperty(RmlConstants.VALUES) != null) {
            isArray = true;
            setValues(rmlPropertyContainer.getStringProperty(RmlConstants.VALUES));
        }
        rmlPropertyContainer.initProperty(prop, RmlConstants.TARGET);
        if (rmlPropertyContainer.getStringProperty(RmlConstants.TARGET) != null) {
            target = rmlPropertyContainer.getStringProperty(RmlConstants.TARGET).toUpperCase();
        }

        rmlPropertyContainer.initProperty(prop, RmlConstants.TITLE, rmlPropertyContainer.getStringProperty(RmlConstants.TITLE));
        rmlPropertyContainer.initProperty(prop, RmlConstants.TYPE);
        if (rmlPropertyContainer.getStringProperty(RmlConstants.TYPE) != null) {
            setType(rmlPropertyContainer.getStringProperty(RmlConstants.TYPE));
        }
        rmlPropertyContainer.initProperty(prop, RmlConstants.HALIGNMENT, "LEFT");
        rmlPropertyContainer.initProperty(prop, RmlConstants.VALIGNMENT, "CENTER");
        rmlPropertyContainer.initProperty(prop, RmlConstants.BG_COLOR, Color.class, Color.white);
        rmlPropertyContainer.initProperty(prop, RmlConstants.VISIBLE, "YES");
        rmlPropertyContainer.initProperty(prop, RmlConstants.EDITABLE, "HAND");
        rmlPropertyContainer.initProperty(prop, RmlConstants.DEP);
        if (rmlPropertyContainer.getStringProperty(RmlConstants.DEP) != null) {
            setDep(rmlPropertyContainer.getStringProperty(RmlConstants.DEP));
        }
        rmlPropertyContainer.initProperty(prop, RmlConstants.EXP);
        rmlPropertyContainer.initProperty(prop, RmlConstants.EDITEXP);
        rmlPropertyContainer.initProperty(prop, RmlConstants.DROPEXP);
        rmlPropertyContainer.initProperty(prop, RmlConstants.EDIT);

        rmlPropertyContainer.initProperty(prop, RmlConstants.EDITMASK);
        if (rmlPropertyContainer.getStringProperty(RmlConstants.EDITMASK) != null) {
            setMask();
        }

        rmlPropertyContainer.initProperty(prop, RmlConstants.PASSWORD, "NO");
        rmlPropertyContainer.initProperty(prop, RmlConstants.MULTILINE, "NO");
        rmlPropertyContainer.initProperty(prop, RmlConstants.FUNCTION);

        fm = Toolkit.getDefaultToolkit().getFontMetrics(font);
    }

    public void setValues(String values) {
        if (values.trim().startsWith("DS:")) {
            targetArray = values.trim().substring(3);
        } else {
            StringTokenizer st = new StringTokenizer(values.trim(), ",");
            while (st.hasMoreTokens()) {
                items.add(st.nextToken());
            }
        }

    }

    public boolean isVisible() {
        return rmlPropertyContainer.getBooleanProperty(RmlConstants.VISIBLE);
    }

    public void retrieve() {
        try {
            if (ds != null) {
                ds.retrieve();
            }
        } catch (Exception e) {
            log.error("Shit happens", e);
        }
    }

    /**
     * задает связанные столбцы (столбцы, котрые будут пересчитываться при изменении значения в этом столбце)
     * @param dep - список зависымых столбцов через запятую
     */
    public void setDep(String dep) {

        rmlPropertyContainer.put(RmlConstants.DEP, dep);
        
        dep = dep.toUpperCase();
        dep = dep.trim();
        StringTokenizer st = new StringTokenizer(dep, ",");
        int count = st.countTokens();
        if (count == 0) {
            return;
        }
        depends = new String[count];
        for (int i = 0; i < count; i++) {
            depends[i] = st.nextToken().trim().toUpperCase();
            if (ZetaProperties.views_debug > 0) {
                log.debug(depends[i]);
            }
        }

    }

    public void setParent(Object p) {
        parent = p;
    }

    public void setTitle(String title) {
        rmlPropertyContainer.put(RmlConstants.TITLE, title);
    }

    /**
     * задает тип данных столбца
     * @param type - тип данных java.sql.Types, поддерживаются следующие типы:
     * java.sql.Types.NUMERIC
     * java.sql.Types.VARCHAR
     * java.sql.Types.DATE
     * java.sql.Types.BOOLEAN;
     */
    public void setType(int type) {
        this.type = type;
        if (isArray) {
            validator.setArrayType(GridSwing.getJType(type));
        } else {
            validator.setType(GridSwing.getJType(type));
        }
    }

    /**
     * задает тип данных столбца
     * @param t - тип ("number", "string", "date", "boolean")
     */
    public void setType(String t) {
        rmlPropertyContainer.put(RmlConstants.TYPE, t);

        int type = generateType(t);
        setType(type);
    }

    /**
     * преобразует тип из string в java.sql.Types
     * @param t - тип данных
     * @return соответвующий тип java.sql.Types 
     */
    public static int generateType(String t) {
        int type = Integer.MIN_VALUE;
        t = t.toUpperCase();
        if (t.equalsIgnoreCase("NUMBER")) {
            type = java.sql.Types.NUMERIC;
        } else if (t.equalsIgnoreCase("STRING")) {
            type = java.sql.Types.VARCHAR;
        } else if (t.equalsIgnoreCase("DATE")) {
            type = java.sql.Types.DATE;
        } else if (t.equalsIgnoreCase("BOOLEAN")) {
            type = java.sql.Types.BOOLEAN;
        }
        return type;
    }

    public void setTarget(String target) {
        this.target = target.toUpperCase();
    }

    public VALIDATOR getValidator() {
        return validator;
    }

    public String getAlias() {
        return rmlPropertyContainer.getStringProperty(RmlConstants.ALIAS);
    }


    public String getTarget() {
        return target;
    }

    public boolean isPassword() {
        return rmlPropertyContainer.getBooleanProperty(RmlConstants.PASSWORD);
    }

    public String getCalc(){
    	return rmlPropertyContainer.getStringProperty(RmlConstants.EXP);
    }
    
    public String getEdit() {
        return rmlPropertyContainer.getStringProperty(RmlConstants.EDIT);
    }

    public String getEditExp() {
        return rmlPropertyContainer.getStringProperty(RmlConstants.EDITEXP);
    }

    public String getDropExp() {
        return rmlPropertyContainer.getStringProperty(RmlConstants.DROPEXP);
    }

    public String valueToString(Object value) throws Exception {
        return validator.toString(value);
    }

    public Object valueToObject(String value) throws Exception {
        return validator.toObject(value);
    }

    public void setMask(String mask) {
        validator.setMask(mask);
    }

    @Override
    public void notifyHandler(Object o) {
        if (targetArray == null) {
            return;
        }
        int size = ds.getRowCount();
        items.clear();
        for (int i = 0; i < size; i++) {
            Object value = ds.getValue(i, targetArray);
            if (!items.contains(value)) {
                items.add(value);
            }
        }
    }

    public void setDSRow(int row) {
        if (ds != null) {
            ds.setCurrentRow(row);
        }
    }

    public void setIsArray(boolean array) {
        isArray = array;
    }

    public boolean isArray() {
        return isArray;
    }

    public Object[] getItems() {
        return items.toArray();
    }

    public boolean isMultiline() {
        return rmlPropertyContainer.getBooleanProperty(RmlConstants.MULTILINE);
    }

    public void setAlias(String alias) {
        rmlPropertyContainer.put(RmlConstants.ALIAS, alias);
    	super.setAlias(alias);
    }

    public void setEditStyle(String editStyle) {
        rmlPropertyContainer.put(RmlConstants.EDITABLE, editStyle);
    }

    public void setDatastore(Datastore datastore) {
        this.ds = datastore;
        ds.addHandler(this);
    }

    public Datastore getDatastore() {
        return ds;
    }

    public boolean isEditable() {
        return !rmlPropertyContainer.getStringProperty(RmlConstants.EDITABLE).equalsIgnoreCase(RmlConstants.NO) &&
                !rmlPropertyContainer.getStringProperty(RmlConstants.EDITABLE).equalsIgnoreCase(RmlConstants.READONLY);
    }

    public Boolean getBooleanProperty(String propName) {
        return rmlPropertyContainer.getBooleanProperty(propName);
    }

    public String getStringProperty(String propName) {
        return rmlPropertyContainer.getStringProperty(propName);
    }

    public Integer getIntProperty(String propName) {
        return rmlPropertyContainer.getIntProperty(propName);
    }

    public Color getColorProperty(String propName) {
        return rmlPropertyContainer.getColorProperty(propName);
    }

    public Font getFontProperty(String propName) {
        return rmlPropertyContainer.getFontProperty(propName);
    }

    @Override
    public Object method(String method, Object arg) throws Exception {
        if (method.toLowerCase().startsWith("set")) {
            String propertyName = method.substring(3).toUpperCase();
            if (propertyName.equals("DS")) {
                if (arg instanceof Datastore) {
                    setDS(arg);
                } else {
                    throw new RTException("CASTEXCEPTION", "Bad arguments in Column@setDS");
                }
            } else {
                rmlPropertyContainer.put(propertyName, arg);

                if (propertyName.equals(RmlConstants.TYPE)) {
                    setType(rmlPropertyContainer.getStringProperty(RmlConstants.TYPE));
                } else if (propertyName.equals(RmlConstants.DEP)) {
                    setDep(rmlPropertyContainer.getStringProperty(RmlConstants.DEP));
                } else if (propertyName.equals(RmlConstants.EDITMASK)) {
                    setMask();
                } else if (propertyName.equals(RmlConstants.TITLE) && parent instanceof GridSwing) {
                    ((GridSwing) parent).getTableManager().setColumnTitle(this, rmlPropertyContainer.getStringProperty(RmlConstants.TITLE));
                } else if (propertyName.equals(RmlConstants.TARGET)) {
                    target = rmlPropertyContainer.getStringProperty(RmlConstants.TARGET).toUpperCase();
                } else if (propertyName.equals(RmlConstants.SIZE)) {
                    column.setWidth(rmlPropertyContainer.getIntProperty(RmlConstants.SIZE));
                } else if (propertyName.equals(RmlConstants.VALUES)) {
                    isArray = true;
                    setValues(rmlPropertyContainer.getStringProperty(RmlConstants.VALUES));
                }
            }
        } else if (method.toLowerCase().startsWith("get")) {
            String propertyName = method.substring(3).toUpperCase();
            if (propertyName.equals("DS")) {
                return ds;
            } else if (propertyName.equals(RmlConstants.TARGET)) {
                return target;
            } else if (propertyName.equals(RmlConstants.TYPE)) {
                return type;
            } else if (rmlPropertyContainer.containsKey(propertyName)) {
                return rmlPropertyContainer.get(propertyName);
            } else {
                throw new RTException("NoPropertyException", "Bad arguments in Column@set" + propertyName);
            }
        } else if (method.equalsIgnoreCase("calc")) {
            calc();
        } else {
            throw new RTException("HASNOMETHODEXCEPTION", "there is no such method in Column" + method);
        }
        return new Nil();
    }

    /**
     * 
     */
    public void setMask() {
        validator.setMask(rmlPropertyContainer.getStringProperty(RmlConstants.EDITMASK));
    }

    /**
     * Задать Datastore для данного столбца 
     * @param datastore - Datastore
     */
    public void setDS(Object datastore) {
        ds = (Datastore) datastore;
    }

    public void setFont(Font font) {
        rmlPropertyContainer.put(RmlConstants.FONT, font);
    }

    public void setFontColor(Color color) {
        rmlPropertyContainer.put(RmlConstants.FONT_COLOR, color);
    }

    public void setBgColor(Color color) {
        rmlPropertyContainer.put(RmlConstants.BG_COLOR, color);
    }

	@Override
	public void addChild(RmlObject child) {
        container.addChildToCollection(child);
	    
        if (child instanceof Datastore) {
            ds = (Datastore) child;
            ds.addHandler(this);
        }
	}

	public TableColumn getColumn(){
		return column;
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

    public void setTitleBgColor(Color color) {
        rmlPropertyContainer.put(RmlConstants.TITLEBAR_BG_COLOR, color);
    }

    public void setTitleFgColor(Color color) {
        rmlPropertyContainer.put(RmlConstants.TITLE_FONT_COLOR, color);
    }

    public void setVisible(boolean visible) {
        rmlPropertyContainer.put(RmlConstants.VISIBLE, visible ? "YES" : "NO");
    }
    
    //TODO Удалить к ебеням когда станет не нужно и никому не показывать и не рассказывать.
    public void runDropHook(){
        if (getDropExp() != null){
            try {
                document.executeScript(getDropExp(), false);
            } catch (Exception e) {
                log.error("Shit happens", e);
            }
        }
    }
}