package views.grid.filter.model;

import javax.swing.*;
import java.util.*;

/**
 * @author: vagapova.m
 * @since: 30.10.2010
 */
public class HeaderFilterListModel extends AbstractListModel {

    private List<String> filterNames;

    private Map<String, List<HeaderListItem>> filterFields;

    public HeaderFilterListModel() {
        filterNames = new ArrayList<String>();
        filterFields = new HashMap<String, List<HeaderListItem>>();
    }

    @Override
    public int getSize() {
        return filterNames.size();
    }

    @Override
    public Object getElementAt(int index) {
        return filterNames.get(index);
    }

    public void add(HeaderListItem filterField) {
        String itemName = filterField.getDisplayName();
        List<HeaderListItem> list = filterFields.get(itemName);
        if (list == null) {
            list = new ArrayList<HeaderListItem>();
        }
        if (!list.contains(filterField)) {
            list.add(filterField);
            filterFields.put(itemName, list);
            if (!filterNames.contains(itemName)) {
                filterNames.add(itemName);
                Collections.sort(filterNames);
            }
        }
    }

    public boolean isEmpty() {
        return getSize() == 0;
    }

    public List<HeaderListItem> getListItemsAt(int index) {
        return filterFields.get(filterNames.get(index));
    }
}
