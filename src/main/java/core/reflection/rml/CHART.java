package core.reflection.rml;

import core.document.Document;
import core.parser.Proper;
import core.rml.Container;
import core.rml.RmlConstants;
import core.rml.RmlObject;
import core.rml.VisualRmlObject;
import core.rml.ui.impl.ZChartPanelImpl;
import core.rml.ui.impl.ZPanelImpl;
import core.rml.ui.interfaces.ZComponent;
import core.rml.ui.interfaces.ZPanel;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import publicapi.ChartAPI;
import publicapi.RetrieveableAPI;
import publicapi.RmlContainerAPI;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.StringTokenizer;


/**
 * Инструмент для построения графиков и диаграмм
 */
public class CHART extends VisualRmlObject implements ChartAPI, RetrieveableAPI, RmlContainerAPI {
    private static final Logger log = Logger.getLogger(CHART.class);

    /**
     *
     */

    private Container container = new Container(this);

    private DATASTORE ds = null;

    private int style = 0;

    private int xcolumn = 0;          // column, который надо рисовать по x

    private int[] ycolumns = {1};      // column, который надо рисовать по y

    private int xlcolumn = 0;          // column со значениями легенды х

    private Color bgColor = null;

    private String[] names = {};

    private JFreeChart chart = null;
    private ZPanel chpanel = ZPanelImpl.create();
    private String title = null;
    private String[] exploded = {};

    private final static HashMap<String, Integer> styleMap = new HashMap<String, Integer>();

    static {
        styleMap.put("PIE", 1);
        styleMap.put("XY", 2);
        styleMap.put("BAR", 3);
        styleMap.put("PIE3D", 4);
    }

    public void fromDS() {
    }

    public void init(Proper prop, Document doc) {
        String sp;
        Integer ip;

        chpanel.setLayout(new GridLayout(1, 1));

        sp = (String) prop.get(RmlConstants.ALIAS);
        alias = sp;

        sp = (String) prop.get("STYLE");
        if (sp != null) {
            style = styleMap.get(sp);
        }


        ip = ((Integer) prop.get("XCOLUMN"));
        if (ip != null) {
            xcolumn = ip.intValue();
        }

        ip = ((Integer) prop.get("XAXISCOLUMN"));
        if (ip != null) {
            xlcolumn = ip.intValue();
        }

        title = (String) prop.get("TITLE");

        sp = (String) prop.get("DATACOLUMNS");
        try {
            log.debug("sp=" + sp);
            StringTokenizer st = new StringTokenizer(sp, ",");
            int ct = st.countTokens();
            ycolumns = new int[ct];
            for (int i = 0; i < ct; i++) {
                String s = st.nextToken();
                ycolumns[i] = Integer.parseInt(s);

            }
        } catch (Exception e) {
            log.error("not setting datacolumns!!!", e);
        }
        sp = (String) prop.get("LEGENDA");
        try {
            StringTokenizer st = new StringTokenizer(sp, ",");
            int ct = st.countTokens();
            names = new String[ct];
            for (int i = 0; i < ct; i++) {
                String s = st.nextToken();
                names[i] = s;
            }
        } catch (Exception e) {
            log.error("not setting dadacolumns!!!", e);
        }

        sp = (String) prop.get("EXPLODED");
        if (sp != null) {
            StringTokenizer st = new StringTokenizer(sp, ",");
            int ct = st.countTokens();
            exploded = new String[ct];
            for (int i = 0; i < ct; i++) {
                String s = st.nextToken();
                exploded[i] = s;
            }
        }


        super.init(prop, doc);

        createChart();

//        chart.setBackgroundPaint(bgColor);
//        chart.getPlot().setBackgroundPaint(bgColor);
//    
    }


    protected void createChart() {
        chpanel.removeAll();
        try {
            ds.retrieve();
        } catch (Exception e) {
            log.error(e);
        }
        switch (style) {
            case 4:  //pie 3d
            case 1: { //pie
                DefaultPieDataset dataset = new DefaultPieDataset();
                for (int i = 0; i < names.length; i++) {
                    dataset.setValue(names[i], (Double) ds.getValue(i, ycolumns[0]));
                }
                if (style == 1) {
                    chart = ChartFactory.createPieChart(title, dataset, true, true, false);
                } else {
                    chart = ChartFactory.createPieChart3D(title, dataset, true, true, false);
                }
                for (String exp : exploded) {
                    ((PiePlot) chart.getPlot()).setExplodePercent(exp, 0.2);
                }
                break;
            }
            case 2: { //XY
                XYSeriesCollection dataset = new XYSeriesCollection();
                for (int i = 0; i < names.length; i++) {
                    XYSeries series = new XYSeries(names[i]);
                    for (int j = 0; j < ds.getRowCount(); j++) {
                        series.add((Double) ds.getValue(j, xcolumn), (Double) ds.getValue(j, ycolumns[i]));
                    }
                    dataset.addSeries(series);
                }
                chart = ChartFactory.createXYLineChart(title, null, null, dataset, PlotOrientation.VERTICAL, true, true, false);
                break;
            }

            case 3: { //bar
                DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                for (int i = 0; i < names.length; i++) {
                    for (int j = 0; j < ds.getRowCount(); j++) {
                        dataset.addValue((Double) ds.getValue(j, ycolumns[i]), names[i], (String) ds.getValue(j, xcolumn));
                    }
                }
                chart = ChartFactory.createBarChart(title, null, null, dataset, PlotOrientation.VERTICAL, true, true, false);
                break;
            }
        }
        chpanel.add(ZChartPanelImpl.create(chart));
        chpanel.repaint();
    }

//    public JComponent getChart() throws Exception{
//        retrieve();
//        createChart();
//        return chp;
//    }

    public int retrieve() throws Exception {
        createChart();
        return 0;
    }

    public void toDS() {
    }

    public void update() {
    }

    @Override
    public void focusThis() {
        // TODO Auto-generated method stub

    }

    @Override
    public ZComponent getVisualComponent() {
        return chpanel;
    }

    public void addChild(RmlObject child) {
        if (child instanceof DATASTORE) {
            ds = (DATASTORE) child;
        }
    }

    public RmlObject[] getChildren() {
        return container.getChildren();
    }

    public void initChildren() {
    }

    public Container getContainer() {
        return container;
    }

    public boolean addChildrenAutomaticly() {
        return true;
    }

    @Override
    protected Border getDefaultBorder() {
        return new EmptyBorder(0, 0, 0, 0);
    }
}
