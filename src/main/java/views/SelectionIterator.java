package views;

import action.calc.objects.base_iterator;


public class SelectionIterator extends base_iterator {
    Object[] data;

    public SelectionIterator(Object[] data) {
        super();
        if (data == null) {
            return;
        }
        this.data = data;
        init(data.length - 1);
    }

    public Object set_value(Object value) {
        return null;
    }

    public Object value() throws Exception {
        return data[cursor];
    }
}
