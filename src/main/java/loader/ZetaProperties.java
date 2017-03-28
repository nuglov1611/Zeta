/*
 * File: ZetaProperties.java
 * 
 * Created: Wed Apr 7 14:01:09 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package loader;

import java.util.Properties;

public class ZetaProperties {
    public final static String CORE_VERSION             = "7.2.0";

    public static String HOME_PATH             = "./";

    public static boolean DEMO             = false;
    
    public static boolean ONLOAD = false;
    
    public static String       ORACLE_VERSION           = "";

    public static String       NAFIGATOR_MAXIMIZE       = "nafigator.maximize";

    public final static String EXCEPTION_LOADER         = "exception.loader";

    public final static String DEBUG_LOADER             = "debug.loader";

//    public final static String DEBUG_COLOR              = "debug.color";

    public final static String DEBUG_PROTOCOL           = "debug.protocol";

    public final static String DEBUG_RML                = "debug.rml";

    public final static String DEBUG_VIEWS              = "debug.views";

    public final static String DEBUG_DSTORE             = "debug.dstore";

    public final static String TREE_DEBUG               = "debug.tree";

    public final static String DEBUG_PARSER             = "debug.parser";

    public final static String DEBUG_CALCULATOR         = "debug.calculator";

//    public final static String CLASS_SERVER             = "class.server";
//
//    public final static String DOC_BTN_CANCEL           = "doc.cancel";
//
//    public final static String DOC_BTN_DOK              = "doc.dok";
//
//    public final static String DOC_BTN_HOK              = "doc.hok";
//
//    public final static String DOC_BTN_SAVE             = "doc.save";
//
//    public final static String DOC_BTN_NEW              = "doc.new";
//
//    public final static String LOGIN_OK                 = "login.ok";
//
//    public static final String LOGIN_SETTINGS           = "login.settings";
//
//    public final static String LOGIN_LUSER              = "login.user.label";
//
//    public final static String LOGIN_LPASSWORD          = "login.password.label";
//
//    public final static String LOGIN_AFTER_EXIT         = "login.afterExit";

    public final static String DEMO_PROPS = "properties/ZetaPropers.xml";

    public final static String IMAGE_LOGO = "images/zetase_cloudlogo.png";

    public final static String IMAGE_ICON = "images/zetase_cloudicon.png";

    public static final String IMAGE_FILTER_SORT_UP = "images/filterSortUp.png";

    public static final String IMAGE_FILTER_SORT_DOWN = "images/filterSortDown.png";

    public static final String IMAGE_FILTER = "images/filter.png";

    public static final String IMAGE_SORT_UP = "images/sortUp.png";

    public static final String IMAGE_SORT_DOWN = "images/sortDown.png";

    public final static String IMAGE_BROWSE = "images/browse.png";

    public final static String PROPNAME                 = "properties/zeta.properties";

//    public final static int    TEXTBG                   = 0;
//
//    public final static int    TEXTFG                   = 1;
//
//    public final static int    BTNBG                    = 2;
//
//    public final static int    BTNFG                    = 3;
//
//    public final static int    FLDBG                    = 4;

//    public final static int    FLDFG                    = 5;

    public final static String MSG_RUSUREEXIT           = "msg.RUSureExit";

    public final static String MSG_YESBUTTON             = "msg.YesButtonLabel";

    public final static String MSG_CLOSEBUTTON             = "msg.CloseButtonLabel";
    
    public final static String MSG_OKBUTTON             = "msg.OkButtonLabel";

    public static final String MSG_NOBUTTON             = "msg.NoButtonLabel";
    
    public final static String MSG_CANCELBUTTON         = "msg.CanscelButtonLabel";

    public static final String MSG_INPUTBUTTON = "msg.InputButtonLabel";

    public final static String MSG_USERALREADYEXIST     = "\u041f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u0435\u043b\u044c \u0441 \u0442\u0430\u043a\u0438\u043c \u0438\u043c\u0435\u043d\u0435\u043c \u0443\u0436\u0435 \u043f\u0440\u0438\u0441\u0443\u0442\u0441\u0442\u0432\u0443\u0435\u0442 \u0432 \u0441\u0438\u0441\u0442\u0435\u043c\u0435!!! \u0412\u043e\u0439\u0442\u0438 \u043f\u043e\u0432\u0442\u043e\u0440\u043d\u043e?";

//    public final static String MSG_BADUSERSCOUNT1       = "msg.BadUsersCount1";

//    public final static String MSG_BADUSERSCOUNT2       = "msg.BadUsersCount2";

//    public final static String MSG_BADLICENSE           = "msg.BadLicense";

    public final static String MSG_BADUSERORPASSWORD    = "msg.BadUserOrPassword";

    public final static String MSG_BLOCKED              = "msg.Blocked";

//    public final static String MSG_UNCLOSEABLE          = "msg.Uncloseable";
//
//    public final static String MSG_RETRIVING            = "msg.Retriving";
//
//    public final static String TITLE_RETRIVING          = "title.Retriving";

//    public final static String MSG_CANTCONNECTTODS      = "msg.CantConnnectToDocumentServer";

    public final static String MSG_CANTCONNECTTODBS     = "msg.CantConnectToDataBaseServer";

//    public final static String MSG_CANTLOADCLASS        = "msg.CantLoadClass";

//    public final static String MSG_PARSERERROR          = "msg.ParsingError";

    public final static String MSG_UNKNOWNERROR         = "msg.UnknownFatalError";

//    public final static String MSG_ERRORREADNODE        = "msg.ErrorReadNode";

//    public final static String MSG_ERRORLOADNODE        = "msg.ErrorLoadNode";

    public final static String MSG_CANTLOADDOCUMENT     = "msg.CantLoadDocument";

//    public final static String MSG_BADDOCUMENT          = "msg.BadDocument";

    public final static String MSG_BADEDITVALUEPREFIX   = "msg.BadEditValuePrefix";

    public final static String MSG_BADEDITVALUEPOSTFIX  = "msg.BadEditValuePostfix";

    public final static String MSG_BADEDITVALUE         = "msg.BadEditValue";

    public final static String MSG_BADEDITBUTTONEDIT    = "msg.BadEditButtonEdit";

    public final static String MSG_BADEDITBUTTONUNDO    = "msg.BadEditButtonUndo";

    public final static String MSG_BADEDITVALUEHEADER   = "msg.BadEditValueHeader";

//    public final static String DSTORE_CHANGE1           = "dstore.change_from";
//
//    public final static String DSTORE_CHANGE2           = "dstore.change_to";
//
//    public final static String CLR_DEFAULT              = "color.msg.default";
//
//    public final static String CLR_DOCUMENT             = "color.document";
//
//    public final static String CLR_MSG_INFO             = "color.msg.info";
//
//    public final static String CLR_MSG_ERROR            = "color.msg.error";
//
//    public final static String CLR_MSG_QUEST            = "color.msg.quest";
//
//    public final static String CLR_LOGIN                = "color.login";
//
//    public final static String CLR_WORKPLACE            = "color.workplace";
//
//    public final static String CLR_TREEVIEW             = "color.treeview";

    public final static String TITLE_MAINWINDOW         = "title.mainwindow";

    public final static String TITLE_MESSAG             = "title.messag";

    public final static String TITLE_SURE               = "title.sure";

//    public final static String TITLE_WORKPLACE          = "title.workplace";

//    public final static String TITLE_HELPER             = "title.helper";
//
//    public final static String TITLE_LOGIN              = "title.login";

    public final static String TITLE_ERROR              = "title.error";

    public static final String TITLE_INPUT = "title.input";

    public static final String TITLE_WARNING = "title.warning";

    public static final String TITLE_IFORMATION = "title.info";

    public final static String TITLE_NAFIGATOR          = "title.nafigator";

//    public final static String FONT_DEFAULT             = "font.default";
//
//    public final static String FONT_LOGIN_BTN           = "font.login.btn";
//
//    public final static String FONT_LOGIN_FIELD         = "font.login.field";
//
//    public final static String FONT_LOGIN_TEXT          = "font.login.text";
//
//    public final static String FONT_MSG_TEXT            = "font.msg.text";

//    public final static String FONT_MSG_BTN             = "font.msg.btn";
//
//    public final static String FONT_MSG_FIELD           = "font.msg.field";
//
//    public final static String FONT_WORKPLACE_TEXT      = "font.workplace.text";
//
//    public final static String FONT_WORKPLACE_BTN       = "font.workplace.btn";
//
//    public final static String FONT_WORKPLACE_FIELD     = "font.workplace.field";
//
//    public final static String FONT_TREEVIEW_POINT      = "font.treeview.point";
//
//    public final static String FONT_TREEVIEW_NODE       = "font.treeview.node";
//
//    public final static String FONT_DOCUMENT            = "font.document";
//
//    public final static String HILITING_TREEVIEW        = "hiliting.treeview";
//
//    public final static String OFFSET_TREEVIEW          = "offset.treeview";

//    public final static String NAFIGATOR_HEADER         = "nafigator.header";

    public final static String NAFIGATOR_WIDTH          = "nafigator.width";

    public final static String NAFIGATOR_HEIGHT         = "nafigator.height";

    public final static String NAFIGATOR_X              = "nafigator.x";

    public final static String NAFIGATOR_Y              = "nafigator.y";

//    public final static String LOADING_STRING           = "loading.label";
//
//    public final static String LOADING_WINDOW           = "loading.window";
//
//    public final static String LOADING_FOREGROUND       = "loading.foreground";
//
//    public final static String LOADING_BACKGROUND       = "loading.background";
//
//    public final static String LOADING_FONT             = "loading.font";

    public static final int MESSAGE_SURE = 0;

    public static final int MESSAGE_ERROR = 1;

    public static final int MESSAGE_WARN = 2;

    public static final int MESSAGE_INFO = 3;
    
    public static final int MESSAGE_CONFIRMATION = 4;

    public final static String NEED_EXTRAH              = "extrah.need";

    public final static String DEFAULT_EXTRAH           = "extrah.default";

    public final static String SYSTEM_OUT               = "system.out";
//
//    public final static String SYSTEM_OUT_SIZE          = "system.out.lines";
//
//    public final static String SYSTEM_OUT_WIDTH         = "system.out.width";
//
//    public final static String SYSTEM_OUT_HEIGHT        = "system.out.height";
//
//    public final static String SYSTEM_OUT_X             = "system.out.x";
//
//    public final static String SYSTEM_OUT_Y             = "system.out.y";
//
//    public final static String SYSTEM_OUT_TITLE         = "system.out.title";
//
//    public final static String SYSTEM_OUT_BACKGROUND    = "system.out.background";
//
//    public final static String SYSTEM_OUT_FOREGROUND    = "system.out.foreground";
//
//    public final static String SYSTEM_OUT_FONT          = "system.out.font";
//
//    public final static String SYSTEM_EDITOR_WIDTH      = "system.editor.width";
//
//    public final static String SYSTEM_EDITOR_HEIGHT     = "system.editor.height";
//
//    public final static String SYSTEM_EDITOR_X          = "system.editor.x";
//
//    public final static String SYSTEM_EDITOR_Y          = "system.editor.y";
//
//    public final static String SYSTEM_EDITOR_TITLE      = "system.editor.title";
//
//    public final static String SYSTEM_EDITOR_BACKGROUND = "system.editor.background";
//
//    public final static String SYSTEM_EDITOR_FOREGROUND = "system.editor.foreground";
//
//    public final static String SYSTEM_EDITOR_FONT       = "system.editor.font";

//    public final static String SYSTEM_EDITOR_BROWSER    = "system.editor.browser";
//
//    public final static String SYSTEM_EDITOR_ALLTABLES  = "system.editor.alltables";
//
//    public final static String FILEDIALOG_BACKGROUND    = "filedialog.background";
//
//    public final static String FILEDIALOG_FOREGROUND    = "filedialog.foreground";

    public final static String PRINTING_REMOTE          = "printing.remote";

    public final static String PRINTING_HOST            = "printing.host";

    public final static String PRINTING_PRINTER_NAME    = "printing.printer_name";

    public final static String PRINTING_BUFFER_SIZE     = "printing.buffer_size";

    public final static String PRINTING_PORT            = "printing.port";

    public final static String HELP_BROWSER             = "help.browser";

    public final static String HELP_START_PAGE          = "help.start_page";

    public final static String _DEFAULTFONT             = "Arial,BOLD,14";

    public final static String _DEFAULTCOLOR            = "#efefef,black,gray,black,gray,black";

    public final static String REP_INIT_MASHTAB         = "rep.InitMashtab";


    public static String       DEFAULTFONT              = _DEFAULTFONT;

    public static String       DEFAULTCOLOR             = _DEFAULTCOLOR;

    public static Properties   prop;

    public static int          loader_debug             = 0;

    public static int          protocol_debug           = 0;

    public static int          rml_debug                = 0;

    public static int          views_debug              = 0;

    public static int          lisp_debug               = 0;

    public static int          dstore_debug             = 0;

    public static int          tree_groups_debug        = 0;

    public static int          parser_debug             = 3;

    public static int          calc_debug               = 0;

    public static boolean      loader_exception         = true;

}