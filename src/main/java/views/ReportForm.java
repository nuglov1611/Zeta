package views;

import core.document.Document;
import core.parser.Proper;
import core.rml.RmlObject;
import core.rml.VisualRmlObject;
import core.rml.dbi.Datastore;
import core.rml.ui.impl.ZPanelImpl;
import core.rml.ui.interfaces.ZComponent;
import core.rml.ui.interfaces.ZPanel;
import publicapi.RmlContainerAPI;

import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


public class ReportForm extends VisualRmlObject implements RmlContainerAPI {

    ZPanel form = ZPanelImpl.create();

    core.rml.Container container = new core.rml.Container(this);

    Datastore ds = null;

    int beginRow = 0;

    core.rml.dbi.Group currentGroup = null;

    Hashtable<String, Field> fields = new Hashtable<String, Field>();

    Label[] labels = null;

    Line[] lines = null;

    IMage[] images = null;

    public boolean isPrint = false;

    String type = null;


    public void paint(Graphics g, int a) {
        Graphics gim = g.create();
        g.setColor(form.getBackground());
        Rectangle r = form.getBounds();
        g.fillRect(r.x * a / 100, r.y * a / 100, r.width * a / 100, r.height
                * a / 100);
        Field[] fs = getFields();
        if (fs != null) {
            for (Field f : fs) {
                f.setScaleFont(a);
                f.paint(g, a);
            }
        }
        if (labels != null) {
            for (Label label : labels) {
                label.setScaleFont(a);
                label.paint(g, a);

            }
        }
        if (lines != null) {
            for (Line line : lines) {
                line.paint(g, a);
            }
        }
        if (images != null) {
            for (IMage image : images) {
                image.paint(gim, a);
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
        } else {
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
                String alias = o.getAlias();
                if (alias == null) {
                    System.out
                            .println("alias for field "
                                    + o
                                    + " not defined.This field will not be added into ReportForm!");
                } else {
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
        } else {
            return null;
        }
    }

    public Label[] getLabels() {
        final RmlObject[] children = container.getChildren();
        final ArrayList<Label> temp = new ArrayList<Label>();

        for (RmlObject child : children) {
            if (child instanceof views.Label) {
                temp.add((Label) child);
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
        for (Field f : fs) {
            if (!f.isComputed()) {
                Object val = ds
                        .getValue(currentGroup.begrow, f.gettarget());
                // System.out.println("---value="+val);
                f.setType(ds.getType(f.gettarget()));
                f.needSetString = true;
                f.setValue(val);
            } else {
                if (currentGroup == null) {
                    return;
                }
                Object o = currentGroup.getValueByName(f.getAlias());
                f.needSetString = true;
                f.setValue(o);
            }
        }
    }

    public void fillFields2() { // for colontituls
        Field[] fs = getFields();
        if (fs == null) {
            return;
        }
        for (Field f2 : fs) {
            if (f2 != null) {
                f2.needSetString = true;
            }
        }
        for (Field f1 : fs) {
            if (!f1.isComputed()) {
                Object val = ds.getValue( /* currentGroup.begrow */0, f1
                        .gettarget());
                // System.out.println("---value="+val);
                f1.setType(ds.getType(f1.gettarget()));
                f1.needSetString = true;
                f1.setValue(val);
                f1.calcDep();
            } else {
                f1.calc();
                f1.calcDep();
            }
        }
        for (Field f : fs) {
            if (f != null) {
                f.needSetString = false;
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
