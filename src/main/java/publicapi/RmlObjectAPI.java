/**
 * 
 */
package publicapi;

import proguard.annotation.Keep;
import core.document.Document;
import core.parser.Proper;

/**
 * Стандартный объект в системе "Зета"
 * Это может быть как визуальные компоненты:
 * - SplitPane
 * - Grid
 * - Button
 * - Label
 * 
 * так и логические компоненты для работы с БД и прочих операций
 * - Datastore
 * - DSCollection
 * и д.р.

 * Создать объект можно с помощью фабрики RML-объектов:
 * @code
 *	{
 *   importPackage(org.apache.log4j);
 *	 importPackage(java.util);
 *   importPackage(Packages.publicapi);
 * 	 log = LogManager.getLogger('PreloadScript');
 *	 hash = new Hashtable();
 *   log.debug('СОЗДАЕМ label !');
 *   newlabel  = RmlObjectFactory.createObject('label');
 *   hash.clear();
 *   left = new java.lang.Integer(left);
 *   top = new java.lang.Integer(top);
 *   width = new java.lang.Integer(width);
 *   height = new java.lang.Integer(height);
 *   font_size = new java.lang.Integer(font_size);
 *   hash.put('left', left);
 *   hash.put('top', top);
 *   hash.put('width', width);
 *   hash.put('height', height);
 *   hash.put('font_size', font_size);
 *   hash.put('value', value);
 *   newlabel.init(RmlObjectFactory.getProperties(hash),SELF);
 * }
 * @endcode
 * RML-объекты, реализующие интерфейс RmlContainerAPI могут содержать в себе другие объекты
 * 
 * @author uglov
 *
 */
public interface RmlObjectAPI {

	
    /**
     * Инициализация RML-объекта 
     * @param prop - набор параметров
     * @param doc - ссылка на документ
     */
    @Keep
    public void init(Proper prop, Document doc);


}
