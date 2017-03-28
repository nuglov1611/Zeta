package publicapi;

import proguard.annotation.Keep;

/**
 * ������ ��� ���������� �������� � ��
 * @author uglov
 *
 */
public interface DBExecutorAPI extends RmlObjectAPI {

	/**
	 * ���������� ��� ������������� ���������
	 * @param index ����� ������������� ���������
	 * @param type ��� java.sql.Types
	 */
	@Keep
	public void registerOutParameter(int index, int type);
	
	/**
	 * ���������� ��������� ������� 
	 * @return �������� ������������������� ���������
	 */
	@Keep
	public Object getResult();
	
	/**
	 * ������������ ����� ����������� � �� (���������� �������� ������������) ��� ��������� ���� 
	 * @param shared ���� true - �� ����� ����������� �� ���������, ����� �������������� ����� 
	 *               ����������� �������� ������������
	 */
	@Keep
	public void setSharedConnection(boolean shared);
	
	/**
	 * ��������� ������ � �� � ����������� ��� �� ���������� ������
	 * @param blocking - true/false
	 * 
	 */
	@Keep
	public void setBlocking(boolean blocking);
	
	/**
	 * ������ ����� �������
	 * @param q - ������ 
	 */
	@Keep
	public void setQuery(String q);
	
	/**
	 * ��������� ������
	 */
	@Keep
	public void execute();
}
