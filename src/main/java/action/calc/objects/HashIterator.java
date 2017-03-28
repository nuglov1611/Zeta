/*
 * File: HashIterator.java
 * 
 * Created: Wed May 12 08:58:30 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package action.calc.objects;

import java.util.Enumeration;
import java.util.Hashtable;

import action.api.RTException;


public class HashIterator extends base_iterator {
    Object[]                  keys = null;

    Hashtable<String, Object> hash = null;

    public HashIterator(Hashtable<String, Object> hash) {
        super(hash.size() - 1);
        this.hash = hash;
        keys = new Object[hash.size()];
        Enumeration<String> e = hash.keys();
        for (int i = 0; e.hasMoreElements(); ++i) {
            keys[i] = e.nextElement();
        }
    }

    public Object set_value(Object value) throws Exception {
        throw new RTException("ReadOnlyException",
                "Can't modify key in hashtable");
    }

    @Override
    public Double size() throws Exception {
        return new Double(keys.length);
    }

    public Object value() throws Exception {
        if (cursor != -1) {
            return keys[cursor];
        }
        else {
            throw new RTException("IteratorException",
                    "iterator must be posited on any element");
        }
    }
}
