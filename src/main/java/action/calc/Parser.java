/*
 * File: Parser.java
 * 
 * Created: Fri Apr 23 09:33:19 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package action.calc;

import loader.ZetaProperties;

import org.apache.log4j.Logger;

public class Parser {
    protected final static Logger log    = Logger.getLogger(Parser.class);

    static final int              SS     = 0;

    static final int              SF     = 1;

    static final int              SP     = 2;

    static final int              SUP    = 3;

    static final String[]         states = { "Parser#__START__",
            "Parser#__FINISH__", "Parser#__SP__", "Parser#__UPSS__" };

    static int                    level;

    static int                    state  = SS;

    synchronized public static OP parse(char[] text) throws Exception {
        level = 0;
        return parse1(text);
    }

    synchronized public static OP parse1(char[] text) throws Exception {
        ++level;
        OP expr = null;
        Object operand = null;
        // OP lastop = null;
        Lexemator lex = new Lexemator(text);
        int state = SS; // �������� �� ���������� ��������� :)
        try {
            while (true) { // ���� �� ��������
                if (ZetaProperties.calc_debug > 25) {
                    log.debug("~clac.Parser::parse : " + states[state] + ":"
                            + level);
                }
                switch (state) {
                // start ---------------------------------------------------
                case SS:
                    lex.next();
                    state = SP;
                    switch (lex.type()) {
                    case Lexemator.LDEF:
                        operand = new Alias(lex.as_string());
                        break;
                    case Lexemator.LTAG:
                        operand = new Func(lex.as_string(), lex.args());
                        break;
                    case Lexemator.LEXPR:
                        operand = parse1(lex.as_string().toCharArray());
                        break;
                    case Lexemator.LOP:
                        operand = lex.as_op();
                        if ((operand instanceof LABEL)
                                || (operand instanceof COMA)) {
                            state = SP;
                        }
                        else {
                            state = SUP;
                        }
                        break;
                    case Lexemator.LSTR:
                        operand = lex.as_string();
                        break;
                    case Lexemator.LNUM:
                        operand = new Double(lex.as_double());
                        break;
                    case Lexemator.LEND:
                        state = SF;
                        break;
                    default:
                        throw new ResonException(
                                "~clac.Parser::parse lexema is uncnown");
                    }
                    break;
                // stop ---------------------------------------------------
                case SF:
                    if (expr == null) {
                        expr = new EMPTYOP();
                    }
                    if (ZetaProperties.calc_debug > 25) {
                        log.debug("level " + level + " " + expr.expr());
                    }
                    return expr;
                    // create node
                    // ---------------------------------------------------
                case SP:
                    lex.next();
                    state = SS;
                    if ((expr != null) && (ZetaProperties.calc_debug > 25)) {
                        log.debug("<__SP__>level " + level + " " + expr.expr());
                    }

                    if (expr == null) {
                        if (operand instanceof OP) {
                            expr = (OP) operand;
                        }
                        else {
                            expr = new ASIS(operand);
                        }
                        if (ZetaProperties.calc_debug > 25) {
                            log.debug("<__SP__>level " + level + " "
                                    + expr.expr());
                        }
                    }
                    else if ((operand instanceof LABEL)
                            || (operand instanceof COMA)) {
                        expr = expr.setOP((OP) operand);
                    }
                    else {
                        expr.setOperand(operand);
                    }

                    switch (lex.type()) {
                    case Lexemator.LEND:
                        if (ZetaProperties.calc_debug > 25) {
                            log
                                    .debug("<END>level " + level + " "
                                            + expr.expr());
                        }
                        return expr;
                    case Lexemator.LEXPR:
                        operand = parse1(lex.as_string().toCharArray());
                        if (ZetaProperties.calc_debug > 25) {
                            log.debug("<_SP_LEXPR_>" + ((OP) operand).expr()
                                    + " level " + level + " " + expr.expr());
                        }
                        expr = expr.setOP(new COMA());
                        if (ZetaProperties.calc_debug > 25) {
                            log.debug("<_SP_LEXPR_>level " + level + " "
                                    + expr.expr());
                        }

                        // expr.setOperand(operand);
                        state = SP;
                        break;
                    case Lexemator.LOP:
                        expr = expr.setOP(lex.as_op());
                        if (ZetaProperties.calc_debug > 25) {
                            log.debug("level " + level + " " + expr.expr());
                        }
                        break;
                    case Lexemator.LDEF:
                        expr = expr.setOP(new COMA());
                        operand = new Alias(lex.as_string());
                        state = SP;
                        break;
                    case Lexemator.LNUM:
                        expr = expr.setOP(new COMA());
                        operand = new Double(lex.as_double());
                        state = SP;
                        break;
                    case Lexemator.LSTR:
                        expr = expr.setOP(new COMA());
                        operand = lex.as_string();
                        state = SP;
                        break;
                    default:
                        throw new ResonException("Bad Expression, lexema <"
                                + Lexemator.lextypes[lex.type()] + "> "
                                + lex.as_string() + ",  may be need ';' ?");
                    }
                    break;
                // parse unar op
                // ---------------------------------------------------
                case SUP:
                    if (operand instanceof Unar) {
                        lex.next();
                        switch (lex.type()) {
                        case Lexemator.LNUM:
                            ((OP) operand).setOperand(new Double(lex
                                    .as_double()));
                            break;
                        case Lexemator.LDEF:
                            ((OP) operand)
                                    .setOperand(new Alias(lex.as_string()));
                            break;
                        case Lexemator.LEXPR:
                            ((OP) operand).setOperand(parse1(lex.as_string()
                                    .toCharArray()));
                            break;
                        default:
                            throw new ResonException(
                                    "Bad Expression after unar op");
                        }
                        state = SP;
                        break;
                    }
                    else {
                        throw new ResonException("Bad Unar Operation");
                    }
                }
            }
        }
        catch (Exception e) {
        	log.error("", e);
            String s = lex.as_string();
            throw new ResonException(e.getMessage() + "\n\tLEVEL " + level
                    + ", TYPE " + Lexemator.lextypes[lex.type()] + ", VALUE "
                    + s.substring(0, (s.length() > 60) ? 60 : s.length()));
        }
        finally {
            --level;
        }
    }
}
