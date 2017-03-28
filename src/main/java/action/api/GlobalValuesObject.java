/*
 * File: GlobalValuesObject.java
 * 
 * Created: Mon Apr 26 13:05:28 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package action.api;

public interface GlobalValuesObject {
    Object getValue() throws Exception;

    Object getValueByName(String name) throws Exception;

    void setValue(Object obj) throws Exception;

    void setValueByName(String name, Object obj) throws Exception;
}
