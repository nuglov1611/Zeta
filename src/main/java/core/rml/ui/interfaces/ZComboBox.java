package core.rml.ui.interfaces;

import core.rml.ui.RequiresEDT;
import core.rml.ui.RequiresEDTPolicy;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;


public interface ZComboBox extends ZComponent {

    int getSelectedIndex();

    void addItemListener(ItemListener listener);

    Object getSelectedItem();

    Object getItemAt(int selectedIndex);

    void addActionListener(ActionListener al);

    ComboBoxModel getModel();

    int getItemCount();

    @RequiresEDT(RequiresEDTPolicy.SYNC)
    void addItem(Object child);

    @RequiresEDT(RequiresEDTPolicy.SYNC)
    void removeAllItems();

    @RequiresEDT(RequiresEDTPolicy.SYNC)
    void setSelectedIndex(int intValue);

    @RequiresEDT(RequiresEDTPolicy.SYNC)
    void setSelectedItem(Object cur_itm);


}
