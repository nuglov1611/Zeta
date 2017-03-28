package publicapi;

import proguard.annotation.Keep;

/**
 * Визуальный компонент "надпись"
 */
public interface LabelAPI extends VisualRmlObjectAPI {
    /**
     * Возвращает текущий текст надписи
     *
     * @return текст
     */
    @Keep
    String getText();

    /**
     * Задает текст надписи. Сам элемент при этом становится видимым
     *
     * @param text - текст надписи
     */
    @Keep
    void setText(String text);


}
