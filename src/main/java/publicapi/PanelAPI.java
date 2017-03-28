package publicapi;

import action.api.RTException;
import proguard.annotation.Keep;

/**
 * ����������� ��������� "������"
 */
public interface PanelAPI extends VisualRmlObjectAPI, RmlContainerAPI, RetrieveableAPI {

    /**
     * ������� ����������� ����
     *
     * @return ���� MENU
     */
    @Keep
    views.Menu getMenu();

    /**
     * �������� ����������� ����
     *
     * @param m MENU ����
     * @throws RTException
     */
    @Keep
    void setMenu(views.Menu m) throws RTException;


}
