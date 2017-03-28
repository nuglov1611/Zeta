/*
 * File: FINALLY.java
 * 
 * Created: Thu Apr 29 15:07:37 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package core.reflection.functions;

import loader.ZetaProperties;

import org.apache.log4j.Logger;

import action.api.GlobalValuesObject;
import action.calc.CalcException;
import action.calc.OP;
import action.calc.functions.BaseExternFunction;

public class FINALLY extends BaseExternFunction {
    protected final static Logger log = Logger.getLogger(FINALLY.class);

    public Object eval() throws Exception {
        if (ZetaProperties.calc_debug > 2) {
            log.debug("~clac.funcions.FINALLY::eval");
        }
        Object result = expr.eval();
        if (ZetaProperties.calc_debug > 2) {
            log.debug("~clac.funcions.FINALLY::eval string result " + result);
        }
        Object o = OP.getAliases().get("##return_exception##");
        if (o != null) {
            CalcException exception = (CalcException) ((GlobalValuesObject) o)
                    .getValue();
            throw exception;
        }
        else {
            GlobalValuesObject ob = ((GlobalValuesObject) OP.getAliases()
                    .get("##return_value##"));
            if (ob == null) {
                return new Double(0);
            }
            o = ob.getValue();
            return o;
        }
    }

    @Override
    public String toString() {
        return "FINALLY";
    }
}
