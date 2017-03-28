package views.grid.filter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.RowFilter;

/**
 * Row filter used to skip rows from original data model
 *
 * @author vagapova.m
 * @since 18.07.2010
 */
public class GridRowFilter extends RowFilter {

    public static final String AND = "AND";

    public static final String OR = "OR";

    /**
     * collection of pairs column index and set of rules applied
     */
    private Map<Integer, List<FilterRule>> filterRules;

    public GridRowFilter() {
        filterRules = new HashMap<Integer, List<FilterRule>>();
    }

    public GridRowFilter(RowFilter rowFilter) {
        filterRules = new HashMap<Integer, List<FilterRule>>();
        if (rowFilter != null && rowFilter instanceof GridRowFilter) {
            Map<Integer, List<FilterRule>> customFilterRules = ((GridRowFilter)rowFilter).getFilterRules();
            filterRules.putAll(customFilterRules);
        }
    }

    public void addFilter(Integer columnIndex, String operator, String criteria, Object value) {
        List<FilterRule> rules = filterRules.get(columnIndex);
        if (rules == null) {
            rules = new ArrayList<FilterRule>();
            filterRules.put(columnIndex, rules);
        }
        FilterRule filterRule = new FilterRule(operator, criteria, value);
        rules.add(filterRule);
    }

    /**
     * Add an "equals to" filter condition to input data to analyze.
     *
     * @param columnIndex column name
     * @param value       filter value
     */
    public final void addEqualsFilter(Integer columnIndex, String operator, Object value) {
        addFilter(columnIndex, operator, FilterRule.EQ, value);
    }

    /**
     * Add an "NOT equals to" filter condition to input data to analyze.
     *
     * @param columnIndex column name
     * @param value       filter value
     */
    public final void addNotEqualsFilter(Integer columnIndex, String operator, Object value) {
        addFilter(columnIndex, operator, FilterRule.NEQ, value);
    }


    /**
     * Add an "is null" filter condition to input data to analyze.
     *
     * @param columnIndex column name
     * @param value       filter value
     */
    public final void addIsNullFilter(Integer columnIndex, String operator, Object value) {
        addFilter(columnIndex, operator, FilterRule.IS_NULL, value);
    }


    /**
     * Add an "is NOT null" filter condition to input data to analyze.
     *
     * @param columnIndex column name
     * @param value       filter value
     */
    public final void addIsNotNullFilter(Integer columnIndex, String operator, Object value) {
        addFilter(columnIndex, operator, FilterRule.IS_NOT_NULL, value);
    }


    /**
     * Add an "less than" filter condition to input data to analyze.
     *
     * @param columnIndex column name
     * @param value       filter value
     */
    public final void addLessThanFilter(Integer columnIndex, String operator, Object value) {
        addFilter(columnIndex, operator, FilterRule.LT, value);
    }


    /**
     * Add an "less or equals to" filter condition to input data to analyze.
     *
     * @param columnIndex column name
     * @param value       filter value
     */
    public final void addLessOrEqualsToFilter(Integer columnIndex, String operator, Object value) {
        addFilter(columnIndex, operator, FilterRule.LE, value);
    }


    /**
     * Add an "greater than" filter condition to input data to analyze.
     *
     * @param columnIndex column name
     * @param value       filter value
     */
    public final void addGreaterThanFilter(Integer columnIndex, String operator, Object value) {
        addFilter(columnIndex, operator, FilterRule.GT, value);
    }


    /**
     * Add an "greater or equals to" filter condition to input data to analyze.
     *
     * @param columnIndex column name
     * @param value       filter value
     */
    public final void addGreaterOrEqualsToFilter(Integer columnIndex, String operator, Object value) {
        addFilter(columnIndex, operator, FilterRule.GE, value);
    }


    /**
     * Remove filter conditions for the specified column name.
     *
     * @param columnIndex column name
     */
    public final void removeFilters(Integer columnIndex) {
        filterRules.remove(columnIndex);
    }


    /**
     * Invoked by TableModelFilter class to skip rows.
     *
     * @param columnIndex column name under analysis
     * @param value       current value for the specified column
     * @return <code>true</code> if row must be skipped, <code>false</code> otherwise
     */
    public final boolean matchCriteria(Integer columnIndex, Object value) {
        List<FilterRule> rules = filterRules.get(columnIndex);
        if (rules == null) {
            return false;
        }
        Boolean totalMatch = null;
        for (FilterRule rule : rules) {
            boolean matchCriteria = false;
            if (rule.getCriteria().equals(FilterRule.EQ)) {
                if (value == null && rule.getValue() == null) {
                    matchCriteria = true;
                } else if (value != null && rule.getValue() == null) {
                    matchCriteria = false;
                } else if (value == null && rule.getValue() != null) {
                    matchCriteria = false;
                } else if (value != null) {
                    matchCriteria = value.equals(rule.getValue());
                }
            } else if (rule.getCriteria().equals(FilterRule.NEQ)) {
                if (value == null && rule.getValue() == null) {
                    matchCriteria = false;
                } else if (value != null && rule.getValue() == null) {
                    matchCriteria = true;
                } else if (value == null && rule.getValue() != null) {
                    matchCriteria = true;
                } else if (value != null) {
                    matchCriteria = !value.equals(rule.getValue());
                }
            } else if (rule.getCriteria().equals(FilterRule.IS_NULL)) {
                matchCriteria = value == null;
            } else if (rule.getCriteria().equals(FilterRule.IS_NOT_NULL)) {
                matchCriteria = value != null;
            } else if (rule.getCriteria().equals(FilterRule.LT)) {
                if (value == null) {
                    matchCriteria = false;
                } else if (value instanceof Number) {
                    matchCriteria = ((Number) value).doubleValue() < ((Number) rule.getValue()).doubleValue();
                } else if (value instanceof Date) {
                    matchCriteria = ((Date) value).compareTo((Date) rule.getValue()) < 0;
                } else {
                    matchCriteria = value.toString().compareTo(rule.getValue().toString()) < 0;
                }
            } else if (rule.getCriteria().equals(FilterRule.LE)) {
                if (value == null) {
                    matchCriteria = true;
                } else if (value instanceof Number) {
                    matchCriteria = ((Number) value).doubleValue() <= ((Number) rule.getValue()).doubleValue();
                } else if (value instanceof Date) {
                    matchCriteria = ((Date) value).compareTo((Date) rule.getValue()) <= 0;
                } else {
                    matchCriteria = value.toString().compareTo(rule.getValue().toString()) <= 0;
                }
            } else if (rule.getCriteria().equals(FilterRule.GT)) {
                if (value == null) {
                    matchCriteria = true;
                } else if (value instanceof Number) {
                    matchCriteria = ((Number) value).doubleValue() > ((Number) rule.getValue()).doubleValue();
                } else if (value instanceof Date) {
                    matchCriteria = ((Date) value).compareTo((Date) rule.getValue()) > 0;
                } else {
                    matchCriteria = value.toString().compareTo(rule.getValue().toString()) > 0;
                }
            } else if (rule.getCriteria().equals(FilterRule.GE)) {
                if (value == null) {
                    matchCriteria = true;
                } else if (value instanceof Number) {
                    matchCriteria = ((Number) value).doubleValue() >= ((Number) rule.getValue()).doubleValue();
                } else if (value instanceof Date) {
                    matchCriteria = ((Date) value).compareTo((Date) rule.getValue()) >= 0;
                } else {
                    matchCriteria = value.toString().compareTo(rule.getValue().toString()) >= 0;
                }
            }

            //Analyze operator
            if (totalMatch == null) {
                totalMatch = matchCriteria;
            } else {
                if (rule.getOperator().equals(OR)) {
                    totalMatch |= matchCriteria;
                } else if (rule.getOperator().equals(AND)) {
                    totalMatch &= matchCriteria;
                }
            }
        }
        return totalMatch;
    }

    @Override
    public boolean include(Entry entry) {
        boolean match = true;
        for (Integer columnIndex : filterRules.keySet()) {
            match = matchCriteria(columnIndex, entry.getValue(columnIndex));
            if (!match) {
                break;
            }
        }
        return match;
    }

    public Set<Integer> getColumnIndexes() {
        return filterRules.keySet();
    }

    public Map<Integer,List<FilterRule>> getFilterRules() {
        return filterRules;
    }
}
