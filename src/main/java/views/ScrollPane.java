package views;

import core.document.Document;
import core.parser.Proper;
import core.rml.Container;
import core.rml.RmlObject;
import core.rml.VisualRmlObject;
import core.rml.ui.impl.ZScrollPaneImpl;
import core.rml.ui.interfaces.ZComponent;
import core.rml.ui.interfaces.ZScrollPane;
import publicapi.ScrollPaneAPI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

/**
 *
 */
public class ScrollPane extends VisualRmlObject implements ScrollPaneAPI {

    private ZScrollPane scrollPane = ZScrollPaneImpl.create();

    private Container container = new Container(this);

    public int bg_color = Color.lightGray.getRGB();

    public ScrollPane() {
        scrollPane.setWheelScrollingEnabled(true);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(5);
        scrollPane.getVerticalScrollBar().setUnitIncrement(5);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setViewPosition(new Point(0, 0));
        scrollPane.getViewport().setSize(new Dimension(50, 50));
        scrollPane.getViewport().setMinimumSize(new Dimension(50, 50));
        scrollPane.getViewport().setViewSize(new Dimension(50, 50));
        // getViewport().setPreferredSize(new Dimension(100,100));
        scrollPane.getViewport().setExtentSize(new Dimension(100, 100));
    }

    public void init(Proper prop, Document doc) {
        super.init(prop, doc);
    }

    public int retrieve() throws Exception {
        container.retrieveAll();
        return 0;
    }


    public void scrollInView(Rectangle rect) {
        scrollPane.getViewport().setViewPosition(
                new Point((scrollPane.getViewport().getViewPosition().x + rect.x),
                        (scrollPane.getViewport().getViewPosition().y + rect.y)));
    }


    public void fromDS() {
        container.fromDSAll();
    }

    @Override
    public void toDS() {
        container.toDSAll();
    }

    public void update() throws SQLException {
        container.updateAll();
    }

    @Override
    public void focusThis() {
        // TODO Auto-generated method stub

    }

    @Override
    public ZComponent getVisualComponent() {
        return scrollPane;
    }

    @Override
    public void addChild(RmlObject child) {
        container.addChildToCollection(child);

        if (child instanceof VisualRmlObject) {
            scrollPane.getViewport().setView(((VisualRmlObject) child).getVisualComponent().getJComponent());
        }
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
