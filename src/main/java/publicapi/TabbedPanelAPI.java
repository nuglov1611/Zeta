package publicapi;

import proguard.annotation.Keep;

/**
 * ������ � ���������� 
 *
 */
public interface TabbedPanelAPI extends RmlContainerAPI, RetrieveableAPI, VisualRmlObjectAPI {
	
    /**
     * ���������� ����� ��������, �������� � ������ ������ �������.
     * @return
     */
	@Keep
    public int getCurrentTab();

    /**
     * ������� (������� �������) �������� 
     * @param tabNumber - ����� �������� (��������� � 0)
     */
	@Keep
    public void setCurrentTab(int tabNumber);

}
