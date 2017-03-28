package core.connection;

import java.sql.SQLException;

public class BadPasswordException extends SQLException {
    public BadPasswordException(String msg) {
        super(msg);
    }

    public BadPasswordException(String msg, Throwable reason) {
        super(msg, reason);
    }

}
