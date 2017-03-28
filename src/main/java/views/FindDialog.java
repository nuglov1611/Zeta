package views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

import views.grid.GridSwing;
import core.browser.WorkspaceManager;

public class FindDialog extends JDialog {
    JCheckBox      casebox;

    JCheckBox      down;

    JCheckBox      up;

    ButtonGroup    direction;

    JTextField     pattern;

    JButton        find;

    JButton        close;

    Object         parent;

    public boolean find_pressed;

    public boolean go_down = true;

    public boolean caze    = false;

    public String  text;

    public FindDialog(Object parent, String title, int width, int height,
            boolean modal) {
        super(WorkspaceManager.getCurWorkspace().getFrame(), title, modal);
        if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            this.setUndecorated(true);
            this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
            // setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.parent = parent;
        JPanel p1, p2, p3, p21, p22, p211, p212, p213, p221, p222, p223;
        setSize(width, height);
        setResizable(false);
        direction = new ButtonGroup();
        setLayout(new BorderLayout());
        p1 = new JPanel();
        p1.setLayout(new FlowLayout(FlowLayout.LEFT));
        p1.add(new JLabel(StringBundle.FindDialog_Pattern));
        pattern = new JTextField(30);
        pattern.addKeyListener(new KL());
        p1.add(pattern);

        p2 = new JPanel();
        p2.setLayout(new GridLayout(1, 2));
        p21 = new JPanel();
        p21.setLayout(new GridLayout(3, 1));
        // p21.add(new Label(FindDialog_Direction));
        p211 = new JPanel();
        p211.setLayout(new FlowLayout(FlowLayout.LEFT));
        p211.add(new JLabel(StringBundle.FindDialog_Direction));
        p212 = new JPanel();
        p212.setLayout(new FlowLayout(FlowLayout.LEFT));
        down = new JCheckBox(StringBundle.FindDialog_Direction_Down, true);
        direction.add(down);
        down.addItemListener(new IL());
        down.addKeyListener(new KL());
        p212.add(down);
        p213 = new JPanel();
        p213.setLayout(new FlowLayout(FlowLayout.LEFT));
        up = new JCheckBox(StringBundle.FindDialog_Direction_Up, false);
        direction.add(up);
        up.addItemListener(new IL());
        up.addKeyListener(new KL());
        p213.add(up);
        p21.add(p211);
        p21.add(p212);
        p21.add(p213);
        p22 = new JPanel();
        p22.setLayout(new GridLayout(3, 1));
        p221 = new JPanel();
        p222 = new JPanel();
        p223 = new JPanel();
        p223.setLayout(new FlowLayout(FlowLayout.LEFT));
        // casebox = new Checkbox(StringBundle.FindDialog_Case,true);
        casebox = new JCheckBox(StringBundle.FindDialog_Case, caze);
        casebox.addItemListener(new IL());
        casebox.addKeyListener(new KL());
        p223.add(casebox);
        p22.add(p221);
        p22.add(p222);
        p22.add(p223);
        p2.add(p21);
        p2.add(p22);

        p3 = new JPanel();
        find = new JButton(StringBundle.FindDialog_Button_Find);
        find.addActionListener(new BL());
        find.addKeyListener(new KL());
        close = new JButton(StringBundle.FindDialog_Button_Close);
        close.addActionListener(new BL());
        close.addKeyListener(new KL());
        p3.add(find);
        p3.add(close);

        getContentPane().add(p1, BorderLayout.NORTH);
        getContentPane().add(p2, BorderLayout.CENTER);
        getContentPane().add(p3, BorderLayout.SOUTH);

        // ¬ыравниваем диалог по центру приложени€
        if (parent instanceof GridSwing) {
            setLocationRelativeTo(((GridSwing) parent).getVisualComponent().getTopLevelAncestor());
        }
    }

    @Override
    public void setVisible(boolean visibility) {
        if (visibility) {
            // ƒанный поток устанавливает заголовок окна
            // new Thread(new TitleSetter()).start();
            super.setVisible(true);
            pattern.requestFocus();
        }
        else {
            super.setVisible(false);
        }
    }

    class KL extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                new BL().actionPerformed(new ActionEvent(find,
                        ActionEvent.ACTION_PERFORMED, ""));
            }
            else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                new BL().actionPerformed(new ActionEvent(close,
                        ActionEvent.ACTION_PERFORMED, ""));
            }
        }
    }

    class BL implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(find)) {
                find_pressed = true;
                text = pattern.getText();
                dispose();
                if (parent != null) {
                    if (parent instanceof GridSwing) {
                        ((GridSwing) parent).focusThis();
                    }
                }
                return;
            }
            if (e.getSource().equals(close)) {
                find_pressed = false;
                dispose();
                if (parent != null) {
                    if (parent instanceof GridSwing) {
                        ((GridSwing) parent).focusThis();
                    }
                }
                return;
            }
        }
    }

    class IL implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            if (e.getSource().equals(down)) {
                if (((JCheckBox) e.getSource()).isSelected()) {
                    go_down = true;
                }
                else {
                    go_down = false;
                }
                return;
            }
            if (e.getSource().equals(up)) {
                if (((JCheckBox) e.getSource()).isSelected()) {
                    go_down = false;
                }
                else {
                    go_down = true;
                }
                return;
            }
            if (e.getSource().equals(casebox)) {
                if (((JCheckBox) e.getSource()).isSelected()) {
                    caze = true;
                }
                else {
                    caze = false;
                }
                return;
            }
        }
    }
}
