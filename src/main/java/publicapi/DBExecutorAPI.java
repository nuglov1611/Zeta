package publicapi;

import proguard.annotation.Keep;

/**
 * Объект для выполнения запросов к БД
 *
 * @author uglov
 */
public interface DBExecutorAPI extends RmlObjectAPI {

    /**
     * Установить тип возвращаемого параметра
     *
     * @param index номер возвращаемого параметра
     * @param type  тип java.sql.Types
     */
    @Keep
    void registerOutParameter(int index, int type);

    /**
     * Возвращает результат запроса
     *
     * @return значение зарегистрированного параметра
     */
    @Keep
    Object getResult();

    /**
     * Использовать общее подключение к БД (подлючение рабочего пространства) или создавать свое
     *
     * @param shared если true - то новое подключение не создается, будет использоваться общее
     *               подключение рабочего пространства
     */
    @Keep
    void setSharedConnection(boolean shared);

    /**
     * Выполнять запрос к БД в блокирующем или не блокирующм режиме
     *
     * @param blocking - true/false
     */
    @Keep
    void setBlocking(boolean blocking);

    /**
     * Задать текст запроса
     *
     * @param q - запрос
     */
    @Keep
    void setQuery(String q);

    /**
     * Выполнить запрос
     */
    @Keep
    void execute();
}
