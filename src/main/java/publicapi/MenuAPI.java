package publicapi;

import proguard.annotation.Keep;
import views.Item;

import javax.swing.*;

public interface MenuAPI extends RmlObjectAPI, RmlContainerAPI {
    /**
     * ���������� ������� ����
     *
     * @param i ����� ��������
     * @return ������� ����
     */
    @Keep
    JMenuItem getItemAt(int i);

    /**
     * ������� ������� ����
     *
     * @param i ����� ��������
     */
    @Keep
    void removeItem(int i);

    /**
     * ���������� ���-�� ��������� � ����
     *
     * @return ���-�� ���������
     */
    @Keep
    int getSize();

    /**
     * ��������� ����� ������� � ����. ���� ����� ����� ������ ��� ���-�� ��������� � ����, �� ������� ��������� � ����� ����
     *
     * @param i    - ����� ��� ���������� ��������
     * @param item - ������� ����
     */
    @Keep
    void putItem(int i, Item item);

}
