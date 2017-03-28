package views.grid;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import org.apache.log4j.Logger;

import publicapi.ColumnSetAPI;
import views.grid.model.GridMetadataModel;
import core.document.Document;
import core.parser.Proper;
import core.rml.Container;
import core.rml.RmlObject;

/**
 * @author: vagapova.m
 * @since: 19.12.2010
 */
public class GridColumnSet extends RmlObject implements ColumnSetAPI{

    private static final Logger log = Logger.getLogger(GridColumnSet.class);
    
    Container container = new Container(this);

    protected int margin = 0;

    private Vector<GridColumn> columns = new Vector<GridColumn>();

    private Vector<GridColumnSet> columnSets = new Vector<GridColumnSet>();

    private String title;

    private DefaultTableCellRenderer renderer;

    public GridColumnSet() {
        this.renderer = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JTableHeader header = table.getTableHeader();
                TableCellRenderer headerRenderer = header.getDefaultRenderer();
                Component component = headerRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (header != null) {
//                    setForeground(header.getForeground());
//                    setBackground(header.getBackground());
//                    setFont(header.getFont());
                }
                if (component instanceof JLabel) {
                    ((JLabel) component).setHorizontalAlignment(JLabel.CENTER);
                }
//                component.setText((value == null) ? "" : value.toString());
//                setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                return component;
            }
        };
    }

    public void setParent(Object p) {
        for(GridColumn col : columns){
        	col.setParent(p);
        }
        for(GridColumnSet cols : columnSets){
        	cols.setParent(p);
        }
    }
    
    public void init(Proper prop, Document doc) {
    	super.init(prop, doc);
        String sp;
        if (prop == null) {
            return;
        }
        sp = (String) prop.get("TITLE");
        if (sp != null) {
            title = sp;
        }
    }

    /**
     * @param tableColumn       TableColumn
     * @param columnsCollection ColumnGroups
     * @return hierarchy collection of all sets that contains given column
     */
    public Vector<GridColumnSet> getColumnSets(GridColumn tableColumn, Vector<GridColumnSet> columnsCollection) {
        columnsCollection.addElement(this);
        if (columns.contains(tableColumn)) {
            return columnsCollection;
        }
        for (GridColumnSet columnSet : columnSets) {
            Vector<GridColumnSet> groups = columnSet.getColumnSets(tableColumn, (Vector<GridColumnSet>) columnsCollection.clone());
            if (groups != null) {
                return groups;
            }
        }
        return null;
    }

    public String getHeaderValue() {
        return title;
    }

    public Dimension getSize(JTable table, GridMetadataModel metadataModel) {
        Component comp = renderer.getTableCellRendererComponent(
                table, getHeaderValue(), false, false, -1, -1);
        int height = comp.getPreferredSize().height;
        int width = 0;
        for (GridColumn column : columns) {
            int columnModelIndex = metadataModel.getColumnModelIndex(column);
            if (columnModelIndex != -1) {
                try {
                    width += table.getColumnModel().getColumn(columnModelIndex).getWidth();
                } catch (ArrayIndexOutOfBoundsException e) {
                    width += column.getColumn().getWidth();
                }
            } else {
                width += column.getColumn().getWidth();
            }
//            width += margin;
        }
        for (GridColumnSet columnSet : columnSets) {
            width += columnSet.getSize(table, metadataModel).width;
        }
        return new Dimension(width, height);
    }

    public void setColumnMargin(int margin) {
        this.margin = margin;
        for (GridColumnSet columnSet : columnSets) {
            columnSet.setColumnMargin(margin);
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Vector<GridColumn> getColumns() {
        return columns;
    }

    public Vector<GridColumnSet> getColumnSets() {
        return columnSets;
    }

    public TableCellRenderer getRenderer() {
        return renderer;
    }

	@Override
	public Object method(String method, Object arg) throws Exception {
		return null;
	}

	@Override
	public void addChild(RmlObject child) {
	    container.addChildToCollection(child);
        if (child == null) {
            log.error("Object GridSwing cannot be created!");
            throw new Error("Object GridSwing cannot be created!");
        }
        if (child instanceof GridColumn) {
            columns.add((GridColumn) child);
        } else if (child instanceof GridColumnSet) {
            columnSets.add((GridColumnSet) child);
        }
	}

	@Override
	public RmlObject[] getChildren() {
		return container.getChildren();
	}

	@Override
	public void initChildren() {
	}

	@Override
	public Container getContainer() {
		return container;
	}

	@Override
	public boolean addChildrenAutomaticly() {
		return true;
	}
}
