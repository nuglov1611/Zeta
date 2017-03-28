/*
 * File: DEBUGON.java
 * 
 * Created: Mon May 3 14:08:03 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package core.reflection.functions;

import action.calc.ExternFunction;
import action.calc.OP;

public class DEBUGON extends DEBUGING implements ExternFunction {
    @Override
    public Object eval() throws Exception {
        boolean dbg = debuging;
        debuging = true;
        try {
            return OP.doOP(expr);
        }
        finally {
            debuging = dbg;
        }
    }

    @Override
    public String toString() {
        return "DEBUGON";
    }
}
