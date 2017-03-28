package core.rml.ui.interfaces;

import java.awt.Component;

import javax.swing.JScrollBar;
import javax.swing.JTable;
import javax.swing.JViewport;

import views.grid.renderer.cross.CellSpanTable;

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
