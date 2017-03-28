package views.util;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import views.UTIL;
import core.parser.Proper;
import core.rml.RmlConstants;

/**
 * User: mvagapova
 * Date: 06.02.11
 */
public class RmlPropertyContainer {

    /**
     * Contains all rml properties
     */
    private Map<String, Object> rmlProperties = new HashMap<String, Object>();

    private static final Logger log = Logger.getLogger(RmlPropertyContainer.class);

    public void initProperty(Proper properties, String propName) {
        initProperty(properties, propName, false);
    }

    public void initProperty(Proper properties, String propName, Boolean parseDep) {
        initProperty(properties, propName, null, null, parseDep);
    }

    public void initProperty(Proper properties, String propName, Object defaultValue) {
        initProperty(properties, propName, null, defaultValue);
    }

    public void initProperty(Proper properties, String propName, Class propType, Object defaultValue) {
        initProperty(properties, propName, propType, defaultValue, false);
    }

    public void initProperty(Proper properties, String propName, Class propType, Object defaultValue, Boolean isDepObject) {
        Object propValue = properties.get(propName);
        if (propValue != null) {
            if (propType != null && propType == Color.class) {
                rmlProperties.put(propName, UTIL.getColor(propValue.toString()));
            } else {
                rmlProperties.put(propName, propValue);
            }
            if (isDepObject) {
                try {
                    String[] dep = UTIL.parseDep(propValue.toString());
                    rmlProperties.put(RmlConstants.PARSE_PREFIX + propName, dep);
                } catch (Exception e) {
                    log.error("Shit happens", e);
                }
            }
        } else {
            rmlProperties.put(propName, defaultValue);
        }
    }

    public Object getProperty(String propName) {
        return rmlProperties.get(propName);
    }

    public Boolean getBooleanProperty(String propName) {
        Object propValue = rmlProperties.get(propName);
        return propValue != null && propValue.toString().equalsIgnoreCase(RmlConstants.YES);
    }

    public String getStringProperty(String propName) {
        Object propValue = rmlProperties.get(propName);
        if (propValue != null) {
            return propValue.toString();
        }
        return null;
    }

    public Integer getIntProperty(String propName) {
        Object propValue = rmlProperties.get(propName);
        if (propValue != null && propValue instanceof Integer) {
            return (Integer) propValue;
        }
        return null;
    }

    public Color getColorProperty(String propName) {
        Object propValue = rmlProperties.get(propName);
        if (propValue != null && propValue instanceof Color) {
            return (Color) propValue;
        }
        return null;
    }

    public Font getFontProperty(String propName) {
        Object propValue = rmlProperties.get(propName);
        if (propValue != null && propValue instanceof Font) {
            return (Font) propValue;
        }
        return null;
    }

    public void put(String key, Object value) {
        rmlProperties.put(key, value);
    }

    public Object get(String key) {
        return rmlProperties.get(key);
    }

    public boolean containsKey(String propertyName) {
        return rmlProperties.containsKey(propertyName);
    }
}
