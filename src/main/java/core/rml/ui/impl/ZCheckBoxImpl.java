package core.rml.ui.impl;

import core.rml.ui.EDTInvocationHandler;
import core.rml.ui.interfaces.ZCheckBox;
import org.apache.log4j.Logger;

import javax.swing.*;

public class ZCheckBoxImpl extends ZButtonImpl implements ZCheckBox {

    private static final Logger log = Logger.getLogger(ZCheckBoxImpl.class);

    public static ZCheckBox create() {
        try {
            return (ZCheckBox) java.lang.reflect.Proxy.newProxyInstance(ZCheckBoxImpl.class.getClassLoader(),
                    new Class[]{ZCheckBox.class}, new EDTInvocationHandler(new ZCheckBoxImpl(new JCheckBox())));

        } catch (SecurityException e) {
            log.error("!", e);
        }
        return null;
    }


    protected ZCheckBoxImpl(JComponent comp) {
        super(comp);
    }


    @Override
    public boolean isSelected() {
        return ((AbstractButton) jcomponent).isSelected();
    }


    @Override
    public void setSelected(boolean selected) {
        ((AbstractButton) jcomponent).setSelected(selected);
    }
}
