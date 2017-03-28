package publicapi;

import proguard.annotation.Keep;

/**
 * Визуальный компонент - объединение RadioButton. В один момент времени в группе может быть включена только одна кнопка.
 * @author 
 *
 */
public interface RadioGroupAPI extends VisualRmlObjectAPI, RmlContainerAPI {

    /**
     * Возвращает значение ассоциированное с текущей (включенной) кнопкой в группе
     * @return значение ассоциированное с текущей выбранной кнопкой
     */
	@Keep
	public Object getCurrentValue();
	
	 /**
     * Включает кнопку 
     * @param buttonNumber номер кнопки в группе
     * @param selected значение true - включить false - выключить
     */
	@Keep
    public void setSelected(int buttonNumber, boolean selected);
    
	 /**
     * Возвращает номер выбраной кнопки 
     * @return номер выбраной кнопки
     */
	@Keep
    public int getSelected();
	
}
