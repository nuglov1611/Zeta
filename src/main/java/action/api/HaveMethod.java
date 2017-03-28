/*
 * File: HaveMethod.java
 * 
 * Created: Wed May 12 08:12:10 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package action.api;

public interface HaveMethod {
    Object method(String method, Object arg) throws Exception;
}
