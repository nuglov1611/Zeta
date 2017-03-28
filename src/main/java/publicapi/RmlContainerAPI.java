package publicapi;

import proguard.annotation.Keep;
import core.rml.Container;
import core.rml.RmlObject;


/**
 * Объект RML, который может собержать в себе другие RML-объеты
 * Для добавления вложенного объекта следует испольовать метод addChild(child)
 * Когда все объекты будет добавленны крайне необходимо вызвать метод initChildren()для инициализации всех параметров вложенных объектов
 * 
 * пример доавбление на форму CheckGroup c двумя CheckBox:
 * @code
    checkgroup  = createCheckGroup(250, 40);
    check_action = '~($X\
                   ($debug \'checkbox_show_busy_rooms action\')\
                    ($grid2_repaint)\
                    ($ret \'\')\
                   )~'
    checkgroup.addChild(createCheckBox('checkbox_show_busy_rooms', 'занятые', 14, 'ON', 'OFF', 'NO', check_action));
 
    checkgroup  = createCheckGroup(250, 70);
    form.addChild(checkgroup);
    check_action = '~($X\
                    ($debug \'checkbox_show_all_rooms action\')\
                    ($grid2_repaint)\
                    ($ret \'\')\
                   )~';
    checkgroup.addChild(createCheckBox('checkbox_show_all_rooms', 'все', 14, 'ON', 'OFF', 'YES', check_action));
    checkgroup.initChildren();
    form.addChild(checkgroup);
    @endcode
 *    
 * @author nuglov
 */
@Keep
public interface RmlContainerAPI {

	/**
	 * Добавить объект
	 * 
	 * @param child
	 */
    @Keep
    public void addChild(RmlObject child);
    
    
    /**
     * Получить вложенные объекты 
     * @return массив объетов 
     */
    @Keep
    public RmlObject[] getChildren();
    
    
    /**
     * Выполнить инициализацию вложенных объектов 
     * 
     */
    @Keep
	public void initChildren() throws Exception;

    /**
     * Получить контейнер верхнего уровня 
     * @return контейнер 
     */
    @Keep
    public Container getContainer();
    
	public boolean addChildrenAutomaticly();
}
