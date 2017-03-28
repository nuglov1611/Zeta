package views;

import core.document.Document;
import core.parser.Proper;
import core.rml.Container;
import core.rml.RmlObject;
import core.rml.VisualRmlObject;
import core.rml.ui.impl.ZSplitPaneImpl;
import core.rml.ui.interfaces.ZComponent;
import core.rml.ui.interfaces.ZSplitPane;
import org.apache.log4j.Logger;
import publicapi.SplitPaneAPI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicBorders;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.SQLException;

/**
 * Панель с разделителем
 */
public class SplitPanel extends VisualRmlObject implements SplitPaneAPI {

    private ZSplitPane panel = ZSplitPaneImpl.create(JSplitPane.VERTICAL_SPLIT);

    class CompL extends ComponentAdapter {
        public void componentResized(ComponentEvent e) {
            panel.setDividerLocation(percent);
            panel.revalidate();
            panel.repaint();
        }
    }

    /**
     *
     */
    private static final Logger log = Logger
            .getLogger(SplitPanel.class);

    private Container container = new Container(this);

    public final static int HORIZONTAL = 1;

    public final static int VERTICAL = 2;

    private double percent = 0.5;

    public SplitPanel() {
        panel.setOneTouchExpandable(true);
        panel.getJComponent().addComponentListener(new CompL());
        panel.setMinimumSize(new Dimension(0, 0));
    }

    public void initChildren() {
        int place = 0;
        final RmlObject[] objs = container.getChildren();
        for (RmlObject child : objs) {
            if (child instanceof VisualRmlObject) {
                if (place == 0) {
                    panel.setLeftComponent(((VisualRmlObject) objs[0]).getVisualComponent().getJComponent());
                    place = 1;
                } else if (place == 1) {
                    panel.setRightComponent(((VisualRmlObject) objs[1]).getVisualComponent().getJComponent());
                    place = 2;
                } else {
                    place++;
                }
            }

        }
        if (place != 2)
            log.warn("Bad number of visual objects in SplitPanel: " + place + "!");
        panel.validate();
    }

    public void init(Proper prop, Document doc) {
        super.init(prop, doc);
        if (prop != null) {
            String sp = (String) prop.get("TYPE");
            if (sp != null) {
                if (sp.equals("VERTICAL")) {
                    panel.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
                }
                if (sp.equals("HORIZONTAL")) {
                    panel.setOrientation(JSplitPane.VERTICAL_SPLIT);
                }
            }

            Integer ip = (Integer) prop.get("PERCENT");
            if (ip != null) {
                double per = ip.doubleValue() / 100;
                if (per < 0) {
                    per = 0.001;
                }
                if (per > 1) {
                    per = 0.99;
                }
                percent = per;
                panel.setDividerLocation(100);

            }
        } else {
            log.warn("prop=null!");
        }
        try {
            container.addChildren(prop, doc);
        } catch (Exception e) {
            log.error("!", e);
        }
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
    public void addChild(RmlObject child) {
        container.addChildToCollection(child);
    }

    @Override
    public void focusThis() {
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
    public Container getContainer() {
        return container;
    }

    @Override
    public boolean addChildrenAutomaticly() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected Border getDefaultBorder() {
        return BasicBorders.getSplitPaneBorder();
    }
}
