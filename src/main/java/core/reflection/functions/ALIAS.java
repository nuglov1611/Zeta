/*
 * File: ALIAS.java
 * 
 * Created: Mon May 3 14:36:57 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package core.reflection.functions;

import action.calc.Alias;
import action.calc.OP;
import action.calc.ResonException;
import action.calc.functions.BaseExternFunction;
import loader.ZetaProperties;
import org.apache.log4j.Logger;

public class ALIAS extends BaseExternFunction {
    protected final static Logger log = Logger.getLogger(ALIAS.class);

    public Object eval() throws Exception {
        if (ZetaProperties.calc_debug > 2) {
            log.debug("~calc.functions.ALIAS::eval");
        }
        Object o = OP.doHardOP(expr);
        String reson = "reson: alias value is " + o + "\n\t" + "Object: "
                + toString() + "\n";

        if (o instanceof String) {
            return new Alias(((String) o).trim().toUpperCase()).getValue();
        } else {
            throw new ResonException(
                    "~calc.functions.I::eval\n\ttype of args is't String \n\t"
                            + reson);
        }
    }
}
