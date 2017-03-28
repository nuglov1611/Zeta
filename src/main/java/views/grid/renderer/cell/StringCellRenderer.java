package views.grid.renderer.cell;

import core.rml.RmlConstants;
import views.grid.GridColumn;
import views.grid.manager.GridTableManager;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.*;
import java.awt.*;

public class StringCellRenderer implements TableCellRenderer {

    private GridTableManager tableManager;

    JTextField onelineCell;

    JTextArea multilineCell;

    public StringCellRenderer(GridTableManager tableManager) {
        super();
        this.tableManager = tableManager;
        onelineCell = new JTextField();
        multilineCell = new JTextArea();
        multilineCell.setLineWrap(true);
        multilineCell.setEditable(false);
        multilineCell.setWrapStyleWord(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        column = tableManager.convertColumnIndexToModel(column);
        GridColumn currColumn = tableManager.getVColumn(column);
        boolean tableMultine = tableManager.getParent().getBooleanProperty(RmlConstants.MULTILINE);
        boolean columnMultiline = currColumn.isMultiline();

        String halignment = currColumn.getStringProperty(RmlConstants.HALIGNMENT);
        if (tableMultine || columnMultiline) {
            initComponent(multilineCell, table, value, isSelected, hasFocus, row, column);
            Style style = ((StyledDocument) (multilineCell).getDocument()).getStyle(StyleContext.DEFAULT_STYLE);
            if ("LEFT".equals(halignment.toUpperCase())) {
                StyleConstants.setAlignment(style, StyleConstants.ALIGN_LEFT);
            } else if ("CENTER".equals(halignment.toUpperCase())) {
                StyleConstants.setAlignment(style, StyleConstants.ALIGN_CENTER);
            } else if ("RIGHT".equals(halignment.toUpperCase())) {
                StyleConstants.setAlignment(style, StyleConstants.ALIGN_RIGHT);
            }
            return multilineCell;
        } else {
            initComponent(onelineCell, table, value, isSelected, hasFocus, row, column);
            if ("LEFT".equals(halignment.toUpperCase())) {
                onelineCell.setHorizontalAlignment(JTextField.LEFT);
            } else if ("CENTER".equals(halignment.toUpperCase())) {
                onelineCell.setHorizontalAlignment(JTextField.CENTER);
            } else if ("RIGHT".equals(halignment.toUpperCase())) {
                onelineCell.setHorizontalAlignment(JTextField.RIGHT);
            }
            return onelineCell;
        }
    }

    private void initComponent(JTextComponent textComponent, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Font font = tableManager.getUIManager().getFont(row, column);
        if (font != null) {
            textComponent.setFont(font);
        }
        if (isSelected) {
            textComponent.setForeground(table.getSelectionForeground());
            textComponent.setBackground(table.getSelectionBackground());
        } else {
            Color fontColor = tableManager.getUIManager().getFgColor(row, column);
            if (fontColor != null) {
                textComponent.setForeground(fontColor);
            }
            Color bgColor = tableManager.getUIManager().getBgColor(row, column);
            if (bgColor != null) {
                textComponent.setBackground(bgColor);
            }
        }

        if (hasFocus && !tableManager.isRowSelected()) {
            Border border = null;
            if (isSelected) {
//                border = UIManager.getBorder("Table.focusSelectedCellHighlightBorder");

                border = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.gray, Color.white, Color.lightGray), new LineBorder(new Color(204, 204, 204), 3, true));

//                border = BorderFactory.createLineBorder(parentGrid.getTbsbg_color(), 2);
            }
            if (border == null) {
                border = UIManager.getBorder("Table.focusCellHighlightBorder");
            }
            textComponent.setBorder(border);
        } else {
            textComponent.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        }
        if (value != null) {
            textComponent.setText(String.valueOf(value));
        } else {
            textComponent.setText("");
        }
    }
}