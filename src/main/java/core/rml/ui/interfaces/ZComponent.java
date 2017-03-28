package core.rml.ui.interfaces;

import core.rml.ui.RequiresEDT;
import core.rml.ui.RequiresEDTPolicy;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ComponentListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

public interface ZComponent {


    void addComponentListener(ComponentListener listener);

    void addFocusListener(FocusListener listener);

    void addKeyListener(KeyListener listener);

    void addMouseListener(MouseListener listener);

    Color getBackground();

    Color getForeground();


    Container getParent();

    Rectangle getBounds();

    Font getFont();

    FontMetrics getFontMetrics(Font font);

    int getHeight();

    Insets getInsets();

    JComponent getJComponent();

    LayoutManager getLayout();

    Point getLocation();

    Point getLocationOnScreen();

    Dimension getPreferredSize();

    Toolkit getToolkit();

    Component getTopLevelAncestor();

    int getWidth();

    int getX();

    int getY();

    boolean isEnabled();

    boolean isFocusOwner();

    boolean isFocusable();

    boolean isVisible();

    void repaint();

    void setToolTipText(String text);


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

    @RequiresEDT
//(RequiresEDTPolicy.SYNC)
    void revalidate();

    @RequiresEDT
    void setAlignmentX(float alignment);

    @RequiresEDT
    void setAlignmentY(float alignment);

    @RequiresEDT
    void setMinimumSize(Dimension d);

    @RequiresEDT
    void setMaximumSize(Dimension dimension);


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
    void remove(ZComponent component);

    @RequiresEDT
    void remove(int index);

    @RequiresEDT
    void removeAll();

    Component getComponent(int i);

    @RequiresEDT
    void doLayout();


    @RequiresEDT(RequiresEDTPolicy.SYNC)
    void transferFocus();

    @RequiresEDT(RequiresEDTPolicy.SYNC)
    boolean requestFocusInWindow();

    @RequiresEDT
    void setBounds(int left, int top, int width, int height);

    @RequiresEDT
    void setLayout(LayoutManager layout);

    @RequiresEDT
    void paint(Graphics graphics);


}