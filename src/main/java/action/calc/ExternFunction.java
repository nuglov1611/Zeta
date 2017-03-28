/*
 * File: ExternFunction.java
 * 
 * Created: Fri Apr 23 10:40:33 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package action.calc;

import java.util.Hashtable;

public interface ExternFunction {
    public Object eval() throws Exception;

    public void getAliases(Hashtable<String, Object> h) throws Exception;

    public void init(String arg) throws Exception;
}
