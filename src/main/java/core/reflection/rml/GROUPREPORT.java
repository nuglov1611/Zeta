/*
 * File: GROUPTREPORT.java
 * 
 * Created: Thu Mar 18 15:17:47 1999
 * 
 * Copyright(c) by Alexey Swistunow
 */
package core.reflection.rml;

import loader.ZetaProperties;

import org.apache.log4j.Logger;

import core.document.Document;
import core.parser.Proper;
import core.rml.dbi.GroupReport;


/**
 */
public class GROUPREPORT extends GroupReport {
    private static final Logger log = Logger.getLogger(GROUPREPORT.class);

    public void init(Proper prop, Document doc) {
    	super.init(prop, doc);
        if (ZetaProperties.dstore_debug > 0)
            log.debug("rml.GROUPREPORT.doParsing called");

        setReadOnly(true);

        String str = (String) prop.get("QUERY");
        if (str != null)
            setSql(str);
        str = (String) prop.get("UNIQUE");
        if (str != null)
            setUnique(str);
        str = (String) prop.get("UPDATEABLE");
        if (str != null)
            setUpdateable(str);
        str = (String) prop.get("LINKS");
        if (str != null)
            setLinks(str);
        str = (String) prop.get("DEFAULT");
        if (str != null)
            setDefaults(str);
        String head = (String) prop.get("HEAD");
        if (head == null || head.compareTo("NO") == 0)
            setHead(false);
        else
            setHead(true);
        ;

        str = (String) prop.get("GROUPING");
        String str2 = (String) prop.get("SORTING");
        String str3 = (String) prop.get("TREEPARAM");
        if (str != null && str2 != null) {
            setParameters(str, str2, str3);
            if (ZetaProperties.dstore_debug > 0)
                log.debug("rml.GROUPREPORT.doParsing calling resolveAllGroups");
        }

        str = (String) prop.get("SORTORDER");
        if (str != null)
            setSortOrder(str);
    }
}
