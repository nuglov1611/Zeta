package core.reflection.rml;

import core.document.Document;
import core.document.Shortcutter;
import core.parser.Proper;
import core.rml.RmlObject;
import org.apache.log4j.Logger;
import views.UTIL;

public class SHORTCUTTER extends RmlObject implements Shortcutter {
    private static final Logger log = Logger.getLogger(SHORTCUTTER.class);


    private String exp = null;

    public void init(Proper prop, Document doc) {
        super.init(prop, doc);
        exp = (String) prop.get("EVENT");
        String shrt = (String) prop.get("SHORTCUT");
        if (shrt != null)
            try {
                String[] ar = UTIL.parseDep(shrt);
                for (String element : ar)
                    doc.addShortcut(element, this);
            } catch (Exception e) {
                log.error("", e);
            }
    }

    public void processShortcut() {
        try {
            document.executeScript(exp, false);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Override
    public Object method(String method, Object arg) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

}
