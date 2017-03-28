/*
 * File: EXECUTOR.java
 * 
 * Created: Fri Apr 23 12:59:06 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Swistunov
 */

package core.rml.dbi;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

import loader.ZetaProperties;

import org.apache.log4j.Logger;

import core.connection.BadPasswordException;
import core.document.Document;

public class EXECUTOR {
    protected final static Logger log = Logger.getLogger(EXECUTOR.class);
    private Document doc = null;

    public EXECUTOR(Document d) throws BadPasswordException, SQLException {
        doc = d;
    }

    public void execute(Object[] args, Hashtable<String, Object> aliases) throws SQLException, BadPasswordException{
        if (args == null) {
            return;
        }
        String query = "begin ";
        for (int i = 0; i < args.length - 1; i++) {
            query = query + args[i]/* .toString() */+ ", ";
            if (ZetaProperties.dstore_debug > 2) {
                log.debug(query);
            }
        }
        // Добаваляем последний аргумент и ";"
        query = query + args[args.length - 1] + ";";
        query = query + " end;";
        if (ZetaProperties.dstore_debug > 2) {
            log.debug(query);
        }

        Statement stmt = null;
        try {
            stmt = doc.getConnection().createStatement();
            stmt.execute(query);
            doc.getConnection().commit();
            DSCollection.repeatLocks();
        }
        catch (SQLException e) {

            log.error("", e);
            throw e;
        } 
        finally {
            try {
                stmt.close();
            }
            catch (SQLException e) {
                log.error("!", e);
            }
        }
    }
}
