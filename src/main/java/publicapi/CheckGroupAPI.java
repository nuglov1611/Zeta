package publicapi;

/**
 * ������ ��������� ChackBox (� ������ ����� ���� ������� ������ ���� CheckBox,
 * �.�. ��� ��������� ���� �������� �� ������ ��������� �������� ��������� �
 * ��������� "��������")
 *
 *- alignment - ������������ ��������� ������ ������. ����� ���� 2 ��������: horizontal � vertical. �������� �� ��������� vertical.
 *- gap - ���������� �� ��������� ����� ���������� ������. �������� ����� ������������� ������. ����� ����������� � ��������. �������� �� ��������� 5
 *
 *@code
   {checkgroup
      top=75
      left=0
      alignment=center
      alias=cbgroup
      gap=30
      { checkbox 
          label = "CheckBox1"
          offvalue = "off" onvalue = "on" check = yes
      }
      { checkbox 
          label = "CheckBox2"
          offvalue = "off" onvalue = "on" check = no
      }
      { checkbox 
        label = "CheckBox3"
        offvalue = "off" onvalue = "on" check = yes
      }
      { checkbox 
        label = "CheckBox4"
        offvalue = "off" onvalue = "on" check = no
      }
    }  
  @endcode
 *  
 */
public interface CheckGroupAPI extends VisualRmlObjectAPI, RmlContainerAPI {

}
