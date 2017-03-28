package core.rml.dbi;

import org.apache.log4j.Logger;

import java.util.Vector;

/**
 * User: vagapova.m
 * Date: 19.07.2010
 */
public class FilterManager {
    private static final Logger log = Logger
            .getLogger(FilterManager.class);

    public FilterManager() {
    }

    public Datastore createFilter(Datastore ownerDatastore, Vector<Integer> rowIndexes) {
        Datastore ds = null;
        if (ownerDatastore.getParentDatastore() == null) {
            ds = ownerDatastore;
        } else {
            ds = ownerDatastore.getParentDatastore();
        }
        DatastoreModel dsModel = ds.getModel();
        DatastoreModel filteredModel = new DatastoreModel();
        filteredModel.copyMetadata(dsModel);
        for (Integer rowIndex : dsModel.getRowIndexes()) {
            if (rowIndexes.contains(rowIndex)) {
                filteredModel.addRow(dsModel.getRow(rowIndex));
            }
        }
        Datastore rep = new Datastore(ds);
        rep.setModel(filteredModel);
        rep.setFiltered(true);
        return rep;
    }

}
