package core.rml.dbi;

import java.sql.Types;
import java.util.StringTokenizer;
import java.util.Vector;

import loader.ZetaProperties;

import org.apache.log4j.Logger;

public class GroupReport extends Datastore implements DataTree {
    private static final Logger log = Logger.getLogger(GroupReport.class);

    int[][] groupCriteria;

    int[][] directions;

    int[] sort_columns;

    int[] dir_columns;

    String sortOrder;

    Group root;

    int levelColumn, parentColumn, idColumn,
            nameColumn;

    boolean tree = false;

    String treeParam;

    String[] groups;

    String[] direction;

    int row;

    boolean putLast = false;

    private int[] sortDirection;


    public int[] getGroupColumn() {
        int length = 0;
        if (groupCriteria != null) {
            for (int i = 0; i < groupCriteria.length; i++) {
                length += groupCriteria[i].length;
            }
        }
        int[] ret = new int[length];

        if (groupCriteria != null) {
            for (int i = 0; i < groupCriteria.length; i++) {
                for (int j = 0; j < groupCriteria[i].length; j++, length--) {
                    ret[length - 1] = groupCriteria[i][j];
                }
            }
        }
        return ret;

    }

    /**
     * вызывается из rml формат:"column1,column2;column3,column4" "1,1;1,0;"
     */
    public void setParameters(String grouping, String dir, String treeParam) {
        if (ZetaProperties.dstore_debug > 1) {
            log.debug("core.rml.dbi.GroupReport.setParameters :called " + treeParam);
        }
        if (treeParam != null) {
            this.treeParam = treeParam.toUpperCase();
        }
        if (grouping == null && dir == null) {
            // Обработка ситуации когда не заданы параметры группировк
        }
        else {
            StringTokenizer st1 = new StringTokenizer(grouping.toUpperCase(),
                    ";");
            StringTokenizer st2 = new StringTokenizer(dir, ";");
            int countTokens = st1.countTokens();
            groups = new String[countTokens];
            direction = new String[countTokens];
            for (int i = 0; i < countTokens; i++) {
                groups[i] = st1.nextToken();
                direction[i] = st2.nextToken();
            }
        }
    }

    public boolean isTree() {
        return tree;
    }

    public int getIdColumn() {
        return idColumn;
    }

    /**
     * Группировка задается в виде массива вида
     * {"column1,column2","column3,column4"...} {"0,1","1,1"...}-параметры
     * сортировк
     */
    public void setGrouping(String[] groups, String[] direction) {
        if (ZetaProperties.dstore_debug > 1) {
            log.debug("core.rml.dbi.GroupReport.setGrouping : called");
        }
        groupCriteria = new int[groups.length][];
        directions = new int[groups.length][];
        int i, k;
        int countToken;
        if (treeParam != null) {

            StringTokenizer st3 = new StringTokenizer(treeParam, ";");

            if (st3.countTokens() != 4) {
                if (ZetaProperties.dstore_debug > 1) {
                    log.debug("core.rml.dbi.GroupReport.setParameters : bad TreeParam");
                }
            }

            levelColumn = getColumn(st3.nextToken());

            parentColumn = getColumn(st3.nextToken());

            idColumn = getColumn(st3.nextToken());

            nameColumn = getColumn(st3.nextToken());
            tree = true;

        }

        for (i = 0; i < groups.length; i++) {
            StringTokenizer st = new StringTokenizer(groups[i], ",");
            countToken = st.countTokens();
            groupCriteria[i] = new int[countToken];
            for (k = 0; k < countToken; k++) {
                groupCriteria[i][k] = getColumn(st.nextToken());
            }
        }
        for (i = 0; i < groups.length; i++) {
            StringTokenizer st = new StringTokenizer(direction[i], ",");
            countToken = st.countTokens();
            directions[i] = new int[countToken];
            for (k = 0; k < countToken; k++) {
                directions[i][k] = new Integer(st.nextToken()).intValue();
            }
        }

    }

    public boolean eq(Object o1, Object o2, int type) {
        switch (type) {
            case Types.NUMERIC: {
                if (((Double) o1).doubleValue() == ((Double) o2).doubleValue()) {
                    return true;
                }
                else {
                    return false;
                }
            }
            case Types.CHAR:
            case Types.VARCHAR: {
                if (((String) o1).compareTo((String) o2) == 0) {
                    return true;
                }
                else {
                    return false;
                }
            }
            default: {
                log.debug("core.rml.dbi.GroupReport.eq: UNKNOWN TYPE FOR OPERATIONS!!!");
            }
        }
        return false;

    }

    public void resolveTree(int lev) {
        Vector<Group> st = new Vector<Group>();
        root = new Group(0, 0);
        st.addElement(root);
        int count = getRowCount();
        for (int i = 0; i < count; i++) {
            Group gr = new Group(i, i);
            gr.setReport(this);
            Object levelValue = getValue(i, levelColumn);
            int level = 0;
            if (levelValue != null) {
                level = ((Double) levelValue).intValue();
            }
            if (st.size() == level) {
                st.addElement(gr);
            }
            else {
                st.insertElementAt(gr, level);
            }
            st.elementAt(level - 1).addChild(gr);
        }

    }

    public Group[] resolveTree2(int level) {
        int new_level = level;
        int old_level = level;
        Group gr = null;
        // row = begrow;
        boolean flag = true;
        boolean put = false;
        Vector<Group> v = new Vector<Group>();
        try {
            while ((new_level != old_level - 1) && (row < getRowCount() - 1)
                    && flag) {
                gr = new Group(row, row);
                try {
                    new_level = ((Double) getValue(row + 1, levelColumn))
                            .intValue();
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    log.error("Shit happens", e);
                    gr = new Group(row + 1, row + 1);
                    v.addElement(gr);
                    throw e;

                }

                if (new_level == old_level + 1) {
                    row++;
                    gr.setSubgroups(resolveTree2(level + 1));
                    put = true;
                    try {
                        new_level = ((Double) getValue(row + 1, levelColumn))
                                .intValue();
                    }
                    catch (ArrayIndexOutOfBoundsException e) {
                        log.error("Shit happens", e);
                        throw new Exception("@@@@@@@@");
                    }
                    if (new_level < old_level) {
                        v.addElement(gr);
                        log.debug("throw Exception!");
                        throw new Exception("&&&&&&");
                    }

                }

                if (new_level == old_level || put) {
                    v.addElement(gr);
                    row++;
                    put = false;
                }
                else {
                    flag = false;
                    v.addElement(gr);
                    put = false;
                }

            }
        }
        catch (Exception e) {
            log.error("Shit happens", e);
        }
        if (row == getRowCount() - 1) {
            int size = v.size();
            if (v.elementAt(size - 1).begrow != gr.begrow) {
                v.addElement(gr);
            }
            else {
                v.addElement(new Group(row, row));
            }
            putLast = true;
        }
        Group[] grps = new Group[v.size()];
        v.copyInto(grps);
        return grps;
    }

    public Group[] resolveOneGroup(int level, Group group) {
        // строки ОТСОРТИРОВАНЫ
        if (level == groupCriteria.length) {
            return null;
        }
        int[] columns = new int[groupCriteria[level].length];
        columns = groupCriteria[level];
        Object[] n = new Object[groupCriteria[level].length];
        int begrow = group.begrow;
        int endrow = group.endrow;
        Vector<Group> v = new Vector<Group>();
        int beg = begrow;
        int end = endrow;
        int i;

        for (i = 0; i < columns.length; i++) {
            n[i] = getValue(begrow, columns[i]);
        }
        for (i = begrow + 1; i <= endrow; i++) {
            for (int k = 0; k < columns.length; k++) {
                if (!eq(n[k], getValue(i, columns[k]), getType(columns[k]))) {
                    end = i - 1;
                    Group gr = new Group(beg, end);
                    gr.setReport(this);
                    gr.setSubgroups(resolveOneGroup(level + 1, gr));
                    v.addElement(gr);
                    beg = i;
                    n[k] = getValue(i, columns[k]);
                }
                else {
                    continue;
                }
            }
        }
        Group gr = new Group(beg, endrow);
        gr.setReport(this);
        gr.setSubgroups(resolveOneGroup(level + 1, gr));
        v.addElement(gr);
        // if (v.size() == 0) v.addElement(new Group(begrow,endrow));
        Group[] sub = new Group[v.size()];
        v.copyInto(sub);
        return sub;
    }

    public void resolveAllGroups() {
        // СНАЧАЛА СОРТИРОВКА!!!
        if (ZetaProperties.dstore_debug > 0) {
            log.debug("core.rml.dbi.GroupReport.resolveAllGroups  calling");
        }
        if (groups != null && direction != null) {
            setGrouping(groups, direction);
            Vector<Integer> v = new Vector<Integer>();
            Vector<Integer> v1 = new Vector<Integer>();
            int i, k;
            for (i = 0; i < groupCriteria.length; i++) {
                for (k = 0; k < groupCriteria[i].length; k++) {
                    v.addElement(groupCriteria[i][k]);
                    v1.addElement(directions[i][k]);
                }
            }
            //TODO проверить сортировку без кода
            int[] sort = new int[v.size()];
            int[] dim = new int[v1.size()];
            for (i = 0; i < v.size(); i++) {
                sort[i] = v.elementAt(i).intValue();
                dim[i] = v1.elementAt(i).intValue();
            }
            if (ZetaProperties.dstore_debug > 1) {
                log.debug("core.rml.dbi.GroupReport.setSort  keys=" + model.getDataIndexes() + " skeys="
                        + model.getRowIndexes());
            }
            if (!tree) {

                setSortOrder_real(sortOrder);
                if ((sort_columns != null) && (dir_columns != null)) {
                    int[] c = new int[sort.length + sort_columns.length];
                    int[] d = new int[sort.length + sort_columns.length];
                    System.arraycopy(sort, 0, c, 0, sort.length);
                    System.arraycopy(sort_columns, 0, c, sort.length,
                            sort_columns.length);

                    System.arraycopy(dim, 0, d, 0, dim.length);
                    System.arraycopy(dir_columns, 0, d, dim.length,
                            dir_columns.length);
                    setSort(c, d);
                }
                else {
                    setSort(sort, dim);
                }
            }

            try {
                if (ZetaProperties.dstore_debug > 1) {
                    log.debug("core.rml.dbi.GroupReport.setSort  calling");
                }

                if (tree) {
                    row = 0;
                    resolveTree(1);
                }
                else {
                    root = new Group(0, getRowCount() - 1);
                    root.setReport(this);
                    root.setSubgroups(resolveOneGroup(0, root));
                }
                if (ZetaProperties.dstore_debug > 1) {
                    log
                            .debug("core.rml.dbi.GroupReport.resolveAllGroups:: all groups maked!!! ");
                }
            }
            catch (Exception e) {
                log.error("Shit happens", e);
            }
        }
        else {
            root = new Group(0, getRowCount() - 1);
            Group[] grs = new Group[this.getRowCount()];
            for (int l = 0; l < this.getRowCount(); l++) {
                grs[l] = new Group(l, l);
            }
            root.setSubgroups(grs);
        }
    }

    @Override
    public int retrieve() throws Exception {
        int res = super.retrieve();
        resolveAllGroups();
        putLast = false;
        if (ZetaProperties.tree_groups_debug > 0) {
            printAllGroups();
        }
        return res;
    }

    /**
     * rowid представляет из себя сложную систему навигации по группа ПРИМЕР-
     * массив вида {1,0,0} щзначает дай нулевую строку нулевой подгруппы первой
     * группы
     */
    public Object getGroupValue(int[] rowid, int column) {
        Group[] sub = root.getSubgroups();
        for (int i = 0; i < rowid.length - 1; i++) {
            sub = sub[rowid[i]].getSubgroups();
        }
        return getValue(sub[rowid[rowid.length - 2]].begrow
                + rowid[rowid.length - 1], column);
    }

    public Object getGroupValue(int[] rowid) {
        Group[] sub = root.getSubgroups();

        for (int i = 0; i < rowid.length - 1; i++) {
            sub = sub[rowid[i]].getSubgroups();
        }
        if (!tree) {
            return getValue(sub[rowid[rowid.length - 1]].begrow,
                    groupCriteria[rowid.length - 1][0]);
        }
        else {
            return getValue(sub[rowid[rowid.length - 1]].begrow, nameColumn);
        }
        // return
        // getValue(sub[rowid[rowid.length-2]].begrow+rowid[rowid.length-1
        // ],column);
    }

    public int getGroupDimension(int[] gr_id) {
        Group[] sub = root.getSubgroups();
        for (int i = 0; i < gr_id.length; i++) {
            sub = sub[gr_id[i]].getSubgroups();
        }
        return sub.length;
    }

    public Group getRoot() {
        return root;
    }

    public void printAllGroups() {
        printGroups(0, root);
    }

    public String getNode(String n_id) {
        log.debug("We called :" + n_id);
        StringTokenizer st = new StringTokenizer(n_id, "/");
        int[] rowid = new int[st.countTokens()];
        int count = st.countTokens();
        for (int i = 0; i < count; i++) {
            try {
                String s = st.nextToken();
                int pos = s.indexOf('#');
                if (pos != -1) {
                    s = s.substring(0, pos);
                }
                rowid[i] = Integer.parseInt(s);
            }
            catch (Exception e) {
                log.error("Shit happens", e);
            }
        }
        try {
            Group[] sub = root.getSubgroups();
            for (int i = 1; i < rowid.length; i++) {
                sub = sub[rowid[i]].getSubgroups();
            }
            String ret = "";
            for (int i = 0; i < sub.length; i++) {
                if (sub[i].getSubgroups() != null) {
                    if (!tree) {
                        ret = ret
                                + "N/"
                                + getValue(sub[i].begrow,
                                groupCriteria[rowid.length - 1][0])
                                + "/" + i + "#" + sub[i].begrow + "\n";
                    }
                    else {
                        ret = ret + "N/" + getValue(sub[i].begrow, nameColumn)
                                + "/" + i + "#" + sub[i].begrow + "\n";
                    }
                }
                else {
                    if (!tree) {
                        ret = ret
                                + "P/"
                                + getValue(sub[i].begrow,
                                groupCriteria[rowid.length - 1][0])
                                + "/" + sub[i].begrow + "\n";
                    }
                    else {
                        ret = ret + "P/" + getValue(sub[i].begrow, nameColumn)
                                + "/" + sub[i].begrow + "\n";
                    }
                }
            }
            return ret;
        }
        catch (Exception e) {
            log.error("Shit happens", e);
            return "P/" + "null" + "/" + "-1";
        }
    }

    public void printGroups(int level, Group gr) {
        if (getRowCount() == 0) {
            return;
        }
        Group[] grps;
        String s = new String("");
        grps = gr.getSubgroups();
        for (int l = 0; l < level; l++) {
            s = s + " ";
        }
        if (grps == null) {
            for (int i = gr.begrow; i <= gr.endrow; i++) {
                for (int j = 0; j < getCountColumns(); j++) {
                    log.info(s + " |" + getValue(i, j));
                }
            }
            log.info("---------------------------------------------" + level);
        }
        else {
            for (int i = gr.begrow; i <= gr.endrow; i++) {
                for (int j = 0; j < getCountColumns(); j++) {
                    log.info(s + " |" + getValue(i, j));
                }
            }
            log.info("---------------------------------------------" + level);
            for (int k = 0; k < grps.length; k++) {
                printGroups(level + 1, grps[k]);
            }
        }
    }

    @Override
    public void setSortOrder(String SortOrder) {
        this.sortOrder = SortOrder;
    }

    public void setSortOrder_real(String SortOrder) {
        if (SortOrder != null) {
            if (model.getColumnNames().isEmpty()) {
                tables = sqlManager.extractTableAndColumnNames(model);
            }
            StringTokenizer st = new StringTokenizer(SortOrder, ";");
            int count = st.countTokens();
            int[] sort_fields = new int[count];
            int[] sort_dim = new int[count];
            for (int i = 0; i < count; i++) {
                String param = st.nextToken();
                StringTokenizer st1 = new StringTokenizer(param);
                sort_fields[i] = getColumn(st1.nextToken().toUpperCase());
                sort_dim[i] = (new Integer(st1.nextToken())).intValue();
            }
            this.sort_columns = sort_fields;
            this.dir_columns = sort_dim;
        }
    }

    /**
     *Устанавливает и выполняет сортировк
     */
    public void setSort() {
        VMatrix vm = new VMatrix(this, sortcolumn, sortDirection);
        Sorter s = new Sorter(vm);
        model.setRowIndexes(s.getSortedArray());
    }

    public void setSort(int[] column, int[] direction) {
        this.sortcolumn = column;
        this.sortDirection = direction;
        VMatrix vm = new VMatrix(this, column, direction);
        Sorter s = new Sorter(vm);
        model.setRowIndexes(s.getSortedArray());
    }

    /**
     *Сбрасывает сортировк
     */
    public void resetSort() {
//        System.arraycopy(keys, 0, skeys, 0, keys.length);
    }
}
