/*
 * File: XFunction.java
 * 
 * Created: Tue Apr 27 09:35:16 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package action.calc;

import action.api.ARGV;

public interface XFunction {
    public Object call(ARGV _V) throws Exception;
}
