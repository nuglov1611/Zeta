/*
 * File: TRACE.java
 * 
 * Created: Fri Apr 30 09:34:37 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package core.reflection.functions;

import action.calc.OP;
import action.calc.functions.BaseExternFunction;

public class TRACE extends BaseExternFunction {
    static final String fun = "FUN trace : ";

    public Object eval() throws Exception {
        boolean trace = OP.getTrace();
        OP.setTrace(true);
        try {
            return expr.eval();
        } finally {
            OP.setTrace(trace);
        }

    }
}
