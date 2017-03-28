package views;

import action.api.RTException;
import core.document.Document;
import core.parser.Proper;
import core.rml.Container;
import core.rml.RmlObject;
import core.rml.VisualRmlObject;
import core.rml.ui.impl.ZPanelImpl;
import core.rml.ui.impl.ZTabbedPaneImpl;
import core.rml.ui.interfaces.ZComponent;
import core.rml.ui.interfaces.ZPanel;
import core.rml.ui.interfaces.ZTabbedPane;
import org.apache.log4j.Logger;
import publicapi.EditorAPI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicBorders;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Графический компонент "Редактор"
 */
public class Editor extends VisualRmlObject implements EditorAPI {

    private static final Logger LOG = Logger.getLogger(Editor.class);

    class DefaultAL implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("###add_tab###")) {
                BaseEditor be = new BaseEditor();
                be.init(baseProper, document);
                be.parent = Editor.this;
                if (tabPanel != null) {
                    tabPanel.add("" + (tabPanel.getTabCount() + 1), be.getVisualComponent());
                }
            }
            if (e.getActionCommand().equals("###remove_tab###")) {
                if (tabPanel != null) {
                    if (tabPanel.getTabCount() > 1) {
                        int current = tabPanel.getSelectedIndex();
                        int old = current;
                        if (current > 0) {
                            current--;
                        } else {
                            current++;
                        }
                        try {
                            tabPanel.setSelectedIndex(current);
                            tabPanel.remove(old);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
            if (e.getActionCommand().equals("###run_exp###")) {
                if (exp != null) {
                    try {
                        document.executeScript(exp, false);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    class MenuAL implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command != null && !command.equals("")) {
                try {
                    document.doAction(command, null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out
                            .println("exception inside core.document.ACTION:doAction : "
                                    + e);
                }
            }
            if (e.getSource() instanceof views.Item) {
                String script = ((views.Item) e.getSource()).getExp();
                try {
                    document.executeScript(script, false);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out
                            .println("views.Editor$MenuAL::actionPerformed() : "
                                    + ex);
                }
            }
        }
    }

    class PCL implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals("CurrentTab")) {
                if (((Integer) e.getOldValue()).intValue() != -1
                        && ((Integer) e.getOldValue()).intValue() != ((Integer) e
                        .getNewValue()).intValue()) {
                    try {
                        document.executeScript(tabExp, false);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        System.out
                                .println("views.Editor$PCL::propertyChange() : "
                                        + ex);
                    }
                }
            }
        }
    }

    class ShortcutStruct {
        int key;

        int modifiers;

        String expr;

        // String action;
        public ShortcutStruct(int key, int modifiers, String calc1) {
            this.key = key;
            this.modifiers = modifiers;
            expr = calc1;
        }

        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }

            if (!(o instanceof ShortcutStruct)) {
                return false;
            }
            ShortcutStruct s = (ShortcutStruct) o;
            return key == s.key && modifiers == s.modifiers;
        }
    }

    /**
     *
     */

    private Container container = new Container(this);

    String exp;

    String tabExp;

    String multiWindow = "NO";

    views.Menu menu = null;

    Proper baseProper = null;

    Vector<Object> baseEditors = new Vector<Object>();

    ZTabbedPane tabPanel;

    private HashMap<Integer, BaseEditor> editors = new HashMap<Integer, BaseEditor>();

    ZPanel panel = ZPanelImpl.create();

    // Методы интерфейса GlobalValuesObject

    private Vector<ShortcutStruct> shortcuts = new Vector<ShortcutStruct>();

    public Editor() {
        super();
//        panel.enableEvents(AWTEvent.MOUSE_EVENT_MASK);
//        panel.enableEvents(AWTEvent.KEY_EVENT_MASK);
        panel.setLayout(new GridLayout(1, 1));
    }

    BaseEditor getBaseEditor() {
        if (multiWindow.equals("YES")) {
            if (tabPanel != null) {
                return editors.get(tabPanel.getSelectedIndex());
            } else {
                return null;
            }
        } else {
            return editors.get(0);
        }
    }

    JPopupMenu getPopupMenu() {
        return menu.getMenu().getPopupMenu();
    }

    public Object getValue() {
        return this;
    }

    private void addEditor(Proper prop, int tab) {
        BaseEditor be = new BaseEditor();
        be.init(prop, document);
        be.parent = this;
        if (tab > -1) {
            tabPanel.add("" + tab, be.getVisualComponent());
            editors.put(tab, be);
        } else {
            panel.add(be.getVisualComponent());
            editors.put(tab, be);
        }


    }

    public Object getValueByName(String name) {
        return getValue();
    }

    public void init(Proper prop, Document doc) {
        super.init(prop, doc);
        String sp;

        if (prop == null) {
            return;
        }
        baseProper = prop;

        sp = (String) prop.get("EXP");
        if (sp != null) {
            exp = sp;
        }

        tabExp = (String) prop.get("TABSELEXP");

        sp = (String) prop.get("MULTIWINDOW");
        if (sp != null) {
            multiWindow = sp;
        }
        if (multiWindow.equals("YES")) {
            tabPanel = ZTabbedPaneImpl.create(JTabbedPane.BOTTOM,
                    JTabbedPane.SCROLL_TAB_LAYOUT /* JTabbedPane.ROUNDED */);
            tabPanel.addPropertyChangeListener(new PCL());

            addEditor(prop, 1);

            panel.add(tabPanel);
            menu = new views.Menu();
            JMenuItem mi = new JMenuItem("Добавить закладку");
            mi.setActionCommand("###add_tab###");
            mi.addActionListener(new DefaultAL());
            menu.add(mi);
            mi = new JMenuItem("Удалить закладку");
            mi.setActionCommand("###remove_tab###");
            mi.addActionListener(new DefaultAL());
            menu.add(mi);
            mi = new JMenuItem("Выполнить");
            mi.setActionCommand("###run_exp###");
            mi.addActionListener(new DefaultAL());
            menu.add(mi);
            mi = new JMenuItem("-");
            menu.add(mi);

        } else {
            addEditor(prop, -1);
        }
    }

    public Object method(String method, Object arg) throws Exception {
        if (method.equals("SETTEXT") && (arg instanceof String)) {
            BaseEditor be = getBaseEditor();
            if (be != null) {
                be.setText((String) arg);
            }
            return null;
        } else if (method.equals("GETTEXT")) {
            BaseEditor be = getBaseEditor();
            if (be != null) {
                return be.getText();
            }
        } else if (method.equals("SETACTION")) {
            if (arg instanceof Vector) {
                Vector v = (Vector) arg;
                String hotkey = null;
                String action = null;
                if (v.size() != 2) {
                    throw new RTException("Syntax", "bad number of arguments");
                }
                for (int i = 0; i < 2; i++) {
                    if (!(v.elementAt(i) instanceof String)) {
                        throw new RTException("", "bad type of argument");
                    }
                    if (i == 0) {
                        hotkey = (String) v.elementAt(0);
                    }
                    if (i == 1) {
                        action = (String) v.elementAt(1);
                    }
                }
                setShortcut(hotkey, action);
            }
        } else if (method.equals("ADDTAB")) {
            if (!multiWindow.equals("YES")) {
                return null;
            }
            if (tabPanel != null) {
                addEditor(baseProper, (tabPanel.getTabCount() + 1));
            }
        } else if (method.equals("DELTAB")) {
            if (!multiWindow.equals("YES")) {
                return null;
            }
            if (tabPanel != null) {
                if (tabPanel.getTabCount() > 1) {
                    int current = tabPanel.getSelectedIndex();
                    int old = current;
                    if (current > 0) {
                        current--;
                    } else {
                        current++;
                    }
                    try {
                        tabPanel.setSelectedIndex(current);
                        tabPanel.remove(old);
                        editors.remove(old);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } else if (method.equals("SETCURRENTTAB")) {
            if (!multiWindow.equals("YES")) {
                return null;
            }
            if (!(arg instanceof Double)) {
                return null;
            }
            int cur = ((Double) arg).intValue();
            if (tabPanel != null) {
                tabPanel.setSelectedIndex(cur);
            }
        } else if (method.equals("GETCURRENTTAB")) {
            if (!multiWindow.equals("YES")) {
                return new Double(0);
            }
            if (tabPanel != null) {
                return new Double(tabPanel.getSelectedIndex());
            }
            return new Double(0);
        } else if (method.equals("SETTABLABEL")) {
            if (!multiWindow.equals("YES")) {
                return null;
            }
            if (!(arg instanceof Vector)) {
                return null;
            }
            Vector v = (Vector) arg;
            if (v.size() != 2) {
                return null;
            }
            int tab = 0;
            String label = null;
            try {
                label = (String) v.elementAt(0);
                tab = ((Double) v.elementAt(1)).intValue();
                if (tabPanel != null) {
                    tabPanel.setTitleAt(tab, label);
                }
            } catch (Exception ex) {
                LOG.error("", ex);
                return null;
            }
        } else
            return super.method(method, arg);

        return null;
    }

    public void processShortcut(int key, int modifiers) {
        if (shortcuts == null) {
            return;
        }
        for (int i = 0; i < shortcuts.size(); i++) {
            ShortcutStruct s = shortcuts.elementAt(i);
            // System.out.println("inside processShortcut");
            if (s != null && s.key == key && s.modifiers == modifiers
                    && s.expr != null) {
                try {
                    document.executeScript(s.expr, false);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out
                            .println("views.Editor::processShortcut() : " + e);
                }
            }
        }
    }

    void setShortcut(String shortcut, String action) {
        if (shortcut == null || action == null) {
            return;
        }
        StringTokenizer st = new StringTokenizer(shortcut, "+");
        int cnt = st.countTokens();
        if (cnt == 0) {
            return;
        }
        int key = 0;
        int modifiers = 0;
        for (int i = 0; i < Math.min(cnt, 2); i++) {
            String sh = st.nextToken().toUpperCase();
            if (sh.equals("VK_0")) {
                key = KeyEvent.VK_0;
            }
            if (sh.equals("VK_1")) {
                key = KeyEvent.VK_1;
            }
            if (sh.equals("VK_2")) {
                key = KeyEvent.VK_2;
            }
            if (sh.equals("VK_3")) {
                key = KeyEvent.VK_3;
            }
            if (sh.equals("VK_4")) {
                key = KeyEvent.VK_4;
            }
            if (sh.equals("VK_5")) {
                key = KeyEvent.VK_5;
            }
            if (sh.equals("VK_6")) {
                key = KeyEvent.VK_6;
            }
            if (sh.equals("VK_7")) {
                key = KeyEvent.VK_7;
            }
            if (sh.equals("VK_8")) {
                key = KeyEvent.VK_8;
            }
            if (sh.equals("VK_9")) {
                key = KeyEvent.VK_9;
            }
            if (sh.equals("VK_A")) {
                key = KeyEvent.VK_A;
            }
            if (sh.equals("VK_ACCEPT")) {
                key = KeyEvent.VK_ACCEPT;
            }
            if (sh.equals("VK_ADD")) {
                key = KeyEvent.VK_ADD;
            }
            if (sh.equals("VK_ALT")) {
                modifiers = KeyEvent.ALT_MASK;
            }
            if (sh.equals("VK_B")) {
                key = KeyEvent.VK_B;
            }
            if (sh.equals("VK_BACK_QUOTE")) {
                key = KeyEvent.VK_BACK_QUOTE;
            }
            if (sh.equals("VK_BACK_SLASH")) {
                key = KeyEvent.VK_BACK_SLASH;
            }
            if (sh.equals("VK_BACK_SPACE")) {
                key = KeyEvent.VK_BACK_SPACE;
            }
            if (sh.equals("VK_C")) {
                key = KeyEvent.VK_C;
            }
            if (sh.equals("VK_CANCEL")) {
                key = KeyEvent.VK_CANCEL;
            }
            if (sh.equals("VK_CAPS_LOCK")) {
                key = KeyEvent.VK_CAPS_LOCK;
            }
            if (sh.equals("VK_CLEAR")) {
                key = KeyEvent.VK_CLEAR;
            }
            if (sh.equals("VK_CLOSE_BRACKET")) {
                key = KeyEvent.VK_CLOSE_BRACKET;
            }
            if (sh.equals("VK_COMMA")) {
                key = KeyEvent.VK_COMMA;
            }
            if (sh.equals("VK_CONTROL")) {
                modifiers = KeyEvent.CTRL_MASK;
            }
            if (sh.equals("VK_CONVERT")) {
                key = KeyEvent.VK_CONVERT;
            }
            if (sh.equals("VK_D")) {
                key = KeyEvent.VK_D;
            }
            if (sh.equals("VK_DECIMAL")) {
                key = KeyEvent.VK_DECIMAL;
            }
            if (sh.equals("VK_DELETE")) {
                key = KeyEvent.VK_DELETE;
            }
            if (sh.equals("VK_DIVIDE")) {
                key = KeyEvent.VK_DIVIDE;
            }
            if (sh.equals("VK_DOWN")) {
                key = KeyEvent.VK_DOWN;
            }
            if (sh.equals("VK_E")) {
                key = KeyEvent.VK_E;
            }
            if (sh.equals("VK_END")) {
                key = KeyEvent.VK_END;
            }
            if (sh.equals("VK_ENTER")) {
                key = KeyEvent.VK_ENTER;
            }
            if (sh.equals("VK_EQUALS")) {
                key = KeyEvent.VK_EQUALS;
            }
            if (sh.equals("VK_ESCAPE")) {
                key = KeyEvent.VK_ESCAPE;
            }
            if (sh.equals("VK_F")) {
                key = KeyEvent.VK_F;
            }
            if (sh.equals("VK_F1")) {
                key = KeyEvent.VK_F1;
            }
            if (sh.equals("VK_F10")) {
                key = KeyEvent.VK_F10;
            }
            if (sh.equals("VK_F11")) {
                key = KeyEvent.VK_F11;
            }
            if (sh.equals("VK_F12")) {
                key = KeyEvent.VK_F12;
            }
            if (sh.equals("VK_F2")) {
                key = KeyEvent.VK_F2;
            }
            if (sh.equals("VK_F3")) {
                key = KeyEvent.VK_F3;
            }
            if (sh.equals("VK_F4")) {
                key = KeyEvent.VK_F4;
            }
            if (sh.equals("VK_F5")) {
                key = KeyEvent.VK_F5;
            }
            if (sh.equals("VK_F6")) {
                key = KeyEvent.VK_F6;
            }
            if (sh.equals("VK_F7")) {
                key = KeyEvent.VK_F7;
            }
            if (sh.equals("VK_F8")) {
                key = KeyEvent.VK_F8;
            }
            if (sh.equals("VK_F9")) {
                key = KeyEvent.VK_F9;
            }
            if (sh.equals("VK_FINAL")) {
                key = KeyEvent.VK_FINAL;
            }
            if (sh.equals("VK_G")) {
                key = KeyEvent.VK_G;
            }
            if (sh.equals("VK_H")) {
                key = KeyEvent.VK_H;
            }
            if (sh.equals("VK_HELP")) {
                key = KeyEvent.VK_HELP;
            }
            if (sh.equals("VK_HOME")) {
                key = KeyEvent.VK_HOME;
            }
            if (sh.equals("VK_I")) {
                key = KeyEvent.VK_I;
            }
            if (sh.equals("VK_INSERT")) {
                key = KeyEvent.VK_INSERT;
            }
            if (sh.equals("VK_J")) {
                key = KeyEvent.VK_J;
            }
            if (sh.equals("VK_K")) {
                key = KeyEvent.VK_K;
            }
            if (sh.equals("VK_KANA")) {
                key = KeyEvent.VK_KANA;
            }
            if (sh.equals("VK_KANJI")) {
                key = KeyEvent.VK_KANJI;
            }
            if (sh.equals("VK_L")) {
                key = KeyEvent.VK_L;
            }
            if (sh.equals("VK_LEFT")) {
                key = KeyEvent.VK_LEFT;
            }
            if (sh.equals("VK_M")) {
                key = KeyEvent.VK_M;
            }
            if (sh.equals("VK_META")) {
                key = KeyEvent.VK_META;
            }
            if (sh.equals("VK_MODECHANGE")) {
                key = KeyEvent.VK_MODECHANGE;
            }
            if (sh.equals("VK_MULTIPLY")) {
                key = KeyEvent.VK_MULTIPLY;
            }
            if (sh.equals("VK_N")) {
                key = KeyEvent.VK_N;
            }
            if (sh.equals("VK_NONCONVERT")) {
                key = KeyEvent.VK_NONCONVERT;
            }
            if (sh.equals("VK_NUM_LOCK")) {
                key = KeyEvent.VK_NUM_LOCK;
            }
            if (sh.equals("VK_NUMPAD0")) {
                key = KeyEvent.VK_NUMPAD0;
            }
            if (sh.equals("VK_NUMPAD1")) {
                key = KeyEvent.VK_NUMPAD1;
            }
            if (sh.equals("VK_NUMPAD2")) {
                key = KeyEvent.VK_NUMPAD2;
            }
            if (sh.equals("VK_NUMPAD3")) {
                key = KeyEvent.VK_NUMPAD3;
            }
            if (sh.equals("VK_NUMPAD4")) {
                key = KeyEvent.VK_NUMPAD4;
            }
            if (sh.equals("VK_NUMPAD5")) {
                key = KeyEvent.VK_NUMPAD5;
            }
            if (sh.equals("VK_NUMPAD6")) {
                key = KeyEvent.VK_NUMPAD6;
            }
            if (sh.equals("VK_NUMPAD7")) {
                key = KeyEvent.VK_NUMPAD7;
            }
            if (sh.equals("VK_NUMPAD8")) {
                key = KeyEvent.VK_NUMPAD8;
            }
            if (sh.equals("VK_NUMPAD9")) {
                key = KeyEvent.VK_NUMPAD9;
            }
            if (sh.equals("VK_O")) {
                key = KeyEvent.VK_O;
            }
            if (sh.equals("VK_OPEN_BRACKET")) {
                key = KeyEvent.VK_OPEN_BRACKET;
            }
            if (sh.equals("VK_P")) {
                key = KeyEvent.VK_P;
            }
            if (sh.equals("VK_PAGE_DOWN")) {
                key = KeyEvent.VK_PAGE_DOWN;
            }
            if (sh.equals("VK_PAGE_UP")) {
                key = KeyEvent.VK_PAGE_UP;
            }
            if (sh.equals("VK_PAUSE")) {
                key = KeyEvent.VK_PAUSE;
            }
            if (sh.equals("VK_PERIOD")) {
                key = KeyEvent.VK_PERIOD;
            }
            if (sh.equals("VK_PRINTSCREEN")) {
                key = KeyEvent.VK_PRINTSCREEN;
            }
            if (sh.equals("VK_Q")) {
                key = KeyEvent.VK_Q;
            }
            if (sh.equals("VK_QUOTE")) {
                key = KeyEvent.VK_QUOTE;
            }
            if (sh.equals("VK_R")) {
                key = KeyEvent.VK_R;
            }
            if (sh.equals("VK_RIGHT")) {
                key = KeyEvent.VK_RIGHT;
            }
            if (sh.equals("VK_S")) {
                key = KeyEvent.VK_S;
            }
            if (sh.equals("VK_SCROLL_LOCK")) {
                key = KeyEvent.VK_SCROLL_LOCK;
            }
            if (sh.equals("VK_SEMICOLON")) {
                key = KeyEvent.VK_SEMICOLON;
            }
            if (sh.equals("VK_SEPARATER")) {
                key = KeyEvent.VK_SEPARATER;
            }
            if (sh.equals("VK_SHIFT")) {
                modifiers = KeyEvent.SHIFT_MASK;
            }
            if (sh.equals("VK_SLASH")) {
                key = KeyEvent.VK_SLASH;
            }
            if (sh.equals("VK_SPACE")) {
                key = KeyEvent.VK_SPACE;
            }
            if (sh.equals("VK_SUBTRACT")) {
                key = KeyEvent.VK_SUBTRACT;
            }
            if (sh.equals("VK_T")) {
                key = KeyEvent.VK_T;
            }
            if (sh.equals("VK_TAB")) {
                key = KeyEvent.VK_TAB;
            }
            if (sh.equals("VK_U")) {
                key = KeyEvent.VK_U;
            }
            if (sh.equals("VK_UNDEFINED")) {
                key = KeyEvent.VK_UNDEFINED;
            }
            if (sh.equals("VK_UP")) {
                key = KeyEvent.VK_UP;
            }
            if (sh.equals("VK_V")) {
                key = KeyEvent.VK_V;
            }
            if (sh.equals("VK_W")) {
                key = KeyEvent.VK_W;
            }
            if (sh.equals("VK_X")) {
                key = KeyEvent.VK_X;
            }
            if (sh.equals("VK_Y")) {
                key = KeyEvent.VK_Y;
            }
            if (sh.equals("VK_Z")) {
                key = KeyEvent.VK_Z;
            }
        }
        try {
            ShortcutStruct sh = new ShortcutStruct(key, modifiers, action);
            if (shortcuts != null) {
                for (int i = 0; i < shortcuts.size(); i++) {
                    if (sh.equals(shortcuts.elementAt(i))) {
                        shortcuts.setElementAt(sh, i);
                        return;
                    }
                }
                shortcuts.addElement(sh);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setValue(Object value) {
        if (!(value instanceof String)) {
            return;
        }
        BaseEditor be = getBaseEditor();
        if (be != null) {
            ((JTextArea) be.getVisualComponent()).setText((String) value);
        }
    }

    public void setValueByName(String name, Object value) {
        setValue(value);
    }

    public String type() {
        return "VIEWS_GROUP";
    }

    @Override
    public void focusThis() {
        getBaseEditor().getVisualComponent().requestFocus();
    }

    @Override
    public ZComponent getVisualComponent() {
        return panel;
    }

    @Override
    public void addChild(RmlObject child) {
        container.addChildToCollection(child);

        if (child instanceof views.Menu) {
            menu = (views.Menu) child;
            menu.addActionListenerRecursiv(new MenuAL());
        }
    }

    @Override
    public RmlObject[] getChildren() {
        return container.getChildren();
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public void initChildren() {
    }

    @Override
    public boolean addChildrenAutomaticly() {
        return true;
    }

    @Override
    protected Border getDefaultBorder() {
        return BasicBorders.getTextFieldBorder();
    }
}
