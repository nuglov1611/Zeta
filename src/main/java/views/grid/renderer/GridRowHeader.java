package views.grid.renderer;

import javax.swing.*;
import java.util.List;

/**
 * @author: vagapova.m
 * @since: 01.11.2010
 */
public class GridRowHeader extends JList {

    private List<Integer> rowSize;

    public GridRowHeader(ListModel dataModel) {
        super(dataModel);
    }

    public void setRowSize(List<Integer> rowSize) {
        this.rowSize = rowSize;
    }

    public List<Integer> getRowSize() {
        return rowSize;
    }
}
