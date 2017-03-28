package publicapi;

import core.rml.dbi.GroupReport;
import proguard.annotation.Keep;

/**
 * ���������� ��������� "������". �� ���� ���������� ������������� GroupReport
 */
public interface TreeViewAPI extends VisualRmlObjectAPI, RmlContainerAPI, RetrieveableAPI {
    /**
     * ������ ������� �������
     *
     * @param n ����� �������� ���� -1, �� ���������� �������� �������
     */
    @Keep
    void setCurrentNode(int n);

    /**
     * ������ �������� ������ ��� ������
     *
     * @param dataTree - �������� ������
     */
    @Keep
    void setSource(GroupReport dataTree);

    /**
     * ���������� ���-�� ������� �� ������� �������� � ������, ���������� �� ����� �� ��������
     *
     * @return ���-�� ������� �� ������� ��������
     */
    @Keep
    int getLevel();


}
