package publicapi;

import proguard.annotation.Keep;

/**
 * Визуальный Rml-объект "столбец" используется при описании таблицы.  
 *
 */
public interface GridColumnAPI extends RmlObjectAPI, RmlContainerAPI {
    /**
     * задает связанные столбцы (столбцы, котрые будут пересчитываться при изменении значения в этом столбце)
     * @param dep - список зависымых столбцов через запятую
     */
    @Keep
    public void setDep(String dep);

    /**
     * задает тип данных столбца
     * @param type - тип данных java.sql.Types, поддерживаются следующие типы:
     * java.sql.Types.NUMERIC
     * java.sql.Types.VARCHAR
     * java.sql.Types.DATE
     * java.sql.Types.BOOLEAN;
     */
    @Keep
    public void setType(int type);
   
    /**
     * задает тип данных столбца
     * @param t - тип ("number", "string", "date", "boolean")
     */
    @Keep
    public void setType(String t);
   
    /**
     * Задать Datastore для данного столбца 
     * @param datastore - Datastore
     */
    @Keep
    public void setDS(Object datastore);

   
}
