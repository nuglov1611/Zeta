package core.rml.ui.impl;

import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;

import org.apache.log4j.Logger;

import core.rml.ui.EDTInvocationHandler;
import core.rml.ui.ZComponentImpl;
import core.rml.ui.interfaces.ZButton;

public class ZButtonImpl extends ZComponentImpl implements ZButton {


	private static final Logger log = Logger.getLogger(ZButtonImpl.class);

	public static ZButton create(String label) {
		try {
			return (ZButton) java.lang.reflect.Proxy.newProxyInstance(ZButtonImpl.class.getClassLoader(),
	                new Class[]{ZButton.class}, new EDTInvocationHandler(new ZButtonImpl(new JButton(label))));
			
		} catch (SecurityException e) {
			log.error("!", e);
		} 
		return null;
	}

	
	public static ZButton create(){
		try {
			return (ZButton) java.lang.reflect.Proxy.newProxyInstance(ZButtonImpl.class.getClassLoader(),
	                new Class[]{ZButton.class}, new EDTInvocationHandler(new ZButtonImpl(new JButton())));
			
		} catch (SecurityException e) {
			log.error("!", e);
		} 
		return null;
	}
	
	
	
	protected ZButtonImpl(JComponent comp){
		super(comp);
	}

	@Override
	public void addActionListener(ActionListener listener) {
		((AbstractButton)jcomponent).addActionListener(listener);
	}


	@Override
	public void setIcon(Icon im) {
		((AbstractButton)jcomponent).setIcon(im);
	}


	@Override
	public void setText(String text) {
		((AbstractButton)jcomponent).setText(text);
	}



}
