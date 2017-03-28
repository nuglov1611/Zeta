package core.rml.dbi;

public interface DataTree {
    int getGroupDimension(int[] rowid);

    Object getGroupValue(int[] rowid);

    Group getRoot();

    void resolveAllGroups();

}
