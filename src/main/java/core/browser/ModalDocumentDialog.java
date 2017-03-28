/**
 *
 */
package core.browser;

import core.document.Document;
import core.document.exception.LoadDocumentException;
import core.parser.Proper;
import loader.ZetaProperties;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

/*
 * @author uglov
 */
public class ModalDocumentDialog extends DocumentContainer implements Runnable {

    class WL extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            closeDocument();
        }

        @Override
        public void windowActivated(WindowEvent e) {
            if (getCurDoc() != null) {
                getCurDoc().activatedWindowRequestors(e);
            }
        }

    }

    private static final Logger log = Logger
            .getLogger(ModalDocumentDialog.class);

    private JDialog window = null;

    public JMenuBar createMenuBar(ArrayList<JMenu> documentMenu) {
        JMenuBar ret = new JMenuBar();
        if (documentMenu != null) {
            for (JMenu menu : documentMenu)
                ret.add(menu);
        }

        return ret;
    }

    public ModalDocumentDialog(Workspace ws, Window parentWindow) {
        super();

        window = new JDialog(ws.getFrame(), true);
        window.setModalityType(ModalityType.DOCUMENT_MODAL);

//        window.setGlassPane(lockPanel);
//        lockPanel.setVisible(false);
        setWorkSpace(ws);

        if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            window.setUndecorated(true);
            window.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
        }
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        window.addWindowListener(new WL());

        window.setIconImage(Toolkit.getDefaultToolkit().getImage(
                getClass().getClassLoader().getResource(ZetaProperties.IMAGE_ICON)));

        window.getContentPane().setLayout(new GridLayout(1, 1));
        window.getContentPane().add(this);

        validate();
    }

    public void setWindowSize(int w, int h) {
        window.setSize(w, h);
    }

    @Override
    public void closeDocumentWindow() {
        window.dispose();
    }

    @Override
    protected void setTitle(String title) {
        window.setTitle(title);
    }

    @Override
    protected void showDocumentInNewWindow(String doc_name, Object[] args)
            throws LoadDocumentException {
        ModalDocumentDialog dg = new ModalDocumentDialog(getWorkSpace(), window);

        Document dc = Document.getDocumentFromHash(doc_name, getCurDoc(), args,
                dg);
        if (dc == null) {
            dc = new Document(doc_name, args, getCurDoc(), dg);
        }

        int width, height;
        try {
            width = ((Integer) ((Proper) dc.getAliases().get("###propers###"))
                    .get("DOCUMENT_WIDTH")).intValue();
        } catch (Exception e) {
            width = 600;
            //log.error("Shit happens", e);
        }
        try {
            height = ((Integer) ((Proper) dc.getAliases().get("###propers###"))
                    .get("DOCUMENT_HEIGHT")).intValue();
        } catch (Exception e) {
            height = 400;
            //log.error("Shit happens", e);
        }
        dg.setWindowSize(width, height);
        dg.loadDocument(dc);
    }

    @Override
    protected void showDocumentWindow() {
        if (!window.isVisible()) {
            if (SwingUtilities.isEventDispatchThread())
                window.setVisible(true);
            else {
                try {
                    SwingUtilities.invokeAndWait(this);
                } catch (Exception e) {
                    log.error("!", e);
                    SwingUtilities.invokeLater(this);
                }
            }
        }
    }

    @Override
    public void run() {
        if (window != null && !window.isVisible()) {
            window.setVisible(true);
        }
    }

    @Override
    public void lockFrame() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                showInfo("ƒанна€ операци€ может зан€ть значительное врем€");
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//                lockPanel.setVisible(true);
            }
        });
    }

    @Override
    public void unlockFrame() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                clearInfo();
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//                lockPanel.setVisible(false);
            }
        });
    }

    @Override
    public void setMenuBar(JMenuBar menu) {
        window.setJMenuBar(menu);
    }


}
