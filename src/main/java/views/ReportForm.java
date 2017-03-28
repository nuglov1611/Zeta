package views;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.border.Border;

import publicapi.RmlContainerAPI;
import core.document.Document;
import core.parser.Proper;
import core.rml.RmlObject;
import core.rml.VisualRmlObject;
import core.rml.dbi.Datastore;
import core.rml.ui.impl.ZPanelImpl;
import core.rml.ui.interfaces.ZComponent;
import core.rml.ui.interfaces.ZPanel;


public class ReportForm extends VisualRmlObject implements RmlContainerAPI {
	
	ZPanel form = ZPanelImpl.create();
	
	core.rml.Container container = new core.rml.Container(this);
	
    Datastore            ds           = null;

    int                      beginRow     = 0;

    core.rml.dbi.Group                currentGroup = null;

    Hashtable<String, Field> fields       = new Hashtable<String, Field>();

    Label[]                  labels       = null;

    Line[]                   lines        = null;

    IMage[]                  images       = null;

    public boolean           isPrint      = false;

    String                   type         = null;


    public void paint(Graphics g, int a) {
        Graphics gim = g.create();
        g.setColor(form.getBackground());
        Rectangle r = form.getBounds();
        g.fillRect(r.x * a / 100, r.y * a / 100, r.width * a / 100, r.height
                * a / 100);
        Field[] fs = getFields();
        if (fs != null) {
            for (int i = 0; i < fs.length; i++) {
                fs[i].setScaleFont(a);
                fs[i].paint(g, a);
            }
        }
        if (labels != null) {
            for (int i = 0; i < labels.length; i++) {
                labels[i].setScaleFont(a);
                labels[i].paint(g, a);

            }
        }
        if (lines != null) {
            for (int i = 0; i < lines.length; i++) {
                lines[i].paint(g, a);
            }
        }
        if (images != null) {
            for (int i = 0; i < images.length; i++) {
                images[i].paint(gim, a);
            }
        }
    }

    public String getType() {
        return type;
    }

    public void init(Proper prop, Document doc) {
        form.setLayout(null);
        super.init(prop, doc);

        String sp = (String) prop.get("TYPE"); // это св-во используется для
        // колонтитулов
        if (sp != null) {
            type = sp;
        }
        
        sp = ((String) prop.get("BG_COLOR"));
        if (sp != null) {
            form.setBackground(UTIL.getColor(sp));
        }
        else {
        	form.setBackground(Color.white);
        }
        
    }

    public void initChildren() {
        Vector<Object> vlines = new Vector<Object>();
        Vector<Object> vimages = new Vector<Object>(3);
        final RmlObject[] objs = container.getChildren();
        for (RmlObject o : objs) {
            if (o instanceof views.Field) {
                if (((Field) o).gettarget() == null) {
                    int type = ((Field) o).getType();
                    if (type == Integer.MIN_VALUE) {
                        System.out
                                .println("views.Panel.addChildren says : type for computed field not defined!");
                        continue;
                    }
                }
                form.add(((Field) o).getVisualComponent().getJComponent());
//                ((Field) o).setFieldParent(this);
                ((Field) o).setNeedTranslate(true);
                String alias = ((Field) o).getAlias();
                if (alias == null) {
                    System.out
                            .println("alias for field "
                                    + (Field) o
                                    + " not defined.This field will not be added into ReportForm!");
                }
                else {
                    fields.put(alias, (Field) o);
                }
                continue;
            }
            if (o instanceof views.Label) {
            	 form.add(((views.Label) o).getVisualComponent().getJComponent());
                ((Label) o).setParent(this);
                ((Label) o).needTranslate = true;
                continue;
            }

            if (o instanceof views.Line) {
                ((views.Line) o).parent = this;
                vlines.addElement(o);
            }
            // alex 05-09-2001
            if (o instanceof views.IMage) {
                Image im = ((views.IMage) o).getImage();
                form.getJComponent().prepareImage(im, form.getJComponent());
                // add((Component)o);
                vimages.addElement(o);
            }
            // alex 05-09-2001
        }
        // fields = getFields();
        labels = getLabels();
        if (vlines.size() > 0) {
            lines = new Line[vlines.size()];
            vlines.copyInto(lines);
        }
        // alex 05-09-2001
        if (vimages.size() > 0) {
            images = new IMage[vimages.size()];
            vimages.copyInto(images);
        }
        // alex 05-09-2001
    }

    public void setDatastore(Datastore ds) {
        this.ds = ds;
    }

    public Datastore getDatastore() {
        return this.ds;
    }

    public core.rml.dbi.Group getStore() {
        return currentGroup;
    }

    public Field[] getFields() {
        if (fields == null) {
            return null;
        }
        Enumeration<Field> e = fields.elements();
        if (e == null) {
            return null;
        }
        Vector<Field> v = new Vector<Field>();
        while (e.hasMoreElements()) {
            v.addElement(e.nextElement());
        }
        if (v.size() == 0) {
            return null;
        }
        Field[] ret = new Field[v.size()];
        v.copyInto(ret);
        return ret;
    }

    // public void setFields(Field[] fields) {
    // this.fields = fields;
    // }

    public Field getField(String alias) {
        if (fields != null) {
            return fields.get(alias);
        }
        else {
            return null;
        }
    }

    public Label[] getLabels() {
    	final RmlObject[] children = container.getChildren();
    	final ArrayList<Label> temp = new ArrayList<Label>();
    	
        for (RmlObject child : children) {
            if (child instanceof views.Label) {
                temp.add((Label)child);
            }
        }
        final Label[] ret = new Label[temp.size()];
        return temp.toArray(ret);
    }

    public void fillFields() {
        Field[] fs = getFields();
        if (fs == null) {
            return;
        }
        for (int i = 0; i < fs.length; i++) {
            if (!fs[i].isComputed()) {
                Object val = ds
                        .getValue(currentGroup.begrow, fs[i].gettarget());
                // System.out.println("---value="+val);
                fs[i].setType(ds.getType(fs[i].gettarget()));
                fs[i].needSetString = true;
                fs[i].setValue(val);
            }
            else {
                if (currentGroup == null) {
                    return;
                }
                Object o = currentGroup.getValueByName(fs[i].getAlias());
                fs[i].needSetString = true;
                fs[i].setValue(o);
            }
        }
    }

    public void fillFields2() { // for colontituls
        Field[] fs = getFields();
        if (fs == null) {
            return;
        }
        for (int i = 0; i < fs.length; i++) {
            if (fs[i] != null) {
                fs[i].needSetString = true;
            }
        }
        for (int i = 0; i < fs.length; i++) {
            if (!fs[i].isComputed()) {
                Object val = ds.getValue( /* currentGroup.begrow */0, fs[i]
                        .gettarget());
                // System.out.println("---value="+val);
                fs[i].setType(ds.getType(fs[i].gettarget()));
                fs[i].needSetString = true;
                fs[i].setValue(val);
                fs[i].calcDep();
            }
            else {
                fs[i].calc();
                fs[i].calcDep();
            }
        }
        for (int i = 0; i < fs.length; i++) {
            if (fs[i] != null) {
                fs[i].needSetString = false;
            }
        }

    }

	public Rectangle getBounds() {
		return form.getBounds();
	}

	@Override
	public void focusThis() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ZComponent getVisualComponent() {
		return form;
	}

	@Override
	public void addChild(RmlObject child) {
	}

	@Override
	public RmlObject[] getChildren() {
		return container.getChildren();
	}

	@Override
	public core.rml.Container getContainer() {
		return container;
	}

	@Override
	public boolean addChildrenAutomaticly() {
		return true;
	}

	@Override
	protected Border getDefaultBorder() {
		// TODO Auto-generated method stub
		return null;
	}

}
