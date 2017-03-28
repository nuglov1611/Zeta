/*
 * File: QUOTE.java
 * 
 * Created: Thu May 13 11:52:44 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package core.reflection.functions;

import action.calc.Quoted;
import action.calc.functions.BaseExternFunction;

public class QUOTE extends BaseExternFunction {
    public Object eval() throws Exception {
        return (new Quoted(expr));
    }
}
