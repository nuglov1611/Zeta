/*
 * File: Quoted.java
 * 
 * Created: Thu May 13 12:05:41 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package action.calc;

import action.api.HaveMethod;
import action.api.RTException;
import action.calc.objects.class_type;


public class Quoted implements Const, HaveMethod, class_type {
    OP e;

    public Quoted(OP e) {
        this.e = e;
    }

    public OP getOP() {
        return e;
    }

    public Object method(String method, Object arg) throws Exception {
        if (method.equals("EVAL")) {
            return e.eval();
        } else {
            throw new RTException("HasMethodException",
                    "object Quoted has not method " + method);
        }
    }

    public String type() throws Exception {
        return "QUOTED";
    }

}
