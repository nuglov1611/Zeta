package core.rml.ui.impl;

import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.apache.log4j.Logger;

import core.rml.ui.EDTInvocationHandler;
import core.rml.ui.ZComponentImpl;
import core.rml.ui.interfaces.ZComboBox;

public class ZComboBoxImpl extends ZComponentImpl implements ZComboBox {
	
	private static final Logger log = Logger.getLogger(ZComboBoxImpl.class);

	public static ZComboBox create(){
		try {
			return (ZComboBox) java.lang.reflect.Proxy.newProxyInstance(ZComboBoxImpl.class.getClassLoader(),
	                new Class[]{ZComboBox.class}, new EDTInvocationHandler(new ZComboBoxImpl(new JComboBox())));
			
		} catch (SecurityException e) {
			log.error("!", e);
		} 
		return null;
	}
	
	
	protected ZComboBoxImpl(JComponent comp){
		super(comp); 
	}


	@Override
	public void addActionListener(ActionListener al) {
		((JComboBox)jcomponent).addActionListener(al);
	}


	@Override
	public void addItem(Object child) {
		((JComboBox)jcomponent).addItem(child);
	}


	@Override
	public Object getItemAt(int index) {
		return ((JComboBox)jcomponent).getItemAt(index);
	}


	@Override
	public int getItemCount() {
		return ((JComboBox)jcomponent).getItemCount();
	}


	@Override
	public ComboBoxModel getModel() {
		return ((JComboBox)jcomponent).getModel();
	}


	@Override
	public int getSelectedIndex() {
		return ((JComboBox)jcomponent).getSelectedIndex();
	}


	@Override
	public void removeAllItems() {
		((JComboBox)jcomponent).removeAllItems();		
	}


	@Override
	public void setSelectedIndex(int index) {
		((JComboBox)jcomponent).setSelectedIndex(index);		
	}


	@Override
	public void setSelectedItem(Object item) {
		((JComboBox)jcomponent).setSelectedItem(item);		
	}


	@Override
	public void addItemListener(ItemListener listener) {
		((JComboBox)jcomponent).addItemListener(listener);
	}


	@Override
	public Object getSelectedItem() {
		return ((JComboBox)jcomponent).getSelectedItem();
	}
}
