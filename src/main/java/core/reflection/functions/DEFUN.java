/*
 * File: DEFUN.java
 * 
 * Created: Tue Apr 27 10:15:53 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package core.reflection.functions;

import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import action.calc.OP;
import action.calc.Parser;
import action.calc.Tree;
import action.calc.functions.NullExternFunction;

public class DEFUN extends NullExternFunction {
	private static final Logger log    = Logger.getLogger(DEFUN.class);
    static final String fun  = "FUN DEFUN : ";

    Tree                func = null;

    String              name = null;

    public Object eval() throws Exception {
        OP.getFunctions().put(name, func);
        return new Double(1);
    }

    @Override
    public void init(String arg) throws Exception {
        int p1 = arg.indexOf('<');
        int p2 = arg.indexOf('>');
        name = arg.substring(0, p1).trim().toUpperCase();
        String s = arg.substring(p1 + 1);
        StringTokenizer st = new StringTokenizer(
                s.substring(0, s.indexOf('>')), ",");
        Tree a = new Tree();
        Tree foo = a;
        try {
            foo.left = st.nextToken().trim().toUpperCase();
            while (st.hasMoreTokens()) {
                foo.right = new Tree();
                foo = (Tree) foo.right;
                foo.left = st.nextToken().trim().toUpperCase();
            }
        }
        catch (Exception e) {
        	log.error("", e);
        }

        func = new Tree(Parser.parse1(arg.substring(p2 + 1).toCharArray()), a);
    }
}
