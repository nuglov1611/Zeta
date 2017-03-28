package views;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import loader.Loader;

import org.apache.log4j.Logger;

import publicapi.ImageAPI;
import core.document.Document;
import core.parser.Proper;
import core.rml.VisualRmlObject;
import core.rml.ui.impl.ZPanelImpl;
import core.rml.ui.interfaces.ZComponent;
import core.rml.ui.interfaces.ZPanel;

/**
* Графический компонент "Изображение"
* 
*/
public class IMage extends VisualRmlObject implements ImageAPI {
	
	private class iPanel extends JPanel{
		
	    public void paint(Graphics g) {
	        if (im != null) {
	            if (!resize) {
	                g.drawImage(im, left, top, this);
	            }
	            else {
	                g.drawImage(im, left, top, width, height, this);
	            }
	        }
	    }
		
	}

    private static final Logger log    = Logger.getLogger(IMage.class);

    private ZPanel imagePanel = null;
    
    Image                       im;

    String                      iname;

    boolean                     resize = false;

    public void init(Proper prop, Document doc) {
		try {
			imagePanel = ZPanelImpl.create(new iPanel());
			
		} catch (SecurityException e) {
			log.error("!", e);
		} 
    	
    	
        super.init(prop, doc);
    	String sp;
        
        sp = (String) prop.get("RESIZE", "NO");
        if (sp.toUpperCase().equals("YES")) {
            resize = true;
        }
        iname = (String) prop.get("SRC");
        im = getImage(iname);
        imagePanel.setLocation(left, top);
        imagePanel.setSize(width, height);
    }


    public void paint(Graphics g, int a) {
        if (im != null) {
            if (!resize) {
                g.drawImage(im, left * a / 100, top * a / 100, imagePanel.getJComponent());
            }
            else {
                g.drawImage(im, left * a / 100, top * a / 100, width * a / 100,
                        height * a / 100, imagePanel.getJComponent());
            }
        }
    }

    /**
     * Возвращает текущее изображение
     * @return изображение 
     */
    public Image getImage() {
        return im;
    }

    /**
    * Загружает изображение из RML-репозитория
    * @param name - путь к файлу-изображению в RML-репозитории 
    * @return загруженное изображение 
    */   
    public Image getImage(String name) {
        if (name == null || name.equals("")) {
            return null;
        }

        Image image = null;
        byte[] img;
        try {
            img = Loader.getInstanceRml().loadByName_bytes(name);
            Toolkit t = Toolkit.getDefaultToolkit();
            image = t.createImage(img);
        }
        catch (Exception e) {
           log.error("Cann't load image: "+name);
        }
        return image;
    }



	@Override
	public void focusThis() {
		imagePanel.requestFocus();
	}



	@Override
	public ZComponent getVisualComponent() {
		return imagePanel;
	}


	@Override
	protected Border getDefaultBorder() {
		return new EmptyBorder(0,0,0,0);
	}
}
