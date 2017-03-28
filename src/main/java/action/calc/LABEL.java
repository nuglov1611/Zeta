/*
 * File: LABEL.java
 * 
 * Created: Thu Apr 29 08:40:11 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package action.calc;

import java.util.Enumeration;
import java.util.Vector;

import loader.ZetaProperties;

import org.apache.log4j.Logger;

class LABEL extends OP implements Unar {
    protected final static Logger log = Logger.getLogger(LABEL.class);

    String                        label;

    public LABEL(String label) {
        sym = ':';
        sym2 = ':';
        prior = -1;
        this.label = label.trim().toUpperCase();
    }

    @Override
    public Object eval() throws NullPointerException, ClassCastException,
            Exception {
        Vector<Object> v;
        Object result;
        result = doOP(left);
        if (result instanceof Vector) {
            v = (Vector<Object>) result;
        }
        else {
            v = new Vector<Object>();
            v.addElement(result);
        }
        if (right != null) {
            result = doOP(right);
            if (result instanceof Vector) {
                Vector<Object> r = (Vector<Object>) result;
                for (Enumeration<Object> e = r.elements(); e.hasMoreElements();) {
                    v.addElement(e.nextElement());
                }
            }
            else {
                v.addElement(result);
            }
        }
        return v;
    }

    @Override
    public Object evalLabel(String label) throws Exception {
        if (ZetaProperties.calc_debug > 2) {
            log.debug("~calc.LABEL::evalLabel label " + label);
        }
        if (this.label.equals(label)) {
            return ((OP) right).eval();
        }
        else {
            return ((OP) left).evalLabel(label);
        }
    }

    @Override
    public String expr() {
        return "(" + expare(left) + toString() + expare(right) + ")";
    }

    @Override
    public String toString() {
        return ":" + label + ":";
    }
}
