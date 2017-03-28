package publicapi;

import proguard.annotation.Keep;

/**
 * Элемент для управления Фокусировкой в рамках документа
 */
		
public interface FocuserAPI extends RmlObjectAPI {
    
    /**
     * Передать фокус на объект 
     * 
     *  @param component - объект для передачи фокуса
     */
    @Keep
    public void focus(Object component);
    
    /**
     * Передать фокус на слудещий объект 
     * 
     */
    @Keep
    public void focusNext();
   
    /**
     * Передать фокус на предыдущий объект 
     * 
     */
    @Keep
    public void focusPrevious();
}
