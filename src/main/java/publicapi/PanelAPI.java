package publicapi;

import action.api.RTException;
import proguard.annotation.Keep;

/**
 * Графический компонент "Панель"
 */
public interface PanelAPI extends VisualRmlObjectAPI, RmlContainerAPI, RetrieveableAPI {

    /**
     * Получть контекстное меню
     *
     * @return меню MENU
     */
    @Keep
    views.Menu getMenu();

    /**
     * Добавить контекстное меню
     *
     * @param m MENU меню
     * @throws RTException
     */
    @Keep
    void setMenu(views.Menu m) throws RTException;


}
