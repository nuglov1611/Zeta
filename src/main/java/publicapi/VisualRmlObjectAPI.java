package publicapi;

import java.awt.Dimension;
import java.awt.Point;

import proguard.annotation.Keep;

/**
 * ��������� ��� Rml-��������, ������� ����������� �������������.
 * ��� ��������� ���������� ����� ������������ ����� ����������:
 *- BACKGROUND - ���� ����
 *- FOREGROUND - ���� ������
 *- FONT - ��������� ������ (��������, ������, �����)
 *- ���� ����� �������� ��������� �������� ���� �� �����:
 *	- FONT_FACE
 *	- FONT_FAMILY
 *	- FONT_SIZE
 *- LEFT - � ���������� ������ �������� ����
 *- TOP - Y ���������� ������ �������� ����
 *- WIDTH - �����
 *- HEIGHT - ������
 *- VISIBLE - (yes/no) ��������� ����������
 *- FIRSTFOCUS - (yes/no) ���������� �� ��������� ������ ��� �������� ���������
 *- POSITION - ��������� ���������� ���� � ���������� ������ Border Layout (CENTER, EAST, WEST, SOUTH, NORTH)
 *- TOOLTIPTEXT - ����� ����������� ���������
 * 
 * 
 * 
 * @author nuglov
 * {@inheritDoc}
 */
public interface VisualRmlObjectAPI extends RmlObjectAPI {

    /**
     * ���������� ������� ������������ ����������.
     * @return ������ �������� (width, height)
     */
    @Keep
    public Dimension getSize();
    
    /**
     * ���������� �������� �������� ��������� ������������ ����������.
     * @return true - �������
     */
    @Keep
    public boolean isVisible();
    
    /**
     * ���������� ���������� ������������ ����������.
     * @return ���������� �������� ������ ����
     */
    @Keep
    public Point getPosition();
    
    /**
     * ���������� ������� ������� (������ � ������)
     * @param w - ������
     * @param h - ������
     */
    @Keep
    public void setSize(int w, int h);
    
    /**
     * ���������� ������ ����������� ����������
     * @param h ������
     */
    @Keep
    public void setHeight(int h);
    
    /**
     * ���������� ������ ����������� ����������
     * @param w ������ 
     */
    @Keep
    public void setWidth(int w);
  
    /**
     * �������� ����� 
     */
    @Keep
    public void requestFocus();

    /**
     * �������� ������ ����������� ������������ ���������� ��������� �����
     * @return true - ���� ������ ����� ��������� �����, false - ���� �� �����
     */
    @Keep
    public boolean isFocusable();
   
    /**
     * ������ ����������� ������������ ���������� ��������� �����
     * @param focusable true - ��������� ����� ��������� �����, false - �� ����� ��������� �����
     */
    @Keep
    public void setFocusable(boolean focusable);
 
    /**
     * ������ ���������� ������������ ���������� �� ����������� � ���������
     * @param x ���������� �� �����������
     * @param y ���������� �� ���������
     */
    @Keep
    public void setLocation(int x, int y);
    
    /**
     * ������ ���������� ������������ ���������� �� ����������� (���������� ����� �������)
     * @param x ����������
     */
    @Keep
    public void setLeft(int x);

    /**
     * ������ ���������� ������������ ���������� �� ��������� (���������� ������� �������)
     * @param y ����������
     */
    @Keep
    public void setTop(int y);
    
    /**
     * ������ ������� "����������" ������������ ����������
     * @param enabled ������� (true - ��������� ��������, false - �� ��������("�����"))
     */
    @Keep
    public void setEnabled(boolean enabled);

    /**
     * ������ ������� ��������� ������������ ����������
     * @param visible ������� (true - �������, false - �� �������)
     */
    @Keep
    public void setVisible(boolean visible);

}
