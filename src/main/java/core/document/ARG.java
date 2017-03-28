package core.document;

import action.api.GlobalValuesObject;
import action.api.RTException;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;

public class ARG extends ArrayList<Object> implements GlobalValuesObject {

    /**
     *
     */
    private static final long serialVersionUID = 1L;


    private static final Logger log = Logger.getLogger(ARG.class);

//    private Object[]            objs;

    public ARG(Object[] o) {
        if (o != null)
            Collections.addAll(this, o);
    }

    public Object getValue() {
        return "NOTHING";
    }

    public Object getValueByName(String name) throws Exception {
        try {
            return get((int) Integer.valueOf(name).longValue());
        } catch (NumberFormatException e) {
            int i = name.indexOf('.');
            if (i != -1) {
                return ((GlobalValuesObject) get((int) Integer.valueOf(
                        name.substring(0, i)).longValue())).getValueByName(name
                        .substring(i + 1));
            } else {
                throw e;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return "NOTHING";
        }
    }

    public void setValue(Object obj) {
    }

    public void setValueByName(String name, Object obj) throws Exception {
        int i = name.indexOf('.');
        if (i != -1) {
            ((GlobalValuesObject) get((int) Integer.valueOf(
                    name.substring(0, i)).longValue())).setValueByName(name
                    .substring(i + 1), obj);
        } else {
            throw new RTException("CASTEXCEPTION", "cant set in ARGUMENTS."
                    + name);
        }
    }
}
