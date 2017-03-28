package core.rml.ui.interfaces;

import java.awt.Component;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.SingleSelectionModel;
import javax.swing.event.ChangeListener;

import core.rml.ui.RequiresEDT;
import core.rml.ui.RequiresEDTPolicy;

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
