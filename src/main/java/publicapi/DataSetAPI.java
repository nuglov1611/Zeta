package publicapi;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import proguard.annotation.Keep;
import core.connection.BadPasswordException;

/**
 * Интерфейс для манипулирования данными в БД. Предназначен в первую очередь для работы с типом данных BLOB
 * @author uglov
 *
 */
public interface DataSetAPI extends RmlObjectAPI {

	
	/**
	 * Установить текст SQL-запроса
	 * @param q текст запроса
	 */
	@Keep
	public void setQuery(String q);
	
	/**
	 * Вернуть выражение SQL-запроса
	 * @return запрос
	 */
	@Keep
	public String getQuery();

	/**
	 * Удалить стоку
	 * @param row номер строки
	 */
	@Keep
    public void deleteRow(int row);
    
    /**
     * Установить текущую строку
     * @param rowNum
     * @throws SQLException
     */
	@Keep
    public void setCurRow(int rowNum) throws SQLException;
    
    /** 
     * Сохранить значения в БД
     * @throws SQLException
     * @throws FileNotFoundException
     */
	@Keep
    public void update() throws SQLException, FileNotFoundException;


    /**
     * Установить значение в ячейку
     * @param row номер строки
     * @param column номер столбца
     * @param value значение (если тип столбца Blob, то данный параметр может быть типа byte[] (непосредственно массив для сохранения в Blob); 
     *              либо тип String, в этом случае считается что запись ведется из файла, а парметр содержить путь к файлу)
     */
	@Keep
    public void setValue(int row, int column, Object value);

    /**
     * Вернуть значение из ячейки
     * @param row номер строки
     * @param col номер столбца
     * @return значение (если тип стобца Blob то тип возвращаемого значения будет byte[])
     * @throws SQLException
     * @throws IOException
     */
	@Keep
    public Object getValue(int row, int col) throws SQLException, IOException;
    
    /**
     * Вернуть значение из ячейки текущей строки
     * @param col номер столбца
     * @return значение (если тип стобца Blob то тип возвращаемого значения будет byte[])
     * @throws SQLException
     * @throws IOException
     */
	@Keep
    public Object getValue(int col) throws SQLException, IOException;
    
    /**
     * Вернуть значение из ячейки текущей строки
     * @param column альяс столбца
     * @return значение (если тип стобца Blob то тип возвращаемого значения будет byte[])
     * @throws SQLException
     * @throws IOException
     */
	@Keep
    public Object getValue(String column) throws SQLException, IOException;
    
    /**
     * Обновить данные в DataSet (выполнить запрос в БД)
     * @return кол-во возвращенных строк 
     * @throws BadPasswordException если не верные логин/пароль при подключении к БД 
     * @throws SQLException
     */
	@Keep
    public int retrieve() throws BadPasswordException, SQLException;
}
