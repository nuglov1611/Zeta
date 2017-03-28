package views.grid.model.cross;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * CrossDataModel contains data cell
 *
 * @author: mvagapova
 * @since: 20/07/2010
 */
public class CrossDataModel extends AbstractTableModel {

    private ArrayList<Object[]> dataFields;

    public CrossDataModel() {
        dataFields = new ArrayList<Object[]>();
    }

    public ArrayList<Object[]> getDataFields() {
        return dataFields;
    }

    public void setDataFields(ArrayList<Object[]> dataFields) {
        this.dataFields = dataFields;
    }

    public int getColumnCount() {
        if (dataFields.size() > 0)
            return (dataFields.get(0)).length;
        else
            return 0;
    }


    public int getRowCount() {
        return dataFields.size();
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        return (dataFields.get(rowIndex))[columnIndex];
    }

}
