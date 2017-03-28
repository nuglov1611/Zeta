package core.browser;

import core.connection.BadPasswordException;
import core.connection.ConnectException;
import core.connection.DBMSConnection;
import core.document.Document;
import core.document.exception.LoadDocumentException;
import core.rml.ui.impl.ZLabelImpl;
import core.rml.ui.impl.ZPanelImpl;
import core.rml.ui.impl.ZProgressBarImpl;
import core.rml.ui.interfaces.ZLabel;
import core.rml.ui.interfaces.ZPanel;
import core.rml.ui.interfaces.ZProgressBar;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.BusyPainter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.util.ArrayList;

public abstract class DocumentContainer extends JPanel {

    private static final Logger log = Logger.getLogger(DocumentContainer.class);

    private ZPanel docPanel;

    private ZLabel info;

    private ZProgressBar progressView;

    private ZPanel statusPanel;

    private Document curDoc = null;

    private Workspace workSpace = null;

    private Document firstDoc = null;

    protected JXPanel lockPanel = new JXPanel(false);
    protected JXBusyLabel busyLabel = null;

    /**
     * Creates new form DocumentContainer
     */
    public DocumentContainer() {
        super();
        initComponents();
    }

    private void initLockPanel() {
        lockPanel.setAlpha(.5f);
//        final GridBagLayout layout = new GridBagLayout();
//        final GridBagConstraints c = new GridBagConstraints();
//        lockPanel.setLayout(layout);
//        
//        c.fill = GridBagConstraints.NONE;
//        c.gridx = 1;
//        c.gridy = 1;
//        c.gridheight = 1;
//        c.gridwidth = 1;
//        c.anchor = GridBagConstraints.CENTER;
//        busyLabel = new JXBusyLabel(new Dimension(30, 30));
//        busyLabel.getBusyPainter().setHighlightColor(new Color(44, 61, 146).darker());
//        busyLabel.getBusyPainter().setBaseColor(new Color(168, 204, 241).brighter());
//        busyLabel.getBusyPainter().setPoints(9);
//        busyLabel.setBusy(true);
//        busyLabel.setDelay(100);


//        layout.setConstraints(busyLabel, c);
//        lockPanel.add(busyLabel);
        BusyPainter painter = new BusyPainter(
                new Rectangle2D.Float(0, 0, 13.500001f, 1),
                new RoundRectangle2D.Float(12.5f, 12.5f, 59.0f, 59.0f, 10, 10));
        painter.setTrailLength(5);
        painter.setPoints(31);
        painter.setFrame(1);
        lockPanel.setBackgroundPainter(painter);


        lockPanel.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {

            }

            public void mousePressed(MouseEvent e) {

            }

            public void mouseReleased(MouseEvent e) {

            }

            public void mouseEntered(MouseEvent e) {

            }

            public void mouseExited(MouseEvent e) {

            }
        });
        lockPanel.addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent e) {
            }

            public void keyReleased(KeyEvent e) {
            }

            public void keyTyped(KeyEvent e) {
            }
        });
        lockPanel.setFocusCycleRoot(true);
    }

    private void initComponents() {

        initLockPanel();

        setLayout(new BorderLayout());
        docPanel = ZPanelImpl.create();
        statusPanel = ZPanelImpl.create();
        info = ZLabelImpl.create();
        progressView = ZProgressBarImpl.create();

        docPanel.setLayout(new java.awt.GridLayout(1, 1));
        add(docPanel.getJComponent());

        statusPanel.setBorder(javax.swing.BorderFactory
                .createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        statusPanel.setMinimumSize(new java.awt.Dimension(20, 100));
        statusPanel.setLayout(new java.awt.GridLayout(1, 2));

        info.setMinimumSize(new java.awt.Dimension(10, 10));
        info.setPreferredSize(new java.awt.Dimension(10, 10));
        statusPanel.add(info);

        progressView.setBorder(null);
        statusPanel.add(progressView);

        add(statusPanel.getJComponent(), java.awt.BorderLayout.SOUTH);
    }

    protected abstract void setTitle(String title);

    protected abstract void showDocumentInNewWindow(String doc_name,
                                                    Object[] args) throws LoadDocumentException;

    protected abstract void showDocumentWindow();

    protected abstract void closeDocumentWindow();

    protected void setWorkSpace(Workspace ws) {
        workSpace = ws;
    }

    public Workspace getWorkSpace() {
        return workSpace;
    }

    public Connection getConnection() throws ConnectException,
            BadPasswordException {
        return DBMSConnection.getConnection(workSpace);
    }

    public void loadDocument(String doc_name, Object[] args, boolean new_window)
            throws LoadDocumentException {
        if (doc_name.startsWith("/"))
            doc_name = doc_name.substring(1);

        if (new_window) {
            showDocumentInNewWindow(doc_name, args);
        } else {
            Document dc = null;
            dc = Document.getDocumentFromHash(doc_name, curDoc, args, this);
            if (dc == null) {
                dc = new Document(doc_name, args, curDoc, this);
            }

            loadDocument(dc);
        }
    }

    protected void loadDocument(Document doc) {
        curDoc = doc;

        if (firstDoc == null) {
            firstDoc = doc;
        }
        docPanel.removeAll();
        docPanel.add(curDoc.getPanel());

        setTitle(curDoc.getTitle());

        //doc.initDocumentMenu();
        doc.setDocumentMenu();

        docPanel.revalidate();
        docPanel.repaint();
        showDocumentWindow();
        log.debug("Document " + doc.mypath + "/" + doc.myname + " called!");
        activateFocus();

    }

    public void closeDocument() {
        if (!curDoc.close())
            return;
        if (curDoc.getParentDocument() != null && curDoc != firstDoc) {
            loadDocument(curDoc.getParentDocument());
        } else {
            closeDocumentWindow();
        }
    }

    public Document getCurDoc() {
        return curDoc;
    }

    public abstract void lockFrame();

    public abstract void unlockFrame();

    synchronized public void clearInfo() {
        info.setText("");
        info.setVisible(false);
    }

    synchronized public void showInfo(String text) {
        info.setText(text);
        info.setVisible(true);
    }

    synchronized public void showProgress(int p) {
        progressView.setValue(p);
        info.setVisible(true);
    }

    private void activateFocus() {
        if (requestFocusInWindow()) {
            transferFocus();
        }
    }


    public abstract void setMenuBar(JMenuBar menu);

    public abstract JMenuBar createMenuBar(ArrayList<JMenu> documentMenu);
}
