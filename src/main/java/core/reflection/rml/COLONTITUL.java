package core.reflection.rml;

import core.document.Document;
import core.parser.Proper;
import views.ReportHT;

/**
 * Rml-������ "����������" ������������ � Report
 */
public class COLONTITUL extends ReportHT {
    @Override
    public void init(Proper prop, Document doc) {
        super.init(prop, doc);
        if (prop.get("TYPE") == null)
            prop.put("TYPE", "TOP");
    }
}
