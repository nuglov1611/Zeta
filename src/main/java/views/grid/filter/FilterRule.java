package views.grid.filter;

/**
 * @author: vagapova.m
 * @since: 30.10.2010
 */
public class FilterRule {

    public static final String EQ = "=";

    public static final String NEQ = "<>";

    public static final String LT = "<";

    public static final String LE = "<=";

    public static final String GT = ">";

    public static final String GE = ">=";

    public static final String IS_NULL = "IS NULL";

    public static final String IS_NOT_NULL = "IS NOT NULL";

    private String operator;

    private String criteria;

    private Object value;

    public FilterRule() {
    }

    public FilterRule(String operator, String criteria, Object value) {
        this.operator = operator;
        this.criteria = criteria;
        this.value = value;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
