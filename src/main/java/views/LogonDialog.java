package views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LogonDialog extends JDialog {

    static String userLabel = "Имя пользователя";

    static String passLabel = "Пароль";

    public int result = 2;                    // cancel;

    private JTextField user = new JTextField();

    private JPasswordField pass = new JPasswordField();

    JButton ok = new JButton("OK");

    JButton cancel = new JButton("Отмена");

    public LogonDialog(Frame parent, String title) {
        super(parent, title, true);
        if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            this.setUndecorated(true);
            this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
            // setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }
        setLayout(null);
        int th = 25;
        JLabel userl = new JLabel(userLabel, SwingConstants.LEFT);
        JLabel passl = new JLabel(passLabel, SwingConstants.LEFT);
        this.addWindowListener(new WL());
        ok.addActionListener(new AL());
        cancel.addActionListener(new AL());
        user.addKeyListener(new KL());
        pass.addKeyListener(new KL());
        ok.addKeyListener(new KL());
        cancel.addKeyListener(new KL());

        int lWidth = 125;
        int lHeight = 25;
        float c = 1.2f;
        int bpHeight = 35;
        int gap = 10;

        JPanel p1 = new JPanel();
        p1.setLayout(null);
        JPanel p2 = new JPanel();
        p2.setLayout(new FlowLayout());
        int fWidth = (int) (lWidth * c);
        int width = 2 * gap + lWidth + fWidth;
        int height = 2 * gap + 2 * lHeight + bpHeight;

        user.setBounds(gap + lWidth, gap, fWidth, lHeight);
        pass.setBounds(gap + lWidth, 2 * gap + lHeight, fWidth, lHeight);
        userl.setBounds(gap, gap, lWidth, lHeight);
        passl.setBounds(gap, 2 * gap + lHeight, lWidth, lHeight);

        p1.setBounds(0, th, width, height - bpHeight);
        p2.setBounds(0, th + height - bpHeight, width, bpHeight);
        p1.add(userl);
        p1.add(user);
        p1.add(passl);
        p1.add(pass);
        p2.add(ok);
        p2.add(cancel);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

        this.setBounds((d.width - width) / 2, (d.height - height - th) / 2,
                width, height + th + 30);
        this.add(p1);
        this.add(p2);
        // System.out.println("insets="+getInsets());

    }

    public String getUser() {
        return user.getText();
    }

    public String getPassword() {
        return new String(pass.getPassword());
    }

    class AL implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(ok)) {
                result = 1;
                // dispose();
                setVisible(false);
            } else if (e.getSource().equals(cancel)) {
                result = 2;
                // dispose();
                setVisible(false);
            }
        }
    }

    class WL extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            result = 2;
            // dispose();
            setVisible(false);
        }
    }

    class KL extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                if (e.getSource() == user) {
                    pass.requestFocus();
                } else if (e.getSource() == pass) {
                    new AL().actionPerformed(new ActionEvent(ok,
                            ActionEvent.ACTION_PERFORMED, ""));
                }
            } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                new AL().actionPerformed(new ActionEvent(cancel,
                        ActionEvent.ACTION_PERFORMED, ""));
            }
        }
    }
}
