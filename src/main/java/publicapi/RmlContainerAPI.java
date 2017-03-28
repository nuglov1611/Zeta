package publicapi;

import proguard.annotation.Keep;
import core.rml.Container;
import core.rml.RmlObject;


/**
 * ������ RML, ������� ����� ��������� � ���� ������ RML-������
 * ��� ���������� ���������� ������� ������� ����������� ����� addChild(child)
 * ����� ��� ������� ����� ���������� ������ ���������� ������� ����� initChildren()��� ������������� ���� ���������� ��������� ��������
 * 
 * ������ ���������� �� ����� CheckGroup c ����� CheckBox:
 * @code
    checkgroup  = createCheckGroup(250, 40);
    check_action = '~($X\
                   ($debug \'checkbox_show_busy_rooms action\')\
                    ($grid2_repaint)\
                    ($ret \'\')\
                   )~'
    checkgroup.addChild(createCheckBox('checkbox_show_busy_rooms', '�������', 14, 'ON', 'OFF', 'NO', check_action));
 
    checkgroup  = createCheckGroup(250, 70);
    form.addChild(checkgroup);
    check_action = '~($X\
                    ($debug \'checkbox_show_all_rooms action\')\
                    ($grid2_repaint)\
                    ($ret \'\')\
                   )~';
    checkgroup.addChild(createCheckBox('checkbox_show_all_rooms', '���', 14, 'ON', 'OFF', 'YES', check_action));
    checkgroup.initChildren();
    form.addChild(checkgroup);
    @endcode
 *    
 * @author nuglov
 */
@Keep
public interface RmlContainerAPI {

	/**
	 * �������� ������
	 * 
	 * @param child
	 */
    @Keep
    public void addChild(RmlObject child);
    
    
    /**
     * �������� ��������� ������� 
     * @return ������ ������� 
     */
    @Keep
    public RmlObject[] getChildren();
    
    
    /**
     * ��������� ������������� ��������� �������� 
     * 
     */
    @Keep
	public void initChildren() throws Exception;

    /**
     * �������� ��������� �������� ������ 
     * @return ��������� 
     */
    @Keep
    public Container getContainer();
    
	public boolean addChildrenAutomaticly();
}
