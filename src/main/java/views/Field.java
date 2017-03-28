package views;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.ParseException;
import java.util.StringTokenizer;

import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;
import javax.swing.text.MaskFormatter;

import loader.ZetaProperties;
import loader.ZetaUtility;

import org.apache.log4j.Logger;

import publicapi.FieldAPI;
import views.field.BaseField;
import views.field.DateCalendar;
import views.focuser.FocusPosition;
import views.focuser.Focusable;
import views.grid.GridSwing;
import action.api.ScriptApi;
import core.document.AliasesKeys;
import core.document.Document;
import core.document.NotifyInterface;
import core.document.Shortcutter;
import core.parser.Proper;
import core.reflection.objects.VALIDATOR;
import core.rml.VisualRmlObject;
import core.rml.dbi.Group;
import core.rml.ui.impl.ZButtonImpl;
import core.rml.ui.impl.ZFormattedTextFieldImpl;
import core.rml.ui.impl.ZPanelImpl;
import core.rml.ui.impl.ZPasswordFieldImpl;
import core.rml.ui.impl.ZScrollPaneImpl;
import core.rml.ui.impl.ZTextAreaImpl;
import core.rml.ui.impl.ZTextFieldImpl;
import core.rml.ui.interfaces.ZButton;
import core.rml.ui.interfaces.ZComponent;
import core.rml.ui.interfaces.ZPanel;
import core.rml.ui.interfaces.ZPasswordField;
import core.rml.ui.interfaces.ZTextArea;
import core.rml.ui.interfaces.ZTextComponent;
import core.rml.ui.interfaces.ZTextField;

/**
* Графический компонент "Поле ввода"
* 
*/
public class Field extends VisualRmlObject implements FieldAPI, BaseField, ActionListener, NotifyInterface, Shortcutter, Focusable, KeyListener, FocusListener, MouseListener {

	private static final String READONLY = "READONLY";

	public static final String NO = "NO";

	public static final String ALL = "ALL";

	public static final String HANDBOOK = "HANDBOOK";

	protected ZPanel fieldPanel = ZPanelImpl.create();
	
    protected ZTextComponent editField;
    private FocusPosition                    fp               = new FocusPosition();

    
    @Override
    public Object getValueByName(String name) throws Exception {
        return null;
    }

    public void setValueByName(String name, Object obj) throws Exception {
    }

    @Override
    public int getFocusPosition() {
        return fp.getFocusPosition();
    }

    @Override
    public void setFocusPosition(int position) {
        fp.setFocusPosition(position);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

	
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button.getJComponent()) {
            bclick(Field.this, e);
        }
    }

    public void focusGained(FocusEvent e) {
        if (!button.isVisible())// && !alwaysShowButton)
        {
            if (editable.compareTo(HANDBOOK) == 0 || editable.compareTo(ALL) == 0) {
                showButton(true);
            }
            else if ((type == java.sql.Types.DATE ||type == java.sql.Types.TIMESTAMP)
                    && editable.compareTo(READONLY) != 0) {
                showButton(true);
            }
        }

        Object source = e.getSource();

        if (source.equals(fieldPanel.getJComponent())) {
            if (editable.compareToIgnoreCase(HANDBOOK) == 0
                    && button.isVisible()) {
                button.requestFocusInWindow();
            }
            else {
                editField.requestFocusInWindow();
            }
        }
    }

    public void focusLost(FocusEvent e) {
        Component next_focus = e.getOppositeComponent();

        if (next_focus != null && !next_focus.equals(fieldPanel.getJComponent())
                && !next_focus.equals(editField.getJComponent()) && !next_focus.equals(button.getJComponent())
                && !next_focus.equals(cld)) {
            if (button.isVisible() && !alwaysShowButton) {
                showButton(false);
            }
            finishTheEditing();
        }
        else if (next_focus == null) {
            if (button.isVisible() && !alwaysShowButton) {
                showButton(false);
            }
            finishTheEditing();
        }
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if(multiLine){
            	((ZTextArea) editField).append("\n"); 
            	((ZTextArea) editField).setCaretPosition(((ZTextArea) editField).getDocument().getLength());            	
            }else{
            	finishTheEditing();
            }
        }

        if (document.executeShortcut(e)) {
            e.consume();
            return;
        }

        switch (e.getKeyCode()) {
        case KeyEvent.VK_ESCAPE:
            editField.setText(svalue);
            editField.selectAll();
            e.consume();
            break;
        case KeyEvent.VK_ENTER:
            if ((e.getModifiers() & (InputEvent.ALT_MASK + InputEvent.CTRL_MASK)) == 0) {
                showButton(false);
                fieldPanel.transferFocus();
                e.consume();
            }
            else if ((e.getModifiers() & InputEvent.ALT_MASK) != 0) {
                if (button.isVisible()) {
                    bclick(Field.this, null);
                }
                e.consume();
            }
            break;
        case KeyEvent.VK_E:
            if ((e.getModifiers() & InputEvent.CTRL_MASK) != 0) {
                calcExtra();
                e.consume();
            }
            break;
        default:
            Panel fp = getFormParent();
            if (fp != null && e.getKeyCode() == fp.menuKey) {
                log.debug(fieldPanel.getLocation());
                fp.showMenu((JPanel) fieldPanel.getJComponent());
                e.consume();
            }
            break;
        }
    }

    public void mousePressed(MouseEvent e) {
    	fieldPanel.requestFocus();
    }

    /**
     * 
     */
    private static final Logger              log              = Logger
                                                                      .getLogger(Field.class);

    static final JTextComponent.KeyBinding[] defaultBindings  = {
            new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_C,
                    InputEvent.CTRL_MASK), DefaultEditorKit.copyAction),
            new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_A,
                    InputEvent.CTRL_MASK), DefaultEditorKit.selectAllAction),
            new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_V,
                    InputEvent.CTRL_MASK), DefaultEditorKit.pasteAction),
            new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_X,
                    InputEvent.CTRL_MASK), DefaultEditorKit.cutAction), };

    private static final int                  BUTTON_WIDTH               = 20;

    private DateCalendar                              cld              = null;

    private ZButton                          button           = ZButtonImpl.create(
                                                                      "...");

    private int                                      type             = Integer.MIN_VALUE;            // говорит о том, что type не определен

    private boolean                           alwaysShowButton = true;

    private String[]                                 depends          = null;

    /**
     * скрипт, реализующий открытие документа-справочника для подборки значений в случае HandBook, 
     * 
     */
    private String                                   edit             = null;

    

    /**
     * скрипт, реализующий связь между значением поля и столбцами DATASTORE, 
     * возвращеного из справочника (объект STORE)
     */
    private String                                   editExp          = null;
    /**
     * **************
     */
    private VALIDATOR                         validator        = new VALIDATOR();

    private String                            border           = "3DLOWERED";

    private Object                         pValue           = null;

    private Font                                     scaleFont        = null;                         // данный фонт используется для отрисов

    // с масштабированием
    private FontMetrics                              fm               = null;

    private int                                      dw               = 2;

    private int                                      dh               = 2;

    private String                                   halignment       = "LEFT";

    private String                                   valignment       = "CENTER";

    private boolean                                  isComputed       = false;

    private String                                   exp              = null;

    private String                                   extraexp         = null;

    private String                                   target           = null;

    private String                                   editable         = "HAND";

    private String                                   svalue           = null;

    private boolean                                  needTranslate    = false;                        // для отчетов = true

    // для документов = false
    public boolean                           needSetString    = true;                         // определяет, нужно ли устанавливать

    // String'овое

    // значение при вызове setValue(Object)
    private boolean                                  multiLine        = false;

    private boolean                                  wordWrap         = false;

    private boolean                                  password         = false;

    private String mask = null;

	//нужен для построения последовательности расчета зависимых полей
    private ScriptApi calc = null;

    public Field() {
        fieldPanel.setLayout(null);
        button.setFocusable(false);
    }

    private void bclick(Field f, ActionEvent e) {
        if (type == java.sql.Types.DATE || type == java.sql.Types.TIME
                || type == java.sql.Types.TIMESTAMP) {
            cld = DateCalendar.getInstance(this);
        }
        else if (edit != null) {
            try {
                document.doAction(edit, this);
            }
            catch (Exception ex) {
                log.error("Shit happens!!!", ex);
            }
        }
    }

    public void calc() {
        try {
        	//TODO проверить как заполняются колонтитулы в репорте 
        	//при выполнении скрипта в отдельном потоке. И вообще как пересчитываются зависимые поля.
            document.executeScript(exp, true);
        }
        catch (Exception e) {
            log.error("Shit happens!!!", e);
        }

    }

    public void calcDep() {
        if (depends == null) {
            return;
        }
        for (String element : depends) {
            views.Field f = (views.Field) document.findObject(element);
            if (f != null) {
                f.calc();
            }
            else {
                log
                        .info("~views.Field::calcDep() : object views.Field not found for alias "
                                + element);
            }
        }
    }

    protected void calcHandbookDep() {
        if (depends == null) {
            return;
        }
        for (String element : depends) {
            views.Field f = (views.Field) document.findObject(element);
            if (f != null) {
                f.calcHandbookExp();
            }
            else {
                log
                        .info("~views.Field::calcDep() : object views.Field not found for alias "
                                + element);
            }
        }
    }

    protected void calcHandbookExp() {
        try {
        	document.executeScript(editExp, true);
        }
        catch (Exception e) {
            log.error("Shit happens!!!", e);
        }
    }

    protected void calcExtra() {
        try {
            if (extraexp != null) {
            	document.executeScript(extraexp, true);
            }
        }
        catch (Exception e) {
            log.error("Shit happens!!!", e);
        }

    }

    public void paint(Graphics g, int a) {
    	if(!isVisible())
    		return;
//        try {
//            if (document.calculate(exp).equals("NO")) {
//                return;
//            }
//        }
//        catch (Exception e) {
//            log.error("Shit happens!!!", e);
//            if (!fieldPanel.isVisible()) {
//                return;
//            }
//        }
        int dx = fieldPanel.getBounds().x;
        int dy = fieldPanel.getBounds().y;
        if (!isNeedTranslate()) {
            dx = 0;
            dy = 0;
        }
        g.translate(dx * a / 100, dy * a / 100);
        int width = getSize().width;
        int height = getSize().height;
        g.setColor(fieldPanel.getBackground());
        if (!fieldPanel.getBackground().equals(Color.WHITE)) {
            log.debug("Field, named " + alias + " is not white!!!!!");
        }
        g.fillRect(0, 0, width * a / 100, height * a / 100);

        if (border.equals("3DLOWERED")) {
            g.setColor(Color.darkGray);
            g.drawLine(0, 0, width * a / 100, 0);
            g.drawLine(0, 0, 0, height * a / 100);

            g.setColor(Color.white);
            g.drawLine(0, height * a / 100, width * a / 100, height * a / 100);
            g.drawLine(width * a / 100, height * a / 100, width * a / 100, 0);

            g.setColor(Color.black);
            g.drawLine(a / 100, a / 100, (width - 1) * a / 100, a / 100);
            g.drawLine(a / 100, a / 100, a / 100, (height - 1) * a / 100);

            g.setColor(Color.lightGray);
            g.drawLine(a / 100, (height - 1) * a / 100, (width - 1) * a / 100,
                    (height - 1) * a / 100);
            g.drawLine((width - 1) * a / 100, (height - 1) * a / 100,
                    (width - 1) * a / 100, a / 100);
        }
        else if (border.equals("BOX")) {
            g.setColor(Color.black);
            SmartLine line = new SmartLine(g);
            line.setType(0);
            if (parent instanceof ReportForm) {
                if (((ReportForm) parent).isPrint) {
                    line.isPrint = true;
                }
                else {
                    line.isPrint = false;
                }
            }
            line.draw(0, 0, width, a);
            line.draw(0, height, width, a);
            line.setType(1);
            line.draw(0, 0, height + 1, a);
            line.draw(width, 0, height + 1, a);
        }

        g.setFont(scaleFont);

        if (svalue != null) {
            g.setColor(fieldPanel.getForeground());
            if (multiLine) { // нужно распарсить строки и сделать выравнивание
                String svalue1;
                if (wordWrap) {
                    svalue1 = UTIL.makeWrap(svalue, " ", fieldPanel.getBounds().width - dw
                            - 3, fm);
                }
                else {
                    svalue1 = svalue;
                }
                StringTokenizer st = new StringTokenizer(svalue1, "\n", true);
                int cnt = st.countTokens(); // кол-во стро
                String[] tok = new String[cnt];
                boolean ptisnl = false;
                int curind = 0;
                for (int i = 0; i < cnt; i++) {
                    String next = st.nextToken();
                    if (!next.equals("\n") && ptisnl) {
                        ptisnl = false;
                        tok[curind - 1] = next;
                        continue;
                    }
                    else {
                        if (next.equals("\n")) {
                            ptisnl = true;
                        }
                        tok[curind] = next;
                        curind++;
                    }
                }
                cnt = curind;
                int y1 = UTIL.getOutPoint(width, height, fm, halignment,
                        valignment, dw, dh, 0, 0, "A")[1];
                if (valignment.equals("TOP")) {
                }
                if (valignment.equals("CENTER")) {
                    if (cnt % 2 == 0) {
                        y1 -= (fm.getHeight() * (cnt / 2) - (fm.getHeight() / 2));
                    }
                    else {
                        y1 -= (fm.getHeight() * (cnt / 2));
                    }
                }
                if (valignment.equals("BOTTOM")) {
                    y1 -= (fm.getHeight() * (cnt / 2));
                }
                for (int i = 0; i < curind; i++) {
                    String next = tok[i];
                    if (next.equals("\n")) {
                        next = "";
                    }
                    int[] xy = UTIL.getOutPoint(width, height, fm, halignment,
                            valignment, dw, dh, 0, 0, next);
                    g.setClip(0, 0, (getSize().width - dw) * a / 100,
                            (getSize().height - dh) * a / 100);
                    g.drawString(next, xy[0] * a / 100, (y1 + i
                            * fm.getHeight())
                            * a / 100);
                }
            }
            else {
                String val = svalue;
                if (password) {
                    char[] ar = val.toCharArray();
                    for (int i = 0; i < ar.length; i++) {
                        ar[i] = '*';
                    }
                    val = new String(ar);
                }
                int[] xy = UTIL.getOutPoint(width, height, fm, halignment,
                        valignment, dw, dh, 0, 0, val);
                g.setClip(0, 0, (getSize().width - dw) * a / 100,
                        (getSize().height - dh) * a / 100);
                g.drawString(val, xy[0] * a / 100, xy[1] * a / 100);
            }
        }

        g.translate(-dx * a / 100, -dy * a / 100);
    }

    public void finishTheEditing() {
        Object val = null;
        try {
            val = validator.toObject(gettext());
        }
        catch (Exception ex) {
            selectUndo();
            editField.setText(svalue);
            return;
        }
        Panel fp = null;
        core.rml.dbi.Datastore ds = null;
        if ((fp = getFormParent()) != null && (ds = fp.getDatastore()) != null
                && target != null) {
            Object now_val = null;
            try {
                now_val = ds.getValue(0, target);
            }
            catch (Exception e) {
                log.error("Null in DATASTORE: row = 0, column =" + target, e);
            }
            if ((now_val != null && !now_val.equals(val))
                    || (now_val == null && val != null)) {
                if (val != null) {
                    setValue(val);
                   // calcHandbookExp(); //Вернул зачем-то удаленный HandBookExp
                    calcDep();
                }
                else {
                    setValue(null);
                    //calcHandbookExp();//Вернул зачем-то удаленный HandBookExp
                    calcDep();
                }
            }
            // setValue(ds.getValue(0, target));
        }
        else if (val != null && !val.equals(pValue)) {
            setValue(val);
            calcHandbookExp();
            calcDep();
        }
    }

    public String getEditable() {
        return editable;
    }

    public String getExp() {
        return exp;
    }

    public Panel getFormParent() {
        if (parent instanceof Panel) {
            return (Panel) parent;
        }
        else {
            return null;
        }
    }

    public String gettext() {
        String val = editField.getText();
        if(editField instanceof JFormattedTextField)
            try {
                ((JFormattedTextField) editField).commitEdit();
                val = (String) ((JFormattedTextField) editField).getValue();
            }
            catch (ParseException e) {
                //log.error("!", e);
            }
        return val;
    }

    public int getType() {
        return type;
    }

    @Override
    public Object getValue() {
        if (parent instanceof views.ReportForm) { // для Field'а в
            // ReportForm'e
            try {
                core.rml.dbi.Datastore ds = null;
                core.rml.dbi.Group gr = ((views.ReportForm) parent).getStore();
                ds = ((views.ReportForm) parent).getDatastore();
                if (gr != null) {
                    if (isComputed) {
                        return gr.getValueByName(alias);
                    }
                    else {
                        if (ds != null) {
                            return ds.getValue(gr.begrow, target);
                        }
                    }
                }
            }
            catch (Exception e) {
                log.error("Shit happens!!!", e);
            }
        }
        // if (!inFromEditField)
        // {
        // fromEditField(gettext());
        // }
        if (pValue != null) {
            log.debug("Retur value is: " + pValue.toString());
        }
        else {
            log.debug("Retur value is: NULL");
        }
        return pValue;
    }

    public void init(Proper prop, Document doc) {
    	super.init(prop, doc);
        String sp;
        button.setVisible(false);
        sp = (String) prop.get("ALWAYSSHOWBUTTON", NO);

        alwaysShowButton = sp.equals("YES");
        password = ((String) prop.get("PASSWORD", NO)).equals("YES");

        sp = (String) prop.get("MULTILINE", NO);
        multiLine = sp.equals("YES");

        mask  = (String) prop.get("MASK");
        
        if (password) {
            editField = ZPasswordFieldImpl.create();
        }
        else if (multiLine) {
            editField = ZTextAreaImpl.create();
            ((ZTextArea) editField).setLineWrap(true);
            ((ZTextArea) editField).setWrapStyleWord(true);
        }
        else if(mask != null && !mask.trim().equals("")){
            try {
                MaskFormatter mf = new MaskFormatter(mask);
                mf.setPlaceholderCharacter('_');
                mf.setOverwriteMode(true);
                editField = ZFormattedTextFieldImpl.create(mf);
            }
            catch (ParseException e) {
                log.error("!", e);
                editField = ZTextFieldImpl.create();
            }
        }
        else {
            editField = ZTextFieldImpl.create();
        }

        Keymap k = editField.getKeymap();
        JTextComponent.loadKeymap(k, defaultBindings, editField.getActions());
        button.addActionListener(this);
        button.addFocusListener(this);
        editField.addFocusListener(this);
        fieldPanel.addFocusListener(this);
        editField.addKeyListener(this);
        fieldPanel.addKeyListener(this);
        editField.addMouseListener(this);

        if(!multiLine){
	        button.setSize(BUTTON_WIDTH, height - 1);
	        button.setMaximumSize(new Dimension(BUTTON_WIDTH, height - 1));
	        editField.setSize(width - 1, height - 1);
	        editField.setLocation(0, 0);
	        button.setLocation(width - BUTTON_WIDTH, 0);
	        editField.setMinimumSize(new Dimension(width - BUTTON_WIDTH - 1, fieldPanel.getHeight() - 1));
	        fieldPanel.add(editField);
        }else{
        	fieldPanel.setLayout(new GridLayout(1,1));
        	fieldPanel.add(ZScrollPaneImpl.create(editField));
        }

        seteditable((String) prop.get("EDITABLE", "HAND"));
        fieldPanel.doLayout();
        fieldPanel.validate();

        editField.setFont(font);
        scaleFont = editField.getFont();
        fm = fieldPanel.getFontMetrics(scaleFont);

        sp = (String) prop.get("EXP");
        if (sp != null) {
            exp = sp;
            isComputed = true;
            setCalc(ScriptApi.getAPI(sp));            
        }
        sp = (String) prop.get("EXTRAEXP");
        if (sp != null) {
            extraexp = sp;
        }

        sp = (String) prop.get("DEP");
        if (sp != null) {
            setdep(sp);
        }

        target = (String) prop.get("TARGET");

        sp = (String) prop.get("TYPE");
        if (sp != null) {
            setType(sp);
        }

        sp = (String) prop.get("VALUE");
        if (sp != null) {
            try {
                pValue = validator.toObject(sp);
                settext(sp);
            }
            catch (Exception e) {
                log.error("Shit happens!!!", e);
            }
        }

        sp = (String) prop.get("EDIT");
        if (sp != null) {
            edit = sp;
            setCalc(ScriptApi.getAPI(sp));
        }

        sp = (String) prop.get("EDITEXP");
        if (sp != null) {
            editExp = sp;
            setCalc(ScriptApi.getAPI(sp));
        }

        sp = (String) prop.get("HALIGNMENT");
        if (sp != null) {
            halignment = sp;
            if (halignment.equals("LEFT")) {
                if (editField instanceof ZTextField || editField instanceof ZPasswordField) {
                    ((ZTextField) editField)
                            .setHorizontalAlignment(JTextField.LEFT);
                }
                else if(editField instanceof ZTextArea){
                    ((ZTextArea) editField).setAlignmentY(JTextArea.LEFT_ALIGNMENT);
                }
            }
            if (halignment.equals("RIGHT")) {
                if (editField instanceof ZTextField || editField instanceof ZPasswordField) {
                    ((ZTextField) editField)
                            .setHorizontalAlignment(JTextField.RIGHT);
                }
                else if(editField instanceof ZTextArea){
                    ((ZTextArea) editField).setAlignmentY(JTextArea.RIGHT_ALIGNMENT);
                }
            }
            if (halignment.equals("CENTER")) {
                if (editField instanceof ZTextField || editField instanceof ZPasswordField) {
                    ((ZTextField) editField)
                            .setHorizontalAlignment(JTextField.CENTER);
                }
                else if(editField instanceof ZTextArea){
                    ((ZTextArea) editField).setAlignmentY(JTextArea.CENTER_ALIGNMENT);
                }
            }
        }

        sp = (String) prop.get("VALIGNMENT");
        if (sp != null) {
            valignment = sp;
            if (valignment.equals("TOP")) {
                editField.setAlignmentX(JTextComponent.TOP_ALIGNMENT);
            }
            if (valignment.equals("BOTTOM")) {
                editField.setAlignmentX(JTextComponent.BOTTOM_ALIGNMENT);
            }
            if (valignment.equals("CENTER")) {
                editField.setAlignmentX(JTextComponent.CENTER_ALIGNMENT);
            }
        }

        sp = (String) prop.get("EDITMASK");
        if (sp != null) {
            validator.setMask(sp);
        }

        sp = (String) prop.get("BORDER");
        if (sp != null) {
            setBorder(sp);
        }
        else {
            setBorder(border);
        }

        sp = (String) prop.get("WORDWRAP");
        if (sp != null && sp.equals("YES")) {
            wordWrap = true;
        }
        else {
            wordWrap = false;
        }

        sp = (String) prop.get("SHORTCUT");
        if (sp != null) {
            try {
                String[] ar = UTIL.parseDep(sp);
                for (int i = 0; i < ar.length; i++) {
                    doc.addShortcut(ar[i], this);
                }
            }
            catch (Exception e) {
                log.error("Shit happens!!!", e);
            }
        }

        sp = (String) prop.get("FIRSTFOCUS");
        if (sp != null && sp.toUpperCase().equals("YES")) {
            doc.setFirstFocus(this.editField.getJComponent());
        }
        
        sp = ((String) prop.get("FONT_COLOR"));
        if (sp != null) {
            editField.setForeground(UTIL.getColor(sp));
        }
        else {
            editField.setForeground(Color.black);
        }

        sp = ((String) prop.get("BG_COLOR"));
        if (sp != null) {
            editField.setBackground(UTIL.getColor(sp));
            fieldPanel.setBackground(UTIL.getColor(sp));
        }
        else {
            editField.setBackground(Color.white);
            fieldPanel.setBackground(Color.white);
        }
        
        editField.setToolTipText(toolTipText);
        
    }

    void setBorder(String border) {
        this.border = border;
        if (border.equals("3DLOWERED")) {
            dw = 3;
            dh = 3;
        }
        else if (border.equals("BOX")) {
            dw = 2;
            dh = 2;
        }
    }

    public void notifyIt() {
        Container par = fieldPanel.getParent();
        core.rml.dbi.Datastore ds2 = (core.rml.dbi.Datastore) document.findObject(AliasesKeys.STORE);
        log.debug("Notify it in Field called");
        log.debug("ds2=" + ds2);
        if (ds2 != null) {
            if (editExp != null) {
                try {
                    document.executeScript(editExp, true);
                }
                catch (Exception e) {
                    log.error("Shit happens!!!", e);
                }
                if (depends != null) {
                    calcHandbookDep();
                    calcDep();
                }
            }
        }
        if (par instanceof NotifyInterface) {
            ((NotifyInterface) parent).notifyIt();
        }
    }

    public void setScaleFont(int a) {
        Font tmp = editField.getFont();
        if (tmp == null) {
            return;
        }
        scaleFont = new Font(tmp.getName(), tmp.getStyle(), tmp.getSize() * a
                / 100);
    }

    public void setdep(String dep) {
        dep = dep.toUpperCase();
        dep = dep.trim();
        StringTokenizer st = new StringTokenizer(dep, ",");
        int count = st.countTokens();
        if (count == 0) {
            return;
        }
        depends = new String[count];
        for (int i = 0; i < count; i++) {

            depends[i] = st.nextToken().trim();
            if (ZetaProperties.views_debug > 0) {
                log.debug(depends[i]);
            }
        }

    }

    /**
     * Задает тип редактирования 
     * @param edit тип редактирования 
     * допустимые значения: HANDBOOK, ALL, NO, READONLY  
     */
    public void seteditable(String edit) {
        editable = edit;
        if (alwaysShowButton) {
            if (edit.equals(HANDBOOK) || edit.equals(ALL)) {
                showButton(true);
            }
            else {
                showButton(false);
            }
        }
        if (editable.equals(HANDBOOK) || editable.equals(NO)
                || editable.equals(READONLY)) {
            editField.setEditable(false);
        }
        else {
            editField.setEditable(true);
        }
    }

    public void setfont_color(String color) {
        try {
            int red = Integer.parseInt(color.substring(1, 3), 16);
            int green = Integer.parseInt(color.substring(3, 5), 16);
            int blue = Integer.parseInt(color.substring(5, 7), 16);
            editField.setForeground(new Color((red << 16) + (green << 8) + blue));
        }
        catch (Exception e) {
            log.error("Shit happens!!!", e);
        }

    }

    public void settext(String text) {
        svalue = text;
        editField.setText(text);
    }

    public void setType(int type) {
        this.type = type;
        if ((type == java.sql.Types.DATE || type == java.sql.Types.TIME || type == java.sql.Types.TIMESTAMP)
                && alwaysShowButton) {
            showButton(true);
        }
        validator.setType(views.grid.GridSwing.getJType(type));
    }

    public void setType(String t) {
        t = t.toUpperCase();
        if (t.equals("NUMBER")) {
            type = java.sql.Types.NUMERIC;
        }
        if (t.equals("STRING")) {
            type = java.sql.Types.VARCHAR;
        }
        if (t.equals("DATE")) {
            type = java.sql.Types.DATE;
            showButton(true);
        }
        validator.setType(views.grid.GridSwing.getJType(type));
    }

    public Object textToObject(String str) throws Exception{
    	return validator.toObject(str);
    }
    
    private void setvalue(String text) {
        svalue = text;
        editField.setText(text);
        editField.selectAll();
    }

    public void setValue(Object o) {
        Panel fp = null;
        core.rml.dbi.Datastore ds = null;
        try {
            if ((fp = getFormParent()) != null) {
                if ((ds = fp.getDatastore()) != null) {
                    if (target != null) {
                        if (needSetString) {
                            setvalue(validator.toString(o));
                        }
                        try {
                            ds.setValue(0, target, o);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        setvalue(svalue);
                    }
                }
            }
            else if (parent instanceof views.ReportForm) { // для
                // Field
                // 'а в
                // ReportForm
                // 'e
                Group go = ((views.ReportForm) parent)
                        .getStore();
                if (go != null && isComputed && o!=null) {
                    go.setValueByName(alias, o);
                }
                if (needSetString) {
                    setvalue(validator.toString(o));
                }
            }
            pValue = o;
        }
        catch (Exception e) {
            log.error("exception inside views.Field::setValue; alias=" + alias,
                    e);
        }
    }

    public void setTextOnly(Object o) {
        try {
            editField.setText(validator.toString(o));
        }
        catch (Exception e) {
            log.error("exception inside views.Field::setTextOnly; alias="
                    + alias, e);
        }
    }

    protected void showButton(boolean show) {
        if (show) {
            fieldPanel.add(button);
            button.setSize(BUTTON_WIDTH, fieldPanel.getHeight() - 1);
            editField.setSize(fieldPanel.getWidth() - BUTTON_WIDTH - 1, fieldPanel.getHeight() - 1);
            button.setEnabled(true);
            button.setVisible(true);
            fieldPanel.doLayout();
            fieldPanel.revalidate();
            fieldPanel.repaint();
        }
        else {
            button.setVisible(false);
            fieldPanel.remove(button);
            editField.setSize(fieldPanel.getWidth() - 1, fieldPanel.getHeight() - 1);
            fieldPanel.doLayout();
            fieldPanel.revalidate();
            fieldPanel.repaint();
        }
    }

    public void processShortcut() {
    	fieldPanel.requestFocus();
        if (editable.equals(HANDBOOK)) {
            bclick(this, null);
        }
    }

    public void focusThis() {
        editField.requestFocusInWindow();
    }

    public String gettarget() {
        return target;
    }

    public void settarget(String tg) {
        target = tg;
    }

    protected boolean selectUndo() {
        Toolkit.getDefaultToolkit().beep();
        editField.selectAll();
        String mustBeType;
        switch (GridSwing.getJType(getType())) {
        case 0:
            mustBeType = "Numeric";
            break;
        case 1:
            mustBeType = "String";
            break;
        case 2:
            mustBeType = "Data";
            break;
        default:
            mustBeType = "Unknown";
        }
        JOptionPane
                .showMessageDialog(SwingUtilities.getWindowAncestor(fieldPanel.getJComponent()),
                        ZetaUtility.pr(ZetaProperties.MSG_BADEDITVALUEPREFIX) + " "
                                + mustBeType, ZetaUtility
                                .pr(ZetaProperties.MSG_BADEDITVALUEHEADER),
                        JOptionPane.ERROR_MESSAGE);

        return true;
    }

	@Override
	public ZComponent getVisualComponent() {
		return fieldPanel;
	}

	@Override
	public Rectangle getBounds() {
		return fieldPanel.getBounds();
	}

	@Override
	public Point getLocationOnScreen() {
		return fieldPanel.getLocationOnScreen();
	}

	@Override
	public boolean isEditable() {
		return false;
	}

	@Override
	public void requestFocus() {
		fieldPanel.requestFocus();
	}

	@Override
	public void setFocusable(boolean focusable) {
		fieldPanel.setFocusable(focusable);
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

	@Override
	protected Border getDefaultBorder() {
		return BasicBorders.getTextFieldBorder();
	}

	/**
	 * @param isComputed the isComputed to set
	 */
	public void setComputed(boolean isComputed) {
		this.isComputed = isComputed;
	}

	/**
	 * @return the isComputed
	 */
	public boolean isComputed() {
		return isComputed;
	}

	/**
	 * @param calc the calc to set
	 */
	public void setCalc(ScriptApi calc) {
		this.calc = calc;
	}

	/**
	 * @return the calc
	 */
	public ScriptApi getCalc() {
		return calc;
	}

	/**
	 * @param needTranslate the needTranslate to set
	 */
	public void setNeedTranslate(boolean needTranslate) {
		this.needTranslate = needTranslate;
	}

	/**
	 * @return the needTranslate
	 */
	public boolean isNeedTranslate() {
		return needTranslate;
	}

	@Override
	public String getText() {
		return editField.getText();
	}

	@Override
	public void setTarget(String t) {
		target = t;
	}

	@Override
	public String getTarget() {
		return target;
	}
}
