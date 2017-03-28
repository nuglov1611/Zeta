package publicapi;

import core.rml.Container;
import core.rml.RmlObject;
import proguard.annotation.Keep;


/**
 * ������ RML, ������� ����� ��������� � ���� ������ RML-������
 * ��� ���������� ���������� ������� ������� ����������� ����� addChild(child)
 * ����� ��� ������� ����� ���������� ������ ���������� ������� ����� initChildren()��� ������������� ���� ���������� ��������� ��������
 * <p>
 * ������ ���������� �� ����� CheckGroup c ����� CheckBox:
 *
 * @author nuglov
 * @code checkgroup  = createCheckGroup(250, 40);
 * check_action = '~($X\
 * ($debug \'checkbox_show_busy_rooms action\')\
 * ($grid2_repaint)\
 * ($ret \'\')\
 * )~'
 * checkgroup.addChild(createCheckBox('checkbox_show_busy_rooms', '�������', 14, 'ON', 'OFF', 'NO', check_action));
 * <p>
 * checkgroup  = createCheckGroup(250, 70);
 * form.addChild(checkgroup);
 * check_action = '~($X\
 * ($debug \'checkbox_show_all_rooms action\')\
 * ($grid2_repaint)\
 * ($ret \'\')\
 * )~';
 * checkgroup.addChild(createCheckBox('checkbox_show_all_rooms', '���', 14, 'ON', 'OFF', 'YES', check_action));
 * checkgroup.initChildren();
 * form.addChild(checkgroup);
 * @endcode
 */
@Keep
public interface RmlContainerAPI {

    /**
     * �������� ������
     *
     * @param child
     */
    @Keep
    void addChild(RmlObject child);


    /**
     * �������� ��������� �������
     *
     * @return ������ �������
     */
    @Keep
    RmlObject[] getChildren();


    /**
     * ��������� ������������� ��������� ��������
     */
    @Keep
    void initChildren() throws Exception;

    /**
     * �������� ��������� �������� ������
     *
     * @return ���������
     */
    @Keep
    Container getContainer();

    boolean addChildrenAutomaticly();
}
