package core.rml.ui.impl;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;

import org.apache.log4j.Logger;

import views.grid.renderer.cross.CellSpanTable;
import core.rml.ui.EDTInvocationHandler;
import core.rml.ui.ZComponentImpl;
import core.rml.ui.interfaces.ZComponent;
import core.rml.ui.interfaces.ZRadioButton;
import core.rml.ui.interfaces.ZScrollPane;


public class ZScrollPaneImpl extends ZComponentImpl implements ZScrollPane {
	private static final Logger log = Logger.getLogger(ZScrollPaneImpl.class);

	public static ZScrollPane create(){
		try {
			return (ZScrollPane) java.lang.reflect.Proxy.newProxyInstance(ZScrollPaneImpl.class.getClassLoader(),
	                new Class[]{ZScrollPane.class}, new EDTInvocationHandler(new ZScrollPaneImpl(new JScrollPane())));
			
		} catch (SecurityException e) {
			log.error("!", e);
		} 
		return null;
	}

	public static ZComponent create(ZComponent component) {
		try {
			return (ZScrollPane) java.lang.reflect.Proxy.newProxyInstance(ZRadioButton.class.getClassLoader(),
	                new Class[]{ZScrollPane.class}, new EDTInvocationHandler(new ZScrollPaneImpl(new JScrollPane(component.getJComponent()))));
			
		} catch (SecurityException e) {
			log.error("!", e);
		} 
		return null;
	}
	
	public static ZScrollPane create(JComponent component) {
		try {
			return (ZScrollPane) java.lang.reflect.Proxy.newProxyInstance(ZRadioButton.class.getClassLoader(),
	                new Class[]{ZScrollPane.class}, new EDTInvocationHandler(new ZScrollPaneImpl(new JScrollPane(component))));
			
		} catch (SecurityException e) {
			log.error("!", e);
		} 
		return null;
	}
	
	
	protected ZScrollPaneImpl(JComponent comp){
		super(comp); 
	}

	@Override
	public JScrollBar getHorizontalScrollBar() {
		return ((JScrollPane)jcomponent).getHorizontalScrollBar();
	}

	@Override
	public JScrollBar getVerticalScrollBar() {
		return ((JScrollPane)jcomponent).getVerticalScrollBar();
	}

	@Override
	public JViewport getViewport() {
		return ((JScrollPane)jcomponent).getViewport();
	}

	@Override
	public void setHorizontalScrollBarPolicy(int policy) {
		((JScrollPane)jcomponent).setHorizontalScrollBarPolicy(policy);
	}

	@Override
	public void setVerticalScrollBarPolicy(int policy) {
		((JScrollPane)jcomponent).setVerticalScrollBarPolicy(policy);		
	}

	@Override
	public void setWheelScrollingEnabled(boolean b) {
		((JScrollPane)jcomponent).setWheelScrollingEnabled(b);		
	}

	@Override
	public void setRowHeaderView(CellSpanTable rowsTable) {
		((JScrollPane)jcomponent).setRowHeaderView(rowsTable);
		
	}

	@Override
	public void setColumnHeaderView(CellSpanTable colsTable) {
		((JScrollPane)jcomponent).setColumnHeaderView(colsTable);
		
	}

	@Override
	public void setViewportView(JTable dataTable) {
		((JScrollPane)jcomponent).setViewportView(dataTable);
	}

	@Override
	public JViewport getRowHeader() {
		return ((JScrollPane)jcomponent).getRowHeader();
	}

	@Override
	public void setRowHeaderView(Component rowHeader) {
		((JScrollPane)jcomponent).setRowHeaderView(rowHeader);
	}

}
