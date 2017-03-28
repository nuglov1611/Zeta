package core.rml.ui.interfaces;

import core.rml.ui.RequiresEDT;

public interface ZLabel extends ZComponent {
    @RequiresEDT
    void setText(String s);


}
