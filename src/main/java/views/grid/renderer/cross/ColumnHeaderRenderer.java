package views.grid.renderer.cross;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import views.grid.GridSwing;
import views.grid.model.cross.node.GlobalColGenericNode;

/**
 * TableCellRenderer used for column fields
 *
 * @author: vagapova.m
 * @since: 27.08.2010
 */
public class ColumnHeaderRenderer extends DefaultTableCellRenderer {

    JPanel b = new JPanel() {

        public void paint(Graphics g) {
            super.paint(g);
            if (t != null && col != -1 && row != -1) {
                GlobalColGenericNode n = parentGrid.getCrossModelManager().getColumn(col);
                if (n != null &&
                        row == n.getLevel() - 1 &&
                        row < parentGrid.getParametersAccessor().getColsSize() - 1) {
                    if (!n.isNodeExpanded()) {
                        g.drawLine(7, 11, 11, 11);
                        g.drawLine(9, 11 - 2, 9, 11 + 2);
                    } else {
                        g.drawLine(7, 11, 11, 11);
                    }
                    g.setColor(Color.lightGray);
                    g.drawRect(5, 11 - 4, 8, 8);
                }
            }
        }

    };
    JLabel l = new JLabel();
    JTable t = null;
    int col = -1;
    int row = -1;

    private GridSwing parentGrid;

    public ColumnHeaderRenderer(GridSwing parentGrid) {
        this.parentGrid = parentGrid;
        b.setBackground(new JButton().getBackground());
        b.setBorder(BorderFactory.createRaisedBevelBorder());
        b.setSize(b.getWidth(), 30);
        b.setLayout(new GridBagLayout());
        b.add(l, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
                , GridBagConstraints.NORTHWEST, GridBagConstraints.CENTER, new Insets(2, 16, 0, 0), 0, 0));
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        this.t = table;
        this.col = column;
        this.row = row;
        l.setText(value == null ? "" : value.toString());
        return b;
    }
}
