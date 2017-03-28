package publicapi;

import proguard.annotation.Keep;

import java.sql.SQLException;

/**
 * ������, ��������������� ��� ������ � ����� ������. ������ ������ ���� ��������������� �������� � ��,
 * ���� ����� � ���� ��������� ����� �������, ������ � ���� ������ �������� ������ ����������� ������
 * ��� ������� �������.
 */
public interface RetrieveableAPI {
    void fromDS();

    /**
     * ��������� ������ � ��
     *
     * @return ���-�� ����� ���������� � �������
     * @throws Exception
     */
    @Keep
    int retrieve() throws Exception;

    void toDS();

    /**
     * ��������� ���������� ������ � ��
     *
     * @return ���-�� ����� ���������� � �������
     * @throws Exception
     */
    @Keep
    void update() throws SQLException;
}
