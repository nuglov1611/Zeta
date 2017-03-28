package core.parser;

import core.document.Document;
import core.rml.RmlObject;
import loader.ZetaProperties;
import org.apache.log4j.Logger;

/*
 * ����� Lexemator ��������� ����������� ���������� ������������ �
 * �������������� ����������� ������� RML'�.
 */
class Lexemator {

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
     * ����� �����
     */
    public static final int LINT = 2;

    /**
     * ������
     */
    public static final int LSTR = 3;

    /**
     * �������������� �����
     */
    public static final int LFLT = 4;

    /**
     * }
     */
    public static final int LCBK = 5;

    /**
     * {
     */
    public static final int LOBK = 6;

    /**
     * ����� ������ ������
     */
    public static final int LEND = 7;

    static final String lextypes[] = {"identificator", "EQU", "Integer",
            "String", "float", "'}'", "'{'", "LEND"};

    static final int SS = 0;

    static final int SAZ = 1;

    static final int SF = 2;

    static final int SSTR1 = 3;

    static final int SSTR2 = 4;

    static final int SINT = 5;

    static final int SFLT = 6;

    static final int SCOMENT = 7;

    char[] text;

    int counter;

    int mytype;

    StringBuffer mystring;

    int line = 0;

    /**
     * ����������� ���������������� � ���������� - ����������� �������
     */
    public Lexemator(char[] text) {
        this.text = text;
        counter = 0;
        line = 1;
    }

    /**
     * ���� ������� ��� �������������� �����
     */
    public double as_double() throws Exception {
        String s = mystring.toString();
        if (ZetaProperties.parser_debug > 2)
            System.out.println("~rml.Lexemator::as_double str is " + s + "!");
        try {
            Double d = Double.valueOf(s);
            return d.doubleValue();
        } catch (NumberFormatException e) {
            if (ZetaProperties.parser_debug > 2) {
                log.error("~rml.Lexemator::as_double ", e);
            }
            throw new Exception();
        }
    }

    /**
     * ���� ������� ��� ����� �����
     */
    public int as_int() throws Exception {
        String s = mystring.toString();
        if (ZetaProperties.parser_debug > 2)
            System.out.println("~rml.Lexemator::as_int str is " + s + "!");
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            if (ZetaProperties.parser_debug > 2) {
                log.error("~rml.Lexemator::as_int ", e);
            }
            throw new Exception();
        }
    }

    /**
     * ���� ������� ��� ������
     */
    public String as_string() {
        String s;
        if (mytype == LDEF)
            s = mystring.toString().toUpperCase();
        else
            s = mystring.toString();
        if (ZetaProperties.parser_debug > 2)
            System.out.println("~rml.Lexemator::as_string ? " + s);
        return s;
    }

    boolean is_AZ(char x) {
        return (Character.isLetter(x) || (x == '_') || (x == '.') || (x == ',')
                || (x == '(') || (x == ')') || (x == '|') || (x == '$'));
    }

    boolean is_BK(char x) {
        return (is_OBK(x) || is_CBK(x));
    }

    boolean is_CBK(char x) {
        return (x == '}');
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
        return (x == '{');
    }

    boolean is_S(char x) {
        return ( /* is_S1(x)|| */is_S2(x));
    }

    // boolean is_S1(char x){return /*(x == '\'')*/;}
    boolean is_S2(char x) {
        return (x == '\"');
    }

    boolean is_XDIL(char x) {
        return (is_DIL(x) || is_EQU(x) || is_BK(x) || is_S(x) || (x == '/'));
    }

    /**
     * ����� ���������� ��������� �������
     */
    public void next() throws Exception {
        int state = SS; // �������� �� ���������� ��������� :)
        if (counter >= text.length) {
            mytype = LEND;
            return;
        }
        char ch = text[counter];
        mystring = new StringBuffer();
        if (ZetaProperties.parser_debug > 2)
            System.out.println("~rml.Lexemator::next : begin parsing");
        try { // ����������� ����� ������ ��������
            while (true) { // ���� �� ��������
                if (ch == '\n')
                    ++line;
                switch (state) { // ��������� �� ��������� ��������
                    case SS: // ��������� ���������
                        if (is_DIL(ch)) { // ���� �����������
                            state = SS; // ����� �� �����
                            ch = text[++counter];
                        } else if (is_AZ(ch))
                            state = SAZ; // �������������
                        else if (is_S2(ch))
                            state = SSTR2; // ������� ������� ����
                        else if (is_EQU(ch)) { // =
                            mytype = LEQU;
                            state = SF; // ����� ������� �������
                            ++counter;
                        } else if (is_OBK(ch)) { // {
                            mytype = LOBK;
                            state = SF; // �����
                            ++counter;
                        } else if (is_CBK(ch)) { // }
                            mytype = LCBK;
                            state = SF; // �����
                            ++counter;
                        } else if (is_NUM(ch))
                            state = SINT; // �����
                        else if (ch == '/') {
                            if (text[counter + 1] == '/') {
                                ch = text[counter += 2];
                                try {
                                    while (ch != '\n')
                                        ch = text[++counter];
                                    ++line;
                                } catch (Exception e) {
                                    log.error("~rml.Lexemator::next coments \n\t", e);
                                }
                            }
                        } else
                            ch = text[++counter];

                    /* throw new Exception("Lexemator: '"+ch+"' in SS"); */
                        break;
                    case SAZ: // �������������
                        while (is_AZ(ch) || is_NUM(ch)) {
                            // ���� ������� ��������
                            mystring.append(ch); // �������� � ������ �������
                            ch = text[++counter];
                        }
                        if (is_XDIL(ch)) { // ����� ��������������
                            mytype = LDEF;
                            state = SF; // �����
                        } else
                            throw new Exception("Lexemator: '" + ch
                                    + "' in SAZ line " + line);
                        break;
                    case SINT: // �����
                        while (is_NUM(ch)) {
                            // ���� �����
                            mystring.append(ch); // �������� � ������ �������
                            ch = text[++counter];
                        }
                        if (ch == '.')
                            state = SFLT;
                        else if (is_XDIL(ch)) { // ����� �����
                            mytype = LINT;
                            state = SF; // �����
                        } else
                            throw new Exception("Lexemator: '" + ch
                                    + "' in SINT line " + line);
                        break;
                    case SFLT: // �������������� �����
                        mystring.append(".");
                        ch = text[++counter];
                        while (is_NUM(ch)) {
                            // ���� �����
                            mystring.append(ch); // �������� � ������ �������
                            ch = text[++counter];
                        }
                        if (is_XDIL(ch)) { // ����� �����
                            mytype = LFLT;
                            state = SF; // �����
                        } else
                            throw new Exception("Lexemator: '" + ch
                                    + "' in SFLT line " + line);
                        break;
                    case SSTR2: // ������ ������
                        ch = text[++counter];
                        while (!is_S2(ch)) { // ���� �� ����� ������
                            if (ch == '\'') {
                                mystring.append(ch);
                                ch = text[++counter];
                                while (ch != '\'') {
                                    mystring.append(ch);
                                    if (ch == '\\') {
                                        ch = text[++counter];
                                        mystring.append(ch);
                                    }
                                    ch = text[++counter];
                                }
                            } else if (ch == '\\')
                                ch = text[++counter];
                            mystring.append(ch); // ���������� ������� ������
                            ch = text[++counter];
                        }
                        mytype = LSTR;
                        state = SF; // �����
                        ++counter;
                        break;
                    case SF: // �����
                        if (ZetaProperties.parser_debug > 2)
                            System.out.println("~rml.Lexemator::"
                                    + lextypes[mytype]);
                        return;

                    default:
                        throw new Exception("Lexemator: !!!! R U normal !!!!");
                }
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            mytype = LEND;
        }
    }

    /**
     * ���� ��� �������
     */
    public int type() {
        if (ZetaProperties.parser_debug > 2)
            System.out.println("~rml.Lexemator::next lextype " + mytype);
        return mytype;
    }
}

/**
 * ������
 */
public class Parser {
    private static final Logger log = Logger
            .getLogger(Parser.class);


    static final int SS = 0;

    static final int STAG = 1;

    static final int SP = 2;

    static final int SPROP = 3;

    static final int SVSD = 4;

    static final int SINT = 5;

    static final int SFLT = 6;

    static final int SSTR = 7;

    static final int SDEF = 8;

    static final int SBK = 9;

    static final int SOBK = 10;

    static final int SF = 11;

    static final String sts[] = {"START", "TAG", "SP", "PROP", "SVSD",
            "INT", "FLOAT", "STRING", "IDENT", "SBK", "SOBK", "FINISH"};

    /**
     * ��������� �� ������ ������ ������ �������
     */

    static final String rp = "~rml.Parser::paser ";

    static float position = 0;
    static float increment = 70;

    private Document document = null;

    public Parser(Document doc) {
        document = doc;
    }

    /**
     * ��������� ������ ������� �� ������ �������� ���������� �
     * ����������������� �������� �� args
     */
    public Proper createProper(char[] text, Object[] args) throws Exception {
        Lexemator lex = new Lexemator(text);
        return parser(lex);
    }

    /**
     * ����������� ���������� ��������� �� ������ �������
     */
    public RmlObject[] getContent(Proper prop) {
        RmlObject[] res;
        if (prop.content == null)
            return new RmlObject[0];
        int i = 0;
        prop = prop.content;
        for (Proper foo = prop; foo != null; foo = foo.next) {
            ++i;
        }
        float old_seg = increment;
        float start = position;
        increment /= i;
        res = new RmlObject[i];
        Class cl;
        RmlObject pobj;
        for (int j = 0; j < i && prop != null; prop = prop.next, ++j) {
            try {
                cl = Class.forName("core.reflection.rml." + prop.tag);
                pobj = (RmlObject) cl.newInstance();
                pobj.init(prop, document);
                res[j] = pobj;
            } catch (Exception e) {
                log.error("!", e);
                if (ZetaProperties.parser_debug > 2) {
                    log.error("~rml.Parser::getContent ", e);
                }
                res[j] = null;
            }
            position = start + (j + 1) * increment;
            document.setProgress((byte) position);
        }
        increment = old_seg;
        position = start;
        return res;
    }

    Proper parser(Lexemator lex) throws Exception {
        int state = SS;
        Proper prop = new Proper();
        String propName = "none";
        if (ZetaProperties.parser_debug > 1)
            System.out.println(rp + "start parsing");
        while (true) { // ���� �� ��������
            if (ZetaProperties.parser_debug > 1)
                System.out.println(rp + sts[state]);
            switch (state) {
                case SS:
                    lex.next();
                    if (lex.type() == Lexemator.LDEF)
                        state = STAG;
                    else
                        state = SBK;
                    break;
                case STAG: // ��������� ����
                    prop.tag = lex.as_string();
                    if (ZetaProperties.parser_debug > 1)
                        System.out.println(rp + sts[SP]);
                    // none BREAK; !!!!
                case SP:
                    lex.next();
                    if (lex.type() == Lexemator.LDEF)
                        state = SPROP;
                    else
                        state = SBK;
                    break;
                case SPROP: // ��������
                    propName = lex.as_string();
                    lex.next();
                    if (lex.type() != Lexemator.LEQU) {
                        prop.put(propName, "");
                        if (lex.type() == Lexemator.LDEF)
                            state = SPROP;
                        else
                            state = SBK;
                        break;
                    }
                    if (ZetaProperties.parser_debug > 1)
                        System.out.println(rp + sts[SVSD]);
                    // none BREAK; !!!!
                case SVSD: // ��������� �������� ��������
                    lex.next();
                    state = SP;
                    if (ZetaProperties.parser_debug > 1)
                        System.out.println(rp + " lexema " + lex.type());
                    switch (lex.type()) {
                        case Lexemator.LINT:
                            prop.put(propName, new Integer(lex.as_int()));
                            break;
                        case Lexemator.LFLT:
                            prop.put(propName, new Double(lex.as_double()));
                            break;
                        case Lexemator.LSTR:
                        case Lexemator.LDEF:
                            if (ZetaProperties.parser_debug > 1)
                                System.out.println(rp + ": = str");
                            prop.put(propName, lex.as_string());
                            break;
                        default:
                            throw new Exception(rp
                                    + "No reaction,(proper = ?????) line " + lex.line
                                    + "\n\tstring value = " + lex.as_string());
                    }
                    break;
                case SBK: // ��������� ��������
                    if (lex.type() == Lexemator.LOBK)
                        state = SOBK;
                    else if (lex.type() == Lexemator.LCBK) {
                        state = SF;
                        if (ZetaProperties.parser_debug > 1)
                            System.out.println(rp + "------- } ");
                        lex.next();
                    } else
                        state = SF;

                /*
                 * throw new Exception(rp+"\n\t"+ ZetaUtility.PRINT_LIGHT+
                 * "RML error in line "+lex.line+ ZetaUtility.PRINT_NORMAL);
                 */
                    break;
                case SOBK: // ����� ��������
                    if (ZetaProperties.parser_debug > 1)
                        System.out.println(rp + "------- { ");
                    Proper p = parser(lex);
                    Proper.add(prop, p);
                    state = SBK;
                    if (lex.type() == Lexemator.LEND)
                        state = SF;
                    break;
                case SF: // �������� ���������
                    return prop;

                default:
                    throw new Exception(rp + " Uncnown Error!!!!!!!");
            }
        }
    }
}
