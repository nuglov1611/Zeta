/*
 * File: DSIterator.java
 * 
 * Created: Tue May 11 09:45:34 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Swistunov
 */

package core.rml.dbi;

import action.api.RTException;
import action.calc.objects.base_iterator;

public class DSIterator extends base_iterator {
    Datastore ds;

    public DSIterator(Datastore ds) {
        super(ds.getRowCount() - 1);
        this.ds = ds;
    }

    public Object value() throws Exception {
        if (cursor == -1) {
            throw new RTException("IteratorException",
                    "DSIterator is not positioned!");
        }
        return new DsRow(cursor, ds);
    }

    public Object set_value(Object obj) throws RTException {
        // throw new RTException("ReadOnlyException",
        // "DSIterator is not positioned!");
        return null;
    }

}
