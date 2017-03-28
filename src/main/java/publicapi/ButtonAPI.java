package publicapi;

import proguard.annotation.Keep;

/**
 * Графический компонент "кнопка"
 *
 * свойства:
 * - label - Надпись на кнопке. Значение по умолчанию "". Может содержать html-теги.
 * - action - действие выполняемое при нажатии на кнопку
 * - aAction - действие выполняемое при нажатии на кнопку
 * - alignment - положение текста на кнопке
 *  - center - по центру
 *  - left - прижать влево
 *  - right - прижать вправо
 *  - по умолчанию "center"
 * - shortCut - сочетание клавишь (горячая клавиша)
 * - ICON - путь к картинке (иконке), которая будет изображена на кнопке
 * - ICONSCALED - подогнать изображение под размеры объекта (роастянуть наа весь объект)
 *  - YES - - изменить размеры изображения в соответвии с размерами кнопки
 *  - NO - оставить оригинальные размеры изображения (значение по-умолчанию)
 *  
 *  @code
  {button
    top=10
    left=10
    label="Get date"
    action = "($X
                 ($g.ds@RETRIEVE)
                 (data = ($g.ds@getValue 'COL1'))
                 ($g.date@setValue data)
                 ($ret '')
              )"
  }
  {button
    left = 20 top = 10 width = 100 height = 20
    label = "Добавить"
    icon="images\\ab.gif"
    border=empty
    foreground = "#000000"
    action = "($X
                ($g.self@doaction 'createnew docs/run_plan.rml -1, -1, -1')
                ($g.self@doAction 'retrieve tree1')
                ($g.grid_group1@retrieve)
                ($ret '')
            )"
   } 
  @endcode
 */

public interface ButtonAPI extends VisualRmlObjectAPI {

    /**
     * Задать текст надписи на кнопке
     * @param caption текст надписи
     */
    @Keep
    public void setCaption(String caption);
	
    /**
     * Выполнить действие ассоциированное с кнопкой
     */
    @Keep
    public void doAction();
	
}
