/*
 * File: StringIterator.java
 * 
 * Created: Wed May 12 09:28:56 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package action.calc.objects;

import action.api.RTException;


public class StringIterator extends base_iterator {
    char arr[] = null;

    public StringIterator(String s) {
        super(s.length() - 1);
        arr = s.toCharArray();
    }

    public Object set_value(Object value) throws Exception {
        throw new RTException("ReadOnlyException",
                "Can't modify String, String is CONSTANT");
    }

    public Object value() throws Exception {
        if (cursor != -1) {
            return "" + arr[cursor];
        } else {
            throw new RTException("IteratorException",
                    "iterator must be posited on any element");
        }
    }
}
