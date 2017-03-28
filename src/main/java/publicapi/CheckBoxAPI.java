package publicapi;

import action.api.RTException;
import proguard.annotation.Keep;

/**
 * ����������� ������� CheckBox
 * ��������:
 * - label - ������������ ��� ��������. ���� ����� ������ �������� "", ������� ����� ������������ �� ���� ������ ���� �������� ����������� ������� ��� �������� (������ ������� � ��������). �������� �� ��������� "".
 * - offvalue - �������� ������������ ��� ����������� ���������, ��� ������� ������� � �������� �������� �� ����������. ������������ ��� ������� �������� checked. �������� �� ��������� "no".
 * - onvalue - �������� ������������ ��� ����������� ���������, ��� ������� ������� � �������� �������� ����������. ������������ ��� ������� �������� checked. �������� �� ��������� "yes".
 * - check - ��������� �� ��, ���������� ������� � �������� �������� ��� ��������� ������������� �������� ��� ���. ��� ��������� ���������� �������� ������������ ��������� ��������� "yes" � "no". �������� �� ��������� "no", �.�. �� ��������� ������� �� ����������.
 * - shortCut - ��������� ������
 * - action - �������� ����������� �� ��������� �������� �������
 *
 * @code {checkbox
 * alias=cb1
 * label = "CheckBox1"
 * offvalue = "off" onvalue = "on" check = yes
 * }
 * @endcode
 */
public interface CheckBoxAPI extends VisualRmlObjectAPI {

    /**
     * ���������� ��������� ���������� �������������� ���������������� ��������
     *
     * @param value �������� (onValue ��� offValue)
     * @throws RTException ���� value �� �������� �� onValue �� offValue
     */
    @Keep
    void setState(Object value) throws RTException;

    /**
     * ������������� ��������� CheckBox
     *
     * @param selected �������� true - �������, false - ��������
     */
    @Keep
    void setSelected(boolean selected);

    /**
     * @return �������� CheckBox true - �������, false - ��������
     */
    @Keep
    boolean isSelected();

    /**
     * @return ��������������� � ���������� �������� (onValue - ���� �������, �������� - ���� �������� )
     */
    @Keep
    String getStateValue();

}
