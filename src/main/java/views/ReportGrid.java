package views;

import action.api.Calc;
import action.api.ScriptApi;
import core.document.Document;
import core.parser.Proper;
import core.rml.Container;
import core.rml.RmlConstants;
import core.rml.RmlObject;
import core.rml.VisualRmlObject;
import core.rml.ui.EDTInvocationHandler;
import core.rml.ui.interfaces.ZComponent;
import publicapi.RmlContainerAPI;
import views.grid.GridColumn;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Vector;

public class ReportGrid extends VisualRmlObject implements RmlContainerAPI {

    private class RGrid extends JComponent implements ZComponent {

        @Override
        public JComponent getJComponent() {
            return this;
        }

        @Override
        public Component add(ZComponent component) {
            return super.add(component.getJComponent());
        }

        @Override
        public Component add(String positionForBorder, ZComponent component) {
            return super.add(positionForBorder, component.getJComponent());
        }

        @Override
        public void add(ZComponent component, Object constraints) {
            super.add(component.getJComponent(), constraints);
        }

        @Override
        public void remove(ZComponent component) {
            super.remove(component.getJComponent());
        }

    }

    private Container container = new Container(this);

    private ZComponent reportGrid = (ZComponent) java.lang.reflect.Proxy.newProxyInstance(RGrid.class.getClassLoader(),
            new Class[]{ZComponent.class}, new EDTInvocationHandler(new RGrid()));

    int sizeRow = 20;

//    int                               left       = 1;    // смещение грида по оси X(<1 нельзя!)
//
//    int                               top        = 0;    // смещение грида по оси Y

    public core.rml.dbi.Datastore ds;

    GridColumn[] columns;

    int[] helpArray;

    int[] calcArray;

    int offset = 0;    // смещение в пикселах для текущей страницы,

    // по которому будет идти отрисовка строк ReportGrid
    // int numRows = 0;
    public int beginRow = 0;    // начальная строка в Datastore - источнике данных

    public int endRow = 0;    // конечная строка в Datastore - источнике данных

    // Group group=null;//группа, к которой принадлежит даннный ReportGrid
    // Dimension freePageSize = null;

    int freeHeight = 0;

    // Размер свободной части страницы
    // (часть пространства может быть занята, например, колонтитулами),
    // в которой производится отрисовка
    boolean drawIt = false;

    boolean isPrint = false;

    // определяет, каким образом будет нарисована сетка в гриде
    // 0 бит (1) - обрамляющий Rectangle
    // 1 бит (2) - вертикальные лин
    // 2 бит (4) - горизонтальные лин
    int drawGrid = 0;

    private Report parent = null;

    private Font[] fonts = null;


    public void init(Proper prop, Document doc) {
        super.init(prop, doc);

        Integer ip = null;
        ip = (Integer) prop.get("ROWSIZE");
        if (ip != null) {
            sizeRow = ip.intValue();
        }

        ip = (Integer) prop.get("DRAWGRID");
        if (ip != null) {
            drawGrid = ip.intValue();
        }

        ip = (Integer) prop.get("LEFT");
        if (ip != null) {
            left = ip.intValue();
        }

        ip = (Integer) prop.get("TOP");
        if (ip != null) {
            top = ip.intValue();
        }
    }

    public void setParent(Report parent) {
        this.parent = parent;
    }

    public void initChildren() {

        RmlObject[] objs = container.getChildren();

        int cc = 0;
        int vc = 0;
        try {
            for (RmlObject obj1 : objs) {
                if (obj1 == null) {
                    throw new Error(
                            "~views.ReportGrid::addChildren : Object views.Grid cannot be created!");
                }
                if (obj1 instanceof GridColumn) {
                    cc++;
                    if (((GridColumn) obj1).isVisible()) {
                        vc++;
                    }
                }
            }
            columns = new GridColumn[cc];
            helpArray = new int[vc];
            cc = 0;
            vc = 0;
            for (RmlObject obj : objs) {
                if (obj instanceof GridColumn) {
                    columns[cc] = (GridColumn) obj;
                    columns[cc].setParent(this);
                    if (columns[cc].isVisible()) {
                        helpArray[vc] = cc;
                        vc++;
                    }
                    cc++;
                }
            }

            createCalcSequence();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("~views.Grid::addChildren : " + e);
        }
        fonts = new Font[helpArray.length];
    }

    public void createFonts(int a) {
        for (int i = 0; i < helpArray.length; i++) {
            Font tmp = getVColumn(i).getFontProperty(RmlConstants.FONT);
//            fonts[i] = new Font(tmp.getName(), tmp.getStyle(), tmp.getSize()
//                    * a / 100);
            fonts[i] = tmp.deriveFont(tmp.getSize() * a / 100);
        }
    }

    public GridColumn getVColumn(int i) {
        try {
            return columns[helpArray[i]];
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int[] getOutPoint(GridColumn col, int x, int y, String str) {
        if (col == null) {
            return null;
        }
        int xp, yp;
        int width = col.getColumn().getWidth();
        int height = sizeRow;
        int sw = col.fm.stringWidth(str);
        int sh = col.fm.getHeight() - col.fm.getDescent();
        int desc = 0; // col.fm.getDescent();
        int wwidth = width - 2 * col.dw;
        int wheight = height - 2 * col.dh;
        if (col.getStringProperty(RmlConstants.HALIGNMENT).equals("LEFT")) {
            xp = x + col.dw;
        } else if (col.getStringProperty(RmlConstants.HALIGNMENT).equals("RIGHT")) {
            xp = x + col.dw + wwidth - sw;
        } else if (col.getStringProperty(RmlConstants.HALIGNMENT).equals("CENTER")) {
            xp = x + col.dw + (wwidth - sw) / 2;
        } else {
            xp = x + col.dw;
        }

        if (col.getStringProperty(RmlConstants.VALIGNMENT).equals("BOTTOM")) {
            yp = y + col.dh + wheight - desc;
        } else if (col.getStringProperty(RmlConstants.VALIGNMENT).equals("TOP")) {
            yp = y + col.dh + sh - desc;
        } else if (col.getStringProperty(RmlConstants.VALIGNMENT).equals("CENTER")) {
            yp = y + col.dh + sh + (wheight - sh) / 2 - desc;
        } else {
            yp = y + col.dh + wheight - desc;
        }

        int[] ret = new int[2];
        ret[0] = xp - 1;
        ret[1] = yp;
        return ret;
    }

    public int getNumRows() {
        return endRow - beginRow + 1;
    }

    public void setDatastore(core.rml.dbi.Datastore ds) {
        this.ds = ds;
        for (GridColumn column : columns) {
            if (column.getTarget() == null) {
                if (column.getType() == Integer.MIN_VALUE) {
                    System.out
                            .println("views.Grid.addChildren says : type for computed column not defined!");
                    continue;
                }
                column.setTarget(ds.addColumn(column.getType()));
            }
        }
    }

    public core.rml.dbi.Datastore getDATASTORE() {
        return ds;
    }

    public void setFreeHeight(int height) {
        this.freeHeight = height;
    }

    public int getFreeHeight() {
        return freeHeight;
    }

    // рисует строки ReportGrid'а в данном графическом контексте,
    // отображенном на текущую страницу.
    // Возвращает количество строк, которые уместились на стран

    public int drawRows(Graphics g, int a) {
        int drawedRows = 0;
        // System.out.println("inside drawRows");
        // System.out.println("drawIt="+drawIt);
        // System.out.println("freeHeight="+freeHeight);
        // System.out.println("offset="+offset);
        if (drawGrid != 0) {
            freeHeight--;
        }
        // freeHeight-=top;
        drawedRows = Math.min(freeHeight / sizeRow, endRow - beginRow + 1);
        if (!drawIt || (a <= 0) || drawedRows == 0) {
            // freeHeight-=drawedRows*sizeRow;
            return drawedRows;
        }
        int colw = getColumnsSize();
        SmartLine line = new SmartLine(g);
        line.isPrint = parent.isPrint;
        // createFonts(a);
        if ((drawGrid & 2) != 0) {
            for (int i = 0; i < helpArray.length; i++) {
                int x = getColumnX(i);
                // g.setClip(x+left,offset,2,drawedRows*sizeRow);
                g.setColor(Color.black);
                line.setType(1);
                line.draw(x + left, offset, drawedRows * sizeRow /* +1 */, a); // (
                // !
                // отрисовка
                // с
                // масштабированием
                // !
                // )
            }
        }
        if ((drawGrid & 1) != 0) {
            g.setColor(Color.black);
            line.setType(0);
            line.draw(left, offset, getColumnsSize(), a); // (!отрисовка с
            // масштабированием
            // !)
            line.draw(left, offset + drawedRows * sizeRow, getColumnsSize(), a);
            line.setType(1);
            line.draw(left, offset, drawedRows * sizeRow, a); // (!отрисовка с
            // масштабированием
            // !)
            line.draw(left + getColumnsSize(), offset,
                    drawedRows * sizeRow + 1, a); // (!отрисовка с
            // масштабированием!)
            // g.drawRect(left, offset, getColumnsSize(), drawedRows*sizeRow);
        }

        for (int i = beginRow; i <= endRow; i++) {
            int y = getRowY(i - beginRow);
            if ((drawGrid & 4) != 0) {
                g.setColor(Color.black);
                // g.setClip(0+left,offset+y,colw+1,2);
                line.setType(0);
                line.draw(0 + left, offset + y, colw, a); // (!отрисовка с
                // масштабированием
                // !)
            }
            if (sizeRow > freeHeight) {
                return (i - beginRow);
            }
            freeHeight -= sizeRow;
            for (int j = 0; j < helpArray.length; j++) {
                g.setFont(fonts[j]);
                int x = getColumnX(j);
                // x-=delta;
                int height = sizeRow;
                int width = getVColumn(j).getColumn().getWidth();
                String str = getSourceText(i, j);
                GridColumn col = getVColumn(j);
                int cdw = col.dw;
                if (col.getStringProperty(RmlConstants.HALIGNMENT).equals("RIGHT")) {
                    cdw = cdw * 5 / 2;
                }
                int[] xy = UTIL.getOutPoint(col.getColumn().getWidth(), sizeRow, col.fm,
                        col.getStringProperty(RmlConstants.HALIGNMENT), col.getStringProperty(RmlConstants.VALIGNMENT), cdw, col.dh,
                        x + left, offset + y, str);

                g.setClip((left + x + 1) * a / 100, (offset + y + 1) * a / 100,
                        (width - 1) * a / 100, (height - 1) * a / 100);
                g.setColor(Color.black);
                g.drawString(str, xy[0] * a / 100, xy[1] * a / 100);
                // System.out.println("str="+str+" x="+xy[0]+" y="+xy[1]);
            }
        }
        // g.setClip(null);
        // g.setClip(0,0,30000,30000);
        return getNumRows();
    }

    public int getColumnX(int col) {
        int sum = 0;
        for (int i = 0; i < col; i++) {
            sum += getVColumn(i).getColumn().getWidth();
        }
        return sum;
    }

    int getColumnNumByAlias(String al) {
        if (columns == null) {
            return -1;
        }
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].getAlias() != null) {
                if (columns[i].getAlias().equals(al)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int getColumnsSize() {
        int sum = 0;
        for (int i = 0; i < helpArray.length; i++) {
            sum += getVColumn(i).getColumn().getWidth();
        }
        return sum;
    }

    public int getRowY(int row) {
        return row * sizeRow;
    }

    public String getSourceText(int r, int c) {
        Object value = ds.getValue(r, getVColumn(c).getTarget());
        // System.out.println("value="+value);
        try {
            if (value != null) {
                // System.out.println("validator type = "+columns[c].validator.
                // type);
                return getVColumn(c).getValidator().toString(value);
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("~views.Grid::getSourceText() : " + e);
            return "";
        }

    }

    void createCalcSequence() { // создает последовательность для вычисления
        // Computed Column'ов
        if (columns == null) {
            return;
        }
        Vector<String> names = new Vector<String>();
        Vector<Vector<String>> Bn = new Vector<Vector<String>>();
        for (GridColumn column : columns) {
            String alias = column.getAlias();
            if (alias != null && column.getStringProperty(RmlConstants.EXP) != null) { // кладем его в
                // вектор names
                names.addElement(alias);
                Vector<String> bi = new Vector<String>();
                String cc = column.getCalc();
                String[] als = null;
                try {
                    if (cc != null) {
                        als = ((Calc) ScriptApi.getAPI(cc)).getAliases();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (als != null) {
                    for (String al : als) {
                        if (al != null
                                && (!al.equals(column.getAlias()))) {
                            bi.addElement(al);
                        }
                    }
                }
                Bn.addElement(bi);

            }
        } // end of for
        Vector<String> ret = null;
        if (names.size() > 0) {
            try {
                ret = UTIL.createSequence(names, Bn);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("views.ReportGrid::createCalcSequence() : "
                        + e);
                throw new Error(e.getMessage());
            }
        }
        if (ret != null) {
            if (ret.size() > 0) {
                calcArray = new int[ret.size()];
            } else {
                return;
            }

            for (int i = 0; i < ret.size(); i++) {
                String name = ret.elementAt(i);
                int index = 0;
                index = getColumnNumByAlias(name);
                if (index != -1) {
                    calcArray[i] = index;
                }
            }
        }
    } // end of create sequence

    @Override
    public void focusThis() {
        // TODO Auto-generated method stub

    }

    @Override
    public ZComponent getVisualComponent() {
        return reportGrid;
    }

    @Override
    public void addChild(RmlObject child) {
    }

    @Override
    public RmlObject[] getChildren() {
        return container.getChildren();
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public boolean addChildrenAutomaticly() {
        return true;
    }

    @Override
    protected Border getDefaultBorder() {
        // TODO Auto-generated method stub
        return null;
    }

}
