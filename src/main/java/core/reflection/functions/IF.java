/*
 * File: IF.java
 * 
 * Created: Fri Apr 23 13:07:23 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package core.reflection.functions;

import java.util.Hashtable;

import loader.ZetaProperties;

import org.apache.log4j.Logger;

import action.api.RTException;
import action.calc.ExternFunction;
import action.calc.Func;
import action.calc.Lexemator;
import action.calc.OP;
import action.calc.Parser;

public class IF implements ExternFunction {
    private static final Logger log   = Logger.getLogger(IF.class);

    OP                          expr  = null;

    OP                          doing = null;

    OP                          elser = null;

    public Object eval() throws Exception {
        if (ZetaProperties.calc_debug > 2) {
            log.debug("~clac.funcions.IF::eval");
        }
        Object result = expr.eval();
        if (result instanceof Double) {
            if (ZetaProperties.calc_debug > 2) {
                log.debug("~clac.funcions.IF::eval double result " + result);
            }
            if (((Double) result).doubleValue() == 1) {
                return doing.eval();
            }
            else if (elser != null) {
                return elser.eval();
            }
        }
        else if (result instanceof String) {
            if (ZetaProperties.calc_debug > 2) {
                log.debug("~clac.funcions.IF::eval string result " + result);
            }
            if (((String) result).trim().toUpperCase().compareTo("TRUE") == 0) {
                return doing.eval();
            }
            else if (elser != null) {
                return elser.eval();
            }
        }
        else {
            throw new RTException("SYNTAX", "RESALT of expression in IF is "
                    + result);
        }
        return new Double(0);
    }

    public void getAliases(Hashtable<String, Object> h) throws Exception {
        if (doing != null) {
            doing.getAliases(h);
        }
        if (elser != null) {
            elser.getAliases(h);
        }
        if (expr != null) {
            expr.getAliases(h);
        }
    }

    public void init(String arg) throws Exception {
        Lexemator lex = new Lexemator(arg.toCharArray());
        lex.next();
        if (ZetaProperties.calc_debug > 2) {
            log.debug("~clac.funcions.IF::init LEXPR");
        }
        if (lex.type() == Lexemator.LEXPR) {
            expr = Parser.parse1(lex.as_string().toCharArray());
        }
        else {
            throw new Exception();
        }
        lex.next();
        if (ZetaProperties.calc_debug > 2) {
            log.debug("~clac.funcions.IF::init LEXPR for doing");
        }
        if (lex.type() == Lexemator.LEXPR) {
            doing = Parser.parse1(lex.as_string().toCharArray());
        }
        else {
            throw new Exception();
        }
        lex.next();
        if (ZetaProperties.calc_debug > 2) {
            log.debug("~clac.funcions.IF::init LED/LTAG");
        }
        switch (lex.type()) {
        case Lexemator.LEND:
            break;
        case Lexemator.LTAG:
            elser = new Func(lex.as_string(), lex.args());
            break;
        default:
            throw new Exception();
        }
        if (ZetaProperties.calc_debug > 2) {
            log.debug("~clac.funcions.IF::init end of parse");
        }
    }

    @Override
    public String toString() {
        return "IF";
    }
}
