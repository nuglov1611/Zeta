package views;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import loader.ZetaProperties;
import loader.ZetaUtility;

import org.apache.log4j.Logger;

import views.util.ResourceHelper;
import core.browser.WorkspaceManager;

/**
 * Класс для показа системных сообщений разных типов:
 * ERROR - ошибка, X картинка, кнопка Ок
 * CONFIRMATION - подтверждение, доступны кнопки Да, Нет
 * INFORMATION - уведомление о произошедшем событии
 * INPUT - отображается окошко для ввода дополнительных данных
 *
 * @author Marina Vagapova
 */
public class MessageFactory {

    private static final Logger log = Logger.getLogger(MessageFactory.class);

    private static final String OK_MESSAGE_TEXT = ZetaUtility.pr(ZetaProperties.MSG_OKBUTTON, "Ok");

    private static final String NO_MESSAGE_TEXT = ZetaUtility.pr(ZetaProperties.MSG_NOBUTTON, "No");
    
    private static final String CANCEL_MESSAGE_TEXT = ZetaUtility.pr(ZetaProperties.MSG_CANCELBUTTON, "Cancel");

    private static final String INPUT_MESSAGE_TEXT = ZetaUtility.pr(ZetaProperties.MSG_INPUTBUTTON, "Input");

    public static final String CANCEL_INPUT = "-";

    private static final String YES_MESSAGE_TEXT = ZetaUtility.pr(ZetaProperties.MSG_YESBUTTON, "Yes");

    private static final String CLOSE_MESSAGE_TEXT = ZetaUtility.pr(ZetaProperties.MSG_CLOSEBUTTON, "Close");

    /**
     * Used to display message messageDialog in various views
     * ERROR mean X icon and ok button only
     * CONFIRMATION - ? icon and ok and cancel button
     * INFO - ! icon and ok button only
     * SIMPLE - no icon and ok button only
     */
    public enum Type {

        ERROR, CONFIRMATION, SIMPLE, INFO, WARNING, INPUT, EXTENDED_CONFIRMATION
    }

    private static MessageFactory instance;

    private MessageFactory() {
    }

    public static MessageFactory getInstance() {
        if (instance == null) {
            instance = new MessageFactory();
        }
        return instance;
    }

    private String processMessage(String messageText) {
        final int textLength = 60;
        StringBuilder temp = new StringBuilder();
        String[] lines = messageText.split("[%]");
        for (int lineIndex = 0; lineIndex < lines.length; lineIndex++) {
            String[] words = lines[lineIndex].split("\\s");
            int wordPartIndex = 0;
            for (int wordIndex = 0; wordIndex < words.length;) {
                StringBuilder line = new StringBuilder();
                do {
                    String word = words[wordIndex];
                    if (word.length() > textLength) {
                        int startIndex = wordPartIndex * textLength;
                        if (startIndex < word.length()) {
                            int wordLength = (wordPartIndex + 1) * textLength - line.length();
                            if (wordLength > word.length()) {
                                wordLength = word.length();
                                wordIndex++;
                            }
                            line.append(word.substring(startIndex, wordLength));
                            wordPartIndex++;
                        } else {
                            wordIndex++;
                        }
                    } else {
                        wordPartIndex = 0;
                        // In case when long word was met
                        if (!line.toString().endsWith(" ")) {
                            line.append(' ');
                        }
                        line.append(words[wordIndex]).append(' ');
                        wordIndex++;
                    }
                }
                while (line.length() < textLength && wordIndex < words.length);
                //Check if line length is over maximum, exclude last space symbol
                if (line.length() > textLength) {
                    line.delete(line.length() - 1 - words[wordIndex - 1].length(), line.length());
                    wordIndex--;
                }
                //Remove last space
                if (line.length() > 0 && line.toString().endsWith(" ")) {
                    line.delete(line.length() - 1, line.length());
                }
                temp.append("<p align=\"left\">").append(line).append("</p>");
            }
        }
        temp.insert(0, "<html>");
        temp.append("</html>");
        return temp.toString();
    }

    private String extractTitle(Type messageType) {
        String strType;
        if (messageType == Type.ERROR) {
            strType = ZetaUtility.pr(ZetaProperties.TITLE_ERROR, "error");
        } else if (messageType == Type.CONFIRMATION) {
            strType = ZetaUtility.pr(ZetaProperties.TITLE_SURE, "U R sure ?");
        } else if (messageType == Type.WARNING){
            strType = ZetaUtility.pr(ZetaProperties.TITLE_WARNING, "warning");
        } else if (messageType == Type.INFO) {
            strType = ZetaUtility.pr(ZetaProperties.TITLE_IFORMATION, "information");
        } else if (messageType == Type.INPUT){
            strType = ZetaUtility.pr(ZetaProperties.TITLE_INPUT, "input");
        } else {
            strType = ZetaUtility.pr(ZetaProperties.TITLE_MESSAG, "message");
        }
        return strType;
    }

    public boolean showMessage(final String messageText, final Type messageType) {
        return showMessage(WorkspaceManager.getCurWorkspace(), messageText, messageType);
    }

    public boolean showMessage(Component owner, String message, Type messageType) {
        return showMessage(owner, null, message, messageType);   
    }

    public boolean showMessage(final Component owner, final String messageHeader,
                               final String messageText, final Type messageType) {
        return showMessage(owner, messageHeader, messageText, messageType, null);
    }

    public boolean showMessage(Component owner, String message, Type messageType, String font) {
        return showMessage(owner, null, message, messageType, font);
    }

    public boolean showMessage(final Component owner, final String messageHeader,
                               final String messageText, final Type messageType, String font) {
        int optionType = JOptionPane.DEFAULT_OPTION;
        int iconType = JOptionPane.PLAIN_MESSAGE;
        Object[] options = null;
        Object defaultOption = OK_MESSAGE_TEXT;
        String title = messageHeader;
        if (title == null) {
            title = extractTitle(messageType);
        }
        final String dialogText = processMessage(messageText);

        if (Type.CONFIRMATION == messageType) {
            iconType = JOptionPane.QUESTION_MESSAGE;
//            options = new Object[]{OK_MESSAGE_TEXT, CANCEL_MESSAGE_TEXT};
            options = new Object[]{YES_MESSAGE_TEXT, NO_MESSAGE_TEXT};
            optionType = JOptionPane.YES_NO_OPTION;         
        } else if (Type.EXTENDED_CONFIRMATION == messageType) {
            iconType = JOptionPane.QUESTION_MESSAGE;
//            options = new Object[]{OK_MESSAGE_TEXT, NO_MESSAGE_TEXT, CANCEL_MESSAGE_TEXT};
            options = new Object[]{YES_MESSAGE_TEXT, NO_MESSAGE_TEXT, CANCEL_MESSAGE_TEXT};
            optionType = JOptionPane.YES_NO_CANCEL_OPTION;
            defaultOption = CANCEL_MESSAGE_TEXT;
        } else if (Type.INFO == messageType) {
            iconType = JOptionPane.INFORMATION_MESSAGE;
//            options = new Object[]{OK_MESSAGE_TEXT};
            options = new Object[]{CLOSE_MESSAGE_TEXT};
        } else if (Type.SIMPLE == messageType) {
            options = new Object[]{OK_MESSAGE_TEXT};
        } else if (Type.WARNING == messageType) {
            iconType = JOptionPane.WARNING_MESSAGE;
//          options = new Object[]{OK_MESSAGE_TEXT};
            options = new Object[]{CLOSE_MESSAGE_TEXT};
        } else if (Type.ERROR == messageType) {
            iconType = JOptionPane.ERROR_MESSAGE;
//          options = new Object[]{OK_MESSAGE_TEXT};
            options = new Object[]{CLOSE_MESSAGE_TEXT};
        } else {
            log.error("Shit happens", new UnsupportedOperationException());
        }
        int answer;
        if (font != null) {  
            JLabel message = new JLabel(dialogText);
            Font messageFont = ResourceHelper.getFont(font);
            if (messageFont != null) {
                message.setFont(messageFont);
            }
            answer = JOptionPane.showOptionDialog(owner, message, title,
                    optionType, iconType, null, options, defaultOption);

        } else {
            answer = JOptionPane.showOptionDialog(owner, dialogText, title,
                    optionType, iconType, null, options, defaultOption);
        }

        return answer == JOptionPane.YES_OPTION;
    }

    
    /**
     * Show YesNoCancel dialog
     *
     * @param owner       - parent owner
     * @param messageText - message text
     * @return chosen option
     */
    public int showYesNoCancelMessage(final Component owner, final String messageText){
        int optionType = JOptionPane.YES_NO_CANCEL_OPTION;
        int iconType = JOptionPane.QUESTION_MESSAGE;
//        Object[] options = new Object[]{OK_MESSAGE_TEXT, NO_MESSAGE_TEXT, CANCEL_MESSAGE_TEXT};
        Object[] options = new Object[]{YES_MESSAGE_TEXT, NO_MESSAGE_TEXT, CANCEL_MESSAGE_TEXT};
        
        final Object defaultOption = CANCEL_MESSAGE_TEXT;

        return JOptionPane.showOptionDialog(owner, messageText, null,
                optionType, iconType, null, options, defaultOption);
    }
    
    
    /**
     * Show input dialog
     *
     * @param owner       - parent owner
     * @param messageText - message text
     * @return value from input or null if user cancelled the input
     */
    public String showInputMessage(Component owner, String messageText) {
        return showInputMessage(owner, messageText, "");
    }

    /**
     * Show input dialog
     *
     * @param owner       - parent owner
     * @param messageText - message text
     * @param inputValue  -  initial input value to be dispayed in the dialog
     * @return value from input or null if user cancelled the input
     */
    public String showInputMessage(Component owner, String messageText,
                                  String inputValue) {
        Object defaultOption = INPUT_MESSAGE_TEXT;
        Object[] options = new Object[]{INPUT_MESSAGE_TEXT, CANCEL_MESSAGE_TEXT};
        String title = extractTitle(Type.INPUT);
        final String dialogText = processMessage(messageText);
        JOptionPane    pane = new JOptionPane(dialogText, JOptionPane.PLAIN_MESSAGE,
                                              JOptionPane.OK_CANCEL_OPTION, null,
                                              options, defaultOption);

        pane.setWantsInput(true);
        pane.setSelectionValues(null);
        pane.setInitialSelectionValue(inputValue);
        pane.setComponentOrientation(((owner == null) ?
	    JOptionPane.getRootFrame() : owner).getComponentOrientation());

        JDialog dialog = pane.createDialog(owner, title);

        pane.selectInitialValue();
        dialog.setVisible(true);
        dialog.dispose();

        Object value = pane.getInputValue();
        if (pane.getValue().equals(CANCEL_MESSAGE_TEXT)) {
            return CANCEL_INPUT;
        }
        return value.toString();
    }

    /**
     * Test function
     *
     * @param args
     */
    public static void main(String[] args) {
        JFrame testFrame = new JFrame();
        boolean answer = MessageFactory.getInstance().showMessage(testFrame, null, "its veryyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy long text", MessageFactory.Type.INFO, "Times New Roman-30");
        System.out.println("Information: " + answer);
        answer = MessageFactory.getInstance().showMessage(testFrame, null, "Are you sure?", MessageFactory.Type.CONFIRMATION);
        System.out.println("Confirmation: " + answer);
        answer = MessageFactory.getInstance().showMessage(testFrame, null, "Are you sure?", MessageFactory.Type.EXTENDED_CONFIRMATION);
        System.out.println("Confirmation Ext: " + answer);
        answer = MessageFactory.getInstance().showMessage(testFrame, null, "Wrong choise! " + answer, MessageFactory.Type.ERROR);
        System.out.println("Error: " + answer);
        answer = MessageFactory.getInstance().showMessage(testFrame, null, "Caution! Truth is out of there...", MessageFactory.Type.WARNING);
        System.out.println("Warning: " + answer);
        Object value = MessageFactory.getInstance().showInputMessage(testFrame, "Input your value here");
        System.out.println("Input default: " + value);
        value = MessageFactory.getInstance().showInputMessage(testFrame, "Input your value here", "blabla");
        System.out.println("Input blabla: " + value);
        System.exit(0);
    }
}
