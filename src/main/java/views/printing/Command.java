package views.printing;

import java.util.StringTokenizer;

public class Command {
    public int command;

    public String[] sargs;

    public int[] iargs;

    public String source;

    public String dest;

    public Command(int com, String so, String des, int[] ia, String[] sa) {
        command = com;
        source = so;
        dest = des;
        iargs = ia;
        setSArgs(sa);
    }

    public Command(String command1) throws Exception {
        int c = command1.indexOf("\"");
        if (c == -1) {
            c = command1.length();
        }
        StringTokenizer st = new StringTokenizer(command1.substring(0, c), ",");
        command = Integer.parseInt(st.nextToken());
        source = st.nextToken();
        dest = st.nextToken();
        int count = st.countTokens();
        iargs = new int[count];
        for (int i = 0; i < count; i++) {
            iargs[i] = Integer.parseInt(st.nextToken());
        }
        String tail = command1.substring(c, command1.length());
        StringTokenizer st2 = new StringTokenizer(tail, "\"");
        count = st2.countTokens();
        sargs = new String[count];
        String s;
        boolean wasslash = false;

        for (int i = 0; i < count; i++) {
            StringBuilder sb = new StringBuilder();
            s = st2.nextToken();
            for (int j = 0; j < s.length(); j++) {
                if (s.charAt(j) == '\\' && !wasslash) {
                    wasslash = true;
                    continue;
                }
                if (s.charAt(j) == 'k' && wasslash) {
                    sb.append("\"");
                    wasslash = false;
                    continue;
                }
                if (s.charAt(j) == '\\' && wasslash) {
                    sb.append("\\");
                    wasslash = false;
                    continue;
                }
                sb.append(s.charAt(j));
            }
            sargs[i] = sb.toString();

        }
    }

    public String getString() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(command);

        sb.append(",");
        if (source != null) {
            sb.append(source);
        } else {
            throw new Exception("Bad data into Command");
        }
        sb.append(",");
        if (dest != null) {
            sb.append(dest);
        } else {
            throw new Exception("Bad data into Command");
        }
        sb.append(",");
        if (iargs == null) {
            throw new Exception("Bad data into Command");
        }
        System.out.println(iargs.length + "=length");
        for (int element : iargs) {
            sb.append(String.valueOf(element));
            sb.append(",");
        }
        if (sargs == null) {
            throw new Exception("Bad data into Command");
        }
        for (String element : sargs) {
            if (element == null) {
                sb.append("");
            } else {
                StringBuilder sbinner = new StringBuilder();
                for (int j = 0; j < element.length(); j++) {
                    if (element.charAt(j) == '"') {
                        sbinner.append("\\k");
                        continue;
                    }
                    if (element.charAt(j) == '\\') {
                        sbinner.append("\\\\");
                        continue;
                    }
                    sbinner.append(element.charAt(j));
                }
                sb.append("\"");
                sb.append(sbinner.toString());
            }
        }
        return sb.toString();
    }

    public void setCommand(int com) {
        command = com;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public void setIArgs(int[] ia) {
        iargs = ia;
    }

    public void setSArgs(String[] sa) {
        String s;
        boolean wasslash = false;
        if (sa == null) {
            return;
        }
        sargs = new String[sa.length];
        for (int i = 0; i < sa.length; i++) {
            StringBuilder sb = new StringBuilder();
            s = sa[i];
            for (int j = 0; j < s.length(); j++) {
                if (s.charAt(j) == '\\' && !wasslash) {
                    wasslash = true;
                    continue;
                }
                if (s.charAt(j) == 'k' && wasslash) {
                    sb.append("\"");
                    wasslash = false;
                    continue;
                }
                if (s.charAt(j) == '\\' && wasslash) {
                    sb.append("\\");
                    wasslash = false;
                    continue;
                }
                sb.append(s.charAt(j));
            }
            sargs[i] = sb.toString();
        }
    }

    public void setSource(String sou) {
        source = sou;
    }

}