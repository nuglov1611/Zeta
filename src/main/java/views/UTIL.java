package views;

import java.awt.Color;
import java.awt.FontMetrics;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

public class UTIL {

    public static Vector<String> createSequence(Vector<String> names,
            Vector<Vector<String>> Bn) throws Exception {
        if (names == null || Bn == null) {
            throw new Exception("input Vector's may not be null!");
        }
        int a = names.size();
        int b = Bn.size();
        if (a != b || a == 0) {
            throw new Exception(
                    "sizes of names and Bn must be equals and != 0 ");
        }
        Vector<String> ret = new Vector<String>(); // здесь будет хранится
        // возвращаемая
        // последовательность

        for (int j = 0; j < Bn.size(); j++) {
            Vector<String> bj = Bn.elementAt(j);
            if (bj == null) {
                throw new Exception("vector Bi may not be null!");
            }
            int cur = -1;
            int bjsize = bj.size();
            for (int k = 0; k < bjsize; k++) {
                cur++;
                String bjk = (String) bj.elementAt(cur);
                if (!names.contains(bjk)) {
                    bj.removeElement(bjk);
                    cur--;
                }
            }
        }

        while (names.size() > 0) {
            int currenta = -1;
            int oldsize = names.size();
            for (int i = 0; i < oldsize; i++) {
                currenta++;
                String ai = names.elementAt(currenta);
                Vector<String> bi = Bn.elementAt(currenta);
                if (bi == null) {
                    throw new Exception("vector Bi may not be null!");
                }
                if (bi.size() == 0) { // значит, филд с именем ai не зависит от
                    // других филдов в names
                    ret.addElement(ai);
                    names.removeElement(ai);
                    currenta--;
                    Bn.removeElement(bi);
                }
            }
            if (oldsize == names.size()) {
                throw new Exception("circular references may not be in names!");
            }

            for (int j = 0; j < Bn.size(); j++) {
                Vector<String> bj = Bn.elementAt(j);
                if (bj == null) {
                    throw new Exception("vector Bi may not be null!");
                }
                int cur = -1;
                int bjsize = bj.size();
                for (int k = 0; k < bjsize; k++) {
                    cur++;
                    String bjk = (String) bj.elementAt(cur);
                    if (!names.contains(bjk)) {
                        bj.removeElement(bjk);
                        cur--;
                    }
                }
            }

        } // end of while

        return ret;
    }

    public static Color getColor(String color) {
        try {
            int red = Integer.parseInt(color.substring(1, 3), 16);
            int green = Integer.parseInt(color.substring(3, 5), 16);
            int blue = Integer.parseInt(color.substring(5, 7), 16);
            return new Color(((red << 16) + (green << 8) + blue));
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception inside Grid.getColor: "
                    + e.getMessage());
            return Color.black;
        }
    }

    public static int[] getOutPoint(int width, int height, FontMetrics fm,
            String halignment, String valignment, int dw, int dh, int x, int y,
            String str) {
        int xp, yp;
        // int width = col.size;
        // int height = sizeRow;
        // if (col==null) return null;
        // FontMetrics fm = new FontMetrics(col.font);
        int sw = fm.stringWidth(str);
        int sh = fm.getHeight() - fm.getDescent();
        int desc = 0; // col.fm.getDescent();
        int wwidth = width - 2 * dw;
        int wheight = height - 2 * dh;
        if (halignment.equals("LEFT")) {
            xp = x + dw;
        }
        else if (halignment.equals("RIGHT")) {
            xp = x + dw + wwidth - sw;
        }
        else if (halignment.equals("CENTER")) {
            xp = x + dw + (wwidth - sw) / 2 + 1;
        }
        else {
            xp = x + dw;
        }

        if (valignment.equals("BOTTOM")) {
            yp = y + dh + wheight - desc;
        }
        else if (valignment.equals("TOP")) {
            yp = y + dh + sh - desc;
        }
        else if (valignment.equals("CENTER")) {
            yp = y + dh + sh + (wheight - sh) / 2 - desc - 1;
        }
        else {
            yp = y + dh + wheight - desc;
        }

        int[] ret = new int[2];
        ret[0] = xp;
        ret[1] = yp;
        return ret;
    }

    public static String makeWrap(String str, String delim, int width,
            FontMetrics fm) {
        if (delim == null) {
            return str;
        }
        StringBuffer retbuffer = new StringBuffer();
        StringTokenizer tl1 = new StringTokenizer(str, "\n", true);
        int countl1 = tl1.countTokens();
        for (int i = 0; i < countl1; i++) {
            String tok = tl1.nextToken();
            if (tok.equals("\n")) {
                retbuffer.append(tok);
                continue;
            }
            StringTokenizer tl2 = new StringTokenizer(tok, delim, true);
            StringTokenizer help_t = new StringTokenizer(tok, delim, false);
            // int countl2 = tl2.countTokens();
            int countl2 = help_t.countTokens();
            int sw = 0; // здесь будет инкрементироватся в соответвии с
            // токенами длина текущей строки
            for (int j = 0; j < countl2; j++) {
                String tok2;
                int tw = 0;
                String sdel = "";
                try {
                    while ((tok2 = tl2.nextToken()) != null
                            && tok2.length() == 1
                            && delim.indexOf(tok2.charAt(0)) >= 0) { // значит,
                        // встретился
                        // символ
                        // -
                        // разделитель
                        tw += fm.stringWidth(tok2);
                        sdel += tok2;
                    }
                }
                catch (NoSuchElementException e) { // строка заканчивается на
                    // раздлитель, выходим
                    // из цикла по j
                    e.printStackTrace();
                    retbuffer.append(sdel);
                    break;
                }
                tw += fm.stringWidth(tok2);
                if (tw + sw > width) {
                    if (j == 0) {
                        retbuffer.append(tok2);
                        retbuffer.append("\n");
                        sw = 0;
                    }
                    else {
                        retbuffer.append("\n");
                        retbuffer.append(tok2);
                        sw = fm.stringWidth(tok2);
                    }
                }
                else {
                    retbuffer.append(sdel);
                    retbuffer.append(tok2);
                    sw += tw;
                }
            }
        }
        return retbuffer.toString();
    }

    public static String[] parseDep(String dep) throws Exception {
        if (dep == null) {
            throw new Exception("depends is null!");
        }
        StringTokenizer st = new StringTokenizer(dep, ";");
        int ct = st.countTokens();
        if (ct == 0) {
            throw new Exception("bad depends!");
        }
        String[] work = new String[ct];
        int counter = 0;
        while (st.hasMoreTokens()) {
            String str = st.nextToken();
            work[counter] = str;
            counter++;
        }
        return work;
    }

    public static String toCommandString(String str) {
        // преобразует строку str в строку, допустимую для
        // передачи на сервер печати( " -> \k ; \ -> \\)
        if (str == null) {
            return "";
        }
        StringBuffer ret = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '"') {
                ret.append("\\k");
                continue;
            }
            if (str.charAt(i) == '\\') {
                ret.append("\\\\");
                continue;
            }
            ret.append(str.charAt(i));
        }
        return ret.toString();
    }

}
