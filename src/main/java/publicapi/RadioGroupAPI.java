package publicapi;

import proguard.annotation.Keep;

/**
 * ���������� ��������� - ����������� RadioButton. � ���� ������ ������� � ������ ����� ���� �������� ������ ���� ������.
 *
 * @author
 */
public interface RadioGroupAPI extends VisualRmlObjectAPI, RmlContainerAPI {

    /**
     * ���������� �������� ��������������� � ������� (����������) ������� � ������
     *
     * @return �������� ��������������� � ������� ��������� �������
     */
    @Keep
    Object getCurrentValue();

    /**
     * �������� ������
     *
     * @param buttonNumber ����� ������ � ������
     * @param selected     �������� true - �������� false - ���������
     */
    @Keep
    void setSelected(int buttonNumber, boolean selected);

    /**
     * ���������� ����� �������� ������
     *
     * @return ����� �������� ������
     */
    @Keep
    int getSelected();

}
