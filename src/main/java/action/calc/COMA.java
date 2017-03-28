/*
 * File: COMA.java
 * 
 * Created: Thu Apr 29 10:16:32 1999
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

public class COMA extends OP {
    protected final static Logger log = Logger.getLogger(COMA.class);

    public COMA() {
        sym = ';';
        sym2 = ' ';
        prior = 0;
    }

    @Override
    public Object eval() throws NullPointerException, ClassCastException,
            Exception {
        if (ZetaProperties.calc_debug > 3) {
            log.debug("~calc.COMA::eval aliases " + OP.getAliases());
        }
        Vector<Object> v;
        Object result;
        if (left != null) {
            result = doOP(left);
            if (result instanceof Vector) {
                v = (Vector<Object>) result;
            }
            else {
                v = new Vector<Object>();
                v.addElement(result);
            }
        }
        else {
            v = new Vector<Object>();
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
    public String expr() {
        return "(" + expare(left) + toString() + expare(right) + ")";
    }
}
