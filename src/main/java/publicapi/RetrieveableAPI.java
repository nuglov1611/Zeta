package publicapi;

import java.sql.SQLException;

import proguard.annotation.Keep;
import core.connection.BadPasswordException;
import core.rml.dbi.exception.UpdateException;

/**
 * ������, ��������������� ��� ������ � ����� ������. ������ ������ ���� ��������������� �������� � ��, 
 * ���� ����� � ���� ��������� ����� �������, ������ � ���� ������ �������� ������ ����������� ������ 
 * ��� ������� �������. 
 *
 */
public interface RetrieveableAPI {
    public void fromDS();

    /**
     * ��������� ������ � ��
     * @return ���-�� ����� ���������� � �������
     * @throws Exception
     */
    @Keep
    public int retrieve() throws Exception;

    public void toDS();

    /**
     * ��������� ���������� ������ � ��
     * @return ���-�� ����� ���������� � �������
     * @throws Exception
     */
    @Keep
    public void update() throws UpdateException, BadPasswordException, SQLException;
}
