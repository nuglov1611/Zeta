package publicapi;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import proguard.annotation.Keep;
import core.connection.BadPasswordException;

/**
 * ��������� ��� ��������������� ������� � ��. ������������ � ������ ������� ��� ������ � ����� ������ BLOB
 * @author uglov
 *
 */
public interface DataSetAPI extends RmlObjectAPI {

	
	/**
	 * ���������� ����� SQL-�������
	 * @param q ����� �������
	 */
	@Keep
	public void setQuery(String q);
	
	/**
	 * ������� ��������� SQL-�������
	 * @return ������
	 */
	@Keep
	public String getQuery();

	/**
	 * ������� �����
	 * @param row ����� ������
	 */
	@Keep
    public void deleteRow(int row);
    
    /**
     * ���������� ������� ������
     * @param rowNum
     * @throws SQLException
     */
	@Keep
    public void setCurRow(int rowNum) throws SQLException;
    
    /** 
     * ��������� �������� � ��
     * @throws SQLException
     * @throws FileNotFoundException
     */
	@Keep
    public void update() throws SQLException, FileNotFoundException;


    /**
     * ���������� �������� � ������
     * @param row ����� ������
     * @param column ����� �������
     * @param value �������� (���� ��� ������� Blob, �� ������ �������� ����� ���� ���� byte[] (��������������� ������ ��� ���������� � Blob); 
     *              ���� ��� String, � ���� ������ ��������� ��� ������ ������� �� �����, � ������� ��������� ���� � �����)
     */
	@Keep
    public void setValue(int row, int column, Object value);

    /**
     * ������� �������� �� ������
     * @param row ����� ������
     * @param col ����� �������
     * @return �������� (���� ��� ������ Blob �� ��� ������������� �������� ����� byte[])
     * @throws SQLException
     * @throws IOException
     */
	@Keep
    public Object getValue(int row, int col) throws SQLException, IOException;
    
    /**
     * ������� �������� �� ������ ������� ������
     * @param col ����� �������
     * @return �������� (���� ��� ������ Blob �� ��� ������������� �������� ����� byte[])
     * @throws SQLException
     * @throws IOException
     */
	@Keep
    public Object getValue(int col) throws SQLException, IOException;
    
    /**
     * ������� �������� �� ������ ������� ������
     * @param column ����� �������
     * @return �������� (���� ��� ������ Blob �� ��� ������������� �������� ����� byte[])
     * @throws SQLException
     * @throws IOException
     */
	@Keep
    public Object getValue(String column) throws SQLException, IOException;
    
    /**
     * �������� ������ � DataSet (��������� ������ � ��)
     * @return ���-�� ������������ ����� 
     * @throws BadPasswordException ���� �� ������ �����/������ ��� ����������� � �� 
     * @throws SQLException
     */
	@Keep
    public int retrieve() throws BadPasswordException, SQLException;
}
