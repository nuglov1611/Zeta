package core.rml.ui.impl;

import core.rml.ui.EDTInvocationHandler;
import core.rml.ui.ZComponentImpl;
import core.rml.ui.interfaces.ZList;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.util.Vector;

public class ZListImpl extends ZComponentImpl implements ZList {

    private static final Logger log = Logger.getLogger(ZListImpl.class);

    public static ZList create() {
        try {
            return (ZList) java.lang.reflect.Proxy.newProxyInstance(ZListImpl.class.getClassLoader(),
                    new Class[]{ZList.class}, new EDTInvocationHandler(new ZListImpl(new JList())));

        } catch (SecurityException e) {
            log.error("!", e);
        }
        return null;
    }


    protected ZListImpl(JComponent comp) {
        super(comp);
    }


    @Override
    public void addListSelectionListener(ListSelectionListener listener) {
        ((JList) jcomponent).addListSelectionListener(listener);
    }


    @Override
    public ListModel getModel() {
        return ((JList) jcomponent).getModel();
    }


    @Override
    public int getSelectedIndex() {
        return ((JList) jcomponent).getSelectedIndex();
    }


    @Override
    public void setListData(Vector<?> listData) {
        ((JList) jcomponent).setListData(listData);
    }


    @Override
    public void setSelectedIndex(int index) {
        ((JList) jcomponent).setSelectedIndex(index);
    }


    @Override
    public Object getSelectedValue() {
        return ((JList) jcomponent).getSelectedValue();
    }

    @Override
    public void setSelectedValue(Object val) {
        ((JList) jcomponent).setSelectedValue(val, true);
    }
}
