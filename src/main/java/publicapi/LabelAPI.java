package publicapi;

import proguard.annotation.Keep;

/**
 * ���������� ��������� "�������"
 */
public interface LabelAPI extends VisualRmlObjectAPI {
    /**
     * ���������� ������� ����� �������
     *
     * @return �����
     */
    @Keep
    String getText();

    /**
     * ������ ����� �������. ��� ������� ��� ���� ���������� �������
     *
     * @param text - ����� �������
     */
    @Keep
    void setText(String text);


}
