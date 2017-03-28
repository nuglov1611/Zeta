package core.rml.dbi;

import java.util.List;

public class VMatrix {
    Datastore ds;

    int[] columns;

    int[] directions;

    public VMatrix(Datastore ds, int[] columns, int[] directions) {
        this.ds = ds;
        this.columns = columns;
        this.directions = directions;
    }

    public Object get(int rows, int cols) {
        return ds.getValue(rows, columns[cols]);
    }

    public int getType(int col) {
        return ds.getType(columns[col]);
    }

    public List<Integer> getKeys() {
        return ds.getModel().getRowIndexes();
    }

    public int[] getDirections() {
        return directions;
    }
}
