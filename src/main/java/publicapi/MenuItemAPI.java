package publicapi;

import proguard.annotation.Keep;

/**
 * ������� ����
 */
public interface MenuItemAPI extends RmlObjectAPI {
    /**
     * ���������� ������ ������������� ��� ������ ����� �������� ����
     *
     * @return ������
     */
    @Keep
    String getExp();

    /**
     * ������ ������ ������������� ��� ������ ����� �������� ����
     *
     * @param exp ������
     */
    @Keep
    void setExp(String exp);

    /**
     * ���������� �������� ���������, ������������� ��� ������ ����� ������ ����
     *
     * @return ������-�������� (������, ����������� ��������)
     */
    @Keep
    String getAction();

    /**
     * ������ �������� ���������, ������������� ��� ������ ����� ������ ����
     *
     * @param action - ������-�������� (�������� ������, ����������� ��������)
     */
    @Keep
    void setAction(String action);

    /**
     * ���������� �������� ������� ������ ����
     *
     * @return ����� ��������
     */
    @Keep
    String getLabel();

    /**
     * ������ �������� ��������
     *
     * @param label ����� ��������
     */
    @Keep
    void setLabel(String label);

    /**
     * ��������� ���������� �������� (������ ��������/�� ��������)
     *
     * @param enabled true - ��������, false - �� �������� (�����)
     */
    @Keep
    void setEnabled(boolean enabled);

    /**
     * ���������� ��������� ��������
     *
     * @return true ���� ������� ��������, false ���� �� �������� (�����)
     */
    @Keep
    boolean isEnabled();


}
