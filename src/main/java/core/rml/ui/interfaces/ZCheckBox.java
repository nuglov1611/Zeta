package core.rml.ui.interfaces;

import core.rml.ui.RequiresEDT;

public interface ZCheckBox extends ZButton {

    @RequiresEDT
    void setSelected(boolean selected);

    boolean isSelected();

}
