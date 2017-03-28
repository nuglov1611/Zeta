/*
 * File: WHILE.java
 * 
 * Created: Wed Apr 28 12:27:12 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package core.reflection.functions;

import action.calc.*;
import loader.ZetaProperties;
import org.apache.log4j.Logger;

import java.util.Hashtable;

public class WHILE implements ExternFunction {
    protected final static Logger log = Logger.getLogger(WHILE.class);

    OP expr = null;

    OP doing = null;

    public Object eval() throws Exception {
        if (ZetaProperties.calc_debug > 2) {
            log.debug("~clac.funcions.WHILE::eval");
        }

        while (true) {
            Object result = expr.eval();
            boolean f = false;
            if (result instanceof Double) {
                if (ZetaProperties.calc_debug > 2) {
                    log.debug("~clac.funcions.WHILE::eval double result "
                            + result);
                }
                if (((Double) result).doubleValue() == 1) {
                    f = true;
                } else {
                    break;
                }
            } else if (result instanceof String) {
                if (ZetaProperties.calc_debug > 2) {
                    log.debug("~clac.funcions.WHILE::eval string result "
                            + result);
                }
                if (((String) result).trim().toUpperCase().compareTo("TRUE") == 0) {
                    f = true;
                } else {
                    break;
                }
            } else {
                throw new ResonException("~clac.funcions.WHILE::eval \n\t"
                        + "result type must be logical");
            }
            if (f) {
                try {
                    doing.eval();
                } catch (ContinueException e) {
                } catch (BreakException e) {
                }
            }
        }
        return new Double(0);
    }

    public void getAliases(Hashtable<String, Object> h) throws Exception {
        expr.getAliases(h);
        if (doing != null) {
            doing.getAliases(h);
        }
    }

    public void init(String arg) throws Exception {
        Lexemator lex = new Lexemator(arg.toCharArray());
        lex.next();
        if (ZetaProperties.calc_debug > 2) {
            log.debug("~clac.funcions.WHILE::init LEXPR");
        }
        if (lex.type() == Lexemator.LEXPR) {
            expr = Parser.parse1(lex.as_string().toCharArray());
        } else {
            throw new Exception();
        }
        lex.next();
        if (ZetaProperties.calc_debug > 2) {
            log.debug("~clac.funcions.WHILE::init LEXPR for doing");
        }
        if (lex.type() == Lexemator.LEXPR) {
            doing = Parser.parse1(lex.as_string().toCharArray());
        } else {
            throw new Exception();
        }
        lex.next();
        if (ZetaProperties.calc_debug > 2) {
            log.debug("~clac.funcions.WHILE::init end of parse");
        }
    }

    @Override
    public String toString() {
        return "WHILE";
    }
}
