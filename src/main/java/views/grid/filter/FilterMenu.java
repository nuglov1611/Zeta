package views.grid.filter;

import java.awt.Component;

import javax.swing.Action;
import javax.swing.JList;
import javax.swing.JPopupMenu;

import views.grid.GridColumn;
import views.grid.GridSwing;
import views.grid.action.DialogFilterAction;
import views.grid.action.SelectAllFilterAction;
import views.grid.filter.model.HeaderFilterListModel;
import views.grid.filter.model.HeaderListItem;
import views.grid.listener.GridHeaderPopupMouseListener;
import core.rml.ui.impl.ZScrollPaneImpl;

/**
 * @author: vagapova.m
 * @since: 27.10.2010
 */
public class FilterMenu extends JPopupMenu {

    private GridHeaderPopupMouseListener headerPopupMouseListener;

    private GridSwing grid;

    private JList list;

    private SelectAllFilterAction selectAllAction;

    private Action dialogFilterAction;

    public FilterMenu(GridSwing grid) {
        this.grid = grid;
        init();
    }

    private void init() {
        selectAllAction = new SelectAllFilterAction(grid, "Все");
        dialogFilterAction = new DialogFilterAction(grid, "Задать фильтр");
        headerPopupMouseListener = new GridHeaderPopupMouseListener(grid);
        add(selectAllAction);
        add(dialogFilterAction);
        addSeparator();
        list = new JList();
        list.addMouseMotionListener(headerPopupMouseListener);
        list.addMouseListener(headerPopupMouseListener);
        add(ZScrollPaneImpl.create(list).getJComponent());
    }

    public void createFilterModel(int columnIndex) {
        HeaderFilterListModel listModel = new HeaderFilterListModel();
        Integer modelIndex = grid.getTableManager().convertColumnIndexToModel(columnIndex);
        GridColumn column = grid.getVColumn(modelIndex);
        list.setName(modelIndex.toString());
        int rowCount = grid.getTableManager().getRowCount();
        for (int i = 0; i < rowCount; i++) {
            Object value = grid.getTableManager().getValueAt(i, columnIndex);
            String displayName = "";
                try {
                    displayName = column.valueToString(value);
                } catch (Exception ignored) {
                }
            if (displayName == null || "".equals(displayName)) {
                displayName = "<Пусто>";
            }

            HeaderListItem nextItem = new HeaderListItem(displayName, value);
            listModel.add(nextItem);
        }
        list.setModel(listModel);
        if (!listModel.isEmpty()) {
            list.ensureIndexIsVisible(0);
        }
    }

    @Override
    public void show(Component invoker, int x, int y) {
        super.show(invoker, x, y);
    }
}
