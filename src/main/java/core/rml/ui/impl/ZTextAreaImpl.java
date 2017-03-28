package core.rml.ui.impl;

import core.rml.ui.EDTInvocationHandler;
import core.rml.ui.ZComponentImpl;
import core.rml.ui.interfaces.ZTextArea;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.Keymap;

public class ZTextAreaImpl extends ZComponentImpl implements ZTextArea {


    private static final Logger log = Logger.getLogger(ZTextAreaImpl.class);

    public static ZTextArea create() {
        try {
            return (ZTextArea) java.lang.reflect.Proxy.newProxyInstance(ZTextAreaImpl.class.getClassLoader(),
                    new Class[]{ZTextArea.class}, new EDTInvocationHandler(new ZTextAreaImpl(new JTextArea())));

        } catch (SecurityException e) {
            log.error("!", e);
        }
        return null;
    }


    protected ZTextAreaImpl(JComponent comp) {
        super(comp);
    }


    @Override
    public void append(String text) {
        ((JTextArea) jcomponent).append(text);
    }


    @Override
    public void setCaretPosition(int position) {
        ((JTextArea) jcomponent).setCaretPosition(position);
    }


    @Override
    public Action[] getActions() {
        return ((JTextArea) jcomponent).getActions();
    }


    @Override
    public Keymap getKeymap() {
        return ((JTextArea) jcomponent).getKeymap();
    }


    @Override
    public String getText() {
        return ((JTextArea) jcomponent).getText();
    }


    @Override
    public void selectAll() {
        ((JTextArea) jcomponent).selectAll();
    }


    @Override
    public void setEditable(boolean b) {
        ((JTextArea) jcomponent).setEditable(b);
    }


    @Override
    public void setText(String text) {
        ((JTextArea) jcomponent).setText(text);
    }


    @Override
    public Document getDocument() {
        return ((JTextArea) jcomponent).getDocument();
    }


    @Override
    public void setLineWrap(boolean b) {
        ((JTextArea) jcomponent).setLineWrap(b);
    }


    @Override
    public void setWrapStyleWord(boolean b) {
        ((JTextArea) jcomponent).setWrapStyleWord(b);
    }

}
