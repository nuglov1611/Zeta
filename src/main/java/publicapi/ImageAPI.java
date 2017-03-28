package publicapi;

import proguard.annotation.Keep;

import java.awt.*;

public interface ImageAPI extends VisualRmlObjectAPI {
    /**
     * Возвращает текущее изображение
     *
     * @return изображение
     */
    @Keep
    Image getImage();

    /**
     * Загружает изображение из RML-репозитория
     *
     * @param name - путь к файлу-изображению в RML-репозитории
     * @return загруженное изображение
     */
    @Keep
    Image getImage(String name);

}
