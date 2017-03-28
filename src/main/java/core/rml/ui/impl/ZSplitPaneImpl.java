package core.rml.ui.impl;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

import org.apache.log4j.Logger;

import core.rml.ui.EDTInvocationHandler;
import core.rml.ui.ZComponentImpl;
import core.rml.ui.interfaces.ZSplitPane;

public class ZSplitPaneImpl extends ZComponentImpl implements ZSplitPane {

	private static final Logger log = Logger.getLogger(ZSplitPaneImpl.class);

	public static ZSplitPane create(int split){
		try {
			return (ZSplitPane) java.lang.reflect.Proxy.newProxyInstance(ZSplitPaneImpl.class.getClassLoader(),
	                new Class[]{ZSplitPane.class}, new EDTInvocationHandler(new ZSplitPaneImpl(new JSplitPane(split))));
			
		} catch (SecurityException e) {
			log.error("!", e);
		} 
		return null;
	}
	
	
	
	protected ZSplitPaneImpl(JComponent comp){
		super(comp);
	}



	@Override
	public void setDividerLocation(int percent) {
		((JSplitPane) jcomponent).setDividerLocation(percent);
	}



	@Override
	public void setDividerLocation(double percent) {
		((JSplitPane) jcomponent).setDividerLocation(percent);
	}



	@Override
	public void setLeftComponent(Component comp) {
		((JSplitPane) jcomponent).setLeftComponent(comp);		
	}



	@Override
	public void setOneTouchExpandable(boolean b) {
		((JSplitPane) jcomponent).setOneTouchExpandable(b);		
	}



	@Override
	public void setOrientation(int orientation) {
		((JSplitPane) jcomponent).setOrientation(orientation);		
	}



	@Override
	public void setRightComponent(Component comp) {
		((JSplitPane) jcomponent).setRightComponent(comp);		
	}
}
