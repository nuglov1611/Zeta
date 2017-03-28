/*
 * File: GOTO.java
 * 
 * Created: Thu Apr 29 08:59:17 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package core.reflection.functions;

import action.calc.Alias;
import action.calc.GotoException;
import action.calc.OP;
import action.calc.functions.BaseExternFunction;

public class GOTO extends BaseExternFunction {
    public Object eval() throws Exception {
        Object o = OP.doSoftOP(expr);
        if (o instanceof Alias) {
            o = ((Alias) o).name();
        }
        throw new GotoException((String) o);
    }
}
