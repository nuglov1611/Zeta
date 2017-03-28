package views.grid.filter.model;

/**
 * @author: vagapova.m
 * @since: 28.10.2010
 */
public class HeaderListItem {

    private String displayName;

    private Object value;

    public HeaderListItem(String displayName, Object value) {
        this.displayName = displayName;
        this.value = value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return displayName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HeaderListItem) {
            if (displayName == null && ((HeaderListItem) obj).getDisplayName() == null ||
                    displayName != null && displayName.equals(((HeaderListItem) obj).getDisplayName())) {
                if ((value == null && ((HeaderListItem) obj).getValue() == null) || (value != null && value.equals(((HeaderListItem) obj).getValue()))) {
                    return true;
                }
            }
        }
        return false;
    }
}
