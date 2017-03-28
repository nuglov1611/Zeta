/*
 * File: Calc.java
 * 
 * Created: Mon Apr 19 13:56:58 1999
 * 
 * 
 * 
 * Author: Alexey Chen
 */

package action.api;

import action.calc.CalcLan;
import org.apache.log4j.Logger;

import java.util.Hashtable;


/*
 * Класс реализующий калькулятор
 */

/*
 * ~~~~ calc.language=zscript
 */

public class Calc extends ScriptApi {
    private static final Logger log = Logger.getLogger(Calc.class);


    String code = null;

    CalcLan cl = null;

    protected Calc(String s) {
        code = s;
    }

    public Object eval(Hashtable<String, Object> aliases) throws Exception {
        parse(aliases);
        return cl.eval(aliases);
    }

    public String[] getAliases() throws Exception {
        if (cl == null) {
            try {
                cl = new CalcLan();
            } catch (Exception e) {
                log.error("cant load language", e);
                throw new Exception("cant load language :\n\t" + e.getMessage());
            }
            cl.initExpr(code);
        }
        return cl.getAliases();
    }

    public String getExpression() {
        return code;
    }

    public void parse(Hashtable<String, Object> aliases) throws Exception {
        if (cl == null) {
            cl = new CalcLan();
            cl.initExpr(code);
        }
    }

    @Override
    public String toString() {
        return "Calc expr \n" + code;
    }
}
