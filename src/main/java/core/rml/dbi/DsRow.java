/*
 * File: DsRow.java
 * 
 * Created: Wed May 12 11:20:32 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Swistunov
 */
package core.rml.dbi;

import action.api.HaveMethod;
import action.api.RTException;
import action.calc.objects.class_type;

import java.util.Vector;

public class DsRow implements HaveMethod, class_type {
    Datastore parent;

    int row;

    public DsRow(int row, Datastore parent) {
        this.row = row;
        this.parent = parent;
    }

    public Object method(String method, Object arg) throws Exception {
        if (method.equals("FIELD")) {
            if ((arg instanceof String)) {
                return parent.getValue(row, (String) arg);
            } else if ((arg instanceof Double)) {
                return parent.getValue(row, ((Double) arg).intValue());
            } else {
                throw new RTException("CastException",
                        "DsRow::FIELD must have one argument compateable with String type");
            }

        } else if (method.equals("SETFIELD")) {
            try {
                Vector<Object> v = (Vector<Object>) arg;
                String field = (String) v.elementAt(0);
                Object o = v.elementAt(1);
                parent.setValue(row, field, o);
                return new Double(0);
            } catch (ClassCastException e) {
                // e.printStackTrace();
                throw new RTException("CastException",
                        "DsRow::SETFIELD must have two arguments:\n"
                                + "first  String type");

            }
        }
        throw new RTException("HasNotMethod", "method " + method
                + " not defined in class DsRow!");
    }

    public String type() {
        return "DSROW";
    }
}
