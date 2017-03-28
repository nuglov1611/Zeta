package core.rml.ui.interfaces;

import javax.swing.Action;
import javax.swing.text.Keymap;

import core.rml.ui.RequiresEDT;
import core.rml.ui.RequiresEDTPolicy;

public interface ZTextComponent extends ZComponent {

	@RequiresEDT(RequiresEDTPolicy.SYNC)
	void setText(String svalue);

	@RequiresEDT(RequiresEDTPolicy.SYNC)
	void selectAll();

	String getText();

	Keymap getKeymap();

	Action[] getActions();

	void setEditable(boolean b);
}
