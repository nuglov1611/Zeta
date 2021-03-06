package publicapi;

import core.document.Document;
import core.parser.Proper;
import proguard.annotation.Keep;

/**
 * ����������� ������ � ������� "����"
 * ��� ����� ���� ��� ���������� ����������:
 * - SplitPane
 * - Grid
 * - Button
 * - Label
 * <p>
 * ��� � ���������� ���������� ��� ������ � �� � ������ ��������
 * - Datastore
 * - DSCollection
 * � �.�.
 * <p>
 * ������� ������ ����� � ������� ������� RML-��������:
 *
 * @author uglov
 * @code {
 * importPackage(org.apache.log4j);
 * importPackage(java.util);
 * importPackage(Packages.publicapi);
 * log = LogManager.getLogger('PreloadScript');
 * hash = new Hashtable();
 * log.debug('������� label !');
 * newlabel  = RmlObjectFactory.createObject('label');
 * hash.clear();
 * left = new java.lang.Integer(left);
 * top = new java.lang.Integer(top);
 * width = new java.lang.Integer(width);
 * height = new java.lang.Integer(height);
 * font_size = new java.lang.Integer(font_size);
 * hash.put('left', left);
 * hash.put('top', top);
 * hash.put('width', width);
 * hash.put('height', height);
 * hash.put('font_size', font_size);
 * hash.put('value', value);
 * newlabel.init(RmlObjectFactory.getProperties(hash),SELF);
 * }
 * @endcode RML-�������, ����������� ��������� RmlContainerAPI ����� ��������� � ���� ������ �������
 */
public interface RmlObjectAPI {


    /**
     * ������������� RML-�������
     *
     * @param prop - ����� ����������
     * @param doc  - ������ �� ��������
     */
    @Keep
    void init(Proper prop, Document doc);


}
