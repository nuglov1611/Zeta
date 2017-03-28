package views;

import core.document.Document;
import core.parser.Proper;
import core.rml.VisualRmlObject;
import core.rml.ui.impl.ZLabelImpl;
import core.rml.ui.interfaces.ZComponent;
import core.rml.ui.interfaces.ZLabel;
import publicapi.LabelAPI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.StringTokenizer;

/**
 * Визуальный компонент "надпись"
 */
public class Label extends VisualRmlObject implements LabelAPI {

    private ZLabel label = ZLabelImpl.create();

    int dw = 2;

    int dh = 2;

    String border = "NONE";

//    Font                      scaleFont     = null;    // данный фонт используется для отрисовки

    // с масштабированием
    FontMetrics fm = null;

    String svalue = null;

    String halignment = "LEFT";

    String valignment = "CENTER";

    String visible = "YES";

    private Object parent = null;

    private Color bg_color = null;

    public boolean needTranslate = false;

    boolean wordWrap = false;

    boolean multiLine = false;

    public void init(Proper prop, Document doc) {
        super.init(prop, doc);
        String sp;
        Integer ip;

        fm = label.getFontMetrics(label.getFont());


        sp = (String) prop.get("HALIGNMENT");
        if (sp != null) {
            halignment = sp;
            if (halignment.equals("CENTER")) {
                label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            } else if (halignment.equals("LEFT")) {
                label.setAlignmentX(JLabel.LEFT_ALIGNMENT);
            } else if (halignment.equals("RIGHT")) {
                label.setAlignmentX(JLabel.RIGHT_ALIGNMENT);
            }
        }

        sp = (String) prop.get("VALIGNMENT");
        if (sp != null) {
            valignment = sp;
            if (valignment.equals("CENTER")) {
                label.setAlignmentY(JLabel.CENTER_ALIGNMENT);
            } else if (valignment.equals("TOP")) {
                label.setAlignmentY(JLabel.TOP_ALIGNMENT);
            } else if (valignment.equals("BOTTOM")) {
                label.setAlignmentY(JLabel.BOTTOM_ALIGNMENT);
            }
        }

        sp = (String) prop.get("BORDER");
        if (sp != null) {
            border = sp;
        }

        sp = (String) prop.get("MULTILINE");
        multiLine = sp != null && sp.toUpperCase().equals("YES");

        sp = (String) prop.get("WORDWRAP");
        wordWrap = sp != null && sp.toUpperCase().equals("YES");

        sp = (String) prop.get("VALUE");
        if (sp != null) {
            svalue = sp;
            label.setText(getText());

        }
    }

    public void setScaleFont(int a) {
        Font tmp = label.getFont();
        if (tmp == null) {
            return;
        }
        font = new Font(tmp.getName(), tmp.getStyle(), tmp.getSize() * a
                / 100);
    }

    public void paint(Graphics g, int a) {
        // pavel patch
        try {
            if (document.calculateMacro(visible).equalsIgnoreCase("NO")) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (!label.isVisible()) {
                return;
            }
        }
        // end
        int dx = label.getBounds().x;
        int dy = label.getBounds().y;
        if (!needTranslate) {
            dx = 0;
            dy = 0;
        }
        g.translate(dx * a / 100, dy * a / 100);
        int width = getSize().width;
        int height = getSize().height;
        /* ----------- */
        g.setColor(label.getBackground());
        g.fillRect(0, 0, width * a / 100, height * a / 100);
        /* ----------- */

        if (border.equals("3DLOWERED")) {
            g.setColor(Color.darkGray);
            g.drawLine(0, 0, width * a / 100, 0);
            g.drawLine(0, 0, 0, height * a / 100);

            g.setColor(Color.white);
            g.drawLine(0, height * a / 100, width * a / 100, height * a / 100);
            g.drawLine(width * a / 100, height * a / 100, width * a / 100, 0);

            g.setColor(Color.black);
            g.drawLine(a / 100, a / 100, (width - 1) * a / 100, a / 100);
            g.drawLine(a / 100, a / 100, a / 100, (height - 1) * a / 100);

            g.setColor(Color.lightGray);
            g.drawLine(a / 100, (height - 1) * a / 100, (width - 1) * a / 100,
                    (height - 1) * a / 100);
            g.drawLine((width - 1) * a / 100, (height - 1) * a / 100,
                    (width - 1) * a / 100, a / 100);
        } else if (border.equals("BOX")) {
            g.setColor(Color.black);
            SmartLine line = new SmartLine(g);
            line.setType(0);
            if (parent instanceof ReportForm) {
                line.isPrint = ((ReportForm) parent).isPrint;
            }
            line.draw(0, 0, width, a);
            line.draw(0, height, width, a);
            line.setType(1);
            line.draw(0, 0, height + 1, a);
            line.draw(width, 0, height + 1, a);
        }
        g.setFont(font);

        if (getText() != null) {
            g.setColor(label.getForeground());
            if (multiLine) { // нужно распарсить строки и сделать выравнивание
                String svalue1;
                if (wordWrap) {
                    svalue1 = UTIL.makeWrap(getText(), " ", label.getBounds().width - dw
                            - 3, fm);
                } else {
                    svalue1 = getText();
                }
                StringTokenizer st = new StringTokenizer(svalue1, "\n", true);
                int cnt = st.countTokens(); // кол-во строк
                String[] tok = new String[cnt];
                boolean ptisnl = false;
                int curind = 0;
                for (int i = 0; i < cnt; i++) {
                    String next = st.nextToken();
                    if (!next.equals("\n") && ptisnl) {
                        ptisnl = false;
                        tok[curind - 1] = next;
                    } else {
                        if (next.equals("\n")) {
                            ptisnl = true;
                        }
                        tok[curind] = next;
                        curind++;
                    }
                }
                cnt = curind;
                int y1 = UTIL.getOutPoint(width, height, fm, halignment,
                        valignment, dw, dh, 0, 0, "A")[1];
                if (valignment.equals("TOP")) {
                }
                if (valignment.equals("CENTER")) {
                    if (cnt % 2 == 0) {
                        y1 -= (fm.getHeight() * (cnt / 2) - (fm.getHeight() / 2));
                    } else {
                        y1 -= (fm.getHeight() * (cnt / 2));
                    }
                }
                if (valignment.equals("BOTTOM")) {
                    y1 -= (fm.getHeight() * (cnt / 2));
                }
                for (int i = 0; i < curind; i++) {
                    String next = tok[i];
                    if (next.equals("\n")) {
                        next = "";
                    }
                    int[] xy = UTIL.getOutPoint(width, height, fm, halignment,
                            valignment, dw, dh, 0, 0, next);
                    g.setClip(0, 0, (getSize().width - dw) * a / 100,
                            (getSize().height - dh) * a / 100);
                    g.drawString(next, xy[0] * a / 100, (y1 + i
                            * fm.getHeight())
                            * a / 100);
                }
            } else {
                int[] xy = UTIL.getOutPoint(width, height, fm, halignment,
                        valignment, dw, dh, 0, 0, getText());
                g.setClip(0, 0, (getSize().width - dw) * a / 100,
                        (getSize().height - dh) * a / 100);
                g.drawString(getText(), xy[0] * a / 100, xy[1] * a / 100);
            }
        }
        g.translate(-dx * a / 100, -dy * a / 100);
    }

    public Object method(String method, Object arg) throws Exception {
        if (method.equals("SETVALUE") && (arg instanceof String)) {
            setText((String) arg);
        } else if (method.equals("GETVALUE")) {
            return getText();
        } else {
            return super.method(method, arg);
        }

        return null;
    }

    /**
     * Возвращает текущий текст надписи
     *
     * @return текст
     */
    public String getText() {
        return svalue;
    }

    /**
     * Задает текст надписи. Сам элемент при этом становится видимым
     *
     * @param text - текст надписи
     */
    public void setText(String text) {
        svalue = text;
        label.setText(getText());
        label.setVisible(true);
        label.repaint();
    }

    public String type() {
        return "VIEWS_LABEL";
    }

    // Методы интерфейса GlobalValuesObject
    public void setValue(Object o) {
    }

    public void setValueByName(String name, Object o) {
    }

    public Object getValue() {
        return this;
    }

    public Object getValueByName(String name) {
        return null;
    }

    public void setParent(Object _parent) {
        parent = _parent;
        if (bg_color == null && _parent instanceof views.ReportForm) {
            label.setBackground(Color.WHITE);
        }
    }

    @Override
    public void focusThis() {
        label.requestFocus();
    }

    @Override
    public ZComponent getVisualComponent() {
        return label;
    }

    @Override
    protected Border getDefaultBorder() {
        return new EmptyBorder(0, 0, 0, 0);
    }
}
