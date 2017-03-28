package views;

import core.document.Document;
import core.parser.Proper;
import core.rml.VisualRmlObject;
import core.rml.ui.impl.ZTextAreaImpl;
import core.rml.ui.interfaces.ZComponent;
import core.rml.ui.interfaces.ZTextArea;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicBorders;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class BaseEditor extends VisualRmlObject implements KeyListener, MouseListener {

    /**
     *
     */

    private ZTextArea editor = ZTextAreaImpl.create();
    String exp;

    JPopupMenu pm = new JPopupMenu();

    Editor parent = null;

    public BaseEditor() {
        super();
    }

    public void init(Proper prop, Document doc) {
        super.init(prop, doc);
        String sp;
        if (prop == null) {
            return;
        }

        sp = (String) prop.get("EDITABLE");
        if (sp != null) {
            setEditable(sp);
        }


        exp = (String) prop.get("EXP");

        sp = (String) prop.get("VALUE");
        if (sp != null) {
            editor.setText(sp);
        }
    }

    void setEditable(String ed) {
        if (ed.equals("READONLY")) {
            editor.setEditable(false);
        } else {
            editor.setEditable(true);
        }
    }

    @Override
    public void focusThis() {
        editor.requestFocus();
    }

    @Override
    public ZComponent getVisualComponent() {
        return editor;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_E && e.isControlDown()) {
            if (exp != null) {
                try {
                    document.executeScript(exp, false);
                } catch (Exception ex) {
                    System.out
                            .println("views.BaseEditor::processKeyEvent : "
                                    + ex);
                    ex.printStackTrace();
                }
                return;
            }
        }
        if (parent != null) {
            parent.processShortcut(e.getKeyCode(), e.getModifiers());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.isPopupTrigger()) {
            if (parent != null) {
                JPopupMenu pm = parent.getPopupMenu();
                if (pm != null) {
                    pm.show(parent.getVisualComponent().getJComponent(), e.getX(), e.getY());
                }
            }
            e.consume();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    public String getText() {
        return editor.getText();
    }

    public void setText(String arg) {
        editor.setText(arg);
    }

    @Override
    protected Border getDefaultBorder() {
        return BasicBorders.getTextFieldBorder();
    }
}
