/*
 * File: ToolBar.java
 * 
 * Created: Wed Jun 30 15:56:30 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.SQLException;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import publicapi.ToolBarAPI;
import core.connection.BadPasswordException;
import core.document.Document;
import core.parser.Proper;
import core.rml.Container;
import core.rml.RmlObject;
import core.rml.VisualRmlObject;
import core.rml.dbi.exception.UpdateException;
import core.rml.ui.impl.ZPanelImpl;
import core.rml.ui.interfaces.ZComponent;
import core.rml.ui.interfaces.ZPanel;

/**
 * Панель с кнопками 
 * Зачастую контейнер, содержащий все остальные объекты документа. 
 * Кнопки располагаются либо снизу либо сверху от главной панели документа 
 *
 */
public class ToolBar extends VisualRmlObject implements FocusListener, ToolBarAPI {
	
	private ZPanel panel = ZPanelImpl.create();
	
	private Container container = new Container(this);
	
    /**
     * 
     */
    ZPanel                    pan              = ZPanelImpl.create();
    
    VisualRmlObject main = null;


    public ToolBar() {
    	panel.setLayout(new BorderLayout());
        pan.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 2));
        panel.addFocusListener(this);
        //panel.enableEvents(AWTEvent.FOCUS_EVENT_MASK);
    }

    public void init(Proper p, Document doc) {
    	
    	super.init(p, doc);
        String s = (String) p.get("ALIGN");
        if ((s == null) || (s.toUpperCase().equals("SOUTH"))) {
        	panel.add("South", pan);
        }
        else {
        	panel.add("North", pan);
        }
        s = (String) p.get("BACKGROUND");
        if (s != null) {
            pan.setBackground(loader.ZetaUtility.color(s));
        }
        s = (String) p.get("FOREGROUND");
        if (s != null) {
            pan.setForeground(loader.ZetaUtility.color(s));
        }
        s = (String) p.get("FONT");
        if (s != null) {
            pan.setFont(loader.ZetaUtility.font(s));
        }
    }

    public int retrieve() throws Exception {
    	container.retrieveAll();
    	return 0;
    }

    public void toDS() {
    	container.toDSAll();
    }

    public void fromDS() {
    	container.fromDSAll();
    }


    public void update() throws UpdateException, BadPasswordException, SQLException {
    	container.updateAll();
    }

	@Override
	public void addChild(RmlObject child) {
        container.addChildToCollection(child);
	    
		if(child instanceof VisualRmlObject){
			if(main == null){
				main = (VisualRmlObject) child;
		        panel.add("Center", ((VisualRmlObject) child).getVisualComponent());
		        
			}else{
	            pan.add(((VisualRmlObject) child).getVisualComponent());
			}
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
        if (main != null) {
            (main.getVisualComponent()).requestFocus();
        }
        else {
            Component c = pan.getComponent(0);
            c.requestFocus();
        }
	}

	@Override
	public void focusLost(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focusThis() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ZComponent getVisualComponent() {
		return panel;
	}

	@Override
	public RmlObject[] getChildren() {
		return container.getChildren();
	}

	@Override
	public void initChildren() {
	}

	@Override
	public Container getContainer() {
		return container;
	}

	@Override
	public boolean addChildrenAutomaticly() {
		return true;
	}

	@Override
	protected Border getDefaultBorder() {
		return new EmptyBorder(0,0,0,0);
	}

}
