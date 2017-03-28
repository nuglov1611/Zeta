package core.rml.dbi;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Progress extends JFrame {
    JLabel lb;

    public Progress() {
        super("");
        setResizable(false);
        setSize(200, 100);
        setLayout(new BorderLayout());
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        lb = new JLabel("");
        p.add("North", lb);

        add(p);
        setVisible(false);
    }

    public void setprogress(int rows) {
        String text = rows + ":";
        lb.setText(text);
    }

}
