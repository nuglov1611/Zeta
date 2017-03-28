package publicapi;

import proguard.annotation.Keep;
import action.api.RTException;

/**
 * Графический элемент CheckBox
 * Свойства:
 *- label - отображаемое имя чекбокса. Если будет пустое значение "", чекбокс будет представлять из себя только один активный управляющий элемент без надписей (Только квадрат с галочкой). Значение по умолчанию "".
 *- offvalue - значение используемое для обозначения состояния, при котором галочка в активном элементе не поставлена. Используется при анализе свойства checked. Значение по умолчанию "no". 
 *- onvalue - значение используемое для обозначения состояния, при котором галочка в активном элементе поставлена. Используется при анализе свойства checked. Значение по умолчанию "yes".
 *- check - указывает на то, поставлена галочка в активном элементе при первичной инициализации чекбокса или нет. Для сравнения введенного значения используются строковые константы "yes" и "no". Значение по умолчанию "no", т.е. по умолчанию галочка не поставлена.
 *- shortCut - сочетание клавиш
 *- action - действие запускаемое по изменению состояни объекта
 * 
 * @code
  {checkbox 
      alias=cb1
      label = "CheckBox1"
      offvalue = "off" onvalue = "on" check = yes
  }
  @endcode 
 * 
 */
public interface CheckBoxAPI extends VisualRmlObjectAPI {

    /**
     * Установить состояние компонента соответсвующее ассоциированному значению
     * @param value значение (onValue или offValue)
     * @throws RTException если value не является ни onValue ни offValue 
     */
    @Keep
    public void setState(Object value) throws RTException;

    /**
     * Устонавливает состояние CheckBox
     * @param selected сосояние true - включен, false - выключен
     */
    @Keep
    public void setSelected(boolean selected);

    /**
     * @return сосояние CheckBox true - включен, false - выключен
     */
    @Keep
    public boolean isSelected();

    /**
     * @return ассоциированное с состоянием значение (onValue - если включен, щааМфдгу - если выключен )
     */
    @Keep
    public String getStateValue();

}
