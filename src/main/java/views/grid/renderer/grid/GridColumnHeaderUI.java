package views.grid.renderer.grid;

import views.grid.GridColumnSet;
import views.grid.GridSwing;
import views.grid.model.GridMetadataModel;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * @author vagapova.m
 * @since 19.12.2010
 */
public class GridColumnHeaderUI extends BasicTableHeaderUI {

    private GridSwing grid;

    public GridColumnHeaderUI(GridSwing grid) {
        super();
        this.grid = grid;
    }

    public void paint(Graphics g, JComponent c) {
        Rectangle clipBounds = g.getClipBounds();
        if (header.getColumnModel() == null) {
            return;
        }
        TableColumnModel columnModel = header.getColumnModel();
        int column = 0;
        GridMetadataModel metadataModel = grid.getTableManager().getMetadataModel();
        metadataModel.setColumnMargin(columnModel);
        Dimension size = header.getSize();
        Rectangle cellRect = new Rectangle(0, 0, size.width, size.height);
        Hashtable<GridColumnSet, Rectangle> rectangleHashtable = new Hashtable<GridColumnSet, Rectangle>();

        Enumeration<TableColumn> enumeration = columnModel.getColumns();
        while (enumeration.hasMoreElements()) {
            cellRect.height = size.height;
            cellRect.y = 0;
            TableColumn aColumn = enumeration.nextElement();
            Enumeration columnSets = metadataModel.getColumnSets(aColumn);
            if (columnSets != null) {
                int groupHeight = 0;
                while (columnSets.hasMoreElements()) {
                    GridColumnSet columnSet = (GridColumnSet) columnSets.nextElement();
                    Rectangle columnSetRect = rectangleHashtable.get(columnSet);
                    if (columnSetRect == null) {
                        columnSetRect = new Rectangle(cellRect);
                        Dimension d = columnSet.getSize(header.getTable(), metadataModel);
                        columnSetRect.width = d.width;
                        columnSetRect.height = d.height;
                        rectangleHashtable.put(columnSet, columnSetRect);
                    }
                    paintCell(g, columnSetRect, columnSet);
                    groupHeight += columnSetRect.height;
                    cellRect.height = size.height - groupHeight;
                    cellRect.y = groupHeight;
                }
            }
            cellRect.width = aColumn.getWidth();// + columnMargin;
            if (cellRect.intersects(clipBounds)) {
                paintCell(g, cellRect, column);
            }
            cellRect.x += cellRect.width;
            column++;
        }
    }

    private void paintCell(Graphics g, Rectangle cellRect, int columnIndex) {
        TableColumn aColumn = header.getColumnModel().getColumn(columnIndex);
        TableCellRenderer renderer = aColumn.getHeaderRenderer();
        if (renderer == null) {
            renderer = header.getDefaultRenderer();
        }
        Component component = renderer.getTableCellRendererComponent(
                header.getTable(), aColumn.getHeaderValue(), false, false, -1, columnIndex);
        rendererPane.add(component);
        rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y,
                cellRect.width, cellRect.height, true);
    }

    private void paintCell(Graphics g, Rectangle cellRect, GridColumnSet columnSet) {
        Component component = columnSet.getRenderer().getTableCellRendererComponent(
                header.getTable(), columnSet.getTitle(), false, false, -1, -1);
        rendererPane.add(component);
        rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y,
                cellRect.width, cellRect.height, true);
    }

    private int getHeaderHeight(GridMetadataModel metadataModel) {
        int height = 0;
        TableColumnModel columnModel = header.getColumnModel();
        for (int column = 0; column < columnModel.getColumnCount(); column++) {
            TableColumn aColumn = columnModel.getColumn(column);
            TableCellRenderer renderer = aColumn.getHeaderRenderer();
            if (renderer == null) {
                renderer = header.getDefaultRenderer();
            }
            Component comp = renderer.getTableCellRendererComponent(
                    header.getTable(), aColumn.getHeaderValue(), false, false, -1, column);
            int cHeight = comp.getPreferredSize().height;
            Enumeration<GridColumnSet> enumer = metadataModel.getColumnSets(aColumn);
            if (enumer != null) {
                while (enumer.hasMoreElements()) {
                    GridColumnSet columnSet = enumer.nextElement();
                    cHeight += columnSet.getSize(header.getTable(), metadataModel).height;
                }
            }
            height = Math.max(height, cHeight);
        }
        return height;
    }

    private Dimension createHeaderSize(GridMetadataModel metadataModel, long width) {
        TableColumnModel columnModel = header.getColumnModel();
        width += columnModel.getColumnMargin() * columnModel.getColumnCount();
        if (width > Integer.MAX_VALUE) {
            width = Integer.MAX_VALUE;
        }
        return new Dimension((int) width, getHeaderHeight(metadataModel));
    }

    public Dimension getPreferredSize(JComponent c) {
        long width = 0;
        TableColumnModel columnModel = header.getColumnModel();
        GridMetadataModel metadataModel = grid.getTableManager().getMetadataModel();
        Enumeration<TableColumn> enumeration = columnModel.getColumns();
        while (enumeration.hasMoreElements()) {
            TableColumn aColumn = enumeration.nextElement();
            width = width + aColumn.getPreferredWidth();
        }
        return createHeaderSize(metadataModel, width);
    }
}
