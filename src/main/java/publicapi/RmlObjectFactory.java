package publicapi;
        
import java.util.Hashtable;

import loader.ZetaProperties;

import org.apache.log4j.Logger;

import proguard.annotation.Keep;
import core.parser.Proper;
import core.rml.RmlObject;

/**
 * Фабрика для создания Rml-объектов
 * @author uglov
 *
 */
@Keep
public class RmlObjectFactory {
	private static final Logger log = Logger.getLogger(RmlObject.class);
	
	/**
	 * Создание RML-объекта по названию
	 * @param objectName название объекта
	 * @return экземпляр RmlObject
	 */
	@Keep
	public static RmlObjectAPI createObject(String objectName){
		RmlObject res = null;
        try {
        	Class cl = Class.forName("core.reflection.rml." + objectName.toUpperCase());
        	res = (RmlObject) cl.newInstance();
        }
        catch (Exception e) {
        	log.error("!", e);
        	if (ZetaProperties.parser_debug > 2) {
                log.error("~rml.Parser::getContent ", e);
            }
            res = null;
        }
        return res;
	}

	
	/**
	 * Создание Контейнера для свойств RML-объекта
	 * @return экземпляр RmlProperties
	 */
	@Keep
	public static RmlProperties getProperties(){
		return new Proper();
	}
	
	/**
	 * Создание Контейнера для свойств RML-объекта
	 * @param p таблица свойств
	 * @return экземпляр RmlProperties
	 */
	@Keep
	public static RmlProperties getProperties(Hashtable<String, Object> p){
		return new Proper(p);
	}
	
}
