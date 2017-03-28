package publicapi;

import proguard.annotation.Keep;

/**
 * ���������� Rml-������ "�������" ������������ ��� �������� �������.  
 *
 */
public interface GridColumnAPI extends RmlObjectAPI, RmlContainerAPI {
    /**
     * ������ ��������� ������� (�������, ������ ����� ��������������� ��� ��������� �������� � ���� �������)
     * @param dep - ������ ��������� �������� ����� �������
     */
    @Keep
    public void setDep(String dep);

    /**
     * ������ ��� ������ �������
     * @param type - ��� ������ java.sql.Types, �������������� ��������� ����:
     * java.sql.Types.NUMERIC
     * java.sql.Types.VARCHAR
     * java.sql.Types.DATE
     * java.sql.Types.BOOLEAN;
     */
    @Keep
    public void setType(int type);
   
    /**
     * ������ ��� ������ �������
     * @param t - ��� ("number", "string", "date", "boolean")
     */
    @Keep
    public void setType(String t);
   
    /**
     * ������ Datastore ��� ������� ������� 
     * @param datastore - Datastore
     */
    @Keep
    public void setDS(Object datastore);

   
}
