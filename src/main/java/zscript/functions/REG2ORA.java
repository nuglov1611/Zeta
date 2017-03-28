package zscript.functions;

import action.api.RTException;

public class REG2ORA {

    public static Object eval(Object o) throws RTException {
        if (!(o instanceof String)) {
            throw new RTException("CastException", "REG2ORA");
        }
        return reg2ora((String) o);
    }

    static String reg2ora(String str) {
        if (str == null) {
            return null;
        }
        if (str.equals("")) {
            return "";
        }
        StringBuilder ret = new StringBuilder();
        boolean was_slash = false;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            switch (ch) {
                case '\\':
                    if (was_slash) {
                        ret.append("\\");
                        break;
                    } else {
                        was_slash = true;
                        continue;
                    }
                case '*':
                    if (was_slash) {
                        ret.append("*");
                    } else {
                        ret.append("%");
                    }
                    break;
                case '?':
                    if (was_slash) {
                        ret.append("?");
                    } else {
                        ret.append("_");
                    }
                    break;
                case '%':
                    ret.append("^%");
                    break;
                case '_':
                    ret.append("^_");
                    break;
                case '^':
                    ret.append("^^");
                    break;
                default:
                    ret.append(ch);
            }
            was_slash = false;
        }
        return ret.toString();
    }
}
