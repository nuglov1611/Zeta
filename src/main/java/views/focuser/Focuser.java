package views.focuser;

import action.api.RTException;
import core.rml.Container;
import core.rml.RmlObject;
import core.rml.VisualRmlObject;
import publicapi.FocuserAPI;
import publicapi.RmlContainerAPI;

import javax.swing.FocusManager;
import javax.swing.*;
import java.awt.*;
import java.util.Vector;

/*
 * Focuser realization class. Author: Nikolay Uglov
 */
public class Focuser extends RmlObject implements RmlContainerAPI, FocuserAPI {
    private FocusPolicy policy = new FocusPolicy();

    private Container container = new Container(this);
    private JPanel focus_root = null;

    private Component first_component;

    private Vector<FocusItem> items = new Vector<FocusItem>();

    private Vector<Component> order = new Vector<Component>();

    public static Focusable getFocusable(Component comp) {
        if (comp == null) {
            return null;
        }

        if (comp instanceof Focusable) {
            return (Focusable) comp;
        } else {
            return getFocusable(comp.getParent());
        }
    }

    public boolean accept(Component aComponent) {
        Focusable f = getFocusable(aComponent);
        Focusable f_cur = getFocusable(FocusManager.getCurrentManager()
                .getFocusOwner());
        if (f != null && order.indexOf(f) == -1) {
            return false;
        } else return !(f != null && f == f_cur);
    }

    public void setFirsFocus(Component first) {
        first_component = first;
    }

    @Override
    // Ìועמהû טםעונפויסא class_method
    public Object method(String method, Object arg) throws Exception {
        if (method.equals("FOCUS")) {
            if (arg instanceof Vector) {
                arg = ((Vector<Object>) arg).elementAt(0);
            }
            focus(arg);
        } else if (method.equals("FOCUSNEXT")) {
            focusNext();
            // FocusManager.getCurrentManager().downFocusCycle();
        } else if (method.equals("FOCUSPREVIOUS")) {
            focusPrevious();
            // FocusManager.getCurrentManager().upFocusCycle();
        } else {
            throw new RTException("HasMethodException",
                    "object FOCUSER has not method " + method);
        }
        return new Double(0);
    }

    public void focusPrevious() {
        FocusManager.getCurrentManager().focusPreviousComponent();
    }

    public void focusNext() {
        FocusManager.getCurrentManager().focusNextComponent();
    }

    public void focus(Object component) {
        if (component instanceof Focusable) {
            //KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
            ((Focusable) component).focusThis();
        }
    }

    public void activateFocusers() {
        if (items.size() > 0) {
            policy = new FocusPolicy(this);
            setOrder();
        }
        focus_root = (JPanel) document.getPanel().getJComponent();
        if (first_component != null) {
            policy.setFirstFocus(first_component);
        }
        focus_root.setFocusCycleRoot(true);
        focus_root.setFocusTraversalPolicy(policy);
        focus_root.setFocusTraversalPolicyProvider(true);
    }

    private void setOrder() {
        int focus_position = 1;
        for (FocusItem item : items) {
            RmlObject obj = document.findObject(item.getTerget());
            if (obj != null) {
                if (obj instanceof Focusable) {
                    ((Focusable) obj).setFocusPosition(focus_position);
                    order.add(((VisualRmlObject) obj).getVisualComponent().getJComponent());
                    focus_position++;
                }
            }
        }
    }

    public Component getNextComponent(Component aComponent) {
        int idx = (order.indexOf(getFocusable(aComponent)) + 1) % order.size();
        return order.get(idx);
    }

    public Component getPreviousComponent(Component aComponent) {
        int idx = order.indexOf(getFocusable(aComponent)) - 1;
        if (idx < 0) {
            idx = order.size() - 1;
        }
        return order.get(idx);
    }

    @Override
    public void addChild(RmlObject child) {
        if (child instanceof FocusItem) {
            items.add((FocusItem) child);
        }
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
}
