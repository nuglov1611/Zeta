package core.rml.ui.interfaces;

import views.grid.renderer.cross.CellSpanTable;

import javax.swing.*;
import java.awt.*;

public interface ZScrollPane extends ZComponent {

    JViewport getViewport();

    void setWheelScrollingEnabled(boolean b);

    JScrollBar getHorizontalScrollBar();

    JScrollBar getVerticalScrollBar();

    void setVerticalScrollBarPolicy(int verticalScrollbarAsNeeded);

    void setHorizontalScrollBarPolicy(int horizontalScrollbarAsNeeded);

    void setRowHeaderView(CellSpanTable rowsTable);

    void setColumnHeaderView(CellSpanTable colsTable);

    void setViewportView(JTable dataTable);

    JViewport getRowHeader();

    void setRowHeaderView(Component rowHeader);

}
