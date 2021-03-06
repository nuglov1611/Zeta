package core.rml.ui.impl;

import core.rml.ui.EDTInvocationHandler;
import core.rml.ui.interfaces.ZRadioButton;
import org.apache.log4j.Logger;

import javax.swing.*;

public class ZRadioButtonImpl extends ZCheckBoxImpl implements ZRadioButton {

    private static final Logger log = Logger.getLogger(ZRadioButton.class);

    public static ZRadioButton create() {
        try {
            return (ZRadioButton) java.lang.reflect.Proxy.newProxyInstance(ZRadioButtonImpl.class.getClassLoader(),
                    new Class[]{ZRadioButton.class}, new EDTInvocationHandler(new ZRadioButtonImpl(new JRadioButton())));

        } catch (SecurityException e) {
            log.error("!", e);
        }
        return null;
    }


    protected ZRadioButtonImpl(JComponent comp) {
        super(comp);
    }
}
