package publicapi;

import action.api.RTException;
import core.rml.dbi.Datastore;
import proguard.annotation.Keep;
import views.Menu;

import java.util.Map;

/**
 * ��������� ��� ���������� ���������� ��������� "�������"
 */
public interface GridAPI extends VisualRmlObjectAPI, RmlContainerAPI {

    //Column metods

    /**
     * ������������ Datastore ������
     *
     * @param col - ����� �������
     */
    @Keep
    void retrieveColumn(String col) throws RTException;

    /**
     * ���������� ���� ���� �������
     *
     * @param col   - ������ �������
     * @param color - ����
     */
    @Keep
    void setColumnBgColor(int col, String color);

    /**
     * ���������� ���� ���� �������
     *
     * @param col   - ����� �������
     * @param color - ����
     */
    @Keep
    void setColumnBgColor(String col, String color);


    /**
     * ���������� ���� ������ �������
     *
     * @param col   - ������ �������
     * @param color - ����
     */
    @Keep
    void setColumnFgColor(int col, String color);

    /**
     * ���������� ���� ������ �������
     *
     * @param col   - ����� �������
     * @param color - ����
     */
    @Keep
    void setColumnFgColor(String col, String color);


    /**
     * ���������� ���� ���� ��������� �������
     *
     * @param col   - ������ �������
     * @param color - ����
     */
    @Keep
    void setColumnTitleBgColor(int col, String color);


    /**
     * ���������� ���� ���� ��������� �������
     *
     * @param col   - ����� �������
     * @param color - ����
     */
    @Keep
    void setColumnTitleBgColor(String col, String color);

    /**
     * ���������� ���� ������ ��������� �������
     *
     * @param col   - ������ �������
     * @param color - ����
     */
    @Keep
    void setColumnTitleFgColor(int col, String color);

    /**
     * ���������� ���� ������ ��������� �������
     *
     * @param col   - ����� �������
     * @param color - ����
     */
    @Keep
    void setColumnTitleFgColor(String col, String color);


    /**
     * ���������� ����� �������
     *
     * @param col  - ����� �������
     * @param font - �����
     */
    @Keep
    void setColumnFont(int col, String font);

    /**
     * ���������� ����� �������
     *
     * @param col  - ����� �������
     * @param font - �����
     */
    @Keep
    void setColumnFont(String col, String font);


    /**
     * ���������� ��������� �������
     *
     * @param col   - ������ �������
     * @param title - ���������
     */
    @Keep
    void setColumnTitle(int col, String title);

    /**
     * ���������� ��������� �������
     *
     * @param col   - ����� �������
     * @param title - ���������
     */
    @Keep
    void setColumnTitle(String col, String title);

    /**
     * ���������� �������� "visible" �������
     *
     * @param col     - ������ �������
     * @param visible - ���� true - ������� �������
     */
    @Keep
    void setColumnVisible(int col, boolean visible);

    /**
     * ���������� �������� "visible" �������
     *
     * @param col     - ����� �������
     * @param visible - ���� true - ������� �������
     */
    @Keep
    void setColumnVisible(String col, boolean visible);

    /**
     * ���������� �������� "visible" �������
     *
     * @param col - ������ �������
     * @return ���� true - ������� �������
     */
    @Keep
    boolean isColumnVisible(int col);

    /**
     * ���������� �������� "visible" �������
     *
     * @param col - ����� �������
     * @return ���� true - ������� �������
     */
    @Keep
    boolean isColumnVisible(String col);

    /**
     * �������� �������, �� ��������� � ��
     *
     * @param params - ��������� �������
     */
    @Keep
    void addTypeColumn(Map<String, Object> params);


    /**
     * �������� �������, ��������� � ��
     *
     * @param params - ��������� �������
     */
    @Keep
    void addTargetColumn(Map<String, Object> params);

    /**
     * �������� ������� � ����������� ��������
     *
     * @param params - ��������� �������
     */
    @Keep
    void addComboColumn(Map<String, Object> params);

    /**
     * �������� ������� � ����������� ��������
     *
     * @param params - ��������� �������
     */
    @Keep
    void addComboTypeColumn(Map<String, Object> params);

    /**
     * ���������� �������� ��������� �������
     *
     * @param col - ������ �������
     * @return ��������� �������
     */
    @Keep
    String getColumnTitle(int col);

    /**
     * ���������� �������� ��������� �������
     *
     * @param col - ����� �������
     * @return ��������� �������
     */
    @Keep
    String getColumnTitle(String col);

    /**
     * ���������� �������� ����� �������� �������
     *
     * @return ����� �������
     */
    @Keep
    String getCurrentColumnAlias();

    /**
     * ���������� ������ �������� ������� ��������! ������� ����� �������� ��� ��������� ���-�� ��������.
     *
     * @return ������ �������
     */
    @Keep
    int getCurrentColumnIndex();

    /**
     * ���������� ����� ������� ������
     *
     * @return ����� ������
     */
    @Keep
    int getCurrentRowIndex();


    /**
     * ���������� ������� �������
     *
     * @param col ������ �������
     */
    @Keep
    void setCurrentColumn(int col);

    /**
     * ������� �������
     *
     * @param col - ������ �������
     */
    @Keep
    boolean deleteColumn(int col);

    /**
     * ������� �������
     *
     * @param col - ����� �������
     */
    @Keep
    boolean deleteColumn(String col);

    /**
     * ���������� ���-�� ������� ��������
     *
     * @return ���-�� ������� ��������
     */
    @Keep
    int getVisibleColumnCount();

    /**
     * ���������� ���-�� ��������
     *
     * @return ���-�� ��������
     */
    @Keep
    int getColumnCount();


    //Row methods

    /**
     * ���������� ������� ������
     *
     * @param row - ������
     */
    @Keep
    void setCurrentRow(int row);

    /**
     * �������� ����� ������
     */
    @Keep
    void addRow();

    /**
     * ������� �������������
     */
    @Keep
    void deleteRow();

    /**
     * ���������� ���� ���� ������
     *
     * @param row   - ������
     * @param color - ����
     */
    @Keep
    void setRowBGColor(int row, String color);

    /**
     * ���������� ���� ������
     *
     * @param row   - ������
     * @param color - ����
     */
    @Keep
    void setRowFGColor(int row, String color);

    /**
     * ���������� ���� ���� ��������� ������
     *
     * @param row   - ������
     * @param color - ����
     */
    @Keep
    void setRowTitleBGColor(int row, String color);

    /**
     * ���������� ���� ������ ��������� ������
     *
     * @param row   - ������
     * @param color - ����
     */
    @Keep
    void setRowTitleFGColor(int row, String color);

    /**
     * ���������� ����� ������
     *
     * @param row  - ������
     * @param font - �����
     */
    @Keep
    void setRowFont(int row, String font);

    /**
     * ���������� �������� ������
     *
     * @param row   - ������
     * @param title - ���������
     */
    @Keep
    void setRowTitle(int row, String title);

    /**
     * ������� ��������� ������
     *
     * @param row - ������
     */
    @Keep
    void deleteRowTitle(int row);

    /**
     * ���������� ���-�� �����
     *
     * @return ���-�� �����
     */
    @Keep
    int getRowCount();


    //Grid methods

    /**
     * ���������� �������� ����� �� ������� ������ � ��������� �������
     *
     * @param col - ����� �������
     * @return �������� ������
     */
    @Keep
    Object currentValue(String col) throws RTException;

    /**
     * ���������� �������� �� ������
     *
     * @param row - ����� ������
     * @param col - ������ �������
     * @return �������� ������
     */
    @Keep
    Object getValue(int row, int col);

    /**
     * ���������� �������� �� ������
     *
     * @param row - ����� ������
     * @param col - ����� �������
     * @return �������� ������
     */
    @Keep
    Object getValue(int row, String col);

    /**
     * ���������� �������� ����� ��������� ������� �������� � ���������� �������
     *
     * @param col - ����� �������
     * @return ������ ��������
     */
    @Keep
    Object[] getSelectionValues(String col);

    /**
     * ���������� ������ ����� �������� � ���������� �������
     *
     * @return ������ ������� �����
     */
    @Keep
    int[] getSelection();

    /**
     * ������������ ������ �� ��
     *
     * @param keepFilters �������� ��� �������� ������� �������� ��������
     * @return ���-�� �����
     */
    @Keep
    int retrieve(boolean keepFilters);

    /**
     * ������ �������������� ������� ������
     */
    @Keep
    void edit();

    /**
     * ������ Datastore
     *
     * @param ds
     */
    @Keep
    void setDatastore(Datastore ds);

    /**
     * �������� Datastore, �������� � ���� ��������
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
     * ���������� ����� �������
     *
     * @param col - ����� �������
     * @return ����� �������� �� ���� �������
     */
    @Keep
    double sum(String col) throws RTException;

    /**
     * ���������� ������� � ����
     */
    @Keep
    void dumpToFile();

    /**
     * ��������� ����
     *
     * @return Menu
     */
    @Keep
    Menu getMenu();

    /**
     * ������ ����
     *
     * @param m Menu
     */
    @Keep
    void setMenu(Menu m);

    /**
     * ������������ �������
     */
    @Keep
    void repaint();

    /**
     */
    @Keep
    void invertSelection();

    /**
     * �������� ������ � ������ ���������
     *
     * @param row - ����� ������
     */
    @Keep
    void fastSelection(int row);

    /**
     * ���������� ���������� ������
     *
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
     *
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
     *
     * @param row   ����� ������
     * @param col   ������ �������
     * @param color ����
     */
    @Keep
    void setCellBGColor(int row, int col, String color);

    /**
     * ������ ���� ������ ������
     *
     * @param row   ����� ������
     * @param col   ������ �������
     * @param color ����
     */
    @Keep
    void setCellFGColor(int row, int col, String color);

    /**
     * ������ ����� ������
     *
     * @param row  ����� ������
     * @param col  ������ �������
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
     *
     * @param row   ����� ������
     * @param col   ����� �������
     * @param value �������� ��� �������
     */
    @Keep
    void setValue(int row, int col, Object value);

    /**
     * ���������� �������� � ������
     *
     * @param row   ����� ������
     * @param col   ����� �������
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
     *
     * @return ���� true �� �������������� ���������
     */
    @Keep
    boolean isEditable();

    /**
     * ��������� ���������� �� �������������� �������
     *
     * @param editable ���� true �� �������������� ���������
     */
    @Keep
    void setEditable(boolean editable);
}
