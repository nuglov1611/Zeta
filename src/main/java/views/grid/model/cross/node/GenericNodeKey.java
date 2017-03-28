package views.grid.model.cross.node;

import java.util.Arrays;

/**
 * Key of path of nodes
 *
 * @author vagapova.m
 * @since 18.07.2010
 */
public class GenericNodeKey {

    private Object[] path = null;
    private int hashCode;

    public GenericNodeKey(Object[] path, int hashCode) {
        this.path = path;
        this.hashCode = hashCode;
    }

    public GenericNodeKey() {
        this.path = new Object[0];
        this.hashCode = 1;
    }

    public final Object[] getPath() {
        return path;
    }

    public final Object getLastNode() {
        if (path.length > 0)
            return path[path.length - 1];
        return null;
    }

    public final GenericNodeKey appendKey(Object node) {
        Object[] newpath = new Object[path.length + 1];
        System.arraycopy(path, 0, newpath, 0, path.length);
        newpath[newpath.length - 1] = node;
        return new GenericNodeKey(newpath, hashCode * node.hashCode());
    }

    public final boolean equals(Object obj) {
        if (obj == null || !(obj instanceof GenericNodeKey))
            return false;
        Object[] path2 = ((GenericNodeKey) obj).getPath();
        if (path2.length != path.length)
            return false;
        for (int i = 0; i < path2.length; i++)
            if (!path2[i].equals(path[i]))
                return false;
        return true;
    }

    @Override
    public String toString() {
        return "path=" + (path == null ? null : Arrays.asList(path));
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}
