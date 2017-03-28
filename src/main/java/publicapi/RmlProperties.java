package publicapi;

import java.util.Hashtable;

import proguard.annotation.Keep;

/**
 * ��������� ������� ��� ������������� RML-������� 
 * @author uglov
 *
 */
@Keep
public interface RmlProperties {

 
	/**
	 * �������� ��������
	 * @param propertyName �������� ��������
	 * @param property �������� ��������
	 */
    @Keep
    public void put(String propertyName, Object property);

    /**
     * ������������� ������ ������� �� ���-�������
     * @param properties ������� �� ����������
     */
    @Keep
    public void importHash(Hashtable<String, Object> properties);
}
