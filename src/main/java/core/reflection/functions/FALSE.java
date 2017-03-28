/*
 * File: FALSE.java
 * 
 * Created: Thu May 13 09:09:23 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package core.reflection.functions;

import action.calc.ReturnException;
import action.calc.functions.NullExternFunction;

public class FALSE extends NullExternFunction {
    static final String fun = "FUN FALSE : ";

    public Object eval() throws Exception {
        throw new ReturnException(new Double(0));
    }

}
