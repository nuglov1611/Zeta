/*
 * File: CATCH.java
 * 
 * Created: Thu Apr 29 13:50:38 1999
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

import action.api.ARGV;
import action.api.GlobalValuesObject;
import action.api.RTException;
import action.calc.CalcException;
import action.calc.ExternFunction;
import action.calc.Func;
import action.calc.Lexemator;
import action.calc.OP;
import action.calc.Parser;


public class CATCH implements ExternFunction {
    private static final Logger log     = Logger.getLogger(CATCH.class);

    OP                          expr    = null;

    OP                          doing   = null;

    OP                          catcher = null;

    public Object eval() throws Exception {
        Object rets = new Double(0);
        if (ZetaProperties.calc_debug > 2) {
            log.debug("~clac.funcions.CATCH::eval");
        }
        Object result = expr.eval();
        if (result instanceof String) {
            if (ZetaProperties.calc_debug > 2) {
                log.debug("~clac.funcions.CATCH::eval string result " + result);
            }
            Object o = OP.getAliases().get("##exception##");
            if (o != null) {
                String exception = ((RTException) ((GlobalValuesObject) o)
                        .getValue()).type;
                String rst = ((String) result).trim().toUpperCase();
                if (OP.getTrace()) {
                    log.debug(Func.stab + Func.level + ":CATCH '" + rst
                            + "'/ check exception " + exception);
                }
                if (rst.equals(exception) || rst.equals("ANY")) {
                    try {
                        if (ZetaProperties.calc_debug > 2) {
                            log.debug("~clac.funcions.CATCH::eval doing\n\t"
                                    + doing.expr());
                        }
                        rets = doing.eval();
                    }
                    catch (CalcException e) {
                        if (ZetaProperties.calc_debug > 2) {
                            log.error("doing CalcException", e);
                        }
                        GlobalValuesObject gvo = new ARGV();
                        OP.getAliases().put("##return_exception##", gvo);
                        gvo.setValue(e);
                    }
                    catch (Exception e) {
                        if (ZetaProperties.calc_debug > 2) {
                            log.error("doing Exception\n\t", e);
                        }
                        throw e;
                    }
                    OP.getAliases().remove("##exception##");
                }
                if (catcher != null) {
                    return OP.doOP(catcher);
                }
                else {
                    o = OP.getAliases().get("##exception##");
                    if (o != null) {
                        throw (Exception) ((GlobalValuesObject) o).getValue();
                    }
                    o = OP.getAliases().get("##return_exception##");
                    if (o != null) {
                        throw (Exception) ((GlobalValuesObject) o).getValue();
                    }
                }
            }
            else if (catcher != null) {
                return OP.doOP(catcher);
            }
            else {
                o = OP.getAliases().get("##return_exception##");
                if (o != null) {
                    throw (Exception) ((GlobalValuesObject) o).getValue();
                }
                else {
                    o = ((ARGV) OP.getAliases().get("##return_value##")).getValue();
                    return o;
                }
            }
        }
        else {
            throw new Exception();
        }
        return rets;
    }

    public void getAliases(Hashtable<String, Object> h) throws Exception {
        expr.getAliases(h);
        doing.getAliases(h);
        if (catcher != null) {
            catcher.getAliases(h);
        }
    }

    public void init(String arg) throws Exception {
        Lexemator lex = new Lexemator(arg.toCharArray());
        lex.next();
        if (ZetaProperties.calc_debug > 2) {
            log.debug("~clac.funcions.CATCH::init LEXPR");
        }
        if (lex.type() == Lexemator.LEXPR) {
            expr = Parser.parse1(lex.as_string().toCharArray());
        }
        else {
            throw new Exception();
        }
        lex.next();
        if (ZetaProperties.calc_debug > 2) {
            log.debug("~clac.funcions.CATCH::init LEXPR for doing");
        }
        if (lex.type() == Lexemator.LEXPR) {
            doing = Parser.parse1(lex.as_string().toCharArray());
        }
        else {
            throw new Exception();
        }
        lex.next();
        if (ZetaProperties.calc_debug > 2) {
            log.debug("~clac.funcions.CATCH::init LED/LTAG");
        }
        switch (lex.type()) {
        case Lexemator.LEND:
            break;
        case Lexemator.LTAG:
            catcher = new Func(lex.as_string(), lex.args());
            break;
        default:
            throw new Exception();
        }
        if (ZetaProperties.calc_debug > 2) {
            log.debug("~clac.funcions.CATCH::init end of parse");
        }
    }

    @Override
    public String toString() {
        return "CATCH";
    }
}
