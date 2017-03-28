package views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import views.grid.GridSwing;
import core.browser.WorkspaceManager;
import core.reflection.objects.VALIDATOR;

// Данный класс нужен для задания параметров фильтрации

// и сортировки столбцов Grid'a с помощью диалогового окна
public class FilterDialog extends JDialog {
    
    public static int  OK          = 0;

    public static int  RESET       = 1;

    public static int  CANCEL      = 2;

    public int         result      = -1;

    JButton ok = new JButton(StringBundle.FilterDialog_Button_Ok);

    JButton cancel = new JButton(StringBundle.FilterDialog_Button_Cancel);

    JButton reset = new JButton(StringBundle.FilterDialog_Button_Reset);

    JButton save = new JButton(StringBundle.FilterDialog_Button_Save);

    JPanel             p           = new JPanel();                                  // сюда будут встроены header, spane, buttonPanel

    JPanel             header      = new JPanel();

    JPanel             buttonPanel = new JPanel();                                  // в эту панель будут встраиваться кнопки

    ScrollPane         spane       = new ScrollPane();                              // в эту ScrollPane встроится p1

    JPanel             p1          = new JPanel();                                  // в эту панель будут встраиваться субпанели

    JPanel[]           subpanels   = null;                                          // в эту панель будут встраиваться эл-ты

    // управления

    int                rowSize     = 30;

    int                bpHeight    = 35;

    int                left        = 30;                                            // отступы при расположении элементов

    int                top         = 15;                                            //

    GridSwing          parent      = null;

    FilterStruct[]     data        = null;

    JLabel[]           titles      = null;

    SmartCheckbox[][]  boxes       = null;

    JLabel[]           sortOrder   = null;

    SmartTextField[][] values      = null;

    int                numRows     = 0;

    public FilterDialog(GridSwing parent, String title, int width, int height) {
        super(WorkspaceManager.getCurWorkspace().getFrame(), title, true);
        setLayout(new GridLayout(1, 1));
        setSize(width, height);
        this.parent = parent;
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        if (parent == null) {
            return;
        }
        subpanels = new JPanel[numRows];
        for (int i = 0; i < data.length; i++) {
            subpanels[i] = new JPanel();
            subpanels[i].setBounds(0, rowSize * i + top, getSize().width,
                    rowSize);
            subpanels[i].setLayout(null);
        }

        // System.out.println("before adding controls into dialog");
        // Добавляем эл-ты управления в диалоговое окно
        p.setSize(getSize().width, getSize().height);
        p.setLayout(new BorderLayout());

        header.setSize(getSize().width, rowSize);
        header.setLayout(null);
        buttonPanel.setSize(getSize().width, bpHeight);
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(ok);
        ok.addKeyListener(new KL());
        buttonPanel.add(reset);
        reset.addKeyListener(new KL());
        buttonPanel.add(save);
        save.addKeyListener(new KL());
        buttonPanel.add(cancel);
        cancel.addKeyListener(new KL());

        p1.setSize(getSize().width, numRows * rowSize);
        // p1.setLayout(new GridLayout(numRows,1));
        p1.setLayout(null);

        for (int i = 0; i < data.length; i++) {
            if (data[i].sortOrder != 0) {
                sortOrder[i].setText("(" + (data[i].sortOrder) + ")");
            }
            boxes[i][0].setState(data[i].sort, 0);
            boxes[i][0].sort = data[i].sortOrder;
        }
        ((JScrollPane) spane.getVisualComponent()).getViewport().add(p1);

        p.add(header, "North");
        p.add(spane.getVisualComponent().getJComponent(), "Center");
        p.add(buttonPanel, "South");

        this.getContentPane().add(p); // добавляем "всеобъемлющую" панель в
        // диалоговое окно
    }

    public FilterStruct[] getFilterStruct() {
        boolean result = false;
        for (int i = 0; i < numRows; i++) {
            data[i].sortOrder = boxes[i][0].sort;
            data[i].sort = boxes[i][0].isSelected();
            result |= boxes[i][0].isSelected();
            data[i].filter = boxes[i][1].isSelected();
            result |= boxes[i][1].isSelected();
            data[i].minValue = values[i][0].getValue();
            result |= (data[i].minValue != null);
            data[i].maxValue = values[i][1].getValue();
            result |= (data[i].maxValue != null);
        }
        if (!result) {
            return null;
        } else {
            return data;
        }
    }

    class SmartCheckbox extends JCheckBox {
        SmartCheckbox[][] siblings = null;

        JLabel[]          labels   = null;

        boolean           isSort   = false;

        int               sort     = 0;    // приоритет в sort order'e

        public SmartCheckbox(SmartCheckbox[][] siblings, JLabel[] labels,
                boolean isSort) {
            super();
            this.siblings = siblings;
            this.labels = labels;
            this.isSort = isSort;
        }

        public int getIndex() { // возвращает порядковый номер эл-та в массиве
            // siblings
            if (siblings == null) {
                return -1;
            }
            for (int i = 0; i < siblings.length; i++) {
                if (siblings[i][0].equals(this)) {
                    return i;
                }
            }
            return -1;
        }

        public int getMaxPrior() {
            if (siblings == null) {
                return 0;
            }
            int current = 0;
            for (int i = 0; i < siblings.length; i++) {
                if (siblings[i][0].sort > current) {
                    current = siblings[i][0].sort;
                }
            }
            return current;
        }

        public void setState(boolean b) { // в этом методе пересчитываем
            // sortOrder
            super.setSelected(b);
            if (!isSort) {
                return;
            }
            if (labels == null || siblings == null) {
                return;
            }
            // System.out.println("item state changed!");
            if (b) {
                int ind = getIndex();
                if (ind < 0) {
                    return;
                }
                if (labels[ind].getText().equals("")) {
                    int sp = getMaxPrior();
                    sort = sp + 1;
                    labels[ind].setText(new String("(" + (sort) + ")"));
                }
            } else {
                if (sort > 0) {
                    labels[getIndex()].setText("");
                    for (int i = 0; i < siblings.length; i++) {
                        if (siblings[i][0].sort > sort) {
                            siblings[i][0].sort--;
                            labels[siblings[i][0].getIndex()]
                                    .setText(new String("("
                                            + siblings[i][0].sort + ")"));
                        }
                    }
                    sort = 0;
                }
            }
        }

        public void setState(boolean b, int f) {
            super.setSelected(b);
        }

    }

    class SmartTextField extends JTextField implements FocusListener {
    	VALIDATOR validator;

        Object    value = null;

        public SmartTextField(VALIDATOR v) {
            super();
            validator = v;
            addFocusListener(this);
        }

        public void setValue(Object value) {
            this.value = value;
            try {
                String str = validator.toString(value);
                setText(str);
            }
            catch (Exception ex) {
                ex.printStackTrace();
                setText("");
            }
        }

        public Object getValue() {
            return value;
        }

        public void focusLost(FocusEvent e) {
            if (validator != null) {
                String str = getText();
                if (str.equals("")) {
                    setValue(null);
                    return;
                }
                ;
                try {
                    value = validator.toObject(str);
                    str = validator.toString(value);
                    setText(str);
                }
                catch (Exception ex) {

                    ex.printStackTrace();
                    setText("");
                }
            }
        }

        public void focusGained(FocusEvent e) {
        }
    }

    class KL extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                if (e.getSource() instanceof SmartTextField) {
                    ((SmartTextField) e.getSource()).focusLost(null);
                }
                new AL().actionPerformed(new ActionEvent(ok,
                        ActionEvent.ACTION_PERFORMED, ""));
            } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                if (e.getSource() instanceof SmartTextField) {
                    ((SmartTextField) e.getSource()).focusLost(null);
                }
                new AL().actionPerformed(new ActionEvent(cancel,
                        ActionEvent.ACTION_PERFORMED, ""));
            }
        }
    }

    class AL implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(ok)) {
                result = OK;
                FilterDialog.this.dispose();
                return;
            }
            if (e.getSource().equals(cancel)) {
                result = CANCEL;
                FilterDialog.this.dispose();
                return;
            }
            if (e.getSource().equals(reset)) {
                result = RESET;
                FilterDialog.this.dispose();
                return;
            }
            if (e.getSource().equals(save)) {
                try {
                    // parent.saveFilter(getFilterStruct());
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    System.out
                            .println("views.Filterdialog$AL::actionPerformed : "
                                    + ex);
                }

            }

        }
    }
}
