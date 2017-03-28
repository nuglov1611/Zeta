/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package core.rml;

import action.api.GlobalValuesObject;
import action.api.HaveMethod;
import action.api.ScriptApi;
import core.document.Document;
import core.parser.Proper;
import org.apache.log4j.Logger;
import publicapi.RmlContainerAPI;
import publicapi.RmlObjectAPI;

import java.util.StringTokenizer;

/**
 * Суперкласс для всех RML-объектов в Зете.
 *
 * @author nick
 *         {@docRoot}
 */
public abstract class RmlObject implements HaveMethod, GlobalValuesObject, RmlObjectAPI {
    private static final Logger log = Logger.getLogger(RmlObject.class);

    /**
     * Идентификатор объекта. Должен быть уникальным.
     */
    protected String alias = null;

    protected RmlContainerAPI parent = null;

    protected Document document = null;

    //Список зависимых компонентов
    private String[] dependences = null;

    //Действие, выполняемое, при изменения основного компонента
    private ScriptApi depExpression = null;

    //Действие, выполняемое, при изменении состояния компонента
    private ScriptApi commitExpression = null;

    protected void initComminAPI(Proper prop) {

        String dep = (String) prop.get("DEPLIST");
        if (dep != null) {
            dep = dep.toUpperCase();
            dep = dep.trim();
            StringTokenizer st = new StringTokenizer(dep, ",");
            int count = st.countTokens();
            if (count == 0) {
                return;
            }
            dependences = new String[count];
            for (int i = 0; i < count; i++) {
                dependences[i] = st.nextToken().trim();
            }
        }

        final String comm = (String) prop.get("COMMITEXP");
        if (comm != null) {
            commitExpression = ScriptApi.getAPI(comm);
        }

        final String depExp = (String) prop.get("DEPEXP");
        if (depExp != null) {
            depExpression = ScriptApi.getAPI(depExp);
        }
    }

    private void calcDep() {
        if (dependences == null) {
            return;
        }
        for (String dependence : dependences) {
            RmlObject o = document.findObject(dependence);
            if (o != null) {
                o.onPrincipalChange();
            } else {
                System.out.println("Object not found for alias "
                        + dependence);
            }
        }
    }

    private void onPrincipalChange() {
        try {
            if (depExpression != null) {
                depExpression.eval(document.getAliases());
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private void onCommit() {
        try {
            if (commitExpression != null) {
                commitExpression.eval(document.getAliases());
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }

    protected void commit() {
        onCommit();
        calcDep();
    }

    public void addToContainer(RmlContainerAPI container) {
        this.parent = container;
        document.registrate(this);
    }

    @Override
    public void init(Proper prop, Document doc) {
        document = doc;
        if (prop == null) {
            return;
        }
        setAlias((String) prop.get("ALIAS"));
        initComminAPI(prop);

        if (this instanceof RmlContainerAPI) {
            if (((RmlContainerAPI) this).addChildrenAutomaticly())
                try {
                    ((RmlContainerAPI) this).getContainer().addChildren(prop, doc);
                } catch (Exception e) {
                    log.error("!", e);
                }
        }
    }

    abstract public Object method(String method, Object arg) throws Exception;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
        if (alias != null && !alias.trim().equals(""))
            document.registrate(this);
    }

    public Object getValue() throws Exception {
        return this;
    }

    public Object getValueByName(String name) throws Exception {
        return null;
    }

    public void setValue(Object obj) throws Exception {
    }

    public void setValueByName(String name, Object obj) throws Exception {
    }

    public Object findObject(String alias) {
        return document.getObject(alias);
    }
}
