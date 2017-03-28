package core.rml.ui.impl;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;

import org.apache.log4j.Logger;

import core.rml.ui.EDTInvocationHandler;
import core.rml.ui.interfaces.ZTextField;

public class ZFormattedTextFieldImpl extends ZTextFieldImpl {
	private static final Logger log = Logger.getLogger(ZFormattedTextFieldImpl.class);

	public static ZTextField create(MaskFormatter mask){
		try {
			return (ZTextField) java.lang.reflect.Proxy.newProxyInstance(ZFormattedTextFieldImpl.class.getClassLoader(),
	                new Class[]{ZTextField.class}, new EDTInvocationHandler(new ZFormattedTextFieldImpl(new JFormattedTextField(mask))));
			
		} catch (SecurityException e) {
			log.error("!", e);
		} 
		return null;
	}
	
	
	protected ZFormattedTextFieldImpl(JComponent comp){
		super(comp);
	}
	
}
