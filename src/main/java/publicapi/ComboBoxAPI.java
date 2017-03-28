package publicapi;

import proguard.annotation.Keep;

/**
 * Графический компонент "выпадающий список"
 *- target - имя столбца [[Datastore]], который будет служить источником данных. Сама [[Datastore]] должна быть вложена в ComboBox.
 *- action - скрипт, который выполняется при смене выбраного элемента списка. В первую очередь вызывается action выбраного элемента ([[ListItem]]), затем action ComboBox. (выражение [[calc]]) * 
 * 
 * Может включать в себя 
 *- DatastoreAPI
 *- ListItemAPI
 * 
 * @code
  {combobox
    alias=combo
    {item
      alias=itm1
      label=item1
      action="($X
                 ($g.date@setValue 'Выбран Item1')
                 ($ret '')
              )"
    }
    {item
      alias=itm2
      label=item2
      action="($X
                 ($g.date@setValue 'Выбран Item2')
                 ($ret '')
              )"
    }
    {item
      alias=itm3
      label=item3
      action="($g.self@doAction 'open comboWithDS.rml')"
    }
  }
   @endcode
 * @author nick
 *
 */
public interface ComboBoxAPI extends VisualRmlObjectAPI {

    /**
     * Добавить элемент
     * Sring item_alias - альяс элемента
     * Sring item_label - текст элемента
     * Sring item_action - дейтвие по нажатию
     */
    @Keep
    public void addItem(String item_alias, String item_label, String item_action);


    /**
     * Вернуть номер выбраного элемента
     * @return индекс
     */
    @Keep
    public int getSelectedIndex();
    
    /**
     * Вернуть выбраный элемент
     * @return индекс
     */
    @Keep
    public Object getSelectedItem();
    
    /**
     * Установить выбранный элемент по тексту элемента
     * @param item_label текст
     */
    @Keep
    public void setSelectedItem(String item_label);

    /**
     * Кол-во элементов
     * @return кол-во
     */
    @Keep
    public int getItemCount();

    /**
     * Получить элемент по номеру
     * @param index номер
     * @return элемент
     */
    @Keep
    public Object getItemAt(int index);

    /**
     * Установить выбранный элемент по номеру
     * @param index номер
     */
    @Keep
    public void setSelectedIndex(int index);

    /**
     * Установить выбранный элемент
     * @param item элемент
     */
    @Keep
    public void setSelectedItem(Object item);
}
