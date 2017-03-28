/*
 * File: NOTRACE.java
 * 
 * Created: Fri Apr 30 09:37:52 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package core.reflection.functions;

import action.calc.OP;
import action.calc.functions.BaseExternFunction;

public class NOTRACE extends BaseExternFunction {
    static final String fun = "FUN notrace : ";

    public Object eval() throws Exception {
        boolean trace = OP.getTrace();
        OP.setTrace(false);
        try {
            return expr.eval();
        }
        finally {
            OP.setTrace(trace);
        }
    }
}
