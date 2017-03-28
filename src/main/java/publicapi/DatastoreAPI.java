package publicapi;

import java.sql.SQLException;

import proguard.annotation.Keep;
import action.api.RTException;
import core.connection.BadPasswordException;
import core.connection.ConnectException;
import core.rml.dbi.Datastore;
import core.rml.dbi.UpdateException;

/**
 * Объект для работы с БД
 * 
 * Свойства:
 *- editable - Возмозноть редакрирования данных.
 *  - "Yes" - редактирование возможно
 *  - "No" - редактирование не возможно
 *  - Значение по-умолчанию "No"
 *- query - запрос к БД, может содержать вычисляемые вставки [[calc]].
 *- unique -
 *- updateable - возможность сохранения данных в БД.
 *  - "Yes" - возможно
 *  - "No" - не возможно
 *  - Значение по-умолчанию "No"
 *- links - какое-то свойство :)
 *- defaults - значенияпо-умолчанию
 *- head - какое-то свойство :) 
 *  - "Yes"
 *  - "No"
 *  - Значение по-умолчанию "No"
 *- Actions - какое-то свойство :) 
 *- insDep - какое-то свойство :)
 *- upDep - какое-то свойство :)
 *- delDep - какое-то свойство :)
 *- selAction - какое-то свойство :)
 *- sortOrder - какое-то свойство :)
 *- defRow - какое-то свойство :) 
 *
 * @code  
  {datastore
    alias=ds
    query = "select 'Время' col1 from dual"
  }
 @endcode
 * 
 */
public interface DatastoreAPI extends RmlObjectAPI, RmlContainerAPI, RetrieveableAPI {

    /**
     * устанавливает,доступен ли обьект только для чтения
     * @param readOnly - только чтение, если true 
     */
    @Keep
    public void setReadOnly(boolean readOnly);
    
    /**
     * filed = ~alias~,
     * @internal
     */
    @Keep
    public void setDefaults(String def);
    
    /**
     * Очищает коллекцию данных
     */
    @Keep
    public void clear();
    
    /**
     * удаляет строку
     *
     * @param row - индекс строки
     */
    @Keep
    public void delRow(int row);
   
    /**
     * создает новую строку и возвращает ее ИНДЕКС Вновь созданная строка
     * СТАНОВИТСЯ ТЕКУЩЕ
     * @return номер новой строки
     */
    @Keep
    public int newRow();
  
    /**
     * Возвращает значение столбца выборки из текущей строк
     *
     * @param column - alias (или target) столбца
     * @throws RTException
     */
    @Keep
    public Object getValue(String column) throws RTException;
	
    /**
     * Возвращает значение столбца выборки из текущей строк
     *
     * @param column - номер столбца
     * @throws RTException
     */
    @Keep
    public Object getValue(int column) throws RTException;
    
    /**
     * Возвращает значение столбца выборки из строки с ИНДЕКСОМ row
     * @param row - номер строки
     * @param column - номер столбца
     */
    @Keep
    public Object getValue(int row, int column);
    
    /**
     * Возвращает значение столбца выборки из строки с ИНДЕКСОМ row
     * @param row - номер строки
     * @param column - alias (или target) столбца
     * 
     * @throws RTException
     */
    @Keep
    public Object getValue(int row, String column);

    /**
     * Устанавливает значение столбца выборки для текущей строк
     * 
     * @param column - alias (или target) столбца
     * @param value - значение  
     */
    @Keep
    public void setValue(String column, Object value);

    /**
     * Устанавливает значение столбца выборки для текущей строк
     * @param column - номер столбца
     * @param value - значение  
     */
    @Keep
    public void setValue(int column, Object value);
    
    /**
     * Устанавливает значение столбца выборки для строки с ИНДЕКСОМ row
     * 
     * @param row
     * @param column
     * @param value
     */
    @Keep
    public void setValue(int row, String column, Object value);

    /**
     * Добавляе новый столбец
     * @param typeCol - тип столбца (java.sql.Types)
     * @return имя столбца
     */
    @Keep
    public String addColumn(int typeCol);
    
    /**
     * синхронизирует содержимое внутреннего буфера с БД
     * @throws BadPasswordException 
     * @throws SQLException
     */
    @Keep
    public void update() throws UpdateException, BadPasswordException, SQLException;
    
    /**
     * Заполняет обьект данными из базы.Данный метод ДОЛЖЕН вызываться перед
     * ЛЮБЫМИ метода обьекта, оперирующего с данны
     */
    @Keep
    public int retrieve() throws Exception;
    
    /**
     * Установка запроса, для выборки данных
     * @param sql - текст запроса (может содержать макрос)
     */
    @Keep
    public void setSql(String sql);

    /**
     * Возвращает текст запроcа
     */
    @Keep
    public String getSql();
    
    /**
     * Возвращает текущую строку Нумерация строк начинается с 0
     */
    @Keep
    public int getCurRow();
    
    /**
     * Устанавливает текущую строку Нумерация строк начинается с 0
     * @param row - номер строки
     */
    @Keep
    public void setCurrentRow(int row);
    
    /**
     * Переходит к следующей по порядку строке
     */
    @Keep
    public void nextRow();
   
    /**
     * Возвращает имена столбцов результирующего набора
     */
    @Keep
    public String[] getNames();
    
    /**
     * Возвращает количество столбцов результирующего набора
     */
    @Keep
    public int getCountColumns();

    /**
     * Возвращает количество строк результирующего набора
     * @return кол-во строк
     */
    @Keep
    public int getRowCount();

    /**
     * Возвращает тип столбца
     */
    @Keep
    public int getType(String column);
    
    /**
     * Возвращает тип столбца
     */
    @Keep
    public int getType(int column);
    
    /**
     * Создает новую DATASTORE, содержащую данные из нескольких Datastore
     * @param ds - массив Datastore (если аргумент null, то вернет копию текущей Datastore)
     * @return результирующую Datastore
     */
    @Keep
    public Datastore dsConcat(Datastore[] ds);
    
    /**
     * делает текущей нулевую строк
     */
    @Keep
    public void setFirst();
   

    /**
     * по имени столбца возвращает его номер
     */
    @Keep
    public int getColumn(String colnam);
    
	/**
	 * Выполнить произвольный запрос
	 *  
	 * @param query - запрос
	 * @throws SQLException
	 * @throws ConnectException
	 * @throws BadPasswordException
	 */
    @Keep
	public void executeQuery(String query) throws SQLException,
			ConnectException, BadPasswordException;
    
	/**
	 * Устанавливает ограничение на кол-во строк 
	 * @param maxRows
	 */
    @Keep
	public void setMaxFethRow(int maxRows);
    
    /**
     * Оповещает, связанные с Datastore, визуальные компоненты о том что данные изменились
     */
    @Keep
    public void notifyViews();

   
}
