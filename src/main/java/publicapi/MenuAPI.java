package publicapi;

import javax.swing.JMenuItem;

import proguard.annotation.Keep;
import views.Item;

public interface MenuAPI extends RmlObjectAPI, RmlContainerAPI {
	/**
	 * Возвращает элемент меню 
	 * @param i номер элемента
	 * @return элемент меню
	 */
    @Keep
	public JMenuItem getItemAt(int i);

	/**
	 * Удаляет элемент меню
	 * @param i номер элемента
	 */
    @Keep
	public void removeItem(int i);
    
	/**
	 * Возвращает кол-во элементов в меню
	 * @return кол-во элементов
	 */
    @Keep
	public int getSize();
    
	/**
	 * Добавляет новый элемент в меню. Если номер будет больше чем кол-во элементов в меню, то элемент добавится в конец меню
	 * @param i - номер для добавления элемента
	 * @param item - элемент меню
	 */
    @Keep
	public void putItem(int i, Item item);

}
