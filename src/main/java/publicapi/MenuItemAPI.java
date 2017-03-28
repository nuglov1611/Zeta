package publicapi;

import proguard.annotation.Keep;

/**
* ������� ����
* 
*/
public interface MenuItemAPI extends RmlObjectAPI {
    /**
     * ���������� ������ ������������� ��� ������ ����� �������� ����
     * @return ������
     */
    @Keep
    public String getExp();
    
    /**
     * ������ ������ ������������� ��� ������ ����� �������� ����  
     * @param exp ������
     */
    @Keep
    public void setExp(String exp);

    /**
     * ���������� �������� ���������, ������������� ��� ������ ����� ������ ����
     * @return ������-�������� (������, ����������� ��������)
     */
    @Keep
    public String getAction();

    /**
     * ������ �������� ���������, ������������� ��� ������ ����� ������ ����
     * @param action - ������-�������� (�������� ������, ����������� ��������)
     */
    @Keep
    public void setAction(String action);

    /**
     * ���������� �������� ������� ������ ����
     * @return ����� ��������
     */
    @Keep
    public String getLabel();

    /**
     * ������ �������� ��������
     * @param label ����� ��������
     */
    @Keep
    public void setLabel(String label );

    /**
     * ��������� ���������� �������� (������ ��������/�� ��������) 
     * @param enabled true - ��������, false - �� �������� (�����)
     */
	@Keep
    public void setEnabled(boolean enabled);
	
    /**
     * ���������� ��������� �������� 
     * @return true ���� ������� ��������, false ���� �� �������� (�����)
     */
    @Keep
    public boolean isEnabled();


}
