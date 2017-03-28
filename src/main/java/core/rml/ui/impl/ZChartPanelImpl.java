package core.rml.ui.impl;

import javax.swing.JComponent;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import core.rml.ui.EDTInvocationHandler;
import core.rml.ui.ZComponentImpl;
import core.rml.ui.interfaces.ZComponent;

public class ZChartPanelImpl extends ZComponentImpl implements ZComponent {
	private static final Logger log = Logger.getLogger(ZChartPanelImpl.class);

	public static ZComponent create(JFreeChart chart){
		try {
			return (ZComponent) java.lang.reflect.Proxy.newProxyInstance(ZChartPanelImpl.class.getClassLoader(),
	                new Class[]{ZComponent.class}, new EDTInvocationHandler(new ZChartPanelImpl(new ChartPanel(chart))));
			
		} catch (SecurityException e) {
			log.error("!", e);
		} 
		return null;
	}
	
	
	
	protected ZChartPanelImpl(JComponent chart) {
		super(chart);
	}
}
