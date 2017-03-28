package publicapi;

import javax.swing.JMenuItem;

import proguard.annotation.Keep;
import views.Item;

public interface MenuAPI extends RmlObjectAPI, RmlContainerAPI {
	/**
	 * ���������� ������� ���� 
	 * @param i ����� ��������
	 * @return ������� ����
	 */
    @Keep
	public JMenuItem getItemAt(int i);

	/**
	 * ������� ������� ����
	 * @param i ����� ��������
	 */
    @Keep
	public void removeItem(int i);
    
	/**
	 * ���������� ���-�� ��������� � ����
	 * @return ���-�� ���������
	 */
    @Keep
	public int getSize();
    
	/**
	 * ��������� ����� ������� � ����. ���� ����� ����� ������ ��� ���-�� ��������� � ����, �� ������� ��������� � ����� ����
	 * @param i - ����� ��� ���������� ��������
	 * @param item - ������� ����
	 */
    @Keep
	public void putItem(int i, Item item);

}
