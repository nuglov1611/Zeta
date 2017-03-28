package core.rml.ui.interfaces;

import core.rml.ui.RequiresEDT;
import core.rml.ui.RequiresEDTPolicy;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.beans.PropertyChangeListener;

public interface ZTabbedPane extends ZComponent {

    int getSelectedIndex();

    void addPropertyChangeListener(PropertyChangeListener pcl);

    int getTabCount();

    SingleSelectionModel getModel();

    void addChangeListener(ChangeListener tsl);

    @RequiresEDT
    void setTitleAt(int tab, String label);

    @RequiresEDT(RequiresEDTPolicy.SYNC)
    void addTab(String string, Component jComponent);

    @RequiresEDT(RequiresEDTPolicy.SYNC)
    void setTabPlacement(int bottom);

    @RequiresEDT(RequiresEDTPolicy.SYNC)
    void setTabLayoutPolicy(int scrollTabLayout);

    @RequiresEDT(RequiresEDTPolicy.SYNC)
    void setSelectedIndex(int intex);

    @RequiresEDT(RequiresEDTPolicy.SYNC)
    void addTab(String title, Icon icon, Component component);

}
