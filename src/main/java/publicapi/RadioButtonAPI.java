package publicapi;

import proguard.annotation.Keep;

public interface RadioButtonAPI extends VisualRmlObjectAPI {

	/**
	 * ���������� ��������� ������ (��������/���������)
	 * @return true - ��������, false - ��������� 
	 */
	@Keep
	public boolean isSelected();
	
	
	/**
	 * ������ ��������� ������ (��������/���������)
	 * @param selected ���� true - ��������, false - ��������� 
	 */
	@Keep
	public void setSelected(boolean selected);
	
	

}
