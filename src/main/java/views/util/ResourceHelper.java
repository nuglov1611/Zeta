package views.util;

import org.apache.log4j.Logger;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : mvagapova.
 */
public class ResourceHelper {

    private static final Logger log = Logger.getLogger(ResourceHelper.class);

    //Хранит цвета в зависимости от имени цвета
    private static Map<String, Color> supportedColors = new HashMap<String, Color>();

    public static final String DEFAULT_COLOR = "DEFAULT";

    static {
        supportedColors.put("BLACK", Color.BLACK);
        supportedColors.put("BLUE", Color.BLUE);
        supportedColors.put("CYAN", Color.CYAN);
        supportedColors.put("DARK_GRAY", Color.DARK_GRAY);
        supportedColors.put("GRAY", Color.GRAY);
        supportedColors.put("GREEN", Color.GREEN);
        supportedColors.put("LIGHT_GRAY", Color.LIGHT_GRAY);
        supportedColors.put("MAGENTA", Color.MAGENTA);
        supportedColors.put("ORANGE", Color.ORANGE);
        supportedColors.put("PINK", Color.PINK);
        supportedColors.put("RED", Color.RED);
        supportedColors.put("WHITE", Color.WHITE);
        supportedColors.put("YELLOW", Color.YELLOW);
    }

    /**
     * Return converted color value
     *
     * @param colorName - имя цвета или его hexedecimal шестизначное значение
     * @return
     */
    public static synchronized Color getColor(final String colorName) {
        Color convertedColor = null;
        if (supportedColors.containsKey(colorName.toUpperCase())) {
            convertedColor = supportedColors.get(colorName.toUpperCase());
        } else {
            try {
                convertedColor = Color.decode(colorName);
            } catch (Exception e) {
                log.error("Shit happens! Can't decode color " + colorName, e);
            }
        }
        return convertedColor;
    }

    public static synchronized Font getFont(final String fontName) {
        Font font = null;
        try {
            font = Font.decode(fontName);
        } catch (Exception e) {
            log.error("Shit happens! Can't decode font " + fontName, e);
        }
        return font;
    }

}
