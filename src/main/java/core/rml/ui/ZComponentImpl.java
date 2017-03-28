package core.rml.ui;

import core.rml.ui.interfaces.ZComponent;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ComponentListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

public class ZComponentImpl implements ZComponent {
    private final static Logger Log = Logger.getLogger(ZComponentImpl.class);

    protected JComponent jcomponent = null;

    protected ZComponentImpl(JComponent comp) {
        jcomponent = comp;
        // Since there is no common ancestor for text components,
        // enable DnD for them here
        if (jcomponent instanceof JTextComponent) {
            ((JTextComponent) jcomponent).setDragEnabled(true);
        }
    }

    @Override
    public void addComponentListener(ComponentListener listener) {
        jcomponent.addComponentListener(listener);
    }

    @Override
    public void addFocusListener(FocusListener listener) {
        jcomponent.addFocusListener(listener);
    }

    @Override
    public void addKeyListener(KeyListener listener) {
        jcomponent.addKeyListener(listener);
    }

    @Override
    public void addMouseListener(MouseListener listener) {
        jcomponent.addMouseListener(listener);
    }

    @Override
    public Color getBackground() {
        return jcomponent.getBackground();
    }

    @Override
    public Color getForeground() {
        return jcomponent.getForeground();
    }


    @Override
    public Rectangle getBounds() {
        return jcomponent.getBounds();
    }

    @Override
    public Font getFont() {
        return jcomponent.getFont();
    }

    @Override
    public FontMetrics getFontMetrics(Font font) {
        return jcomponent.getFontMetrics(font);
    }

    @Override
    public int getHeight() {
        return jcomponent.getHeight();
    }

    @Override
    public Insets getInsets() {
        return jcomponent.getInsets();
    }

    @Override
    public JComponent getJComponent() {
        return jcomponent;
    }

    @Override
    public LayoutManager getLayout() {
        return jcomponent.getLayout();
    }

    @Override
    public Point getLocation() {
        return jcomponent.getLocation();
    }

    @Override
    public Point getLocationOnScreen() {
        return jcomponent.getLocationOnScreen();
    }

    @Override
    public Dimension getPreferredSize() {
        return jcomponent.getPreferredSize();
    }

    @Override
    public Toolkit getToolkit() {
        return jcomponent.getToolkit();
    }

    @Override
    public Component getTopLevelAncestor() {
        return jcomponent.getTopLevelAncestor();
    }

    @Override
    public int getWidth() {
        return jcomponent.getWidth();
    }

    @Override
    public int getX() {
        return jcomponent.getX();
    }

    @Override
    public int getY() {
        return jcomponent.getY();
    }

    @Override
    public boolean isEnabled() {
        return jcomponent.isEnabled();
    }

    @Override
    public boolean isFocusOwner() {
        return jcomponent.isFocusOwner();
    }

    @Override
    public boolean isFocusable() {
        return jcomponent.isFocusable();
    }

    @Override
    public boolean isVisible() {
        return jcomponent.isVisible();
    }

    @Override
    public void repaint() {
        jcomponent.repaint();
    }

    @Override
    public void requestFocus() {
        jcomponent.requestFocus();
    }

    @Override
    public boolean requestFocusInWindow() {
        return jcomponent.requestFocusInWindow();
    }

    @Override
    public void revalidate() {
        jcomponent.revalidate();
    }

    @Override
    public void setAlignmentX(float alignment) {
        jcomponent.setAlignmentX(alignment);
    }

    @Override
    public void setAlignmentY(float alignment) {
        jcomponent.setAlignmentY(alignment);
    }

    @Override
    public void setBackground(Color background) {
        jcomponent.setBackground(background);
    }

    @Override
    public void setBorder(Border border) {
        jcomponent.setBorder(border);
    }

    @Override
    public void setCursor(Cursor cursor) {
        jcomponent.setCursor(cursor);
    }

    @Override
    public void setEnabled(boolean enabled) {
        jcomponent.setEnabled(enabled);
    }

    @Override
    public void setFocusable(boolean focusable) {
        jcomponent.setFocusable(focusable);
    }

    @Override
    public void setFont(Font font) {
        jcomponent.setFont(font);
    }

    @Override
    public void setForeground(Color foreground) {
        jcomponent.setForeground(foreground);
    }

    @Override
    public void setLocation(int left, int top) {
        jcomponent.setLocation(left, top);
    }

    @Override
    public void setMaximumSize(Dimension maximumSize) {
        jcomponent.setMaximumSize(maximumSize);
    }

    @Override
    public void setMinimumSize(Dimension minimumSize) {
        jcomponent.setMinimumSize(minimumSize);
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        jcomponent.setPreferredSize(preferredSize);
    }

    @Override
    public void setSize(int width, int height) {
        jcomponent.setSize(width, height);
    }

    @Override
    public void setSize(Dimension size) {
        jcomponent.setSize(size);
    }

    @Override
    public void setVisible(boolean visible) {
        jcomponent.setVisible(visible);
    }

    @Override
    public void transferFocus() {
        jcomponent.transferFocus();
    }

    @Override
    public void validate() {
        jcomponent.validate();
    }

    @Override
    public Component add(ZComponent component) {
        return jcomponent.add(component.getJComponent());
    }

    @Override
    public Component add(String positionForBorder, ZComponent component) {
        try {
            return jcomponent.add(positionForBorder, component.getJComponent());
        } catch (ClassCastException e) {
            Log.error("Хрень какая-то! " + positionForBorder + component.getJComponent().getClass(), e);
            return null;
        }
    }

    @Override
    public void add(ZComponent component, Object constrans) {
        jcomponent.add(component.getJComponent(), constrans);
    }

    @Override
    public void remove(int index) {
        jcomponent.remove(index);
    }

    @Override
    public void remove(ZComponent component) {
        jcomponent.remove(component.getJComponent());
    }

    @Override
    public void removeAll() {
        jcomponent.removeAll();
    }

    @Override
    public Component getComponent(int i) {
        return jcomponent.getComponent(i);
    }

    @Override
    public void doLayout() {
        jcomponent.doLayout();
    }

    @Override
    public Container getParent() {
        return jcomponent.getParent();
    }

    @Override
    public void setBounds(int left, int top, int width, int height) {
        jcomponent.setBounds(left, top, width, height);
    }

    @Override
    public void setLayout(LayoutManager layout) {
        jcomponent.setLayout(layout);
    }


    @Override
    public Graphics getGraphics() {
        return jcomponent.getGraphics();
    }


    @Override
    public void paint(Graphics graphics) {
        jcomponent.paint(graphics);
    }

    @Override
    public void setToolTipText(String text) {
        jcomponent.setToolTipText(text);
    }

}
