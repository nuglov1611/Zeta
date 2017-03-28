package zscript.functions;

import action.api.RTException;

public class D2S {

    static String[] mnum  = { "", "����", "���", "���", "������", "����",
            "�����", "����", "������", "������", "������", "�����������",
            "����������", "����������", "������������", "����������",
            "�����������", "����������", "������������", "������������" };

    static String[] fnum  = { "", "����", "���" };

    static String[] decs  = { "", "", "��������", "��������", "�����",
            "���������", "����������", "���������", "�����������", "���������" };

    static String[] sotni = { "", "���", "������", "������", "���������",
            "�������", "��������", "�������", "���������", "���������" };

    static String[] rs    = { "", "�����", "�������", "��������", "��������",
            "�����������", "10^18", "10^21" };                                   // �������

    // ������� ���������
    static String[] mokon = { "��", "", "�", "�", "�", "��", "��", "��", "��",
            "��"         };

    static String[] fokon = { "", "�", "�", "�", "�", "", "", "", "", "" };

    static String[] rubs  = { "��", "�", "�", "�", "�", "��", "��", "��", "��",
            "��"         };

    static String[] kops  = { "��", "���", "���", "���", "���", "��", "��",
            "��", "��", "��" };

    static String   rub   = "����";

    static String   kop   = "����";

    private static String pr(int three, int r, boolean rubs) { // ������
        // ���������������
        // �������
        // ������������
        // ���� ������
        // (3 �����)
        String str = "";
        if (three == 0) {
            return str;
        }
        int num = three / 100;
        if (num > 0) {
            str = sotni[num];
        }
        num = three % 100;
        if (num == 0) {
            if (r == 0) {
                str += " " + rs[r];
            }
            else if (r == 1) {
                str += " " + rs[r] + fokon[0];
            }
            else {
                str += " " + rs[r] + mokon[0];
            }
            return str.trim();
        }

        int num2 = 0;
        if (num < 20) {
            if (num > 9) {
                num2 = 0;
            }
            else {
                num2 = num;
            }
            if (r != 1) { // ������� - �� �����
                if (rubs || num > 2) {
                    str += " " + mnum[num];
                }
                else {
                    str += " " + fnum[num];
                }
                // if (num<10) str+=
            }
            else {
                if (num < 3) {
                    str += " " + fnum[num];
                }
                else {
                    str += " " + mnum[num];
                }
            }
        }
        else {
            str += " " + decs[num / 10];
            if (r == 1) {
                if ((num % 10) < 3) {
                    str += " " + fnum[num % 10];
                }
                else {
                    str += " " + mnum[num % 10];
                }
            }
            else {
                if (!rubs && (num % 10) < 3) {
                    str += " " + fnum[num % 10];
                }
                else {
                    str += " " + mnum[num % 10];
                }
            }
            num2 = num % 10;
        }
        str = str.trim();
        if (r == 0) {
            str += " " + rs[r];
        }
        else if (r == 1) {
            str += " " + rs[r] + fokon[num2];
        }
        else {
            str += " " + rs[r] + mokon[num2];
        }
        return str.trim();
    }

    private static int getend(long l) {
        if ((l % 100) < 10) {
            return (int) (l % 100);
        }
        if (l % 100 >= 10 && l % 100 < 20) {
            return 0;
        }
        return (int) (l % 10);
    }

    private static String d2s(Double number) {
        if (number.doubleValue() > (double) Long.MAX_VALUE
                || -number.doubleValue() > (double) Long.MAX_VALUE) {
            return null;
        }
        int r = 0;
        String str = "";
        String sign = "";
        if (number.doubleValue() < 0) {
            number = new Double(-number.doubleValue());
            sign = "����� ";
        }
        // ���������� �� ����� ����� �������
        double tmp = number.doubleValue();
        number = new Double(((double) Math.round((tmp * 100))) / 100);
        //
        long l = number.longValue();
        long n = l;
        while (true) {
            n /= 1000;
            if (n > 0) {
                r++;
            }
            else {
                break;
            }
        }
        int curr; // ������� ������ ����, �����. �������
        n = l;
        for (int i = 0; i <= r; i++) { // ���� �� ��������
            // mul*=1000;
            curr = (int) (n % 1000);
            n /= 1000;
            str = pr(curr, i, true) + " " + str;
        }
        if (str.trim().length() > 0) {
            StringBuffer temp = new StringBuffer(str.trim());
            temp.setCharAt(0, Character.toUpperCase(temp.charAt(0)));
            str = temp + " " + rub + rubs[getend(l)];
        }
        else { // ���-�� ������=0
            str = "���� ������";
        }

        int k = (int) Math.round((number.doubleValue() - l) * 100);

        if (k > 0) {
            str += " " + k + " " + kop + kops[getend(k)];
        }
        else {
            str += " 00 ������";
        }

        return sign + str.trim();
    }

    public static Object eval(Object o) throws RTException {
        if (!(o instanceof Double)) {
            throw new RTException("CastException", "D2S");
        }
        return d2s((Double) o);
    }
}
