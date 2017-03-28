package core.connection;

import java.sql.SQLException;

public class ConnectException extends SQLException {
    public ConnectException(String msg) {
        super(msg);
    }

    public ConnectException(String msg, Throwable reason) {
        super(msg, reason);
    }
}