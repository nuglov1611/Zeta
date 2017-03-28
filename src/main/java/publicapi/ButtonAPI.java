package publicapi;

import proguard.annotation.Keep;

/**
 * ����������� ��������� "������"
 *
 * ��������:
 * - label - ������� �� ������. �������� �� ��������� "". ����� ��������� html-����.
 * - action - �������� ����������� ��� ������� �� ������
 * - aAction - �������� ����������� ��� ������� �� ������
 * - alignment - ��������� ������ �� ������
 *  - center - �� ������
 *  - left - ������� �����
 *  - right - ������� ������
 *  - �� ��������� "center"
 * - shortCut - ��������� ������� (������� �������)
 * - ICON - ���� � �������� (������), ������� ����� ���������� �� ������
 * - ICONSCALED - ��������� ����������� ��� ������� ������� (���������� ��� ���� ������)
 *  - YES - - �������� ������� ����������� � ���������� � ��������� ������
 *  - NO - �������� ������������ ������� ����������� (�������� ��-���������)
 *  
 *  @code
  {button
    top=10
    left=10
    label="Get date"
    action = "($X
                 ($g.ds@RETRIEVE)
                 (data = ($g.ds@getValue 'COL1'))
                 ($g.date@setValue data)
                 ($ret '')
              )"
  }
  {button
    left = 20 top = 10 width = 100 height = 20
    label = "��������"
    icon="images\\ab.gif"
    border=empty
    foreground = "#000000"
    action = "($X
                ($g.self@doaction 'createnew docs/run_plan.rml -1, -1, -1')
                ($g.self@doAction 'retrieve tree1')
                ($g.grid_group1@retrieve)
                ($ret '')
            )"
   } 
  @endcode
 */

public interface ButtonAPI extends VisualRmlObjectAPI {

    /**
     * ������ ����� ������� �� ������
     * @param caption ����� �������
     */
    @Keep
    public void setCaption(String caption);
	
    /**
     * ��������� �������� ��������������� � �������
     */
    @Keep
    public void doAction();
	
}
