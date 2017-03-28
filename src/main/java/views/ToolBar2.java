package views;

import core.document.Document;
import core.parser.Proper;
import core.rml.Container;
import core.rml.RmlObject;
import core.rml.VisualRmlObject;
import core.rml.ui.impl.ZPanelImpl;
import core.rml.ui.interfaces.ZComponent;
import core.rml.ui.interfaces.ZPanel;
import publicapi.ToolBar2API;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.SQLException;

/**
 * Еще одна Панель с кнопками
 */

public class ToolBar2 extends VisualRmlObject implements FocusListener, ToolBar2API {

    private ZPanel panel = ZPanelImpl.create();

    private Container container = new Container(this);

    public ToolBar2() {
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 30));
        panel.addFocusListener(this);
    }


    public void init(Proper p, Document doc) {
        super.init(p, doc);

        String s = (String) p.get("ALIGN");
        Integer i;

        if (s != null)
            if (s.equals("LEFT"))
                ((FlowLayout) panel.getLayout()).setAlignment(FlowLayout.LEFT);
            else if (s.equals("RIGHT"))
                ((FlowLayout) panel.getLayout()).setAlignment(FlowLayout.RIGHT);
            else if (s.equals("CENTER"))
                ((FlowLayout) panel.getLayout()).setAlignment(FlowLayout.CENTER);
        i = (Integer) p.get("VGAP");
        if (i != null)
            ((FlowLayout) panel.getLayout()).setVgap(i.intValue());
        i = (Integer) p.get("HGAP");
        if (i != null)
            ((FlowLayout) panel.getLayout()).setHgap(i.intValue());
    }

    public int retrieve() throws Exception {
        container.retrieveAll();
        return 0;
    }

    public void toDS() {
        container.toDSAll();
    }

    public void fromDS() {
        container.fromDSAll();
    }

    public void update() throws SQLException {
        container.updateAll();
    }

    @Override
    public void focusGained(FocusEvent e) {
        try {
            Component c = panel.getJComponent().getComponent(0);
            c.requestFocus();
        } catch (IndexOutOfBoundsException ex) {
            ex.printStackTrace();
            panel.getParent().requestFocus();
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
    }

    @Override
    public void addChild(RmlObject child) {
        container.addChildToCollection(child);

        if (child instanceof VisualRmlObject) {
            panel.add(((VisualRmlObject) child).getVisualComponent());
        }
    }

    @Override
    public void focusThis() {
        Component c = panel.getJComponent().getComponent(0);
        c.requestFocus();
    }

    @Override
    public ZComponent getVisualComponent() {
        return panel;
    }


    @Override
    public RmlObject[] getChildren() {
        return container.getChildren();
    }


    @Override
    public void initChildren() {
    }


    @Override
    public Container getContainer() {
        return container;
    }


    @Override
    public boolean addChildrenAutomaticly() {
        return true;
    }


    @Override
    protected Border getDefaultBorder() {
        return new EmptyBorder(0, 0, 0, 0);
    }

}
