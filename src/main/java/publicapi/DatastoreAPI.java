package publicapi;

import action.api.RTException;
import core.connection.BadPasswordException;
import core.connection.ConnectException;
import core.rml.dbi.Datastore;
import proguard.annotation.Keep;

import java.sql.SQLException;

/**
 * ������ ��� ������ � ��
 * <p>
 * ��������:
 * - editable - ���������� �������������� ������.
 * - "Yes" - �������������� ��������
 * - "No" - �������������� �� ��������
 * - �������� ��-��������� "No"
 * - query - ������ � ��, ����� ��������� ����������� ������� [[calc]].
 * - unique -
 * - updateable - ����������� ���������� ������ � ��.
 * - "Yes" - ��������
 * - "No" - �� ��������
 * - �������� ��-��������� "No"
 * - links - �����-�� �������� :)
 * - defaults - ����������-���������
 * - head - �����-�� �������� :)
 * - "Yes"
 * - "No"
 * - �������� ��-��������� "No"
 * - Actions - �����-�� �������� :)
 * - insDep - �����-�� �������� :)
 * - upDep - �����-�� �������� :)
 * - delDep - �����-�� �������� :)
 * - selAction - �����-�� �������� :)
 * - sortOrder - �����-�� �������� :)
 * - defRow - �����-�� �������� :)
 *
 * @code {datastore
 * alias=ds
 * query = "select '�����' col1 from dual"
 * }
 * @endcode
 */
public interface DatastoreAPI extends RmlObjectAPI, RmlContainerAPI, RetrieveableAPI {

    /**
     * �������������,�������� �� ������ ������ ��� ������
     *
     * @param readOnly - ������ ������, ���� true
     */
    @Keep
    void setReadOnly(boolean readOnly);

    /**
     * filed = ~alias~,
     *
     * @internal
     */
    @Keep
    void setDefaults(String def);

    /**
     * ������� ��������� ������
     */
    @Keep
    void clear();

    /**
     * ������� ������
     *
     * @param row - ������ ������
     */
    @Keep
    void delRow(int row);

    /**
     * ������� ����� ������ � ���������� �� ������ ����� ��������� ������
     * ���������� ������
     *
     * @return ����� ����� ������
     */
    @Keep
    int newRow();

    /**
     * ���������� �������� ������� ������� �� ������� �����
     *
     * @param column - alias (��� target) �������
     * @throws RTException
     */
    @Keep
    Object getValue(String column) throws RTException;

    /**
     * ���������� �������� ������� ������� �� ������� �����
     *
     * @param column - ����� �������
     * @throws RTException
     */
    @Keep
    Object getValue(int column) throws RTException;

    /**
     * ���������� �������� ������� ������� �� ������ � �������� row
     *
     * @param row    - ����� ������
     * @param column - ����� �������
     */
    @Keep
    Object getValue(int row, int column);

    /**
     * ���������� �������� ������� ������� �� ������ � �������� row
     *
     * @param row    - ����� ������
     * @param column - alias (��� target) �������
     * @throws RTException
     */
    @Keep
    Object getValue(int row, String column);

    /**
     * ������������� �������� ������� ������� ��� ������� �����
     *
     * @param column - alias (��� target) �������
     * @param value  - ��������
     */
    @Keep
    void setValue(String column, Object value);

    /**
     * ������������� �������� ������� ������� ��� ������� �����
     *
     * @param column - ����� �������
     * @param value  - ��������
     */
    @Keep
    void setValue(int column, Object value);

    /**
     * ������������� �������� ������� ������� ��� ������ � �������� row
     *
     * @param row
     * @param column
     * @param value
     */
    @Keep
    void setValue(int row, String column, Object value);

    /**
     * �������� ����� �������
     *
     * @param typeCol - ��� ������� (java.sql.Types)
     * @return ��� �������
     */
    @Keep
    String addColumn(int typeCol);

    /**
     * �������������� ���������� ����������� ������ � ��
     *
     * @throws BadPasswordException
     * @throws SQLException
     */
    @Keep
    void update() throws SQLException;

    /**
     * ��������� ������ ������� �� ����.������ ����� ������ ���������� �����
     * ������ ������ �������, ������������ � �����
     */
    @Keep
    int retrieve() throws Exception;

    /**
     * ��������� �������, ��� ������� ������
     *
     * @param sql - ����� ������� (����� ��������� ������)
     */
    @Keep
    void setSql(String sql);

    /**
     * ���������� ����� �����c�
     */
    @Keep
    String getSql();

    /**
     * ���������� ������� ������ ��������� ����� ���������� � 0
     */
    @Keep
    int getCurRow();

    /**
     * ������������� ������� ������ ��������� ����� ���������� � 0
     *
     * @param row - ����� ������
     */
    @Keep
    void setCurrentRow(int row);

    /**
     * ��������� � ��������� �� ������� ������
     */
    @Keep
    void nextRow();

    /**
     * ���������� ����� �������� ��������������� ������
     */
    @Keep
    String[] getNames();

    /**
     * ���������� ���������� �������� ��������������� ������
     */
    @Keep
    int getCountColumns();

    /**
     * ���������� ���������� ����� ��������������� ������
     *
     * @return ���-�� �����
     */
    @Keep
    int getRowCount();

    /**
     * ���������� ��� �������
     */
    @Keep
    int getType(String column);

    /**
     * ���������� ��� �������
     */
    @Keep
    int getType(int column);

    /**
     * ������� ����� DATASTORE, ���������� ������ �� ���������� Datastore
     *
     * @param ds - ������ Datastore (���� �������� null, �� ������ ����� ������� Datastore)
     * @return �������������� Datastore
     */
    @Keep
    Datastore dsConcat(Datastore[] ds);

    /**
     * ������ ������� ������� �����
     */
    @Keep
    void setFirst();


    /**
     * �� ����� ������� ���������� ��� �����
     */
    @Keep
    int getColumn(String colnam);

    /**
     * ��������� ������������ ������
     *
     * @param query - ������
     * @throws SQLException
     * @throws ConnectException
     * @throws BadPasswordException
     */
    @Keep
    void executeQuery(String query) throws SQLException;

    /**
     * ������������� ����������� �� ���-�� �����
     *
     * @param maxRows
     */
    @Keep
    void setMaxFethRow(int maxRows);

    /**
     * ���������, ��������� � Datastore, ���������� ���������� � ��� ��� ������ ����������
     */
    @Keep
    void notifyViews();


}
