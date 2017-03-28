package publicapi;

import proguard.annotation.Keep;

/**
 * ���������� ��������� "�������"
 */
public interface LabelAPI extends VisualRmlObjectAPI {
    /**
     * ���������� ������� ����� �������
     * @return �����
     */
    @Keep
    public String getText();
    
    /**
     * ������ ����� �������. ��� ������� ��� ���� ���������� �������
     * @param text - ����� �������
     */
    @Keep
    public void setText(String text);


}
