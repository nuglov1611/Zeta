/*
 * File: Lexemator.java
 * 
 * Created: Fri Apr 23 09:33:30 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package action.calc;

import loader.ZetaProperties;
import org.apache.log4j.Logger;

/*
 * ����� ��������� ����������� ����������
 */
public class Lexemator {
    private static final Logger log = Logger.getLogger(Lexemator.class);

    /**
     * ������������� - �����, ���, ���� ....
     */
    public static final int LDEF = 0;

    /**
     * =
     */
    public static final int LEQU = 1;

    /**
     * ������
     */
    public static final int LSTR = 3;

    /**
     * �������������� �����
     */
    public static final int LNUM = 4;

    /**
     * )
     */
    public static final int LCBK = 5;

    /**
     * (
     */
    public static final int LOBK = 6;

    /**
     * ����� ������ ������
     */
    public static final int LEND = 7;

    /**   */
    public static final int LOP = 8;

    /**  */
    public static final int LEXPR = 9;

    /** */
    public static final int LTAG = 10;

    public static final String lextypes[] = {"@@_identificator_@@",
            "@@_EQU_@@", "", "@@_String_@@", "@@_NUMBER_@@", "@@_')'_@@",
            "@@_'('_@@", "@@_LEND_@@", "@@_OPERATION_@@", "@@_Expression_@@",
            "@@_TAG_@@"};

    static final String[] states = {"__START__", "__IDENTIFER__",
            "__FINISH__", "__STRING__", "__UNKNOWN__", "__NUMBER__",
            "__UNKNOWN__", "__COMENT__", "__OPERATION__", "__TAG__"};

    static final int SS = 0;

    static final int SAZ = 1;

    static final int SF = 2;

    static final int SSTR = 3;

    static final int SNUM = 5;

    static final int SCOMENT = 7;

    static final int SOP = 8;

    static final int STAG = 9;

    char[] text;

    int counter;

    int mytype;

    StringBuffer mystring;

    OP op;

    String args;

    /**
     * ����������� ���������������� � ���������� - ����������� �������
     */
    public Lexemator(char[] text) {
        this.text = new char[text.length + 1];
        // System.arraycopy(text,0,this.text,0,text.length);
        this.text[text.length] = ' ';
        counter = 0;
        op = null;
        for (int i = 0; i < text.length; ) {
            if (text[i] == '\'') {
                this.text[i] = text[i++];
                try {
                    while (text[i] != '\'') { // ���� �� ����� ������
                        if (text[i] == '\\') {
                            this.text[i] = text[i++];
                        }
                        this.text[i] = text[i++];
                    }
                    this.text[i] = text[i++];
                } catch (Exception e) {
                    log.error("Shit happens", e);
                }
            } else if (text[i] == '#') {
                while (i < text.length && text[i] != '\n') {
                    this.text[i] = ' ';
                    ++i;
                }
            } else {
                this.text[i] = text[i];
                ++i;
            }
        }
    }

    public String args() {
        return args;
    }

    /**
     * ���� ������� ��� �������������� �����
     */
    public double as_double() throws Exception {
        String s = mystring.toString();
        if (ZetaProperties.calc_debug > 26) {
            log.debug("str is " + s + "!");
        }
        try {
            Double d = Double.valueOf(s);
            return d.doubleValue();
        } catch (NumberFormatException e) {
            log.error("Shit happens", e);
            if (ZetaProperties.calc_debug > 26) {
                log.debug("~clac.Lexemator::as_double " + e);
            }
            throw new Exception();
        }
    }

    /**
     * ���� ������� ��� ����� �����
     */
    public int as_int() throws Exception {
        String s = mystring.toString();
        if (ZetaProperties.calc_debug > 26) {
            log.debug("str is " + s + "!");
        }
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            log.error("Shit happens", e);
            if (ZetaProperties.calc_debug > 26) {
                log.debug("~clac.Lexemator::as_int " + e);
            }
            throw new Exception();
        }
    }

    public OP as_op() throws Exception {
        return op;
    }

    /**
     * ���� ������� ��� ������
     */
    public String as_string() {
        String s;
        if (mytype == LDEF) {
            s = mystring.toString().toUpperCase();
        } else {
            s = mystring.toString();
        }
        if (ZetaProperties.calc_debug > 26) {
            log.debug("~clac.Lexemator::as_string ? " + s);
        }
        return s;
    }

    boolean is_AZ(char x) {
        return (x != ':')
                && (Character.isLetter(x) || (x == '_') || (x == '.')
                || (x == '@') || (x == '$'));
    }

    boolean is_BK(char x) {
        return (is_OBK(x) || is_CBK(x));
    }

    boolean is_CBK(char x) {
        return (x == ')');
    }

    boolean is_DIL(char x) {
        return Character.isWhitespace(x);
    }

    boolean is_EQU(char x) {
        return (x == '=');
    }

    boolean is_NUM(char x) {
        return Character.isDigit(x);
    }

    boolean is_OBK(char x) {
        return (x == '(');
    }

    boolean is_op(char x) {
        switch (x) {
            case '!': // System.out.println("test as "+'!');
            case '=': // System.out.println("test as "+'=');
            case '>': // System.out.println("test as "+'>');
            case '<': // System.out.println("test as "+'<');
            case '&': // System.out.println("test as "+'&');
            case '|': // System.out.println("test as "+'|');
            case '+': // System.out.println("test as "+'+');
            case '-': // System.out.println("test as "+'-');
            case '*': // System.out.println("test as "+'*');
            case '/': // System.out.println("test as "+'/');
            case ',': // System.out.println("test as "+'/');
            case ';': // System.out.println("test as "+'/');
                return true;
            default:
                return false;
        }
    }

    boolean is_S(char x) {
        return (x == '\'');
    }

    boolean is_XDIL(char x) {
        return (is_DIL(x) || is_EQU(x) || is_BK(x) || is_S(x) || (x == '/') || (is_op(x)));
    }

    /**
     * ����� ���������� ��������� �������
     */
    public void next() throws Exception {
        int state = SS; // �������� �� ���������� ��������� :)
        mystring = new StringBuffer();
        if (ZetaProperties.calc_debug > 26) {
            log.debug("~clac.Lexemator::next : begin parsing");
        }
        try { // ����������� ����� ������ ��������
            char ch = text[counter];
            while (true) { // ���� �� ��������
                if (ch == '$') {
                    ch = text[++counter];
                    state = STAG;
                }
                if (ZetaProperties.calc_debug > 26) {
                    log.debug("~clac.Lexemator::next : state=" + states[state]
                            + ":" + counter + ":" + ch);
                }
                switch (state) { // ��������� �� ��������� ��������
                    // start ---------------------------------------------------
                    case SS: // ��������� ���������

                        // �������� ���� �� �������� �������
                        if (is_DIL(ch)) { // ���� �����������
                            state = SS; // ����� �� �����
                            ch = text[++counter];
                        }
                        // �������� �������
                        // �������� �� ��������
                        else if (is_op(ch)) {
                            state = SOP; // ��������
                        } else if (is_AZ(ch)) {
                            state = SAZ; // �������������
                        } else if (ch == '\'') {
                            state = SSTR; // ������
                        } else if (is_OBK(ch)) { /* ( */
                            ++counter;
                            mystring = readExpr();
                            ch = text[counter];
                            state = SF;
                            mytype = LEXPR;
                        }
                        // �������� �� �����
                        else if (is_NUM(ch)) {
                            state = SNUM; // �����
                        } else { // ���-�� ����������
                            ch = text[counter++];
                            throw new ResonException("Lexemator: '" + ch + "'");
                        }
                        break;
                    // ������ �������������� ��� �����
                    // --------------------------------------
                    case SAZ: // �������������
                        while (is_AZ(ch) || is_NUM(ch)) {
                            // ���� ������� ��������
                            mystring.append(ch); // �������� � ������ �������
                            ch = text[++counter];
                        }
                        if (ch == ':') {
                            op = new LABEL(mystring.toString());
                            mytype = LOP;
                            state = SF;
                            ch = text[++counter];
                        } else if (is_XDIL(ch)) { // ����� ��������������
                            mytype = LDEF;
                            state = SF; // �����
                        } else {
                            throw new ResonException("Lexemator: '" + ch + "'");
                        }
                        break;
                    // ������ ���� -------------------------------------------
                    case STAG: // ���
                        while (is_AZ(ch) || is_NUM(ch) || (ch == '(')) {
                            // ���� ������� ��������
                            if (ch == '(') {
                                ++counter;
                                mystring.append("(" + readExpr() + ")");
                                --counter;
                            } else {
                                mystring.append(ch); // �������� � ������ �������
                            }
                            ch = text[++counter];
                        }
                        if (is_XDIL(ch)) { // ����� ����
                            mytype = LTAG;
                            state = SF; // �����
                        } else {
                            throw new ResonException("Lexemator: '" + ch + "'");
                        }
                        args = new String(text, counter, text.length - counter);
                        if (ZetaProperties.calc_debug > 2) {
                            log.debug("~clac.Lexemator::next : TAG " + mystring
                                    + "args " + args);
                        }
                        counter = text.length;
                        break;
                    // ������ �����
                    // ----------------------------------------------------
                    case SNUM: // �����
                        while (is_NUM(ch)) {
                            // ���� �����
                            mystring.append(ch); // �������� � ������ �������
                            ch = text[++counter];
                        }
                        if (is_XDIL(ch)) {
                            mytype = LNUM;
                            state = SF; // �����
                            break;
                        } else if (ch == '.') {
                            mystring.append(".");
                            ch = text[++counter];
                            while (is_NUM(ch)) { // ���� �����
                                mystring.append(ch); // �������� � ������ �������
                                ch = text[++counter];
                            }
                            if (is_XDIL(ch)) { // ����� �����
                                mytype = LNUM;
                                state = SF; // �����
                                break;
                            }
                        }
                        throw new ResonException("Lexemator: '" + ch + "'");
                        // ������ ��������
                        // ----------------------------------------------------
                    case SOP:
                        op = OP.getOP(ch, text[counter + 1]);
                        if (op != null) {
                            counter += 2;
                            ch = text[counter];
                        } else if ((op = OP.getOP(ch)) != null) {
                            ch = text[++counter];
                        } else {
                            throw new ResonException("Lexemator: Bad Operation '"
                                    + ch + "'");
                        }
                        mytype = LOP;
                        state = SF;
                        mystring = new StringBuffer(op.toString());
                        break;

                    // ������ ������
                    // ----------------------------------------------------
                    case SSTR: // ������ ������
                        ch = text[++counter];
                        while (ch != '\'') { // ���� �� ����� ������
                            if (ch == '\\') {
                                ch = text[++counter];
                                switch (ch) {
                                    case 't':
                                    case 'T':
                                        ch = '\t';
                                        break;
                                    case 'n':
                                    case 'N':
                                        ch = '\n';
                                        break;
                                    case 'b':
                                    case 'B':
                                        ch = '\b';
                                        break;
                                    case 'e':
                                    case 'E':
                                        ch = '\033';
                                        break;
                                }
                            }
                            mystring.append(ch); // ���������� ������� ������
                            ch = text[++counter];
                        }
                        mytype = LSTR;
                        state = SF; // �����
                        ++counter;
                        break;
                    // stop
                    // -----------------------------------------------------------
                    case SF: // �����
                        if (ZetaProperties.calc_debug > 26) {
                            log.debug("~clac.Lexemator::" + lextypes[mytype] + ":"
                                    + as_string());
                        }
                        return;

                    // error ??
                    // --------------------------------------------------
                    // ---------
                    default:
                        throw new Exception("Lexemator: !!!! UNCKNOWN STATE ????");
                }
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            // log.error("Shit happens", e);
            mytype = LEND;
        }
    }

    /**
     *
     */
    StringBuffer readExpr() throws Exception {
        int xc = 1;
        try {
            char ch = text[counter];
            StringBuffer sb = new StringBuffer();
            while (true) {
                ch = text[counter];
                if (ch == ')') {
                    --xc;
                } else if (ch == '(') {
                    ++xc;
                }
                if (xc == 0) {
                    break;
                }
                sb.append(ch);
                ++counter;
            }
            ++counter;
            return sb;
        } catch (IndexOutOfBoundsException e) {
            log.error("Shit happens", e);
            throw new ResonException("~~clac.Lexemator::redExpr may be '"
                    + ((xc > 0) ? ')' : '(') + "' ?");
        }
    }

    /**
     * ���� ��� �������
     */
    public int type() {
        return mytype;
    }
}
