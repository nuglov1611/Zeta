package views;

import core.document.Document;
import core.parser.Proper;
import core.rml.VisualRmlObject;
import core.rml.ui.EDTInvocationHandler;
import core.rml.ui.ZComponentImpl;
import core.rml.ui.interfaces.ZComponent;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;


public class Line extends VisualRmlObject {

    class LineComp extends ZComponentImpl {

        protected LineComp(JComponent comp) {
            super(comp);
        }

    }

    //TODO Разобраться с этим классом
    ZComponent line = (ZComponent) java.lang.reflect.Proxy.newProxyInstance(LineComp.class.getClassLoader(),
            new Class[]{ZComponent.class}, new EDTInvocationHandler(new LineComp(new JPanel())));


    int size;

    public int LINE_HORIZONTAL = 0;

    public int LINE_VERTICAL = 1;

    int type = LINE_HORIZONTAL;

    Object parent;

    public void init(Proper prop, Document doc) {
        super.init(prop, doc);
        Integer ip;
        String sp;

        ip = (Integer) prop.get("SIZE");
        if (ip != null) {
            size = ip.intValue();
        }
        sp = (String) prop.get("TYPE");
        if (sp != null && sp.equals("HORIZONTAL")) {
            type = LINE_HORIZONTAL;
        } else {
            type = LINE_VERTICAL;
        }
    }

    public void paint(Graphics g, int a) {
        SmartLine sl = new SmartLine(g);
        sl.setType(type);
        if (parent instanceof ReportForm) {
            sl.isPrint = ((ReportForm) parent).isPrint;
        }
        sl.draw(left, top, size, a);
    }


    @Override
    public void focusThis() {
    }

    @Override
    public ZComponent getVisualComponent() {
        return line;
    }

    @Override
    protected Border getDefaultBorder() {
        return null;
    }

}
