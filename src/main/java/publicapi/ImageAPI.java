package publicapi;

import proguard.annotation.Keep;

import java.awt.*;

public interface ImageAPI extends VisualRmlObjectAPI {
    /**
     * ���������� ������� �����������
     *
     * @return �����������
     */
    @Keep
    Image getImage();

    /**
     * ��������� ����������� �� RML-�����������
     *
     * @param name - ���� � �����-����������� � RML-�����������
     * @return ����������� �����������
     */
    @Keep
    Image getImage(String name);

}
