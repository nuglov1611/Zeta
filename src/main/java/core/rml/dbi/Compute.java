package core.rml.dbi;

/**
 * @author: vagapova.m
 * @since: 03.10.2010
 */
public class Compute {
    String name;

    int type;

    public Compute(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }
}
