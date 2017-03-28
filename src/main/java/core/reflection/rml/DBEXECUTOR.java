package core.reflection.rml;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;

import publicapi.DBExecutorAPI;
import action.api.ScriptApi;
import core.connection.DBMSConnection;
import core.document.Document;
import core.parser.Proper;
import core.rml.RmlObject;


/**
 *
 * @author nuglov
 */
public class DBEXECUTOR extends RmlObject implements DBExecutorAPI, Runnable{

    private static final Logger log = Logger
    .getLogger(DBEXECUTOR.class);
	
	
    private String query = null;
    private Object result = null;
    private boolean blockingExecution = true;
    private boolean sharedConnection = true;
    private CallableStatement cst = null;
    private int resType = Types.NULL;
    private int resIndex = 1;

    private final Object dataLock = new Object();
    private final Object resultLock = new Object();


    public void init(Proper prop, Document doc) {
    	super.init(prop, doc);
        blockingExecution = ((String) prop.get("BLOCKING", "YES")).equalsIgnoreCase("yes");
        sharedConnection = ((String) prop.get("SHAREDCONNECTION", "YES")).equalsIgnoreCase("yes");
        query = (String) prop.get("QUERY");
        setResType((String) prop.get("OUTTYPE", "NULL"));
        resIndex = ((Integer) prop.get("OUTINDEX", new Integer(1))).intValue();
        initComminAPI(prop);
        document = doc;
    }

    private void setResType(String type){
        if(type.equalsIgnoreCase("varchar")){
            resType = Types.VARCHAR;
        }
        else if(type.equalsIgnoreCase("numeric")){
            resType = Types.NUMERIC;
        }
        else if(type.equalsIgnoreCase("char")){
            resType = Types.CHAR;
        }
        else if(type.equalsIgnoreCase("date")){
            resType = Types.DATE;
        }
        else{
            resType = Types.NULL;
        }
    }

    public Object method(String method, Object arg) throws Exception {
        if(method.equalsIgnoreCase("execute")){
            execute();
        }
        else if(method.equalsIgnoreCase("setQuery")){
            setQuery((String) arg);
        }
        else if(method.equalsIgnoreCase("setSharedConnection")){
            setSharedConnection(((String) arg).equalsIgnoreCase("yes"));
        }
        else if(method.equalsIgnoreCase("setBlocking")){
        	setBlocking(((String) arg).equalsIgnoreCase("yes"));
        }
        else if(method.equalsIgnoreCase("getResult")){
            return getResult().toString();
        }
        else if(method.equalsIgnoreCase("registerOutParameter")){
            synchronized(dataLock){
                  final Vector v = (Vector) arg;
                  registerOutParameter(((Double)v.elementAt(0)).intValue(), ((Double)v.elementAt(1)).intValue());
            }
        }

        return new Double(0);

    }

	/**
	 * Установить тип возвращаемого параметра
	 * @param index номер возвращаемого параметра
	 * @param type тип java.sql.Types
	 */
	public void registerOutParameter(int index, int type) {
		resIndex = index;
		resType = type;
	}

	/**
	 * Возвращает результат запроса 
	 * @return значение зарегистрированного параметра
	 */
	public Object getResult() {
		synchronized(resultLock){
		    if(result != null)
		        return result;
		    else
		        return "";
		}
	}

	/**
	 * Использовать общее подключение к БД (подлючение рабочего пространства) или создавать свое 
	 * @param shared если true - то новое подключение не создается, будет использоваться общее 
	 *               подключение рабочего пространства
	 */
	public void setSharedConnection(boolean shared) {
		sharedConnection = shared;
	}

	/**
	 * Выполнять запрос к БД в блокирующем или не блокирующм режиме 
	 * @param blocking если true - при вызове execute создастся новый поток.
	 */
	public void setBlocking(boolean blocking) {
		blockingExecution = blocking;
	}

	/**
	 * Задать текст запроса
	 * @param q - запрос 
	 */
	public void setQuery(String q) {
		synchronized(dataLock){
		    query = q;
		}
	}

	/**
	 * Выполнить запрос
	 */
	public void execute() {
		if(blockingExecution){
		    run();
		}
		else{
		    new Thread(this).start();
		}
	}

	public void run() {
        try {
            Connection con = null;
            synchronized(dataLock){
                if(sharedConnection){
                    con = document.getConnection();
                }
                else{
                    con = DBMSConnection.getConnection(this);
                }
                cst = con.prepareCall(query);
                if(resType != Types.NULL){
                    cst.registerOutParameter(resIndex, resType);
                }
            }

            synchronized(resultLock){
                cst.execute();
                if(resType != Types.NULL)
                    result = cst.getObject(resIndex);
            }
            commit();

        } catch (Exception ex) {
            log.error("!", ex);
        }
        finally{
            if(cst != null) {
                try {
                    cst.close();
                } catch (SQLException ex) {
                    log.error("", ex);
                }
                cst = null;
            }
            DBMSConnection.closeConnection(this);
        }
    }

	@Override
	public Object getValue() throws Exception {
		return this;
	}

	@Override
	public Object getValueByName(String name) throws Exception {
		return null;
	}

	@Override
	public void setValue(Object obj) throws Exception {
	}

	@Override
	public void setValueByName(String name, Object obj) throws Exception {
	}
	
    //Список зависимых компонентов
    private String[] dependences = null;

    //Действие, выполняемое, при изменения основного компонента
    private ScriptApi depExpression = null;

    //Действие, выполняемое, при изменении состояния компонента
    private ScriptApi commitExpression = null;

    protected void initComminAPI(Proper prop){

        String dep = (String) prop.get("DEPLIST");
        if (dep != null) {
            dep = dep.toUpperCase();
            dep = dep.trim();
            StringTokenizer st = new StringTokenizer(dep, ",");
            int count = st.countTokens();
            if (count == 0) {
                return;
            }
            dependences = new String[count];
            for (int i = 0; i < count; i++) {
                dependences[i] = st.nextToken().trim();
            }
        }

         final String comm = (String) prop.get("COMMITEXP");
         if(comm != null){
             commitExpression = ScriptApi.getAPI(comm);
         }

         final String depExp = (String) prop.get("DEPEXP");
         if(depExp != null){
             depExpression = ScriptApi.getAPI(depExp);
         }
    }

//    private void calcDep() {
//        if (dependences == null) {
//            return;
//        }
//        for (int i = 0; i < dependences.length; i++) {
//            Object o = (Object) core.document.findObject(dependences[i]);
//            if (o != null) {
//                o.onPrincipalChange();
//            }
//            else {
//                System.out.println("Object not found for alias "
//                                + dependences[i]);
//            }
//        }
//    }
//
//    private void onPrincipalChange(){
//        try {
//            if (depExpression != null) {
//                depExpression.eval(core.document.getAliases());
//            }
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
    private void onCommit(){
        try {
            if (commitExpression != null) {
                commitExpression.eval(document.getAliases());
            }
        }
        catch (Exception e) {
            log.error("", e);
        }
    }

    protected void commit(){
        onCommit();
//        calcDep();
    }
	
}
