/*
 * File: ASIS.java
 * 
 * Created: Mon Apr 26 10:14:32 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package action.calc;

class ASIS extends OP {
    public ASIS(Object x) {
        left = x;
        prior = OP.ALIAS_PRIOR;
    }

    @Override
    public Object eval() throws Exception {
        return left;
    }
}
