package core.rml.ui.interfaces;

import java.awt.Component;

import core.rml.ui.RequiresEDT;
import core.rml.ui.RequiresEDTPolicy;

public interface ZSplitPane extends ZComponent {
	
	@RequiresEDT(RequiresEDTPolicy.SYNC)
	void setLeftComponent(Component comp);

	@RequiresEDT(RequiresEDTPolicy.SYNC)
	void setRightComponent(Component comp);
	
	@RequiresEDT
	void setDividerLocation(int percent);

	@RequiresEDT
	void setDividerLocation(double percent);
	
	void setOneTouchExpandable(boolean b);

	@RequiresEDT
	void setOrientation(int horizontalSplit);
	
	

}
