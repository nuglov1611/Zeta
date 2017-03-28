package core.reflection.rml;

import java.awt.event.KeyEvent;

import org.apache.log4j.Logger;

import publicapi.KeyCatcherAPI;
import action.api.RTException;
import action.calc.objects.class_type;
import core.document.Document;
import core.document.KeyCatcher;
import core.parser.Proper;
import core.rml.RmlObject;

public class KEYCATCHER extends RmlObject implements KeyCatcherAPI, KeyCatcher, class_type {
	private static final Logger log = Logger.getLogger(KEYCATCHER.class);
	
    private String                      exp         = null;

    private boolean                   eachKey   = false;

    private int[]                     endKeys   = null;

    private boolean                   finished  = false;

    private boolean                   inProcess = false;

    private char                      nowChar;

    private StringBuffer              msg       = new StringBuffer();

    private int                       keyNum    = 0;

    private char[]                    keyBuf    = null;

    public boolean catchKey(KeyEvent e) {
        if (eachKey) {
            nowChar = e.getKeyChar();
            inProcess = true;
            try {
            	document.executeScript(exp, false);
            }
            catch (Exception ex) {
                log.error("", ex);
            }
            inProcess = false;
            if (finished) {
                finished = false;
                return false;
            }
        }
        else if (e.getKeyCode() == endKeys[keyNum]) {
            keyBuf[keyNum++] = e.getKeyChar();
            if (keyNum == endKeys.length) {
                try {
                	document.executeScript(exp, false);
                }
                catch (Exception ex) {
                    log.error("", ex);
                }
                msg.setLength(0);
                keyNum = 0;
                return false;
            }
        }
        else {
            if (keyNum > 0) {
                int dropLen = 1;
                do {
                    while (dropLen < keyNum && endKeys[dropLen] != endKeys[0]) {
                        dropLen++;
                    }
                    if (dropLen < keyNum) {
                        int tmp = dropLen + 1;
                        while (tmp < keyNum
                                && endKeys[tmp] == endKeys[tmp - dropLen]) {
                            tmp++;
                        }
                        if (tmp < keyNum
                                || e.getKeyCode() != endKeys[tmp - dropLen]) {
                            dropLen++;
                        }
                        else {
                            break;
                        }
                    }
                } while (dropLen < keyNum);
                msg.append(keyBuf, 0, dropLen);
                System.arraycopy(keyBuf, dropLen, keyBuf, 0, keyNum - dropLen);
                keyNum -= dropLen;
                if (e.getKeyCode() == endKeys[keyNum]) {
                    keyBuf[keyNum++] = e.getKeyChar();
                    return true;
                }
            }
            msg.append(e.getKeyChar());
        }

        return true;
    }

    public void init(Proper prop, Document doc) {
    	super.init(prop, doc);

        exp = (String) prop.get("EVENT");

        String keys = (String) prop.get("EVENTKEYS");

        String sp = (String) prop.get("HANDLEEACHKEY");
        if (sp != null)
            eachKey = sp.equalsIgnoreCase("yes");

        sp = (String) prop.get("ENDKEYS");
        if (sp != null)
            try {
                endKeys = doc.parseKeys(sp);
            }
            catch (IllegalArgumentException e) {
            	log.error("", e);
            }

//        if (!eachKey && (endKeys == null || endKeys.length == 0))
//            return null;
        if (!eachKey)
            keyBuf = new char[endKeys.length];

        try {
            doc.addKeyCatcher(this, keys);
        }
        catch (IllegalArgumentException e) {
        	log.error("", e);
        }
    }

    public Object getValue() {
        return this;
    }

    public Object getValueByName(String name) {
        try {
            if (name.equals("MESSAGE"))
                return method("GETMESSAGE", null);
            else if (name.equals("KEYCHAR"))
                return method("GETKEYCHAR", null);
            else if (name.equals("INPUT"))
                return method("GETMESSAGE", null);
        }
        catch (Exception e) {
        }
        return null;
    }

    public Object method(String method, Object arg) throws Exception {
        if (method.equals("GETKEYCHAR")) {
            if (!inProcess)
                throw new RTException("WrongState",
                        "Need to be in handling each key for allowing method getKeyChar");
            return new String(new char[] { nowChar });
        }
        else if (method.equals("FINISHPROCESSING")) {
            if (!inProcess)
                throw new RTException("WrongState",
                        "Need to be in handling each key for allowing method finishProcessing");
            finished = true;
            return null;
        }
        else if (method.equals("GETMESSAGE")) {
            if (eachKey)
                throw new RTException("WrongState",
                        "Need to be in handling message for allowing method getMessage");
            return msg.toString();
        }
        else
            throw new RTException("HasNotMethod", "method " + method
                    + " not defined in class rml.KEYCATCHER!");
    }

    public void setValue(Object value) {
    }

    public void setValueByName(String name, Object value) {
    }

    public String type() {
        return "KeyCatcher";
    }

}
