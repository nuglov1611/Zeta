package publicapi;

import proguard.annotation.Keep;
import action.api.RTException;

/**
 * ����������� ������� CheckBox
 * ��������:
 *- label - ������������ ��� ��������. ���� ����� ������ �������� "", ������� ����� ������������ �� ���� ������ ���� �������� ����������� ������� ��� �������� (������ ������� � ��������). �������� �� ��������� "".
 *- offvalue - �������� ������������ ��� ����������� ���������, ��� ������� ������� � �������� �������� �� ����������. ������������ ��� ������� �������� checked. �������� �� ��������� "no". 
 *- onvalue - �������� ������������ ��� ����������� ���������, ��� ������� ������� � �������� �������� ����������. ������������ ��� ������� �������� checked. �������� �� ��������� "yes".
 *- check - ��������� �� ��, ���������� ������� � �������� �������� ��� ��������� ������������� �������� ��� ���. ��� ��������� ���������� �������� ������������ ��������� ��������� "yes" � "no". �������� �� ��������� "no", �.�. �� ��������� ������� �� ����������.
 *- shortCut - ��������� ������
 *- action - �������� ����������� �� ��������� �������� �������
 * 
 * @code
  {checkbox 
      alias=cb1
      label = "CheckBox1"
      offvalue = "off" onvalue = "on" check = yes
  }
  @endcode 
 * 
 */
public interface CheckBoxAPI extends VisualRmlObjectAPI {

    /**
     * ���������� ��������� ���������� �������������� ���������������� ��������
     * @param value �������� (onValue ��� offValue)
     * @throws RTException ���� value �� �������� �� onValue �� offValue 
     */
    @Keep
    public void setState(Object value) throws RTException;

    /**
     * ������������� ��������� CheckBox
     * @param selected �������� true - �������, false - ��������
     */
    @Keep
    public void setSelected(boolean selected);

    /**
     * @return �������� CheckBox true - �������, false - ��������
     */
    @Keep
    public boolean isSelected();

    /**
     * @return ��������������� � ���������� �������� (onValue - ���� �������, �������� - ���� �������� )
     */
    @Keep
    public String getStateValue();

}
