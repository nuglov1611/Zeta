/*
 * File: TRY.java
 * 
 * Created: Thu Apr 29 13:50:32 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package core.reflection.functions;

import action.api.ARGV;
import action.api.GlobalValuesObject;
import action.api.RTException;
import action.calc.*;
import loader.ZetaProperties;
import org.apache.log4j.Logger;

import java.util.Hashtable;

public class TRY implements ExternFunction {
    private static final Logger log = Logger.getLogger(TRY.class);

    OP expr = null;

    OP catcher = null;

    public Object eval() throws Exception {
        if (ZetaProperties.calc_debug > 2) {
            log.debug("~clac.funcions.TRY::eval");
        }
        try {
            Object result = expr.eval();
            GlobalValuesObject gvo = new ARGV();
            OP.getAliases().put("##return_value##", gvo);
            gvo.setValue(result);
        } catch (NullPointerException e) {
            //log.error("Shit happens", e);
            GlobalValuesObject gvo = new ARGV();
            OP.getAliases().put("##exception##", gvo);
            gvo.setValue(new RTException("NULLEXCEPTION",
                    "element not initialized"));
            return OP.doOP(catcher);
        } catch (RTException e) {
            //log.error("Shit happens", e);
            GlobalValuesObject gvo = new ARGV();
            OP.getAliases().put("##exception##", gvo);
            gvo.setValue(e);
            return OP.doOP(catcher);
        } catch (CalcException e) {
            //log.error("Shit happens", e);
            GlobalValuesObject gvo = new ARGV();
            OP.getAliases().remove("##exception##");
            OP.getAliases().put("##return_exception##", gvo);
            gvo.setValue(e);
            return OP.doOP(catcher);
        }
        OP.getAliases().remove("##exception##");
        OP.getAliases().remove("##return_exception##");
        return OP.doOP(catcher);
    }

    public void getAliases(Hashtable<String, Object> h) throws Exception {
        expr.getAliases(h);
        if (catcher != null) {
            catcher.getAliases(h);
        }
    }

    public void init(String arg) throws Exception {
        Lexemator lex = new Lexemator(arg.toCharArray());
        lex.next();
        if (ZetaProperties.calc_debug > 2) {
            log.debug("~clac.funcions.TRY::init LEXPR");
        }
        if (lex.type() == Lexemator.LEXPR) {
            expr = Parser.parse1(lex.as_string().toCharArray());
        } else {
            throw new Exception();
        }
        lex.next();
        if (ZetaProperties.calc_debug > 2) {
            log.debug("~clac.funcions.TRY::init LED/LTAG");
        }
        switch (lex.type()) {
            case Lexemator.LTAG:
                catcher = new Func(lex.as_string(), lex.args());
                break;
            default:
                throw new RTException("SYNTAX", "Must has catch and/or finally");
        }
        if (ZetaProperties.calc_debug > 2) {
            log.debug("~clac.funcions.TRY::init end of parse");
        }
    }

    @Override
    public String toString() {
        return "TRY";
    }
}
