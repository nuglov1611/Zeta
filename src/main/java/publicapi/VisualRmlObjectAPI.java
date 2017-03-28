package publicapi;

import proguard.annotation.Keep;

import java.awt.*;

/**
 * Интерфейс для Rml-объектов, имеющих графическое представление.
 * Все визуалные компоненты имеют обязательный набор параметров:
 * - BACKGROUND - цвет фона
 * - FOREGROUND - цвет шрифта
 * - FONT - параметры шрифта (название, размер, стиль)
 * - либо можно задавать параметры отдельно друг от друга:
 * - FONT_FACE
 * - FONT_FAMILY
 * - FONT_SIZE
 * - LEFT - Х координата левого верхнего угла
 * - TOP - Y координата левого верхнего угла
 * - WIDTH - длина
 * - HEIGHT - высота
 * - VISIBLE - (yes/no) видимость компонента
 * - FIRSTFOCUS - (yes/no) претендует на получение фокуса при создании документа
 * - POSITION - положение компонента если в контейнере выбран Border Layout (CENTER, EAST, WEST, SOUTH, NORTH)
 * - TOOLTIPTEXT - текст всплывающей подсказки
 *
 * @author nuglov
 *         {@inheritDoc}
 */
public interface VisualRmlObjectAPI extends RmlObjectAPI {

    /**
     * Возвращает размеры графического компонента.
     *
     * @return массив размеров (width, height)
     */
    @Keep
    Dimension getSize();

    /**
     * Возвращает значение признака видимости графического компонента.
     *
     * @return true - видимый
     */
    @Keep
    boolean isVisible();

    /**
     * Возвращает координаты графического компонента.
     *
     * @return координаты верхнего левого угла
     */
    @Keep
    Point getPosition();

    /**
     * Установить размеры объекта (ширину и высоту)
     *
     * @param w - ширина
     * @param h - высота
     */
    @Keep
    void setSize(int w, int h);

    /**
     * Установить высоту визуального компонента
     *
     * @param h высота
     */
    @Keep
    void setHeight(int h);

    /**
     * Установить ширину визуального компонента
     *
     * @param w ширина
     */
    @Keep
    void setWidth(int w);

    /**
     * Получить фокус
     */
    @Keep
    void requestFocus();

    /**
     * Поволяет узнать возможность графического компонента принимать фокус
     *
     * @return true - если объект может принимать фокус, false - если не может
     */
    @Keep
    boolean isFocusable();

    /**
     * Задать возможность графического компонента принимать фокус
     *
     * @param focusable true - компонент может принимать фокус, false - не может принимать фокус
     */
    @Keep
    void setFocusable(boolean focusable);

    /**
     * Задать координаты графического компонента по горизонтали и вертикали
     *
     * @param x координата по горизонтали
     * @param y координата по вертикали
     */
    @Keep
    void setLocation(int x, int y);

    /**
     * Задать координату графического компонента по горизонтали (координата левой границы)
     *
     * @param x координата
     */
    @Keep
    void setLeft(int x);

    /**
     * Задать координату графического компонента по вертикали (координата верхней границы)
     *
     * @param y координата
     */
    @Keep
    void setTop(int y);

    /**
     * Задать признак "активности" графического компонента
     *
     * @param enabled признак (true - компонент активный, false - не активный("серый"))
     */
    @Keep
    void setEnabled(boolean enabled);

    /**
     * Задать признак видимости графического компонента
     *
     * @param visible признак (true - видимый, false - не видимый)
     */
    @Keep
    void setVisible(boolean visible);

}
