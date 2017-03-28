/*
 * File: Tree.java
 * 
 * Created: Fri Apr 23 09:30:05 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package action.calc;

/*
 * Класс для построения дервьев разбора
 */
public class Tree {
    public Object left = null;

    public Object right = null;

    public Tree() {
    }

    public Tree(Object left, Object right) {
        this.left = left;
        this.right = right;
    }
}
