// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov Date: 26.05.2008
// 17:08:31
// Home Page : http://members.fortunecity.com/neshkov/dj.html - Check often for
// new version!
// Decompiler options: packimports(3)
// Source File Name: P2S.java

package core.reflection.functions;

import action.api.RTException;
import action.calc.OP;
import action.calc.functions.BaseExternFunction;

// Referenced classes of package calc.functions:
// BaseExternFunction

public class P2S extends BaseExternFunction {

    public P2S() {
    }

    public Object eval() throws Exception {
        Object obj = OP.doHardOP(super.expr);
        if (!(obj instanceof Double)) {
            throw new RTException("CastException", "D2S");
        } else {
            return p2s((Double) obj);
        }
    }

    private int getend(long l) {
        if (l % 100L < 10L) {
            return (int) (l % 100L);
        }
        if (l % 100L >= 10L && l % 100L < 20L) {
            return 0;
        } else {
            return (int) (l % 10L);
        }
    }

    private String p2s(Double double1) {
        if (double1.doubleValue() > 9.2233720368547758E+018D
                || -double1.doubleValue() > 9.2233720368547758E+018D) {
            return null;
        }
        int i = 0;
        String s = "";
        String s1 = "";
        if (double1.doubleValue() < 0.0D) {
            double1 = new Double(-double1.doubleValue());
            s1 = "\u043C\u0438\u043D\u0443\u0441 ";
        }
        double d = double1.doubleValue();
        double1 = new Double((double) Math.round(d * 100D) / 100D);
        long l = double1.longValue();
        long l1 = l;
        do {
            l1 /= 1000L;
            if (l1 <= 0L) {
                break;
            }
            i++;
        } while (true);
        l1 = l;
        for (int k = 0; k <= i; k++) {
            int j = (int) (l1 % 1000L);
            l1 /= 1000L;
            s = pr(j, k, true) + " " + s;
        }

        if (s.trim().length() > 0) {
            s = s + rub + rubs[getend(l)];
        } else {
            s = "\u043D\u043E\u043B\u044C \u043F\u043E\u0437\u0438\u0446\u0438\u0439";
        }
        return s1
                + "\u0412\u0441\u0435\u0433\u043E "
                + s.trim()
                + " \u0432 \u0434\u043E\u043A\u0443\u043C\u0435\u043D\u0442\u0435";
    }

    private String pr(int i, int j, boolean flag) {
        String s = "";
        if (i == 0) {
            return s;
        }
        int k = i / 100;
        if (k > 0) {
            s = sotni[k];
        }
        k = i % 100;
        if (k == 0) {
            if (j == 0) {
                s = s + " " + rs[j];
            } else if (j == 1) {
                s = s + " " + rs[j] + fokon[0];
            } else {
                s = s + " " + rs[j] + mokon[0];
            }
            return s.trim();
        }
        int l = 0;
        if (k < 20) {
            if (k > 9) {
                l = 0;
            } else {
                l = k;
            }
            if (j != 1) {
                if (flag || k > 2) {
                    s = s + " " + mnum[k];
                } else {
                    s = s + " " + fnum[k];
                }
            } else if (k < 3) {
                s = s + " " + fnum[k];
            } else {
                s = s + " " + mnum[k];
            }
        } else {
            s = s + " " + decs[k / 10];
            if (j == 1) {
                if (k % 10 < 3) {
                    s = s + " " + fnum[k % 10];
                } else {
                    s = s + " " + mnum[k % 10];
                }
            } else if (!flag && k % 10 < 3) {
                s = s + " " + fnum[k % 10];
            } else {
                s = s + " " + mnum[k % 10];
            }
            l = k % 10;
        }
        s = s.trim();
        if (j == 0) {
            s = s + " " + rs[j];
        } else if (j == 1) {
            s = s + " " + rs[j] + fokon[l];
        } else {
            s = s + " " + rs[j] + mokon[l];
        }
        return s.trim();
    }

    static String mnum[] = {
            "",
            "\u043E\u0434\u043D\u0430",
            "\u0434\u0432\u0435",
            "\u0442\u0440\u0438",
            "\u0447\u0435\u0442\u044B\u0440\u0435",
            "\u043F\u044F\u0442\u044C",
            "\u0448\u0435\u0441\u0442\u044C",
            "\u0441\u0435\u043C\u044C",
            "\u0432\u043E\u0441\u0435\u043C\u044C",
            "\u0434\u0435\u0432\u044F\u0442\u044C",
            "\u0434\u0435\u0441\u044F\u0442\u044C",
            "\u043E\u0434\u0438\u043D\u043D\u0430\u0434\u0446\u0430\u0442\u044C",
            "\u0434\u0432\u0435\u043D\u0430\u0434\u0446\u0430\u0442\u044C",
            "\u0442\u0440\u0438\u043D\u0430\u0434\u0446\u0430\u0442\u044C",
            "\u0447\u0435\u0442\u044B\u0440\u043D\u0430\u0434\u0446\u0430\u0442\u044C",
            "\u043F\u044F\u0442\u043D\u0430\u0434\u0446\u0430\u0442\u044C",
            "\u0448\u0435\u0441\u0442\u043D\u0430\u0434\u0446\u0430\u0442\u044C",
            "\u0441\u0435\u043C\u043D\u0430\u0434\u0446\u0430\u0442\u044C",
            "\u0432\u043E\u0441\u0435\u043C\u043D\u0430\u0434\u0446\u0430\u0442\u044C",
            "\u0434\u0435\u0432\u044F\u0442\u043D\u0430\u0434\u0446\u0430\u0442\u044C"};

    static String fnum[] = {"", "\u043E\u0434\u043D\u0430",
            "\u0434\u0432\u0435"};

    static String decs[] = {
            "",
            "",
            "\u0434\u0432\u0430\u0434\u0446\u0430\u0442\u044C",
            "\u0442\u0440\u0438\u0434\u0446\u0430\u0442\u044C",
            "\u0441\u043E\u0440\u043E\u043A",
            "\u043F\u044F\u0442\u044C\u0434\u0435\u0441\u044F\u0442",
            "\u0448\u0435\u0441\u0442\u044C\u0434\u0435\u0441\u044F\u0442",
            "\u0441\u0435\u043C\u044C\u0434\u0435\u0441\u044F\u0442",
            "\u0432\u043E\u0441\u0435\u043C\u044C\u0434\u0435\u0441\u044F\u0442",
            "\u0434\u0435\u0432\u044F\u043D\u043E\u0441\u0442\u043E"};

    static String sotni[] = {"", "\u0441\u0442\u043E",
            "\u0434\u0432\u0435\u0441\u0442\u0438",
            "\u0442\u0440\u0438\u0441\u0442\u0430",
            "\u0447\u0435\u0442\u044B\u0440\u0435\u0441\u0442\u0430",
            "\u043F\u044F\u0442\u044C\u0441\u043E\u0442",
            "\u0448\u0435\u0441\u0442\u044C\u0441\u043E\u0442",
            "\u0441\u0435\u043C\u044C\u0441\u043E\u0442",
            "\u0432\u043E\u0441\u0435\u043C\u044C\u0441\u043E\u0442",
            "\u0434\u0435\u0432\u044F\u0442\u044C\u0441\u043E\u0442"};

    static String rs[] = {
            "",
            "\u0442\u044B\u0441\u044F\u0447",
            "\u043C\u0438\u043B\u043B\u0438\u043E\u043D",
            "\u043C\u0438\u043B\u043B\u0438\u0430\u0440\u0434",
            "\u0442\u0440\u0438\u043B\u043B\u0438\u043E\u043D",
            "\u043A\u0432\u0430\u0434\u0440\u0438\u043B\u043B\u0438\u043E\u043D",
            "10^18", "10^21"};

    static String mokon[] = {"\u043E\u0432", "", "\u0430", "\u0430", "\u0430",
            "\u043E\u0432", "\u043E\u0432", "\u043E\u0432", "\u043E\u0432",
            "\u043E\u0432"};

    static String fokon[] = {"", "\u0430", "\u0438", "\u0438", "\u0438", "",
            "", "", "", ""};

    static String rubs[] = {"\u0438\u0439", "\u0438\u044F", "\u0438\u0438",
            "\u0438\u0438", "\u0438\u0438", "\u0438\u0439", "\u0438\u0439",
            "\u0438\u0439", "\u0438\u0439", "\u0438\u0439"};

    static String rub = "\u043F\u043E\u0437\u0438\u0446";

}