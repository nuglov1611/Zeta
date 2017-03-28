package core.rml.ui.interfaces;

import java.awt.event.ActionListener;

import javax.swing.Icon;

import core.rml.ui.RequiresEDT;

public interface ZButton extends ZComponent {
	
	@RequiresEDT
	public void setText(String s);

	@RequiresEDT
	public void setIcon(Icon im);

	public void addActionListener(ActionListener field);
}
