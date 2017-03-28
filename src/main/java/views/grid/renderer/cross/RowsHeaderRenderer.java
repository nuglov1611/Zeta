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
import views.grid.model.cross.node.RowGenericNode;

/**
 * TableCellRenderer used for row fields.
 *
 * @author: vagapova.m
 * @since: 27.08.2010
 */
public class RowsHeaderRenderer extends DefaultTableCellRenderer {

    JPanel buttonPanel = new JPanel() {

        public void paint(Graphics g) {
            super.paint(g);
            if (table != null && col != -1 && row != -1) {
                RowGenericNode n = parentGrid.getCrossModelManager().getRow(row);
                if (n != null &&
                        col == n.getLevel() - 1 &&
                        col < parentGrid.getParametersAccessor().getRowsSize() - 1) {
                    if (!n.isNodeExpanded()) {
                        g.drawLine(7, 9, 11, 9);
                        g.drawLine(9, 9 - 2, 9, 9 + 2);
                    } else {
                        g.drawLine(7, 9, 11, 9);
                    }
                    g.setColor(Color.lightGray);
                    g.drawRect(5, 9 - 4, 8, 8);
                }
            }
        }

    };
    JLabel currentLabel = new JLabel();
    JTable table = null;
    int col = -1;
    int row = -1;

    public static String convertToMultiline(String orig)
    {
        return "<html>" + orig.replaceAll("\n", "<br>") + "</html>";
    }

    private GridSwing parentGrid;

    public RowsHeaderRenderer(GridSwing parentGrid) {
        this.parentGrid = parentGrid;
        buttonPanel.setBackground(new JButton().getBackground());
        buttonPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        buttonPanel.setSize(buttonPanel.getWidth(), 30);
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.add(currentLabel, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
                , GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        this.table = table;
        this.col = column;
        this.row = row;
        currentLabel.setText(convertToMultiline(value == null ? "" : value.toString()));
        return buttonPanel;
    }

}
