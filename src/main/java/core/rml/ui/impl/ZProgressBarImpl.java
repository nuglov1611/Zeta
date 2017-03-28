package core.rml.ui.impl;

import javax.swing.JComponent;
import javax.swing.JProgressBar;

import org.apache.log4j.Logger;

import core.rml.ui.EDTInvocationHandler;
import core.rml.ui.ZComponentImpl;
import core.rml.ui.interfaces.ZProgressBar;

public class ZProgressBarImpl extends ZComponentImpl implements ZProgressBar {

	private static final Logger log = Logger.getLogger(ZProgressBarImpl.class);

	public static ZProgressBar create(){
		try {
			return (ZProgressBar) java.lang.reflect.Proxy.newProxyInstance(ZProgressBarImpl.class.getClassLoader(),
	                new Class[]{ZProgressBar.class}, new EDTInvocationHandler(new ZProgressBarImpl(new JProgressBar())));
			
		} catch (SecurityException e) {
			log.error("!", e);
		} 
		return null;
	}

	protected ZProgressBarImpl(JComponent comp) {
		super(comp);
	}

	@Override
	public void setValue(int p) {
		((JProgressBar) jcomponent).setValue(p);
	}
}
