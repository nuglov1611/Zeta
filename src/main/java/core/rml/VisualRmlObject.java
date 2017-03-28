/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.rml;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import loader.Loader;
import loader.ZetaUtility;

import org.apache.log4j.Logger;

import publicapi.RmlContainerAPI;
import publicapi.VisualRmlObjectAPI;
import action.api.RTException;
import action.calc.Nil;
import core.document.Document;
import core.parser.Proper;
import core.rml.ui.interfaces.ZComponent;

/**
 * Суперкаласс для Rml-объектов, имеющих графическое представление.
 * @author nuglov
 * {@inheritDoc}
 */
abstract public class VisualRmlObject extends RmlObject implements VisualRmlObjectAPI {
	/**
	 *@internal
	 */
	private static final Logger log = Logger.getLogger(VisualRmlObject.class);

	/**
	 *@internal
	 */
    public static int UNKNOWN_COLOR = -1;
	/**
	 *@internal
	 */
    public static int SELECTED = 1;
	/**
	 *@internal
	 */
    public static int UNSELECTED = 2;
	/**
	 *@internal
	 */
    public static int PRESSED = 3;
    /**
     * @internal
     * Координата левого верхнего угла визуального компонента по оси X
     */
    protected int left = 0;
    /**
     * @internal
     * Координата левого верхнего угла визуального компонента по оси Y
     */
    protected int top = 0;
    /**
     * @internal
     * Ширина визуального компонента
     */
    protected int width = 0;
    /**
     * @internal
     * Высота визуального компонента
     */
    protected int height = 0;
    /**
     * @internal
     * Цвет фона визуального компонента
     */
    protected String background;
    /**
     * @internal
     * Ссылка на картинку для заднего фона
     */
    protected String bgimage = null;
    /**
     * @internal
     * Цвет шрифта визуального компонента
     */
    protected String foreground;
    /**
     * @internal
     * Флаг видимости компонента
     */
    protected String visible = "yes";
    /**
     * @internal
     * Прозрачность фона компонента, варьируется от 0 (прозрачный) до 255 (непрозрачный)
     */
    protected int bgTransparency = -1;

    /**
	 *@internal
	 */
    protected boolean firstFocus = false;
    /**
     * @internal
     * Шрифт визуального компонента
     */
    protected String toolTipText = null; 
    
	/**
	 *@internal
	 */
    protected Font font = null;
	/**
	 *@internal
	 */
    protected String borderLayoutPos = BorderLayout.CENTER;
    
	/**
	 *@internal
	 */
    protected Border border = null;

	/**
	 *@internal
	 */
    abstract public void focusThis();

	/**
	 *@internal
	 */
    abstract public ZComponent getVisualComponent();

    
    /**
     * Возвращает размеры графического компонента.
     * @return массив размеров (width, height)
     */
    public Dimension getSize() {
        return new Dimension(width, height);
    }

    /**
     * Возвращает значение признака видимости графического компонента.
     * @return true - видимый
     */
    public boolean isVisible(){
    	try {
			return ((String) document.calculateMacro(visible)).equalsIgnoreCase(RmlConstants.YES);
		} catch (Exception e) {
			log.error("!", e);
			return true;
		}
    }
    
    /**
     * Возвращает координаты графического компонента.
     * @return координаты верхнего левого угла
     */
    public Point getPosition() {
        return new Point(left, top);
    }

	/**
	 *@internal
	 */
    public void init(Proper prop, Document doc) {
        super.init(prop, doc);
        if (prop == null) {
            return;
        }
        String s = null;
        Integer ip = null;

        background = (String) prop.get(RmlConstants.BG_COLOR);
        if(background == null)
            background = (String) prop.get(RmlConstants.BACKGROUND);

        foreground = (String) prop.get(RmlConstants.FONT_COLOR);
        if(foreground == null)
            foreground = (String) prop.get(RmlConstants.FOREGROUND);

        s = (String) prop.get("FONT");
        if (s != null) {
        	font = loader.ZetaUtility.font1(s);
        }else if (getVisualComponent() != null){
	        	if(getVisualComponent().getFont() != null){
	        	String name = (String) prop.get("FONT_FACE", "Serif");
	        	Integer style = (Integer) prop.get("FONT_FAMILY", getVisualComponent().getFont().getStyle());
	        	Integer size = (Integer) prop.get("FONT_SIZE", getVisualComponent().getFont().getSize());
	        	font = new Font(name, style, size);
        	}
        }

        ip = (Integer) prop.get(RmlConstants.LEFT);
        if (ip != null) {
            left = ip.intValue();
        }

        ip = (Integer) prop.get(RmlConstants.TOP);
        if (ip != null) {
            top = ip.intValue();
        }
        ip = (Integer) prop.get(RmlConstants.WIDTH);
        if (ip != null) {
            width = ip.intValue();
        }
        ip = (Integer) prop.get(RmlConstants.HEIGHT);
        if (ip != null) {
            height = ip.intValue();
        }


        visible = (String) prop.get(RmlConstants.VISIBLE, "yes");

        s = (String) prop.get(RmlConstants.FIRSTFOCUS);
        if (s != null) {
            firstFocus = s.equalsIgnoreCase(RmlConstants.NO);
        }

//        bgimage = (String) prop.get(RmlConstants.BGIMAGE);
//        if (bgimage != null) {
//            setBGImage(bgimage, (String) prop.get(RmlConstants.BGIMAGESTYLE), UNSELECTED, true);
//        }
//
//        ip = (Integer) prop.get(RmlConstants.BG_TRANSPARENCY);
//        if (ip != null) {
//            bgTransparency = ip.intValue();
//        }

        s = (String) prop.get(RmlConstants.POSITION, "CENTER");
        if (s.equalsIgnoreCase(BorderLayout.CENTER)) {
            borderLayoutPos = BorderLayout.CENTER;
        } else if (s.equalsIgnoreCase(BorderLayout.EAST)) {
            borderLayoutPos = BorderLayout.EAST;
        } else if (s.equalsIgnoreCase(BorderLayout.WEST)) {
            borderLayoutPos = BorderLayout.WEST;
        } else if (s.equalsIgnoreCase(BorderLayout.SOUTH)) {
            borderLayoutPos = BorderLayout.SOUTH;
        } else if (s.equalsIgnoreCase(BorderLayout.NORTH)) {
            borderLayoutPos = BorderLayout.NORTH;
        }
        
        toolTipText = (String) prop.get(RmlConstants.TOOLTIPTEXT);

        initCommonVisualComponent(getVisualComponent());
        setBorder(prop);
    }

	/**
	 *@internal
	 */
    private void setBorder(Proper prop) {
		if(getVisualComponent() == null )
			return;
		String b = (String) prop.get(RmlConstants.BORDER);
		Insets ins = getVisualComponent().getInsets(); 
//		if(ins == null)
//			ins = new Insets(0,0,0,0);

		if (b!=null){
			if(b.equalsIgnoreCase("line")){
				Color bc = ZetaUtility.color((String)prop.get("BORDERCOLOR", "black"));
				int bthick = (Integer)prop.get("BORDERTHICK", 1);
				boolean bround = ((String)prop.get("BROUND", "NO")).equalsIgnoreCase(RmlConstants.YES);
				border = new LineBorder(bc, bthick, bround);
				getVisualComponent().setBorder(border);
			}else if(b.equalsIgnoreCase("empty")){
				border = new EmptyBorder(ins);
				getVisualComponent().setBorder(border);
			}else if(b.equalsIgnoreCase("default")){
				border = getDefaultBorder();
				getVisualComponent().setBorder(border);
			}
		}
		
		int btop = (Integer) prop.get("BTOP", ins.top);
		int bleft = (Integer) prop.get("BLEFT", ins.left);
		int bbottom = (Integer) prop.get("BBOTTOM", ins.bottom);
		int bright = (Integer) prop.get("BRIGHT", ins.right);
		
		getVisualComponent().getInsets().set(btop, bleft, bbottom, bright);
	}

	/**
	 *@internal
	 */
    protected abstract Border getDefaultBorder();
    
	/**
	 *@internal
	 */
	public String getPositionForBorder() {
        return borderLayoutPos;
    }

    /**
     * @internal
     * Initialize rml object with all assigned properties
     * Be sure that you initialize visual object before calling this method and
     * calling init()
     */
    protected void initCommonVisualComponent(ZComponent visualObject) {
        if (visualObject != null) {
            if (font != null) {
                visualObject.setFont(font);
            }
            if (background != null) {
                visualObject.setBackground(ZetaUtility.color(background));
            }
            if (foreground != null) {
                visualObject.setForeground(ZetaUtility.color(foreground));
            }
            visualObject.setLocation(left, top);
            visualObject.setSize(width, height);
            visualObject.setVisible(isVisible());
            visualObject.setToolTipText(toolTipText);
        }
    }

	/**
	 *@internal
	 */
    public static int getColor(String color) {
        int res = UNKNOWN_COLOR;
        if (color != null) {
            try {
                int red = Integer.parseInt(color.substring(1, 3), 16);
                int green = Integer.parseInt(color.substring(3, 5), 16);
                int blue = Integer.parseInt(color.substring(5, 7), 16);
                res = ((red << 16) + (green << 8) + blue);
            } catch (Exception e) {
            	log.error("", e);
            }
        }

        return res;
    }

	/**
	 *@internal
	 */
    public Object method(String method, Object arg) throws Exception {
        if (getVisualComponent() != null) {
            if (method.equalsIgnoreCase("setVisible")) {
                setVisible(((String) visible).equalsIgnoreCase("YES"));
            } else if(method.equalsIgnoreCase("setEnabled")){
                if (arg instanceof Double) {
                    setEnabled(((Double) arg).intValue() != 0);
                } else if (arg instanceof String) {
                    setEnabled(((String) arg).equalsIgnoreCase("yes"));
                }
                else {
                    throw new RTException("ClassCastException",
                            "Wrong paramter of setEnabled number/string");
                }
            }else if (method.equalsIgnoreCase("setTop")) {
                setTop(((Double) arg).intValue());
            } else if (method.equalsIgnoreCase("setLeft")) {
                setLeft(((Double) arg).intValue());
            } else if (method.equalsIgnoreCase("setPosition")) {
                final Vector v = (Vector) arg;
                setLocation(((Double) v.elementAt(0)).intValue(), ((Double) v.elementAt(1)).intValue());
            } else if (method.equalsIgnoreCase("setFocusable")) {
                setFocusable(((String) arg).equalsIgnoreCase("YES"));
            } else if (method.equalsIgnoreCase("isFocusable")) {
                return isFocusable() ? RmlConstants.YES : RmlConstants.NO;
            } else if (method.equalsIgnoreCase("requestFocus")) {
                requestFocus();
            } else if (method.equalsIgnoreCase("setWidth")) {
                setWidth(((Double) arg).intValue());
            } else if (method.equalsIgnoreCase("setHeight")) {
                setHeight(((Double) arg).intValue());
            } else if (method.equalsIgnoreCase("setSize")) {
                final Vector v = (Vector) arg;
                setSize(((Double) v.elementAt(0)).intValue(), ((Double) v.elementAt(1)).intValue());
//            } else if (method.equalsIgnoreCase("setBGImage")) {
//                if (arg instanceof Image) {
//                    setBGImage((Image) arg, null, UNSELECTED);
//                } else if (arg instanceof Vector) {
//                    final Vector v = (Vector) arg;
//                    final String bgtype = (String) v.elementAt(1);
//                    final Image img = (Image) v.elementAt(0);
//                    setBGImage(img, bgtype, UNSELECTED);
//                }
            } else {
                throw new RTException("HasNotMethod", "method " + method + " not defined in RmlObject");
            }
        } else {
            throw new RTException("VisualIsNull", "Can't set properties, visual component for " + this.getClass().getName() + " alias " + alias + " is null");
        }

        return new Nil();
    }

    /**
     * Установить размеры объекта (ширину и высоту)
     * @param w - ширина
     * @param h - высота
     */
    public void setSize(int w, int h) {
        width = w;
        height = h;
        getVisualComponent().setSize(new Dimension(width, height));
    }

    /**
     * Установить высоту визуального компонента
     * @param h высота
     */
    public void setHeight(int h) {
        height = h;
        getVisualComponent().setSize(getVisualComponent().getWidth(), height);
    }

    /**
     * Установить ширину визуального компонента
     * @param w ширина 
     */
    public void setWidth(int w) {
        width = w;
        getVisualComponent().setSize(width, getVisualComponent().getHeight());
    }

    /**
     * Получить фокус 
     */
    public void requestFocus() {
        getVisualComponent().requestFocus();
    }

    /**
     * Поволяет узнать возможность графического компонента принимать фокус
     * @return true - если объект может принимать фокус, false - если не может
     */
    public boolean isFocusable() {
        return getVisualComponent().isFocusable();
    }

    /**
     * Задать возможность графического компонента принимать фокус
     * @param focusable true - компонент может принимать фокус, false - не может принимать фокус
     */
    public void setFocusable(boolean focusable) {
        getVisualComponent().setFocusable(focusable);
    }

    /**
     * Задать координаты графического компонента по горизонтали и вертикали
     * @param x координата по горизонтали
     * @param y координата по вертикали
     */
    public void setLocation(int x, int y) {
        left = x;
        top = y;
        getVisualComponent().setLocation(left, top);
    }

    /**
     * Задать координату графического компонента по горизонтали (координата левой границы)
     * @param x координата
     */
    public void setLeft(int x) {
        left = x;
        getVisualComponent().setLocation(left, getVisualComponent().getY());
    }

    /**
     * Задать координату графического компонента по вертикали (координата верхней границы)
     * @param y координата
     */
    public void setTop(int y) {
        top = y;
        getVisualComponent().setLocation(getVisualComponent().getX(), top);
    }

    /**
     * Задать признак "активности" графического компонента
     * @param enabled признак (true - компонент активный, false - не активный("серый"))
     */
    public void setEnabled(boolean enabled) {
        getVisualComponent().setEnabled(enabled);
    }

    /**
     * Задать признак видимости графического компонента
     * @param visible признак (true - видимый, false - не видимый)
     */
    public void setVisible(boolean visible) {
        setVisible(visible, true);
    }

    protected Image loadImage(String link) {
        if (link == null || link.trim().equals("")) {
            return null;
        }

        String clink = null;

        try {
            try {
                clink = document.calculateMacro(link).toString();
            } catch (Exception e) {
                System.out.println(" ERROR IN IMAGE LINK EXPRESSION:");
                log.error("", e);
                return null;
            }

            final byte[] img_data = Loader.getInstanceRml().loadByName_bytes(clink);
            
            return Toolkit.getDefaultToolkit().createImage(img_data);
        } catch (Exception ex) {
        	log.error("", ex);
        }
        return null;
    }

	/**
	 *@internal
	 */
    protected void setBGImage(final Image image, final String constr, final int style_type) {
//        Display.getInstance().callSerially(
//                new Runnable() {
//                    public void run() {
//                        try {
//                            setBGImage(image, constr, style_type, false);
//                        } catch (Exception ex) {
//                            log.error("", ex);
//                        }
//                    }
//                });
    }

	/**
	 *@internal
	 */
    protected void setBGImage(final String link, final String constr, final int style_type, final boolean skipValidating) {
//        Display.getInstance().callSerially(
//                new Runnable() {
//                    public void run() {
//                        try {
//                            setBGImage(loadImage(link), constr, style_type, skipValidating);
//                        } catch (Exception ex) {
//                            log.error("", ex);
//                        }
//                    }
//                });
    }

	/**
	 *@internal
	 */
    protected void setBGImage(Image img, String constr, int style_type, boolean skipValidating) {
//        if (img == null) {
//            return;
//        }
//        byte style = Style.BACKGROUND_IMAGE_SCALED;
//        if (constr != null) {
//            if (constr.equalsIgnoreCase("IMAGE_SCALED")) {
//                style = Style.BACKGROUND_IMAGE_SCALED;
//            } else if (constr.equalsIgnoreCase("IMAGE_TILE_BOTH")) {
//                style = Style.BACKGROUND_IMAGE_TILE_BOTH;
//            } else if (constr.equalsIgnoreCase("IMAGE_TILE_VERTICAL")) {
//                style = Style.BACKGROUND_IMAGE_TILE_VERTICAL;
//            } else if (constr.equalsIgnoreCase("IMAGE_TILE_HORIZONTAL")) {
//                style = Style.BACKGROUND_IMAGE_TILE_HORIZONTAL;
//            } else if (constr.equalsIgnoreCase("IMAGE_ALIGN_TOP")) {
//                style = Style.BACKGROUND_IMAGE_ALIGNED;
//                getVisualComponent().getStyle().setBackgroundAlignment(Style.BACKGROUND_IMAGE_ALIGN_TOP);
//            } else if (constr.equalsIgnoreCase("IMAGE_ALIGN_BOTTOM")) {
//                style = Style.BACKGROUND_IMAGE_ALIGNED;
//                getVisualComponent().getStyle().setBackgroundAlignment(Style.BACKGROUND_IMAGE_ALIGN_BOTTOM);
//            } else if (constr.equalsIgnoreCase("IMAGE_ALIGN_LEFT")) {
//                style = Style.BACKGROUND_IMAGE_ALIGNED;
//                getVisualComponent().getStyle().setBackgroundAlignment(Style.BACKGROUND_IMAGE_ALIGN_LEFT);
//            } else if (constr.equalsIgnoreCase("IMAGE_ALIGN_RIGHT")) {
//                style = Style.BACKGROUND_IMAGE_ALIGNED;
//                getVisualComponent().getStyle().setBackgroundAlignment(Style.BACKGROUND_IMAGE_ALIGN_RIGHT);
//            } else if (constr.equalsIgnoreCase("IMAGE_ALIGN_CENTER")) {
//                style = Style.BACKGROUND_IMAGE_ALIGNED;
//                getVisualComponent().getStyle().setBackgroundAlignment(Style.BACKGROUND_IMAGE_ALIGN_CENTER);
//            }
//        }
//        Style st = null;
//        if (style_type == SELECTED) {
//            st = getVisualComponent().getSelectedStyle();
//        } else if (style_type == PRESSED && (getVisualComponent() instanceof ZButton)) {
//            st = ((ZButton) getVisualComponent()).getPressedStyle();
//        } else {
//            st = getVisualComponent().getUnselectedStyle();
//        }
//        st.setBackgroundType(style);
//        st.setBgImage(img);
//        if (!skipValidating) {
//            try {
//                document.getForm().revalidate();
//            } catch (NullPointerException e) {
//                System.out.println("null exception inside setBGImage: " + alias + " img: " + img + " constr: " + constr + " style: " + style_type);
//                log.error("", e);
//            }
//        }
    }

	/**
	 *@internal
	 */
    public boolean getVisible() {
        return isVisible() && getVisualComponent().isVisible();
    }

	/**
	 *@internal
	 */
    public void setVisible(boolean vsbl, boolean direct) {

        if (direct) {
            //При рямом вызове сохраняем состояние объекта
            visible = vsbl ? RmlConstants.YES : RmlConstants.NO ;
        }

        if (vsbl && !isVisible()) {
            //не нужно делать видимым невидимый компонент
            // в случае непрямого вызова воздействия
            return;
        } else if (vsbl
                && (parent != null)
                && (parent instanceof VisualRmlObject)
                && !((VisualRmlObject) parent).getVisible()) {
            //не нужно делать видимым компонент, если его контейнер невидим
            return;
        } else {
            getVisualComponent().setVisible(vsbl);
        }

        if (this instanceof RmlContainerAPI) {
            //нужно применить ту же политику ко всем детям.
            //В этом случае вызов является косвеным
            final RmlObject[] children = ((RmlContainerAPI) this).getChildren();
            if (children != null) {
                for (int i = 0; i < children.length; i++) {
                    if (children[i] instanceof VisualRmlObject) {
                        ((VisualRmlObject) children[i]).setVisible(vsbl, false);
                    }
                }
            }
        }
        getVisualComponent().repaint();

    }

	/**
	 *@internal
	 */
	public Font getFont() {
		return font;
	}

	/**
	 *@internal
	 */
	public void setFont(Font font) {
		getVisualComponent().setFont(font);
	}
	
//	protected void setIcon(String path, String description, boolean scaled){
//		ImageIcon icon = createImageIcon(path, description);
//		if(icon != null){
//			if(scaled){
//				getVisualComponent().setI
//			}
//		}
//	}

    /**
     * @internal
     * Creates an ImageIcon if the path is valid.
     * @param String - resource path
     * @param String - description of the file
     */
    protected ImageIcon createImageIcon(String path,
            String description) {
    	ImageIcon img = null;
        if (path != null) {
			try {
	        	final String isrc = (String) document.calculateMacro(path);
	        	if(description == null)
	        		img = new ImageIcon(Loader.getInstanceRml().loadByName_bytes(isrc));
	        	else
	        		img = new ImageIcon(Loader.getInstanceRml().loadByName_bytes(isrc), description);
			} catch (Exception e) {
				log.error("!", e);
			}
        }
        
        return img;
    }
    
    /**
     * @internal
     * Resizes an image using a Graphics2D object backed by a BufferedImage.
     * @param srcImg - source image to scale
     * @param w - desired width
     * @param h - desired height
     * @return - the new resized image
     */
    protected Image getScaledImage(Image srcImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        return resizedImg;
    }
}
