/*
 * File: CalcLan.java
 * 
 * Created: Sun Nov 14 21:11:04 1999
 * 
 * Copyright (c) by Alexey Chen
 */

package action.calc;

import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import action.api.RTException;

/*
 * ~~~~ calc.language.calc = xyz.chen.calc.CalcLan
 */

public class CalcLan implements __calc__ {
    protected final static Logger log  = Logger.getLogger(CalcLan.class);

    char[]                        text = null;

    OP                            expr = null;

    public CalcLan() {
    }

    public Object eval(Hashtable<String, Object> aliases) throws Exception {
        parse();
        Object a = null;
        try {
            try {
                a = expr.eval(aliases);
            }
            catch (ReturnException e) {
//                log.error("Shit happens!", e);
                throw new RTException("ReturnException",
                        "return without X function");
            }
            catch (BreakException e) {
//                log.error("Shit happens!!!", e);
                throw new RTException("BreakException", "break out from loop");
            }
            catch (ContinueException e) {
//                log.error("Shit happens", e);
                throw new RTException("CountinueException",
                        "continue out from loop");
            }
            catch (GotoException e) {
//                log.error("Shit happens", e);
                throw new RTException("GotoException", "goto " + e.label
                        + " out from X function");
            }
            catch (CalcException e) {
//                log.error("Shit happens", e);
                throw new RTException("CalcException", e.getMessage());
            }
            catch (NullPointerException e) {
//                log.error("Shit happens", e);
                throw new RTException("NullException",
                        "may be not initializet any alement? ");
            }
        }
        catch (RTException e) {
            log.error("Shit happens", e);
            String s = new String(text);
            s = ((s.length() > 80) ? s.substring(0, 80) : s).replace('\n', ' ');
            log.debug("!------\n" + s + "\n!------");
            log.debug("Calculator RunTime Exception:\n\t"+"TYPE: " + e.type + "\n\t"
                    + "RESON: " + e.getMessage() + "\ntrap______________"
                    + e.trap);
            throw new Exception(e.getMessage());
        }
        return a;
    }

    public String[] getAliases() throws Exception {
        parse();
        Hashtable<String, Object> h = new Hashtable<String, Object>();
        expr.getAliases(h);
        String[] s = new String[h.size()];
        int i = 0;
        for (Enumeration<String> e = h.keys(); e.hasMoreElements(); ++i) {
            s[i] = e.nextElement();
            if (s[i].startsWith("G.")) {
                s[i] = s[i].substring(s[i].indexOf('.') + 1);
            }
        }
        return s;
    }

    public String getExpression() {
        return new String(text);
    }

    public void initExpr(String s) throws Exception {
        text = s.toCharArray();
    }

    public OP parse() throws Exception {
        if (expr == null) {
            try {
                expr = Parser.parse(text);
            }
            catch (Exception e) {
                String s = new String(text);
                s = ((s.length() > 80) ? s.substring(0, 80) : s).replace('\n',
                        ' ');
                log.debug("!------\n" + s + "\n!------");
                log.error("Calculator Paser Exception:\n\t", e);
                throw new Exception("Parser exception");
            }
            return expr;
        }
        else {
            return expr;
        }
    }
}
