/*
 * File: RET.java
 * 
 * Created: Tue Apr 27 09:24:37 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package core.reflection.functions;

import action.calc.OP;
import action.calc.ReturnException;
import action.calc.functions.BaseExternFunction;
import loader.ZetaProperties;

public class RET extends BaseExternFunction {
    static final String fun = "FUN RET : ";

    public Object eval() throws Exception {
        if (ZetaProperties.calc_debug > 2) {
            System.out
                    .println(fun + " " + expr.expr() + "  OP.soft=" + OP.getSoft());
        }
        throw new ReturnException(OP.doOP(expr));
    }
}
