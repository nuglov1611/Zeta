package publicapi;

import java.sql.SQLException;

import proguard.annotation.Keep;
import action.api.RTException;
import core.connection.BadPasswordException;
import core.connection.ConnectException;
import core.rml.dbi.Datastore;
import core.rml.dbi.UpdateException;

/**
 * ������ ��� ������ � ��
 * 
 * ��������:
 *- editable - ���������� �������������� ������.
 *  - "Yes" - �������������� ��������
 *  - "No" - �������������� �� ��������
 *  - �������� ��-��������� "No"
 *- query - ������ � ��, ����� ��������� ����������� ������� [[calc]].
 *- unique -
 *- updateable - ����������� ���������� ������ � ��.
 *  - "Yes" - ��������
 *  - "No" - �� ��������
 *  - �������� ��-��������� "No"
 *- links - �����-�� �������� :)
 *- defaults - ����������-���������
 *- head - �����-�� �������� :) 
 *  - "Yes"
 *  - "No"
 *  - �������� ��-��������� "No"
 *- Actions - �����-�� �������� :) 
 *- insDep - �����-�� �������� :)
 *- upDep - �����-�� �������� :)
 *- delDep - �����-�� �������� :)
 *- selAction - �����-�� �������� :)
 *- sortOrder - �����-�� �������� :)
 *- defRow - �����-�� �������� :) 
 *
 * @code  
  {datastore
    alias=ds
    query = "select '�����' col1 from dual"
  }
 @endcode
 * 
 */
public interface DatastoreAPI extends RmlObjectAPI, RmlContainerAPI, RetrieveableAPI {

    /**
     * �������������,�������� �� ������ ������ ��� ������
     * @param readOnly - ������ ������, ���� true 
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
     * ������� ��������� ������
     */
    @Keep
    public void clear();
    
    /**
     * ������� ������
     *
     * @param row - ������ ������
     */
    @Keep
    public void delRow(int row);
   
    /**
     * ������� ����� ������ � ���������� �� ������ ����� ��������� ������
     * ���������� ������
     * @return ����� ����� ������
     */
    @Keep
    public int newRow();
  
    /**
     * ���������� �������� ������� ������� �� ������� �����
     *
     * @param column - alias (��� target) �������
     * @throws RTException
     */
    @Keep
    public Object getValue(String column) throws RTException;
	
    /**
     * ���������� �������� ������� ������� �� ������� �����
     *
     * @param column - ����� �������
     * @throws RTException
     */
    @Keep
    public Object getValue(int column) throws RTException;
    
    /**
     * ���������� �������� ������� ������� �� ������ � �������� row
     * @param row - ����� ������
     * @param column - ����� �������
     */
    @Keep
    public Object getValue(int row, int column);
    
    /**
     * ���������� �������� ������� ������� �� ������ � �������� row
     * @param row - ����� ������
     * @param column - alias (��� target) �������
     * 
     * @throws RTException
     */
    @Keep
    public Object getValue(int row, String column);

    /**
     * ������������� �������� ������� ������� ��� ������� �����
     * 
     * @param column - alias (��� target) �������
     * @param value - ��������  
     */
    @Keep
    public void setValue(String column, Object value);

    /**
     * ������������� �������� ������� ������� ��� ������� �����
     * @param column - ����� �������
     * @param value - ��������  
     */
    @Keep
    public void setValue(int column, Object value);
    
    /**
     * ������������� �������� ������� ������� ��� ������ � �������� row
     * 
     * @param row
     * @param column
     * @param value
     */
    @Keep
    public void setValue(int row, String column, Object value);

    /**
     * �������� ����� �������
     * @param typeCol - ��� ������� (java.sql.Types)
     * @return ��� �������
     */
    @Keep
    public String addColumn(int typeCol);
    
    /**
     * �������������� ���������� ����������� ������ � ��
     * @throws BadPasswordException 
     * @throws SQLException
     */
    @Keep
    public void update() throws UpdateException, BadPasswordException, SQLException;
    
    /**
     * ��������� ������ ������� �� ����.������ ����� ������ ���������� �����
     * ������ ������ �������, ������������ � �����
     */
    @Keep
    public int retrieve() throws Exception;
    
    /**
     * ��������� �������, ��� ������� ������
     * @param sql - ����� ������� (����� ��������� ������)
     */
    @Keep
    public void setSql(String sql);

    /**
     * ���������� ����� �����c�
     */
    @Keep
    public String getSql();
    
    /**
     * ���������� ������� ������ ��������� ����� ���������� � 0
     */
    @Keep
    public int getCurRow();
    
    /**
     * ������������� ������� ������ ��������� ����� ���������� � 0
     * @param row - ����� ������
     */
    @Keep
    public void setCurrentRow(int row);
    
    /**
     * ��������� � ��������� �� ������� ������
     */
    @Keep
    public void nextRow();
   
    /**
     * ���������� ����� �������� ��������������� ������
     */
    @Keep
    public String[] getNames();
    
    /**
     * ���������� ���������� �������� ��������������� ������
     */
    @Keep
    public int getCountColumns();

    /**
     * ���������� ���������� ����� ��������������� ������
     * @return ���-�� �����
     */
    @Keep
    public int getRowCount();

    /**
     * ���������� ��� �������
     */
    @Keep
    public int getType(String column);
    
    /**
     * ���������� ��� �������
     */
    @Keep
    public int getType(int column);
    
    /**
     * ������� ����� DATASTORE, ���������� ������ �� ���������� Datastore
     * @param ds - ������ Datastore (���� �������� null, �� ������ ����� ������� Datastore)
     * @return �������������� Datastore
     */
    @Keep
    public Datastore dsConcat(Datastore[] ds);
    
    /**
     * ������ ������� ������� �����
     */
    @Keep
    public void setFirst();
   

    /**
     * �� ����� ������� ���������� ��� �����
     */
    @Keep
    public int getColumn(String colnam);
    
	/**
	 * ��������� ������������ ������
	 *  
	 * @param query - ������
	 * @throws SQLException
	 * @throws ConnectException
	 * @throws BadPasswordException
	 */
    @Keep
	public void executeQuery(String query) throws SQLException,
			ConnectException, BadPasswordException;
    
	/**
	 * ������������� ����������� �� ���-�� ����� 
	 * @param maxRows
	 */
    @Keep
	public void setMaxFethRow(int maxRows);
    
    /**
     * ���������, ��������� � Datastore, ���������� ���������� � ��� ��� ������ ����������
     */
    @Keep
    public void notifyViews();

   
}
