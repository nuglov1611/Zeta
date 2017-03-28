package publicapi;

import proguard.annotation.Keep;
import core.rml.dbi.GroupReport;

/**
 * ���������� ��������� "������". �� ���� ���������� ������������� GroupReport
 *
 */
public interface TreeViewAPI extends VisualRmlObjectAPI, RmlContainerAPI, RetrieveableAPI {
    /**
     * ������ ������� �������
     * @param n ����� �������� ���� -1, �� ���������� �������� �������
     */
    @Keep
    public void setCurrentNode(int n);

    /**
     * ������ �������� ������ ��� ������
     * @param dataTree - �������� ������
     */
    @Keep
    public void setSource(GroupReport dataTree);
 
    /**
     * ���������� ���-�� ������� �� ������� �������� � ������, ���������� �� ����� �� ��������
     * @return ���-�� ������� �� ������� ��������
     */
    @Keep
    public int getLevel();
    
    

}
