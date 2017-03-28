package publicapi;

import proguard.annotation.Keep;

/**
* Графический компонент "Поле ввода"
* 
*/
public interface FieldAPI extends VisualRmlObjectAPI {
    /**
     * Задает тип редактирования 
     * @param edit тип редактирования значения:
     * HANDBOOK 
     * ALL
     * NO
     * READONLY  
     */
    @Keep
    public void seteditable(String edit);

    /**
     * Задает объект для отображения (значене) 
     * @param val значение
     */
    @Keep
    public void setValue(Object val);


    /**
     * Получить хранящийся объект 
     * @return значение
     */
    @Keep
    public Object getValue();

    /**
     * Получить текст из поля 
     * @return значение
     */
    @Keep
    public String getText();

    /**
     * Задать тип 
     * @param t тип:
     * java.sql.Types.NUMERIC
     * java.sql.Types.VARCHAR
     * java.sql.Types.DATE
     */
    @Keep
    public void setType(int t);

    /**
     * Задать связанный столбец Datastore расположенной на форме 
     * @param t столбец Datastore
     */
    @Keep
    public void setTarget(String t);
    
    /**
     * Получить связанный столбец Datastore расположенной на форме 
     * @return столбец Datastore
     */
    @Keep
    public String getTarget();
}
