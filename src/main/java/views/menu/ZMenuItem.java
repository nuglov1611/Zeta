package views.menu;

import javax.swing.*;

public class ZMenuItem extends JMenuItem {
    String exp = null;

    public ZMenuItem() {
        super();
    }

    public void setExp(String e) {
        exp = e;
    }

    public String getExp() {
        return exp;
    }
}
