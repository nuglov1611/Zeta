package publicapi;

import core.rml.Container;
import core.rml.RmlObject;
import proguard.annotation.Keep;


/**
 * Объект RML, который может собержать в себе другие RML-объеты
 * Для добавления вложенного объекта следует испольовать метод addChild(child)
 * Когда все объекты будет добавленны крайне необходимо вызвать метод initChildren()для инициализации всех параметров вложенных объектов
 * <p>
 * пример доавбление на форму CheckGroup c двумя CheckBox:
 *
 * @author nuglov
 * @code checkgroup  = createCheckGroup(250, 40);
 * check_action = '~($X\
 * ($debug \'checkbox_show_busy_rooms action\')\
 * ($grid2_repaint)\
 * ($ret \'\')\
 * )~'
 * checkgroup.addChild(createCheckBox('checkbox_show_busy_rooms', 'занятые', 14, 'ON', 'OFF', 'NO', check_action));
 * <p>
 * checkgroup  = createCheckGroup(250, 70);
 * form.addChild(checkgroup);
 * check_action = '~($X\
 * ($debug \'checkbox_show_all_rooms action\')\
 * ($grid2_repaint)\
 * ($ret \'\')\
 * )~';
 * checkgroup.addChild(createCheckBox('checkbox_show_all_rooms', 'все', 14, 'ON', 'OFF', 'YES', check_action));
 * checkgroup.initChildren();
 * form.addChild(checkgroup);
 * @endcode
 */
@Keep
public interface RmlContainerAPI {

    /**
     * Добавить объект
     *
     * @param child
     */
    @Keep
    void addChild(RmlObject child);


    /**
     * Получить вложенные объекты
     *
     * @return массив объетов
     */
    @Keep
    RmlObject[] getChildren();


    /**
     * Выполнить инициализацию вложенных объектов
     */
    @Keep
    void initChildren() throws Exception;

    /**
     * Получить контейнер верхнего уровня
     *
     * @return контейнер
     */
    @Keep
    Container getContainer();

    boolean addChildrenAutomaticly();
}
