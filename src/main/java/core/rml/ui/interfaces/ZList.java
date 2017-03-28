package core.rml.ui.interfaces;

import core.rml.ui.RequiresEDT;
import core.rml.ui.RequiresEDTPolicy;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.util.Vector;

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
    void setSelectedValue(Object val);

}
