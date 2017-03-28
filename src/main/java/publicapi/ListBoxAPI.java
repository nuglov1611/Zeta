package publicapi;

import proguard.annotation.Keep;

/**
 * ������� ���������� - ������. ����� ������������� ���� �� ������ ������� [[Datastore]] ���� �������. 
 * 
 * �������� =
 * - target - ��� ������� [[Datastore]], ������� ����� ������� ���������� ������. ���� [[Datastore]] ������ ���� ������� � ListBox.
 * - action - ������, ������� ����������� ��� ����� ��������� �������� ������. � ������ ������� ���������� action ��������� �������� ([[ListItem]]), ����� action ComboBox. (��������� [[calc]])
 *
 * ������
 * @code
 {listbox
   alias=combo
   {item
     alias=itm1
     label=item1
     action="($X
                ($g.date@setValue '������ Item1')
                ($ret '')
             )"
   }
   {item
     alias=itm2
     label=item2
     action="($X
                ($g.date@setValue '������ Item2')
                ($ret '')
             )"
   }
   {item
     alias=itm3
     label=item3
     action="($g.self@doAction 'open comboWithDS.rml')"
   }
 }
 @endcode
 */
public interface ListBoxAPI extends VisualRmlObjectAPI {

    /**
     * �������� �������
     * Sring item_alias - ����� ��������
     * Sring item_label - ����� ��������
     * Sring item_action - ������� �� �������
     */
    @Keep
    public void addItem(String item_alias, String item_label, String item_action);


    /**
     * ������� ����� ��������� ��������
     * @return ������
     */
    @Keep
    public int getSelectedIndex();
    
    /**
     * ������� �������� �������
     * @return ������
     */
    @Keep
    public Object getSelectedValue();
    
    /**
     * ���������� ��������� ������� �� ������ ��������
     * @param item_label �����
     */
    @Keep
    public void setSelectedItem(String item_label);

    /**
     * ���-�� ���������
     * @return ���-��
     */
    @Keep
    public int getItemCount();

    /**
     * �������� ������� �� ������
     * @param index �����
     * @return �������
     */
    @Keep
    public Object getItemAt(int index);

    /**
     * ���������� ��������� ������� �� ������
     * @param index �����
     */
    @Keep
    public void setSelectedIndex(int index);

    /**
     * ���������� ��������� �������
     * @param item �������
     */
    @Keep
    public void setSelectedItem(Object item);

}
