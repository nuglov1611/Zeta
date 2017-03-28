package core.rml.ui.impl;

import core.rml.ui.EDTInvocationHandler;
import core.rml.ui.ZComponentImpl;
import core.rml.ui.interfaces.ZTabbedPane;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.beans.PropertyChangeListener;

public class ZTabbedPaneImpl extends ZComponentImpl implements ZTabbedPane {

    private static final Logger log = Logger.getLogger(ZTabbedPaneImpl.class);

    public static ZTabbedPane create() {
        try {
            return (ZTabbedPane) java.lang.reflect.Proxy.newProxyInstance(ZTabbedPaneImpl.class.getClassLoader(),
                    new Class[]{ZTabbedPane.class}, new EDTInvocationHandler(new ZTabbedPaneImpl(new JTabbedPane())));

        } catch (SecurityException e) {
            log.error("!", e);
        }
        return null;
    }

    public static ZTabbedPane create(int bottom, int scrollTabLayout) {
        try {
            JTabbedPane pane = new JTabbedPane(bottom, scrollTabLayout);
            return (ZTabbedPane) java.lang.reflect.Proxy.newProxyInstance(ZTabbedPaneImpl.class.getClassLoader(),
                    new Class[]{ZTabbedPane.class}, new EDTInvocationHandler(new ZTabbedPaneImpl(pane)));

        } catch (SecurityException e) {
            log.error("!", e);
        }
        return null;
    }

    protected ZTabbedPaneImpl(JComponent comp) {
        super(comp);
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
        ((JTabbedPane) jcomponent).addChangeListener(listener);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        jcomponent.addPropertyChangeListener(listener);
    }

    @Override
    public void addTab(String title, Icon icon, Component component) {
        ((JTabbedPane) jcomponent).addTab(title, icon, component);
    }

    @Override
    public void addTab(String title, Component component) {
        ((JTabbedPane) jcomponent).addTab(title, component);
    }

    @Override
    public SingleSelectionModel getModel() {
        return ((JTabbedPane) jcomponent).getModel();
    }

    @Override
    public int getSelectedIndex() {
        return ((JTabbedPane) jcomponent).getSelectedIndex();
    }

    @Override
    public int getTabCount() {
        return ((JTabbedPane) jcomponent).getTabCount();
    }

    @Override
    public void setSelectedIndex(int index) {
        ((JTabbedPane) jcomponent).setSelectedIndex(index);
    }

    @Override
    public void setTabLayoutPolicy(int policy) {
        ((JTabbedPane) jcomponent).setTabLayoutPolicy(policy);
    }

    @Override
    public void setTabPlacement(int tabPlacement) {
        ((JTabbedPane) jcomponent).setTabPlacement(tabPlacement);
    }

    @Override
    public void setTitleAt(int tab, String label) {
        ((JTabbedPane) jcomponent).setTitleAt(tab, label);
    }
}
