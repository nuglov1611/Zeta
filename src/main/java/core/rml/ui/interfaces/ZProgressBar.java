package core.rml.ui.interfaces;

import core.rml.ui.RequiresEDT;

public interface ZProgressBar extends ZComponent {

    @RequiresEDT
    void setValue(int p);

}
