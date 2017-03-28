package core.reflection.rml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import loader.ZetaProperties;
import oracle.jdbc.OracleResultSet;
import oracle.sql.BLOB;

import org.apache.log4j.Logger;

import publicapi.DataSetAPI;
import action.api.RTException;
import core.connection.BadPasswordException;
import core.document.Document;
import core.parser.Proper;
import core.rml.RmlConstants;
import core.rml.RmlObject;

/**
 */
public class DATASET extends RmlObject implements DataSetAPI {

    private static final Logger log = Logger.getLogger(DATASET.class);

    private class DelData extends UpdData{
    }
    
    private class UpdData extends Hashtable<Integer, Object> {
        protected void update(int column, Object data) {
            put(column, data);
        }
    }

    private String                      query      = null;

    private Connection                  conn       = null;

    private OracleResultSet             rset       = null;

    private Statement                   stmt       = null;

    private int                         rowCount   = 0;

    private int                         currow     = -1;

    private Hashtable<Integer, UpdData> updateData = new Hashtable<Integer, UpdData>();

    public void init(Proper prop, Document doc) {
    	super.init(prop, doc);

        query = (String) prop.get("QUERY");
    }

    @Override
    public Object method(String method, Object arg) throws Exception {
        Object res = null;
        if (method.equalsIgnoreCase(RmlConstants.GET_QUERY)) {
            res = getQuery();
        }
        else if (method.equals(RmlConstants.SET_QUERY)) {
            if (arg != null && arg instanceof String) {
                setQuery(arg.toString());
            }
        }
        else if (method.equals("SETVALUE")) {
            try {
                Vector<Object> v = (Vector<Object>) arg;
                if(v == null){
                    return -1;
                }
                int crow = (v.size() < 3)?currow:((Double) v.elementAt(0)).intValue();
                Object cdata = (v.size() < 3) ? v.elementAt(1) : v.elementAt(2);
                Object tcol = (v.size() < 3) ? v.elementAt(0) : v.elementAt(1);
                int ccol = (tcol instanceof Double) ? ((Double)tcol).intValue() : rset.findColumn(tcol.toString());
                setValue(crow, ccol, cdata);
                res = new Double(0);
            }
            catch (ClassCastException e) {
                log.error("Shit happens", e);
                throw new RTException(
                        "CastException",
                        "method SETVALUE: wrong parameters");
            }

        }
        else if (method.equals("GETVALUE")) {

            if (arg instanceof String) {
                res = getValue((String) arg);
            }
            else if (arg instanceof Double) {
                res = getValue(((Double) arg).intValue());
            }
            else if (arg instanceof Vector){
                Vector<Object> v = (Vector<Object>) arg;
                int crow = ((Double) v.elementAt(0)).intValue();
                Object tcol = v.elementAt(1);
                int ccol = (tcol instanceof Double) ? ((Double)tcol).intValue() : rset.findColumn(tcol.toString());
                
                res = getValue(crow, ccol);
            }
            else {
                throw new RTException(
                        "CastException",
                        "method GETVALUE: wrong parameters");
            }
        }
        else if (method.equals("DELETEROW")) {
            if(arg == null){
                deleteRow(currow);
            }
            else if(arg instanceof Double){
                deleteRow(((Double) arg).intValue());
            }
            else {
                throw new RTException(
                        "CastException",
                        "method DELETEROW: wrong parameters");
            }
        }
        else if (method.equals("RETRIEVE")) {
            retrieve();
            res = new Double(rowCount);
        }
        else if (method.equalsIgnoreCase("UPDATE")) {
            update();
        }
        else if (method.equalsIgnoreCase("SETCURROW")) {
            setCurRow(((Double) arg).intValue());
        }
        else {
            throw new RTException("HasNotMethod", "method " + method
                    + " not defined in class views.CheckBox!");
        }

        return res;
    }

	/**
	 * Установить текст SQL-запроса
	 * @param q текст запроса
	 */
	public void setQuery(String q) {
		query = q;
	}

	/**
	 * Вернуть выражение SQL-запроса
	 * @return запрос
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * Удалить стоку
	 * @param row номер строки
	 */
    public void deleteRow(int row) {
        updateData.put(row, new DelData());
    }

    /**
     * Установить текущую строку
     * @param rowNum
     * @throws SQLException
     */
    public void setCurRow(int rowNum) throws SQLException {
        if (rset != null) {
            if(rowNum > 0)
                rset.absolute(rowNum);
            currow = rowNum;
        }
    }

    /** 
     * Сохранить значения в БД
     * @throws SQLException
     * @throws FileNotFoundException
     */
    public void update() throws SQLException, FileNotFoundException {
    	try{
    	    conn.setAutoCommit(true);
	        Enumeration<Integer> rows = updateData.keys();
	        int rown = 0;
	        for (; rows.hasMoreElements();) {
	            rown = rows.nextElement();
	
	            final UpdData curr = updateData.get(rown);
	            if(curr instanceof DelData){
	                delRowFromDB(rown);
	            }else{
    	            final Enumeration<Integer> columns = curr.keys();
    	            boolean need_insert = false;
    	            if (rown > rowCount) { // Новая строка
    	                rset.moveToInsertRow();
    	                need_insert = true;
    	            }
    	            else {// Update значений
    	                rset.absolute(rown);
    	            }
    	            int columnN = 0;
    	            boolean updated = false;
    	            for (; columns.hasMoreElements();) {
    	                columnN = columns.nextElement();
    	                switch (rset.getMetaData().getColumnType(columnN)) {
    	                case Types.BLOB:
    	                	BLOB blb = BLOB.createTemporary(conn, false, BLOB.DURATION_SESSION);
    
    	                	writeBlob(curr.get(columnN).toString(), blb);
                            rset.updateBLOB(columnN, blb);
    	                    updated = true;
    	                    break;
    	                case Types.CLOB:
    	                    break;
    	                default:
    	                    rset.updateObject(columnN, curr.get(columnN));
    	                    updated = true;
    	                    break;
    	                }
    	            }
    	            if (updated) {
    	                if (need_insert) {
    	                    rset.insertRow();
    	                    rset.moveToCurrentRow();
    	                }else{
    	                	rset.updateRow();
    	                }
    	                //conn.commit();
    	            }
    	        }
	        }
    	}
    	finally{
    		conn.rollback();
            conn.setAutoCommit(false);

    	}
    }

    private void delRowFromDB(int row) throws SQLException {
        rset.absolute(row);
        rset.deleteRow();
        conn.commit();
    }

    private byte[] readBlob(BLOB sourceBlob) throws SQLException, IOException {
        InputStream is = sourceBlob.getBinaryStream();
        int chunk = (int) sourceBlob.getChunkSize();
        
        byte[] res = null;
        byte[] buff = new byte[chunk];
        final Vector<byte[]> file = new Vector<byte[]>();
        int rsize = 0;
        int totalread = 0;
        while ((rsize = is.read(buff)) != -1) {
        	file.addElement(buff.clone());
            totalread += rsize;
        }
        
        res = new byte[totalread];
        Enumeration<byte[]> e = file.elements();
        for(int i=0; e.hasMoreElements(); ){
        	final byte[] b = e.nextElement();
        	int len = 0;
        	if(i+b.length > totalread){
        		len = totalread - i;
        	}else{
        		len = b.length;
        	}
        		
        	System.arraycopy(b, 0, res, i, len);
        	i += len;
        }
        
        
        return res;
    }

    private int writeBlob(Object file, BLOB destBlob) throws SQLException {
    	destBlob.clearCachedData();
        int totbytesWritten = 0;
        try {
        	if(file instanceof String){
	            File inputFile = new File(file.toString());
	
	            OutputStream out = destBlob.setBinaryStream(1);
	            
	            FileInputStream in = new FileInputStream(inputFile);
	            byte[] buffer = new byte[destBlob.getBufferSize()];
	            int cnt;
	            while ((cnt = in.read(buffer, 0, destBlob.getBufferSize())) != -1) {
	                out.write(buffer, 0, cnt);
	                out.flush();
	                totbytesWritten +=cnt;
	            }
	            in.close();
	            out.close();
        	}else if(file instanceof byte[]){
	            OutputStream out = destBlob.setBinaryStream(1);

	            byte[] buffer = (byte[]) file;
	            int cnt;
	            while (totbytesWritten < buffer.length){
	            	if((buffer.length - totbytesWritten) >= destBlob.getBufferSize())
	            		cnt = destBlob.getBufferSize();
	            	else
	            		cnt = (buffer.length - totbytesWritten);
	            	
	                out.write(buffer, totbytesWritten, cnt);
	                out.flush();
	                totbytesWritten +=cnt;
	            }
        	}
        }
        catch (FileNotFoundException e) {
            log.error("!", e);
        }
        catch (SQLException e) {
            log.error("!", e);
        }
        catch (IOException e) {
            log.error("!", e);
        }
        return totbytesWritten;
    }

    /**
     * Установить значение в ячейку
     * @param row номер строки
     * @param column номер столбца
     * @param value значение
     */
    public void setValue(int row, int column, Object value) {
        if (rset != null) {
            if (updateData.get(row) == null) {
                updateData.put(row, new UpdData());
            }
            updateData.get(row).update(column, value);
        }
    }

    /**
     * Вернуть значение из ячейки
     * @param row номер строки
     * @param col номер столбца
     * @return значение
     * @throws SQLException
     * @throws IOException
     */
    public Object getValue(int row, int col) throws SQLException, IOException {
        Object res = null;
        if(rset.absolute(row)){
            if (rset.getMetaData().getColumnType(col) == Types.BLOB) {
                res = readBlob(rset.getBLOB(col));
            }
            else {
                res = rset.getObject(col);
            }
        }
        
        return res;
    }

    
    /**
     * Вернуть значение из ячейки текущей строки
     * @param col номер столбца
     * @return значение 
     * @throws SQLException
     * @throws IOException
     */
    public Object getValue(int col) throws SQLException, IOException {
        return getValue(currow, col);
    }

    /**
     * Вернуть значение из ячейки текущей строки
     * @param column альяс столбца
     * @return значение 
     * @throws SQLException
     * @throws IOException
     */
    public Object getValue(String column) throws SQLException, IOException {
        return getValue(currow, rset.findColumn(column));
    }

    /**
     * Обновить данные в DataSet (выполнить запрос в БД)
     * @return кол-во возвращенных строк 
     * @throws BadPasswordException если не верные логин/пароль при подключении к БД 
     * @throws SQLException
     */
    public int retrieve() throws BadPasswordException, SQLException {
        String sql = "";
        rowCount = 0;

        try {
            sql = (String) document.calculateMacro(getQuery());
        }
        catch (Exception e) {
            log.error("Shit happens", e);
            if (ZetaProperties.dstore_debug > 1) {
                log.error(" ERROR IN SQL EXPRESSION:", e);
            }
        }
        if (ZetaProperties.dstore_debug > 1) {
            log.debug("sql=" + sql);
        }

        if (sql == null) {
            return -1;
        }

        conn = document.getConnection();
        stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);

        rset = (OracleResultSet) stmt.executeQuery(sql);
        
        
        while (rset.next()) {
            rowCount++;
        }

        if(rowCount > 0)
            setCurRow(1);
        else
            setCurRow(0);

        return rowCount;
    }
}
