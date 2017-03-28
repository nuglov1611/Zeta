package views;

import action.api.Calc;
import action.api.RTException;
import action.api.ScriptApi;
import core.document.Document;
import core.parser.Proper;
import core.reflection.rml.REPORTHEADER;
import core.reflection.rml.REPORTTRAILER;
import core.rml.Container;
import core.rml.RmlObject;
import publicapi.RmlContainerAPI;

import java.util.Vector;

public class Group extends RmlObject implements RmlContainerAPI {


    private Container container = new Container(this);

    ReportForm rHeader = null;

    ReportForm rTrailer = null;

    ReportGrid rGrid = null;

    Group group = null;

    public core.rml.dbi.Group currentGroup = null; // используется при вычислении

    // групповых ф-ций

    public int curpos = -1;

    Vector<String> seq = null; // здесь будет хранится последовательность


    public void setValue(Object o) {
    }

    public void setValueByName(String name, Object o) {
    }

    public Object getValue() {
        return this;
    }

    public Object getValueByName(String name) {
        return null;
    }

    public void setDatastore(core.rml.dbi.Datastore ds) {
        if (rGrid != null) {
            rGrid.setDatastore(ds);
        }
        if (rHeader != null) {
            rHeader.setDatastore(ds);
        }
        if (rTrailer != null) {
            rTrailer.setDatastore(ds);
        }
        if (group != null) {
            group.setDatastore(ds);
        }
    }

    public void setParent(Report parent) {
        if (rGrid != null) {
            rGrid.setParent(parent);
        }
        if (group != null) {
            group.setParent(parent);
        }
    }

    public void setCurPos(int pos) {
        curpos = pos;
        if (group != null) {
            group.setCurPos(pos);
        }
    }

    public void createFonts(int a) {
        if (rGrid != null) {
            rGrid.createFonts(a);
        } else if (group != null) {
            group.createFonts(a);
        }
    }

    public void init(Proper prop, Document doc) {
        super.init(prop, doc);
    }

    public void initChildren() {
        if (rGrid != null && group != null) {
            rGrid = null;
        }
        // в нижеследующем коде получаем "магическую последовательность"
        // для вычисления значений Computed Field'ов
        // if (true) return;
        if ((rHeader == null) && (rTrailer == null)) {
            return;
        }
        Field[] hfs = null; // филды header'а
        Field[] tfs = null; // филды trailer'а
        if (rHeader != null) {
            hfs = rHeader.getFields();
        }
        if (rTrailer != null) {
            tfs = rTrailer.getFields();
        }
        if (tfs == null && hfs == null) {
            return;
        }

        Vector<String> names = new Vector<String>();
        Vector<Vector<String>> Bn = new Vector<Vector<String>>();
        if (hfs != null) {
            for (Field hf : hfs) {
                if ((hf != null) && hf.isComputed()) {
                    names.addElement(hf.getAlias());
                    String[] exps = null;
                    try {
                        if (hf.getCalc() != null) {
                            exps = ((Calc) hf.getCalc()).getAliases();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Vector<String> bi = new Vector<String>();
                    if (exps != null) {
                        for (String exp : exps) {
                            if (!exp.equals(hf.getAlias())) {
                                bi.addElement(exp);
                            }
                        }
                    }
                    Bn.addElement(bi);
                }
            }
        }
        if (tfs != null) {
            for (Field tf : tfs) {
                if ((tf != null) && tf.isComputed()) {
                    names.addElement(tf.getAlias());
                    String[] exps = null;
                    try {
                        if (tf.getCalc() != null) {
                            exps = ((Calc) tf.getCalc()).getAliases();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Vector<String> bi = new Vector<String>();
                    if (exps != null) {
                        for (String exp : exps) {
                            if (!exp.equals(tf.getAlias())) {
                                bi.addElement(exp);
                            }
                        }
                    }
                    Bn.addElement(bi);
                }
            }
        }
        if (names.size() == 0) {
            return;
        }
        try {
            seq = UTIL.createSequence(names, Bn); // получаем
            // "магическую последовательность"
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("~views.Group::addChildren : " + e);
        }
    }

    public ReportGrid getGrid() {
        if (rGrid != null) {
            return rGrid;
        }
        if (group != null) {
            return group.getGrid();
        } else {
            return null;
        }
    }

    public Object method(String method, Object arg) throws Exception {
        class help {
            double summa = 0;

            void obhod(core.rml.dbi.Group dgr, views.Group vgr, int worklevel,
                       int level, ScriptApi cc) {
                if (dgr == null) {
                    return;
                }
                if (vgr == null) {
                    return;
                }
                core.rml.dbi.Group[] subgr = dgr.getSubgroups();
                if (worklevel == level) { // добрались до нужного уровня,
                    // суммируем значения
                    if (vgr.rHeader != null) {
                        vgr.rHeader.currentGroup = dgr;
                    }
                    if (vgr.rTrailer != null) {
                        vgr.rTrailer.currentGroup = dgr;
                    }
                    try {
                        Object objs = cc.eval(document.getAliases());
                        if (objs != null) {
                            Double d = (Double) objs;
                            summa += d.doubleValue();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }
                if (subgr != null) {
                    for (core.rml.dbi.Group aSubgr : subgr) {
                        obhod(aSubgr, vgr.group, worklevel + 1, level, cc);
                    }
                }
            }

        } // end of class help

        double summa = 0;
        help h = new help();

        if (method.equals("SUM")) {
            if (arg instanceof String) {
                ScriptApi cc = ScriptApi.getAPI((String) arg);
                String[] names = null;
                try {
                    names = ((Calc) cc).getAliases();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (names == null) {
                    throw new RTException("Syntax",
                            "aliases in function SUM not found");
                }

                int result = 0;
                try {
                    result = find(this, names, 0);
                } catch (RTException e) {
                    e.printStackTrace();
                    throw e;
                }
                if (result == 0) { // все алиасы в текущей группе
                    Object objs = null;
                    try {
                        objs = cc.eval(document.getAliases());
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RTException("Any", e.getMessage());
                    }
                    if (objs != null) {
                        Double d = null;
                        try {
                            d = (Double) objs;
                        } catch (ClassCastException e) {
                            e.printStackTrace();
                            throw new RTException("CastException", e
                                    .getMessage());
                        }
                        return d;
                    } else {
                        return null;
                    }

                }
                if (result == -1) { // возможно, алиасы либо в гриде, либо в
                    // датасторе
                    if (currentGroup != null) {
                        ReportGrid rg;
                        if ((rg = getGrid()) != null) {
                            if (rg.ds != null) {
                                for (int i = currentGroup.begrow; i <= currentGroup.endrow; i++) {
                                    rg.ds.setCurrentRow(i);
                                    Object objs = cc.eval(document.getAliases());
                                    if (objs != null) {
                                        try {
                                            Double d = (Double) objs;
                                            summa += d.doubleValue();
                                        } catch (ClassCastException e) {
                                            e.printStackTrace();
                                            throw new RTException("", e
                                                    .getMessage());
                                        }
                                    }
                                }
                                return new Double(summa);
                            }
                        }
                    }
                }

                if (result > 0) { // значиит, все names в одной группе
                    // views.Group;
                    // организуем обход дерева для суммирования значений
                    h.obhod(currentGroup, this, 0, result, cc);
                    return new Double(h.summa);
                }
            }
        }
        if (method.equals("CURRENTROW")) {
            return new Double(curpos);
        }
        throw new RTException("HasNotMethod", "method " + method
                + " not defined in class views.Group!");
    }

    int find(views.Group vgr, String[] names, int level) throws RTException {
        if (names == null) {
            return level;
        }
        boolean inhead = true;
        boolean intrail = true;
        boolean res = true;
        for (int i = 0; i < names.length; i++) {
            inhead = vgr.rHeader != null && vgr.rHeader.fields != null
                    && vgr.rHeader.fields.get(names[i]) != null;

            intrail = vgr.rTrailer != null && vgr.rTrailer.fields != null
                    && vgr.rTrailer.fields.get(names[i]) != null;

            if (i == 0) {
                res = inhead || intrail;
            } else if (inhead || intrail != res) {
                throw new RTException("Syntax",
                        "Aliases must be in same level!");
            }
        }
        if (res) { // значит, все алиасы функции SUM принадлежат группе vgr
            return level;
        } else {
            if (vgr.group != null) {
                return find(vgr.group, names, level + 1);
            } else {
                return -1; // говорит о том ,что ни в одной из подгрупп данной
                // группы
            }
            // филдов с алиасами names не обнаружено
        }

    }

    public String type() {
        return "VIEWS_GROUP";
    }

    @Override
    public void addChild(RmlObject child) {
        if (child instanceof REPORTHEADER) {
            rHeader = ((REPORTHEADER) child).getForm();
        } else if (child instanceof REPORTTRAILER) {
            rTrailer = ((REPORTTRAILER) child).getForm();
        } else if (child instanceof Group) {
            group = (Group) child;
        } else if (child instanceof ReportGrid) {
            rGrid = (ReportGrid) child;
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
        return true;
    }
}
