package core.rml;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;

import javax.swing.BoxLayout;

import loader.ZetaUtility;

import core.parser.Proper;
import core.rml.ui.interfaces.ZComponent;
import core.rml.ui.interfaces.ZPanel;
import org.apache.log4j.Logger;

public class LayoutMng {

	private static final Logger Log = Logger.getLogger(LayoutMng.class);

	public static void setLayout(ZPanel panel, Proper proper, LayoutManager deflayout) {
        String lt = (String) proper.get("LAYOUT");
        if (lt != null) {
            lt = lt.toLowerCase();
            if (lt.startsWith("abs")) {
                panel.setLayout(null);
            } else if (lt.equalsIgnoreCase("border")) {
                panel.setLayout(new BorderLayout());
            } else if (lt.equalsIgnoreCase("box")) {
            	final String axis = (String) proper.get("AXIS");  
            	if(axis != null && axis.equalsIgnoreCase("X"))
            		panel.setLayout(new BoxLayout(panel.getJComponent(), BoxLayout.X_AXIS));
            	else
            		panel.setLayout(new BoxLayout(panel.getJComponent(), BoxLayout.Y_AXIS));
            } else if (lt.equalsIgnoreCase("grid")) {
                Integer x = (Integer) proper.get("GRID_X");
                Integer y = (Integer) proper.get("GRID_Y");
                if (x != null && y != null) {
                	panel.setLayout(new GridLayout(x.intValue(), y.intValue()));
                } else {
                    ZetaUtility.message("Неправильное описание Grid layout!");
                }
            } else if (lt.equalsIgnoreCase("flow")) {
            	panel.setLayout(new FlowLayout());
            }
        }else{
        	panel.setLayout(deflayout);
        }
	}

	
	public static void add(ZComponent parent, VisualRmlObject child){
		if(parent.getLayout() instanceof BorderLayout){
			parent.add(child.getPositionForBorder(), child.getVisualComponent());
		}else{
			try{
				parent.add(child.getVisualComponent());
			}catch(Exception e){
				Log.error("");
			}
		}
	}
}
