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
 * ������� ����
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
     * ���������� ������ ������������� ��� ������ ����� �������� ����
     *
     * @return ������
     */
    public String getExp() {
        return item.getExp();
    }

    /**
     * ������ ������ ������������� ��� ������ ����� �������� ����
     *
     * @param exp ������
     */
    public void setExp(String exp) {
        item.setExp(exp);
    }

    // ������ ���������� GlobalValuesObject
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
     * ���������� �������� ���������, ������������� ��� ������ ����� ������ ����
     *
     * @return ������-�������� (������, ����������� ��������)
     */
    public String getAction() {
        return item.getActionCommand();
    }

    /**
     * ������ �������� ���������, ������������� ��� ������ ����� ������ ����
     *
     * @param action - ������-�������� (�������� ������, ����������� ��������)
     */
    public void setAction(String action) {
        item.setActionCommand(action);
    }

    /**
     * ���������� �������� ������� ������ ����
     *
     * @return ����� ��������
     */
    public String getLabel() {
        return item.getText();
    }

    /**
     * ������ �������� ��������
     *
     * @param label - ����� ��������
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
     * ��������� ���������� �������� (������ ��������/�� ��������)
     *
     * @param enabled true - ��������, false - �� �������� (�����)
     */
    public void setEnabled(boolean enabled) {
        item.setEnabled(enabled);
    }

    /**
     * ���������� ��������� ��������
     *
     * @return true ���� ������� ��������, false ���� �� �������� (�����)
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
