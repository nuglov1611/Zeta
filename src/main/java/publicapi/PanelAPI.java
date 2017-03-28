package publicapi;

import proguard.annotation.Keep;
import action.api.RTException;

/**
* Графический компонент "Панель"
* 
*/
public interface PanelAPI extends VisualRmlObjectAPI, RmlContainerAPI, RetrieveableAPI {

    /**
     * Получть контекстное меню
     * @return меню MENU 
     */
    @Keep
    public views.Menu getMenu();

    /**
     * Добавить контекстное меню
     * @param m MENU меню 
     * @throws RTException
     */
    @Keep
    public void setMenu(views.Menu m) throws RTException;
    
    
   
}
