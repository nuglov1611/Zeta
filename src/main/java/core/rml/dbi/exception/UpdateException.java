package core.rml.dbi.exception;

import java.sql.SQLException;


/*
 * ьросается при проблемах с Update
 */
public class UpdateException extends SQLException {

    public UpdateException(String msg) {
        super(msg);
    }

    public UpdateException(SQLException e) {
        super(e.getMessage(), e.getSQLState(), e.getErrorCode());
    }
}
