/*
 * File: __calc__.java
 * 
 * Created: Sun Nov 14 21:12:31 1999
 * 
 * Copyright (c) by Alexey Chen
 */

package action.calc;

import java.util.Hashtable;

public interface __calc__ {
    Object eval(Hashtable<String, Object> aliases) throws Exception;

    String[] getAliases() throws Exception;

    void initExpr(String expr) throws Exception;
}
