package core.reflection.objects;

import action.api.HaveMethod;
import action.api.RTException;
import action.calc.objects.class_constructor;
import action.calc.objects.class_type;

import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

public class VALIDATOR implements class_constructor, HaveMethod, class_type {

    public static final int UNKNOWN_TYPE = -1;
    public static final int NUMERIC_TYPE = 0;
    public static final int STRING_TYPE = 1;
    public static final int DATE_TYPE = 2;
    public static final int ARRAY_NUMERIC_TYPE = 3;
    public static final int ARRAY_STRING_TYPE = 4;
    public static final int ARRAY_DATE_TYPE = 5;

    public static final int BOOLEAN_TYPE = 6;

    private static final String TRUE = "YES";
    private static final String FALSE = "NO";

    // сначала вызываем setType, затем setMask
    int type = -1; // 0-Double;1-String;2-java.util.Date
    String mask = null;
    SimpleDateFormat df = null;
    private String sep = " ";
    private String dot = ".";
    private int cnt = 0;

    public VALIDATOR() {
    }

    public Object constructor(Object arg) throws Exception {
        return this;
    }

    String formatNumber(Double obj) {
        Double dd;
        double d = 0;
        String lt, rt;
        String sign = "";
        long lg;
        int i;
        dd = obj;
        d = dd.doubleValue();
        if (d < 0) {
            d = -d;
            sign = "-";
        }
        if (cnt == 0) {
            lg = (long) Math.floor(d + 0.5d);
            rt = "";
            lt = (new Long(lg)).toString();
        } else {
            lg = new Double(Math.floor(d)).longValue();
            // lt = (new Long(lg)).toString();
            d -= lg;
            for (i = 1; i <= cnt; i++) {
                d *= 10;
            }
            if (d == 0) {
                char[] chars = new char[cnt];
                for (int j = 0; j < chars.length; j++) {
                    chars[j] = '0';
                }
                rt = new String(chars);
            } else {
                // rt = new Double(Math.floor(d + 0.5d)).toString();
                rt = new Long(new Double(Math.floor(d + 0.5d)).longValue())
                        .toString();
                String addstr = "";
                if (cnt - rt.length() > 0) {
                    char[] tmp = new char[cnt - rt.length()];
                    for (i = 0; i < tmp.length; i++) {
                        tmp[i] = '0';
                    }
                    addstr = new String(tmp);
                    rt = addstr + rt;
                } else if (cnt - rt.length() < 0) {
                    // значит, после округления дробной части произошел
                    // перенос в старший разряд
                    rt = rt.substring(1, rt.length());
                    lg++;
                }
            }
            lt = (new Long(lg)).toString();
        }
        StringBuilder stb = new StringBuilder(lt);
        int ln = stb.length(), inc = 0;
        for (i = 1; i < ln + inc; i++) {
            if (i % 3 == 0) {
                stb.insert(ln + inc - i, sep);
            }
        }
        if (rt.equals("")) {
            return sign + stb.toString();
        } else {
            return (sign + stb.toString() + dot + rt);
        }
    }

    public Object method(String method, Object arg) throws Exception {
        if (method.equalsIgnoreCase("setType")) {
            if (arg instanceof Vector) {
                throw new RTException("CastException",
                        "VALIDATOR.setType must called with one argument");
            }
            int type = 0;
            if (arg instanceof String) {
                String s = (String) arg;
                if (s.equalsIgnoreCase("NUMBER")) {
                    type = 0;
                } else if (s.equalsIgnoreCase("STRING")) {
                    type = 1;
                } else if (s.equalsIgnoreCase("DATE")) {
                    type = 2;
                }
            } else if (arg instanceof Double) {
                type = ((Double) arg).intValue();
            }
            setType(type);
            return new Double(1);

        } else if (method.equalsIgnoreCase("setMask")) {
            if (arg instanceof String) {
                setMask((String) arg);
            } else {
                throw new RTException("CastException",
                        "VALIDATOR.setMask must called with one String argument");
            }
            return new Double(1);

        } else if (method.equalsIgnoreCase("toString")) {
            return toString(arg);
        } else if (method.equalsIgnoreCase("toObject")) {
            if (arg instanceof String) {
                return toObject((String) arg);
            } else {
                throw new RTException("CastException",
                        "VALIDATOR.toObject must called with one String argument");
            }
        } else {
            throw new RTException("HasMethodException",
                    "object VALIDATOR has not method " + method);
        }
    }

    public void setMask(String mask) {
        this.mask = mask;
        setNumberFormat(mask);
    }

    void setNumberFormat(String s) {
        if (s == null) {
            return;
        }
        s = s.trim();
        int i1 = s.indexOf('#', 0);
        if (i1 >= 0) {
            int i2 = s.indexOf('#', i1 + 1);
            if (i2 >= 0) {
                sep = s.substring(i1 + 1, i2);
                i1 = i2;
            }
        }
        if (s.length() > i1 + 1) {
            cnt = 0;
            String s2 = s.substring(i1 + 1, s.length());
            int pos1 = s2.indexOf('0', 0);
            if (pos1 >= 0) {
                int pos2 = s2.indexOf('0', pos1 + 1);
                if (pos2 >= 0) {
                    dot = s2.substring(pos1 + 1, pos2);
                    for (int i = pos2 + 1; i < s2.length()
                            && s2.charAt(i) == '0'; i++) {
                        cnt++;
                    }
                    cnt++;
                } else {
                    dot = s2.substring(pos1 + 1, s2.length());
                }
            }
        }
    }

    public void setArrayType(int type) {
        switch (type) {
            case NUMERIC_TYPE:
                this.type = ARRAY_NUMERIC_TYPE;
                break;
            case DATE_TYPE:
                this.type = ARRAY_DATE_TYPE;
                df = new SimpleDateFormat("dd.MM.yyyy", Locale.UK);
                break;
            case STRING_TYPE:
                this.type = ARRAY_STRING_TYPE;
                break;
        }
    }

    public void setType(int type) {
        this.type = type;
        if (type == DATE_TYPE) {
            df = new SimpleDateFormat("dd.MM.yyyy");
        }
    }

    public Object toObject(String str) throws Exception {
        if (str == null || str.trim().equals("")) {
            return null;
        }
        switch (type) {
            case NUMERIC_TYPE:
            case ARRAY_NUMERIC_TYPE: {
                return undoFormat(str);
            }
            case STRING_TYPE:
            case ARRAY_STRING_TYPE: {
                return str.trim();
            }
            case DATE_TYPE:
            case ARRAY_DATE_TYPE: {
                Date date = df.parse(str, new ParsePosition(0));
                if (date == null) {
                    throw new Exception("Bad Date format!");
                } else {
                    return date;
                }
            }
            case BOOLEAN_TYPE: {
                return str.equalsIgnoreCase(TRUE);
            }
            default:
                return null;
        }
    }

    public String toString(Object o) throws Exception {
        if (o == null) {
            if (type == BOOLEAN_TYPE)
                return FALSE;
            else
                return "";
        }
        switch (type) {
            case NUMERIC_TYPE:
            case ARRAY_NUMERIC_TYPE: {
                if (o instanceof String) {
                    o = Double.parseDouble(o.toString());
                }
                return formatNumber((Double) o);
            }
            case STRING_TYPE:
            case ARRAY_STRING_TYPE: {
                if (o instanceof Double) {
                    o = o.toString();
                }
                return (String) o;
            }
            case DATE_TYPE:
            case ARRAY_DATE_TYPE: {
                if (o instanceof String) {
                    o = toObject((String) o);
                }
                String str = df.format((java.util.Date) o, new StringBuffer(),
                        new FieldPosition(0)).toString();
                if (str == null || str.equals("")) {
                    throw new Exception("Bad Date !");
                } else {
                    return str;
                }
            }
            case BOOLEAN_TYPE: {
                if (o instanceof Boolean)
                    return ((Boolean) o) ? TRUE : FALSE;
                else if (o instanceof String)
                    return (((String) o).equalsIgnoreCase(TRUE)) ? TRUE : FALSE;
            }
            default: {
                return "";
            }
        }
    }

    public String type() throws Exception {
        return "VALIDATOR";
    }

    Double undoFormat(String src) throws Exception {
        if (src == null) {
            return null;
        }
        src = src.trim();
        if (src == null || src.length() == 0) {
            return null;
        }
        src = src.replace(',', '.');
        int endint = src.indexOf(dot); // конец целой част
        if (endint == -1) {
            endint = src.length();
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < endint; i++) {
            if (sep.indexOf(src.charAt(i)) == -1) {
                sb.append(src.charAt(i));
            }
        }
        boolean dotAppended = false;
        for (int i = endint + dot.length(); i < Math.min(
                cnt + endint + dot.length(), src.length()); i++) {
            if (!dotAppended) {
                dotAppended = true;
                sb.append('.'); // десятичная точка
            }
            if (sep.indexOf(src.charAt(i)) == -1) {
                sb.append(src.charAt(i));
            }
        }
        // Пробуем вернуть Double
        return new Double(Double.parseDouble(sb.toString()));
    }

    public int getType() {
        return type;
    }
}
