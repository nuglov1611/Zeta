package core.rml.dbi;

import loader.ZetaProperties;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;

public class MsgDebug extends JFrame {

    JPanel p = new JPanel();

    JButton ButClose = new JButton("�������");

    JButton ButClear = new JButton("��������");

    boolean ok = false;

    JTextArea ta;

    class FL extends FocusAdapter {

        @Override
        public void focusGained(FocusEvent e) {
            ta.requestFocus();
        }
    }

    class KL extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                setVisible(false);
            } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                clear();
                setVisible(false);
            }
        }

    }

    class AL implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == ButClose) {
                ok = true;
            } else if (e.getSource() == ButClear) {
                clear();
                return;
            }
            setVisible(false);
        }
    }

    public MsgDebug(String title) {
        super(title);
        if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            this.setUndecorated(true);
            this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
            setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        }
        setIconImage(Toolkit.getDefaultToolkit().getImage(
                getClass().getClassLoader().getResource(ZetaProperties.IMAGE_ICON)));
        addFocusListener(new FL());

        KL kl = new KL();
        AL al = new AL();

        p.setLayout(new BorderLayout());

        ta = new JTextArea();
        ta.setBorder(new LineBorder(Color.BLACK));

        ta.setEditable(false);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);

        JScrollPane sp = new JScrollPane(ta);
        ta.setAutoscrolls(true);
        p.add("Center", sp);
        JPanel bp = new JPanel();
        p.add("South", bp);
        bp.setLayout(new GridLayout(1, 3));

        bp.add(ButClose);
        bp.add(ButClear);
        add(p);

        ta.addKeyListener(kl);
        Font fnt = new Font("Arial", Font.PLAIN, 10);
        ta.setFont(fnt);
        ButClose.addKeyListener(kl);
        ButClear.addKeyListener(kl);

        ButClose.addActionListener(al);
        ButClear.addActionListener(al);

    }

    public void addMessage(String msg) {
        ta.append(msg);
        Rectangle r = new Rectangle(1, ta.getHeight(), 1, 20);
        ta.scrollRectToVisible(r);
    }

    public void clear() {
        ta.setText("");
    }
}
