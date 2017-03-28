package core.rml.ui.interfaces;

import java.awt.GridBagConstraints;

import javax.swing.JComponent;
import javax.swing.JPanel;

import core.rml.ui.RequiresEDT;
import core.rml.ui.RequiresEDTPolicy;

public interface ZPanel extends ZComponent {

	@RequiresEDT(RequiresEDTPolicy.SYNC)
	void add(JPanel tb, GridBagConstraints c);

	@RequiresEDT(RequiresEDTPolicy.SYNC)
	void add(JComponent component);


}
