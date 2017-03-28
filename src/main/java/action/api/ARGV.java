/*
 * File: ARGV.java
 * 
 * Created: Tue Apr 27 09:16:50 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package action.api;

import loader.ZetaProperties;
import org.apache.log4j.Logger;

import java.util.Hashtable;

public class ARGV extends Hashtable<String, Object> implements GlobalValuesObject {
    private static final Logger log = Logger.getLogger(ARGV.class);

//    Hashtable<String, Object>   h   = new Hashtable<String, Object>();

    Object object;

    public ARGV() {
        if (ZetaProperties.calc_debug > 2) {
            log.debug("~calc.ARGV::<init> create new argv!");
        }
    }

    public Object getValue() throws Exception {
        if (object == null) {
            return new Double(0);
        }
        return object;
    }

    public Object getValueByName(String name) throws Exception {
        int i = name.indexOf('.');
        Object o;
        if (i != -1) {
            o = get(name.substring(0, i));
            if (!(o instanceof GlobalValuesObject)) {
                throw new RTException("NullException", "variable "
                        + name.substring(0, i) + " is not container");
            }
            try {
                o = ((GlobalValuesObject) o).getValueByName(name
                        .substring(i + 1));
            } catch (NullPointerException e) {
                log.error("Shit happens", e);
                throw new RTException("NullException", "in argv: variable "
                        + name + " is not initialized");

            }
        } else {
            o = get(name);
        }
        if (o == null) {
            throw new RTException("NullException", "in argv: variable " + name
                    + " is not initialized");
        }
        return o;
    }

//    public Hashtable<String, Object> hash() {
//        return ;
//    }

    public void setValue(Object obj) {
        if (obj == this) {
            new RTException("EXCEPTION", "cicle reference on self");
        }
        object = obj;
    }

    public void setValueByName(String name, Object obj) throws Exception {
        int i = name.indexOf('.');
        if (obj == this) {
            new RTException("EXCEPTION", "cicle reference on self");
        }
        if (i != -1) {
            Object o = get(name.substring(0, i));
            if (!(o instanceof GlobalValuesObject)) {
                throw new RTException("NullException", "variable "
                        + name.substring(0, i) + " is not container");
            }
            ((GlobalValuesObject) o).setValueByName(name.substring(i + 1), obj);

        } else {
            put(name, obj);
        }
    }

    @Override
    public String toString() {
        return "ARGV"; // h.toString();
    }
}
