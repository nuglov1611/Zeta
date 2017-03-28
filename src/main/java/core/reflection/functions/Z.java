/*
 * File: Z.java
 * 
 * Created: Thu May 13 09:12:48 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package core.reflection.functions;

import action.calc.GotoException;
import action.calc.ReturnException;
import action.calc.functions.BaseExternFunction;

public class Z extends BaseExternFunction {
    static final String fun = "FUN Z : ";

    public Object eval() throws Exception {
        boolean flag = true;
        String label = null;
        Object result = new Double(0);
        try {
            while (flag) {
                flag = false;
                try {
                    if (label != null) {
                        expr.evalLabel(label);
                    }
                    else {
                        expr.eval();
                    }
                }
                catch (ReturnException e) {
                    // e.printStackTrace();
                    result = e.result;
                }
                catch (GotoException e) {
                    // e.printStackTrace();
                    label = e.label;
                    flag = true;
                }
            }
        }
        finally {
        }
        return result;
    }
}
