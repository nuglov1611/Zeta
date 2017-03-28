package views.grid.model.cross.aggregator;

import java.io.Serializable;

/**
 * @author: vagapova.m
 * @since: 01.10.2010
 */
public class LetterAggregator extends GenericAggregator implements Serializable {

    char[] fromChars = new char[0];
    char[] toChars = new char[0];
    String[] groupValues = new String[0];


    public LetterAggregator() {
    }


    public final void addGroup(char fromChar, char toChar, String groupValue) {
        char[] aux = new char[fromChars.length + 1];
        System.arraycopy(fromChars, 0, aux, 0, fromChars.length);
        aux[aux.length - 1] = Character.toUpperCase(fromChar);
        fromChars = aux;

        aux = new char[toChars.length + 1];
        System.arraycopy(toChars, 0, aux, 0, toChars.length);
        aux[aux.length - 1] = Character.toUpperCase(toChar);
        toChars = aux;

        String[] aux2 = new String[groupValues.length + 1];
        System.arraycopy(groupValues, 0, aux2, 0, groupValues.length);
        aux2[aux2.length - 1] = groupValue;
        groupValues = aux2;
    }


    public final Object decodeValue(Object value) {
        if (value != null && value.toString().length() > 0) {
            if (fromChars.length == 0)
                return value.toString().toUpperCase().substring(0, 1);

            char c = value.toString().toUpperCase().charAt(0);
            for (int i = 0; i < fromChars.length; i++)
                if (c >= fromChars[i] && c <= toChars[i])
                    return groupValues[i];

            return value.toString().toUpperCase().substring(0, 1);
        }
        return value;
    }


    public final boolean equals(Object obj) {
        if (obj.getClass() != LetterAggregator.class)
            return false;
        String s1 = "";
        for (String groupValue : groupValues) s1 += groupValue + "_";
        String s2 = "";
        for (int i = 0; i < ((LetterAggregator) obj).groupValues.length; i++)
            s2 += ((LetterAggregator) obj).groupValues[i] + "_";
        return s1.equals(s2);
    }


    public final int hashCode() {
        String s1 = "";
        for (String groupValue : groupValues) s1 += groupValue + "_";
        return s1.hashCode();
    }


}