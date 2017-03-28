package publicapi;

import proguard.annotation.Keep;

import java.util.Hashtable;

/**
 * Контейнер свойств для инициализации RML-объекта
 *
 * @author uglov
 */
@Keep
public interface RmlProperties {


    /**
     * Добавить свойство
     *
     * @param propertyName название свойства
     * @param property     значение свойства
     */
    @Keep
    void put(String propertyName, Object property);

    /**
     * Импортировать список свойств из Хэш-таблицы
     *
     * @param properties таблица со свойствами
     */
    @Keep
    void importHash(Hashtable<String, Object> properties);
}
