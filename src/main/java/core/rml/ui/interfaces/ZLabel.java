package core.rml.ui.interfaces;

import core.rml.ui.RequiresEDT;

public interface ZLabel extends ZComponent {
	@RequiresEDT
	public void setText(String s);



}
