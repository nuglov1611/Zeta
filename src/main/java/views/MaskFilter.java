package views;

import java.util.Vector;

public class MaskFilter {
    static char mask_one = '\u0001'; // ����� '?'

    static char mask_any = '\u0000'; // ����� '*'

    // Vector template = new Vector();
    char[] template = null;

    public MaskFilter(String mask) {
        if (mask == null || mask.equals("")) {
            return;
        }
        Vector<Character> templ = new Vector<Character>();
        // if (mask==null) throw new
        // IllegalArgumentException("Null argument in MaskFilter(String)");
        char[] chars = mask.toCharArray();
        if (chars.length == 0)
        // templ.addElement("");
        {
            return;
        }
        boolean sop = false; // ������ ������ � ���� \+���-��
        for (char element : chars) {
            if (element == '*' && !sop) {
                int s = templ.size();
                if (s == 0 || (templ.elementAt(s - 1).charValue() != mask_any)) {
                    templ.addElement(new Character(mask_any));
                    continue;
                } else if (s != 0 && (templ.elementAt(s - 1).charValue() == 0)) {
                    continue;
                }
            }
            if (element == '?' && !sop) {
                templ.addElement(new Character(mask_one));
                continue;
            }
            if (element == '\\' && !sop) {
                sop = true;
                continue;
            }
            if (element == '*' && sop) {
                templ.addElement(new Character('*'));
                sop = false;
                continue;
            }
            if (element == '?' && sop) {
                templ.addElement(new Character('?'));
                sop = false;
                continue;
            }
            if (element == '\\' && sop) {
                templ.addElement(new Character('\\'));
                sop = false;
                continue;
            }
            sop = false;
            templ.addElement(new Character(element));
        }
        if (templ.size() > 0) {
            template = new char[templ.size()];
        }
        for (int i = 0; i < templ.size(); i++) {
            template[i] = templ.elementAt(i).charValue();
        }
    }

    public boolean accept(String str) {
        if (template == null) {
            return str == null || str.equals("");
        }

        if (template[0] == mask_any
                && template[template.length - 1] == mask_any) {
            return accept2(str, 0, str.length() - 1, template, 0,
                    template.length - 1);
        } else if (template[0] == mask_any
                && template[template.length - 1] != mask_any) {
            int[] be = getNextTemp2(template.length - 1, 0, template);
            if (be == null) {
                return false;
            }
            if (endsWith(str, 0, str.length() - 1, template, be[1], be[0])) {
                return accept2(str, 0, str.length() - 1 - (be[0] - be[1] + 1),
                        template, 0, template.length - 1 - (be[0] - be[1] + 1));
            } else {
                return false;
            }
        } else if (template[0] != mask_any
                && template[template.length - 1] == mask_any) {
            int[] be = getNextTemp1(0, template.length - 1, template);
            if (be == null) {
                return false;
            }
            if (startsWith(str, 0, str.length() - 1, template, be[0], be[1])) {
                return accept2(str, (be[1] - be[0] + 1), str.length() - 1,
                        template, (be[1] - be[0] + 1), template.length - 1);
            } else {
                return false;
            }
        } else if (template[0] != mask_any
                && template[template.length - 1] != mask_any) {
            int[] be1 = getNextTemp1(0, template.length - 1, template);
            int[] be2 = getNextTemp2(template.length - 1, 0, template);
            if (be1 == null || be2 == null) {
                return false;
            }
            if (be1[0] == be2[1] && be1[1] == be2[0]) {
                return template.length == str.length()
                        && startsWith(str, 0, str.length() - 1, template,
                        be1[0], be1[1]);
            } else if (startsWith(str, 0, str.length() - 1, template, be1[0],
                    be1[1])
                    && endsWith(str, 0, str.length() - 1, template, be2[1],
                    be2[0])) {
                return accept2(str, (be1[1] - be1[0] + 1), str.length() - 1
                                - (be2[0] - be2[1] + 1), template,
                        (be1[1] - be1[0] + 1), template.length - 1
                                - (be2[0] - be2[1] + 1));
            } else {
                return false;
            }
        }
        return false;
    }

    private boolean accept2(String str, int sb, int se, char[] temp, int tb,
                            int te) {
        int current = sb;
        int[] be = new int[]{-1, tb - 1};
        while ((be = getNextTemp1(be[1] + 1, te, temp)) != null) {
            int index;
            if ((index = indexOf(str, current, se, temp, be[0], be[1])) < 0) {
                return false;
            }
            current = index + be[1] - be[0] + 1;
        }
        return true;
    }

    private boolean endsWith(String str, int sb, int se, char[] template,
                             int tb, int te) {
        return startsWith(str, se - (te - tb), se, template, tb, te);
    }

    private int[] getNextTemp1(int start, int end, char[] template) {
        if (start < 0 || start > template.length - 1 || end - start < 0
                || end > template.length - 1) {
            return null;
        }
        int[] ret = new int[]{-1, -1};
        for (int i = start; i <= end; i++) {
            if (template[i] != mask_any && ret[0] == -1) {
                ret[0] = i;
                if (i == end) {
                    ret[1] = i;
                    return ret;
                } else {
                    continue;
                }
            }
            if (ret[0] != -1 && (template[i] == mask_any || (i == end))) {
                if (template[i] == mask_any) {
                    ret[1] = i - 1;
                } else {
                    ret[1] = i;
                }
                return ret;
            }
        }
        return null;
    }

    private int[] getNextTemp2(int start, int end, char[] template) {
        if (start < 0 || start > template.length - 1 || start - end < 0
                || end < 0) {
            return null;
        }
        int[] ret = new int[]{-1, -1};
        for (int i = start; i >= end; i--) {
            if (template[i] != mask_any && ret[0] == -1) {
                ret[0] = i;
                if (i == end) {
                    ret[1] = i;
                    return ret;
                } else {
                    continue;
                }
            }
            if (ret[0] != -1 && (template[i] == mask_any || (i == end))) {
                if (template[i] == mask_any) {
                    ret[1] = i + 1;
                } else {
                    ret[1] = i;
                }
                return ret;
            }
        }
        return null;
    }

    private int indexOf(String str, int sb, int se, char[] template, int tb,
                        int te) {
        int tlen = te - tb + 1;
        int slen = se - sb + 1;
        if (slen == 0 && tlen == 0) {
            return sb < 0 ? -1 : sb;
        }
        if (tb < 0 || te > template.length - 1) {
            return -1;
        }
        if (tlen < 0 || slen < 0 || sb < 0 || se > str.length() - 1
                || tlen > slen) {
            return -1;
        }
        for (int i = 0; i <= slen - tlen; i++) {
            if (startsWith(str, sb + i, se, template, tb, te)) {
                return sb + i;
            }
        }
        return -1;
    }

    private boolean startsWith(String str, int sb, int se, char[] template,
                               int tb, int te) {
        // str - ������, � ������� ������������ �����
        // template - ������(����������� ������ ����� '?')
        // sb - ��������� ������� ��� ������ � ������
        // se - �������� ������� ��� ������ � ������
        // tb - ��������� ������� �������
        // te - �������� ������� �������
        int tlen = te - tb + 1;
        int slen = se - sb + 1;
        if (slen == 0 && tlen == 0) {
            return true;
        }
        if (tb < 0 || te > template.length - 1) {
            return false;
        }

        if (tlen <= 0 || slen <= 0 || sb < 0 || se > str.length() - 1
                || tlen > slen) {
            return false;
        }
        for (int i = tb; i <= te; i++) {
            if (template[i] == mask_one) {
                continue;
            }
            if (str.charAt(sb + i - tb) != template[i]) {
                return false;
            }
        }
        return true;
    }

    // for testing
    /*
     * public static void main(String[] args) throws Exception{ //MaskFilter mf
     * = new MaskFilter("?DD*fs???****?\\?\\\\"); MaskFilter mf = new
     * MaskFilter("?*���*��������??????*��\\?"); //MaskFilter mf = new
     * MaskFilter("");//(
     * "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh������������������������������������������������?"
     * );
     * 
     * 
     * long t1 = System.currentTimeMillis(); boolean res = false; for (int
     * i=0;i<50000;i++) { res =
     * mf.accept("hhhhhhhhhhh������������������������������������������������?"
     * ); } long t2 = System.currentTimeMillis();
     * System.out.println("result="+res);
     * System.out.println("Execution time = "+(t2-t1)); try {
     * Thread.currentThread().sleep(2200); }catch(Exception e) {} }
     */
}
