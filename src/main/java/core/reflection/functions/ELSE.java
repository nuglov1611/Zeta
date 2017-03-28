/*
 * File: ELSE.java
 * 
 * Created: Mon Apr 26 13:58:07 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package core.reflection.functions;

import action.calc.ExternFunction;
import action.calc.Lexemator;
import action.calc.OP;
import action.calc.Parser;
import loader.ZetaProperties;
import org.apache.log4j.Logger;

import java.util.Hashtable;

public class ELSE implements ExternFunction {
    private static final Logger log = Logger.getLogger(ELSE.class);

    OP doing = null;

    public Object eval() throws Exception {
        if (ZetaProperties.calc_debug > 2) {
            log.debug("~clac.funcions.ELSE::eval");
        }
        return doing.eval();

    }

    public void getAliases(Hashtable<String, Object> h) throws Exception {
        doing.getAliases(h);
    }

    public void init(String arg) throws Exception {
        Lexemator lex = new Lexemator(arg.toCharArray());
        lex.next();
        if (ZetaProperties.calc_debug > 2) {
            log.debug("~clac.funcions.ELSE::init LEXPR");
        }
        if (lex.type() == Lexemator.LEXPR) {
            doing = Parser.parse1(lex.as_string().toCharArray());
        } else {
            throw new Exception();
        }
        if (ZetaProperties.calc_debug > 2) {
            log.debug("~clac.funcions.ELSE::init end of parse");
        }
    }

    @Override
    public String toString() {
        return "ELSE";
    }
}
