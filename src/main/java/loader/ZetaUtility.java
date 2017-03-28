/*
 * File: ZetaUtility.java
 * 
 * Created: Mon Mar 22 17:44:49 1999
 * 
 * Copyright(c) by Alexey Chen
 */

package loader;

import static loader.ZetaProperties.DEBUG_CALCULATOR;
import static loader.ZetaProperties.DEBUG_DSTORE;
import static loader.ZetaProperties.DEBUG_LOADER;
import static loader.ZetaProperties.DEBUG_PARSER;
import static loader.ZetaProperties.DEBUG_PROTOCOL;
import static loader.ZetaProperties.DEBUG_RML;
import static loader.ZetaProperties.DEBUG_VIEWS;
import static loader.ZetaProperties.DEFAULTCOLOR;
import static loader.ZetaProperties.DEFAULTFONT;
import static loader.ZetaProperties.EXCEPTION_LOADER;
import static loader.ZetaProperties.TREE_DEBUG;
import static loader.ZetaProperties.calc_debug;
import static loader.ZetaProperties.dstore_debug;
import static loader.ZetaProperties.loader_debug;
import static loader.ZetaProperties.loader_exception;
import static loader.ZetaProperties.parser_debug;
import static loader.ZetaProperties.prop;
import static loader.ZetaProperties.protocol_debug;
import static loader.ZetaProperties.rml_debug;
import static loader.ZetaProperties.tree_groups_debug;
import static loader.ZetaProperties.views_debug;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import views.MessageFactory;
import boot.Boot;
import core.browser.WorkspaceManager;

public class ZetaUtility {
    private static final Logger log    = Logger.getLogger(ZetaUtility.class);

    static Object               pr_tmp = new Object();

    //##########################################################################
    // ######
    /*
     * color manipulation
     */

    static {
        try {

            Properties pr = new Properties();
            
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            pr.load(cl.getResourceAsStream(ZetaProperties.PROPNAME));
            ZetaProperties.prop = pr;
            ZetaUtility.setdebuging();

        }
        catch (Exception e) {
            log.fatal("\tBroken with init properties of curent system", e);
            System.exit(0);
        }
    }

    public static Color color(String s) {
        s = s.toLowerCase().trim();
        if (s.compareTo("gray") == 0)
            return Color.gray;
        else if (s.compareTo("lightgray") == 0)
            return Color.lightGray;
        else if (s.compareTo("darkgray") == 0)
            return Color.darkGray;
        else if (s.compareTo("white") == 0)
            return Color.white;
        else if (s.compareTo("black") == 0)
            return Color.black;
        else if (s.compareTo("red") == 0)
            return Color.red;
        else if (s.compareTo("green") == 0)
            return Color.green;
        else if (s.compareTo("blue") == 0)
            return Color.blue;
        else if (s.compareTo("yellow") == 0)
            return Color.yellow;
        else if (s.compareTo("cyan") == 0)
            return Color.cyan;
        else if (s.compareTo("magenta") == 0)
            return Color.magenta;
        else if (s.compareTo("pink") == 0)
            return Color.pink;
        else if ((s.charAt(0) == '#') || (s.charAt(0) == '$'))
            try {
                if (s.length() == 4)
                    return new Color(fromhex(s.substring(1, 2)), fromhex(s
                            .substring(2, 3)), fromhex(s.substring(3, 4)));
                else if (s.length() == 7)
                    return new Color(fromhex(s.substring(1, 3)), fromhex(s
                            .substring(3, 5)), fromhex(s.substring(5, 7)));
                else
                    return Color.gray;
            }
            catch (Exception e) {
                log.warn("Bad color", e);
                return Color.gray;
            }
        else
            return Color.gray;
    }

    public static Color color(String s, int type) {
        String[] p_color = parse(6, prop.getProperty(s), DEFAULTCOLOR);
        return color(p_color[type]);
    }

    public static Color[] colors(String s) {
        String[] p_color = parse(6, s, DEFAULTCOLOR);
        Color[] cl = new Color[6];
        for (int i = 0; i < 6; ++i)
            cl[i] = color(p_color[i]);
        return cl;
    }

    public static String face(String s) {
        String[] fn = parse(3, prop.getProperty(s), DEFAULTFONT);
        return fn[0];
    }

    public static int fam(String s) {
        String[] fn = parse(3, prop.getProperty(s), DEFAULTFONT);
        return fontFam(fn[1]);
    }

    //##########################################################################
    // ######
    /*
     * properties manipulation
     */

    public static Font font(String s) {
        String[] p_font = parse(3, prop.getProperty(s), DEFAULTFONT);
        return new Font(p_font[0], fontFam(p_font[1]), Integer.valueOf(
                p_font[2]).intValue());
    }

    public static Font font1(String s) {
        String[] p_font = parse(3, s, DEFAULTFONT);
        return new Font(p_font[0], fontFam(p_font[1]), Integer.valueOf(
                p_font[2]).intValue());
    }

    //##########################################################################
    // ######
    /*
     * dialog manipulation
     */

    public static int fontFam(String s) {
        s = s.toUpperCase();
        if (s.compareTo("BOLD") == 0)
            return Font.BOLD;
        else if (s.compareTo("ITALIC") == 0)
            return Font.ITALIC;
        else if (s.compareTo("BOLD|ITALIC") == 0)
            return Font.BOLD | Font.ITALIC;
        else if (s.compareTo("ITALIC|BOLD") == 0)
            return Font.BOLD | Font.ITALIC;
        else if (s.compareTo("PLAIN") == 0)
            return Font.PLAIN;
        else
            return Font.BOLD;
    }

    static int fromhex(String s) {
        return Integer.parseInt(s, 16);
    }

    //##########################################################################
    // ######
    /*
     * encoding tools
     */

    public static String[] parse(int num, String s, String d) {
        log.debug("~loader.GLOBAL::parse args " + num + ",{" + s + "},{" + d
                + "}");
        if (s == null)
            return parse(num, d, null);
        StringTokenizer st = new StringTokenizer(s, ",");
        if (st.countTokens() < num)
            return parse(num, d, null);
        String[] r = new String[num];
        for (int i = 0; i < num; ++i)
            try {
                r[i] = st.nextToken().trim();
            }
            catch (Exception e) {
                log.error("Parsing trouble", e);
                return parse(num, d, null);
            }
        return r;
    }

    public static String[] parseColor(String s) {
        return parse(6, s, DEFAULTCOLOR);
    }

    public static String pr(String s) {
        if (log.isDebugEnabled())
            log.debug("read property: " + s + "=" + prop.getProperty(s));
        return prop.getProperty(s);
    }

    public static String pr(String s, String def) {
        String foo = "";
        if (log.isInfoEnabled())
            log.debug("read property: " + s + "=" + prop.getProperty(s));
        foo = prop.getProperty(s);
        if ((foo == null) || (foo.compareTo("") == 0))
            return def;
        else
            return foo;
    }

    public static void setdebuging() {

        try {
            loader_debug = Integer.valueOf(pr(DEBUG_LOADER, "0")).intValue();
        }
        catch (Exception e) {
            log.error("Shit happens", e);
        }
        try {
            protocol_debug = Integer.valueOf(pr(DEBUG_PROTOCOL, "0"))
                    .intValue();
        }
        catch (Exception e) {
            log.error("Shit happens", e);
        }
        try {
            rml_debug = Integer.valueOf(pr(DEBUG_RML, "0")).intValue();
        }
        catch (Exception e) {
            log.error("Shit happens", e);
        }
        try {
            views_debug = Integer.valueOf(pr(DEBUG_VIEWS, "0")).intValue();
        }
        catch (Exception e) {
            log.error("Shit happens", e);
        }
        try {
            dstore_debug = Integer.valueOf(pr(DEBUG_DSTORE, "0")).intValue();
        }
        catch (Exception e) {
            log.error("Shit happens", e);
        }
        try {
            tree_groups_debug = Integer.valueOf(pr(TREE_DEBUG, "0")).intValue();
        }
        catch (Exception e) {
            log.error("Shit happens", e);
        }
        try {
            parser_debug = Integer.valueOf(pr(DEBUG_PARSER, "0")).intValue();
        }
        catch (Exception e) {
            log.error("Shit happens", e);
        }
        try {
            calc_debug = Integer.valueOf(pr(DEBUG_CALCULATOR, "0")).intValue();
        }
        catch (Exception e) {
            log.error("Shit happens", e);
        }

        try {
            loader_exception = (pr(EXCEPTION_LOADER, "OFF").toUpperCase()
                    .compareTo("ON") == 0);
        }
        catch (Exception e) {
            log.error("Shit happens", e);
        }
    }

    //##########################################################################
    // ######
    /*
     * loading information
     */

    public static int size(String s) {
        String[] fn = parse(3, prop.getProperty(s), DEFAULTFONT);
        int r = Integer.valueOf(fn[1]).intValue();
        if (r < 1)
            return 14;
        return r;
    }

    public static Double extSure(String message){
        return new Double(MessageFactory.getInstance().showYesNoCancelMessage(WorkspaceManager.getCurWorkspace(), message));
    }
    
    public static Object input(String message) {
        return input(message, "", WorkspaceManager.getCurWorkspace());
    }

    public static Object input(String message, String initValue) {
        return input(message, initValue, WorkspaceManager.getCurWorkspace());
    }

    public static Object input(String message, String initValue, Component fr) {
        return MessageFactory.getInstance().showInputMessage(fr, message, initValue);
    }

    public static boolean sure(String message, int messageType) {
        return sure(message, messageType, null);
    }
    public static boolean sure(String message, int messageType, String font) {
        return sure(message, messageType, font, WorkspaceManager.getCurWorkspace());
    }

    public static boolean sure(String message, int messageType, String font, Component fr) {
        boolean answer;
        if (messageType == ZetaProperties.MESSAGE_ERROR) {
            answer = MessageFactory.getInstance().showMessage(fr, message, MessageFactory.Type.ERROR);
        } else if (messageType == ZetaProperties.MESSAGE_WARN) {
            answer = MessageFactory.getInstance().showMessage(fr, message, MessageFactory.Type.WARNING);
        } else if (messageType == ZetaProperties.MESSAGE_INFO) {
            answer = MessageFactory.getInstance().showMessage(fr, message, MessageFactory.Type.INFO);
        } else if(messageType == ZetaProperties.MESSAGE_CONFIRMATION){
            answer = MessageFactory.getInstance().showMessage(fr, message, MessageFactory.Type.EXTENDED_CONFIRMATION);
        } else {
            answer = MessageFactory.getInstance().showMessage(fr, message, MessageFactory.Type.CONFIRMATION);
        }
        return answer;
    }
    
    public static void oracleMessage(Throwable error, Component owner){
        String err_msg = "Неизвестная ошибка БД";
        String err_msg2 = null;
        if (error.getMessage() != null) {
            Pattern pattern = Pattern.compile(".*ORA-([\\d]{5}).*", Pattern.DOTALL);
            Matcher m;
            do {
                err_msg2 = error.getMessage();
                m = pattern.matcher(err_msg2);
                boolean m1 = m.find();
                boolean m2 = m.matches();
                error = error.getCause();
            }while (!m.matches() && error != null);
            if (m.matches()) {
                err_msg = err_msg2;
                String ora = m.group(1);
                ora = "msg." + ora;
                err_msg2 = ZetaUtility.pr(ora, null);
            }
        }
        message(err_msg2 == null ? err_msg : err_msg2, ZetaProperties.MESSAGE_ERROR, owner);
    }

    public static void message(String message) {
        message(message, ZetaProperties.MESSAGE_ERROR);
    }
    
    public static void message(String message, int messageType) {
        message(null, message, messageType);
    }

    public static void message(String message, int messageType, String font) {
        message(null, message, messageType, WorkspaceManager.getCurWorkspace(), font);
    }

    public static void message(String header, String message, int messageType) {
        message(header, message, messageType, WorkspaceManager.getCurWorkspace(), null);
    }

    public static void message(String message, int messageType, Component owner) {
        message(null, message, messageType, owner, null);
    }

    public static void message(String header, String message, int messageType, Component owner, String font) {
        try {
            if (owner == null) {
                owner = Boot.getInstance();
            }
            if (messageType == ZetaProperties.MESSAGE_ERROR) {
                MessageFactory.getInstance().showMessage(owner, header, message,
                        MessageFactory.Type.ERROR, font);
            } else if (messageType == ZetaProperties.MESSAGE_WARN) {
                MessageFactory.getInstance().showMessage(owner, header, message,
                        MessageFactory.Type.WARNING, font);
            } else {
                MessageFactory.getInstance().showMessage(owner, header, message,
                        MessageFactory.Type.SIMPLE, font);
        }
        }
        catch (Exception e) {
            log.error("Shit happens", e);
        }
    }
}
