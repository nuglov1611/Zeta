package publicapi;

import proguard.annotation.Keep;

import java.util.Hashtable;

/**
 * ��������� ������� ��� ������������� RML-�������
 *
 * @author uglov
 */
@Keep
public interface RmlProperties {


    /**
     * �������� ��������
     *
     * @param propertyName �������� ��������
     * @param property     �������� ��������
     */
    @Keep
    void put(String propertyName, Object property);

    /**
     * ������������� ������ ������� �� ���-�������
     *
     * @param properties ������� �� ����������
     */
    @Keep
    void importHash(Hashtable<String, Object> properties);
}
