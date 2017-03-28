/*
 * File: NullExternFunction.java
 * 
 * Created: Tue Jun 1 16:52:35 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package action.calc.functions;

import action.calc.ExternFunction;

import java.util.Hashtable;


public abstract class NullExternFunction implements ExternFunction {
    public void getAliases(Hashtable<String, Object> h) throws Exception {
    }

    public void init(String arg) throws Exception {
    }
}
