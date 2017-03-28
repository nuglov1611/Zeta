package core.rml.ui.impl;

import core.rml.ui.EDTInvocationHandler;
import core.rml.ui.ZComponentImpl;
import core.rml.ui.interfaces.ZTextField;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.text.Keymap;

public class ZTextFieldImpl extends ZComponentImpl implements ZTextField {


    private static final Logger log = Logger.getLogger(ZTextFieldImpl.class);

    public static ZTextField create() {
        try {
            return (ZTextField) java.lang.reflect.Proxy.newProxyInstance(ZTextFieldImpl.class.getClassLoader(),
                    new Class[]{ZTextField.class}, new EDTInvocationHandler(new ZTextFieldImpl(new JTextField())));

        } catch (SecurityException e) {
            log.error("!", e);
        }
        return null;
    }


    protected ZTextFieldImpl(JComponent comp) {
        super(comp);
    }


    @Override
    public Action[] getActions() {
        return ((JTextField) jcomponent).getActions();
    }


    @Override
    public Keymap getKeymap() {
        return ((JTextField) jcomponent).getKeymap();
    }


    @Override
    public String getText() {
        return ((JTextField) jcomponent).getText();
    }


    @Override
    public void selectAll() {
        ((JTextField) jcomponent).selectAll();
    }


    @Override
    public void setEditable(boolean b) {
        ((JTextField) jcomponent).setEditable(b);
    }


    @Override
    public void setText(String text) {
        ((JTextField) jcomponent).setText(text);
    }


    @Override
    public void setHorizontalAlignment(int alignment) {
        ((JTextField) jcomponent).setHorizontalAlignment(alignment);
    }
}
