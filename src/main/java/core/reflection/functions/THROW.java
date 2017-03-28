/*
 * File: THROW.java
 * 
 * Created: Tue Apr 27 09:24:37 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package core.reflection.functions;

import action.api.RTException;
import action.calc.OP;
import action.calc.ResonException;
import action.calc.functions.BaseExternFunction;
import loader.ZetaProperties;

import java.util.Vector;

public class THROW extends BaseExternFunction {
    static final String fun = "FUN THROW : ";

    public Object eval() throws Exception {
        if (ZetaProperties.calc_debug > 2) {
            System.out
                    .println(fun + " " + expr.expr() + "  OP.soft=" + OP.getSoft());
        }
        Object a = OP.doOP(expr);
        if ((a instanceof Vector) && (((Vector<String>) a).size() == 2)) {
            try {
                throw new RTException(((Vector<String>) a).elementAt(0),
                        ((Vector<String>) a).elementAt(1));
            } catch (ClassCastException e) {
                throw new ResonException(
                        "~calc.functions.THROW::eval must be String,String");
            }
        } else {
            throw new ResonException(
                    "~calc.functions.THROW::eval must be 2 args");
        }
    }
}
