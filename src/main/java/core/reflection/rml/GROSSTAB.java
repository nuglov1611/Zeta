package core.reflection.rml;

import core.document.Document;
import core.parser.Proper;
import core.rml.dbi.GrossTab;


/**
 */
public class GROSSTAB extends GrossTab {
    public void init(Proper prop, Document doc) {
        super.init(prop, doc);
        String query = (String) prop.get("QUERY");
        setSql(query);
        setParameters(prop.get("ROWCONDITION"), prop.get("COLUMNCONDITION"),
                prop.get("DATACONDITION"), prop.get("EVAL"));
    }
}
