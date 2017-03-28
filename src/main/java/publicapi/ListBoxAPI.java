package publicapi;

import proguard.annotation.Keep;

/**
 * Элемент управления - список. Может формироваться либо на основе столбца [[Datastore]] либо вручную.
 * <p>
 * Свойства =
 * - target - имя столбца [[Datastore]], который будет служить источником данных. Сама [[Datastore]] должна быть вложена в ListBox.
 * - action - скрипт, который выполняется при смене выбраного элемента списка. В первую очередь вызывается action выбраного элемента ([[ListItem]]), затем action ComboBox. (выражение [[calc]])
 * <p>
 * Пример
 *
 * @code {listbox
 * alias=combo
 * {item
 * alias=itm1
 * label=item1
 * action="($X
 * ($g.date@setValue 'Выбран Item1')
 * ($ret '')
 * )"
 * }
 * {item
 * alias=itm2
 * label=item2
 * action="($X
 * ($g.date@setValue 'Выбран Item2')
 * ($ret '')
 * )"
 * }
 * {item
 * alias=itm3
 * label=item3
 * action="($g.self@doAction 'open comboWithDS.rml')"
 * }
 * }
 * @endcode
 */
public interface ListBoxAPI extends VisualRmlObjectAPI {

    /**
     * Добавить элемент
     * Sring item_alias - альяс элемента
     * Sring item_label - текст элемента
     * Sring item_action - дейтвие по нажатию
     */
    @Keep
    void addItem(String item_alias, String item_label, String item_action);


    /**
     * Вернуть номер выбраного элемента
     *
     * @return индекс
     */
    @Keep
    int getSelectedIndex();

    /**
     * Вернуть выбраный элемент
     *
     * @return индекс
     */
    @Keep
    Object getSelectedValue();

    /**
     * Установить выбранный элемент по тексту элемента
     *
     * @param item_label текст
     */
    @Keep
    void setSelectedItem(String item_label);

    /**
     * Кол-во элементов
     *
     * @return кол-во
     */
    @Keep
    int getItemCount();

    /**
     * Получить элемент по номеру
     *
     * @param index номер
     * @return элемент
     */
    @Keep
    Object getItemAt(int index);

    /**
     * Установить выбранный элемент по номеру
     *
     * @param index номер
     */
    @Keep
    void setSelectedIndex(int index);

    /**
     * Установить выбранный элемент
     *
     * @param item элемент
     */
    @Keep
    void setSelectedItem(Object item);

}
