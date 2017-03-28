/*
 * File: GroupIterator.java
 * 
 * Created: Tue May 11 12:06:53 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Swistunov
 */
package core.rml.dbi;

import action.api.RTException;
import action.calc.objects.base_iterator;


public class GroupIterator extends base_iterator {
    GroupReport ds;

    Group gr;

    Group[] grs;

    int count;

    public GroupIterator(GroupReport ds, Group gr) {
        super();
        this.ds = ds;
        if (gr != null) {
            grs = gr.getSubgroups();
            if (grs == null) {
                count = 0;
            } else {
                count = grs.length;
            }
            super.init(count - 1);
        }
    }

    public Object value() throws Exception {
        if (cursor == -1) {
            throw new RTException("IteratorException",
                    "DSIterator is not positioned!");
        }
        if (grs == null) {
            return new DsRow(gr.begrow + cursor, ds);
        } else {
            grs[cursor].setReport(ds);
            return grs[cursor];
        }
    }

    public Object set_value(Object obj) throws Exception {
        return null;
    }
}
