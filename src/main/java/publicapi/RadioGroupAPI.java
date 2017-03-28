package publicapi;

import proguard.annotation.Keep;

/**
 * ���������� ��������� - ����������� RadioButton. � ���� ������ ������� � ������ ����� ���� �������� ������ ���� ������.
 * @author 
 *
 */
public interface RadioGroupAPI extends VisualRmlObjectAPI, RmlContainerAPI {

    /**
     * ���������� �������� ��������������� � ������� (����������) ������� � ������
     * @return �������� ��������������� � ������� ��������� �������
     */
	@Keep
	public Object getCurrentValue();
	
	 /**
     * �������� ������ 
     * @param buttonNumber ����� ������ � ������
     * @param selected �������� true - �������� false - ���������
     */
	@Keep
    public void setSelected(int buttonNumber, boolean selected);
    
	 /**
     * ���������� ����� �������� ������ 
     * @return ����� �������� ������
     */
	@Keep
    public int getSelected();
	
}
