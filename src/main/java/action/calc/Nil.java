/*
 * File: Nil.java
 * 
 * Created: Wed Jun 16 15:54:38 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package action.calc;

import action.api.GlobalValuesObject;
import action.calc.objects.class_type;


public class Nil implements GlobalValuesObject, class_type {

    // public static Nil NIL;

    @Override
    public boolean equals(Object o) {
        return (o instanceof Nil);
    }

    public Object getValue() throws Exception {
        return this;
    }

    public Object getValueByName(String name) throws Exception {
        return null;
    }

    public void setValue(Object obj) throws Exception {
    }

    public void setValueByName(String name, Object obj) throws Exception {
    }

    @Override
    public String toString() {
        return "NIL";
    }

    public String type() {
        return "NIL";
    }
}
