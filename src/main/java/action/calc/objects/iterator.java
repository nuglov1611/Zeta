/*
 * File: iterator.java
 * 
 * Created: Wed May 12 08:13:19 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package action.calc.objects;

public interface iterator {
    Double first() throws Exception;

    Double last() throws Exception;

    Double next() throws Exception;

    Double offset(Double x) throws Exception;

    Double prev() throws Exception;

    Object set_value(Object value) throws Exception;

    Double size() throws Exception;

    Object value() throws Exception;
}
