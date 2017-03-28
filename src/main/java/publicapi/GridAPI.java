package publicapi;

import java.util.Map;

import proguard.annotation.Keep;
import views.Menu;
import action.api.RTException;
import core.rml.dbi.Datastore;

/**
 * ��������� ��� ���������� ���������� ��������� "�������"
 */
public interface GridAPI extends VisualRmlObjectAPI, RmlContainerAPI  {

	//Column metods
	
	/**
	 * ������������ Datastore ������
	 * @param col  - ����� �������
	 */
	@Keep
	public void retrieveColumn(String col) throws RTException;
	
	/**
	 * ���������� ���� ���� �������
	 * @param col  - ������ �������
	 * @param color  - ����
	 */
	@Keep
	public void setColumnBgColor(int col, String color);
			
	/**
	 * ���������� ���� ���� �������
	 * @param col  - ����� �������
	 * @param color  - ����
	 */
	@Keep
	public void setColumnBgColor(String col, String color);

	
	/**
	 * ���������� ���� ������ �������
	 * @param col  - ������ �������
	 * @param color  - ����
	 */
	@Keep
	public void setColumnFgColor(int col, String color);
			
	/**
	 * ���������� ���� ������ �������
	 * @param col  - ����� �������
	 * @param color  - ����
	 */
	@Keep
	public void setColumnFgColor(String col, String color);

	
	/**
	 * ���������� ���� ���� ��������� �������
	 * @param col  - ������ �������
	 * @param color  - ����
	 */
	@Keep
	public void setColumnTitleBgColor(int col, String color);
	

	/**
	 * ���������� ���� ���� ��������� �������
	 * @param col  - ����� �������
	 * @param color  - ����
	 */
	@Keep	
	public void setColumnTitleBgColor(String col, String color);

	/**
	 * ���������� ���� ������ ��������� �������
	 * @param col  - ������ �������
	 * @param color  - ����
	 */
	@Keep
	public void setColumnTitleFgColor(int col, String color);
	
	/**
	 * ���������� ���� ������ ��������� �������
	 * @param col  - ����� �������
	 * @param color  - ����
	 */
	@Keep	
	public void setColumnTitleFgColor(String col, String color);

			
	/**
	 * ���������� ����� �������
	 * @param col  - ����� �������
	 * @param font  - �����
	 */
	@Keep	
	public void setColumnFont(int col, String font);

	/**
	 * ���������� ����� �������
	 * @param col  - ����� �������
	 * @param font  - �����
	 */
	@Keep	
	public void setColumnFont(String col, String font);
	
	
	/**
	 * ���������� ��������� �������
	 * @param col  - ������ �������
	 * @param title  - ���������
	 */
	@Keep	
	public void setColumnTitle(int col, String title);

	/**
	 * ���������� ��������� �������
	 * @param col  - ����� �������
	 * @param title  - ���������
	 */
	@Keep	
	public void setColumnTitle(String col, String title);
	
	/**
	 * ���������� �������� "visible" �������
	 * @param col  - ������ �������
	 * @param visible  - ���� true - ������� ������� 
	 */
	@Keep	
	public void setColumnVisible(int col, boolean visible);
	
	/**
	 * ���������� �������� "visible" �������
	 * @param col  - ����� �������
	 * @param visible  - ���� true - ������� ������� 
	 */
	@Keep	
	public void setColumnVisible(String col, boolean visible);
	
	/**
	 * ���������� �������� "visible" �������
	 * @param col  - ������ �������
	 * @return ���� true - ������� ������� 
	 */
	@Keep	
	public boolean isColumnVisible(int col);
	
	/**
	 * ���������� �������� "visible" �������
	 * @param col  - ����� �������
	 * @return ���� true - ������� ������� 
	 */
	@Keep	
	public boolean isColumnVisible(String col);
	
	/**
	 * �������� �������, �� ��������� � ��
	 * @param params  - ��������� �������
	 */
	@Keep	
	public void addTypeColumn(Map<String,Object> params);
	

	/**
	 * �������� �������, ��������� � ��
	 * @param params  - ��������� �������
	 */
	@Keep	
	public void addTargetColumn(Map<String,Object> params);
	
	/**
	 * �������� ������� � ����������� ��������
	 * @param params  - ��������� �������
	 */
	@Keep	
	public void addComboColumn(Map<String,Object> params);
	
	/**
	 * �������� ������� � ����������� ��������
	 * @param params  - ��������� �������
	 */
	@Keep	
	public void addComboTypeColumn(Map<String,Object> params);
	
	/**
	 * ���������� �������� ��������� �������
	 * @param col  - ������ �������
	 * @return ��������� ������� 
	 */
	@Keep	
	public String getColumnTitle(int col);
	
	/**
	 * ���������� �������� ��������� �������
	 * @param col  - ����� �������
	 * @return ��������� ������� 
	 */
	@Keep	
	public String getColumnTitle(String col);
	
	/**
	 * ���������� �������� ����� �������� �������
	 * @return ����� �������
	 */
	@Keep	
	public String getCurrentColumnAlias();
	
	/**
	 * ���������� ������ �������� ������� ��������! ������� ����� �������� ��� ��������� ���-�� ��������.
	 * @return ������ �������
	 */
	@Keep	
	public int getCurrentColumnIndex();
	
    /**
     * ���������� ����� ������� ������ 
     * @return ����� ������
     */
    @Keep   
    public int getCurrentRowIndex();
	

	/**
	 * ���������� ������� �������
	 * @param col ������ �������
	 */
	@Keep	
	public void setCurrentColumn(int col);
	
	/**
	 * ������� �������
	 * @param col  - ������ �������
	 */
	@Keep	
	public boolean deleteColumn(int col);
	
	/**
	 * ������� �������
	 * @param col  - ����� �������
	 */
	@Keep	
	public boolean deleteColumn(String col);
	
	/**
	 * ���������� ���-�� ������� ��������
	 * @return ���-�� ������� ��������
	 */
	@Keep
	public int getVisibleColumnCount();

	/**
	 * ���������� ���-�� ��������
	 * @return ���-�� ��������
	 */
	@Keep
	public int getColumnCount();
	

	
	//Row methods
	
	/**
	 *���������� ������� ������
	 *@param row - ������ 
	 */
	@Keep
	public void setCurrentRow(int row);
	
	/**
	 *�������� ����� ������ 
	 */
	@Keep
	public void addRow();
	
	/**
	 * ������� �������������
	 */
	@Keep
	public void deleteRow();
	
	/**
	 * ���������� ���� ���� ������
	 *@param row - ������ 
	 *@param color - ���� 
	 */
	@Keep
	public void setRowBGColor(int row, String color);
	
	/**
	 *���������� ���� ������ 
	 *@param row - ������ 
	 *@param color - ���� 
	 */
	@Keep
	public void setRowFGColor(int row, String color);
	
	/**
	 *���������� ���� ���� ��������� ������ 
	 *@param row - ������ 
	 *@param color - ���� 
	 */
	@Keep
	public void setRowTitleBGColor(int row, String color);
	
	/**
	 * ���������� ���� ������ ��������� ������
	 *@param row - ������ 
	 *@param color - ���� 
	 */
	@Keep
	public void setRowTitleFGColor(int row, String color);
	
	/**
	 *���������� ����� ������ 
	 *@param row - ������ 
	 *@param font - ����� 
	 */
	@Keep
	public void setRowFont(int row, String font);
	
	/**
	 *���������� �������� ������
	 *@param row - ������ 
	 *@param title - ��������� 
	 */
	@Keep
	public void setRowTitle(int row, String title);
	
	/**
	 *������� ��������� ������ 
	 *@param row - ������ 
	 */
	@Keep
	public void deleteRowTitle(int row);
	
	/**
	 *���������� ���-�� �����
	 *@return ���-�� ����� 
	 */
	@Keep
	public int getRowCount();
	
	
	//Grid methods
	
	/**
	 * ���������� �������� ����� �� ������� ������ � ��������� �������
	 * @param col - ����� �������
	 * @return �������� ������
	 */
	@Keep
	public Object currentValue(String col) throws RTException;
	
	/**
	 * ���������� �������� �� ������ 
	 * @param row - ����� ������
	 * @param col - ������ �������
	 * @return �������� ������
	 */
	@Keep
	public Object getValue(int row, int col);
	
	/**
	 * ���������� �������� �� ������ 
	 * @param row - ����� ������
	 * @param col - ����� �������
	 * @return �������� ������
	 */
	@Keep
	public Object getValue(int row, String col);
	
	/**
	 * ���������� �������� ����� ��������� ������� �������� � ���������� ������� 
	 * @param col - ����� �������
	 * @return ������ ��������
	 */
	@Keep
	public Object[] getSelectionValues(String col);
	
	/**
	 * ���������� ������ ����� �������� � ���������� ������� 
	 * @return ������ ������� �����
	 */
	@Keep
	public int[] getSelection();
	
	/**
	 * ������������ ������ �� ��
	 * @param keepFilters �������� ��� �������� ������� �������� ��������
	 * @return ���-�� �����
	 */
	@Keep
	public int retrieve(boolean keepFilters);
	
	/**
	 * ������ �������������� ������� ������
	 */
	@Keep
	public void edit();
	
	/**
	 * ������ Datastore
	 * @param ds
	 */
	@Keep
	public void setDatastore(Datastore ds);
	
	/**
	 * �������� Datastore, �������� � ���� ��������
	 * @return Datastore
	 */
	@Keep
	public Datastore getDatastore();
	
	/**
	 * 
	 * @return Datastore
	 */
	@Keep
	public Datastore getAllDatastore();
	
	/**
	 * ���������� ����� �������
	 * @param col - ����� �������
	 * @return ����� �������� �� ���� �������
	 */
	@Keep
	public double sum(String col) throws RTException;
	
	/**
	 * ���������� ������� � ����
	 */
	@Keep
	public void dumpToFile();
	
	/**
	 * ��������� ����
	 * @return Menu
	 */
	@Keep
	public Menu getMenu();
	
	/**
	 * ������ ����
	 * @param m Menu
	 */
	@Keep
	public void setMenu(Menu m);
	
	/**
	 * ������������ �������
	 */
	@Keep
	public void repaint();
	
	/**
	 */
	@Keep
	public void invertSelection();
	
	/**
	 * �������� ������ � ������ ���������
	 * @param row - ����� ������
	 */
	@Keep
	void fastSelection(int row);
	
	/**
	 * ���������� ���������� ������
	 * @param row ����� ������
	 */
	@Keep
	void setSelection(int row);
	
	/**
	 * ������� ��� ��������
	 */
	@Keep
	void selectAll();
	
	/**
	 * ������� ���-�� ����� �������
	 * @return ���-�� �����
	 */
	@Keep
	int size();
	
	/**
	 * ��������� ������� (��������� ������ ��������, ����� ��� ��� ���������� � �������� ������ ������� (������ ����������, ����������� �������))
	 */
	@Keep
	void allign();
	
	/**
	 * ������ ���� ���� ������ 
	 * @param row ����� ������
	 * @param col ������ �������
	 * @param color ����
	 */
	@Keep
	void setCellBGColor(int row, int col, String color);
	
	/**
	 * ������ ���� ������ ������ 
	 * @param row ����� ������
	 * @param col ������ �������
	 * @param color ����
	 */
	@Keep
	void setCellFGColor(int row, int col, String color);
	
	/**
	 * ������ ����� ������ 
	 * @param row ����� ������
	 * @param col ������ �������
	 * @param font �����
	 */
	@Keep
	void setCellFont(int row, int col, String font);

	/**
	 * �������� ������ ������
	 */
	@Keep
	void showSearchDialog();
	
	/**
	 * ���������� �������� � ������
	 * @param row ����� ������ 
	 * @param col ����� �������
	 * @param value �������� ��� �������
	 */
	@Keep
	void setValue(int row, int col, Object value);

	/**
	 * ���������� �������� � ������
	 * @param row ����� ������ 
	 * @param col ����� �������
     * @param value �������� ��� �������
	 */
	@Keep
	void setValue(int row, String col, Object value);
	
	/**
	 * ���������� ����������
	 */
	@Keep
	void notifySubscribers();
	
	/**
	 * �������� ���������� �� �������������� �������
	 * @return ���� true �� �������������� ���������
	 */
	@Keep
	boolean isEditable();
	
	/**
	 * ��������� ���������� �� �������������� �������
	 * @param editable ���� true �� �������������� ���������
	 */
	@Keep
	void setEditable(boolean editable);
}
