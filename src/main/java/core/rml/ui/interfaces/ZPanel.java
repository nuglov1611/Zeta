package core.rml.ui.interfaces;

import core.rml.ui.RequiresEDT;
import core.rml.ui.RequiresEDTPolicy;

import javax.swing.*;
import java.awt.*;

public interface ZPanel extends ZComponent {

    @RequiresEDT(RequiresEDTPolicy.SYNC)
    void add(JPanel tb, GridBagConstraints c);

    @RequiresEDT(RequiresEDTPolicy.SYNC)
    void add(JComponent component);


}
