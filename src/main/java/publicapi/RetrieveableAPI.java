package publicapi;

import java.sql.SQLException;

import proguard.annotation.Keep;
import core.connection.BadPasswordException;
import core.rml.dbi.exception.UpdateException;

/**
 * Объект, предназначенный для работы с базой данных. Данный объект либо непосредственно работает с БД, 
 * либо может в себе содержать такие объекты, котрые в этом случае зачастую служат источниками данных 
 * для данного объекта. 
 *
 */
public interface RetrieveableAPI {
    public void fromDS();

    /**
     * Выполнить запрос к БД
     * @return кол-во строк ренувшихся в запросе
     * @throws Exception
     */
    @Keep
    public int retrieve() throws Exception;

    public void toDS();

    /**
     * Сохранить измененные данные в БД
     * @return кол-во строк ренувшихся в запросе
     * @throws Exception
     */
    @Keep
    public void update() throws UpdateException, BadPasswordException, SQLException;
}
