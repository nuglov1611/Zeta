package publicapi;

import proguard.annotation.Keep;

import java.awt.*;

/**
 * ��������� ��� Rml-��������, ������� ����������� �������������.
 * ��� ��������� ���������� ����� ������������ ����� ����������:
 * - BACKGROUND - ���� ����
 * - FOREGROUND - ���� ������
 * - FONT - ��������� ������ (��������, ������, �����)
 * - ���� ����� �������� ��������� �������� ���� �� �����:
 * - FONT_FACE
 * - FONT_FAMILY
 * - FONT_SIZE
 * - LEFT - � ���������� ������ �������� ����
 * - TOP - Y ���������� ������ �������� ����
 * - WIDTH - �����
 * - HEIGHT - ������
 * - VISIBLE - (yes/no) ��������� ����������
 * - FIRSTFOCUS - (yes/no) ���������� �� ��������� ������ ��� �������� ���������
 * - POSITION - ��������� ���������� ���� � ���������� ������ Border Layout (CENTER, EAST, WEST, SOUTH, NORTH)
 * - TOOLTIPTEXT - ����� ����������� ���������
 *
 * @author nuglov
 *         {@inheritDoc}
 */
public interface VisualRmlObjectAPI extends RmlObjectAPI {

    /**
     * ���������� ������� ������������ ����������.
     *
     * @return ������ �������� (width, height)
     */
    @Keep
    Dimension getSize();

    /**
     * ���������� �������� �������� ��������� ������������ ����������.
     *
     * @return true - �������
     */
    @Keep
    boolean isVisible();

    /**
     * ���������� ���������� ������������ ����������.
     *
     * @return ���������� �������� ������ ����
     */
    @Keep
    Point getPosition();

    /**
     * ���������� ������� ������� (������ � ������)
     *
     * @param w - ������
     * @param h - ������
     */
    @Keep
    void setSize(int w, int h);

    /**
     * ���������� ������ ����������� ����������
     *
     * @param h ������
     */
    @Keep
    void setHeight(int h);

    /**
     * ���������� ������ ����������� ����������
     *
     * @param w ������
     */
    @Keep
    void setWidth(int w);

    /**
     * �������� �����
     */
    @Keep
    void requestFocus();

    /**
     * �������� ������ ����������� ������������ ���������� ��������� �����
     *
     * @return true - ���� ������ ����� ��������� �����, false - ���� �� �����
     */
    @Keep
    boolean isFocusable();

    /**
     * ������ ����������� ������������ ���������� ��������� �����
     *
     * @param focusable true - ��������� ����� ��������� �����, false - �� ����� ��������� �����
     */
    @Keep
    void setFocusable(boolean focusable);

    /**
     * ������ ���������� ������������ ���������� �� ����������� � ���������
     *
     * @param x ���������� �� �����������
     * @param y ���������� �� ���������
     */
    @Keep
    void setLocation(int x, int y);

    /**
     * ������ ���������� ������������ ���������� �� ����������� (���������� ����� �������)
     *
     * @param x ����������
     */
    @Keep
    void setLeft(int x);

    /**
     * ������ ���������� ������������ ���������� �� ��������� (���������� ������� �������)
     *
     * @param y ����������
     */
    @Keep
    void setTop(int y);

    /**
     * ������ ������� "����������" ������������ ����������
     *
     * @param enabled ������� (true - ��������� ��������, false - �� ��������("�����"))
     */
    @Keep
    void setEnabled(boolean enabled);

    /**
     * ������ ������� ��������� ������������ ����������
     *
     * @param visible ������� (true - �������, false - �� �������)
     */
    @Keep
    void setVisible(boolean visible);

}
