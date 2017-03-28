package views;

import core.document.Document;
import core.parser.Proper;
import core.rml.Container;
import core.rml.RmlConstants;
import core.rml.RmlObject;
import core.rml.VisualRmlObject;
import core.rml.ui.impl.ZPanelImpl;
import core.rml.ui.interfaces.ZComponent;
import core.rml.ui.interfaces.ZPanel;
import org.apache.log4j.Logger;
import publicapi.CheckGroupAPI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Vector;

/**
 * Группа связанных ChackBox (в группе может быть включен только один CheckBox,
 * т.е. при включении одно элемента из группы остальные элементы переходят в состояние "выключен")
 */
public class CheckGroup extends VisualRmlObject implements CheckGroupAPI {
    /**
     *
     */
    private static final Logger log = Logger.getLogger(CheckGroup.class);

    private ZPanel panel = ZPanelImpl.create();

    private Container container = new Container(this);

    private static final int GAP = 5;

    public static final int DEFAULT_FONT_SIZE = 12;

    private Vector<Object> boxes = new Vector<Object>();

    /**
     * выравнивать кнопки по горизонтали или по вертикали
     */
    private boolean horizontalAlignment = false;

//    private int               leftBound;
//
//    private int               topBound;


    private void setBoxFont(CheckBox box, int fontSize) {
        Font currentFont = box.getVisualComponent().getFont();
        box.getVisualComponent().setFont(new Font(currentFont.getName(), currentFont.getStyle(),
                fontSize));
    }

    public void init(Proper prop, Document doc) {
        super.init(prop, doc);
        String sp;

        sp = (String) prop.get(RmlConstants.ALIGNMENT);
        horizontalAlignment = sp != null && sp.equals(RmlConstants.HORIZONTAL);

        try {
            container.addChildren(prop, doc);
        } catch (Exception e) {
            log.error("!", e);
        }
    }

    public void addChild(RmlObject child) {
        container.addChildToCollection(child);
        if (child instanceof CheckBox) {
            boxes.addElement(child);
        }
    }

    @Override
    public void focusThis() {
        panel.requestFocus();
    }

    @Override
    public ZComponent getVisualComponent() {
        return panel;
    }


    @Override
    public RmlObject[] getChildren() {
        RmlObject[] ret = new RmlObject[boxes.size()];
        return boxes.toArray(ret);
    }

    @Override
    public void initChildren() {
        if (boxes.size() == 0) {
            return;
        }
        // пересчитываем размер пенели в зависимости от размеров и числа
        // компонентов
        int width = 0;
        int height = 0;

        JFrame fix = new JFrame(); // фиктивный фрейм, нужен чтобы
        // getPreferredSize
        // вернул правильное значение
        fix.setLayout(new FlowLayout());
        for (int i = 0; i < boxes.size(); i++) {
            fix.add(((CheckBox) boxes.elementAt(i)).getVisualComponent().getJComponent());
        }
        fix.pack();
        for (int i = 0; i < boxes.size(); i++) {
            CheckBox box = (CheckBox) boxes.elementAt(i);
//            int boxFontSize = box.getVisualComponent().getFont().get;
//            if (boxFontSize != CheckBox.DEFAULT_FONT_SIZE) {
//                setBoxFont(box, boxFontSize);
//            }
//            else if (fontSize != DEFAULT_FONT_SIZE) {
//                setBoxFont(box, fontSize);
//            }
            Dimension d = box.getVisualComponent().getPreferredSize();
            if (horizontalAlignment) {
                width += d.width + GAP;
                if (height < d.height) {
                    height = d.height;
                }
            } else {
                if (width < d.width) {
                    width = d.width;
                }
                height += d.height + GAP;
            }
        }
        fix.removeAll();
        fix.dispose();

        panel.setBounds(left, top, width, height);

        if (horizontalAlignment) {
            panel.setLayout(new GridLayout(1, boxes.size()));
        } else {
            panel.setLayout(new GridLayout(boxes.size(), 1));
        }

        for (int i = 0; i < boxes.size(); i++) {
            CheckBox box = (CheckBox) boxes.elementAt(i);
            panel.add(box.getVisualComponent());
        }

    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public boolean addChildrenAutomaticly() {
        return false;
    }

    @Override
    protected Border getDefaultBorder() {
        return new EmptyBorder(0, 0, 0, 0);
    }
}
