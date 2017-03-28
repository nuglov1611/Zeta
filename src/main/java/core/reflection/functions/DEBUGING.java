/*
 * File: DEBUGING.java
 * 
 * Created: Mon May 3 14:08:39 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package core.reflection.functions;

import action.calc.OP;
import action.calc.functions.BaseExternFunction;
import loader.ZetaProperties;
import org.apache.log4j.Logger;


public class DEBUGING extends BaseExternFunction {
    protected final static Logger log = Logger.getLogger(DEBUGING.class);

    static final String fun = "FUN DEBUG : ";

    static boolean debuging = true;

    public Object eval() throws Exception {
        if (ZetaProperties.calc_debug > 2) {
            log.debug(fun + " " + expr.expr() + "  OP.soft=" + OP.getSoft());
        }
        Object o;
        o = OP.doHardOP(expr);
        if (debuging) {
            if (o instanceof Object[]) {
                o = OP.printArray((Object[]) o);
            }
        }
        log.debug("#" + o);
        return o;
    }
}
