package core.rml.ui.impl;

import core.rml.ui.EDTInvocationHandler;
import core.rml.ui.interfaces.ZPasswordField;
import org.apache.log4j.Logger;

import javax.swing.*;

public class ZPasswordFieldImpl extends ZTextFieldImpl implements ZPasswordField {


    private static final Logger log = Logger.getLogger(ZPasswordFieldImpl.class);

    public static ZPasswordField create() {
        try {
            return (ZPasswordField) java.lang.reflect.Proxy.newProxyInstance(ZPasswordFieldImpl.class.getClassLoader(),
                    new Class[]{ZPasswordField.class}, new EDTInvocationHandler(new ZPasswordFieldImpl(new JPasswordField())));

        } catch (SecurityException e) {
            log.error("!", e);
        }
        return null;
    }


    protected ZPasswordFieldImpl(JComponent comp) {
        super(comp);
    }

}
