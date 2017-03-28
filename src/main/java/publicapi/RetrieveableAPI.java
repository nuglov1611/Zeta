package publicapi;

import proguard.annotation.Keep;

import java.sql.SQLException;

/**
 * Объект, предназначенный для работы с базой данных. Данный объект либо непосредственно работает с БД,
 * либо может в себе содержать такие объекты, котрые в этом случае зачастую служат источниками данных
 * для данного объекта.
 */
public interface RetrieveableAPI {
    void fromDS();

    /**
     * Выполнить запрос к БД
     *
     * @return кол-во строк ренувшихся в запросе
     * @throws Exception
     */
    @Keep
    int retrieve() throws Exception;

    void toDS();

    /**
     * Сохранить измененные данные в БД
     *
     * @return кол-во строк ренувшихся в запросе
     * @throws Exception
     */
    @Keep
    void update() throws SQLException;
}
