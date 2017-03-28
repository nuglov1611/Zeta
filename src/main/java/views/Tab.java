package views;

import core.document.Document;
import core.parser.Proper;
import core.rml.Container;
import core.rml.*;
import core.rml.ui.impl.ZPanelImpl;
import core.rml.ui.interfaces.ZComponent;
import core.rml.ui.interfaces.ZPanel;
import publicapi.TabAPI;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

/**
 * Закладка в TabbedPane
 *
 * @author user1
 */

public class Tab extends VisualRmlObject implements TabAPI {

    private ZPanel tab = ZPanelImpl.create();
    private String label = "";
    private String seletAction = null;
    private Container container = new Container(this);
    private String icon = null;

    public String getIcon() {
        return icon;
    }

    public void init(Proper prop, Document doc) {
        super.init(prop, doc);

        label = (String) prop.get("LABEL", "");
        seletAction = (String) prop.get("SELEXP");

        LayoutMng.setLayout(tab, prop, null);

        icon = (String) prop.get(RmlConstants.ICON);
    }

    public void Select() {
        if (seletAction == null)
            return;

        try {
            document.executeScript(seletAction, true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void focusThis() {
    }

    public void addChild(RmlObject child) {
        container.addChildToCollection(child);

        if (child instanceof VisualRmlObject) {
            if (tab.getLayout() instanceof BorderLayout) {
                final String cnstr = ((VisualRmlObject) child).getPositionForBorder();
                tab.add(cnstr, ((VisualRmlObject) child).getVisualComponent());
            } else {
                tab.add(((VisualRmlObject) child).getVisualComponent());
            }
        }
    }

    public String getLabel() {
        return label;
    }

    public ZComponent getVisualComponent() {
        return tab;
    }

    @Override
    protected Border getDefaultBorder() {
        return new EmptyBorder(0, 0, 0, 0);
    }

    @Override
    public boolean addChildrenAutomaticly() {
        return true;
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
    public void initChildren() throws Exception {
    }

    @Override
    public void fromDS() {
        container.fromDSAll();
    }

    @Override
    public int retrieve() throws Exception {
        container.retrieveAll();
        return 0;
    }

    @Override
    public void toDS() {
        container.toDSAll();
    }

    @Override
    public void update() throws
            SQLException {
        container.updateAll();
    }
}
