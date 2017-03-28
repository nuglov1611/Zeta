package core.rml.dbi;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import loader.ZetaProperties;
import loader.ZetaUtility;

import org.apache.log4j.Logger;

import publicapi.DatastoreAPI;
import action.api.RTException;
import action.api.ScriptApi;
import action.calc.OP;
import action.calc.Quoted;
import core.connection.BadPasswordException;
import core.connection.ConnectException;
import core.document.Document;
import core.parser.Proper;
import core.rml.Container;
import core.rml.RmlObject;
import core.rml.dbi.exception.UpdateException;


/**
 * Объект для работы с БД
 */

public class Datastore extends RmlObject implements DatastoreAPI {
    private static final Logger log = Logger
            .getLogger(Datastore.class);

    /**
     * @internal
     */
    private Container container = new Container(this); 
    
    /**
     * @internal
     */
    public static final String compute = "@@COMPUTE_";

    /**
     * @internal
     */
    Vector<Compute> computeColumn = new Vector<Compute>();;

    /**
     * @internal
     * признак обновляемости данного обьекта
     */
    boolean readOnly = true;

    /**
     * @internal
     * исходный обьект
     */
    Datastore parent = null;

    /**
     * @internal
     * текущая строка
     */
    int currentRow;


    /**
     * @internal
     * массивы, определяющий сортировку поле
     */

    int[] sortcolumn = null;

    /**
     * @internal
     */
    int[] direction = null;

    /**
     * @internal
     * таблицы, к которым относятся столбцы
     */
    Hashtable<String, Vector<String>> tables = new Hashtable<String, Vector<String>>();

    /**
     * @internal
     * индексы полей, которые можно изменят
     */
    int[] updColumns = null;

    /**
     * @internal
     * Массив описании actions
     */
    String[] actions;

    /**
     * @internal
     * связи с родительскими обьекта
     */
    Hashtable<Integer, Integer> links;

    /**
     * @internal
     */
    String strLinks;

    /**
     * @internal
     */
    boolean head = false;

    /**
     * @internal
     */
    String[] defaults;

    /**
     * @internal
     */
    String[][] deps = null;

    /**
     * @internal
     */
    Datastore[] subStores;

//    /**
//     * @internal
//     * алиас данного обьекта
//     */
//
//    String alias;

    /**
     * @internal
     */
    Packer packer;

    /**
     * @internal
     */
    Handler handler = null;

    /**
     * @internal
     */
    Object[] defrow;

    /**
     * @internal
     */
    String selAction = null;

    /**
     * @internal
     */
    protected SqlManager sqlManager;

    /**
     * @internal
     */
    protected DatastoreModel model;

    /**
     * @internal
     */
    private FilterManager filterManager;

    /**
     * @internal
     */
    private boolean filtered = false;

    /**
     * @internal
     * конструкто
     * 
     */
    public Datastore(Datastore parent) {
    	this();
        if (ZetaProperties.dstore_debug > 1) {
            log.debug("begin constructor....");
        }

        this.parent = parent;
        this.currentRow = 0;
        sqlManager.setSql(parent.getSql());
        this.model = parent.getModel();
        this.tables = parent.getTables();
        if (ZetaProperties.dstore_debug > 1) {
            log.debug("end constructor....");
        }
    }
    
    /**
     * @internal
     */
    public Datastore(Document doc) {
    	this();
    	document = doc;
    }

    	
    /**
     * @internal
     */
    public Datastore() {
        sqlManager = new SqlManager();
        model = new DatastoreModel();
        filterManager = new FilterManager();
    }

    
    public void init(Proper prop, Document doc){
        super.init(prop, doc);
        
        String editable = (String) prop.get("EDITABLE");
        if ((editable != null)) {
            setReadOnly(editable.equalsIgnoreCase("no"));
        }

        String str = (String) prop.get("QUERY");
        if (str != null) {
            setSql(str);
        }

        str = (String) prop.get("UNIQUE");
        if (str != null) {
            setUnique(str);
        }

        str = (String) prop.get("UPDATEABLE");
        if (str != null) {
            setUpdateable(str);
        }

        str = (String) prop.get("LINKS");
        if (str != null) {
            setLinks(str);
        }

        str = (String) prop.get("DEFAULT");
        if (str != null) {
            setDefaults(str);
        }

        String head = (String) prop.get("HEAD");
        if (head == null || head.compareTo("NO") == 0) {
            setHead(false);
        }
        else {
            setHead(true);
        }

        str = (String) prop.get("ACTIONS");
        if (str != null) {
            initActions(str);
        }
        if (ZetaProperties.dstore_debug > 2) {
            log.debug("rml.DATASTORE.doParsing Actions init");
        }

        
        str = (String) prop.get("DEFROW");
        if (str != null) {
            setDefRow(str);
        }
        str = (String) prop.get("SELACTION");
        if (str != null) {
            setSelAction(str);
        }

        str = (String) prop.get("SORTORDER");
        if (str != null) {
            setSortOrder(str);
        }
    }
    
    /**
     * @internal
     */
    public void addPacker(Packer p) {
        this.packer = p;
    }

    /**
     * @internal
     */
    public void initActions(String act) {
        if (act == null) {
            return;
        }
        StringTokenizer st = new StringTokenizer(act, ";");
        int countToken = st.countTokens();
        actions = new String[countToken];
        for (int i = 0; i < countToken; i++) {
            actions[i] = st.nextToken().trim();
        }
    }

    /**
     * @internal
     */
    public void doAction(int id) {
        try {
            sqlManager.executeQuery(document.getConnection(), (String) document.calculateMacro(actions[id]));
        }
        catch (SQLException e) {
            log.error("DBMS Error!", e);
        }
        catch (Exception e) {
            log.error("Calc error!", e);
        }
    }

    /**
     * @internal
     */
    public void addSubStores(Object[] objs) {
        if (objs == null) {
            return;
        }
        Vector<Object> v = new Vector<Object>();

        for (int i = 0; i < objs.length; i++) {
            if (objs[i] instanceof Datastore) {
                v.addElement(objs[i]);
            }
        }
        subStores = new Datastore[v.size()];
        v.copyInto(subStores);
    }

    /**
     * @internal
     */
    public void setSortOrder(String SortOrder) {
        try {
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
            this.sortcolumn = sort_fields;
            this.direction = sort_dim;
        }
        catch (Exception e) {
            log.error("Shit happens", e);
        }
    }

    /**
     * @internal
     */
    public void notify(String alias, int op_id, int row) {
        if (handler != null) {
            handler.notifyHandler(row);
        }

    }

    /**
     * @internal
     */
    public void setDefRow(String dr) {
        StringTokenizer st = new StringTokenizer(dr, ",");
        int countToken = st.countTokens();
        defrow = new Object[countToken];
        for (int i = 0; i < countToken; i++) {
            if (model.getColumnType(i) == Types.NUMERIC) {
                defrow[i] = new Double(st.nextToken());
            } else {
                defrow[i] = st.nextToken();
            }
        }
    }

    /**
     * @internal
     */
    public void notifyChildren(int op_id, int row) {
        if (deps == null) {
            return;
        }
        if (deps[op_id] == null) {
            return;
        }
        for (int i = 0; i < deps[op_id].length; i++) {
            Datastore child = (Datastore) document.findObject(deps[op_id][i]);
            child.notify(alias, op_id, row);
        }
    }

    /**
     * @internal
     */
    public void addHandler(Handler hd) {
        this.handler = hd;
    }

    /**
     * @internal
     */
    public void removeHandler() {
        this.handler = null;
    }

    /**
     * @internal
     */
    public void setSelAction(String str) {
        this.selAction = str;
    }

    /**
     * устанавливает,доступен ли обьект только для чтения
     * @param readOnly - только чтение, если true 
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * filed = ~alias~,
     * @internal
     */
    public void setDefaults(String def) {
        String str = new String();
        String expr = def.toUpperCase();
        String f1, f2;
        tables = sqlManager.extractTableAndColumnNames(model);
        if (expr == null) {
            return;
        }
        StringTokenizer st = new StringTokenizer(expr, ",");
        int countToken = st.countTokens();
        defaults = new String[model.getColumnCount()];
        for (int i = 0; i < countToken; i++) {
            str = st.nextToken();
            if (ZetaProperties.dstore_debug > 2) {
                log.debug("core.rml.dbi.DATASTORE.setDefaults def=" + str);
            }
            int index = str.indexOf("=");
            f1 = str.substring(0, index).trim();
            f2 = str.substring(index + 1, str.length()).trim();
            if (ZetaProperties.dstore_debug > 2) {
                log.debug("core.rml.dbi.DATASTORE.setDefaults f1=" + f1 + " f2=" + f2);
            }
            try {
                int curind = getColumn(f1);
                defaults[curind] = f2;
            }
            catch (Exception e) {
                log.error("Shit happens", e);
            }
        }
    }

    /**
     * Очищает коллекцию данных
     */
    public void clear() {
        if (model != null) {
            model.clearData();
        }
        if (model != null) {
            sqlManager.clear();
        }

    }

    /**
     * @internal
     */
    public boolean isHead() {
        if (ZetaProperties.dstore_debug > 1) {
            log.debug("core.rml.dbi.DATASTORE.isHead head=" + head);
        }
        return head;
    }

    /**
     * @internal
     */
    public void setHead(boolean head) {
        this.head = head;
    }

    /**
     * @internal
     */
    public void setUpdateable(String data) {
    }

    /**
     * @internal
     * ищет первое вхождение о в поле columnIndex, начиная с start_pos возвращает
     * ИНДЕКС строки если ничего не нашел возвращает -1
     */
    public Vector<Integer> findElement(Object o, int columnIndex, int start_pos) {
        Vector<Integer> foundRowIndexes = new Vector<Integer>();
        for (int rowIndex = start_pos; rowIndex < getRowCount(); rowIndex++) {
            Object val = null;
            try {
                val = getValue(rowIndex, columnIndex);

                if (o instanceof String) {
                    if ((((String) val).trim()).equals(((String) o).trim())) {
                        if (ZetaProperties.dstore_debug > 1) {
                            log.debug("core.rml.dbi.DATASTORE.findElement FOUND!!! ");
                        }
                        foundRowIndexes.addElement(rowIndex);
                    }
                } else if (o instanceof Double) {
                    if (((Double) val).doubleValue() == ((Double) o)
                            .doubleValue()) {
                        if (ZetaProperties.dstore_debug > 1) {
                            log.debug("core.rml.dbi.DATASTORE.findElement FOUND!!! ");
                        }
                        foundRowIndexes.addElement(rowIndex);
                    }
                }
            }
            catch (Exception e) {
                log.error("core.rml.dbi.DATASTORE.findElement keys.length ="
                        + model.getRowCount(), e);
            }
        }
        return foundRowIndexes;
    }

    /**
     * Поиск объекта в Dayastore
     * @param o - объект для поиска
     * @param column - номер столбца
     * @return массив номеров строк содержащих такой же объект
     */
    public int[] quickFind(Object o, int column) {
        if (getRowCount() == 0) {
            return new int[0];
        }

        Vector<Integer> v = new Vector<Integer>();

        int first = 0;
        int last = getRowCount() - 1;
        int fnd = 1;
        int pos = 0;
        while (fnd != 0) {
            pos = (first + last) / 2;
            Object val = getValue(pos, column);
            fnd = compareObjs(o, val);
            if (last == first) {
                break;
            }
            if (fnd == -1) {
                last = pos - 1;
            } else if (fnd == 1) {
                first = pos + 1;
            }
        }
        if (fnd == 0) {
            v.addElement(new Integer(pos));
            int pos2 = pos;
            while (--pos2 >= 0) {
                if (compareObjs(o, getValue(pos2, column)) == 0) {
                    v.addElement(new Integer(pos2));
                } else {
                    break;
                }
            }
            pos2 = pos;
            while (++pos2 < getRowCount()) {
                if (compareObjs(o, getValue(pos2, column)) == 0) {
                    v.addElement(new Integer(pos2));
                } else {
                    break;
                }
            }
        }

        int[] ret = new int[v.size()];
        for (int k = 0; k < v.size(); k++) {
            ret[k] = v.elementAt(k).intValue();
        }
        return ret;
    }

    /**
     * @internal
     */
    private int compareObjs(Object o1, Object o2) {
        if (o1 == null || o2 == null) {
            if (o1 == null) {
                if (o2 == null) {
                    return 0;
                } else {
                    return -1;
                }
            } else {
                return -1;
            }
        } else if (o1 instanceof String && o2 instanceof String) {
            return ((String) o1).compareTo((String) o2);
        } else if (o1 instanceof Double && o2 instanceof Double) {
            double d1 = ((Double) o1).doubleValue();
            double d2 = ((Double) o2).doubleValue();
            if (d1 == d2) {
                return 0;
            } else if (d1 < d2) {
                return -1;
            } else {
                return 1;
            }
        } else if (o1 instanceof java.util.Date && o2 instanceof java.util.Date) {
            if (o1.equals(o2)) {
                return 0;
            } else if (((java.util.Date) o1).before((java.util.Date) o2)) {
                return -1;
            } else {
                return 1;
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * @internal
     * устанавливает список первичных ключей для кождой updateble таблицы если
     * для какой то из таблиц список не задан, ее изменение невозможно формат
     * ('table','column1,column2, ...')
     */
    public void setUnique(String data) {
        String table, tabledata, tabledata2;
        StringTokenizer st;
        String keys[];
        while (true) {
            int b = data.indexOf("(");
            if (b == -1) {
                return;
            }
            int e = data.indexOf(")");
            if (e == -1) {
                return;
            }
            tabledata2 = data.substring(e + 1, data.length());
            tabledata = data.substring(b + 1, e);

            data = data.substring(e, data.length());
            int b1 = tabledata.indexOf("'");
            tabledata = tabledata.substring(b1 + 1, tabledata.length());
            int e1 = tabledata.indexOf("'");
            table = tabledata.substring(0, e1).toUpperCase();
            tabledata = tabledata.substring(e1 + 1, tabledata.length());
            b = tabledata.indexOf("'");
            tabledata = tabledata.substring(b + 1, tabledata.length());
            e = tabledata.indexOf("'");
            tabledata = tabledata.substring(0, e);
            st = new StringTokenizer(tabledata, ",");
            keys = new String[st.countTokens()];
            int i = 0;
            while (st.hasMoreElements()) {
                keys[i] = st.nextToken().toUpperCase().trim();
                i++;
            }
            sqlManager.setPkColumns(keys, table);
            data = tabledata2;
        }
    }

    /**
     * @internal
     * инициализирует links "field=field,....."
     */
    public void setLinks(String links) {
        this.strLinks = links;
    }

    /**
     * @internal
     */
    public void resolveLinks(String expr, Datastore rem) {
        if (expr == null) {
            return;
        }
        String str = new String();
        expr = expr.toUpperCase();
        String f1, f2;
        if (expr == null) {
            return;
        }
        StringTokenizer st = new StringTokenizer(expr, ",");
        int countToken = st.countTokens();
        links = new Hashtable<Integer, Integer>(countToken);
        for (int i = 0; i < countToken; i++) {
            str = st.nextToken();
            int index = str.indexOf("=");
            f1 = str.substring(0, index).trim();
            f2 = str.substring(index + 1, str.length()).trim();
            Integer curind = new Integer(getColumn(f1));
            Integer remind = new Integer(rem.getColumn(f2));
            links.put(remind, curind);
        }

    }

    /**
     * @internal
     */
    public Hashtable<String, Vector<String>> getTables() {
        return tables;
    }

    /**
     * @internal
     * возвращает,доступен ли обьект только для чтения
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * @internal
     * Устанавливает столбцы(поля), подлежащие дальнейшей записи в баз
     */
    public void setUpdColumns(String[] columns) {
        for (int i = 0; i < columns.length; i++) {
            updColumns[getColumn(columns[i])] = 1;
        }
    }

    /**
     * @internal
     * Возвращает столбцы(поля), подлежащие дальнейшей записи в баз
     */
    public String[] getUpdColumns() {
        String[] ret = null;
        Vector<String> v = new Vector<String>();
        for (int i = 0; i < model.getColumnCount(); i++) {
            if (updColumns[i] == 1) {
                v.addElement(model.getColumnName(i));
            }
        }
        v.copyInto(ret);
        return ret;
    }

    /**
     * @internal
     * Устанавливает столбцы(поля), подлежащие дальнейшей записи в баз
     */
    public void setUpdColumns(int[] columns) {
        updColumns = new int[columns.length];
        System.arraycopy(columns, 0, updColumns, 0, columns.length);
    }

    /**
     * @internal
     * Возвращает столбцы(поля), подлежащие дальнейшей записи в баз
     */
    public int[] getIUpdColumns() {
        return updColumns;
    }

    /**
     * удаляет строку
     *
     * @param row - индекс строки
     */
    public void delRow(int row) {
        if (parent == null) {
            sqlManager.addTransaction(model.getRowIndex(row), DatastoreTransaction.TRANSACTION_DELETE);
            int oldcrow = currentRow;
            currentRow = row;
            if (ZetaProperties.dstore_debug > 1) {
                log
                        .debug("core.rml.dbi.DATASTORE::delRowForKey calling notify children row="
                                + row);
            }
            currentRow = oldcrow;
            model.deleteRow(row);
        } else {
            parent.delRow(row);
        }
    }

    /**
     * создает новую строку и возвращает ее ИНДЕКС Вновь созданная строка
     * СТАНОВИТСЯ ТЕКУЩЕ
     * @return номер новой строки
     */
    public int newRow() {
        if (!(parent instanceof Datastore)) {
            if (ZetaProperties.dstore_debug > 0) {
                log.debug("core.rml.dbi.DATASTORE.newRow colName=" + model.getColumnNames());
            }
            if (ZetaProperties.dstore_debug > 0) {
                log.debug("core.rml.dbi.DATASTORE.newRow defaults=" + Arrays.toString(defaults));
            }
            int newRowIndex = model.addRow();
            sqlManager.addTransaction(model.getRowIndex(newRowIndex), DatastoreTransaction.TRANSACTION_INSERT);
            if (defaults != null) {
                for (int k = 0; k < defaults.length; k++) {
                    if (ZetaProperties.dstore_debug > 0) {
                        log.debug("core.rml.dbi.DATASTORE.newRow defaults[" + k + "]="
                                + defaults[k]);
                    }
                    if (defaults[k] != null) {
                        try {
                            int l = defaults[k].indexOf("~");
                            int r = defaults[k].lastIndexOf('~');
                            String str = defaults[k].substring(l + 1, r);
                            ScriptApi c = ScriptApi.getAPI(str);
                            sqlManager.addTransaction(model.getRowIndex(newRowIndex),
                                    model.getColumnName(k), DatastoreTransaction.TRANSACTION_INSERT);
                        	model.addValue(newRowIndex, k, c.eval(document.getAliases()));
                            	
                        } catch (Exception e) {
                            log.error("Shit happens", e);
                        }
                        if (ZetaProperties.dstore_debug > 0) {
                            log.debug("core.rml.dbi.DATASTORE.newRow insert default value:"
                                    + model.getValueAt(newRowIndex, k));
                        }
                    }
                }
            }
            setCurrentRow(newRowIndex);
            return newRowIndex;
        } else {
            return parent.newRow();
        }
    }

    // ////////////////////////GlobalValueObject functions ////////////////

    /**
     * @internal
     * реализация интерфейса GlobalValueObject
     */
    public void setValue(Object obj) {
    }

    /**
     * @internal
     * реализация интерфейса GlobalValueObject
     */
    public Object getValue() {
        return this;
    }

    /**
     * @internal
     * реализация интерфейса GlobalValueObject
     */
    public void setValueByName(String name, Object obj) {
        setValue(currentRow, name, obj);
    }

    /**
     * @internal
     * реализация интерфейса GlobalValueObject
     */
    public Object getValueByName(String name) throws RTException {
        if (currentRow == -1) {
            return defrow[getColumn(name)];
        }
        try {
            return getValue(currentRow, name);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            log.error("Не могу получить значение из DATASTORE " + alias
                    + " документа " + document.mypath + "/" + document.myname
                    + "\nСтрока: " + currentRow + "\nСтолбец: " + name
                    + "\n Всего строк данных в DATASTORE: " + model.getRowCount());
            throw new RTException("NullException", "Column is not inicialized");
        }
    }

    // ///////////////////////////////////////////////////////////////////

    /**
     * Возвращает значение столбца выборки из текущей строк
     *
     * @param column - alias (или target) столбца
     * @throws RTException
     */
    public Object getValue(String column) throws RTException {
        return getValue(currentRow, getColumn(column));
    }

    /**
     * Возвращает значение столбца выборки из текущей строк
     *
     * @param column - номер столбца
     * @throws RTException
     */
    public Object getValue(int column) throws RTException {
        return getValue(currentRow, column);
    }

    /**
     * Возвращает значение столбца выборки из строки с ИНДЕКСОМ row
     * @param row - номер строки
     * @param column - номер столбца
     */
    public Object getValue(int row, int column) {
        try {
            if (row < 0) {
                log.warn("DATASTORE " + alias
                        + ": Wrong!!! row < 0 !!!!. Column = " + column);
                return null;
            }
            if (parent == null || filtered) {
                if (model.getRowCount() == 0) {
                    log.warn("DATASTORE " + alias + " is empty ");
                    return null;
                }
                return model.getValueAt(row, column);
            } else {
                return parent.getValue(row, column);
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            log.error("Не могу получить значение из DATASTORE " + alias
                    + " документа " + document.mypath + "/" + document.myname
                    + "\nСтрока: " + row + "\nСтолбец: " + column
                    + "\n Всего строк данных в DATASTORE: " + model.getRowCount());
            throw e;
        }
    }

    /**
     * Возвращает значение столбца выборки из строки с ИНДЕКСОМ row
     * @param row - номер строки
     * @param column - alias (или target) столбца
     * 
     * @throws RTException
     */
    public Object getValue(int row, String column) {
        return getValue(row, getColumn(column));
    }

    // ////////////////////////////////////////////////////////////////////////

    /**
     * Устанавливает значение столбца выборки для текущей строк
     * 
     * @param column - alias (или target) столбца
     * @param value - значение  
     */
    public void setValue(String column, Object value) {
        setValue(currentRow, column, value);
    }

    /**
     * Устанавливает значение столбца выборки для текущей строк
     * @param column - номер столбца
     * @param value - значение  
     */
    public void setValue(int column, Object value) {
        if (parent == null) {
            setValue(currentRow, column, value);
        } else {
            parent.setValue(currentRow, column, value);
        }
    }

    /**
     * @internal
     * Устанавливает значение столбца выборки для строки с ИНДЕКСОМ row
     */
    public void setValue(int rowIndex, int columnIndex, Object value) {
        setValue(rowIndex, model.getColumnName(columnIndex), value);
    }

    /**
     * Устанавливает значение столбца выборки для строки с ИНДЕКСОМ row
     * 
     * @param row
     * @param column
     * @param value
     */
    public void setValue(int row, String column, Object value) {
        if (parent == null) {
            Object oldValue = model.getValueAt(row, column);
            if ((value == null && value != oldValue)|| (value != null && !value.equals(oldValue)))   {
                model.setValueAt(row, column, value);
                sqlManager.addTransaction(model.getRowIndex(row), column,
                        DatastoreTransaction.TRANSACTION_UPDATE);
            }
        } else {
            parent.setValue(row, column, value);
        }
    }

    /**
     * @internal
     */
    public Datastore getParentDatastore() {
        return parent;
    }

    // ///////////////////////////////////////////////////////////////

    /**
     * Добавляе новый столбец
     * @param typeCol - тип столбца {@link java.sql.Types}
     * @return имя столбца
     */
    public String addColumn(int typeCol) {
        return addColumn(typeCol, false);    
    }

    /**
     * @internal
     */
    public String addColumn(int typeCol, boolean addToModel) {
        int newColIndex = computeColumn.size();
        String name = compute + newColIndex;
        Compute newColumn = new Compute(name, typeCol);
        computeColumn.addElement(newColumn);
        if (addToModel) {
        int modelColumnCount = model.getColumnCount();
            model.addColumnType(modelColumnCount, newColumn.getType());
            model.addColumnName(modelColumnCount, newColumn.getName());
        }
        return name;
    }

    /**
     * синхронизирует содержимое внутреннего буфера с БД
     * @throws BadPasswordException 
     * @throws SQLException
     */
    public void update() throws UpdateException, BadPasswordException, SQLException {
        try {
            if (readOnly) {
                return;
            }
            if (parent != null) {
                if (ZetaProperties.dstore_debug > 1) {
                    log.debug("calling parent update...");
                }
                parent.update();
                return;
            }
            sqlManager.update(document.getConnection(), model, tables);
        }
        catch (BadPasswordException e) {
            log.error("Unknown Exception in Datastore.update:", e);
            ErrorReader.getInstance().addMessage(e.getMessage());
		}
        catch (SQLException ee3) {
            log.error("Unknown Exception in Datastore.update:", ee3);
            ErrorReader.getInstance().addMessage(ee3.getMessage());

            new UpdateException(ee3);
        } 
    }

    /**
     * @internal
     */
    public void rollback() {
        sqlManager.rollback();
    }

    /**
     * Заполняет обьект данными из базы.Данный метод ДОЛЖЕН вызываться перед
     * ЛЮБЫМИ метода обьекта, оперирующего с данны
     */
    public int retrieve() throws Exception {
        sqlManager.clear();
        if (parent == null) {
            model = sqlManager.retrieveModel(document, document.getConnection(), computeColumn, alias);
        } else {
            log.debug("core.rml.dbi.DATASTORE.retrieve call parent...");
            parent.retrieve();
        }
        currentRow = 0;
        tables = sqlManager.extractTableAndColumnNames(model);
        if (subStores != null) {
            for (int i = 0; i < subStores.length; i++) {
                subStores[i].retrieve();
            }
        }
        if ((sortcolumn != null) && (direction != null)) {
//                TODO убрать сортировку в таблицу
//                setSort();
        }
        notifyViews();
        return 0;
    }

    /**
     * Установка запроса, для выборки данных
     * @param sql - текст запроса (может содержать макрос)
     */
    public void setSql(String sql) {
        if (parent == null) {
            sqlManager.setSql(sql);
        } else {
            parent.setSql(sql);
        }
    }

    /**
     * Возвращает текст запроcа
     */
    public String getSql() {
        return sqlManager.getSql();
    }

//    /**
//     * Устанавливает и выполняет сортировк
//     */
//    public void setSort() {
//        VMatrix vm = new VMatrix(this, sortcolumn, direction);
//        Sorter s = new Sorter(vm);
//        skeys = s.getSortedArray();
//    }
//
//    public void setSort(int[] column, int[] direction) {
//        this.sortcolumn = column;
//        this.direction = direction;
//        VMatrix vm = new VMatrix(this, column, direction);
//        Sorter s = new Sorter(vm);
//        skeys = s.getSortedArray();
//    }
//
//    /**
//     * Сбрасывает сортировк
//     */
//    public void resetSort() {
//        System.arraycopy(keys, 0, skeys, 0, keys.length);
//    }

    /**
     * Возвращает текущую строку Нумерация строк начинается с 0
     */
    public int getCurRow() {
        return currentRow;
    }

    /**
     * Устанавливает текущую строку Нумерация строк начинается с 0
     * @param row - номер строки
     */
    public void setCurrentRow(int row) {
        int old = currentRow;
        currentRow = row;
        try {
            if ((selAction != null) && (old != currentRow)) {
                document.doAction(selAction, null);
            }
            if (parent != null) {
                parent.setCurrentRow(row);
            }
        }
        catch (Exception e) {
            log.error("Shit happens", e);
        }
    }

    /**
     * Переходит к следующей по порядку строке
     */
    public void nextRow() {
        currentRow++;
        try {
            if (selAction != null) {
            	document.doAction(selAction, null);
            }
            if (parent != null) {
                parent.setCurrentRow(currentRow);
            }
        }
        catch (Exception e) {
            log.error("Shit happens", e);
        }
    }

    /**
     * Возвращает имена столбцов результирующего набора
     */
    public String[] getNames() {
        String[] names = new String[model.getColumnCount()];
        return model.getColumnNames().toArray(names);
    }

    /**
     * Возвращает количество столбцов результирующего набора
     */
    public int getCountColumns() {
        return model.getColumnCount();
    }

    /**
     * Возвращает количество строк результирующего набора
     * @return кол-во строк
     */
    public int getRowCount() {
        return model.getRowCount();
    }

    /**
     * Возвращает тип столбца
     */
    public int getType(String column) {
        return model.getColumnType(column);
    }

    /**
     * Возвращает тип столбца
     */
    public int getType(int column) {
        return model.getColumnType(column);
    }

    /**
     * Создает новую DATASTORE, содержащую данные из нескольких Datastore
     * @param ds - массив Datastore (если аргумент null, то вернет копию текущей Datastore)
     * @return результирующую Datastore
     */
    public Datastore dsConcat(Datastore[] ds) {
        Datastore ret;
        Datastore par;
        if (parent == null) {
            par = this;
        } else {
            par = parent;
        }
        ret = new Datastore(document);
        
        ret.setSql(par.getSql());
        ret.tables = par.getTables();
        ret.setModel(par.getModel());
        try {
            for (int i = 0; i < ds.length; i++) {
                for (int j = 0; j < ds[i].getRowCount(); j++) {
                    int nr = ret.newRow();
                    for (int k = 0; k < ds[i].getCountColumns(); k++) {
                        ret.setValue(nr, k, ds[i].getValue(j, k));
                    }
                }
            }

        }
        catch (Exception e) {
            log.error("Shit happens", e);
        }
        return ret;
    }

    /**
     * @internal
     * заполняет данное DATASTORE данными из другого DATASTORE <br>
     * <b>может вызываться перед retrieve()</b>
     */
    public void fromDs(Datastore source) {
        Integer sourceColumn;
        Integer column;
        tables = sqlManager.extractTableAndColumnNames(source.getModel());
        resolveLinks(strLinks, source);

        for (int i = 0; i < source.getRowCount(); i++) {
            int nr = newRow();
            for (int j = 0; j < source.getCountColumns(); j++) {
                sourceColumn = new Integer(j);
                column = links.get(sourceColumn);
                setValue(nr, column.intValue(), source.getValue(i, sourceColumn
                        .intValue()));
                model.setColumnType(column, source.getType(sourceColumn));
            }
        }
    }

    /**
     * делает текущей нулевую строк
     */
    public void setFirst() {
        currentRow = 0;
    }

    /**
     * по имени столбца возвращает его номер
     */
    public int getColumn(String colnam) {
        int index = model.getColumnIndex(colnam);
        if (index == -1) {
            if (ZetaProperties.dstore_debug > 1) {
                log.debug("core.rml.dbi.DATASTORE.getColumn Column " + colnam
                        + " not found!!! in Document: " + document.mypath + "/"
                        + document.myname);
                if (ZetaProperties.dstore_debug > 2) {
                    String col_names = "\n";
                    for (int i = 0; i < model.getColumnCount(); i++) {
                        col_names += model.getColumnName(i) + "\n";
                    }
                    log.debug("Names of columns are:" + col_names);

                    String separator = "\n-------------------------------------------------------------------------";
                    String message = "Документ: " + document.mypath + "/" + document.myname
                            + "\n DATASTORE: " + alias + separator
                            + "\nДоступные имена столбцов: " + col_names;
                    String header = "Столбец с именем \"" + colnam
                            + "\" не найден!";
                    ZetaUtility.message(header, message, ZetaProperties.MESSAGE_ERROR);
                }
            }
        }

        return index;
    }

    /**
     * @internal
     * @param obj
     * @return
     */
    boolean isTrue(Object obj) {
        if (obj instanceof Double) {
            return ((Double) obj).intValue() != 0;
        }
        return obj instanceof String && ((String) obj).equalsIgnoreCase("TRUE");
    }

    /**
     * @internal
     * Implementation for Calculator Methods
     */
    public Object method(String method, Object arg) throws Exception {
        if (method.equals("SETMAXFETCHROW")) {
            if (!(arg instanceof Double)) {
                throw new RTException("CastException",
                        "method SETMAXFETCHROW must have one parameter with Number type");
            }
            setMaxFethRow(((Double)arg).intValue());
            return new String("");

        }
        if (method.equals("EXECUTE")) {
            if (arg instanceof String) {
                try {
                    executeQuery((String) arg);
                }
                catch (SQLException e) {
                    log.error("Shit happens", e);
                    ErrorReader.getInstance().addMessage(e.getMessage());
                    throw new RTException("RunTime", e.getMessage());
                }

            } else {
                throw new RTException("CastException",
                        "method EXECUTE must have one parameter with String type");
            }

        } else if (method.equals("ITERATOR")) {
            return new DSIterator(this);
        } else if (method.equals("SETVALUE")) {
            try {
                Vector<Object> v = (Vector<Object>) arg;
                setValue((String) v.elementAt(0), v.elementAt(1));
                return new Double(0);
            }
            catch (ClassCastException e) {
                log.error("Shit happens", e);
                throw new RTException(
                        "CastException",
                        "method SETVALUE must have"
                                + "two parameter, first compateable with String ");

            }

        } else if (method.equals("GETVALUE")) {

            if (arg instanceof String) {
                return getValue((String) arg);
            } else if (arg instanceof Double) {
                return getValue(((Double) arg).intValue());
            } else {
                throw new RTException(
                        "CastException",
                        "method GETVALUE must have"
                                + "one parameter,compateable with String or Numeric type");
            }

        } else if (method.equals("RETRIEVE")) {
            retrieve();
            return new Double(getRowCount());

        } else if (method.equals("SORT")) {
            try {
                Vector<Object> v = (Vector<Object>) arg;
                Object[] sort = (Object[]) v.elementAt(0);
                Object[] dim = (Object[]) v.elementAt(1);
                if (sort.length != dim.length) {
                    throw new RTException(
                            "Index Exception",
                            "method Sort must have"
                                    + "two parameters compateable with Array type and with equals length");
                }

                int[] isort = new int[sort.length];
                int[] idim = new int[dim.length];

                for (int i = 0; i < idim.length; i++) {
                    idim[i] = ((Double) dim[i]).intValue();
                    isort[i] = ((Double) sort[i]).intValue();
                }
                //TODO перенести сортировку в грид
//                setSort(isort, idim);
            }
            catch (ClassCastException e) {
                log.error("Shit happens", e);
                throw new RTException(
                        "CastException",
                        "method Sort must have"
                                + "two parameters compateable with Array type and with equals length");

            }
            catch (ArrayIndexOutOfBoundsException e1) {
                log
                        .error(
                                "Index Exception method Sort must have two parameters compateable with Array type and with equals length",
                                e1);
                throw new RTException(
                        "Index Exception",
                        "method Sort must have"
                                + "two parameters compateable with Array type and with equals length");

            }

            //TODO перенести сортировку в грид
//            setSort();
            return new Double(0);

        } else if (method.equals("CLEARSORT")) {
            //TODO перенести сортировку в грид
//            resetSort();
            return new Double(0);

        } else if (method.equals("FILTER")) {
            if (arg instanceof Quoted) {
                Vector<Integer> v1 = new Vector<Integer>();
                int currow = getCurRow();
                for (int i = 0; i < getRowCount(); i++) {
                    setCurrentRow(i);
                    if (isTrue(((OP) ((Quoted) arg).getOP()).eval())) {
                        v1.addElement(new Integer(i));
                    }
                }
                int[] ret = new int[v1.size()];
                for (int i = 0; i < v1.size(); i++) {
                    ret[i] = v1.elementAt(i).intValue();
                }
                setCurrentRow(currow);
                return null;//filterManager.createFilter_with_abs_keys(this, ret);
            }
        } else if (method.equals("SUM")) {
            try {
                int col = -1000;
                double d = 0;
                if (arg instanceof Double) {
                    col = ((Double) arg).intValue();
                }
                if (arg instanceof String) {
                    col = getColumn((String) arg);
                }
                if (col == -1000) {
                    throw new RTException(
                            "CastException",
                            "method Sum must have"
                                    + "one parameter compateable with String or Number type");
                }
                for (int i = 0; i < getRowCount(); i++) {
                    d += ((Double) getValue(i, col)).doubleValue();
                }
                return new Double(d);
            }
            catch (ClassCastException e) {
                log.error("Shit happens", e);
                throw new RTException("CastException",
                        "method Sum must have one parameter ");
            }
        } else if (method.equals("SIZE")) {
            return new Double(getRowCount());
        } else if (method.equals("UPDATE")) {
            update();
            return new Double(0);
        } else if (method.equals("NEWROW")) {
            return new Double(newRow());
        } else if (method.equals("CLEAR")) {
            clear();
            return new Double(0);
        } else if (method.equals("SETSQL")) {
            if (arg instanceof String) {
                setSql((String) arg);
                return new Double(0);
            } else {
                throw new RTException("CastException",
                        "method SetSql must have"
                                + "one parameter compateable with String type");
            }

        } else if (method.equals("DELROW")) {
            if (arg instanceof Double) {
                delRow(((Double) arg).intValue());
                return new Double(0);
            } else {
                throw new RTException("CastException",
                        "method DelRow must have"
                                + "one parameter compateable with Number type");
            }

        } else if (method.equals("GETSQL")) {
            return new String(getSql());
        } else if (method.equals("GETCURROW")) {
            return new Double(getCurRow());
        } else if (method.equals("SETCURROW")) {
            if (arg instanceof Double) {
                setCurrentRow(((Double) arg).intValue());
                return new Double(0);
            } else {
                throw new RTException("CastException",
                        "method SetCurRow must have"
                                + "one parameter compateable with Number type");
            }

        } else if (method.equals("FASTSETCURROW")) {
            if (arg instanceof Double) {
                currentRow = ((Double) arg).intValue();
                return new Double(0);
            } else {
                throw new RTException("CastException",
                        "method FastSetCurRow must have"
                                + "one parameter compateable with Number type");
            }

        } else if (method.equals("NOTIFYVIEWS")) {
            notifyViews();
            return new Double(0);
        } else if (method.equals("CONCAT") || method.equals("CLONE")) {
            Datastore[] dss;
            if (arg == null) {
                dss = new Datastore[1];
                dss[0] = this;
                return dsConcat(dss);
            }
            if ((arg instanceof Vector)) {
                dss = new Datastore[((Vector<Object>) arg).size()];
                ((Vector<Object>) arg).copyInto(dss);
                return dsConcat(dss);
            } else if (arg instanceof Datastore) {
                dss = new Datastore[1];
                dss[0] = (Datastore) arg;
                return dsConcat(dss);
            }
        } else if (method.equals("ADDCOLUMN")) {
            if (arg == null) {
                return addColumn(java.sql.Types.CHAR);
            }
            try {
                String s = ((String) arg).toUpperCase();
                if (s.equals("NUMERIC")) {
                    return addColumn(java.sql.Types.DOUBLE);
                } else if (s.equals("DATE")) {
                    return addColumn(java.sql.Types.TIMESTAMP);
                } else if (s.equals("STRING")) {
                    return addColumn(java.sql.Types.CHAR);
                } else {
                    throw new RTException(
                            "Syntax",
                            "method ADDCOLUMN must have"
                                    + "one parameter equals NUMERIC, DATE or STRING");
                }
            }
            catch (ClassCastException e) {
                log.error("Shit happens", e);
                throw new RTException("CastException",
                        "method ADDCOLUMN must have"
                                + "one parameter compatible with String type");
            }
        } else if (method.equals("QUICKFIND")) {
            try {
                Vector<Object> v = (Vector<Object>) arg;
                int column = ((Double) v.elementAt(0)).intValue();
                Object o = v.elementAt(1);
                int[] ar = quickFind(o, column);
                Object[] oar = new Object[ar.length];
                for (int i = 0; i < oar.length; i++) {
                    oar[i] = new Double(ar[i]);
                }
                return oar;
            }
            catch (Exception e) {
                log.error("Shit happens", e);
                throw new RTException("RunTime", e.getMessage());
            }
        } else {

            throw new RTException("HasNotMethod", "method " + method
                    + " not defined in class DATASTORE!");
        }
        return new Double(0);
    }

	/**
	 * Выполнить произвольный запрос
	 *  
	 * @param query - запрос
	 * @throws SQLException
	 * @throws ConnectException
	 * @throws BadPasswordException
	 */
	public void executeQuery(String query) throws SQLException,
			ConnectException, BadPasswordException {
		sqlManager.executeQuery(document.getConnection(), query);
		// в этом блоке не уверен -спросить у Пашки
		document.getConnection().commit();
		DSCollection.repeatLocks();
		// конец блока в котором не уверен
	}

	/**
	 * Устанавливает ограничение на кол-во строк 
	 * @param maxRows
	 */
	public void setMaxFethRow(int maxRows) {
		sqlManager.setMaxFetchRow(maxRows);
		if (ZetaProperties.dstore_debug > 1) {
		    log.debug("setMaxFetchRow called with arg " + maxRows);
		}
	}

    /**
     * Оповещает, связанные с Datastore, визуальные компоненты о том что данные изменились
     */
    public void notifyViews() {
        if (handler != null) {
            handler.notifyHandler(null);
        }
    }

    /**
     * @internal
     */
    public String type() {
        return "DATASTORE";
    }

    /**
     * @internal
     */
    protected void finalize() {
    }

    /**
     * @internal
     */
    public DatastoreModel getModel() {
        return model;
    }

    /**
     * @internal
     */
    public void setModel(DatastoreModel model) {
        this.model = model;
    }

    /**
     * @internal
     */
    public FilterManager getFilterManager() {
        return filterManager;
    }

    @Override
    /**
     * @internal
     */
    public String toString() {
        return alias != null ? alias : super.toString();
    }

    /**
     * @internal
     */
    public List<Integer> getRows() {
        if (model != null) {
            return model.getRowIndexes();
        }
        return Collections.emptyList();
    }

    /**
     * @internal
     */
    public void setFiltered(boolean filtered) {
        this.filtered = filtered;
    }

    /**
     * @internal
     */
	@Override
	public void fromDS() {
		// TODO Auto-generated method stub
		
	}

    /**
     * @internal
     */
	@Override
	public void toDS() {
		// TODO Auto-generated method stub
		
	}

    /**
     * @internal
     */
	@Override
	public void addChild(RmlObject child) {
		container.addChildToCollection(child);
	}

    /**
     * @internal
     */
	@Override
	public RmlObject[] getChildren() {
		return container.getChildren();
	}

    /**
     * @internal
     */
	@Override
	public void initChildren() {
		RmlObject[] objs = container.getChildren();
        addSubStores(objs);
	}

    /**
     * @internal
     */
	@Override
	public Container getContainer() {
		return container;
	}

    /**
     * @internal
     */
	@Override
	public boolean addChildrenAutomaticly() {
		return true;
	}
}
