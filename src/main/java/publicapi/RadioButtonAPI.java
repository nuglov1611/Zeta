package publicapi;

import proguard.annotation.Keep;

public interface RadioButtonAPI extends VisualRmlObjectAPI {

    /**
     * Возвращает состояние кнопки (включена/выключена)
     *
     * @return true - включена, false - выключена
     */
    @Keep
    boolean isSelected();


    /**
     * Задает состояние кнопки (включена/выключена)
     *
     * @param selected если true - включена, false - выключена
     */
    @Keep
    void setSelected(boolean selected);


}
