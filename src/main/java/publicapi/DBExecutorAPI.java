package publicapi;

import proguard.annotation.Keep;

/**
 * ������ ��� ���������� �������� � ��
 *
 * @author uglov
 */
public interface DBExecutorAPI extends RmlObjectAPI {

    /**
     * ���������� ��� ������������� ���������
     *
     * @param index ����� ������������� ���������
     * @param type  ��� java.sql.Types
     */
    @Keep
    void registerOutParameter(int index, int type);

    /**
     * ���������� ��������� �������
     *
     * @return �������� ������������������� ���������
     */
    @Keep
    Object getResult();

    /**
     * ������������ ����� ����������� � �� (���������� �������� ������������) ��� ��������� ����
     *
     * @param shared ���� true - �� ����� ����������� �� ���������, ����� �������������� �����
     *               ����������� �������� ������������
     */
    @Keep
    void setSharedConnection(boolean shared);

    /**
     * ��������� ������ � �� � ����������� ��� �� ���������� ������
     *
     * @param blocking - true/false
     */
    @Keep
    void setBlocking(boolean blocking);

    /**
     * ������ ����� �������
     *
     * @param q - ������
     */
    @Keep
    void setQuery(String q);

    /**
     * ��������� ������
     */
    @Keep
    void execute();
}
