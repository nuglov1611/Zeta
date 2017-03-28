/*
 * File: BaseExternFunction.java
 * 
 * Created: Tue Jun 1 15:05:17 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package action.calc.functions;

import java.util.Hashtable;

import action.calc.ExternFunction;
import action.calc.OP;
import action.calc.Parser;


public abstract class BaseExternFunction implements ExternFunction {
    protected OP expr;

    public void getAliases(Hashtable<String, Object> h) throws Exception {
        expr.getAliases(h);
    }

    public void init(String arg) throws Exception {
        expr = Parser.parse1(arg.toCharArray());
    }
}
