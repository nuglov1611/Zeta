/*
 * File: NullOutputStream.java
 * 
 * Created: Wed Jun 30 08:34:13 1999
 * 
 * Copyright (c) by Almanex Technologes
 * 
 * 
 * Author: Alexey Chen
 */

package loader;

import java.io.OutputStream;

public class NullOutputStream extends OutputStream {
    @Override
    public void write(byte[] buf, int off, int len) {
    }

    @Override
    public void write(int a) {
    }
}
