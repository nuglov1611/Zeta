package core.rml.ui.interfaces;

import core.rml.ui.RequiresEDT;

import javax.swing.*;
import java.awt.event.ActionListener;

public interface ZButton extends ZComponent {

    @RequiresEDT
    void setText(String s);

    @RequiresEDT
    void setIcon(Icon im);

    void addActionListener(ActionListener field);
}
