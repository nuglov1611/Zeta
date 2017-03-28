package publicapi;

import proguard.annotation.Keep;

/**
 * ������ � ����������
 */
public interface TabbedPanelAPI extends RmlContainerAPI, RetrieveableAPI, VisualRmlObjectAPI {

    /**
     * ���������� ����� ��������, �������� � ������ ������ �������.
     *
     * @return
     */
    @Keep
    int getCurrentTab();

    /**
     * ������� (������� �������) ��������
     *
     * @param tabNumber - ����� �������� (��������� � 0)
     */
    @Keep
    void setCurrentTab(int tabNumber);

}
