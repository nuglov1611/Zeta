package views.focuser;

import java.awt.Component;
import java.awt.Container;

import javax.swing.LayoutFocusTraversalPolicy;

public class FocusPolicy extends LayoutFocusTraversalPolicy {
    Component first_focus = null;

    Component last_focus  = null;

    Focuser   focuser     = null;

    public FocusPolicy() {
        super();
    }

    protected boolean accept(Component aComponent) {
        if (focuser == null) {
            return super.accept(aComponent);
        }

        return (super.accept(aComponent) && focuser.accept(aComponent));
    }

    public FocusPolicy(Focuser focuser) {
        super();
        // super.setImplicitDownCycleTraversal(false);
        this.setComparator(new FocusComparator(super.getComparator()));
        this.focuser = focuser;
    }

    public void setFirstFocus(Component comp) {
        first_focus = comp;
    }

    public void setLastFocus(Component comp) {
        last_focus = comp;
    }

    public Component getDefaultComponent(Container focusCycleRoot) {

        if (first_focus != null) {
            return first_focus;
        }
        else {
            return super.getDefaultComponent(focusCycleRoot);
        }
    }

    public Component getLastComponent(Container focusCycleRoot) {
        if (last_focus != null) {
            return last_focus;
        }
        else {
            return super.getLastComponent(focusCycleRoot);
        }
    }

    public Component getComponentAfter(Container focusCycleRoot,
            Component aComponent) {
        if (focuser != null) {
            return focuser.getNextComponent(aComponent);
        }
        else {
            return super.getComponentAfter(focusCycleRoot, aComponent);
        }
    }

    public Component getComponentBefore(Container focusCycleRoot,
            Component aComponent) {
        if (focuser != null) {
            return focuser.getPreviousComponent(aComponent);
        }
        else {
            return super.getComponentBefore(focusCycleRoot, aComponent);
        }
    }
}
