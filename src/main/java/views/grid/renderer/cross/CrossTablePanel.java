package views.grid.renderer.cross;

import core.rml.ui.EDTInvocationHandler;
import core.rml.ui.impl.ZPanelImpl;
import core.rml.ui.interfaces.ZPanel;
import core.rml.ui.interfaces.ZScrollPane;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;

/**
 * @author: vagapova.m
 * @since: 21.10.2010
 */
public class CrossTablePanel extends ZPanelImpl {

    private static final Logger log = Logger.getLogger(CrossTablePanel.class);

    public static CrossTablePanel create() {
        return create(new JPanel());
    }


    public static CrossTablePanel create(JPanel panel) {
        try {
            return (CrossTablePanel) java.lang.reflect.Proxy.newProxyInstance(CrossTablePanel.class.getClassLoader(),
                    new Class[]{ZPanel.class}, new EDTInvocationHandler(new CrossTablePanel(panel)));

        } catch (SecurityException e) {
            log.error("!", e);
        }
        return null;
    }


    public static CrossTablePanel create(ZPanelImpl panel) {
        try {
            return (CrossTablePanel) java.lang.reflect.Proxy.newProxyInstance(CrossTablePanel.class.getClassLoader(),
                    new Class[]{ZPanel.class}, new EDTInvocationHandler(panel));

        } catch (SecurityException e) {
            log.error("!", e);
        }
        return null;
    }


    public static CrossTablePanel create(LayoutManager layout) {
        return create(new JPanel(layout));
    }


    protected CrossTablePanel(JComponent comp) {
        super(comp);
    }


    private JTable dataTable = new JTable();

    private CellSpanTable rowsTable = new CellSpanTable();

    private CellSpanTable colsTable = new CellSpanTable();

    private ZScrollPane mainScrollPane;

//    protected CrossTablePanel() {
//        super();
//    }

    public JTable getDataTable() {
        return dataTable;
    }

    public void setDataTable(JTable dataTable) {
        this.dataTable = dataTable;
    }

    public CellSpanTable getRowsTable() {
        return rowsTable;
    }

    public void setRowsTable(CellSpanTable rowsTable) {
        this.rowsTable = rowsTable;
    }

    public CellSpanTable getColsTable() {
        return colsTable;
    }

    public void setColsTable(CellSpanTable colsTable) {
        this.colsTable = colsTable;
    }

    public void setMainScrollPane(ZScrollPane mainScrollPane) {
        this.mainScrollPane = mainScrollPane;
    }

    public ZScrollPane getMainScrollPane() {
        return mainScrollPane;
    }

    @Override
    public void repaint() {
        // repaint tables content...
        if (rowsTable != null) {
            rowsTable.repaint();
        }
        if (colsTable != null) {
            colsTable.repaint();
        }
        if (dataTable != null) {
            dataTable.repaint();
        }
        super.repaint();
    }

    @Override
    public void revalidate() {
        if (rowsTable != null) {
            rowsTable.revalidate();
        }
        if (colsTable != null) {
            colsTable.revalidate();
        }
        if (dataTable != null) {
            dataTable.revalidate();
        }
        super.revalidate();
    }
}
