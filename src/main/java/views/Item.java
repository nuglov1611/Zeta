package views;

import action.calc.Nil;
import core.document.Document;
import core.parser.Proper;
import core.rml.RmlObject;
import loader.ZetaProperties;
import publicapi.MenuItemAPI;
import views.menu.ZMenuItem;

import javax.swing.*;

/**
 * Элемент меню
 */
public class Item extends RmlObject implements MenuItemAPI {

    private ZMenuItem item = new ZMenuItem();

    public Item() {
        item.setActionCommand("");
    }

    public void init(Proper prop, Document doc) {
        super.init(prop, doc);
        String sp;
        sp = (String) prop.get("LABEL");
        if (sp != null) {
            item.setText(sp);
        }
        sp = (String) prop.get("ACTION");
        if (sp != null) {
            item.setActionCommand(sp);
        } else {
            item.setActionCommand("");
        }

        item.setExp((String) prop.get("EXP"));

        if (ZetaProperties.views_debug > 0) {
            System.out.println("action command is " + getAction());
        }
    }

    /**
     * Возвращает скрипт выполняющийся при выборе этого элемента меню
     *
     * @return скрипт
     */
    public String getExp() {
        return item.getExp();
    }

    /**
     * Задает скрипт выполняющийся при выборе этого элемента меню
     *
     * @param exp скрипт
     */
    public void setExp(String exp) {
        item.setExp(exp);
    }

    // Методы интерфейса GlobalValuesObject
    public void setValue(Object o) {
    }

    public void setValueByName(String name, Object o) {
        if (name.toUpperCase().equals("LABEL")) {
            item.setText(o.toString());
        } else if (name.toUpperCase().equals("ACTION")) {
            item.setActionCommand(o.toString());
        } else if (name.toUpperCase().equals("EXP")) {
            item.setExp((String) o);
        }

    }

    public Object getValue() {
        return this;
    }

    public Object getValueByName(String name) {
        if (name.toUpperCase().equals("LABEL")) {
            return getLabel();
        } else if (name.toUpperCase().equals("ACTION")) {
            return getAction();
        } else if (name.toUpperCase().equals("EXP")) {
            return getExp();
        } else {
            return new Nil();
        }

    }


    /**
     * Возвращает действие документа, выполняющееся при выборе этого пункта меню
     *
     * @return скрипт-действие (скрипт, вычисляющий действие)
     */
    public String getAction() {
        return item.getActionCommand();
    }

    /**
     * Задает действие документа, выполняющееся при выборе этого пункта меню
     *
     * @param action - скрипт-действие (возможно скрипт, вычисляющий действие)
     */
    public void setAction(String action) {
        item.setActionCommand(action);
    }

    /**
     * Возвращает название данного пункта меню
     *
     * @return текст названия
     */
    public String getLabel() {
        return item.getText();
    }

    /**
     * Задает название элемента
     *
     * @param label - текст названия
     */
    public void setLabel(String label) {
        item.setText(label);
    }


    public String type() {
        return "SVR_ITEM";
    }

    @Override
    public Object method(String method, Object arg) throws Exception {
        if (method.equalsIgnoreCase("setEnabled")) {
            setEnabled(((String) arg).equalsIgnoreCase("yes"));
        }
        return null;
    }

    /**
     * Управляет состоянием элемента (делает активным/не активным)
     *
     * @param enabled true - активный, false - не активный (серый)
     */
    public void setEnabled(boolean enabled) {
        item.setEnabled(enabled);
    }

    /**
     * Возвращает состояние элемента
     *
     * @return true если элемент активный, false если не активный (серый)
     */
    public boolean isEnabled() {
        return item.isEnabled();
    }

    public String getText() {
        return getLabel();
    }

    public JMenuItem getItem() {
        return item;
    }

}
