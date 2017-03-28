package views.field;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import views.grid.editor.DateField;

public class DateCalendar extends JFrame {

    private static final long serialVersionUID = 1L;

    private static DateCalendar cld;

    private static final int[] daysOfWeek = {Calendar.MONDAY,
            Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY,
            Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY};

    private static final String[] daysOfWeekNames = {"Пн", "Вт", "Ср", "Чт",
            "Пт", "Сб", "Вс"};

    private static final String[] monthNames = {"Январь", "Февраль",
            "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь",
            "Октябрь", "Ноябрь", "Декабрь"};

    public static DateCalendar getInstance(BaseField parField) {
        if (cld == null) {
            cld = new DateCalendar();
        }
        cld.parField = parField;
        Date dt = (Date) parField.getValue();
        if (dt != null) {
            cld.execute(dt);
        }
        else {
            cld.execute(new Date(System.currentTimeMillis()));
        }
        return cld;
    }

    private JLabel bDownYear = new JLabel("<<", SwingConstants.CENTER);

    private JLabel bDownMonth = new JLabel("<", SwingConstants.CENTER);

    private JLabel lCaption = new JLabel("", SwingConstants.CENTER);

    private JLabel bUpYear = new JLabel(">>", SwingConstants.CENTER);

    private JLabel bUpMonth = new JLabel(">", SwingConstants.CENTER);

    private JLabel[] days = new JLabel[42];

    private int cellWidth = 0;

    private int cellHeight = 0;

    private int rowCount = 6;

    private int num_days = rowCount * 7;

    private Calendar curDate = Calendar.getInstance();

    private JLabel curLabel = null;

    private int curNum = 0;

    private BaseField parField = null;
    
    private JPanel cPanel = new JPanel();


    private DateCalendar() {
        addKeyListener(new KL(this));
        addFocusListener(new FL(this));
        setUndecorated(true);
    }

    @Override
    public void dispose() {
        super.dispose();
        parField.requestFocus();
    }

    private void execute(Date initial) {
//        setBackground(Color.lightGray);
        setLayout(new GridLayout(1,1));
    	add(cPanel);
    	cPanel.setLayout(null);
    	cPanel.setBackground(Color.WHITE);
    	cPanel.add(bDownYear);
    	cPanel.add(bDownMonth);
    	cPanel.add(lCaption);
        lCaption.setBackground(Color.lightGray);
        cPanel.add(bUpMonth);
        cPanel.add(bUpYear);

        for (int i = 0; i < num_days; i++) {
            days[i] = new JLabel("", SwingConstants.CENTER);
            days[i].addMouseListener(new ML(this));
            cPanel.add(days[i]);
        }

        bDownYear.addMouseListener(new ML(this));
        bDownMonth.addMouseListener(new ML(this));
        bUpYear.addMouseListener(new ML(this));
        bUpMonth.addMouseListener(new ML(this));

        curDate.setTime(initial);

        pack();

        Graphics grph = getGraphics();
        FontMetrics mtr = grph.getFontMetrics(grph.getFont());

        cellWidth = mtr.stringWidth("30") * 2;
        cellHeight = mtr.getHeight() * 5 / 4;

        setSize(cellWidth * 7 + 4, cellHeight * (rowCount + 2) + 8);

        Dimension dm = new Dimension(cellWidth * 7 + 4, cellHeight * 8 + 8);
        Rectangle parRect = new Rectangle(parField.getBounds());
        parRect.setLocation(parField.getLocationOnScreen());
        parRect.translate(0, parRect.height);
        int j = parRect.x + dm.width
                - Toolkit.getDefaultToolkit().getScreenSize().width;
        if (j > 0) {
            parRect.translate(-j, 0);
        }
        j = parRect.y + parRect.height + dm.height
                - Toolkit.getDefaultToolkit().getScreenSize().height;
        if (j > 0) {
            parRect.translate(0, -dm.height - parRect.height);
        }
        setLocation(parRect.getLocation());

        int butWidth = mtr.stringWidth("<<") * 3 / 2;
        bDownYear.setSize(butWidth, cellHeight);
        bDownMonth.setSize(butWidth, cellHeight);
        bUpYear.setSize(butWidth, cellHeight);
        bUpMonth.setSize(butWidth, cellHeight);
        int captWidth = cellWidth * 7 - butWidth * 4;
        lCaption.setSize(captWidth, cellHeight);

        bDownYear.setLocation(2, 2);
        bDownMonth.setLocation(butWidth + 2, 2);
        lCaption.setLocation(butWidth * 2 + 2, 2);
        bUpMonth.setLocation(butWidth * 2 + captWidth + 2, 2);
        bUpYear.setLocation(butWidth * 3 + captWidth + 2, 2);

        for (int k = 0; k < rowCount; k++) {
            for (int i = 0; i < 7; i++) {
                days[k * 7 + i].setLocation(i * cellWidth + 2, (k + 2)
                        * cellHeight + 4);
                days[k * 7 + i].setSize(cellWidth, cellHeight);
            }
            days[k * 7 + 6].setForeground(Color.red);
        }

        for (int i = 0; i < 7; i++) {
            JLabel tmp = new JLabel(daysOfWeekNames[i], SwingConstants.CENTER);
            cPanel.add(tmp);
            tmp.setLocation(i * cellWidth + 2, cellHeight + 4);
            tmp.setSize(cellWidth, cellHeight - 2);
            //tmp.setBackground(Color.lightGray);
            tmp.setBackground(Color.white);
            if (i == 6) {
                tmp.setForeground(Color.red);
            }
        }

        validate();

        fillCurMonth();

        setVisible(true);

        requestFocus();

    }

    private void fillCurMonth() {
        Calendar firstDay = Calendar.getInstance();
        firstDay.setTime(curDate.getTime());
        firstDay.set(Calendar.DAY_OF_MONTH, 1);
        int first = firstDay.get(Calendar.DAY_OF_WEEK);
        int j = 0;
        while (first != daysOfWeek[j]) {
            days[j].setText("");
            days[j].setEnabled(false);
            days[j].setBorder(null);
            days[j].setOpaque(false);
            j++;
        }
        firstDay.add(Calendar.MONTH, 1);
        firstDay.add(Calendar.DAY_OF_MONTH, -1);
        first = firstDay.get(Calendar.DAY_OF_MONTH);

        int i = 1;
        while (i <= first) {
            for (; j < num_days && i <= first; i++) {
                days[j].setText(String.valueOf(i));
                days[j].setEnabled(true);
                days[j].setBorder(null);
                days[j].setOpaque(false);
                if (i == curDate.get(Calendar.DAY_OF_MONTH)
                        && days[j] != curLabel) {
                    days[j].setBackground(Color.lightGray);
                    days[j].setForeground(Color.blue);
                    days[j].setOpaque(true);
                    days[j].setBorder(new LineBorder(Color.black));
                    if (curLabel != null) {
                        if (curNum % 7 == 6) {
//                            curLabel.setBackground(Color.lightGray);
                            curLabel.setForeground(Color.red);
                        } else {
//                            curLabel.setBackground(Color.lightGray);
                            curLabel.setForeground(Color.black);
                        }
                    }
                    curLabel = days[j];
                    curNum = j;
                }
                j++;
            } // for (int i = 1; j < num_days && i <= first; i++)

            if (i <= first) {
                num_days = ++rowCount * 7;
                setSize(getSize().width, getSize().height + cellHeight);
                for (int m = days.length - 7; m < days.length; m++) {
                    days[m].setVisible(true);
                }
            } // if (i <= first)

        } // while (i <= first)

        if (num_days - j >= 7) {
            num_days = --rowCount * 7;
            setSize(getSize().width, getSize().height - cellHeight);
            for (int m = days.length - 7; m < days.length; m++) {
                days[m].setVisible(false);
            }
        } // if (num_days - j > 7)
        for (; j < num_days; j++) {
            days[j].setText("");
            days[j].setEnabled(false);
        }

        lCaption.setText(monthNames[curDate.get(Calendar.MONTH)] + ", "
                + curDate.get(Calendar.YEAR));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        int tmp = getSize().width;
        g.drawRect(0, 0, tmp - 1, getSize().height - 1);
        g.drawRect(1, 1, tmp - 3, getSize().height - 3);
        g.drawLine(0, cellHeight + 2, tmp - 1, cellHeight + 2);
        g.drawLine(0, (cellHeight << 1) + 2, tmp - 1, (cellHeight << 1) + 2);
    }

    /**
     * ****************************
     * Listeners           *
     * begin               *
     * ******************************
     */
    class FL extends FocusAdapter {
        protected DateCalendar owner;

        public FL(DateCalendar calendar) {
            owner = calendar;
        }

        @Override
        public void focusLost(FocusEvent e) {
            owner.setVisible(false);
            owner.dispose();
            cld = null;
        }
    }

    class KL extends KeyAdapter {
        DateCalendar owner;

        public KL(DateCalendar cld) {
            owner = cld;
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getModifiers() == 0) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    curDate.add(Calendar.DAY_OF_MONTH, -1);
                    fillCurMonth();
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    curDate.add(Calendar.DAY_OF_MONTH, 1);
                    fillCurMonth();
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    curDate.add(Calendar.DAY_OF_MONTH, -7);
                    fillCurMonth();
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    curDate.add(Calendar.DAY_OF_MONTH, 7);
                    fillCurMonth();
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_PAGE_UP) {
                    curDate.add(Calendar.MONTH, -1);
                    fillCurMonth();
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
                    curDate.add(Calendar.MONTH, 1);
                    fillCurMonth();
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER
                        || e.getKeyCode() == KeyEvent.VK_SPACE) {
//                    if (!parField.isEditable()) {
//                        parField.setValue(curDate.getTime());
//                        parField.finishTheEditing();
//                    }
                    if (parField instanceof DateField){ 
                    	if(parField.isEditable()) 
                    		parField.setValue(curDate.getTime());
                    }else{
                		parField.setTextOnly(curDate.getTime());
                		parField.finishTheEditing();
                    }
                    dispose();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            } else if ((e.getModifiers() & (InputEvent.SHIFT_MASK
                    + InputEvent.CTRL_MASK + InputEvent.ALT_MASK)) == InputEvent.CTRL_MASK) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT
                        || e.getKeyCode() == KeyEvent.VK_UP) {
                    curDate.add(Calendar.MONTH, -1);
                    fillCurMonth();
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT
                        || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    curDate.add(Calendar.MONTH, 1);
                    fillCurMonth();
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_PAGE_UP) {
                    curDate.add(Calendar.YEAR, -1);
                    fillCurMonth();
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
                    curDate.add(Calendar.YEAR, 1);
                    fillCurMonth();
                    e.consume();
                }
            } else if ((e.getModifiers() & (InputEvent.SHIFT_MASK
                    + InputEvent.CTRL_MASK + InputEvent.ALT_MASK)) == InputEvent.ALT_MASK) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT
                        || e.getKeyCode() == KeyEvent.VK_UP) {
                    curDate.add(Calendar.YEAR, -1);
                    fillCurMonth();
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT
                        || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    curDate.add(Calendar.YEAR, 1);
                    fillCurMonth();
                    e.consume();
                }
            }
        }
    }

    class ML extends MouseAdapter {
        protected DateCalendar owner;

        public ML(DateCalendar calendar) {
            owner = calendar;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getSource() == owner.bDownYear) {
                owner.curDate.add(Calendar.YEAR, -1);
                owner.fillCurMonth();
            } else if (e.getSource() == owner.bDownMonth) {
                owner.curDate.add(Calendar.MONTH, -1);
                owner.fillCurMonth();
            } else if (e.getSource() == owner.bUpYear) {
                owner.curDate.add(Calendar.YEAR, 1);
                owner.fillCurMonth();
            } else if (e.getSource() == owner.bUpMonth) {
                owner.curDate.add(Calendar.MONTH, 1);
                owner.fillCurMonth();
            } else {
                owner.curDate.set(Calendar.DAY_OF_MONTH, Integer
                        .parseInt(((JLabel) e.getComponent()).getText()));
                if (owner.parField instanceof DateField){ 
                	if(owner.parField.isEditable()) 
                		owner.parField.setValue(curDate.getTime());
                }else{
            		owner.parField.setTextOnly(curDate.getTime());
            		owner.parField.finishTheEditing();
                }
                owner.dispose();
            }
        }
    }
}
