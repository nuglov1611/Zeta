/*
 * File: X.java
 * 
 * Created: Tue Apr 27 09:06:02 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package core.reflection.functions;

import java.util.Enumeration;
import java.util.Hashtable;

import loader.ZetaProperties;

import org.apache.log4j.Logger;

import action.api.ARGV;
import action.calc.GotoException;
import action.calc.OP;
import action.calc.ReturnException;
import action.calc.XFunction;
import action.calc.functions.BaseExternFunction;

public class X extends BaseExternFunction implements XFunction {
    protected final static Logger log = Logger.getLogger(X.class);

    static final String           fun = "FUN X : ";

    OP                            args;

    public Object call(ARGV _V) throws Exception {
        boolean flag = true;
        String label = null;
        if (ZetaProperties.calc_debug > 2) {
            log.debug("called X function with " + _V/*.hash()*/);
        }
        Object V = OP.getAliases().get("V");
        OP.getAliases().put("V", _V);
        if (V != null) {
            _V.setValueByName("V", V);
        }
        Object result = new Double(0);
        try {
            while (flag) {
                flag = false;
                try {
                    if (label != null) {
                        expr.evalLabel(label);
                    }
                    else {
                        expr.eval();
                    }
                }
                catch (ReturnException e) {
                    // e.printStackTrace();
                    result = e.result;
                }
                catch (GotoException e) {
                    // e.printStackTrace();
                    label = e.label;
                    flag = true;
                }
            }
        }
        finally {
            if (V != null) {
                OP.getAliases().put("V", V);
            }
            else {
                OP.getAliases().remove("V");
            }
        }
        return result;
    }

    public Object eval() throws Exception {
        return call(new ARGV());
    }

    @Override
    public void getAliases(Hashtable<String, Object> h) throws Exception {
        super.getAliases(h);
        for (Enumeration<String> e = h.keys(); e.hasMoreElements();) {
            String s = e.nextElement();
            if (s.indexOf('.') == -1) {
                h.remove(s);
            }
        }
    }
}
