package core.rml.ui.interfaces;

import core.rml.ui.RequiresEDT;
import core.rml.ui.RequiresEDTPolicy;

import javax.swing.text.Document;

public interface ZTextArea extends ZTextComponent {

    Document getDocument();

    @RequiresEDT(RequiresEDTPolicy.SYNC)
    void append(String text);

    @RequiresEDT(RequiresEDTPolicy.SYNC)
    void setCaretPosition(int position);

    @RequiresEDT
    void setLineWrap(boolean b);

    @RequiresEDT
    void setWrapStyleWord(boolean b);
}
