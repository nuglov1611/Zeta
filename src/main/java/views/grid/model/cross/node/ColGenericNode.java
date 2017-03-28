package views.grid.model.cross.node;

import java.util.ArrayList;

import views.grid.model.cross.functions.GenericFunction;

/**
 * Column field node: contains its children nodes
 *
 * @author vagapova.m
 * @since 20.07.2010
 */
public class ColGenericNode {

    /**
     * node identifier, i.e. the nodes path
     */
    private GenericNodeKey vpath = null;

    /**
     * list of GenericNode objects
     */
    private ArrayList childrenNodes = new ArrayList();

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

  /** data field values */
  private GenericFunction[] gf = new GenericFunction[0];

    public ColGenericNode() {
        rootNode = true;
    }


  /**
   * @param userObject object stored inside this
   */
  public ColGenericNode(GenericNodeKey vpath, GenericFunction[] gf) {
    this.vpath = vpath;
    this.value = vpath.getLastNode();
    this.gf = gf;
  }

    /**
     * Add a child node to this.
     *
     * @param childNode GenericNode to add
     */
    public final void add(ColGenericNode childNode) {
        childNode.setLevel(level + 1);
        childrenNodes.add(childNode);
    }


  /**
   * @return object stored inside this
   */
  public final GenericFunction[] getGenericFunctions() {
    return gf;
  }

    /**
     * Add a child node to this.
     *
     * @param childNode GenericNode to add
     */
    public final void remove(ColGenericNode childNode) {
        childrenNodes.remove(childNode);
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
    public final ColGenericNode getChildren(int index) {
        return (ColGenericNode) childrenNodes.get(index);
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
        if (obj == null || !(obj instanceof ColGenericNode))
            return false;
        return ((ColGenericNode) obj).vpath.equals(vpath);
    }


    public final int hashCode() {
        return vpath.hashCode();
    }


    /**
     * @return node value
     */
    public final Object getValue() {
        return value;
    }


    /**
     * @return <code>true</code> if this is a root node
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
}
