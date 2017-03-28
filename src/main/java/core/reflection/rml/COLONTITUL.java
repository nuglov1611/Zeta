package core.reflection.rml;

import views.ReportHT;
import core.document.Document;
import core.parser.Proper;

/**
 * Rml-объект "колонтитул" используется в Report
 *
 */
public class COLONTITUL extends ReportHT {
    @Override
    public void init(Proper prop, Document doc) {
    	super.init(prop, doc);
        if (prop.get("TYPE") == null)
            prop.put("TYPE", "TOP");
    }
}
