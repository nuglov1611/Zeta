package publicapi;

import proguard.annotation.Keep;

/**
 * ������� ��� ���������� ������������ � ������ ���������
 */
		
public interface FocuserAPI extends RmlObjectAPI {
    
    /**
     * �������� ����� �� ������ 
     * 
     *  @param component - ������ ��� �������� ������
     */
    @Keep
    public void focus(Object component);
    
    /**
     * �������� ����� �� �������� ������ 
     * 
     */
    @Keep
    public void focusNext();
   
    /**
     * �������� ����� �� ���������� ������ 
     * 
     */
    @Keep
    public void focusPrevious();
}
