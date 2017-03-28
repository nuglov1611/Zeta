package views;

import core.document.Document;
import core.parser.Proper;
import core.rml.RmlObject;


public class ReportHT extends RmlObject {
    ReportForm f = null;

    public void init(Proper prop, Document doc) {
        super.init(prop, doc);
        f = new ReportForm();
        f.init(prop, doc);
    }

    public ReportForm getForm() {
        return f;
    }

    @Override
    public Object method(String method, Object arg) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
