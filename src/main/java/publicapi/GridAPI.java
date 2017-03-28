package publicapi;

import action.api.RTException;
import core.rml.dbi.Datastore;
import proguard.annotation.Keep;
import views.Menu;

import java.util.Map;

/**
 * Интерфейс для управления визуальным элементом "Таблица"
 */
public interface GridAPI extends VisualRmlObjectAPI, RmlContainerAPI {

    //Column metods

    /**
     * Перезачитать Datastore стобца
     *
     * @param col - альяс столбца
     */
    @Keep
    void retrieveColumn(String col) throws RTException;

    /**
     * Установить цвет фона столбца
     *
     * @param col   - индекс столбца
     * @param color - цвет
     */
    @Keep
    void setColumnBgColor(int col, String color);

    /**
     * Установить цвет фона столбца
     *
     * @param col   - альяс столбца
     * @param color - цвет
     */
    @Keep
    void setColumnBgColor(String col, String color);


    /**
     * Установить цвет шрифта столбца
     *
     * @param col   - индекс столбца
     * @param color - цвет
     */
    @Keep
    void setColumnFgColor(int col, String color);

    /**
     * Установить цвет шрифта столбца
     *
     * @param col   - альяс столбца
     * @param color - цвет
     */
    @Keep
    void setColumnFgColor(String col, String color);


    /**
     * Установить цвет фона заголовка столбца
     *
     * @param col   - индекс столбца
     * @param color - цвет
     */
    @Keep
    void setColumnTitleBgColor(int col, String color);


    /**
     * Установить цвет фона заголовка столбца
     *
     * @param col   - альяс столбца
     * @param color - цвет
     */
    @Keep
    void setColumnTitleBgColor(String col, String color);

    /**
     * Установить цвет шрифта заголовка столбца
     *
     * @param col   - индекс столбца
     * @param color - цвет
     */
    @Keep
    void setColumnTitleFgColor(int col, String color);

    /**
     * Установить цвет шрифта заголовка столбца
     *
     * @param col   - альяс столбца
     * @param color - цвет
     */
    @Keep
    void setColumnTitleFgColor(String col, String color);


    /**
     * Установить шрифт столбца
     *
     * @param col  - альяс столбца
     * @param font - шрифт
     */
    @Keep
    void setColumnFont(int col, String font);

    /**
     * Установить шрифт столбца
     *
     * @param col  - альяс столбца
     * @param font - шрифт
     */
    @Keep
    void setColumnFont(String col, String font);


    /**
     * Установить заголовок столбца
     *
     * @param col   - индекс столбца
     * @param title - заголовок
     */
    @Keep
    void setColumnTitle(int col, String title);

    /**
     * Установить заголовок столбца
     *
     * @param col   - альяс столбца
     * @param title - заголовок
     */
    @Keep
    void setColumnTitle(String col, String title);

    /**
     * Установить свойство "visible" столбца
     *
     * @param col     - индекс столбца
     * @param visible - если true - столбец видимый
     */
    @Keep
    void setColumnVisible(int col, boolean visible);

    /**
     * Установить свойство "visible" столбца
     *
     * @param col     - альяс столбца
     * @param visible - если true - столбец видимый
     */
    @Keep
    void setColumnVisible(String col, boolean visible);

    /**
     * Возвращает свойство "visible" столбца
     *
     * @param col - индекс столбца
     * @return если true - столбец видимый
     */
    @Keep
    boolean isColumnVisible(int col);

    /**
     * Возвращает свойство "visible" столбца
     *
     * @param col - альяс столбца
     * @return если true - столбец видимый
     */
    @Keep
    boolean isColumnVisible(String col);

    /**
     * Добавить столбец, не связанный с БД
     *
     * @param params - параметры столбца
     */
    @Keep
    void addTypeColumn(Map<String, Object> params);


    /**
     * Добавить столбец, связанный с БД
     *
     * @param params - параметры столбца
     */
    @Keep
    void addTargetColumn(Map<String, Object> params);

    /**
     * Добавить столбец с выпадающими списками
     *
     * @param params - параметры столбца
     */
    @Keep
    void addComboColumn(Map<String, Object> params);

    /**
     * Добавить столбец с выпадающими списками
     *
     * @param params - параметры столбца
     */
    @Keep
    void addComboTypeColumn(Map<String, Object> params);

    /**
     * Возвращает свойство заголовок столбца
     *
     * @param col - индекс столбца
     * @return заголовок столбца
     */
    @Keep
    String getColumnTitle(int col);

    /**
     * Возвращает свойство заголовок столбца
     *
     * @param col - альяс столбца
     * @return заголовок столбца
     */
    @Keep
    String getColumnTitle(String col);

    /**
     * Возвращает свойство альяс текущего столбца
     *
     * @return альяс столбца
     */
    @Keep
    String getCurrentColumnAlias();

    /**
     * Возвращает индекс текущего столбца Внимание! индексы могут меняться при изменении кол-ва столбцов.
     *
     * @return индекс столбца
     */
    @Keep
    int getCurrentColumnIndex();

    /**
     * Возвращает номер текущей строки
     *
     * @return номер строки
     */
    @Keep
    int getCurrentRowIndex();


    /**
     * Установить текущий столбец
     *
     * @param col индекс столбца
     */
    @Keep
    void setCurrentColumn(int col);

    /**
     * Удалить столбец
     *
     * @param col - индекс столбца
     */
    @Keep
    boolean deleteColumn(int col);

    /**
     * Удалить столбец
     *
     * @param col - альяс столбца
     */
    @Keep
    boolean deleteColumn(String col);

    /**
     * Возвращает кол-во видимых столбцов
     *
     * @return кол-во видимых столбцов
     */
    @Keep
    int getVisibleColumnCount();

    /**
     * Возвращает кол-во столбцов
     *
     * @return кол-во столбцов
     */
    @Keep
    int getColumnCount();


    //Row methods

    /**
     * Установить текущую строку
     *
     * @param row - строка
     */
    @Keep
    void setCurrentRow(int row);

    /**
     * Добавить новую строку
     */
    @Keep
    void addRow();

    /**
     * Удалить текущуюстроку
     */
    @Keep
    void deleteRow();

    /**
     * Установить цвет фона строки
     *
     * @param row   - строка
     * @param color - цвет
     */
    @Keep
    void setRowBGColor(int row, String color);

    /**
     * Уствновить цвет шрифта
     *
     * @param row   - строка
     * @param color - цвет
     */
    @Keep
    void setRowFGColor(int row, String color);

    /**
     * Установить цвет фона заголовка строки
     *
     * @param row   - строка
     * @param color - цвет
     */
    @Keep
    void setRowTitleBGColor(int row, String color);

    /**
     * Установить цвет шрифта заголовка строки
     *
     * @param row   - строка
     * @param color - цвет
     */
    @Keep
    void setRowTitleFGColor(int row, String color);

    /**
     * Установить шрифт строки
     *
     * @param row  - строка
     * @param font - шрифт
     */
    @Keep
    void setRowFont(int row, String font);

    /**
     * Установить заголовк строки
     *
     * @param row   - строка
     * @param title - заголовок
     */
    @Keep
    void setRowTitle(int row, String title);

    /**
     * Удалить заголовок строки
     *
     * @param row - строка
     */
    @Keep
    void deleteRowTitle(int row);

    /**
     * Возвращает кол-во строк
     *
     * @return сол-во строк
     */
    @Keep
    int getRowCount();


    //Grid methods

    /**
     * Возвращает значение ячеки из текущей строки и заданного столбца
     *
     * @param col - альяс столбца
     * @return значение ячекий
     */
    @Keep
    Object currentValue(String col) throws RTException;

    /**
     * Возвращает значение из ячейки
     *
     * @param row - номер строки
     * @param col - индекс столбца
     * @return значение ячекий
     */
    @Keep
    Object getValue(int row, int col);

    /**
     * Возвращает значение из ячейки
     *
     * @param row - номер строки
     * @param col - альяс столбца
     * @return значение ячекий
     */
    @Keep
    Object getValue(int row, String col);

    /**
     * Возвращает значения ячеек заданного столбца входящих в выделенную область
     *
     * @param col - альяс столбца
     * @return массив значений
     */
    @Keep
    Object[] getSelectionValues(String col);

    /**
     * Возвращает номера строк входящих в выделенную область
     *
     * @return массив номеров строк
     */
    @Keep
    int[] getSelection();

    /**
     * Перезачитать данный из БД
     *
     * @param keepFilters оставить или сбросить текущие значения фильтров
     * @return кол-во строк
     */
    @Keep
    int retrieve(boolean keepFilters);

    /**
     * Начать редактирование текущей ячейки
     */
    @Keep
    void edit();

    /**
     * Задать Datastore
     *
     * @param ds
     */
    @Keep
    void setDatastore(Datastore ds);

    /**
     * Получить Datastore, связаную с этой таблицей
     *
     * @return Datastore
     */
    @Keep
    Datastore getDatastore();

    /**
     * @return Datastore
     */
    @Keep
    Datastore getAllDatastore();

    /**
     * Вычисление суммы столбца
     *
     * @param col - альяс столбца
     * @return сумму значений во всех ячейках
     */
    @Keep
    double sum(String col) throws RTException;

    /**
     * Сохранение таблицы в файл
     */
    @Keep
    void dumpToFile();

    /**
     * Получение меню
     *
     * @return Menu
     */
    @Keep
    Menu getMenu();

    /**
     * Задать меню
     *
     * @param m Menu
     */
    @Keep
    void setMenu(Menu m);

    /**
     * Перерисовать таблицу
     */
    @Keep
    void repaint();

    /**
     */
    @Keep
    void invertSelection();

    /**
     * Добавить строку в список выбранных
     *
     * @param row - номер строки
     */
    @Keep
    void fastSelection(int row);

    /**
     * Установить выделенную строку
     *
     * @param row номер строки
     */
    @Keep
    void setSelection(int row);

    /**
     * Выбрать все значения
     */
    @Keep
    void selectAll();

    /**
     * Вернуть кол-во строк таблицы
     *
     * @return кол-во строк
     */
    @Keep
    int size();

    /**
     * Выравнять таблицу (подгоняет размер столбцов, чтобы они все уместились в заданный размер таблицы (размер контейнера, содержащего таблицу))
     */
    @Keep
    void allign();

    /**
     * Задать цвет фона ячейки
     *
     * @param row   номер строки
     * @param col   индекс столбца
     * @param color цвет
     */
    @Keep
    void setCellBGColor(int row, int col, String color);

    /**
     * Задать цвет шрифта ячейки
     *
     * @param row   номер строки
     * @param col   индекс столбца
     * @param color цвет
     */
    @Keep
    void setCellFGColor(int row, int col, String color);

    /**
     * Задать шрифт ячейки
     *
     * @param row  номер строки
     * @param col  индекс столбца
     * @param font шрифт
     */
    @Keep
    void setCellFont(int row, int col, String font);

    /**
     * Показать диалог поиска
     */
    @Keep
    void showSearchDialog();

    /**
     * Установить значение в ячейку
     *
     * @param row   номер строки
     * @param col   номер столбца
     * @param value значение для вставки
     */
    @Keep
    void setValue(int row, int col, Object value);

    /**
     * Установить значение в ячейку
     *
     * @param row   номер строки
     * @param col   альяс столбца
     * @param value значение для вставки
     */
    @Keep
    void setValue(int row, String col, Object value);

    /**
     * Оповестить слушателей
     */
    @Keep
    void notifySubscribers();

    /**
     * Значение разрешения на редактирование таблицы
     *
     * @return если true то редактирование разрешено
     */
    @Keep
    boolean isEditable();

    /**
     * Установка разрешения на редактирование таблицы
     *
     * @param editable если true то редактирование разрешено
     */
    @Keep
    void setEditable(boolean editable);
}
