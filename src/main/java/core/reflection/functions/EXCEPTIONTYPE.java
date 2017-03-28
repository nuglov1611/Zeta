/*
 * File: EXCEPTIONTYPE.java
 * 
 * Created: Thu Apr 29 16:41:48 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package core.reflection.functions;

import action.api.GlobalValuesObject;
import action.api.RTException;
import action.calc.OP;
import action.calc.functions.NullExternFunction;

public class EXCEPTIONTYPE extends NullExternFunction {
    public Object eval() throws Exception {
        Object o = OP.getAliases().get("##exception##");
        if (o != null) {
            return ((RTException) ((GlobalValuesObject) o).getValue()).type;
        } else {
            return "No Exception";
        }
    }
}
