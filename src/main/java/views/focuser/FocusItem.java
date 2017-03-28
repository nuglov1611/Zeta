package views.focuser;

import core.document.Document;
import core.parser.Proper;
import core.rml.RmlObject;
import publicapi.FocusItemAPI;

public class FocusItem extends RmlObject implements FocusItemAPI {
    protected int number = 0;

    protected String target = "";

    public void init(Proper prop, Document doc) {
        super.init(prop, doc);
        target = (String) prop.get("TARGET");
    }

    public String getTerget() {
        return target;
    }

    @Override
    public Object method(String method, Object arg) throws Exception {
        return null;
    }
}
