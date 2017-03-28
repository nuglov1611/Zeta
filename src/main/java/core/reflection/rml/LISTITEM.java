package core.reflection.rml;

import action.calc.Nil;
import core.document.Document;
import core.parser.Proper;
import core.rml.RmlObject;
import org.apache.log4j.Logger;
import publicapi.ListItemAPI;

public class LISTITEM extends RmlObject implements ListItemAPI {
    private static final Logger log = Logger.getLogger(RmlObject.class);

    private String label = "";
    private String action = null;
    private String alias = "";

    public String toString() {
        return getLabel();
    }

    public Object method(String method, Object arg) throws Exception {
        if (method.equals("SETLABEL") && (arg instanceof String)) {
            setLabel((String) arg);
        } else if (method.equals("GETLABEL")) {
            return getLabel();
        } else if (method.equals("SETACTION")) {
            setAction((String) arg);
        } else if (method.equals("DOACTION")) {
            doAction();
        }
        return new Nil();
    }

    /**
     * @param arg
     */
    public void setAction(String arg) {
        action = arg;
    }

    /**
     * @param arg
     */
    public void setLabel(String arg) {
        label = arg;
    }

    /**
     * @return
     */
    public String getLabel() {
        return label;
    }

    public void doAction() {
        if (action != null && !action.trim().equals("")) {
            try {
                document.executeScript(action, false);
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    @Override
    public void init(Proper prop, Document doc) {
        document = doc;
        label = (String) prop.get("LABEL", "");
        action = (String) prop.get("ACTION");
        alias = (String) prop.get("ALIAS");
    }

    @Override
    public Object getValue() throws Exception {
        return this;
    }

    @Override
    public Object getValueByName(String name) throws Exception {
        return this;
    }

    @Override
    public void setValue(Object obj) throws Exception {
    }

    @Override
    public void setValueByName(String name, Object obj) throws Exception {
    }

    public void removeFromDoc() {
        try {
            document.getAliases().remove(alias);
        } catch (java.lang.NullPointerException e) {

        }
    }


}
