/**
 * Copyrigth(c) 1999, by Gama author Alexey Chen (xx.2.99)
 */

package core.parser;

/**
 */

import publicapi.RmlProperties;

import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Set;


/*
 * Узел дерева свойств
 */
public class Proper implements RmlProperties {

    public static void add(Proper prop, Proper p) {
        if (prop.content == null)
            prop.content = p;
        else {
            Proper foo = prop.content;
            while (foo.next != null)
                foo = foo.next;
            foo.next = p;
        }
    }

    public String tag = "UNKNOWN";

    public Proper content = null;

    public Proper next = null;

    public Hashtable hash;

    public Proper() {
        hash = new Hashtable();
    }

    public Proper(Hashtable<String, Object> p) {
        // hash = p;
        hash = new Hashtable();
        importHash(p);
    }

    public Object get(String alias) {
        Object res;
        res = hash.get(alias);
        return res;
    }

    public Object get(String alias, Object o) {
        Object res;
        res = hash.get(alias);
        if (res == null)
            res = o;
        return res;
    }

    @Override
    public void put(String alias, Object obj) {
        hash.put(alias, obj);
    }

    @Override
    public void importHash(Hashtable<String, Object> properties) {
        Set<Entry<String, Object>> entrys = properties.entrySet();
        for (Entry<String, Object> entry : entrys) {
            hash.put(entry.getKey().toUpperCase(), entry.getValue());
        }
    }
}
