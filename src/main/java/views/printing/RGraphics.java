package views.printing;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.io.DataOutputStream;
import java.text.AttributedCharacterIterator;

import views.UTIL;

public class RGraphics extends Graphics {
    DataOutputStream dos   = null;

    protected Font   font  = null;

    protected Color  color = null;

    RGraphics(DataOutputStream dos) {
        this.dos = dos;
    }

    @Override
    public void clearRect(int x, int y, int width, int height) {
    }

    @Override
    public void clipRect(int x, int y, int width, int height) {
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
    }

    @Override
    public Graphics create() {
        return this;
    }

    @Override
    public Graphics create(int x, int y, int width, int height) {
        return this;
    }

    @Override
    public void dispose() {
        // System.out.println("inside RGraphics.dispose()");
        writeCommand(PrintConstants.END_PAGE + "," + "1" + "," + "1");
        flush();
    }

    @Override
    public void draw3DRect(int x, int y, int width, int height, boolean raised) {
    }

    @Override
    public void drawArc(int x, int y, int width, int height, int startAngle,
            int arcAngle) {
    }

    @Override
    public void drawBytes(byte data[], int offset, int length, int x, int y) {
    }

    @Override
    public void drawChars(char data[], int offset, int length, int x, int y) {
    }

    @Override
    public boolean drawImage(Image img, int x, int y, Color bgcolor,
            ImageObserver observer) {
        return true;
    }

    @Override
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        return true;
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height,
            Color bgcolor, ImageObserver observer) {
        return true;
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height,
            ImageObserver observer) {
        return true;
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
            int sx1, int sy1, int sx2, int sy2, Color bgcolor,
            ImageObserver observer) {
        return true;
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
            int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        return true;
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        writeCommand(PrintConstants.drawLine + "," + "1" + "," + "1" + "," + x1
                + "," + y1 + "," + x2 + "," + y2);
    }

    public void drawLine(int x1, int y1, int x2, int y2, int w) {
        writeCommand(PrintConstants.drawLine + "," + "1" + "," + "1" + "," + x1
                + "," + y1 + "," + x2 + "," + y2 + "," + w);
    }

    @Override
    public void drawOval(int x, int y, int width, int height) {
    }

    @Override
    public void drawPolygon(int xPoints[], int yPoints[], int nPoints) {
    }

    @Override
    public void drawPolygon(Polygon p) {
    }

    @Override
    public void drawPolyline(int xPoints[], int yPoints[], int nPoints) {
    }

    @Override
    public void drawRect(int x, int y, int width, int height) {
        writeCommand(PrintConstants.drawRect + "," + "1" + "," + "1" + "," + x
                + "," + y + "," + width + "," + height);
    }

    @Override
    public void drawRoundRect(int x, int y, int width, int height,
            int arcWidth, int arcHeight) {
    }

    @Override
    public void drawString(
            AttributedCharacterIterator attributedCharacterIterator, int int1,
            int int2) {
    }

    @Override
    public void drawString(String str, int x, int y) {
        writeCommand(PrintConstants.drawString + "," + "1" + "," + "1" + ","
                + x + "," + y + ",\"" + UTIL.toCommandString(str));
        /*
         * Command com = null; try { com = new Command(c.drawString,"1","1",new
         * int[]{x,y},new String[]{str}); writeCommand(com.getString());
         * }catch(Exception e){}
         */

    }

    @Override
    public void fill3DRect(int x, int y, int width, int height, boolean raised) {
    }

    @Override
    public void fillArc(int x, int y, int width, int height, int startAngle,
            int arcAngle) {
    }

    @Override
    public void fillOval(int x, int y, int width, int height) {
    }

    @Override
    public void fillPolygon(int xPoints[], int yPoints[], int nPoints) {
    }

    @Override
    public void fillPolygon(Polygon p) {
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        writeCommand(PrintConstants.fillRect + "," + "1" + "," + "1" + "," + x
                + "," + y + "," + width + "," + height);
    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height,
            int arcWidth, int arcHeight) {
    }

    @Override
    public void finalize() {
    }

    public void flush() {
        if (dos != null) {
            try {
                dos.flush();
            }
            catch (Exception e) {
            }
        }
    }

    @Override
    public Shape getClip() {
        return null;
    }

    @Override
    public Rectangle getClipBounds() {
        return null;
    }

    @Override
    public Rectangle getClipRect() {
        return null;
    }

    @Override
    public Color getColor() {
        return null;
    }

    @Override
    public Font getFont() {
        return null;
    }

    @Override
    public FontMetrics getFontMetrics() {
        if (font != null) {
            return getFontMetrics(font);
        }
        else {
            return null;
        }
    }

    @Override
    public FontMetrics getFontMetrics(Font f) {
        return Toolkit.getDefaultToolkit().getFontMetrics(f);
    }

    @Override
    public void setClip(int x, int y, int width, int height) {
        writeCommand(PrintConstants.setClip + "," + "1" + "," + "1" + "," + x
                + "," + y + "," + width + "," + height);
    }

    @Override
    public void setClip(Shape clip) {
        Rectangle r = null;
        if (clip != null) {
            r = clip.getBounds();
        }
        if (r != null) {
            setClip(r.x, r.y, r.width, r.height);
        }
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
        if (color != null) {
            writeCommand(PrintConstants.setColor + "," + "1" + "," + "1" + ","
                    + color.getRGB());
        }
    }

    @Override
    public void setFont(Font font) {
        this.font = font;
        if (font == null) {
            return;
        }
        writeCommand(PrintConstants.setFont + "," + "1" + "," + "1" + ","
                + font.getStyle() + "," + font.getSize() + ",\""
                + font.getName());

    }

    @Override
    public void setPaintMode() {
    }

    @Override
    public void setXORMode(Color c1) {
    }

    @Override
    public String toString() {
        return "views.printing.RGraphics";
    }

    @Override
    public void translate(int x, int y) {
        writeCommand(PrintConstants.translate + "," + "1" + "," + "1" + "," + x
                + "," + y);
    }

    protected void writeCommand(Command com) {
        if (dos != null) {
            try {
                dos.writeUTF(com.getString());
            }
            catch (Exception e) {
            }
            ;
        }
    }

    protected void writeCommand(String com) {
        // System.out.println("inside RGraphics.writeCommand");
        // System.out.println("command="+com);
        if (dos != null) {
            try {
                dos.writeUTF(com);
            }
            catch (Exception e) {
            }
            ;
        }
    }
}
