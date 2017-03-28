package core.rml.dbi;

import java.sql.SQLException;

/*
 * ��������� ��� ��������� � Update
 */
public class UpdateException extends SQLException {
    /**
     * ���� ������ � ������� ��������� ������
     */
    int badRow = -1;

    /**
     * ������, � ������� ��������� ������
     */
    int ds     = -1;

    UpdateException(String msg, int br) {
        super(msg);
        this.badRow = br;
    }

    UpdateException(String msg, int br, int DS) {
        super(msg);
        this.badRow = br;
        this.ds = DS;
    }

    UpdateException(SQLException e, int br, int DS) {
        super(e.getMessage(), e.getSQLState(), e.getErrorCode());
        this.badRow = br;
        this.ds = DS;
        // this.error = e;
    }

    UpdateException(SQLException e, int br) {
        super(e.getMessage(), e.getSQLState(), e.getErrorCode());
        this.badRow = br;

        // this.error = e;
    }

    /**
     * �������� ���� ������ � ������� ��������� ������
     */
    public int getBadKey() {
        return badRow;
    }

    /**
     * �������� ����� �������, � ������� ��������� ������
     */
    public int getBadDATASTORE() {
        return ds;
    }

}
