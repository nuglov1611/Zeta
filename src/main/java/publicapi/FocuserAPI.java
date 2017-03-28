package publicapi;

import proguard.annotation.Keep;

/**
 * ������� ��� ���������� ������������ � ������ ���������
 */

public interface FocuserAPI extends RmlObjectAPI {

    /**
     * �������� ����� �� ������
     *
     * @param component - ������ ��� �������� ������
     */
    @Keep
    void focus(Object component);

    /**
     * �������� ����� �� �������� ������
     */
    @Keep
    void focusNext();

    /**
     * �������� ����� �� ���������� ������
     */
    @Keep
    void focusPrevious();
}
