package publicapi;

import proguard.annotation.Keep;

/**
 * ����������� ��������� "���� �����"
 */
public interface FieldAPI extends VisualRmlObjectAPI {
    /**
     * ������ ��� ��������������
     *
     * @param edit ��� �������������� ��������:
     *             HANDBOOK
     *             ALL
     *             NO
     *             READONLY
     */
    @Keep
    void seteditable(String edit);

    /**
     * ������ ������ ��� ����������� (�������)
     *
     * @param val ��������
     */
    @Keep
    void setValue(Object val);


    /**
     * �������� ���������� ������
     *
     * @return ��������
     */
    @Keep
    Object getValue();

    /**
     * �������� ����� �� ����
     *
     * @return ��������
     */
    @Keep
    String getText();

    /**
     * ������ ���
     *
     * @param t ���:
     *          java.sql.Types.NUMERIC
     *          java.sql.Types.VARCHAR
     *          java.sql.Types.DATE
     */
    @Keep
    void setType(int t);

    /**
     * ������ ��������� ������� Datastore ������������� �� �����
     *
     * @param t ������� Datastore
     */
    @Keep
    void setTarget(String t);

    /**
     * �������� ��������� ������� Datastore ������������� �� �����
     *
     * @return ������� Datastore
     */
    @Keep
    String getTarget();
}
