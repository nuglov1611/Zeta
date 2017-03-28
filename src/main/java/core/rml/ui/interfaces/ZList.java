package core.rml.ui.interfaces;

import java.util.Vector;

import javax.swing.ListModel;
import javax.swing.event.ListSelectionListener;

import core.rml.ui.RequiresEDT;
import core.rml.ui.RequiresEDTPolicy;

public interface ZList extends ZComponent {

	ListModel getModel();

	int getSelectedIndex();
	
	Object getSelectedValue();

	void addListSelectionListener(ListSelectionListener sl);

	@RequiresEDT(RequiresEDTPolicy.SYNC)
	void setListData(Vector<?> listData);
	
	@RequiresEDT(RequiresEDTPolicy.SYNC)
	void setSelectedIndex(int intValue);

    @RequiresEDT(RequiresEDTPolicy.SYNC)
    public void setSelectedValue(Object val);

}
