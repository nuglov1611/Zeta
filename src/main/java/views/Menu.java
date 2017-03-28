package views;

import action.api.RTException;
import core.document.Document;
import core.parser.Proper;
import core.rml.Container;
import core.rml.RmlObject;
import org.apache.log4j.Logger;
import publicapi.MenuAPI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * Контекстное меню
 */
public class Menu extends RmlObject implements MenuAPI {
    private static final Logger log = Logger.getLogger(Menu.class);

    private Container container = new Container(this);

    JMenu menu = new JMenu();

    String label = "Submenu1";

    Color bg_color = null;

    public Menu() {
    }

    public void addActionListenerRecursiv(ActionListener al) {
        int item_count = menu.getItemCount(); // getItemCount();
        Object cur_itm = null;
        for (int i = 0; i < item_count; i++) {
            cur_itm = menu.getItem(i); // menu.getItem(i);
            if (cur_itm != null) {
                if (cur_itm instanceof JMenu) {
                    views.Menu tmp_menu = new views.Menu();
                    tmp_menu.setMenu((JMenu) cur_itm);
                    tmp_menu.addActionListenerRecursiv(al);
                } else if (cur_itm instanceof JMenuItem) {
                    ((JMenuItem) cur_itm).addActionListener(al);
                }
            }
        }
    }

    public void append(JMenuItem mi) {
        menu.add(mi);
    }

    public Color getBackground() {
        return bg_color;
    }

    public JMenuItem getItem(int index) {
        return menu.getItem(index); // menu.getItem(index);
    }

    public int getItemCount() {
        return menu.getItemCount(); // menu.getItemCount();
    }

    public String getLabel() {
        return label;
    }

    public JMenu getMenu() {
        return menu;
    }

    public Object getValue() {
        return this;
    }

    public Object getValueByName(String name) {
        try {
            return getItem(Integer.parseInt(name));
        } catch (NumberFormatException e) {
            log.error("Shit happen!", e);
            return label;
        }
    }

    public void init(Proper prop, Document doc) {
        super.init(prop, doc);
        String sp;
        sp = (String) prop.get("LABEL");
        if (sp != null) {
            label = sp;
        }
        menu.setText(label);

        sp = (String) prop.get("BG_COLOR");
        if (sp != null) {
            bg_color = UTIL.getColor(sp);
        }
    }

    public Object method(String method, Object arg) throws Exception {
        if (method.toUpperCase().equals("SIZE")) {
            return new Double(getSize());
        } else if (method.toUpperCase().equals("REMOVE")) {
            try {
                int i = ((Double) arg).intValue();
                removeItem(i);

            } catch (Exception e) {
                log.error("Shit happen", e);
                throw new RTException("CastException",
                        "method REMOVE must have one Numeric parameter"
                                + e.getMessage());
            }
            return new Double(0);
        }
        if (method.toUpperCase().equals("GET")) {
            return getItemAt(((Double) arg).intValue());
        }
        if (method.toUpperCase().equals("PUT")) {
            Double d = (Double) ((Vector<Object>) arg).elementAt(0);

            putItem(d.intValue(), (Item) ((Vector<Object>) arg).elementAt(1));
            return new Double(0);
        } else {
            throw new RTException("HasNotMethod", "method " + method
                    + " not defined in class views.Menu!");
        }
    }

    /**
     * Возвращает элемент меню
     *
     * @param i номер элемента
     * @return элемент меню
     */
    public JMenuItem getItemAt(int i) {
        return getItem(i);
    }

    /**
     * Удаляет элемент меню
     *
     * @param i номер элемента
     */
    public void removeItem(int i) {
        menu.remove(i);
    }

    /**
     * Возвращает кол-во элементов в меню
     *
     * @return кол-во элементов
     */
    public int getSize() {
        return menu.getItemCount();
    }

    public void set(JMenuItem it, int i) {
        menu.insert(it, i);
    }

    public void setMenu(JMenu menu) {
        this.menu = menu;
    }

    // Методы интерфейса GlobalValuesObject
    public void setValue(Object o) {
    }

    public void setValueByName(String name, Object o) {
        try {
            putItem(Integer.parseInt(name), (Item) o);
        } catch (NumberFormatException e) {
            log.error("Shit happen!", e);
            label = o.toString();
        }
    }

    /**
     * Добавляет новый элемент в меню. Если номер будет больше чем кол-во элементов в меню, то элемент добавится в конец меню
     *
     * @param i    - номер для добавления элемента
     * @param item - элемент меню
     */
    public void putItem(int i, Item item) {
        if (i > menu.getItemCount() - 1) {
            append(item.getItem());
        } else {
            set(item.getItem(), i);
        }
    }

    public String type() {
        return "SVR_MENU";
    }

    public void show(Component comp, int x, int y) {
        menu.getPopupMenu().show(comp, x, y);
    }

    public JMenuItem add(JMenuItem item) {
        return menu.add(item);
    }

    @Override
    public void addChild(RmlObject child) {
        container.addChildToCollection(child);

        if (child instanceof views.Item) {
            if (((views.Item) child).getText().equals("-")) {
                menu.addSeparator();
            } else {
                JMenuItem mi = ((views.Item) child).getItem();
                mi.addActionListener(null); // нужно!
                menu.add(mi);
            }
        } else if (child instanceof views.Menu) {
            JMenu m = ((views.Menu) child).getMenu();
            menu.add(m);
        }
    }

    @Override
    public RmlObject[] getChildren() {
        return container.getChildren();
    }

    @Override
    public void initChildren() {
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public boolean addChildrenAutomaticly() {
        return true;
    }
}
