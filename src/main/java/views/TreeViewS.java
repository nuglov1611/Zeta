package views;

import action.api.RTException;
import core.document.AliasesKeys;
import core.document.Closeable;
import core.document.Document;
import core.document.Shortcutter;
import core.parser.Proper;
import core.rml.Container;
import core.rml.RmlObject;
import core.rml.VisualRmlObject;
import core.rml.dbi.DataTree;
import core.rml.dbi.Datastore;
import core.rml.dbi.Group;
import core.rml.dbi.GroupReport;
import core.rml.ui.impl.ZScrollPaneImpl;
import core.rml.ui.interfaces.ZComponent;
import core.rml.ui.interfaces.ZScrollPane;
import org.apache.log4j.Logger;
import publicapi.TreeViewAPI;
import views.focuser.FocusPosition;
import views.focuser.Focusable;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import static java.awt.event.KeyEvent.VK_CONTEXT_MENU;
import static java.awt.event.KeyEvent.VK_ENTER;

/**
 * Визуальный компонент "дерево". По сути визуальное представление GroupReport
 */
public class TreeViewS extends VisualRmlObject implements ActionListener,
        Focusable, Shortcutter, Closeable, TreeViewAPI {

    private ZScrollPane treePanel = ZScrollPaneImpl.create();
    Container container = new Container(this);

    class ML extends MouseAdapter {

        public void mouseClicked(MouseEvent e) {
            // TreeNodeS node = selectNode(e);
            selected_node = selectNode(e);
            switch (e.getButton()) {
                case MouseEvent.BUTTON1: {
                    if (e.getClickCount() == 2) {
                        if (selected_node != null) {
                            if (selected_node.getChildCount() == 0) {
                                listAction2();
                            } else {
                                nodeAction();
                            }
                        }
                    }
                    if (e.getClickCount() == 1) {
                        if (selected_node != null) {
                            if (selected_node.getChildCount() == 0) {
                                listAction();
                            }
                        }
                    }
                }
                break;
                case MouseEvent.BUTTON3: {
                    rightClickReaction(e);
                }
                break;
                default:
                    break;
            }
        }
    }

    class KL extends KeyAdapter {

        public void keyPressed(KeyEvent e) {
            selected_node = (TreeNodeS) tree.getLastSelectedPathComponent();
            if (document.executeShortcut(e)) {
                return;
            }
            switch (e.getKeyCode()) {
                case VK_ENTER:
                    if (selected_node != null) {
                        core.rml.dbi.Group g = selected_node.getDataObject();
                        if (g != null) {
                            setCurrentRow(g.begrow);
                        }
                        if (selected_node.getChildCount() == 0) {
                            listAction2();
                        } else {
                            nodeAction();
                        }
                        return;
                    }
                    break;
                case VK_CONTEXT_MENU:
                    rightClickReaction(null);
                    break;
            }
        }
    }


    private static final Logger log = Logger
            .getLogger(TreeViewS.class);

    private FocusPosition fp = new FocusPosition();

    private JTree tree;

    private int FontFamily = 0;

    private int FontSize = 12;

    private Color FontColor = null;

    private String FontName = null;

    TreeNodeS root;

    TreeNodeS selected_node;

    protected DataTree gr;

    String nodeAction;

    String listAction;

    String listAction2;

    String rootName;

    String ret;

    Color background = null;

    Color foreground = null;

    views.Menu menu = null;

    boolean expandAll;

    boolean sorted;

    boolean storePath = false;

    public static final int MENU_KEY = 525;

    Vector<Object> vPath = new Vector<Object>();             // path for current node

    public TreeViewS() {
        treePanel.setMinimumSize(new Dimension(0, 0));
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        try {
            document.doAction(command, null);
        } catch (Exception ex) {
            log.error("Shit happens", ex);
        }

    }

    public void initChildren() {
        RmlObject[] objs = container.getChildren();
        for (RmlObject element : objs) {
            if (element instanceof DataTree) {
                gr = (DataTree) element;
            } else if (element instanceof views.Menu) {
                menu = (views.Menu) element;
                menu.addActionListenerRecursiv(this);
            }
        }
    }

    public void closeNotify() {
        core.rml.dbi.Datastore ds2 = null;
        if (ret.equals("YES")) {
            ds2 = returnSelection();
        }
        if (ds2 != null) {
            document.getAliases().put(AliasesKeys.RETURNSTORE, ds2);
        }
    }

    void createTree() {
        root = new TreeNodeS(rootName);
        tree = new JTree(root);
        tree.setToolTipText(toolTipText);
        tree.setDragEnabled(true);
        setFont();
        tree.addMouseListener(new ML());
        tree.addKeyListener(new KL());

        if (background != null) {
            tree.setBackground(background);
        }
        if (foreground != null) {
            tree.setForeground(foreground);
        }

        core.rml.dbi.Group r = gr.getRoot();
        if (r != null) {
            root.setDataObject(r);
            recurse(r, root, new int[]{});
        }
        if (expandAll) {
            expandAll();
        } else {
            tree.expandPath(new TreePath(root.getPath()));
        }
        treePanel.getViewport().add(tree);
    }

    public void expandAll() {
        expandNode(root);
    }

    protected void expandNode(DefaultMutableTreeNode n) {
        if (n == null) {
            return;
        }

        tree.expandPath(new TreePath(n.getPath()));
        if (n.getChildCount() > 0) {
            expandNode((DefaultMutableTreeNode) n.getFirstChild());
        }
        expandNode(n.getNextSibling());
    }

    public void focusThis() {
    }

    public void fromDS() {
    }

    public void sortNodes(TreeNodeS[] ar) {
        int k, j, n = ar.length;
        if (n == 0) {
            return;
        }

        k = n;
        for (j = n / 2; j >= 1; j--) {
            pros(ar, j, k);
        }
        for (k = n - 1; k >= 1; k--) {
            swap(ar, 1 - 1, k + 1 - 1);
            pros(ar, 1, k);
        }

    } // public void sortNodes(TreeNode[] ar)

    private void swap(TreeNodeS[] ar, int i1, int i2) {
        TreeNodeS tmp = ar[i1];
        ar[i1] = ar[i2];
        ar[i2] = tmp;
    }

    private int sign(int x) {
        if (x == 0) {
            return 0;
        }
        return x < 0 ? -1 : 1;
    }

    private int compareRows(TreeNodeS[] ar, int i1, int i2) {
        return ar[i1].toString().compareTo(ar[i2].toString());
    }

    private void pros(TreeNodeS[] ar, int x, int k) {
        int y;
        while (true) {
            y = x + x;
            switch (sign(y - k) + 2) {
                case 1: {
                    if (compareRows(ar, y - 1, y + 1 - 1) < 0) {
                        y++;
                    }
                }
                case 2: {
                    if (compareRows(ar, x - 1, y - 1) >= 0) {
                        return;
                    }
                    swap(ar, x - 1, y - 1);
                    x = y;
                    break;
                }
                case 3: {
                    return;
                }
            }
        }
    } // private void pros(TreeNode[] ar, int x, int k)

    public Object getValue() {
        return this;
    }

    public Object getValueByName(String name) {
        return null;
    }

    public void init(Proper prop, Document doc) {
        super.init(prop, doc);
        String sp;
        listAction = (String) prop.get("LISTACTION");
        listAction2 = (String) prop.get("LISTACTION2");
        nodeAction = (String) prop.get("NODEACTION");
        rootName = (String) prop.get("ROOTNAME", "");
        ret = (String) prop.get("RETURN", "NO");
        sp = (String) prop.get("BACKGROUND");
        if (sp != null) {
            background = UTIL.getColor(sp);
        }
        sp = (String) prop.get("FOREGROUND");
        if (sp != null) {
            foreground = UTIL.getColor(sp);
        }
        expandAll = prop.get("EXPANDALL", "NO").equals("YES");
        sorted = prop.get("SORTED", "YES").equals("YES");
        if (prop.get("STOREPATH", "NO").equals("YES")) {
            storePath = true;
        }
        doc.addHandler(this);

        sp = (String) prop.get("SHORTCUT");
        if (sp != null) {
            try {
                String[] ar = UTIL.parseDep(sp);
                for (String element : ar) {
                    doc.addShortcut(element, this);
                }
            } catch (Exception e) {
                log.error("Shit happens", e);
            }
        }

        sp = (String) prop.get("FIRSTFOCUS");
        if (sp != null && sp.toUpperCase().equals("YES")) {
            treePanel.requestFocusInWindow();
        }

        sp = (String) prop.get("FONT_FAMILY");
        if (sp != null) {
            try {
                FontFamily = new Integer(sp);
            } catch (NumberFormatException e) {
                log.error("Bad Font Family!!!", e);
                FontFamily = 0;
            }
        }
        try {
            FontSize = (Integer) prop.get("FONT_SIZE", 12);
        } catch (Exception e) {
            log.error("Bad Font Size!!!", e);
            FontSize = 12;
        }
        sp = (String) prop.get("FONT_NAME");
        if (sp != null) {
            FontName = sp;
        }
        sp = (String) prop.get("FONT_COLOR");
        if (sp != null) {
            FontColor = loader.ZetaUtility.color(sp);
        }
    }

    public void listAction() {
        try {
            if (listAction != null) {
                document.doAction(listAction, null);
            }
        } catch (Exception e) {
            log.error("Shit happens", e);
        }
    }

    public void listAction2() {
        log.debug("listAction2 called !;listAction2=" + listAction2);
        try {
            if (listAction2 != null) {
                document.doAction(listAction2, null);
            }
        } catch (Exception e) {
            log.error("Shit happens", e);
        }
    }

    // Методы интерфейса class_method

    public Object method(String method, Object arg) throws Exception {
        // обработка вызова метода CurrentValue
        if (method.equals("GETLEVEL")) {
            if (tree == null) {
                return new Double(0);
            } else {
                return new Double(getLevel());
            }
        } else if (method.equals("SETSOURCE")) {
            if (!(arg instanceof DataTree)) {
                throw new RTException("CastException",
                        "wrong paramters of treeview.setSource <groupreport>");
            }
            setSource((GroupReport) arg);
            return null;
        } else if (method.equals("SETCURRENTNODE")) {
            if (!(arg instanceof Double)) {
                throw new RTException("CastException",
                        "wrong paramters of treeview.setCurrentNode <number>");
            }
            int n = ((Double) arg).intValue();
            setCurrentNode(n);
            return null;
        } else {
            return super.method(method, arg);
        }
    }

    /**
     * Задает текущий элемент
     *
     * @param n номер элемента если -1, то выбирается корневой элемент
     */
    public void setCurrentNode(int n) {
        TreeNodeS node = root;
        if (n == -1) {
            tree.setSelectionPath(new TreePath(root.getPath()));
        } else {
            while (node != null) {
                tree.expandPath(new TreePath(node.getPath()));
                node = (TreeNodeS) node.getFirstLeaf();
                if (node == null) {
                    break;
                }
                int max = -1;
                TreeNodeS maxNode = null;
                core.rml.dbi.Group grp = node.getDataObject();
                while (n < grp.begrow || n > grp.endrow) {
                    if (grp.begrow < n && grp.begrow > max) {
                        max = grp.begrow;
                        maxNode = node;
                    }
                    node = (TreeNodeS) node.getNextSibling();
                    if (node == null) {
                        break;
                    }
                    grp = node.getDataObject();
                }
                if (node == null && maxNode != null) {
                    node = maxNode;
                }
                if (grp.begrow == grp.endrow && grp.begrow == n) {
                    tree.setSelectionPath(new TreePath(node.getPath()));
                    break;
                }
            }
        }
    }

    /**
     * Задает источник данных для дерева
     *
     * @param dataTree - источник данных
     */
    public void setSource(GroupReport dataTree) {
        gr = dataTree;
        createTree();
    }

    /**
     * Возвращает кол-во уровней до данного элемента в дереве, расстояние от корня до элемента
     *
     * @return кол-во уровней до данного элемента
     */
    public int getLevel() {
        TreeNodeS _node = null;
        TreePath _path = tree.getSelectionPath();
        if (_path == null)
        // There is no selection. Default to the root node.
        {
            _node = root;
        } else {
            _node = (TreeNodeS) (_path.getLastPathComponent());
        }
        return _node.getLevel();
    }

    public void nodeAction() {
        log.debug("nodeAction called !");
        try {
            if (nodeAction != null) {
                document.doAction(nodeAction, null);
            }
        } catch (Exception e) {
            log.error("Shit happens", e);
        }

    }

    /*
     * public boolean imageUpdate(Image img,int flags,int x,int y,int w,int h){
     * boolean loading = (flags & (ALLBITS|ABORT)) == 0; if (!loading)
     * repaint(); return loading; }
     */

    public void processShortcut() {
        treePanel.requestFocus();
    }

    // отображает стр-ру GroupReport на TreeView2

    protected void recurse(core.rml.dbi.Group g, TreeNodeS node, int[] path) {
        core.rml.dbi.Group[] subgroups = g.getSubgroups();
        if (subgroups == null) {
            return;
        }

        int[] newpath = new int[path.length + 1];
        if (newpath.length > 1) {
            System.arraycopy(path, 0, newpath, 0, newpath.length - 1);
        }

        TreeNodeS[] ar = new TreeNodeS[subgroups.length];
        for (int i = 0; i < subgroups.length; i++) {
            newpath[newpath.length - 1] = i;
            Object o = gr.getGroupValue(newpath);
            ar[i] = new TreeNodeS(o == null ? "" : o.toString());
            ar[i].setDataObject(subgroups[i]);
            recurse(subgroups[i], ar[i], newpath);
        }

        if (sorted) {
            sortNodes(ar);
        }

        for (TreeNodeS element : ar) {
            node.add(element);
        }
    }

    public TreeNodeS restorePath(core.rml.dbi.GroupReport grep) {
        try {
            if (grep.isTree()) {
                int[] arr = new int[100];
                for (int i = 0; i < 100; i++) {
                    arr[i] = grep.getIdColumn();
                }
                return restorePath(arr);
            } else {
                return restorePath(grep.getGroupColumn());
            }
        } catch (Exception e) {
            log.error("Shit happens", e);
            return null;

        }

    }

    TreeNodeS restorePath(int[] column) {
        if (vPath.size() == 0) {
            return null;
        }
        TreeNodeS node = root;
        Group gr = node.getDataObject();
        TreeNodeS cnode = (TreeNodeS) node.getFirstChild();
        for (int i = vPath.size() - 1, k = column.length - 1; i >= 0; i--, k--) {
            core.rml.dbi.Group[] subs = gr.getSubgroups(); // д ЄвЁз_бЄЁ RЇа_¤_<Ё<Ё
            // ЄR<Ёз_бвўR
            if (subs == null || subs.length == 0) {
                break;
            }

            for (Group element : subs) {
                Group _g = cnode.getDataObject();

                if (_g.getReport().getValue(_g.begrow, column[k]).equals(
                        vPath.elementAt(i))) {
                    tree.expandPath(new TreePath(cnode.getPath()));
                    tree.setSelectionPath(new TreePath(cnode.getPath()));
                    node = cnode;
                    if (cnode.getChildCount() > 0) {
                        cnode = (TreeNodeS) cnode.getFirstChild();
                    } else {
                        break;
                    }
                    gr = _g;
                    break;
                } else {
                    node = cnode;
                }
                cnode = (TreeNodeS) cnode.getNextSibling();
            }
        }
        return node;
    }

    public int retrieve() {
        log.debug("TreeViewS retrieve called gr=" + gr);
        if (gr != null) {
            try {
                if (storePath) {
                    storePath((core.rml.dbi.GroupReport) gr);
                }
                ((core.rml.dbi.Datastore) gr).retrieve();
                createTree();
                if (storePath) {
                    TreeNodeS node = restorePath((core.rml.dbi.GroupReport) gr);
                    if (node != null) {
                        core.rml.dbi.Group g = node.getDataObject();
                        if (g != null) {
                            setCurrentRow(g.begrow);
                        }

                        if (node.getChildCount() == 0) {
                            listAction();
                        }
                        selected_node = node;
                    }

                }
            } catch (Exception e) {
                log.error("Shit happens", e);
            }
        }
        return 0;
    }

    private Datastore returnSelection() {
        if (gr == null || ((core.rml.dbi.Datastore) gr).getRowCount() == 0) {
            return null;
        }

        TreeNodeS node = (TreeNodeS) tree.getLastSelectedPathComponent();

        if (node == null) {
            return null;
        }

        Group g = node.getDataObject();
        if (g == null) {
            return null;
        }
        Vector<Integer> filteredIndexes = new Vector<Integer>();
        filteredIndexes.add(g.begrow);
        return ((Datastore) gr).getFilterManager().createFilter(((Datastore) gr), filteredIndexes);
    }

    private void rightClickReaction(MouseEvent e) {
        if (menu != null) {
            if (e != null) {
                // menu.show(e.getComponent(), e.getX(), e.getY());
                menu.show(treePanel.getJComponent(), e.getX(), e.getY());
            } else {
                menu.show(treePanel.getJComponent(), 0, 0);
            }
        }

    }

    private void setCurrentRow(int r) {
        if (gr != null) {
            ((core.rml.dbi.Datastore) gr).setCurrentRow(r);
        }
    }

    // Методы интерфейса GlobalValuesObject

    public void setValue(Object o) {
    }

    public void setValueByName(String name, Object o) {
    }

    private void storeCurrentPath(int[] column) {
        vPath.removeAllElements();
        // TreeNodeS node = new TreeNodeS(tree.getSelectionPath()
        // .getLastPathComponent());
        if (selected_node != null) {
            int level = selected_node.getLevel();
            Group gr = selected_node.getDataObject();
            int size = column.length;
            vPath.addElement(gr.getReport().getValue(gr.begrow,
                    column[size - level]));
            int i = 1;
            while ((selected_node = (TreeNodeS) selected_node.getPreviousNode()) != null) {
                gr = selected_node.getDataObject();
                vPath.addElement(gr.getReport().getValue(gr.begrow,
                        column[size - level + i++]));
            }
        }
    }

    public void storePath(GroupReport grep) {
        try {
            if (grep.isTree()) {
                log.debug("it's tree...");

                int[] arr = new int[100];
                for (int i = 0; i < 100; i++) {
                    arr[i] = grep.getIdColumn();
                }
                storeCurrentPath(arr);
            } else {
                storeCurrentPath(grep.getGroupColumn());
            }
        } catch (Exception e) {
            log.error("Shit happens", e);
        }

    }

    public void toDS() {
    }

    // Методы интерфейса class_type

    public String type() {
        return "VIEWS_TREEVIEW2";
    }

    public void update() {
    }

    private TreeNodeS selectNode(MouseEvent e) {
        JTree tmp_tree = (JTree) e.getSource();
        tmp_tree.setSelectionPath(tmp_tree.getClosestPathForLocation(e.getY(),
                e.getY()));
        TreeNodeS node = (TreeNodeS) tmp_tree.getLastSelectedPathComponent();
        if (node != null) {
            core.rml.dbi.Group g = node.getDataObject();
            if (g != null) {
                setCurrentRow(g.begrow);
            }
        }
        return node;
    }

    private void setFont() {
        if (tree != null) {
            tree.setFont(new Font(FontName, FontFamily, FontSize));
            tree.setRowHeight(FontSize + 2);
            if (FontColor != null) {
                tree.setForeground(FontColor);
            }
        }
    }

    public int getFocusPosition() {
        return fp.getFocusPosition();
    }

    public void setFocusPosition(int position) {
        fp.setFocusPosition(position);
    }

    public void addChild(RmlObject child) {
        container.addChildToCollection(child);
    }

    public RmlObject[] getChildren() {
        return container.getChildren();
    }

    @Override
    public void setFocusable(boolean focusable) {
        tree.setFocusable(focusable);
    }

    @Override
    public ZComponent getVisualComponent() {
        return treePanel;
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
