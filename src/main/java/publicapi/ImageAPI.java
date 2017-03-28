package publicapi;

import java.awt.Image;

import proguard.annotation.Keep;

public interface ImageAPI extends VisualRmlObjectAPI {
    /**
     * Возвращает текущее изображение
     * @return изображение 
     */
    @Keep
    public Image getImage();
    
    /**
    * Загружает изображение из RML-репозитория
    * @param name - путь к файлу-изображению в RML-репозитории 
    * @return загруженное изображение 
    */   
    @Keep
    Image getImage(String name);

}
