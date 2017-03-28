package core.rml.ui.interfaces;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.border.Border;

import core.rml.ui.RequiresEDT;
import core.rml.ui.RequiresEDTPolicy;

public interface ZComponent {

	
	public void addComponentListener(ComponentListener listener);

	public void addFocusListener(FocusListener listener);

	public void addKeyListener(KeyListener listener);

	public void addMouseListener(MouseListener listener);

	public Color getBackground() ;
	
	public Color getForeground();

	
	public Container getParent();

	public Rectangle getBounds();

	public Font getFont();

	public FontMetrics getFontMetrics(Font font);

	public int getHeight();

	public Insets getInsets();

	public JComponent getJComponent();

	public LayoutManager getLayout();

	public Point getLocation();

	public Point getLocationOnScreen();

	public Dimension getPreferredSize();

	public Toolkit getToolkit();

	public Component getTopLevelAncestor();

	public int getWidth();

	public int getX();

	public int getY();

	public boolean isEnabled();

	public boolean isFocusOwner();

	public boolean isFocusable();

	public boolean isVisible();

	public void repaint();

	public void setToolTipText(String text);	
	
	
	Graphics getGraphics();

	
	
//Methods which must execute in EDT context 	
	
    @RequiresEDT
    void setFont(Font font);

    @RequiresEDT
    void setBackground(Color background);

    @RequiresEDT
    void setForeground(Color foreground);

    @RequiresEDT
    void setLocation(int left, int top);

    @RequiresEDT
    void setSize(int width, int height);
    
    //@RequiresEDT(RequiresEDTPolicy.SYNC)
    @RequiresEDT
    void setVisible(boolean visible);

    @RequiresEDT
	void setBorder(Border border);
    
    @RequiresEDT
	void setEnabled(boolean b);
    
    @RequiresEDT
	void setFocusable(boolean focusable);
    
    @RequiresEDT(RequiresEDTPolicy.SYNC)
	void requestFocus();
    
    @RequiresEDT
	void setSize(Dimension size);
    
    @RequiresEDT
	void setPreferredSize(Dimension dimension);
    
	@RequiresEDT//(RequiresEDTPolicy.SYNC)
	void revalidate();

	@RequiresEDT
	void setAlignmentX(float alignment);

	@RequiresEDT
	void setAlignmentY(float alignment);
	
	@RequiresEDT
	void setMinimumSize(Dimension d);
	
	@RequiresEDT
	public void setMaximumSize(Dimension dimension);


	@RequiresEDT
	void setCursor(Cursor predefinedCursor);
	
	@RequiresEDT
	void validate();

	@RequiresEDT(RequiresEDTPolicy.SYNC)
	Component add(ZComponent component);

	@RequiresEDT(RequiresEDTPolicy.SYNC)
	Component add(String positionForBorder, ZComponent component);

	@RequiresEDT
	void add(ZComponent component, Object constraints);

	@RequiresEDT
	public void remove(ZComponent component);
	
	@RequiresEDT
	public void remove(int index); 

	@RequiresEDT
	public void removeAll();
	
	public Component getComponent(int i);

	@RequiresEDT
	public void doLayout();
	
	
	@RequiresEDT(RequiresEDTPolicy.SYNC)
	void transferFocus();
	
	@RequiresEDT(RequiresEDTPolicy.SYNC)
	boolean requestFocusInWindow();
	
	@RequiresEDT
	public void setBounds(int left, int top, int width, int height);

	@RequiresEDT
	public void setLayout(LayoutManager layout);
	
	@RequiresEDT
	void paint(Graphics graphics);

	
}