package core.rml.ui.impl;

import java.awt.GridBagConstraints;
import java.awt.LayoutManager;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import core.rml.ui.EDTInvocationHandler;
import core.rml.ui.ZComponentImpl;
import core.rml.ui.interfaces.ZPanel;

public class ZPanelImpl extends ZComponentImpl implements ZPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(ZPanelImpl.class);

	public static ZPanel create(){
		return create(new JPanel());
	}


	public static ZPanel create(JPanel panel){
		try {
			return (ZPanel) java.lang.reflect.Proxy.newProxyInstance(ZPanelImpl.class.getClassLoader(),
	                new Class[]{ZPanel.class}, new EDTInvocationHandler(new ZPanelImpl(panel)));
			
		} catch (SecurityException e) {
			log.error("!", e);
		} 
		return null;
	}
	
	
	public static ZPanel create(ZPanelImpl panel){
		try {
			return (ZPanel) java.lang.reflect.Proxy.newProxyInstance(ZPanelImpl.class.getClassLoader(),
	                new Class[]{ZPanel.class}, new EDTInvocationHandler(panel));
			
		} catch (SecurityException e) {
			log.error("!", e);
		} 
		return null;
	}
	
	
	public static ZPanel create(LayoutManager layout) {
		return create(new JPanel(layout));
	}

	
	
	protected ZPanelImpl(JComponent comp){
		super(comp);
	}


	//TODO Может быть этот метод утащить в ZComponent?
	public void add(JPanel tb, GridBagConstraints c) {
		((JPanel)jcomponent).add(tb, c);
	}

	//TODO Может быть этот метод утащить в ZComponent?
	public void add(JComponent component) {
		((JPanel)jcomponent).add(component);
	}

}
