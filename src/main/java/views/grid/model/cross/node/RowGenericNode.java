package views.grid.model.cross.node;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Row field node: contains row field nodes and a link to the col fields hierarchy;
 *
 * @author: vagapova.m
 * @since: 18.07.2010
 */
public class RowGenericNode {

    /**
     * collection of pairs <col fields path,ColGenericNode>
     */
    private HashMap<GenericNodeKey, ColGenericNode> vtreeNodes = new HashMap<GenericNodeKey, ColGenericNode>();

    /**
     * list of GenericNode objects
     */
    private ArrayList<RowGenericNode> childrenNodes = new ArrayList<RowGenericNode>();

    /**
     * flag used to define if current node is expanded
     */
    private boolean nodeExpanded = true;

    /**
     * node value
     */
    private Object value = null;

    /**
     * this is a root node
     */
    private boolean rootNode = false;

    /**
     * depth level
     */
    private int level = 0;

    /**
     * parent node of column fields list
     */
    private ColGenericNode colsParentNode = null;

    /**
     * node identifier
     */
    private GenericNodeKey hpath = null;

    public RowGenericNode() {
        rootNode = true;
    }

    /**
     * @param hpath node identifier, based on its nodes path
     */
    public RowGenericNode(GenericNodeKey hpath) {
        this.hpath = hpath;
        this.value = hpath.getLastNode();
    }

    /**
     * Add a child node to this.
     *
     * @param childNode GenericNode to add
     */
    public final void add(RowGenericNode childNode) {
        childNode.setLevel(level + 1);
        boolean added = false;
        for (int i = 0; i < childrenNodes.size(); i++)
            if (isLessThan(
                    childNode.getValue(),
                    (childrenNodes.get(i)).getValue()
            )) {
                childrenNodes.add(i, childNode);
                added = true;
                break;
            }
        if (!added)
            childrenNodes.add(childNode);
    }

    private boolean isLessThan(Object o1, Object o2) {
        if (o1 == null || o2 == null)
            return true;
        if (o1 instanceof Number)
            return ((Number) o1).doubleValue() < ((Number) o2).doubleValue();
        else if (o1 instanceof java.util.Date)
            return ((java.util.Date) o1).compareTo((java.util.Date) o2) < 0;
        else
            return o1.toString().compareTo(o2.toString()) < 0;
    }

    /**
     * Add a child node to this.
     *
     * @param childNode GenericNode to add
     */
    public final void remove(RowGenericNode childNode) {
        childrenNodes.remove(childNode);
    }

    /**
     * @return collection of pairs <col fields path,ColGenericNode>
     */
    public final HashMap<GenericNodeKey, ColGenericNode> getVtreeNodes() {
        return vtreeNodes;
    }

    /**
     * @return children number
     */
    public final int getChildrenCount() {
        return childrenNodes.size();
    }

    /**
     * @param index children index
     * @return children
     */
    public final RowGenericNode getChildren(int index) {
        return childrenNodes.get(index);
    }

    /**
     * @return define if current node is expanded
     */
    public final boolean isNodeExpanded() {
        return nodeExpanded;
    }

    /**
     * Define if current node is expanded.
     *
     * @param nodeExpanded define if current node is expanded
     */
    public final void setNodeExpanded(boolean nodeExpanded) {
        this.nodeExpanded = nodeExpanded;
    }

    public final boolean equals(Object obj) {
        return !(obj == null || !(obj instanceof RowGenericNode)) && ((RowGenericNode) obj).hpath.equals(hpath);
    }

    public final int hashCode() {
        return hpath.hashCode();
    }

    /**
     * @return node value
     */
    public final Object getValue() {
        return value;
    }

    /**
     * @return true if this is a root node
     */
    public final boolean isRootNode() {
        return rootNode;
    }

    /**
     * @return depth level
     */
    public final int getLevel() {
        return level;
    }

    /**
     * Set the depth level.
     *
     * @param level depth level
     */
    public final void setLevel(int level) {
        this.level = level;
    }

    /**
     * @return parent node of column fields list
     */
    public final ColGenericNode getColsParentNode() {
        return colsParentNode;
    }

    /**
     * Set the parent node of column fields list.
     *
     * @param colsParentNode parent node of column fields list
     */
    public final void setColsParentNode(ColGenericNode colsParentNode) {
        this.colsParentNode = colsParentNode;
    }
}
