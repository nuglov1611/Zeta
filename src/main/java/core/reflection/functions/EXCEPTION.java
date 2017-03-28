/*
 * File: EXCEPTION.java
 * 
 * Created: Thu Apr 29 15:31:16 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package core.reflection.functions;

import action.api.GlobalValuesObject;
import action.calc.OP;
import action.calc.functions.NullExternFunction;

public class EXCEPTION extends NullExternFunction {
    public Object eval() throws Exception {
        Object o = OP.getAliases().get("##exception##");
        if (o != null) {
            return ((Exception) ((GlobalValuesObject) o).getValue())
                    .getMessage();
        }
        else {
            return "No Exception";
        }
    }
}
