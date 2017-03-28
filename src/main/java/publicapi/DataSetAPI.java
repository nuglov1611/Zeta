package publicapi;

import core.connection.BadPasswordException;
import proguard.annotation.Keep;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

/**
 * ��������� ��� ��������������� ������� � ��. ������������ � ������ ������� ��� ������ � ����� ������ BLOB
 *
 * @author uglov
 */
public interface DataSetAPI extends RmlObjectAPI {


    /**
     * ���������� ����� SQL-�������
     *
     * @param q ����� �������
     */
    @Keep
    void setQuery(String q);

    /**
     * ������� ��������� SQL-�������
     *
     * @return ������
     */
    @Keep
    String getQuery();

    /**
     * ������� �����
     *
     * @param row ����� ������
     */
    @Keep
    void deleteRow(int row);

    /**
     * ���������� ������� ������
     *
     * @param rowNum
     * @throws SQLException
     */
    @Keep
    void setCurRow(int rowNum) throws SQLException;

    /**
     * ��������� �������� � ��
     *
     * @throws SQLException
     * @throws FileNotFoundException
     */
    @Keep
    void update() throws SQLException, FileNotFoundException;


    /**
     * ���������� �������� � ������
     *
     * @param row    ����� ������
     * @param column ����� �������
     * @param value  �������� (���� ��� ������� Blob, �� ������ �������� ����� ���� ���� byte[] (��������������� ������ ��� ���������� � Blob);
     *               ���� ��� String, � ���� ������ ��������� ��� ������ ������� �� �����, � ������� ��������� ���� � �����)
     */
    @Keep
    void setValue(int row, int column, Object value);

    /**
     * ������� �������� �� ������
     *
     * @param row ����� ������
     * @param col ����� �������
     * @return �������� (���� ��� ������ Blob �� ��� ������������� �������� ����� byte[])
     * @throws SQLException
     * @throws IOException
     */
    @Keep
    Object getValue(int row, int col) throws SQLException, IOException;

    /**
     * ������� �������� �� ������ ������� ������
     *
     * @param col ����� �������
     * @return �������� (���� ��� ������ Blob �� ��� ������������� �������� ����� byte[])
     * @throws SQLException
     * @throws IOException
     */
    @Keep
    Object getValue(int col) throws SQLException, IOException;

    /**
     * ������� �������� �� ������ ������� ������
     *
     * @param column ����� �������
     * @return �������� (���� ��� ������ Blob �� ��� ������������� �������� ����� byte[])
     * @throws SQLException
     * @throws IOException
     */
    @Keep
    Object getValue(String column) throws SQLException, IOException;

    /**
     * �������� ������ � DataSet (��������� ������ � ��)
     *
     * @return ���-�� ������������ �����
     * @throws BadPasswordException ���� �� ������ �����/������ ��� ����������� � ��
     * @throws SQLException
     */
    @Keep
    int retrieve() throws SQLException;
}
