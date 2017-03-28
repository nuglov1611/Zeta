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
    public Object getValue() throws Exception;

    public Object getValueByName(String name) throws Exception;

    public void setValue(Object obj) throws Exception;

    public void setValueByName(String name, Object obj) throws Exception;
}
