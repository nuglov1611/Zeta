/*
 * File: L.java
 * 
 * Created: Thu May 13 09:19:44 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package core.reflection.functions;

import action.calc.BreakException;
import action.calc.ContinueException;
import action.calc.GotoException;
import action.calc.functions.BaseExternFunction;

public class L extends BaseExternFunction {
    static final String fun = "FUN L : ";

    public Object eval() throws Exception {
        boolean flag = true;
        String label = null;
        Object result = new Double(0);
        try {
            while (flag) {
                flag = false;
                try {
                    if (label != null) {
                        if (label.equals("BEGINLOOP")) {
                            result = expr.eval();
                        }
                        else {
                            result = expr.evalLabel(label);
                        }
                    }
                    else {
                        result = expr.eval();
                    }
                }
                catch (GotoException e) {
                    // e.printStackTrace();
                    label = e.label;
                    flag = true;
                }
                catch (ContinueException e) {
                    // e.printStackTrace();
                    label = "BEGINLOOP";
                    flag = true;
                }
                catch (BreakException e) {
                    // e.printStackTrace();
                    break;
                }
            }
        }
        finally {
        }
        return result;
    }
}
