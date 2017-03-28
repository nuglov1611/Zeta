package core.rml.ui.impl;

import core.rml.ui.EDTInvocationHandler;
import core.rml.ui.ZComponentImpl;
import core.rml.ui.interfaces.ZLabel;
import org.apache.log4j.Logger;

import javax.swing.*;

public class ZLabelImpl extends ZComponentImpl implements ZLabel {

    private static final Logger log = Logger.getLogger(ZLabelImpl.class);

    public static ZLabel create() {
        try {
            return (ZLabel) java.lang.reflect.Proxy.newProxyInstance(ZLabelImpl.class.getClassLoader(),
                    new Class[]{ZLabel.class}, new EDTInvocationHandler(new ZLabelImpl(new JLabel())));

        } catch (SecurityException e) {
            log.error("!", e);
        }
        return null;
    }

    public static ZLabel create(String text) {
        try {
            return (ZLabel) java.lang.reflect.Proxy.newProxyInstance(ZLabelImpl.class.getClassLoader(),
                    new Class[]{ZLabel.class}, new EDTInvocationHandler(new ZLabelImpl(new JLabel(text))));

        } catch (SecurityException e) {
            log.error("!", e);
        }
        return null;
    }

    protected ZLabelImpl(JComponent comp) {
        super(comp);
    }


    @Override
    public void setText(String text) {
        ((JLabel) jcomponent).setText(text);
    }


}
