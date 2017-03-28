package publicapi;

import java.util.Hashtable;

import proguard.annotation.Keep;

/**
 * Контейнер свойств для инициализации RML-объекта 
 * @author uglov
 *
 */
@Keep
public interface RmlProperties {

 
	/**
	 * Добавить свойство
	 * @param propertyName название свойства
	 * @param property значение свойства
	 */
    @Keep
    public void put(String propertyName, Object property);

    /**
     * Импортировать список свойств из Хэш-таблицы
     * @param properties таблица со свойствами
     */
    @Keep
    public void importHash(Hashtable<String, Object> properties);
}
