/*
 * File: IndexedAlias.java
 * 
 * Created: Wed Apr 28 08:55:50 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package action.calc;

import action.api.RTException;
import loader.ZetaProperties;
import org.apache.log4j.Logger;

import java.util.Enumeration;
import java.util.Vector;

public class IndexedAlias extends Alias implements Const {
    protected final static Logger log = Logger
            .getLogger(IndexedAlias.class);

    int index;

    Vector<Double> indexes = null;

    public IndexedAlias(IndexedAlias alias, int index) {
        super(alias.name());
        indexes = alias.indexes;
        if (indexes == null) {
            indexes = new Vector<Double>();
            indexes.addElement(new Double(alias.index));
        }
        indexes.addElement(new Double(index));
        if (ZetaProperties.calc_debug > 2) {
            log.debug("~calc.IndexedAlias::<init> alias is " + alias
                    + " index=" + index);
        }
    }

    public IndexedAlias(IndexedAlias alias, Vector<Double> index) {
        super(alias.name());
        indexes = alias.indexes;
        if (indexes == null) {
            indexes = new Vector<Double>();
            indexes.addElement(new Double(alias.index));
        }
        for (Enumeration<Double> e = index.elements(); e.hasMoreElements(); ) {
            indexes.addElement(e.nextElement());
        }
        if (ZetaProperties.calc_debug > 2) {
            log.debug("~calc.IndexedAlias::<init> alias is " + alias
                    + " index=" + index);
        }
    }

    public IndexedAlias(String alias, int index) {
        super(alias);
        this.index = index;
        indexes = null;
        if (ZetaProperties.calc_debug > 2) {
            log.debug("~calc.IndexedAlias::<init> alias is " + alias
                    + " index=" + index);
        }
    }

    public IndexedAlias(String alias, Vector<Double> index) {
        super(alias);
        indexes = index;
        if (ZetaProperties.calc_debug > 2) {
            log.debug("~calc.IndexedAlias::<init> alias is " + alias
                    + " index=" + index);
        }
    }

    @Override
    public Object eval() throws Exception {
        return getValue();
    }

    @Override
    public String expr() {
        return toString();
    }

    @Override
    public Object getValue() throws Exception {
        Object o = super.getValue();
        if (o instanceof Object[]) {
            try {
                if (indexes == null) {
                    return ((Object[]) o)[index];
                } else {
                    try {
                        Object result = o;
                        for (Enumeration<Double> e = indexes.elements(); e
                                .hasMoreElements(); ) {
                            result = ((Object[]) result)[e.nextElement()
                                    .intValue()];
                        }
                        return result;
                    } catch (ClassCastException e) {
                        log.error("", e);
                        throw new RTException("CastException",
                                "Element is not ARRAY or index is not NUMBER");
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                log.error("", e);
                throw new RTException("IndexException", "max index "
                        + (((Object[]) o).length - 1) + " but index=" + index);
            }
        } else {
            throw new ResonException(
                    "~calc.IndexedAlias::getValue type not Object[] \n\t"
                            + "reson: alias value is " + o + "\n\t"
                            + "Object: " + toString());
        }
    }

    @Override
    public Object setValue(Object a) throws Exception {
        Object o = super.getValue();
        if (o instanceof Object[]) {
            try {
                if (indexes == null) {
                    ((Object[]) o)[index] = a;
                } else {
                    try {
                        Object result = o;
                        for (Enumeration<Double> e = indexes.elements(); e
                                .hasMoreElements(); ) {
                            if (e.hasMoreElements()) {
                                result = ((Object[]) result)[e.nextElement()
                                        .intValue()];
                            } else {
                                ((Object[]) result)[e.nextElement().intValue()] = a;
                            }
                        }
                    } catch (ClassCastException e) {
                        log.error("", e);
                        throw new RTException("CastException",
                                "Element is not ARRAY or index is not NUMBER");
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                log.error("", e);
                throw new ResonException(
                        "~calc.IndexedAlias::setValue index exception \n\t"
                                + "reson: array.length="
                                + ((Object[]) o).length + "but index=" + index
                                + "\n\t" + "Object: " + toString());
            }
        } else {
            throw new ResonException(
                    "~calc.IndexedAlias::setValue type not Object[] \n\t"
                            + "reson: alias value is " + o + "\n\t"
                            + "Object: " + toString());
        }
        return a;
    }

    @Override
    public String toString() {
        return "IndexedAlias:" + index + "#" + left
                + ((right != null) ? "." + right : "");
    }
}
