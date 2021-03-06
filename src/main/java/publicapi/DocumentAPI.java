package publicapi;

import core.parser.Proper;
import core.rml.RmlObject;
import proguard.annotation.Keep;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

/**
 * �������� - �������� ��������� ��� RML-��������. ����������� ��������� RML-������.
 * <p>
 * ��������
 * - document_title - ��������� ���������, ������������ ������ ���� � title bar. ��� ������ �������� ���� title bar �� ������������. �������� �� ��������� - ������
 * - layout - ����� ���������� ���������� RML-�������� � ���������. ���������� ��������: "abs", "border", "box", "grid", "flow". �������� �� ��������� - "grid"
 * - version - ������ ���������. �������� �� ��������� - "..."
 * - postloadscript - ������ ������������� ����� �������� �������� (����������� ��������� [[calc]])
 * - preloadscript - ������ ������������� �� �������� �������� (����������� ��������� [[calc]])
 * - closescript - ������ ������������� ����� ��������� ��������� (����������� ��������� [[calc]])
 * - hashable - �������� yes/no. � ������ ���� �������� ����� yes, �������� ��������� ���� ��������� ��� ����� ������ �������. ���������� ������������� ������ ���� ��������� ���������. ���� ����� ����������� ��� ���������� - ������� ��������� �� �����������.
 * - �������� ��-��������� 'no'
 * - axis - ������ ���������� �������� ��� box layout.
 * - X - ���������� ����������� �� �����������
 * - Y - ���������� ����������� �� ���������
 * - �������� ��-��������� 'Y'
 * - document_width - ������ ���������
 * - document_height - ������ ���������
 * - grid_x - ����� ����� �� ����������� ��� grid layout. ������������ ���� ���� layout = "grid"
 * - grid_y - ����� ����� �� ��������� ��� grid layout. ������������ ���� ���� layout = "grid"
 * <p>
 * <p>
 * ������
 *
 * @code Cp1251
 * DOC
 * document_title="���������� ������������"
 * document_width=900
 * document_height=600
 * {toolbar
 * {treeview2
 * return=yes
 * expandall=yes
 * alias=tree
 * nodeAction="~($g.self@acthok)~"
 * listAction2="~($g.self@acthok)~"
 * nodefont="Courier,bold|italic,12" pointfont="Courier,italic,12"
 * hilite=gray,white
 * node=black point=gray
 * rootname="�����������"
 * hiliting=on
 * {groupreport
 * alias = gr
 * query = "select client.id_client, client.id_parent, client.code1, name_client, lvl, client.address1, territory.id_territory, territory.name_territory
 * from (
 * select client.id_client, client.id_parent, decode(level, 4, code1||' '||name_client, name_client) name_client, level lvl,
 * client.address1, client.id_terr, client.code1
 * from client
 * where client.id_client != -1
 * ~($if (($try (ARGUMENTS.0) $catch ('Any')('-999-')) != '-999-') (' and client.id_type = ' + ARGUMENTS.0)
 * $else ('')
 * )~
 * connect by prior client.id_client = client.id_parent
 * start with  ~($if (($try (ARGUMENTS.0) $catch ('Any')('-999-'))  != '-999-') ('id_client = ' + ARGUMENTS.0)
 * $else ('id_parent is null')
 * )~
 * ) client, territory
 * where client.id_terr = territory.id_territory (+)"
 * grouping = "name_client"
 * sorting = "1"
 * treeparam = "lvl;client.id_client;client.id_parent;name_client"
 * editable = no
 * } // gp
 * } // treewiew
 * {button
 * alias = but2
 * left=400   height=20       width=100   top =10
 * label="OK"
 * action="($X
 * ($if (($g.tree@getLevel) == 0)
 * (
 * (gr.client.id_client = -1)
 * (gr.client.name_client = '�� ��������')
 * )
 * )
 * ($g.self@acthok)
 * )"
 * shortcut = "F3"
 * }
 * {button
 * alias = but3
 * left=500   height=20       width=100   top =10
 * label="������� ESC"
 * action = "($g.self@actcancel)"
 * shortcut = "ESCAPE"
 * }
 * }
 * @endcode
 */
public interface DocumentAPI extends RmlContainerAPI {

    /**
     * ����������� ���� �� ���������� � ������-��������� ��������� � RML-�����������
     *
     * @return ����
     */
    @Keep
    String getPath();

    /**
     * ����������� ��� ����� � ��������� ���������
     *
     * @return ��� �����
     */
    @Keep
    String getName();

    /**
     * ��������� ������ �� ���� Datastore � ����
     *
     * @return
     */
    @Keep
    int actSave();

    /**
     * ������� �������� ��� ����������
     *
     * @return
     */
    @Keep
    int actCancel();

    /**
     * ��������� ������ �� ���� Datastore � ���� � ������� ��������
     *
     * @return
     */
    @Keep
    int actDoc();

    /**
     * ��������� ������ � ������ STORE � ������� ��������
     *
     * @return
     */
    @Keep
    int actHok();

    /**
     * ������� �������� � ����� ����
     *
     * @return
     */
    @Keep
    int actNew();

    /**
     * ��������� action
     *
     * @param action ������ ��� ���������� action
     */
    @Keep
    void doAction(String action) throws Exception;

    /**
     * ��������� ������ � ����������� ������
     *
     * @param script ������
     * @return ��������� ���������� �������
     */
    @Keep
    Object calculate(String script) throws Exception;


    /**
     * ��������� ������ � �� ����������� ������ (������ ������� � ��������� ������)
     *
     * @param script ������
     */
    @Keep
    void runInBackground(String script) throws Exception;

    /**
     * �������� ���� � RML-�����������
     *
     * @return ���� � �����������
     */
    @Keep
    String getServerParth();

    /**
     * �������� ��� ������� ���������
     *
     * @return ������� ��������
     */
    @Keep
    Hashtable<String, Object> getAliases();


    /**
     * @param alias
     * @return ������
     */
    @Keep
    Object getObject(String alias);

    /**
     * ��������� � ��������� RML-��������
     *
     * @param file - ��� RML-�����
     * @return ������ Proper
     */
    @Keep
    Proper loadRml(String file) throws Exception;

    /**
     * ��������� RML-���������
     *
     * @param rml - ��� RML-�����
     * @return ������ Proper
     */
    @Keep
    Proper parse(String rml) throws Exception;

    /**
     * ����� �������� �����
     *
     * @return ���� � �����
     */
    @Keep
    File openFileDlg();


    /**
     * ��������� ���������� �����
     *
     * @param file ���� � �����
     * @return ���������� �����
     */
    @Keep
    String readFile(String file) throws IOException;

    /**
     * �������� ���������� � �����
     *
     * @param file  ���� � �����
     * @param value ���������� ��� ������
     */
    @Keep
    void writeToFile(String file, byte[] value) throws IOException;

    /**
     * ��������� ��������� ����� (��������� ������) � ������������� ������
     *
     * @param cmd ��������� ��� ����������
     */
    @Keep
    void shell(String cmd) throws IOException;

    /**
     * ��������� ��������� ����� (��������� ������) � ����������� ������
     *
     * @param cmd ��������� ��� ����������
     * @return ��� ������
     */
    @Keep
    int shellWait(String cmd) throws IOException, InterruptedException;

    /**
     * ���������������� ������ � ����� ������ Rml-�������� ���������
     *
     * @param rmlObject - ������
     */
    @Keep
    void registrate(RmlObject rmlObject);

    /**
     * �������� ���� ��������� � ������� ������������
     */
    @Keep
    void initDocumentMenu();

    /**
     * �������� �������� ������� � ���� ���������
     * ������ ��������� �������� ����:
     *
     * @param item - ������� ����
     * @code importClass(javax.swing.JMenu);
     * importClass(javax.swing.JMenuItem);
     * <p>
     * i1 = new JMenuItem('Test1');
     * i1.setActionCommand('~js:{THEME_TEST.setValue(11); \'\'; }~');
     * i2 = new JMenu('Test2');
     * i3 = new JMenu('Test3');
     * m1 = new JMenu('TestMenu1');
     * i11 = new JMenuItem('Test11');
     * i12 = new JMenuItem('Test12');
     * i13 = new JMenuItem('Test13');
     * m1.add(i11);
     * m1.add(i12);
     * m1.add(i13);
     * <p>
     * SELF.addMenu(m1);
     * SELF.addMenu(i1);
     * SELF.addMenu(i2);
     * SELF.addMenu(i3);
     * SELF.initDocumentMenu();
     * @endcode
     */
    @Keep
    void addMenu(JMenu item);
}
