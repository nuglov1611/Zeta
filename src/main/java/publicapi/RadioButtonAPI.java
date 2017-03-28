package publicapi;

import proguard.annotation.Keep;

public interface RadioButtonAPI extends VisualRmlObjectAPI {

    /**
     * ���������� ��������� ������ (��������/���������)
     *
     * @return true - ��������, false - ���������
     */
    @Keep
    boolean isSelected();


    /**
     * ������ ��������� ������ (��������/���������)
     *
     * @param selected ���� true - ��������, false - ���������
     */
    @Keep
    void setSelected(boolean selected);


}
