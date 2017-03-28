package views;

import javax.swing.tree.DefaultMutableTreeNode;

public class TreeNodeS extends DefaultMutableTreeNode {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    core.rml.dbi.Group data_object;

    public TreeNodeS(Object s) {
        super(s);
        data_object = null;
    }

    public core.rml.dbi.Group getDataObject() {
        return data_object;
    }

    public void setDataObject(core.rml.dbi.Group d_o) {
        data_object = d_o;
    }
}
