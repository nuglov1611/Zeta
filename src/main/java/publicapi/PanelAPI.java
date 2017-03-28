package publicapi;

import proguard.annotation.Keep;
import action.api.RTException;

/**
* ����������� ��������� "������"
* 
*/
public interface PanelAPI extends VisualRmlObjectAPI, RmlContainerAPI, RetrieveableAPI {

    /**
     * ������� ����������� ����
     * @return ���� MENU 
     */
    @Keep
    public views.Menu getMenu();

    /**
     * �������� ����������� ����
     * @param m MENU ���� 
     * @throws RTException
     */
    @Keep
    public void setMenu(views.Menu m) throws RTException;
    
    
   
}
