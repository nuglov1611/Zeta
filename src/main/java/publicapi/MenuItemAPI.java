package publicapi;

import proguard.annotation.Keep;

/**
* Элемент меню
* 
*/
public interface MenuItemAPI extends RmlObjectAPI {
    /**
     * Возвращает скрипт выполняющийся при выборе этого элемента меню
     * @return скрипт
     */
    @Keep
    public String getExp();
    
    /**
     * Задает скрипт выполняющийся при выборе этого элемента меню  
     * @param exp скрипт
     */
    @Keep
    public void setExp(String exp);

    /**
     * Возвращает действие документа, выполняющееся при выборе этого пункта меню
     * @return скрипт-действие (скрипт, вычисляющий действие)
     */
    @Keep
    public String getAction();

    /**
     * Задает действие документа, выполняющееся при выборе этого пункта меню
     * @param action - скрипт-действие (возможно скрипт, вычисляющий действие)
     */
    @Keep
    public void setAction(String action);

    /**
     * Возвращает название данного пункта меню
     * @return текст названия
     */
    @Keep
    public String getLabel();

    /**
     * Задает название элемента
     * @param label текст названия
     */
    @Keep
    public void setLabel(String label );

    /**
     * Управляет состоянием элемента (делает активным/не активным) 
     * @param enabled true - активный, false - не активный (серый)
     */
	@Keep
    public void setEnabled(boolean enabled);
	
    /**
     * Возвращает состояние элемента 
     * @return true если элемент активный, false если не активный (серый)
     */
    @Keep
    public boolean isEnabled();


}
