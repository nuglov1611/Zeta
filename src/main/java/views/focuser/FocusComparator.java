/**
 *
 */
package views.focuser;

import java.awt.*;
import java.util.Comparator;

/*
 * @author nuglov
 */
public class FocusComparator implements Comparator<Component> {
    private Comparator<? super Component> default_comparator = null;

    public FocusComparator(Comparator<? super Component> comparator) {
        default_comparator = comparator;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(Component o1, Component o2) {
        Focusable f1 = Focuser.getFocusable(o1);
        Focusable f2 = Focuser.getFocusable(o2);

        if (f1 != null && f2 != null) {
            int pos1 = f1.getFocusPosition();
            int pos2 = f2.getFocusPosition();
            if (pos1 > pos2) {
                return 1;
            } else if (pos1 < pos2) {
                return -1;
            } else {
                return 0;
            }
        } else {
            return default_comparator.compare(o1, o2);
        }

        // return 0;
    }

}
