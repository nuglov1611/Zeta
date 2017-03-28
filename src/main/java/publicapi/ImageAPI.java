package publicapi;

import java.awt.Image;

import proguard.annotation.Keep;

public interface ImageAPI extends VisualRmlObjectAPI {
    /**
     * ���������� ������� �����������
     * @return ����������� 
     */
    @Keep
    public Image getImage();
    
    /**
    * ��������� ����������� �� RML-�����������
    * @param name - ���� � �����-����������� � RML-����������� 
    * @return ����������� ����������� 
    */   
    @Keep
    Image getImage(String name);

}
