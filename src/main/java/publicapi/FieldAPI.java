package publicapi;

import proguard.annotation.Keep;

/**
* ����������� ��������� "���� �����"
* 
*/
public interface FieldAPI extends VisualRmlObjectAPI {
    /**
     * ������ ��� �������������� 
     * @param edit ��� �������������� ��������:
     * HANDBOOK 
     * ALL
     * NO
     * READONLY  
     */
    @Keep
    public void seteditable(String edit);

    /**
     * ������ ������ ��� ����������� (�������) 
     * @param val ��������
     */
    @Keep
    public void setValue(Object val);


    /**
     * �������� ���������� ������ 
     * @return ��������
     */
    @Keep
    public Object getValue();

    /**
     * �������� ����� �� ���� 
     * @return ��������
     */
    @Keep
    public String getText();

    /**
     * ������ ��� 
     * @param t ���:
     * java.sql.Types.NUMERIC
     * java.sql.Types.VARCHAR
     * java.sql.Types.DATE
     */
    @Keep
    public void setType(int t);

    /**
     * ������ ��������� ������� Datastore ������������� �� ����� 
     * @param t ������� Datastore
     */
    @Keep
    public void setTarget(String t);
    
    /**
     * �������� ��������� ������� Datastore ������������� �� ����� 
     * @return ������� Datastore
     */
    @Keep
    public String getTarget();
}
