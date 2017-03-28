/*
 * File: OP.java
 * 
 * Created: Fri Apr 23 09:29:57 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package action.calc;

import java.util.Hashtable;

import loader.ZetaProperties;

import org.apache.log4j.Logger;

class ABOVE extends Arifmetic {
    public ABOVE() {
        sym = '>';
        prior = L_PRIOR;
    }

    @Override
    double ops(double a, double b) {
        return (a > b) ? 1 : 0;
    }
}

class AND extends Logical {
    public AND() {
        sym = '&';
        prior = L_PRIOR;
    }

    @Override
    boolean ops(boolean a, boolean b) {
        return a && b;
    }
}

abstract class Arifmetic extends OP {
    @Override
    public Object eval() throws NullPointerException, ClassCastException,
            Exception {
    	try{
	        return new Double(ops(((Double) doHardOP(left)).doubleValue(),
	                ((Double) doHardOP(right)).doubleValue()));
    	}catch(ClassCastException e){
    		log.error("java.lang.String left="+left+" or right="+ right + "cannot be cast to java.lang.Double", e);
    		throw e;
    	}
    }

    abstract double ops(double a, double b);
}

class BELOW extends Arifmetic {
    public BELOW() {
        sym = '<';
        prior = L_PRIOR;
    }

    @Override
    double ops(double a, double b) {
        return (a < b) ? 1 : 0;
    }
}

class DIV extends Arifmetic {
    public DIV() {
        sym = '/';
        prior = MD_PRIOR;
    }

    @Override
    double ops(double a, double b) {
        return a / b;
    }
}

class EQU extends OP {

    public EQU() {
        sym = '=';
        sym2 = '=';
        prior = Q_PRIOR;
    }

    @Override
    public Object eval() throws Exception, NullPointerException,
            ClassCastException {
        int res;
        if (doHardOP(left).equals(doHardOP(right))) {
            res = 1;
        }
        else {
            res = 0;
        }
        if (ZetaProperties.calc_debug > 20) {
            log.debug("~calc.EQU::eval " + doOP(left) + toString()
                    + doOP(right) + " = " + res);
        }
        return new Double(res);
    }
}

class EQUABOVE extends Arifmetic {
    public EQUABOVE() {
        sym2 = '=';
        prior = L_PRIOR;
    }

    @Override
    double ops(double a, double b) {
        return (a >= b) ? 1 : 0;
    }
}

class EQUBELOW extends Arifmetic {
    public EQUBELOW() {
        sym2 = '=';
        prior = L_PRIOR;
    }

    @Override
    double ops(double a, double b) {
        return (a <= b) ? 1 : 0;
    }
}

abstract class Logical extends OP {
    @Override
    public Object eval() throws Exception, NullPointerException,
            ClassCastException {
        int res;
        if (ops(instof(doHardOP(left)), instof(doHardOP(right)))) {
            res = 1;
        }
        else {
            res = 0;
        }
        if (ZetaProperties.calc_debug > 20) {
            log.debug(doHardOP(left) + toString() + doHardOP(right) + " = "
                    + res);
        }
        return new Double(res);
    }

    public boolean instof(Object x) throws Exception, NullPointerException,
            ClassCastException {
        try {
            return ((Double) x).doubleValue() != 0;
        }
        catch (Exception e) {
            log.error("Shit happens", e);
            return ((String) x).trim().toUpperCase().compareTo("TRUE") == 0;
        }
    }

    abstract boolean ops(boolean a, boolean b);
}

class MINUS extends Arifmetic implements Unar {
    public MINUS() {
        sym = '-';
        left = new Double(0);
        prior = AM_PRIOR;
    }

    @Override
    double ops(double a, double b) {
        return a - b;
    }
}

class MUL extends Arifmetic {
    public MUL() {
        sym = '*';
        prior = MD_PRIOR;
    }

    @Override
    double ops(double a, double b) {
        return a * b;
    }
}

class NOT extends Logical implements Unar {
    public NOT() {
        sym = '!';
        left = new Double(0);
        prior = U_PRIOR;
    }

    @Override
    public String expr() {
        return "(" + toString() + expare(right) + ")";
    }

    @Override
    boolean ops(boolean a, boolean b) {
        return !b;
    }
}

class NOTEQU extends EQU {
    public NOTEQU() {
        sym2 = '=';
        prior = SET_PRIOR;
    }

    @Override
    public Object eval() throws Exception {
        if (((Double) super.eval()).doubleValue() == 0) {
            return new Double(1);
        }
        else {
            return new Double(0);
        }
    }
}



// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
/**
 * Суть операция
 */

public abstract class OP extends Tree {
    protected final static Logger           log         = Logger
                                                                .getLogger(OP.class);

    private static ThreadLocal<Hashtable<String, Object>> lAliases = new ThreadLocal<Hashtable<String,Object>>();
    private static ThreadLocal<Hashtable<String, Tree>> lFunctions = new ThreadLocal<Hashtable<String, Tree>>();
    private static ThreadLocal<Boolean> lTrace = new ThreadLocal<Boolean>(){
    	  @Override 
    	  protected Boolean initialValue() {
              return false;    
          }
    };

    private static ThreadLocal<Boolean> lSoft = new ThreadLocal<Boolean>(){
  	  @Override 
  	  protected Boolean initialValue() {
            return false;    
        }
  };
    
    public static synchronized final  Hashtable<String, Object> getAliases(){
    	return lAliases.get();
    }

    public static synchronized final  void setAliases(Hashtable<String, Object> aliases) {
    	
    	lAliases.set(aliases);
    	
        if (aliases != null) {
            Hashtable<String, Tree> o = (Hashtable<String, Tree>) aliases
                    .get("##functions##");
            if (o == null) {
                o = new Hashtable<String, Tree>();
                aliases.put("##functions##", o);
            }
            lFunctions.set(o);
        }
        else {
        	lFunctions.set(null);
        }
    }

    public static synchronized final  Hashtable<String, Tree> getFunctions(){
    	return lFunctions.get();
    }
    
    public static synchronized final  boolean getSoft(){
    	return lSoft.get();
    }
    
    public static synchronized final  void setSoft(boolean soft){
    	lSoft.set(soft);
    }
    
    public static synchronized final  boolean getTrace(){
    	return lTrace.get();
    }
    
    public static synchronized final  void setTrace(boolean trace){
    	lTrace.set(trace);
    }

    public static final int                 ALIAS_PRIOR = 12;

    public static final int                 U_PRIOR     = 10;

    public static final int                 AM_PRIOR    = 6;

    public static final int                 MD_PRIOR    = 8;

    public static final int                 L_PRIOR     = 4;

    public static final int                 Q_PRIOR     = 2;

    public static final int                 SET_PRIOR   = 1;

    public static Object doHardOP(Object a) throws Exception {
        boolean x = lSoft.get();
        lSoft.set(false);
        try {
            return doOP(a);
        }
        finally {
        	lSoft.set(x);
        }
    }

    public static Object doOP(Object x) throws Exception {
    	while (x instanceof OP) {
            if (ZetaProperties.calc_debug > 2) {
                log.debug("~calc.OP::doOP eval " + ((OP) x).expr());
            }
            if (lSoft.get() && (x instanceof Alias)) {
                break;
            }
            x = ((OP) x).eval();
        }
        return x;
    }

    public static Object doSoftOP(Object a) throws Exception {

    	boolean x = lSoft.get();
        lSoft.set(true);
        try {
            return doOP(a);
        }
        finally {
            lSoft.set(x);
        }
    }

    public static OP getOP(char sym) {
        return getOP(sym, ' ');
    }

    public static OP getOP(char sym, char sym2) {
        switch (sym2) {
        case '=':
            switch (sym) {
            case '=':
                return new EQU();
            case '>':
                return new EQUABOVE();
            case '<':
                return new EQUBELOW();
            case '!':
                return new NOTEQU();
            case '+':
                return new SETPLUS();
            case '-':
                return new SETMINUS();
            case '*':
                return new SETMUL();
            case '/':
                return new SETDIV();
            case '&':
                return new SETAND();
            case '|':
                return new SETOR();
            default:
                return null;
            }
        case ' ':
            switch (sym) {
            case '=':
                return new SET();
            case '>':
                return new ABOVE();
            case '<':
                return new BELOW();
            case '+':
                return new PLUS();
            case '-':
                return new MINUS();
            case '*':
                return new MUL();
            case '/':
                return new DIV();
            case '&':
                return new AND();
            case '|':
                return new OR();
            case '!':
                return new NOT();
            case ';':
                return new COMA();
            case ',':
                return new COMA();
            default:
                return null;
            }
        default:
            return null;
        }
    }

    public static String printArray(Object[] obj) {
        if (obj == null) {
            return "[null]";
        }
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        int i = 0;
        int len = (obj).length;
        if (len == 0) {
            return "[]";
        }
        while (true) {
            Object x = (obj)[i];
            if (x instanceof Object[]) {
                sb.append(printArray((Object[]) x));
            }
            else {
                sb.append(x);
            }
            ++i;
            if (i < len) {
                sb.append(",");
            }
            else {
                break;
            }
        }
        sb.append("]");
        return sb.toString();
    }

    int  prior = 0;

    char sym   = ' ';

    char sym2  = ' ';

    public abstract Object eval() throws Exception;

    public synchronized Object eval(Hashtable<String, Object> aliases)
            throws Exception {

    	boolean xsoft = lSoft.get();
        lSoft.set(false);
        Hashtable<String, Object> foo = lAliases.get();
        setAliases(aliases);
        boolean xtrace = lTrace.get();
        lTrace.set(false);
        try {
            return eval();
        }
        catch (Exception e) {
            log.error("~calc.OP::eval EVAL_EXCEPTION::", e);
            throw e;
        }
        finally {
            setAliases(foo);
            lSoft.set(xsoft);
            lTrace.set(xtrace);
        }
    }

    public Object evalLabel(String label) throws Exception {
        if (ZetaProperties.calc_debug > 2) {
            log.debug("~calc.OP::evalLabel label " + label);
        }
        throw new ResonException("label " + label + " Not found");
    }

    String expare(Object x) {
        if (x == null) {
            return "#NULL#";
        }
        if (x instanceof OP) {
            return ((OP) x).expr();
        }
        return x.toString();
    }

    public String expr() {
        return "(" + expare(left) + " " + toString() + " " + expare(right)
                + ")";
    }

    public void getAliases(Hashtable<String, Object> h) throws Exception {
        if ((left != null) && (left instanceof OP)) {
            ((OP) left).getAliases(h);
        }
        if ((right != null) && (right instanceof OP)) {
            ((OP) right).getAliases(h);
        }
    }

    public OP setOP(OP op) {
        if (ZetaProperties.calc_debug > 2) {
            log.debug("~calc.OP::setOP\n\top=" + op.expr() + "\n\tthis="
                    + expr());
        }
        if (prior < op.prior) {
            if (ZetaProperties.calc_debug > 2) {
                log.debug("~calc.OP::setOP\n\tRIGHT=" + op.expr() + "\n\tTOP="
                        + this);
            }
            // оптимизация остсточной рекурсии
            if ((right != null) && (right instanceof OP)
                    && !(right instanceof Const)) {
                right = ((OP) right).setOP(op);
                return this;
            }
            else {
                op.left = right;
                right = op;
                return this;
            }
        }
        else {
            if (ZetaProperties.calc_debug > 2) {
                log
                        .debug("~calc.OP::setOP\n\tTOP=" + op + "\n\tLEFT="
                                + expr());
            }
            op.left = this;
            return op;
        }
    }

    public void setOperand(Object foo) throws Exception {
        if (right != null) {
            if ((right instanceof Const) || !(right instanceof OP)) {
                throw new Exception("try set operand in Not null point");
            }
            else if (right instanceof OP) {
                ((OP) right).setOperand(foo);
            }
        }
        else {
            right = foo;
        }
    }

    @Override
    public String toString() {
        return "" + sym + sym2;
    }
}

class OR extends Logical {
    public OR() {
        sym = '|';
        prior = L_PRIOR;
    }

    @Override
    boolean ops(boolean a, boolean b) {
        return a || b;
    }
}

class PLUS extends OP {
    public PLUS() {
        sym = '+';
        left = new Double(0);
        prior = AM_PRIOR;
    }

    @Override
    public Object eval() throws NullPointerException, ClassCastException,
            Exception {
        Object a = doHardOP(left);
        Object b = doHardOP(right);
        if(a == null)
        	a = "";
        if(b== null)
        	b ="";
        if (a instanceof Double) {
            return new Double(((Double) a).doubleValue()
                    + ((Double) b).doubleValue());
        }
        else if (a instanceof String) {
            if (b instanceof Object[]) {
                b = printArray((Object[]) b);
            }
            return (String) a + b.toString();
        }
        else {
            throw new ResonException(
                    "~calc.PLUS::eval  PLUS may added only\n\t"
                            + "String+Double\n\t" + "String+String\n\t"
                            + "Double+Double");
        }
    }
}

class SET extends OP {
    public static Object set(Object a, Object b) throws NullPointerException,
            ClassCastException, Exception {
        if (ZetaProperties.calc_debug > 20) {
            log.debug("~calc.SET::static#set a="
                    + ((a instanceof OP) ? ((OP) a).expr() : a) + " b="
                    + ((b instanceof OP) ? ((OP) b).expr() : b));
        }
        while (!(a instanceof Alias)) {
            if (ZetaProperties.calc_debug > 20) {
                log.debug("~calc.SET::static#set a is " + a);
            }
            if (a instanceof OP) {
                a = ((OP) a).eval();
            }
            else {
                throw new Exception("Can't set Constant to new value");
            }
        }
        ((Alias) a).setValue(doOP(b));
        return ((Alias) a).getValue();
    }

    public SET() {
        sym = '=';
        prior = SET_PRIOR;
    }

    @Override
    public Object eval() throws Exception {
        return set(left, right);
    }
}

class SETAND extends AND {
    public SETAND() {
        sym2 = '=';
        prior = SET_PRIOR;
    }

    @Override
    public Object eval() throws Exception {
        return SET.set(left, super.eval());
    }
}

class SETDIV extends DIV {
    public SETDIV() {
        sym2 = '=';
        prior = SET_PRIOR;
    }

    @Override
    public Object eval() throws Exception {
        return SET.set(left, super.eval());
    }
}

class SETMINUS extends MINUS {
    public SETMINUS() {
        sym2 = '=';
        prior = SET_PRIOR;
    }

    @Override
    public Object eval() throws Exception {
        return SET.set(left, super.eval());
    }
}

class SETMUL extends MUL {
    public SETMUL() {
        sym2 = '=';
        prior = SET_PRIOR;
    }

    @Override
    public Object eval() throws Exception {
        return SET.set(left, super.eval());
    }
}

class SETOR extends OR {
    public SETOR() {
        sym2 = '=';
        prior = SET_PRIOR;
    }

    @Override
    public Object eval() throws Exception {
        return SET.set(left, super.eval());
    }
}

class SETPLUS extends PLUS {
    public SETPLUS() {
        sym2 = '=';
        prior = SET_PRIOR;
    }

    @Override
    public Object eval() throws Exception {
        return SET.set(left, super.eval());
    }
}
