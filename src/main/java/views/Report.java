package views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.JobAttributes;
import java.awt.PageAttributes;
import java.awt.PrintJob;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;

import loader.ZetaProperties;
import loader.ZetaUtility;

import org.apache.log4j.Logger;

import properties.PropertyConstants;
import properties.PropertyManager;
import publicapi.ReportAPI;
import publicapi.RetrieveableAPI;
import publicapi.RmlContainerAPI;
import views.grid.GridColumn;
import views.printing.RPrintJob;
import action.api.RTException;
import action.calc.objects.class_type;
import core.document.Document;
import core.parser.Proper;
import core.reflection.rml.COLONTITUL;
import core.reflection.rml.REPORTHEADER;
import core.reflection.rml.REPORTTRAILER;
import core.rml.Container;
import core.rml.RmlObject;
import core.rml.VisualRmlObject;
import core.rml.ui.impl.ZButtonImpl;
import core.rml.ui.impl.ZComboBoxImpl;
import core.rml.ui.impl.ZLabelImpl;
import core.rml.ui.impl.ZPanelImpl;
import core.rml.ui.impl.ZScrollPaneImpl;
import core.rml.ui.impl.ZTextFieldImpl;
import core.rml.ui.interfaces.ZButton;
import core.rml.ui.interfaces.ZComboBox;
import core.rml.ui.interfaces.ZComponent;
import core.rml.ui.interfaces.ZLabel;
import core.rml.ui.interfaces.ZPanel;
import core.rml.ui.interfaces.ZScrollPane;
import core.rml.ui.interfaces.ZTextField;

public class Report extends VisualRmlObject implements ReportAPI, RetrieveableAPI, RmlContainerAPI, class_type{

	private ZPanel reportPanel = ZPanelImpl.create(new GridBagLayout());
	
    private class FL extends FocusAdapter {
        public void focusGained(FocusEvent e) {
            tb.mashtab.requestFocus();
        }
    }

    private class KL extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            if (document.executeShortcut(e)) {
                e.consume();
                return;
            }

            if ((e.getModifiers() & (KeyEvent.CTRL_MASK + KeyEvent.ALT_MASK)) == KeyEvent.CTRL_MASK) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    if ((e.getModifiers() & KeyEvent.SHIFT_MASK) == 0) {
                        sp.getHorizontalScrollBar().setValue(
                                sp.getHorizontalScrollBar().getValue()
                                        - sp.getHorizontalScrollBar()
                                                .getUnitIncrement());
                    }
                    else {
                        sp.getHorizontalScrollBar().setValue(
                                sp.getHorizontalScrollBar().getMinimum());
                    }
                    e.consume();
                    return;
                }
                else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    if ((e.getModifiers() & KeyEvent.SHIFT_MASK) == 0) {
                        sp.getHorizontalScrollBar().setValue(
                                sp.getHorizontalScrollBar().getValue()
                                        + sp.getHorizontalScrollBar()
                                                .getUnitIncrement());
                    }
                    else {
                        sp.getHorizontalScrollBar().setValue(
                                sp.getHorizontalScrollBar().getMaximum());
                    }
                    e.consume();
                    return;
                }
                else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    if ((e.getModifiers() & KeyEvent.SHIFT_MASK) == 0) {
                        sp.getVerticalScrollBar().setValue(
                                sp.getVerticalScrollBar().getValue()
                                        - sp.getVerticalScrollBar()
                                                .getUnitIncrement());
                    }
                    else {
                        prevPageAction();
                    }
                    e.consume();
                    return;
                }
                else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if ((e.getModifiers() & KeyEvent.SHIFT_MASK) == 0) {
                        sp.getVerticalScrollBar().setValue(
                                sp.getVerticalScrollBar().getValue()
                                        + sp.getVerticalScrollBar()
                                                .getUnitIncrement());
                    }
                    else {
                        nextPageAction();
                    }
                    e.consume();
                    return;
                }
                else if (e.getKeyCode() == KeyEvent.VK_PAGE_UP) {
                    if ((e.getModifiers() & KeyEvent.SHIFT_MASK) == 0) {
                        sp.getVerticalScrollBar().setValue(
                                sp.getVerticalScrollBar().getValue()
                                        - sp.getVerticalScrollBar()
                                                .getBlockIncrement());
                    }
                    else {
                        prevPageAction();
                    }
                    e.consume();
                    return;
                }
                else if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
                    if ((e.getModifiers() & KeyEvent.SHIFT_MASK) == 0) {
                        sp.getVerticalScrollBar().setValue(
                                sp.getVerticalScrollBar().getValue()
                                        + sp.getVerticalScrollBar()
                                                .getBlockIncrement());
                    }
                    else {
                        nextPageAction();
                    }
                    e.consume();
                    return;
                }
                else if (e.getKeyCode() == KeyEvent.VK_HOME) {
                    if ((e.getModifiers() & KeyEvent.SHIFT_MASK) == 0) {
                        sp.getVerticalScrollBar().setValue(
                                sp.getVerticalScrollBar().getMinimum());
                    }
                    else {
                        firstPageAction();
                    }
                    e.consume();
                    return;
                }
                else if (e.getKeyCode() == KeyEvent.VK_END) {
                    if ((e.getModifiers() & KeyEvent.SHIFT_MASK) == 0) {
                        sp.getVerticalScrollBar().setValue(
                                sp.getVerticalScrollBar().getMaximum());
                    }
                    else {
                        lastPageAction();
                    }
                    e.consume();
                    return;
                }
            }

        }
    }

    private static final Logger log            = Logger.getLogger(Report.class);
    
    private Container container = new Container(this);

    private boolean             update_view    = true;

    // OpenOfficeExport exporterToOO = null;

    String                      query          = null;

    final int                   th             = 65;                                 // высота toolbar'а

    final String                rootGroupAlias = "ROOTGROUP";

    final String                nextPageLabel  = StringBundle.Report_Label_NextPage; // "—лед.стр."


    final String                prevPageLabel  = StringBundle.Report_Label_PrevPage; // "ѕред.стр."


    final String                firstPageLabel = StringBundle.Report_Label_FirstPage; // "ѕерв.стр."

    // ;

    final String                lastPageLabel  = StringBundle.Report_Label_LastPage; // "ѕосл.стр."

    // ;

    final String                printLabel     = StringBundle.Report_Label_Print;

    core.rml.dbi.Datastore               ds             = null;

    ReportForm[]                colon          = new ReportForm[2];

    views.Group                 root           = null;

    core.rml.dbi.Group                   droot          = null;

    ZScrollPane                 sp             = null;                               // new JScrollPane();

    int                         numRows        = 0;                                  // число строк в Datastore;

    MyToolbar                   tb             = null;                               // new MyToolbar();

    WorkCanvas                  workArea       = null;                               // new WorkCanvas();

    // пол€ дл€ печати и разбивки на страницы
    int                         numPages       = 0;

    String                      orientation    = "PORTRAIT";

    int                         currentPage    = 0;                                  // текуща€ страница(инкрементируетс€ внутри

    // processGroup)

    int                         displayPage    = 0;                                  // страница отчета, отображаема€ на дисплее

    int                         printPage      = -1;                                 // страница, которую необходимо напечатать(=-1, если

    // печатаем все)

    int                         offset         = 0;                                  // смещение дл€ отрисовки на текущей странице

    boolean                     isPrint        = false;                              // =true, если вывод на принте

    public Dimension            pageSize       = new Dimension(550, 800);            // magic numbers for

    // A4(portrait)

    boolean                     first_time     = true;

    int                         mashtab;                                             // масштаб при отрисовке в контекте диспле€

    Image                       image          = null;                               // здесь содержимое текущей страницы

    Graphics                    curGraphics    = null;

    PrintJob                    pjob           = null;

    boolean                     needDrawing    = false;

    boolean                     printed        = false;

    boolean                     needCreatePage = true;

    boolean                     wrepaint       = true;

    public Report() {
        workArea = new WorkCanvas();
        sp = ZScrollPaneImpl.create(workArea);
        tb = new MyToolbar();
        reportPanel.setSize(800, 500);
        GridBagConstraints c = new GridBagConstraints();
        reportPanel.setBackground(Color.white);
        workArea.setBackground(Color.WHITE);
        c.gridx = 0;
        c.gridy = 0;
        c.ipady = th;
        c.weighty = th;
        c.weightx = 600;
        c.fill = GridBagConstraints.BOTH;
        reportPanel.add(tb, c);

        workArea.setPreferredSize(new Dimension(50 + pageSize.width * mashtab
                / 100, 50 + pageSize.height * mashtab / 100));
        sp.getViewport().setViewSize(workArea.getPreferredSize());

        sp.setSize(getSize().width, getSize().height - th + 1);

        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 600;
        c.weighty = 600;
        c.fill = GridBagConstraints.BOTH;
        reportPanel.add(sp, c);

        sp.getHorizontalScrollBar().setUnitIncrement(20);
        sp.getVerticalScrollBar().setUnitIncrement(20);

        workArea.addFocusListener(new FL());
        tb.addFocusListener(new FL());
        reportPanel.doLayout();
        reportPanel.validate();
    }

    public int getCountPages() {
        if (ds == null) {
            return 0;
        }
        if (numRows == 0) {
            return 1;
        }
        needDrawing = false;
        currentPage = 0;
        offset = getC1Height();
        processGroup(droot, root);
        int ret = currentPage + 1;
        currentPage = 0;
        offset = getC1Height();
        return ret;
    }

    public Dimension getPageSize() {
        return pageSize;
    }

    public void setPageSize(Dimension size) {
        pageSize = size;
    }

    public void print(int npage, int epage) {
        String rp = ZetaUtility.pr(ZetaProperties.PRINTING_REMOTE, "NO");
        if (rp.toUpperCase().equals("YES")) {
            String host = ZetaUtility.pr(ZetaProperties.PRINTING_HOST, "");
            String pname = ZetaUtility.pr(ZetaProperties.PRINTING_PRINTER_NAME, "DEFAULT");
            int port = 0;
            int bsize = 0;
            try {
                port = Integer
                        .parseInt(ZetaUtility.pr(ZetaProperties.PRINTING_PORT, "8001"));
                bsize = Integer.parseInt(ZetaUtility.pr(ZetaProperties.PRINTING_BUFFER_SIZE,
                        "50000"));
            }
            catch (Exception e) {
                log.error("Shit happens!!!", e);
            }
            try {
                pjob = new RPrintJob(host, port, pname, orientation, bsize);
            }
            catch (Exception e) {
                log.error("Shit happens!!!", e);
            }
            if (pjob == null) {
                log.debug("Print Server unavaible!");
                return;
            }
        }
        else {
            JobAttributes job_atr = new JobAttributes();
            job_atr.setCopies(tb.numCopies.getSelectedIndex() + 1);
            PageAttributes page_atr = new PageAttributes();
            page_atr.setOrigin(PageAttributes.OriginType.PRINTABLE);
            if (orientation.toUpperCase().equals("LANDSCAPE")) {
                page_atr
                        .setOrientationRequested(PageAttributes.OrientationRequestedType.LANDSCAPE);
            }
            else {
                page_atr
                        .setOrientationRequested(PageAttributes.OrientationRequestedType.PORTRAIT);
            }

            pjob = reportPanel.getToolkit().getPrintJob(new JFrame(), "Printing Test",
                    job_atr, page_atr);
        }
        if (pjob != null) {
            log.debug("page size = " + pjob.getPageDimension());
            log.debug("page resolution is " + pjob.getPageResolution());
        }
        int savem = mashtab;
        mashtab = 100; // печатаем всегда в масштабе 100%
        if (root != null) {
            root.createFonts(mashtab);
        }

        for (int j = npage; j <= epage; j++) {
            curGraphics = pjob.getGraphics();
            needDrawing = true;
            offset = getC1Height();
            currentPage = 0;
            printPage = j;
            isPrint = true;
            if (isDrawPage()) {
                drawColon(colon[0]); // рисует верхний колонтитул
                drawColon(colon[1]); // рисует нижний колонтитул
            }
            processGroup(droot, root);
            curGraphics.setClip(0, 0, pageSize.width * mashtab / 100,
                    pageSize.height * mashtab / 100);
            curGraphics.dispose();
        }
        mashtab = savem;
        pjob.end();
        isPrint = false;
    }

    public void init(Proper prop, Document doc) {
    	super.init(prop, doc);
        String sp = (String) prop.get("ORIENTATION");
        if (sp != null) {
            orientation = sp;
            if (orientation.toUpperCase().equals("LANDSCAPE")) {
                int t = pageSize.width;
                pageSize.width = pageSize.height;
                pageSize.height = t;
                workArea.setPreferredSize(new Dimension(50 + pageSize.width
                        * mashtab / 100, 50 + pageSize.height * mashtab / 100));
                this.sp.getViewport().setViewSize(workArea.getPreferredSize());
            }
        }
    }

    public void initChildren() {

    	REPORTHEADER rHeader = null;
        REPORTTRAILER rTrailer = null;
        ReportGrid rGrid = null;
        views.Group group = null;
        root = new views.Group();
        Proper p = new Proper();
        p.put("ALIAS", rootGroupAlias);
        root.init(p, document);
        document.registrate(root);
//        if (aliases != null) {
//            aliases.put(rootGroupAlias, root);
//            root.aliases = aliases;
//            root.alias = rootGroupAlias;
//        }

        RmlObject[] objs = container.getChildren();
        
        for (RmlObject child : objs) {
            if (child instanceof REPORTHEADER) {
                rHeader = (REPORTHEADER) child;
            }
            else if (child instanceof REPORTTRAILER) {
                rTrailer = (REPORTTRAILER) child;
            }
            else if (child instanceof COLONTITUL) {
                ReportForm col = ((COLONTITUL) child).getForm();
                if (col.getType().equals("TOP")) {
                    colon[0] = col;
                }
                else if (col.getType().equals("BOTTOM")) {
                    colon[1] = col;
                }
            }
            else if (child instanceof views.Group) {
                group = (views.Group)child;
            }
            else if (child instanceof ReportGrid) {
                rGrid = (ReportGrid) child;
            }
            else if (child instanceof core.rml.dbi.GroupReport
                    || child instanceof core.rml.dbi.Datastore) {
                ds = (core.rml.dbi.Datastore) child;
            }
        }
        if (rGrid != null && group != null) {
            rGrid = null;
        }
        log.debug("Header=" + rHeader);
        root.addChild(rHeader);
        root.addChild(group);
        root.addChild(rGrid);
        root.addChild(rTrailer);
        root.initChildren();
        log.debug("root.Header=" + root.rHeader);
        root.setDatastore(ds);
        root.setParent(this);
        root.createFonts(mashtab);
    }

    void drawColon(ReportForm f) {
        if (f == null) {
            return;
        }
        fillColon(f);
        if (f.getType().equals("TOP")) { // значит, это верхний колонтитул
            f.paint(curGraphics, mashtab);
        }
        else { // а это нижний
            curGraphics.translate(0, (pageSize.height - getC2Height())
                    * mashtab / 100);
            f.paint(curGraphics, mashtab);
            curGraphics.translate(0, -(pageSize.height - getC2Height())
                    * mashtab / 100);
        }
    }

    void fillColon(ReportForm f) {
        if (f == null) {
            return;
        }
        f.setDatastore(ds);
        f.fillFields2();
    }

    public synchronized void processGroup(core.rml.dbi.Group dgr, views.Group vgr) {

        if (dgr == null || vgr == null) {
            return;
        }
        core.rml.dbi.Group[] subgr = dgr.getSubgroups();
        processHT(vgr.rHeader, dgr);

        if (subgr == null) {
            processGrid(vgr.rGrid, dgr);
        }
        else {
            for (int i = 0; i < subgr.length; i++) {
                processGroup(subgr[i], vgr.group);
            }
        }

        processHT(vgr.rTrailer, dgr);

    }

    public void processHT(ReportForm f, core.rml.dbi.Group gr) {
        if (f == null) {
            return;
        }
        Rectangle r = f.getBounds();
        int hw = r.height + r.y; // высота header'а
        if (hw == 0) {
            return;
        }
        int free = getPageFreeSpace();
        if (hw > free) {
            incPage();
        }
        f.isPrint = isPrint;
        if (isDrawPage()) {
            curGraphics.translate(0, offset * mashtab / 100);
            curGraphics.setClip(0, 0, pageSize.width * mashtab / 100,
                    pageSize.height * mashtab / 100);
            f.currentGroup = gr;
            f.fillFields(); // заполн€ет String-значени€ми Field'ы
            // (Object-значени€ лежат либо в Datastore либо в
            // core.rml.dbi.Group)
            try {
                f.paint(curGraphics, mashtab);
            }
            catch (Exception e) {
                log.error("Shit happens!!!", e);
            }

            curGraphics.translate(0, -offset * mashtab / 100);
        }
        offset += hw;

    }

    public void processGrid(ReportGrid rg, core.rml.dbi.Group gr) {
        int delta = 0;
        if (rg == null || gr == null) {
            return;
        }
        int drawedRows = 0;
        if (drawedRows >= gr.endrow - gr.begrow + 1) {
            log.debug("alarm!!!");
        }
        offset += rg.getPosition().y;
        while (drawedRows < gr.endrow - gr.begrow + 1) {
            if (isDrawPage()) {
                rg.drawIt = true;
            }
            else {
                rg.drawIt = false;
            }
            rg.beginRow = gr.begrow + drawedRows;
            rg.endRow = gr.endrow;

            if (offset == 0) {
                delta = 1;
            }
            else {
                delta = 0;
            }
            offset += delta;

            rg.offset = offset;
            rg.setFreeHeight(getPageFreeSpace() - rg.getPosition().y - delta);
            int dr = rg.drawRows(curGraphics, mashtab);
            if (rg.endRow - rg.beginRow + 1 > dr) {
                incPage();
            }
            else {
                offset += dr * rg.sizeRow;
                if (rg.drawGrid != 0) {
                    offset++;
                }
            }
            drawedRows += dr;
        }
    }

    public int getC1Height() {
        Rectangle r = null;
        if (colon[0] == null) {
            return 0;
        }
        r = colon[0].getBounds();
        return r.y + r.height;
    }

    public int getC2Height() {
        Rectangle r = null;
        if (colon[1] == null) {
            return 0;
        }
        r = colon[1].getBounds();
        return r.y + r.height;
    }

    // ¬озвращает высоту страницы без учета места, занимаемого
    // колонтитулами
    int getPageFreeSpace() {
        if (pageSize == null) {
            return 0;
        }
        else {
            return pageSize.height - offset - getC2Height(); /* getC1Height() */
        }
    }

    void incPage() {
        offset = getC1Height();
        currentPage++;
        if (printPage == -1 && isPrint) {
            curGraphics.dispose();
            curGraphics = pjob.getGraphics();
            if (curGraphics == null) {
                log.debug("graphics context for next page is null!!!");
                return;
            }
        }
        if (isDrawPage()) {
            drawColon(colon[0]);
            drawColon(colon[1]);
        }
    }

    public void toDS() {
    }

    public void fromDS() {
    }

    public void update() {
    }

    public int retrieve() {

    	int res = 0;
    	
        try {
            long t1 = System.currentTimeMillis();
            if (ds != null) {
                try {
                	query = (String) document.calculateMacro(ds.getSql());
                }
                catch (Exception e) {
                    log.error("Shit happens!!!", e);
                    return res;
                }
                if (query.equals("")) {
                    return res;
                }
                
                res = ds.retrieve();
                numRows = ds.getRowCount();
                if (numRows <= 0) {
                    return res;
                }
                initDbiRoot();
                if (root != null) {
                    ReportGrid rg = root.getGrid();
                    if (rg != null) {
                        if (rg.columns != null) {
                            GridColumn[] columns = rg.columns;
                            for (int i = 0; i < columns.length; i++) {
                                if (columns[i].getTarget() != null) {
                                    columns[i].setType(ds.getType(columns[i]
                                            .getTarget()));
                                }
                            }
                            for (int j = 0; j < numRows; j++) {
                                ds.setCurrentRow(j);
                                if (rg.calcArray != null) {
                                    for (int i = 0; i < rg.calcArray.length; i++) {
                                        columns[rg.calcArray[i]].calc();
                                    }
                                }
                            }
                            ds.setCurrentRow(0);
                        }
                    }
                }
                createTree(droot, root);
                computeTree(0, droot, root);
                root.setCurPos(-1);
                if(reportPanel.getGraphics() != null)
                	reportPanel.paint(reportPanel.getGraphics());
            }
            else {
                throw new Error("views.Report have not DATASTORE!");
            }
            long t2 = System.currentTimeMillis();
            log.debug("******views.Report: retrieved " + ds.getRowCount()
                    + " rows");
            log.debug("******views.Report: retrieve time = " + (t2 - t1));

            setUpdateFlag(true);
            reportPanel.validate();
        }
        catch (Exception e) {
            log.error("Shit happens!!!", e);
        }
		return res;
    }

    public void initDbiRoot() {
        if (ds instanceof core.rml.dbi.GroupReport) {
            droot = ((core.rml.dbi.GroupReport) ds).getRoot();

        }
        else if (ds instanceof core.rml.dbi.Datastore) {
            droot = new core.rml.dbi.Group(0, ((core.rml.dbi.Datastore) ds).getRowCount() - 1);
        }
    }

    // данный метод используетс€ дл€ занесени€ в объекты core.rml.dbi.Group
    // computed field'ов дл€ объектов ReportHeader и ReportTrailer
    public void createTree(core.rml.dbi.Group dgr, views.Group vgr) throws RTException {
        if (dgr == null || vgr == null) {
            return;
        }
        core.rml.dbi.Group[] subgr = dgr.getSubgroups();
        createHT(vgr.rHeader, dgr);
        createHT(vgr.rTrailer, dgr);

        if (subgr != null) {
            for (int i = 0; i < subgr.length; i++) {
                createTree(subgr[i], vgr.group);
            }
        }
    }

    public void createHT(ReportForm f, core.rml.dbi.Group gr) throws RTException {
        if (f == null) {
            return;
        }
        Field[] fields = f.getFields();
        if (fields == null) {
            return;
        }
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getExp() != null) { // значит это computed field и его
                // надо занести в core.rml.dbi.Group
                gr.addField(fields[i].getAlias());
            }
            if (!fields[i].isComputed()) {
                fields[i].setValue(ds.getValue(fields[i].gettarget()));
            }
        }
    }

    // данный метод используетс€ дл€ вычислени€
    // computed field'ов дл€ объектов ReportHeader и ReportTrailer
    // созданных методом createTree
    public void computeTree(int j, core.rml.dbi.Group dgr, views.Group vgr) {
        if (dgr == null) {
            return;
        }
        core.rml.dbi.Group[] subgr = dgr.getSubgroups();
        if (subgr != null) {
            for (int i = 0; i < subgr.length; i++) {
                computeTree(i, subgr[i], vgr.group);
            }
        }
        computeHT(j, vgr.rHeader, vgr.rTrailer, dgr, vgr);

    }

    public void computeHT(int j, ReportForm header, ReportForm trailer,
            core.rml.dbi.Group dgr, views.Group vgr) {
        if (vgr == null) {
            return;
        }
        if (dgr == null) {
            return;
        }
        if (header == null && trailer == null) {
            return;
        }
        vgr.currentGroup = dgr;
        vgr.curpos++;
        if (header != null) {
            header.currentGroup = dgr;
        }
        if (trailer != null) {
            trailer.currentGroup = dgr;
        }
        if (vgr.seq == null) {
            return;
        }
        for (int i = 0; i < vgr.seq.size(); i++) {
            String alias = (String) vgr.seq.elementAt(i);
            if (alias == null) {
                continue;
            }
            Field f = null;
            if (header != null) {
                f = header.getField(alias);
            }
            if (trailer != null && f == null) {
                f = trailer.getField(alias);
            }
            if (f != null) {
                f.needSetString = false;
                f.calc();
            }
        }
    }

    void nextPageAction() {
        if (displayPage == numPages - 1) {
            return;
        }
        displayPage++;
        needCreatePage = true;
        wrepaint = true;
        setUpdateFlag(true);
        reportPanel.repaint();
    }

    void prevPageAction() {
        if (displayPage == 0) {
            return;
        }
        displayPage--;
        needCreatePage = true;
        wrepaint = true;
        setUpdateFlag(true);
        reportPanel.repaint();
    }

    void firstPageAction() {
        if (displayPage == 0) {
            return;
        }
        displayPage = 0;
        needCreatePage = true;
        wrepaint = true;
        setUpdateFlag(true);
        reportPanel.repaint();
    }

    void lastPageAction() {
        if (displayPage == numPages - 1) {
            return;
        }
        displayPage = numPages - 1;
        needCreatePage = true;
        wrepaint = true;
        setUpdateFlag(true);
        reportPanel.repaint();
    }

    void printAction() {
        if (tb.whatPrint.getSelectedIndex() == 0) {
            print(-1, -1); // распечатка всех страниц
        }
        else if (tb.whatPrint.getSelectedIndex() == 1) {
            print(displayPage, displayPage); // распечатка одной страницы
        }
        else if (tb.whatPrint.getSelectedIndex() == 2) {
            // распечатка диапазона страниц
            log.debug("print diapazon");
            try {
                int beg = Integer.parseInt(tb.fBegPages.getText());
                int end = Integer.parseInt(tb.fEndPages.getText());
                if (beg <= 0) {
                    beg = 1;
                }
                if (end <= 0) {
                    end = 0;
                }
                else if (end > numPages) {
                    end = numPages;
                }
                log.debug("beg = " + beg + " end = " + end + " numPages="
                        + numPages);
                print(beg - 1, end - 1);
            }
            catch (Exception e) {
                log.error("Shit happens!!!", e);
            }
        }
    }

    boolean isDrawPage() {
        if (isPrint) {
            return needDrawing && (currentPage == printPage || printPage == -1);
        }
        else {
            return needDrawing && (currentPage == displayPage);
        }
    }

    void setMashtab(int m) {
        log.debug("mashtab is " + m);
        if (mashtab == m) {
            return;
        }
        mashtab = m;
        if (root != null) {
            root.createFonts(mashtab); // ћасштабируем шрифты дл€ грида в нашем
            // отчете
        }
        image = null;
        workArea.setPreferredSize(new Dimension(50 + pageSize.width * mashtab
                / 100, 50 + pageSize.height * mashtab / 100));
        sp.getViewport().setViewSize(workArea.getPreferredSize());
        needCreatePage = true;
        wrepaint = true;
        setUpdateFlag(true);
        reportPanel.doLayout();
        reportPanel.repaint();
    }

    class MyToolbar extends JPanel implements ItemListener {
        Font coolFont = new Font("Monospaced", 1, 14);

//        class MyButton extends ZButtonImpl {
//
//            MyButton(String s) {
//                super(s);
//                this.setMargin(new Insets(0, 1, 1, 0));
//                addKeyListener(new KL());
//                setFocusable(false);
//            }
//        }

        ZButton nextPage   = ZButtonImpl.create(nextPageLabel);

        ZButton prevPage   = ZButtonImpl.create(prevPageLabel);

        ZButton firstPage  = ZButtonImpl.create(firstPageLabel);

        ZButton lastPage   = ZButtonImpl.create(lastPageLabel);

        ZButton print      = ZButtonImpl.create(printLabel);

        ZPanel   wp1        = ZPanelImpl.create();

        ZPanel   wp11       = ZPanelImpl.create();

        ZPanel   wp12       = ZPanelImpl.create();

        ZPanel   wp2        = ZPanelImpl.create();

        ZLabel   pages      = ZLabelImpl.create(StringBundle.Report_Label_Pages);

        ZLabel   iz         = ZLabelImpl.create(StringBundle.Report_Label_Iz);

        ZLabel   curPage    = ZLabelImpl.create("");

        ZLabel   numPages   = ZLabelImpl.create("");

        ZLabel   lnumCopies = ZLabelImpl.create(StringBundle.Report_Label_NumCopies);

//        class MyTextField extends JTextField {
//
//            MyTextField() {
//                addKeyListener(new KL());
//                setEnabled(false);
//            }
//        }

        ZLabel      lBegPages  = ZLabelImpl.create(StringBundle.Report_Label_BegPages);

        ZLabel      lEndPages  = ZLabelImpl.create(StringBundle.Report_Label_EndPages);

        ZTextField fBegPages  = ZTextFieldImpl.create();

        ZTextField fEndPages  = ZTextFieldImpl.create();

        ZComboBox   numCopies  = ZComboBoxImpl.create();

        ZComboBox   whatPrint  = ZComboBoxImpl.create();

        ZComboBox   mashtab    = ZComboBoxImpl.create();

        // JButton saveButton = new JButton();

        ZLabel      lwhatPrint = ZLabelImpl.create(StringBundle.Report_Label_WhatPrint);

        ZLabel      lmashtab   = ZLabelImpl.create(StringBundle.Report_Label_Mashtab);

        public MyToolbar() {
        	super();
        	      	
            setBackground(Color.lightGray);
            setFont(new Font("Dialog", 0, 12));
            setLayout(new GridLayout(2, 1));
            wp1.setLayout(new GridLayout(1, 1));
            wp2.setLayout(null);
            wp12.setLayout(new GridLayout(1, 6));
            wp11.setLayout(null);

            KeyListener keyListener = new KL();
            ActionListener actionLiatener = new AL(this);
            
            nextPage.addKeyListener(keyListener);
            nextPage.setFocusable(false);

            prevPage.addKeyListener(keyListener);
            prevPage.setFocusable(false);

            firstPage.addKeyListener(keyListener);
            firstPage.setFocusable(false);

            lastPage.addKeyListener(keyListener);
            lastPage.setFocusable(false);

            print.addKeyListener(keyListener);
            print.setFocusable(false);
            

            wp1.addFocusListener(new FL());
            wp11.addFocusListener(new FL());
            wp12.addFocusListener(new FL());
            wp2.addFocusListener(new FL());
            
            
            lmashtab.setBounds(10, 10, 50, 15);
            mashtab.setBounds(62, 7, 55, 20);

            mashtab.addItem("200%");
            mashtab.addItem("175%");
            mashtab.addItem("150%");
            mashtab.addItem("125%");
            mashtab.addItem("100%");
            mashtab.addItem("75%");
            mashtab.addItem("50%");
            mashtab.addItem("25%");
            mashtab.addItemListener(this);
            try {
//                String s = ZetaUtility.pr(ZetaUtility.REP_INIT_MASHTAB, "100");
                String s = PropertyManager.getIntance().getProperty(PropertyConstants.REPORT_ZOOM);
                int f = 4;
                for (int i = 0; i < mashtab.getItemCount(); i++) {
                    if (mashtab.getItemAt(i).equals(s + "%")) {
                        f = i;
                        break;
                    }
                }
                mashtab.setSelectedIndex(f);
                s = mashtab.getItemAt(f).toString();
                Report.this.mashtab = Integer.parseInt(s.substring(0, s
                        .length() - 1));
            }
            catch (Exception e) {
                log.error("Shit happens!!!", e);
                mashtab.setSelectedIndex(4);
                Report.this.mashtab = 100;
            }

            pages.setBounds(120, 10, 30, 20);
            curPage.setBounds(150, 10, 30, 20);
            iz.setBounds(180, 10, 25, 20);
            numPages.setBounds(205, 10, 30, 20);
            lnumCopies.setBounds(235, 10, 40, 20);
            numCopies.setBounds(275, 10, 50, 20);
            initNumCopies();
            lBegPages.setBounds(340, 10, 10, 15);
            fBegPages.setBounds(355, 10, 35, 20);
            lEndPages.setBounds(390, 10, 20, 15);
            fEndPages.setBounds(410, 10, 35, 20);

            lwhatPrint.setBounds(10, 10, 55, 20);
            whatPrint.setBounds(65, 10, 120, 20);

            whatPrint.addItem(StringBundle.Report_Label_AllPages);
            whatPrint.addItem(StringBundle.Report_Label_OnePage);
            whatPrint.addItem(StringBundle.Report_Label_DPages);
            whatPrint.addActionListener(new AL(this));
            wp11.add(lmashtab);
            wp11.add(mashtab);
            wp11.add(pages);
            wp11.add(curPage);
            wp11.add(iz);
            wp11.add(numPages);
            wp11.add(lnumCopies);
            wp11.add(numCopies);
            wp11.add(lBegPages);
            wp11.add(fBegPages);
            wp11.add(lEndPages);
            wp11.add(fEndPages);

            nextPage.setFont(coolFont);
            prevPage.setFont(coolFont);
            firstPage.setFont(coolFont);
            lastPage.setFont(coolFont);
            wp12.add(firstPage);
            firstPage.addActionListener(new AL(this));
            wp12.add(prevPage);
            prevPage.addActionListener(new AL(this));
            wp12.add(nextPage);
            nextPage.addActionListener(new AL(this));
            wp12.add(lastPage);
            lastPage.addActionListener(new AL(this));
            wp12.add(print);
            print.addActionListener(new AL(this));
            // wp12.add(saveButton);
            // saveButton.addActionListener(new AL(this));

            wp1.add(wp11);
            wp2.add(lwhatPrint);
            wp2.add(whatPrint);
            wp12.setLocation(200, 10);
            wp12.setSize(350, 22);
            wp2.add(wp12);

            add(wp1.getJComponent());
            add(wp2.getJComponent());
        }

        void initNumCopies() {
            for (int i = 1; i <= 10; i++) {
                numCopies.addItem(String.valueOf(i));
            }
            for (int i = 20; i <= 100; i += 10) {
                numCopies.addItem(String.valueOf(i));
            }
        }

        public void itemStateChanged(ItemEvent e) {
            if (e.getSource().equals(mashtab.getJComponent())) {
                int m = 100;
                try {
                    String str = (String) mashtab.getSelectedItem();
                    str = str.substring(0, str.length() - 1);
                    m = Integer.parseInt(str);
                }
                catch (Exception ex) {
                    log.error("Shit happens!!!", ex);
                }
                Report.this.setMashtab(m);
            }
        }
    }

    public Object method(String method, Object arg) throws Exception {
        if (method.equals("CURRENTPAGE")) {
            if (isPrint) {
                return new Double(currentPage + 1);
            }
            else {
                return new Double(displayPage + 1);
            }
        }
        else if (method.equals("TOTALPAGES")) {
            return new Double(numPages);
        }else
        	return super.method(method, arg);
    }

    public String type() {
        return "VIEWS_REPORT";
    }

    public void setValue(Object o) {
    }

    public void setValueByName(String name, Object o) {
    }

    public Object getValue() {
        return this;
    }

    public Object getValueByName(String name) {
        return null;
    }

    class WorkCanvas extends JComponent {
        boolean        clean = false;

        public boolean note  = false;

        public String  str1  = null;

        public String  str2  = null;

        public synchronized void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (note) {
                str1 = "";
                str2 = "";
                g.drawString(str1, 80, 150);
                g.drawString(str2, 80, 200);
            }
            note = false;
            if (ds != null && update_view) {
                String res = null;
                try {
                    res = (String) document.calculateMacro(ds.getSql());
                }
                catch (Exception e) {
                    log.error("Shit happens!!!", e);
                }
                
                if ((res == null) || res.equals("")) {
                    note = true;
                    str1 = "ѕроверьте правильность";
                    str2 = "задани€ параметров!";
                    g.setFont(new Font("Serif", 2, 40));
                    g.setColor(new Color(0, 100, 0));
                    g.drawString(str1, 80, 150);
                    g.drawString(str2, 80, 200);
                    return;
                }

                if (image == null) {
                    image = createImage(pageSize.width * mashtab / 100,
                            pageSize.height * mashtab / 100);
                }

                if (!query.equals(res)) {

                    log.debug("paint in clean mode called");
                    g.setColor(getBackground());
                    g.fillRect(0, 0, getSize().width, getSize().height);
                    retrieve();
                    if (ds.getRowCount() > 0) {
                        needCreatePage = true;
                        numPages = 0;
                        displayPage = 0;
                        wrepaint = true;
                    }
                }
            }

            if (ds.getRowCount() == 0) {
                note = true;
                str1 = "ƒанных не получено!";
                str2 = "";
                g.setFont(new Font("Serif", 2, 40));
                g.setColor(new Color(0, 100, 0));
                g.drawString(str1, 80, 150);
                g.drawString(str2, 80, 200);
                return;
            }
            if (numPages == 0) {
                numPages = getCountPages();
                tb.numPages.setText(String.valueOf(numPages));
            }

            if (needCreatePage) {
                needCreatePage = false;
                tb.curPage.setText(String.valueOf(displayPage + 1));

                curGraphics = image.getGraphics();
                curGraphics.setColor(getBackground());
                curGraphics.fillRect(0, 0, pageSize.width * mashtab / 100,
                        pageSize.height * mashtab / 100);

                needDrawing = true;
                currentPage = 0;
                offset = getC1Height();
                processGroup(droot, root);
                curGraphics.setClip(0, 0, pageSize.width * mashtab / 100,
                        pageSize.height * mashtab / 100);
                drawColon(colon[0]); // рисует верхний колонтитул
                drawColon(colon[1]); // рисует нижний колонтитул
                curGraphics.setColor(Color.blue);
                curGraphics.setClip(0, 0, pageSize.width * mashtab / 100,
                        pageSize.height * mashtab / 100);
                curGraphics.drawRect(0, 0, pageSize.width * mashtab / 100 - 1,
                        pageSize.height * mashtab / 100 - 1);
            }
            wrepaint = false;
            g.drawImage(Report.this.image, 10, 10, null);
            setUpdateFlag(false);
            log.debug("paint in draw mode called");
        }
    }

    class AL implements ActionListener {
        MyToolbar t = null;

        public AL(MyToolbar t) {
            this.t = t;
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(t.nextPage.getJComponent())) {
                Report.this.nextPageAction();
            }
            else if (e.getSource().equals(t.prevPage.getJComponent())) {
                Report.this.prevPageAction();
            }
            else if (e.getSource().equals(t.firstPage.getJComponent())) {
                Report.this.firstPageAction();
            }
            else if (e.getSource().equals(t.lastPage.getJComponent())) {
                Report.this.lastPageAction();
            }
            else if (e.getSource().equals(t.print.getJComponent())) {
                Report.this.printAction();
            }
            // else if (e.getSource().equals(t.saveButton))
            // {
            // exporterToOO = new OpenOfficeExport(Report.this);
            // new Thread(exporterToOO).start();
            // }
            else if (e.getSource().equals(t.whatPrint.getJComponent())) {
                if (t.whatPrint.getSelectedIndex() == 2) {
                    t.fBegPages.setEnabled(true);
                    t.fEndPages.setEnabled(true);
                }
                else {
                    t.fBegPages.setEnabled(false);
                    t.fEndPages.setEnabled(false);
                }
            }
        }
    }

    public void setUpdateFlag(boolean flag) {
        update_view = flag;
    }

	@Override
	public void focusThis() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ZComponent getVisualComponent() {
		return reportPanel;
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
