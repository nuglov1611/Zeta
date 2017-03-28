package core.rml.dbi;

import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;

public class GrossTab extends Datastore {
    private static final Logger       log       = Logger
                                                        .getLogger(GrossTab.class);

    int                               rowCr;

    int                               colCr;

    int                               dataCr;

    int                               eval      = 1;

    Vector<Object>                    rowData   = new Vector<Object>();

    Vector<Object>                    colData   = new Vector<Object>();

    Hashtable<String, Vector<Object>> groupData = new Hashtable<String, Vector<Object>>();

    int                               sortRows  = 1;

    int                               sortCols  = 1;

    public void build() {
        for (int i = 0; i < super.getRowCount(); i++) {
            log.debug("i=" + i);
            Object rd = super.getValue(i, rowCr).toString().trim();
            Object cd = super.getValue(i, colCr).toString().trim();
            log.debug(rd + " " + cd);
            Object data = super.getValue(i, dataCr);
            if (data == null) {
                data = new Double(0);
            }
            if (!rowData.contains(rd)) {
                rowData.addElement(rd);
            }
            if (!colData.contains(cd)) {
                colData.addElement(cd);
            }
            String key = rd + "#" + cd;
            Vector<Object> mas = groupData.get(key);
            try {
                if (mas == null) {
                    Vector<Object> array = new Vector<Object>();
                    array.addElement(data);
                    groupData.put(key, array);
                }
                else {
                    mas.addElement(data);
                }
            }
            catch (Exception e) {
                log.error("Shit happens", e);
            }
        }
        int compSize = 0;
        if (computeColumn != null) {
            compSize = computeColumn.size();
        }

        model = new DatastoreModel();
        int countColumns = colData.size() + compSize + 1;
        int tpr = getType(rowCr);
        try {
            QSort.setFCompare(new FCompareVector());

            if (sortRows == 1) {
                QSort.QuickSort(rowData, 0, rowData.size() - 1);
            }
            if (sortCols == 1) {
                QSort.QuickSort(colData, 0, colData.size() - 1);
            }
        }
        catch (Exception e) {
            log.error("Shit happens", e);
        }

        for (int i = 0; i < countColumns; i++) {
            model.addColumnType(i, java.sql.Types.DOUBLE);
        }
        model.setColumnType(0, tpr);
        model.addColumnName(0, "******");
        for (int j = 0; j < colData.size(); j++) {
            model.addColumnName(j+1, colData.elementAt(j).toString()); // colName[0] =
            // "------";
        }
    }

    public void setParameters(Object rc, Object cc, Object dc, Object evl) {
        try {
            rowCr = ((Integer) rc).intValue();
            colCr = ((Integer) cc).intValue();
            dataCr = ((Integer) dc).intValue();
            eval = ((Integer) evl).intValue();
        }
        catch (Exception e) {
            log
                    .error(
                            "in GrossTab MUST be set parameters: \n rowcondition,columncondition,datacondition",
                            e);
        }
    }

    @Override
    public int retrieve() {
        try {
            colData.removeAllElements();
            rowData.removeAllElements();
            groupData.clear();
            super.retrieve();
        }
        catch (Exception e) {
            log.error("Shit happens", e);
        }
        build();
        eval(eval);
        return 1;
    }

    public void eval(int eval) {
        switch (eval) {
        case 1:
            do_summ();
            break;
        case 2:
            do_count();
            break;
        default:
            do_count();
        }
    }

    public void do_summ() {
        for (int row = 0; row < rowData.size(); row++) {
            int modelRowIndex = model.addRow();
            model.addValue(modelRowIndex, rowData.elementAt(row));
            for (int col = 0; col < colData.size(); col++) {
                String key = rowData.elementAt(row).toString().trim() + "#"
                        + colData.elementAt(col).toString().trim();
                Vector<Object> v = groupData.get(key);
                double summ = 0;
                if (v != null) {
                    for (int i = 0; i < v.size(); i++) {
                        summ = summ + (Double) v.elementAt(i);
                    }
                }
                model.addValue(modelRowIndex, summ);
            }
        }
        groupData = new Hashtable<String, Vector<Object>>();
    }

    public void do_count() {
        for (int row = 0; row < rowData.size(); row++) {
            int modelRowIndex = model.addRow();
            model.addValue(modelRowIndex, rowData.elementAt(row));
            for (int col = 0; col < colData.size(); col++) {
                Vector<Object> v = groupData.get(rowData.elementAt(row) + "#"
                        + colData.elementAt(col));
                if (v != null) {
                    model.addValue(modelRowIndex, (double) v.size());
                }
                else {
                    model.addValue(modelRowIndex, new Double(0));
                }
            }
        }
        groupData = new Hashtable<String, Vector<Object>>();
    }

}
