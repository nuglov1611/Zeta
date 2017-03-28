package publicapi;

import proguard.annotation.Keep;
import views.Item;

import javax.swing.*;

public interface MenuAPI extends RmlObjectAPI, RmlContainerAPI {
    /**
     * Возвращает элемент меню
     *
     * @param i номер элемента
     * @return элемент меню
     */
    @Keep
    JMenuItem getItemAt(int i);

    /**
     * Удаляет элемент меню
     *
     * @param i номер элемента
     */
    @Keep
    void removeItem(int i);

    /**
     * Возвращает кол-во элементов в меню
     *
     * @return кол-во элементов
     */
    @Keep
    int getSize();

    /**
     * Добавляет новый элемент в меню. Если номер будет больше чем кол-во элементов в меню, то элемент добавится в конец меню
     *
     * @param i    - номер для добавления элемента
     * @param item - элемент меню
     */
    @Keep
    void putItem(int i, Item item);

}
