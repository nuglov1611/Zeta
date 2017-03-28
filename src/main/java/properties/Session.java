package properties;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/*
 * Class represents session object which settings are used while collaboration
 * with system is performing
 * 
 * @author Marina Vagapova
 */
public class Session implements Comparable {

    private String id;

    private Map<String, String> properties;

    public Session() {
        this(generateId());
    }

    public Session(String id) {
        this(id, null);
    }

    public Session(String id, Map<String, String> properties) {
        this.id = id;
        this.properties = new HashMap<String, String>();
        if (properties != null) {
            this.properties.putAll(properties);
        }
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static String generateId() {
        return UUID.randomUUID().toString();
    }

    public String getProperty(String name) {
        String property = "";
        if (properties.containsKey(name)) {
            property = properties.get(name);
        }
        return property;
    }

    public void setProperty(String name, String value) {
        properties.put(name, value);
    }

    public Iterable<String> getPropertyNames() {
        return properties.keySet();
    }

    public Session clone(String sessionName) {
        Session session = new Session(generateId(), properties);
        session.setProperty(PropertyConstants.NAME, sessionName);        
        return session;
    }

    @Override
    public String toString() {
        return properties.get(PropertyConstants.NAME);
    }

    public int compareTo(Object o) {
        int compareResult = -1;
        if (o instanceof Session) {
            String currentName = this.getProperty(PropertyConstants.NAME);
            String otherName = ((Session)o).getProperty(PropertyConstants.NAME);
            compareResult = currentName.compareTo(otherName);
        }
        return compareResult;
    }
}