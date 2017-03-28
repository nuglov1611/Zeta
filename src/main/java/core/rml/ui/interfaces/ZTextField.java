package core.rml.ui.interfaces;

import core.rml.ui.RequiresEDT;

public interface ZTextField extends ZTextComponent {

	@RequiresEDT
	void setHorizontalAlignment(int left);

}
